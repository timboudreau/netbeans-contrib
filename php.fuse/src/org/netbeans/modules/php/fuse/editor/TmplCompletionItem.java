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
package org.netbeans.modules.php.fuse.editor;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import org.netbeans.api.editor.completion.Completion;
import javax.swing.ImageIcon;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.StyledDocument;
import org.netbeans.spi.editor.completion.CompletionItem;
import org.netbeans.spi.editor.completion.CompletionTask;
import org.netbeans.spi.editor.completion.support.CompletionUtilities;
import org.openide.text.NbDocument;

/**
 *
 * @author cawe
 */
public class TmplCompletionItem implements CompletionItem {

    private static Color fieldColor = Color.decode("0x0000B2");
    private static ImageIcon fieldIcon = null;
    private ImageIcon _icon;
    private int _type;
    private int _carretOffset;
    private int _dotOffset;
    private String _text;

    public TmplCompletionItem(String text, int dotOffset, int carretOffset) {
        _text = text;
        _dotOffset = dotOffset;
        _carretOffset = carretOffset;
        if (fieldIcon == null) {
            fieldIcon = new ImageIcon(getClass().getResource("/org/netbeans/modules/php/fuse/resources/CCIconTmpl.gif"));
        }
        _icon = fieldIcon;
    }

    private void doSubstitute(final JTextComponent component,
            final String toAdd, final int backOffset) {
        final StyledDocument doc =
                (StyledDocument) component.getDocument();
        class AtomicChange implements Runnable {

            public void run() {
                String value = getText();
                if (toAdd != null) {
                    value += toAdd;
                }
                try {
                    doc.remove(_dotOffset, _carretOffset - _dotOffset);
                    doc.insertString(_dotOffset, value + "", null);
                    component.setCaretPosition(component.getCaretPosition() - backOffset);
                } catch (BadLocationException e) {
                    e.printStackTrace();
                }
            }
        }
        try {
            NbDocument.runAtomicAsUser(doc, new AtomicChange());
        } catch (BadLocationException ex) {
            ex.printStackTrace();
        }
    }

    public void defaultAction(JTextComponent component) {
        doSubstitute(component, null, 0);
        Completion.get().hideAll();
    }

    public void processKeyEvent(KeyEvent evt) {
    }

    public int getPreferredWidth(Graphics g, Font defaultFont) {
        return CompletionUtilities.getPreferredWidth(
                _text, null, g, defaultFont);
    }

    public void render(Graphics g, Font defaultFont,
            Color defaultColor, Color backgroundColor,
            int width, int height, boolean selected) {
        CompletionUtilities.renderHtml(_icon, _text, null, g,
                defaultFont, (selected ? Color.white : fieldColor), width,
                height, selected);
    }

    public CompletionTask createDocumentationTask() {
        return null;
    }

    public CompletionTask createToolTipTask() {
        return null;
    }

    public boolean instantSubstitution(JTextComponent component) {
        return true;
    }

    public int getSortPriority() {
        return 0;
    }

    public CharSequence getSortText() {
        return getText();
    }

    public CharSequence getInsertPrefix() {
        return getText();
    }

    public String getText() {
        return _text;
    }
}
