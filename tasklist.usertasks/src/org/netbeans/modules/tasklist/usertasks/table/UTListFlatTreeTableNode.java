package org.netbeans.modules.tasklist.usertasks.table;

import org.netbeans.modules.tasklist.usertasks.treetable.AdvancedTreeTableNode;
import org.netbeans.modules.tasklist.usertasks.treetable.AdvancedTreeTableNode;
import java.util.Collections;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.modules.tasklist.usertasks.table.UTBasicTreeTableModel;
import org.netbeans.modules.tasklist.usertasks.table.grouping.Groups;
import java.util.List;
import org.netbeans.modules.tasklist.usertasks.*;
import java.util.Comparator;
import java.util.Iterator;
import org.netbeans.modules.tasklist.core.table.SortingModel;
import org.netbeans.modules.tasklist.usertasks.treetable.AdvancedTreeTableNode;
import org.netbeans.modules.tasklist.usertasks.treetable.FilterIntf;

import org.netbeans.modules.tasklist.usertasks.model.UserTask;
import org.netbeans.modules.tasklist.usertasks.model.UserTaskList;
import org.netbeans.modules.tasklist.usertasks.table.grouping.Group;
import org.netbeans.modules.tasklist.usertasks.treetable.NotComparator;
import org.netbeans.modules.tasklist.usertasks.treetable.TreeTableNode;
import org.netbeans.modules.tasklist.usertasks.util.ObjectListEvent;
import org.netbeans.modules.tasklist.usertasks.util.ObjectListListener;
import org.netbeans.modules.tasklist.usertasks.util.UTListTreeAbstraction;
import org.netbeans.modules.tasklist.usertasks.util.UTUtils;
import org.netbeans.modules.tasklist.usertasks.util.UnaryFunction;

/**
 * Task list node.
 *
 * @author tl
 */
public class UTListFlatTreeTableNode extends UTListBasicTreeTableNode {
    private ObjectListListener tl;
    private SortingModel sm;
    private ObjectListListener objectListListener;
    
    /** 
     * Creates a new instance of UserTaskTreeTableNode
     *  
     * @param filter filter to be used or null
     * @param ut a user task list
     * @param m model for this node
     * @param comparator comparator to be used or null
     * @param sm sorting model
     */
    public UTListFlatTreeTableNode(FilterIntf filter, 
            UTFlatTreeTableModel m, UserTaskList ut, 
            Comparator<AdvancedTreeTableNode> comparator, SortingModel sm) {
        super(m, null, ut);
        this.filter = filter;
        this.comparator = comparator;
        
        this.sm = sm;
        this.sm.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                // TODO: this could be done in a better way for some 
                // situations
                refreshChildren();
            }
        });

        objectListListener = new ObjectListListener() {
            public void listChanged(ObjectListEvent e) {
                // TODO: this could be done in a better way for some 
                // situations
                refreshChildren();
            }
        };
        ut.addObjectListListener(objectListListener);
    }

    @Override
    public void destroy() {
        super.destroy();
        getObject().removeObjectListListener(objectListListener);
    }
        
    public Iterator getChildrenObjectsIterator() {
        UserTaskList utl = getObject();
        UTListTreeAbstraction ta = new UTListTreeAbstraction(utl);
        UnaryFunction filter = new UnaryFunction() {
            public Object compute(Object obj) {
                if (!(obj instanceof UserTask))
                    return Boolean.FALSE;
                UserTask ut = (UserTask) obj;
                return Boolean.valueOf(!ut.isValuesComputed());
            }            
        };
        List filtered = UTUtils.filter(ta, filter);
        
        if (sm.getSortedColumn() == -1)
            return filtered.iterator();
        else {
            Comparator<UserTask> cmp = getUTComparator();
            Collections.sort(filtered, cmp);
            final UnaryFunction g = Groups.getGroupBuilder(
                    sm.getSortedColumn());
            List<List<UserTask>> groups = UTUtils.group(filtered,
                    new Comparator<UserTask>() {
                public int compare(UserTask o1, UserTask o2) {
                    Object v1 = g.compute(o1);
                    Object v2 = g.compute(o2);
                    return v1.equals(v2) ? 0 : 1;
                }
            });
            return groups.iterator();
        }
    }
    
    
    /**
     * Inserts a new task into the right group.
     * 
     * @param newTask a task 
     */
    public void insertIntoAGroup(UserTask newTask) {
        UnaryFunction gb = getGroupBuilder();
        if (gb == null) {
            // TODO: this could be done better
            refreshChildren();
        } else {
            Group newTaskGroup = (Group) gb.compute(newTask);
            boolean found = false;
            for (int i = 0; i < getChildCount(); i++) {
                GroupTreeTableNode treeTableNode = 
                        (GroupTreeTableNode) getChildAt(i);
                Group g = treeTableNode.getGroup();
                if (g.equals(newTaskGroup)) {
                    found = true;
                    treeTableNode.insertNew(newTask);
                    break;
                }
            }
            if (!found) {
                // TODO: this could be done in a more gentle way
                refreshChildren();
            }
        }
    }
    
    /**
     * Returns current group builder.
     * 
     * @return group builder or null if the view is not sorted 
     */
    public UnaryFunction getGroupBuilder() {
        if (sm.getSortedColumn() == -1)
            return null;
        else
            return Groups.getGroupBuilder(sm.getSortedColumn());
    } 
    
    /**
     * Returns current comparator for UserTasks.
     * 
     * @return comparator or null (respecting sorting order) 
     */
    public Comparator<UserTask> getUTComparator() {
        final int property = sm.getSortedColumn();
        if (property == -1)
            return null;
        
        final Comparator propComparator = UTBasicTreeTableModel.COMPARATORS[
                    property];
        Comparator cmp = new Comparator() {
            public int compare(Object o1, Object o2) {
                Object prop1 = UTColumns.getProperty(
                        (UserTask) o1, property);
                Object prop2 = UTColumns.getProperty(
                        (UserTask) o2, property);
                return propComparator.compare(prop1, prop2);
            }
        };
        if (sm.isSortOrderDescending())
            cmp = new NotComparator(cmp);
        return cmp;
    }
    
    public AdvancedTreeTableNode createChildNode(Object child) {
        if (sm.getSortedColumn() < 0)
            return new UTFlatTreeTableNode(
                    this, filter, (UTFlatTreeTableModel) model, 
                    (UserTask) child, comparator);
        else {
            UnaryFunction g = Groups.getGroupBuilder(
                    sm.getSortedColumn());
            List<UserTask> list = (List<UserTask>) child;
            Group group = (Group) g.compute(list.get(0));
            return new GroupTreeTableNode(
                    this, filter, (UTFlatTreeTableModel) model, 
                    list, comparator, group);
        }
    }
}
