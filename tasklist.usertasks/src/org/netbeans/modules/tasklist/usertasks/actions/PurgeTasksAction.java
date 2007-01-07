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

package org.netbeans.modules.tasklist.usertasks.actions;

import java.awt.event.ActionEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.tree.TreePath;
import org.netbeans.modules.tasklist.usertasks.UserTaskTreeTableNode;
import org.netbeans.modules.tasklist.usertasks.UserTaskView;
import org.netbeans.modules.tasklist.usertasks.model.UserTask;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.NbBundle;

/**
 * Delete all tasks marked completed
 *
 * @author Tor Norbye 
 * @author tl
 */
public class PurgeTasksAction extends UTViewAction {
    private static final long serialVersionUID = 2;

    /**
     * Constructor.
     * 
     * @param utv view associated with this action 
     */
    public PurgeTasksAction(UserTaskView utv) {
        super(utv, NbBundle.getMessage(PurgeTasksAction.class, 
                "LBL_PurgeTodo")); // NOI18N
    }
    
    public void actionPerformed(ActionEvent e) {
        NotifyDescriptor nd = new NotifyDescriptor.Confirmation(
            NbBundle.getMessage(PurgeTasksAction.class, "PurgeTasks"), // NOI18N
            NbBundle.getMessage(PurgeTasksAction.class, "PurgeTasksTitle"), // NOI18N
            NotifyDescriptor.OK_CANCEL_OPTION
        );
        if (DialogDisplayer.getDefault().notify(nd) == NotifyDescriptor.OK_OPTION) {
            TreePath[] tp = utv.getTreeTable().getSelectedPaths();
            UserTask[] uts = new UserTask[tp.length];
            for (int i = 0; i < tp.length; i++) {
                UserTaskTreeTableNode n = (UserTaskTreeTableNode) 
                        tp[i].getLastPathComponent();
                uts[i] = n.getUserTask();
            }
            uts = UserTask.reduce(uts);
            for (int i = 0; i < uts.length; i++) {
                uts[i].purgeCompleted();
                if (uts[i].isDone()) {
                    if (uts[i].getParent() != null)
                        uts[i].getParent().getSubtasks().remove(uts[i]);
                    else
                        uts[i].getList().getSubtasks().remove(uts[i]);
                }
            }
        }
    }

    public void valueChanged(ListSelectionEvent e) {
        TreePath[] tp = utv.getTreeTable().getSelectedPaths();
        setEnabled(tp.length > 0);
    }
}
