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

package org.netbeans.modules.j2ee.jetty;

import javax.enterprise.deploy.spi.TargetModuleID;
import junit.framework.TestCase;

/**
 *
 * @author novakm
 */
public class JetModuleTest extends TestCase {

    private static JetTarget jt;
    private static String TARG_NAME = "jt_name";
    private static String TARG_DESC = "jt_description";
    private static String TARG_URI = "jt_uri";
    private static String MOD_NAME = "jm_name";
    private static String MOD_ONS = "jm_objectnamestring";
    private static String MOD_CP = "jt_contextPath";
    private static String MOD_WP = "jt_warPath";
    private static String SERVER_ADDR="http://localhost:8080";

    public JetModuleTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    private JetTarget getJetTarget() {
        if (jt == null) {
            jt = new JetTarget(TARG_NAME, TARG_DESC, TARG_URI);
        }
        return jt;
    }

    private JetModule getJetModule() {
        JetTarget targ = getJetTarget();
        return new JetModule(targ, MOD_NAME, MOD_ONS, MOD_CP, MOD_WP);
    }

    /**
     * Test of getName method, of class JetModule.
     */
    public void testGetName() {
        System.out.println("getName");
        JetModule instance = getJetModule();
        String expResult = MOD_NAME;
        String result = instance.getName();
        assertEquals(expResult, result);
    }

    /**
     * Test of getContextPath method, of class JetModule.
     */
    public void testGetContextPath() {
        System.out.println("getContextPath");
        JetModule instance = getJetModule();
        String expResult = MOD_CP;
        String result = instance.getContextPath();
        assertEquals(expResult, result);
    }

    /**
     * Test of getTarget method, of class JetModule.
     */
    public void testGetTarget() {
        System.out.println("getTarget");
        JetModule instance = getJetModule();
        JetTarget expResult = getJetTarget();
        JetTarget result = instance.getTarget();
        assertEquals(expResult, result);
    }

    /**
     * Test of getWarPath method, of class JetModule.
     */
    public void testGetWarPath() {
        System.out.println("getWarPath");
        JetModule instance = getJetModule();
        String expResult = MOD_WP;
        String result = instance.getWarPath();
        assertEquals(expResult, result);
    }

    /**
     * Test of getModuleID method, of class JetModule.
     */
    public void testGetModuleID() {
        System.out.println("getModuleID");
        JetModule instance = getJetModule();
        String expResult = SERVER_ADDR+MOD_CP;
        String result = instance.getModuleID();
        assertEquals(expResult, result);
    }

    /**
     * Test of getWebURL method, of class JetModule.
     */
    public void testGetWebURL() {
        System.out.println("getWebURL");
        JetModule instance = getJetModule();
        String expResult = SERVER_ADDR+MOD_CP;
        String result = instance.getWebURL();
        assertEquals(expResult, result);
    }

    /**
     * Test of getObjectNameString method, of class JetModule.
     */
    public void testGetObjectNameString() {
        System.out.println("getObjectNameString");
        JetModule instance = getJetModule();
        String expResult = MOD_ONS;
        String result = instance.getObjectNameString();
        assertEquals(expResult, result);
    }

    /**
     * Test of getParentTargetModuleID method, of class JetModule.
     */
    public void testGetParentTargetModuleID() {
        System.out.println("getParentTargetModuleID");
        JetModule instance = getJetModule();
        TargetModuleID expResult = null;
        TargetModuleID result = instance.getParentTargetModuleID();
        assertEquals(expResult, result);
    }

    /**
     * Test of getChildTargetModuleID method, of class JetModule.
     */
    public void testGetChildTargetModuleID() {
        System.out.println("getChildTargetModuleID");
        JetModule instance = getJetModule();
        TargetModuleID[] expResult = null;
        TargetModuleID[] result = instance.getChildTargetModuleID();
        assertEquals(expResult, result);
    }

}
