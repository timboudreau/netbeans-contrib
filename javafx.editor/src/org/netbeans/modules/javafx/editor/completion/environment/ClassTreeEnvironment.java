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

import com.sun.source.tree.ClassTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.TypeParameterTree;
import com.sun.source.util.SourcePositions;
import com.sun.source.util.TreePath;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.tools.Diagnostic;
import org.netbeans.api.javafx.lexer.JFXTokenId;
import org.netbeans.api.javafx.source.CompilationController;
import org.netbeans.api.javafx.source.TreeUtilities;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.modules.javafx.editor.completion.JavaFXCompletionEnvironment;
import org.netbeans.modules.javafx.editor.completion.JavaFXCompletionQuery;
import static org.netbeans.modules.javafx.editor.completion.JavaFXCompletionQuery.*;

/**
 *
 * @author David Strupl
 */
public class ClassTreeEnvironment extends JavaFXCompletionEnvironment<ClassTree> {
    
    private static final Logger logger = Logger.getLogger(ClassTreeEnvironment.class.getName());
    private static final boolean LOGGABLE = logger.isLoggable(Level.FINE);

    public ClassTreeEnvironment(ClassTree t, int offset, String prefix, CompilationController controller, TreePath path, SourcePositions sourcePositions, JavaFXCompletionQuery query) {
        super(t, offset, prefix, controller, path, sourcePositions, query);
    }
    
    @Override
    protected void inside(ClassTree t) {
        log("inside ClassTree " + t);
        ClassTree cls = t;
        int startPos = (int) sourcePositions.getEndPosition(root, cls.getModifiers());
        if (startPos <= 0) {
            startPos = (int) sourcePositions.getStartPosition(root, cls);
        }
        String headerText = controller.getText().substring(startPos, offset);
        int idx = headerText.indexOf('{');
        //NOI18N
        if (idx >= 0) {
            addKeywordsForClassBody();
            return;
        }
        TreeUtilities tu = controller.getTreeUtilities();
        Tree lastImpl = null;
        for (Tree impl : cls.getImplementsClause()) {
            int implPos = (int) sourcePositions.getEndPosition(root, impl);
            if (implPos == Diagnostic.NOPOS || offset <= implPos) {
                break;
            }
            lastImpl = impl;
            startPos = implPos;
        }
        if (lastImpl != null) {
            TokenSequence<JFXTokenId> last = findLastNonWhitespaceToken(startPos, offset);
            if (last != null && last.token().id() == JFXTokenId.COMMA) {
            }
            return;
        }
        Tree ext = cls.getExtendsClause();
        if (ext != null) {
            int extPos = (int) sourcePositions.getEndPosition(root, ext);
            if (extPos != Diagnostic.NOPOS && offset > extPos) {
                TokenSequence<JFXTokenId> last = findLastNonWhitespaceToken(extPos + 1, offset);
                //addKeyword(IMPLEMENTS_KEYWORD, SPACE, false);
                return;
            }
        }
        TypeParameterTree lastTypeParam = null;
        for (TypeParameterTree tp : cls.getTypeParameters()) {
            int tpPos = (int) sourcePositions.getEndPosition(root, tp);
            if (tpPos == Diagnostic.NOPOS || offset <= tpPos) {
                break;
            }
            lastTypeParam = tp;
            startPos = tpPos;
        }
        if (lastTypeParam != null) {
            TokenSequence<JFXTokenId> first = findFirstNonWhitespaceToken(startPos, offset);
            if (first != null && first.token().id() == JFXTokenId.GT) {
                first = nextNonWhitespaceToken(first);
                if (first != null && first.offset() < offset) {
                    if (first.token().id() == JFXTokenId.EXTENDS) {
                        return;
                    }
                }
            } else {
                if (lastTypeParam.getBounds().isEmpty()) {
                    addKeyword(EXTENDS_KEYWORD, SPACE, false);
                }
            }
            return;
        }
        TokenSequence<JFXTokenId> lastNonWhitespaceToken = findLastNonWhitespaceToken(startPos, offset);
        if (lastNonWhitespaceToken != null) {
            switch (lastNonWhitespaceToken.token().id()) {
                case EXTENDS:
                    break;
                case IDENTIFIER:
                    break;
            }
            return;
        }
        lastNonWhitespaceToken = findLastNonWhitespaceToken((int) sourcePositions.getStartPosition(root, cls), offset);
        if (path.getParentPath().getLeaf().getKind() == Tree.Kind.COMPILATION_UNIT) {
            addClassModifiers(cls.getModifiers().getFlags());
        } else {
            addMemberModifiers(cls.getModifiers().getFlags(), false);
        }

    }

    private static void log(String s) {
        if (LOGGABLE) {
            logger.fine(s);
        }
    }
}
