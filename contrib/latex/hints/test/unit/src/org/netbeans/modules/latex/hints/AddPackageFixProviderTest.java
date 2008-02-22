package org.netbeans.modules.latex.hints;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import javax.swing.JEditorPane;
import javax.swing.text.StyledDocument;
import org.netbeans.modules.gsf.api.CancellableTask;
import org.netbeans.api.lexer.Language;
import org.netbeans.core.startup.Main;
import org.netbeans.junit.NbTestCase;
import org.netbeans.modules.latex.UnitUtilities;
import org.netbeans.modules.latex.bibtex.loaders.MyDataLoader;
import org.netbeans.modules.latex.editor.TexLanguage;
import org.netbeans.modules.latex.model.LaTeXParserResult;
import org.netbeans.modules.latex.model.ParseError;
import org.netbeans.modules.latex.model.Utilities;
import org.netbeans.napi.gsfret.source.CompilationController;
import org.netbeans.napi.gsfret.source.Phase;
import org.netbeans.napi.gsfret.source.Source;
import org.netbeans.spi.editor.hints.Fix;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;

/**
 *
 * @author Jan Lahoda
 */
public class AddPackageFixProviderTest extends NbTestCase {
    
    public AddPackageFixProviderTest(String testName) {
        super(testName);
    }            

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
        test("\\documentclass{article}\n" + 
             "\\begin{document}\n" + 
             "\\shortmid\n" + 
             "\\end{document}\n",
             2,
             true,
             "\\documentclass{article}\n" +
             "\\usepackage{amsfonts}\n" +
             "\\begin{document}\n" +
             "\\shortmid\n" +
             "\\end{document}\n");
    }

    public void testSimple2() throws Exception {
        test("\\documentclass{article}\n" + 
             "\\usepackage{graphicx}\n" + 
             "\\begin{document}\n" + 
             "\\shortmid\n" + 
             "\\end{document}\n",
             3,
             true,
             "\\documentclass{article}\n" +
             "\\usepackage{amsfonts}\n" +
             "\\usepackage{graphicx}\n" + 
             "\\begin{document}\n" +
             "\\shortmid\n" +
             "\\end{document}\n");
    }
    
    public void testSimple3() throws Exception {
        test("\\documentclass{article}\n" + 
             "\\usepackage{algorithmicx}\n" + 
             "\\begin{document}\n" + 
             "\\shortmid\n" + 
             "\\end{document}\n",
             3,
             true,
             "\\documentclass{article}\n" +
             "\\usepackage{algorithmicx}\n" + 
             "\\usepackage{amsfonts}\n" +
             "\\begin{document}\n" +
             "\\shortmid\n" +
             "\\end{document}\n");
    }
    
    public void testSimple4() throws Exception {
        test("\\documentclass{article}\n" + 
             "\\usepackage{algorithmicx}\n" + 
             "\\usepackage{graphicx}\n" + 
             "\\begin{document}\n" + 
             "\\shortmid\n" + 
             "\\end{document}\n",
             4,
             true,
             "\\documentclass{article}\n" +
             "\\usepackage{algorithmicx}\n" + 
             "\\usepackage{amsfonts}\n" +
             "\\usepackage{graphicx}\n" + 
             "\\begin{document}\n" +
             "\\shortmid\n" +
             "\\end{document}\n");
    }
    
    private void test(String code, final int line, final boolean command, String golden) throws Exception {
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

                LaTeXParserResult lpr = LaTeXParserResult.get(parameter);
                
                for (ParseError e : lpr.getErrors()) {
                    if (e.getStart().getLine() == line) {
                        List<Fix> fixes = new AddPackageFixProvider(command).resolveFixes(parameter, e);
                        
                        assertNotNull(fixes);
                        assertEquals(1, fixes.size());
                        
                        fixes.get(0).implement();
                    }
                }
            }
        }, true);
        
        String result = doc.getText(0, doc.getLength());
        
        assertEquals(golden, result);
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
