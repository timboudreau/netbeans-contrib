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

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumnModel;
import javax.swing.tree.TreePath;
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
import org.netbeans.modules.tasklist.usertasks.renderers.CategoryTableCellRenderer;
import org.netbeans.modules.tasklist.usertasks.renderers.DateTableCellRenderer;
import org.netbeans.modules.tasklist.usertasks.renderers.DurationTableCellRenderer;
import org.netbeans.modules.tasklist.usertasks.renderers.EffortTableCellRenderer;
import org.netbeans.modules.tasklist.usertasks.renderers.LineTableCellRenderer;
import org.netbeans.modules.tasklist.usertasks.renderers.PercentsTableCellRenderer;
import org.netbeans.modules.tasklist.usertasks.renderers.PriorityTableCellRenderer;
import org.netbeans.modules.tasklist.usertasks.renderers.SummaryTreeCellRenderer;
import org.netbeans.modules.tasklist.usertasks.treetable.AdvancedTreeTableNode;
import org.netbeans.modules.tasklist.usertasks.treetable.BooleanTableCellRenderer;
import org.netbeans.modules.tasklist.usertasks.treetable.DefaultMutableTreeTableNode;
import org.netbeans.modules.tasklist.usertasks.treetable.DefaultTreeTableModel;
import org.netbeans.modules.tasklist.usertasks.treetable.NodesTreeTable;
import org.netbeans.modules.tasklist.usertasks.treetable.SortingHeaderRenderer;
import org.netbeans.modules.tasklist.usertasks.treetable.TreeTable;
import org.openide.explorer.ExplorerManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.nodes.Node;
import org.openide.util.actions.SystemAction;
import org.netbeans.modules.tasklist.usertasks.model.UserTask;
import org.netbeans.modules.tasklist.usertasks.model.UserTaskList;

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
            new DefaultMutableTreeTableNode(), new String[] {""})); // NOI18N
        setTreeTableModel(
            new UserTasksTreeTableModel(utl, getSortingModel(), filter));
        setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        setShowHorizontalLines(true);
        setShowVerticalLines(true);
        setAutoResizeMode(AUTO_RESIZE_OFF);
        getTree().setCellRenderer(new SummaryTreeCellRenderer());
        getTree().setShowsRootHandles(true);
        getTree().setToggleClickCount(3);
        
        setAutoCreateColumnsFromModel(false);
        
        if (UTUtils.LOGGER.isLoggable(Level.FINER)) {
            getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {
                    public void valueChanged(ListSelectionEvent e) {
                        UTUtils.LOGGER.fine(e.getFirstIndex() + " " +  // NOI18N
                            e.getLastIndex() + " " +  // NOI18N
                            e.getValueIsAdjusting() + " " +  // NOI18N
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
        
        setColumnsConfig(createDefaultColumnsConfig());
    }

    /**
     * Creates default columns configuration
     *
     * @return default columns
     */
    private TreeTable.ColumnsConfig createDefaultColumnsConfig() {
        TreeTable.ColumnsConfig ret = new TreeTable.ColumnsConfig();
        ret.ascending = false;
        ret.columnWidths = new int[] {18, 18, 400, 60, 60, 60, 80, 80, 80};
        ret.sortedColumn = UserTaskTreeTableNode.PRIORITY;
        ret.columns = new int[] {
            UserTaskTreeTableNode.DONE,
            UserTaskTreeTableNode.PRIORITY,
            UserTaskTreeTableNode.SUMMARY,
            UserTaskTreeTableNode.CATEGORY,
            UserTaskTreeTableNode.OWNER,
            UserTaskTreeTableNode.PERCENT_COMPLETE,
            UserTaskTreeTableNode.EFFORT,
            UserTaskTreeTableNode.REMAINING_EFFORT,
            UserTaskTreeTableNode.SPENT_TIME
        };
        return ret;
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

    public void createDefaultColumnsFromModel() {
        super.createDefaultColumnsFromModel();
            
        TableColumnModel tcm = getColumnModel();
        if (tcm.getColumnCount() < 14)
            return;
        
        SortingHeaderRenderer r = new SortingHeaderRenderer();
        r.setIcon(new ImageIcon(
            UserTasksTreeTable.class.getResource("priority.gif"))); // NOI18N
        tcm.getColumn(UserTaskTreeTableNode.PRIORITY).setHeaderRenderer(r);
        tcm.getColumn(UserTaskTreeTableNode.PRIORITY).setCellRenderer(
            new PriorityTableCellRenderer());
        
        r = new SortingHeaderRenderer();
        r.setIcon(new ImageIcon(
            UserTasksTreeTable.class.getResource("checkbox.gif"))); // NOI18N
        tcm.getColumn(UserTaskTreeTableNode.DONE).setHeaderRenderer(r);
        tcm.getColumn(UserTaskTreeTableNode.DONE).setCellRenderer(
            new BooleanTableCellRenderer());
        tcm.getColumn(UserTaskTreeTableNode.DONE).setMinWidth(17);
        tcm.getColumn(UserTaskTreeTableNode.PERCENT_COMPLETE).setCellRenderer(
            new PercentsTableCellRenderer());
        DurationTableCellRenderer dr = new DurationTableCellRenderer();
        tcm.getColumn(UserTaskTreeTableNode.REMAINING_EFFORT).setCellRenderer(dr);
        tcm.getColumn(UserTaskTreeTableNode.SPENT_TIME).setCellRenderer(dr);
        tcm.getColumn(UserTaskTreeTableNode.LINE_NUMBER).setCellRenderer(
            new LineTableCellRenderer());
        DateTableCellRenderer dcr = new DateTableCellRenderer();
        tcm.getColumn(UserTaskTreeTableNode.CREATED).setCellRenderer(dcr);
        tcm.getColumn(UserTaskTreeTableNode.LAST_EDITED).setCellRenderer(dcr);
        tcm.getColumn(UserTaskTreeTableNode.DUE_DATE).setCellRenderer(dcr);
        tcm.getColumn(UserTaskTreeTableNode.COMPLETED_DATE)
            .setCellRenderer(dcr);
        tcm.getColumn(UserTaskTreeTableNode.PERCENT_COMPLETE).
            setCellEditor(new PercentsTableCellEditor());
        
        tcm.getColumn(UserTaskTreeTableNode.CATEGORY).
            setCellEditor(new CategoryTableCellEditor());
        tcm.getColumn(UserTaskTreeTableNode.CATEGORY).
            setCellRenderer(new CategoryTableCellRenderer());
        
        tcm.getColumn(UserTaskTreeTableNode.PRIORITY).setCellEditor(
            new PriorityTableCellEditor());
        tcm.getColumn(UserTaskTreeTableNode.PRIORITY).setCellRenderer(
            new PriorityTableCellRenderer());
        
        tcm.getColumn(UserTaskTreeTableNode.EFFORT).setCellRenderer(
            new EffortTableCellRenderer());
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

    public String getToolTipText(java.awt.event.MouseEvent event) {
        Point point = event.getPoint();
        int row = rowAtPoint(point);
        
        String result = null;
        if (row >= 0) {
            Object node = getNodeForRow(row);

            if (node instanceof UserTaskTreeTableNode) {
                result = ((UserTaskTreeTableNode) node).getUserTask().getDetails();
                if (result.length() == 0)
                    result = null;
            } else if (node instanceof UserTaskListTreeTableNode) {
                FileObject fo = ((UserTaskListTreeTableNode) node).
                    getUserTaskList().getFile();
                result = FileUtil.getFileDisplayName(fo);
            }
        }
        return result;
    }

    public void setColumnModel(TableColumnModel columnModel) {
        if (UTUtils.LOGGER.isLoggable(Level.FINE))
            Thread.dumpStack();
        
        super.setColumnModel(columnModel);
    }
}
