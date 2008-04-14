/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
 * 
 * Contributor(s):
 * 
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package org.netbeans.api.javafx.source;
import com.sun.source.tree.Tree;
import com.sun.source.util.TreePath;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import javax.swing.text.Document;
import org.netbeans.api.javafx.lexer.JFXTokenId;
import org.netbeans.api.javafx.source.JavaFXSource.Phase;
import org.netbeans.api.lexer.Language;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;

/**
 *
 * @author alex
 */
public class PathFinderTest {
    public PathFinderTest() {
    }
    
    @org.junit.Test
    public void pathForTest() throws Exception {
        File f = File.createTempFile("Test", ".fx");
        toFile(f,
                "/* Top comment */\n" +
                "\n" +
                "import javafx.ui.*;\n" +
                "/** @author nemo */\n" +
                "\n" +
                "Frame {\n" +
                "  title: \"Hello World F3\"\n" +
                "  content: Label{\n"+
                "      text:\"Hello World\"\n}"+
                "}"
        );
        FileObject fo = FileUtil.toFileObject(f);
        JavaFXSource src = JavaFXSource.forFileObject(fo);
        System.err.println("src=" + src);
        DataObject dobj = DataObject.find(fo);
        EditorCookie ec = dobj.getCookie(EditorCookie.class);
        Document doc = ec.openDocument();
        doc.putProperty(Language.class, JFXTokenId.language());

        src.runWhenScanFinished(new CancellableTask<CompilationController>() {
            public void cancel() {
                }
            public void run(CompilationController controller) throws Exception {
                if (controller.toPhase(Phase.ANALYZED).compareTo(Phase.ANALYZED) < 0) {//TODO: ELEMENTS_RESOLVED may be sufficient
                    throw new Exception(
                                "Unable to resolve "+controller.getCompilationUnit().getSourceFile()+" to phase "+Phase.ANALYZED+", current phase = "+controller.getPhase()+
                                "\nDiagnostics = "/*+ci.getDiagnostics()*/+
                                "\nFree memory = "+Runtime.getRuntime().freeMemory());
                }
                int currentOffset = 80;
                TreePath currentPath = controller.getTreeUtilities().pathFor(currentOffset);
                Tree tree = currentPath.getLeaf();
                System.out.println("Tree is: "+ tree.toString());
            }
        }, true);
        
    }

    private void toFile(File f, String s) throws  Exception {
        OutputStream os = new FileOutputStream(f);
        Writer w = new OutputStreamWriter(os);
        w.write(s);
        w.close();
        os.close();
    }
    
}
