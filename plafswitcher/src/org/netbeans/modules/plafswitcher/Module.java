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
 *
 * Contributor(s): Petr Nejedly
 */

package org.netbeans.modules.plafswitcher;

import java.util.prefs.Preferences;
import javax.swing.UIManager;
import org.openide.ErrorManager;
import org.openide.modules.ModuleInstall;

/**
* Module installation class for PlafSwitcher.
*
* @author Petr Nejedly
*/
public class Module extends ModuleInstall {

    static String getPlaf() {
        return Preferences.userNodeForPackage(Module.class).get("plaf", null);
    }
    
    static void setPlaf(String plaf) {
        Preferences.userNodeForPackage(Module.class).put("plaf", plaf);
    }

    /** Module installed again. */
    public void restored() {
        String plaf = getPlaf();
        if (plaf != null) {
            try {
                UIManager.setLookAndFeel(plaf);
            } catch (Exception e) {
                // Only log, do not show to the user
                ErrorManager.getDefault().annotate(e, ErrorManager.UNKNOWN, "Cannot set PLAF to " + plaf, null, null, null);
                ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
            }
        }
    }
}
