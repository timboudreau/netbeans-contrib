/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 * 
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 * 
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 * 
 * Contributor(s): Tim Boudreau
 * 
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */

package org.netbeans.api.objectregistries;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.objectloader.ObjectLoader;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.Repository;
import org.openide.util.Lookup;
import org.openide.util.Lookup.Provider;
import org.openide.util.lookup.Lookups;

/**
 * A generic registry of objects in some folder in the system filesystem,
 * with special support for registering objects against the type that an
 * ObjectLoader will load, such that you can get the instance of the object
 * registered without actually triggering I/O.
 * <p/>
 * Objects may be registered as .instance files or similar, under folders that
 * specify type names underneath the root folder.  If the Lookup contains
 * an object of a type matching that folder name, all registered objects 
 * of type <code>type</code> in that folder are returned.
 *
 * @author Tim Boudreau
 */
public final class ByLookupRegistry<Type> extends Registry<Type> {
    private Provider provider;
    private final Class<Type> type;
    private final String rootFolder;
    private static final Logger logger = Logger.getLogger(
            ByLookupRegistry.class.getName());
    /**
     * Create a new registry
     * @param type The type of objects you are interested in
     * @param provider A Lookup.Provider
     * @param rootFolder A folder in the system filesystem
     */
    ByLookupRegistry (Class<Type> type, Lookup.Provider provider, String rootFolder) {
        this.type = type;
        this.provider = provider;
        this.rootFolder = rootFolder;
    }
    
    public void setProvider (Provider provider) {
        this.provider = provider;
    }
    
    @Override
    public String toString() {
        String provString = provider == null ? "null" : 
            provider.getLookup().toString();
        return super.toString() + " over " + rootFolder + " type " + 
                type.getName() + ' ' + provString;
    }

    /**
     * Get all of the objects of the given type in this registry.
     * @return A list of objects
     * @throws IllegalStateException if the Lookup.Provider is null
     */
    public List<Type> get() {
        if (provider == null) {
            throw new IllegalStateException ("Provider is not set");
        }
        //PENDING - various places we could cache data for performance
        Lookup lkp = provider.getLookup();
        List <Type> result = new ArrayList <Type>();
        //Get all the items in the lookup - use Lookup.Item to avoid
        //aggressively resolving items in the lookup where possible
        Collection <? extends Lookup.Item> cc = 
                lkp.lookupResult(Object.class).allItems();
        
        //Iterate them
        for (Lookup.Item item : cc) {
            //Get all the interface and supertype names for each object in the
            //lookup
            Set <String> names = allNames (item.getType());
            //Iterate them
            for (String name : names) {
                //See if there is an sfs folder ala com-foo-bar-Baz under
                //our root folder
                FileObject folder = folderFor (name);
                if (folder != null) {
                    //If so, make a FolderLookup over it and add all actions
                    //in the FolderLookup to the result
                    if (ObjectLoader.class.isAssignableFrom(item.getType())) {
                        //Special handling so we can have ObjectLoader-sensitive
                        //actions that are sensitive not to ObjectLoader.class,
                        //but to the type of object it will load
                        ObjectLoader ldr = (ObjectLoader) item.getInstance();
                        Class typeItLoads = ldr.type();
                        if (logger.isLoggable(Level.FINER)) logger.log(
                                Level.FINER, "Object loader lookup on type " + 
                                ldr.type().getName());
                        //Look for folders ala
                        //root/org-netbeans-api-dynactions-ObjectLoader/com-foo-bar-Baz
                        //which contain actions
                        Set <String> loaderTypeNames = allNames(typeItLoads);
                        for (String ltn : loaderTypeNames) {
                            FileObject fld = folder.getFileObject(ltn);
                            if (fld != null) {
                                if (logger.isLoggable(Level.FINER)) logger.log(
                                        Level.FINER, "Found folder " + fld.getPath());
                                
                                Lookup currLookup = Lookups.forPath(
                                        rootFolder + "/" + name + "/" + ltn);
                                result.addAll (currLookup.lookupAll(
                                        type));
                            } else if (logger.isLoggable(Level.FINER)) {
                                logger.log(Level.FINER, "Did not find folder " + ltn);
                            }
                        }
                    } else {
                        String folderName = folder.getPath();
                        if (logger.isLoggable(Level.FINER)) logger.log(
                                Level.FINER, "Try to lookup on " + folderName);
                        
                        Lookup lookupForType = Lookups.forPath(folderName);
                        Collection <? extends Type> all = 
                                lookupForType.lookupAll(type);
                        
                        if (logger.isLoggable(Level.FINER)) {
                        if (!all.isEmpty()) {
                            logger.log(
                                    Level.FINER, "Got " + all + " for " + 
                                    rootFolder);
                        } else {
                            logger.log(Level.FINER, "  empty for " + 
                                    type.getName() + 
                                    ".   All contents: " + 
                                    lookupForType.lookupAll(Object.class));
                        }
                        }
                        result.addAll (all);
                    }
                }
            }
        }
        return result;
    }
    
    private FileObject folderFor (String typeName) {
        FileObject result = Repository.getDefault().getDefaultFileSystem().
                getRoot().getFileObject(rootFolder + "/" + typeName);
        
        if (result != null && !result.isFolder()) {
            throw new IllegalStateException (result.getPath() + " is a " +
                    "file, not a folder");
        }
        
        if (logger.isLoggable(Level.FINEST)) logger.log(Level.FINEST, 
            "Folder for " + typeName + " returns " + 
            (result == null ? "null" : result.getPath()));
        return result;
    }
    
    private Set<String> allNames (Class c) {
        Set <String> s = new HashSet<String>();
        iter (c, s);
        if (logger.isLoggable(Level.FINEST)) logger.log(Level.FINEST, 
            "All names of " + c.getName() + ": " + s);
        return s;
    }
    
    private void iter (Class type, Set <String> s) {
        if (type == null) {
            return;
        }
        s.add(xform(type.getName()));
        for (Class clazz : type.getInterfaces()) {
            iter (clazz, s);
        }
        if (type != Object.class) {
            Class zuper = type.getSuperclass();
            iter (zuper, s);
        }
    }
    
    private String xform(String typeName) {
        return typeName.replace('.', '-');
    }    
}
