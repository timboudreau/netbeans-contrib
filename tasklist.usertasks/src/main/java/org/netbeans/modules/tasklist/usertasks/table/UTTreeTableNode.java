package org.netbeans.modules.tasklist.usertasks.table;

import org.netbeans.modules.tasklist.usertasks.treetable.AdvancedTreeTableNode;
import org.netbeans.modules.tasklist.usertasks.treetable.AdvancedTreeTableNode;
import org.netbeans.modules.tasklist.usertasks.table.UTTreeTableModel;
import org.netbeans.modules.tasklist.usertasks.*;
import java.util.Comparator;
import java.util.Iterator;
import org.netbeans.modules.tasklist.usertasks.treetable.AdvancedTreeTableNode;
import org.netbeans.modules.tasklist.usertasks.treetable.FilterIntf;
import org.netbeans.modules.tasklist.usertasks.treetable.TreeTableNode;

import org.netbeans.modules.tasklist.usertasks.model.UserTask;
import org.netbeans.modules.tasklist.usertasks.table.UTBasicTreeTableNode;
import org.netbeans.modules.tasklist.usertasks.util.ObjectListEvent;
import org.netbeans.modules.tasklist.usertasks.util.ObjectListListener;

/**
 * Task node
 * 
 * @author tl
 */
public class UTTreeTableNode extends UTBasicTreeTableNode {
    /**
     * Filters a task.
     *
     * @return true if the filter accepts <code>ut</code> or one of 
     * it's subtasks
     */
    public static boolean acceptsRecursively(UserTask ut, FilterIntf filter) {
        if (filter.accept(ut))
            return true;
        Iterator it = ut.getSubtasks().iterator();
        while (it.hasNext()) {
            UserTask child = (UserTask) it.next();
            if (acceptsRecursively(child, filter))
                return true;
        }
        return false;
    }
    
    protected boolean unmatched;
    protected ObjectListListener tl;
    
    /** 
     * Creates a new instance of UTTreeTableNode
     *  
     * @param filter filter to be used or null
     * @param ut a user task
     * @param m model for this node
     * @param comparator comparator to be used or null
     */
    public UTTreeTableNode(    
            TreeTableNode parent, FilterIntf filter, UTBasicTreeTableModel m, 
            UserTask ut, 
            Comparator<AdvancedTreeTableNode> comparator) {
        super(parent, filter, m, ut, comparator);
        
        tl = new ObjectListListener() {
            public void listChanged(ObjectListEvent e) {
                switch (e.getType()) {
                    case ObjectListEvent.EVENT_ADDED: {
                        Object[] obj = e.getObjects();
                        for (int i = 0; i < obj.length; i++) {
                            fireChildObjectAdded(obj[i]);
                        }
                        break;
                    }
                    case ObjectListEvent.EVENT_REMOVED: {
                        Object[] obj = e.getObjects();
                        for (int i = 0; i < obj.length; i++) {
                            fireChildObjectRemoved(obj[i]);
                        }
                        break;
                    }
                    case ObjectListEvent.EVENT_REORDERED:
                        fireChildObjectsReordered();
                        break;
                    case ObjectListEvent.EVENT_STRUCTURE_CHANGED:
                        refreshChildren();
                        break;
                    default:
                        throw new InternalError("unexpected event type"); // NOI18N
                }
            }
        };
        ut.getSubtasks().addListener(tl);
    }
    
    /**
     * Returns true if the filter does not accept this node but accepts
     * some of the children
     *
     * @return true = unmatched
     */
    public boolean isUnmatched() {
        return unmatched;
    }
    
    /**
     * Sets the unmatched attribute
     *
     * @param unmatched true = the filter does not accept this node but 
     * some of it's subnodes
     */
    public void setUnmatched(boolean unmatched) {
        this.unmatched = unmatched;
    }
    
    public void destroy() {
        super.destroy();
        getUserTask().getSubtasks().removeListener(tl);
    }    
    
    public Iterator getChildrenObjectsIterator() {
        return getUserTask().getSubtasks().iterator();
    }
    
    public AdvancedTreeTableNode<UserTask> createChildNode(Object child) {
        UTTreeTableNode n = new UTTreeTableNode(
            this, filter, (UTTreeTableModel) model, 
            (UserTask) child, comparator);
        if (getFilter() != null && !getFilter().accept(child))
            n.unmatched = true;
        return n;
    }    
    
    public boolean accept(Object child) {
        if (getFilter() == null)
            return true;
        
        return acceptsRecursively((UserTask) child, getFilter());
    }
}
