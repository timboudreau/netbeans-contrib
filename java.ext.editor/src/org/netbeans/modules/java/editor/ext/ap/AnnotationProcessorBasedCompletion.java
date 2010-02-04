/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2009 Sun Microsystems, Inc. All rights reserved.
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
 * Software is Sun Microsystems, Inc. Portions Copyright 2009 Sun
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
package org.netbeans.modules.java.editor.ext.ap;

import com.sun.source.tree.AnnotationTree;
import com.sun.source.tree.AssignmentTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.Tree.Kind;
import com.sun.source.util.TreePath;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.annotation.processing.Completion;
import javax.annotation.processing.Messager;
import javax.annotation.processing.Processor;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.tools.Diagnostic;
import org.netbeans.api.java.lexer.JavaTokenId;
import org.netbeans.api.java.source.CompilationController;
import org.netbeans.api.java.source.CompilationInfo;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.java.source.JavaSource.Phase;
import org.netbeans.api.java.source.SourceUtils;
import org.netbeans.api.java.source.Task;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.modules.editor.java.JavaCompletionItem;
import org.netbeans.spi.editor.completion.CompletionItem;
import org.netbeans.spi.editor.completion.CompletionProvider;
import org.netbeans.spi.editor.completion.CompletionResultSet;
import org.netbeans.spi.editor.completion.CompletionTask;
import org.netbeans.spi.editor.completion.support.AsyncCompletionQuery;
import org.netbeans.spi.editor.completion.support.AsyncCompletionTask;
import org.netbeans.spi.editor.completion.support.CompletionUtilities;
import org.openide.util.Exceptions;

/**
 *
 * @author lahvac
 */
public class AnnotationProcessorBasedCompletion extends AsyncCompletionQuery {

    @Override
    protected void query(final CompletionResultSet resultSet, Document doc, final int caretOffset) {
        try {
            JavaSource js = JavaSource.forDocument(doc);
            
            js.runUserActionTask(new Task<CompilationController>() {
                public void run(CompilationController cc) throws Exception {
                    cc.toPhase(Phase.RESOLVED);

                    for (Entry<? extends Completion, ? extends CompletionItem> e : resolveCompletion(cc, caretOffset).entrySet()) {
                        resultSet.addItem(e.getValue());
                    }
                }

            }, true);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        } finally {
            resultSet.finish();
        }
    }

    private static final Set<JavaTokenId> IGNORED_TOKENS = EnumSet.of(JavaTokenId.WHITESPACE, JavaTokenId.BLOCK_COMMENT, JavaTokenId.JAVADOC_COMMENT, JavaTokenId.LINE_COMMENT);

    private static boolean moveIgnoreWS(TokenSequence<JavaTokenId> ts, boolean previous) {
        boolean result;

        while ((result = previous ? ts.movePrevious() : ts.moveNext()) && IGNORED_TOKENS.contains(ts.token().id()))
            ;

        return result;
    }

