/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004S Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.tasklist.docscan;

import org.openide.util.NbBundle;

/**
 * Utility methods
 *
 * @author Petr Kuzel
 */
final class Util {

    public static String getString(String key) {
        return NbBundle.getBundle(Util.class).getString(key);
    }

    public static char getChar(String key) {
        return NbBundle.getBundle(Util.class).getString(key).charAt(0);
    }

    public static String getMessage(String key, Object obj) {
        return NbBundle.getMessage(Util.class,key, obj);
    }

    public static String getMessage(String key, Object obj, Object obj2) {
        return NbBundle.getMessage(Util.class,key, obj, obj2);
    }

}
