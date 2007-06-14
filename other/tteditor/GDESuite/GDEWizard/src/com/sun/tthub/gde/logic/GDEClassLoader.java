
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

package com.sun.tthub.gde.logic;

import com.sun.tthub.gde.util.*;

/**
 * 
 * @author Hareesh Ravindran
 */
public final class GDEClassLoader extends MultiClassLoader {
    
    // Initialize a jarResources
    private JarResources jarResources = new JarResources();
    
    // The parent of this class loader is the system class loader.
    public GDEClassLoader() {}    
    
    
    /**
     * This is a package scoped method which will be called by the 
     * GDEClassesManager to fill it with the required jarResources from the
     * GDEFolder
     * 
     */
    protected JarResources getJarResources() { return jarResources; }
    
    /**
     * This method is overridden from the MultiClassLoader class. The multi class
     * loader will use this function to load the required bytes. 
     *
     * @return null if the loading of the class bytes from the jar file failed
     *      due to certain issues like invalid jar file format, insufficient 
     *      previlege to read the jar file etc.
     */
    protected byte[] loadClassBytes(String className) {
        String clsName = formatClassName(className);
        return jarResources.getResource(clsName);
    }    
}
