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
 * The Original Software is the ETable module. The Initial Developer of the Original
 * Software is Nokia. Portions Copyright 2005 Nokia. All Rights Reserved.
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
package org.netbeans.api.tableview;

import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.dnd.DnDConstants;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.EventObject;
import java.util.Properties;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import org.netbeans.swing.etable.ETable;
import org.netbeans.swing.etable.TableColumnSelector;
import org.netbeans.swing.outline.DefaultOutlineModel;
import org.netbeans.swing.outline.Outline;
import org.netbeans.swing.outline.OutlineModel;
import org.netbeans.swing.outline.RowModel;
import org.openide.awt.MouseUtils;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.view.NodeTreeModel;
import org.openide.explorer.view.Visualizer;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.WeakListeners;

/**
 * Explorer view displaying nodes in a tree table.
 * @author David Strupl
 */
public class OutlineView extends JScrollPane {

    /** The table */
    private Outline outline;
    /** Explorer manager, valid when this view is showing */
    ExplorerManager manager;
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
    
    /** true if drag support is active */
    private transient boolean dragActive = true;

    /** true if drop support is active */
    private transient boolean dropActive = true;

    /** Drag support */
    transient OutlineViewDragSupport dragSupport;

    /** Drop support */
    transient OutlineViewDropSupport dropSupport;
    transient boolean dropTargetPopupAllowed = true;

    // default DnD actions
    transient private int allowedDragActions = DnDConstants.ACTION_COPY_OR_MOVE | DnDConstants.ACTION_REFERENCE;
    transient private int allowedDropActions = DnDConstants.ACTION_COPY_OR_MOVE | DnDConstants.ACTION_REFERENCE;

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
                // activation of drop target
        if (DragDropUtilities.dragAndDropEnabled) {
            ExplorerDnDManager.getDefault().addFutureDropTarget(this);
            // note: drag target is activated on focus gained
        }

        outline.addFocusListener(new java.awt.event.FocusListener(){
            public void focusGained(java.awt.event.FocusEvent ev) {
                // unregister
                ev.getComponent().removeFocusListener(this);

                // lazy activation of drag source
                if (DragDropUtilities.dragAndDropEnabled && dragActive) {
                    setDragSource(true);
                    // note: dropTarget is activated in constructor
                }
            }

            public void focusLost(java.awt.event.FocusEvent ev) {
            }

        });
        TableColumnSelector tcs = (TableColumnSelector) Lookup.getDefault().lookup(TableColumnSelector.class);
        if (tcs != null) {
            outline.setColumnSelector(tcs);
        }
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
    Node getNodeFromRow(int rowIndex) {
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

    /********** Support for the Drag & Drop operations *********/
    /** Drag support is enabled by default.
    * @return true if dragging from the view is enabled, false
    * otherwise.
    */
    public boolean isDragSource() {
        return dragActive;
    }

    /** Enables/disables dragging support.
    * @param state true enables dragging support, false disables it.
    */
    public void setDragSource(boolean state) {
        // create drag support if needed
        if (state && (dragSupport == null)) {
            dragSupport = new OutlineViewDragSupport(this, outline);
        }

        // activate / deactivate support according to the state
        dragActive = state;

        if (dragSupport != null) {
            dragSupport.activate(dragActive);
        }
    }

    /** Drop support is enabled by default.
    * @return true if dropping to the view is enabled, false
    * otherwise<br>
    */
    public boolean isDropTarget() {
        return dropActive;
    }

    /** Enables/disables dropping support.
    * @param state true means drops into view are allowed,
    * false forbids any drops into this view.
    */
    public void setDropTarget(boolean state) {
        // create drop support if needed
        if (dropActive && (dropSupport == null)) {
            dropSupport = new OutlineViewDropSupport(this, outline, dropTargetPopupAllowed);
        }

        // activate / deactivate support according to the state
        dropActive = state;

        if (dropSupport != null) {
            dropSupport.activate(dropActive);
        }
    }

    /** Actions constants comes from {@link java.awt.dnd.DnDConstants}.
    * All actions (copy, move, link) are allowed by default.
    * @return int representing set of actions which are allowed when dragging from
    * asociated component.
     */
    public int getAllowedDragActions() {
        return allowedDragActions;
    }

    /** Sets allowed actions for dragging
    * @param actions new drag actions, using {@link java.awt.dnd.DnDConstants}
    */
    public void setAllowedDragActions(int actions) {
        // PENDING: check parameters
        allowedDragActions = actions;
    }

    /** Actions constants comes from {@link java.awt.dnd.DnDConstants}.
    * All actions are allowed by default.
    * @return int representing set of actions which are allowed when dropping
    * into the asociated component.
    */
    public int getAllowedDropActions() {
        return allowedDropActions;
    }

    /** Sets allowed actions for dropping.
    * @param actions new allowed drop actions, using {@link java.awt.dnd.DnDConstants}
    */
    public void setAllowedDropActions(int actions) {
        // PENDING: check parameters
        allowedDropActions = actions;
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
            private String tooltip;
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
            protected TableCellRenderer createDefaultHeaderRenderer() {
                TableCellRenderer orig = super.createDefaultHeaderRenderer();
                OutlineViewOutlineHeaderRenderer ovohr = new OutlineViewOutlineHeaderRenderer(orig);
                return ovohr;
            }
            /** This is here to compute and set the header tooltip. */
            class OutlineViewOutlineHeaderRenderer implements TableCellRenderer {
                private TableCellRenderer orig;
                public OutlineViewOutlineHeaderRenderer(TableCellRenderer delegate) {
                    orig = delegate;
                }
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    Component oc = orig.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    if (tooltip == null) {
                        TableModel model = getModel();
                        if (model.getRowCount() <= 0) {
                            // now rows --> we cannot compute the header tooltip
                            return oc;
                        }
                        Object sampleValue = model.getValueAt(0, getModelIndex());
                        if (sampleValue instanceof Node.Property) {
                            Node.Property prop = (Node.Property)sampleValue;
                            tooltip = prop.getShortDescription();
                        }
                    }
                    if ((tooltip != null) && (oc instanceof JComponent)) {
                        JComponent jc = (JComponent)oc;
                        jc.setToolTipText(tooltip);
                    }
                    return oc;
                }
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
