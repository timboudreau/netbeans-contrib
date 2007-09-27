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
package org.netbeans.modules.editor.fscompletion.spi.support;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.beans.BeanInfo;
import java.io.IOException;
import javax.swing.ImageIcon;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import org.netbeans.api.editor.completion.Completion;
import org.netbeans.editor.BaseDocument;
import org.netbeans.spi.editor.completion.CompletionItem;
import org.netbeans.spi.editor.completion.CompletionTask;
import org.netbeans.spi.editor.completion.support.CompletionUtilities;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;

/**
 *
 * @author Jan Lahoda
 */
final class FSCompletionItem implements CompletionItem {
    
    private FileObject file;
    private ImageIcon  icon;
    private int        anchor;
    private String     toAdd;
    private String     prefix;
    
    /** Creates a new instance of FSCompletionItem */
    public FSCompletionItem(FileObject file, String prefix, int anchor) throws IOException {
        this.file = file;
        
        DataObject od = DataObject.find(file);
        
        icon = new ImageIcon(od.getNodeDelegate().getIcon(BeanInfo.ICON_COLOR_16x16));
        
        this.anchor = anchor;
        
        this.prefix = prefix;
    }

    private void doSubstitute(JTextComponent component, String toAdd, int backOffset) {
        BaseDocument doc = (BaseDocument) component.getDocument();
        int caretOffset = component.getCaretPosition();
        String value = getText();
        
        if (toAdd != null) {
            value += toAdd;
        }
        
        // Update the text
        doc.atomicLock();
        try {
            String prefix = doc.getText(anchor, caretOffset - anchor);
            
            doc.remove(caretOffset - prefix.length(), prefix.length());
            doc.insertString(caretOffset - prefix.length(), value, null);
            
            component.setCaretPosition(component.getCaretPosition() - backOffset);
        } catch (BadLocationException e) {
            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
        } finally {
            doc.atomicUnlock();
        }
    }
    
    public void defaultAction(JTextComponent component) {
        doSubstitute(component, null, 0);
        Completion.get().hideAll();
    }
    
    public void processKeyEvent(KeyEvent evt) {
        if (evt.getID() == KeyEvent.KEY_TYPED) {
            String toAdd = null;
            
            switch (evt.getKeyChar()) {
                case '/': if (toAdd == null) toAdd = "/";
                doSubstitute((JTextComponent) evt.getSource(), toAdd, toAdd.length() - 1);
                evt.consume();
                break;
            }
        }
    }
    
    public int getPreferredWidth(Graphics g, Font defaultFont) {
        return CompletionUtilities.getPreferredWidth(file.getNameExt(), null, g, defaultFont);
    }

    public void render(Graphics g, Font defaultFont, Color defaultColor, Color backgroundColor, int width, int height, boolean selected) {
        CompletionUtilities.renderHtml(icon, file.getNameExt(), null, g, defaultFont, defaultColor, width, height, selected);
    }

    public CompletionTask createDocumentationTask() {
        return null;
    }

    public CompletionTask createToolTipTask() {
        return null;
    }

    public boolean instantSubstitution(JTextComponent component) {
        return true; //????
    }

    public int getSortPriority() {
        return 100;
    }

    public CharSequence getSortText() {
        return getText();
    }

    public CharSequence getInsertPrefix() {
        return getText();
    }
    
    private String getText() {
        return prefix + file.getNameExt();
    }
    
    public int hashCode() {
        return getText().hashCode();
    }
    
    public boolean equals(Object o) {
        if (!(o instanceof FSCompletionItem)) 
            return false;
        
        FSCompletionItem remote = (FSCompletionItem) o;
        
        return getText().equals(remote.getText());
    }
}
