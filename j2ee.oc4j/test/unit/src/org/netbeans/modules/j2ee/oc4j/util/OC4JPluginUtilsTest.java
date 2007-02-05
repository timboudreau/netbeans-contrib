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

package org.netbeans.modules.j2ee.oc4j.util;

import java.io.File;
import java.util.Collection;
import org.netbeans.junit.NbTestCase;

/**
 *
 * @author pblaha
 */
public class OC4JPluginUtilsTest extends NbTestCase {
    File homeDir;
    
    public OC4JPluginUtilsTest(String testName) {
        super(testName);
    }
    
    protected void setUp() throws Exception {
        homeDir = new File(getDataDir() + File.separator + "oc4jhome");
    }
    
    protected void tearDown() throws Exception {
    }
    
    public void testPathForElements() {
        System.out.println("pathForElements");
        assertEquals(homeDir + OC4JPluginUtils.CONFIG_DIR + File.separator + "rmi.xml", OC4JPluginUtils.getPathForElement(homeDir.getAbsolutePath(), "rmi-config").iterator().next());
        assertTrue(OC4JPluginUtils.getPathForElement(homeDir.getAbsolutePath(), "jms-config").isEmpty());
        assertTrue(OC4JPluginUtils.getPathForElement(homeDir.getAbsolutePath(), "web-site").size() == 2);
    }
    
    public void testRMIPort() {
        System.out.println("RMIPort");
        assertEquals(23791, OC4JPluginUtils.getAdminPort(homeDir.getAbsolutePath()));
    }
    
    public void testHttpPort() {
        System.out.println("HttpPort");
        assertEquals(23791, OC4JPluginUtils.getAdminPort(homeDir.getAbsolutePath()));
    }
    
    public void testWebSites() {
        System.out.println("webSites");
        Collection<String> webSites = OC4JPluginUtils.getWebSites(homeDir.getAbsolutePath());
        assertTrue("server.xml doesn't include default web site", webSites.contains("default"));
        assertTrue("server.xml doesn't include secure web site", webSites.contains("secure"));
    }
    
}
