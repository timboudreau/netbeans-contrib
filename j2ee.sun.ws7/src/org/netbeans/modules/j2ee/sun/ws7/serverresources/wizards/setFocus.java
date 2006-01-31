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
/*
 * setFocus.java
 * 
 */

package org.netbeans.modules.j2ee.sun.ws7.serverresources.wizards;

import java.awt.Component;
import javax.swing.SwingUtilities;

/**
 *
 ** Code reused from Appserver common API module 
 * Class to move fous between wizard panels
 * new setFocus(firstPanel.getComponent(1)); 
 */

public class setFocus implements Runnable {
    private Component comp;
    public setFocus(Component comp) {
        this.comp = comp;
        try {
            SwingUtilities.invokeLater(this);
        } catch(java.lang.Exception e) {
            e.printStackTrace();
        }
    }
    public void run() {
        comp.requestFocus();
    }
}
