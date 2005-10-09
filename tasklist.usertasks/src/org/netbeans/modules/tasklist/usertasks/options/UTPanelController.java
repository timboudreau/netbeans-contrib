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

package org.netbeans.modules.tasklist.usertasks.options;

import org.netbeans.spi.options.OptionsCategory;
import org.openide.util.HelpCtx;

/**
 * PanelController for the options.
 *
 * @author tl
 */
public class UTPanelController extends OptionsCategory.PanelController {
    private UTOptionsPanel panel = new UTOptionsPanel();
    
    /**
     * Creates a new instance of UTPanelController.
     */
    public UTPanelController() {
    }

    public javax.swing.JComponent getComponent(org.openide.util.Lookup masterLookup) {
        return panel;
    }

    public void removePropertyChangeListener(java.beans.PropertyChangeListener l) {
        panel.removePropertyChangeListener(l);
    }

    public void addPropertyChangeListener(java.beans.PropertyChangeListener l) {
        panel.addPropertyChangeListener(l);
    }

    public void update() {
        panel.update();
    }

    public boolean isValid() {
        return true;
    }

    public boolean isChanged() {
        return panel.isChanged();
    }

    public org.openide.util.HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    public void cancel() {
        panel.cancel();
    }

    public void applyChanges() {
        panel.applyChanges();
    }
}
