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
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.Iterator;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JEditorPane;
import org.netbeans.api.tasklist.SuggestionPerformer;
import org.netbeans.api.tasklist.SuggestionManager;
import org.openide.cookies.EditorCookie;
import org.openide.ErrorManager;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.loaders.DataObject;
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
        boolean enabled = false;
        for (int i = 0; i < node.length; i++) {
            Task task = TaskNode.getTask(node[i]);
            if ((task != null) && (task.getAction() != null)) {
                enabled = true;
            }
        }
        return enabled;
    }

    protected void performAction(Node[] node) {
        SuggestionManagerImpl manager = 
             (SuggestionManagerImpl)SuggestionManager.getDefault();

        boolean skipConfirm = false;
        SuggestionsView tlv = SuggestionsView.getCurrentView();
        if (tlv == null) {
            // INTERNAL ERROR
            return;
        }

        Collection originalModified =
            new ArrayList(DataObject.getRegistry().getModifiedSet());
        boolean fixingStarted = false;
        try {

        for (int i = 0; i < node.length; i++) {
            SuggestionImpl item = (SuggestionImpl)TaskNode.getTask(node[i]);
            if (item == null) {
                continue;
            }

            SuggestionPerformer performer = item.getAction();
            if (performer == null) {
                continue;
            }
            
            boolean doConfirm = manager.isConfirm(item.getSType());
            Object confirmation = null;
            if (doConfirm && !skipConfirm) {
                confirmation = performer.getConfirmation(item);
            }
            if (confirmation != null) {
                // Show in source editor as well, if possible
                if (tlv != null) {
                    tlv.show(item, new SuggestionAnno(item));
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
                /* Don't include a confirmation-suppression toggle on
                   the dialog itself - users may think they have fine
                   control over confirmations (e.g. that they can skip
                   confirmations for only the unused-imports removal
                   in PMD).

                   Instead, they can turn off confirmations in the
                   Edit Types... dialog - and once they know how to do
                   that, they also know how to get it back - and they
                   will also see the granularity of the types they
                   are manipulating.
                JCheckBox noConfirmButton = new JCheckBox();
                Actions.setMenuText(noConfirmButton,
                   NbBundle.getMessage(FixAction.class, "NoConfirm"), true); // NOI18N
                
		dlg.setAdditionalOptions(new Object[] {
                                           noConfirmButton
                                         });
                */

                dlg.setModal(true);
                final Dialog dialog = DialogDisplayer.getDefault().createDialog(dlg);
                dialog.pack();
                dialog.show();
                Object pressedButton = dlg.getValue();

                if (tlv != null) {
                    tlv.unshow(item);
                }
                
                if (pressedButton == cancelButton) {
                    break; // CANCELLED
                } else if (pressedButton == fixAllButton) {
                    skipConfirm = true;
                } else if (pressedButton == skipButton) {
                    // [PENDING] Remove the item, but don't actually perform it
                    //manager.register(itemType, null, itemList);
                    
                    continue;
                } // else: fixButton - go ahead and fix
                
                /* Removed - see comment above declaration
                if (noConfirmButton.isSelected()) {
                    manager.setConfirm(((SuggestionImpl)item).getSType(), false, true);
                }
                */
            }
            if (!fixingStarted) {
                fixingStarted = true;
                manager.setFixing(true);
            }
            performer.perform(item);
            
            // Remove suggestion when we've performed it
            List itemList = new ArrayList(1);
            itemList.add(item);
            SuggestionList sList = (SuggestionList)item.getList();
            manager.register(item.getSType().getName(), null, itemList,
                             sList, true);
        }
        } finally {
            if (fixingStarted) {
                manager.setFixing(false);
            }
        }
        
        // Handle files that have been modified by the fix operation.
        // It's not as simple as looking at the Line objects for the
        // tasks that we've fixed but not done a line.show() on since
        // actions are allowed to modify ANY files. For example, the
        // javaparser module can create a new method in a different
        // class. So instead we diff the modified files list before
        // and after fix, and for the newly modified files, check if
        // they are open. (Hm, how the heck do we do that? They have
        // open documents.... how can I check if they have an actual
        // editor? Will the EditorCookie help?)

        // See if the set of modified files have changed
        Set modifiedRO = DataObject.getRegistry().getModifiedSet();
        Set modified = new java.util.HashSet(modifiedRO);
        modified.removeAll(originalModified);
        
        boolean haveModified = false;
        Iterator it = modified.iterator();
        while (it.hasNext()) {
            DataObject dao = (DataObject)it.next();
            EditorCookie cookie = 
                (EditorCookie)dao.getCookie(EditorCookie.class);
            if (cookie != null) {
                JEditorPane[] panes = cookie.getOpenedPanes();
                if ((panes == null) || (panes.length == 0)) {
                    haveModified = true;
                }
            }
        }
        if (haveModified) {
                JButton openFiles = new JButton();
                Actions.setMenuText(openFiles,
                   NbBundle.getMessage(FixAction.class, 
                                       "ShowFiles"), true); // NOI18N
                
                JButton selectFiles = new JButton();
                Actions.setMenuText(selectFiles,
                   NbBundle.getMessage(FixAction.class, 
                                       "SelectFiles"), true); // NOI18N
                
                JButton saveFiles = new JButton();
                Actions.setMenuText(saveFiles,
                   NbBundle.getMessage(FixAction.class, 
                                       "SaveAllFiles"), true); // NOI18N
                
                JButton cancelButton = new JButton();
                Actions.setMenuText(cancelButton,
                   NbBundle.getMessage(FixAction.class, 
                                       "Cancel"), true); // NOI18N
                
                String title = NbBundle.getMessage(FixAction.class, 
                                                   "FixSavesTitle");
                DialogDescriptor dlg = new DialogDescriptor(
                       NbBundle.getMessage(FixAction.class, 
                                           "FixFileSaves"), // NOI18N
                       title,
                       true,
                       (node.length > 1) ?
                       new JButton [] {
                          openFiles,
                          // NOT YET IMPLEMENTED: selectFiles,
                          saveFiles,
                          cancelButton
                       }
                       :
                       new JButton [] {
                          openFiles,
                          // NOT YET IMPLEMENTED: selectFiles,
                          saveFiles,
                          cancelButton
                       },
                    
                       openFiles,
                       DialogDescriptor.DEFAULT_ALIGN,
                       null,
                       null);
                dlg.setMessageType(NotifyDescriptor.PLAIN_MESSAGE);
                dlg.setModal(true);
                final Dialog dialog = 
                    DialogDisplayer.getDefault().createDialog(dlg);
                dialog.pack();
                dialog.show();
                Object pressedButton = dlg.getValue();

                if (pressedButton == openFiles) {
                    it = modified.iterator();
                    while (it.hasNext()) {
                        DataObject dao = (DataObject)it.next();
                        EditorCookie cookie = 
                            (EditorCookie)dao.getCookie(EditorCookie.class);
                        if (cookie != null) {
                            cookie.open();
                        }
                    }
                } else if (pressedButton == saveFiles) {
                    it = modified.iterator();
                    while (it.hasNext()) {
                        DataObject dao = (DataObject)it.next();
                        EditorCookie cookie = 
                            (EditorCookie)dao.getCookie(EditorCookie.class);
                        if (cookie != null) {
                            try {
                                cookie.saveDocument();
                            } catch (Exception e) {
                                ErrorManager.getDefault().notify(
                                               ErrorManager.WARNING, e);
                            }
                        }
                    }
                } else if (pressedButton == selectFiles) {
                    //XXX TODO!
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
