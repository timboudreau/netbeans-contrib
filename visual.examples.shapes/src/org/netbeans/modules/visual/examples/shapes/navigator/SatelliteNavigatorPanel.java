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
package org.netbeans.modules.visual.examples.shapes.navigator;

import javax.swing.JComponent;
import org.netbeans.modules.visual.examples.shapes.palette.ShapeTopComponent;
import org.netbeans.spi.navigator.NavigatorPanel;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

public class SatelliteNavigatorPanel implements NavigatorPanel {
    
    public SatelliteNavigatorPanel() {
    }
    
    public String getDisplayHint() {
        return NbBundle.getMessage(SatelliteNavigatorPanel.class, "HINT_SatelliteNavigationPanel");
    }
    
    public String getDisplayName() {
        return NbBundle.getMessage(SatelliteNavigatorPanel.class, "CTL_SatelliteNavigationPanel");
    }
    
    public JComponent getComponent() {
        return ShapeTopComponent.getDefault().getNavigatorView ();
    }
    
    public void panelActivated(Lookup context) {
    }
    
    public void panelDeactivated() {
    }
    
    public Lookup getLookup() {
        return null;
    }

}
