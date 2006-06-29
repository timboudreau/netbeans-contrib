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
 * The Original Software is the ETable module. The Initial Developer of the Original
 * Software is Nokia. Portions Copyright 2005 Nokia. All Rights Reserved.
 */
package org.netbeans.api.tableview;

import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.EventObject;
import java.util.Properties;
import javax.swing.Action;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.tree.AbstractLayoutCache;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import org.netbeans.swing.etable.ETable;
import org.netbeans.swing.etable.ETableColumn;
import org.netbeans.swing.etable.QuickFilter;
import org.netbeans.swing.outline.DefaultOutlineModel;
import org.netbeans.swing.outline.Outline;
import org.netbeans.swing.outline.OutlineModel;
import org.netbeans.swing.outline.RowModel;
import org.openide.ErrorManager;
import org.openide.awt.MouseUtils;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.view.NodeTreeModel;
import org.openide.explorer.view.Visualizer;
import org.openide.nodes.Node;
import org.openide.nodes.NodeOp;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.util.WeakListeners;

/**
 * Explorer view displaying nodes in a tree table.
 * @author David Strupl
 */
public class OutlineView extends JScrollPane {

    /** The table */
    private Outline outline;
    /** Explorer manager, valid when this view is showing */
    private ExplorerManager manager;
    /** not null if popup menu enabled */
    private PopupAdapter popupListener;
    /** the most important listener (on four types of events */
    private TableSelectionListener managerListener = null;
    /** weak variation of the listener for property change on the explorer manager */
    private PropertyChangeListener wlpc;
    /** weak variation of the listener for vetoable change on the explorer manager */
    private VetoableChangeListener wlvc;
    
    private OutlineModel model;
    private NodeTreeModel treeModel;
    private PropertiesRowModel rowModel;
    /** */
    private NodePopupFactory popupFactory;

    /** Creates a new instance of TableView */
    public OutlineView() {
        this(null);
    }    
    
    /** Creates a new instance of TableView */
    public OutlineView(String nodesColumnLabel) {
        treeModel = new NodeTreeModel();
        rowModel = new PropertiesRowModel();
        model = createOutlineModel(treeModel, rowModel, nodesColumnLabel);
        outline = new OutlineViewOutline(model);
        rowModel.setOutline(outline);
        outline.setRenderDataProvider(new NodeRenderDataProvider(outline));
        SheetCell tableCell = new SheetCell.OutlineSheetCell(outline);
        outline.setDefaultRenderer(Node.Property.class, tableCell);
        outline.setDefaultEditor(Node.Property.class, tableCell);
        setViewportView(outline);
        setPopupAllowed(true);
        // do not care about focus
        setRequestFocusEnabled (false);
        outline.setRequestFocusEnabled(true);
        java.awt.Color c = javax.swing.UIManager.getColor("Table.background1");
        if (c == null) {
            c = javax.swing.UIManager.getColor("Table.background");
        }
        if (c != null) {
            getViewport().setBackground(c);
        }
        getActionMap().put("org.openide.actions.PopupAction", new PopupAction());
        popupFactory = new OutlinePopupFactory();
    }

    /**
     * This method allows plugging own OutlineModel to the OutlineView.
     * You can override it and create different model in the subclass.
     */
    protected OutlineModel createOutlineModel(TreeModel treeModel, RowModel rowModel, String label) {
        return DefaultOutlineModel.createOutlineModel(treeModel, rowModel, false, label);
    }
    
    /** Requests focus for the tree component. Overrides superclass method. */
    public void requestFocus () {
        outline.requestFocus();
    }
    
    /** Requests focus for the tree component. Overrides superclass method. */
    public boolean requestFocusInWindow () {
        return outline.requestFocusInWindow();
    }
    
    /**
     * Getter for the embeded table component.
     */
    public Outline getOutline() {
        return outline;
    }
    
    /** Is it permitted to display a popup menu?
     * @return <code>true</code> if so
     */
    public boolean isPopupAllowed () {
        return popupListener != null;
    }

    public void setProperties(Node.Property[] newProperties) {
        rowModel.setProperties(newProperties);
        outline.tableChanged(null);
    }
    
    /** Enable/disable displaying popup menus on tree view items.
    * Default is enabled.
    * @param value <code>true</code> to enable
    */
    public void setPopupAllowed (boolean value) {
        if (popupListener == null && value) {
            // on
            popupListener = new PopupAdapter ();
            outline.addMouseListener (popupListener);
            return;
        }
        if (popupListener != null && !value) {
            // off
            outline.removeMouseListener (popupListener);
            popupListener = null;
            return;
        }
    }
    
    /** Initializes the component and lookup explorer manager.
     */
    public void addNotify () {
        super.addNotify ();
        lookupExplorerManager ();
    }
    
