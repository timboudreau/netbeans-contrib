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
     * @return properties
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
     * Loads column configuration from a view
     *
     * @param v a view
     */
    public void loadFrom(TaskListView v) {
        // String[]
        ArrayList props = new ArrayList();
        
        sortingColumn = null;
        ColumnProperty columns[] = v.getColumns();
        for (int i = 0; i < columns.length; i++) {
            Boolean treeColumn = 
                (Boolean) columns[i].getValue("TreeColumnTTV"); // NOI18N
            // Is the column visible?
            Boolean invisible =
                (Boolean) columns[i].getValue("InvisibleInTreeTableView"); // NOI18N
            
            // Grrr.... openide must not be using the Boolean enum's;
            // it must be creating new Boolean objects.... so I've
            // gotta use boolean value instead of this nice line:
            //    if (!(invisible == Boolean.TRUE)) {
            if (treeColumn != null && treeColumn.booleanValue()) {
                props.add(0, columns[i].getName());
            } else if ((invisible == null) || !invisible.booleanValue()) {
                props.add(columns[i].getName());
            }

            Boolean sorting = (Boolean) columns[i].getValue( 
                "SortingColumnTTV"); // NOI18N
            if ((sorting != null) && (sorting.booleanValue())) {
                sortingColumn = columns[i].getName();
                Boolean desc = (Boolean) columns[i].getValue( 
                    "DescendingOrderTTV"); // NOI18N
                ascending = (desc != Boolean.TRUE);
            }
        }

        TableColumnModel m = v.getTable().getColumnModel();
        properties = (String[]) props.toArray(new String[props.size()]);
        
        widths = new int[props.size()];
        for (int i = 0; i < props.size(); i++) {
            widths[i] = m.getColumn(i).getWidth();
        }
        
        fireChange();
    }

    /**
     * Configures view's column widths.
     *
     * @param v view that should be configured
     */
    public void configure(TaskListView v) {
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
    private void fireChange() {
        ChangeEvent e = null;
        for (int i = 0; i < listeners.size(); i++) {
            ChangeListener l = (ChangeListener) listeners.get(i);
            if (e == null)
                e = new ChangeEvent(this);
            l.stateChanged(e);
        }
    }
}