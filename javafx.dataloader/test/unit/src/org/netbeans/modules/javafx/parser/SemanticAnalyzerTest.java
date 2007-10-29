/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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

package org.netbeans.modules.javafx.parser;

import java.util.HashMap;
import java.util.Map;
import javax.swing.text.Document;
import org.netbeans.api.gsf.ColoringAttributes;
import org.netbeans.api.gsf.CompilationInfo;
import org.netbeans.api.gsf.OffsetRange;
import org.netbeans.junit.NbTestCase;
import org.netbeans.editor.BaseDocument;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.openide.ErrorManager;
import org.netbeans.modules.javafx.lexer.JavaFXTokenId;
import java.io.File;
import org.openide.filesystems.FileUtil;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.nio.CharBuffer;
import org.netbeans.modules.javafx.editor.JavaFXPier;

import net.java.javafx.typeImpl.Compilation;
import net.java.javafx.type.expr.CompilationUnit;
import java.io.StringReader;
import javax.swing.text.Document;
import org.openide.cookies.EditorCookie;

/**
 * Test the semantic analyzer / highlighter
 * 
 * @author Tor Norbye
 */
public class SemanticAnalyzerTest extends NbTestCase {

    public SemanticAnalyzerTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws java.lang.Exception {
    }

    @Override
    protected void tearDown() throws java.lang.Exception {
    }

    private String annotate(Document doc, Map<OffsetRange, ColoringAttributes> highlights) throws Exception {
        StringBuilder sb = new StringBuilder();
        String text = doc.getText(0, doc.getLength());
        Map<Integer, OffsetRange> starts = new HashMap<Integer, OffsetRange>(100);
        Map<Integer, OffsetRange> ends = new HashMap<Integer, OffsetRange>(100);
        for (OffsetRange range : highlights.keySet()) {
            starts.put(range.getStart(), range);
            ends.put(range.getEnd(), range);
        }

        for (int i = 0; i < text.length(); i++) {
            if (starts.containsKey(i)) {
                sb.append("|>");
                OffsetRange range = starts.get(i);
                ColoringAttributes ca = highlights.get(range);
                if (ca != null) {
                    sb.append(ca.name());
                    sb.append(':');
                }
            }
            if (ends.containsKey(i)) {
                sb.append("<|");
                
            }
            sb.append(text.charAt(i));
        }

        return sb.toString();
    }

    protected BaseDocument getDocument(FileObject fo) {
        try {
            return getDocument(readFile(fo));
        }
        catch (Exception ex){
            fail(ex.toString());
            return null;
        }
    }
    
    
    private String readFile(final FileObject rakeTargetFile) {
        try {
            final StringBuilder sb = new StringBuilder(5000);
            rakeTargetFile.getFileSystem().runAtomicAction(new FileSystem.AtomicAction() {

                public void run() throws IOException {

                    if (rakeTargetFile == null) {
                        return;
                    }

                    InputStream is = rakeTargetFile.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is));

                    while (true) {
                        String line = reader.readLine();

                        if (line == null) {
                            break;
                        }

                        sb.append(line);
                        sb.append('\n');
                    }
                }
            });

