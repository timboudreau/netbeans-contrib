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
import java.util.ListIterator;
import java.util.Map;

import org.netbeans.api.tasklist.*;
import org.netbeans.modules.tasklist.core.TaskList;
import org.netbeans.modules.tasklist.core.TaskListView;
import org.netbeans.modules.tasklist.core.TaskNode;

import org.openide.nodes.Node;
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
                SuggestionsView view;
                if (getView() instanceof SuggestionsView) {
                    view = (SuggestionsView)getView();
                } else {
                    view = SuggestionsView.getCurrentView();
                }
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

    /** For the category tasks, update the expansion state */
    void flushExpansion() {
        if (categoryTasks == null) {
            return;
        }
        TaskListView v = getView();
        if ((v == null) || !(v instanceof SuggestionsView)) {
            return;
        }
        SuggestionsView view = (SuggestionsView)v;
        SuggestionManagerImpl manager =
            (SuggestionManagerImpl)SuggestionManager.getDefault();
        Node root = view.getEffectiveRoot();
        Iterator it = categoryTasks.values().iterator();
        while (it.hasNext()) {
            SuggestionImpl s = (SuggestionImpl)it.next();
            Node n = TaskNode.find(root, s);
            if (n == null) {
                continue;
            }
            SuggestionType type = s.getSType();
            boolean expanded = view.isExpanded(n);
            if (expanded) {
                manager.setExpandedType(type, true);
            } else if (manager.isExpandedType(type)) {
                // Only set it to false if it's already recorded to be true
                manager.setExpandedType(type, false);
            }
        }
    }

    /** Locate the next suggestion from the given suggestion.
     * Used for example to jump to the previous or next error when
     * the user presses F12/S-F12.  This will skip over category
     * nodes etc.
     *
     * @param curr The current suggestion from which you want to find
     *   a neighbor
     * @param wrap If true, wrap around the end/front of the list
     *    and return the next/previous element. If false, return null
     *    when you reach the end or the front of the list, depending
     *    on your search direction.
     * @return the next element following curr that is
     *    not a category node */
     */
    public SuggestionImpl findNext(SuggestionImpl curr, 
                            boolean wrap) {
        currFound = false;
        List tasks = getTasks();
        SuggestionImpl s = findNext(tasks, curr, wrap);
        if ((s == null) && wrap && currFound) {
            // Start search one more time, this time not for
            // curr but just the first eligible element
            s = findNext(tasks, curr, wrap);
        }
        return s;
    }

    private boolean currFound;

    private SuggestionImpl findNext(List tasks, SuggestionImpl curr, 
                            boolean wrap) {
        Iterator it = tasks.iterator();
        while (it.hasNext()) {
            SuggestionImpl s = (SuggestionImpl)it.next();
            if (s == curr) {
                currFound = true;
            } else if (s.hasSubtasks()) {
                SuggestionImpl f = findNext(s.getSubtasks(), curr, wrap);
                if (f != null) {
                    return f;
                }
            } else if (currFound) {
                return s;
            }
        }
        return null;
    }


    /** Locate the previous suggestion from the given suggestion.
     * Used for example to jump to the previous or next error when
     * the user presses F12/S-F12.  This will skip over category
     * nodes etc.
     *
     * @param curr The current suggestion from which you want to find
     *   a neighbor
     * @param wrap If true, wrap around the end/front of the list
     *    and return the next/previous element. If false, return null
     *    when you reach the end or the front of the list, depending
     *    on your search direction.
     * @return the element preceding curr that is
     *    not a category node */
     */
    public SuggestionImpl findPrev(SuggestionImpl curr, 
                            boolean wrap) {
        currFound = false;
        List tasks = getTasks();
        SuggestionImpl s = findPrev(tasks, curr, wrap);
        if ((s == null) && wrap && currFound) {
            // Start search one more time, this time not for
            // curr but just the first eligible element
            s = findPrev(tasks, curr, wrap);
        }
        return s;
    }

    private SuggestionImpl findPrev(List tasks, SuggestionImpl curr, 
                            boolean wrap) {
        ListIterator it = tasks.listIterator(tasks.size());
        while (it.hasPrevious()) {
            SuggestionImpl s = (SuggestionImpl)it.previous();
            if (s == curr) {
                currFound = true;
            } else if (s.hasSubtasks()) {
                SuggestionImpl f = findPrev(s.getSubtasks(), curr, wrap);
                if (f != null) {
                    return f;
                }
            } else if (currFound) {
                return s;
            }
        }
        return null;
    }
}
