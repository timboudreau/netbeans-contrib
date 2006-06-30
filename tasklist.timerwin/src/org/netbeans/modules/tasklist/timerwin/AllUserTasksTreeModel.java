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
