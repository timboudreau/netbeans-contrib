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

package org.netbeans.modules.tasklist.usertasks.editors;

import java.awt.Component;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;

import org.netbeans.modules.tasklist.usertasks.model.UserTask;
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
