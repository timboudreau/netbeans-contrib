package org.netbeans.modules.tasklist.usertasks.renderers;

import javax.swing.table.DefaultTableCellRenderer;

/**
 * Cell renderer for line numbers
 */
public class LineTableCellRenderer extends DefaultTableCellRenderer {
    /**
     * Creates a new instance of LineTableCellRenderer
     */
    public LineTableCellRenderer() {
    }
    
    public java.awt.Component getTableCellRendererComponent(
        javax.swing.JTable table, Object value, boolean isSelected, 
        boolean hasFocus, int row, int column) {
        super.getTableCellRendererComponent(
            table, value, isSelected, hasFocus, row, column);
        if (value == null)
            return this;
        int n = ((Integer) value).intValue();
        if (n == 0)
            setText("");
        else
            setText(Integer.toString(n));
        return this;
    }    
}
