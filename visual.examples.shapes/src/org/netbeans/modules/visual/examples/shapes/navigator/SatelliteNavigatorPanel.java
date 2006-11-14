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
