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
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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

package org.netbeans.modules.tasklist.usertasks;

import org.netbeans.modules.tasklist.usertasks.treetable.AdvancedTreeTableNode;
import org.netbeans.modules.tasklist.usertasks.treetable.AdvancedTreeTableNode;
import org.netbeans.modules.tasklist.usertasks.table.UTBasicTreeTableNode;
import org.netbeans.modules.tasklist.usertasks.table.UTTreeTableModel;
import org.netbeans.modules.tasklist.usertasks.table.UTTreeTableNode;
import com.toedter.calendar.JDateChooserCellEditor;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.RepaintManager;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.tree.TreePath;
import org.netbeans.modules.tasklist.export.ExportAction;
import org.netbeans.modules.tasklist.export.ImportAction;
import org.netbeans.modules.tasklist.filter.Filter;
import org.netbeans.modules.tasklist.filter.FilterAction;
import org.netbeans.modules.tasklist.filter.RemoveFilterAction;

import org.netbeans.modules.tasklist.usertasks.actions.CollapseAllAction;
import org.netbeans.modules.tasklist.usertasks.actions.ExpandAllUserTasksAction;
import org.netbeans.modules.tasklist.usertasks.actions.GoToUserTaskAction;
import org.netbeans.modules.tasklist.usertasks.actions.PauseAction;
import org.netbeans.modules.tasklist.usertasks.actions.ScheduleAction;
import org.netbeans.modules.tasklist.usertasks.actions.StartTaskAction;
import org.netbeans.modules.tasklist.usertasks.editors.CategoryTableCellEditor;
import org.netbeans.modules.tasklist.usertasks.editors.EffortTableCellEditor;
import org.netbeans.modules.tasklist.usertasks.editors.OwnerTableCellEditor;
import org.netbeans.modules.tasklist.usertasks.editors.PercentsTableCellEditor;
import org.netbeans.modules.tasklist.usertasks.editors.PriorityTableCellEditor;
import org.netbeans.modules.tasklist.usertasks.renderers.PriorityTableCellRenderer;
import org.netbeans.modules.tasklist.usertasks.renderers.SummaryTreeCellRenderer;
import org.netbeans.modules.tasklist.usertasks.table.UTColumns;
import org.netbeans.modules.tasklist.usertasks.treetable.DefaultMutableTreeTableNode;
import org.netbeans.modules.tasklist.usertasks.treetable.DefaultTreeTableModel;
import org.netbeans.modules.tasklist.usertasks.treetable.TreeTable;
import org.netbeans.modules.tasklist.usertasks.treetable.TreeTableDragGestureRecognizer;
import org.netbeans.modules.tasklist.usertasks.util.UTUtils;
import org.openide.awt.MouseUtils;
import org.openide.nodes.Node;
import org.netbeans.modules.tasklist.usertasks.model.UserTask;
import org.netbeans.modules.tasklist.usertasks.model.UserTaskList;
import org.netbeans.modules.tasklist.usertasks.renderers.DateTableCellRenderer;
import org.netbeans.modules.tasklist.usertasks.renderers.DoneTreeTableCellRenderer;
import org.netbeans.modules.tasklist.usertasks.renderers.DueDateTableCellRenderer;
import org.netbeans.modules.tasklist.usertasks.renderers.DurationTableCellRenderer;
import org.netbeans.modules.tasklist.usertasks.renderers.EffortTableCellRenderer;
import org.netbeans.modules.tasklist.usertasks.renderers.LineTableCellRenderer;
import org.netbeans.modules.tasklist.usertasks.renderers.PercentsTableCellRenderer;
import org.netbeans.modules.tasklist.usertasks.renderers.URLTableCellRenderer;
import org.netbeans.modules.tasklist.usertasks.table.UTFlatTreeTableModel;
import org.netbeans.modules.tasklist.usertasks.table.UTListFlatTreeTableNode;
import org.netbeans.modules.tasklist.usertasks.transfer.MyTransferHandler;
import org.netbeans.modules.tasklist.usertasks.treetable.AdvancedTreeTableNode;
import org.netbeans.modules.tasklist.usertasks.treetable.SortingHeaderRenderer;
import org.openide.actions.CopyAction;
import org.openide.actions.CutAction;
import org.openide.actions.DeleteAction;
import org.openide.actions.PasteAction;
import org.openide.util.Utilities;
import org.openide.util.actions.SystemAction;
import org.openide.windows.WindowManager;

