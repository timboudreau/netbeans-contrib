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

package org.netbeans.modules.tasklist.usertasks.dependencies;

import javax.swing.tree.TreeModel;
import org.netbeans.modules.tasklist.usertasks.model.UserTask;
import org.netbeans.modules.tasklist.usertasks.model.UserTaskList;
import org.netbeans.modules.tasklist.usertasks.util.ObjectList;

/**
 * TreeModel for user tasks
 *
 * @author tl
 */
public class UserTaskTreeModel implements TreeModel {
    private UserTaskList utl;

    /**
     * Creates a new model for a task list.
     *
     * @param utl a task list
     */
    public UserTaskTreeModel(UserTaskList utl) {
        this.utl = utl;
    }

    public boolean isLeaf(Object node) {
        return extractObjectList(node).size() == 0;
    }

    public int getChildCount(Object parent) {
        return extractObjectList(parent).size();
    }

    public void valueForPathChanged(javax.swing.tree.TreePath path, Object newValue) {
    }

    public void removeTreeModelListener(javax.swing.event.TreeModelListener l) {
    }

    public void addTreeModelListener(javax.swing.event.TreeModelListener l) {
    }

    public Object getChild(Object parent, int index) {
        return extractObjectList(parent).get(index);
    }

    public Object getRoot() {
        return utl;
    }

    public int getIndexOfChild(Object parent, Object child) {
        return -1;
    }
    
    /**
     * Extracts children from a node.
     * 
     * @param node a node 
     */
    private ObjectList extractObjectList(Object node) {
        if (node instanceof UserTaskList)
            return ((UserTaskList) node).getSubtasks();
        else
            return ((UserTask) node).getSubtasks();
    }
}
