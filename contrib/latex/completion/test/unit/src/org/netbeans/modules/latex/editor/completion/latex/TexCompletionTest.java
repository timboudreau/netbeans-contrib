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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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
package org.netbeans.modules.latex.editor.completion.latex;


import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.swing.JEditorPane;
import javax.swing.text.Document;
import javax.swing.text.StyledDocument;
import org.netbeans.api.lexer.Language;
import org.netbeans.core.startup.Main;
import org.netbeans.junit.NbTestCase;
import org.netbeans.modules.editor.completion.CompletionImpl;
import org.netbeans.modules.editor.completion.CompletionItemComparator;
import org.netbeans.modules.editor.completion.CompletionResultSetImpl;
import org.netbeans.modules.latex.UnitUtilities;
import org.netbeans.modules.latex.bibtex.loaders.MyDataLoader;
import org.netbeans.modules.latex.editor.TexLanguage;
import org.netbeans.modules.latex.model.Utilities;
import org.netbeans.spi.editor.completion.CompletionItem;
import org.netbeans.spi.editor.completion.CompletionProvider;
import org.netbeans.spi.editor.completion.CompletionTask;

import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.text.NbDocument;



/**
 * <FONT COLOR="#CC3333" FACE="Courier New, Monospaced" SIZE="+1">
 * <B>
 * Editor module API test: completion/TexCompletionTest
 * </B>
 * </FONT>
 * 
 * <P>
 * <B>What it tests:</B><BR>
 * The purpose of this test is to test Java (and HTML) code completion. This test
 * is done on some layer between user and API. It uses file and completion
 * is called on the top of the file, but it is never shown.
 * </P>
 * 
 * <P>
 * <B>How it works:</B><BR>
 * TestFile is opened, given text is written to it, and code completion is
 * asked to return response. The type of completion is defined by the type of
 * the file. Unfortunately, it is not possible to ask completion for response
 * without opening the file.
 * </P>
 * 
 * <P>
 * <B>Settings:</B><BR>
 * This test is not complete test, it's only stub, so for concrete test instance
 * it's necessary to provide text to add and whether the response should be
 * sorted. No more settings needed, when runned on clean build.
 * </P>
 * 
 * <P>
 * <B>Output:</B><BR>
 * The output should be completion reponse in human readable form.
 * </P>
 * 
 * <P>
 * <B>Possible reasons of failure:</B><BR>
 * <UL>
 * <LI>An exception when obtaining indent engine (for example if it doesn't exist).</LI>
 * <LI>An exception when writting to indent engine.</LI>
 * <LI>Possibly unrecognized MIME type.</LI>
 * <LI>Indent engine error.</LI>
 * <LI>The file can not be opened. This test must be able to open the file.
 * The test will fail if it is not able to open the file. In case it starts
 * opening sequence, but the editor is not opened, it may lock.</LI>
 * </UL>
 * </P>
 * 
 * @author Jan Lahoda
 * @version 1.0
 */
public class TexCompletionTest extends NbTestCase {
    
    /**
     * Creates new TexCompletionTest
     */
    public TexCompletionTest(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        System.setProperty("netbeans.user", getWorkDir().getAbsolutePath());
        new File(new File(getWorkDir(), "var"), "log").mkdirs();
        UnitUtilities.initLookup();
        UnitUtilities.prepareTest(new String[] {"/org/netbeans/modules/latex/resources/mf-layer.xml", "/org/netbeans/modules/latex/bibtex/layer.xml"}, new Object[] {MyDataLoader.getLoader(MyDataLoader.class)});
        
        FileUtil.setMIMEType("tex", "text/x-tex");
        FileUtil.setMIMEType("bib", "text/x-bibtex");
        
        Main.initializeURLFactory();
        
        TexCompletionItem.TEST_DO_NOT_HIDE = true;
    }
    
    public void testrefargsorted() throws Exception {
        test("\\ref{}", "completion/CommandCompletionTest.tex", 5, 5);
    }
    
    public void testcite1sorted() throws Exception {
        test("", "completion/CiteTest1.tex", 6, 12);
    }
    
    public void testcite2sorted() throws Exception {
        test("", "completion/CiteTest2.tex", 9, 6);
    }
    
    public void testcite3sorted() throws Exception {
        test("", "completion/CiteTest3.tex", 6, 12);
    }
    
    public void testcite4sorted() throws Exception {
        test("", "completion/CiteTest4.tex", 5, 10);
    }
    
    public void testnewenvreftestsorted() throws Exception {
        test("", "completion/NewEnvRefTest.tex", 11, 7);
    }
    
    public void testnewenvreftest2sorted() throws Exception {
        test("", "completion/NewEnvRefTest2.tex", 9, 15);
    }
    
    public void testnewcmdreftestsorted() throws Exception {
        test("", "completion/NewCmdRefTest.tex", 7, 10);
    }
    
    public void testnotfulltest1sorted() throws Exception {
        test("", "completion/NotFullTest1.tex", 5, 4);
    }
    
    public void testnotfulltest2sorted() throws Exception {
        test("", "completion/NotFullTest2.tex", 3, 4);
    }
    
    public void testmultiplepackages1sorted() throws Exception {
        test("", "completion/MultiplePackagesTest1.tex", 3, 26);
    }
//    
//    public void testmultiplebibliographies1sorted() throws Exception {
//        test("", "completion/MultipleBibliographies1.tex", 6, 12);
//    }
//
    public void testArgumentNotProsedBeforeArgStart() throws Exception {
        test("", "completion/RefTest.tex", 6, 5);
    }

