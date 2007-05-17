package org.netbeans.modules.tasklist.usertasks.table;

import org.netbeans.modules.tasklist.usertasks.treetable.AdvancedTreeTableNode;
import org.netbeans.modules.tasklist.usertasks.treetable.AdvancedTreeTableNode;
import org.netbeans.modules.tasklist.usertasks.table.UTTreeTableModel;
import org.netbeans.modules.tasklist.usertasks.table.UTTreeTableNode;
import org.netbeans.modules.tasklist.usertasks.*;
import java.util.Iterator;
import org.netbeans.modules.tasklist.usertasks.treetable.AdvancedTreeTableNode;
import org.netbeans.modules.tasklist.usertasks.treetable.FilterIntf;

import org.netbeans.modules.tasklist.usertasks.model.UserTask;
import org.netbeans.modules.tasklist.usertasks.model.UserTaskList;
import org.netbeans.modules.tasklist.usertasks.util.ObjectListEvent;
import org.netbeans.modules.tasklist.usertasks.util.ObjectListListener;

/**
 * Task node
 *
 * @author tl
 */
public class UTListTreeTableNode extends UTListBasicTreeTableNode {
    private ObjectListListener tl;
    
    /** 
     * Creates a new instance of UserTaskTreeTableNode
     *  
     * @param filter filter to be used or null
     * @param ut a user task list
     * @param m model for this node
     */
    public UTListTreeTableNode(FilterIntf filter,UTBasicTreeTableModel m, UserTaskList ut) {
        super(m, null, ut);
        this.filter = filter;
        tl = new ObjectListListener() {
            public void listChanged(ObjectListEvent ev) {
                switch (ev.getType()) {
                    case ObjectListEvent.EVENT_ADDED: {
                    	Object[] obj = ev.getObjects();
                    	for (int i = 0; i < obj.length; i++) {
                            fireChildObjectAdded(obj[i]);
                    	}
                        break;
                    }
                    case ObjectListEvent.EVENT_REMOVED: {
                    	Object[] obj = ev.getObjects();
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
                }
            }
        };
        ut.getSubtasks().addListener(tl);
    }
    
    public void destroy() {
        super.destroy();
        this.getObject().getSubtasks().removeListener(tl);
    }    
    
    public Iterator getChildrenObjectsIterator() {
        return getObject().getSubtasks().iterator();
    }
    
    public AdvancedTreeTableNode createChildNode(Object child) {
        UTTreeTableNode n = new UTTreeTableNode(
            this, filter, (UTTreeTableModel) model, 
            (UserTask) child, comparator);
        if (getFilter() != null && !getFilter().accept(child))
            n.setUnmatched(true);
        return n;
    }
    
    public boolean accept(Object child) {
        if (getFilter() == null)
            return true;
        
        return UTTreeTableNode.acceptsRecursively(
            (UserTask) child, getFilter());
    }
}
