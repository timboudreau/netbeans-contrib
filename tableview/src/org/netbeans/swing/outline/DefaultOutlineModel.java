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
 * the two.  Note that the constructor is not public;  the TableModel that is
 * proxied is the OutlineModel's own.  To make use of this class, implement
 * RowModel - that is a mini-table model in which the TreeModel is responsible
 * for defining the set of rows; it is passed an object from the tree, which
 * it may use to generate values for the other columns.  Pass that and the
 * TreeModel you want to use to <code>createOutlineModel</code>.
 * <p>
 * A note on TableModelEvents produced by this model:  There is a slight 
 * impedance mismatch between TableModelEvent and TreeModelEvent.  When the
 * tree changes, it is necessary to fire TableModelEvents to update the display.
 * However, TreeModelEvents support changes to discontiguous segments of the
 * model (i.e. &quot;child nodes 3, 4 and 9 were deleted&quot;).  TableModelEvents
 * have no such concept - they operate on contiguous ranges of rows.  Therefore,
 * one incoming TreeModelEvent may result in more than one TableModelEvent being
 * fired.  Discontiguous TreeModelEvents will be broken into their contiguous
 * segments, which will be fired sequentially (in the case of removals, in
 * reverse order).  So, the example above would generate two TableModelEvents,
 * the first indicating that row 9 was removed, and the second indicating that
 * rows 3 and 4 were removed.
 * <p>
 * In the case of TreeModelEvents which add items to an unexpanded tree node,
 * a simple value change TableModelEvent will be fired for the row in question
 * on the tree column index.
 * <p>
 * Note also that if the model is large-model, removal events may only indicate
 * those indices which were visible at the time of removal, because less data
 * is retained about the position of nodes which are not displayed.  In this
 * case, the only issue is the accuracy of the scrollbar in the model; in
 * practice this is a non-issue, since it is based on the Outline's row count,
 * which will be accurate.
 * <p>
 * A note to subclassers, if we even leave this class non-final:  If you do
 * not use ProxyTableModel and RowMapper (which probably means you are doing
 * something wrong), <strong>do not fire structural changes from the TableModel</strong>.
 * This class is designed such that the TreeModel is entirely in control of the
 * count and contents of the rows of the table.  It and only it may fire 
 * structural changes.
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
            if (isDiscontiguous(e)) {
                System.err.println("Discontiguous event: " + e);
                //XXX just for the output
                getContiguousIndexBlocks (e, false);
            }
            
            getLayout().treeNodesInserted(e);
            fireTreeChange (e, NODES_INSERTED);
        }
        
        public void treeNodesRemoved(TreeModelEvent e) {
            System.err.println("\nTreeNodesRemoved");
            
            
//            if (isDiscontiguous(e)) {
                System.err.println("Discontiguous event: " + e);
                //XXX just for the output
                getContiguousIndexBlocks (e, true);
//            }
            
            //Okay, one or more nodes was removed.  The event's tree path
            //will be the parent.  Now we need to find out about any children
            //that were also removed so we can create a TreeModelEvent with
            //the right number of removed rows.
            
            //Note there is a slight impedance mismatch between TreeModel and
            //TableModel here - if we're using a large model layout cache,
            //we don't actually know what was offscreen - the data is already
            //gone from the model, so even if we know it was expanded, we
            //can't find out how many children it had.
            
            //The only thing this really affects is the scrollbar, and in
            //fact, the standard JTable UIs will update it correctly, since
            //the scrollbar will read getRowCount() to calculate its position.
            //In theory, this could break on a hyper-efficient TableUI that
            //attempted to manage scrollbar position *only* based on the
            //content of table model events.  That's pretty unlikely; but if
            //it happens, the solution is for Outline.getPreferredSize() to
            //proxy the preferred size from the layout cache
            
            TreePath path = e.getTreePath();
            lastRemoveWasExpanded = getTreePathSupport().isExpanded(path);

            //See if it's expanded - if it wasn't we're just going to blow
            //away one row anyway
            if (lastRemoveWasExpanded) {
                Object[] kids = e.getChildren();
                
                //TranslateEvent uses countRemoved to set the TableModelEvent
                countRemoved = kids.length;
                
                //Iterate the removed children
                for (int i=0; i < kids.length; i++) {
                    //Get the child's path
                    TreePath childPath = path.pathByAddingChild(kids[i]);
                    
                    //If it's not expanded, we don't care
                    if (getTreePathSupport().isExpanded(childPath)) {
                        //Find the number of *visible* children.  This may not
                        //be all the children, but it's the best information we have.
                        int visibleChildren = 
                            getLayout().getVisibleChildCount(childPath);

                        //add in the number of visible children
                        countRemoved += visibleChildren;
                    }
                    //Kill any references to the dead path to avoid memory leaks
                    getTreePathSupport().removePath(childPath);
                }
            } else {
                //Only one thing to remove
                countRemoved = 1;
            }
            
            //Tell the layout what happened, now that we've mined it for data
            //about the visible children of the removed paths
            getLayout().treeNodesRemoved(e);

            //Fire the tree change (fireTreeChange() will create a translated
            //TableModelEvent and fire it)
            fireTreeChange (e, NODES_REMOVED);
        }
        
        public void treeStructureChanged(TreeModelEvent e) {
            //This will translate to a generic "something changed" TableModelEvent
            getLayout().treeStructureChanged(e);
            fireTreeChange (e, STRUCTURE_CHANGED);
        }
        
        public void treeCollapsed(TreeExpansionEvent event) {
            //FixedHeightLayoutCache tests if the event is null.
            //Don't know how it could be, but there's probably a reason...
	    if(event != null) {
		TreePath path = event.getPath();

                //Tell the layout about the change
		if(path != null && getTreePathSupport().isVisible(path)) {
		    getLayout().setExpandedState(path, false);
		}
                
	    }
            
            //Fire the table model event constructed in treeWillCollapse
            fireTableChange (pendingExpansionEvent);
            pendingExpansionEvent = null;
            inProgressEvent = null;
        }
        
        /** Re&euml;expand descendants of a newly expanded path which were
         * expanded the last time their parent was expanded */
        private void updateExpandedDescendants(TreePath path) {
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
            
            //Fire the table model event constructed in treeWillExpand
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
                //If so, delete the expansion event we thought we were going
                //to use in treeExpanded/treeCollapsed, so that it doesn't
                //stick around forever holding references to objects from the
                //model
                pendingExpansionEvent = null;
                inProgressEvent = null;
            }
        }        
    }
    
    int countRemoved = 0;
    private boolean lastRemoveWasExpanded = false;
    
    /** Create a TableModelEvent that reflects the change described by the
     * tree model event, of the passed type */
    private void fireTranslatedEvent (TreeModelEvent e, int type) {
        TableModelEvent event = translateEvent (e, type);
        if (event != null) {
            fireTableChange(event);
        }
    }

    /** Determine if the indices referred to by a TreeModelEvent are
     * contiguous.  If they are not, we will need to generate multiple
     * TableModelEvents for each contiguous block */
    private boolean isDiscontiguous (TreeModelEvent e) {
        int[] indices = e.getChildIndices();
        System.err.println("isDiscontiguous " + e);
        if (indices.length == 1) {
            System.err.println("length is 1 - false");
            return false;
        }
        Arrays.sort(indices);
        int lastVal = indices[0];
        for (int i=1; i < indices.length; i++) {
            if (indices[i] != lastVal + 1) {
                System.err.println("  found discontinuity");
                return true;
            } else {
                lastVal++;
            }
        }
        return false;
    }

    /** Returns an array of int[]s each one representing a contiguous set of 
     * indices in the tree model events child indices - each of which can be
     * fired as a single TableModelEvent.  The length of the return value is
     * the number of TableModelEvents required to represent this TreeModelEvent.
     * If reverseOrder is true (needed for remove events, where the last indices
     * must be removed first or the indices of later removals will be changed),
     * the returned int[]s will be sorted in reverse order, and the order in
     * which they are returned will also be from highest to lowest. */
    private Object[] getContiguousIndexBlocks (TreeModelEvent e, boolean reverseOrder) {
        int[] indices = e.getChildIndices();
        
        //Quick check if there's only one index
        if (indices.length == 1) {
            return new Object[] {indices};
        }
        
        //The array of int[]s we'll return
        ArrayList al = new ArrayList();
        
        //Sort the indices as requested
        if (reverseOrder) {
            inverseSort (indices);
        } else {
            Arrays.sort (indices);
        }


        //The starting block
        ArrayList currBlock = new ArrayList(indices.length / 2);
        al.add(currBlock);
        
        //The value we'll check against the previous one to detect the
        //end of contiguous segment
        int lastVal = -1;
        
        //Iterate the indices
        for (int i=0; i < indices.length; i++) {
            if (i != 0) {
                //See if we've hit a discontinuity
                boolean newBlock = reverseOrder ? indices[i] != lastVal - 1 :
                    indices[i] != lastVal + 1;
                    
                if (newBlock) {
                    currBlock = new ArrayList(indices.length - 1);
                    al.add(currBlock);
                }
            }
            System.err.println("Adding " + indices[i] + " to block " + al.size());
            currBlock.add (new Integer(indices[i]));
            lastVal = indices[i];
        }
        
        System.err.println("Found " + al.size() + " discontiguous blocks");
         
        for (int i=0; i < al.size(); i++) {
            ArrayList curr = (ArrayList) al.get(i);
            Integer[] ints = (Integer[]) curr.toArray(new Integer[0]);
            
            System.err.println("Block " + i + ": " + curr);
            al.set(i, toArrayOfInt(ints));
        }
        
        return al.toArray();
    }
    
    private int[] toArrayOfInt (Integer[] ints) {
        int[] result = new int[ints.length];
        for (int i=0; i < ints.length; i++) {
            result[i] = ints[i].intValue();
        }
        return result;
    }
    
    /** Sort an array of ints from highest to lowest */
    private void inverseSort (int[] array) {
        //Kinda brute force & ugly
        for (int i=0; i < array.length; i++) {
            array[i] *= -1;
        }
        Arrays.sort(array);
        for (int i=0; i < array.length; i++) {
            array[i] *= -1;
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
                //It's just a value change - probably the user set a value in
                //one of the other columns
                return new TableModelEvent (this, row);
            case NODES_INSERTED :
                //See if the insert was into something that might affect display
                boolean realInsert = getLayout().isExpanded(path);
                
                if (realInsert) {
                    //Get the indices inserted
                    int[] indices = e.getChildIndices();
                    
                    //Sort them - they should be presorted, but some notes in
                    //FixedHeightLayoutCache sources that it should be sorting
                    //them and isn't - so we will here
                    Arrays.sort(indices);
                    
                    if (indices.length == 0) {
                        //Shouldn't happen
                        return null;
                    } else if (indices.length == 1) {
                        
                        //Only one index to change, fire a simple event.  It
                        //will be the first index in the array + the row +
                        //1 because the 0th child of a node is 1 greater than
                        //its row index
                        int affectedRow = row + indices[0] + 1;
                        return new TableModelEvent (this, affectedRow, affectedRow, 
                            TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT);
                        
                    } else {
                        //Find the first and last indices.
                        int lowest = indices[0] + 1;
                        int highest = indices[indices.length-1] + 1;
                        return new TableModelEvent (this, row + lowest, row + highest,
                            TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT);
                        
                    }
                } else {
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
                    int lastRow = firstRow + (countRemoved - 1);
                    
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
