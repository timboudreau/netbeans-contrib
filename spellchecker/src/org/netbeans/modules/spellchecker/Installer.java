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
package org.netbeans.modules.spellchecker;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.JTextComponent;
import org.netbeans.editor.Registry;
import org.openide.modules.ModuleInstall;

/**
 * Manages a module's lifecycle. Remember that an installer is optional and
 * often not needed at all.
 */
public class Installer extends ModuleInstall {

    private static ChangeListener l = new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
            JTextComponent c = Registry.getMostActiveComponent();

            if (c != null) {
                ComponentPeer.assureInstalled(c);
            }
        }
    };

    public void restored() {
        Registry.addChangeListener(l);
        // By default, do nothing.
        // Put your startup code here.
    }
    
}
