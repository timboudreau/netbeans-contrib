package org.netbeans.modules.scala.core;

import org.netbeans.junit.NbTestCase;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 * @author Martin Krauskopf
 */
public class ScalaDataObjectTest extends NbTestCase {
    
    public ScalaDataObjectTest(String testName) {
        super(testName);
    }
    
    public void testGetPackageName() throws Exception {
        FileObject workDirFO = FileUtil.toFileObject(getWorkDir());
        FileObject scalaClass = FileUtil.createData(workDirFO, "src/my/test/sample/HelloWorld.scala");
        assertEquals("right package", "my.test.sample", ScalaDataObject.getPackageName(scalaClass));
    }
    
}
