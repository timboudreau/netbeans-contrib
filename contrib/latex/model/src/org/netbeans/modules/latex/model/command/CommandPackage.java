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
 * Contributor(s):
 *
 * The Original Software is the LaTeX module.
 * The Initial Developer of the Original Software is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002-2007.
 * All Rights Reserved.
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
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.model.command;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.xml.parsers.ParserConfigurationException;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.Repository;
import org.openide.xml.EntityCatalog;
import org.xml.sax.SAXException;

/**
 *
 * @author Jan Lahoda
 */
public final class CommandPackage extends NamedAttributableWithSubElements {
    
    private static final boolean debug = Boolean.getBoolean("netbeans.latex.commandpackages.debug");
    private static final boolean timeDebug =    Boolean.getBoolean("netbeans.latex.commandpackages.time.debug")
                                             || Boolean.getBoolean("netbeans.latex.commandpackages.debug");

    private static final String DEFAULT_DOCUMENT_CLASS = "default-latex-docclass"; //TODO: find some better place and make it customizable
    
    public static final int PACKAGE = 1;
    public static final int DOCUMENT_CLASS = 2;
    
    private int type = 0;
    
    private Map<String, Option> options;
    private Set<String>              includes;
    
    /*package private*/void setType(int type) {
        this.type = type;
    }
    
    /** Creates a new instance of Package */
    /*package private*/ CommandPackage() {
    }
    
    public int getType() {
        return type;
    }
    
    public synchronized Map<String, Option> getOptions() {
        if (options == null)
            return options = new HashMap<String, Option>();
        
        return options;
    }
    
    public synchronized Set<String> getIncludes() {
        if (includes == null)
            return includes = new HashSet<String>();

        return includes;
    }
    
    private static Map<String, CommandPackage> name2documentClass = null;
    private static Map<String, CommandPackage> name2package = null;
    
    private static synchronized Map<String, CommandPackage> getName2DocumentClass() {
        if (name2documentClass == null) {
            load();
        }
        
        return name2documentClass;
    }
    
    private static synchronized Map<String, CommandPackage> getName2Package() {
        if (name2package == null) {
            load();
        }
        
        return name2package;
    }
    
    public static CommandPackage getDefaultDocumentClass() {
        CommandPackage cp = getCommandPackageForName(DEFAULT_DOCUMENT_CLASS);
        
        if (cp.getType() != DOCUMENT_CLASS)
            return null;
        
        return cp;
    }

    public static CommandPackage getCommandPackageForName(String name) {
        CommandPackage result = getName2DocumentClass().get(name);
        
        if (result != null)
            return result;
        
        return getName2Package().get(name);
    }

    /**Returns the collection of names of known document classes.
     *
     * @returns Collection<String> of names of known document classes.
     */
    public static synchronized Collection getKnownDocumentClasses() {
        return Collections.unmodifiableCollection(getName2DocumentClass().keySet());
    }
    
    /**Returns the collection of names of known packages.
     *
     * @returns Collection<String> of names of known packages.
     */
    public static synchronized Collection getKnownPackages() {
        return Collections.unmodifiableCollection(getName2Package().keySet());
    }
    
    private static final String FILE_ROOT = "latex/commands2";
    
    private static synchronized void load() {
        long start = System.currentTimeMillis();
        
        name2documentClass = new HashMap<String, CommandPackage>();
        name2package = new HashMap<String, CommandPackage>();
        
        FileObject root = Repository.getDefault().getDefaultFileSystem().findResource(FILE_ROOT);
        FileObject[] children = root.getChildren();
        
        for (int cntr = 0; cntr < children.length; cntr++) {
            if (children[cntr].getAttribute("command-package") == null)
                continue;
            
            loadCommandPackage(children[cntr]);
            
            if (debug)
                System.err.println("found command package: " + children[cntr]);
        }
        
        long end = System.currentTimeMillis();
        
        if (timeDebug)
            System.err.println("Loading of command packages took: " + (end - start));
    }
    
    private static synchronized void loadCommandPackage(FileObject fo) {
        try {
            Command_descriptionHandlerImpl handler = new Command_descriptionHandlerImpl();
            
            new Command_descriptionParser(handler, EntityCatalog.getDefault()).parse(fo.getURL());
            
            CommandPackage pack = handler.getCommandPackage();
            
            switch (pack.getType()) {
                case CommandPackage.PACKAGE:
                    name2package.put(pack.getName(), pack);
                    break;
                case CommandPackage.DOCUMENT_CLASS:
                    name2documentClass.put(pack.getName(), pack);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown command package type: " + pack.getType());
            }
        } catch(IOException e) {
            e.printStackTrace(System.err);
        } catch(SAXException e) {
            e.printStackTrace(System.err);
        } catch(ParserConfigurationException e) {
            e.printStackTrace(System.err);
        }
    }
}