    static Map<? extends Completion, ? extends CompletionItem> resolveCompletion(CompilationInfo info, int pos) {
        String userText = "";
        TokenSequence<JavaTokenId> ts = info.getTokenHierarchy().tokenSequence(JavaTokenId.language());
        final int origPos = pos;
        int subsOffset = pos;

        ts.move(pos);

        if (!moveIgnoreWS(ts, true)) {
            return Collections.emptyMap();
        }

        boolean implicitValue = true;

        while (ts.token().id() == JavaTokenId.IDENTIFIER || ts.token().id() == JavaTokenId.DOT) {
            subsOffset = ts.offset();
            
            if (!moveIgnoreWS(ts, true)) {
                return Collections.emptyMap();
            }

            pos = ts.offset();
        }

        if (ts.token().id() == JavaTokenId.LPAREN) {
            //TODO: check context to the right?
            implicitValue = true;
        }

        while (moveIgnoreWS(ts, false) && ts.offset() < origPos) {
            switch (ts.token().id()) {
                case IDENTIFIER:
                case STRING_LITERAL:
                case DOT:
                    int tokenEnd = ts.offset() + ts.token().length();

                    userText += info.getText().substring(ts.offset(), Math.min(tokenEnd, origPos));
            }
        }

        if (!implicitValue && ts.token().id() != JavaTokenId.EQ && ts.token().id() != JavaTokenId.COMMA) {
            return Collections.emptyMap();
        }
        
        TreePath tp = info.getTreeUtilities().pathFor(pos);

        if (tp.getParentPath() == null) {
            return Collections.emptyMap();
        }

        TreePath pp = tp;

        while (pp.getLeaf().getKind() != Kind.ANNOTATION) {
            if (pp.getParentPath() == null) {
                return Collections.emptyMap();
            }

            pp = pp.getParentPath();
        }

        if (pp.getLeaf().getKind() != Kind.ANNOTATION) {
            return Collections.emptyMap();
        }

        AnnotationTree at = (AnnotationTree) pp.getLeaf();
        ExpressionTree param = null;

        for (ExpressionTree et : at.getArguments()) {
            if (info.getTrees().getSourcePositions().getStartPosition(info.getCompilationUnit(), et) < pos) {
                param = et;
            } else {
                break;
            }
        }

        Name attributeName;
        
        if (param != null) {
            if (param.getKind() != Kind.ASSIGNMENT) {
                return Collections.emptyMap();
            }

            Tree variable = ((AssignmentTree) param).getVariable();

            if (variable.getKind() != Kind.IDENTIFIER) {
                return Collections.emptyMap();
            }

            attributeName = ((IdentifierTree) variable).getName();
        } else {
            if (!implicitValue) {
                return Collections.emptyMap();
            }

            attributeName = info.getElements().getName("value");
        }

        TreePath declaration = findDeclaration(tp);

        if (declaration == null) {
            return Collections.emptyMap();
        }

        Element element = info.getTrees().getElement(declaration);
        Element annotationElement = info.getTrees().getElement(new TreePath(tp.getParentPath(), /*TODO: pp != tp.getPP()*/at.getAnnotationType()));

        if (annotationElement == null || annotationElement.getKind() != ElementKind.ANNOTATION_TYPE) {
            return Collections.emptyMap();
        }

        //TODO: finds the first matching AM:
        AnnotationMirror annotation =  null;

        for (AnnotationMirror am : element.getAnnotationMirrors()) {
            if (am.getAnnotationType().asElement().equals(annotationElement)) {
                annotation = am;
                break;
            }
        }

        if (annotation == null) {
            return Collections.emptyMap();
        }
        
        ExecutableElement member = null;

        for (ExecutableElement ee : ElementFilter.methodsIn(annotationElement.getEnclosedElements())) {
            if (attributeName.contentEquals(ee.getSimpleName())) {
                member = ee;
            }
        }

        if (member == null) {
            return Collections.emptyMap();
        }
        
        List<Completion> completions = new LinkedList<Completion>();
        
        for (Processor p : Utilities.matching(info, Utilities.resolveProcessors(info, true), ((TypeElement) annotationElement).getQualifiedName().toString())) {
            p.init(new ProcessingEnvironmentImpl(info, new MessagerImpl()));
            for (Completion c : p.getCompletions(element, annotation, member, userText)) {
                completions.add(c);
            }
        }

        Map<Completion, CompletionItem> result = new LinkedHashMap<Completion, CompletionItem>();

        TypeMirror ret = member.getReturnType();
        boolean arrayType = false;
        boolean classType = false;
        boolean stringType = false;

        if (ret != null) {
            if (ret.getKind() == TypeKind.ARRAY) {
                arrayType = true;
                ret = ((ArrayType) ret).getComponentType();
            }
            if (ret.getKind() == TypeKind.DECLARED) {
                TypeElement clazz = info.getElements().getTypeElement("java.lang.Class");
                TypeElement string = info.getElements().getTypeElement("java.lang.String");
                classType = clazz.equals(((DeclaredType) ret).asElement());
                stringType = string.equals(((DeclaredType) ret).asElement());
            }
        }

        for (final Completion c : completions) {
            boolean singleValue;

            if (arrayType) {
                singleValue = !c.getValue().startsWith("{");
            } else {
                singleValue = true;
            }
            
            if (classType && singleValue) {
                String possibleFQN = c.getValue();

                if (possibleFQN.endsWith(".class")) {
                    possibleFQN = possibleFQN.substring(0, possibleFQN.length() - ".class".length());
                }
                
                TypeElement type = info.getElements().getTypeElement(possibleFQN);

                if (type != null) {
                    if (possibleFQN.startsWith(userText) || type.getSimpleName().toString().startsWith(userText)) {
                        result.put(c, new TypeCompletionItem(JavaCompletionItem.createTypeItem(type, (DeclaredType) type.asType()/*TODO*/, subsOffset, true, false/*TODO*/, false, false, true), possibleFQN, subsOffset));
                    }
                    continue;
                }
            }

            if (stringType && singleValue) {
                Completion cc = new Completion() {
                    public String getValue() {
                        String val = c.getValue();
                        if (val.charAt(0) == '"') {
                            return val;
                        } else {
                            return "\"" + c.getValue() + "\"";
                        }
                    }

                    public String getMessage() {
                        return c.getMessage();
                    }
                };

                if (cc.getValue().startsWith(userText)) {
                    result.put(c, new PlainCompletionBasedCompletionItem(cc, subsOffset, userText.length()));
                }

                continue;
            }

            result.put(c, new PlainCompletionBasedCompletionItem(c, subsOffset, userText.length()));
        }

        return result;
    }

    private static TreePath findDeclaration(TreePath tp) {
        while (   tp != null
               && tp.getLeaf().getKind() != Kind.CLASS
               && tp.getLeaf().getKind() != Kind.METHOD
               && tp.getLeaf().getKind() != Kind.VARIABLE) {
            tp = tp.getParentPath();
        }

        return tp;
    }

