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
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 2009 Sun
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
package org.netbeans.modules.java.editor.ext.fold;

import com.sun.source.util.TreePath;
import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.swing.text.Document;
import junit.framework.TestSuite;
import org.netbeans.api.java.source.CompilationInfo;
import org.netbeans.api.java.source.ElementHandle;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.java.source.JavaSource.Phase;
import org.netbeans.api.java.source.SourceUtilsTestUtil;
import org.netbeans.api.java.source.TestUtilities;
import org.netbeans.api.java.source.TreePathHandle;
import org.netbeans.api.java.source.TypeMirrorHandle;
import org.netbeans.core.startup.Main;
import org.netbeans.junit.NbTestCase;
import org.netbeans.junit.NbTestSuite;
import org.netbeans.modules.java.source.usages.IndexUtil;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.URLMapper;
import org.openide.util.NbBundle;
import static org.junit.Assert.*;

/**
 *
 * @author lahvac
 */
public class ClosureCodeCompletionTest extends NbTestCase {

    public ClosureCodeCompletionTest(String name) {
        super(name);
    }

//    public static TestSuite suite() {
//        NbTestSuite s = new NbTestSuite();
//
//        s.addTest(new ClosureCodeCompletionTest("testNoExplicitSiteStatic"));
//        s.addTest(new ClosureCodeCompletionTest("testNoExplicitSiteTopLevel"));
//        s.addTest(new ClosureCodeCompletionTest("testNoExplicitSiteAnonymous"));
//
//        return s;
//    }
    
    public void testSimpleExistence() throws Exception {
        performExistenceTest("package test;\n" +
                             "public class Test {\n" +
                             "    private void test() {" +
                             "        javax.swing.SwingUtilities.invokeLater(|);\n" +
                             "    }\n" +
                             "}\n",
                             "java.lang.Runnable");
    }

    public void testMultiParams() throws Exception {
        performExistenceTest("package test;\n" +
                             "public abstract class Test<T> {\n" +
                             "    private static void test() {" +
                             "        Test<String> tt = null;\n" +
                             "        tt.test(null, |);\n" +
                             "    }" +
                             "    private void test(String s, Test<T> t) {}\n" +
                             "    abstract void test1();" +
                             "}\n",
                             "test.Test<java.lang.String>");
    }

    public void testNoAbstract() throws Exception {
        performExistenceTest("package test;\n" +
                             "public class Test<T> {\n" +
                             "    private static void test() {" +
                             "        Test<String> tt = null;\n" +
                             "        tt.test(null, |);\n" +
                             "    }" +
                             "    private void test(String s, Test<T> t) {}\n" +
                             "}\n");
    }

    public void testTooManyAbstracts() throws Exception {
        performExistenceTest("package test;\n" +
                             "public abstract class Test<T> {\n" +
                             "    private static void test() {" +
                             "        Test<String> tt = null;\n" +
                             "        tt.test(null, |);\n" +
                             "    }" +
                             "    private void test(String s, Test<T> t) {}\n" +
                             "    abstract void test1();" +
                             "    abstract void test2();" +
                             "}\n");
    }

    public void testMoreMethods() throws Exception {
        performExistenceTest("package test;\n" +
                             "public abstract class Test {\n" +
                             "    private static void test() {" +
                             "        javax.swing.JComponent c = null;\n" +
                             "        c.addPropertyChangeListener(|);\n" +
                             "    }" +
                             "}\n",
                             "java.beans.PropertyChangeListener");
    }

    public void testMoreMethodsMoreParams() throws Exception {
        performExistenceTest("package test;\n" +
                             "public abstract class Test {\n" +
                             "    private static void test() {" +
                             "        javax.swing.JComponent c = null;\n" +
                             "        c.addPropertyChangeListener(\"test\", |);\n" +
                             "    }" +
                             "}\n",
                             "java.beans.PropertyChangeListener");
    }
    
    public void testNoExplicitSiteStatic() throws Exception {
        performExistenceTest("package test;\n" +
                             "import static javax.swing.SwingUtilities.invokeLater;" +
                             "public abstract class Test {\n" +
                             "    private static void test() {" +
                             "        invokeLater(|);\n" +
                             "    }" +
                             "}\n",
                             "java.lang.Runnable");
    }

    public void testNoExplicitSiteTopLevel() throws Exception {
        performExistenceTest("package test;\n" +
                             "public abstract class Test implements java.util.Collection<Runnable>{\n" +
                             "    private void test() {" +
                             "        add(|);\n" +
                             "    }" +
                             "}\n",
                             "java.lang.Runnable");
    }

