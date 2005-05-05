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

import java.io.Serializable;
import org.netbeans.modules.tasklist.usertasks.UTUtils;
import org.netbeans.modules.tasklist.usertasks.model.UserTask;
import org.netbeans.modules.tasklist.usertasks.UserTaskNode;
import org.netbeans.modules.tasklist.usertasks.model.UserTaskObjectList;
import org.netbeans.modules.tasklist.usertasks.UserTaskView;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.NodeAction;

/**
 * Moves a task down
 */
public class MoveDownAction extends NodeAction {
    /**
     * Creates a new instance
     */
    public MoveDownAction() {
    }
    
    protected void performAction(org.openide.nodes.Node[] activatedNodes) {
        UserTaskNode n = (UserTaskNode) activatedNodes[0];
        UserTask ut = n.getTask();
        UserTaskView utv = UserTaskView.getCurrent();
        Object es = utv.getTreeTable().getExpandedNodesAndSelection();
        ut.moveDown();
        utv.getTreeTable().setExpandedNodesAndSelection(es);
    }
    
    protected boolean enable(org.openide.nodes.Node[] activatedNodes) {
        if (activatedNodes.length != 1)
            return false;
        
        if (!(activatedNodes[0] instanceof UserTaskNode))
            return false;
        
        UTUtils.LOGGER.fine("checking the index"); // NOI18N
        UserTaskNode n = (UserTaskNode) activatedNodes[0];
        UserTask ut = n.getTask();
        
        if (ut.getList() == null)
            return false;
        
        UserTaskObjectList list;
        if (ut.getParent() == null)
            list = ut.getList().getSubtasks();
        else
            list = ut.getParent().getSubtasks();
        
        if (list.indexOf(ut) == list.size() - 1) 
            return false;
        
        return true;
    }
    
    protected String iconResource() {
        return "org/netbeans/modules/tasklist/usertasks/actions/moveDown.gif"; // NOI18N
    }
    
    public org.openide.util.HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
    
    public String getName() {
        return NbBundle.getMessage(MoveUpAction.class, "MoveDown"); // NOI18N
    }    
    
    protected boolean asynchronous() {
        return false;
    }    
}
