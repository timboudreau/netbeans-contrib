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
 * SplitContainer.java
 *
 * Created on May 2, 2004, 4:48 PM
 */

package org.netbeans.swing.splitcontainer;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import javax.swing.JComponent;

/**
 * A multiple split - split container.
 *
 * @author  Tim Boudreau
 */
public class SplitContainer extends JComponent {
    private SplitLayoutModel layoutModel = null;
    private int gap = 8;

    /** Creates a new instance of SplitContainer */
    public SplitContainer() {
        updateUI();
    }
    
    public void updateUI() {
        SplitContainerUI ui = new SplitContainerUIImpl(this);
        setUI (ui);
    }
    
    void setLayoutModel (SplitLayoutModel mdl) {
        this.layoutModel = mdl;
    }
    
    public SplitLayoutModel getLayoutModel() {
        return layoutModel;
    }
    
    public boolean isValidateRoot() {
        return true;
    }
    
    /**
     * Spacing for splitters. <strong>This value must be an even number </strong>
     */
    public int getGap() {
        return gap;
    }
    
    protected void addImpl(Component comp, Object constraints, int idx) {
        if (!(constraints instanceof Constraint)) {
            throw new IllegalArgumentException ("Constraint must be an " +
                "instance of org.netbeans.swing.splitcontainer.Constraint");
        }
        if (layoutModel == null) {
            throw new NullPointerException ("Layout model is null");
        }
        Rectangle r = ((Constraint) constraints).getBounds(getSize());
        int gp = getGap() / 2;
        r.x += gp;
        r.y += gp;
        r.width -= getGap();
        r.height -= getGap();
        ((SplitLayoutModelImpl) layoutModel).putBounds (comp, r);
        super.addImpl (comp, constraints, idx);
    }
    
    public boolean isOptimizedDrawingEnabled() {
        return true;
    }
    
    public IntersticeFactory getIntersticeFactory() {
        
        AWTEvent eo = EventQueue.getCurrentEvent();
        if (eo instanceof MouseEvent) {
            MouseEvent me = (MouseEvent) eo;
            if (me.isAltDown()) {
                return new NearestNeighborIntersticeFactory();
            } else {
                return new LineOfSightIntersticeFactory();
            }
        }
         
        return new NearestNeighborIntersticeFactory(); //XXX
    }
    
    
}
