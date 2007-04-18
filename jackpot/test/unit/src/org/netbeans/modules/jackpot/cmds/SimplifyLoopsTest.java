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
