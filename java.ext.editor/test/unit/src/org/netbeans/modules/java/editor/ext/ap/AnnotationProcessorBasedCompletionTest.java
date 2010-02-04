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
package org.netbeans.modules.java.editor.ext.ap;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.processing.Completion;
import javax.swing.JEditorPane;
import javax.swing.SwingUtilities;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import junit.framework.TestSuite;
import org.netbeans.api.editor.mimelookup.MimePath;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.source.ClasspathInfo;
import org.netbeans.api.java.source.CompilationInfo;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.java.source.JavaSource.Phase;
import org.netbeans.api.java.source.SourceUtilsTestUtil;
import org.netbeans.api.java.source.TestUtilities;
import org.netbeans.core.startup.Main;
import org.netbeans.editor.BaseDocument;
import org.netbeans.junit.NbTestCase;
import org.netbeans.junit.NbTestSuite;
import org.netbeans.modules.editor.NbEditorKit;
import org.netbeans.modules.java.source.usages.IndexUtil;
import org.netbeans.spi.editor.completion.CompletionItem;
import org.netbeans.spi.editor.mimelookup.MimeDataProvider;
import org.netbeans.spi.java.classpath.support.ClassPathSupport;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.URLMapper;
import org.openide.loaders.DataObject;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ServiceProvider;
import static org.junit.Assert.*;

/**
 *
 * @author lahvac
 */
public class AnnotationProcessorBasedCompletionTest extends NbTestCase {

    public AnnotationProcessorBasedCompletionTest(String name) {
        super(name);
    }

//    public static TestSuite suite() {
//        NbTestSuite s = new NbTestSuite();
//
//        s.addTest(new AnnotationProcessorBasedCompletionTest("testTypeFitering2"));
//
//        return s;
//    }

    public void testSimpleExistence() throws Exception {
        performCompletionTest("package test;\n" +
                              "@org.netbeans.modules.java.editor.ext.ap.TestAnnotation(clazz1=|)" +
                              "public class Test {\n" +
                              "}\n",
                              ":java.util.List");
    }

    public void testSimpleExistenceWithPrefix() throws Exception {
        performCompletionTest("package test;\n" +
                              "@org.netbeans.modules.java.editor.ext.ap.TestAnnotation(clazz1=Li|)" +
                              "public class Test {\n" +
                              "}\n",
                              ":java.util.List");
    }

    public void testSimpleExistenceString() throws Exception {
        performCompletionTest("package test;\n" +
                              "@org.netbeans.modules.java.editor.ext.ap.TestAnnotation(string=|)" +
                              "public class Test {\n" +
                              "}\n",
                              ":test");
    }

    public void testSimpleExistenceStringWithPrefix() throws Exception {
        performCompletionTest("package test;\n" +
                              "@org.netbeans.modules.java.editor.ext.ap.TestAnnotation(string=\"t|\")" +
                              "public class Test {\n" +
                              "}\n",
                              ":test");
    }

    public void testSimpleExistenceStringFiltering() throws Exception {
        performCompletionTest("package test;\n" +
                              "@org.netbeans.modules.java.editor.ext.ap.TestAnnotation(string=\"t|\")" +
                              "public class Test {\n" +
                              "}\n",
                              ":test");
    }

    public void testTypeFitering1() throws Exception {
        performCompletionTest("package test;\n" +
                              "@org.netbeans.modules.java.editor.ext.ap.TestAnnotation(multiType=Lin|)" +
                              "public class Test {\n" +
                              "}\n",
                              ":java.util.LinkedList");
    }

    public void testTypeFitering2() throws Exception {
        performCompletionTest("package test;\n" +
                              "@org.netbeans.modules.java.editor.ext.ap.TestAnnotation(multiType=java.util|)" +
                              "public class Test {\n" +
                              "}\n",
                              ":java.util.LinkedList");
    }

    public void XtestSimpleExistenceStringWithPrefixUnclosed() throws Exception {
        performCompletionTest("package test;\n" +
                              "@org.netbeans.modules.java.editor.ext.ap.TestAnnotation(string=\"t|)" +
                              "public class Test {\n" +
                              "}\n",
                              ":test");
    }

    public void testSimpleExistenceWithoutName() throws Exception {
        performCompletionTest("package test;\n" +
                              "@org.netbeans.modules.java.editor.ext.ap.TestAnnotation(|)" +
                              "public class Test {\n" +
                              "}\n",
                              ":test");
    }

    public void testSimpleExistenceArray() throws Exception {
        performCompletionTest("package test;\n" +
                              "@org.netbeans.modules.java.editor.ext.ap.TestAnnotation({\"asdfasdf\", |})" +
                              "public class Test {\n" +
                              "}\n",
                              ":test");
    }

    public void testTypeApplication() throws Exception {
        performApplicationTest("package test;\n" +
                               "@org.netbeans.modules.java.editor.ext.ap.TestAnnotation(clazz1=|)" +
                               "public class Test {\n" +
                               "}\n",
                               "package test;\n\n" +
                               "import java.util.List;\n\n" +
                               "@org.netbeans.modules.java.editor.ext.ap.TestAnnotation(clazz1=List.class)" +
                               "public class Test {\n" +
                               "}\n");
    }

    public void testTypeApplicationWithPrefix() throws Exception {
        performApplicationTest("package test;\n" +
                               "@org.netbeans.modules.java.editor.ext.ap.TestAnnotation(clazz1=Li|)" +
                               "public class Test {\n" +
                               "}\n",
                               "package test;\n\n" +
                               "import java.util.List;\n\n" +
                               "@org.netbeans.modules.java.editor.ext.ap.TestAnnotation(clazz1=List.class)" +
                               "public class Test {\n" +
                               "}\n");
    }

