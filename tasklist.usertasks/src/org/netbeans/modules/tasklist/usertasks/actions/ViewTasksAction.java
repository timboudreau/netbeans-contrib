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

import org.netbeans.modules.tasklist.usertasks.UserTaskView;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;


/** 
 * Show the User TaskList topcomponent
 *
 * @author Tor Norbye 
 */
public class ViewTasksAction extends CallableSystemAction {

    private static final long serialVersionUID = 1;

    public void performAction() {
        show();
    }
    
    protected boolean asynchronous() {
        return false;
    }

    static void show() {
        UserTaskView view = UserTaskView.getDefault();
        if (view != null) {
            view.showInMode();
        }
    }
    
    public String getName() {
        return NbBundle.getMessage(ViewTasksAction.class,
                                   "LBL_ViewTodoList"); // NOI18N
    }
    
    protected String iconResource() {
        return "org/netbeans/modules/tasklist/usertasks/actions/taskView.gif"; // NOI18N
    }
    
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
        // If you will provide context help then use:
        // return new HelpCtx (MyAction.class);
    }
}
