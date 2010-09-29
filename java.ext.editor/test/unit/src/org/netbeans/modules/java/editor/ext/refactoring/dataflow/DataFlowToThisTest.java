/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
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
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
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
 * Portions Copyrighted 2010 Sun Microsystems, Inc.
 */

package org.netbeans.modules.java.editor.ext.refactoring.dataflow;

import com.sun.source.util.TreePath;
import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.source.ClasspathInfo;
import org.netbeans.api.java.source.CompilationInfo;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.java.source.JavaSource.Phase;
import org.netbeans.api.java.source.SourceUtilsTestUtil;
import org.netbeans.api.java.source.TestUtilities;
import org.netbeans.core.startup.Main;
import org.netbeans.junit.NbTestCase;
import org.netbeans.modules.java.source.usages.IndexUtil;
import org.netbeans.spi.java.classpath.support.ClassPathSupport;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.URLMapper;
import static org.junit.Assert.*;

/**
 *
 * @author lahvac
 */
public class DataFlowToThisTest extends NbTestCase {

    public DataFlowToThisTest(String name) {
        super(name);
    }

    public void testSimple1() throws Exception {
        performTest("package test;\n" +
                    "public class Test {\n" +
                    "    private void test(int ii) {\n" +
                    "        int yy = 0;\n" +
                    "        if (ii == 0) {\n" +
                    "            yy = ii;\n" +
                    "        } else {\n" +
                    "            yy = 42;\n" +
                    "        }\n" +
                    "        System.err.println(y|y);" +
                    "        yy = -1;\n" +
                    "    }\n" +
                    "}\n",
                    "yy = <b>ii</b>;:false",
                    "yy = <b>42</b>;:true");
    }

    public void testMethod1() throws Exception {
        performTest("package test;\n" +
                    "public class Test {\n" +
                    "    private void test(int ii, int aa) {\n" +
                    "        int yy = 0;\n" +
                    "        if (i|i == 0) {\n" +
                    "            yy = ii;\n" +
                    "        } else {\n" +
                    "            yy = 42;\n" +
                    "        }\n" +
                    "    }\n" +
                    "    private void c() {\n" +
                    "        test(1);\n" +
                    "    }\n" +
                    "}\n",
                    "test(<b>int ii</b>, int aa):false");
    }

    public void testMethod2() throws Exception {
        performTest("package test;\n" +
                    "public class Test {\n" +
                    "    private void test(int i|i, int aa) {\n" +
                    "        int yy = 0;\n" +
                    "        if (ii == 0) {\n" +
                    "            yy = ii;\n" +
                    "        } else {\n" +
                    "            yy = 42;\n" +
                    "        }\n" +
                    "    }\n" +
                    "    private void c() {\n" +
                    "        test(1, 2);\n" +
                    "    }\n" +
                    "}\n",
                    "test(<b>1</b>, 2);:true");
    }

    public void testGenericTypeInHTML() throws Exception {
        performTest("package test;\n" +
                    "public class Test {\n" +
                    "    private void test(int ii, Iterable<String> it) {\n" +
                    "        int yy = 0;\n" +
                    "        if (i|i == 0) {\n" +
                    "            yy = ii;\n" +
                    "        } else {\n" +
                    "            yy = 42;\n" +
                    "        }\n" +
                    "    }\n" +
                    "    private void c() {\n" +
                    "        test(1, 2);\n" +
                    "    }\n" +
                    "}\n",
                    "test(<b>int ii</b>, Iterable&lt;String> it):false");
    }

    public void testFieldInitialization1() throws Exception {
        performTest("package test;\n" +
                    "public class Test {\n" +
                    "    private String test;\n" +
                    "    { test = \"a\"; }\n" +
                    "    public Test() {}\n" +
                    "    public Test(int a) {test = String.valueOf(a); }\n" +
                    "    private void test() {\n" +
                    "        System.err.println(te|st);\n" +
                    "    }\n" +
                    "}\n",
                    "public Test(int a) {test = <b>String.valueOf(a)</b>; }:true",
                    "{ test = <b>\"a\"</b>; }:true");
    }

