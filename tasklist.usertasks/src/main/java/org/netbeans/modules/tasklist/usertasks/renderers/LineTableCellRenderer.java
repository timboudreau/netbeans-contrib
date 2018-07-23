package org.netbeans.modules.tasklist.usertasks.renderers;

import javax.swing.table.DefaultTableCellRenderer;

/**
 * Cell renderer for line numbers
 */
public class LineTableCellRenderer extends DefaultTableCellRenderer {
    @Override
    protected void setValue(Object value) {
        if (value == null)
            return;
        int n = ((Integer) value).intValue();
        if (n == 0)
            setText(""); // NOI18N
        else
            setText(Integer.toString(n));
    }
}
