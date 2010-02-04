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
package org.netbeans.modules.java.editor.ext.fold;

import com.sun.source.tree.BlockTree;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.NewClassTree;
import com.sun.source.tree.Scope;
import com.sun.source.tree.StatementTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.Tree.Kind;
import com.sun.source.tree.TypeParameterTree;
import com.sun.source.util.TreePath;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.StyledDocument;
import org.netbeans.api.java.lexer.JavaTokenId;
import org.netbeans.api.java.source.CompilationController;
import org.netbeans.api.java.source.CompilationInfo;
import org.netbeans.api.java.source.ElementHandle;
import org.netbeans.api.java.source.ElementUtilities.ElementAcceptor;
import org.netbeans.api.java.source.GeneratorUtilities;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.java.source.JavaSource.Phase;
import org.netbeans.api.java.source.ModificationResult;
import org.netbeans.api.java.source.Task;
import org.netbeans.api.java.source.TreeMaker;
import org.netbeans.api.java.source.TreePathHandle;
import org.netbeans.api.java.source.TypeMirrorHandle;
import org.netbeans.api.java.source.WorkingCopy;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.lib.editor.util.swing.DocumentUtilities;
import org.netbeans.spi.editor.completion.CompletionItem;
import org.netbeans.spi.editor.completion.CompletionProvider;
import org.netbeans.spi.editor.completion.CompletionResultSet;
import org.netbeans.spi.editor.completion.CompletionTask;
import org.netbeans.spi.editor.completion.support.AsyncCompletionQuery;
import org.netbeans.spi.editor.completion.support.AsyncCompletionTask;
import org.netbeans.spi.editor.completion.support.CompletionUtilities;
import org.openide.text.NbDocument;
import org.openide.util.Exceptions;
import org.openide.util.NbCollections;

/**
 *
 * @author lahvac
 */
public class ClosureCodeCompletion extends AsyncCompletionQuery {

    @Override
    protected void query(final CompletionResultSet resultSet, Document doc, final int caretOffset) {
        JavaSource js = JavaSource.forDocument(doc);

        try {
            js.runUserActionTask(new Task<CompilationController>() {
                public void run(CompilationController cc) throws Exception {
                    cc.toPhase(Phase.RESOLVED);
                    
                    for (GenerateClosureCompletionItem i : computeItems(cc, caretOffset)) {
                        resultSet.addItem(i);
                    }
                }

            }, true);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        } finally {
            resultSet.finish();
        }
    }

    static Map<? extends DeclaredType, ? extends ExecutableElement> evalType(CompilationInfo info, int caretOffset, TreePath[] mitOut, int[] paramOut) {
        TreePath current = info.getTreeUtilities().pathFor(caretOffset);

        if (current.getLeaf().getKind() != Kind.METHOD_INVOCATION) {
            return null;
        }

        TokenSequence<JavaTokenId> ts = info.getTokenHierarchy().tokenSequence(JavaTokenId.language());

        ts.move(caretOffset);

        if (!ts.movePrevious()) {
            return null;
        }

        while (ts.token().id() == JavaTokenId.WHITESPACE/*TODO*/) {
            if (!ts.movePrevious()) {
                return null;
            }
        }
        
        if (ts.token().id() != JavaTokenId.LPAREN && ts.token().id() != JavaTokenId.COMMA) {
            return null;
        }

        MethodInvocationTree mit = (MethodInvocationTree) current.getLeaf();
        int param = 0;

        for (Tree p : mit.getArguments()) {
            if (info.getTrees().getSourcePositions().getEndPosition(info.getCompilationUnit(), p) < caretOffset) {
                param++;
            } else {
                break;
            }
        }

        Name methodName;
        TypeMirror site;
        Iterable<? extends Element> allEnclosedElements;
        Scope currentScope;

        switch (mit.getMethodSelect().getKind()) {
            case IDENTIFIER:
                methodName = ((IdentifierTree) mit.getMethodSelect()).getName();
                site = null;
                allEnclosedElements = info.getElementUtilities().getLocalMembersAndVars(currentScope = info.getTrees().getScope(current), new ElementAcceptor() {

            public boolean accept(Element e, TypeMirror type) {
                return true;
            }
        });
                break;
            case MEMBER_SELECT:
                MemberSelectTree mst = (MemberSelectTree) mit.getMethodSelect();
                methodName = mst.getIdentifier();
                TreePath siteTP = new TreePath(new TreePath(current, mit.getMethodSelect()), mst.getExpression());
                site = info.getTrees().getTypeMirror(siteTP);
                TypeElement siteEl = (TypeElement) info.getTypes().asElement(site);
                allEnclosedElements = siteEl != null ? info.getElements().getAllMembers(siteEl) : null;
                currentScope = null;
                break;
            default:
                throw new UnsupportedOperationException();
        }

        if (site != null && site.getKind() != TypeKind.DECLARED) {
            return null;
        }

        Map<DeclaredType, ExecutableElement> result = new HashMap<DeclaredType, ExecutableElement>();//TODO: duplicates

        OUTER: for (ExecutableElement method : ElementFilter.methodsIn(allEnclosedElements)) {
            if (!methodName.equals(method.getSimpleName()) || param >= method.getParameters().size()) {
                continue;
            }

            DeclaredType currentSite = inferSite(info, site, method, currentScope);

            if (currentSite == null) continue; //???
            
            TypeMirror type = ((ExecutableType) info.getTypes().asMemberOf(currentSite, method)).getParameterTypes().get(param);

            if (type.getKind() != TypeKind.DECLARED) {
                continue;
            }

            TypeElement  clazz = (TypeElement) info.getTypes().asElement(type);
            ExecutableElement abstractMethod = null;

            for (ExecutableElement innerMethod : ElementFilter.methodsIn(clazz.getEnclosedElements())) {
                if (innerMethod.getModifiers().contains(Modifier.ABSTRACT)) {
                    if (abstractMethod != null) {
                        continue OUTER;
                    }
                    abstractMethod = innerMethod;
                }
            }

            if (abstractMethod == null) {
                continue;
            }

            result.put((DeclaredType) type, abstractMethod);
        }

        mitOut[0] = current;
        paramOut[0] = param;
        
        return result;
    }

