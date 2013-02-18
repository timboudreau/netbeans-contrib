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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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
package org.netbeans.modules.editor.hints.i18n;

import org.netbeans.junit.NbTestCase;
import org.netbeans.modules.java.hints.test.api.HintTest;

/**
 *
 * @author Jan Lahoda
 */
public class I18NCheckerTest extends NbTestCase {
    
    public I18NCheckerTest(String testName) {
        super(testName);
    }

    public void testSimple() throws Exception {
        performAnalysisTest("test/Test.java", "package test; public class Test {private String s = \"s\";}", "0:52-0:55:hint:Hardcoded String");
    }
    
    public void testSimpleNOI18N() throws Exception {
        performAnalysisTest("test/Test.java", "package test; public class Test {\nprivate String s = \"s\";//NOI18N\n}");
    }
    
    public void testSimpleNoNOI18N() throws Exception {
        performAnalysisTest("test/Test.java", "package test; public class Test {\nprivate String s = \"s\";\n//NOI18N\n}", "1:19-1:22:hint:Hardcoded String");
    }
    
    public void testAddToBundle() throws Exception {
        AddToBundleFix.TESTS = true;//XXX
        HintTest.create()
                .input("package test; public class Test {\nprivate String s = \"s\";}")
                .run(I18NChecker.class)
                .findWarning("1:19-1:22:hint:Hardcoded String")
                .applyFix("Create new bundle and replace with localized string")
                .assertOutput("package test; import java.util.ResourceBundle; public class Test { private String s = ResourceBundle.getBundle(\"Bundle.properties\").getString(\"s\");}");
    }
    
    public void testZeroLengthString() throws Exception {
        performAnalysisTest("test/Test.java", "package test; public class Test {\nprivate String s = \"\";\n}");
    }
    
    public void testNoStringLiteral() throws Exception {
        performAnalysisTest("test/Test.java", "package test; public class Test {\nprivate void t() {String s2 = null; String s = s2 + s2;}\n}");
    }
    
    public void testCompound1() throws Exception {
        performAnalysisTest("test/Test.java", "package test; public class Test {\nprivate void t() {String s2 = null; String s = \"x\" + s2 + s2;}\n}", "1:47-1:60:hint:Hardcoded String");
    }
    
    public void testCompound2() throws Exception {
        performAnalysisTest("test/Test.java", "package test; public class Test {\nprivate void t() {String s2 = null; String s = \"\" + s2 + s2;}\n}");
    }
    
    public void testAnnotation1() throws Exception {
        performAnalysisTest("test/Test.java", "package test; @SuppressWarnings(\"something\") public class Test {}\n");
    }
    
    public void testAnnotation2() throws Exception {
        performAnalysisTest("test/Test.java", "package test; @SuppressWarnings({\"something\", \"AAAA\"}) public class Test {}\n");
    }
    
    private void performAnalysisTest(String fileName, String code, String... golden) throws Exception {
        HintTest.create()
                .input(fileName, code)
                .run(I18NChecker.class)
                .assertWarnings(golden);
    }
    
}
