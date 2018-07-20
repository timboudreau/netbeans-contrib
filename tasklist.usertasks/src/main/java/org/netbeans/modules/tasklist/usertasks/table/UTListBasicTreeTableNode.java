package org.netbeans.modules.tasklist.usertasks.table;

import org.netbeans.modules.tasklist.usertasks.treetable.AdvancedTreeTableNode;
import org.netbeans.modules.tasklist.usertasks.treetable.AdvancedTreeTableNode;
import org.netbeans.modules.tasklist.usertasks.*;
import org.netbeans.modules.tasklist.usertasks.treetable.AdvancedTreeTableNode;
import org.netbeans.modules.tasklist.usertasks.treetable.DefaultTreeTableModel;
import org.netbeans.modules.tasklist.usertasks.treetable.TreeTableNode;
import org.openide.util.NbBundle;
import org.netbeans.modules.tasklist.usertasks.model.UserTaskList;

/**
 * Task list node.
 *
 * @author tl
 */
public abstract class UTListBasicTreeTableNode extends 
        AdvancedTreeTableNode<UserTaskList> {      
    /** 
     * Creates a new instance.
     *
     * @param model tree table model this node belongs to
     * @param parent parent of this node or null if this node is a root
     * @param object object associated with this node
     */
    public UTListBasicTreeTableNode(DefaultTreeTableModel model,
                                    TreeTableNode parent, UserTaskList object) {
        super(model, parent, object);
    }
    
    public Object getValueAt(int column) {
        switch (column) {
            case UTColumns.SUMMARY:
                return NbBundle.getMessage(UTListBasicTreeTableNode.class, 
                        "TaskList");  // NOI18N
            default:
                return null;
        }
    }    
    
    public void setValueAt(Object aValue, int column) {
    }
}
