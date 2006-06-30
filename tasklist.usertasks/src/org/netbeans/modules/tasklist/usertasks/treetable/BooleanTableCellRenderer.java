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

package org.netbeans.modules.tasklist.usertasks.treetable;

import java.awt.Component;
import java.awt.Rectangle;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

/**
 * Table cell renderer for boolean values
 */
public class BooleanTableCellRenderer extends JCheckBox
implements TableCellRenderer {
    private DefaultTableCellRenderer def = new DefaultTableCellRenderer();
    protected static Border noFocusBorder = new EmptyBorder(1, 1, 1, 1); 
    
    /**
     * Creates a new instance of BooleanTableCellRenderer
     */
    public BooleanTableCellRenderer() {
        setHorizontalAlignment(JLabel.CENTER);
        setBorder(noFocusBorder);
        setBorderPainted(true);
        setBorderPaintedFlat(true);
        setOpaque(true);
    }

    public Component getTableCellRendererComponent(JTable table, Object value,
        boolean isSelected, boolean hasFocus, int row, int column) {
        if (value == null)
            return def.getTableCellRendererComponent(table, value, 
                isSelected, hasFocus, row, column);
        if (isSelected) {
            setForeground(table.getSelectionForeground());
            super.setBackground(table.getSelectionBackground());
        }
        else {
            setForeground(table.getForeground());
            setBackground(table.getBackground());
        }

	if (hasFocus) {
	    setBorder(UIManager.getBorder("Table.focusCellHighlightBorder") ); // NOI18N
	} else {
	    setBorder(noFocusBorder);
	}
        
        setSelected((value != null && ((Boolean)value).booleanValue()));
        return this;
    }
}
