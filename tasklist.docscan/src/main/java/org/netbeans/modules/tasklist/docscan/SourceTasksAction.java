/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
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
