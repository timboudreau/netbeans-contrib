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

package org.netbeans.modules.groovy.groovyproject.ui.customizer;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.Document;
import org.netbeans.api.project.ant.AntArtifact;
import org.netbeans.api.project.libraries.Library;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.DialogDescriptor;
import org.openide.awt.MouseUtils;
import org.openide.filesystems.FileObject;
import org.openide.util.NbBundle;

/** Represents field for main script name and the button to main script chooser.
 * of classpath editing controls.
 *
 * @author Jiri Rechtacek
 */
final class VisualMainScriptSupport {
    
    private final JTextField mainScriptTextField;
    private final JButton chooseMainScriptButton;
    private final FileObject sourceRoot;
    
    private final ArrayList actionListeners = new ArrayList();
    
    public VisualMainScriptSupport (JTextField mainScriptTextField, JButton chooseMainScriptButton, FileObject sourceRoot) {

        this.mainScriptTextField = mainScriptTextField;
        this.chooseMainScriptButton = chooseMainScriptButton;
        this.sourceRoot = sourceRoot;
                                     
        // Register the button listener
        MainScriptListener actionListener = new MainScriptListener ();
        
        chooseMainScriptButton.addActionListener (actionListener);
        mainScriptTextField.getDocument ().addDocumentListener (actionListener);
    } 
    
    public void setMainScript (String mainScript) {
        mainScriptTextField.setText (mainScript);
    }
    
    public String getMainScript () {
        return mainScriptTextField == null ? "" : mainScriptTextField.getText (); // NOI18N
    }
    
    /** Action listeners will be informed when the value of the
     * list changes.
     */
    public void addActionListener( ActionListener listener ) {
        actionListeners.add( listener );
    }
    
    public void removeActionListener( ActionListener listener ) {
        actionListeners.remove( listener );
    }
    
    private void fireActionPerformed() {
        ArrayList listeners;
        
        synchronized ( this ) {
             listeners = new ArrayList( actionListeners );
        }
        
        ActionEvent ae = new ActionEvent( this, 0, null );
        
        for( Iterator it = listeners.iterator(); it.hasNext(); ) {
            ActionListener al = (ActionListener)it.next();
            al.actionPerformed( ae );
        }
        
    }
        
    // Private methods ---------------------------------------------------------

    // Private innerclasses ----------------------------------------------------
    
    private class MainScriptListener implements ActionListener, DocumentListener {
        private final JButton okButton = new JButton (NbBundle.getMessage (VisualMainScriptSupport.class, "LBL_ChooseMainScript_OK"));
     
        // Implementation of ActionListener ------------------------------------
        
        /** Handles button events
         */        
        public void actionPerformed( ActionEvent e ) {
            
            // only chooseMainScriptButton can be performed
            
            final MainScriptChooser panel = new MainScriptChooser (sourceRoot);
            Object[] options = new Object[] {
                okButton,
                DialogDescriptor.CANCEL_OPTION
            };
            panel.addChangeListener (new ChangeListener () {
               public void stateChanged(ChangeEvent e) {
                   if (e.getSource () instanceof MouseEvent && MouseUtils.isDoubleClick (((MouseEvent)e.getSource ()))) {
                       // click button and finish the dialog with selected class
                       okButton.doClick ();
                   } else {
                       okButton.setEnabled (panel.getSelectedMainScript () != null);
                   }
               }
            });
            okButton.setEnabled (panel.getSelectedMainScript () != null);
            DialogDescriptor desc = new DialogDescriptor (panel,
                    NbBundle.getMessage (VisualMainScriptSupport.class, "LBL_ChooseMainScript_Title" ),
                true, options, options[0], DialogDescriptor.BOTTOM_ALIGN, null, null);
            //desc.setMessageType (DialogDescriptor.INFORMATION_MESSAGE);
            Dialog dlg = DialogDisplayer.getDefault ().createDialog (desc);
            dlg.setVisible (true);
            if (desc.getValue() == options[0]) {
               mainScriptTextField.setText (panel.getSelectedMainScript ());
            } 
            dlg.dispose();
        }
        
        // Implementation of document listener ---------------------------------
        
        public void changedUpdate (DocumentEvent e) {
            fireActionPerformed ();
        }
        
        public void insertUpdate( DocumentEvent e ) {
            changedUpdate( e );
        }
        
        public void removeUpdate( DocumentEvent e ) {
            changedUpdate( e );
        }
        
        
    }
    
}
