package ide.analysis.modernize;

import org.netbeans.modules.ide.analysis.modernize.impl.ModernizeFix;
import org.netbeans.modules.ide.analysis.modernize.impl.YamlParser;
import java.io.File;
import java.nio.file.Files;
import java.util.List;
import static junit.framework.TestCase.fail;
import org.netbeans.modules.cnd.test.CndCoreTestUtils;
import static org.netbeans.modules.nativeexecution.test.NativeExecutionBaseTestCase.createTempFile;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Ilia Gromov
 */
public class HeaderTest extends TidyTestCase {

    private final String source = "src/llvm-header-guard.cpp";
    private final String header = "src/llvm-header-guard.h";

    public HeaderTest() {
        super("project-with-headers");
    }

    public void testLlvmHeaderGuard() {
        performTest(source, "llvm-header-guard", true);
    }

    @Override
    protected void processTestResults(List<ModernizeFix> fixes, String footerName) throws Exception {
        for (ModernizeFix fix : fixes) {
            fix.implement();

            for (YamlParser.Replacement replacement : fix.getReplacements()) {
                FileObject fo = FileUtil.toFileObject(new File(replacement.filePath));

                DataObject dObj = DataObject.find(fo);
                EditorCookie ec = dObj.getLookup().lookup(EditorCookie.class);
                ec.saveDocument();
            }
        }

        File diffFile = createTempFile("diff", null, false);

        boolean diff = CndCoreTestUtils.diff(new File(FileUtil.toFile(getProjectDir()), header), getGoldenFile(header), diffFile);

        if (diff) {
            byte[] readAllBytes = Files.readAllBytes(diffFile.toPath());
            System.err.println(new String(readAllBytes));
            fail();
        }
    }
}
