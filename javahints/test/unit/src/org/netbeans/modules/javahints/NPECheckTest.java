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
    
    public void testTernary() throws Exception {
        performAnalysisTest("test/Test.java", "package test; class Test {private void te|st(int i) {String s = i == 0 ? \"\" : null; s.length();}}");
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
    
    @Override
    protected List<ErrorDescription> computeErrors(CompilationInfo info, TreePath path) {
        return new NPECheck().run(info, path);
    }

    static {
        NbBundle.setBranding("test");
    }
}
