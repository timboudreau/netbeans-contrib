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

package org.netbeans.modules.latex.hints;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import javax.swing.text.StyledDocument;
import junit.framework.Assert;
import org.netbeans.api.gsf.CancellableTask;
import org.netbeans.api.lexer.Language;
import org.netbeans.core.startup.Main;
import org.netbeans.junit.NbTestCase;
import org.netbeans.modules.latex.UnitUtilities;
import org.netbeans.modules.latex.bibtex.loaders.MyDataLoader;
import org.netbeans.modules.latex.editor.TexLanguage;
import org.netbeans.modules.latex.model.LaTeXParserResult;
import org.netbeans.modules.latex.model.command.Node;
import org.netbeans.napi.gsfret.source.CompilationController;
import org.netbeans.napi.gsfret.source.CompilationInfo;
import org.netbeans.napi.gsfret.source.Phase;
import org.netbeans.napi.gsfret.source.Source;
import org.netbeans.spi.editor.hints.ChangeInfo;
import org.netbeans.spi.editor.hints.ChangeInfo.Change;
import org.netbeans.spi.editor.hints.ErrorDescription;
import org.netbeans.spi.editor.hints.Fix;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;

/**
 *
 * @author Jan Lahoda
 */
public class AddItemHintTest extends NbTestCase {
    
    public AddItemHintTest(String testName) {
        super(testName);
    }            

    @Override
    protected void setUp() throws Exception {
        System.setProperty("netbeans.user", getWorkDir().getAbsolutePath());
        new File(new File(getWorkDir(), "var"), "log").mkdirs();
        UnitUtilities.initLookup();
        UnitUtilities.prepareTest(new String[] {"/org/netbeans/modules/latex/resources/mf-layer.xml", "/org/netbeans/modules/latex/bibtex/layer.xml"}, new Object[] {MyDataLoader.getLoader(MyDataLoader.class)});
        
        FileUtil.setMIMEType("tex", "text/x-tex");
        FileUtil.setMIMEType("bib", "text/x-bibtex");
        
        Main.initializeURLFactory();
        
        super.setUp();
    }
    
    public void testSimple1() throws Exception {
        testFix("\\documentclass{article}\n" + 
             "\\begin{document}\n" + 
             "\\begin{itemize}\n" + 
             "\\item{test|}\n" + 
             "\\end{itemize}\n" +
             "\\end{document}\n",
             "Add \\item below",
             "\\documentclass{article}\n" + 
             "\\begin{document}\n" + 
             "\\begin{itemize}\n" + 
             "\\item{test}\n" + 
             "\\item{|}\n" + 
             "\\end{itemize}\n" +
             "\\end{document}\n");
    }
    
    public void testSimple2() throws Exception {
        testFix("\\documentclass{article}\n" + 
             "\\begin{document}\n" + 
             "\\begin{itemize}\n" + 
             "\\item{|test}\n" + 
             "\\end{itemize}\n" +
             "\\end{document}\n",
             "Add \\item above",
             "\\documentclass{article}\n" + 
             "\\begin{document}\n" + 
             "\\begin{itemize}\n" + 
             "\\item{|}\n" + 
             "\\item{test}\n" + 
             "\\end{itemize}\n" +
             "\\end{document}\n");
    }
    
    public void testSimple3() throws Exception {
        testFix("\\documentclass{article}\n" + 
             "\\begin{document}\n" + 
             "\\begin{itemize}\n" + 
             "\\item test|\n" + 
             "\\end{itemize}\n" +
             "\\end{document}\n",
             "Add \\item below",
             "\\documentclass{article}\n" + 
             "\\begin{document}\n" + 
             "\\begin{itemize}\n" + 
             "\\item test\n" + 
             "\\item |\n" + 
             "\\end{itemize}\n" +
             "\\end{document}\n");
    }
    
    public void testSimple4() throws Exception {
        testFix("\\documentclass{article}\n" + 
             "\\begin{document}\n" + 
             "\\begin{itemize}\n" + 
             "\\item |test\n" + 
             "\\end{itemize}\n" +
             "\\end{document}\n",
             "Add \\item above",
             "\\documentclass{article}\n" + 
             "\\begin{document}\n" + 
             "\\begin{itemize}\n" + 
             "\\item |\n" + 
             "\\item test\n" + 
             "\\end{itemize}\n" +
             "\\end{document}\n");
    }
    
    public void testSimple5() throws Exception {
        testFix("\\documentclass{article}\n" + 
             "\\begin{document}\n" + 
             "\\begin{itemize}\n" + 
             "\\item[test]{test|}\n" + 
             "\\end{itemize}\n" +
             "\\end{document}\n",
             "Add \\item below",
             "\\documentclass{article}\n" + 
             "\\begin{document}\n" + 
             "\\begin{itemize}\n" + 
             "\\item[test]{test}\n" + 
             "\\item[|]{}\n" + 
             "\\end{itemize}\n" +
             "\\end{document}\n");
    }
    