            if (sb.length() > 0) {
                return sb.toString();
            } else {
                return null;
            }
        }
        catch (IOException ioe){
            ErrorManager.getDefault().notify(ioe);

            return null;
        }
    }
    
    protected BaseDocument getDocument(String s) {
        try {
            BaseDocument doc = new BaseDocument(null, false);
            doc.putProperty(org.netbeans.api.lexer.Language.class, JavaFXTokenId.language());
            doc.insertString(0, s, null);

            return doc;
        }
        catch (Exception ex){
            fail(ex.toString());
            return null;
        }
    }
    
    protected FileObject getTestFile(String relFilePath) {
        File wholeInputFile = new File(getDataDir(), relFilePath);
        if (!wholeInputFile.exists()) {
            NbTestCase.fail("File " + wholeInputFile + " not found.");
        }
        FileObject fo = FileUtil.toFileObject(wholeInputFile);
        assertNotNull(fo);

        return fo;
    }

    public CompilationInfo getInfo(String file) throws Exception {
        FileObject fileObject = getTestFile(file);

        String text = readFile(fileObject);
        BaseDocument doc = getDocument(text);
        
        CompilationInfo info = new TestCompilationInfo(fileObject, doc, text);
        
        assertNotNull(info);

        return info;
    }
    
    protected File getDataSourceDir() {
        // Check whether token dump file exists
        // Try to remove "/build/" from the dump file name if it exists.
        // Otherwise give a warning.
        File inputFile = getDataDir();
        String inputFilePath = inputFile.getAbsolutePath();
        boolean replaced = false;
        if (inputFilePath.indexOf("/build/test/") != -1) {
            inputFilePath = inputFilePath.replace("/build/test/", "/test/");
            replaced = true;
        }
        if (!replaced && inputFilePath.indexOf("/test/work/sys/") != -1) {
            inputFilePath = inputFilePath.replace("/test/work/sys/", "/test/unit/");
            replaced = true;
        }
        if (!replaced) {
            System.err.println("Warning: Attempt to use dump file " +
                    "from sources instead of the generated test files failed.\n" +
                    "Patterns '/build/test/' or '/test/work/sys/' not found in " + inputFilePath
            );
        }
        inputFile = new File(inputFilePath);
        assertTrue(inputFile.exists());
        
        return inputFile;
    }
    
    protected File getDataFile(String relFilePath) {
        File inputFile = new File(getDataSourceDir(), relFilePath);
        
        return inputFile;
    }
    
    protected static String readFile(File f) throws Exception {
        FileReader r = new FileReader(f);
        int fileLen = (int)f.length();
        CharBuffer cb = CharBuffer.allocate(fileLen);
        r.read(cb);
        cb.rewind();
        return cb.toString();
    }
    
    protected void assertDescriptionMatches(String relFilePath, String description, boolean includeTestName, String ext) throws Exception {
        File rubyFile = getDataFile(relFilePath);
        if (!rubyFile.exists()) {
            NbTestCase.fail("File " + rubyFile + " not found.");
        }

        File goldenFile = getDataFile(relFilePath + (includeTestName ? ("." + getName()) : "") + ext);
        if (!goldenFile.exists()) {
            if (!goldenFile.createNewFile()) {
                NbTestCase.fail("Cannot create file " + goldenFile);
            }
            FileWriter fw = new FileWriter(goldenFile);
            try {
                fw.write(description);
            }
            finally {
                fw.close();
            }
            NbTestCase.fail("Created generated golden file " + goldenFile + "\nPlease re-run the test.");
        }

        String expected = readFile(goldenFile);

        // Because the unit test differ is so bad...
        if (false) { // disabled
            if (!expected.equals(description)) {
                BufferedWriter fw = new BufferedWriter(new FileWriter("/tmp/expected.txt"));
                fw.write(expected);
                fw.close();
                fw = new BufferedWriter(new FileWriter("/tmp/actual.txt"));
                fw.write(description);
                fw.close();
            }
        }

        assertEquals(expected.trim(), description.trim());
    }
    
    private void checkSemantic(String relFilePath) throws Exception {
        SemanticAnalysis analyzer = new SemanticAnalysis();
        CompilationInfo info = getInfo(relFilePath);
        
        String text = info.getDocument().getText(0, info.getDocument().getLength());
        Compilation compilation = JavaFXPier.getNewCompilation(info.getFileObject());
        JavaFXPier.readCompilationUnit(compilation, info.getFileObject().getPath(), new StringReader(text));

        analyzer.run(info);
        Map<OffsetRange, ColoringAttributes> highlights = analyzer.getHighlights();

        String annotatedSource = annotate(info.getDocument(), highlights);

        assertDescriptionMatches(relFilePath, annotatedSource, false, ".semantic");
    }

    public void testAnalysis() throws Exception {
        checkSemantic("code.fx");
    }

/*    public void testAnalysis2() throws Exception {
        checkSemantic("testfiles/ape.rb");
    }

    public void testAnalysis3() throws Exception {
        checkSemantic("testfiles/date.rb");
    }

    public void testAnalysis4() throws Exception {
        checkSemantic("testfiles/resolv.rb");
    }

    public void testUnused() throws Exception {
        checkSemantic("testfiles/unused.rb");
    }*/
}