/**
 * TT for user tasks
 *
 * @author tl
 */
public class UserTasksTreeTable extends TreeTable {
    private UserTask selected;
    private UserTaskView utv;
    
    /**
     * Creates a new instance of UserTasksTreeTable
     * 
     * @param utv view
     * @param utl list with user tasks
     * @param filter used filter or null
     */
    public UserTasksTreeTable(UserTaskView utv, UserTaskList utl,
    Filter filter) {
        super(new DefaultTreeTableModel(
            new DefaultMutableTreeTableNode(), new String[] {""})); // NOI18N
        this.utv = utv;
        
        // this disables automatic scrolling if using keyboard
        // setAutoscrolls(false);
        setTreeTableModel(
                new UTTreeTableModel(utl, getSortingModel(), filter));
        setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        setShowHorizontalLines(true);
        setShowVerticalLines(true);
        setAutoResizeMode(AUTO_RESIZE_OFF);
        getTree().setCellRenderer(new SummaryTreeCellRenderer());
        getTree().setShowsRootHandles(true);
        getTree().setToggleClickCount(3);
        getTree().setRootVisible(false);
        
        setAutoCreateColumnsFromModel(false);
        
        /* DEBUG
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
         */
        
        /* is not used anymore
        getSelectionModel().addListSelectionListener(
            new ListSelectionListener() {
                public void valueChanged(ListSelectionEvent e) {
                    int row = getSelectedRow();
                    UserTask ut = null;
                    if (row >= 0) {
                        Object node = getNodeForRow(row);
                        if (node instanceof UTTreeTableNode) {
                            ut = ((UTTreeTableNode) node).getUserTask();
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
         */
        
        addMouseListener(new MouseUtils.PopupMouseAdapter() {
            public void showPopup(MouseEvent e) {
                int row = rowAtPoint(e.getPoint());
                int col = columnAtPoint(e.getPoint());
                Action[] actions;

                if (row < 0 || col < 0)
                    return;
                
                if (!getSelectionModel().isSelectedIndex(row)) {
                    setRowSelectionInterval(row, row);
                }
                actions = getActions_();

                JPopupMenu pm = Utilities.actionsToPopup(actions,
                        UserTasksTreeTable.this);

                if (pm != null)
                    pm.show(UserTasksTreeTable.this, e.getX(), e.getY());
            }
        });
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = rowAtPoint(e.getPoint());
                    if (!getSelectionModel().isSelectedIndex(row)) {
                        setRowSelectionInterval(row, row);
                    }
                    if (UserTasksTreeTable.this.utv.showTaskAction.isEnabled())
                        UserTasksTreeTable.this.utv.showTaskAction.
                                actionPerformed(new ActionEvent(
                                UserTasksTreeTable.this.utv.getTreeTable(), 0, 
                                "")); // NOI18N
                }
            }
        });
        
        setColumnsConfig(createDefaultColumnsConfig());
        
        TreeTableDragGestureRecognizer.enableDnD(this);
        setTransferHandler(new MyTransferHandler());
        setAutoscrolls(true);
    }

    @Override
    protected void configureEnclosingScrollPane() {
        super.configureEnclosingScrollPane();
        final JScrollPane sp = (JScrollPane) SwingUtilities.getAncestorOfClass(
                JScrollPane.class, this);
        sp.addMouseListener(new MouseUtils.PopupMouseAdapter() {
            public void showPopup(MouseEvent e) {
                JPopupMenu pm = Utilities.actionsToPopup(getActions_(),
                        UserTasksTreeTable.this);

                if (pm != null)
                    pm.show(sp, e.getX(), e.getY());
            }
        });
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
        ret.sortedColumn = UTColumns.PRIORITY;
        ret.columns = new int[] {
            UTColumns.DONE,
            UTColumns.PRIORITY,
            UTColumns.SUMMARY,
            UTColumns.CATEGORY,
            UTColumns.OWNER,
            UTColumns.PERCENT_COMPLETE,
            UTColumns.EFFORT,
            UTColumns.REMAINING_EFFORT,
            UTColumns.SPENT_TIME
        };
        return ret;
    }
    
    /**
     * Creates a org.openide.Node for an object
     * 
     * @param obj an object
     * @return create node
     */
    public Node createNode(Object obj) {
        UserTaskList utl = ((UTTreeTableModel) getTreeTableModel()).
            getUserTaskList();
        UTBasicTreeTableNode node = (UTBasicTreeTableNode) obj;
        UserTask ut = node.getUserTask();
        return new UserTaskNode(node, ut, utl, this);
    }

    public void createDefaultColumnsFromModel() {
        super.createDefaultColumnsFromModel();
            
        TableColumnModel tcm = getColumnModel();
        if (tcm.getColumnCount() < 14)
            return;
        
        JDateChooserCellEditor dc = new JDateChooserCellEditor();
        
        SortingHeaderRenderer r = new SortingHeaderRenderer();
        r.setIcon(new ImageIcon(
            UserTasksTreeTable.class.getResource("checkbox.gif"))); // NOI18N
        tcm.getColumn(UTColumns.DONE).setHeaderRenderer(r);
        tcm.getColumn(UTColumns.DONE).setCellRenderer(
            new DoneTreeTableCellRenderer());
        tcm.getColumn(UTColumns.DONE).setMinWidth(17);
        
        tcm.getColumn(UTColumns.PERCENT_COMPLETE).
            setCellEditor(new PercentsTableCellEditor());
        tcm.getColumn(UTColumns.PERCENT_COMPLETE).setCellRenderer(
            new PercentsTableCellRenderer());

        DurationTableCellRenderer dr = new DurationTableCellRenderer();
        tcm.getColumn(UTColumns.REMAINING_EFFORT).setCellRenderer(dr);

        tcm.getColumn(UTColumns.SPENT_TIME).setCellRenderer(dr);
        tcm.getColumn(UTColumns.SPENT_TIME).setCellEditor(
                new EffortTableCellEditor());

        DateTableCellRenderer dcr = new DateTableCellRenderer();
        tcm.getColumn(UTColumns.CREATED).setCellRenderer(dcr);

        tcm.getColumn(UTColumns.LAST_EDITED).setCellRenderer(dcr);

        tcm.getColumn(UTColumns.DUE_DATE).setCellRenderer(dcr);

        tcm.getColumn(UTColumns.COMPLETED_DATE)
            .setCellRenderer(dcr);

        tcm.getColumn(UTColumns.CATEGORY).
            setCellEditor(new CategoryTableCellEditor());
        tcm.getColumn(UTColumns.CATEGORY).
            setCellRenderer(new DefaultTableCellRenderer());
        
        SortingHeaderRenderer priorityRenderer = new SortingHeaderRenderer();
        priorityRenderer.setIcon(new ImageIcon(
            UserTasksTreeTable.class.getResource("priority.gif"))); // NOI18N
        tcm.getColumn(UTColumns.PRIORITY).setHeaderRenderer(
                priorityRenderer);
        tcm.getColumn(UTColumns.PRIORITY).setCellRenderer(
            new PriorityTableCellRenderer());
        tcm.getColumn(UTColumns.PRIORITY).setCellEditor(
            new PriorityTableCellEditor());
        tcm.getColumn(UTColumns.PRIORITY).setCellRenderer(
            new PriorityTableCellRenderer());
        
        tcm.getColumn(UTColumns.EFFORT).setCellRenderer(
            new EffortTableCellRenderer());
        tcm.getColumn(UTColumns.EFFORT).setCellEditor(
                new EffortTableCellEditor());

        tcm.getColumn(UTColumns.OWNER).
            setCellEditor(new OwnerTableCellEditor());
        tcm.getColumn(UTColumns.OWNER).
            setCellRenderer(new DefaultTableCellRenderer());
        
        tcm.getColumn(UTColumns.DUE_DATE).setCellRenderer(
            new DueDateTableCellRenderer());
        tcm.getColumn(UTColumns.DUE_DATE).setWidth(100);
        tcm.getColumn(UTColumns.DUE_DATE).setCellEditor(dc);
        
        tcm.getColumn(UTColumns.START).setCellRenderer(dcr);
        tcm.getColumn(UTColumns.START).setCellEditor(dc);

        tcm.getColumn(UTColumns.SPENT_TIME_TODAY).setCellRenderer(dr);
    }

    /**
     * Finds the path to the specified task
     *
     * @param task the task
     * @return found path or null
     */
    public TreePath findPath(UserTask task) {
        if (getTreeTableModel() instanceof UTTreeTableModel) {
            List<UserTask> l = new ArrayList<UserTask>();
            while (task != null) {
                l.add(0, task);
                task = task.getParent();
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
        } else {
            UTListFlatTreeTableNode root =  
                    (UTListFlatTreeTableNode) getTreeTableModel().getRoot();
            AdvancedTreeTableNode n = root.findObjectDeep(task);
            return new TreePath(n.getPathToRoot());
        }
    }
    
    public javax.swing.Action[] getActions_() {
        return new Action[] {
            utv.newTaskAction,
            //SystemAction.get(ShowScheduleViewAction.class),
            null,
            new StartTaskAction(utv),
            PauseAction.getInstance(),
            null,
            utv.showTaskAction,
            new GoToUserTaskAction(utv),
            null,
            SystemAction.get(CutAction.class),
            SystemAction.get(CopyAction.class),
            SystemAction.get(PasteAction.class),
            utv.pasteAtTopLevelAction,
            null,
            SystemAction.get(DeleteAction.class),
            null,
            utv.moveUpAction,
            utv.moveDownAction,
            utv.moveLeftAction,
            utv.moveRightAction,
            null,
            SystemAction.get(FilterAction.class),
            SystemAction.get(RemoveFilterAction.class),
            null,
            utv.purgeTasksAction,
            utv.clearCompletedAction,
            SystemAction.get(ScheduleAction.class),
            null,
            SystemAction.get(ExpandAllUserTasksAction.class),
            SystemAction.get(CollapseAllAction.class),
            null,
            SystemAction.get(ImportAction.class),
            SystemAction.get(ExportAction.class),

            // Property: node specific, but by convention last in menu
            null,
            utv.propertiesAction
        };
    }    

    protected Serializable writeReplaceNode(Object node) {
        if (node instanceof UTBasicTreeTableNode)
            return ((UTBasicTreeTableNode) node).getUserTask().getUID();
        else
            return null;
    }

    protected Object readResolveNode(Object parent, Object node) {
        if (node == null)
            return null;
        
        AdvancedTreeTableNode p = (AdvancedTreeTableNode) parent;
        String uid = (String) node;
        
        for (int i = 0; i < p.getChildCount(); i++) {
            UTBasicTreeTableNode ch = (UTBasicTreeTableNode) p.getChildAt(i);
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

            if (node instanceof UTTreeTableNode) {
                result = ((UTTreeTableNode) node).getUserTask().getDetails();
                result = UTUtils.prepareForTooltip(result);
                if (result.length() == 0)
                    result = null;
            }
        }
        return result;
    }

    /* DEBUG */
    public void paint(Graphics g) {
        super.paint(g); 
    }
    /* */
}


        
        
        