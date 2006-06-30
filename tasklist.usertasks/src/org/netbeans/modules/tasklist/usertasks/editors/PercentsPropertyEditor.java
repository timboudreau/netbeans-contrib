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

import java.beans.PropertyEditorSupport;

import javax.swing.JProgressBar;
import javax.swing.UIManager;

import org.openide.ErrorManager;
import org.openide.util.NbBundle;

/**
 * PropertyEditor for percents field.
 */
public class PercentsPropertyEditor extends PropertyEditorSupport {
    private static String[] TAGS = {
        "0", "5", "10", "15", "20", "25", "30", "35", "40", "45", "50", // NOI18N
        "55", "60", "65", "70", "75", "80", "85", "90", "95", "100" // NOI18N
    };

    private static JProgressBar progressBar;
    
    static {
        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        progressBar.setBackground(UIManager.getColor("Table.background")); // NOI18N
    }
    
    public boolean isPaintable() {
        return true;
    }

    public void paintValue(java.awt.Graphics gfx, java.awt.Rectangle box) {
        int n = ((Integer) getValue()).intValue();
        progressBar.setValue(n);
        progressBar.setString(n + "%"); // NOI18N
        int height = box.height > 15 ? 15 : box.height;
        int width = box.width > 100 ? 100 : box.width;
        int y = (box.height - height) / 2;
        progressBar.setSize(width, height);
        
        gfx.translate(box.x, box.y + y);
        progressBar.paint(gfx);
        gfx.translate(-box.x, -box.y - y);
    }
    
    public void setAsText(String text) throws java.lang.IllegalArgumentException {
        try {
            setValue(new Integer(text));
        } catch (NumberFormatException e) {
            IllegalArgumentException iae = 
                new java.lang.IllegalArgumentException(
                    NbBundle.getMessage(PercentsPropertyEditor.class, 
                    "NotANumber")); // NOI18N
            ErrorManager.getDefault().annotate(iae, ErrorManager.USER, 
                iae.getMessage(), 
                iae.getMessage(), e, new java.util.Date());
            throw iae;
        }
    }
    
    public String[] getTags() {
        return TAGS;
    }    
}
