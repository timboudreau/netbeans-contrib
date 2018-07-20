/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 */

package org.netbeans.modules.tasklist.usertasks.table;

import java.net.URL;
import javax.swing.event.ChangeEvent;
import org.netbeans.modules.tasklist.usertasks.*;
import java.util.Comparator;
import java.util.Date;
import javax.swing.event.ChangeListener;


import org.netbeans.modules.tasklist.usertasks.treetable.BooleanComparator;
import org.netbeans.modules.tasklist.usertasks.treetable.DefaultTreeTableModel;
import org.netbeans.modules.tasklist.core.table.SortingModel;
import org.netbeans.modules.tasklist.usertasks.treetable.StringIgnoreCaseComparator;
import org.netbeans.modules.tasklist.usertasks.model.UserTask;
import org.netbeans.modules.tasklist.usertasks.model.UserTaskList;

/**
 * Basic TT model for user tasks.
 * 
 * @author tl
 */
public abstract class UTBasicTreeTableModel extends DefaultTreeTableModel {
    /**
     * Comparator for due dates. 
     */
    private static class DueDateComparator implements Comparator<Date> {
        public int compare(Date o1, Date o2) {
            if (o1 == null && o2 == null)
                return 0;
            if (o1 == null)
                return 1;
            if (o2 == null)
                return -1;
            return o1.compareTo(o2);
        }
    }
    
    /**
     * Comparator for java.net.URL. 
     */
    private static class URLComparator implements Comparator<URL> {
        public int compare(URL o1, URL o2) {
            if (o1 == null && o2 == null)
                return 0;
            if (o1 == null)
                return 1;
            if (o2 == null)
                return -1;
            return o1.toExternalForm().compareTo(o2.toExternalForm());
        }
    }
    
    /**
     * Compares to java.lang.Strings. "" and null are bigger than everything 
     * else. 
     */
    private static class TextComparator implements Comparator<String> {
        public int compare(String f1, String f2) {
            boolean empty1 = f1 == null || f1.length() == 0;
            boolean empty2 = f2 == null || f2.length() == 0;
            
            if (empty1 && empty2)
                return 0;
            if (empty1)
                return 1;
            if (empty2)
                return -1;
            return f1.compareToIgnoreCase(f2);
        }
    }
 
    /**
     * Comparator for the "owner" column
     */
    private static class OwnerComparator implements Comparator {
        public int compare(Object o1, Object o2) {
            if (o1 == null && o2 == null)
                return 0;
            if (o1 == null)
                return -1;
            if (o2 == null)
                return 1;
            UserTask ut1 = (UserTask) o1;
            UserTask ut2 = (UserTask) o2;
            String owner1 = ut1.getOwner();
            String owner2 = ut2.getOwner();
            if (owner1.length() == 0 && owner2.length() == 0)
                return 0;
            if (owner1.length() == 0)
                return 1;
            if (owner2.length() == 0)
                return -1;
            return owner1.compareToIgnoreCase(owner2);
        }
    }

    /**
     * Comparators for the corresponding columns. 
     */
    public static final Comparator[] COMPARATORS = {
        new StringIgnoreCaseComparator(),
        new PriorityComparator(),
        new BooleanComparator(), 
        SortingModel.DEFAULT_COMPARATOR, 
        SortingModel.DEFAULT_COMPARATOR,
        SortingModel.DEFAULT_COMPARATOR, 
        SortingModel.DEFAULT_COMPARATOR, 
        SortingModel.DEFAULT_COMPARATOR, 
        SortingModel.DEFAULT_COMPARATOR, 
        SortingModel.DEFAULT_COMPARATOR, 
        SortingModel.DEFAULT_COMPARATOR,
        SortingModel.DEFAULT_COMPARATOR,
        new DueDateComparator(),
        SortingModel.DEFAULT_COMPARATOR,
        SortingModel.DEFAULT_COMPARATOR,
        SortingModel.DEFAULT_COMPARATOR
    };
    
    private static final Class[] COLUMN_CLASS = {
        null, 
        Integer.class,
        Boolean.class,
        Integer.class,
        UserTask.class,
        Integer.class,
        Integer.class,
        String.class,
        UserTask.class,
        String.class,
        Long.class,
        Long.class,
        Long.class,
        String.class,
        Date.class,
        Integer.class
    };

