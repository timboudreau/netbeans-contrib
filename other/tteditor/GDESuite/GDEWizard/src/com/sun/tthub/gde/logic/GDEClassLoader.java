
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
