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

package org.netbeans.modules.tasklist.core;


import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.netbeans.modules.tasklist.core.translators.XMLTranslator;
import org.netbeans.modules.tasklist.core.translators.HTMLSupport;
import org.netbeans.modules.tasklist.core.translators.FormatTranslator;
import org.openide.ErrorManager;
import org.openide.util.NbBundle;

/**
 * This class models hierarchical tasklist.
 *
 * @author Tor Norbye
 * @author Tim Lebedkov
 */
public class TaskList implements ObservableList { // XXX remove the publicness.

    // List category
    public final static String USER_CATEGORY = "usertasks"; // NOI18N

    /**
     * Creates a new instance of TaskList.
     * {@link #getRoot} must be overriden properly.
     */
    public TaskList() {
    }

    /** Creates a new instance of TaskList with a specified root */
    public TaskList(Task root) { // Must this be public?
        this.root = root;
        root.setList(this);
    }

    /** Has the options set changed such that we need to save */
    protected boolean needSave = false;
    protected boolean dontSave = false;

    protected void setNeedSave(boolean b) {
        needSave = b;
    }

    protected void setDontSave(boolean b) {
        dontSave = b;
    }

    protected List list = null;
    protected Task root = null;

    public Task getRoot() {
        if (root == null) {
            // Just use the name "Description" since for some reason,
            // when we have no items the TreeView puts the root node
            // description as the header for the leftmost column...
            root = new Task();
            root.setSummary(NbBundle.getMessage(TaskList.class,
                    "Description")); // NOI18N
            root.setList(this);
        }
        return root;
    }

    /** Add a list of tasks to the tasklist, and remove a list of
     *	tasks from the tasklist. This is done instead of a separate
     *  add and remove method such that you can change the tasklist
     *  atomically without having an intermediate screen refresh.
     * 	Note that if a task appears in both lists, it will be ADDED.
     *	(Because the removal will occur first.)
     *
     * @param addList The list of tasks to be added. Can be null.
     * @param removeList The list of tasks to be removed. Can be null.
     * @param append If true, append to the list, otherwise prepend. Ignored
     *   if the after parameter is not null.
     * @param parent Normally null, but you can specify a parent task
     *               here if you want to add subitems
     * @param after The task which will be immediately before
     * the new subtask after the addition (e.g. add
     * this subtask directly AFTER the specified
     * task). Overrides the append parameter.
     */
    public void addRemove(List addList, List removeList, boolean append,
                          Task parent, Task after) {
        // Disable updates for the duration of the list update
        setSilentUpdate(true, true, false);

        boolean modified = false;

        // Remove items
        // TODO add Task.removeSubtasks(List) ? See addSubtasks below
        ListIterator it;
        if (removeList != null) {
            it = removeList.listIterator();
            while (it.hasNext()) {
                Task task = (Task) it.next();
                modified = true;
                remove(task);
            }
        }

        if (parent == null) {
            if (root == null) {
                root = getRoot();
            }
            parent = root;
        }

        if (addList != null) {
            modified = true;

            // User insert: prepend to the list
            parent.addSubtasks(addList, append, after);
        }

        // Update the task list now
        // Only save if non-temporary items were added

        // XXX - now that I have added a parent reference, should
        // the property notification happen relative to it? Probably yes.
        // Need parent reference in setSilentUpdate
        setSilentUpdate(false, true, modified);
    }

    /** Add a task to the task list.
     * @param task The task to be added. */
    public void add(Task task) {
        add(task, false, true);
    }

    /** Add a task to the task list.
     * @param task The task to be added.
     * @param append If true, append the item to the list, otherwise prepend
     * @param show If true, show the task in the list
     */
    public void add(Task task, boolean append, boolean show) {
        if (root == null) {
            root = getRoot();
        }
        if (task.getParent() == null) {
            task.setParent(root);
        }
        Task parent = task.getParent();
        // User insert: prepend to the list
        parent.addSubtask(task, append);

        needSave = true;

        // Show the new item
        // XXX fix this
        if (show) {
            notifySelected(task);
        }

        notifyAdded(task);

        // TODO make this smarter later on, such that I only save when necessary
        save();
    }

    /** Add a task to the task list.
     * @param task The task to be added.
     * @param after The task which will be immediately before
     * the new subtask after the addition (e.g. add
     * this subtask directly AFTER the specified
     * task)
     * @param show If true, show the task in the list
     */
    public void add(Task task, Task after, boolean show) {
        if (root == null) {
            root = getRoot();
        }
        if (task.getParent() == null) {
            task.setParent(root);
        }
        Task parent = task.getParent();
        // User insert: prepend to the list
        parent.addSubtask(task, after);

        needSave = true;

        // Show the new item
        // XXX fix this
        if (show) {
            notifySelected(task);
        }

        // TODO make this smarter later on, such that I only save when necessary
        save();
    }


    /** Remove a task from the list.
     * @param task The task to be removed. */
    public void remove(Task task) {
        if (task.getParent() != null) {
            task.getParent().removeSubtask(task);
        } else {
            root.removeSubtask(task);
        }
        needSave = true;

        // Ensure that we're not showing any markers for this item
        notifyRemoved(task);

        // TODO make this smarter later on, such that I only save when necessary
        save();
    }

