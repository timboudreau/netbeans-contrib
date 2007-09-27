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

package org.netbeans.modules.keybindings.ui;

import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.List;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.table.AbstractTableModel;
import javax.swing.text.EditorKit;
import org.netbeans.editor.BaseKit;
import org.netbeans.modules.keybindings.ui.KeyBindingsHelper.MultiKeyBinding;
import org.openide.ErrorManager;
import org.openide.util.NbBundle;

/**
 *
 * @author Sandip V. Chitale (Sandip.Chitale@Sun.Com)
 */
public class BindingsTableModel extends AbstractTableModel {
    
    static final int KEY_STROKES            = 0;
    static final int KEY_STROKES_CHARS_ONLY = 1;
    static final int ACTION                 = 2;
    static final int SCOPE                  = 3;
    
    private static String[] _columnNames =
    {
        NbBundle.getMessage(BindingsTableModel.class, "HEADERLABEL_keySequencePrefix"),     // KEY_SEQUENCE
        NbBundle.getMessage(BindingsTableModel.class, "HEADERLABEL_keyCharsSequecePrefix"), // KEY_SEQUENCE_CHARS_ONLY
        NbBundle.getMessage(BindingsTableModel.class, "HEADERLABEL_actionPrefix"),          // ACTION
        NbBundle.getMessage(BindingsTableModel.class, "HEADERLABEL_scope"),                 // SCOPE
    };
    
    private KeyBindingsHelper.MultiKeyBinding[] _bindings = new KeyBindingsHelper.MultiKeyBinding[0];
    
    /**
     * Creates a new instance of BindingsTableModel
     */
    public BindingsTableModel(List bindings) {
        int size = bindings.size();
        _bindings = new KeyBindingsHelper.MultiKeyBinding[size];
        try {
            bindings.toArray(_bindings);
        } catch (Throwable t) {
            ErrorManager.getDefault().notify(ErrorManager.EXCEPTION,  t);
        }
    }
    
    public int getColumnCount() {
        return _columnNames.length;
    }
    
    public String getColumnName(int column) {
        return _columnNames[column];
    }
    
    public Class getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case KEY_STROKES:
            case KEY_STROKES_CHARS_ONLY:
                //return JTextPane.KeyBinding.class;
            case ACTION:
            case SCOPE:
                return String.class;
        }
        return null;
    }
    
    public Object getValueAt(int rowIndex, int columnIndex) {
        KeyBindingsHelper.MultiKeyBinding keyBinding = _bindings[rowIndex];
        switch (columnIndex) {
            case KEY_STROKES:
                return keyBindingToString(keyBinding, false);
            case KEY_STROKES_CHARS_ONLY:
                return keyBindingToString(keyBinding, true);
            case ACTION:
                return keyBinding.actionName;
            case SCOPE:
                return getScopeName(keyBinding.kit);
        }
        return "";
    }
    
    public int getRowCount() {
        return _bindings.length;
    }
    
    public static String keyBindingToString(JTextPane.KeyBinding keyBinding , boolean charOnly) {
        if (keyBinding instanceof KeyBindingsHelper.MultiKeyBinding) {
            if (((KeyBindingsHelper.MultiKeyBinding) keyBinding).keys != null) {
                return keySequenceToString(((MultiKeyBinding) keyBinding).keys, charOnly);
            } else {
                return keySequenceToString(new KeyStroke[] {keyBinding.key}, charOnly);
            }
        }
        return keySequenceToString(new KeyStroke[] {keyBinding.key}, charOnly);
    }
    
    /**
     * Creates nice textual description of sequence of KeyStrokes. Usable for
     * displaying MultiKeyBindings. The keyStrokes are delimited by space.
     * @param Array of KeyStrokes representing the actual sequence.
     * @return String describing the KeyStroke sequence.
     */
    public static String keySequenceToString( KeyStroke[] seq , boolean charOnly) {
        if (seq == null) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        for( int i=0; i<seq.length; i++ ) {
            if( i>0 ) sb.append( " " );  // NOI18N
            sb.append( keyStrokeToString( seq[i], charOnly));
        }
        return sb.toString();
    }
    
    /**
     * Creates nice textual representation of KeyStroke.
     * Modifiers and an actual key label are concated by plus signs
     * @param the KeyStroke to get description of
     * @return String describing the KeyStroke
     */
    public static String keyStrokeToString( KeyStroke stroke, boolean charOnly) {
        String modifText = KeyEvent.getKeyModifiersText( stroke.getModifiers() );
        String keyText = (stroke.getKeyCode() == KeyEvent.VK_UNDEFINED) ?
            String.valueOf(stroke.getKeyChar()) : getKeyText(stroke.getKeyCode());
        if( !charOnly && modifText.length() > 0 ) return modifText + '+' + keyText;
        else return keyText;
    }
    
    /** @return slight modification of what KeyEvent.getKeyText() returns.
     *  The numpad Left, Right, Down, Up get extra result.
     */
    private static String getKeyText(int keyCode) {
        String ret = KeyEvent.getKeyText(keyCode);
        if (ret != null) {
            switch (keyCode) {
                case KeyEvent.VK_KP_DOWN:
                    ret = prefixNumpad(ret, KeyEvent.VK_DOWN);
                    break;
                case KeyEvent.VK_KP_LEFT:
                    ret = prefixNumpad(ret, KeyEvent.VK_LEFT);
                    break;
                case KeyEvent.VK_KP_RIGHT:
                    ret = prefixNumpad(ret, KeyEvent.VK_RIGHT);
                    break;
                case KeyEvent.VK_KP_UP:
                    ret = prefixNumpad(ret, KeyEvent.VK_UP);
                    break;
            }
        }
        return ret;
    }
    
    private static String prefixNumpad(String key, int testKeyCode) {
        if (key.equals(KeyEvent.getKeyText(testKeyCode))) {
            key = "KP-" + key;
        }
        return key;
    }
    
    private static HashMap scopeNameMap = new HashMap();
    
    private static String getScopeName(String kit) {
        if (kit == null) {
            return NbBundle.getMessage(BindingsTableModel.class, "LABEL_unknown");
        }
        
        if (kit.length() == 0) {
            return "";
        }
        String scopeName = (String) scopeNameMap.get(kit);
        if (scopeName == null) {
            try {
                Class c = Class.forName(kit);
                EditorKit editorKit = BaseKit.getKit(c);
                scopeName = NbBundle.getMessage(BindingsTableModel.class, "LABEL_content_type") + editorKit.getContentType();
            } catch (Throwable t) {
                scopeName = NbBundle.getMessage(BindingsTableModel.class, "LABEL_editor_kit") + kit;
            }            
            if (scopeName != null) {
                scopeNameMap.put(kit, scopeName);
            }
        }
        return scopeName;
    }
}
