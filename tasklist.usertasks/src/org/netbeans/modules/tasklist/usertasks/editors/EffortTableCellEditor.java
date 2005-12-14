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
import java.util.Arrays;
import java.util.ResourceBundle;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;
import org.netbeans.modules.tasklist.usertasks.Settings;
import org.netbeans.modules.tasklist.usertasks.model.Duration;
import org.netbeans.modules.tasklist.usertasks.model.UserTask;
import org.openide.util.NbBundle;

/**
 * TableCellEditor for duration values.
 *
 * @author tl
 */
public class EffortTableCellEditor extends DefaultCellEditor {
    private static String[] TEXTS = {
        "Duration5Min", // NOI18N
        "Duration10Min", // NOI18N
        "Duration15Min", // NOI18N
        "Duration20Min", // NOI18N
        "Duration30Min", // NOI18N
        "Duration45Min", // NOI18N
        "Duration1Hour", // NOI18N
        "Duration1_5Hour", // NOI18N
        "Duration2Hours", // NOI18N
        "Duration2_5Hours", // NOI18N
        "Duration3Hours", // NOI18N
        "Duration4Hours", // NOI18N
        "Duration5Hours", // NOI18N
        "Duration6Hours", // NOI18N
        "Duration7Hours", // NOI18N
        "Duration8Hours" // NOI18N
    };
    
    /**
     * Corresponds to duration values in TAGS.
     * This array must be sorted.
     */
    private static final int[] DURATIONS = new int[] {
        5,
        10,
        15,
        20,
        30,
        45,
        60,
        90,
        120,
        150,
        180,
        240,
        300,
        360,
        420,
        480
    };

    static {
        assert DURATIONS.length == TEXTS.length;
        
        ResourceBundle rb = NbBundle.getBundle(EffortTableCellEditor.class);
        for (int i = 0; i < TEXTS.length; i++) {
            TEXTS[i] = rb.getString(TEXTS[i]);
        }
    }
    
    /**
     * Creates a new instance of PriorityTableCellRenderer
     */
    public EffortTableCellEditor() {
        super(new JComboBox(TEXTS));
    }

    public Component getTableCellEditorComponent(JTable table, Object value,
        boolean isSelected, int row, int column) {
        if (value instanceof UserTask) {
            int d = ((UserTask) value).getEffort();
            int index = Arrays.binarySearch(DURATIONS, d);
            if (index >= 0)
                ((JComboBox) editorComponent).setSelectedIndex(index);
        }
        return editorComponent;
    }
    
    public Object getCellEditorValue() { 
        int index = ((JComboBox) editorComponent).getSelectedIndex();
        return new Integer(DURATIONS[index]);
    }
}
