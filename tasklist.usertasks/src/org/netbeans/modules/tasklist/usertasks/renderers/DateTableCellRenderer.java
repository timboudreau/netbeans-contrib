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

package org.netbeans.modules.tasklist.usertasks.renderers;

import java.awt.Component;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.table.DefaultTableCellRenderer;
import org.netbeans.modules.tasklist.usertasks.DateSelectionPanel;

/**
 * A table cell renderer for the Date class.
 *
 * @author tl
 */
public class DateTableCellRenderer extends DefaultTableCellRenderer {
    private static SimpleDateFormat format = new SimpleDateFormat();

    // TODO: not used
    public void setAsText(String s) throws java.lang.IllegalArgumentException {
        if (s.trim().length() == 0) {
            setValue(null);
            return;
        }
        /*try {
            // TODO: setValue(format.parse(s));
        } catch (ParseException e) {
            String msg = NbBundle.getMessage(DateTableCellRenderer.class,
                "IllegalDateValue", new Object[] {s}); //NOI18N
            RuntimeException iae = new IllegalArgumentException(msg); 
            ErrorManager.getDefault().annotate(iae, ErrorManager.USER, msg,
                msg, e, new java.util.Date());
            throw iae;
        }*/
    }

    public Component getTableCellRendererComponent(javax.swing.JTable table, 
        Object value, boolean isSelected, boolean hasFocus, 
        int row, int column) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, 
            row, column);
        
        if (value instanceof Date) {
            setText(format.format((Date) value));
        } else if (value instanceof Long) {
            long v = ((Long) value).longValue();
            if (v == 0)
                setText(""); // NOI18N
            else
                setText(format.format(new Date(v)));
        } else {
            setText(""); // NOI18N
        }
        return this;
    }    
}
