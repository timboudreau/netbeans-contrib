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
import org.netbeans.api.jackpot.test.TestUtilities;
import org.netbeans.junit.NbTestCase;

/** Just testing things that are broken.
 *
 * @author Jaroslav Tulach
 */
public class ImportTest extends NbTestCase {
    
    public ImportTest(String testName) {
        super(testName);
    }
    
    protected void setUp() throws Exception {
        TestUtilities.makeScratchDir(this);
    }

    protected void tearDown() throws Exception {
    }

    public void testAddsImportCorrectly() throws Exception {
        String code = 
            "package x.y;\n" +
            "import java.util.Vector;\n" +
            "class ShouldUseList {\n" +
            "  Vector v;\n" +
            "}\n";
        
        String rule = "mapclass java.util.Vector => java.util.LinkedList;\n";
        
        File java = new File(getWorkDir(), "ShouldUseList.java");
        TestUtilities.copyStringToFile(java, code);
        File ruleFile = new File(getWorkDir(), "r.rules");
        TestUtilities.copyStringToFile(ruleFile, rule);
        
        TestUtilities.applyRules(getWorkDir(), ruleFile.toURI().toURL());
        
        String res = TestUtilities.copyFileToString(java);
        
        if (res.indexOf("Vector") >= 0) {
            fail("No vector shall be there: "+ res);
        }
    }
    public void testNoLinesCauseNPE() throws Exception {
        String code = 
            "package x.y;" +
            "import java.util.Vector;" +
            "class ShouldUseList {" +
            "  Vector v;" +
            "}";
        
        String rule = "mapclass java.util.Vector => java.util.LinkedList;\n";
        
        File java = new File(getWorkDir(), "ShouldUseList.java");
        TestUtilities.copyStringToFile(java, code);
        File ruleFile = new File(getWorkDir(), "r.rules");
        TestUtilities.copyStringToFile(ruleFile, rule);
        
        TestUtilities.applyRules(getWorkDir(), ruleFile.toURI().toURL());
        
        String res = TestUtilities.copyFileToString(java);
        
        if (res.indexOf("Vector") >= 0) {
            fail("No vector shall be there: "+ res);
        }
    }
}
