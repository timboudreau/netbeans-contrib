package org.netbeans.modules.tasklist.usertasks.renderers;

import java.awt.Component;
import java.awt.FontMetrics;
import java.awt.Insets;
import java.text.MessageFormat;

import javax.swing.table.DefaultTableCellRenderer;
import org.netbeans.modules.tasklist.usertasks.Settings;
import org.netbeans.modules.tasklist.usertasks.UserTask;

import org.openide.util.NbBundle;

/**
 * Cell renderer for duration values
 */
public class DurationTableCellRenderer extends DefaultTableCellRenderer {
    private static final MessageFormat EFFORT_FORMAT = 
        new MessageFormat(NbBundle.getMessage(DurationTableCellRenderer.class, 
            "EffortFormat")); // NOI18N
    private static final MessageFormat SHORT_EFFORT_FORMAT = 
        new MessageFormat(NbBundle.getMessage(DurationTableCellRenderer.class, 
            "ShortEffortFormat")); // NOI18N
    private int[] duration;
    
    /**
     * Creates a new instance of DurationTableCellRenderer
     */
    public DurationTableCellRenderer() {
    }
    
    public Component getTableCellRendererComponent(javax.swing.JTable table, 
        Object value, boolean isSelected, boolean hasFocus, 
        int row, int column) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, 
            row, column);
        if (value == null)
            duration = null;
        else
            duration = UserTask.splitDuration(((Integer) value).intValue(),
                Settings.getDefault().getHoursPerDay());
        return this;
    }
    
    public String getText() {
        if (duration == null)
            return "";
        
        String s = EFFORT_FORMAT.format(new Object[] {
            new Integer(duration[2]), 
            new Integer(duration[1]), 
            new Integer(duration[0])
        }).trim();
        FontMetrics fm = getFontMetrics(getFont());
        int w = getWidth();
        Insets insets = getInsets();
        w -= insets.left + insets.right;
        if (w >= fm.stringWidth(s))
            return s;
        return SHORT_EFFORT_FORMAT.format(new Object[] {
            new Integer(duration[2]), 
            new Integer(duration[1]), 
            new Integer(duration[0])
        }).trim();
    }
}
