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
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.NewClassTree;
import com.sun.source.tree.ParameterizedTypeTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.Tree.Kind;
import com.sun.source.util.SourcePositions;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import org.netbeans.api.annotations.common.NonNull;
import org.netbeans.api.editor.fold.FoldType;
import org.netbeans.api.java.lexer.JavaTokenId;
import org.netbeans.api.java.source.CancellableTask;
import org.netbeans.api.java.source.CompilationInfo;
import org.netbeans.api.java.source.JavaSource.Phase;
import org.netbeans.api.java.source.JavaSource.Priority;
import org.netbeans.api.java.source.JavaSourceTaskFactory;
import org.netbeans.api.java.source.support.CancellableTreePathScanner;
import org.netbeans.api.java.source.support.EditorAwareJavaSourceTaskFactory;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.modules.editor.fold.spi.support.FoldInfo;
import org.netbeans.modules.editor.fold.spi.support.FoldInfoHolder;
import org.netbeans.modules.i18n.java.JavaResourceHolder;
import org.netbeans.modules.java.editor.ext.fold.Messages.Call;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.Exceptions;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author lahvac
 */
public class ShorteningFold implements CancellableTask<CompilationInfo> {

    private final AtomicBoolean cancel = new AtomicBoolean();
    
    public void run(CompilationInfo info) throws Exception {
        cancel.set(false);

        Document doc = info.getSnapshot().getSource().getDocument(false);

        if (doc == null) {
            return ;
        }

        Collection<FoldInfo> folds = compute(info, doc, cancel);
        
        FoldInfoHolder.getHolder(doc).setFolds(ShorteningFold.class.getName(), folds);
    }

    public void cancel() {
        cancel.set(true);
    }

    static Collection<FoldInfo> compute(CompilationInfo info, Document doc, AtomicBoolean cancel) {
        Visitor v = new Visitor(info, doc, cancel);

        v.scan(info.getCompilationUnit(), null);

        return v.folds;
    }

    private static final class Visitor extends CancellableTreePathScanner<Void, Void> {

        private final @NonNull CompilationInfo info;
        private final @NonNull Document doc;
        private final List<FoldInfo> folds = new LinkedList<FoldInfo>();
        private final Set<NewClassTree> shortened = new HashSet<NewClassTree>();

        public Visitor(@NonNull CompilationInfo info, @NonNull Document doc, AtomicBoolean cancel) {
            super(cancel);
            this.info = info;
            this.doc = doc;
        }

        @Override
        public Void visitNewClass(NewClassTree node, Void p) {
            try {
                if (!shortened.contains(node) && node.getIdentifier().getKind() == Kind.PARAMETERIZED_TYPE) {
                    diamondShortening(((ParameterizedTypeTree) node.getIdentifier()).getTypeArguments());
                }
            } catch (BadLocationException ex) {
                Exceptions.printStackTrace(ex);
            }
            return super.visitNewClass(node, p);
        }

        @Override
        public Void visitMethodInvocation(MethodInvocationTree node, Void p) {
            try {
                for (ExpressionTree param : node.getArguments()) {
                    if (param.getKind() != Kind.NEW_CLASS)
                        continue;

                        NewClassTree nct = (NewClassTree) param;

                        if (anonymousParamShortening(nct)) {
                            shortened.add(nct);
                        }
                }

                diamondShortening(node.getTypeArguments());

                if (FoldTypes.I18N.enabled()) {
                    Call m = Messages.resolvePossibleMessageCall(info, getCurrentPath());

                    if (m != null) {
                        JavaResourceHolder h = new JavaResourceHolder();
                        h.setResource(DataObject.find(m.bundle));
                        String val = h.getValueForKey(m.key);
                        int start = (int) info.getTrees().getSourcePositions().getStartPosition(info.getCompilationUnit(), node);
                        int end = (int) info.getTrees().getSourcePositions().getEndPosition(info.getCompilationUnit(), node);

                        folds.add(FoldInfo.create(doc, start, end, SHORTENING, val, FoldTypes.I18N.collapsed()));
                    }
                }
            } catch (DataObjectNotFoundException ex) {
                Exceptions.printStackTrace(ex);
            } catch (BadLocationException ex) {
                Exceptions.printStackTrace(ex);
            }
            
            return super.visitMethodInvocation(node, p);
        }

