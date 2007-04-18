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
import org.netbeans.api.jackpot.Transformer;
import org.netbeans.api.jackpot.test.TestUtilities;
import org.netbeans.junit.NbTestCase;
import org.netbeans.modules.jackpot.engine.Engine;
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
    public void testChangePublicConstants() throws Exception {
        File base = copyDataToWorkDir("one/PublicStatics.java");
        File subclass = copyDataToWorkDir("two/StaticsReference.java");
        MinimizeFieldAccess xform = (MinimizeFieldAccess)Engine.createCommand(transformer);
        xform.setIgnoreConstants(false);
        if (TestUtilities.applyTransformer(getWorkDir(), xform) == 0)
            fail("constant not changed");
    }
    
    /**
     * Verify that public constants referenced from another package are ignored.
     */
    public void testIgnorePublicConstants() throws Exception {
        File base = copyDataToWorkDir("one/PublicStatics.java");
        File subclass = copyDataToWorkDir("two/StaticsReference.java");
        MinimizeFieldAccess xform = (MinimizeFieldAccess)Engine.createCommand(transformer);
        xform.setIgnoreConstants(true);
        if (TestUtilities.applyTransformer(getWorkDir(), xform) != 0)
            fail("constant not ignored");
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
    
    /**
     * Verify that a public variable is made private.
     */
    public void testPublicToPrivate() throws Exception {
        String code = 
            "package x.y;\n" +
            "class PublicToPrivate {\n" +
            "    public int n = 0;\n" + 
            "    int count() {\n" +
            "        return ++n;\n" +
            "    }\n" +
            "}\n";
        
        String golden =
            "package x.y;\n" +
            "class PublicToPrivate {\n" +
            "    private int n = 0;\n" + 
            "    int count() {\n" +
            "        return ++n;\n" +
            "    }\n" +
            "}\n";

        File java = new File(getWorkDir(), "PublicToPrivate.java");
        TestUtilities.copyStringToFile(java, code);
        if (TestUtilities.applyTransformer(getWorkDir(), transformer) == 0) {
            fail("transformation failed");
        }
        String result = TestUtilities.copyFileToString(java);
        assertEquals(golden, result);
    }
}
