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

package org.netbeans.modules.javafx.editor.completion.environment;

import com.sun.source.tree.EnhancedForLoopTree;
import com.sun.source.tree.ErroneousTree;
import com.sun.source.tree.ExpressionStatementTree;
import com.sun.source.tree.ForLoopTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.Tree;
import com.sun.source.util.TreePath;
import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import org.netbeans.api.javafx.lexer.JFXTokenId;
import org.netbeans.api.javafx.source.JavaFXSource.Phase;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.modules.javafx.editor.completion.JavaFXCompletionEnvironment;
import static org.netbeans.modules.javafx.editor.completion.JavaFXCompletionQuery.*;

/**
 *
 * @author David Strupl
 */
public class ExpressionStatementTreeEnvironment extends JavaFXCompletionEnvironment<ExpressionStatementTree> {
    
    private static final Logger logger = Logger.getLogger(ExpressionStatementTreeEnvironment.class.getName());
    private static final boolean LOGGABLE = logger.isLoggable(Level.FINE);

    @Override
    protected void inside(ExpressionStatementTree est) throws IOException {
        log("inside ExpressionStatementTree " + est);
        Tree t = est.getExpression();
        if (t.getKind() == Tree.Kind.ERRONEOUS) {
            Iterator<? extends Tree> it = ((ErroneousTree) t).getErrorTrees().iterator();
            if (it.hasNext()) {
                t = it.next();
            } else {
                TokenSequence<JFXTokenId> ts = controller.getTokenHierarchy().tokenSequence(JFXTokenId.language());
                ts.move((int) getSourcePositions().getStartPosition(root, est));
                ts.movePrevious();
                switch (ts.token().id()) {
                    case FOR:
                    case IF:
                    case WHILE:
                        return;
                }
                localResult();
                Tree parentTree = path.getParentPath().getLeaf();
                switch (parentTree.getKind()) {
                    case FOR_LOOP:getPath().getLeaf();
                        if (((ForLoopTree) parentTree).getStatement() == est) {
                            addKeywordsForStatement();
                        } else {
                            addValueKeywords();
                        }
                        break;
                    case ENHANCED_FOR_LOOP:
                        if (((EnhancedForLoopTree) parentTree).getStatement() == est) {
                            addKeywordsForStatement();
                        } else {
                            addValueKeywords();
                        }
                        break;
                    case VARIABLE:
                        addValueKeywords();
                        break;
                    default:
                        addKeywordsForStatement();
                        break;
                }
                return;
            }
        }
        TreePath tPath = new TreePath(path, t);
        if (t.getKind() == Tree.Kind.MODIFIERS) {
            // TODO: 
            // insideModifiers(tPath);
        } else if (t.getKind() == Tree.Kind.MEMBER_SELECT && ERROR.contentEquals(((MemberSelectTree) t).getIdentifier())) {
            //controller.toPhase(Phase.ELEMENTS_RESOLVED);
            controller.toPhase(Phase.ANALYZED);
            TreePath expPath = new TreePath(tPath, ((MemberSelectTree) t).getExpression());
            TypeMirror type = controller.getTrees().getTypeMirror(expPath);
            switch (type.getKind()) {
                case TYPEVAR:
                    type = ((TypeVariable) type).getUpperBound();
                    if (type == null) {
                        return;
                    }
                case ARRAY:
                case DECLARED:
                case BOOLEAN:
                case BYTE:
                case CHAR:
                case DOUBLE:
                case FLOAT:
                case INT:
                case LONG:
                case SHORT:
                case VOID:
                    break;
                default:
                    
            }
            log("NOT IMPLEMENTED: insideExpressionStatement ");
        } else {
            insideExpression(tPath);
        }
    }

    private static void log(String s) {
        if (LOGGABLE) {
            logger.fine(s);
        }
    }
}
