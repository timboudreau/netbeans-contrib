/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2001 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.corba.wizard.nodes.gui;

import org.openide.DialogDescriptor;
import org.openide.util.HelpCtx;
import javax.swing.JButton;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
/**
 *
 * @author  tzezula
 * @version 
 */
public class ExDialogDescriptor extends DialogDescriptor implements PropertyChangeListener {
    
    private JButton okButton;
    private JButton cancelButton;
    
    public final static String OK = "OK"; // No I18N
    public final static String CANCEL = "CANCEL"; // No I18N

    /** Creates new ExDialogDescriptor */
    public ExDialogDescriptor(ExPanel panel, String title, boolean isModal, ActionListener listener) {
        super (panel,title,isModal,listener);
        this.okButton = new JButton (java.util.ResourceBundle.getBundle("org/netbeans/modules/corba/wizard/nodes/gui/Bundle").getString("CTL_Ok")); 
        this.okButton.setActionCommand (OK);  
        this.cancelButton = new JButton (java.util.ResourceBundle.getBundle("org/netbeans/modules/corba/wizard/nodes/gui/Bundle").getString("CTL_Cancel"));
        this.cancelButton.setActionCommand (CANCEL); 
        this.setOptions ( new Object[] {okButton, cancelButton});
        this.setOptionsAlign (DialogDescriptor.BOTTOM_ALIGN);
        panel.addPropertyChangeListener (this);
    }
    
    public void enableOk () {
        okButton.setEnabled (true);
    }
    
    public void enableCancel () {
        cancelButton.setEnabled (true);
    }
    
    public void disableOk () {
        okButton.setEnabled (false);
    }
    
    public void disableCancel () {
        cancelButton.setEnabled (false);
    }
    
    public boolean isOkEnabled () {
        return okButton.isEnabled();
    }
    
    public boolean isCancelEnabled () {
        return cancelButton.isEnabled ();
    }

    public void propertyChange(final java.beans.PropertyChangeEvent event) {
        if (event.getPropertyName().equals("Ok")){ // No I18N
            okButton.setEnabled(((Boolean)event.getNewValue()).booleanValue());
        }
        else if (event.getPropertyName().equals("Cancel")){  //No I18N
            cancelButton.setEnabled(((Boolean)event.getNewValue()).booleanValue());
        }
    }
}