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

import java.util.Iterator;

import org.netbeans.modules.tasklist.usertasks.model.UserTask;
import org.netbeans.modules.tasklist.usertasks.model.UserTaskList;
import org.netbeans.modules.tasklist.usertasks.UserTaskListNode;
import org.netbeans.modules.tasklist.usertasks.UserTaskNode;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.NodeAction;

/** 
 * Clears "percent complete" properties of all subtasks recursively.
 */
public class ClearCompletedAction extends NodeAction {
    private static final long serialVersionUID = 1;

    protected boolean enable(Node[] node) {
        return node.length == 1 && 
            (node[0] instanceof UserTaskNode || 
            node[0] instanceof UserTaskListNode);
    }
     
    protected boolean asynchronous() {
        return false;
    }
    
    protected void performAction(Node[] nodes) {
        NotifyDescriptor nd = new NotifyDescriptor.Confirmation(
            NbBundle.getMessage(PurgeTasksAction.class, "ClearCompletedQuestion"), // NOI18N
            NbBundle.getMessage(PurgeTasksAction.class, "ClearCompletedTitle"), // NOI18N
            NotifyDescriptor.OK_CANCEL_OPTION
        );
        if (DialogDisplayer.getDefault().notify(nd) == NotifyDescriptor.OK_OPTION) {
            if (nodes[0] instanceof UserTaskNode) {
                UserTask ptsk = ((UserTaskNode) nodes[0]).getTask();
                clearCompleted(ptsk);
            } else {
                UserTaskList utl = 
                    ((UserTaskListNode) nodes[0]).getUserTaskList();
                Iterator it = utl.getSubtasks().iterator();
                while (it.hasNext()) {
                    UserTask ut = (UserTask) it.next();
                    clearCompleted(ut);
                }
            }
        }
    }
    
    /**
     * Clears the percent complete information for the specified task
     * and all it's subtasks.
     *
     * @param ut a task
     */
    private void clearCompleted(UserTask ut) {
        if (!ut.isProgressComputed())
            ut.setDone(false);
        Iterator it = ut.getSubtasks().iterator();
        while (it.hasNext()) {
            clearCompleted((UserTask) it.next());
        }
    }
    
    public String getName() {
        return NbBundle.getMessage(NewTaskAction.class, 
                                   "ClearCompleted"); // NOI18N
    }
    
    /* Only in context menu
    protected String iconResource() {
        return "org/netbeans/modules/tasklist/core/newTask.gif"; // NOI18N
    }
     */
    
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
        // If you will provide context help then use:
        // return new HelpCtx (PurgeTodoItemsAction.class);
    }
}
