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

package org.netbeans.modules.tasklist.usertasks.table;

import org.netbeans.modules.tasklist.usertasks.treetable.AdvancedTreeTableNode;
import org.netbeans.modules.tasklist.usertasks.treetable.AdvancedTreeTableNode;
import org.netbeans.modules.tasklist.usertasks.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.tree.TreeNode;

import javax.swing.tree.TreePath;

import org.netbeans.modules.tasklist.usertasks.treetable.AdvancedTreeTableNode;
import org.netbeans.modules.tasklist.usertasks.treetable.DefaultMutableTreeTableNode;
import org.netbeans.modules.tasklist.usertasks.treetable.FilterIntf;
import org.netbeans.modules.tasklist.core.table.SortingModel;
import org.netbeans.modules.tasklist.filter.Filter;
import org.netbeans.modules.tasklist.usertasks.model.UserTask;
import org.netbeans.modules.tasklist.usertasks.model.UserTaskList;

/**
 * Flat model for user tasks.
 * 
 * @author tl
 */
public class UTFlatTreeTableModel extends UTBasicTreeTableModel {
    /**
     * Creates a new instance of UserTasksTreeTableModel
     *
     * @param root root node
     * @param sm a sorting model
     * @param filter a filter
     */
    public UTFlatTreeTableModel(UserTaskList utl, SortingModel sm, 
    final Filter filter) {
        super(utl, sm);
        
        FilterIntf fi = null;
        if (filter != null) {
            fi = new FilterIntf() {
                public boolean accept(Object obj) {
                    if (obj instanceof UserTask) {
                        return filter.accept((UserTask) obj);
                    } else {
                        return true;
                    }                
                }
            };
        }
        this.root = new UTListFlatTreeTableNode(fi, this, utl, null, sm);
        //this.root = new UTListDependenciesTreeTableNode(fi, this, utl, null);
    }
    
    public void destroy() {
        super.destroy();
        ((AdvancedTreeTableNode) root).destroy();
    }

    /**
     * Finds the path to the specified user task
     *
     * @param a user task from this model
     * @return path to the task or null
     */
    public TreePath findPathTo(UserTask ut) {
        List<UserTask> path = new ArrayList<UserTask>();
        while (ut != null) {
            path.add(0, ut);
            ut = ut.getParent();
        }
        List<TreeNode> retp = new ArrayList<TreeNode>();
        retp.add((TreeNode) getRoot());
        
        DefaultMutableTreeTableNode n = (DefaultMutableTreeTableNode) getRoot();
        for(int i = 0; i < path.size(); i++) {
            Object obj = path.get(i);
            boolean found = false;
            for (int j = 0; j < n.getChildCount(); j++) {
                if (((DefaultMutableTreeTableNode) n.getChildAt(j)).
                    getUserObject() == obj) {
                    found = true;
                    retp.add(n);
                    break;
                }
            }
            if (!found)
                return null;
        }
        
        return new TreePath(retp.toArray());
    }
}
