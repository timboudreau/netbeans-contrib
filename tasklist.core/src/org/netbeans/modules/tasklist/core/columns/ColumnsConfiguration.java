/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */ 

package org.netbeans.modules.tasklist.core.columns;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.font.FontRenderContext;
import java.beans.PropertyChangeSupport;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableColumn;

import org.netbeans.modules.tasklist.core.ColumnProperty;
import org.netbeans.modules.tasklist.core.TaskListView;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.Repository;

/**
 * View's columns
 *
 * @author Tim Lebedkov
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
    public void fireChange() {
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