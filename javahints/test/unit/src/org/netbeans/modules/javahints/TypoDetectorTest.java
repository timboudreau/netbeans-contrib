/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.javahints;

import com.sun.source.util.TreePath;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import javax.swing.text.Document;
import org.netbeans.api.java.lexer.JavaTokenId;
import org.netbeans.api.java.source.CompilationInfo;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.java.source.JavaSource.Phase;
import org.netbeans.api.java.source.SourceUtilsTestUtil;
import org.netbeans.api.lexer.Language;
import org.netbeans.junit.NbTestCase;
import org.netbeans.spi.editor.hints.Fix;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.LocalFileSystem;
import org.openide.filesystems.Repository;
import org.openide.loaders.DataObject;

/**
 *
 * @author Jan Lahoda
 */
public class TypoDetectorTest extends NbTestCase {
    
    public TypoDetectorTest(String testName) {
        super(testName);
    }
    
    protected void setUp() throws Exception {
        super.setUp();
        SourceUtilsTestUtil.prepareTest(new String[] {"org/netbeans/modules/java/editor/resources/layer.xml"}, new Object[0]);
        TypoDetector.LOG.setLevel(Level.FINE);
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testSimple1() throws Exception {
        performAnalysisTest("test/Test.java", "package test; public class test {private int test; public void dfgh() {tfst = 0;}}", 119 - 48, Arrays.asList("rename: test"));
    }
    
    public void testSimple2() throws Exception {
        performAnalysisTest("test/Test.java", "package test; public class test {public void dfgh() {tfst ffff = null;} private int test;}", 101 - 48, Arrays.asList("rename: test"));
    }
    
    public void testSimple3() throws Exception {
        performAnalysisTest("test/Test.java", "package test; public class test {public void test() {tfst();} private int test;}", 101 - 48, Arrays.asList("rename: test"));
    }
    
    public void testSimple4() throws Exception {
        performAnalysisTest("test/Test.java", "package test; public class test {public void test() {java.util.List<tfst> t = null;}}", 116 - 48, Arrays.asList("rename: test"));
    }
    
    public void testSimple5() throws Exception {
        performAnalysisTest("test/Test.java", "package test; public class test {public void test() {java.util.List<tfst> t = null;} private int test;}", 116 - 48, Arrays.asList("rename: test"));
    }
    
    public void testSimple6() throws Exception {
        performAnalysisTest("test/Test.java", "package test; public class test {@Overide public int hashCode() {return 0;} private int Overidf;}", 86 - 48, Arrays.asList("rename: Override"));
    }
    
    protected void prepareTest(String fileName, String code) throws Exception {
        FileObject workFO = makeScratchDir(this);
        
        assertNotNull(workFO);
        
        FileObject sourceRoot = workFO.createFolder("src");
        FileObject buildRoot  = workFO.createFolder("build");
        FileObject cache = workFO.createFolder("cache");
        
        FileObject data = FileUtil.createData(sourceRoot, fileName);
        
        writeIntoFile(data, code);
        
        SourceUtilsTestUtil.prepareTest(sourceRoot, buildRoot, cache);
        
        DataObject od = DataObject.find(data);
        EditorCookie ec = od.getCookie(EditorCookie.class);
        
        assertNotNull(ec);
        
        doc = ec.openDocument();
        doc.putProperty(Language.class, JavaTokenId.language());
        
        JavaSource js = JavaSource.forFileObject(data);
        
        assertNotNull(js);
        
        info = SourceUtilsTestUtil.getCompilationInfo(js, Phase.RESOLVED);
        
        assertNotNull(info);
    }
    
    private CompilationInfo info;
    private Document doc;
    
    private void performAnalysisTest(String fileName, String code, int pos, List<String> golden) throws Exception {
        prepareTest(fileName, code);
        
        TreePath path = info.getTreeUtilities().pathFor(pos);
        
        List<Fix> fixes = new TypoDetector().run(info, "compiler.err.doesnt.exist", pos, path, null);
        List<String> fixNames = new ArrayList<String>();
        
        assertNotNull(fixes);
        
        for (Fix f : fixes) {
            fixNames.add(((TypoDetector.FixImpl) f).toDebugString());
        }
        
        assertEquals(golden, fixNames);
    }
    
    /**Copied from org.netbeans.api.project.
     * Create a scratch directory for tests.
     * Will be in /tmp or whatever, and will be empty.
     * If you just need a java.io.File use clearWorkDir + getWorkDir.
     */
    public static FileObject makeScratchDir(NbTestCase test) throws IOException {
        test.clearWorkDir();
        File root = test.getWorkDir();
        assert root.isDirectory() && root.list().length == 0;
        FileObject fo = FileUtil.toFileObject(root);
        if (fo != null) {
            // Presumably using masterfs.
            return fo;
        } else {
            // For the benefit of those not using masterfs.
            LocalFileSystem lfs = new LocalFileSystem();
            try {
                lfs.setRootDirectory(root);
            } catch (PropertyVetoException e) {
                assert false : e;
            }
            Repository.getDefault().addFileSystem(lfs);
            return lfs.getRoot();
        }
    }
    
    private void writeIntoFile(FileObject file, String what) throws Exception {
        FileLock lock = file.lock();
        OutputStream out = file.getOutputStream(lock);
        
        try {
            out.write(what.getBytes());
        } finally {
            out.close();
            lock.releaseLock();
        }
    }
    
}
