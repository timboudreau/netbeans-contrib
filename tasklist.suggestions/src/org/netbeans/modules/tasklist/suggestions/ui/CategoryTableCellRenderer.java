/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.tasklist.suggestions.ui;

import java.awt.Component;
import javax.swing.ImageIcon;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import org.netbeans.modules.tasklist.client.Suggestion;

/**
 * Renderer for Image.
 *
 * @author tl
 */
public class CategoryTableCellRenderer extends DefaultTableCellRenderer {
    /**
     * Creates a new instance of ImageTableCellRenderer.
     */
    public CategoryTableCellRenderer() {
        setIcon(new ImageIcon());
    }

    public Component getTableCellRendererComponent(JTable table, Object value, 
            boolean isSelected, boolean hasFocus, int row, int column) {
        super.getTableCellRendererComponent(table, value, isSelected, 
                hasFocus, row, column);
         
        Suggestion s = (Suggestion) value;
        ((ImageIcon) getIcon()).setImage(s.getIcon());
        setText(s.getType());
        
        return this;
    }
}
