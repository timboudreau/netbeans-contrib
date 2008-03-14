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

package org.netbeans.modules.javafx.editor;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import org.netbeans.api.editor.completion.Completion;
import org.netbeans.editor.BaseDocument;
import org.netbeans.spi.editor.completion.CompletionDocumentation;
import org.netbeans.spi.editor.completion.CompletionItem;
import org.netbeans.spi.editor.completion.CompletionResultSet;
import org.netbeans.spi.editor.completion.CompletionTask;
import org.netbeans.spi.editor.completion.support.AsyncCompletionQuery;
import org.netbeans.spi.editor.completion.support.AsyncCompletionTask;
import org.netbeans.spi.editor.completion.support.CompletionUtilities;
import org.openide.ErrorManager;
import org.openide.util.Utilities;

public class JavaFXCompletionItem implements CompletionItem {
    public final static int EVENT = 1;
    public final static int ARRAY = 2;
    public final static int FIELD = 3;
    public final static int CLASS = 5;
    public final static int METHOD = 6;
    public final static int KEYWORD = 7;
    
    private static Color classColor = Color.decode("0x7C0000");
    private static ImageIcon classIcon = null;
    private static Color methodColor = Color.decode("0x7C0000");
    private static ImageIcon methodIcon = null;
    private static Color fieldColor = Color.decode("0x0000B2");
    private static ImageIcon fieldIcon = null;
    private static ImageIcon eventIcon = null;
    private static Color keywordColor = Color.decode("0x00B200");
    private static ImageIcon keywordIcon = null;
    private ImageIcon icon;
    private int _type;
    private int _carretOffset;
    private int _dotOffset;
    private String _name;
    private String _displayName;
    private CompletionDocumentation javaDoc;
    
    
    /** Creates a new instance of JSCompletionItem */
    public JavaFXCompletionItem(String name, String label, int type, int dotOffset, int carretOffset, CompletionDocumentation doc) {
        _displayName = label;
        _type = type;
        _dotOffset = dotOffset;
        _carretOffset = carretOffset;
        javaDoc = doc;

        if(methodIcon == null){
            methodIcon = new ImageIcon(Utilities.loadImage("org/netbeans/modules/javafx/editor/resources/method-icon.png"));
            fieldIcon = new ImageIcon(Utilities.loadImage("org/netbeans/modules/javafx/editor/resources/field-icon.png"));
            classIcon = new ImageIcon(Utilities.loadImage("org/netbeans/modules/javafx/editor/resources/class-icon.png"));
            eventIcon = new ImageIcon(Utilities.loadImage("org/netbeans/modules/javafx/editor/resources/event-icon.png"));
            keywordIcon = new ImageIcon(Utilities.loadImage("org/netbeans/modules/javafx/editor/resources/keyword-icon.png"));
        }
        
        _name = name;
        
        switch(type){
            case KEYWORD :
                icon = keywordIcon;               
                break;

            case CLASS :
                icon = classIcon;
                break;
                
            case METHOD :
                icon = methodIcon;               
                break;
                
            case EVENT :
                icon = eventIcon;
                break;
                
            case FIELD :
            case ARRAY :                
                icon = fieldIcon;
                break;
        }
    }
    
    private void doSubstitute(JTextComponent component, String toAdd, int backOffset) {
        final BaseDocument doc = (BaseDocument) component.getDocument();
        final int caretPos = component.getCaretPosition();
        String value = getText();
        
        if (toAdd != null) {
            value += toAdd;
        }
        
        // Update the text
        doc.atomicLock();
        
        try {
            component.select(_dotOffset+1, caretPos);
            component.replaceSelection(value);
            component.setCaretPosition(component.getCaretPosition() + _carretOffset);
            
        } catch (Exception e) {
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
        if (evt.getID() == KeyEvent.KEY_TYPED && evt.getKeyCode() == KeyEvent.VK_ENTER) {
            defaultAction((JTextComponent) evt.getSource());
            evt.consume();
        }
    }
    
    public int getPreferredWidth(Graphics g, Font defaultFont) {
        //defaultFont = defaultFont.deriveFont(defaultFont.getStyle() ^ Font.BOLD);
        switch(_type){
            case ARRAY : return CompletionUtilities.getPreferredWidth(_name + "[]", null, g, defaultFont);
            case METHOD : return CompletionUtilities.getPreferredWidth(_displayName, null, g, defaultFont);
            default : return CompletionUtilities.getPreferredWidth(_displayName, null, g, defaultFont);
        }
    }
    
    
    public void render(Graphics g, Font defaultFont, Color defaultColor, Color backgroundColor, int width, int height, boolean selected) {
        //defaultFont = defaultFont.deriveFont(defaultFont.getStyle() ^ Font.BOLD);
        switch(_type){
            case ARRAY :
                CompletionUtilities.renderHtml(icon, _displayName, null, g, defaultFont,
                        (selected ? Color.white : fieldColor), width, height, selected);
                break;
            case METHOD :
                CompletionUtilities.renderHtml(icon, _displayName, null, g, defaultFont,
                        (selected ? Color.white : methodColor), width, height, selected);
                break;
            case CLASS :
                CompletionUtilities.renderHtml(icon, _displayName, null, g, defaultFont,
                        (selected ? Color.white : classColor), width, height, selected);
                break;
            default :
                CompletionUtilities.renderHtml(icon, _displayName, null, g, defaultFont,
                        (selected ? Color.white : fieldColor), width, height, selected);
                break;
        }
        
    }
    
    public CompletionTask createDocumentationTask() {
        return new AsyncCompletionTask(new AsyncCompletionQuery() {
            protected void query(CompletionResultSet resultSet, Document doc, int caretOffset) {
                resultSet.setDocumentation(javaDoc);
                resultSet.finish();
            }
        });
    }
    
    public CompletionTask createToolTipTask() {
        return null;
    }
    
    public boolean instantSubstitution(JTextComponent component) {
        defaultAction(component);
        return true;
    }
    
    public int getSortPriority() {
        if(_type == CLASS) return CLASS;
        if(_type == METHOD) return METHOD;
        return FIELD;
    }
    
    public CharSequence getSortText() {
        return getText();
    }
    
    public CharSequence getInsertPrefix() {
        return getText();
    }
    
    public String getText() {
        return _name;
    }
    
    public int hashCode() {
        return getText().hashCode();
    }
    
    public boolean equals(Object o) {
        if (!(o instanceof JavaFXCompletionItem))
            return false;
        
        JavaFXCompletionItem remote = (JavaFXCompletionItem) o;
        
        return getText().equals(remote.getText());
    }
}
