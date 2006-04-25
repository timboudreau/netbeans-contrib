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

package org.netbeans.modules.fixtabs;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.JTextComponent;
import org.netbeans.editor.Registry;
import org.openide.modules.ModuleInstall;

/**
 *
 * @author Andrei Badea
 */
public class ConvertTabsModuleInstall extends ModuleInstall {

    // prevent the listener from begin GCd (Registry holds the listeners weakly)
    private static ChangeListener listener;

    public void restored() {
        assert listener == null;
        listener = new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                HighlightTabs.getDefault().install(Registry.getMostActiveComponent());
            }
        };
        Registry.addChangeListener(listener);

        // we need to install the highlighting in the active component when the module
        // is installed/enabled through Module Manager/Update Center
        JTextComponent component = Registry.getMostActiveComponent();
        if (component != null) {
            HighlightTabs.getDefault().install(component);
        }
    }

    public void uninstalled() {
        Registry.removeChangeListener(listener);
        listener = null;
        HighlightTabs.getDefault().uninstall();
    }
}
