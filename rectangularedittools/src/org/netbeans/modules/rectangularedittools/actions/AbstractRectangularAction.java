/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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
package org.netbeans.modules.rectangularedittools.actions;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.StringTokenizer;
import javax.swing.JEditorPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.StyledDocument;
import org.netbeans.editor.BaseDocument;
import org.openide.cookies.EditorCookie;
import org.openide.nodes.Node;
import org.openide.text.NbDocument;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.actions.CookieAction;
import org.openide.util.datatransfer.ExClipboard;
import org.openide.windows.TopComponent;

/**
 *
 * @author Sandip V. Chitale (Sandip.Chitale@Sun.Com)
 */
public abstract class AbstractRectangularAction extends CookieAction {

    private static Clipboard clipboard;

    protected void performAction(Node[] activatedNodes) {
        EditorCookie ec = (EditorCookie) activatedNodes[0].getCookie(EditorCookie.class);
        if (ec != null) {
            JEditorPane[] panes = ec.getOpenedPanes();
            if (panes != null) {
                TopComponent activetc = TopComponent.getRegistry().getActivated();
                for (int i = 0; i < panes.length; i++) {
                    if (activetc.isAncestorOf(panes[i])) {
                        doRectangleOperation(panes[i]);
                        break;
                    }
                }
            }
        }
    }

