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

package org.netbeans.modules.tasklist.usertasks.renderers;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.Date;
import javax.swing.JTable;
import org.netbeans.modules.tasklist.usertasks.Settings;
import org.netbeans.modules.tasklist.usertasks.UserTaskTreeTableNode;
import org.netbeans.modules.tasklist.usertasks.model.Duration;
import org.netbeans.modules.tasklist.usertasks.model.UserTask;

import org.netbeans.modules.tasklist.usertasks.treetable.TreeTable;

/**
 * Renderer for the due date
 *
 * @author tl
 */
public class DueDateTableCellRenderer extends DateTableCellRenderer {
    private Font boldFont, normalFont;
    
    /**
     * Constructor
     */
    public DueDateTableCellRenderer() {
    }

    protected Duration getDuration(Object obj) {
        UserTask ut = (UserTask) obj;
        if (ut == null) {
            return null;
        } else {
            return new Duration(ut.getEffort(),
                Settings.getDefault().getHoursPerDay(), 
                Settings.getDefault().getDaysPerWeek());
        }
    }

    public Component getTableCellRendererComponent(
        JTable table, 
        Object value, boolean isSelected, boolean hasFocus, 
        int row, int column) {
        Object node = ((TreeTable) table).getNodeForRow(row);
        if (normalFont == null || !normalFont.equals(table.getFont())) {
            normalFont = table.getFont();
            boldFont = normalFont.deriveFont(Font.BOLD);
        }
        setForeground(null);
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, 
            row, column);
        if (node instanceof UserTaskTreeTableNode) {
            UserTaskTreeTableNode n = (UserTaskTreeTableNode) node;
            UserTask ut = (UserTask) n.getUserTask();
            Date due = ut.getDueDate();
            boolean overdue = false;
            if (!ut.isDone()) {
                if (due != null &&
                    due.getTime() < System.currentTimeMillis())
                    overdue = true;
            } else {
                if (due != null &&
                        due.getTime() < ut.getCompletedDate())
                    overdue = true;
            }
            setFont(overdue ? boldFont : normalFont);
            if (!isSelected && overdue)
                setForeground(Color.RED);
        }
        return this;
    }
}
