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

import java.awt.event.ActionEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import org.netbeans.modules.tasklist.usertasks.UserTaskTreeTableNode;
import org.netbeans.modules.tasklist.usertasks.UserTaskView;
import org.netbeans.modules.tasklist.usertasks.model.UserTask;
import org.netbeans.modules.tasklist.usertasks.util.UTUtils;
import org.openide.util.NbBundle;

/**
 * Moves a task to the left.
 *
 * @author tl
 */
public class MoveRightAction extends UTViewAction {
    private static final long serialVersionUID = 1;

    /**
     * Creates a new instance.
     *
     * @param utv a view
     */
    public MoveRightAction(UserTaskView utv) {
        super(utv, NbBundle.getMessage(MoveRightAction.class,
                "MoveRight")); // NOI18N
    }
    
    public void actionPerformed(ActionEvent event) {
        // figuring out where we are
        TreePath sel = utv.getTreeTable().getSelectedPath();
        UserTaskTreeTableNode n = 
                (UserTaskTreeTableNode) sel.getLastPathComponent();
        TreeNode parent = n.getParent();
        int index = parent.getIndex(n);
        UserTaskTreeTableNode newParent = 
                (UserTaskTreeTableNode) parent.getChildAt(index - 1);
        TreePath newParentPath = sel.getParentPath().
                pathByAddingChild(newParent);
        TreePath[] expanded = utv.getTreeTable().getExpandedNodesUnder(sel);
        UserTask[] expandedTasks = new UserTask[expanded.length];
        for (int i = 0; i < expanded.length ; i++) {
            expandedTasks[i] = ((UserTaskTreeTableNode) 
                expanded[i].getLastPathComponent()).getUserTask();
        }
        utv.getTreeTable().clearSelection();

        // moving the task
        UserTask ut = n.getUserTask();
        ut.getParentObjectList().remove(ut);
        newParent.getUserTask().getSubtasks().add(ut);
        
        // expanding and selecting nodes
        utv.getTreeTable().expandPath(newParentPath);
        index = newParent.getIndexOfObject(ut);
        if (index >= 0) {
            for (int i = 0; i < expandedTasks.length; i++) {
                TreePath p = utv.getTreeTable().findPath(expandedTasks[i]);
                if (p != null)
                    utv.getTreeTable().expandPath(p);
            }
            utv.getTreeTable().select(newParentPath.pathByAddingChild(
                    newParent.getChildAt(index)));
        }
    }

    public void valueChanged(ListSelectionEvent e) {
        TreePath[] sel = utv.getTreeTable().getSelectedPaths();
        if (utv.getTreeTable().getSortingModel().getSortedColumn() == -1 &&
                sel.length == 1) {
            Object last = sel[0].getLastPathComponent();
            if (last instanceof UserTaskTreeTableNode) {
                UserTaskTreeTableNode n = (UserTaskTreeTableNode) last;
                TreeNode parent = n.getParent();
                if (parent == null)
                    UTUtils.LOGGER.fine(n.getUserTask() + 
                            " does not have a parent"); // NOI18N
                int index = parent.getIndex(n);
                setEnabled(index != 0);
            } else {
                setEnabled(false);
            }
        } else {
            setEnabled(false);
        }
    }
}
