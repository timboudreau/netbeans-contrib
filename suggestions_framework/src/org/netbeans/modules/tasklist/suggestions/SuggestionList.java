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

import org.netbeans.modules.tasklist.core.TaskList;
import org.netbeans.modules.tasklist.core.TaskListView;
import org.netbeans.modules.tasklist.core.TaskNode;
import org.netbeans.modules.tasklist.core.Task;
import org.netbeans.modules.tasklist.client.SuggestionManager;

import org.openide.nodes.Node;
import org.openide.util.NbBundle;


/**
 * A list of suggestions adding category grouping capabilities to
 * orginary tasklist.
 *
 * @author Tor Norbye
 */
public class SuggestionList extends TaskList {

    /** Number of tasks we allow before for a type before dropping
     * the task into a sublevel with a category task containing the
     * tasks of that type.
     */
    private static final int MAX_INLINE = 20;

    static final Object CATEGORY_NODE_SEED = new Object();

    private final int groupTreshold;

    /** Construct a new SuggestionManager instance. */
    public SuggestionList() {
        this(MAX_INLINE);
    }

    public SuggestionList(int groupTreshold) {
        this.groupTreshold = groupTreshold;
    }

    synchronized SuggestionImpl getCategoryTask(SuggestionType type,
                                                boolean create) {
        SuggestionImpl category = null;
        if (categoryTasks != null) {
            category = (SuggestionImpl)categoryTasks.get(type);
        }
        if (create && (category == null)) {
            category = new SuggestionImpl(type.getLocalizedName(),type, null, CATEGORY_NODE_SEED);
            category.setType(type.getName());
            category.setIcon(type.getIconImage());
            category.setVisitable(false);
            // Don't duplicate the provider field! We don't want
            // SMI.stuffCache to keep category task nodes stashed...
            SuggestionManagerImpl manager =
                (SuggestionManagerImpl)SuggestionManager.getDefault();
            if (manager.isExpandedType(type)) {
                // TODO it's callers reponsibity
//                SuggestionsView view;
//                if (getView() instanceof SuggestionsView) {
//                    view = (SuggestionsView)getView();
//                } else {
//                    view = SuggestionsView.getCurrentView();
//                }
//                if (view != null) {
//                    manager.scheduleNodeExpansion(view,
//                                                  category);
//                }
            }

            if (categoryTasks == null) {
                categoryTasks = new HashMap(20);
            }
            categoryTasks.put(type, category);
            // Add the category in the given position
            SuggestionImpl after = findAfter(type);
            if (after != null) {
                addCategory(category, after);
            } else {
                addCategory(category, false);
            }
        }
        return category;
    }

    /** Add a task to the task list.
     * @param task The task to be added.
     * @param after The task which will be immediately before
     * the new subtask after the addition (e.g. add
     * this subtask directly AFTER the specified
     * task)
     * @deprecated use Task.addSubtask(Task subtask, Task after) instead
     */
    private void addCategory(Task task, Task after) {
        if (task.getParent() == null) {
            appendTask(task);
        } else {
            Task parent = task.getParent();
            // User insert: prepend to the list
            parent.removeSubtask(task);
            parent.addSubtask(task, after);
        }
    }


    /** Add a task to the task list.
     * @param task The task to be added.
     * @param append If true, append the item to the list, otherwise prepend
     * @deprecated use Task.addSubtask(Task subtask, boolean append) instead
     */
    private void addCategory(Task task, boolean append) {
        if (task.getParent() == null) {
            appendTask(task);
        } else {
            // it's really funny contruct  (XXX probably want to add it to list end)
            Task parent = task.getParent();
            parent.removeSubtask(task);
            parent.addSubtask(task, append);
        }
    }


    private Map categoryTasks = null;

    /** Return the task that we need to put this new category type
     * immadiately following. */
    SuggestionImpl findAfter(SuggestionType type) {
        SuggestionImpl after = null;
        int pos = type.getPosition();
        Iterator it = getTasks().iterator();
        int i = 0;
        while (it.hasNext()) {
            i++;
            SuggestionImpl s = (SuggestionImpl)it.next();
            int spos = s.getSType().getPosition();
            if (spos > pos) {
                break;
            } else {
                after = s;
            }
        }

        // #42618 hotfix
        if (i == getTasks().size()) {
            return null;
        } else {
            return after;
        }
    }

    /** Remove the given category node, if unused.
        @param force If true, remove the category node even if it has subtasks
    */
    synchronized void removeCategory(SuggestionImpl category, boolean force) {
        //SuggestionImpl category = (SuggestionImpl)s.getParent();
        if ((category != null) && (force || !category.hasSubtasks())) {
            category.getParent().removeSubtask(category);
            categoryTasks.remove(category.getSType());
        }
    }

    synchronized void removeCategory(SuggestionType type) {
        if (getTasks().size() == 0) {
            categoryTasks = null;
            return;
        }
        Iterator ti = getTasks().iterator();
        ArrayList removeTasks = new ArrayList(50);
        while (ti.hasNext()) {
            SuggestionImpl suggestion = (SuggestionImpl)ti.next();
            if (suggestion.getSType() == type) {
                removeTasks.add(suggestion);
            }
        }
        addRemove(null, removeTasks, false, null, null);
        if (categoryTasks != null) {
            categoryTasks.remove(type);
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


    /**
     * Gets number of items then displays inlined once
     * exceeded it's grouped (by category).
     * @return
     */
    final int getGroupTreshold() {
        return groupTreshold;  // TODO #37068 turn into user's option
    }

}
