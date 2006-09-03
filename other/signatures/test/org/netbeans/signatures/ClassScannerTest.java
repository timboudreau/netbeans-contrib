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
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.signatures;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import junit.framework.TestCase;

/**
 * @author Jesse Glick
 */
public class ClassScannerTest extends TestCase {
    
    public ClassScannerTest(String n) {
        super(n);
    }
    
    private File antJar;
    private File antModuleJar;
    
    protected void setUp() throws Exception {
        super.setUp();
        antJar = new File(Class.forName("org.apache.tools.ant.Project").getProtectionDomain().getCodeSource().getLocation().toURI());
        assertTrue(antJar.getAbsolutePath(), antJar.isFile());
        antModuleJar = new File(antJar.getParentFile().getParentFile().getParentFile().getParentFile(), "nbbuild/netbeans/ide8/modules/org-apache-tools-ant-module.jar");
        assertTrue(antModuleJar.getAbsolutePath(), antModuleJar.isFile());
    }

    public void testFindPackages() throws Exception {
        Collection<String> antClasses = ClassScanner.findTopLevelClasses(true, Collections.singleton(antJar));
        //System.out.println(antClasses);
        assertTrue(antClasses.contains("org.apache.tools.ant.Project"));
        assertTrue(antClasses.contains("org.apache.tools.ant.filters.TokenFilter$Filter"));
        Collection<String> antModuleClasses = ClassScanner.findTopLevelClasses(true, Collections.singleton(antModuleJar));
        assertFalse(antModuleClasses.contains("org.apache.tools.ant.module.run.StandardLogger"));
        //System.out.println(antModuleClasses);
        antModuleClasses = ClassScanner.findTopLevelClasses(false, Collections.singleton(antModuleJar));
        assertTrue(antModuleClasses.contains("org.apache.tools.ant.module.run.StandardLogger"));
        //System.out.println(antModuleClasses);
    }
    
}
