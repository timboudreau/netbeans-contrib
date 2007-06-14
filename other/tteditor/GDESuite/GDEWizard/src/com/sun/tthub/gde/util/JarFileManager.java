
/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 * 
 * Copyright 2007 Sun Microsystems, Inc. All Rights Reserved
 *
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