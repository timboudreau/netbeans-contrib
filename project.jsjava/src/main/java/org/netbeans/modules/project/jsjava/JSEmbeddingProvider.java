/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2014 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
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
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
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
 * Portions Copyrighted 2014 Sun Microsystems, Inc.
 */
package org.netbeans.modules.project.jsjava;

import com.sun.source.tree.AnnotationTree;
import com.sun.source.tree.AssignmentTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.LiteralTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.util.SourcePositions;
import com.sun.source.util.TreePath;
import com.sun.source.util.TreePathScanner;
import com.sun.source.util.Trees;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.lang.model.element.TypeElement;
import javax.swing.text.Document;
import org.netbeans.api.editor.mimelookup.MimeRegistration;
import org.netbeans.api.java.source.CompilationInfo;
import org.netbeans.api.java.source.JavaParserResultTask;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.lexer.Language;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.modules.parsing.api.Snapshot;
import org.netbeans.modules.parsing.spi.Parser;
import org.netbeans.modules.parsing.spi.Scheduler;
import org.netbeans.modules.parsing.spi.SchedulerEvent;
import org.netbeans.modules.parsing.spi.SchedulerTask;
import org.netbeans.modules.parsing.spi.TaskFactory;

/**
 *
 * @author Tomas Zezula
 */
public final class JSEmbeddingProvider extends JavaParserResultTask<Parser.Result> {
    private static final Logger LOG = Logger.getLogger(JSEmbeddingProvider.class.getName());

    private static final int PRIORITY = 1000;
    private static final String JS_ANNOTATION = "net.java.html.js.JavaScriptBody";  //NOI18N
    private static final String BODY = "body";                            //NOI18N
    private static final String JAVA_MIME_TYPE = "text/x-java";           //NOI18N
    private static final String JAVASCRIPT_MIME_TYPE = "text/javascript"; //NOI18N
    private final AtomicBoolean canceled = new AtomicBoolean();

    private JSEmbeddingProvider() {
        super(JavaSource.Phase.ELEMENTS_RESOLVED);
    }

    @Override
    public int getPriority() {
        return PRIORITY;
    }

    @Override
    public void cancel() {
        canceled.set(true);
    }

    @Override
    public Class<? extends Scheduler> getSchedulerClass() {
        return Scheduler.EDITOR_SENSITIVE_TASK_SCHEDULER;
    }

    @Override
    public void run(Parser.Result t, SchedulerEvent se) {
        canceled.set(false);
        final CompilationInfo ci = CompilationInfo.get(t);
        final CompilationUnitTree cu = ci.getCompilationUnit();
        final Trees trees = ci.getTrees();
        final SourcePositions sp = trees.getSourcePositions();
        final Finder f = new Finder(trees);
        final List<LiteralTree> result = new ArrayList<LiteralTree>();
        f.scan(cu, result);
        if (!result.isEmpty()) {
            try {
                final TokenHierarchy<Document> tk = TokenHierarchy.get(ci.getDocument());
                final Language<?> java = Language.find(JAVA_MIME_TYPE);
                final Language<?> javaScript = Language.find(JAVASCRIPT_MIME_TYPE);
                if (java != null && javaScript != null) {
                    final TokenSequence<?> seq = tk.tokenSequence(java);
                    if (seq != null) {
                        for (LiteralTree lt : result) {
                            final int start = (int) sp.getStartPosition(cu, lt);
                            final int end = (int) sp.getEndPosition(cu, lt);
                            seq.move(start);
                            while (seq.moveNext() && seq.offset() < end) {
                                seq.createEmbedding(javaScript, 1, 1, true);
                            }
                        }
                    }
                }
            } catch (IOException ioe) {
                LOG.log(Level.WARNING, null, ioe);
            }
        }
    }




    private static final class Finder extends TreePathScanner<Void, List<? super LiteralTree>> {

        private final Trees trees;
        private CompilationUnitTree cu;
        private boolean inEmbedding;

        Finder(final Trees trees) {
            this.trees = trees;
        }

        @Override
        public Void visitCompilationUnit(
                final CompilationUnitTree unit,
                final List<? super LiteralTree> p) {
            this.cu = unit;
            return super.visitCompilationUnit(unit, p);
        }



        @Override
        public Void visitMethod(
                final MethodTree m,
                final List<? super LiteralTree> p) {
            for (AnnotationTree a : m.getModifiers().getAnnotations()) {
                final TypeElement ae = (TypeElement) trees.getElement(TreePath.getPath(cu, a.getAnnotationType()));
                if (ae != null && JS_ANNOTATION.contentEquals(ae.getQualifiedName())) {
                    final List<? extends ExpressionTree> args =  a.getArguments();
                    for (ExpressionTree kvp : args) {
                        if (kvp instanceof AssignmentTree) {
                            final AssignmentTree assignemt = (AssignmentTree) kvp;
                            if (BODY.equals(assignemt.getVariable().toString())) {
                                inEmbedding = true;
                                try {
                                    scan(assignemt.getExpression(), p);
                                } finally {
                                    inEmbedding = false;
                                }
                            }
                        }
                    }
                }
            }
            return null;
        }

        @Override
        public Void visitLiteral(LiteralTree node, List<? super LiteralTree> p) {
            if (inEmbedding) {
                p.add(node);
            }
            return super.visitLiteral(node, p);
        }

    }

    @MimeRegistration(
            service = TaskFactory.class,
            mimeType = JAVA_MIME_TYPE)
    public static final class Factory extends TaskFactory {
        @Override
        public Collection<? extends SchedulerTask> create(Snapshot snpsht) {
            return Collections.singleton(new JSEmbeddingProvider());
        }
    }

}
