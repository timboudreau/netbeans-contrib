/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.tasklist.suggestions;

import java.awt.Dialog;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import org.netbeans.api.tasklist.SuggestionPerformer;
import org.netbeans.api.tasklist.SuggestionManager;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.Actions;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.NodeAction;

import org.netbeans.modules.tasklist.core.*;

/**
 * Automatically fix a task for which a fix-method has been registered
 *
 * @todo Performance enhancement: when fixing many suggestions, reuse
 *   the DialogDescriptor and button objects; just replace the main pane
 *   contents.
 *
 * @author Tor Norbye
 */

public class FixAction extends NodeAction {

    protected boolean enable(Node[] node) {
        if ((node == null) || (node.length < 1)) {
            return false;
        }
        boolean enabled = true;
        for (int i = 0; i < node.length; i++) {
            Task task = TaskNode.getTask(node[i]);
            if ((task == null) || (task.getAction() == null)) {
                enabled = false;
            }
        }
        return enabled;
    }

    protected void performAction(Node[] node) {
        SuggestionManagerImpl manager = 
             (SuggestionManagerImpl)SuggestionManager.getDefault();
        boolean fixingStarted = false;
        try {
        boolean skipConfirm = false;
        TaskListView tlv = TaskListView.getCurrent();
        for (int i = 0; i < node.length; i++) {
            Task item = TaskNode.getTask(node[i]); //safe - see enable check

            SuggestionPerformer performer = item.getAction();
            Object confirmation = performer.getConfirmation(item);
            boolean doConfirm = manager.isConfirm(((SuggestionImpl)item).getSType());
            if (doConfirm && !skipConfirm && (confirmation != null)) {
                // Show in source editor as well, if possible
                if (tlv != null) {
                    tlv.show(item);
                    tlv.select(item);
                }

                JButton fixButton = new JButton();
                Actions.setMenuText(fixButton,
                   NbBundle.getMessage(FixAction.class, "FixIt"), true); // NOI18N
                
                JButton fixAllButton = new JButton();
                Actions.setMenuText(fixAllButton,
                   NbBundle.getMessage(FixAction.class, "FixAll"), true); // NOI18N
                
                JButton skipButton = new JButton();
                Actions.setMenuText(skipButton,
                   NbBundle.getMessage(FixAction.class, "Skip"), true); // NOI18N
                
                JButton cancelButton = new JButton();
                Actions.setMenuText(cancelButton,
                   NbBundle.getMessage(FixAction.class, "Cancel"), true); // NOI18N
                
                JCheckBox noConfirmButton = new JCheckBox();
                Actions.setMenuText(noConfirmButton,
                   NbBundle.getMessage(FixAction.class, "NoConfirm"), true); // NOI18N
                
                String title = NbBundle.getMessage(FixAction.class, "TITLE_fixconfirm");
                DialogDescriptor dlg = new DialogDescriptor(
                       confirmation,
                       title,
                       true,
                       (node.length > 1) ?
                       new JButton [] {
                          fixButton,
                          skipButton,
                          fixAllButton,
                          cancelButton
                       }
                       :
                       new JButton [] {
                          fixButton,
                          cancelButton
                       },
                    
                       fixButton,
                       DialogDescriptor.DEFAULT_ALIGN,
                       null,
                       null);
                dlg.setMessageType(NotifyDescriptor.PLAIN_MESSAGE);
		dlg.setAdditionalOptions(new Object[] {
                                           noConfirmButton
                                         });
                dlg.setModal(true);
                final Dialog dialog = DialogDisplayer.getDefault().createDialog(dlg);
                dialog.pack();
                dialog.show();
                Object pressedButton = dlg.getValue();
                if (pressedButton == cancelButton) {
                    return; // CANCELLED
                } else if (pressedButton == fixAllButton) {
                    skipConfirm = true;
                } else if (pressedButton == skipButton) {
                    // [PENDING] Remove the item, but don't actually perform it
                    //manager.remove(item);
                    
                    continue;
                } // else: fixButton - go ahead and fix
                
                if (noConfirmButton.isSelected()) {
                    manager.setConfirm(((SuggestionImpl)item).getSType(), false, true);
                }
            }
            if (!fixingStarted) {
                fixingStarted = true;
                manager.setFixing(true);
            }
            performer.perform(item);
            
            // Remove suggestion when we've performed it
            manager.remove(item);
        }
        } finally {
            if (fixingStarted) {
                manager.setFixing(false);
            }
        }
    }
    
    public String getName() {
        return NbBundle.getMessage(FixAction.class, "LBL_FixConfirm"); // NOI18N
    }

    /*
    protected String iconResource() {
        return "org/netbeans/modules/tasklist/suggestions/fix.gif"; // NOI18N
    }
    */
    
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
        // If you will provide context help then use:
        // return new HelpCtx (NewTodoItemAction.class);
    }
    
}
