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
