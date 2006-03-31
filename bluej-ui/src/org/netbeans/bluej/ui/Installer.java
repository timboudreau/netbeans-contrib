/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.bluej.ui;

import org.netbeans.bluej.ui.window.OpenedBluejProjects;
import org.openide.modules.ModuleInstall;

/**
 * Manages a module's lifecycle. Remember that an installer is optional and
 * often not needed at all.
 */
public class Installer extends ModuleInstall {
    public void restored() {
        // By default, do nothing.
        // Put your startup code here.
        System.setProperty("no.set.rowheight", Boolean.TRUE.toString());
        OpenedBluejProjects.getInstance().addNotify();
    }

    public void uninstalled() {
        super.uninstalled();
        OpenedBluejProjects.getInstance().removeNotify();
    }
    
    
}