    public void testFieldInitialization2() throws Exception {
        performTest("package test;\n" +
                    "public class Test {\n" +
                    "    private String test = \"a\";\n" +
                    "    public Test() {}\n" +
                    "    public Test(int a) {test = String.valueOf(a); }\n" +
                    "    private void test() {\n" +
                    "        System.err.println(te|st);\n" +
                    "    }\n" +
                    "}\n",
                    "public Test(int a) {test = <b>String.valueOf(a)</b>; }:true",
                    "private String test = <b>\"a\"</b>;:true");
    }

    public void testFieldInitializationHiding() throws Exception {
        performTest("package test;\n" +
                    "public class Test {\n" +
                    "    private String test = \"b\";\n" +
                    "    { test = \"a\"; }\n" +
                    "    public Test() {}\n" +
                    "    public Test(int a) {}\n" +
                    "    private void test() {\n" +
                    "        System.err.println(te|st);\n" +
                    "    }\n" +
                    "}\n",
                    "{ test = <b>\"a\"</b>; }:true");
    }

    public void testConstructorAndFactory1() throws Exception {
        performTest("package test;\n" +
                    "public class Test {\n" +
                    "    private String test;\n" +
                    "    private Test(String test) { this.test = te|st; }\n" +
                    "    private void test() {\n" +
                    "        System.err.println(te|st);\n" +
                    "    }\n" +
                    "    public static Test create(String test) {\n" +
                    "        return new Test(test);" +
                    "    }\n" +
                    "}\n",
                    "Test(<b>String test</b>):false");
    }

    public void testConstructorAndFactory2() throws Exception {
        performTest("package test;\n" +
                    "public class Test {\n" +
                    "    private String test;\n" +
                    "    private Test(String te|st) { this.test = test; }\n" +
                    "    private void test() {\n" +
                    "        System.err.println(te|st);\n" +
                    "    }\n" +
                    "    public static Test create(String test) {\n" +
                    "        return new Test(test);" +
                    "    }\n" +
                    "}\n",
                    "return new Test(<b>test</b>); }:false");
    }

    public void testMultifileMethod() throws Exception {
        performTest(Arrays.asList(
                        new FileData("test/Test1.java",
                                     "package test;\n" +
                                     "public class Test1 {\n" +
                                     "    Test1(String te|st) {}\n" +
                                     "}\n"),
                        new FileData("test/Test2.java",
                                     "package test;\n" +
                                     "public class Test2 {\n" +
                                     "    private Test2() { new Test1(\"a\"); }\n" +
                                     "}\n")),
                        "private Test2() { new Test1(<b>\"a\"</b>); }:true");
    }

    public void testMultifileField() throws Exception {
        performTest(Arrays.asList(
                        new FileData("test/Test1.java",
                                     "package test;\n" +
                                     "public class Test1 {\n" +
                                     "    static String test;\n" +
                                     "    { System.err.println(te|st); }\n" +
                                     "}\n"),
                        new FileData("test/Test2.java",
                                     "package test;\n" +
                                     "public class Test2 {\n" +
                                     "    private Test2() { Test1.test = \"a\"; }\n" +
                                     "}\n")),
                        "private Test2() { Test1.test = <b>\"a\"</b>; }:true");
    }

    public void testSpeed1() throws Exception {
        performTest(Arrays.asList(
                        new FileData("test/Test1.java",
                                     "package test;\n" +
                                     "public class Test1 {\n" +
                                     "    private static String test;\n" +
                                     "    { System.err.println(te|st); }\n" +
                                     "}\n"),
                        new FileData("test/Test2.java",
                                     "package test;\n" +
                                     "public class Test2 {\n" +
                                     "    private Test2() { Test1.test = \"a\"; }\n" +
                                     "}\n")));
    }

