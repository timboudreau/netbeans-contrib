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

package org.netbeans.modules.mount;

import org.netbeans.spi.project.ActionProvider;
import org.openide.util.Lookup;

/**
 * Manages actions for the mount dummy project.
 * @author Jesse Glick
 */
final class Actions implements ActionProvider {
    
    public Actions() {}

    public String[] getSupportedActions() {
        return new String[] {
            // XXX
        };
    }

    public boolean isActionEnabled(String command, Lookup context) throws IllegalArgumentException {
        throw new IllegalArgumentException();
    }
    
    public void invokeAction(String command, Lookup context) throws IllegalArgumentException {
        throw new IllegalArgumentException();
    }

}
