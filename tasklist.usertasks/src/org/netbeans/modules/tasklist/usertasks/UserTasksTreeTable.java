package org.netbeans.modules.tasklist.usertasks;

import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.TableColumnModel;

import org.netbeans.modules.tasklist.core.TLUtils;
import org.netbeans.modules.tasklist.core.editors.PriorityTableCellRenderer;
import org.netbeans.modules.tasklist.core.filter.Filter;
import org.netbeans.modules.tasklist.usertasks.treetable.*;
import org.openide.explorer.ExplorerManager;
import org.openide.nodes.Node;

/**
 * TT for user tasks
 */
public class UserTasksTreeTable extends NodesTreeTable {
    private static final Logger LOGGER = 
        TLUtils.getLogger(UserTasksTreeTable.class);
    static {
        LOGGER.setLevel(Level.FINE);
    }
    
    /**
     * Creates a new instance of UserTasksTreeTable
     * TODO: comment
     * @param filter used filter or null
     */
    public UserTasksTreeTable(ExplorerManager em, UserTaskList utl,
    Filter filter) {
        super(em, new DefaultTreeTableModel(
            new DefaultMutableTreeTableNode(), new String[] {""}));
        setTreeTableModel(
            new UserTasksTreeTableModel(utl, getSortingModel(), filter));
        setShowHorizontalLines(true);
        setShowVerticalLines(true);
        setAutoResizeMode(AUTO_RESIZE_OFF);
        getTree().setCellRenderer(new SummaryTreeCellRenderer());
        getTree().setShowsRootHandles(true);
        setAutoCreateColumnsFromModel(false);
        TableColumnModel tcm = getColumnModel();
        tcm.getColumn(1).setCellRenderer(
            new PriorityTableCellRenderer());
        tcm.getColumn(2).setCellRenderer(
            new BooleanTableCellRenderer());
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
    }

    public Node createNode(Object obj) {
        obj = ((DefaultMutableTreeTableNode) obj).getUserObject();
        if (obj instanceof UserTask)
            return new UserTaskNode((UserTask) obj);
        else
            return null;
    }
}
