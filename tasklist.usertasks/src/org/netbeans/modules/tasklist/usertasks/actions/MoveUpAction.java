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

import org.netbeans.modules.tasklist.usertasks.UserTask;
import org.netbeans.modules.tasklist.usertasks.UserTaskNode;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.NodeAction;
import org.openide.util.actions.SystemAction;

/**
 * Moves a task up
 */
public class MoveUpAction extends NodeAction {
    /**
     * Creates a new instance of MoveUpAction
     */
    public MoveUpAction() {
    }
    
    protected void performAction(org.openide.nodes.Node[] activatedNodes) {
        UserTaskNode n = (UserTaskNode) activatedNodes[0];
        UserTask ut = n.getTask();
        //if (ut.getParent().indexOf(ut) != 0)
        //    ut.moveUp();
    }
    
    protected boolean enable(org.openide.nodes.Node[] activatedNodes) {
        return activatedNodes.length == 1 && 
            activatedNodes[0] instanceof UserTaskNode;
    }
    
    public org.openide.util.HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
    
    public String getName() {
        return NbBundle.getMessage(MoveUpAction.class, "MoveUp"); // NOI18N
    }
    
}
