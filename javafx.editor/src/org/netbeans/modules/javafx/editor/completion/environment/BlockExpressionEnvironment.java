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

import com.sun.source.tree.StatementTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.TryTree;
import com.sun.source.util.SourcePositions;
import com.sun.source.util.TreePath;
import com.sun.tools.javafx.tree.JFXBlockExpression;
import java.io.IOException;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.tools.Diagnostic;
import org.netbeans.api.javafx.source.CompilationController;
import org.netbeans.modules.javafx.editor.completion.JavaFXCompletionEnvironment;
import org.netbeans.modules.javafx.editor.completion.JavaFXCompletionQuery;
import static javax.lang.model.element.Modifier.*;
import static org.netbeans.modules.javafx.editor.completion.JavaFXCompletionQuery.*;

/**
 *
 * @author David Strupl
 */
public class BlockExpressionEnvironment extends JavaFXCompletionEnvironment<JFXBlockExpression> {
    
    private static final Logger logger = Logger.getLogger(BlockExpressionEnvironment.class.getName());
    private static final boolean LOGGABLE = logger.isLoggable(Level.FINE);

    public BlockExpressionEnvironment(JFXBlockExpression t, int offset, String prefix, CompilationController controller, TreePath path, SourcePositions sourcePositions, JavaFXCompletionQuery query) {
        super(t, offset, prefix, controller, path, sourcePositions, query);
    }
    
    @Override
    protected void inside(JFXBlockExpression t) throws IOException {
        log("inside JFXBlockExpression " + t);
        JFXBlockExpression bl = t;
        int blockPos = (int) sourcePositions.getStartPosition(root, bl);
        String text = getController().getText().substring(blockPos, offset);
        if (text.indexOf('{') < 0) {
            //NOI18N
            addMemberModifiers(Collections.singleton(STATIC), false);
            return;
        }
        StatementTree last = null;
        for (StatementTree stat : bl.getStatements()) {
            int pos = (int) sourcePositions.getStartPosition(root, stat);
            if (pos == Diagnostic.NOPOS || offset <= pos) {
                break;
            }
            last = stat;
        }
        if (last == null) {
        } else if (last.getKind() == Tree.Kind.TRY) {
            if (((TryTree) last).getFinallyBlock() == null) {
                addKeyword(CATCH_KEYWORD, null, false);
                addKeyword(FINALLY_KEYWORD, null, false);
                if (((TryTree) last).getCatches().size() == 0) {
                    return;
                }
            }
        }
        localResult();
        addKeywordsForStatement();
    }

    private static void log(String s) {
        if (LOGGABLE) {
            logger.fine(s);
        }
    }
}
