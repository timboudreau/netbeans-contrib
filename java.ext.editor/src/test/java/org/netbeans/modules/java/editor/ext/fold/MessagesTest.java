package org.netbeans.modules.java.editor.ext.fold;

import com.sun.source.tree.Tree.Kind;
import com.sun.source.util.TreePath;
import java.io.File;
import java.util.Arrays;
import java.util.regex.Pattern;
import org.netbeans.core.startup.Main;
import org.netbeans.api.java.source.CompilationInfo;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.java.source.JavaSource.Phase;
import org.netbeans.api.java.source.SourceUtilsTestUtil;
import org.netbeans.api.java.source.TestUtilities;
import org.netbeans.junit.NbTestCase;
import org.netbeans.modules.java.editor.ext.fold.Messages.Call;
import org.netbeans.modules.java.source.usages.IndexUtil;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.URLMapper;
import org.openide.util.NbBundle;

/**
 *
 * @author lahvac
 */
public class MessagesTest extends NbTestCase {

    public MessagesTest(String name) {
        super(name);
    }

    public void testSimple() throws Exception {
        performTest("package test;\n" +
                    "public class Test {\n" +
                    "    private void test() {\n" +
                    "        org.openide.util.NbBundle.getMessage|(Test.class, \"test1\");" +
                    "    }\n" +
                    "}\n",
                    new Call(FileUtil.createData(sourceDir, "test/Bundle.properties"), "test1", new String[0]));
    }

    public void testConcat1() throws Exception {
        performTest("package test;\n" +
                    "public class Test {\n" +
                    "    private static final String C = \"1\";\n" +
                    "    private void test() {" +
                    "        org.openide.util.NbBundle.getMessage|(Test.class, \"te\" + \"st\" + C);" +
                    "    }\n" +
                    "}\n",
                    new Call(FileUtil.createData(sourceDir, "test/Bundle.properties"), "test1", new String[0]));
    }

    public void testConcat2() throws Exception {
        performTest("package test;\n" +
                    "public class Test {\n" +
                    "    private static final String C = \"1\";\n" +
                    "    private void test() {" +
                    "        org.openide.util.NbBundle.getMessage|(Test.class, \"te\" + \"st\" + test.Test.C);" +
                    "    }\n" +
                    "}\n",
                    new Call(FileUtil.createData(sourceDir, "test/Bundle.properties"), "test1", new String[0]));
    }

    public void testConcat3() throws Exception {
        performTest("package test;\n" +
                    "public class Test {\n" +
                    "    private static final String C = \"1\";\n" +
                    "    private void test() {" +
                    "        org.openide.util.NbBundle.getMessage|(Test.class, \"te\" + \"st\" + get());" +
                    "    }" +
                    "    \n" +
                    "    public static String g() {return C;}\n" +
                    "}\n",
                    null);
    }

    public void testSourceLevel14iz169833() throws Exception {
        performTest("package test;\n" +
                    "public class Test {\n" +
                    "    private static final String C = \"1\";\n" +
                    "    private void test() {" +
                    "        org.openide.util.NbBundle.getMessage|(Test.class, \"te\" + \"st\" + get());" +
                    "    }" +
                    "    \n" +
                    "    public static String g() {return C;}\n" +
                    "}\n",
                    "1.4",
                    null);
    }

    private FileObject sourceDir;
    private FileObject buildDir;

    private void performTest(String code, Call golden) throws Exception {
        performTest(code, "1.5", golden);
    }
    
    private void performTest(String code, String sourceLevel, Call golden) throws Exception {
        int pos = code.indexOf('|');
        
        code = code.replaceAll(Pattern.quote("|"), "");

        FileObject f = FileUtil.createData(sourceDir, "test/Test.java");

        TestUtilities.copyStringToFile(f, code);
        SourceUtilsTestUtil.setSourceLevel(f, sourceLevel);

        FileObject b = FileUtil.createData(sourceDir, "test/Bundle.properties");

        TestUtilities.copyStringToFile(b, "test1=aaa\ntest2=bbb\n");

        CompilationInfo ci = SourceUtilsTestUtil.getCompilationInfo(JavaSource.forFileObject(f), Phase.RESOLVED);

        assertNotNull(ci);

        TreePath tp = ci.getTreeUtilities().pathFor(pos);

        while (tp.getLeaf().getKind() != Kind.METHOD_INVOCATION)
            tp = tp.getParentPath();

        Call real = Messages.resolvePossibleMessageCall(ci, tp);

        if (golden == null && real == null) {
            return ;
        }
        
        assertEquals(golden.bundle, real.bundle);
        assertEquals(golden.key, real.key);
        assertTrue((golden.parameters == real.parameters) || Arrays.equals(golden.parameters, real.parameters));
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
