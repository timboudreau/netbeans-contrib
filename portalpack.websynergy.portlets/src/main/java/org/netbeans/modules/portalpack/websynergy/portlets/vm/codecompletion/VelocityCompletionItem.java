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
 * Portions Copyrighted 2009 Sun Microsystems, Inc.
 */
package org.netbeans.modules.portalpack.websynergy.portlets.vm.codecompletion;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import javax.swing.JToolTip;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.StyledDocument;
import org.netbeans.api.editor.completion.Completion;
import org.netbeans.spi.editor.completion.CompletionItem;
import org.netbeans.spi.editor.completion.CompletionResultSet;
import org.netbeans.spi.editor.completion.CompletionTask;
import org.netbeans.spi.editor.completion.support.AsyncCompletionQuery;
import org.netbeans.spi.editor.completion.support.AsyncCompletionTask;
import org.netbeans.spi.editor.completion.support.CompletionUtilities;

/**
 *
 * @author satyaranjan
 */
public class VelocityCompletionItem implements CompletionItem {

    private String text;
//    private static ImageIcon fieldIcon =
//            new ImageIcon(Utilities.loadImage("org/netbeans/modules/countries/icon.png"));
    private static Color fieldColor = Color.decode("0x0000B2");
    private int caretOffset;
    private int dotOffset;
    private String rightText;
    private String displayText;

    public VelocityCompletionItem(String displayText, String text, String rightText, int dotOffset, int caretOffset) {
        this.text = text;
        this.rightText = rightText;
        this.displayText = displayText;
        this.dotOffset = dotOffset;
        this.caretOffset = caretOffset;
    }

    public void defaultAction(JTextComponent component) {
        StyledDocument doc = (StyledDocument) component.getDocument();
        //Here we remove the characters starting at the start offset
        //and ending at the point where the caret is currently found:

        try {
            /// doc.remove(dotOffset, caretOffset - dotOffset);
            /// doc.insertString(dotOffset, text, null);
            doc.insertString(caretOffset, text, null);
        } catch (BadLocationException ex) {
            ex.printStackTrace();
        }
        //This statement will close the code completion box:
        Completion.get().hideAll();

    }

    public void processKeyEvent(KeyEvent evt) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    public int getPreferredWidth(Graphics g, Font defaultFont) {
        return CompletionUtilities.getPreferredWidth(displayText, null, g, defaultFont);

    }

    public void render(Graphics g, Font defaultFont, Color defaultColor, Color backgroundColor, int width, int height, boolean selected) {
        CompletionUtilities.renderHtml(null, displayText, rightText, g, defaultFont,
                (selected ? Color.white : fieldColor), width, height, selected);

    }

    public CompletionTask createDocumentationTask() {
        return null;
    }

    public CompletionTask createToolTipTask() {
        
        return new AsyncCompletionTask(new AsyncCompletionQuery() {

            protected void query(CompletionResultSet completionResultSet, Document document, int i) {
                JToolTip toolTip = new JToolTip();
                toolTip.setTipText(displayText);
                completionResultSet.setToolTip(toolTip);
                completionResultSet.finish();
            }
        });

    }

    public boolean instantSubstitution(JTextComponent component) {
        return false;
    }

    public int getSortPriority() {
        return 0;
    }

    public CharSequence getSortText() {
        return displayText;
    }

    public CharSequence getInsertPrefix() {
        return displayText;

    }
}
