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
