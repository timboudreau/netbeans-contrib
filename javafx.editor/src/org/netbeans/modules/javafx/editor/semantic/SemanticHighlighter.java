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

import com.sun.javafx.api.tree.ClassDeclarationTree;
import com.sun.javafx.api.tree.FunctionDefinitionTree;
import com.sun.javafx.api.tree.FunctionValueTree;
import com.sun.javafx.api.tree.InitDefinitionTree;
import com.sun.javafx.api.tree.InstantiateTree;
import com.sun.javafx.api.tree.JavaFXTreePathScanner;
import com.sun.javafx.api.tree.StringExpressionTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.MethodTree;
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

    private static final AttributeSet FIELD_HIGHLIGHT = AttributesUtilities.createImmutable(StyleConstants.Foreground, new Color(255, 0, 0));
    private static final AttributeSet METHOD_HIGHLIGHT = AttributesUtilities.createImmutable(StyleConstants.Background, new Color(255, 0, 0));
    private FileObject file;
    private AtomicBoolean cancel = new AtomicBoolean();

    SemanticHighlighter(FileObject file) {
        this.file = file;
    }

    public void cancel() {
        cancel.set(true);
    }

    public void run(CompilationInfo info) throws Exception {
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

//        @Override
//        public Void visitBindExpression(BindExpressionTree tree, List<Result> list) {
//            return super.visitBindExpression(tree, list);
//        }
//
//        @Override
//        public Void visitBlockExpression(BlockExpressionTree tree, List<Result> list) {
//            return super.visitBlockExpression(tree, list);
//        }

        @Override
        public Void visitClassDeclaration(ClassDeclarationTree tree, List<Result> list) {
            addCorrespondingSourcePositions(list);
            return super.visitClassDeclaration(tree, list);
        }

//        @Override
//        public Void visitForExpression(ForExpressionTree tree, List<Result> list) {
//            return super.visitForExpression(tree, list);
//        }
//
//        @Override
//        public Void visitForExpressionInClause(ForExpressionInClauseTree tree, List<Result> list) {
//            return super.visitForExpressionInClause(tree, list);
//        }

        @Override
        public Void visitFunctionDefinition(FunctionDefinitionTree tree, List<Result> list) {
            addCorrespondingSourcePositions(list);
            return super.visitFunctionDefinition(tree, list);
        }

        @Override
        public Void visitFunctionValue(FunctionValueTree tree, List<Result> list) {
            addCorrespondingSourcePositions(list);
            return super.visitFunctionValue(tree, list);
        }

//        @Override
//        public Void visitIndexof(IndexofTree tree, List<Result> list) {
//            return super.visitIndexof(tree, list);
//        }

        @Override
        public Void visitInitDefinition(InitDefinitionTree tree, List<Result> list) {
            addCorrespondingSourcePositions(list);
            return super.visitInitDefinition(tree, list);
        }

        @Override
        public Void visitInstantiate(InstantiateTree tree, List<Result> list) {
            addCorrespondingSourcePositions(list);
            return super.visitInstantiate(tree, list);
        }

//        @Override
//        public Void visitInterpolate(InterpolateTree tree, List<Result> list) {
//            return super.visitInterpolate(tree, list);
//        }
//
//        @Override
//        public Void visitInterpolateValue(InterpolateValueTree tree, List<Result> list) {
//            return super.visitInterpolateValue(tree, list);
//        }
//
//        @Override
//        public Void visitKeyFrameLiteral(KeyFrameLiteralTree tree, List<Result> list) {
//            return super.visitKeyFrameLiteral(tree, list);
//        }
//
//        @Override
//        public Void visitObjectLiteralPart(ObjectLiteralPartTree tree, List<Result> list) {
//            return super.visitObjectLiteralPart(tree, list);
//        }
//
//        @Override
//        public Void visitOnReplace(OnReplaceTree tree, List<Result> list) {
//            return super.visitOnReplace(tree, list);
//        }
//
//        @Override
//        public Void visitPostInitDefinition(InitDefinitionTree tree, List<Result> list) {
//            return super.visitPostInitDefinition(tree, list);
//        }
//
//        @Override
//        public Void visitSequenceDelete(SequenceDeleteTree tree, List<Result> list) {
//            return super.visitSequenceDelete(tree, list);
//        }
//
//        @Override
//        public Void visitSequenceEmpty(SequenceEmptyTree tree, List<Result> list) {
//            return super.visitSequenceEmpty(tree, list);
//        }
//
//        @Override
//        public Void visitSequenceExplicit(SequenceExplicitTree tree, List<Result> list) {
//            return super.visitSequenceExplicit(tree, list);
//        }
//
//        @Override
//        public Void visitSequenceIndexed(SequenceIndexedTree tree, List<Result> list) {
//            return super.visitSequenceIndexed(tree, list);
//        }
//
//        @Override
//        public Void visitSequenceInsert(SequenceInsertTree tree, List<Result> list) {
//            return super.visitSequenceInsert(tree, list);
//        }
//
//        @Override
//        public Void visitSequenceRange(SequenceRangeTree tree, List<Result> list) {
//            return super.visitSequenceRange(tree, list);
//        }
//
//        @Override
//        public Void visitSequenceSlice(SequenceSliceTree tree, List<Result> list) {
//            return super.visitSequenceSlice(tree, list);
//        }
//
//        @Override
//        public Void visitSetAttributeToObject(SetAttributeToObjectTree tree, List<Result> list) {
//            return super.visitSetAttributeToObject(tree, list);
//        }

        @Override
        public Void visitStringExpression(StringExpressionTree tree, List<Result> list) {
            addCorrespondingSourcePositions(list);
            return super.visitStringExpression(tree, list);
        }

//        @Override
//        public Void visitTimeLiteral(TimeLiteralTree tree, List<Result> list) {
//            return super.visitTimeLiteral(tree, list);
//        }
//
//        @Override
//        public Void visitTrigger(TriggerTree tree, List<Result> list) {
//            return super.visitTrigger(tree, list);
//        }
//
//        @Override
//        public Void visitTypeAny(TypeAnyTree tree, List<Result> list) {
//            return super.visitTypeAny(tree, list);
//        }
//
//        @Override
//        public Void visitTypeClass(TypeClassTree tree, List<Result> list) {
//            return super.visitTypeClass(tree, list);
//        }
//
//        @Override
//        public Void visitTypeFunctional(TypeFunctionalTree tree, List<Result> list) {
//            return super.visitTypeFunctional(tree, list);
//        }
//
//        @Override
//        public Void visitTypeUnknown(TypeUnknownTree tree, List<Result> list) {
//            return super.visitTypeUnknown(tree, list);
//        }

        @Override
        public Void visitMethod(MethodTree tree, List<Result> list) {
            addCorrespondingSourcePositions(list);
            return super.visitMethod(tree, list);
        }

        @Override
        public Void visitMethodInvocation(MethodInvocationTree tree, List<Result> list) {
            addCorrespondingSourcePositions(list);
            return super.visitMethodInvocation(tree, list);
        }
        

        private void addCorrespondingSourcePositions(List<Result> list) {
            Element element = info.getTrees().getElement(getCurrentPath());
            SourcePositions sourcePositions = info.getTrees().getSourcePositions();
            long start = sourcePositions.getStartPosition(info.getCompilationUnit(), getCurrentPath().getLeaf());
            long end = sourcePositions.getEndPosition(info.getCompilationUnit(), getCurrentPath().getLeaf());
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
