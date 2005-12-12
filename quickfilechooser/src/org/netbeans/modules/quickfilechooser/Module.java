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

package org.netbeans.modules.quickfilechooser;

import org.openide.modules.ModuleInstall;

/**
 * Registers the quick file chooser in NetBeans.
 * @author Jesse Glick
 */
public class Module extends ModuleInstall {
    
    public void restored() {
        super.restored();
        Install.install();
    }

    public void uninstalled() {
        super.uninstalled();
        Install.uninstall();
    }
    
}
