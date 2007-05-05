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

/*
 * this file is derived from the "Creating TreeTable" article at
 * http://java.sun.com/products/jfc/tsc/articles/treetable2/index.html
 */
package org.netbeans.modules.tasklist.usertasks.treetable;

import java.util.logging.Level;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.tree.TreePath;
import org.netbeans.modules.tasklist.usertasks.util.UTUtils;

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
 * @author tl
 */
public class TreeTableModelAdapter extends AbstractTableModel {

    private static final long serialVersionUID = 1;

    JTree tree;
    TreeTableModel treeTableModel;
    private TreeExpansionListener tel;
    private TreeModelListener tml;
    
    public TreeTableModelAdapter(TreeTableModel treeTableModel, JTree tree) {
        this.tree = tree;
        this.treeTableModel = treeTableModel;
        
        tel = new TreeExpansionListener() {
            // Don't use fireTableRowsInserted() here; the selection model
            // would get updated twice.
            public void treeExpanded(TreeExpansionEvent event) {
                fireTableDataChanged();
                /** this code seems better, but does not work 
                 * (e.g. for Expand All)
                JTree tree = (JTree) event.getSource();
                int row = tree.getRowForPath(event.getPath());
                int n = tree.getModel().getChildCount(
                        event.getPath().getLastPathComponent());
                if (n != 0)
                    fireTableRowsInserted(row + 1, row + n);
                 */
            }
            public void treeCollapsed(TreeExpansionEvent event) {
                JTree tree = (JTree) event.getSource();
                int row = tree.getRowForPath(event.getPath());
                int n = tree.getModel().getChildCount(
                        event.getPath().getLastPathComponent());
                if (n != 0)
                    fireTableRowsDeleted(row + 1, row + n);
            }
        };
        tree.addTreeExpansionListener(tel);
        
        
        tml = new TreeModelListener() {
            public void treeNodesChanged(TreeModelEvent e) {
                TreePath tp = e.getTreePath();
                if (TreeTableModelAdapter.this.tree.isExpanded(tp)) {
                    int firstRow = Integer.MAX_VALUE;
                    int lastRow = Integer.MIN_VALUE;
                    int[] childIndices = e.getChildIndices();
                    Object last = tp.getLastPathComponent();
                    for (int i = 0; i < childIndices.length; i++) {
                        TreePath childPath = tp.pathByAddingChild(
                            TreeTableModelAdapter.this.treeTableModel.getChild(
                            last, childIndices[i]));
                        int row = TreeTableModelAdapter.this.tree.getRowForPath(
                                childPath);
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
                UTUtils.LOGGER.fine("here"); // NOI18N
                delayedFireTableDataChanged();
            }
        };
        
        // Install a TreeModelListener that can update the table when
        // tree changes. We use delayedFireTableDataChanged as we can
        // not be guaranteed the tree will have finished processing
        // the event before us.
        treeTableModel.addTreeModelListener(tml);
    }
    
    /**
     * This method must always be called if this object is removed from a 
     * TreeTable 
     */
    public void unregister() {
        tree.removeTreeExpansionListener(tel);
        treeTableModel.removeTreeModelListener(tml);
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
        if (treePath == null)
            return null;
        else
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
        /*SwingUtilities.invokeLater(new Runnable() {
            public void run() {*/
                fireTableDataChanged();
            /*}
        });*/
    }

    /**
     * Invokes fireTableRowsInserted after all the pending events have been
     * processed. SwingUtilities.invokeLater is used to handle this.
     */
    protected void delayedFireTableRowsInserted(final int first, 
            final int last) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                fireTableRowsInserted(first, last);
            }
        });
    }
    
    /**
     * Invokes fireTableRowsUpdated after all the pending events have been
     * processed. SwingUtilities.invokeLater is used to handle this.
     */
    protected void delayedRowsUpdated(final int firstRow, final int lastRow) {
        /*SwingUtilities.invokeLater(new Runnable() {
            public void run() {*/
                fireTableRowsUpdated(firstRow, lastRow);
            /*}
        });*/
    }
}

