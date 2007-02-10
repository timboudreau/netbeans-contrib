package org.netbeans.modules.tasklist.usertasks.renderers;

import java.awt.Component;
import java.awt.FontMetrics;
import java.awt.Insets;
import java.text.MessageFormat;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;
import org.netbeans.modules.tasklist.usertasks.options.Settings;
import org.netbeans.modules.tasklist.usertasks.model.Duration;
import org.netbeans.modules.tasklist.usertasks.util.DurationFormat;

/**
 * Cell renderer for duration values
 *
 * @author tl
 */
public class DurationTableCellRenderer extends DefaultTableCellRenderer {
    private static final DurationFormat EFFORT_FORMAT = 
            new DurationFormat(DurationFormat.Type.LONG);
    private static final DurationFormat SHORT_EFFORT_FORMAT = 
            new DurationFormat(DurationFormat.Type.SHORT);

    public Component getTableCellRendererComponent(javax.swing.JTable table, 
        Object value, boolean isSelected, boolean hasFocus, 
        int row, int column) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, 
            row, column);
        
        Duration duration = getDuration(value);
        String txt;
        if (duration == null)
            txt = ""; // NOI18N
        else {
            String s = EFFORT_FORMAT.format(duration);
            FontMetrics fm = getFontMetrics(getFont());
            TableColumnModel tcm = table.getColumnModel();
            int w = tcm.getColumn(column).getWidth() - 
                    tcm.getColumnMargin();
            Insets insets = getInsets();
            w -= insets.left + insets.right;
            if (w >= fm.stringWidth(s))
                txt = s;
            else
                txt = SHORT_EFFORT_FORMAT.format(duration).trim();
        }
        setText(txt);
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
                    Settings.getDefault().getMinutesPerDay(), 
                    Settings.getDefault().getDaysPerWeek(), true);
    }

    // overriden for performance reasons
    @Override
    protected void setValue(Object value) {
    }
}
