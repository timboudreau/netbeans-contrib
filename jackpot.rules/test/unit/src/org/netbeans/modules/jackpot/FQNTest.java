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
 * Verify that mapclass converts fully qualified names in source files.
 */
public class FQNTest extends NbTestCase {
    
    public FQNTest(String testName) {
        super(testName);
    }
    
    protected void setUp() throws Exception {
        TestUtilities.makeScratchDir(this);
    }

    protected void tearDown() throws Exception {
    }

    public void testFQNConversion() throws Exception {
        String code = 
            "package x.y;\n\n" +
            "class FQNConversion {\n" +
            "  java.util.Vector v = new java.util.Vector();\n" +
            "}\n";
        
        String rule = "mapclass java.util.Vector => java.util.LinkedList;\n";
        
        String golden = 
            "package x.y;\n\n" +
            "class FQNConversion {\n" +
            "  java.util.LinkedList v = new java.util.LinkedList();\n" +
            "}\n";
        
        File java = new File(getWorkDir(), "FQNConversion.java");
        TestUtilities.copyStringToFile(java, code);
        File ruleFile = new File(getWorkDir(), "r.rules");
        TestUtilities.copyStringToFile(ruleFile, rule);
        URL ruleURL = ruleFile.toURI().toURL();
        
        if (TestUtilities.applyRules(getWorkDir(), ruleURL) != 2) {
            fail("a transformation was missed");
        }
        String result = TestUtilities.copyFileToString(java);
        assertEquals(golden, result);
    }

    public void testStaticFQNConversion() throws Exception {
        String code = 
            "package x.y;\n\n" +
            "class FQNConversion {\n" +
            "  private static final java.util.Vector v = new java.util.Vector();\n" +
            "}\n";
        
        String rule = "mapclass java.util.Vector => java.util.LinkedList;\n";
        
        String golden = 
            "package x.y;\n\n" +
            "class FQNConversion {\n" +
            "  private static final java.util.LinkedList v = new java.util.LinkedList();\n" +
            "}\n";
        
        File java = new File(getWorkDir(), "FQNConversion.java");
        TestUtilities.copyStringToFile(java, code);
        File ruleFile = new File(getWorkDir(), "r.rules");
        TestUtilities.copyStringToFile(ruleFile, rule);
        URL ruleURL = ruleFile.toURI().toURL();
        int count = TestUtilities.applyRules(getWorkDir(), ruleURL);
        String result = TestUtilities.copyFileToString(java);
        if (count != 2)
            fail("expected 2 modifications, got " + count);
        assertEquals(golden, result);
    }

    public void testFQNConversionWithBuildErrors() throws Exception {
        String code = 
            "package x.y;\n\n" +
            "class FQNConversion extends some.unknown.Class {\n" +
            "  private static final java.util.Vector v = new java.util.Vector();\n" +
            "}\n";
        
        String rule = "mapclass java.util.Vector => java.util.LinkedList;\n";
        
        String golden = 
            "package x.y;\n\n" +
            "class FQNConversion extends some.unknown.Class {\n" +
            "  private static final java.util.LinkedList v = new java.util.LinkedList();\n" +
            "}\n";
        
        File java = new File(getWorkDir(), "FQNConversion.java");
        TestUtilities.copyStringToFile(java, code);
        File ruleFile = new File(getWorkDir(), "r.rules");
        TestUtilities.copyStringToFile(ruleFile, rule);
        URL ruleURL = ruleFile.toURI().toURL();
        
        int count = TestUtilities.applyRules(getWorkDir(), ruleURL, true);
        String result = TestUtilities.copyFileToString(java);
        if (count != 2)
            fail("expected 2 modifications, got " + count);
        assertEquals(golden, result);
    }
}