    protected void doRectangleOperation(final JTextComponent textComponent) {
        if (isReplacingAction() && (!textComponent.isEditable())) {
            beep();
            return;
        }

        final Document doc = textComponent.getDocument();
        if (requiresSelection() && textComponent.getSelectionStart() == -1) {
            beep();
            return;
        }

        // lock the document to capture all changes as a single Undoable change
        if (doc instanceof StyledDocument) {
            try {
                NbDocument.runAtomicAsUser((StyledDocument) doc, new Runnable() {

                    public void run() {
                        Caret caret = textComponent.getCaret();

                        int selectionStart = textComponent.getCaretPosition();
                        int selectionEnd = selectionStart;
                        int selectionLength = 0;
                        boolean backwardSelection = false;

                        // check if there is a selection and normalize it
                        if (caret.isSelectionVisible()) {
                            int selStart = caret.getDot();
                            int selEnd = caret.getMark();
                            backwardSelection = selStart > selEnd;
                            selectionStart = Math.min(selStart, selEnd);
                            selectionEnd = Math.max(selStart, selEnd);
                        } else {
                            beep();
                            return;
                        }

                        // selection length
                        selectionLength = selectionEnd - selectionStart;

                        // get document's root element
                        Element rootElement = doc.getDefaultRootElement();

                        // ----- collect all necessary information -----
                        // gather information about first line of the selection
                        int startLineIndex = rootElement.getElementIndex(selectionStart);
                        Element startLineElement = rootElement.getElement(startLineIndex);
                        int startLineStartOffset = startLineElement.getStartOffset();
                        int startLineEndOffset = startLineElement.getEndOffset();
                        int startLineLength = startLineEndOffset - startLineStartOffset;

                        // gather information about last line of the selection
                        int endLineIndex = rootElement.getElementIndex(selectionEnd);
                        Element endLineElement = rootElement.getElement(endLineIndex);
                        int endLineStartOffset = endLineElement.getStartOffset();
                        int endLineEndOffset = endLineElement.getEndOffset();
                        int endLineLength = endLineEndOffset - endLineStartOffset;

                        // the length of the not-selected prefix in every line:
                        int prefixLen = selectionStart - startLineStartOffset;

                        // length of the selection in the last line of the selection:
                        int selectionEndOffsetInEndLine = selectionEnd - endLineStartOffset;

                        // the width of the selected rectangle:
                        int rectangleWidth = selectionEndOffsetInEndLine - prefixLen;

                        // backward rectangle
                        if (rectangleWidth < 0) {
                            beep();
                            return;
                        }

                        String replacementText = getReplacementText(rectangleWidth);

                        String newline = "\n";
                        boolean replaceMultipleRows = false;
                        StringTokenizer replaceRows = null;

                        if (replacementText != null) {
                            // Tokenize text by newline
                            replaceRows = new StringTokenizer(replacementText, newline);
                            replaceMultipleRows = replaceRows.countTokens() > 1;
                        }

                        String replacementLine = replacementText;

                        try {
                            // Collect the string to replace
                            StringBuffer replacement = new StringBuffer();
                            StringBuffer selectedRect = new StringBuffer();

                            for (int i = startLineIndex; i <= endLineIndex; i++) {
                                Element line = rootElement.getElement(i);
                                int lineStartOffset = line.getStartOffset();
                                int lineEndOffset = line.getEndOffset();
                                int lineLength = lineEndOffset - lineStartOffset;

                                // Width of the text to replace
                                int replacementTextWidth = rectangleWidth;

                                if (isReplacingAction()) {
                                    // Skip lines that are shorter than the rectangle start column
                                    if ((lineLength - 1) <= prefixLen) {
                                        replacement.append(doc.getText(lineStartOffset, lineLength));
                                    } else {
                                        // append the prefix
                                        if (prefixLen >= 0) {
                                            replacement.append(doc.getText(lineStartOffset, prefixLen));
                                        }

                                        if (replacementText == null) {
                                            if (isPostProcessingAction()) {
                                                int textToReplaceWidth = Math.min(lineLength - prefixLen, rectangleWidth);
                                                if (textToReplaceWidth > 0) {
                                                    replacementLine = getPostProcessedText(doc.getText(lineStartOffset + prefixLen, textToReplaceWidth));
                                                    replacement.append(replacementLine);
                                                }
                                            }
                                        } else {
                                            // append the replacement
                                            if (replaceMultipleRows) {
                                                if (replaceRows.hasMoreTokens()) {
                                                    // get next replacement text
                                                    replacementLine = replaceRows.nextToken();
                                                } else {
                                                    // ran out of replacement text, use ""
                                                    replacementLine = "";
                                                }
                                                replacementTextWidth = replacementLine.length();
                                            }

                                            replacement.append(replacementLine);
                                        }

                                        // compute suffix length
                                        int suffixPos = lineStartOffset + prefixLen + rectangleWidth;
                                        int suffixLen = lineStartOffset + lineLength - suffixPos;
                                        // suffix is there
                                        if (suffixLen >= 0) {
                                            // append the suffix
                                            replacement.append(doc.getText(suffixPos, suffixLen));
                                        } else {
                                            // simply append newline
                                            replacement.append(newline);
                                        }
                                    }
                                }

                                if (isCopyingAction()) {
                                    // skip lines that are shorter than the start of the rectangle
                                    if (prefixLen < (lineLength - 1)) {
                                        int prefixPos = lineStartOffset + prefixLen;
                                        int postfixLen = lineStartOffset + lineLength - 1 /* accounts for newline */ - prefixPos;
                                        if (postfixLen >= 0) {
                                            replacementTextWidth = Math.min(replacementTextWidth, postfixLen);
                                            String s = doc.getText(lineStartOffset + prefixLen,
                                                    replacementTextWidth);
                                            selectedRect.append(s);
                                            if (i != endLineIndex) {
                                                selectedRect.append(newline);
                                            }
                                        }
                                    }
                                }
                            }

                            // do the replacement
                            if (isReplacingAction()) {
                                int length = endLineEndOffset - startLineStartOffset;
                                doc.remove(startLineStartOffset, length);
                                doc.insertString(startLineStartOffset, replacement.toString(), null);
                            }

                            if (isCopyingAction()) {
                                Clipboard cb = getExClipboard();
                                cb.setContents(new StringSelection(selectedRect.toString()), null);
                            }

                            if (isRetainSelection()) {
                                // select moved lines
                                if (backwardSelection) {
                                    caret.setDot(selectionEnd);
                                    caret.moveDot(selectionStart);
                                } else {
                                    caret.setDot(selectionStart);
                                    caret.moveDot(selectionEnd);
                                }
                            }
                        } catch (BadLocationException e) {
                            // should not happen
                            beep();
                            return;
                        }
                    }
                });
            } catch (BadLocationException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    protected abstract boolean isReplacingAction();

    protected abstract boolean isCopyingAction();

    protected boolean isRetainSelection() {
        return false;
    }

    protected boolean isPostProcessingAction() {
        return false;
    }

    protected boolean requiresSelection() {
        // cut/copy/delete and replace action require selection
        return true;
    }

    protected boolean enable(Node[] activatedNodes) {
        if (activatedNodes == null || activatedNodes.length == 0) {
            return false;
        }
        EditorCookie ec = (EditorCookie) activatedNodes[0].getCookie(EditorCookie.class);
        if (ec != null) {
            JEditorPane[] panes = ec.getOpenedPanes();
            if (panes != null) {
                TopComponent activetc = TopComponent.getRegistry().getActivated();
                for (int i = 0; i < panes.length; i++) {
                    if (activetc.isAncestorOf(panes[i])) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    protected int mode() {
        return CookieAction.MODE_EXACTLY_ONE;
    }

    protected Class[] cookieClasses() {
        return new Class[]{
                    EditorCookie.class
                };
    }

    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    protected boolean asynchronous() {
        return false;
    }

    protected void beep() {
        Toolkit.getDefaultToolkit().beep();
    }

    protected String getReplacementText(int rectangleWidth) {
        return "";
    }

    protected String getPostProcessedText(String originalText) {
        return originalText;
    }

    protected static void setCaretPosition(JTextComponent textComponent, int pos) throws BadLocationException {
        if (textComponent != null) {
            textComponent.setCaretPosition(pos);
        }
    }

    protected static Clipboard getExClipboard() {
        // Lookup and cache the Platfrom's clipboard
        if (clipboard == null) {
            clipboard = (ExClipboard) Lookup.getDefault().lookup(ExClipboard.class);
            if (clipboard == null) {
                clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            }
        }
        return clipboard;
    }
}
