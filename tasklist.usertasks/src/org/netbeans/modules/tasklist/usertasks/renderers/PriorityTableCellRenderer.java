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

import java.awt.Component;
import java.awt.Image;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import org.netbeans.modules.tasklist.usertasks.model.UserTask;
import org.netbeans.modules.tasklist.usertasks.util.FastImageIcon;
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

    private ImageIcon icon = new FastImageIcon();
    
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

    // overriden for performance reasons
    @Override
    protected void setValue(Object arg0) {
    }
}
