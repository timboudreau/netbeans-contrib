/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.visual.examples.shapes.palette;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.netbeans.modules.visual.examples.shapes.GraphSceneImpl;
import org.openide.util.NbBundle;

/**
 * Action which shows Shape component.
 */
public class ShapeAction extends AbstractAction  {
    
    
    public ShapeAction() {
        
        super(NbBundle.getMessage(ShapeAction.class, "CTL_ShapeAction"));
        
    }
    
    public void actionPerformed(ActionEvent evt) {
        ShapeTopComponent tc = ShapeTopComponent.findInstance ();
        tc.open();
        tc.requestActive();
    }
    
}