        private boolean anonymousParamShortening(NewClassTree nct) throws BadLocationException {
            if (!FoldTypes.CLOSURE.enabled())
                return false;
            
            if (nct.getClassBody() == null)
                return false;

            ClassTree ct = nct.getClassBody();

            MethodTree decl = null;

            for (Tree member : ct.getMembers()) {
                if (member.getKind() != Kind.METHOD) {
                    return false;
                }

                if (((MethodTree) member).getReturnType() == null) //constructor
                    continue;

                if (decl != null) {
                    return false;
                }

                decl = (MethodTree) member;
            }

            if (decl == null) {
                return false;
            }

            boolean collapsed = FoldTypes.CLOSURE.collapsed();
            SourcePositions sp = info.getTrees().getSourcePositions();
            int nctStart = (int) sp.getStartPosition(info.getCompilationUnit(), nct);
            int nctEnd   = (int) sp.getEndPosition(info.getCompilationUnit(), nct);
            int leadsToStart;
            String leadsTo;

            if (decl.getParameters().isEmpty()) {
                leadsToStart = nctStart;
                leadsTo = "{ =>";
            } else {
                int firstParameterStart = (int) sp.getStartPosition(info.getCompilationUnit(), decl.getParameters().get(0));
                folds.add(FoldInfo.create(doc, nctStart, firstParameterStart, SHORTENING, "{", collapsed));
                int lastParameterEnd = (int) sp.getEndPosition(info.getCompilationUnit(), decl.getParameters().get(decl.getParameters().size() - 1));

                leadsToStart = lastParameterEnd;
                leadsTo = "=>";
            }

            BlockTree body = decl.getBody();

            if (singleLineClosure(info, body)) {
                int statementStart = (int) sp.getStartPosition(info.getCompilationUnit(), body.getStatements().get(0));
                int statementEnd   = (int) sp.getEndPosition(info.getCompilationUnit(), body.getStatements().get(0));

                folds.add(FoldInfo.create(doc, leadsToStart, statementStart, SHORTENING, leadsTo, collapsed));
                folds.add(FoldInfo.create(doc, statementEnd, nctEnd, SHORTENING, "}", collapsed));
            } else {
                int blockStart = (int) sp.getStartPosition(info.getCompilationUnit(), body);
                int blockEnd = (int) sp.getEndPosition(info.getCompilationUnit(), body);

                folds.add(FoldInfo.create(doc, leadsToStart, blockStart + 1, SHORTENING, leadsTo, collapsed));
                folds.add(FoldInfo.create(doc, blockEnd - 1, nctEnd, SHORTENING, "}", collapsed));
            }

            return true;
        }

        private void diamondShortening(List<? extends Tree> targs) throws BadLocationException {
            if (!FoldTypes.TYPE_PARAMETERS.enabled())
                return ;
            
            SourcePositions sp = info.getTrees().getSourcePositions();

            if (!targs.isEmpty()) {
                TokenSequence<JavaTokenId> ts = info.getTokenHierarchy().tokenSequence(JavaTokenId.language());
                int typeParamsStart = (int) sp.getStartPosition(info.getCompilationUnit(), targs.get(0));

                ts.move(typeParamsStart);

                while (   ts.movePrevious()
                       && (IGNORED_TOKENS.contains(ts.token().id()) || ts.token().id() == JavaTokenId.LT)) {
                    if (ts.token().id() ==JavaTokenId.LT) {
                        typeParamsStart = ts.offset() + ts.token().length();
                    }
                }

                int typeParamsEnd   = (int) sp.getEndPosition(info.getCompilationUnit(), targs.get(targs.size() - 1));
                
                ts.move(typeParamsEnd);

                while (   ts.moveNext()
                       && (IGNORED_TOKENS.contains(ts.token().id()) || GT_TOKENS.contains(ts.token().id()))) {
                    switch (ts.token().id()) {
                        case GT: typeParamsEnd = ts.offset(); break;
                        case GTGT: typeParamsEnd = ts.offset() + 1; break;
                        case GTGTGT: typeParamsEnd = ts.offset() + 2; break;
                    }
                }

                folds.add(FoldInfo.create(doc, typeParamsStart, typeParamsEnd, SHORTENING, "~", FoldTypes.TYPE_PARAMETERS.collapsed()));
            }
        }
    }

    private static final Set<JavaTokenId> IGNORED_TOKENS = EnumSet.of(JavaTokenId.WHITESPACE, JavaTokenId.BLOCK_COMMENT, JavaTokenId.LINE_COMMENT, JavaTokenId.JAVADOC_COMMENT);
    private static final Set<JavaTokenId> GT_TOKENS = EnumSet.of(JavaTokenId.GT, JavaTokenId.GTGT, JavaTokenId.GTGTGT);

    private static boolean singleLineClosure(CompilationInfo info, BlockTree bt) {
        if (bt.getStatements().size() != 1) {
            return false;
        }

        int start = (int) info.getTrees().getSourcePositions().getStartPosition(info.getCompilationUnit(), bt.getStatements().get(0));
        int end = (int) info.getTrees().getSourcePositions().getEndPosition(info.getCompilationUnit(), bt.getStatements().get(0));
        CharSequence text = info.getSnapshot().getText();
        int offset = start;

        while (offset < end && offset < text.length()) {
            if (text.charAt(offset) == '\n') {
                return false;
            }
            offset++;
        }

        return true;
    }

    @ServiceProvider(service=JavaSourceTaskFactory.class)
    public static final class FactoryImpl extends EditorAwareJavaSourceTaskFactory {

        public FactoryImpl() {
            super(Phase.RESOLVED, Priority.LOW);
        }

        @Override
        protected CancellableTask<CompilationInfo> createTask(FileObject file) {
            return new ShorteningFold();
        }

    }

    private static final FoldType SHORTENING = new FoldType("shortening");

}
