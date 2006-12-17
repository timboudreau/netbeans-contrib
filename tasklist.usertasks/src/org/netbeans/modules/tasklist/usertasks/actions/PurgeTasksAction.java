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
import org.netbeans.modules.tasklist.usertasks.UserTaskView;
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
    
    public void actionPerformed(ActionEvent arg0) {
        NotifyDescriptor nd = new NotifyDescriptor.Confirmation(
            NbBundle.getMessage(PurgeTasksAction.class, "PurgeTasks"), // NOI18N
            NbBundle.getMessage(PurgeTasksAction.class, "PurgeTasksTitle"), // NOI18N
            NotifyDescriptor.OK_CANCEL_OPTION
        );
        if (DialogDisplayer.getDefault().notify(nd) == NotifyDescriptor.OK_OPTION) {
            getSingleSelectedTask().purgeCompleted();
            /* TODO: if (nodes[0] instanceof UserTaskNode) {
                UserTask ptsk = ((UserTaskNode) nodes[0]).getTask();
                ptsk.purgeCompleted();
            } else {
                UserTaskList utl = 
                    ((UserTaskListNode) nodes[0]).getUserTaskList();
                utl.getSubtasks().purgeCompletedItems();
            }*/
        }
    }

    public void valueChanged(ListSelectionEvent e) {
        // TODO: does not work for the top node
        setEnabled(getSingleSelectedTask() != null);
    }
}