    static Iterable<? extends GenerateClosureCompletionItem> computeItems(CompilationInfo info, int caretOffset) {
        TreePath[] mit = new TreePath[1];
        int[] param = new int[1];
        Map<? extends DeclaredType, ? extends ExecutableElement> types = evalType(info, caretOffset, mit, param);

        if (types == null) {
            return Collections.emptyList();
        }

        Collection<GenerateClosureCompletionItem> result = new LinkedList<GenerateClosureCompletionItem>();

        for (Entry<? extends DeclaredType, ? extends ExecutableElement> e : types.entrySet()) {
            result.add(new GenerateClosureCompletionItem(info, e.getKey(), e.getValue(), mit[0], param[0]));
        }

        return result;
    }

    static int rewrite(final Document doc, final int pos/*hack*/, final TypeMirrorHandle<DeclaredType> typeHandle, final ElementHandle<ExecutableElement> methodHandle, final TreePathHandle mitHandle, final int param) throws IOException, BadLocationException {
        final IOException[] io = new IOException[1];
        final BadLocationException[] bad = new BadLocationException[1];
        final int[] result = new int[1];
        NbDocument.runAtomicAsUser((StyledDocument) doc, new Runnable() {

            public void run() {
                try {
                    result[0] = rewriteImpl(doc, pos, typeHandle, methodHandle, mitHandle, param);
                } catch (IOException ex) {
                    io[0] = ex;
                } catch (BadLocationException ex) {
                    bad[0] = ex;
                }
            }
        });

        if (io[0] != null) throw io[0];
        if (bad[0] != null) throw bad[0];

        return result[0];
    }
    
    private static int rewriteImpl(Document doc, int pos/*hack*/, final TypeMirrorHandle<DeclaredType> typeHandle, final ElementHandle<ExecutableElement> methodHandle, final TreePathHandle mitHandle, final int param) throws IOException, BadLocationException {
        ModificationResult mr = JavaSource.forDocument(doc).runModificationTask(new Task<WorkingCopy>() {

            public void run(WorkingCopy wc) throws Exception {
                wc.toPhase(Phase.RESOLVED);
                DeclaredType type = typeHandle.resolve(wc);
                ExecutableElement method = methodHandle.resolve(wc);
                TreePath mit = mitHandle.resolve(wc);

                if (type == null || method == null || mit == null) {
                    //TODO: log
                    return ;
                }
                
                TreeMaker make = wc.getTreeMaker();
                GeneratorUtilities gu = GeneratorUtilities.get(wc);
                MethodTree mt = gu.createMethod(type, method);
                StatementTree empty = make.EmptyStatement();
                
                wc.tag(empty, "caret");

                BlockTree body = make.Block(Collections.singletonList(empty), false);

                mt = make.Method(mt.getModifiers(), mt.getName(), mt.getReturnType(), mt.getTypeParameters(), mt.getParameters(), mt.getThrows(), body, null);
                
                ClassTree clazz = make.Class(null, "", Collections.<TypeParameterTree>emptyList(), null, Collections.<Tree>emptyList(), Collections.singletonList(mt));
                NewClassTree nct = make.NewClass(null, Collections.<ExpressionTree>emptyList(), (ExpressionTree) make.Type(type), Collections.<ExpressionTree>emptyList(), clazz);
                MethodInvocationTree mitree = (MethodInvocationTree) mit.getLeaf();
                List<ExpressionTree> params = new ArrayList<ExpressionTree>(mitree.getArguments());
                if (params.size() <= param) {
                    params.add(nct);
                } else {
                    params.set(param, nct);
                }
                List<ExpressionTree> nueTypeParams = NbCollections.checkedListByCopy(mitree.getTypeArguments(), ExpressionTree.class, true);
                MethodInvocationTree nue = make.MethodInvocation(nueTypeParams,mitree.getMethodSelect(), params);
                wc.rewrite(mitree, nue);//overwrite!!!!!!!!
            }
        });

        mr.commit();

        //XXX:
        CharSequence text = DocumentUtilities.getText(doc);
        int outPos = pos;

        while (outPos < text.length()) {
            if (text.charAt(outPos) == ';') {
                doc.remove(outPos, 1);
                return outPos;
            }
            outPos++;
        }
        
        return pos;
    }

