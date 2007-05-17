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
import org.netbeans.modules.tasklist.usertasks.table.UTTreeTableNode;
import org.netbeans.modules.tasklist.usertasks.table.UTListTreeTableNode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.swing.tree.TreeNode;

import javax.swing.tree.TreePath;

import org.netbeans.modules.tasklist.usertasks.treetable.AdvancedTreeTableNode;
import org.netbeans.modules.tasklist.usertasks.treetable.AdvancedTreeTableNode;
import org.netbeans.modules.tasklist.usertasks.treetable.DefaultMutableTreeTableNode;
import org.netbeans.modules.tasklist.usertasks.treetable.FilterIntf;
import org.netbeans.modules.tasklist.usertasks.treetable.NotComparator;
import org.netbeans.modules.tasklist.core.table.SortingModel;
import org.netbeans.modules.tasklist.filter.Filter;
import org.netbeans.modules.tasklist.usertasks.model.UserTask;
import org.netbeans.modules.tasklist.usertasks.model.UserTaskList;
import org.netbeans.modules.tasklist.usertasks.table.UTBasicTreeTableModel;

/**
 * TT model for user tasks
 * 
 * @author tl
 */
public class UTTreeTableModel extends UTBasicTreeTableModel {
    /**
     * Creates a new instance of UserTasksTreeTableModel
     *
     * @param utl a task list
     * @param sm a sorting model
     * @param filter a filter
     */
    public UTTreeTableModel(UserTaskList utl, SortingModel sm, 
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
        this.root = new UTListTreeTableNode(fi, this, utl);
    }
    
    public void destroy() {
        super.destroy();
        ((AdvancedTreeTableNode) root).destroy();
    }

    @Override
    protected void sortingModelChanged() {
        final int column = sortingModel.getSortedColumn();
        Comparator cmp;
        if (column >= 0) {
            cmp = new Comparator<AdvancedTreeTableNode>() {
                public int compare(AdvancedTreeTableNode o1, 
                        AdvancedTreeTableNode o2) {
                    Object v1 = o1.getValueAt(column);
                    Object v2 = o2.getValueAt(column);
                    return COMPARATORS[column].compare(v1, v2);
                }
            };
            if (sortingModel.isSortOrderDescending())
                cmp = new NotComparator(cmp);
        } else
            cmp = null;
        ((AdvancedTreeTableNode) root).setComparator(cmp);
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
    
    /**
     * Sorts the tree according to the specified sorting model
     */
    public void sort(SortingModel sm) {
        final int sortedColumn = sm.getSortedColumn();
        if (sortedColumn == -1) {
            ((UTListBasicTreeTableNode) getRoot()).setComparator(null);
            return;
        }
        
        @SuppressWarnings("unchecked")
        Comparator<Object> c = COMPARATORS[sortedColumn];
        if (c == null)
            return;
        
        final Comparator<Object> c2;
        if (!sm.isSortOrderDescending())
            c2 = new NotComparator<Object>(c);
        else
            c2 = c;
        
        Comparator<AdvancedTreeTableNode> comparator = 
                new Comparator<AdvancedTreeTableNode>() {
            public int compare(AdvancedTreeTableNode obj1, 
                    AdvancedTreeTableNode obj2) {
                UTBasicTreeTableNode n1 = (UTTreeTableNode) obj1;
                UTBasicTreeTableNode n2 = (UTTreeTableNode) obj2;
                return c2.compare(
                    n1.getValueAt(sortedColumn), n2.getValueAt(sortedColumn));
            }
        };

        ((UTListBasicTreeTableNode) getRoot()).setComparator(comparator);
    }
}
