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

import java.io.File;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.text.Document;
import org.netbeans.api.java.source.SourceUtilsTestUtil;
import org.netbeans.junit.NbTestCase;
import static org.junit.Assert.*;
import org.netbeans.api.java.source.CompilationInfo;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.java.source.JavaSource.Phase;
import org.netbeans.api.java.source.TestUtilities;
import org.netbeans.core.startup.Main;
import org.netbeans.modules.editor.fold.spi.support.FoldInfo;
import org.netbeans.modules.java.source.usages.IndexUtil;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.URLMapper;
import org.openide.util.NbBundle;

/**
 *
 * @author lahvac
 */
public class ShorteningFoldTest extends NbTestCase {

    public ShorteningFoldTest(String name) {
        super(name);
    }

    public void testArgSingleStatement() throws Exception {
        performTest("package test;" +
                    "public class Test {" +
                    "    public static void main(Map<String, String> m) {" +
                    "        main(new Map<String, String>() {" +
                    "            public String map(String p) {" +
                    "                throw new UnsupportedOperationException();" +
                    "            }" +
                    "        });" +
                    "    }" +
                    "    public static interface Map<R, P> {" +
                    "        public R map(P p);" +
                    "    }" +
                    "}",
                    "package test;" +
                    "public class Test {" +
                    "    public static void main(Map<String, String> m) {" +
                    "        main({String p=>throw new UnsupportedOperationException();});" +
                    "    }" +
                    "    public static interface Map<R, P> {" +
                    "        public R map(P p);" +
                    "    }" +
                    "}");
    }

    public void testArgMultipleStatements() throws Exception {
        performTest("package test;" +
                    "public class Test {" +
                    "    public static void main(Map<String, String> m) {" +
                    "        main(new Map<String, String>() {" +
                    "            public String map(String p) {" +
                    "                System.err.println(1);" +
                    "                System.err.println(1);" +
                    "            }" +
                    "        });" +
                    "    }" +
                    "    public static interface Map<R, P> {" +
                    "        public R map(P p);" +
                    "    }" +
                    "}",
                    "package test;" +
                    "public class Test {" +
                    "    public static void main(Map<String, String> m) {" +
                    "        main({String p=>" +
                    "                System.err.println(1);" +
                    "                System.err.println(1);" +
                    "            });" + //TODO: whitespaces on this line
                    "    }" +
                    "    public static interface Map<R, P> {" +
                    "        public R map(P p);" +
                    "    }" +
                    "}");
    }

    public void testNoArgsSingleStatement() throws Exception {
        performTest("package test;" +
                    "public class Test {" +
                    "    public static void main(Runnable r) {" +
                    "        main(new Runnable() {" +
                    "            public void run() {" +
                    "                throw new UnsupportedOperationException();" +
                    "            }" +
                    "        });" +
                    "    }" +
                    "}",
                    "package test;" +
                    "public class Test {" +
                    "    public static void main(Runnable r) {" +
                    "        main({ =>throw new UnsupportedOperationException();});" +
                    "    }" +
                    "}");
    }

    public void testSingleMultilineStatement() throws Exception {
        performTest("package test;" +
                    "public class Test {" +
                    "    public static void main(Runnable r) {" +
                    "        main(new Runnable() {" +
                    "            public void run() {" +
                    "                if (true)\n" +
                    "                     System.err.println();\n" +
                    "            }" +
                    "        });" +
                    "    }" +
                    "}",
                    "package test;" +
                    "public class Test {" +
                    "    public static void main(Runnable r) {" +
                    "        main({ =>" +
                    "                if (true)\n" +
                    "                     System.err.println();\n" +
                    "            });" +
                    "    }" +
                    "}");
    }

    public void testDiamondNCT() throws Exception {
        performTest("package test;" +
                    "import java.util.LinkedList;" +
                    "import java.util.List;" +
                    "public class Test {" +
                    "    public static void main() {" +
                    "        List<String> l = new LinkedList< String >();" +
                    "    }" +
                    "}",
                    "package test;" +
                    "import java.util.LinkedList;" +
                    "import java.util.List;" +
                    "public class Test {" +
                    "    public static void main() {" +
                    "        List<String> l = new LinkedList<~>();" +
                    "    }" +
                    "}");
    }

