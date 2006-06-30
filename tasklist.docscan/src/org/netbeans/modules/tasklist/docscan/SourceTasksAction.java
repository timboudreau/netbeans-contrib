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

package org.netbeans.modules.tasklist.docscan;

import javax.swing.*;

import org.openide.util.actions.CallableSystemAction;
import org.openide.util.*;
import org.netbeans.modules.tasklist.client.SuggestionManager;
import org.netbeans.modules.tasklist.suggestions.SuggestionManagerImpl;
import org.netbeans.modules.tasklist.suggestions.SuggestionsScanner;
import org.netbeans.modules.tasklist.suggestions.SuggestionsBroker;
import org.netbeans.modules.tasklist.core.TaskListView;
import org.netbeans.modules.tasklist.core.Background;

/**
 * Opens window with scanned project source tasks.
 *
 * @author Petr Kuzel
 */
public final class SourceTasksAction extends CallableSystemAction {

    private static final long serialVersionUID = 1;

    protected boolean asynchronous() {
        return false;
    }

    public void performAction() {

        SuggestionManagerImpl manager = (SuggestionManagerImpl)
            SuggestionManager.getDefault();

        if (false == manager.isEnabled(SourceTaskProvider.TYPE)) {
            manager.setEnabled(SourceTaskProvider.TYPE, true, true);
        }

        TaskListView tlview = TaskListView.getTaskListView(SourceTasksView.CATEGORY);
        if (tlview != null) {
            tlview.showInMode();
        } else {
            if (openByDefaultAll()) {
                final SourceTasksList list = new SourceTasksList();
                final SourceTasksView view = new SourceTasksView(list);

                view.showInMode();
                RepaintManager.currentManager(view).paintDirtyRegions();
                Background back = SourceTasksScanner.scanTasksAsync(view);  // delayed class loading
                view.setBackground(back);
            } else {
                TaskListView tlv = new SourceTasksView(SuggestionsBroker.getDefault().startBroker(new SourceTasksProviderAcceptor()));
                tlv.showInMode();
            }
        }
    }

    /** Access the setting defining action behaviour. */
    private boolean openByDefaultAll() {
        return false;
    }


    public String getName() {
        return NbBundle.getMessage(SourceTasksAction.class, "BK0001");
    }

    public HelpCtx getHelpCtx() {
        return new HelpCtx(SourceTasksAction.class);
    }

    protected String iconResource() {
        return "org/netbeans/modules/tasklist/docscan/todosAction.gif";  // NOI18N
    }

    public static interface ScanProgressMonitor extends SuggestionsScanner.ScanProgress {

        /**
         * Returns number of found todos
         * @thread AWT
         * @param todos found
         */
        void statistics(int todos);
    }


}
