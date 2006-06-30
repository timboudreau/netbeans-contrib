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

package org.netbeans.modules.tasklist.core.editors;

import java.beans.PropertyEditorSupport;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

/**
 * PropertyEditor for line numbers.
 *
 * @author tl
 */
public final class ColumnPropertyEditor extends PropertyEditorSupport {
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
