/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.tasklist.core.editors;

import org.netbeans.modules.tasklist.client.SuggestionPriority;
import org.netbeans.modules.tasklist.core.PriorityListCellRenderer;

import java.awt.Color;
import java.awt.Component;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * TableCellRenderer for priorities
 *
 * @author Petr Kuzel
 */
public final class PriorityTableCellRenderer extends DefaultTableCellRenderer {

    private static final long serialVersionUID = 1;

    private static final String[] TAGS = SuggestionPriority.getPriorityNames();


    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean cellHasFocus, int row, int col) {
        super.getTableCellRendererComponent(table, value, isSelected, cellHasFocus, row, col);
        if (value instanceof SuggestionPriority) {
            SuggestionPriority prio = (SuggestionPriority) value;
            setText(TAGS[prio.intValue() - 1]);
            if (!isSelected) {
                setForeground(PriorityListCellRenderer.COLORS[prio.intValue() - 1]);
            }
        }
        return this;
    }
}
