package org.netbeans.modules.tasklist.usertasks.renderers;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import org.netbeans.modules.tasklist.usertasks.options.Settings;
import org.netbeans.modules.tasklist.usertasks.model.Duration;
import org.netbeans.modules.tasklist.usertasks.model.UserTask;
import org.netbeans.modules.tasklist.usertasks.treetable.AdvancedTreeTableNode;
import org.netbeans.modules.tasklist.usertasks.treetable.TreeTable;

/**
 * Renderer for the effort
 */
public class EffortTableCellRenderer extends DurationTableCellRenderer {
    private Font boldFont, normalFont;
    
    /**
     * Constructor
     */
    public EffortTableCellRenderer() {
    }

    public Component getTableCellRendererComponent(javax.swing.JTable table, 
        Object value, boolean isSelected, boolean hasFocus, 
        int row, int column) {
        if (normalFont == null || !normalFont.equals(table.getFont())) {
            normalFont = table.getFont();
            boldFont = normalFont.deriveFont(Font.BOLD);
        }
        setForeground(null);
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, 
            row, column);
        AdvancedTreeTableNode n = (AdvancedTreeTableNode) 
                ((TreeTable) table).getRenderedNode();
        UserTask ut = n.getObject() instanceof UserTask ? 
            (UserTask) n.getObject() : null;
        if (ut != null) {
            boolean b = ut.getEffort() >= ut.getSpentTime() + 
                    ut.getRemainingEffort();
            setFont(b ? normalFont : boldFont);
            if (!isSelected && !b)
                setForeground(Color.RED);
        }
        return this;
    }

    protected Duration getDuration(Object obj) {
        Integer d = (Integer) obj;
        if (d == null) {
            return null;
        } else {
            return new Duration(d.intValue(),
                Settings.getDefault().getMinutesPerDay(), 
                Settings.getDefault().getDaysPerWeek(), true);
        }
    }
}
