/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
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
	    setBorder( UIManager.getBorder("Table.focusCellHighlightBorder") );
	} else {
	    setBorder(noFocusBorder);
	}
        
        setSelected((value != null && ((Boolean)value).booleanValue()));
        return this;
    }
    
    // workaround for a Swing bug (?)
    protected void paintComponent(java.awt.Graphics g) {
        Rectangle oldClip = g.getClipBounds();
        g.setClip(oldClip.x, oldClip.y, 
            oldClip.width - 1, 
            oldClip.height - 1);
        super.paintComponent(g);
        g.setClip(oldClip);
    }    
}
