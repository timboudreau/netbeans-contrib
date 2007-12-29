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
import org.netbeans.spi.editor.hints.Fix;
import org.openide.util.NbBundle;

/**
 *
 * @author Jan Lahoda
 */
public class NegateConditionTest extends TreeRuleTestBase {
    
    public NegateConditionTest(String testName) {
        super(testName);
    }

    public void testSimple1() throws Exception {
        performFixTest("test/Test.java",
                       "package test;\n" +
                       "public class Test {\n" +
                       "    private void test(Object o) {\n" +
                       "        if (|o instanceof String) {}\n" +
                       "    }\n" +
                       "}\n",
                       "3:12-3:12:hint:Negate condition",
                       "FixImpl",
                       "package test; public class Test { private void test(Object o) { if (!(o instanceof String)) {} } } ");
    }
    
    public void testSimple2() throws Exception {
        performFixTest("test/Test.java",
                       "package test;\n" +
                       "public class Test {\n" +
                       "    private void test(Object o) {\n" +
                       "        if (|o == null) {}\n" +
                       "    }\n" +
                       "}\n",
                       "3:12-3:12:hint:Negate condition",
                       "FixImpl",
                       "package test; public class Test { private void test(Object o) { if (o != null) {} } } ");
    }
    
    /*codegen bug*/
    public void DISABLE_testSimple3() throws Exception {
        performFixTest("test/Test.java",
                       "package test;\n" +
                       "public class Test {\n" +
                       "    private void test(Object o) {\n" +
                       "        if (|o == null && o instanceof String) {}\n" +
                       "    }\n" +
                       "}\n",
                       "3:12-3:12:hint:Negate condition",
                       "FixImpl",
                       "package test; public class Test { private void test(Object o) { if (o != null || !(o instanceof String)) {} } } ");
    }
    
    /*codegen bug*/
    public void DISABLE_testSimple4() throws Exception {
        String code = "package test;\n" +
                      "public class Test {\n" +
                      "    private void test(Object o) {\n" +
                      "        if (|o == null || o instanceof String) {}\n" +
                      "    }\n" +
                      "}\n";
       
        performFixTest("test/Test.java",
                       code.replaceFirst("\\|", ""),
                       code.indexOf('|'),
                       "3:12-3:12:hint:Negate condition",
                       "FixImpl",
                       "package test; public class Test { private void test(Object o) { if (o != null && !(o instanceof String)) {} } } ");
    }
    
    public void testSimple5() throws Exception {
        performFixTest("test/Test.java",
                       "package test;\n" +
                       "public class Test {\n" +
                       "    private void test(java.util.Collection c) {\n" +
                       "        if (|c.isEmpty()) {}\n" +
                       "    }\n" +
                       "}\n",
                       "3:12-3:12:hint:Negate condition",
                       "FixImpl",
                       "package test; public class Test { private void test(java.util.Collection c) { if (!c.isEmpty()) {} } } ");
    }
    
    public void testSimple6() throws Exception {
        performFixTest("test/Test.java",
                       "package test;\n" +
                       "public class Test {\n" +
                       "    private void test(Object o) {\n" +
                       "        if (|o != null) {}\n" +
                       "    }\n" +
                       "}\n",
                       "3:12-3:12:hint:Negate condition",
                       "FixImpl",
                       "package test; public class Test { private void test(Object o) { if (o == null) {} } } ");
    }
    
    public void testSimple7() throws Exception {
        performFixTest("test/Test.java",
                       "package test;\n" +
                       "public class Test {\n" +
                       "    private void test(Object o) {\n" +
                       "        if (|(o != null)) {}\n" +
                       "    }\n" +
                       "}\n",
                       "3:12-3:12:hint:Negate condition",
                       "FixImpl",
                       "package test; public class Test { private void test(Object o) { if ((o == null)) {} } } ");
    }
    
    public void testSimple8() throws Exception {
        performFixTest("test/Test.java",
                "package test;\n" +
                "public class Test {\n" +
                "    private void test(java.util.Collection c) {\n" +
                "        if (|!c.isEmpty()) {}\n" +
                "    }\n" +
                "}\n",
                "3:12-3:12:hint:Negate condition",
                "FixImpl",
                "package test; public class Test { private void test(java.util.Collection c) { if (c.isEmpty()) {} } } ");
    }
    
    public void DISABLE_testSimple9() throws Exception {
        performFixTest("test/Test.java",
                       "package test;\n" +
                       "public class Test {\n" +
                       "    private void test(Object o) {\n" +
                       "        if (|!(o != null)) {}\n" +
                       "    }\n" +
                       "}\n",
                       "3:12-3:12:hint:Negate condition",
                       "FixImpl",
                       "package test; public class Test { private void test(Object o) { if (o != null) {} } } ");
    }
    
    public void testSimplea() throws Exception {
        String code =  "package test;\n" +
                       "public class Test {\n" +
                       "    private void test(boolean a, boolean b) {\n" +
                       "        if (|(a && !b) || (!a && b)) {}\n" +
                       "    }\n" +
                       "}\n";

        performFixTest("test/Test.java",
                       code.replaceFirst("\\|", ""),
                       code.indexOf('|'),
                       "3:12-3:12:hint:Negate condition",
                       "FixImpl",
                       "package test; public class Test { private void test(boolean a, boolean b) { if ((!a || b) && (a || !b)) {} } } ");
    }
    
    public void testSimpleb() throws Exception {
        performFixTest("test/Test.java",
                       "package test;\n" +
                       "public class Test {\n" +
                       "    private void test(Object o) {\n" +
                       "        if (|(o instanceof String)) {}\n" +
                       "    }\n" +
                       "}\n",
                       "3:12-3:12:hint:Negate condition",
                       "FixImpl",
                       "package test; public class Test { private void test(Object o) { if (!(o instanceof String)) {} } } ");
    }
    
    @Override
    protected List<ErrorDescription> computeErrors(CompilationInfo info, TreePath path) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected String toDebugString(CompilationInfo info, Fix f) {
        if (f instanceof NegateCondition.FixImpl) {
            return "FixImpl";
        }
        
        return super.toDebugString(info, f);
    }

    @Override
    protected List<ErrorDescription> computeErrors(CompilationInfo info, TreePath path, int offset) {
        NegateCondition c = new NegateCondition();
        
        while (path != null && !c.getTreeKinds().contains(path.getLeaf().getKind())) {
            path = path.getParentPath();
        }
        
        if (path == null) {
            return null;
        }
        
        return c.run(info, path, offset);
    }
    
    static {
        NbBundle.setBranding("test");
    }
    
}
