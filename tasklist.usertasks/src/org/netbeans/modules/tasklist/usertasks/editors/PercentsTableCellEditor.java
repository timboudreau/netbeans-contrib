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

package org.netbeans.modules.tasklist.usertasks.editors;

import java.awt.Component;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import org.netbeans.modules.tasklist.client.SuggestionPriority;
import org.netbeans.modules.tasklist.core.PriorityListCellRenderer;
import org.netbeans.modules.tasklist.usertasks.treetable.AbstractCellEditor;

/**
 * TableCellEditor for SuggestionPriority
 */
public class PercentsTableCellEditor extends DefaultCellEditor {
    private static String[] TAGS = {
        "0%", "5%", "10%", "15%", "20%", "25%", "30%", "35%", "40%", "45%", "50%", // NOI18N
        "55%", "60%", "65%", "70%", "75%", "80%", "85%", "90%", "95%", "100%" // NOI18N
    };
    
    /**
     * Creates a new instance of PriorityTableCellRenderer
     */
    public PercentsTableCellEditor() {
        super(new JComboBox(TAGS));
    }

    public Component getTableCellEditorComponent(JTable table, Object value,
        boolean isSelected, int row, int column) {
        int p = ((Integer) value).intValue();
        ((JComboBox) editorComponent).setSelectedIndex((p + 4) / 5);
        return editorComponent;
    }
    
    public Object getCellEditorValue() { 
        int index = ((JComboBox) editorComponent).getSelectedIndex();
        return new Integer(index * 5);
    }
}
