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
}