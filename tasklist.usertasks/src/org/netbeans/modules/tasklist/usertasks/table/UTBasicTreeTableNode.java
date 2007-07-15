package org.netbeans.modules.tasklist.usertasks.table;

import org.netbeans.modules.tasklist.usertasks.treetable.AdvancedTreeTableNode;
import org.netbeans.modules.tasklist.usertasks.treetable.AdvancedTreeTableNode;
import org.netbeans.modules.tasklist.usertasks.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;
import java.util.Comparator;
import java.util.Date;
import javax.swing.SwingUtilities;
import org.netbeans.modules.tasklist.usertasks.treetable.AdvancedTreeTableNode;
import org.netbeans.modules.tasklist.usertasks.treetable.FilterIntf;
import org.netbeans.modules.tasklist.usertasks.treetable.TreeTableNode;
import org.netbeans.modules.tasklist.usertasks.model.UserTask;

/**
 * Task node
 * 
 * @author tl
 */
public abstract class UTBasicTreeTableNode extends AdvancedTreeTableNode<UserTask> {
    private PropertyChangeListener pcl;
    
    /** 
     * Creates a new instance of UserTaskTreeTableNode
     *  
     * @param filter filter to be used or null
     * @param ut a user task
     * @param m model for this node
     * @param comparator comparator to be used or null
     */
    public UTBasicTreeTableNode(
    TreeTableNode parent, FilterIntf filter, 
    UTBasicTreeTableModel m, UserTask ut, 
            Comparator<AdvancedTreeTableNode> comparator) {
        super(m, parent, ut);
        
        this.filter = filter;
        this.comparator = comparator;
        pcl = new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        fireObjectChanged();
                    }
                });
            }
        };
        object.addPropertyChangeListener(pcl);
    }
    
    /**
     * Returns user task associated with this node
     *
     * @return user task
     */
    public UserTask getUserTask() {
        return (UserTask) object;
    }
    
    public Object getValueAt(int column) {
        UserTask ut = getUserTask();
        switch (column) {
            case UTColumns.SUMMARY:
                return ut.getSummary();
            case UTColumns.PRIORITY:
                return new Integer(ut.getPriority());
            case UTColumns.DONE:
                return Boolean.valueOf(ut.isDone());
            case UTColumns.PERCENT_COMPLETE:
                return new Integer(ut.getPercentComplete());
            case UTColumns.EFFORT:
                return ut.getEffort();
            case UTColumns.REMAINING_EFFORT:
                return new Integer(ut.getRemainingEffort());
            case UTColumns.SPENT_TIME:
                return new Integer(ut.getSpentTime());
            case UTColumns.DETAILS: 
                return ut.getDetails();
            case UTColumns.CATEGORY:
                return ut.getCategory();
            case UTColumns.CREATED:
                return new Long(ut.getCreatedDate());
            case UTColumns.LAST_EDITED:
                return new Long(ut.getLastEditedDate());
            case UTColumns.COMPLETED_DATE:
                return new Long(ut.getCompletedDate());
            case UTColumns.DUE_DATE:
                return ut.getDueDate();
            case UTColumns.OWNER:
                return ut.getOwner();
            case UTColumns.START: {
                long start = ut.getStart();
                if (start == -1)
                    return null;
                else
                    return new Date(start);
            }
            case UTColumns.SPENT_TIME_TODAY:
                return new Integer(ut.getSpentTimeToday());
            default:
                return "<error>"; // NOI18N
        }
    }    
    
    public void setValueAt(Object aValue, int column) {
        UserTask ut = getUserTask();
        switch (column) {
            case UTColumns.SUMMARY:
                ut.setSummary((String) aValue);
                break;
            case UTColumns.DONE:
                ut.setDone(((Boolean) aValue).booleanValue());
                break;
            case UTColumns.CATEGORY:
                ut.setCategory((String) aValue);
                break;
            case UTColumns.PRIORITY:
                ut.setPriority(((Integer) aValue).intValue());
                break;
            case UTColumns.PERCENT_COMPLETE:
                ut.setPercentComplete(((Integer) aValue).intValue());
                break;
            case UTColumns.DETAILS: 
                ut.setDetails((String) aValue);
                break;
            case UTColumns.OWNER:
                ut.setOwner((String) aValue);
                break;
            case UTColumns.EFFORT:
                ut.setEffort(((Integer) aValue).intValue());
                break;
            case UTColumns.SPENT_TIME:
                ut.setSpentTime(((Integer) aValue).intValue());
                break;
            case UTColumns.START:
                ut.setStartDate((Date) aValue);
                break;
            case UTColumns.DUE_DATE:
                ut.setDueDate((Date) aValue);
                break;
        }
    }

    public String toString() {
        // the implementation of this method should be fast.
        // as it is used by the TreeTable's JTree CellRenderer
        return getUserTask().getSummary();
    }

    public void destroy() {
        super.destroy();
        getUserTask().removePropertyChangeListener(pcl);
    }    
    
    public boolean isCellEditable(int column) {
        switch (column) {
            case UTColumns.DONE:
                return !getUserTask().isValuesComputed() &&
                        getUserTask().areDependenciesDone();
            case UTColumns.PERCENT_COMPLETE:
                return !getUserTask().isValuesComputed() &&
                        getUserTask().areDependenciesDone();
            case UTColumns.EFFORT:
                return !getUserTask().isValuesComputed();
            case UTColumns.SPENT_TIME:
                return !getUserTask().isValuesComputed();
            case UTColumns.SUMMARY:
            case UTColumns.CATEGORY:
            case UTColumns.DETAILS:
            case UTColumns.PRIORITY:
            case UTColumns.OWNER:
            case UTColumns.START:
            case UTColumns.DUE_DATE:
                return true;
            default:
                return false;
        }
    }
}
