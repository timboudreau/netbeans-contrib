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
 *
 * Contributor(s): Petr Nejedly
 */

package org.netbeans.modules.plafswitcher;

import javax.swing.UIManager;
import org.openide.ErrorManager;
import org.openide.filesystems.Repository;

/**
* Module installation class for PlafSwitcher.
*
* @author Petr Nejedly
*/
public class Module extends org.openide.modules.ModuleInstall {
    static final long serialVersionUID =-5L;

    // Why such impl? Simply because:
    // *) I want it to work under NB3.5
    // *) I don't want to introduce (pre)deprecated SystemOption
    //    because of such simple thing
    // Later I may use Registry
    static String getPlaf() {
        Object o = Repository.getDefault().getDefaultFileSystem().
            findResource("Actions/View/org-netbeans-modules-plafswitcher-ChoosePlaf.instance").
            getAttribute("org-netbeans-modules-plaf-Selected");
        return (o instanceof String) ? (String)o : null;
    }
    
    static void setPlaf(String plaf) throws java.io.IOException {
        Repository.getDefault().getDefaultFileSystem().
            findResource("Actions/View/org-netbeans-modules-plafswitcher-ChoosePlaf.instance").
            setAttribute("org-netbeans-modules-plaf-Selected", plaf);
    }

    /** Module installed again. */
    public void restored() {
        String plaf = getPlaf();
        if (plaf != null) {
            try {
                UIManager.setLookAndFeel(plaf);
            } catch (Exception e) {
                // Only log, don´t show to the user
                ErrorManager.getDefault().log(ErrorManager.EXCEPTION,
                    "Can´t set PLAF to " + plaf + ": " + e);
            }
        }
    }
}
