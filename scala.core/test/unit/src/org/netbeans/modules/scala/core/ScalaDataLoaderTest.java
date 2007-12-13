package org.netbeans.modules.scala.core;

import java.net.URL;
import org.netbeans.junit.MockServices;
import org.netbeans.junit.NbTestCase;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.MIMEResolver;
import org.openide.filesystems.URLMapper;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;

/**
 * @author Martin Krauskopf
 */
public class ScalaDataLoaderTest extends NbTestCase {
    
    public ScalaDataLoaderTest(String testName) {
        super(testName);
    }
    
    protected void setUp() throws Exception {
        super.setUp();
        MockServices.setServices(MR.class);
        clearWorkDir();
    }

/* XXX moved to editor module    
    public void testBasicFunctionality() throws Exception {
        URL templateURL = ScalaDataLoader.class.getResource("resources/ScalaTemplate.scala");
        FileObject templateFO = URLMapper.findFileObject(templateURL);
        DataObject templateDO = DataObject.find(templateFO);
        assertNotNull("DataObject found", templateDO);
        FileObject srcDir = FileUtil.createFolder(FileUtil.toFileObject(getWorkDir()), "src/test/sample");
        DataFolder folder = DataFolder.findFolder(srcDir);
        DataObject helloDO = templateDO.createFromTemplate(folder, "Hello");
        assertNotNull(helloDO);
    }
*/
    
    public static final class MR extends MIMEResolver {
        
        public String findMIMEType(final FileObject fo) {
            return fo.getExt().equals("scala")
                    ? ScalaDataLoader.REQUIRED_MIME : null;
        }
        
    }
    
}
