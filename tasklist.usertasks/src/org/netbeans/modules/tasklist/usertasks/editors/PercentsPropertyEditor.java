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
import java.beans.PropertyEditorSupport;
import javax.swing.JProgressBar;
import javax.swing.UIManager;
import org.openide.util.NbBundle;

/**
 * PropertyEditor for percents field.
 */
public class PercentsPropertyEditor extends PropertyEditorSupport {
    private static final String COMPUTED =
        NbBundle.getMessage(PercentsPropertyEditor.class, "Computed"); // NOI18N
    
    private static String[] TAGS = {
        COMPUTED,
        "0%", "5%", "10%", "15%", "20%", "25%", "30%", "35%", "40%", "45%", "50%", // NOI18N
        "55%", "60%", "65%", "70%", "75%", "80%", "85%", "90%", "95%", "100%" // NOI18N
    };
    
    /**
     * Used to combine 2 properties: percentComplete and progressComputed
     */
    public static final class Value {
        /** 0..100 progress of a task in percents */
        public int progress;
        
        /** true = value is computed*/
        public boolean computed;
    }
    
    private static JProgressBar progressBar;
    
    static {
        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        progressBar.setBackground(UIManager.getColor("Table.background")); // NOI18N
    }
    
    public String[] getTags() {
        return TAGS;
    }
    
    public String getAsText() {
        Value v = (Value) getValue();
        if (v.computed)
            return COMPUTED;
        else
            return Integer.toString(v.progress);
    }

    public void setAsText(String text) throws java.lang.IllegalArgumentException {
        Value v = new Value();
        if (text.equals(COMPUTED)) {
            v.computed = true;
        } else {
            text = text.trim();
            if (text.endsWith("%")) // NOI18N
                text = text.substring(0, text.length() - 1);
            try {
                v.progress = Integer.parseInt(text);
            } catch(NumberFormatException e) {
                throw new IllegalArgumentException(e.getMessage());
            }
        }
        setValue(v);
    }
    
    public boolean isPaintable() {
        return true;
    }

    public void paintValue(java.awt.Graphics gfx, java.awt.Rectangle box) {
        int n = ((Value) getValue()).progress;
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
}
