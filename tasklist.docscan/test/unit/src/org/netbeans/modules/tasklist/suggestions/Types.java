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
 */

package org.netbeans.modules.tasklist.suggestions;

import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

/**
 * Fake types for unit test purposes. In unit
 * testing mode is not accesible SFS and all
 * registrations on it must be emulated.
 *
 * @author Petr Kuzel
 */
public final class Types {

    public static void installSuggestionTypes() {
        Map types = new HashMap();
        types.put("nb-tasklist-scannedtask", new SuggestionType("", "", "", "", null, Collections.EMPTY_LIST));
        SuggestionTypes.getDefault().setTypes(types);
    }
}
