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

import java.beans.PropertyEditorSupport;
import javax.swing.JLabel;
import org.netbeans.modules.tasklist.client.SuggestionPriority;
import org.netbeans.modules.tasklist.core.PriorityListCellRenderer;

import org.netbeans.modules.tasklist.usertasks.model.UserTask;

/**
 * PropertyEditor for task priorities.
 *
 * @author Tim Lebedkov
 */
public final class PriorityPropertyEditor extends PropertyEditorSupport {
    private static final String[] TAGS = UserTask.getPriorityNames();
    private static final JLabel LABEL = new JLabel();
    
    /**
     * Constructor
     */
    public PriorityPropertyEditor() {
    }

    public String getAsText() {
        Object v = getValue();
        if (v instanceof Integer) {
            int value = ((Integer) v).intValue();
            return UserTask.getPriorityName(value);
        } else {
            return "";
        }
    }

    public void setAsText(String text) throws java.lang.IllegalArgumentException {
        int index = UserTask.getPriority(text);
        if (index == -1) 
            throw new IllegalArgumentException("Unknown priority");
        
        setValue(new Integer(index));
    }
    
    public boolean isPaintable() {
        return true;
    }

    public void paintValue(java.awt.Graphics gfx, java.awt.Rectangle box) {
        Object v = getValue();
        if (v instanceof Integer) {
            int value = ((Integer) v).intValue();
            gfx.translate(box.x, box.y);
            
            // FIXME take into account background color
            LABEL.setForeground(PriorityListCellRenderer.COLORS[value - 1]);
            LABEL.setText(getAsText());
            LABEL.setSize(box.width, box.height);
            LABEL.paint(gfx);
            gfx.translate(-box.x, -box.y);
        }
    }

    public String[] getTags() {
        return TAGS;
    }
}
