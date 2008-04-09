/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.modules.cnd.profiler.views;

import org.netbeans.spi.viewmodel.ColumnModel;

/**
 *
 * @author eu155513
 */
public abstract class PropertyColumnModel extends ColumnModel {
    private int orderNumer = -1;
    private boolean sorted = false;
    private boolean sortedDesc = false;
    private boolean visible = true;
    
    @Override
    public int getCurrentOrderNumber() {
        return orderNumer;
    }

    @Override
    public void setCurrentOrderNumber(int orderNumber) {
        this.orderNumer = orderNumber;
    }

    @Override
    public boolean isSorted() {
        return sorted;
    }

    @Override
    public boolean isSortedDescending() {
        return sortedDesc;
    }

    @Override
    public boolean isVisible() {
        return visible;
    }

    @Override
    public void setSorted(boolean sorted) {
        this.sorted = sorted;
    }

    @Override
    public void setSortedDescending(boolean sortDesc) {
        this.sortedDesc = sortDesc;
    }

    @Override
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
    
    public static ColumnModel createColumn(
            final String name, 
            final String id, 
            final Class type) {
        return new PropertyColumnModel() {
            @Override
            public String getID() {
                return id;
            }

            @Override
            public String getDisplayName() {
                return name;
            }

            @Override
            public Class getType() {
                return type;
            }
        };
    }

    private PropertyColumnModel() {
    }
    
    public static ColumnModel createTimeColumnModel() {
        return createColumn("time", "timeID", Double.class);
    }
    
    public static ColumnModel createSelfTimeColumnModel() {
        return createColumn("Self time", "selftimeID", Double.class);
    }
}
