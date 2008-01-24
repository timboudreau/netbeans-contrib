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

import com.sun.source.tree.Tree.Kind;
import com.sun.source.util.TreePath;
import java.util.List;
import org.netbeans.api.java.source.CompilationInfo;
import org.netbeans.api.java.source.SourceUtilsTestUtil;
import org.netbeans.modules.java.hints.infrastructure.TreeRuleTestBase;
import org.netbeans.spi.editor.hints.ErrorDescription;
import org.netbeans.spi.editor.hints.Fix;
import org.openide.LifecycleManager;

/**
 *
 * @author Jan Lahoda
 */
public class I18NCheckerTest extends TreeRuleTestBase {
    
    public I18NCheckerTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        SourceUtilsTestUtil.prepareTest(new String[] {"org/netbeans/modules/java/editor/resources/layer.xml"}, new Object[0]);
        AddToBundleFix.TESTS = true;
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        LifecycleManager.getDefault().saveAll();
        super.tearDown();
    }
    
    public void testSimple() throws Exception {
        performAnalysisTest("test/Test.java", "package test; public class Test {private String s = \"|s\";}", "0:52-0:52:hint:Hardcoded String");
    }
    
    public void testSimpleNOI18N() throws Exception {
        performAnalysisTest("test/Test.java", "package test; public class Test {\nprivate String s = \"|s\";//NOI18N\n}");
    }
    
    public void testSimpleNoNOI18N() throws Exception {
        performAnalysisTest("test/Test.java", "package test; public class Test {\nprivate String s = \"|s\";\n//NOI18N}", "1:19-1:19:hint:Hardcoded String");
    }
    
    public void testAddToBundle() throws Exception {
        performFixTest("test/Test.java", "package test; public class Test {\nprivate String s = \"s\";}", 97 - 43, "1:19-1:19:hint:Hardcoded String", "A", "package test; import java.util.ResourceBundle; public class Test { private String s = ResourceBundle.getBundle(\"Bundle.properties\").getString(\"s\");}");
    }
    
    public void testZeroLengthString() throws Exception {
        performAnalysisTest("test/Test.java", "package test; public class Test {\nprivate String s = \"|\";\n}");
    }
    
    public void testNoStringLiteral() throws Exception {
        performAnalysisTest("test/Test.java", "package test; public class Test {\nprivate void t() {String s2 = null; String s = s|2 + s2;}\n}");
    }
    
    public void testCompound1() throws Exception {
        performAnalysisTest("test/Test.java", "package test; public class Test {\nprivate void t() {String s2 = null; String s = \"x\" + s|2 + s2;}\n}", "1:47-1:47:hint:Hardcoded String");
    }
    
    public void testCompound2() throws Exception {
        performAnalysisTest("test/Test.java", "package test; public class Test {\nprivate void t() {String s2 = null; String s = \"\" + s|2 + s2;}\n}");
    }
    
    public void testAnnotation1() throws Exception {
        performAnalysisTest("test/Test.java", "package test; @SuppressWarnings(\"somet|hing\") public class Test {}\n}");
    }
    
    public void testAnnotation2() throws Exception {
        performAnalysisTest("test/Test.java", "package test; @SuppressWarnings({\"somet|hing\", \"AAAA\"}) public class Test {}\n}");
    }
    
    @Override
    protected List<ErrorDescription> computeErrors(CompilationInfo info, TreePath path) {
        if ("testNoStringLiteral".equals(getName()) || "testCompound1".equals(getName()) || "testCompound2".equals(getName())) {
            while (path.getLeaf().getKind() != Kind.PLUS) {
                path = path.getParentPath();
            }
            while (path.getParentPath().getLeaf().getKind() == Kind.PLUS) {
                path = path.getParentPath();
            }
        }
        return new I18NChecker().run(info, path);
    }

    @Override
    protected String toDebugString(CompilationInfo info, Fix f) {
        if (f instanceof AddToBundleFix) {
            return "A";
        }
        
        return super.toDebugString(info, f);
    }
    
}
