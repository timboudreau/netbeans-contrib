package org.netbeans.modules.tasklist.usertasks;

import java.util.Iterator;
import java.util.List;
import org.netbeans.modules.tasklist.core.editors.PriorityTableCellRenderer;
import org.netbeans.modules.tasklist.usertasks.treetable.DefaultMutableTreeTableNode;
import org.netbeans.modules.tasklist.usertasks.treetable.NodesTreeTable;
import org.netbeans.modules.tasklist.usertasks.treetable.TreeTableModel;
import org.openide.explorer.ExplorerManager;
import org.openide.nodes.Node;

/**
 * TT for user tasks
 */
public class UserTasksTreeTable extends NodesTreeTable {
    /**
     * TODO
     */
    private static UserTaskTreeTableNode createTreeTableNode(UserTask ut) {
        UserTaskTreeTableNode n = new UserTaskTreeTableNode(ut);
        List tasks = ut.getSubtasks();
        Iterator it = tasks.iterator();
        while (it.hasNext()) {
            n.add(createTreeTableNode((UserTask) it.next()));
        }
        return n;
    }
    
    /**
     * Creates a TT model
     *
     * @return created TT model
     */
    private static TreeTableModel createTreeTableModel(UserTaskList utl) {
        List tasks = utl.getTasks();
        DefaultMutableTreeTableNode n = new DefaultMutableTreeTableNode(utl);
        Iterator it = tasks.iterator();
        while (it.hasNext()) {
            n.add(createTreeTableNode((UserTask) it.next()));
        }
        return new UserTasksTreeTableModel(n);
    }
    
    /**
     * Creates a new instance of UserTasksTreeTable
     * TODO: comment
     */
    public UserTasksTreeTable(ExplorerManager em, UserTaskList utl) {
        super(em, createTreeTableModel(utl));
        setShowHorizontalLines(true);
        setShowVerticalLines(true);
        setAutoResizeMode(AUTO_RESIZE_OFF);
        getTree().setCellRenderer(new SummaryTreeCellRenderer());
        getColumnModel().getColumn(1).setCellRenderer(
            new PriorityTableCellRenderer());
        getColumnModel().getColumn(2).setCellRenderer(
            new BooleanTableCellRenderer());
        getColumnModel().getColumn(3).setCellRenderer(
            new PercentsTableCellRenderer());
        DurationTableCellRenderer dr = new DurationTableCellRenderer();
        getColumnModel().getColumn(4).setCellRenderer(dr);
        getColumnModel().getColumn(5).setCellRenderer(dr);
        getColumnModel().getColumn(6).setCellRenderer(dr);
        getColumnModel().getColumn(9).setCellRenderer(
            new LineTableCellRenderer());
        DateTableCellRenderer dcr = new DateTableCellRenderer();
        getColumnModel().getColumn(11).setCellRenderer(dcr);
        getColumnModel().getColumn(12).setCellRenderer(dcr);
        getColumnModel().getColumn(13).setCellRenderer(dcr);
    }

    public Node createNode(Object obj) {
        obj = ((DefaultMutableTreeTableNode) obj).getUserObject();
        if (obj instanceof UserTask)
            return new UserTaskNode((UserTask) obj);
        else
            return null;
    }
}
