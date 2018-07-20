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

/*
 * this file is derived from the "Creating TreeTable" article at
 * http://java.sun.com/products/jfc/tsc/articles/treetable2/index.html
 */
package org.netbeans.modules.tasklist.usertasks.treetable;

import javax.swing.JTextField;
import javax.swing.DefaultCellEditor;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.EventObject;
import java.util.List;
import java.util.logging.Level;
import javax.swing.Icon;
import javax.swing.InputMap;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import org.netbeans.modules.tasklist.core.table.SortingModel;
import org.netbeans.modules.tasklist.usertasks.treetable.*;
import org.netbeans.modules.tasklist.usertasks.util.UTUtils;



/**
 * A simple JTreeTable component created
 * by using a JTree as a renderer (and editor) for the cells in a 
 * particular column in the JTable.  
 *
 * @author Philip Milne
 * @author Scott Violet
 * @author tl
 */
public class TreeTable extends JTable {
    /**
     * Columns configuration
     */
    public static final class ColumnsConfig extends 
            org.netbeans.modules.tasklist.core.table.ColumnsConfig {
        public static final long serialVersionUID = 2L;
    }    
    
    /**
     * Expanded nodes and selection.
     * See setExpandedNodesAndSelection/getExpandedNodesAndSelection
     */
    private static final class ExpandedNodesAndSelection {
        public static final long serialVersionUID = 1L;
        
        /** selection */
        public TreePath[] selection;
        
        /** expanded nodes */
        public TreePath[] expandedNodes;
    }
    
    private static final long serialVersionUID = 1;
    
    /** A subclass of JTree. */
    protected TreeTableCellRenderer tree;
    
    private TreeTableModel treeTableModel;
    private SortingModel sortingModel;
    
    // TODO: memory leak
    private Object renderedNode;
    
