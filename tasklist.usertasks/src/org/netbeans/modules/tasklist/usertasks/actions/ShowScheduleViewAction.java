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

import org.netbeans.modules.tasklist.usertasks.ScheduleTopComponent;
import org.netbeans.modules.tasklist.usertasks.UserTaskView;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;

/** 
 * Show the Schedule TC
 */
public class ShowScheduleViewAction extends CallableSystemAction {
    private static final long serialVersionUID = 1;

    public void performAction() {
        UserTaskView v = UserTaskView.getCurrent();
        ScheduleTopComponent tc = new ScheduleTopComponent(
            v.getName(), v.getUserTaskList());
        tc.open();
        tc.requestActive();
    }
    
    protected boolean asynchronous() {
        return false;
    }

    public String getName() {
        return NbBundle.getMessage(ShowScheduleViewAction.class,
            "LBL_ViewScheduleWindow"); // NOI18N
    }
    
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
        // If you will provide context help then use:
        // return new HelpCtx (MyAction.class);
    }
}
