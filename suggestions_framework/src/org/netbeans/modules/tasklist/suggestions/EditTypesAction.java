/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2002 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.tasklist.suggestions;


import java.awt.Dialog;
import java.awt.Dimension;
import org.openide.DialogDescriptor;
import org.openide.NotifyDescriptor;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.ActionPerformer;
import org.openide.util.actions.CallableSystemAction;
import org.openide.util.actions.SystemAction;
import org.openide.DialogDisplayer;


/** Filter the tasklist such that only tasks matching a given
 * criteria (or with a subtask matching the given criteria) are
 * shown.
 *
 * @author Tor Norbye */
public final class EditTypesAction extends CallableSystemAction
    implements ActionPerformer {
    
    public void performAction() {
        TypesCustomizer panel = new TypesCustomizer();
        panel.setPreferredSize(new Dimension(550,550));
        DialogDescriptor d = new DialogDescriptor(panel,
            NbBundle.getMessage(EditTypesAction.class,
            "TITLE_typecustomizer")); // NOI18N
        d.setModal(true);
        d.setMessageType(NotifyDescriptor.PLAIN_MESSAGE);
        d.setOptionType(NotifyDescriptor.OK_CANCEL_OPTION);
        //Dialog dlg = TopManager.getDefault().createDialog(d);
        Dialog dlg = DialogDisplayer.getDefault().createDialog(d);
        dlg.pack();
        dlg.show();
        if (d.getValue() == NotifyDescriptor.OK_OPTION) {
            panel.apply();
        }
    }

    protected boolean asynchronous() {
        return false;
    }

    /** XXX why are we ignoring action? */
    public void performAction(SystemAction action) {
        performAction();
    }
    
    /** Return name of the action, as shown in menus etc. */    
    public String getName() {
        return NbBundle.getMessage(EditTypesAction.class, 
                                   "EditTypes"); // NOI18N
    }

    protected String iconResource() {
        return "org/netbeans/modules/tasklist/suggestions/editTypesAction.gif"; // NOI18N
    }

    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
        // If you will provide context help then use:
        // return new HelpCtx (MyAction.class);
    }
}
