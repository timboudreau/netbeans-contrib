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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.netbeans.modules.tasklist.core.TaskList;
import org.netbeans.modules.tasklist.core.TaskListView;



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

            /*
              
            // HACK: special-handle the java parse error nodes: we want
            // them expanded by default (meaning when the node is added
            // for the first time)
            if (type.getName().equals("nb-java-errors")) { // NOI18N
                // from org.netbeans.modules.tasklist.javaparser.ErrorSuggester
                SuggestionsView v = (SuggestionsView)TaskListView.getCurrent();
                if (v != null) {
                    // XXX I don't have a handle on the node here.
                    // I have to defer this until the node is actually created.
                    // Perhaps I can record the collapse-request with the
                    // task object in some way such that in the TaskNode
                    // constructor (or property change listener) I process
                    // the request when the node is created?  The alternative
                    // is adding a timed delay action which looks up the node
                    // and expands it - but that's more problematic (on
                    // slower computers, etc.)  Timer solutions are usually
                    // hacks.
                    
                         //  v.setExpanded(node, true);
                }
            }

            */
        }
        return category;
    }
    private Map categoryTasks = null;
    
    synchronized void removeCategory(SuggestionImpl s) {
        SuggestionImpl category = (SuggestionImpl)s.getParent();
        if ((category != null) && !category.hasSubtasks()) {
            remove(category);
            categoryTasks.remove(category.getSType());
        }
    }
    
    synchronized void removeCategory(SuggestionType type) {
        List tasks = getTasks();
        Iterator ti = tasks.iterator();
        ArrayList removeTasks = new ArrayList(50);
        while (ti.hasNext()) {
            SuggestionImpl suggestion = (SuggestionImpl)ti.next();
            if (suggestion.getSType() == type) {
                removeTasks.add(suggestion);
            }
        }
        addRemove(null, removeTasks, false, null);
        categoryTasks.remove(type);
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
