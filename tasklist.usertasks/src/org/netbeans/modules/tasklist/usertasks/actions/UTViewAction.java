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

package org.netbeans.modules.tasklist.usertasks.actions;

import java.util.ArrayList;
import java.util.List;
import org.netbeans.modules.tasklist.usertasks.table.UTTreeTableNode;
import javax.swing.AbstractAction;
import javax.swing.event.ListSelectionListener;
import javax.swing.tree.TreePath;
import org.netbeans.modules.tasklist.usertasks.table.UTTreeTableNode;
import org.netbeans.modules.tasklist.usertasks.UserTaskView;
import org.netbeans.modules.tasklist.usertasks.model.UserTask;
import org.netbeans.modules.tasklist.usertasks.table.UTBasicTreeTableNode;

/**
 * An action that is aware of selected nodes in a UserTaskView.
 *
 * @author tl
 */
public abstract class UTViewAction extends AbstractAction implements
ListSelectionListener {
    /** view for this action */
    protected UserTaskView utv;
    
    /**
     * Constructor.
     * 
     * @param utv a user task view.
     * @param name Action.NAME
     */
    public UTViewAction(UserTaskView utv, String name) {
        super(name);
        putValue(AbstractAction.SHORT_DESCRIPTION, name);
        this.utv = utv;
        utv.getTreeTable().getSelectionModel().addListSelectionListener(this);
        this.valueChanged(null);
    }
    
    /**
     * Returns the selected task. If more than one task is selected returns null.
     *
     * @return selected task or null
     */
    protected UserTask getSingleSelectedTask() {
        TreePath[] tp = utv.getTreeTable().getSelectedPaths();
        
        // tp[0] is sometimes null if "Expand All" is called...
        if (tp.length == 1 && tp[0] != null) {
            Object last = tp[0].getLastPathComponent();
            if (last instanceof UTBasicTreeTableNode) {
                return ((UTBasicTreeTableNode) last).getUserTask();
            }
        }
        return null;
    }
    
    /**
     * Returns selected tasks. Returns empty list if non-task nodes are selected.
     * 
     * @return selected tasks
     */
    protected List<UserTask> getSelectedTasks() {
        List<UserTask> r = new ArrayList();
        TreePath[] tp = utv.getTreeTable().getSelectedPaths();
        
        for (int i = 0; i < tp.length; i++) {
            TreePath treePath = tp[i];
            
            // tp[0] is sometimes null if "Expand All" is called...
            if (treePath != null && treePath.getLastPathComponent() 
                    instanceof UTBasicTreeTableNode) {
                UserTask ut = ((UTBasicTreeTableNode) treePath.
                        getLastPathComponent()).
                        getUserTask();
                r.add(ut);
            } else {
                r.clear();
                break;
            }
        }

        return r;
    }
}

