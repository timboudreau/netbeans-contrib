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

package org.netbeans.modules.j2ee.oc4j;

import java.net.URL;
import org.netbeans.junit.NbTestCase;

/**
 *
 * @author pblaha
 */
public class OC4JClassLoaderTest extends NbTestCase {
   
    static final String OC4J_HOME = "/home/pblaha/servers/oc4j";
    
public OC4JClassLoaderTest(String testName) {
        super(testName);
    }
    
    protected void setUp() throws Exception {
    }
    
    protected void tearDown() throws Exception {
    }
    
    public void testGetInstance() {
        System.out.println("getInstance");
        OC4JClassLoader instance1 = OC4JClassLoader.getInstance(OC4J_HOME);
        assertNotNull(instance1);
        OC4JClassLoader instance2 = OC4JClassLoader.getInstance(OC4J_HOME);
        assertSame(instance2, instance1);
    }
    
    public void testUpdateLoader() throws Exception {
        System.out.println("updateLoader");
        OC4JClassLoader loader = OC4JClassLoader.getInstance(OC4J_HOME);
        URL[] urls = loader.getURLs();
        for (int i = 0; i < urls.length; i++) {
            System.out.println(urls[i]);
        }
        
        // load class
        Class c = loader.loadClass("oracle.oc4j.admin.deploy.api.J2EEDeploymentManager");
        assertNotNull(c);
        ClassLoader old = Thread.currentThread().getContextClassLoader();
        loader.updateLoader();
        ClassLoader n = Thread.currentThread().getContextClassLoader();
        assertNotSame(old, n);

    }
    
}
