/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.tasklist.core.table;

import java.util.Comparator;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

/**
 * This class describes sorting in a table.
 * 
 * @author tl
 */
public class SortingModel {
    /**
     * Compares Comparables
     */
    public static class DefaultComparator implements
            Comparator<Comparable<Object>> {
        public int compare(Comparable<Object> o1, Comparable<Object> o2) {
            if (o1 == null && o2 == null)
                return 0;
            if (o1 == null)
                return -1;
            if (o2 == null)
                return 1;
            return o1.compareTo(o2);
        }
    }

    public static Comparator DEFAULT_COMPARATOR =
        new DefaultComparator();
    
    private EventListenerList listenerList = new EventListenerList();
    private int sortedColumn = -1;
    private boolean descending = true;

    /**
     * Fires a change event that is used to notify listeners about the
     * changes in SortingModel
     */
    protected void fireChange() {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        ChangeEvent e = null;
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ChangeListener.class) {
                // Lazily create the event:
                if (e == null)
                    e = new ChangeEvent(this);
                ((ChangeListener) listeners[i + 1]).stateChanged(e);
            }
        }
    }
    
    /**
     * Checks whether a column is sortable.
     * This implementation always returns true
     *
     * @param modelColumn column number
     * @return true = sortable
     */
    public boolean isColumnSortable(int modelColumn) {
        return true;
    }
    
    /**
     * Returns sorting order
     *
     * @return true = descending, false = ascending
     */
    public boolean isSortOrderDescending() {
        return descending;
    }
    
    /**
     * Sets sorting order
     *
     * @param d true = descending, false = ascending
     */
    public void setSortOrderDescending(boolean d) {
        this.descending = d;
        fireChange();
    }
    
    /**
     * Changes the sorted column
     *
     * @param index column number in the model or -1 if not sorted
     */
    public void setSortedColumn(int index) {
        this.sortedColumn = index;
        fireChange();
    }
    
    /**
     * Returns the sorted column
     *
     * @return column number in the model or -1 if not sorted
     */
    public int getSortedColumn() {
        return sortedColumn;
    }
    
    /**
     * Adds a listener
     *
     * @param l a listener
     */
    public void addChangeListener(ChangeListener l) {
        this.listenerList.add(ChangeListener.class, l);
    }
    
    /**
     * Removes a listener
     *
     * @param l a listener to be removed
     */
    public void removeChangeListener(ChangeListener l) {
        this.listenerList.remove(ChangeListener.class, l);
    }
}
