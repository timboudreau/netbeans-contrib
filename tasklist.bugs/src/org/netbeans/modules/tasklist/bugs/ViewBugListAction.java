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

package org.netbeans.modules.tasklist.bugs;

import javax.swing.SwingUtilities;
import org.netbeans.modules.tasklist.core.TaskListView;


import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;


/** Show the bug list view
 * @author Tor Norbye */
public class ViewBugListAction extends CallableSystemAction {

    public void performAction() {
	SwingUtilities.invokeLater(new Runnable() {
		public void run() {
		    show();
		}
	    }
	);
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
