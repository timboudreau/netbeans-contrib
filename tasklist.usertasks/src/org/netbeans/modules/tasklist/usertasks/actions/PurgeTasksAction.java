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

package org.netbeans.modules.tasklist.usertasks.actions;

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
 * Delete all tasks marked completed
 *
 * @author Tor Norbye 
 */
public class PurgeTasksAction extends NodeAction {

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
            NbBundle.getMessage(PurgeTasksAction.class, "PurgeTasks"), // NOI18N
            NbBundle.getMessage(PurgeTasksAction.class, "PurgeTasksTitle"), // NOI18N
            NotifyDescriptor.OK_CANCEL_OPTION
        );
        if (DialogDisplayer.getDefault().notify(nd) == NotifyDescriptor.OK_OPTION) {
            if (nodes[0] instanceof UserTaskNode) {
                UserTask ptsk = ((UserTaskNode) nodes[0]).getTask();
                ptsk.purgeCompleted();
            } else {
                UserTaskList utl = 
                    ((UserTaskListNode) nodes[0]).getUserTaskList();
                utl.getSubtasks().purgeCompletedItems();
            }
        }
    }
    
    public String getName() {
        return NbBundle.getMessage(NewTaskAction.class, 
                                   "LBL_PurgeTodo"); // NOI18N
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
