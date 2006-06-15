/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.debugger.localsviewenhancements.ui.models;

import java.beans.PropertyEditor;
import org.netbeans.api.debugger.Properties;
import org.netbeans.spi.viewmodel.ColumnModel;
import org.openide.util.NbBundle;

/**
 *
 * @author Sandip V. Chitale (Sandip.Chitale@Sun.Com)
 */
public class ColumnModels {
    /**
     * Defines model for one table view column. Can be used together with
     * {@link org.netbeans.spi.viewmodel.TreeModel} for tree
     * table view representation.
     */
    private static class AbstractColumn extends ColumnModel {
        
        private String id;
        private String previousColumnId = null;
        private String nextColumnId = null;
        private String displayName;
        private String shortDescription;
        private Class type;
        private boolean defaultVisible;
        private PropertyEditor propertyEditor;
        
        Properties properties = Properties.getDefault().
                getProperties("debugger").getProperties("views");
        
        public AbstractColumn(String id, String displayName, String shortDescription,
                Class type) {
            this(id, displayName, shortDescription, type, true);
        }
        
        public AbstractColumn(String id, String displayName, String shortDescription,
                Class type, boolean defaultVisible) {
            this(id, null, null, displayName, shortDescription, type, defaultVisible);
        }
        
        public AbstractColumn(String id, String previousColumnId, String nextColumnId,
                String displayName, String shortDescription,
                Class type, boolean defaultVisible) {
            this(id, previousColumnId, nextColumnId, displayName, shortDescription,
                    type, defaultVisible, null);
        }
        
        public AbstractColumn(String id, String previousColumnId, String nextColumnId,
                String displayName, String shortDescription,
                Class type, boolean defaultVisible,
                PropertyEditor propertyEditor) {
            this.id = id;
            this.previousColumnId = previousColumnId;
            this.nextColumnId = nextColumnId;
            this.displayName = displayName;
            this.shortDescription = shortDescription;
            this.type = type;
            this.defaultVisible = defaultVisible;
            this.propertyEditor = propertyEditor;
        }
        
        public String getID() {
            return id;
        }
        
        public String getPreviuosColumnID() {
            return previousColumnId;
        }
        
        public String getNextColumnID() {
            return nextColumnId;
        }
        
        public String getDisplayName() {
            return NbBundle.getBundle(ColumnModels.class).getString(displayName);
        }
        
        public String getShortDescription() {
            return NbBundle.getBundle(ColumnModels.class).getString(shortDescription);
        }
        
        public Class getType() {
            return type;
        }
        
        /**
         * Set true if column is visible.
         *
         * @param visible set true if column is visible
         */
        public void setVisible(boolean visible) {
            properties.setBoolean(getID() + ".visible", visible);
        }
        
        /**
         * Set true if column should be sorted by default.
         *
         * @param sorted set true if column should be sorted by default
         */
        public void setSorted(boolean sorted) {
            properties.setBoolean(getID() + ".sorted", sorted);
        }
        
        /**
         * Set true if column should be sorted by default in descending order.
         *
         * @param sortedDescending set true if column should be
         *        sorted by default in descending order
         */
        public void setSortedDescending(boolean sortedDescending) {
            properties.setBoolean(
                    getID() + ".sortedDescending",
                    sortedDescending
                    );
        }
        
        /**
         * Should return current order number of this column.
         *
         * @return current order number of this column
         */
        public int getCurrentOrderNumber() {
            return properties.getInt(getID() + ".currentOrderNumber", -1);
        }
        
        /**
         * Is called when current order number of this column is changed.
         *
         * @param newOrderNumber new order number
         */
        public void setCurrentOrderNumber(int newOrderNumber) {
            properties.setInt(
                    getID() + ".currentOrderNumber",
                    newOrderNumber
                    );
        }
        
        /**
         * Return column width of this column.
         *
         * @return column width of this column
         */
        public int getColumnWidth() {
            return properties.getInt(getID() + ".columnWidth", 150);
        }
        
        /**
         * Is called when column width of this column is changed.
         *
         * @param newColumnWidth a new column width
         */
        public void setColumnWidth(int newColumnWidth) {
            properties.setInt(getID() + ".columnWidth", newColumnWidth);
        }
        
        /**
         * True if column should be visible by default.
         *
         * @return true if column should be visible by default
         */
        public boolean isVisible() {
            return properties.getBoolean(getID() + ".visible", defaultVisible);
        }
        
        /**
         * True if column should be sorted by default.
         *
         * @return true if column should be sorted by default
         */
        public boolean isSorted() {
            return properties.getBoolean(getID() + ".sorted", false);
        }
        
        /**
         * True if column should be sorted by default in descending order.
         *
         * @return true if column should be sorted by default in descending
         * order
         */
        public boolean isSortedDescending() {
            return properties.getBoolean(
                    getID() + ".sortedDescending",
                    false
                    );
        }
        
        /**
         * Returns {@link java.beans.PropertyEditor} to be used for
         * this column. Default implementation returns <code>null</code> -
         * means use default PropertyEditor.
         *
         * @return {@link java.beans.PropertyEditor} to be used for
         *         this column
         */
        public PropertyEditor getPropertyEditor() {
            return propertyEditor;
        }
    }
    
    /**
     * Defines model for one table view column. Can be used together with
     * {@link org.netbeans.spi.viewmodel.TreeModel} for tree
     * table view representation.
     */
    public static ColumnModel createLocalsModifiersColumn() {
        return new AbstractColumn(Constants.LOCALS_MODIFIERS_COLUMN_ID,
                "LocalsToString", // NOI18N Hardcoded :(
                Constants.LOCALS_DECLARED_TYPE_COLUMN_ID,
                "CTL_LocalsView_Column_Modifiers_Name", // NOI18N
                "CTL_LocalsView_Column_Modifiers_Desc", // NOI18N
                String.class,
                true);
    }
    
    /**
     * Defines model for one table view column. Can be used together with
     * {@link org.netbeans.spi.viewmodel.TreeModel} for tree
     * table view representation.
     */
    public static ColumnModel createLocalsDeclaredTypeColumn() {
        return new AbstractColumn(Constants.LOCALS_DECLARED_TYPE_COLUMN_ID,
                Constants.LOCALS_MODIFIERS_COLUMN_ID,
                null,
                "CTL_LocalsView_Column_Declared_Type_Name", // NOI18N
                "CTL_LocalsView_Column_Declared_Type_Desc", // NOI18N
                String.class,
                true);
    }
    
    /**
     * Defines model for one table view column. Can be used together with
     * {@link org.netbeans.spi.viewmodel.TreeModel} for tree
     * table view representation.
     */
    public static ColumnModel createLocalsDeclaredInColumn() {
        return new AbstractColumn(Constants.LOCALS_DECLARED_IN_COLUMN_ID,
                Constants.LOCALS_DECLARED_TYPE_COLUMN_ID,
                null,
                "CTL_LocalsView_Column_Declared_In_Name", // NOI18N
                "CTL_LocalsView_Column_Declared_In_Desc", // NOI18N
                String.class,
                true);
    }
}
