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
 * DefaultOutlineModel.java
 *
 * Created on January 27, 2004, 6:58 PM
 */

package org.netbeans.swing.outline;

import java.util.ArrayList;
import java.util.List;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.table.TableModel;
import javax.swing.tree.AbstractLayoutCache;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.FixedHeightLayoutCache;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.VariableHeightLayoutCache;

/** Proxies a standard TreeModel and TableModel, translating events between
 * the two.
 *
 * @author  Tim Boudreau
 */
public class DefaultOutlineModel implements OutlineModel {
    private TreeModel treeModel;
    private TableModel tableModel;
    private AbstractLayoutCache layout;
    private TreePathSupport treePathSupport;
    private Listener listener;
    
    /** Create a small model OutlineModel using the supplied tree model and row model 
     * @param treeModel The tree model that is the data model for the expandable
     *  tree column of an Outline
     * @param rowModel The row model which will supply values for each row based
     *  on the tree node in that row in the tree model
     */
    public static OutlineModel createOutlineModel(TreeModel treeModel, RowModel rowModel) {
        return createOutlineModel (treeModel, rowModel, false);
    }

    /** Create an OutlineModel using the supplied tree model and row model,
     * specifying if it is a large-model tree */
    public static OutlineModel createOutlineModel(TreeModel treeModel, RowModel rowModel, boolean isLargeModel) {
        TableModel tableModel = new ProxyTableModel(rowModel);
        return new DefaultOutlineModel (treeModel, tableModel, isLargeModel);
    }
    
    /** Creates a new instance of DefaultOutlineModel.  <strong><b>Note</b> 
     * Do not fire table structure changes from the wrapped TableModel (value
     * changes are okay).  Changes that affect the number of rows must come
     * from the TreeModel.   */
    protected DefaultOutlineModel(TreeModel treeModel, TableModel tableModel, boolean largeModel) {
        this.treeModel = treeModel;
        this.tableModel = tableModel;
        
        layout = largeModel ? (AbstractLayoutCache) new FixedHeightLayoutCache() 
            : (AbstractLayoutCache) new VariableHeightLayoutCache();
            
        layout.setRootVisible(true);
        layout.setModel(this);
        listener = new Listener();
        treePathSupport = new TreePathSupport(this, layout);
        treePathSupport.addTreeExpansionListener(listener);
        treePathSupport.addTreeWillExpandListener(listener);
        treeModel.addTreeModelListener(listener);
        tableModel.addTableModelListener(listener);
        if (tableModel instanceof ProxyTableModel) {
            ((ProxyTableModel) tableModel).setOutlineModel(this);
        }
    }
    
    public final TreePathSupport getTreePathSupport() {
        return treePathSupport;
    }    
    
    public final AbstractLayoutCache getLayout() {
        return layout;
    }
    
    public Object getChild(Object parent, int index) {
        return treeModel.getChild (parent, index);
    }
    
    public int getChildCount(Object parent) {
        return treeModel.getChildCount (parent);
    }
    
    public Class getColumnClass(int columnIndex) {
        if (columnIndex == 0) {
            return Object.class;
        } else {
            return tableModel.getColumnClass(columnIndex-1);
        }
    }
    
    public int getColumnCount() {
        return tableModel.getColumnCount()+1;
    }
    
    public String getColumnName(int columnIndex) {
        if (columnIndex == 0) {
            return "Nodes"; //XXX
        } else {
            return tableModel.getColumnName(columnIndex-1);
        }
    }
    
    public int getIndexOfChild(Object parent, Object child) {
        return treeModel.getIndexOfChild(parent, child);
    }
    
    public Object getRoot() {
        return treeModel.getRoot();
    }
    
    public int getRowCount() {
        return getLayout().getRowCount();
    }
    
