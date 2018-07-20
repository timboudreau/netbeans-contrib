package org.netbeans.modules.tasklist.usertasks.table;

import org.netbeans.modules.tasklist.usertasks.treetable.AdvancedTreeTableNode;
import org.netbeans.modules.tasklist.usertasks.treetable.AdvancedTreeTableNode;
import org.netbeans.modules.tasklist.usertasks.*;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import org.netbeans.modules.tasklist.usertasks.treetable.AdvancedTreeTableNode;
import org.netbeans.modules.tasklist.usertasks.treetable.FilterIntf;
import org.netbeans.modules.tasklist.usertasks.treetable.TreeTableNode;
import org.netbeans.modules.tasklist.usertasks.model.UserTask;

/**
 * Task node
 * 
 * @author tl
 */
public class UTFlatTreeTableNode extends UTBasicTreeTableNode {
    /** 
     * Creates a new instance of UserTaskTreeTableNode
     *  
     * @param filter filter to be used or null
     * @param ut a user task
     * @param m model for this node
     * @param comparator comparator to be used or null
     */
    public UTFlatTreeTableNode(
    TreeTableNode parent, FilterIntf filter, 
    UTFlatTreeTableModel m, UserTask ut, 
            Comparator<AdvancedTreeTableNode> comparator) {
        super(parent, filter, m, ut, comparator);
    }
    
    public Iterator getChildrenObjectsIterator() {
        return Collections.emptyList().iterator();
    }
    
    public AdvancedTreeTableNode<UserTask> createChildNode(Object child) {
        UTFlatTreeTableNode n = new UTFlatTreeTableNode(
            this, filter, (UTFlatTreeTableModel) model, 
            (UserTask) child, comparator);
        return n;
    }    
}