    public void testSimple6() throws Exception {
        testFix("\\documentclass{article}\n" + 
             "\\begin{document}\n" + 
             "\\begin{itemize}\n" + 
             "\\item[test]{|test}\n" + 
             "\\end{itemize}\n" +
             "\\end{document}\n",
             "Add \\item above",
             "\\documentclass{article}\n" + 
             "\\begin{document}\n" + 
             "\\begin{itemize}\n" + 
             "\\item[|]{}\n" + 
             "\\item[test]{test}\n" + 
             "\\end{itemize}\n" +
             "\\end{document}\n");
    }
    
    public void testSimple7() throws Exception {
        testFix("\\documentclass{article}\n" + 
             "\\begin{document}\n" + 
             "\\begin{itemize}\n" + 
             "\\item[test] test|\n" + 
             "\\end{itemize}\n" +
             "\\end{document}\n",
             "Add \\item below",
             "\\documentclass{article}\n" + 
             "\\begin{document}\n" + 
             "\\begin{itemize}\n" + 
             "\\item[test] test\n" + 
             "\\item[|] \n" + 
             "\\end{itemize}\n" +
             "\\end{document}\n");
    }
    
    public void testSimple8() throws Exception {
        testFix("\\documentclass{article}\n" + 
             "\\begin{document}\n" + 
             "\\begin{itemize}\n" + 
             "\\item[test] |test\n" + 
             "\\end{itemize}\n" +
             "\\end{document}\n",
             "Add \\item above",
             "\\documentclass{article}\n" + 
             "\\begin{document}\n" + 
             "\\begin{itemize}\n" + 
             "\\item[|] \n" + 
             "\\item[test] test\n" + 
             "\\end{itemize}\n" +
             "\\end{document}\n");
    }
    
    public void testAnalyze1() throws Exception {
        testAnalyze("\\documentclass{article}\n" + 
             "\\begin{document}\n" + 
             "\\begin{itemize}\n" + 
             "\\item{test| }    \t\n" + 
             "\\end{itemize}\n" +
             "\\end{document}\n",
             "Add \\item below");
    }
    
    public void testAnalyze2() throws Exception {
        testAnalyze("\\documentclass{article}\n" + 
             "\\begin{document}\n" + 
             "\\begin{itemize}\n" + 
             "\\item test | \t  \n" + 
             "\\end{itemize}\n" +
             "\\end{document}\n",
             "Add \\item below");
    }
    
    public void testAnalyze3() throws Exception {
        testAnalyze("\\documentclass{article}\n" + 
             "\\begin{document}\n" + 
             "\\begin{itemize}\n" + 
             "\\item   \t \n { |test}\n" + 
             "\\end{itemize}\n" +
             "\\end{document}\n",
             "Add \\item above");
    }
    
    public void testAnalyze4() throws Exception {
        testAnalyze("\\documentclass{article}\n" + 
             "\\begin{document}\n" + 
             "\\begin{itemize}\n" + 
             "\\item     \t \n |test\n" + 
             "\\end{itemize}\n" +
             "\\end{document}\n",
             "Add \\item above");
    }
    
    public void testAnalyze5() throws Exception {
        testAnalyze("\\documentclass{article}\n" + 
             "\\begin{document}\n" + 
             "\\begin{itemize}\n" + 
             "\\item{tes|t }    \t\n" + 
             "\\end{itemize}\n" +
             "\\end{document}\n");
    }
    
    public void testAnalyze6() throws Exception {
        testAnalyze("\\documentclass{article}\n" + 
             "\\begin{document}\n" + 
             "\\begin{itemize}\n" + 
             "\\item tes|t  \t  \n" + 
             "\\end{itemize}\n" +
             "\\end{document}\n");
    }
    
    public void testAnalyze7() throws Exception {
        testAnalyze("\\documentclass{article}\n" + 
             "\\begin{document}\n" + 
             "\\begin{itemize}\n" + 
             "\\item   \t \n { t|est}\n" + 
             "\\end{itemize}\n" +
             "\\end{document}\n");
    }
    
    public void testAnalyze8() throws Exception {
        testAnalyze("\\documentclass{article}\n" + 
             "\\begin{document}\n" + 
             "\\begin{itemize}\n" + 
             "\\item     \t \n t|est\n" + 
             "\\end{itemize}\n" +
             "\\end{document}\n");
    }
    
    public void testAnalyze9() throws Exception {
        testAnalyze("\\documentclass{article}\n" + 
             "\\begin{document}\n" + 
             "\\begin{itemize}\n" + 
             "\\item{{test|} }    \t\n" + 
             "\\end{itemize}\n" +
             "\\end{document}\n");
    }
    
    public void testAnalyzea() throws Exception {
        testAnalyze("\\documentclass{article}\n" + 
             "\\begin{document}\n" + 
             "\\begin{itemize}\n" + 
             "\\item   \t \n { {|test}}\n" + 
             "\\end{itemize}\n" +
             "\\end{document}\n");
    }
    
