/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.swing.menus;

import java.lang.reflect.*;
import org.openide.modules.ModuleInstall;

/**
 * Installer that sets up the system property to supply its own menu bar.
 *
 * @author  Tim Boudreau
 */
public class Install extends ModuleInstall {
    public void restored () {
        
        System.setProperty ("netbeans.winsys.menu_bar.path", //NOI18N
            "menuprovider/org-netbeans-swing-menus-api-TreeMenuBar.instance"); //NOI18N
        
        //XXX for now - module should install it
//        System.setProperty ("org.netbeans.swing.menus.MenuTreeModel", //NOI18N
//            "org.netbeans.modules.legacymenus.SfsMenuModel"); //NOI18N
        
        System.err.println("Set up system properties");
    }
    
    public void uninstalled () {
        
    }
}
