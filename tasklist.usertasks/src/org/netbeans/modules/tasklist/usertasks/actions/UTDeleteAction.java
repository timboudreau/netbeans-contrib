package org.netbeans.modules.tasklist.usertasks.actions;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.tree.TreePath;
import org.netbeans.modules.tasklist.usertasks.UserTaskTreeTableNode;
import org.netbeans.modules.tasklist.usertasks.UserTasksTreeTable;
import org.netbeans.modules.tasklist.usertasks.model.UserTask;
import org.netbeans.modules.tasklist.usertasks.model.UserTaskList;
import org.netbeans.modules.tasklist.usertasks.treetable.TreeTableNode;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.NbBundle;


/**
 * DeleteAction for UserTasks
 *
 * This class is not used.
 */
public class UTDeleteAction extends AbstractAction 
implements ListSelectionListener {
    private UserTasksTreeTable tt;
    
    /**
     * Konstruktor
     *
     * @param tt tree table
     */
    public UTDeleteAction(UserTasksTreeTable tt) {
        this.tt = tt;
        tt.getSelectionModel().addListSelectionListener(this);
    }

    public void valueChanged(ListSelectionEvent event) {
        int[] rows = tt.getSelectedRows();
        if (rows.length == 0)
            return;
        
        boolean e = true;
        for (int i = 0; i < rows.length; i++) {
            Object obj = tt.getNodeForRow(rows[i]);
            if (!(obj instanceof UserTaskTreeTableNode)) {
                e = false;
                break;
            }
        }

        setEnabled(e);
    }
    
    private UserTaskTreeTableNode[] normalize(UserTaskTreeTableNode[] tasks) {
        List<UserTaskTreeTableNode> ret = 
                new ArrayList<UserTaskTreeTableNode>();
    outer:
        for (int i = 0; i < tasks.length; i++) {
            for (int j = 0; j < ret.size(); j++) {
                UserTaskTreeTableNode ut = (UserTaskTreeTableNode) ret.get(j);
                if (ut.getUserTask().isAncestorOf(tasks[i].getUserTask()))
                    continue outer;
            }
            for (int j = 0; j < ret.size(); ) {
                UserTaskTreeTableNode ut = (UserTaskTreeTableNode) ret.get(j);
                if (tasks[i].getUserTask().isAncestorOf(ut.getUserTask()))
                    ret.remove(j);
                else
                    j++;
            }
            ret.add(tasks[i]);
        }
        return ret.toArray(new UserTaskTreeTableNode[ret.size()]);
    }
    
    private boolean doConfirm(UserTaskTreeTableNode[] sel) {
        String message, title;
        if (sel.length == 1) {
            message = NbBundle.getMessage(UTDeleteAction.class, "MSG_ConfirmDeleteObject", // NOI18N
                sel[0].getUserTask().getSummary());
            title = NbBundle.getMessage(UTDeleteAction.class, "MSG_ConfirmDeleteObjectTitle"); // NOI18N
        } else {
            message = NbBundle.getMessage(UTDeleteAction.class, "MSG_ConfirmDeleteObjects", // NOI18N
                new Integer(sel.length));
            title = NbBundle.getMessage(UTDeleteAction.class, "MSG_ConfirmDeleteObjectsTitle"); // NOI18N
        }
        NotifyDescriptor desc = new NotifyDescriptor.Confirmation(message, title, NotifyDescriptor.YES_NO_OPTION);
        return NotifyDescriptor.YES_OPTION.equals(DialogDisplayer.getDefault().notify(desc));
    }

    public void actionPerformed(ActionEvent event) {
        int[] rows = tt.getSelectedRows();
        if (rows.length == 0)
            return;
        
        UserTaskTreeTableNode[] tasks = new UserTaskTreeTableNode[rows.length];
        boolean e = true;
        for (int i = 0; i < tasks.length; i++) {
            Object obj = tt.getNodeForRow(rows[i]);
            if (obj instanceof UserTaskTreeTableNode) {
                tasks[i] = (UserTaskTreeTableNode) obj;
            } else {
                e = false;
                break;
            }
        }
        if (!e)
            return;
        
        tasks = normalize(tasks);
        
        UserTaskTreeTableNode n = tasks[tasks.length - 1];
        TreeTableNode ttn = n.findNextNodeAfterDelete();
        if (ttn instanceof UserTaskTreeTableNode)
            n = (UserTaskTreeTableNode) ttn;
        else 
            n = null;

        // perform action if confirmed
        if (doConfirm(tasks)) {
            //tt.setPaintDisabled(true);
            tt.clearSelection();
            for (int i = 0; i < tasks.length; i++) {
                UserTask item = tasks[i].getUserTask();
                UserTaskList utl = item.getList();
                item.destroy();
                if (item.getParent() != null)
                    item.getParent().getSubtasks().remove(item);
                else
                    utl.getSubtasks().remove(item);
            }
            if (n != null) {
                TreePath tp = new TreePath(n.getPathToRoot());
                tt.select(tp);
                tt.scrollTo(tp);
            }        
            //tt.setPaintDisabled(false);
            tt.repaint();
        }
    }
}