    private static DeclaredType inferSite(CompilationInfo info, TypeMirror inSite, ExecutableElement method, Scope currentScope) {
        if (inSite != null) {
            return (DeclaredType) inSite;
        }

        if (method.getModifiers().contains(Modifier.STATIC)) {
            TypeMirror enclosing = method.getEnclosingElement().asType();

            if (enclosing.getKind() == TypeKind.DECLARED) {
                return (DeclaredType) enclosing;
            }

            return null;
        }

        while (currentScope != null) {
            TypeElement enclClass = currentScope.getEnclosingClass();

            if (enclClass == null) continue;
            
            if (info.getElementUtilities().isMemberOf(method, enclClass)) {
                TypeMirror enclosing = enclClass.asType();

                if (enclosing.getKind() == TypeKind.DECLARED) {
                    return (DeclaredType) enclosing;
                }

                return null;
            }

            currentScope = currentScope.getEnclosingScope(); //not very efficient!
        }
        return null;
    }
    
    private static final class GenerateClosureCompletionItem implements CompletionItem {
        private final String displayName;
        private final TypeMirrorHandle<DeclaredType> tmh;
        private final ElementHandle<ExecutableElement> method;
        private final TreePathHandle mit;
        private final int param;

        public GenerateClosureCompletionItem(CompilationInfo info, DeclaredType type, ExecutableElement method, TreePath mit, int param) {
            ExecutableType et = (ExecutableType) info.getTypes().asMemberOf(type, method);
            StringBuilder params = new StringBuilder();
            boolean first = true;

            for (TypeMirror tm : et.getParameterTypes()) {
                if (!first) params.append(", ");
                params.append(tm.toString()); //XXX
            }

            this.displayName = "{ " + params + " => }";
            this.tmh = TypeMirrorHandle.create(type);
            this.method = ElementHandle.create(method);
            this.mit = TreePathHandle.create(mit, info);
            this.param = param;
        }

        public void defaultAction(JTextComponent component) {
            try {
                int pos = rewrite(component.getDocument(), component.getCaretPosition(), tmh, method, mit, param);

                component.setCaretPosition(pos);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            } catch (BadLocationException ex) {
                Exceptions.printStackTrace(ex);
            }
        }

        public void processKeyEvent(KeyEvent evt) {
        }

        public int getPreferredWidth(Graphics g, Font defaultFont) {
            return CompletionUtilities.getPreferredWidth(displayName, "", g, defaultFont);
        }

        public void render(Graphics g, Font defaultFont, Color defaultColor, Color backgroundColor, int width, int height, boolean selected) {
            CompletionUtilities.renderHtml(null, displayName, "", g, defaultFont, defaultColor, width, height, selected);
        }

        public CompletionTask createDocumentationTask() {
            return null;
        }

        public CompletionTask createToolTipTask() {
            return null;
        }

        public boolean instantSubstitution(JTextComponent component) {
            return true;
        }

        public int getSortPriority() {
            return -1000;
        }

        public CharSequence getSortText() {
            return displayName;
        }

        public CharSequence getInsertPrefix() {
            return null;
        }
    }
    
    public static final class CompletionImpl implements CompletionProvider {

        public CompletionTask createTask(int queryType, JTextComponent component) {
            if (queryType == COMPLETION_QUERY_TYPE) {
                return new AsyncCompletionTask(new ClosureCodeCompletion(), component);
            }

            return null;
        }

        public int getAutoQueryTypes(JTextComponent component, String typedText) {
            return 0;
        }
        
    }
}
