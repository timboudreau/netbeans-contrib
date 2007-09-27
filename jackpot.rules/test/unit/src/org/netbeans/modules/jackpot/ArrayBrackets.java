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
 * Verify that a block with an array declaration retains its brackets.
 */
public class ArrayBrackets extends NbTestCase {
    
    public ArrayBrackets(String testName) {
        super(testName);
    }
    
    protected void setUp() throws Exception {
        TestUtilities.makeScratchDir(this);
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
        
        TestUtilities.applyRules(getWorkDir(), ruleURL);
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
        
        TestUtilities.applyRules(getWorkDir(), ruleURL);
        String result = TestUtilities.copyFileToString(java);
        assertEquals(golden, result);
    }
}
