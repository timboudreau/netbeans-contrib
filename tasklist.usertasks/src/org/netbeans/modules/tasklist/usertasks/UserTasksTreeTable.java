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

package org.netbeans.modules.tasklist.usertasks;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;

import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.tree.TreePath;

import org.netbeans.modules.tasklist.client.SuggestionPriority;
import org.netbeans.modules.tasklist.core.columns.ColumnsConfiguration;
import org.netbeans.modules.tasklist.core.editors.PriorityTableCellRenderer;
import org.netbeans.modules.tasklist.core.export.ExportAction;
import org.netbeans.modules.tasklist.core.export.ImportAction;
import org.netbeans.modules.tasklist.core.filter.Filter;
import org.netbeans.modules.tasklist.usertasks.actions.CollapseAllAction;
import org.netbeans.modules.tasklist.usertasks.actions.ExpandAllUserTasksAction;
import org.netbeans.modules.tasklist.usertasks.actions.NewTaskAction;
import org.netbeans.modules.tasklist.usertasks.actions.NewTaskListAction;
import org.netbeans.modules.tasklist.usertasks.editors.CategoryTableCellEditor;
import org.netbeans.modules.tasklist.usertasks.editors.PercentsTableCellEditor;
import org.netbeans.modules.tasklist.usertasks.editors.PriorityTableCellEditor;
import org.netbeans.modules.tasklist.usertasks.filter.FilterUserTaskAction;
import org.netbeans.modules.tasklist.usertasks.renderers.DateTableCellRenderer;
import org.netbeans.modules.tasklist.usertasks.renderers.DurationTableCellRenderer;
import org.netbeans.modules.tasklist.usertasks.renderers.LineTableCellRenderer;
import org.netbeans.modules.tasklist.usertasks.renderers.PercentsTableCellRenderer;
import org.netbeans.modules.tasklist.usertasks.renderers.SummaryTreeCellRenderer;
import org.netbeans.modules.tasklist.usertasks.treetable.AdvancedTreeTableNode;
import org.netbeans.modules.tasklist.usertasks.treetable.BooleanTableCellRenderer;
import org.netbeans.modules.tasklist.usertasks.treetable.DefaultMutableTreeTableNode;
import org.netbeans.modules.tasklist.usertasks.treetable.DefaultTreeTableModel;
import org.netbeans.modules.tasklist.usertasks.treetable.NodesTreeTable;
import org.netbeans.modules.tasklist.usertasks.treetable.SortingHeaderRenderer;
import org.openide.explorer.ExplorerManager;
import org.openide.nodes.Node;
import org.openide.util.actions.SystemAction;

/**
 * TT for user tasks
 */
public class UserTasksTreeTable extends NodesTreeTable {
    private UserTask selected;
    
    /**
     * Creates a new instance of UserTasksTreeTable
     * 
     * @param em ExplorerManager associated with this tree table
     * @param utl list with user tasks
     * @param filter used filter or null
     */
    public UserTasksTreeTable(ExplorerManager em, UserTaskList utl,
    Filter filter) {
        super(em, new DefaultTreeTableModel(
            new DefaultMutableTreeTableNode(), new String[] {""}));
        setTreeTableModel(
            new UserTasksTreeTableModel(utl, getSortingModel(), filter));
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setShowHorizontalLines(true);
        setShowVerticalLines(true);
        setAutoResizeMode(AUTO_RESIZE_OFF);
        getTree().setCellRenderer(new SummaryTreeCellRenderer());
        getTree().setShowsRootHandles(true);
        getTree().setToggleClickCount(3);
        
        setAutoCreateColumnsFromModel(false);
        setDefaultEditor(SuggestionPriority.class, new PriorityTableCellEditor());
        
        if (UTUtils.LOGGER.isLoggable(Level.FINER)) {
            getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {
                    public void valueChanged(ListSelectionEvent e) {
                        UTUtils.LOGGER.fine(e.getFirstIndex() + " " + 
                            e.getLastIndex() + " " + 
                            e.getValueIsAdjusting() + " " + 
                            UserTasksTreeTable.this.getSelectedRow());
                        if (UTUtils.LOGGER.isLoggable(Level.FINER))
                            Thread.dumpStack();
                    }
                }
            );
        }
        
