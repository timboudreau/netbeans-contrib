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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.modulemanager;

import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import javax.swing.JButton;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.awt.Mnemonics;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;
/**
 * PENDING
 * @author  Jirka Rechtacek (jrechtacek@netbeans.org)
 */
public class ModuleCatalogAction extends CallableSystemAction {

    /** Weak reference to the dialog showing singleton Module Catalog. */
    static private Reference<Dialog> dialogWRef = new WeakReference<Dialog> (null);
    
    public ModuleCatalogAction() {
        putValue("noIconInMenu", Boolean.TRUE); //NOI18N
    }    
    
    public void performAction () {
        
        Dialog dialog = dialogWRef.get ();

        if (dialog == null || ! dialog.isShowing ()) {
            
            JButton closeOption = new JButton ();
            Mnemonics.setLocalizedText (closeOption, NbBundle.getBundle (ModuleCatalogAction.class).getString ("BTN_ModuleCatalog_CloseOption")); // NOI18N
            
            DialogDescriptor dd = new DialogDescriptor (ModuleSelectionPanel.getGUI (true),
                                    NbBundle.getMessage (ModuleCatalogAction.class, "LBL_ModuleCatalogName"), // NOI18N
                                    false,
                                    new Object [] { closeOption },
                                    closeOption,
                                    DialogDescriptor.DEFAULT_ALIGN,
                                    new HelpCtx (ModuleSelectionPanel.class),
                                    null,
                                    true);
            closeOption.addActionListener (new ActionListener () {
                public void actionPerformed (ActionEvent e) {
                    ModuleSelectionPanel.getGUI (false).setWaitingState (false, false);
                }
            });
            dialog = DialogDisplayer.getDefault ().createDialog (dd);
            dialogWRef = new WeakReference<Dialog> (dialog);
            dialog.setVisible (true);
            
        } else {
            dialog.toFront ();
        }
        
    }
    
    protected boolean asynchronous() {
        return false;
    }

    public String getName () {
        return NbBundle.getMessage (ModuleCatalogAction.class, "LBL_ModuleCatalogAction"); // NOI18N
    }

    public HelpCtx getHelpCtx () {
        return null;
    }

    /**
     * Adding hint.
     */
    protected void initialize () {
	super.initialize ();
        putProperty (ModuleCatalogAction.SHORT_DESCRIPTION, NbBundle.getMessage (ModuleCatalogAction.class, "HINT_ModuleCatalogAction"));
    }
}