    public void testDiamondMethod() throws Exception {
        performTest("package test;" +
                    "import java.util.Collections;" +
                    "import java.util.LinkedList;" +
                    "public class Test {" +
                    "    public static void main() {" +
                    "        List<String> l = Collections.</*sadf*/String >emptyList();" +
                    "    }" +
                    "}",
                    "package test;" +
                    "import java.util.Collections;" +
                    "import java.util.LinkedList;" +
                    "public class Test {" +
                    "    public static void main() {" +
                    "        List<String> l = Collections.<~>emptyList();" +
                    "    }" +
                    "}");
    }

    public void testDiamondEmbedded1() throws Exception {
        performTest("package test;" +
                    "import java.util.HashMap;" +
                    "import java.util.List;" +
                    "import java.util.Map;" +
                    "public class Test {" +
                    "    public static void main() {" +
                    "        Map<String, List<String>> l = new HashMap<String, List<String > >();" +
                    "    }" +
                    "}",
                    "package test;" +
                    "import java.util.HashMap;" +
                    "import java.util.List;" +
                    "import java.util.Map;" +
                    "public class Test {" +
                    "    public static void main() {" +
                    "        Map<String, List<String>> l = new HashMap<~>();" +
                    "    }" +
                    "}");
    }

    public void testDiamondEmbedded2() throws Exception {
        performTest("package test;" +
                    "import java.util.HashMap;" +
                    "import java.util.List;" +
                    "import java.util.Map;" +
                    "public class Test {" +
                    "    public static void main() {" +
                    "        Map<String, List<String>> l = new HashMap<String, List<String>>();" +
                    "    }" +
                    "}",
                    "package test;" +
                    "import java.util.HashMap;" +
                    "import java.util.List;" +
                    "import java.util.Map;" +
                    "public class Test {" +
                    "    public static void main() {" +
                    "        Map<String, List<String>> l = new HashMap<~>();" +
                    "    }" +
                    "}");
    }

    public void testDiamondEmbedded3() throws Exception {
        performTest("package test;" +
                    "import java.util.HashMap;" +
                    "import java.util.List;" +
                    "import java.util.Map;" +
                    "public class Test {" +
                    "    public static void main() {" +
                    "        Map<String, List<Class<?>>> l = new HashMap<String, List<Class<?>>>();" +
                    "    }" +
                    "}",
                    "package test;" +
                    "import java.util.HashMap;" +
                    "import java.util.List;" +
                    "import java.util.Map;" +
                    "public class Test {" +
                    "    public static void main() {" +
                    "        Map<String, List<Class<?>>> l = new HashMap<~>();" +
                    "    }" +
                    "}");
    }

    public void testDiamondForMethodParam() throws Exception {
        performTest("package test;" +
                    "import java.util.HashMap;" +
                    "import java.util.List;" +
                    "import java.util.Map;" +
                    "public class Test {" +
                    "    public static void main(Map<String, List<Class<?>>> l) {" +
                    "        main(new HashMap<String, List<Class<?>>>());" +
                    "    }" +
                    "}",
                    "package test;" +
                    "import java.util.HashMap;" +
                    "import java.util.List;" +
                    "import java.util.Map;" +
                    "public class Test {" +
                    "    public static void main(Map<String, List<Class<?>>> l) {" +
                    "        main(new HashMap<~>());" +
                    "    }" +
                    "}");
    }

    public void testDiamondForConstrParam() throws Exception {
        performTest("package test;" +
                    "import java.util.HashMap;" +
                    "import java.util.List;" +
                    "import java.util.Map;" +
                    "public class Test {" +
                    "    public Test(Map<String, List<Class<?>>> l) {" +
                    "        new Test(new HashMap<String, List<Class<?>>>());" +
                    "    }" +
                    "}",
                    "package test;" +
                    "import java.util.HashMap;" +
                    "import java.util.List;" +
                    "import java.util.Map;" +
                    "public class Test {" +
                    "    public Test(Map<String, List<Class<?>>> l) {" +
                    "        new Test(new HashMap<~>());" +
                    "    }" +
                    "}");
    }

