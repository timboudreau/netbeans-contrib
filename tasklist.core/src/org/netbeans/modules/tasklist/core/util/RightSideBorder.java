/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.tasklist.core.util;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import javax.swing.border.AbstractBorder;

/**
 * Raised BevelBorder only with the right side.
 */
public class RightSideBorder extends AbstractBorder {
    /**
     * Creates a new instance of RightSideBorder
     */
    public RightSideBorder() {
    }

    public Insets getBorderInsets(Component c, Insets insets) {
        insets.left = insets.top = insets.bottom = 0;
        insets.right = 2;
        return insets;
    }

    public void paintBorder(Component c, Graphics g, int x, int y, 
        int width, int height) {
        
        Color oldColor = g.getColor();
        int h = height;
        int w = width;

        g.translate(x, y);

        g.setColor(c.getBackground().darker());
        g.drawLine(w-2, 1, w-2, h-2);

        g.setColor(c.getBackground().darker().darker());
        g.drawLine(w-1, 0, w-1, h-1);

        g.translate(-x, -y);
        g.setColor(oldColor);
    }

    public Insets getBorderInsets(Component c) {
        return new Insets(0, 0, 0, 2);
    }

    public boolean isBorderOpaque() {
        return true;
    }
}
