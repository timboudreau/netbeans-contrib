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

package org.netbeans.modules.tasklist.suggestions;

/**
 * Marks TopComponents that visualizes suggestions.
 * These topcomponents must implement this insterface.
 *
 * @author Petr Kuzel
 */
public interface SuggestionView {

    /**
     * Tests whether given view shows given tasks
     * @param category
     * @return
     */
    boolean isObserved(String category);


    /**
     * Provide access to live data model.
     * @return task list that the view manages
     *         or null if it's not interested
     *         in list updates (i.e. snapshot
     *         or hidden view).
     */
    SuggestionList getSuggestionsModel();

}
