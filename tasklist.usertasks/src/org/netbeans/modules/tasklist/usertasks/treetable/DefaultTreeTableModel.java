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
