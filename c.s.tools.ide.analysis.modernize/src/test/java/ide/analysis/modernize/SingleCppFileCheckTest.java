package ide.analysis.modernize;

import com.sun.tools.ide.analysis.modernize.impl.ModernizeFix;
import java.io.File;
import java.nio.file.Files;
import java.util.List;
import org.junit.Test;
import org.netbeans.modules.cnd.test.CndCoreTestUtils;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;

/**
 *
 * @author Ilia Gromov
 */
public class SingleCppFileCheckTest extends TidyTestCase {

    public SingleCppFileCheckTest() {
        super("hello-cmake-world");
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testModernizeUseNullptr() {
        performTest("src/modernize-use-nullptr.cpp", "modernize-use-nullptr", true);
    }

    public void testModernizeLoopConvert() {
        performTest("src/modernize-loop-convert.cpp", "modernize-loop-convert", true);
    }

    @Test
    public void testModernizeRawStringLiteral() {
        performTest("src/modernize-raw-string-literal.cpp", "modernize-raw-string-literal", true);
    }

    @Override
    protected void processTestResults(List<ModernizeFix> fixes, String startFileName) throws Exception {
        for (ModernizeFix fix : fixes) {
            fix.implement();
        }

        FileObject footer = getSourceFile(startFileName);

        DataObject dObj = DataObject.find(footer);
        EditorCookie ec = dObj.getLookup().lookup(EditorCookie.class);
        ec.saveDocument();

        File diffFile = createTempFile("diff", null, false);

        boolean diff = CndCoreTestUtils.diff(FileUtil.toFile(footer), getGoldenFile(startFileName), diffFile);

        if (diff) {
            byte[] readAllBytes = Files.readAllBytes(diffFile.toPath());
            System.err.println(new String(readAllBytes));
            fail();
        }
    }
}
