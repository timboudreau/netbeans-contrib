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
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
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
