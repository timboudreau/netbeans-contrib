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

/*
 * this file is derived from the "Creating TreeTable" article at
 * http://java.sun.com/products/jfc/tsc/articles/treetable2/index.html
 */
package org.netbeans.modules.tasklist.usertasks.treetable;

import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.tree.TreePath;

/**
 * This is a wrapper class takes a TreeTableModel and implements
 * the table model interface. The implementation is trivial, with
 * all of the event dispatching support provided by the superclass:
 * the AbstractTableModel.
 *
 * @version 1.2 10/27/98
 *
 * @author Philip Milne
 * @author Scott Violet
 */
public class TreeTableModelAdapter extends AbstractTableModel {

    private static final long serialVersionUID = 1;

    JTree tree;
    TreeTableModel treeTableModel;
    
    public TreeTableModelAdapter(TreeTableModel treeTableModel, JTree tree) {
        this.tree = tree;
        this.treeTableModel = treeTableModel;
        
        tree.addTreeExpansionListener(new TreeExpansionListener() {
            // Don't use fireTableRowsInserted() here; the selection model
            // would get updated twice.
            public void treeExpanded(TreeExpansionEvent event) {
                fireTableDataChanged();
            }
            public void treeCollapsed(TreeExpansionEvent event) {
                fireTableDataChanged();
            }
        });
        
        // Install a TreeModelListener that can update the table when
        // tree changes. We use delayedFireTableDataChanged as we can
        // not be guaranteed the tree will have finished processing
        // the event before us.
        treeTableModel.addTreeModelListener(new TreeModelListener() {
            public void treeNodesChanged(TreeModelEvent e) {
                TreePath tp = e.getTreePath();
                if (TreeTableModelAdapter.this.tree.isExpanded(tp)) {
                    int firstRow = Integer.MAX_VALUE;
                    int lastRow = Integer.MIN_VALUE;
                    int[] childIndices = e.getChildIndices();
                    Object last = tp.getLastPathComponent();
                    for (int i = 0; i < childIndices.length; i++) {
                        TreePath childPath = tp.pathByAddingChild(
                            TreeTableModelAdapter.this.treeTableModel.getChild(last, childIndices[i]));
                        int row = TreeTableModelAdapter.this.tree.getRowForPath(childPath);
                        if (row < firstRow) 
                            firstRow = row;
                        if (row > lastRow)    
                            lastRow = row;
                    }
                    delayedRowsUpdated(firstRow, lastRow);
                }
            }
            
            public void treeNodesInserted(TreeModelEvent e) {
                delayedFireTableDataChanged();
            }
            
            public void treeNodesRemoved(TreeModelEvent e) {
                delayedFireTableDataChanged();
            }
            
            public void treeStructureChanged(TreeModelEvent e) {
                delayedFireTableDataChanged();
            }
        });
    }
    
    // Wrappers, implementing TableModel interface.
    
    public int getColumnCount() {
        return treeTableModel.getColumnCount();
    }
    
    public String getColumnName(int column) {
        return treeTableModel.getColumnName(column);
    }
    
    public Class getColumnClass(int column) {
        return treeTableModel.getColumnClass(column);
    }
    
    public int getRowCount() {
        int r = tree.getRowCount();
        return r;
    }
    
    protected Object nodeForRow(int row) {
        TreePath treePath = tree.getPathForRow(row);
        return treePath.getLastPathComponent();
    }
    
    public Object getValueAt(int row, int column) {
        Object node = nodeForRow(row);
        if (node == null)
            return null;
        return treeTableModel.getValueAt(node, column);
    }
    
    public boolean isCellEditable(int row, int column) {
        return treeTableModel.isCellEditable(nodeForRow(row), column);
    }
    
    public void setValueAt(Object value, int row, int column) {
        treeTableModel.setValueAt(value, nodeForRow(row), column);
    }
    
    /**
     * Invokes fireTableDataChanged after all the pending events have been
     * processed. SwingUtilities.invokeLater is used to handle this.
     */
    protected void delayedFireTableDataChanged() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                fireTableDataChanged();
            }
        });
    }

    /**
     * Invokes fireTableRowsUpdated after all the pending events have been
     * processed. SwingUtilities.invokeLater is used to handle this.
     */
    protected void delayedRowsUpdated(final int firstRow, final int lastRow) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                fireTableRowsUpdated(firstRow, lastRow);
            }
        });
    }
}

