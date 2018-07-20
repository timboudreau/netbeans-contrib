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
