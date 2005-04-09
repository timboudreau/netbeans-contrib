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
import org.netbeans.modules.tasklist.core.ToggleViewAction;
import org.netbeans.modules.tasklist.suggestions.ui.SuggestionsView;

import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;
import org.openide.windows.TopComponent;


/**
 * Shows the TaskList suggestions window
 *
 * @author Tor Norbye
 * @author Tim Lebedkov
 */
final public class ToggleSuggestionsAction extends ToggleViewAction {

    private static final long serialVersionUID = 1;

    protected boolean isViewOpened() {
        TaskListView view =
	    TaskListView.getTaskListView(SuggestionsView.CATEGORY);
        if (view == null)
            return false;
        return super.isViewOpened();
    }

    protected TopComponent getView(){
        TaskListView view =
	    TaskListView.getTaskListView(SuggestionsView.CATEGORY);
        if (view != null) {
	    return view;
        } else {
            // View not already created: show it this time.
            return SuggestionsView.createSuggestionsView();
        }
    }

    public String getName() {
        return NbBundle.getMessage(ToggleSuggestionsAction.class, "ToggleSuggestions"); // NOI18N
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
