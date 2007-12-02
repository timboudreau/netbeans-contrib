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
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */

package org.netbeans.modules.javahints;

import com.sun.source.util.TreePath;
import java.util.List;
import org.netbeans.api.java.source.CompilationInfo;
import org.netbeans.modules.java.hints.infrastructure.TreeRuleTestBase;
import org.netbeans.spi.editor.hints.ErrorDescription;
import org.openide.util.NbBundle;

/**
 *
 * @author lahvac
 */
public class NPECheckTest extends TreeRuleTestBase {
    
    public NPECheckTest(String testName) {
        super(testName);
    }

    public void testSimpleNull1() throws Exception {
        performAnalysisTest("test/Test.java", "package test; class Test {private void te|st() {Object o; o = null; o.toString();}}", "0:67-0:77:verifier:DN");
    }
    
    public void testSimpleNull2() throws Exception {
        performAnalysisTest("test/Test.java", "package test; class Test {private void te|st() {Object o = null; o.toString();}}", "0:64-0:74:verifier:DN");
    }
    
    public void testIf1() throws Exception {
        performAnalysisTest("test/Test.java", "package test; class Test {private void te|st(Object o) {if (o == null) {o.toString();}}}", "0:71-0:81:verifier:DN");
    }
    
    public void testIf2() throws Exception {
        performAnalysisTest("test/Test.java", "package test; class Test {private void te|st(Object o) {if (o == null) {o = \"\";} o.length();}}");
    }
    
    public void testIf3() throws Exception {
        performAnalysisTest("test/Test.java", "package test; class Test {private void te|st() {String s = null; if (s == null) {s = \"\";} s.length();}}");
    }
    
    public void testIf4() throws Exception {
        performAnalysisTest("test/Test.java", "package test; class Test {private void te|st() {String s = null; if (s == null) {s = \"\";} else {s = \"\";} s.length();}}");
    }
    
    public void testIf5() throws Exception {
        performAnalysisTest("test/Test.java", "package test; class Test {private void te|st() {String s = null; if (s == null) {s = \"\";} else {s = null;} s.length();}}", "0:106-0:114:verifier:Possibly Dereferencing null");
    }
    
    public void testIf6() throws Exception {
        performAnalysisTest("test/Test.java", "package test; class Test {private void te|st(Object o) {if (null == o) {o.toString();}}}", "0:71-0:81:verifier:DN");
    }
    
    public void testIf7() throws Exception {
        performAnalysisTest("test/Test.java", "package test; class Test {private void te|st() {String o = null; if (o != null) {o.toString();}}}");
    }
    
    public void testIf8() throws Exception {
        performAnalysisTest("test/Test.java", "package test; class Test {private void te|st() {String o = null; if (null != o) {o.toString();}}}");
    }
    
    public void testIf9() throws Exception {
        performAnalysisTest("test/Test.java",
                            "package test; class Test {private void te|st(String s) {if (s == null) {} s.length();}}}",
                            "0:73-0:81:verifier:Possibly Dereferencing null");
    }
    
    public void testIfa() throws Exception {
        performAnalysisTest("test/Test.java",
                            "package test; class Test {private void te|st(String s1, String s2) {if (s1 == null) {s1 = s2;} s1.length();}}}");
    }
    
    public void testIfb() throws Exception {
        performAnalysisTest("test/Test.java",
                            "package test; class Test {private void te|st(@Null String s) {if (s == null) {throw new UnsupportedOperationException();} s.length();}} @interface Null {}}");
    }
    
    public void testIfc() throws Exception {
        performAnalysisTest("test/Test.java",
                            "package test; class Test {\n"+
                            "    private void te|st(int i, @CheckForNull String o) {\n" +
                            "        if (i > 2 && o != null && o.length() > 2) {\n" +
                            "        }\n" +
                            "    }\n" +
                            "    @interface CheckForNull{}\n" +
                            "}\n");
    }
    
    public void testIfd() throws Exception {
        performAnalysisTest("test/Test.java",
                            "package test; class Test { private void test(int i, @CheckForNull String o) {\n" +
                            "        if (i > 2 || o == null || o.length() > 2) {\n" +
                            "        }\n" +
                            "    }\n" +
                            "    @interface CheckForNull{}\n" +
                            "}\n", 42);
    }
    
