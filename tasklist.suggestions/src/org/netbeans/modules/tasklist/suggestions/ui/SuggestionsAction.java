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
