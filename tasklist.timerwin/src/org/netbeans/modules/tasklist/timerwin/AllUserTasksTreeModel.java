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

package org.netbeans.modules.tasklist.timerwin;

import javax.swing.tree.TreeModel;
import org.netbeans.modules.tasklist.core.util.ObjectList;
import org.netbeans.modules.tasklist.usertasks.UserTaskView;
import org.netbeans.modules.tasklist.usertasks.UserTaskViewRegistry;
import org.netbeans.modules.tasklist.usertasks.model.UserTaskList;

/**
 * TreeModel for user tasks
 *
 * @author tl
 */
public class AllUserTasksTreeModel implements TreeModel {
    private Object root = new Object();
    private UserTaskList[] utl;
    
    /**
     * Creates a new model for a task list.
     *
     * @param utl a task list
     */
    public AllUserTasksTreeModel() {
        UserTaskView[] all = UserTaskViewRegistry.getInstance().getAll();
        utl = new UserTaskList[all.length];
        for (int i = 0; i < all.length; i++) {
            utl[i] = all[i].getUserTaskList();
        }
    }

    public boolean isLeaf(Object node) {
        if (node == root)
            return false;
        else
            return ((ObjectList.Owner) node).getObjectList().size() == 0;
    }

    public int getChildCount(Object parent) {
        if (parent == root)
            return utl.length;
        else
            return ((ObjectList.Owner) parent).getObjectList().size();
    }

    public void valueForPathChanged(javax.swing.tree.TreePath path, Object newValue) {
    }

    public void removeTreeModelListener(javax.swing.event.TreeModelListener l) {
    }

    public void addTreeModelListener(javax.swing.event.TreeModelListener l) {
    }

    public Object getChild(Object parent, int index) {
        if (parent == root)
            return utl[index];
        else
            return ((ObjectList.Owner) parent).getObjectList().get(index);
    }

    public Object getRoot() {
        return root;
    }

    public int getIndexOfChild(Object parent, Object child) {
        return -1;
    }
}