    public void testTernary1() throws Exception {
        performAnalysisTest("test/Test.java", "package test; class Test {private void te|st(int i) {String s = i == 0 ? \"\" : null; s.length();}}");
    }
    
    public void testTernary2() throws Exception {
        performAnalysisTest("test/Test.java",
                            "package test; class Test {private void te|st(String s) {Object o = s == null ? \"\" : s; s = s.toString();}}",
                            "0:90-0:100:verifier:Possibly Dereferencing null");
    }
    
    public void testTernary3() throws Exception {
        performAnalysisTest("test/Test.java",
                            "package test;\n" +
                            "class Test {\n" +
                            "    public void te|st() {" +
                            "        String e = null;" +
                            "        String f = e == null ? \"\" : e.trim();" +
                            "    }" +
                            "}");
    }
    
    public void testNewClass() throws Exception {
        performAnalysisTest("test/Test.java", "package test; class Test {private void te|st() {String s = null; if (s == null) {s = new String(\"\");} s.length();}}");
    }
    
    public void testCheckForNull1() throws Exception {
        performAnalysisTest("test/Test.java", "package test; class Test {private void te|st() {String s = get(); s.length();} @Nullable private String get() {return \"\";} @interface Nullable {}}", "0:65-0:73:verifier:Possibly Dereferencing null");
    }
    
    public void testCheckForNull2() throws Exception {
        performAnalysisTest("test/Test.java", "package test; class Test {private void te|st() {s.length();} @Nullable private String s; @interface Nullable {}}", "0:47-0:55:verifier:Possibly Dereferencing null");
    }
    
    public void testAssignNullToNotNull() throws Exception {
        performAnalysisTest("test/Test.java", "package test; class Test {private void te|st() {s = null;} @NotNull private String s; @interface NotNull {}}", "0:47-0:55:verifier:ANNNV");
    }
    
    public void testPossibleAssignNullToNotNull() throws Exception {
        performAnalysisTest("test/Test.java", "package test; class Test {private void te|st(int i) {String s2 = null; if (i == 0) {s2 = \"\";} s = s2;} @NotNull private String s; @interface NotNull {}}", "0:93-0:99:verifier:PANNNV");
    }
    
    public void testNullCheckAnd1() throws Exception {
        performAnalysisTest("test/Test.java", "package test; class Test {private void te|st() {String s = null; if (s != null && s.length() > 0) {}}}");
    }
    
    public void testNullCheckAnd2() throws Exception {
        performAnalysisTest("test/Test.java", "package test; class Test {private void te|st() {String s = null; if (s == null && s.length() > 0) {}}}", "0:81-0:89:verifier:DN");
    }
    
    public void testNullCheckOr1() throws Exception {
        performAnalysisTest("test/Test.java", "package test; class Test {private void test() {String s = null; if (s != null || s.length() > 0) {}}}", 89 - 48, "0:81-0:89:verifier:DN");
    }
    
    public void testNullCheckOr2() throws Exception {
        performAnalysisTest("test/Test.java", "package test; class Test {private void test() {String s = null; if (s == null || s.length() > 0) {}}}", 89 - 48);
    }
    
    public void testContinue1() throws Exception {
        performAnalysisTest("test/Test.java",
                            "package test; class Test {private void te|st(String[] sa) {for (String s : sa) {if (s == null) continue; s.length();}}}");
    }
    
    public void testCondition1() throws Exception {
        performAnalysisTest("test/Test.java",
                            "package test; class Test {private void test(String sa) {boolean b = sa != null && (sa.length() == 0 || sa.length() == 1);}}", 71 - 30);
    }
    
    public void testCondition2() throws Exception {
        performAnalysisTest("test/Test.java",
                            "package test; class Test {private void test(String sa) {boolean b = sa == null || (sa.length() == 0 && sa.length() == 1);}}", 71 - 30);
    }
    
    public void testCondition3() throws Exception {
        performAnalysisTest("test/Test.java",
                            "package test; class Test {private void te|st(int i) {Object o2 = i < 1 ? null : \"\"; boolean b = true && o2 != null && o2.toString() != \"\";}}");
    }
    
    public void testWhileAndIf() throws Exception {
        performAnalysisTest("test/Test.java",
                            "package test;\n" +
                            "class Test {\n" +
                            "    private void tes|t(int i, boolean b, Object o2) {\n" +
                            "        Object o = null;\n" +
                            "        while (--i > 0) {\n" +
                            "            if (b) {\n" +
                            "                o = o2;\n" +
                            "            }\n" +
                            "        }\n" +
                            "        o.toString();\n" +
                            "    }" +
                            "}",
                            "9:8-9:18:verifier:DN");
    }
    
