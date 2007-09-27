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

import java.awt.SystemColor;
import javax.swing.JTextField;
import java.util.Vector;
import java.awt.event.*;
import javax.swing.KeyStroke;
import org.netbeans.editor.Utilities;

/**
 *
 * @author Sandip V. Chitale (Sandip.Chitale@Sun.Com)
 */
public class KeySequenceInputField extends JTextField {
    public final static String PROP_KEYSEQUENCE = "keySequence"; // NOI18N
    private Vector strokes = new Vector();
    private StringBuffer text = new StringBuffer();

    private boolean _noModifiers;   
    private boolean _partial;
    
    /** Creates a new instance of KeySequenceInputField */
    public KeySequenceInputField() {
        this(false);
    }
    
    /** Creates a new instance of KeySequenceInputField */
    public KeySequenceInputField(boolean noModifiers) {
        super(1);
        _noModifiers = noModifiers;
        
        _partial = false;
        
        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                keySequenceInputFieldKeyTyped(evt);
            }
            public void keyPressed(java.awt.event.KeyEvent evt) {
                keySequenceInputFieldKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                keySequenceInputFieldKeyReleased(evt);
            }
        });      
    }
    
    /**
     * Clears actual sequence of KeyStrokes
     */
    public void clear() {
        setBackground(SystemColor.window);
        _partial = false;
        strokes.clear();
        text.setLength( 0 );
        setText( text.toString() );
        firePropertyChange( PROP_KEYSEQUENCE, null, null );
    }
    
    public boolean isPartial() {
        return _partial;
    }
    
    /**
     * Returns sequence of completed KeyStrokes as KeyStroke[]
     */
    public KeyStroke[] getKeySequence() {
        return (KeyStroke[])strokes.toArray( new KeyStroke[0] );
    }
    
    
    
    private void keySequenceInputFieldKeyTyped (java.awt.event.KeyEvent evt) {//GEN-FIRST:event_keySequenceInputFieldKeyTyped
        evt.consume();
    }//GEN-LAST:event_keySequenceInputFieldKeyTyped
    
    private void keySequenceInputFieldKeyReleased (java.awt.event.KeyEvent evt) {//GEN-FIRST:event_keySequenceInputFieldKeyReleased
        evt.consume();
        setText( text.toString() );
    }//GEN-LAST:event_keySequenceInputFieldKeyReleased
    
    private void keySequenceInputFieldKeyPressed(java.awt.event.KeyEvent evt) {
        String inputText = getText();
        if (evt.getModifiers() == 0 &&
                KeyStroke.getKeyStroke(KeyEvent.VK_TAB,0).equals(KeyStroke.getKeyStrokeForEvent( evt )) &&
                inputText!=null && inputText.length()>0){
            transferFocus();
            return;
        }
        
        evt.consume();
        
        String modif = KeyEvent.getKeyModifiersText( evt.getModifiers() );
        if( isModifier( evt.getKeyCode() ) ) {
            if (_noModifiers) {
                return;
            }
            setText( text.toString() + modif + '+' ); //NOI18N
            setBackground(SystemColor.inactiveCaption);
            _partial = true;
        } else {
            setBackground(SystemColor.window);
            _partial = false;
            KeyStroke stroke = KeyStroke.getKeyStrokeForEvent( evt );
            if (_noModifiers) {
                stroke = KeyStroke.getKeyStroke(stroke.getKeyCode(), 0, stroke.isOnKeyRelease());
            }
            strokes.add( stroke );
            text.append( Utilities.keyStrokeToString( stroke ) );
            text.append( ' ' );
            setText( text.toString() );
            firePropertyChange( PROP_KEYSEQUENCE, null, null );
        }
    }
    
    private boolean isModifier( int keyCode ) {
        return (keyCode == KeyEvent.VK_ALT) ||
                (keyCode == KeyEvent.VK_ALT_GRAPH) ||
                (keyCode == KeyEvent.VK_CONTROL) ||
                (keyCode == KeyEvent.VK_SHIFT) ||
                (keyCode == KeyEvent.VK_META);
    }
}