    /**
     * Method allowing to read stored values.
     * The stored values should be only those that the user has customized,
     * it does not make sense to store the values that were set using 
     * the initialization code because the initialization code can be run
     * in the same way after restart.
     */
    public void readSettings(Properties p, String propertyPrefix) {
        outline.readSettings(p, propertyPrefix);
    }

    /**
     * Method allowing to store customization values.
     * The stored values should be only those that the user has customized,
     * it does not make sense to store the values that were set using 
     * the initialization code because the initialization code can be run
     * in the same way after restart.
     */
    public void writeSettings(Properties p, String propertyPrefix) {
        outline.writeSettings(p, propertyPrefix);
    }

    /**
     * Allows customization of the popup menus.
     */
    public void setNodePopupFactory(NodePopupFactory newFactory) {
        popupFactory = newFactory;
    }
    
    /**
     * Getter for the current popup customizer factory.
     */
    public NodePopupFactory getNodePopupFactory() {
        return popupFactory;
    }
    
    /** Registers in the tree of components.
     */
    private void lookupExplorerManager () {
        // Enter key in the tree

        if (managerListener == null) {
            managerListener = new TableSelectionListener();
        }
        
        ExplorerManager newManager = ExplorerManager.find(this);
        if (newManager != manager) {
            if (manager != null) {
                manager.removeVetoableChangeListener (wlvc);
                manager.removePropertyChangeListener (wlpc);
            }

            manager = newManager;

            manager.addVetoableChangeListener(wlvc = WeakListeners.vetoableChange(managerListener, manager));
            manager.addPropertyChangeListener(wlpc = WeakListeners.propertyChange(managerListener, manager));
        }
        
        synchronizeRootContext();
        synchronizeSelectedNodes(true);
        
        // Sometimes the listener is registered twice and we get the 
        // selection events twice. Removing the listener before adding it
        // should be a safe fix.
        outline.getSelectionModel().removeListSelectionListener(managerListener);
        outline.getSelectionModel().addListSelectionListener(managerListener);
    }
    
    /** Synchronize the root context from the manager of this Explorer.
    */
    final void synchronizeRootContext() {
        treeModel.setNode(manager.getRootContext());
    }

    /** Synchronize the selected nodes from the manager of this Explorer.
     */
    final void synchronizeSelectedNodes(boolean scroll) {
        expandSelection();
        outline.invalidate();
        invalidate();
        validate();
        Node[] arr = manager.getSelectedNodes ();
        outline.getSelectionModel().clearSelection();
        int size = outline.getRowCount();
        int firstSelection = -1;
        for (int i = 0; i < size; i++) {
            Node n = getNodeFromRow(i);
            for (int j = 0; j < arr.length; j++) {
                if ((n != null) && (n.equals(arr[j]))) {
                    outline.getSelectionModel().addSelectionInterval(i, i);
                    if (firstSelection == -1) {
                        firstSelection = i;
                    }
                }
            }
        }
        if (scroll && (firstSelection >= 0)) {
            JViewport v = getViewport();
            if (v != null) {
                Rectangle rect = outline.getCellRect(firstSelection, 0, true);
                if (v.getExtentSize().height > rect.height) {
                    rect.height = v.getExtentSize().height;
                }
                int ho = outline.getSize().height;
                if (ho > 0) {
                    if (rect.y + rect.height > ho) {
                        rect.height = ho - rect.y;
                        if (rect.height <= 0) {
                            rect.height = 40;
                        }
                    }
                }
                v.setViewPosition(new Point()); // strange line - but without
                                                // it the next one is wrong
                outline.scrollRectToVisible(rect);
            }
        }
    }

    /**
     * Tries to expand nodes selected in the explorer manager.
     */
    private void expandSelection() {
        Node[] arr = manager.getSelectedNodes ();
        for (int i = 0; i < arr.length; i++) {
            if ( (arr[i].getParentNode() == null) && (! outline.isRootVisible())) {
                // don't try to show root if it is invisible
                continue;
            }
            TreeNode tn = Visualizer.findVisualizer(arr[i]);
            if (tn != null) {
                ArrayList al = new ArrayList();
                while (tn != null) {
                    al.add(tn);
                    tn = tn.getParent();
                }
                Collections.reverse(al);
                TreePath tp = new TreePath(al.toArray());
                while ((tp != null) && (tp.getPathCount() > 0)) {
                    tp = tp.getParentPath();
                    if (tp != null) {
                        outline.expandPath(tp);
                    }
                }
            }
        }
    }
    
    /**
     * Deinitializes listeners.
     */
    public void removeNotify () {
        super.removeNotify ();
        outline.getSelectionModel().removeListSelectionListener(managerListener);
        manager.removePropertyChangeListener (wlpc);
        manager.removeVetoableChangeListener (wlvc);
    }

