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

import java.beans.PropertyEditorSupport;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

/**
 * PropertyEditor for line numbers.
 *
 * @author Tim Lebedkov
 */
public class ColumnPropertyEditor extends PropertyEditorSupport {
    private static final JLabel LABEL = new JLabel();
    
    static {
        LABEL.setHorizontalAlignment(SwingConstants.RIGHT);
    }
    
    public boolean isPaintable() {
        return true;
    }

    public void paintValue(java.awt.Graphics gfx, java.awt.Rectangle box) {
        if (getIntValue() <= 0) return;
        
        gfx.translate(box.x, box.y);
        LABEL.setText(String.valueOf(getIntValue()));
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
        Integer a = ((Integer) getValue());
        if (a != null)
            return a.intValue();
        else
            return 0;
    }
}
