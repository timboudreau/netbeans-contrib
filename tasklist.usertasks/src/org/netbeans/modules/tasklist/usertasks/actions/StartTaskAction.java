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

import org.netbeans.modules.tasklist.usertasks.model.StartedUserTask;
import org.netbeans.modules.tasklist.usertasks.model.UserTask;
import org.netbeans.modules.tasklist.usertasks.UserTaskNode;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CookieAction;

/**
 * Starts a task
 */
public class StartTaskAction extends CookieAction {

    private static final long serialVersionUID = 1;

    protected void performAction(Node[] nodes) {
        if (StartedUserTask.getInstance().getStarted() != null)
            StartedUserTask.getInstance().start(null);
        UserTask ptsk = ((UserTaskNode) nodes[0]).getTask();
        if (ptsk.getOwner().length() == 0)
            ptsk.setOwner(System.getProperty("user.name")); // NOI18N
        ptsk.start();
    }

    public String getName() {
        return NbBundle.getMessage(StartTaskAction.class, "StartTask"); // NOI18N
    }
    
    protected String iconResource() {
        return "org/netbeans/modules/tasklist/usertasks/actions/startTask.gif"; // NOI18N
    }
    
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
        // If you will provide context help then use:
        // return new HelpCtx (NewTodoItemAction.class);
    }
    
    protected boolean asynchronous() {
        return false;
    }    
    
    protected Class[] cookieClasses() {
        return new Class[] {StartCookie.class};
    }
    
    protected int mode() {
        return CookieAction.MODE_EXACTLY_ONE;
    }    
}
