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
public class PriorityTableCellEditor extends DefaultCellEditor {
    /**
     * Creates a new instance of PriorityTableCellRenderer
     */
    public PriorityTableCellEditor() {
        super(new JComboBox(SuggestionPriority.getPriorityNames()));
        ((JComboBox) editorComponent).setRenderer(new PriorityListCellRenderer());
    }

    public Component getTableCellEditorComponent(JTable table, Object value,
        boolean isSelected, int row, int column) {
        int index = ((SuggestionPriority) value).intValue() - 1;
        ((JComboBox) editorComponent).setSelectedIndex(index);
        return editorComponent;
    }
    
    public Object getCellEditorValue() { 
        return SuggestionPriority.getPriority(
            ((JComboBox) editorComponent).getSelectedIndex() + 1);
    }
}
