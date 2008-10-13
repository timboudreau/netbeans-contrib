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
package org.netbeans.modules.php.prado.completion;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import javax.swing.text.JTextComponent;
import org.netbeans.api.editor.completion.Completion;
import org.netbeans.editor.BaseDocument;
import org.netbeans.editor.Utilities;
import org.netbeans.modules.editor.indent.api.Indent;
import org.netbeans.spi.editor.completion.CompletionItem;
import org.netbeans.spi.editor.completion.CompletionTask;
import org.netbeans.spi.editor.completion.support.CompletionUtilities;

/**
 *
 * @author Petr Pisl
 */
public abstract class PradoHTMLCompletionItem implements CompletionItem {

    protected String text;
    protected boolean shift;
    protected int substitutionOffset;

    public PradoHTMLCompletionItem(String text, int substitutionOffset) {
        this.text = text;
        this.substitutionOffset = substitutionOffset;
    }
    
    public String getItemText() {
        return text;
    }

    public void defaultAction(JTextComponent component) {
        if (component != null) {
            if (!shift) {
                Completion.get().hideDocumentation();
                Completion.get().hideCompletion();
            }
            int caretOffset = component.getSelectionEnd();
            substituteText(component, caretOffset - substitutionOffset);
        }
    }

    public void processKeyEvent(KeyEvent evt) {
        shift = (evt.getKeyCode() == KeyEvent.VK_ENTER && evt.getID() == KeyEvent.KEY_PRESSED && evt.isShiftDown());
    }

    public int getPreferredWidth(Graphics g, Font defaultFont) {
        return CompletionUtilities.getPreferredWidth(getLeftHtmlText(), getRightHtmlText(), g, defaultFont);
    }

    public void render(Graphics g, Font defaultFont, Color defaultColor, Color backgroundColor, int width, int height, boolean selected) {
        CompletionUtilities.renderHtml(getIcon(), getLeftHtmlText(), getRightHtmlText(), g, defaultFont, defaultColor, width, height, selected);
    }

    public CompletionTask createDocumentationTask() {
        return null;
    }

    public CompletionTask createToolTipTask() {
        return null;
    }

    public boolean instantSubstitution(JTextComponent component) {
        if (component != null) {
            try {
                int caretOffset = component.getSelectionEnd();
                if (caretOffset > substitutionOffset) {
                    String currentText = component.getDocument().getText(substitutionOffset, caretOffset - substitutionOffset);
                    if (!getSubstituteText().toString().startsWith(currentText)) {
                        return false;
                    }
                }
            } catch (BadLocationException ble) {
            }
        }
        defaultAction(component);
        return true;
    }

    public int getSortPriority() {
        return 11;
    }

    public CharSequence getSortText() {
        return getItemText();
    }

    public CharSequence getInsertPrefix() {
        return getItemText();
    }

    protected ImageIcon getIcon() {
        return null;
    }

    protected abstract String getLeftHtmlText();

    protected String getRightHtmlText() {
        return null;
    }

    protected String getSubstituteText() {
        return getItemText();
    }

    protected boolean substituteText(JTextComponent c, int len) {
        return substituteText(c, len, getMoveBackLength());
    }

    protected boolean substituteText(final JTextComponent c, final int len, int moveBack) {
        return substituteText(c, getSubstituteText(), len, moveBack);
    }

    protected boolean substituteText(final JTextComponent c, final String substituteText, final int len, int moveBack) {
        final BaseDocument doc = (BaseDocument) c.getDocument();
        final boolean[] result = new boolean[1];
        result[0] = true;

        doc.runAtomic(new Runnable() {

            public void run() {
                try {
                    //test whether we are trying to insert sg. what is already present in the text
                    String currentText = doc.getText(substitutionOffset, (doc.getLength() - substitutionOffset) < substituteText.length() ? (doc.getLength() - substitutionOffset) : substituteText.length());
                    if (!substituteText.equals(currentText)) {
                        //remove common part
                        doc.remove(substitutionOffset, len);
                        doc.insertString(substitutionOffset, substituteText, null);
                    } else {
                        c.setCaretPosition(c.getCaret().getDot() + substituteText.length() - len);
                    }
                } catch (BadLocationException ex) {
                    result[0] = false;
                }

            }
        });

        //format the inserted text
        reindent(c);

        if (moveBack != 0) {
            Caret caret = c.getCaret();
            int dot = caret.getDot();
            caret.setDot(dot - moveBack);
        }

        return result[0];
    }

    private void reindent(JTextComponent component) {

        final BaseDocument doc = (BaseDocument) component.getDocument();
        final int dotPos = component.getCaretPosition();
        final Indent indent = Indent.get(doc);
        indent.lock();
        try {
            doc.runAtomic(new Runnable() {

                public void run() {
                    try {
                        int startOffset = Utilities.getRowStart(doc, dotPos);
                        int endOffset = Utilities.getRowEnd(doc, dotPos);
                        indent.reindent(startOffset, endOffset);
                    } catch (BadLocationException ex) {
                        //ignore
                        }
                }
            });
        } finally {
            indent.unlock();
        }

    }

    protected int getMoveBackLength() {
        return 0; //default
    }

    public static class Component extends PradoHTMLCompletionItem {

        public Component(String text, int substitutionOffset) {
            super(text, substitutionOffset);
        }

        @Override
        protected String getLeftHtmlText() {
            return "<font color=#ce2c27>&lt;" + getItemText() + "&gt;</font>";  //NOI18N
        }
    }

    public static class Property extends PradoHTMLCompletionItem {

        public Property(String text, int substitutionOffset) {
            super(text, substitutionOffset);
        }

        @Override
        protected String getSubstituteText() {
            return getItemText() + '=';
        }
        
        @Override
        protected String getLeftHtmlText() {
            return "<font color=#009900>" + getItemText() + "</font>"; //NOI18N
        }
    }


}
