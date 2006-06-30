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

package org.netbeans.modules.tasklist.suggestions;

import java.util.Map;
import java.util.Iterator;
import java.util.Collection;

/**
 * Registry of all suggestion types. This is singleton and it also keeps
 * the settings whether the suggestion types which are not active are
 * drawn on the background, whther the combining of suggestions is
 * turned on or off etc. These settings are shared by all views.
 *
 * Based on AnnotationTypes.java in the editor package.
 * <p>
 *
 * @author Tor Norbye, David Konecny
 */

final public class SuggestionTypes {
    /** Static map containing all suggestion types:
        suggestion_name <-> suggestion_type */
    private Map allTypes = null;
    
    /** Flag whether the suggestion types were initialized or not */
    private boolean initializedTypes = false;

    /** Single instance of this class */
    private static SuggestionTypes suggestionTypes = null;
    
    private SuggestionTypes() {
    }

    /**  Returns instance of SuggestionTypes singleton. */
    public static SuggestionTypes getDefault() {
        if (suggestionTypes == null) {
            suggestionTypes = new SuggestionTypes();
        }
        return suggestionTypes;
    }
    
    /** Initialize the map of all suggestion types
     * @param map map containing all suggestion types */    
    final void setTypes(Map map) {
        allTypes = map;
        initializedTypes = map != null;
    }

    public final int getCount() {
        loadTypes();
        if (allTypes == null) {
            return 0;
        } else {
            return allTypes.size();
        }
    }
    
    /** Returns SuggestionType instance for the given name of the type
     * @param name suggestion type name
     * @return instance describing suggestion type */    
    public final SuggestionType getType(String name) {
        loadTypes();
        
        if (allTypes == null)
            return null;
        
        return (SuggestionType)allTypes.get(name);
    }

    /** Returns a collection containing all the registered SuggestionTypes.
     * @return collection containing all registered types */    
    public final Collection getAllTypes() {
        loadTypes();
        
        if (allTypes == null)
            return null;
        
        return allTypes.values();
    }

    /** Check if the types were loaded and load them if not */
    private void loadTypes() {
        if (initializedTypes)
            return;

        SuggestionTypesFolder.getSuggestionTypesFolder();

        initializedTypes = true;        
    }
}
