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
        ruleURL = getClass().getResource("resources/SimplifyBooleanExpressions.rules");
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
        if (TestUtilities.applyRules(getWorkDir(), ruleURL) != 2) {
            fail("a transformation was missed");
        }
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
        if (TestUtilities.applyRules(getWorkDir(), ruleURL) > 0) {
            fail("Conditional was modified");
        }
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
        if (TestUtilities.applyRules(getWorkDir(), ruleURL) > 0) {
            fail("Conditional was modified");
        }
    }
}
