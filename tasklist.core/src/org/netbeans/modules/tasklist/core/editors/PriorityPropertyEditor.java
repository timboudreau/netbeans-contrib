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

package org.netbeans.modules.tasklist.core.editors;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyEditorSupport;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import org.netbeans.modules.tasklist.client.SuggestionPriority;
import org.netbeans.modules.tasklist.core.PriorityListCellRenderer;
import org.netbeans.modules.tasklist.core.Task;

/**
 * PropertyEditor for task priorities.
 *
 * TODO: investigate this call
 * putClientProperty("JComboBox.isTableCellEditor", Boolean.TRUE);
 *
 * @author Tim Lebedkov
 */
public class PriorityPropertyEditor extends PropertyEditorSupport {
    private static final String[] TAGS = Task.getPriorityNames();
    private static final JLabel LABEL = new JLabel();
    
    /**
     * Constructor
     */
    public PriorityPropertyEditor() {
    }

    public String getAsText() {
        return TAGS[getIntValue() - 1];
    }

    public void setAsText(String text) throws java.lang.IllegalArgumentException {
        int index = -1;
        for (int i = 0; i < TAGS.length; i++) {
            if (text.equals(TAGS[i])) {
                index = i;
                break;
            }
        }
        assert index != -1 : "Unknown Tag"; // NOI18N
        
        setValue(Task.getPriority(index + 1));
    }
    
    public boolean isPaintable() {
        return true;
    }

    public void paintValue(java.awt.Graphics gfx, java.awt.Rectangle box) {
        gfx.translate(box.x, box.y);
        LABEL.setForeground(PriorityListCellRenderer.COLORS[getIntValue() - 1]);
        LABEL.setText(getAsText());
        LABEL.setSize(box.width, box.height);
        LABEL.paint(gfx);
        gfx.translate(-box.x, -box.y);
    }

    /**
     * Returns value as integer
     *
     * @return value
     */
    private int getIntValue() {
        Object v = getValue();
        if (v instanceof SuggestionPriority)
            return ((SuggestionPriority) v).intValue();
        else
            return SuggestionPriority.LOW.intValue();
    }
    
    public String[] getTags() {
        return TAGS;
    }
}