    public void testEndTest1() throws Exception {
        test("", "completion/EndTest1.tex", 7, 3);
    }

//    public void testEndTest2() throws Exception {
//        test("", "completion/EndTest2.tex", 5, 3);
//    }

    public void testEndTest3a() throws Exception {
        test("", "completion/EndTest1.tex", 7, 3, 0);
    }
    
    public void testNotFullTest3a() throws Exception {
        test("", "completion/NotFullTest3a.tex", 4, 7, 0);
    }

    public void testNotFullTest3b() throws Exception {
        test("", "completion/NotFullTest3b.tex", 4, 12, 0);
    }

    public void testIncompleteEnvName() throws Exception {
        test("", "completion/IncompleteEnvName.tex", 5, 9);
    }
    
    public void testCommandCommitTest() throws Exception {
        test("\\newcomma", "completion/CommandCommitTest.tex", 4, 9, 0);
    }
    
    public void testRefArgCorrectly() throws Exception {
        test("", "completion/RefArgCorrectlyReplaced.tex", 6, 5, 0);
    }
    
    public void testCountersTest1() throws Exception {
        test("", "completion/CountersTest.tex", 7, 8);
    }
    
    public void testCountersTest2() throws Exception {
        test("", "completion/CountersTest.tex", 8, 10);
    }
    
    private List<? extends CompletionItem> getItems(JEditorPane editor) throws Exception {
        CompletionProvider provider = new TexCompletion();
        
        CompletionTask compTask = provider.createTask(CompletionProvider.COMPLETION_QUERY_TYPE, editor);
        
        assertNotNull(compTask);
        
        CompletionImpl impl = CompletionImpl.get();
        
        Constructor[] ctors = CompletionResultSetImpl.class.getDeclaredConstructors();
        
        assertEquals(1, ctors.length);
        
	ctors[0].setAccessible(true);
	
        CompletionResultSetImpl resultSetImpl = (CompletionResultSetImpl) ctors[0].newInstance(new Object[] {impl, "TestResult", compTask, CompletionProvider.COMPLETION_QUERY_TYPE}); // NOI18N
        
        compTask.query(resultSetImpl.getResultSet());
        
        long started = System.currentTimeMillis();
        
        while (!resultSetImpl.isFinished() && (System.currentTimeMillis() - started) < 1000000)
            Thread.sleep(100);
        
        assertTrue(resultSetImpl.isFinished());

        List<? extends CompletionItem> items = resultSetImpl.getItems();

        Collections.sort(items, CompletionItemComparator.BY_PRIORITY);
        
        return items;
    }

    private void completionQuery(JEditorPane  editor) throws Exception {
        List<? extends CompletionItem> items = getItems(editor);
        
        for (CompletionItem item : items) {
            ref(item.toString());
        }
        
        //make sure the file is created:
        getRef();
    }
    
    private void test(String assign, String testFileName, int line, int column) throws Exception {
        FileObject     testFileObject = getTestFile(testFileName);
        JEditorPane    editor         = getAnEditorPane(testFileObject);
        StyledDocument doc            = (StyledDocument) editor.getDocument();
        int            lineOffset     = NbDocument.findLineOffset(doc, line - 1);
        
        editor.getCaret().setDot(lineOffset);
        doc.insertString(lineOffset, assign, null);
        editor.getCaret().setDot(lineOffset + column);
        
        completionQuery(editor);
        
        assertFile("Output does not match golden file.", getGoldenFile(), new File(getWorkDir(), this.getName() + ".ref"), new File(getWorkDir(), this.getName() + ".diff"));
    }

    private void test(String assign, String testFileName, int line, int column, int itemIndexToCommit) throws Exception {
        FileObject     testFileObject = getTestFile(testFileName);
        JEditorPane    editor         = getAnEditorPane(testFileObject);
        StyledDocument doc            = (StyledDocument) editor.getDocument();
        int            lineOffset     = NbDocument.findLineOffset(doc, line - 1);
        
        editor.getCaret().setDot(lineOffset);
        doc.insertString(lineOffset, assign, null);
        editor.getCaret().setDot(lineOffset + column);

        List<? extends CompletionItem> items = getItems(editor);

        assertTrue(items.toString(), itemIndexToCommit < items.size());

        items.get(itemIndexToCommit).defaultAction(editor);

        getRef().print(doc.getText(0, doc.getLength()));
        
        assertFile("Output does not match golden file.", getGoldenFile(), new File(getWorkDir(), this.getName() + ".ref"), new File(getWorkDir(), this.getName() + ".diff"));
    }
    
    private FileObject getTestFile(String testFile) throws IOException, InterruptedException {
        FileObject data = FileUtil.toFileObject(getDataDir());
        
        assertNotNull(data);
        
        FileObject test = data.getFileObject(testFile);
        
        assertNotNull(test);
        
        return test;
    }

    private static JEditorPane getAnEditorPane(FileObject file) throws Exception {
        Document doc = Utilities.getDefault().openDocument(file);
        JEditorPane pane = new JEditorPane();
        
        pane.setContentType("text/x-tex");
        pane.setDocument(doc);
        
        doc.putProperty(Language.class, TexLanguage.description());
        
        return pane;
    }

    protected boolean runInEQ() {
        return true;
    }
    
}
