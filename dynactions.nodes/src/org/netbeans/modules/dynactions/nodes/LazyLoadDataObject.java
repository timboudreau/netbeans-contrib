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

package org.netbeans.modules.dynactions.nodes;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import org.netbeans.api.objectloader.CacheStrategies;
import org.netbeans.api.objectloader.CacheStrategy;
import org.netbeans.api.objectloader.ObjectLoader;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.MultiDataObject;
import org.openide.loaders.MultiFileLoader;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;

/**
 * DataObject which uses an ObjectLoader to load a typed object from the
 * file represented by this data object on a background thread.  Use in
 * conjunction with DynamicActionsDataNode to create DataObjects/Nodes for
 * files that contain large data sets.
 *
 * @author Tim Boudreau
 */
public abstract class LazyLoadDataObject<T> extends MultiDataObject {
    /**
     * The ObjectLoader that will load the object in the background.
     */
    protected final ObjectLoader<T> ldr;
    /**
     * Content for this DataObject's Lookup.  Add to/remove from it freely.
     */
    protected final InstanceContent content = new InstanceContent();
    private final Lookup lkp;
    /**
     * Create a new data object.
     * @param fo The file
     * @param fileLoader The loader that created this data object
     * @param type The type of the object represented by the file
     * @param strategy A factory for Reference objects that determines how
     * the loaded object will be cached.
     * @throws org.openide.loaders.DataObjectExistsException
     */
    public LazyLoadDataObject(FileObject fo, MultiFileLoader fileLoader, 
           Class type, CacheStrategy strategy) throws DataObjectExistsException{
        super (fo, fileLoader);
        this.ldr = new OL(type, strategy);
        lkp = new ProxyLookup (new AbstractLookup(content), 
                fo.canRead() ? Lookups.fixed(this, ldr) : Lookups.singleton(this));
    }

    public LazyLoadDataObject(FileObject fo, MultiFileLoader ldr, Class type) 
            throws DataObjectExistsException {
        this (fo, ldr, type, CacheStrategies.WEAK);
    }
    
    @Override
    public final Lookup getLookup() {
        return lkp;
    }
    
    protected Class<T> type() {
        return ldr.type();
    }
    
    /**
     * Convenience method called when an object has been loaded from disk.
     * @param object The object that was loaded
     */
    protected void loaded (T object) {
        
    }

    /**
     * Load the object from the primary file's input stream.  Called on a
     * background thread.  The default implementation tries to read an 
     * object from an ObjectInputStream.
     * 
     * @param stream The input stream for the primary file
     * @return The loaded object.
     * @throws java.io.IOException if there is an error loading
     */
    protected abstract T load (InputStream stream) throws IOException; 
    /*{
        System.err.println("LazyLoadDataObject.load");
        ObjectInputStream in = new ObjectInputStream(
                new BufferedInputStream(stream));
//        ClassLoader all = Lookup.getDefault().lookup(ClassLoader.class);
//        ClassLoader curr = Thread.currentThread().getContextClassLoader();
//        Thread.currentThread().setContextClassLoader(all);
        try {
            Object result = in.readObject();
            System.err.println("Read " + result);
            loaded ((T) result);
            return (T) result;
        } catch (ClassNotFoundException ex) {
            Exceptions.printStackTrace(ex);
            throw new IOException (ex);
        } finally {
            in.close();
//            Thread.currentThread().setContextClassLoader(curr);
        }
    }
     */ 
    
    private final class OL extends ObjectLoader<T> {
        private OL(Class<T> type, CacheStrategy strategy) {
            super (type, strategy);
        }

        @Override
        protected T load() throws IOException {
            FileObject fob = getPrimaryFile();
            if (fob.canRead()) {
                Object result = LazyLoadDataObject.this.load (fob.getInputStream());
                if (!super.type().isInstance(result)) {
                    throw new ClassCastException("Serialized instance " +
                            "is of " + "type " + result.getClass() + 
                            " not the expected " + " type " + super.type());
                }
                loaded ((T) result);
                return (T) result;
            } else {
                return null;
            }
        }
    }
}
