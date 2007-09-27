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

package org.netbeans.modules.jackpot.cmds;

import java.io.File;
import org.netbeans.api.jackpot.test.TestUtilities;
import org.netbeans.junit.NbTestCase;

/**
 * Verifies that SimplifyBooleanExpressions.rules doesn't modify a conditional expression.
 *
 * @author tball
 */
public class SimplifyLoopsTest extends NbTestCase {
    private static final String transformer = "org.netbeans.modules.jackpot.cmds.SimplifyLoops";
    
    public SimplifyLoopsTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
        TestUtilities.makeScratchDir(this);
        System.setProperty("netbeans.user", getWorkDir().getAbsolutePath());
    }

    protected void tearDown() throws Exception {
    }

    /**
     * Verifies that an if statement surround a while loop's body gets
     * lifted into the while loop's condition correctly.
     */
    public void testIfLifting() throws Exception {
        // Strange formatting taken from test case (issue 86092).
        String code = 
            "package x.y;\n" +
            "class IfLifting {\n" +
            "    void test() {\n" +
            "        while( remaining() >= 2 )\n" +
            "         {\n" +
            "               if( ( get() == 0 ) && ( get() == 0 ) )\n" +
            "               {\n" +
            "                   break;\n" +
            "               }\n" +
            "         }\n" +
            "    }\n" +
            "    int remaining() { return 0; }\n" +
            "    int get() { return 0; }\n" +
            "}\n";
        
        String golden = 
            "package x.y;\n" +
            "class IfLifting {\n" +
            "    void test() {\n" +
            "        while( remaining() >= 2 && !(get() == 0) || !(get() == 0) )\n" +
            "         {\n" +
            "         }\n" +
            "    }\n" +
            "    int remaining() { return 0; }\n" +
            "    int get() { return 0; }\n" +
            "}\n";
        
        File java = new File(getWorkDir(), "IfLifting.java");
        TestUtilities.copyStringToFile(java, code);
        if (TestUtilities.applyTransformer(getWorkDir(), transformer) == 0) {
            fail("transformation failed");
        }
        String result = TestUtilities.copyFileToString(java);
        assertEquals(golden, result);
    }
    
    public void testNestedLoopLabels() throws Exception {
        String code =
                "package x.y;\n" +
                "class NestedLoopLabels {\n" +
                "    String unUTF(String s) {\n" +
                "        int limit = s.length();\n" +
                "        char c;\n" +
                "        simpleTest: { // do simple cases quickly\n" +
                "            for(int i = 0; i<limit; i++)\n" +
                "                if((c = s.charAt(i))>0x7F)\n" +
                "                    if(c>0xFF) break; // too big\n" +
                "                    else break simpleTest; // maybe UTF-8\n" +
                "            return \"not UTF-8\";\n" +
                "        }\n" +
                "        return \"may be UTF-8\";\n" +
                "    }\n" +
                "}\n";

        File java = new File(getWorkDir(), "NestedLoopLabels.java");
        TestUtilities.copyStringToFile(java, code);
        if (TestUtilities.applyTransformer(getWorkDir(), transformer) > 0) {
            fail("transformation failed");
        }
        String result = TestUtilities.copyFileToString(java);
        assertEquals(code, result);
    }
}
