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

package org.netbeans.modules.importcruncher;

import java.util.prefs.Preferences;

/**
 * Manages preferences for the module.
 * @author Timothy Boudreau, Jesse Glick
 */
class Prefs {
    
    private Prefs() {}
    
    private static final Preferences p = Preferences.userNodeForPackage(Prefs.class);
    
    /** only meaningful if SORT is set */
    public static final String BREAKUP = "breakup"; //NOI18N
    public static final String NO_FQNS = "eliminateFqns"; //NOI18N
    public static final String SORT = "sort"; //NOI18N
    public static final String IMPORT_NESTED_CLASSES = "importNestedClasses";
    public static final String NO_WILDCARDS = "eliminateWildcards"; //NOI18N
    
    public static boolean get(String key) {
        return p.getBoolean(key, !key.equals(IMPORT_NESTED_CLASSES));
    }
    
    public static void set(String key, boolean val) {
        p.putBoolean(key, val);
    }
    
}