    public Object getValueAt(int rowIndex, int columnIndex) {
        Object result;
        if (columnIndex == 0) { //XXX need a column ID - columnIndex = 0 depends on the column model
            TreePath path = getLayout().getPathForRow(rowIndex);
            if (path != null) {
                result = path.getLastPathComponent();
            } else {
                result = null;
            }
        } else {
            result = (tableModel.getValueAt(rowIndex, columnIndex -1));
        }
        return result;
    }
    
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        if (columnIndex == 0) {
            return false; //XXX
        } else {
            return tableModel.isCellEditable(rowIndex, columnIndex-1);
        }
    }
    
    public boolean isLeaf(Object node) {
        return treeModel.isLeaf(node);
    }
    
    private List tableListeners = new ArrayList();
    private List treeListeners = new ArrayList();
    public synchronized void addTableModelListener(TableModelListener l) {
        tableListeners.add (l);
    }
    
    public synchronized void addTreeModelListener(TreeModelListener l) {
        treeListeners.add (l);
    }    
    
    public synchronized void removeTableModelListener(TableModelListener l) {
        tableListeners.remove(l);
    }
    
    public synchronized void removeTreeModelListener(TreeModelListener l) {
        treeListeners.remove(l);
    }
    
    private synchronized void fireTableChange (TableModelEvent e) {
        TableModelListener[] listeners = new TableModelListener[tableListeners.size()];
        listeners = (TableModelListener[]) tableListeners.toArray(listeners);
        for (int i=0; i < listeners.length; i++) {
//            System.err.println("DefaultOutlineModel firing change to " + listeners[i]);
            listeners[i].tableChanged(e);
        }
    }
    
    private synchronized void fireTreeChange (TreeModelEvent e, int type) {
        TreeModelEvent nue = new TreeModelEvent (this, e.getPath(), e.getChildIndices(), e.getChildren());
        
        TreeModelListener[] listeners = new TreeModelListener[treeListeners.size()];
        listeners = (TreeModelListener[]) treeListeners.toArray(listeners);

        //If it's a structural change, we need to dump all our info about the
        //existing tree structure.
        if (type == STRUCTURE_CHANGED) {
            getTreePathSupport().clear();
//            getLayout().treeStructureChanged(nue);
        }
        
        //Now refire it to any listeners
        for (int i=0; i < listeners.length; i++) {
            switch (type) {
                case NODES_CHANGED :
                    listeners[i].treeNodesChanged(nue);
                    break;
                case NODES_INSERTED :
                    listeners[i].treeNodesInserted(nue);
                    break;
                case NODES_REMOVED :
                    listeners[i].treeNodesRemoved(nue);
                    break;
                case STRUCTURE_CHANGED :
                    listeners[i].treeStructureChanged(nue);
                    break;
                default :
                    assert false;
            }
        }
        
        //Now fire the table change
        TableModelEvent tme = translateEvent (nue, type);
        fireTableChange(tme);
    }
    
    private TableModelEvent translateEvent (TreeModelEvent e, int type) {
        //XXX for now, just make all events structure changes, refine it later
        TableModelEvent result = new TableModelEvent (this);
        return result;
    }
    
    
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        tableModel.setValueAt (aValue, rowIndex, columnIndex-1);
    }
    
    public void valueForPathChanged(javax.swing.tree.TreePath path, Object newValue) {
        treeModel.valueForPathChanged(path, newValue);
    }

    public boolean isLargeModel() {
        return layout instanceof FixedHeightLayoutCache;
    }
    
    private static final int NODES_CHANGED = 0;
    private static final int NODES_INSERTED = 1;
    private static final int NODES_REMOVED = 2;
    private static final int STRUCTURE_CHANGED = 3;
    
    //XXX deleteme - just for printing strings when debugging:
    private static final String[] types = new String[] {
        "nodesChanged", "nodesInserted", "nodesRemoved", "structureChanged"
    };
    
    private class Listener implements TableModelListener, TreeModelListener, TreeWillExpandListener, TreeExpansionListener {
        
        public void tableChanged(javax.swing.event.TableModelEvent e) {
            //XXX probably we don't want to rebroadcast these, just
            //translate them - the table model should not be changing
            //out from under us.
            
            //Create a translated table model event
            TableModelEvent nue = new TableModelEvent (DefaultOutlineModel.this,
                e.getFirstRow(), e.getLastRow(), e.getColumn()+1, e.getType());
            //fire it
            fireTableChange (nue);
        }
        
        public void treeNodesChanged(TreeModelEvent e) {
            getLayout().treeNodesChanged(e);
            fireTreeChange (e, NODES_CHANGED);
        }
        
        public void treeNodesInserted(TreeModelEvent e) {
            getLayout().treeNodesInserted(e);
            fireTreeChange (e, NODES_INSERTED);
        }
        
        public void treeNodesRemoved(TreeModelEvent e) {
            getLayout().treeNodesRemoved(e);
            System.err.println("Refiring Tree nodes removed" + e);
            fireTreeChange (e, NODES_REMOVED);
        }
        
        public void treeStructureChanged(TreeModelEvent e) {
            getLayout().treeStructureChanged(e);
            fireTreeChange (e, STRUCTURE_CHANGED);
        }
        
        public void treeCollapsed(TreeExpansionEvent event) {
//            System.err.println("TreeCollapsed: " + event);
	    if(event != null) {
		TreePath path = event.getPath();

//		completeEditing();
		if(path != null && getTreePathSupport().isVisible(path)) {
		    getLayout().setExpandedState(path, false);
                    /*
		    updateLeadRow();
		    updateSize();
                     */
		}
	    }
            fireCheesyEvent();
        }
        
        protected void updateExpandedDescendants(TreePath path) {
            //completeEditing();
                getLayout().setExpandedState(path, true);

                TreePath[] descendants = 
                    getTreePathSupport().getExpandedDescendants(path);

                if(descendants.length > 0) {
                    for (int i=0; i < descendants.length; i++) {
                        getLayout().setExpandedState(descendants[i], true);
                    }
                }
    //	    updateLeadRow();
    //	    updateSize();
        }        
        
        public void treeExpanded(TreeExpansionEvent event) {
//            System.err.println("TreeExpanded: " + event);
            //Mysterious how the event could be null, but JTree tests it
            //so we will too.
	    if(event != null) {
		updateExpandedDescendants(event.getPath());
	    }
            
            fireCheesyEvent();
        }
        
        public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {
//            System.err.println("TreeWillCollapse: " + event);
            
            fireCheesyEvent();
        }
        
        public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {
//            System.err.println("TreeWillExpand: " + event);
            
            fireCheesyEvent();
        }

        /** A standin for real translation of tree events into granular table 
         * model events.  This just fires an expensive "something changed and
         * I'm not telling you what" event. */
        private void fireCheesyEvent() {
            TableModelEvent tme = new TableModelEvent (DefaultOutlineModel.this);
            DefaultOutlineModel.this.fireTableChange(tme);
        }
        
    }
    

    
}