    public void XtestAnalyzeb() throws Exception {
        testAnalyze("\\documentclass{article}\n" + 
             "\\begin{document}\n" + 
             "\\begin{itemize}\n" + 
             "\\item  \t\n | \t \n" + 
             "\\end{itemize}\n" +
             "\\end{document}\n",
             "Add \\item below");
    }
    
    private ErrorDescription computeHint(CompilationInfo info, int offset) throws Exception {
        AddItemHint provider = new AddItemHint();
        
        LaTeXParserResult lpr = (LaTeXParserResult) info.getParserResult();

        Node n = lpr.getCommandUtilities().findNode(info.getDocument(), offset);

        if (n == null) {
            return null;
        }

        final List<ErrorDescription> hints = new LinkedList<ErrorDescription>();

        while (n != null) {
            if (provider.accept(info, n)) {
                List<ErrorDescription> computed = provider.computeHints(info, n, offset);
                
                if (computed != null) {
                    hints.addAll(computed);
                }
            }

            n = n.getParent();
        }

        assertTrue(hints.size() == 1 || hints.isEmpty());
        
        return hints.isEmpty() ? null : hints.get(0);
    }
    
    private void testAnalyze(String code, String... errors) throws Exception {
        testPerformer(code, errors, null, null);
    }
    
    private void testFix(String code, String errorToFix, String golden) throws Exception {
        testPerformer(code, null, errorToFix, golden);
    }
    
    private void testPerformer(String code, final String[] fixesGolden, final String errorToFix, String golden) throws Exception {
        final int[] sourceOffset = new int[1];
        final int[] targetOffset = new int[1];

        code = detectOffsets(code, sourceOffset);
        
        if (golden != null) {
            golden = detectOffsets(golden, targetOffset);
        }
        
        FileObject     testFileObject = getTestFile("text.tex");
        
        copyStringToFile(testFileObject, code);
        
        DataObject od = DataObject.find(testFileObject);
        StyledDocument doc = od.getLookup().lookup(EditorCookie.class).openDocument();
 
        doc.putProperty("mime-type", "text/x-tex");
        doc.putProperty(Language.class, TexLanguage.description());

        Source s = Source.forDocument(doc);
        
        s.runUserActionTask(new CancellableTask<CompilationController>() {
            public void cancel() {}
            public void run(CompilationController parameter) throws Exception {
                parameter.toPhase(Phase.RESOLVED);

                ErrorDescription error = computeHint(parameter, sourceOffset[0]);
                
                if (fixesGolden != null && fixesGolden.length == 0) {
                    assertNull(error);
                    return ;
                }
                
                assertNotNull(error);
                
                List<Fix> fixes = error.getFixes().getFixes();
                Fix toPerform = null;
                List<String> fixesNames = new LinkedList<String>();
                
                for (Fix f : fixes) {
                    if (errorToFix != null && errorToFix.equals(f.getText())) {
                        toPerform = f;
                   }
                    
                    fixesNames.add(f.getText());
                }
                
                if (fixesGolden != null) {
                    assertEquals(Arrays.asList(fixesGolden), fixesNames);
                }
                
                if (errorToFix != null) {
                    assertNotNull(fixesNames.toString(), toPerform);

                    ChangeInfo ci = toPerform.implement();

                    assertNotNull(ci);

                    Change c = ci.get(0);

                    assertEquals(targetOffset[0], c.getStart().getOffset());
                    assertTrue(c.getStart().getOffset() == c.getEnd().getOffset());
                }
            }
        }, true);
        
        if (errorToFix != null) {
            String result = doc.getText(0, doc.getLength());

            assertEquals(golden, result);
        }
    }
    
    public static String detectOffsets(String source, int[] positionOrSpan) {
        //for now, the position/span delimiter is '|', without possibility of escaping:
        String[] split = source.split("\\|");
        
        Assert.assertTrue("incorrect number of position markers (|)", positionOrSpan.length == split.length - 1);
        
        StringBuilder sb = new StringBuilder();
        int index = 0;
        int offset = 0;
        
        for (String s : split) {
            sb.append(s);
            if (index < positionOrSpan.length)
                positionOrSpan[index++] = (offset += s.length());
        }
        
        return sb.toString();
    }
    
    private FileObject getTestFile(String testFile) throws IOException, InterruptedException {
        clearWorkDir();
        
        FileObject workdir = FileUtil.toFileObject(getWorkDir());
        
        assertNotNull(workdir);
        
        FileObject test = workdir.createData("test.tex");
        
        assertNotNull(test);
        
        return test;
    }
    
    @Override
    protected boolean runInEQ() {
        return true;
    }
    
    public final static FileObject copyStringToFile(FileObject f, String content) throws Exception {
        OutputStream os = f.getOutputStream();
        InputStream is = new ByteArrayInputStream(content.getBytes("UTF-8"));
        FileUtil.copy(is, os);
        os.close();
        is.close();

        return f;
    }
    
}
