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

package org.netbeans.modules.tasklist.core.columns;

import java.util.ArrayList;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableColumn;

import org.netbeans.modules.tasklist.core.ColumnProperty;
import org.netbeans.modules.tasklist.core.TaskListView;

/**
 * View's columns
 */
public class ColumnsConfiguration {
    private ArrayList listeners = new ArrayList();

    /** Widths of visible columns */
    private int widths[];

    private String[] properties;

    /** property of the sorting column or null */
    private String sortingColumn;

    private boolean ascending;

    /**
     * Constructor
     *
     * @param properties names of the properties for visible columns
     * @param widths widths of the visible columns
     * @param sort name of the sorting column or null
     * @param ascending true = ascending order, false = descending order
     */
    public ColumnsConfiguration(String[] properties, int[] widths, String sort, 
    boolean ascending) {
        this.properties = properties;
        this.widths = widths;
        this.sortingColumn = sort;
        this.ascending = ascending;
    }
    
    /**
     * Sets the values
     *
     * @param properties names of the properties for visible columns
     * @param widths widths of the visible columns
     * @param sort name of the sorting column or null
     * @param ascending true = ascending order, false = descending order
     */
    public void setValues(String[] properties, int[] widths, String sort,
    boolean ascending) {
        this.properties = properties;
        this.widths = widths;
        this.sortingColumn = sort;
        this.ascending = ascending;
        fireChange();
    }
    
    /**
     * Returns widths of the columns
     *
     * @return widths
     */
    public int[] getWidths() {
        return widths;
    }
    
    /**
     * Returns names of the properties for visible columns
     *
     * @return properties. != null
     */
    public String[] getProperties() {
        return properties;
    }

    /**
     * Returns property of the column the view is sorted on or null.
     *
     * @return name of a property or null
     */
    public String getSortingColumn() {
        return sortingColumn;
    }
    
    /**
     * Returns sorting order
     *
     * @return true = ascending, false = descending
     */
    public boolean getSortingOrder() {
        return ascending;
    }
    
    /**
     * Sets sorting order
     *
     * @param ascending true = ascending
     */
    public void setSortingOrder(boolean ascending) {
        this.ascending = ascending;
    }
    
    /** 
     * Adds a ChangeListener to the listener list.
     *
     * @param l The listener to add.
     */
    public void addChangeListener(ChangeListener l) {
        listeners.add(l);
    }
    
    /** 
     * Removes a ChangeListener from the listener list.
     *
     * @param l The listener to remove.
     */
    public void removeChangeListener(ChangeListener l) {
        listeners.remove(l);
    }
    
    /**
     * Fires a change event
     */
    protected final void fireChange() {
        ChangeEvent e = null;
        for (int i = 0; i < listeners.size(); i++) {
            ChangeListener l = (ChangeListener) listeners.get(i);
            if (e == null)
                e = new ChangeEvent(this);
            l.stateChanged(e);
        }
    }

    /**
     * Loads column configuration from a view
     *
     * @param v a view XXX replace by ColumnProperty[] and TableColumnModel
     * @param cc a columns configuration
     */
    public static void loadColumnsFrom(TaskListView v, ColumnsConfiguration cc) {
//        LOGGER.log(Level.FINE, "loading columns from " + v.getDisplayName(),
//            new Exception());

        // first find the tree column. It will be always the first one.
        ColumnProperty columns[] = v.getColumns();
        ColumnProperty treeColumn = null;
        for (int i = 0; i < columns.length; i++) {
            Object b = columns[i].getValue("TreeColumnTTV"); // NOI18N
            if (b instanceof Boolean && ((Boolean) b).booleanValue()) {
                treeColumn = columns[i];
                break;
            }
        }
        assert treeColumn != null;

        // find the sorted column
        ColumnProperty sortedColumn = null;
        boolean ascending = false;
        for (int i = 0; i < columns.length; i++) {
            Boolean sorting = (Boolean) columns[i].getValue(
                "SortingColumnTTV"); // NOI18N
            if ((sorting != null) && (sorting.booleanValue())) {
                sortedColumn = columns[i];
                Boolean desc = (Boolean) columns[i].getValue(
                    "DescendingOrderTTV"); // NOI18N
                ascending = (desc != Boolean.TRUE);
            }
        }

        // widths
        TableColumnModel m = v.getTable().getColumnModel();
        int[] widths = new int[m.getColumnCount()];
        String[] properties = new String[m.getColumnCount()];
        for (int i = 0; i < m.getColumnCount(); i++) {
            TableColumn tc = m.getColumn(i);
            ColumnProperty cp = null;
            for (int j = 0; j < columns.length; j++) {
                if (columns[j].getDisplayName().equals(tc.getHeaderValue())) {
                    cp = columns[j];
                }
            }
            if (cp != null) {
                properties[i] = cp.getName();
            } else {
                // tree column
                properties[i] = treeColumn.getName();
            }
            widths[i] = tc.getWidth();
        }

        cc.setValues(properties, widths,
            sortedColumn == null ? null : sortedColumn.getName(), ascending);
    }

    /**
     * Configures view's column widths.
     *
     * @param v view that should be configured   XXX replace by ColumnProperty[]
     * @param cc a columns configuration
     */
    public static void configureColumns(TaskListView v, ColumnsConfiguration cc) {
        String[] properties = cc.getProperties();
        int[] widths = cc.getWidths();
        String sortingColumn = cc.getSortingColumn();
        boolean ascending = cc.getSortingOrder();

        ColumnProperty columns[] = v.getColumns();

        for (int i = 0; i < columns.length; i++) {
            // NOTE reverse logic: this is INvisible
            columns[i].setValue("InvisibleInTreeTableView", // NOI18N
            Boolean.TRUE);
        }

        for (int i = 0; i < properties.length; i++) {
            ColumnProperty c = findColumn(columns, properties[i]);
            if (c != null) {
                // Necessary because by default some columns
                // set invisible by default, so I have to
                // override these
                // NOTE reverse logic: this is INvisible
                c.setValue("InvisibleInTreeTableView", // NOI18N
                Boolean.FALSE);
                c.width = widths[i];
//                LOGGER.fine("configure width: " + c.getName() + " " + c.width);
            }
        }

        // Set sorting attribute
        if (sortingColumn != null) {
            ColumnProperty c = findColumn(columns, sortingColumn);
            if (c != null) {
                c.setValue("SortingColumnTTV", Boolean.TRUE); // NOI18N
                // Descending sort?
                c.setValue("DescendingOrderTTV", // NOI18N
                (!ascending) ? Boolean.TRUE : Boolean.FALSE);
            }
        }
    }

    /**
     * Searches a column by name
     *
     * @param columns view columns
     * @param name name of a property
     * @return found column or null
     */
    private static ColumnProperty findColumn(ColumnProperty columns[], String name) {
        for (int i = 0; i < columns.length; i++) {
            if (columns[i].getName().equals(name))
                return columns[i];
        }

        return null;
    }
}