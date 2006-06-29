/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
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