    public void testWhile1() throws Exception {
        performAnalysisTest("test/Test.java",
                            "package test;\n" +
                            "class Test {\n" +
                            "    private void tes|t(String o) {\n" +
                            "        while (o != null && o.length() > 1) {\n" +
                            "            o = o.substring(1);\n" +
                            "        }\n" +
                            "        o.toString();\n" +
                            "    }" +
                            "}",
                            "6:8-6:18:verifier:Possibly Dereferencing null");
    }
    
    public void testWhile2() throws Exception {
        performAnalysisTest("test/Test.java",
                            "package test;\n" +
                            "class Test {\n" +
                            "    private void tes|t(@CheckForNull String o) {\n" +
                            "        while (o != null) {\n" +
                            "            o = o.substring(1);\n" +
                            "        }\n" +
                            "    }" +
                            "    @interface CheckForNull {}\n" +
                            "}");
    }
    
    public void testParameter1() throws Exception {
        performAnalysisTest("test/Test.java",
                            "package test; class Test {private void te|st(int i) {String s = null; ttt(s);} private void ttt(@NotNull String s){}} @interface NotNull {}",
                            "0:73-0:74:verifier:ERR_NULL_TO_NON_NULL_ARG");
    }
    
    public void testParameter2() throws Exception {
        performAnalysisTest("test/Test.java",
                            "package test; class Test {private void te|st(int i) {ttt(t());} private void ttt(@NotNull String s){} private @CheckForNull String t() {return \"\";} } @interface NotNull {} @interface CheckForNull {}",
                            "0:56-0:59:verifier:ERR_POSSIBLENULL_TO_NON_NULL_ARG");
    }
    
    public void testNoMultipleReports1() throws Exception {
        performAnalysisTest("test/Test.java",
                            "package test;\n" +
                            "class Test {\n" +
                            "    private void tes|t(@CheckForNull String o) {\n" +
                            "        o.toString();\n" +
                            "        o.toString();\n" +
                            "        o.toString();\n" +
                            "    }" +
                            "    @interface CheckForNull {}\n" +
                            "}", "3:8-3:18:verifier:Possibly Dereferencing null");
    }
    
    public void testNoMultipleReports2() throws Exception {
        performAnalysisTest("test/Test.java",
                            "package test;\n" +
                            "class Test {\n" +
                            "    private void tes|t(@CheckForNull String o) {\n" +
                            "        if (o.length() > 1 && o.length() > 2);\n" +
                            "    }" +
                            "    @interface CheckForNull {}\n" +
                            "}", "3:12-3:20:verifier:Possibly Dereferencing null");
    }
    
    public void testNoMultipleReports3() throws Exception {
        performAnalysisTest("test/Test.java",
                            "package test;\n" +
                            "class Test {\n" +
                            "    private void tes|t(@CheckForNull String o) {\n" +
                            "        boolean b = o.length() > 1 && o.length() > 2;\n" +
                            "    }" +
                            "    @interface CheckForNull {}\n" +
                            "}", "3:20-3:28:verifier:Possibly Dereferencing null");
    }
    
    public void testNoMultipleReports4() throws Exception {
        performAnalysisTest("test/Test.java",
                            "package test;\n" +
                            "class Test {\n" +
                            "    private void tes|t(@CheckForNull String o) {\n" +
                            "        boolean b = o.length() > 1 && o.length() > 2 ? false : true;\n" +
                            "    }" +
                            "    @interface CheckForNull {}\n" +
                            "}", "3:20-3:28:verifier:Possibly Dereferencing null");
    }
    
    public void testCCE() throws Exception {
        performAnalysisTest("test/Test.java",
                            "package test;\n" +
                            "class Test {\n" +
                            "    private void tes|t() {\n" +
                            "        c.s Object method = new Object();\n" +
                            "    }" +
                            "}"/*, "3:20-3:28:verifier:Possibly Dereferencing null"*/);
    }
    
    @Override
    protected List<ErrorDescription> computeErrors(CompilationInfo info, TreePath path) {
        return new NPECheck().run(info, path);
    }

    static {
        NbBundle.setBranding("test");
    }
}
