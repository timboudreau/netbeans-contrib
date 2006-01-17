package org.netbeans.modules.tasklist.usertasks.renderers;

import java.awt.Component;
import java.awt.FontMetrics;
import java.awt.Insets;
import java.text.MessageFormat;

import javax.swing.table.DefaultTableCellRenderer;
import org.netbeans.modules.tasklist.usertasks.Settings;
import org.netbeans.modules.tasklist.usertasks.model.Duration;

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
    protected Duration duration;

    public Component getTableCellRendererComponent(javax.swing.JTable table, 
        Object value, boolean isSelected, boolean hasFocus, 
        int row, int column) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, 
            row, column);
        duration = getDuration(value);
        return this;
    }

    /**
     * Retrieves duration from the given object
     *
     * @param obj object from the getTableCellRendererComponent method
     * @return duration or null
     */
    protected Duration getDuration(Object obj) {
        if (obj == null)
            return null;
        else
            return new Duration(((Integer) obj).intValue(),
                Settings.getDefault().getHoursPerDay(), Settings.getDefault().getDaysPerWeek());
    }
    
    public String getText() {
        if (duration == null)
            return ""; // NOI18N
        
        String s = EFFORT_FORMAT.format(new Object[] {
            new Integer(duration.weeks),
            new Integer(duration.days), 
            new Integer(duration.hours), 
            new Integer(duration.minutes)
        }).trim();
        FontMetrics fm = getFontMetrics(getFont());
        int w = getWidth();
        Insets insets = getInsets();
        w -= insets.left + insets.right;
        if (w >= fm.stringWidth(s))
            return s;
        return SHORT_EFFORT_FORMAT.format(new Object[] {
            new Integer(duration.weeks),
            new Integer(duration.days), 
            new Integer(duration.hours), 
            new Integer(duration.minutes)
        }).trim();
    }
}
