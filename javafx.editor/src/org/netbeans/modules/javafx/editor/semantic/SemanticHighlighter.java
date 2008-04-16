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
import com.sun.javafx.api.tree.TypeAnyTree;
import com.sun.javafx.api.tree.TypeClassTree;
import com.sun.javafx.api.tree.TypeFunctionalTree;
import com.sun.javafx.api.tree.TypeUnknownTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.LineMap;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.SourcePositions;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.lang.model.element.Element;
import javax.swing.text.AttributeSet;
import javax.swing.text.Document;
import javax.swing.text.StyleConstants;
import org.netbeans.api.editor.settings.AttributesUtilities;
import org.netbeans.api.javafx.source.CancellableTask;
import org.netbeans.api.javafx.source.CompilationInfo;
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

    private static final AttributeSet FIELD_HIGHLIGHT = AttributesUtilities.createImmutable(StyleConstants.Background, Color.GREEN, StyleConstants.Bold, Boolean.TRUE);
    private static final AttributeSet METHOD_HIGHLIGHT = AttributesUtilities.createImmutable(StyleConstants.Foreground, Color.BLACK, StyleConstants.Bold, Boolean.TRUE);

    private FileObject file;
    private AtomicBoolean cancel = new AtomicBoolean();

    SemanticHighlighter(FileObject file) {
        this.file = file;
    }

    public void cancel() {
        cancel.set(true);
    }

    public void run(CompilationInfo info) {
        System.out.println("***  SemanticHighlighter.run()");
        cancel.set(false);
        process(info);
    }

    private void process(CompilationInfo info) {
        System.out.println("***  SemanticHighlighter.process()");
        try {
            DataObject od = DataObject.find(file);
            EditorCookie ec = od.getLookup().lookup(EditorCookie.class);
            if (ec == null) {
                return;
            }

            List<Result> list = new ArrayList<Result>();
            CompilationUnitTree compilationUnit = info.getCompilationUnit();
            JavaFXThreeVisitor javaFXThreeVisitor = new JavaFXThreeVisitor(info);
            javaFXThreeVisitor.scan(compilationUnit, list);
            System.out.println("***  scan finished: result = " + list);
            
            for (Result result : list) {
//                if (ElementKind.METHOD == result.element.getKind()) {
//                    setHighlights(ec.getDocument(), FIELD_HIGHLIGHT);
//                } else if (ElementKind.FIELD == result.element.getKind()) {
                    setHighlights(ec.getDocument(), result.start, result.end, FIELD_HIGHLIGHT);
//                }
            }
        } catch (DataObjectNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    static void setHighlights(Document doc, long startPosition, long endPosition, AttributeSet as) {
        System.out.println("***   setHighlights() start = " + startPosition + ", end = " + endPosition);
        
        if (doc == null) {
            return;
        }

        OffsetsBag bag = new OffsetsBag(doc, true);
        int start = (int) startPosition;
        int end = (int) endPosition;

        if (start >= 0 && end >= 0) {
            bag.addHighlight(start, end, as);
        }

        getBag(doc).setHighlights(bag);
    }

    static OffsetsBag getBag(Document doc) {
        OffsetsBag bag = (OffsetsBag) doc.getProperty(SemanticHighlighter.class);

        if (bag == null) {
            doc.putProperty(SemanticHighlighter.class, bag = new OffsetsBag(doc));
        }

        return bag;
    }

    private static class JavaFXThreeVisitor extends JavaFXTreePathScanner<Void, List<Result>> {

        private CompilationInfo info;

        public JavaFXThreeVisitor(CompilationInfo info) {
            this.info = info;
        }

        @Override
        public Void visitFunctionDefinition(FunctionDefinitionTree tree, List<Result> list) {
            addCorrespondingSourcePositions(list, "function");
            return super.visitFunctionDefinition(tree, list);
        }

        @Override
        public Void visitTypeAny(TypeAnyTree tree, List<Result> list) {
            addCorrespondingSourcePositions(list, "typeAny");
            return super.visitTypeAny(tree, list);
        }

        @Override
        public Void visitTypeClass(TypeClassTree tree, List<Result> list) {
            addCorrespondingSourcePositions(list, "typeClass");
            return super.visitTypeClass(tree, list);
        }

        @Override
        public Void visitTypeFunctional(TypeFunctionalTree tree, List<Result> list) {
            addCorrespondingSourcePositions(list, "typeFunctional");
            return super.visitTypeFunctional(tree, list);
        }

        @Override
        public Void visitTypeUnknown(TypeUnknownTree tree, List<Result> list) {
            addCorrespondingSourcePositions(list, "typeUnknown");
            return super.visitTypeUnknown(tree, list);
        }

        @Override
        public Void visitIdentifier(IdentifierTree tree, List<Result> list) {
            addCorrespondingSourcePositions(list, "typeIdentifier");
            return super.visitIdentifier(tree, list);
        }

        @Override
        public Void visitOther(Tree tree, List<Result> list) {
            addCorrespondingSourcePositions(list, "typeOther");
            return super.visitOther(tree, list);
        }

        @Override
        public Void visitVariable(VariableTree tree, List<Result> list) {
            addCorrespondingSourcePositions(list, "typeVariable");
            return super.visitVariable(tree, list);
        }

        @Override
        public Void visitMethod(MethodTree tree, List<Result> list) {
            addCorrespondingSourcePositions(list, "typeMethod");
            return super.visitMethod(tree, list);
        }

        private void addCorrespondingSourcePositions(List<Result> list, String identifier) {
            Element element = info.getTrees().getElement(getCurrentPath());
            SourcePositions sourcePositions = info.getTrees().getSourcePositions();
            long start = sourcePositions.getStartPosition(info.getCompilationUnit(), getCurrentPath().getLeaf());
            long end = sourcePositions.getEndPosition(info.getCompilationUnit(), getCurrentPath().getLeaf());
            
            LineMap lm = info.getCompilationUnit().getLineMap();
            String lineNum = lm != null ? "" + lm.getLineNumber(start) : "?";
            String colNum = lm != null ? "" + lm.getColumnNumber(start) : "?";
            
            System.out.println("*** vitising: " + identifier + " [" + lineNum + ", " + colNum + "]");
            list.add(new Result(start, end, element));
        }
    }

    private static class Result {

        long start;
        long end;
        Element element;

        public Result(long start, long end, Element element) {
            this.start = start;
            this.end = end;
            this.element = element;
        }

        @Override
        public String toString() {
            return "[" + start + ", " + end + ", " + element + "]";
        }
        
    }
}
