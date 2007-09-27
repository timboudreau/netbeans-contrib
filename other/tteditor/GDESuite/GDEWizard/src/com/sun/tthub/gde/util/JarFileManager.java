
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
 * Copyright 2007 Sun Microsystems, Inc. All Rights Reserved.
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
 * made subject to such option by the copyright holder. *
 */

package com.sun.tthub.gde.util;

import com.sun.tthub.gde.logic.GDEAppContext;
import com.sun.tthub.gde.logic.GDEClassLoader;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.jar.JarFile;
import java.util.jar.JarEntry;


import com.sun.tthub.gdelib.GDEException;

/**
 *
 * @author Hareesh Ravindran
 */
public class JarFileManager {
    
    public static final int TYPE_CLASS = 0;
    public static final int TYPE_INTERFACE = 1;
    public static final int TYPE_ALL = 2;
    
    protected File jarFile;
    
    
    /** Creates a new instance of JarFileManager */
    public JarFileManager() {}
    public JarFileManager(File file) { this.jarFile = file; }
    
    public void setJarFile(File file) { this.jarFile = file; }
    public File getJarFile() { return this.jarFile; }
    
    
    public Collection loadClassesForJar() throws GDEException {
        Collection coll = new ArrayList();
        // if the specified jar file does not exist, throw an exception.
        if(!jarFile.exists()  || !jarFile.canRead()) {
            throw new GDEFileMngmtException("The jar file '" + 
                    jarFile.getAbsolutePath() + 
                    "' does not exist or is not readable");                        
        }
        JarFile jFile = null;
        try {
            jFile = new JarFile(jarFile, false, JarFile.OPEN_READ);                        
            Enumeration e = jFile.entries();
            while (e.hasMoreElements()) {
                JarEntry jarEntry = (JarEntry) e.nextElement();
                if(jarEntry.isDirectory()) // skip if the jar entry is a directory.
                    continue;                
                String jarEntryName = jarEntry.getName();
                if(jarEntryName.endsWith(".class")) {           
                    String str = System.getProperty("file.separator");                    
                    // remove the '.class' at the end of the file name.
                    jarEntryName = jarEntryName.substring(0, 
                                    jarEntryName.length() - 6);
                    String finalName = jarEntryName.replace(str, ".");
                    coll.add(finalName);                    
                }
            }            
            jFile.close();
        } catch(IOException ex) {
            throw new GDEFileMngmtException("Failed to open the jar file", ex);
        }
        return coll;
    }
}    