    /**
     * Shows popup menu invoked on the table.
     */
    void showPopup(int xpos, int ypos, final JPopupMenu popup) {
        if ((popup != null) && (popup.getSubElements().length > 0)) {
            final PopupMenuListener p = new PopupMenuListener() {
                public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                    
                }
                public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                    popup.removePopupMenuListener(this);
                    outline.requestFocus();
                }
                public void popupMenuCanceled(PopupMenuEvent e) {
                    
                }
            };
            popup.addPopupMenuListener(p);
            popup.show(this, xpos, ypos);
        }
    }    
    
    /**
     * Find relevant actions and call the factory to create a popup.
     */
    private JPopupMenu createPopup(Point p) {
        int[] selRows = outline.getSelectedRows();
        ArrayList al = new ArrayList(selRows.length);
        for (int i = 0; i < selRows.length; i++) {
            Node n = getNodeFromRow(selRows[i]);
            if (n != null) {
                al.add(n);
            }
        }
        Node[] arr = (Node[])al.toArray(new Node[al.size()]);
        p = SwingUtilities.convertPoint(this, p, outline);
        int column = outline.columnAtPoint(p);
        int row = outline.rowAtPoint(p);
        return popupFactory.createPopupMenu(row, column, arr, outline);
    }
    
    /**
     * 
     */
    private Node getNodeFromRow(int rowIndex) {
        int row = outline.convertRowIndexToModel(rowIndex);
        TreePath tp = outline.getLayoutCache().getPathForRow(row);
        if (tp == null) {
            return null;
        }
        return Visualizer.findNode(tp.getLastPathComponent());
    }
    
    /** Returns the point at which the popup menu is to be shown. May return null.
     * @return the point or null
     */    
    private Point getPositionForPopup () {
        int i = outline.getSelectionModel().getLeadSelectionIndex();
        if (i < 0) return null;
        int j = outline.getColumnModel().getSelectionModel().getLeadSelectionIndex();
        if (j < 0) {
            j = 0;
        }

        Rectangle rect = outline.getCellRect(i, j, true);
        if (rect == null) return null;

        Point p = new Point(rect.x + rect.width / 3,
                rect.y + rect.height / 2);
        
        // bugfix #36984, convert point by TableView.this
        p =  SwingUtilities.convertPoint (outline, p, OutlineView.this);

        return p;
    }

    /**
     * Action registered in the component's action map.
     */
    private class PopupAction extends javax.swing.AbstractAction implements Runnable {
        public void actionPerformed(ActionEvent evt) {
            SwingUtilities.invokeLater(this);
        }
        public void run() {
            Point p = getPositionForPopup ();
            if (p == null) {
                return ;
            }
            if (isPopupAllowed()) {
                JPopupMenu pop = createPopup(p);
                showPopup(p.x, p.y, pop);
            }
        }
    };
    
    /**
     * Mouse listener that invokes popup.
     */
    private class PopupAdapter extends MouseUtils.PopupMouseAdapter {

	PopupAdapter() {}
	
        protected void showPopup (MouseEvent e) {
            int selRow = outline.rowAtPoint(e.getPoint());

            if (selRow != -1) {
                if (! outline.getSelectionModel().isSelectedIndex(selRow)) {
                    outline.getSelectionModel().clearSelection();
                    outline.getSelectionModel().setSelectionInterval(selRow, selRow);
                }
                Point p = SwingUtilities.convertPoint(e.getComponent(), e.getPoint(), OutlineView.this);
                if (isPopupAllowed()) {
                    JPopupMenu pop = createPopup(p);
                    OutlineView.this.showPopup(p.x, p.y, pop);
                    e.consume();
                }
            } 
        }
    }

    /**
     * Called when selection in tree is changed.
     */
    final void callSelectionChanged (Node[] nodes) {
        manager.removePropertyChangeListener (wlpc);
        manager.removeVetoableChangeListener (wlvc);
        try {
            manager.setSelectedNodes(nodes);
        } catch (PropertyVetoException e) {
            synchronizeSelectedNodes(false);
        } finally {
            // to be sure not to add them twice!
            manager.removePropertyChangeListener (wlpc);
            manager.removeVetoableChangeListener (wlvc);
            manager.addPropertyChangeListener (wlpc);
            manager.addVetoableChangeListener (wlvc);
        }
    }
    
    /** 
     * Check if selection of the nodes could break
     * the selection mode set in the ListSelectionModel.
     * @param nodes the nodes for selection
     * @return true if the selection mode is broken
     */
    private boolean isSelectionModeBroken(Node[] nodes) {
        
        // if nodes are empty or single then everthing is ok
        // or if discontiguous selection then everthing ok
        if (nodes.length <= 1 || outline.getSelectionModel().getSelectionMode() == 
                ListSelectionModel.MULTIPLE_INTERVAL_SELECTION) {
            return false;
        }

        // if many nodes
        
        // breaks single selection mode
        if (outline.getSelectionModel().getSelectionMode() == 
            ListSelectionModel.SINGLE_SELECTION) {
            return true;
        }
        
        // check the contiguous selection mode

        // check selection's rows
        
        // all is ok
        return false;
    }
    
    /**
     * Listener attached to the explorer manager and also to the
     * changes in the table selection.
     */
    private class TableSelectionListener implements VetoableChangeListener, ListSelectionListener, PropertyChangeListener {
        public void propertyChange(java.beans.PropertyChangeEvent evt) {
            if (manager == null) return; // the tree view has been removed before the event got delivered
            if (evt.getPropertyName().equals(ExplorerManager.PROP_ROOT_CONTEXT)) {
                synchronizeRootContext();
            }
            if (evt.getPropertyName().equals(ExplorerManager.PROP_SELECTED_NODES)) {
                synchronizeSelectedNodes(true);
            }
        }

        public void valueChanged(javax.swing.event.ListSelectionEvent listSelectionEvent) {
            int selectedRows[] = outline.getSelectedRows();
            ArrayList selectedNodes = new ArrayList(selectedRows.length);
            for (int i = 0; i < selectedRows.length;i++) {
                Node n = getNodeFromRow(selectedRows[i]);
                if (n != null) {
                    selectedNodes.add(n);
                }
            }
            callSelectionChanged((Node[])selectedNodes.toArray(new Node[selectedNodes.size()]));
        }

        public void vetoableChange(java.beans.PropertyChangeEvent evt) throws java.beans.PropertyVetoException {
            if (evt.getPropertyName().equals(ExplorerManager.PROP_SELECTED_NODES)) {
                // issue 11928 check if selecetion mode will be broken
                Node[] nodes = (Node[])evt.getNewValue();
                if (isSelectionModeBroken(nodes)) {
                    throw new PropertyVetoException("selection mode " +  " broken by " + Arrays.asList(nodes), evt); // NOI18N
                }
            }
        }
        public void run() {
        }
    }

    /**
     * Extension of the ETable that allows adding a special comparator
     * for sorting the rows.
     */
    private static class OutlineViewOutline extends Outline {
        public OutlineViewOutline(OutlineModel mdl) {
            super(mdl);
        }
        
        public Object transformValue(Object value) {
            return PropertiesRowModel.getValueFromProperty(value);
        }
        
        public boolean editCellAt(int row, int column, EventObject e) {
            Object o = getValueAt(row, column);
            if (o instanceof Node.Property) { // && (e == null || e instanceof KeyEvent)) {
                Node.Property p = (Node.Property)o;
                if (p.getValueType() == Boolean.class || p.getValueType() == Boolean.TYPE) {
                    PropertiesRowModel.toggleBooleanProperty(p);
                    Rectangle r = getCellRect(row, column, true);
                    repaint (r.x, r.y, r.width, r.height);
                    return false;
                }
            }
            return super.editCellAt(row, column, e);
        }
        
        protected TableColumn createColumn(int modelIndex) {
            return new OutlineViewOutlineColumn(modelIndex);
        }
        /**
         * Extension of ETableColumn using TableViewRowComparator as
         * comparator.
         */
        private class OutlineViewOutlineColumn extends OutlineColumn {
            public OutlineViewOutlineColumn(int index) {
                super(index);
            }
            protected Comparator getRowComparator(int column, boolean ascending) {
                  return new OutlineRowComparator(column, ascending);
            }
            public boolean isSortingAllowed() {
                boolean res = super.isSortingAllowed();
                TableModel model = getModel();
                if (model.getRowCount() <= 0) {
                    return res;
                }
                Object sampleValue = model.getValueAt(0, getModelIndex());
                if (sampleValue instanceof Node.Property) {
                    Node.Property prop = (Node.Property)sampleValue;
                    Object sortableColumnProperty = prop.getValue("SortableColumn");
                    if (sortableColumnProperty instanceof Boolean) {
                        return ((Boolean)sortableColumnProperty).booleanValue();
                    }
                }
                return res;
            }
        }
    }
    
    private static class OutlinePopupFactory extends NodePopupFactory {
        public OutlinePopupFactory() {
        }

        public JPopupMenu createPopupMenu(int row, int column, Node[] selectedNodes, Component component) {
            if (component instanceof ETable) {
                ETable et = (ETable)component;
                int modelRowIndex = et.convertColumnIndexToModel(column);
                setShowQuickFilter(modelRowIndex != 0);
            }
            return super.createPopupMenu(row, column, selectedNodes, component);
        }
    }
}
