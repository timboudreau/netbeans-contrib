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
import org.openide.ErrorManager;
import org.openide.text.Line;
import org.openide.util.NbBundle;

/**
 * PropertyEditor for line numbers.
 */
public final class LineNumberPropertyEditor extends PropertyEditorSupport {
    private static final JLabel LABEL = new JLabel();

    static {
        LABEL.setHorizontalAlignment(SwingConstants.RIGHT);
    }

    public boolean isPaintable() {
        return true;
    }

    public void paintValue(java.awt.Graphics gfx, java.awt.Rectangle box) {
        if (box.width <= 3)
            return;
        
        gfx.translate(box.x, box.y);
        LABEL.setText(getAsText());
        LABEL.setSize(box.width - 3, box.height);
        LABEL.setForeground(gfx.getColor());
        LABEL.paint(gfx);
        gfx.translate(-box.x, -box.y);
    }

    public void setAsText(String text) throws java.lang.IllegalArgumentException {
        try {
            if (text.trim().length() == 0)
                setValue(new Integer(0));
            else
                setValue(new Integer(Integer.parseInt(text)));
        } catch (NumberFormatException nfe) {
            String msg = NbBundle.getMessage(LineNumberPropertyEditor.class, 
                "IllegalLineNumber", new Object[] {text}); //NOI18N
            RuntimeException iae = new IllegalArgumentException(msg); //NOI18N
            ErrorManager.getDefault().annotate(iae, ErrorManager.USER, msg,
                msg, nfe, new java.util.Date());
            throw iae;
        }
    }
    
    public String getAsText() {
        Object v = getValue();
        String s;
        if (v instanceof Line) {
            s = String.valueOf(((Line) v).getLineNumber() + 1);
        } else if (v instanceof Integer) {
            int n = ((Integer) v).intValue();
            if (n <= 0)
                s = "";
            else
                s = String.valueOf(n);
        } else {
            s = "";
        }
        return s;
    }
}
