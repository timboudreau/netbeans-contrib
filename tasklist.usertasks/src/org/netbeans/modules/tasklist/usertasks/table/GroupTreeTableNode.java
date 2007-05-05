package org.netbeans.modules.tasklist.usertasks.table;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import javax.swing.Icon;
import org.netbeans.modules.tasklist.usertasks.treetable.AdvancedTreeTableNode;
import org.netbeans.modules.tasklist.usertasks.treetable.AdvancedTreeTableNode;
import java.util.List;
import org.netbeans.modules.tasklist.usertasks.*;
import java.util.Comparator;
import java.util.Iterator;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import org.netbeans.modules.tasklist.usertasks.table.grouping.Group;
import org.netbeans.modules.tasklist.usertasks.treetable.AdvancedTreeTableNode;
import org.netbeans.modules.tasklist.usertasks.treetable.FilterIntf;
import org.netbeans.modules.tasklist.usertasks.model.UserTask;
import org.netbeans.modules.tasklist.usertasks.treetable.TreeTableNode;
import org.netbeans.modules.tasklist.usertasks.util.UnaryFunction;

/**
 * Group node.
 *
 * @author tl
 */
public class GroupTreeTableNode extends AdvancedTreeTableNode<List<UserTask>> {
    private static final Icon OPEN_ICON = 
            UIManager.getIcon("Tree.openIcon"); // NOI18N
    private static final Icon CLOSED_ICON = 
            UIManager.getIcon("Tree.closedIcon"); // NOI18N
    
    private Group group;
    private PropertyChangeListener pcl;
    
    /** 
     * Creates a new instance of UserTaskTreeTableNode
     *  
     * @param parent parent node
     * @param filter filter to be used or null
     * @param ut a user task list
     * @param m model for this node
     * @param comparator comparator to be used or null
     */
    public GroupTreeTableNode(TreeTableNode parent, FilterIntf filter, 
            UTFlatTreeTableModel m, List<UserTask> ut, 
            Comparator<AdvancedTreeTableNode> comparator, Group group) {
        super(m, parent, ut);
        this.filter = filter;
        this.comparator = comparator;
        this.group = group;
        
        pcl = new PropertyChangeListener() {
            public void propertyChange(final PropertyChangeEvent evt) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        taskPropertyChange(evt);
                    }
                });
            }
        };
        
        for (int i = 0; i < ut.size(); i++) {
            UserTask userTask = ut.get(i);
            userTask.addPropertyChangeListener(pcl);
        }
        
        // TODO: tasks that do not pass the filter anymore will not be removed
    }

    private void taskPropertyChange(PropertyChangeEvent evt) {
        UserTask ut = (UserTask) evt.getSource();
        
        // this method is called asynchronously using 
        // SwingUtilities.invokeLater. That is why it is possible that we 
        // get events about tasks that are not in this group anymore
        if (!getObject().contains(ut))
            return;

        if (ut.isValuesComputed()) {
            ut.removePropertyChangeListener(pcl);
            GroupTreeTableNode.this.getObject().remove(ut);
            if (getObject().size() == 0)
                // TODO: this could probably be done in a more gentle way
                ((UTListFlatTreeTableNode) parent).refreshChildren();
            else
                refreshChildren();
        } else {
            // this will never return null as we are already in a group 
            // node. e.g. the view is sorted and grouped
            UnaryFunction gb = ((UTListFlatTreeTableNode) getParent()).
                    getGroupBuilder();

            Group g = (Group) gb.compute(ut);

            if (!g.equals(GroupTreeTableNode.this.group)) {
                ut.removePropertyChangeListener(pcl);
                GroupTreeTableNode.this.getObject().remove(ut);
                UTListFlatTreeTableNode p = 
                        (UTListFlatTreeTableNode) getParent();
                if (getObject().size() == 0)
                    // TODO: this could probably be done in a more gentle way
                    p.refreshChildren();
                else
                    refreshChildren();
                p.insertIntoAGroup(ut);
            }
        }
    }
    
    /**
     * Inserts a new task.
     * 
     * @param ut a new task 
     */
    public void insertNew(UserTask ut) {
        Comparator<UserTask> cmp = ((UTListFlatTreeTableNode) getParent()).
                getUTComparator();
        int index = Collections.binarySearch(getObject(), ut, cmp);
        if (index < 0)
            index = -index - 1;
        getObject().add(index, ut);
        ut.addPropertyChangeListener(pcl);
        refreshChildren(); // TODO: this could be done better
    }
    
    /**
     * Returns group associated with this node.
     * 
     * @return group 
     */
    public Group getGroup() {
        return group;
    }

    @Override
    public void destroy() {
        super.destroy();
        
        for (int i = 0; i < getObject().size(); i++) {
            UserTask userTask = getObject().get(i);
            userTask.removePropertyChangeListener(pcl);
        }
    }

    @Override
    public String toString() {
        return group.getDisplayName();
    }
    
    public Iterator getChildrenObjectsIterator() {
        List<UserTask> list = getObject();
        return list.iterator();
    }
    
    public AdvancedTreeTableNode createChildNode(Object child) {
        UTFlatTreeTableNode n = new UTFlatTreeTableNode(
                this, filter, (UTFlatTreeTableModel) model, 
                (UserTask) child, comparator);
        return n;
    }
    
    public Object getValueAt(int column) {
        return null;
    }

    @Override
    public Icon getOpenIcon() {
        return OPEN_ICON;
    }

    @Override
    public Icon getClosedIcon() {
        return CLOSED_ICON;
    }
}
