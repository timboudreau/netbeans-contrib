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
                                   "SuggestionsRoot"), null, null, 
              null)); // NOI18N
    }
    

    synchronized SuggestionImpl getCategoryTask(SuggestionType type,
                                                boolean create) {
        SuggestionImpl category = null;
        if (categoryTasks != null) {
            category = (SuggestionImpl)categoryTasks.get(type);
        }
        if (create && (category == null)) {
            category = new SuggestionImpl();

            category.setSummary(type.getLocalizedName());
            category.setAction(null);
            category.setType(type.getName());
            category.setSType(type);
            category.setIcon(type.getIconImage());
            // Don't duplicate the provider field! We don't want
            // SMI.stuffCache to keep category task nodes stashed...
            SuggestionManagerImpl manager =
                (SuggestionManagerImpl)SuggestionManager.getDefault();
            if (manager.isExpandedType(type)) {
                SuggestionsView view = SuggestionsView.getCurrentView();
                if (view != null) {
                    manager.scheduleNodeExpansion(view,
                                                  category);
                }
            }
            
            if (categoryTasks == null) {
                categoryTasks = new HashMap(20);
            }
            categoryTasks.put(type, category);
            // Add the category in the given position
            SuggestionImpl after = findAfter(type);
            if (after != null) { 
                add(category, after, false);
            } else {
                add(category, false, false);
            }
        }
        return category;
    }
    private Map categoryTasks = null;

    /** Return the task that we need to put this new category type
     * immadiately following. */
    SuggestionImpl findAfter(SuggestionType type) {
        SuggestionImpl after = null;
        List tasks = getTasks();
        if (tasks != null) {
            int pos = type.getPosition();
            Iterator it = tasks.iterator();
            while (it.hasNext()) {
                SuggestionImpl s = (SuggestionImpl)it.next();
                if (s.getSType().getPosition() >= pos) {
                    break;
                } else {
                    after = s;
                }
            }


        }
        return after;
    }
    
    /** Remove the given category node, if unused.
        @param force If true, remove the category node even if it has subtasks
    */
    synchronized void removeCategory(SuggestionImpl category, boolean force) {
        //SuggestionImpl category = (SuggestionImpl)s.getParent();
        if ((category != null) && (force || !category.hasSubtasks())) {
            remove(category);
            categoryTasks.remove(category.getSType());
        }
    }
    
    synchronized void removeCategory(SuggestionType type) {
        List tasks = getTasks();
        if (tasks == null) {
            categoryTasks = null;
            return;
        }
        Iterator ti = tasks.iterator();
        ArrayList removeTasks = new ArrayList(50);
        while (ti.hasNext()) {
            SuggestionImpl suggestion = (SuggestionImpl)ti.next();
            if (suggestion.getSType() == type) {
                removeTasks.add(suggestion);
            }
        }
        addRemove(null, removeTasks, false, null, null);
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
