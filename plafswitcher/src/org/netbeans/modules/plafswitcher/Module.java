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
