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

package org.netbeans.modules.tasklist.suggestions.ui;

import org.netbeans.modules.tasklist.core.TaskListView;
import org.netbeans.modules.tasklist.suggestions.ui.SuggestionsView;

import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;


/**
 * Shows the TaskList suggestions window (it does not toggle).
 *
 * @author Tor Norbye
 */
final public class SuggestionsAction extends CallableSystemAction {

    private static final long serialVersionUID = 1;

    // Creating a window is slow. Make sure multiple quick clicks
    // doesn't create multiple windows.
    private volatile boolean block = false;

    protected boolean asynchronous() {
        return false;  //XXX the blocking logic is useless for synchronous actions
    }

    public void performAction() {
        if (block) {
            return;
        }
        try {
            block = true;
		    show();
        } finally {
            block = false;
        }
    }
    
    private static void show() {
	    TaskListView view =
	    TaskListView.getTaskListView(SuggestionsView.CATEGORY); // NOI18N
        if (view != null) {
	        view.showInMode();
        } else {
            // View not already created: show it this time.
            TaskListView tv = SuggestionsView.createSuggestionsView();
            tv.showInMode();
        }
    }
    
    public String getName() {
        return NbBundle.getMessage(SuggestionsAction.class, "ShowSuggestions"); // NOI18N
    }
    
    protected String iconResource() {
        return "org/netbeans/modules/tasklist/suggestions/suggestion.gif"; // NOI18N
    }
    
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
        // If you will provide context help then use:
        // return new HelpCtx (MyAction.class);
    }
}
