/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2009 Sun Microsystems, Inc. All rights reserved.
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
 * Portions Copyrighted 2009 Sun Microsystems, Inc.
 */

package org.netbeans.modules.javahints;

import com.sun.source.util.TreePath;
import java.lang.reflect.Method;
import java.util.List;
import org.netbeans.api.java.source.CompilationInfo;
import org.netbeans.api.java.source.support.CaretAwareJavaSourceTaskFactory;
import org.netbeans.modules.java.hints.infrastructure.TreeRuleTestBase;
import org.netbeans.spi.editor.hints.ErrorDescription;
import org.netbeans.spi.editor.hints.Fix;
import org.openide.filesystems.FileObject;

/**
 *
 * @author Jan Lahoda
 */
public class ExpandEnhancedForLoopTest extends TreeRuleTestBase {

    public ExpandEnhancedForLoopTest(String name) {
        super(name);
    }

    public void testSimple1() throws Exception {
        performFixTest("test/Test.java",
                       "package test;\n" +
                       "public class Test {\n" +
                       "    private void test() {\n" +
                       "        fo|r (String s : java.util.Arrays.asList(\"a\")) {\n" +
                       "        }\n" +
                       "    }\n" +
                       "}\n",
                       "3:8-3:8:hint:Convert to long for loop",
                       "FixImpl",
                       ("package test;\n" +
                       "import java.util.Iterator;\n" +
                       "public class Test {\n" +
                       "    private void test() {\n" +
                       "        for (Iterator<String> it = java.util.Arrays.asList(\"a\").iterator(); it.hasNext();) {" +
                       "            String s = it.next();\n" +
                       "        }\n" +
                       "    }\n" +
                       "}\n").replaceAll("[ \t\n]+", " "));
    }

    public void testSimple2() throws Exception {
        performFixTest("test/Test.java",
                       "package test;\n" +
                       "public class Test {\n" +
                       "    private void test() {" +
                       "        java.util.List<? extends CharSequence> l = null;\n" +
                       "        fo|r (CharSequence c : l) {\n" +
                       "        }\n" +
                       "    }\n" +
                       "}\n",
                       "3:8-3:8:hint:Convert to long for loop",
                       "FixImpl",
                       ("package test;\n" +
                       "import java.util.Iterator;\n" +
                       "public class Test {\n" +
                       "    private void test() {\n" +
                       "        java.util.List<? extends CharSequence> l = null;\n" +
                       "        for (Iterator<? extends CharSequence> it = l.iterator(); it.hasNext();) {" +
                       "            CharSequence c = it.next();\n" +
                       "        }\n" +
                       "    }\n" +
                       "}\n").replaceAll("[ \t\n]+", " "));
    }

    public void testNoBlock() throws Exception {
        performFixTest("test/Test.java",
                       "package test;\n" +
                       "public class Test {\n" +
                       "    private void test() {" +
                       "        java.util.List<? extends CharSequence> l = null;\n" +
                       "        fo|r (CharSequence c : l)\n" +
                       "            System.err.println(c);\n" +
                       "    }\n" +
                       "}\n",
                       "3:8-3:8:hint:Convert to long for loop",
                       "FixImpl",
                       ("package test;\n" +
                       "import java.util.Iterator;\n" +
                       "public class Test {\n" +
                       "    private void test() {\n" +
                       "        java.util.List<? extends CharSequence> l = null;\n" +
                       "        for (Iterator<? extends CharSequence> it = l.iterator(); it.hasNext();) {" +
                       "            CharSequence c = it.next();\n" +
                       "            System.err.println(c);\n" +
                       "        }\n" +
                       "    }\n" +
                       "}\n").replaceAll("[ \t\n]+", " "));
    }

    public void testEmptyStatement() throws Exception {
        performFixTest("test/Test.java",
                       "package test;\n" +
                       "public class Test {\n" +
                       "    private void test() {" +
                       "        java.util.List<? extends CharSequence> l = null;\n" +
                       "        fo|r (CharSequence c : l);\n" +
                       "    }\n" +
                       "}\n",
                       "3:8-3:8:hint:Convert to long for loop",
                       "FixImpl",
                       ("package test;\n" +
                       "import java.util.Iterator;\n" +
                       "public class Test {\n" +
                       "    private void test() {\n" +
                       "        java.util.List<? extends CharSequence> l = null;\n" +
                       "        for (Iterator<? extends CharSequence> it = l.iterator(); it.hasNext();) {" +
                       "            CharSequence c = it.next();\n" +
                       "        }\n" +
                       "    }\n" +
                       "}\n").replaceAll("[ \t\n]+", " "));
    }

    public void testNegative() throws Exception {
        performAnalysisTest("test/Test.java",
                            "package test;\n" +
                            "public class Test {\n" +
                            "    private void test() {\n" +
                            "        fo|r (String s : new Object()) {\n" +
                            "        }\n" +
                            "    }\n" +
                            "}\n");
    }

    @Override
    protected List<ErrorDescription> computeErrors(CompilationInfo info, TreePath path) {
        int offset = (int) info.getTrees().getSourcePositions().getStartPosition(info.getCompilationUnit(), path.getLeaf());

        while (path != null && !new ExpandEnhancedForLoop().getTreeKinds().contains(path.getLeaf().getKind()))
            path = path.getParentPath();

        if (path == null)
            return null;

        try {
            Method m = CaretAwareJavaSourceTaskFactory.class.getDeclaredMethod("setLastPosition", FileObject.class, int.class);

            assertNotNull(m);

            m.setAccessible(true);

            m.invoke(null, new Object[]{info.getFileObject(), offset});
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }

        return new ExpandEnhancedForLoop().run(info, path);
    }

    @Override
    protected String toDebugString(CompilationInfo info, Fix f) {
        return "FixImpl";
    }

}