/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
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