    /** Notify the task list that some aspect of it has been changed, so
     * it should save itself soon. Eventually calls save */
    public void markChanged() {
        // For now, save right away....
        // TODO - make a timer here, for example 10 seconds, such that
        // everytime we get a markChanged() message, we reset the timer.
        // When the timer expires, we save.
        // Then go change the code such that nobody calls save() directly,
        // only markChanged() - except for the window, perhaps, which may call
        // save on program shutdown. We don't want edits within 10 seconds
        // of shutting down the IDE to be lost...
        needSave = true;
        save();
    }

    /**
     * Mark this list as changed.
     *
     * @param task a task that was changed.
     */
    void markChanged(Task task) {
        markChanged();
        notifyChanged(task);
    }

    /** Write tasks out to disk */
    public void save() {
        needSave = false;
    }

    /** Setter for property silentUpdate.
     * When true, don't notify anybody or save the task list
     when contents changes. This is used for batch operations
     (such as inserting a series of tasks when scanning a source file.)
     * @param silentUpdate New value of property silentUpdate.
     * @param rootUpdates When true, also suppress root modification properties
     * @param saveOnFinish If true, save the task when we stop being silent
     */
    void setSilentUpdate(boolean silentUpdate, boolean rootUpdates,
                         boolean saveOnFinish) {// XXX remove the publicness
        dontSave = silentUpdate;
        needSave = true;
        if (rootUpdates) {
            if (root == null) {
                root = getRoot();
            }
            if (silentUpdate) {
                // XXX this is going to generate lots of updates.
                // I should set silentUpdate on the root during the
                // deletions...
                root.setSilentUpdate(true, false, false, false);
            } else {
                // XXX It would be better NOT to do this, so I don't get
                // a refresh after the items have been deleted!
                root.setSilentUpdate(false, false, true, saveOnFinish);
            }
        }
        if (!dontSave && saveOnFinish) {
            // May do nothing if setSilentUpdate above did a TaskList.markChanged()
            save();
        }
    }

    protected ArrayList listeners = null;

    public void addListener(TaskListener listener) {
        if (listeners == null) {
            listeners = new ArrayList(4);
        }
        listeners.add(listener);
    }

    public void removeListener(TaskListener listener) {
        if (listeners == null) {
            return;
        }
        listeners.remove(listener);
    }

    // XXX it should be on task itself,
    // it has nothing to do with the list membership
    public void notifyChanged(Task task) {
        if (listeners != null) {
            int n = listeners.size();
            for (int i = 0; i < n; i++) {
                TaskListener tl = (TaskListener) listeners.get(i);
                tl.changedTask(task);
            }
        }
    }

    protected void notifyAdded(Task task) {
        if (listeners != null) {
            int n = listeners.size();
            for (int i = 0; i < n; i++) {
                TaskListener tl = (TaskListener) listeners.get(i);
                tl.addedTask(task);
            }
        }
    }

    public void notifySelected(Task task) {
        if (listeners != null) {
            int n = listeners.size();
            for (int i = 0; i < n; i++) {
                TaskListener tl = (TaskListener) listeners.get(i);
                tl.selectedTask(task);
            }
        }
    }

    public void notifyWarped(Task task) {
        if (listeners != null) {
            int n = listeners.size();
            for (int i = 0; i < n; i++) {
                TaskListener tl = (TaskListener) listeners.get(i);
                tl.warpedTask(task);
            }
        }
    }

    public void notifyStructureChanged(Task task) {
        if (listeners != null) {
            int n = listeners.size();
            for (int i = 0; i < n; i++) {
                TaskListener tl = (TaskListener) listeners.get(i);
                tl.structureChanged(task);
            }
        }
    }

    public void notifyRemoved(Task task) {
        if (listeners != null) {
            int n = listeners.size();
            for (int i = 0; i < n; i++) {
                TaskListener tl = (TaskListener) listeners.get(i);
                tl.removedTask(task);
            }
        }
    }

    /** Return a count of the number of tasks in this list. */
    public int size() {
        return root.getSubtaskCountRecursively();
    }

    /** Return the translators capable of handling this tasklist.
     * @return Array of translators that can read/write the tasklist
     */
    public FormatTranslator[] getTranslators() {
        // XXX is it really tasklist property?
        FormatTranslator[] translators = new FormatTranslator[]{
            new HTMLSupport(),
            new XMLTranslator()
        };
        return translators;
    }

    ///* For debugging purposes:
    public void print() {
        System.err.println("\nTask List:\n-------------");
        if (root == null) {
            return;
        }
        recursivePrint(root, 0);
        System.err.println("\n\n");
    }

    private void recursivePrint(Task node, int depth) {
        if (depth > 20) { // probably invalid list
            Thread.dumpStack();
            return;
        }
        for (int i = 0; i < depth; i++) {
            System.err.print("   ");
        }
        System.err.println(node);
        if (node.getSubtasks() != null) {
            List l = node.getSubtasks();
            ListIterator it = l.listIterator();
            while (it.hasNext()) {
                Task task = (Task) it.next();
                recursivePrint(task, depth + 1);
            }
        }
    }
    // */

    /** Remove all the tasks in this tasklist */
    public void clear() {
        if (root != null) {
            root.clear();
            notifyStructureChanged(root);
        }
    }

    /**
     * Return the list of tasks in this tasklist
     *
     * @return subtasks of the root or null
     */
    public List getTasks() {
        return getRoot().getSubtasks();
    }

}
