/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.tasklist.usertasks.actions;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.netbeans.modules.tasklist.core.TLUtils;
import org.netbeans.modules.tasklist.core.Task;
import org.netbeans.modules.tasklist.core.TaskNode;
import org.netbeans.modules.tasklist.usertasks.UserTask;
import org.netbeans.modules.tasklist.usertasks.UserTaskList;
import org.netbeans.modules.tasklist.usertasks.UserTaskNode;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.NodeAction;

/**
 * Stops a task
 */
public class PauseAction extends NodeAction {
    private static final long serialVersionUID = 1;

    protected boolean enable(Node[] node) {
        if (node.length != 1)
            return false;
        
        if (!(node[0] instanceof UserTaskNode))
            return false;
        
        UserTask ut = ((UserTaskNode) node[0]).getTask();
        
        return UserTaskList.getStarted() == ut;
    }
     
    protected void performAction(Node[] nodes) {
        UserTaskList.getStarted().stop();
    }

    public String getName() {
        return NbBundle.getMessage(StartTaskAction.class, "Pause"); // NOI18N
    }
    
    protected String iconResource() {
        return null;
    }
    
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
        // If you will provide context help then use:
        // return new HelpCtx (NewTodoItemAction.class);
    }
    
    protected boolean asynchronous() {
        return false;
    }    
}
