/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.clazz;

import java.util.ResourceBundle;

import org.openide.util.NbBundle;

/**
 *
 * @author  sdedic
 * @version 
 */
final class Util extends Object {
    private static ResourceBundle bundle;
    
    static ResourceBundle getBundle() {
        if (bundle != null)
            return bundle;
        synchronized (Util.class) {
            if (bundle == null)
                bundle = NbBundle.getBundle(ClassModule.class);
        }
        return bundle;
    }
    
    static String getString(String key) {
        return getBundle().getString(key);
    }
}