    public void testSpeed2() throws Exception {
        performTest(Arrays.asList(
                        new FileData("test/Test1.java",
                                     "package test;\n" +
                                     "public class Test1 {\n" +
                                     "    public final static String test = \"b\";\n" +
                                     "    { System.err.println(te|st); }\n" +
                                     "}\n"),
                        new FileData("test/Test2.java",
                                     "package test;\n" +
                                     "public class Test2 {\n" +
                                     "    private Test2() { Test1.test = \"a\"; }\n" +
                                     "}\n")),
                        "public final static String test = <b>\"b\"</b>;:true");
    }

    public void testVariableInitializer() throws Exception {
        performTest("package test;\n" +
                    "public class Test {\n" +
                    "    private void test(int ii) {\n" +
                    "        int yy = 42;\n" +
                    "        System.err.println(y|y);" +
                    "        yy = -1;\n" +
                    "    }\n" +
                    "}\n",
                    "int yy = <b>42</b>;:true");
    }

    public void testFlow1() throws Exception {
        performTest("package test;\n" +
                    "public class Test {\n" +
                    "    private void test(int ii) {\n" +
                    "        int yy = 42;\n" +
                    "        if (ii == 0) {\n" +
                    "            yy = 1;\n" +
                    "        } else {\n" +
                    "            yy = 0;\n" +
                    "        }\n" +
                    "        System.err.println(y|y);\n" +
                    "        yy = -1;\n" +
                    "    }\n" +
                    "}\n",
                    "yy = <b>1</b>;:true",
                    "yy = <b>0</b>;:true");
    }

    private FileObject sourceDir;
    private FileObject buildDir;
    private FileObject testBinaryRoot;

    private void performTest(String code, String... golden) throws Exception {
        performTest(Collections.singleton(new FileData("test/Test.java", code)), golden);
    }

    private void performTest(Iterable<? extends FileData> code, String... golden) throws Exception {
        int pos = (-1);
        FileObject file = null;

        for (FileData f : code) {
            FileObject fo = FileUtil.createData(sourceDir, f.fileName);
            String content = f.content;

            if (file == null) {
                file = fo;
                pos = content.indexOf("|");

                assertTrue(pos != (-1));

                content = content.replace("|", "");
            }

            TestUtilities.copyStringToFile(fo, content);
        }

        assertNotNull(file);

        SourceUtilsTestUtil.compileRecursively(sourceDir);
        
        ClassPath boot = ClassPathSupport.createClassPath(System.getProperty("sun.boot.class.path"));
        ClassPath compilation = ClassPathSupport.createClassPath(testBinaryRoot);
        ClassPath source = ClassPathSupport.createClassPath(sourceDir);
        JavaSource js = JavaSource.create(ClasspathInfo.create(boot, compilation, source), file);
        CompilationInfo ci = SourceUtilsTestUtil.getCompilationInfo(js, Phase.RESOLVED);

        assertNotNull(ci);

        TreePath master = ci.getTreeUtilities().pathFor(pos);
        UseDescription orig = UseDescription.create(ci, master);
//        Element el = ci.getTrees().getElement(master);
//        Set<UseDescription> actual = new HashSet<UseDescription>();
        Collection<? extends UseDescription> actual = DataFlowToThis.findWrites(orig);

//        DataFlowToThis.findWrites(ci, el, master, actual);

        Set<String> actualAsString = new HashSet<String>();

        for (UseDescription ud : actual) {
            actualAsString.add(ud.toDebugString(ci));
        }

        assertEquals(new HashSet<String>(Arrays.asList(golden)), actualAsString);
    }

    @Override
    public void setUp() throws Exception {
        SourceUtilsTestUtil.prepareTest(new String[0], new Object[0]);
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

        testBinaryRoot = URLMapper.findFileObject(DataFlowToThisTest.class.getProtectionDomain().getCodeSource().getLocation());

        assertNotNull(testBinaryRoot);

        //XXX:
//        myself = FileUtil.getArchiveRoot(myself);
//
//        assertNotNull(myself);

        SourceUtilsTestUtil.prepareTest(sourceDir, buildDir, cache, new FileObject[] {testBinaryRoot});
    }

    private static final class FileData {

        private final String fileName;
        private final String content;

        FileData(String fileName, String content) {
            this.fileName = fileName;
            this.content = content;
        }

    }

}