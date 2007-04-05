/*
 * MinimizeMethodAccessTest.java
 *
 * Created on December 18, 2006, 11:33 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.netbeans.modules.jackpot.cmds;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import org.netbeans.api.jackpot.test.TestUtilities;
import org.netbeans.junit.NbTestCase;
import org.openide.filesystems.FileUtil;

/**
 * MinimizeMethodAccess transformer unit test.
 */
public class MinimizeMethodAccessTest extends NbTestCase {
    private static final String transformer = "org.netbeans.modules.jackpot.cmds.MinimizeMethodAccess";
    
    public MinimizeMethodAccessTest(String testName) {
        super(testName);
    }
    
    protected void setUp() throws Exception {
        TestUtilities.makeScratchDir(this);
    }

    /**
     * Verify that implemented methods are not minimized.
     */
    public void testIgnoreImplemented() throws Exception {
        File orig = new File(getDataDir(), "MinimizeMethodAccess/MyTableModel.java");
        assert orig.exists();
        File src = new File(getWorkDir(), "MyTableModel.java");
        FileUtil.copy(new FileInputStream(orig), new FileOutputStream(src));
        
        if (TestUtilities.applyTransformer(getWorkDir(), transformer) != 0)
            fail("transformation failed: too many modifications");
    }
    
    /**
     * Verify that abstract methods are handled correctly.
     */
    public void testAbstractMethods() throws Exception {
        File base = copyDataToWorkDir("one/AbstractBase.java");
        File subclass = copyDataToWorkDir("two/Subclass.java");
        if (TestUtilities.applyTransformer(getWorkDir(), transformer) != 0)
            fail("transformation failed: too many modifications");
    }
    
    private File copyDataToWorkDir(String relativePath) throws IOException {
        String path = "MinimizeMethodAccess/" + relativePath;
        File orig = new File(getDataDir(), path);
        assert orig.exists();
        File src = new File(getWorkDir(), relativePath);
        src.getParentFile().mkdir();
        FileUtil.copy(new FileInputStream(orig), new FileOutputStream(src));
        return src;
    }
}
