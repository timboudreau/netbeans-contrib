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

package org.netbeans.modules.tasklist.usertasks.renderers;

import org.netbeans.modules.tasklist.core.PriorityListCellRenderer;
import java.awt.Component;
import java.awt.Image;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import org.netbeans.modules.tasklist.usertasks.model.UserTask;
import org.openide.util.Utilities;

/**
 * TableCellRenderer for priorities
 *
 * @author Petr Kuzel
 */
public final class PriorityTableCellRenderer extends DefaultTableCellRenderer {
    private static final Image LOW = Utilities.loadImage(
        "org/netbeans/modules/tasklist/usertasks/renderers/low.gif"); // NOI18N
    private static final Image MEDIUM_LOW = Utilities.loadImage(
        "org/netbeans/modules/tasklist/usertasks/renderers/medium-low.gif"); // NOI18N
    private static final Image HIGH = Utilities.loadImage(
        "org/netbeans/modules/tasklist/usertasks/renderers/high.gif"); // NOI18N
    private static final Image MEDIUM_HIGH = Utilities.loadImage(
        "org/netbeans/modules/tasklist/usertasks/renderers/medium-high.gif"); // NOI18N
    private static final Image MEDIUM = Utilities.loadImage(
        "org/netbeans/modules/tasklist/usertasks/renderers/empty.gif"); // NOI18N
    
    private static final long serialVersionUID = 1;

    private ImageIcon icon = new ImageIcon();
    
    public Component getTableCellRendererComponent(JTable table, Object value, 
        boolean isSelected, boolean cellHasFocus, int row, int col) {
        super.getTableCellRendererComponent(table, value, isSelected, 
            cellHasFocus, row, col);
        if (value != null) {
            int prio = ((Integer) value).intValue();
            setText(UserTask.getPriorityNames()[prio - 1]);
            if (!isSelected) {
                setForeground(PriorityListCellRenderer.COLORS[prio - 1]);
            }
            
            Image im;
            switch (prio) {
                case UserTask.HIGH:
                    im = HIGH;
                    break;
                case UserTask.LOW:
                    im = LOW;
                    break;
                case UserTask.MEDIUM_HIGH:
                    im = MEDIUM_HIGH;
                    break;
                case UserTask.MEDIUM_LOW:
                    im = MEDIUM_LOW;
                    break;
                default:
                    im = MEDIUM;
            }
            icon.setImage(im);
            setIcon(icon);
        } else {
            icon.setImage(MEDIUM);
            setIcon(icon);
        }
        return this;
    }
}
