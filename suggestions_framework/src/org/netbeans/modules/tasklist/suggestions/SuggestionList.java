/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2002 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.tasklist.suggestions;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.netbeans.modules.tasklist.core.TaskList;



import org.netbeans.api.tasklist.*;
import org.openide.util.NbBundle;

/**
 * A list of suggestions
 *
 * @author Tor Norbye
 */


final public class SuggestionList extends TaskList {

    /** Construct a new SuggestionManager instance. */
    public SuggestionList() {
        super(new SuggestionImpl(
              NbBundle.getMessage(SuggestionList.class, 
                                   "SuggestionsRoot"), null, 0)); // NOI18N
    }
    

    synchronized SuggestionImpl getCategoryTask(SuggestionType type) {
        SuggestionImpl category = null;
        if (categoryTasks != null) {
            category = (SuggestionImpl)categoryTasks.get(type);
        }
        if (category == null) {
            category = new SuggestionImpl();

            category.setSummary(type.getLocalizedName());
            category.setAction(null);
            category.setType(type.getName());
            category.setSType(type);
            category.setIcon(type.getIconImage());
            if (categoryTasks == null) {
                categoryTasks = new HashMap(20);
            }
            categoryTasks.put(type, category);
            add(category, false, false);
        }
        return category;
    }
    private Map categoryTasks = null;
    
    private synchronized void removeCategory(SuggestionImpl s) {
        SuggestionImpl category = (SuggestionImpl)s.getParent();
        if ((category != null) && !category.hasSubtasks()) {
            remove(category);
            categoryTasks.remove(category.getSType());
        }
    }

    /** Return the set of category tasks (SuggestionImpl objects) */
    Collection getCategoryTasks() {
        if (categoryTasks != null) {
            return categoryTasks.values();
        }
        return null;
    }

    void clearCategoryTasks() {
        categoryTasks = null; // recreate such that they get reinserted etc.
    }
    

    
}
