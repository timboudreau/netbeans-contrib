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

package org.netbeans.modules.tasklist.usertasks.treetable;

import javax.swing.tree.DefaultTreeModel;

/**
 * Default model for TreeTable
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
        this.columnNames = columnNames;
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
        return column == 0;
    }
    
    public void setValueAt(Object aValue, Object node, int column) {
    }
}
