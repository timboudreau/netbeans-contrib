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

package org.netbeans.modules.tasklist.docscan;

import javax.swing.*;

import org.openide.util.actions.CallableSystemAction;
import org.openide.util.*;
import org.netbeans.modules.tasklist.client.SuggestionManager;
import org.netbeans.modules.tasklist.suggestions.SuggestionManagerImpl;
import org.netbeans.modules.tasklist.suggestions.SuggestionsScanner;

/**
 * Opens window with scanned project source tasks.
 *
 * @author Petr Kuzel
 */
public final class SourceTasksAction extends CallableSystemAction {

    protected boolean asynchronous() {
        return false;
    }

    public void performAction() {

        SuggestionManagerImpl manager = (SuggestionManagerImpl)
            SuggestionManager.getDefault();

        if (false == manager.isEnabled(SourceTaskProvider.TYPE)) {
            manager.setEnabled(SourceTaskProvider.TYPE, true, true);
        }

        final SourceTasksList list = new SourceTasksList();
        // The category should be DIFFERENT from the category used
        // for the default suggestion view (the active scanning view)
        // such that the "Show Suggestions View" action does not
        // locate and reuse these windows - and so they can have different
        // column configurations (filename is not useful in the active
        // suggestions view window, but is critical in the directory
        // scan for example.)
        final SourceTasksView view = new SourceTasksView(list);

        view.showInMode();
        RepaintManager.currentManager(view).paintDirtyRegions();
        SourceTasksScanner.scanTasksAsync(view);  // delayed class loading
    }



    public String getName() {
        return "TODOs";
    }

    public HelpCtx getHelpCtx() {
        return new HelpCtx(SourceTasksAction.class);
    }

    protected String iconResource() {
        return "org/netbeans/modules/tasklist/docscan/scanned-task.gif";  // NOI18N
    }

    public static interface ScanProgressMonitor extends SuggestionsScanner.ScanProgress {
        /**
         * Predics how many folders will be scanned.
         * @thread AWT
         * @param folders estimate.
         */
        void estimate(int folders);

        /**
         * Returns number of found todos
         * @thread AWT
         * @param todos found
         */
        void statistics(int todos);
    }


}