        getSelectionModel().addListSelectionListener(
            new ListSelectionListener() {
                public void valueChanged(ListSelectionEvent e) {
                    int row = getSelectedRow();
                    UserTask ut = null;
                    if (row >= 0) {
                        Object node = getNodeForRow(row);
                        if (node instanceof UserTaskTreeTableNode) {
                            ut = ((UserTaskTreeTableNode) node).getUserTask();
                        }
                    }
                    if (selected != null && selected.getAnnotation() != null) 
                        selected.getAnnotation().setHighlight(false);
                    selected = ut;
                    if (selected != null && selected.getAnnotation() != null)
                        selected.getAnnotation().setHighlight(true);
                }
            }
        );
    }

    public Node createNode(Object obj) {
        if (obj instanceof UserTaskListTreeTableNode) {
            UserTaskListTreeTableNode n = (UserTaskListTreeTableNode) obj;
            UserTaskList utl = n.getUserTaskList();
            return new UserTaskListNode(utl, this);
        } else {
            UserTaskList utl = ((UserTasksTreeTableModel) getTreeTableModel()).
                getUserTaskList();
            UserTaskTreeTableNode node = (UserTaskTreeTableNode) obj;
            UserTask ut = node.getUserTask();
            return new UserTaskNode(node, ut, utl, this);
        }
    }

    /**
     * Configures columns in this tree table
     *
     * @param cc columns configuration
     */
    public void loadColumns(ColumnsConfiguration cc) {
        assert cc != null : "cc == null"; // NOI18N
        
        this.createDefaultColumnsFromModel();

        ArrayList newc = new ArrayList();
        TableColumnModel tcm = getColumnModel();
        assert tcm != null : "tcm == null"; // NOI18N

        String[] p = cc.getProperties();
        String sc = cc.getSortingColumn();
        boolean so = cc.getSortingOrder();
        int[] w = cc.getWidths();
        
        for (int i = 0; i < p.length; i++) {
            for (int j = 0; j < tcm.getColumnCount(); j++) {
                String s = UserTasksTreeTableModel.COLUMN_PROPERTIES[
                    tcm.getColumn(j).getModelIndex()];
                if (s.equals(p[i])) {
                    TableColumn c = tcm.getColumn(j);
                    newc.add(c);
                    tcm.removeColumn(c);
                    c.setPreferredWidth(w[i]);
                    break;
                }
            }
        }
        while (tcm.getColumnCount() > 0) {
            tcm.removeColumn(tcm.getColumn(0));
        }
        for (int i = 0; i < newc.size(); i ++) {
            TableColumn c = (TableColumn) newc.get(i);
            tcm.addColumn(c);
        }
    }

    /**
     * Saves columns data
     *
     * @param cc columns configuration
     */
    public void storeColumns(ColumnsConfiguration cc) {
        assert cc != null : "cc == null"; // NOI18N
        
        TableColumnModel ctm = getColumnModel();
        assert ctm != null : "ctm == null"; // NOI18N
        
        int[] w = new int[ctm.getColumnCount()];
        String[] p = new String[ctm.getColumnCount()];
        for (int i = 0; i < ctm.getColumnCount(); i++) {
            TableColumn c = ctm.getColumn(i);
            w[i] = c.getWidth();
            p[i] = UserTasksTreeTableModel.COLUMN_PROPERTIES[c.getModelIndex()];
        }
        
        cc.setValues(p, w, null, false);
    }

    public void createDefaultColumnsFromModel() {
        super.createDefaultColumnsFromModel();
        TableColumnModel tcm = getColumnModel();
        if (tcm.getColumnCount() < 14)
            return;
        
        tcm.getColumn(1).setCellRenderer(
            new PriorityTableCellRenderer());
        SortingHeaderRenderer r = new SortingHeaderRenderer();
        r.setIcon(new ImageIcon(
            UserTasksTreeTable.class.getResource("checkbox.gif"))); // NOI18N
        tcm.getColumn(2).setHeaderRenderer(r);
        tcm.getColumn(2).setCellRenderer(
            new BooleanTableCellRenderer());
        tcm.getColumn(2).setMinWidth(17);
        tcm.getColumn(3).setCellRenderer(
            new PercentsTableCellRenderer());
        DurationTableCellRenderer dr = new DurationTableCellRenderer();
        tcm.getColumn(4).setCellRenderer(dr);
        tcm.getColumn(5).setCellRenderer(dr);
        tcm.getColumn(6).setCellRenderer(dr);
        tcm.getColumn(9).setCellRenderer(
            new LineTableCellRenderer());
        DateTableCellRenderer dcr = new DateTableCellRenderer();
        tcm.getColumn(11).setCellRenderer(dcr);
        tcm.getColumn(12).setCellRenderer(dcr);
        tcm.getColumn(13).setCellRenderer(dcr);
        tcm.getColumn(UserTaskTreeTableNode.PERCENT_COMPLETE).
            setCellEditor(new PercentsTableCellEditor());
        tcm.getColumn(UserTaskTreeTableNode.CATEGORY).
            setCellEditor(new CategoryTableCellEditor());
    }

    /**
     * Finds the path to the specified task
     *
     * @return found path or null
     */
    public TreePath findPath(UserTask task) {
        List l = new ArrayList();
        while (task != null) {
            l.add(0, task);
            task = (UserTask) task.getParent();
        }
        AdvancedTreeTableNode n = 
            (AdvancedTreeTableNode) getTreeTableModel().getRoot();
        
        for (int i = 0; i < l.size(); i++) {
            int index = n.getIndexOfObject(l.get(i));
            if (index == -1)
                return null;
            n = (AdvancedTreeTableNode) n.getChildAt(index);
        }
        return new TreePath(n.getPathToRoot());
    }
    
    public javax.swing.Action[] getFreeSpaceActions() {
        return new Action[] {
            SystemAction.get(NewTaskAction.class),
            SystemAction.get(NewTaskListAction.class),
            null,
            SystemAction.get(FilterUserTaskAction.class),
            null,
            SystemAction.get(ExpandAllUserTasksAction.class),
            SystemAction.get(CollapseAllAction.class),
            null,
            SystemAction.get(ImportAction.class),
            SystemAction.get(ExportAction.class),
        };
    }    

    protected Object writeReplaceNode(Object node) {
        return ((UserTaskTreeTableNode) node).getUserTask().getUID();
    }

    protected Object readResolveNode(Object parent, Object node) {
        AdvancedTreeTableNode p = (AdvancedTreeTableNode) parent;
        String uid = (String) node;
        
        for (int i = 0; i < p.getChildCount(); i++) {
            UserTaskTreeTableNode ch = 
                (UserTaskTreeTableNode) p.getChildAt(i);
            UserTask ut = ch.getUserTask();
            if (uid.equals(ut.getUID()))
                return ch;
        }
        
        return null;
    }
}
