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
import javax.lang.model.element.TypeElement;
import junit.framework.TestCase;

/**
 * @author Jesse Glick
 */
public class LoaderTest extends TestCase {
    
    public LoaderTest(String n) {
        super(n);
    }
    
    private File antJar;
    
    protected void setUp() throws Exception {
        super.setUp();
        antJar = new File(Class.forName("org.apache.tools.ant.Project").getProtectionDomain().getCodeSource().getLocation().toURI());
        assertTrue(antJar.getAbsolutePath(), antJar.isFile());
    }

    public void testBasicUsage() throws Exception {
        new Loader(antJar) {
            protected void run() {
                TypeElement set = elements().getTypeElement("java.util.Set");
                assertEquals("java.util.Set", set.getQualifiedName().toString());
                assertEquals(1, set.getTypeParameters().size());
                TypeElement prj = elements().getTypeElement("org.apache.tools.ant.Project");
                assertNotNull(prj);
            }
        };
    }
    
}
