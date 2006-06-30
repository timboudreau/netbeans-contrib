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

package org.netbeans.modules.tasklist.bugs;

import org.netbeans.modules.tasklist.core.TaskListView;

import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;


/** Show the bug list view
 * @author Tor Norbye */
public class ViewBugListAction extends CallableSystemAction {

    private static final long serialVersionUID = 1;

    public void performAction() {
	    show();
    }

    protected boolean asynchronous() {
        return false;
    }

    static void show() {
	TaskListView view = 
	    TaskListView.getTaskListView(BugsView.CATEGORY); // NOI18N
        if (view != null) {
	    view.showInMode();
	    return;
        }

	// View not already created: show it this time.
//	TaskListView tv = new BugsView();
//	tv.showInMode();
//
//
//        // Refresh bug list as well, if it isn't already showing
//        BugList list = (BugList)tv.getList();
//        if (list != null) {
//            list.refresh();
//        }
    }
    
    public String getName() {
        return NbBundle.getMessage(ViewBugListAction.class, "ViewBugList"); // NOI18N
    }
    
    protected String iconResource() {
        return "org/netbeans/modules/tasklist/bugs/bugsView.gif"; // NOI18N
    }
    
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
        // If you will provide context help then use:
        // return new HelpCtx (MyAction.class);
    }
}