    /**
     * Constructor.
     * 
     * @param treeTableModel model
     */
    public TreeTable(TreeTableModel treeTableModel) {
	super();
        putClientProperty("JTable.autoStartsEdit", Boolean.FALSE); // NOI18N

        // Create the tree. It will be used as a renderer and editor. 
        // First we create a dummy model for the tree and set later the
        // real model with setModel(). This way JTree's TreeModelListener
        // will be called first and we can update our table.
	tree = new TreeTableCellRenderer(
            new DefaultTreeModel(new DefaultMutableTreeNode()));

	// Install a tableModel representing the visible rows in the tree. 
	setTreeTableModel(treeTableModel);

	// Force the JTable and JTree to share their row selection models. 
	ListToTreeSelectionModelWrapper selectionWrapper = 
            new ListToTreeSelectionModelWrapper();
	tree.setSelectionModel(selectionWrapper);
	setSelectionModel(selectionWrapper.getListSelectionModel()); 

	// Install the tree editor renderer and editor. 
	setDefaultRenderer(TreeTableModel.class, tree); 
        /*
         it is also possible to draw the tree only once:
         setUI(new TreeTableUI(tree));
        setDefaultRenderer(TreeTableModel.class,
                new DefaultTableCellRenderer());*/
        
	setDefaultEditor(TreeTableModel.class, new TreeTableCellEditor());

	// No grid.
	setShowGrid(false);

	// No intercell spacing
	//setIntercellSpacing(new Dimension(0, 0));	

	// And update the height of the trees row to match that of
	// the table.
	/*if (tree.getRowHeight() < 1) {
	    // Metal looks better like this.
	    setRowHeight(18);
	}*/
        
        this.sortingModel = new SortingModel();
        
        InputMap imp2 = getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
 	     
        // copied from TreeView which tried to fix #18292
        // by doing this
        imp2.put(KeyStroke.getKeyStroke("control C"), "none"); // NOI18N
        imp2.put(KeyStroke.getKeyStroke("control V"), "none"); // NOI18N
        imp2.put(KeyStroke.getKeyStroke("control X"), "none"); // NOI18N
        imp2.put(KeyStroke.getKeyStroke("COPY"), "none"); // NOI18N
        imp2.put(KeyStroke.getKeyStroke("PASTE"), "none"); // NOI18N
        imp2.put(KeyStroke.getKeyStroke("CUT"), "none"); // NOI18N
        imp2.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), 
                "expand"); // NOI18N
        imp2.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), 
                "collapse"); // NOI18N

        getActionMap().put("expand", // NOI18N
                new ExpandCollapseAction(true, this));
        getActionMap().put("collapse", // NOI18N
                new ExpandCollapseAction(false, this));
    }

    /**
     * Returnes the node (object returned by TreeTableModel.getChild or
     * getRoot) that is currently being rendered. This method could
     * only be called from a cell renderer to access current node.
     * 
     * @return current node 
     */
    public Object getRenderedNode() {
        return renderedNode;
    }

    /**
     * Returns selected path
     *
     * @return selected path or null
     */
    public TreePath getSelectedPath() {
        int row = getSelectedRow();
        if (row < 0)
            return null;
        
        return tree.getPathForRow(row);
    }

    /**
     * Tests whether the column contains the tree.
     *
     * @param column column index in the view
     * @return true = it is the column with the tree
     */
    public boolean isTreeColumn(int column) {
        return getColumnClass(column) == TreeTableModel.class;
    }
    
    /**
     * Returns selected paths
     *
     * @return selected paths
     */
    public TreePath[] getSelectedPaths() {
        int[] rows = getSelectedRows();
        TreePath[] paths = new TreePath[rows.length];
        for (int i = 0; i < rows.length; i++) {
            paths[i] = tree.getPathForRow(rows[i]);
        }
        return paths;
    }
    
    /**
     * TreeModel does not support a reordering event. Therefore it is 
     * necessary to save the expanded nodes and selection before 
     * a reordering and restore them after such an operation.
     *
     * @return an object that could be used in setExpandedNodesAndSelection()
     */
    public Object getExpandedNodesAndSelection() {
        TreeTable.ExpandedNodesAndSelection ret =
            new TreeTable.ExpandedNodesAndSelection();
        
        Enumeration<TreePath> en = tree.getExpandedDescendants( 
            new TreePath(getTreeTableModel().getRoot()));
        if (en != null) {
            List<TreePath> exp = new ArrayList<TreePath>();
            while (en.hasMoreElements()) {
                exp.add(en.nextElement());
            }
            ret.expandedNodes = exp.toArray(new TreePath[exp.size()]);
        } else {
            ret.expandedNodes = new TreePath[0];
        }
        
        int[] selRows = getSelectedRows();
        ret.selection = new TreePath[selRows.length];
        for (int i = 0; i < selRows.length; i++) {
            ret.selection[i] = tree.getPathForRow(selRows[i]);
        }

        return ret;
    }
    
    /**
     * TreeModel does not support a reordering event. Therefore it is 
     * necessary to save the expanded nodes and selection before 
     * a reordering and restore them after such an operation.
     *
     * @param obj an object that was returned by getExpandedNodesAndSelection()
     */
    public void setExpandedNodesAndSelection(Object obj) {
        TreeTable.ExpandedNodesAndSelection es = 
            (TreeTable.ExpandedNodesAndSelection) obj;
        
        // expanded nodes
        for (int i = 0; i < es.expandedNodes.length; i++) {
            tree.expandPath(es.expandedNodes[i]);
        }

        // selection
        for (int i = 0; i < es.selection.length; i++) {
            int row = tree.getRowForPath(es.selection[i]);
            getSelectionModel().addSelectionInterval(row, row);
        }
    }
    
    /**
     * Expands all nodes
     */    
    public void expandAll() {
        TreePath tp = new TreePath(tree.getModel().getRoot());
        expandAllUnder(tp);
    }
    
    /**
     * Collapses all nodes
     */
    public void collapseAll() {
        Object root = tree.getModel().getRoot();
        TreePath rootPath = new TreePath(root);
        if (getTree().isRootVisible())
            collapseAllUnder(rootPath);
        else {
            int n = tree.getModel().getChildCount(root);
            for (int i = 0; i < n; i++) {
                collapseAllUnder(rootPath.pathByAddingChild(
                        tree.getModel().getChild(root, i)));
            }
        }
    }
    
    /**
     * Expands the whole path so the last element becomes visible
     *
     * @param tp path
     */
    public void expandAllPath(TreePath tp) {
        while (tp != null) {
            tree.expandPath(tp);
            tp = tp.getParentPath();
        }
    }

    /**
     * Does tree.expandPath(tp)
     *
     * @param tp path to be expanded
     */
    public void expandPath(TreePath tp) {
        tree.expandPath(tp);
    }
    
    /**
     * Selects the specified path
     *
     * @param path the path to be selected
     */
    public void select(TreePath path) {
        int row = this.getRowForPath(path);
        if (row >= 0)
            this.getSelectionModel().setSelectionInterval(row, row);
    }

    /**
     * Selects the specified path
     *
     * @param path the path to be selected
     */
    public void select(TreePath[] path) {
        getSelectionModel().clearSelection();
        for (int i = 0; i < path.length; i++) {
            int row = this.getRowForPath(path[i]);
            if (row >= 0)
                getSelectionModel().addSelectionInterval(row, row);
        }
    }

    /**
     * Makes the specified task visible (scrolls to it)
     *
     * @param path to make visible
     */
    public void scrollTo(TreePath path) {
        int row = this.getRowForPath(path);
        if (row > 0) {
            Rectangle r = this.getCellRect(row, 0, true);
            this.scrollRectToVisible(r);
        }
    }
    
    /**
     * Expands all nodes under the specified path
     *
     * @param tp the path
     */
    public void expandAllUnder(TreePath tp) {
        tree.expandPath(tp);
        Object last = tp.getLastPathComponent();
        for (int i = 0; i < tree.getModel().getChildCount(last); i++) {
            Object child = tree.getModel().getChild(last, i);
            expandAllUnder(tp.pathByAddingChild(child));
        }
    }
    
    /**
     * Collapses all nodes under the specified path
     *
     * @param tp the path
     */
    public void collapseAllUnder(TreePath tp) {
        if (!tree.hasBeenExpanded(tp))
            return;
        
        Object last = tp.getLastPathComponent();
        for (int i = 0; i < tree.getModel().getChildCount(last); i++) {
            Object child = tree.getModel().getChild(last, i);
            collapseAllUnder(tp.pathByAddingChild(child));
        }
        tree.collapsePath(tp);
    }

    @Override
    public Component prepareRenderer(TableCellRenderer renderer, int row,
                                     int column) {
        this.renderedNode = getNodeForRow(row);
        return super.prepareRenderer(renderer, row, column);
    }
    
    /**
     * Sets new sorting model
     *
     * @param sm new sorting model or null
     */
    public void setSortingModel(SortingModel sm) {
        SortingModel old = this.sortingModel;
        this.sortingModel = sm;
        firePropertyChange("sortingModel", old, sm); // NOI18N
    }
    
    /**
     * Returns sorting model
     *
     * @return sorting model or null if not supported
     */
    public SortingModel getSortingModel() {
        return sortingModel;
    }
    
    /**
     * Sets new TreeTableModel
     *
     * @param treeTableModel a model
     */
    public void setTreeTableModel(TreeTableModel treeTableModel) {
        this.treeTableModel = treeTableModel;
        if (getModel() instanceof TreeTableModelAdapter)
            ((TreeTableModelAdapter) getModel()).unregister();
        super.setModel(new TreeTableModelAdapter(treeTableModel, tree));
        tree.setModel(treeTableModel);
    }
    
    /**
     * Returns the current model
     *
     * @return model
     */
    public TreeTableModel getTreeTableModel() {
        return treeTableModel;
    }
    
    /**
     * Returns the object for the specified row
     *
     * @param row row number
     */
    public Object getNodeForRow(int row) {
        TreePath tp = tree.getPathForRow(row);
        // debug UTUtils.LOGGER.fine(row + " -> " + tp);
        return tp.getLastPathComponent();
    }
    
    /**
     * Returns the row corresponding to the specified path
     *
     * @param path path to a node
     * @return corresponding row in the table
     */
    public int getRowForPath(TreePath path) {
        return tree.getRowForPath(path);
    }
    
    /**
     * Overridden to message super and forward the method to the tree.
     * Since the tree is not actually in the component hieachy it will
     * never receive this unless we forward it in this manner.
     */
    public void updateUI() {
	super.updateUI();
	if(tree != null) {
	    tree.updateUI();
	    // Do this so that the editor is referencing the current renderer
	    // from the tree. The renderer can potentially change each time
	    // laf changes.
	    setDefaultEditor(TreeTableModel.class, new TreeTableCellEditor());
	}
	// Use the tree's default foreground and background colors in the
	// table. 
        LookAndFeel.installColorsAndFont(this, "Tree.background", // NOI18N
                                         "Tree.foreground", "Tree.font"); // NOI18N
    }

    /**
     * Returns the actual row that is editing as <code>getEditingRow</code>
     * will always return -1.
     */
    private int realEditingRow() {
	return editingRow;
    }

    /**
     * This is overridden to invoke super's implementation, and then,
     * if the receiver is editing a Tree column, the editor's bounds is
     * reset. The reason we have to do this is because JTable doesn't
     * think the table is being edited, as <code>getEditingRow</code> returns
     * -1, and therefore doesn't automatically resize the editor for us.
     */
    public void sizeColumnsToFit(int resizingColumn) { 
	super.sizeColumnsToFit(resizingColumn);
	if (getEditingColumn() != -1 && getColumnClass(editingColumn) ==
	    TreeTableModel.class) {
	    Rectangle cellRect = getCellRect(realEditingRow(),
					     getEditingColumn(), false);
            Component component = getEditorComponent();
	    component.setBounds(cellRect);
            component.validate();
	}
    }

    /**
     * Overridden to invoke repaint for the particular location if
     * the column contains the tree. This is done as the tree editor does
     * not fill the bounds of the cell, we need the renderer to paint
     * the tree in the background, and then draw the editor over it.
     */
    public boolean editCellAt(int row, int column, EventObject e){
        if (cellEditor != null && !cellEditor.stopCellEditing()) {
            return false;
        }

	if (row < 0 || row >= getRowCount() ||
	    column < 0 || column >= getColumnCount()) {
	    return false;
	}

        if (!isCellEditable(row, column)) {
            TableCellEditor editor = getCellEditor(row, column);
            if (editor != null) 
                editor.isCellEditable(e);
            return false;
        }

	boolean retValue = super.editCellAt(row, column, e);
	if (retValue && getColumnClass(column) == TreeTableModel.class) {
	    repaint(getCellRect(row, column, false));
	}
	return retValue;
    }
    
    /**
    /* Workaround for BasicTableUI anomaly. Make sure the UI never tries to 
     * paint the editor. The UI currently uses different techniques to 
     * paint the renderers and editors and overriding setBounds() below 
     * is not the right thing to do for an editor. Returning -1 for the 
     * editing row in this case, ensures the editor is never painted. 
     */
    public int getEditingRow() {
        return (getColumnClass(editingColumn) == TreeTableModel.class) ? -1 :
	        editingRow;  
    }

    /**
     * Overridden to pass the new rowHeight to the tree.
     */
    public void setRowHeight(int rowHeight) { 
        super.setRowHeight(rowHeight); 
	if (tree != null && tree.getRowHeight() != rowHeight) {
            tree.setRowHeight(getRowHeight()); 
	}
    }

    /**
     * Returns the tree that is being shared between the model.
     */
    public JTree getTree() {
	return tree;
    }

    protected javax.swing.table.JTableHeader createDefaultTableHeader() {
        return new SortableTableHeader(columnModel);
    }

    /**
     * Serialization of a node. Instead of serializing nodes some sort of
     * handles to this nodes will be serialized. Override this method
     * to return some handle object. Default implementation just return 
     * <code>node</code>
     *
     * @param node a node from this tree
     * @return handle for the node. != null
     */
    protected Serializable writeReplaceNode(Object node) {
        return (Serializable) node;
    }
    
    /**
     * Deserialization of a node. Override this method to resolve
     * node object during deserialization.
     *
     * @param parent parent node from this TT
     * @param node object read from the stream
     * @return a node from this tree or null
     */
    protected Object readResolveNode(Object parent, Object node) {
        return node;
    }

    /**
     * Returns the columns configuration that could be serialized.
     *
     * @return columns configuration (visible columns, sorting etc.)
     */
    public ColumnsConfig getColumnsConfig() {
        ColumnsConfig cc = new ColumnsConfig();
        
        TableColumnModel ctm = getColumnModel();
        assert ctm != null : "ctm == null"; // NOI18N
        
        cc.columns = new int[ctm.getColumnCount()];
        cc.columnWidths = new int[ctm.getColumnCount()];
        for (int i = 0; i < ctm.getColumnCount(); i++) {
            TableColumn c = ctm.getColumn(i);
            cc.columns[i] = c.getModelIndex();
            cc.columnWidths[i] = c.getWidth();
        }
        
        cc.sortedColumn = getSortingModel().getSortedColumn();
        cc.ascending = !getSortingModel().isSortOrderDescending();
        
        return cc;
    }
    
    /**
     * Sets columns configuration read from a stream.
     *
     * @param config columns configuration
     */
    public void setColumnsConfig(ColumnsConfig config) {
        assert config != null : "config == null"; // NOI18N
        
        this.createDefaultColumnsFromModel();

        ColumnsConfig cc = (ColumnsConfig) config;
        
        ArrayList<TableColumn> newc = new ArrayList<TableColumn>();
        TableColumnModel tcm = getColumnModel();
        assert tcm != null : "tcm == null"; // NOI18N

        for (int i = 0; i < cc.columns.length; i++) {
            for (int j = 0; j < tcm.getColumnCount(); j++) {
                TableColumn c = tcm.getColumn(j);
                if (cc.columns[i] == c.getModelIndex()) {
                    newc.add(c);
                    tcm.removeColumn(c);
                    c.setPreferredWidth(cc.columnWidths[i]);
                    c.setWidth(cc.columnWidths[i]);
                    break;
                }
            }
        }
        while (tcm.getColumnCount() > 0) {
            tcm.removeColumn(tcm.getColumn(0));
        }
        for (int i = 0; i < newc.size(); i ++) {
            tcm.addColumn(newc.get(i));
        }
    }

    /**
     * Returns all expanded nodes
     *
     * @return expanded nodes
     */
    public TreePath[] getExpandedNodes() {
        Enumeration<TreePath> en = tree.getExpandedDescendants( 
            new TreePath(getTreeTableModel().getRoot()));
        
        List<TreePath> paths = new ArrayList<TreePath>();
        if (en != null) {
            while (en.hasMoreElements()) {
                paths.add(en.nextElement());
            }
        }
        
        return paths.toArray(new TreePath[paths.size()]);
    }
    
    /**
     * Sets expanded nodes
     *
     * @param n expanded nodes
     */
    public void setExpandedNodes(TreePath[] n) {
        for (int i = 0; i < n.length; i++) {
            TreePath tp = n[i];
            expandPath(tp);
        }
    }
    
    /**
     * Replaces expanded nodes from this TT for writing into a stream.
     *
     * @param n nodes to be written
     * @return replacement
     */
    public Serializable writeReplaceExpandedNodes(TreePath[] n) {
        List<Serializable> paths = new ArrayList<Serializable>();
        for (int i = 0; i < n.length; i++) {
            paths.add(writeReplaceTreePath((TreePath) n[i]));
        }
        
        return (Serializable) paths;
    }
    
    /**
     * Resolves expanded nodes in this TT from an object read from a stream.
     *
     * @return ser expanded nodes or null
     * @param o read object
     */
    public TreePath[] readResolveExpandedNodes(Object ser) {
        if (ser == null)
            return new TreePath[0];
        
        List<TreePath> ret = new ArrayList<TreePath>();
        List l = (List) ser;
        for (int i = 0; i < l.size(); i++) {
            TreePath tp = readResolveTreePath(l.get(i));
            if (tp != null)
                ret.add(tp);
        }
        
        return ret.toArray(new TreePath[ret.size()]);
    }

    /**
     * Replaces a tree path from this TT for writing into a stream.
     *
     * @param tp a path from this TT
     * @return replacement
     */
    public Serializable writeReplaceTreePath(TreePath tp) {
        Object[] p = tp.getPath();
        p[0] = null;
        for (int i = 1; i < p.length; i++) {
            p[i] = writeReplaceNode(p[i]);
        }
        return p;
    }
    
    /**
     * Resolves a path in this TT from an object read from a stream.
     *
     * @return tp a path from this TT or null
     * @param o read object
     */
    public TreePath readResolveTreePath(Object o) {
        Object[] p = (Object[]) o;
        p[0] = getTreeTableModel().getRoot();
        for (int i = 1; i < p.length; i++) {
            p[i] = readResolveNode(p[i - 1], p[i]);
            if (p[i] == null)
                return null;
        }
        return new TreePath(p);
    }

    /**
     * Returns all expanded nodes under the specified one.
     *
     * @param path a path
     * @return all expanded paths (possibly one with <code>n</code> as the
     * last component)
     */
    public TreePath[] getExpandedNodesUnder(TreePath path) {
        Enumeration<TreePath> en = tree.getExpandedDescendants(path);
        
        List<TreePath> paths = new ArrayList<TreePath>();
        if (en != null) {
            while (en.hasMoreElements()) {
                paths.add(en.nextElement());
            }
        }
        
        return paths.toArray(new TreePath[paths.size()]);
    }

    /*
     * For profiling only.
     *
    public void paint(Graphics g) {
        super.paint(g);
    }*/
    
    /**
     * A TreeCellRenderer that displays a JTree.
     */
    public class TreeTableCellRenderer extends JTree implements
	         TableCellRenderer {

        private static final long serialVersionUID = 1;

	/** Last table/tree row asked to renderer. */
	protected int visibleRow;
        private Border border;

	public TreeTableCellRenderer(TreeModel model) {
	    super(model); 
	}

	/**
	 * updateUI is overridden to set the colors of the Tree's renderer
	 * to match that of the table.
	 */
	public void updateUI() {
	    super.updateUI();
	    // Make the tree's cell renderer use the table's cell selection
	    // colors. 
	    TreeCellRenderer tcr = getCellRenderer();
	    if (tcr instanceof DefaultTreeCellRenderer) {
		DefaultTreeCellRenderer dtcr = ((DefaultTreeCellRenderer)tcr); 
		// For 1.1 uncomment this, 1.2 has a bug that will cause an
		// exception to be thrown if the border selection color is
		// null.
		// dtcr.setBorderSelectionColor(null);
		dtcr.setTextSelectionColor(UIManager.getColor
					   ("Table.selectionForeground")); // NOI18N
		dtcr.setBackgroundSelectionColor(UIManager.getColor
						("Table.selectionBackground")); // NOI18N
	    }
	}

	/**
	 * Sets the row height of the tree, and forwards the row height to
	 * the table.
	 */
	public void setRowHeight(int rowHeight) {
	    if (rowHeight > 0) {
		super.setRowHeight(rowHeight); 
		if (TreeTable.this != null &&
		    TreeTable.this.getRowHeight() != rowHeight) {
		    TreeTable.this.setRowHeight(getRowHeight()); 
		}
	    }
	}

	/**
	 * This is overridden to set the height to match that of the JTable.
	 */
	public void setBounds(int x, int y, int w, int h) {
	    super.setBounds(x, 0, w, TreeTable.this.getHeight());
	}

	/**
	 * Sublcassed to translate the graphics such that the last visible
	 * row will be drawn at 0,0.
	 */
	public void paint(Graphics g) {
            /*Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);*/
        
	    g.translate(0, -visibleRow * getRowHeight());
	    super.paint(g);
            g.translate(0, visibleRow * getRowHeight());
            if (border != null)
                border.paintBorder(this, g, 0, 0, getWidth(), 
                        getRowHeight() - TreeTable.this.getRowMargin());
	}

	/**
	 * TreeCellRenderer method. Overridden to update the visible row.
	 * Original code
	public Component getTableCellRendererComponent(JTable table,
						       Object value,
						       boolean isSelected,
						       boolean hasFocus,
						       int row, int column) {
	    Color background;
	    Color foreground;

	    if(isSelected) {
		background = table.getSelectionBackground();
		foreground = table.getSelectionForeground();
	    }
	    else {
		background = table.getBackground();
		foreground = table.getForeground();
	    }
	    highlightBorder = null;
	    if (realEditingRow() == row && getEditingColumn() == column) {
		background = UIManager.getColor("Table.focusCellBackground");
		foreground = UIManager.getColor("Table.focusCellForeground");
	    }
	    else if (hasFocus) {
		highlightBorder = UIManager.getBorder
		                  ("Table.focusCellHighlightBorder");
		if (isCellEditable(row, column)) {
		    background = UIManager.getColor
			         ("Table.focusCellBackground");
		    foreground = UIManager.getColor
			         ("Table.focusCellForeground");
		}
	    }

	    visibleRow = row;
	    setBackground(background);
	    
	    TreeCellRenderer tcr = getCellRenderer();
	    if (tcr instanceof DefaultTreeCellRenderer) {
		DefaultTreeCellRenderer dtcr = ((DefaultTreeCellRenderer)tcr); 
		if (isSelected) {
		    dtcr.setTextSelectionColor(foreground);
		    dtcr.setBackgroundSelectionColor(background);
		}
		else {
		    dtcr.setTextNonSelectionColor(foreground);
		    dtcr.setBackgroundNonSelectionColor(background);
		}
	    }
	    return this;
	}
    }*/
	/**
	 * TreeCellRenderer method. Overridden to update the visible row.
	 */
	public Component getTableCellRendererComponent(JTable table,
						       Object value,
						       boolean isSelected,
						       boolean hasFocus,
						       int row, int column) {
            if (hasFocus) {
                border = UIManager.getBorder("Table.focusCellHighlightBorder"); // NOI18N
                if (table.isCellEditable(row, column)) {
                    super.setForeground( UIManager.getColor("Table.focusCellForeground") ); // NOI18N
                    super.setBackground( UIManager.getColor("Table.focusCellBackground") ); // NOI18N
                }
            } else {
                border = null;
            }
            
	    if(isSelected)
		setBackground(table.getSelectionBackground());
	    else
		setBackground(table.getBackground());

	    visibleRow = row;
	    return this;
	}
    }


    /**
     * An editor that can be used to edit the tree column. This extends
     * DefaultCellEditor and uses a JTextField (actually, TreeTableTextField)
     * to perform the actual editing.
     * <p>To support editing of the tree column we can not make the tree
     * editable. The reason this doesn't work is that you can not use
     * the same component for editing and renderering. The table may have
     * the need to paint cells, while a cell is being edited. If the same
     * component were used for the rendering and editing the component would
     * be moved around, and the contents would change. When editing, this
     * is undesirable, the contents of the text field must stay the same,
     * including the caret blinking, and selections persisting. For this
     * reason the editing is done via a TableCellEditor.
     * <p>Another interesting thing to be aware of is how tree positions
     * its render and editor. The render/editor is responsible for drawing the
     * icon indicating the type of node (leaf, branch...). The tree is
     * responsible for drawing any other indicators, perhaps an additional
     * +/- sign, or lines connecting the various nodes. So, the renderer
     * is positioned based on depth. On the other hand, table always makes
     * its editor fill the contents of the cell. To get the allusion
     * that the table cell editor is part of the tree, we don't want the
     * table cell editor to fill the cell bounds. We want it to be placed
     * in the same manner as tree places it editor, and have table message
     * the tree to paint any decorations the tree wants. Then, we would
     * only have to worry about the editing part. The approach taken
     * here is to determine where tree would place the editor, and to override
     * the <code>reshape</code> method in the JTextField component to
     * nudge the textfield to the location tree would place it. Since
     * JTreeTable will paint the tree behind the editor everything should
     * just work. So, that is what we are doing here. Determining of
     * the icon position will only work if the TreeCellRenderer is
     * an instance of DefaultTreeCellRenderer. If you need custom
     * TreeCellRenderers, that don't descend from DefaultTreeCellRenderer, 
     * and you want to support editing in JTreeTable, you will have
     * to do something similiar.
     */
    public class TreeTableCellEditor extends DefaultCellEditor {
	public TreeTableCellEditor() {
	    super(new TreeTableTextField());
	}

	/**
	 * Overridden to determine an offset that tree would place the
	 * editor at. The offset is determined from the
	 * <code>getRowBounds</code> JTree method, and additionally
	 * from the icon DefaultTreeCellRenderer will use.
	 * <p>The offset is then set on the TreeTableTextField component
	 * created in the constructor, and returned.
	 */
	public Component getTableCellEditorComponent(JTable table,
						     Object value,
						     boolean isSelected,
						     int r, int c) {
	    Component component = super.getTableCellEditorComponent
		(table, value, isSelected, r, c);
	    JTree t = getTree();
	    Rectangle bounds = t.getRowBounds(r);
	    int offset = bounds.x;
            Rectangle cb = TreeTable.this.getCellRect(r, c, false);
            offset += cb.x;
            offset += 19;
	    ((TreeTableTextField)getComponent()).offset = offset;
	    return component;
	}

	/**
	 * This is overridden to forward the event to the tree. This will
	 * return true if the click count >= 3, or the event is null.
	 */
	public boolean isCellEditable(EventObject e) {
            /* DEBUG if (UTUtils.LOGGER.isLoggable(Level.FINE))
                Thread.dumpStack();
            UTUtils.LOGGER.fine(e.toString());*/
            if (e instanceof MouseEvent) {
		MouseEvent me = (MouseEvent)e;
                
		// If the modifiers are not 0 (or the left mouse button),
                // tree may try and toggle the selection, and table
                // will then try and toggle, resulting in the
                // selection remaining the same. To avoid this, we
                // only dispatch when the modifiers are 0 (or the left mouse
                // button).
		if (me.getModifiers() == 0 ||
                    me.getModifiers() == InputEvent.BUTTON1_MASK) {
		    for (int counter = getColumnCount() - 1; counter >= 0;
			 counter--) {
			if (getColumnClass(counter) == TreeTableModel.class) {
			    MouseEvent newME = new MouseEvent
			          (TreeTable.this.tree, me.getID(),
				   me.getWhen(), me.getModifiers(),
				   me.getX() - getCellRect(0, counter, true).x,
				   me.getY(), me.getClickCount(),
                                   me.isPopupTrigger());
			    TreeTable.this.tree.dispatchEvent(newME);
			    break;
			}
		    }
		}
                /*
                int row = TreeTable.this.rowAtPoint(me.getPoint());
                int col = TreeTable.this.columnAtPoint(me.getPoint());
                int selCol = TreeTable.this.getSelectedColumn();
                int selRow = TreeTable.this.getSelectedRow();
                if (row == selRow && col == selCol)
                    return true;
                 */
		if (me.getClickCount() >= 3) {
		    return true;
		}
		return false;
	    }
	    if (e == null) {
		return true;
	    }
	    return super.isCellEditable(e);
	}
    }

    /**
     * Component used by TreeTableCellEditor. The only thing this does
     * is to override the <code>reshape</code> method, and to ALWAYS
     * make the x location be <code>offset</code>.
     */
    static class TreeTableTextField extends JTextField {
	public int offset;

	public void reshape(int x, int y, int w, int h) {
	    int newX = Math.max(x, offset);
	    super.reshape(newX, y, w - (newX - x), h);
	}
    }


    /**
     * ListToTreeSelectionModelWrapper extends DefaultTreeSelectionModel
     * to listen for changes in the ListSelectionModel it maintains. Once
     * a change in the ListSelectionModel happens, the paths are updated
     * in the DefaultTreeSelectionModel.
     */
    class ListToTreeSelectionModelWrapper extends DefaultTreeSelectionModel {

        private static final long serialVersionUID = 1;

	/** Set to true when we are updating the ListSelectionModel. */
	protected boolean         updatingListSelectionModel;

	public ListToTreeSelectionModelWrapper() {
	    super();
	    getListSelectionModel().addListSelectionListener
	                            (createListSelectionListener());
	}

	/**
	 * Returns the list selection model. ListToTreeSelectionModelWrapper
	 * listens for changes to this model and updates the selected paths
	 * accordingly.
	 */
	ListSelectionModel getListSelectionModel() {
	    return listSelectionModel; 
	}

	/**
	 * This is overridden to set <code>updatingListSelectionModel</code>
	 * and message super. This is the only place DefaultTreeSelectionModel
	 * alters the ListSelectionModel.
	 */
	public void resetRowSelection() {
	    if(!updatingListSelectionModel) {
		updatingListSelectionModel = true;
		try {
		    super.resetRowSelection();
		}
		finally {
		    updatingListSelectionModel = false;
		}
	    }
	    // Notice how we don't message super if
	    // updatingListSelectionModel is true. If
	    // updatingListSelectionModel is true, it implies the
	    // ListSelectionModel has already been updated and the
	    // paths are the only thing that needs to be updated.
	}

	/**
	 * Creates and returns an instance of ListSelectionHandler.
	 */
	protected ListSelectionListener createListSelectionListener() {
	    return new ListSelectionHandler();
	}

	/**
	 * If <code>updatingListSelectionModel</code> is false, this will
	 * reset the selected paths from the selected rows in the list
	 * selection model.
	 */
	protected void updateSelectedPathsFromSelectedRows() {
	    if(!updatingListSelectionModel) {
		updatingListSelectionModel = true;
		try {
		    // This is way expensive, ListSelectionModel needs an
		    // enumerator for iterating.
		    int        min = listSelectionModel.getMinSelectionIndex();
		    int        max = listSelectionModel.getMaxSelectionIndex();

		    clearSelection();
		    if(min != -1 && max != -1) {
			for(int counter = min; counter <= max; counter++) {
			    if(listSelectionModel.isSelectedIndex(counter)) {
				TreePath     selPath = tree.getPathForRow
				                            (counter);

				if(selPath != null) {
				    addSelectionPath(selPath);
				}
			    }
			}
		    }
		}
		finally {
		    updatingListSelectionModel = false;
		}
	    }
	}

	/**
	 * Class responsible for calling updateSelectedPathsFromSelectedRows
	 * when the selection of the list changse.
	 */
	class ListSelectionHandler implements ListSelectionListener {
	    public void valueChanged(ListSelectionEvent e) {
		updateSelectedPathsFromSelectedRows();
	    }
	}
    }
}
