/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2001 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.rmi.activation;

import org.openide.ErrorManager;

/**
 *
 * @author mr97946
 */
public class ActivationModule {
    
    private static ErrorManager errMgr;
    public static ErrorManager getErrorManager(Class clazz) {
        if (errMgr == null) {
            errMgr = ErrorManager.getDefault().getInstance("org.netbeans.modules.rmi.activation");
        }
        return errMgr;
    }
}
