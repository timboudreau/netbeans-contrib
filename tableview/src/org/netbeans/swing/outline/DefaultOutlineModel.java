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
import java.util.Arrays;
import java.util.Enumeration;
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
    //Some constants we use to have a single method handle all translated
    //event firing
    private static final int NODES_CHANGED = 0;
    private static final int NODES_INSERTED = 1;
    private static final int NODES_REMOVED = 2;
    private static final int STRUCTURE_CHANGED = 3;
    
    //XXX deleteme - string version of the avoid constants debug output:
    private static final String[] types = new String[] {
        "nodesChanged", "nodesInserted", "nodesRemoved", "structureChanged"
    };
    
    
    
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
        //Create a new TreeModelEvent with us as the source
        TreeModelEvent nue = new TreeModelEvent (this, e.getPath(), 
            e.getChildIndices(), e.getChildren());
        
        //Fetch the listeners, etc.
        TreeModelListener[] listeners = new TreeModelListener[treeListeners.size()];
        listeners = (TreeModelListener[]) treeListeners.toArray(listeners);

        //If it's a structural change, we need to dump all our info about the
        //existing tree structure - it can be bogus now.  Similar to JTree,
        //this will have the effect of collapsing all expanded paths.  The
        //TreePathSupport takes care of dumping the layout cache's copy of
        //such data
        if (type == STRUCTURE_CHANGED) {
            getTreePathSupport().clear();
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
        fireTranslatedEvent(nue, type);
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
    
    /** A listener which receives events from the wrapped TableModel and TreeModel,
     * updates the layout cache and TreePathSupport's expanded path data as needed,
     * then refires translated events from this DefaultOutlineModel */
    private class Listener implements TableModelListener, TreeModelListener, ExtTreeWillExpandListener, TreeExpansionListener {
        
        public void tableChanged(javax.swing.event.TableModelEvent e) {
            //The *ONLY* time we should see events here is due to user
            //data entry.  The ProxyTableModel should never change out
            //from under us - all structural changes happen through the
            //table model.
            
            //TODO: add assertion failure if we get any structure change events from
            //the table model
            
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
            System.err.println("\nTreeNodesRemoved");
            
            TreePath path = e.getTreePath();
            lastRemoveWasExpanded = getTreePathSupport().isExpanded(path);

            if (lastRemoveWasExpanded) {
                Object[] kids = e.getChildren();
                countRemoved = kids.length;
                for (int i=0; i < kids.length; i++) {
                    TreePath childPath = path.pathByAddingChild(kids[i]);
                    if (getTreePathSupport().isExpanded(childPath)) {
                        int visibleChildren = getLayout().getVisibleChildCount(childPath);
                        System.err.println("Adding in " + visibleChildren + " on " + childPath);
                        countRemoved += visibleChildren;
                    }
                }
            } else {
                countRemoved = 1;
            }
            
            /*
            
            System.err.println("Removed path " + path + " children " + Arrays.asList(e.getChildren()) + " indices " + Arrays.asList(org.openide.util.Utilities.toObjectArray(e.getChildIndices())));
            
            if (lastRemoveWasExpanded) {
                Enumeration en = getLayout().getVisiblePathsFrom(path);
                if (en != null) {
                    while (en.hasMoreElements()) {
                        TreePath chPath = (TreePath) en.nextElement();
                        if (chPath.isDescendant(path)) {
                            System.err.println("ChildPath: " + chPath);
                            countRemoved += 1;
                        }
                    }
                    System.err.println("Count removed: " + countRemoved);
                } else {
                    countRemoved = e.getChildren().length;
                }
            } else {
                //countRemoved won't actually be used for this
                countRemoved = 1;
            }
             */
            
            /*
            countRemoved = e.getChildren().length;
            //Count the number of descendant nodes that are being removed
            TreePath[] paths = treePathSupport.getExpandedDescendants(
                path);
            
            for (int i=0; i < paths.length; i++) {
                if (paths[i].isDescendant(path)) {
                    int indirectlyRemoved = getLayout().getVisibleChildCount(paths[i]);
                    System.err.println("Indirectly removed " + indirectlyRemoved + " on " + paths[i]);
                    countRemoved += indirectlyRemoved;
                }
            }
            
            System.err.println("TreeNodesRemoved " + path + " direct children removed: " + e.getChildren().length + " total removed: " + countRemoved);
            
             */
            
            getLayout().treeNodesRemoved(e);
            System.err.println("Refiring Tree nodes removed" + e);
            fireTreeChange (e, NODES_REMOVED);
        }
        
        public void treeStructureChanged(TreeModelEvent e) {
            getLayout().treeStructureChanged(e);
            fireTreeChange (e, STRUCTURE_CHANGED);
        }
        
        public void treeCollapsed(TreeExpansionEvent event) {
	    if(event != null) {
		TreePath path = event.getPath();

		if(path != null && getTreePathSupport().isVisible(path)) {
		    getLayout().setExpandedState(path, false);
		}
	    }
            fireTableChange (pendingExpansionEvent);
            pendingExpansionEvent = null;
            inProgressEvent = null;
        }
        
        protected void updateExpandedDescendants(TreePath path) {
            getLayout().setExpandedState(path, true);

            TreePath[] descendants = 
                getTreePathSupport().getExpandedDescendants(path);

            if(descendants.length > 0) {
                for (int i=0; i < descendants.length; i++) {
                    getLayout().setExpandedState(descendants[i], true);
                }
            }
        }        
        
        public void treeExpanded(TreeExpansionEvent event) {
            //Mysterious how the event could be null, but JTree tests it
            //so we will too.
	    if(event != null) {
		updateExpandedDescendants(event.getPath());
	    }
            
            fireTableChange (pendingExpansionEvent);
            pendingExpansionEvent = null;
            inProgressEvent = null;
        }
        
        private TableModelEvent pendingExpansionEvent = null;
        private TreeExpansionEvent inProgressEvent = null;
        public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {
            //Construct the TableModelEvent here, before data structures have
            //changed
            pendingExpansionEvent = translateEvent (event, false);
            inProgressEvent = event;
        }
        
        public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {
            //Construct the TableModelEvent here, before data structures have
            //changed
            pendingExpansionEvent = translateEvent (event, true);
            inProgressEvent = event;
        }

        /** Implementation of ExtTreeWillExpandListener - simply removes references
         * held in the events constructed in treeWillExpand/treeWillCollapse to
         * avoid memory leaks */
        public void treeExpansionVetoed(TreeExpansionEvent event, ExpandVetoException exception) {
            //Make sure the event that was vetoed is the one we're interested in
            if (event == inProgressEvent) {
                pendingExpansionEvent = null;
                inProgressEvent = null;
            }
        }        
    }
    
    int countRemoved = 0;
    private boolean lastRemoveWasExpanded = false;
    
    
    private void fireTranslatedEvent (TreeModelEvent e, int type) {
        TableModelEvent event = translateEvent (e, type);
        if (event != null) {
            fireTableChange(event);
        }
    }

    
    /** Translates a TreeExpansionEvent into a precise TableModelEvent indicating
     * the type of change and the rows affected */
    private TableModelEvent translateEvent (TreeExpansionEvent e, boolean expand) {
        //PENDING:  This code should be profiled - the descendent paths search
        //is not cheap, and it might be less expensive (at least if the table
        //does not have expensive painting logic) to simply fire a generic
        //"something changed" table model event and be done with it.
        
        TreePath path = e.getPath();
        
        int firstRow = getLayout().getRowForPath(path);
        if (firstRow == -1) {
            //This does not mean nothing happened, it may just be that we are
            //a large model tree, and the FixedHeightLayoutCache says the
            //change happened in a row that is not showing.
            
            //TODO:  Just to make the table scrollbar adjust itself appropriately,
            //we may want to look up the number of children in the model and
            //fire an event that says that that many rows were added.  Waiting
            //to see if anybody actually will use this (i.e. fires changes in
            //offscreen nodes as a normal part of usage
            return null;
        }
        
        //Get all the expanded descendants of the path that was expanded/collapsed
        TreePath[] paths = getTreePathSupport().getExpandedDescendants(path);
        
        //Start with the number of children of whatever was expanded/collapsed
        int count = treeModel.getChildCount(path.getLastPathComponent());
        
        //Iterate any of the expanded children, adding in their child counts
        for (int i=0; i < paths.length; i++) {
            count += treeModel.getChildCount(paths[i].getLastPathComponent());
        }
        
        //Now we can calculate the last row affected for real
        int lastRow = firstRow + count;
        
        //Construct a table model event reflecting this data
        TableModelEvent result = new TableModelEvent (this, firstRow, lastRow, 
            TableModelEvent.ALL_COLUMNS, expand ? TableModelEvent.INSERT : 
            TableModelEvent.DELETE);
            
        return result;
    }
    
    /** Translates a TreeModelEvent into an appropriate TableModelEvent that 
     * indicates the affected rows and columns.  */
    private TableModelEvent translateEvent (TreeModelEvent e, int type) {
        System.err.println("TRANSLATE EVENT");
        TreePath path = e.getTreePath();
//        TreePath[] paths = getTreePathSupport().getExpandedDescendants(path);
        int row = getLayout().getRowForPath(path);
        System.err.println("Row for path is " + row);
        if (row == -1) {
            System.err.println("Get Row for path was -1 " + path);
            //XXX again, we may want to really do *something* here - in a 
            //large model table, the row will be -1 if the path is not
            //showing onscreen right now, but we might want to cause the
            //table to at least update its scrollbar.
            return null;
        }
        
        switch (type) {
            case NODES_CHANGED :
                System.err.println("Change - returning a change event for row " + row);
                return new TableModelEvent (this, row);
            case NODES_INSERTED :
                System.err.println("Nodes inserted");
                boolean realInsert = getLayout().isExpanded(path);
                if (realInsert) {
                    System.err.println("The parent is open - real insert");
                    int[] indices = e.getChildIndices();
                    Arrays.sort(indices);
                    if (indices.length == 0) {
                        return null;
                    } else if (indices.length == 1) {
                        return new TableModelEvent (this, row + 1, row + 1, 
                            TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT);
                    } else {
                        int lowest = indices[0];
                        int highest = indices[indices.length-1];
                        return new TableModelEvent (this, row + lowest, row + highest,
                            TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT);
                    }
                } else {
                    System.err.println("The parent is closed - change for row " + row);
                    //Nodes were inserted in an unexpanded parent.  Just fire
                    //a change for that row and column so that it gets repainted
                    //in case the node there changed from leaf to non-leaf
                    return new TableModelEvent (this, row, row, 0); //XXX 0 may not be tree column
                }

            case NODES_REMOVED :
                boolean realRemove = lastRemoveWasExpanded;//getLayout().isExpanded(path);
                if (realRemove) {
                    System.err.println("Nodes removed from open countainer");
                    int[] indices = e.getChildIndices();
                    
                    //Comments in FixedHeightLayoutCache suggest we cannot
                    //assume array is sorted, though it should be
                    Arrays.sort(indices);
                    if (indices.length == 0) {
                        //well, that's a little weird
                        return null;
                    } else if (countRemoved == 1) {
                        System.err.println("Only one removed: " + (row + indices[0] + 1));
                        return new TableModelEvent (this, row + indices[0] + 1,
                            row + indices[0] + 1, TableModelEvent.ALL_COLUMNS, 
                            TableModelEvent.DELETE);
                    }
                    System.err.println("Count removed is " + countRemoved);
                    
                    //Add in the first index, and add one to it since the 0th
                    //will have the row index of its parent + 1
                    int firstRow = row + indices[0] + 1;
                    int lastRow = firstRow + countRemoved;
                    
                    System.err.println("TableModelEvent: fromRow: " + firstRow + " toRow: " + lastRow);
                     
                    return new TableModelEvent (this, firstRow, lastRow,
                        TableModelEvent.ALL_COLUMNS, TableModelEvent.DELETE);
                } else {
                    System.err.println("Nodes removed from a closed container. Change for row " + row);
                    //Nodes were removed in an unexpanded parent.  Just fire
                    //a change for that row and column so that it gets repainted
                    //in case the node there changed from leaf to non-leaf
                    TableModelEvent evt = new TableModelEvent (this, row, row, 0); //XXX 0 may not be tree column
                    System.err.println(" Returning " + evt);
                    return evt;
                }

            case STRUCTURE_CHANGED :
                return new TableModelEvent(this);
            default :
                assert false;
        }        
        
        //Could conceivably reach here if something was very wrong and assertions
        //were off.  Fire a generic change and hope it's recoverable.
        TableModelEvent result = new TableModelEvent (this);
        return result;
    }
    
}
