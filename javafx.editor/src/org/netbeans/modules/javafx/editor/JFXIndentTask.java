/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2008 Sun Microsystems, Inc. All rights reserved.
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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2008 Sun
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

package org.netbeans.modules.javafx.editor;

import org.netbeans.api.javafx.lexer.JFXTokenId;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenId;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.editor.BaseDocument;
import org.netbeans.modules.editor.indent.spi.Context;
import org.netbeans.modules.editor.indent.spi.ExtraLock;
import org.netbeans.modules.editor.indent.spi.IndentTask;

import javax.swing.text.BadLocationException;
import javax.swing.text.Position;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * @author Rastislav Komara (<a href="mailto:rastislav.komara@sun.com">RKo</a>)
 */
public class JFXIndentTask implements IndentTask, IndentTask.Factory {
    private static Logger log = Logger.getLogger(JFXIndentTask.class.getName());
    private final Context context;

    public JFXIndentTask() {
        this(null);
        if (log.isLoggable(Level.INFO)) log.info("Creating factory instance for JFXIndetTask");
    }

    JFXIndentTask(Context context) {        
        this.context = context;
    }

    /**
     * Perform reindentation of the line(s) of {@link org.netbeans.modules.editor.indent.spi.Context#document()}
     * between {@link org.netbeans.modules.editor.indent.spi.Context#startOffset()} and {@link org.netbeans.modules.editor.indent.spi.Context#endOffset()}.
     * <br/>
     * It is called from AWT thread and it should process synchronously. It is used
     * after a newline is inserted after the user presses Enter
     * or when a current line must be reindented e.g. when Tab is pressed in emacs mode.
     * <br/>
     * The method should use information from the context and modify
     * indentation at the given offset in the document.
     *
     * @throws javax.swing.text.BadLocationException
     *          in case the indent task attempted to insert/remove
     *          at an invalid offset or e.g. into a guarded section.
     */
    public void reindent() throws BadLocationException {
        if (context == null || !context.isIndent()) {
            return;
        }
        final BaseDocument doc = (BaseDocument) context.document();
        final TokenSequence<JFXTokenId> ts = getTokenSequence(doc, context.startOffset());
        int sl = getScopeLevel(ts);
        while (ts.offset() > context.endOffset()) {
            sl = indentLine(ts, sl);
        }

    }

    private int indentLine(TokenSequence<JFXTokenId> ts, int sl) throws BadLocationException {
        final int lineIndex = context.lineStartOffset(context.startOffset());
        if (context.lineIndent(lineIndex) != sl) {
            context.modifyIndent(lineIndex, sl);
        }
        Token t = ts.moveNext() ? ts.token() : null;
        while (t != null && context.lineStartOffset(ts.offset()) == lineIndex) {
            if (t.id() == JFXTokenId.LPAREN || t.id() == JFXTokenId.LBRACE) {
                sl = sl + getIndentStepLevel();
            } else if (t.id() == JFXTokenId.RPAREN || t.id() == JFXTokenId.RBRACE) {
                sl = sl - getIndentStepLevel();
            }
            t = ts.moveNext() ? ts.token() : null;
        }
        return sl;
    }

    private int getIndentStepLevel() {
        return 4;
    }

    /**
     * Get an extra locking or null if no extra locking is necessary.
     */
    public ExtraLock indentLock() {
        return null;
    }

    private int getScopeLevel(TokenSequence<JFXTokenId> ts) {
        final Position position = context.document().getStartPosition();
        if (position.getOffset() == context.startOffset()) {
            return 0;
        } else {
            int so = 0;
            Token t = ts.movePrevious() ? ts.token() : null;
            while (t != null) {
                if (t.id() == JFXTokenId.LBRACE || t.id() == JFXTokenId.LPAREN) {
                    ;
                }
                t = ts.movePrevious() ? ts.token() : null;
            }
            return so;
        }
    }

    private static <T extends TokenId> TokenSequence<T> getTokenSequence(BaseDocument doc, int dotPos) {
        TokenHierarchy<BaseDocument> th = TokenHierarchy.get(doc);
        TokenSequence<?> seq = th.tokenSequence();
        seq.move(dotPos);
        return (TokenSequence<T>) seq;
    }

    /**
     * Create indenting task.
     *
     * @param context non-null indentation context.
     * @return indenting task or null if the factory cannot handle the given context.
     */
    public IndentTask createTask(Context context) {
        if (context.mimePath().contains(JavaFXEditorKit.FX_MIME_TYPE) && context.isIndent()) {
            return new JFXIndentTask(context);
        }
        return null;
    }
}