    private static final String[] COLUMNS = {
        org.openide.util.NbBundle.getMessage(UTBasicTreeTableModel.class, 
                "ColumnSummary"), // NOI18N
        org.openide.util.NbBundle.getMessage(UTBasicTreeTableModel.class, 
                "ColumnPriority"), // NOI18N
        org.openide.util.NbBundle.getMessage(UTBasicTreeTableModel.class, 
                "ColumnDone"), // NOI18N
        org.openide.util.NbBundle.getMessage(UTBasicTreeTableModel.class, 
                "ColumnPercentComplete"), // NOI18N
        org.openide.util.NbBundle.getMessage(UTBasicTreeTableModel.class, 
                "ColumnEffort"), // NOI18N
        org.openide.util.NbBundle.getMessage(UTBasicTreeTableModel.class, 
                "ColumnRemEffort"), // NOI18N
        org.openide.util.NbBundle.getMessage(UTBasicTreeTableModel.class, 
                "ColumnSpentTime"), // NOI18N
        org.openide.util.NbBundle.getMessage(UTBasicTreeTableModel.class, 
                "ColumnDetails"), // NOI18N
        org.openide.util.NbBundle.getMessage(UTBasicTreeTableModel.class, 
                "ColumnCategory"), // NOI18N
        org.openide.util.NbBundle.getMessage(UTBasicTreeTableModel.class, 
                "ColumnCreated"), // NOI18N
        org.openide.util.NbBundle.getMessage(UTBasicTreeTableModel.class, 
                "ColumnEdited"), // NOI18N
        org.openide.util.NbBundle.getMessage(UTBasicTreeTableModel.class, 
                "ColumnCompletedDate"), // NOI18N
        org.openide.util.NbBundle.getMessage(UTBasicTreeTableModel.class, 
                "ColumnDue"), // NOI18N
        org.openide.util.NbBundle.getMessage(UTBasicTreeTableModel.class, 
                "ColumnOwner"), // NOI18N
        org.openide.util.NbBundle.getMessage(UTBasicTreeTableModel.class, 
                "ColumnStart"), // NOI18N
        org.openide.util.NbBundle.getMessage(UTBasicTreeTableModel.class, 
                "ColumnSpentTimeToday") // NOI18N
    };
            
    private UserTaskList utl;
    
    /** sorting model */
    protected SortingModel sortingModel;
    
    private ChangeListener sortingModelListener;
    
    /**
     * Constructor.
     * 
     * @param utl a task list
     * @param sm a sorting model
     */
    public UTBasicTreeTableModel(UserTaskList utl, SortingModel sm) {
        super(null, COLUMNS);
        this.utl = utl;
        this.sortingModel = sm;
        
        sortingModelListener = new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                sortingModelChanged();
            }
        };
        
        sm.addChangeListener(sortingModelListener);
    }

    /**
     * Will be called whenever the sorting model has changed. 
     */
    protected void sortingModelChanged() {
    }
    
    /**
     * Should be called after this model was removed from a TreeTable.
     * Destroys all nodes
     */
    public void destroy() {
        sortingModel.removeChangeListener(sortingModelListener);
    }
    
    /**
     * Returns the user task list associated with this model
     * 
     * @return user task list
     */
    public UserTaskList getUserTaskList() {
        return utl;
    }
    
    public Class getColumnClass(int column) {
        if (column == 0)
            return super.getColumnClass(0);
        return COLUMN_CLASS[column];
    }
    
    public void fireTreeNodesChanged(Object source, Object[] path, 
    int[] childIndices, Object[] children) {
        super.fireTreeNodesChanged(source, path, childIndices, children);
    }
    
    public void fireTreeNodesInserted(Object source, Object[] path, 
    int[] childIndices, Object[] children) {
        super.fireTreeNodesInserted(source, path, childIndices, children);
    }
    
    public void fireTreeNodesRemoved(Object source, Object[] path, 
    int[] childIndices, Object[] children) {
        super.fireTreeNodesRemoved(source, path, childIndices, children);
    }
    
    public void fireTreeStructureChanged(Object source, Object[] path) {
        super.fireTreeStructureChanged(source, path);
    }
    
    public void fireTreeStructureChanged(Object source, Object[] path, 
    int[] childIndices, Object[] children) {
        super.fireTreeStructureChanged(source, path, childIndices, children);
    }
}
