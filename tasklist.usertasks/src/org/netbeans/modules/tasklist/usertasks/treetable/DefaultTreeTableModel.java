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

package org.netbeans.modules.tasklist.usertasks.treetable;

import java.util.Arrays;
import java.util.Comparator;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import org.netbeans.modules.tasklist.core.table.SortingModel;

/**
 * Default model for TreeTable
 * 
 * @author tl
 */
public class DefaultTreeTableModel extends DefaultTreeModel implements
TreeTableModel {
    private static final long serialVersionUID = 1;

    private String columnNames[];
    
    /**
     * Creates a tree in which any node can have children.
     *
     * @param root a TreeNode object that is the root of the tree
     * @param columnNames names for columns
     * @see #DefaultTreeModel(TreeNode, boolean)
     */
    public DefaultTreeTableModel(TreeTableNode root, String[] columnNames) {
        super(root);
        this.columnNames = columnNames;
    }

    /**
     * Creates a tree specifying whether any node can have children,
     * or whether only certain nodes can have children.
     *
     * @param root a TreeNode object that is the root of the tree
     * @param askAllowsChildren a boolean, false if any node can
     *        have children, true if each node is asked to see if
     *        it can have children
     * @param columnNames names for columns
     * @see #asksAllowsChildren
     */
    public DefaultTreeTableModel(TreeTableNode root, boolean asksAllowsChildren,
    int columnNumber) {
        super(root, asksAllowsChildren);
        this.columnNames = new String[columnNumber];
        Arrays.fill(columnNames, ""); // NOI18N
    }

    public int getColumnCount() {
        return columnNames.length;
    }
    
    public String getColumnName(int column) {
        return columnNames[column];
    }
    
    public Class getColumnClass(int column) {
        if (column == 0)
            return TreeTableModel.class;
        else
            return Object.class;
    }
    
    public Object getValueAt(Object node, int column) {
        return ((TreeTableNode) node).getValueAt(column);
    }
    
    public boolean isCellEditable(Object node, int column) {
        // CHANGEEDIT
        //return column == 0 || ((TreeTableNode) node).isCellEditable(column);
        return ((TreeTableNode) node).isCellEditable(column);
    }
    
    public void setValueAt(Object aValue, Object node, int column) {
        ((TreeTableNode) node).setValueAt(aValue, column);
    }

    public static class ToStringComparator implements Comparator {
        public int compare(Object obj1, Object obj2) {
            String s1 = (obj1 == null) ? "" : obj1.toString(); // NOI18N
            String s2 = (obj2 == null) ? "" : obj2.toString(); // NOI18N
            if (s1 == null)
                s1 = ""; // NOI18N
            if (s2 == null)
                s2 = ""; // NOI18N
            return s1.compareTo(s2);
        }
    }

    /*
     * Notify all listeners that have registered interest for
     * notification on this event type.  The event instance
     * is lazily created using the parameters passed into
     * the fire method.
     * @see EventListenerList
     */
    public void fireTreeStructureChanged(Object source, Object[] path) {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        TreeModelEvent e = null;
        TreePath tp = path == null ? null : new TreePath(path);
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==TreeModelListener.class) {
                // Lazily create the event:
                if (e == null)
                    e = new TreeModelEvent(source, tp);
                ((TreeModelListener)listeners[i+1]).treeStructureChanged(e);
            }
        }
    }
    
    public void fireTreeNodesRemoved(Object source, Object[] path, 
    int[] childIndices, Object[] children) {
        super.fireTreeNodesRemoved(source, path, childIndices, children);
    }
    
    public void fireTreeNodesInserted(Object source, Object[] path, 
    int[] childIndices, Object[] children) {
        super.fireTreeNodesInserted(source, path, childIndices, children);
    }
    
    public void fireTreeNodesChanged(Object source, Object[] path, 
    int[] childIndices, Object[] children) {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        TreeModelEvent e = null;
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==TreeModelListener.class) {
                // Lazily create the event:
                if (e == null) {
                    TreePath tp = path == null || path.length == 0 ? 
                        null : new TreePath(path);
                    e = new TreeModelEvent(source, tp, 
                                           childIndices, children);
                }
                ((TreeModelListener)listeners[i+1]).treeNodesChanged(e);
            }          
        }
    }
    
    public void fireTreeStructureChanged(Object source, Object[] path, 
    int[] childIndices, Object[] children) {
        super.fireTreeStructureChanged(source, path, childIndices, children);
    }    
}
