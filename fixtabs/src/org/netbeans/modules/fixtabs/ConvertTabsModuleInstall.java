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
