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
 * Verify that a block with an array declaration retains its brackets.
 */
public class ArrayBrackets extends NbTestCase {
    
    public ArrayBrackets(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }

    public void testArrayBrackets() throws Exception {
        String code = 
            "package x.y;\n\n" +
            "class ArrayBrackets {\n" +
            "    public static void main(String[] args) {\n" +
            "        String[] items = new String[]{\"Helfrichi\", \"Fire Goby\", \"Butterfish\"};\n\n" +
            "        int count;\n\n" +
            "        System.out.println(\"args\");\n" +
            "        count = 0;\n\n" +
            "        for (String arg : args) {\n" +
            "            System.out.println(arg);\n" +
            "            count++;\n" +
            "        }\n" +
            "    }\n" +
            "}\n";
        
        String rule = "{ $p$; $t $v; $intr$; $v = $e; $q$; } => { $p$; $intr$; $t $v = $e; $q$; } :: !assignedIn($v, $intr$);";
        
        String golden = 
            "package x.y;\n\n" +
            "class ArrayBrackets {\n" +
            "    public static void main(String[] args) {\n" +
            "        String[] items = new String[]{\"Helfrichi\", \"Fire Goby\", \"Butterfish\"};\n\n" +
            "        System.out.println(\"args\");\n" +
            "        int count = 0;\n\n" +
            "        for (String arg : args) {\n" +
            "            System.out.println(arg);\n" +
            "            count++;\n" +
            "        }\n" +
            "    }\n" +
            "}\n";
        
        File java = new File(getWorkDir(), "ArrayBrackets.java");
        TestUtilities.copyStringToFile(java, code);
        File ruleFile = new File(getWorkDir(), "r.rules");
        TestUtilities.copyStringToFile(ruleFile, rule);
        URL ruleURL = ruleFile.toURI().toURL();
        
        if (TestUtilities.applyRules(getWorkDir(), ruleURL) != 1) {
            fail("transformation failed");
        }
        String result = TestUtilities.copyFileToString(java);
        assertEquals(golden, result);
    }

    public void testMultiArrayBrackets() throws Exception {
        String code = 
            "package x.y;\n\n" +
            "class MultiArrayBrackets {\n" +
            "    public static void main(String[] args) {\n" +
            "        int[][] items = new int[][]{{1, 2}, {3, 4}};\n\n" +
            "        int count;\n\n" +
            "        System.out.println(\"items\");\n" +
            "        count = 0;\n" +
            "    }\n" +
            "}\n";
        
        String rule = "{ $p$; $t $v; $intr$; $v = $e; $q$; } => { $p$; $intr$; $t $v = $e; $q$; } :: !assignedIn($v, $intr$);";
        
        String golden = 
            "package x.y;\n\n" +
            "class MultiArrayBrackets {\n" +
            "    public static void main(String[] args) {\n" +
            "        int[][] items = new int[][]{{1, 2}, {3, 4}};\n\n" +
            "        System.out.println(\"items\");\n" +
            "        int count = 0;\n" +
            "    }\n" +
            "}\n";
        
        File java = new File(getWorkDir(), "MultiArrayBrackets.java");
        TestUtilities.copyStringToFile(java, code);
        File ruleFile = new File(getWorkDir(), "r.rules");
        TestUtilities.copyStringToFile(ruleFile, rule);
        URL ruleURL = ruleFile.toURI().toURL();
        
        if (TestUtilities.applyRules(getWorkDir(), ruleURL) != 1) {
            fail("transformation failed");
        }
        String result = TestUtilities.copyFileToString(java);
        assertEquals(golden, result);
    }
}
