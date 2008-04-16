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
import org.netbeans.api.javafx.source.CompilationController;
import org.netbeans.api.javafx.source.JavaFXSource;
import org.netbeans.api.javafx.source.Task;
import org.netbeans.api.javafx.source.TreeUtilities;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenId;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.editor.BaseDocument;
import org.netbeans.modules.editor.indent.spi.Context;
import org.netbeans.modules.editor.indent.spi.ExtraLock;
import org.netbeans.modules.editor.indent.spi.IndentTask;
import org.netbeans.modules.editor.indent.spi.ReformatTask;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Position;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import com.sun.source.util.TreePath;
import com.sun.source.tree.Tree;

/**
 * @author Rastislav Komara (<a href="mailto:rastislav.komara@sun.com">RKo</a>)
 */
class JFXIndentTask implements IndentTask, ReformatTask {
    private static Logger log = Logger.getLogger(JFXIndentTask.class.getName());
    private static final Pattern KEEP_LEVEL_PTRN = Pattern.compile("\\s*(}|\\))\\s*;?\\s*");

    private final Context context;
    private TokenSequence<JFXTokenId> ts = null;

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
        if (context == null) {
            return;
        }
        if (log.isLoggable(Level.INFO)) log.info("Reindent...");

        final List<Context.Region> regions = context.indentRegions();
        if (!regions.isEmpty()) {
            for (Context.Region region : regions) {
                if (log.isLoggable(Level.INFO))
                    log.info("\tRegion: [" + region.getStartOffset() + "," + region.getEndOffset() + "]");
                indentRegion(region);
            }
        } else {
            if (log.isLoggable(Level.INFO))
                log.info("\tLine: [" + context.startOffset() + "," + context.endOffset() + "]");
            indentLine(context.startOffset());
        }
        if (log.isLoggable(Level.INFO)) log.info("... done!");

    }

    private void indentLine(int offset) throws BadLocationException {
        int lso = context.lineStartOffset(offset);
        int si = getScopeIndent(context.document(), lso);
        if (context.lineIndent(lso) != si) {
            context.modifyIndent(lso, si);
        }
    }

    private void indentRegion(Context.Region region) throws BadLocationException {
        int offset = region.getStartOffset();
        do {
//            int lso = context.lineStartOffset(offset);
//            int si = getScopeIndent(context.document(), lso);
//            if (context.lineIndent(lso) != si) {
//                context.modifyIndent(lso, si);
//            }
            indentLine(offset);
            offset = adjustOffsetToNewLine(offset, context.lineStartOffset(offset));
        } while (offset < region.getEndOffset());
    }

    private int adjustOffsetToNewLine(int offset, int lso) throws BadLocationException {
        while (lso == context.lineStartOffset(offset)) {
            offset++;
        }
        return offset;
    }

    private TokenSequence<JFXTokenId> ts() {
        if (ts != null && ts.isValid()) return ts;
        final BaseDocument doc = (BaseDocument) context.document();
        this.ts = getTokenSequence(doc, this.ts != null ? ts.offset() : context.startOffset());
        return this.ts;
    }

    private int getIndentStepLevel() {
        //TODO: [RKo] Load indel level in spaces from settings.
        return 4;
    }

    /**
     * Get an extra locking or null if no extra locking is necessary.
     */
    public ExtraLock indentLock() {
        return null;
    }


    private int getScopeIndent(Document document, int startOffset) throws BadLocationException {
        final Position position = document.getStartPosition();
        if (position.getOffset() == startOffset) {
            return 0;
        } else {
            // we simply adapt indent of previous line and adjust if necessary.
            int lso = context.lineStartOffset(startOffset);
            int ls = context.lineStartOffset(Math.max(0, lso - 1));
            if (ls == lso) return 0;
            int level = context.lineIndent(ls); //previous line indent

///*
            //verify if we need to adjust level in case of previous line.
            ts().move(ls);
            Token t = ts.moveNext() ? ts.token() : null;
            while (t != null && ts.offset() < lso) {
                if (t.id() == JFXTokenId.LBRACE || t.id() == JFXTokenId.LPAREN) {
                    level += getIndentStepLevel();
                } else if (t.id() == JFXTokenId.RBRACE || t.id() == JFXTokenId.RPAREN) {
                    level -= getIndentStepLevel();
                }
                t = ts.moveNext() ? ts.token() : null;
            }

            // Handle special cases. This cases are handled with small guessing.
            int nlo = adjustOffsetToNewLine(lso, lso); //start offset of next line
            if (KEEP_LEVEL_PTRN.matcher(document.getText(lso, nlo - lso)).matches()) {
                // if current line is "closing" line move it -1 level.
                level -= getIndentStepLevel();
            } else if (KEEP_LEVEL_PTRN.matcher(document.getText(ls, lso - ls)).matches()) {
                // if previous line is "closing" line move this +1 level.
                level += getIndentStepLevel();
            }

            //if we got buggy source code we descent only to zero indent.
            return Math.max(0, level);
        }
    }

    private static <T extends TokenId> TokenSequence<T> getTokenSequence(BaseDocument doc, int dotPos) {
        TokenHierarchy<BaseDocument> th = TokenHierarchy.get(doc);
        TokenSequence<?> seq = th.tokenSequence();
        seq.move(dotPos);
        return (TokenSequence<T>) seq;
    }


    /**
     * Perform reformatting of the {@link org.netbeans.modules.editor.indent.spi.Context#document()}
     * between {@link org.netbeans.modules.editor.indent.spi.Context#startOffset()} and {@link org.netbeans.modules.editor.indent.spi.Context#endOffset()}.
     * <br/>
     * This method may be called several times repetitively for different areas
     * of a reformatted area.
     * <br/>
     * It is called from AWT thread and it should process synchronously. It is used
     * after a newline is inserted after the user presses Enter
     * or when a current line must be reindented e.g. when Tab is pressed in emacs mode.
     * <br/>
     * The method should use information from the context and modify
     * indentation at the given offset in the document.
     *
     * @throws javax.swing.text.BadLocationException
     *          in case the formatter attempted to insert/remove
     *          at an invalid offset or e.g. into a guarded section.
     */
    public void reformat() throws BadLocationException {
        /*if (context == null) throw new IllegalStateException("The context of task is null!");
        if (context.isIndent()) {
            reindent();
            return;
        }
        JavaFXSource s = JavaFXSource.forDocument(context.document());
        try {
            s.runUserActionTask(new Task<CompilationController>() {

                public void run(CompilationController controller) throws Exception {
                    final long s = System.currentTimeMillis();
                    final JavaFXSource.Phase phase = controller.toPhase(JavaFXSource.Phase.PARSED);
                    if (log.isLoggable(Level.INFO)) log.info("Parser time: " + (System.currentTimeMillis() - s) + "ms");
                    if (phase.compareTo(JavaFXSource.Phase.PARSED) >= 0) {
                        if (log.isLoggable(Level.INFO)) log.info("The " + phase + " phase has been reached ... OK!");
                        final TreeUtilities tu = controller.getTreeUtilities();
                        final TreePath path = tu.pathFor(context.startOffset());
                        reformat(path.getLeaf());
                    }
                }
            }, true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }*/
        reindent();
    }

    private void reformat(Tree leaf) {
        if (leaf == null) return;
        if (log.isLoggable(Level.INFO)) log.info("LeafKind: " + leaf.getKind());
        switch (leaf.getKind()) {
            case BLOCK:
                reformatBlock(leaf);
                break;
            default:
                
        }
    }

    private void reformatBlock(Tree tree) {
    }

    /**
     * Get an extra locking or null if no extra locking is necessary.
     */
    public ExtraLock reformatLock() {
        return null;
    }
}