    private static final class PlainCompletionBasedCompletionItem implements CompletionItem {

        private final Completion completion;
        private final int        substitutionOffset;
        private final int        removeLength;

        public PlainCompletionBasedCompletionItem(Completion completion, int substitutionOffset, int removeLength) {
            this.completion = completion;
            this.substitutionOffset = substitutionOffset;
            this.removeLength = removeLength;
        }

        public void defaultAction(JTextComponent component) {
            final Document doc = component.getDocument();
            
            try {
                //TODO: atomic
                doc.remove(substitutionOffset, removeLength);
                doc.insertString(substitutionOffset, completion.getValue(), null);
            } catch (BadLocationException ex) {
                Exceptions.printStackTrace(ex);
            }
        }

        public void processKeyEvent(KeyEvent evt) {}

        public int getPreferredWidth(Graphics g, Font defaultFont) {
            return CompletionUtilities.getPreferredWidth(completion.getValue(), "", g, defaultFont);
        }

        public void render(Graphics g, Font defaultFont, Color defaultColor, Color backgroundColor, int width, int height, boolean selected) {
            CompletionUtilities.renderHtml(null, completion.getValue(), "", g, defaultFont, defaultColor, width, height, selected);
        }

        public CompletionTask createDocumentationTask() {
            return null;
        }

        public CompletionTask createToolTipTask() {
            return null;
        }

        public boolean instantSubstitution(JTextComponent component) {
            return false;
        }

        public int getSortPriority() {
            return -200;
        }

        public CharSequence getSortText() {
            return completion.getValue();
        }

        public CharSequence getInsertPrefix() {
            return completion.getValue();
        }
        
    }

    private static final class TypeCompletionItem implements CompletionItem {

        private final CompletionItem delegate;
        private final String fqn;
        private final int substitutionOffset;

        public TypeCompletionItem(CompletionItem delegate, String fqn, int substitutionOffset) {
            this.delegate = delegate;
            this.fqn = fqn;
            this.substitutionOffset = substitutionOffset;
        }

        public void render(Graphics g, Font defaultFont, Color defaultColor, Color backgroundColor, int width, int height, boolean selected) {
            delegate.render(g, defaultFont, defaultColor, backgroundColor, width, height, selected);
        }

        public void processKeyEvent(KeyEvent evt) {}

        public boolean instantSubstitution(JTextComponent component) {
            return delegate.instantSubstitution(component);
        }

        public CharSequence getSortText() {
            return delegate.getSortText();
        }

        public int getSortPriority() {
            return delegate.getSortPriority();
        }

        public int getPreferredWidth(Graphics g, Font defaultFont) {
            return delegate.getPreferredWidth(g, defaultFont);
        }

        public CharSequence getInsertPrefix() {
            return delegate.getInsertPrefix();
        }

        public void defaultAction(final JTextComponent component) {
            try {
                component.getDocument().remove(substitutionOffset, component.getCaretPosition() - substitutionOffset);
                
                JavaSource.forDocument(component.getDocument()).runUserActionTask(new Task<CompilationController>() {
                    public void run(CompilationController parameter) throws Exception {
                        parameter.toPhase(Phase.RESOLVED);
                        TypeElement type = parameter.getElements().getTypeElement(fqn);

                        if (type == null) {
                            //XXX
                            return ;
                        }

                        TreePath tp = parameter.getTreeUtilities().pathFor(substitutionOffset);
                        String toInsert = SourceUtils.resolveImport(parameter, tp, fqn) + ".class";

                        component.getDocument().insertString(substitutionOffset, toInsert, null);
                    }
                }, true);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            } catch (BadLocationException ex) {
                Exceptions.printStackTrace(ex);
            }
        }

        public CompletionTask createToolTipTask() {
            return delegate.createToolTipTask();
        }

        public CompletionTask createDocumentationTask() {
            return delegate.createDocumentationTask();
        }


    }

    public static final class CompletionImpl implements CompletionProvider {

        public CompletionTask createTask(int queryType, JTextComponent component) {
            if (queryType == COMPLETION_QUERY_TYPE) {
                return new AsyncCompletionTask(new AnnotationProcessorBasedCompletion(), component);
            }

            return null;
        }

        public int getAutoQueryTypes(JTextComponent component, String typedText) {
            return 0;
        }

    }

    private static final class MessagerImpl implements Messager {

        public void printMessage(Diagnostic.Kind kind, CharSequence msg) {}

        public void printMessage(Diagnostic.Kind kind, CharSequence msg, Element e) {}

        public void printMessage(Diagnostic.Kind kind, CharSequence msg, Element e, AnnotationMirror a) {}

        public void printMessage(Diagnostic.Kind kind, CharSequence msg, Element e, AnnotationMirror a, AnnotationValue v) {}

    }
}
