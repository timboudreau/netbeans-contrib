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

import javax.swing.UIManager;

/**
 * Install the proper UI.
 * @author Jesse Glick
 */
public class Install {

    /**
     * Register the new UI.
     */
    public static void main(String[] args) {
        UIManager.getDefaults().put("FileChooserUI", ChooserComponentUI.class.getName());
        // To make it work in NetBeans too:
        UIManager.getDefaults().put(ChooserComponentUI.class.getName(), ChooserComponentUI.class);
    }
    
}