    public void testTypeApplicationWithDotClass() throws Exception {
        performApplicationTest("package test;\n" +
                               "@org.netbeans.modules.java.editor.ext.ap.TestAnnotation(clazz2=|)" +
                               "public class Test {\n" +
                               "}\n",
                               "package test;\n\n" +
                               "import java.util.List;\n\n" +
                               "@org.netbeans.modules.java.editor.ext.ap.TestAnnotation(clazz2=List.class)" +
                               "public class Test {\n" +
                               "}\n");
    }

    public void testStringApplication() throws Exception {
        performApplicationTest("package test;\n" +
                               "@org.netbeans.modules.java.editor.ext.ap.TestAnnotation(string=|)" +
                               "public class Test {\n" +
                               "}\n",
                               "package test;\n" +
                               "@org.netbeans.modules.java.editor.ext.ap.TestAnnotation(string=\"test\")" +
                               "public class Test {\n" +
                               "}\n");
    }

    public void testValueArrayApplication() throws Exception {
        performApplicationTest("package test;\n" +
                               "@org.netbeans.modules.java.editor.ext.ap.TestAnnotation(|)" +
                               "public class Test {\n" +
                               "}\n",
                               "package test;\n" +
                               "@org.netbeans.modules.java.editor.ext.ap.TestAnnotation(\"test\")" +
                               "public class Test {\n" +
                               "}\n");
    }

    public void testValueArrayMultiApplication() throws Exception {
        performApplicationTest("package test;\n" +
                               "@org.netbeans.modules.java.editor.ext.ap.TestAnnotation({\"a\", |})" +
                               "public class Test {\n" +
                               "}\n",
                               "package test;\n" +
                               "@org.netbeans.modules.java.editor.ext.ap.TestAnnotation({\"a\", \"test\"})" +
                               "public class Test {\n" +
                               "}\n");
    }

    private FileObject sourceDir;
    private FileObject buildDir;
    private FileObject testBinaryRoot;

    private void performCompletionTest(String code, String... golden) throws Exception {
        int pos = code.indexOf("|");

        code = code.replace("|", "");

        FileObject f = FileUtil.createData(sourceDir, "test/Test.java");

        TestUtilities.copyStringToFile(f, code);

        ClassPath boot = ClassPathSupport.createClassPath(System.getProperty("sun.boot.class.path"));
        ClassPath compilation = ClassPathSupport.createClassPath(testBinaryRoot);
        ClassPath source = ClassPathSupport.createClassPath(sourceDir);
        JavaSource js = JavaSource.create(ClasspathInfo.create(boot, compilation, source), f);
        CompilationInfo ci = SourceUtilsTestUtil.getCompilationInfo(js, Phase.RESOLVED);

        assertNotNull(ci);

        Document doc = ci.getSnapshot().getSource().getDocument(true);

        assertNotNull(doc);

        Iterable<? extends Completion> output = AnnotationProcessorBasedCompletion.resolveCompletion(ci, pos).keySet();

        if (output == null) {
            output = Collections.emptyList();
        }

        List<String> textual = new LinkedList<String>();

        for (Completion c : output) {
            textual.add(c.getMessage() + ":" + c.getValue());
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

        DataObject od = DataObject.find(f);

        System.err.println(od.getClass());

        EditorCookie ec = od.getLookup().lookup(EditorCookie.class);

        Document d = ec.openDocument();

        System.err.println(d.getClass());

        CompilationInfo ci = SourceUtilsTestUtil.getCompilationInfo(JavaSource.forFileObject(f), Phase.RESOLVED);

        assertNotNull(ci);

        Document doc = ci.getSnapshot().getSource().getDocument(true);

        assertNotNull(doc);
        assertTrue(doc instanceof BaseDocument);

        Map<? extends Completion, ? extends CompletionItem> completions = AnnotationProcessorBasedCompletion.resolveCompletion(ci, pos);

        if (completions == null) {
            fail();
        }

        assertEquals(1, completions.size());

        Entry<? extends Completion, ? extends CompletionItem> e = completions.entrySet().iterator().next();

        JTextComponent c = new JEditorPane();

        c.setDocument(doc);
        c.setCaretPosition(pos);
        e.getValue().defaultAction(c);

        SwingUtilities.invokeAndWait(new Runnable() {
            public void run() {}
        });
        assertEquals(golden, doc.getText(0, doc.getLength()));
//        assertEquals(goldenOutPos, outPos);
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

        testBinaryRoot = URLMapper.findFileObject(TestProcessor.class.getProtectionDomain().getCodeSource().getLocation());

        assertNotNull(testBinaryRoot);

        //XXX:
//        myself = FileUtil.getArchiveRoot(myself);
//
//        assertNotNull(myself);

        SourceUtilsTestUtil.prepareTest(sourceDir, buildDir, cache, new FileObject[] {testBinaryRoot});
    }

    @ServiceProvider(service=MimeDataProvider.class)
    public static final class MimeDataProviderImpl implements MimeDataProvider {

        private static final Lookup L = Lookups.singleton(new NbEditorKit());
        
        public Lookup getLookup(MimePath mimePath) {
            if ("text/x-java".equals(mimePath.getPath())) {
                return L;
            }

            return null;
        }
        
    }
}
