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

package org.netbeans.modules.tasklist.suggestions;

import java.awt.Component;
import java.awt.Dialog;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.Iterator;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import org.netbeans.modules.tasklist.client.SuggestionPerformer;
import org.netbeans.modules.tasklist.client.SuggestionManager;
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
import org.openide.awt.Mnemonics;

/**
 * Automatically fix a task for which a fix-method has been registered
 *
 * @todo Performance enhancement: when fixing many suggestions, reuse
 *   the DialogDescriptor and button objects; just replace the main pane
 *   contents.
 *
 * @author Tor Norbye
 */

public final class FixAction extends NodeAction {

    private static final long serialVersionUID = 1;

    protected boolean asynchronous() {
        return false;
    }
    
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

        assert node[0] instanceof SuggestionNode : "Need to be softened later on";
        TaskListView tlv = TaskListView.getCurrent();
        // todo this line causes NPE ((SuggestionNode)node[0]).getView();

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
            if (doConfirm && !skipConfirm && performer.hasConfirmation()) {
                confirmation = performer.getConfirmation(item);
            }
            if (confirmation != null) {
                // Show in source editor as well, if possible
                if (tlv != null) {
                    tlv.showTaskInEditor(item, new SuggestionAnno(item));
                    tlv.select(item);
                }

                JButton fixButton = new JButton();
                Mnemonics.setLocalizedText(fixButton, NbBundle.getMessage(FixAction.class, "FixIt"));
                
                JButton fixAllButton = null;
                JButton skipButton = null;
                if (node.length > 1) {
                    fixAllButton = new JButton();
                    Mnemonics.setLocalizedText(fixAllButton,
                                        NbBundle.getMessage(FixAction.class,
                                                       "FixAll"));
                    skipButton = new JButton();
                    Mnemonics.setLocalizedText(skipButton,
                                        NbBundle.getMessage(FixAction.class,
                                                       "Skip"));
                    fixAllButton.getAccessibleContext().setAccessibleDescription(
                        NbBundle.getMessage(FixAction.class,
                                          "ACSD_FixAll")); // NOI18N
                    skipButton.getAccessibleContext().setAccessibleDescription(
                        NbBundle.getMessage(FixAction.class,
                                          "ACSD_Skip")); // NOI18N
                }
                JButton cancelButton = new JButton();
                Mnemonics.setLocalizedText(cancelButton,
                   NbBundle.getMessage(FixAction.class, "Cancel"));

                if (confirmation instanceof Component) {
                    ((Component)confirmation).getAccessibleContext().
                        setAccessibleDescription(
                                   NbBundle.getMessage(FixAction.class,
                                         "ACSD_Confirmation")); // NOI18N
                }
                fixButton.getAccessibleContext().setAccessibleDescription(
                      NbBundle.getMessage(FixAction.class,
                                          "ACSD_Fix")); // NOI18N
                cancelButton.getAccessibleContext().setAccessibleDescription(
                      NbBundle.getMessage(FixAction.class,
                                          "ACSD_Cancel")); // NOI18N
                
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
                dialog.setVisible(true);
                Object pressedButton = dlg.getValue();

                if (tlv != null) {
                    tlv.showTaskInEditor(null, null);
                }
                
                if (pressedButton == cancelButton) {
                    break; // CANCELLED
                } else if (pressedButton == fixAllButton) {
                    skipConfirm = true;
                } else if (pressedButton == skipButton) {
                    // [PENDING] Remove the item, but don't actually perform it
                    //manager.register(itemType, null, itemList);
                    
                    continue;
                } else if (pressedButton != fixButton) {
                    // For example if you Escape or close the window.
                    // See issue 32149.
                    continue;
                }
                
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
            if (item.isZombie()) {
                // It looks like this item has already been removed. This
                // is due to a race condition, where you double click on
                // a task (to fix it) just as the list is getting rescanned,
                // so the task you selected gets removed and is gone by
                // time time the user reaches the confirmation dialog and
                // selects OK.

                // In this case, try to identify a "replacement" suggestion:
                // the same suggestion in the newly updated list.
                // And how do we know if two tasks are identical?
                // They should
                //  - have the same stype
                //  - have the same description
                //  - have the same line? (well, the task may have moved.
                //          This one is tricky because on the other hand,
                //          it's not uncommon to have the same task listed
                //          repeatedly just varying in line numbers
                //          (e.g. the AvoidDuplicateLiterals rule violation)
                //          so it seems like a good criterion to be sure.
                //          The best solution might be to count the number
                //          of matches and if just one, but with a different
                //          line number, use it.
                //   - have the same priority
                // We don't scan for the following since it's unlikely that
                // the the above will match and not the following
                // (and searching for icon equality for example
                // is a bit harder.)
                //   - have the same icon
                //   - have the same details
                //
                int matches = 0;
                Iterator it = item.getParent().subtasksIterator();
                SuggestionImpl match = null;
                boolean exact = false;
                while (it.hasNext()) {
                    SuggestionImpl sm = (SuggestionImpl)it.next();
                    if (sm.hasSubtasks()) {
                        // It's a category node
                        Iterator it2 = sm.subtasksIterator();
                        while (it2.hasNext()) {
                            SuggestionImpl sm2 = (SuggestionImpl)it2.next();
                            if ((item.getSType() == sm2.getSType()) &&
                                item.getSummary().equals(sm2.getSummary()) &&
                                (item.getPriority() == sm2.getPriority()) &&
                                (item.getPriority() == sm2.getPriority())) {
                                match = sm2;
                                matches++;
                                if (item.getLine().equals(sm2.getLine())) {
                                    exact = true;
                                    break;
                                }
                            }
                        }
                        if (exact) {
                            break;
                        }
                    } else {
                        if ((item.getSType() == sm.getSType()) &&
                            item.getSummary().equals(sm.getSummary()) &&
                            (item.getPriority() == sm.getPriority()) &&
                            (item.getPriority() == sm.getPriority())) {
                            match = sm;
                            matches++;
                            if (item.getLine().equals(sm.getLine())) {
                                exact = true;
                                break;
                            }
                        }
                    }
                }
                if ((match != null) && (exact || (matches == 1))) {
                    //System.err.println("Replaced task " + item + " with task " + match + " (they are equal=" + (item == match));
                    item = match;
                } else {
                    // We haven't found a match. It's probably best to
                    // stick with the old item, since the Action will
                    // probably still work (even though the item won't
                    // get removed from the list by the register call
                    // below.
                    
                    //System.err.println("No match.  matches=" + matches + "  match=" + match + "  exact=" + exact + "    item=" + item);
                }
            }
            performer.perform(item);
            
            // XXX Remove suggestion when we've performed it
            // INSTED action is reponsible for marking suggestion as invalid
//            List itemList = new ArrayList(1);
//            itemList.add(item);
//            manager.register(item.getSType().getName(), null, itemList, sList, true);
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
                Mnemonics.setLocalizedText(openFiles,
                   NbBundle.getMessage(FixAction.class, 
                                       "ShowFiles"));
                
                JButton selectFiles = new JButton();
                Mnemonics.setLocalizedText(selectFiles,
                   NbBundle.getMessage(FixAction.class, 
                                       "SelectFiles"));
                
                JButton saveFiles = new JButton();
                Mnemonics.setLocalizedText(saveFiles,
                   NbBundle.getMessage(FixAction.class, 
                                       "SaveAllFiles"));
                
                JButton cancelButton = new JButton();
                Mnemonics.setLocalizedText(cancelButton,
                   NbBundle.getMessage(FixAction.class, 
                                       "Cancel"));
                
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
                dialog.setVisible(true);
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

    protected String iconResource() {
        return "org/netbeans/modules/tasklist/suggestions/fix.gif"; // NOI18N
    }
    
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
        // If you will provide context help then use:
        // return new HelpCtx (NewTodoItemAction.class);
    }
    
}
