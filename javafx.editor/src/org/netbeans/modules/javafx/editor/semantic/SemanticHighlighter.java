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
package org.netbeans.modules.javafx.editor.semantic;

import com.sun.javafx.api.tree.FunctionDefinitionTree;
import com.sun.javafx.api.tree.JavaFXTreePathScanner;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.util.SourcePositions;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.AttributeSet;
import javax.swing.text.Document;
import javax.swing.text.StyleConstants;
import org.netbeans.api.editor.settings.AttributesUtilities;
import org.netbeans.api.javafx.lexer.JFXTokenId;
import org.netbeans.api.javafx.source.CancellableTask;
import org.netbeans.api.javafx.source.CompilationInfo;
import org.netbeans.api.javafx.source.TreeUtilities;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.spi.editor.highlighting.support.OffsetsBag;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.Exceptions;

/**
 *
 * @author Anton Chechel
 */
public class SemanticHighlighter implements CancellableTask<CompilationInfo> {
    
    private static final String ID_FUNCTION = "function";
    private static final String ID_FIELD = "field";

//    private static final AttributeSet FIELD_HIGHLIGHT = AttributesUtilities.createImmutable(StyleConstants.Foreground, Color.BLACK, StyleConstants.Bold, Boolean.TRUE);
    private static final AttributeSet FIELD_HIGHLIGHT = AttributesUtilities.createImmutable(StyleConstants.Background, new Color(240, 240, 240));
    private static final AttributeSet METHOD_HIGHLIGHT = AttributesUtilities.createImmutable(StyleConstants.Foreground, Color.BLACK, StyleConstants.Bold, Boolean.TRUE);
    
    private static final Logger LOGGER = Logger.getLogger(SemanticHighlighter.class.getName());
    private static final boolean LOGGABLE = LOGGER.isLoggable(Level.FINE);
    
    private FileObject file;
    private AtomicBoolean cancel = new AtomicBoolean();

    SemanticHighlighter(FileObject file) {
        this.file = file;
    }

    public void cancel() {
        cancel.set(true);
    }

    public void run(CompilationInfo info) {
        cancel.set(false);
        process(info);
    }

    private void process(CompilationInfo info) {
        try {
            DataObject od = DataObject.find(file);
            EditorCookie ec = od.getLookup().lookup(EditorCookie.class);
            if (ec == null) {
                return;
            }
            Document doc = ec.getDocument();
            if (doc == null) {
                return;
            }

            List<Result> list = new ArrayList<Result>();
            CompilationUnitTree compilationUnit = info.getCompilationUnit();
            JavaFXThreeVisitor javaFXThreeVisitor = new JavaFXThreeVisitor(info);
            javaFXThreeVisitor.scan(compilationUnit, list);
            setHighlights(doc, list);

        } catch (DataObjectNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    static void setHighlights(Document doc, List<Result> list) {
        OffsetsBag bag = new OffsetsBag(doc, true);
        for (Result result : list) {
            int start = (int) result.start;
            int end = (int) result.end;

            if (start >= 0 && end >= 0) {
                bag.addHighlight(start, end, getAttributeSet(result.identifier));
            } else {
                log("* Incorrect positions for highlighting: " + start + ", " + end);
            }
        }

        getBag(doc).setHighlights(bag);
    }

    private static AttributeSet getAttributeSet(String identifier) {
        if (ID_FUNCTION.equals(identifier)) {
            return METHOD_HIGHLIGHT;
        } else if (ID_FIELD.equals(identifier)) {
            return FIELD_HIGHLIGHT;
        }
        return FIELD_HIGHLIGHT;
    }

    static OffsetsBag getBag(Document doc) {
        OffsetsBag bag = (OffsetsBag) doc.getProperty(SemanticHighlighter.class);

        if (bag == null) {
            doc.putProperty(SemanticHighlighter.class, bag = new OffsetsBag(doc));
        }

        return bag;
    }

    private static void log(String s) {
        if (LOGGABLE) {
            LOGGER.fine(s);
        }
    }

    private static class JavaFXThreeVisitor extends JavaFXTreePathScanner<Void, List<Result>> {

        private CompilationInfo info;
        private TreeUtilities tu;

        public JavaFXThreeVisitor(CompilationInfo info) {
            this.info = info;
            tu = new TreeUtilities(info);
        }

        @Override
        public Void visitFunctionDefinition(FunctionDefinitionTree tree, List<Result> list) {
//            String name = ((JFXFunctionDefinition) tree).getName().toString();

            SourcePositions sourcePositions = info.getTrees().getSourcePositions();
            long start = sourcePositions.getStartPosition(info.getCompilationUnit(), getCurrentPath().getLeaf());
            long end = sourcePositions.getEndPosition(info.getCompilationUnit(), getCurrentPath().getLeaf());
            
            if (start < 0 || end < 0) { // synthetic
                return super.visitFunctionDefinition(tree, list);
            }
            
            TokenSequence<JFXTokenId> ts = tu.tokensFor(tree);
            while (ts.moveNext()) {
                Token t = ts.token();
                if (JFXTokenId.IDENTIFIER.equals(t.id())) { // first identifiers is a name
                    start = ts.offset();
                    end = start + t.length();
                    break;
                }
            }

            list.add(new Result(start, end,ID_FUNCTION));
            return super.visitFunctionDefinition(tree, list);
        }

    }

    private static class Result {

        long start;
        long end;
        String identifier; // temporary since element doesn't work


        public Result(long start, long end, String identifier) {
            this.start = start;
            this.end = end;
            this.identifier = identifier;
        }

        @Override
        public String toString() {
            return "[" + start + ", " + end + ", " + identifier + "]";
        }
    }
}
