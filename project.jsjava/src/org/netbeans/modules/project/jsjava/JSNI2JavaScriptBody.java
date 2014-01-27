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
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.LiteralTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.Tree.Kind;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreePath;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.netbeans.api.java.lexer.JavaTokenId;
import static org.netbeans.api.java.lexer.JavaTokenId.BLOCK_COMMENT;
import static org.netbeans.api.java.lexer.JavaTokenId.JAVADOC_COMMENT;
import static org.netbeans.api.java.lexer.JavaTokenId.LINE_COMMENT;
import static org.netbeans.api.java.lexer.JavaTokenId.WHITESPACE;
import org.netbeans.api.java.source.CompilationInfo;
import org.netbeans.api.java.source.TreeMaker;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.spi.editor.hints.ErrorDescription;
import org.netbeans.spi.editor.hints.Fix;
import org.netbeans.spi.java.hints.ErrorDescriptionFactory;
import org.netbeans.spi.java.hints.Hint;
import org.netbeans.spi.java.hints.HintContext;
import org.netbeans.spi.java.hints.JavaFix;
import org.netbeans.spi.java.hints.TriggerTreeKind;
import org.openide.util.NbBundle.Messages;

@Hint(displayName = "#DN_JSNI2JavaScriptBody", description = "#DESC_JSNI2JavaScriptBody", category = "general")
@Messages({
    "DN_JSNI2JavaScriptBody=JSNI to @JavaScriptBody",
    "DESC_JSNI2JavaScriptBody=JSNI to @JavaScriptBody"
})
public class JSNI2JavaScriptBody {

    @TriggerTreeKind(Kind.METHOD)
    @Messages("ERR_JSNI2JavaScriptBody=Can convert JSNI to @JavaScriptBody")
    public static ErrorDescription computeWarning(final HintContext ctx) {
        Token<JavaTokenId> token = findBlockToken(ctx.getInfo(), ctx.getPath(), ctx);

        if (token == null) {
            return null;
        }

        Fix fix = new FixImpl(ctx.getInfo(), ctx.getPath()).toEditorFix();
        return ErrorDescriptionFactory.forName(ctx, ctx.getPath(), Bundle.ERR_JSNI2JavaScriptBody(), fix);
    }

    private static Token<JavaTokenId> findBlockToken(CompilationInfo info, TreePath path, HintContext ctx) {
        int end = (int) info.getTrees().getSourcePositions().getEndPosition(path.getCompilationUnit(), path.getLeaf());
        TokenSequence<JavaTokenId> ts = info.getTokenHierarchy().tokenSequence(JavaTokenId.language());

        if (ts == null) return null;

        ts.move(end);

        if ((ctx != null && ctx.isCanceled()) || !ts.movePrevious() || ts.token().id() != JavaTokenId.SEMICOLON) return null;

        OUTER: while (ts.movePrevious()) {
            if (ctx != null && ctx.isCanceled()) return null;

            switch (ts.token().id()) {
                case WHITESPACE: break;
                case LINE_COMMENT: break;
                case JAVADOC_COMMENT: break;
                case BLOCK_COMMENT:
                    final CharSequence tok = ts.token().text();
                    final int l = tok.length(); 
                    if (l > 4 
                        && tok.subSequence(0, 4).toString().equals("/*-{") // NOI18N
                        && tok.subSequence(l - 4, l).toString().equals("}-*/") // NOI18N
                    ) {
                        return ts.offsetToken();
                    }
                    break;
                default:
                    break OUTER;
            }
        }

        return null;
    }

    private static final class FixImpl extends JavaFix {

        public FixImpl(CompilationInfo info, TreePath tp) {
            super(info, tp);
        }

        @Override
        @Messages("FIX_JSNI2JavaScriptBody=Convert JSNI to @JavaScriptBody")
        protected String getText() {
            return Bundle.FIX_JSNI2JavaScriptBody();
        }

        @Override
        protected void performRewrite(TransformationContext ctx) {
            Token<JavaTokenId> jsniComment = findBlockToken(ctx.getWorkingCopy(), ctx.getPath(), null);

            if (jsniComment == null) {
                //XXX: warn?
                return ;
            }
            
            TreeMaker make = ctx.getWorkingCopy().getTreeMaker();
            MethodTree mt = (MethodTree) ctx.getPath().getLeaf();
            List<LiteralTree> params = new ArrayList<LiteralTree>();

            for (VariableTree p : mt.getParameters()) {
                params.add(make.Literal(p.getName().toString()));
            }
            
            String body = jsniComment.text().toString().replace("\"", "\\\"");
            body = body.replace("/*-{", "").replace("}-*/", "");
            
            List<ExpressionTree> arr = new ArrayList<ExpressionTree>();
            arr.add(make.Assignment(make.Identifier("args"), make.NewArray(null, Collections.<ExpressionTree>emptyList(), params)));
            if (body.contains("@") && body.contains("::")) {
                arr.add(make.Assignment(make.Identifier("javacall"), make.Literal(true)));
            }
            ExpressionTree exp = null;
            final String[] lines = body.split("\n");
            for (int i = 0; i < lines.length; i++) {
                String line = lines[i];
                if (i < lines.length - 1) {
                    line = line + "\n";
                }
                if (exp == null) {
                    exp = make.Literal(line);
                } else {
                    exp = make.Binary(Kind.PLUS, exp, make.Literal(line));
                }
                
            }
            arr.add(make.Assignment(make.Identifier("body"), exp));
            
            AnnotationTree jsBody = make.Annotation(make.QualIdent("net.java.html.js.JavaScriptBody"), arr);
            ctx.getWorkingCopy().rewrite(mt.getModifiers(), make.addModifiersAnnotation(mt.getModifiers(), jsBody));
        }
    }
}