    public void testDiamondNoShorteningForIncorrectParams1() throws Exception {
        performTest("package test;" +
                    "import java.util.HashMap;" +
                    "import java.util.List;" +
                    "import java.util.Map;" +
                    "public class Test {" +
                    "    public static void main() {" +
                    "        Map<String, List<Class<?>>> l = new HashMap<Integer, List<Class<?>>>();" +
                    "    }" +
                    "}",
                    "package test;" +
                    "import java.util.HashMap;" +
                    "import java.util.List;" +
                    "import java.util.Map;" +
                    "public class Test {" +
                    "    public static void main() {" +
                    "        Map<String, List<Class<?>>> l = new HashMap<Integer, List<Class<?>>>();" +
                    "    }" +
                    "}");
    }

    public void testDiamondNoShorteningForIncorrectParams2() throws Exception {
        performTest("package test;" +
                    "import java.util.HashMap;" +
                    "import java.util.List;" +
                    "import java.util.Map;" +
                    "public class Test {" +
                    "    public static void main(Map<String, List<Class<?>>> l) {" +
                    "        main(new HashMap<Integer, List<Class<?>>>());" +
                    "    }" +
                    "}",
                    "package test;" +
                    "import java.util.HashMap;" +
                    "import java.util.List;" +
                    "import java.util.Map;" +
                    "public class Test {" +
                    "    public static void main(Map<String, List<Class<?>>> l) {" +
                    "        main(new HashMap<Integer, List<Class<?>>>());" +
                    "    }" +
                    "}");
    }

    public void testDiamondNoShorteningForIncorrectParams3() throws Exception {
        performTest("package test;" +
                    "import java.util.HashMap;" +
                    "import java.util.List;" +
                    "import java.util.Map;" +
                    "public class Test {" +
                    "    public Test(Map<String, List<Class<?>>> l) {" +
                    "        new Test(new HashMap<Integer, List<Class<?>>>());" +
                    "    }" +
                    "}",
                    "package test;" +
                    "import java.util.HashMap;" +
                    "import java.util.List;" +
                    "import java.util.Map;" +
                    "public class Test {" +
                    "    public Test(Map<String, List<Class<?>>> l) {" +
                    "        new Test(new HashMap<Integer, List<Class<?>>>());" +
                    "    }" +
                    "}");
    }

    public void testNbBundle() throws Exception {
        performTest("package test;" +
                    "import org.openide.util.NbBundle;" +
                    "public class Test {" +
                    "    public static void main() {" +
                    "        String l = NbBundle.getMessage(Test.class, \"test1\");" +
                    "    }" +
                    "}",
                    "package test;" +
                    "import org.openide.util.NbBundle;" +
                    "public class Test {" +
                    "    public static void main() {" +
                    "        String l = aaa;" +
                    "    }" +
                    "}");
    }

    public void testNBIZ172263() throws Exception {
        performTest("package test;" +
                    "public class Test {" +
                    "    public static void main(Runnable r) {" +
                    "        main(new Runnable() {" +
                    "        });" +
                    "    }" +
                    "}",
                    "package test;" +
                    "public class Test {" +
                    "    public static void main(Runnable r) {" +
                    "        main(new Runnable() {" +
                    "        });" +
                    "    }" +
                    "}");
    }

    private FileObject sourceDir;
    private FileObject buildDir;

    private void performTest(String code, String golden) throws Exception {
        FileObject f = FileUtil.createData(sourceDir, "test/Test.java");
        
        TestUtilities.copyStringToFile(f, code);

        FileObject b = FileUtil.createData(sourceDir, "test/Bundle.properties");

        TestUtilities.copyStringToFile(b, "test1=aaa\ntest2=bbb\n");
        
        CompilationInfo ci = SourceUtilsTestUtil.getCompilationInfo(JavaSource.forFileObject(f), Phase.RESOLVED);

        assertNotNull(ci);
        
        Document doc = ci.getSnapshot().getSource().getDocument(true);
        
        assertNotNull(doc);
        
        Collection<FoldInfo> folds = ShorteningFold.compute(ci, doc, new AtomicBoolean());

        for (FoldInfo i : folds) {
            doc.remove(i.getStart().getOffset(), i.getEnd().getOffset() - i.getStart().getOffset());
            doc.insertString(i.getStart().getOffset(), i.getDescription(), null);
        }

        String real = doc.getText(0, doc.getLength());

        assertEquals(golden, real);
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