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
 * Verify that a block with an enhanced for statement is formatted correctly.
 */
public class EnhancedForTest extends NbTestCase {
    
    public EnhancedForTest(String testName) {
        super(testName);
    }
    
    protected void setUp() throws Exception {
        TestUtilities.makeScratchDir(this);
    }

    protected void tearDown() throws Exception {
    }

    public void testEnhancedFor() throws Exception {
        String code = 
            "package x.y;\n\n" +
            "class EnhancedFor {\n" +
            "    public static void main(String[] args) {\n" +
            "        int count;\n" +
            "        System.out.println(\"args\");\n" +
            "        count = 0;\n\n" +
            "        for (String arg : args) {\n" +
            "            System.out.println(arg);\n" +
            "            count++;\n" +
            "        }\n" +
            "    }\n" +
            "}\n";
        
        String rule = "{ $t $v; $intr$; $v = $e; $q$; } => { $intr$; $t $v = $e; $q$; } :: !assignedIn($v, $intr$);";
        
        String golden = 
            "package x.y;\n\n" +
            "class EnhancedFor {\n" +
            "    public static void main(String[] args) {\n" +
            "        System.out.println(\"args\");\n" +
            "        int count = 0;\n\n" +
            "        for (String arg : args) {\n" +
            "            System.out.println(arg);\n" +
            "            count++;\n" +
            "        }\n" +
            "    }\n" +
            "}\n";
        
        File java = new File(getWorkDir(), "EnhancedFor.java");
        TestUtilities.copyStringToFile(java, code);
        File ruleFile = new File(getWorkDir(), "r.rules");
        TestUtilities.copyStringToFile(ruleFile, rule);
        URL ruleURL = ruleFile.toURI().toURL();
        
        TestUtilities.applyRules(getWorkDir(), ruleURL);
        String result = TestUtilities.copyFileToString(java);
        assertEquals(golden, result);
    }
}
