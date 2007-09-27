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

package org.netbeans.modules.jackpot;

import java.io.File;
import java.net.URL;
import org.netbeans.api.jackpot.test.TestUtilities;
import org.netbeans.junit.NbTestCase;

/**
 * Verifies that SimplifyBooleanExpressions.rules doesn't modify a conditional expression.
 *
 * @author tball
 */
public class SimplifyBooleanExpressionTest extends NbTestCase {
    URL ruleURL;
    
    public SimplifyBooleanExpressionTest(String testName) {
        super(testName);
    }
    
    protected void setUp() throws Exception {
        TestUtilities.makeScratchDir(this);
        ruleURL = getClass().getResource("scripts/SimplifyBooleanExpressions.rules");
    }

    protected void tearDown() throws Exception {
    }

    /**
     * Tests "if (true) $a => $a;" for single and multi-line statements.
     */
    public void testIfTrue() throws Exception {
        String code = 
            "package x.y;\n" +
            "class IfTrue {\n" +
            "    void test() {\n" +
            "        if (true) System.out.println();\n" +
            "        if (true)\n" +
            "            System.out.println();\n" +
            "    }\n" +
            "}\n";
        
        String golden = 
            "package x.y;\n" +
            "class IfTrue {\n" +
            "    void test() {\n" +
            "        System.out.println();\n" +
            "        System.out.println();\n" +
            "    }\n" +
            "}\n";
        
        File java = new File(getWorkDir(), "IfTrue.java");
        TestUtilities.copyStringToFile(java, code);
        TestUtilities.applyRules(getWorkDir(), ruleURL);
        String result = TestUtilities.copyFileToString(java);
        assertEquals(golden, result);
    }

    public void testIgnoresConditional() throws Exception {
        String code = 
            "package x.y;\n" +
            "class ShouldIgnoreConditional {\n" +
            "    int test(java.awt.List list) {\n" +
            "        int rowIndex = (list.getRows() > 1 ? 1 : 0);\n" +
            "        return rowIndex;\n" +
            "    }\n" +
            "}\n";
        
        File java = new File(getWorkDir(), "ShouldIgnoreConditional.java");
        TestUtilities.copyStringToFile(java, code);
        TestUtilities.applyRules(getWorkDir(), ruleURL);
        String result = TestUtilities.copyFileToString(java);
        assertEquals(code, result); // should be no change
    }
    
    public void testAnotherConditional() throws Exception {
        String code = 
            "package x.y;\n" +
            "import java.util.Vector;\n" +
            "class AnotherConditional {\n" +
            "    public static void main(String[] args) {\n" +
            "        Vector myVector = new Vector(10);\n" +
            "        int i = 0;\n" +
            "        if (myVector.size() == 0)\n" +
            "            i++;\n" +
            "    }\n" +
            "}\n";

        File java = new File(getWorkDir(), "ShouldIgnoreConditional.java");
        TestUtilities.copyStringToFile(java, code);
        TestUtilities.applyRules(getWorkDir(), ruleURL);
        String result = TestUtilities.copyFileToString(java);
        assertEquals(code, result); // should be no change
    }
}
