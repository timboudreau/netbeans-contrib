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
package org.netbeans.modules.pkgbrowser;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.windows.TopComponent;

/**
 * Action which shows the package browser component.
 */
public class PkgBrowserAction extends AbstractAction {

    public PkgBrowserAction() {
        super(NbBundle.getMessage(PkgBrowserAction.class, "CTL_PkgBrowserAction"));
        putValue(SMALL_ICON, new ImageIcon(Utilities.loadImage(PkgBrowserTopComponent.ICON_PATH, true)));
    }
    
    public void actionPerformed(ActionEvent evt) {
        TopComponent win = PkgBrowserTopComponent.findInstance();
        win.open();
        win.requestActive();
    }
    
}
