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
/*
 * NbPopupComponent.java
 *
 * Created on December 22, 2003, 9:43 PM
 */

package org.netbeans.swing.dropshadow;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.JComponent;
import javax.swing.JPanel;

/** Lightweight popup container to go with NbPopupFactory, which will display
 * a lightweight popup with a drop-shadow.
 *
 * @author  Tim Boudreau   */
class NbPopupComponent extends JPanel {
    private Component child;
    
    public NbPopupComponent() {
        setOpaque(false);
        setLayout(new BorderLayout());
        setBorder (new DropShadowBorder());
    }

    public boolean isOptimizedDrawingEnabled() {
        return false;
    }

    void setChild (Component c) {
        if (this.child != null) {
            remove(child);
        }
        if (c != null) {
            add (c, BorderLayout.CENTER);
        }
        child = c;
    }

    public void layout() {
        if (child != null) {
            child.setBounds(0,0,getWidth() - getOffset(),
                getHeight() - getOffset());

//            child.setBounds(0,0,getWidth(),getHeight());
        }
    }
    
    private int getOffset() {
        return ((DropShadowBorder) getBorder()).getOffset();
    }

    public Dimension getPreferredSize() {
        Dimension result = super.getPreferredSize();
        return result;
    }
}
