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
import javax.swing.tree.TreePath;
import org.netbeans.modules.tasklist.usertasks.UserTaskListTreeTableNode;
import org.netbeans.modules.tasklist.usertasks.UserTaskTreeTableNode;
import org.netbeans.modules.tasklist.usertasks.UserTaskView;
import org.netbeans.modules.tasklist.usertasks.model.UserTask;
import org.netbeans.modules.tasklist.usertasks.model.UserTaskObjectList;
import org.netbeans.modules.tasklist.usertasks.treetable.AdvancedTreeTableNode;
import org.openide.util.NbBundle;

/**
 * Moves a task to the left.
 *
 * @author tl
 */
public class MoveLeftAction extends UTViewAction {
    private static final long serialVersionUID = 1;

    /**
     * Creates a new instance.
     *
     * @param utv a view
     */
    public MoveLeftAction(UserTaskView utv) {
        super(utv, NbBundle.getMessage(MoveLeftAction.class,
                "MoveLeft")); // NOI18N
    }
    
    public void actionPerformed(ActionEvent event) {
        // figuring out where we are
        TreePath sel = utv.getTreeTable().getSelectedPath();
        UserTaskTreeTableNode n = 
                (UserTaskTreeTableNode) sel.getLastPathComponent();
        UserTaskTreeTableNode parent = (UserTaskTreeTableNode) n.getParent();
        AdvancedTreeTableNode newParent = 
                (AdvancedTreeTableNode) parent.getParent();
        TreePath newParentPath = sel.getParentPath().getParentPath();
        TreePath[] expanded = utv.getTreeTable().getExpandedNodesUnder(
                newParentPath);
        UserTask[] expandedTasks = new UserTask[expanded.length];
        for (int i = 0; i < expanded.length ; i++) {
            if (expanded[i].getLastPathComponent() instanceof
                    UserTaskTreeTableNode) {
                expandedTasks[i] = ((UserTaskTreeTableNode) 
                    expanded[i].getLastPathComponent()).getUserTask();
            }
        }
        utv.getTreeTable().clearSelection();
        int selColumn = utv.getTreeTable().getSelectedColumn();

        // moving the task
        UserTask ut = n.getUserTask();
        int index = ut.getParentObjectList().identityIndexOf(ut);
        while (ut.getParentObjectList().size() > index + 1) {
            ut.getSubtasks().add(ut.getParentObjectList().remove(index + 1));
        }
        ut.getParentObjectList().remove(ut);
        UserTaskObjectList newParentList;
        if (newParent instanceof UserTaskTreeTableNode)
            newParentList = ((UserTaskTreeTableNode) newParent).
                    getUserTask().getSubtasks();
        else
            newParentList = ((UserTaskListTreeTableNode) newParent).
                    getUserTaskList().getSubtasks();
        int parentIndex = newParentList.identityIndexOf(parent.getUserTask());
        newParentList.add(parentIndex + 1, ut);
        
        // expanding and selecting nodes
        index = newParent.getIndexOfObject(ut);
        if (index >= 0) {
            TreePath newPath = newParentPath.pathByAddingChild(
                    newParent.getChildAt(index));
            utv.getTreeTable().expandPath(newPath);
            for (int i = 0; i < expandedTasks.length; i++) {
                if (expandedTasks[i] != null) {
                    TreePath p = utv.getTreeTable().findPath(expandedTasks[i]);
                    if (p != null)
                        utv.getTreeTable().expandPath(p);
                }
            }
            utv.getTreeTable().select(newPath);
            utv.getTreeTable().getColumnModel().getSelectionModel().
                    addSelectionInterval(selColumn, selColumn);
        }
    }

    public void valueChanged(ListSelectionEvent e) {
        TreePath[] sel = utv.getTreeTable().getSelectedPaths();
        if (utv.getTreeTable().getSortingModel().getSortedColumn() == -1 && 
                sel.length == 1) {
            Object last = sel[0].getLastPathComponent();
            if (last instanceof UserTaskTreeTableNode) {
                UserTaskTreeTableNode n = (UserTaskTreeTableNode) last;
                setEnabled(n.getUserTask().getParent() != null);
            } else {
                setEnabled(false);
            }
        } else {
            setEnabled(false);
        }
    }
}
