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
 */

package org.netbeans.modules.importcruncher;

import java.util.prefs.Preferences;
import org.openide.util.NbPreferences;

/**
 * Manages preferences for the module.
 * @author Timothy Boudreau, Jesse Glick
 */
class Prefs {

    private Prefs() {}

    private static final Preferences p = NbPreferences.forModule(Prefs.class);

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
