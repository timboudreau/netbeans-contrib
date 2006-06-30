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

package org.netbeans.modules.corba.wizard.nodes.gui;

import org.openide.DialogDescriptor;
import org.openide.util.NbBundle;
import javax.swing.JButton;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.util.ResourceBundle;
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
    
    private ResourceBundle bundle;

    /** Creates new ExDialogDescriptor */
    public ExDialogDescriptor(ExPanel panel, String title, boolean isModal, ActionListener listener) {
        super (panel,title,isModal,listener);
        this.bundle = NbBundle.getBundle (ExDialogDescriptor.class);
        this.okButton = new JButton (this.bundle.getString("CTL_Ok")); 
        this.okButton.setActionCommand (OK);  
        this.okButton.getAccessibleContext().setAccessibleDescription (this.bundle.getString ("AD_Ok"));
        this.cancelButton = new JButton (this.bundle.getString("CTL_Cancel"));
        this.cancelButton.setActionCommand (CANCEL); 
        this.cancelButton.getAccessibleContext().setAccessibleDescription(this.bundle.getString("AD_Cancel"));
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
        if ("Ok".equals(event.getPropertyName())){ // No I18N
            okButton.setEnabled(((Boolean)event.getNewValue()).booleanValue());
        }
        else if ("Cancel".equals(event.getPropertyName())){  //No I18N
            cancelButton.setEnabled(((Boolean)event.getNewValue()).booleanValue());
        }
    }
}