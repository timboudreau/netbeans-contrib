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

import org.netbeans.modules.tasklist.usertasks.UserTask;
import org.netbeans.modules.tasklist.usertasks.renderers.PriorityListCellRenderer;

/**
 * TableCellEditor for SuggestionPriority
 */
public class PriorityTableCellEditor extends DefaultCellEditor {
    /**
     * Creates a new instance of PriorityTableCellRenderer
     */
    public PriorityTableCellEditor() {
        super(new JComboBox(new Integer[] {
            new Integer(UserTask.HIGH),
            new Integer(UserTask.MEDIUM_HIGH),
            new Integer(UserTask.MEDIUM),
            new Integer(UserTask.MEDIUM_LOW),
            new Integer(UserTask.LOW),
        }));
        ((JComboBox) editorComponent).setRenderer(new PriorityListCellRenderer());
    }

    public Component getTableCellEditorComponent(JTable table, Object value,
        boolean isSelected, int row, int column) {
        ((JComboBox) editorComponent).setSelectedItem(value);
        return editorComponent;
    }
    
    public Object getCellEditorValue() { 
        return ((JComboBox) editorComponent).getSelectedItem();
    }
}
