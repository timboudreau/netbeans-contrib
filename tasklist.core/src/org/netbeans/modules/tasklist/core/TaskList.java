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
 * This class models flat list or hierarchical tasks.
 * It dispatches membership events.
 *
 * @author Tor Norbye
 * @author Tim Lebedkov
 */
public class TaskList implements ObservableList, TaskListener {

    // List category
    final static String USER_CATEGORY = "usertasks"; // NOI18N

    // data holder
    protected Task root = null;
    
    private final ArrayList listeners = new ArrayList(67);
    
    /** Has the options set changed such that we need to save */
    protected boolean needSave = false;
    protected boolean dontSave = false;
    
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
        root.addTaskListener(this);
    }

    protected void setNeedSave(boolean b) {
        needSave = b;
    }

    protected void setDontSave(boolean b) {
        dontSave = b;
    }

    /**
     * Returns the root task of the task list
     *
     * @return root task
     */
    public Task getRoot() {
        if (root == null) {
            // Just use the name "Description" since for some reason,
            // when we have no items the TreeView puts the root node
            // description as the header for the leftmost column...
            root = new Task();
            root.setSummary(NbBundle.getMessage(TaskList.class,
                    "Description")); // NOI18N
            root.addTaskListener(this);
            root.setList(this);  // TODO why does task know about its list?
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
        Iterator it;
        if (removeList != null) {
            it = removeList.iterator();
            while (it.hasNext()) {
                Task task = (Task) it.next();
                task.removeTaskListener(this);
                if (parent != null) {
                    parent.removeSubtask(task);
                } else {
                    root.removeSubtask(task);
                }
                modified = true;
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

            it = addList.iterator();
            while (it.hasNext()) {
                Task next = (Task) it.next();
                next.addTaskListener(this);
            }

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

    /**
     * Notify the task list that some aspect of it has been changed, so
     * it should save itself soon. Eventually calls save 
     */
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

    public void addTaskListener(TaskListener listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    public void removeTaskListener(TaskListener listener) {
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }

    /** Fire TaskListener.addedTask */
    protected void fireAdded(Task task) {
        synchronized (listeners) {
            int n = listeners.size();
            for (int i = 0; i < n; i++) {
                TaskListener tl = (TaskListener) listeners.get(i);
                tl.addedTask(task);
            }
        }
    }

    /**
     * Fire TaskListener.selectedTask
     * @deprecated splitting model from the view
     */
    private void notifySelected(Task task) {
        synchronized (listeners) {
            int n = listeners.size();
            for (int i = 0; i < n; i++) {
                TaskListener tl = (TaskListener) listeners.get(i);
                tl.selectedTask(task);
            }
        }
    }

    /**
     * Fire TaskListener.warpedTask
     * @deprecated splitting model from the view
     */
    private void notifyWarped(Task task) {
        synchronized (listeners) {
            int n = listeners.size();
            for (int i = 0; i < n; i++) {
                TaskListener tl = (TaskListener) listeners.get(i);
                tl.warpedTask(task);
            }
        }
    }

    /** Fire TaskListener.structureChanged */
    protected void fireStructureChanged(Task task) {
        TaskListener[] taskListeners; // some listeners are self deregistering on this event causing index exception
        synchronized (listeners) {
            taskListeners = new TaskListener[listeners.size()];
            taskListeners = (TaskListener[]) listeners.toArray(taskListeners);
        }
        for (int i = 0; i < taskListeners.length; i++) {
            taskListeners[i].structureChanged(task);
        }
    }

    /** Fire TaskListener.removedTask */
    protected void fireRemoved(Task pt, Task task) {
        TaskListener[] taskListeners;  // some listeners are self deregistering on this event causing index exception
        synchronized (listeners) {
            taskListeners = new TaskListener[listeners.size()];
            taskListeners = (TaskListener[]) listeners.toArray(taskListeners);
        }
        for (int i = 0; i < taskListeners.length; i++) {
            taskListeners[i].removedTask(pt, task);
        }

    }

    /** 
     * Return a count of the number of tasks in this list. 
     *
     * @deprecated use getRoot().getSubtaskCountRecursively() instead
     */
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

    /**
     * Remove all the tasks in this tasklist 
     */
    public void clear() {
        if (root != null) {
            root.clear();
            fireStructureChanged(root);
        }
    }

    /** For debugging purposes, only. Writes directly to serr. */
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

    // TaskListener impl ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /** Internal listener implementtaion. Cumulates event from list members to list level. */
    public void selectedTask(Task t) {
        // XXX ignore
    }

    /** Internal listener implementtaion. Cumulates event from list members to list level. */
    public void warpedTask(Task t) {
        // XXX ignore
    }

    /** Internal listener implementtaion. Cumulates event from list members to list level. */
    public void addedTask(Task t) {
        fireAdded(t);
    }

    /** Internal listener implementtaion. Cumulates event from list members to list level. */
    public void removedTask(Task pt, Task t) {
        fireRemoved(pt, t);
    }

    /** Internal listener implementtaion. Cumulates event from list members to list level. */
    public void structureChanged(Task t) {
        fireStructureChanged(t);
    }


}
