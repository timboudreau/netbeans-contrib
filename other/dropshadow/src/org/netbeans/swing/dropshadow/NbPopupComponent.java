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