    public void testNoExplicitSiteAnonymous() throws Exception {
        performExistenceTest("package test;\n" +
                             "public abstract class Test {\n" +
                             "    private static void test() {" +
                             "        new java.util.LinkedList<Runnable>() {\n" +
                             "            private void test() {\n" +
                             "                 add(|);\n" +
                             "            }\n" +
                             "        };\n" +
                             "    }" +
                             "}\n",
                             "java.lang.Runnable");
    }

    public void testApplicationTest1() throws Exception {
        performApplicationTest("package test;\n" +
                               "public class Test {\n" +
                               "    private void test() {" +
                               "        javax.swing.SwingUtilities.invokeLater(|);\n" +
                               "    }\n" +
                               "}\n",
                               "package test;\n" +
                               "public class Test {\n" +
                               "    private void test() {" +
                               "        javax.swing.SwingUtilities.invokeLater(new Runnable() {\n" +
                               "\n" +
                               "            public void run() {\n" +
                               "                |\n" +
                               "            }\n" +
                               "        });\n" +
                               "    }\n" +
                               "}\n");
    }

    public void testBrokenSite174030() throws Exception {
        performExistenceTest("package test;\n" +
                               "public class Test {\n" +
                               "    private void test() {" +
                               "        cannot.resolve(|);\n" +
                               "    }\n" +
                               "}\n");
    }

    private FileObject sourceDir;
    private FileObject buildDir;

    private void performExistenceTest(String code, String... golden) throws Exception {
        int pos = code.indexOf("|");

        code = code.replace("|", "");
        
        FileObject f = FileUtil.createData(sourceDir, "test/Test.java");

        TestUtilities.copyStringToFile(f, code);

        CompilationInfo ci = SourceUtilsTestUtil.getCompilationInfo(JavaSource.forFileObject(f), Phase.RESOLVED);

        assertNotNull(ci);

        Document doc = ci.getSnapshot().getSource().getDocument(true);

        assertNotNull(doc);

        Map<? extends DeclaredType, ? extends ExecutableElement> realTypeMap = ClosureCodeCompletion.evalType(ci, pos, new TreePath[1], new int[1]);
        Iterable<? extends TypeMirror> realType = realTypeMap != null ? realTypeMap.keySet() : null;

        if (realType == null) {
            realType = Collections.emptyList();
        }

        List<String> textual = new LinkedList<String>();

        for (TypeMirror tm : realType) {
            textual.add(tm.toString());
        }
        
        assertEquals(Arrays.asList(golden), textual);

    }

    private void performApplicationTest(String code, String golden) throws Exception {
        int pos = code.indexOf("|");

        code = code.replace("|", "");

        int goldenOutPos = golden.indexOf("|");

        golden = golden.replace("|", "");

        FileObject f = FileUtil.createData(sourceDir, "test/Test.java");

        TestUtilities.copyStringToFile(f, code);

        CompilationInfo ci = SourceUtilsTestUtil.getCompilationInfo(JavaSource.forFileObject(f), Phase.RESOLVED);

        assertNotNull(ci);

        Document doc = ci.getSnapshot().getSource().getDocument(true);

        assertNotNull(doc);

        TreePath[] mit = new TreePath[1];
        int[] param = new int[1];
        Map<? extends DeclaredType, ? extends ExecutableElement> types = ClosureCodeCompletion.evalType(ci, pos, mit, param);

        if (types == null) {
            fail();
        }

        assertEquals(1, types.size());
        
        Entry<? extends DeclaredType, ? extends ExecutableElement> e = types.entrySet().iterator().next();

        int outPos = ClosureCodeCompletion.rewrite(doc, pos, TypeMirrorHandle.create(e.getKey()), ElementHandle.create(e.getValue()), TreePathHandle.create(mit[0], ci), param[0]);

        assertEquals(golden, doc.getText(0, doc.getLength()));
        assertEquals(goldenOutPos, outPos);
    }

    @Override
    public void setUp() throws Exception {
        SourceUtilsTestUtil.setLookup(new Object[0], ShorteningFoldTest.class.getClassLoader());
        Main.initializeURLFactory();

        clearWorkDir();
        File wd = getWorkDir();
        assert wd.isDirectory() && wd.list().length == 0;
        FileObject dir = FileUtil.toFileObject(wd);

        assertNotNull(dir);

        sourceDir = FileUtil.createFolder(dir, "src");
        buildDir = FileUtil.createFolder(dir, "build");

        FileObject cache = FileUtil.createFolder(dir, "cache");

        IndexUtil.setCacheFolder(FileUtil.toFile(cache));

        FileObject util = URLMapper.findFileObject(NbBundle.class.getProtectionDomain().getCodeSource().getLocation());

        assertNotNull(util);

        util = FileUtil.getArchiveRoot(util);

        assertNotNull(util);

        SourceUtilsTestUtil.prepareTest(sourceDir, buildDir, cache, new FileObject[] {util});
    }
}