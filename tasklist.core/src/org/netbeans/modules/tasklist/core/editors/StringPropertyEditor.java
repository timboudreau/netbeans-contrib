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

/**
 * PropertyEditor for String.class.
 * Does not paint non-editable values in gray as default property editor for
 * strings.
 *
 * @author Tim Lebedkov
 */
public class StringPropertyEditor extends PropertyEditorSupport {
    public boolean isPaintable() {
        return true;
    }

    public void paintValue(java.awt.Graphics gfx, java.awt.Rectangle box) {
        int y = gfx.getFontMetrics().getHeight() - gfx.getFontMetrics().getDescent();
        gfx.drawString(getAsText(), box.x , box.y + y);
    }
}
