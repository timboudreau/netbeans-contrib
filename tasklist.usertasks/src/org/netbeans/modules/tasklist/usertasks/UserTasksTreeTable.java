package org.netbeans.modules.tasklist.usertasks;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ListSelectionModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.netbeans.modules.tasklist.core.TLUtils;
import org.netbeans.modules.tasklist.core.columns.ColumnsConfiguration;
import org.netbeans.modules.tasklist.core.editors.PriorityTableCellRenderer;
import org.netbeans.modules.tasklist.core.filter.Filter;
import org.netbeans.modules.tasklist.usertasks.treetable.DefaultMutableTreeTableNode;
import org.netbeans.modules.tasklist.usertasks.treetable.DefaultTreeTableModel;
import org.netbeans.modules.tasklist.usertasks.treetable.NodesTreeTable;
import org.openide.explorer.ExplorerManager;
import org.openide.nodes.Node;
import org.netbeans.modules.tasklist.usertasks.treetable.BooleanTableCellRenderer;

/**
 * TT for user tasks
 */
public class UserTasksTreeTable extends NodesTreeTable {
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
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setShowHorizontalLines(true);
        setShowVerticalLines(true);
        setAutoResizeMode(AUTO_RESIZE_OFF);
        getTree().setCellRenderer(new SummaryTreeCellRenderer());
        getTree().setShowsRootHandles(true);
        getTree().setToggleClickCount(3);
        //getTree().setEditable(true);
        setAutoCreateColumnsFromModel(false);
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
            return new UserTaskNode(node, ut, utl);
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
}
