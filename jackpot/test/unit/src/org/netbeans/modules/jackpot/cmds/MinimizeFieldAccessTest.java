/*
 * MinimizeFieldAccessTest.java
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
 * MinimizeFieldAccess transformer unit test.
 */
public class MinimizeFieldAccessTest extends NbTestCase {
    private static final String transformer = "org.netbeans.modules.jackpot.cmds.MinimizeFieldAccess";
    
    public MinimizeFieldAccessTest(String testName) {
        super(testName);
    }
    
    protected void setUp() throws Exception {
        TestUtilities.makeScratchDir(this);
    }
    
    /**
     * Verify that public constants referenced from another package are ignored.
     */
    public void testPublicConstants() throws Exception {
        File base = copyDataToWorkDir("one/PublicStatics.java");
        File subclass = copyDataToWorkDir("two/StaticsReference.java");
        if (TestUtilities.applyTransformer(getWorkDir(), transformer) != 0)
            fail("transformation failed: too many modifications");
    }
    
    private File copyDataToWorkDir(String relativePath) throws IOException {
        String path = "MinimizeFieldAccess/" + relativePath;
        File orig = new File(getDataDir(), path);
        assert orig.exists();
        File src = new File(getWorkDir(), relativePath);
        src.getParentFile().mkdir();
        FileUtil.copy(new FileInputStream(orig), new FileOutputStream(src));
        return src;
    }
}
