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

package org.netbeans.modules.tasklist.suggestions;

import java.util.Map;

/** Registry of all suggestion types. This is singleton and it also keeps
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
    public static SuggestionTypes getTypes() {
        if (suggestionTypes == null) {
            suggestionTypes = new SuggestionTypes();
        }
        return suggestionTypes;
    }
    
    /** Initialize the map of all suggestion types
     * @param map map containing all suggestion types */    
    public final void setTypes(Map map) {
        allTypes = map;
    }

    public final void removeType(String name) {
        allTypes.remove(name);
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

    /** Iterator of all suggestion type names in the system */
    /* XXX Not yet needed
    public Iterator getSuggestionTypeNames() {
        loadTypes();

        // if following code throws NPE the problem is that
        // in Editors/SuggestionTypes folder there is a XML file
        // which could not be succesfully parsed. This resulted
        // in that allTypes array is null
        Set temp = new HashSet();
        temp.addAll(allTypes.keySet());
        return temp.iterator();
    }
    */
    
    /** Gets count of all suggestion type names */
    /* XXX Not yet needed
    public int getSuggestionTypeNamesCount() {
        loadTypes();
        
        return allTypes.keySet().size();
    }
    */
    
    /** Check if the types were loaded and load them if not */
    private void loadTypes() {
        if (initializedTypes)
            return;

        SuggestionTypesFolder.getSuggestionTypesFolder();

        initializedTypes = true;        
    }
}
