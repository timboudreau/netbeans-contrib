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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.EventListenerList;
import org.netbeans.modules.tasklist.client.Suggestion;
import org.netbeans.modules.tasklist.client.SuggestionPriority;
import org.openide.ErrorManager;
import org.openide.nodes.Node;
import org.openide.nodes.Node.Cookie;

/**
 * Class which represents a task in the tasklist.
 *
 * @author Tor Norbye
 */
public class Task extends Suggestion implements Cloneable, Cookie {
    private static final Logger LOGGER = TLUtils.getLogger(Task.class);

    static {
        LOGGER.setLevel(Level.OFF);
    }

    /**
     * Some of this items attributes (such as its description - anything
     * except the subtask list) has changed
     *
     * @deprecated Suggestion provides specialized properties
     */
    static final String PROP_ATTRS_CHANGED = "attrs"; // NOI18N

    /** Set&lt;TaskListener> */
    protected EventListenerList listeners = new EventListenerList();
    // TODO: old code private Set listeners = new HashSet(2);

    private boolean visitable;

    /**
     * When true, don't notify anybody of updates to this object - and don't
     * modify the edited timestamp. Used by the restore code.
     */
    protected boolean silentUpdate = false;

    private Task parent;

    /** key shared by all clones */
    private Object key;

    /** If this item has subtasks, they are stored in this list */
    protected LinkedList subtasks = null;

    /** When true, this item has been removed from a list.
        The old list reference is still kept around so that
        we can use it to search for a reincarnation of the task.

        @deprecated duplicates isValid property
     */
    private boolean zombie = false;

    public Task() {
        super(null, null, null);
        parent = null;
        visitable = true;
        key = new Object();
    }

    public Task(String desc, Task parent) {
        super(null, desc, null);
        this.parent = parent;
        visitable = true;
        key = new Object();
    }

    /**
     * Removes all subtasks.
     */
    public void clear() {
        if (hasSubtasks()) {
            subtasks.clear();
            fireStructureChanged();
        }
    }

    /**
     * Returns indent level for this task. If parent == null returns 0
     *
     * @return indent level for this task
     */
    public int getLevel() {
        Task t = getParent();
        int level = 0;
        while (t != null) {
            level++;
            t = t.getParent();
        }
        return level;
    }

    /**
     * Set the description/summary of the task.
     *
     * @param ndesc The new description text
     */
    public void setSummary(String ndesc) {
        super.setSummary(ndesc);
    }

    public void setDetails(String ndesc) {
        super.setDetails(ndesc);
    }

    public void setPriority(SuggestionPriority priority) {
        super.setPriority(priority);
    }

    /**
     * @return true iff this task is "visitable"; returns true
     * if this node has its own content, false if it's just a "category"
     * node. Used for keyboard traversal: if you press Next (F12) you
     * don't want it to skip over all nonvisitable nodes.
     */
    public boolean isVisitable() {
        return visitable;
    }

    /**
     * Set whether or not this task is "visitable".
     *
     * @param visitable true if this node has its own content, false
     * if it's just a "category" node. Used for keyboard traversal: if
     * you press Next (F12) you don't want it to skip over all
     * nonvisitable nodes.
     */
    public void setVisitable(boolean visitable) {
        this.visitable = visitable;
    }

    /**
     * Fires a PropertyChangeEvent
     *
     * @param propertyName changed property
     * @param oldValue old value (may be null)
     * @param newValue new value (may be null)
     */
    protected void firePropertyChange(String propertyName, Object oldValue,
    Object newValue) {
        if (!silentUpdate) {
            super.firePropertyChange(propertyName, oldValue, newValue);
        }
    }

    public void addTaskListener(TaskListener l) {
        if (LOGGER.isLoggable(Level.FINE))
            Thread.dumpStack();
        listeners.add(TaskListener.class, l);
    }

    public void removeTaskListener(TaskListener l) {
        if (LOGGER.isLoggable(Level.FINE))
            Thread.dumpStack();
        listeners.remove(TaskListener.class, l);
    }

    protected final void fireStructureChanged() {
        if (silentUpdate)
            return;
        
        // Guaranteed to return a non-null array
        Object[] l = listeners.getListenerList();
        for (int i = l.length - 2; i >= 0; i -= 2) {
            if (l[i] == TaskListener.class) {
                ((TaskListener) l[i+1]).structureChanged(this);
            }
        }
//            if (getList() instanceof TaskList) {
//                ((TaskList) getList()).fireStructureChanged(this);
//            }
        if (this instanceof TaskListener) {
            ((TaskListener) this).structureChanged(this);
        }
    }

    /**
     * Fires an addedTask event
     *
     * @param t task that was added
     */
    protected final void fireAddedTask(Task t) {
        if (silentUpdate)
            return;
        
        // Guaranteed to return a non-null array
        Object[] l = listeners.getListenerList();
        for (int i = l.length - 2; i >= 0; i -= 2) {
            if (l[i] == TaskListener.class) {
                ((TaskListener) l[i+1]).addedTask(t);
            }
        }
        if (this instanceof TaskListener) {
            ((TaskListener) this).addedTask(t);
        }
    }


    /**
     * Fires an removedTask event
     *
     * @param t task that was removed
     */
    protected final void fireRemovedTask(Task t) {
        if (silentUpdate)
            return;
        
        // Guaranteed to return a non-null array
        Object[] l = listeners.getListenerList();
        for (int i = l.length - 2; i >= 0; i -= 2) {
            if (l[i] == TaskListener.class) {
                ((TaskListener) l[i+1]).removedTask(this, t);
            }
        }
//            if (getList() instanceof TaskList) {
//                ((TaskList) getList()).fireRemoved(this, t);
//            }
        if (this instanceof TaskListener) {
            ((TaskListener) this).removedTask(this, t);
        }
    }
    
    protected void recursivePropertyChange() {
        firePropertyChange(PROP_ATTRS_CHANGED, null, null);
        if (subtasks != null) {
            ListIterator it = subtasks.listIterator();
            while (it.hasNext()) {
                Task item = (Task)it.next();
                item.recursivePropertyChange();
            }
        }
    }

    /**
     * Returns subtasks of this task
     *
     * @todo all usages require iterator() or size() calls only, so it could be replaced by
     *       subtasksIterator and subtasksCount methods. Add TLUtil.iteratorToCollection.
     * @return children
     */
    public final List getSubtasks() {
        if (subtasks == null) {
            return Collections.EMPTY_LIST;
        } else {
            return subtasks;
        }
    }

    /**
     * Create subtasks iterator. It's remove method is not
     * supported yet. You need to call removeTask().
     *
     * @return non-recursive subtask iterator
     */
    public final Iterator subtasksIterator() {  // in JRE 1.5 could be turned to Iterable by renaming to Iterator<T> iterator().
        if (subtasks == null) {
            return Collections.EMPTY_LIST.iterator();
        } else {
            return subtasks.iterator();
        }
    }

    /** @return subtasks count */
    public final int subtasksCount() {
        if (subtasks == null) {
            return 0;
        } else {
            return subtasks.size();
        }
    }

    /** @return true if task exits in non-recursive subtasks. */
    public final boolean containsSubtask(Task task) {
        if (subtasks == null) {
            return false;
        } else {
            return subtasks.contains(task);
        }
    }

    /**
     * Add subtask to this task. The task will be prepended
     * to the task list.
     *
     * @param subtask task to be added as a subtask, to the front
     * of the list.
     */
    public void addSubtask(Task subtask) {
        addSubtask(subtask, false);
    }

    /**
     * Add subtask in a particular place in the parent's
     * subtask list
     *
     * @param subtask The subtask to be added
     * @param after The task which will be immediately before
     * the new subtask after the addition (e.g. add
     * this subtask directly AFTER the specified
     * task)
     */
    public void addSubtask(Task subtask, Task after) {
        subtask.parent = this;
        if (subtasks == null) {
            // Internal error - shouldn't call this unless you already have a subtask "after")
            ErrorManager.getDefault().log("addSubtask(subtask,after) called where subtasks==null"); // NOI18N
            return;
        }
        int pos = subtasks.indexOf(after);
        subtasks.add(pos+1, subtask);
        fireAddedTask(subtask);
    }

    /**
     * Add subtask in a particular place
     *
     * @param position position for the subtask
     * @param subtask The subtask to be added
     */
    private void addSubtask(int position, Task subtask) {
        subtask.parent = this;
        if (subtasks == null) {
            subtasks = new LinkedList();
        }
        subtasks.add(position, subtask);
        fireAddedTask(subtask);
    }

    /** Add a list of subtasks to this task.
     * @param subtasks The tasks to add
     * @param append When true, append to the list, otherwise prepend. Ignored
     *  if after is not null.
     * @param after The task which will be immediately before
     * the new subtask after the addition (e.g. add
     * this subtask directly AFTER the specified
     * task). Overrides the append parameter.
    */
    public void addSubtasks(List tasks, boolean append, Task after) {
        ListIterator it = tasks.listIterator();
        while (it.hasNext()) {
            Task task = (Task)it.next();
            task.parent = this;
        }

        if (subtasks == null) {
            subtasks = new LinkedList();
        }
        if (after != null) {
            int pos = subtasks.indexOf(after);
            subtasks.addAll(pos+1, tasks);
        } else if (append) {
            subtasks.addAll(tasks);
        } else {
            subtasks.addAll(0, tasks);
        }
        fireStructureChanged();
    }

   /**
    * Add a subtask to this task.
    * @param append When true, add to the end of the list of subtasks instead
    * of the beginning.
    */
    public void addSubtask(Task subtask, boolean append) {
        subtask.parent = this;
        if (subtasks == null) {
            subtasks = new LinkedList();
        }

        // XXX does not work with SuggetionList.addCategory:152
       // assert !subtasks.contains(subtask);
       if (subtasks.contains(subtask)) return;

        if (append) {
            subtasks.addLast(subtask);
        } else {
            subtasks.addFirst(subtask);
        }
        fireAddedTask(subtask);
    }

    /** Remove a particular subtask
     * @param subtask The subtask to be removed */
    public void removeSubtask(Task subtask) {
	//subtask.list = null;

//        if (subtask.getAnnotation() != null) {
//            subtask.getAnnotation().detach();
//            subtask.setAnnotation(null);
//        }

        // We need the list reference later, when looking for a reincarnation
        // of the task. So instead use the zombie field to mark deleted items.
        subtask.zombie = true;
        if (subtasks == null) {
            return;
        }
        subtasks.remove(subtask);
        if (subtasks.size() == 0) {
            subtasks = null;
        }
        
        fireRemovedTask(subtask);
    }

    /**
     * Indicate whether or not this task has any subtasks
     * @return true iff the item has any subtasks
     */
    public final boolean hasSubtasks() {
        return ((subtasks != null) && (subtasks.size() != 0));
    }

    public final Task getParent() {
        return parent;
    }


    /** Traverse to root task (or self)*/
    public final Task getRoot() {
        Task parent = getParent();
        if (parent != null) {
            return parent.getRoot();
        } else {
            return this;
        }
    }


    /** Determines whether given task lies in this context. */
    public final boolean isParentOf(Task task) {
        if (task.getKey() == getKey()) return true;
        Task nextLevel = task.getParent();
        if (nextLevel == null) return false;
        return isParentOf(nextLevel);  // recursion
    }

    /**
     * XXX make paskage private or even private
     * @deprecated iCalSupport should use addSubtask
     */
    /*public final void setParent(Task parent) {
        this.parent = parent;
        // Should we broadcast this change??? Probably not, it's always
        // manipulated as part of add/deletion operations which are tracked
        // elsewhere (see addSubTask for example)
        // if (!silentUpdate) {
        //     supp.firePropertyChange(PROP_CHILDREN_CHANGED, null, null);
        // }
    }*/

    /**
     * Indicate if this item is a "zombie" (e.g. it has been removed
     * from a tasklist. The list it was removed from is still pointed to
     * by the list field. See the Suggestion module's FixAction for an
     * example of why this is useful.
     */
    public boolean isZombie() {
        return zombie;
    }

    /**
     * Write a TodoItem to a text stream. NOT DONE.
     * @param item The task to write out
     * @param w The writer to write the string to
     * @throws IOException Not thrown explicitly by this code, but perhaps
     * by the call it makes to w's write() method
     *
     * @todo Finish the implementation here such that it
     * writes out all the fields, not just the
     * description.
     */
    public static void generate(Task item, Writer w) throws IOException {
	w.write(item.getSummary());
    }

    /**
     * Parse a task from a text stream.
     *
     * @param r The reader to read the task from
     * @throws IOException Not thrown directly by this method, but
     * possibly by r's read() method which it calls
     * @return A new task object which represents the
     * data read from the reader
     * @todo Finish the implementation
     * @see generate
     */
    public static Task parse(Reader r) throws IOException {
        LOGGER.fine("parsing");

        BufferedReader reader = new BufferedReader(r);
        //List notes = new LinkedList(); // List<Note>
        String line;
        while ((line = reader.readLine()) != null) {
            // XXX TodoTransfer's convert
            // method never seems to get called (see explanations in
                // TaskNode.clipboardCopy), so I haven't been
            // able to test this, that's why I haven't expanded the
            // code as much as it should be.
            Task item = new Task();
            item.setSummary(line);
            return item;
        }
        return null;
    }


    /** Generate a string summary of the task; only used
     * for debugging. DO NOT depend on this format for anything!
     * Use generate() instead.
     * @return summary string */
    /*
    public String toString() {
        return "Task[\"" + desc + "\"]"; // NOI18N
        //return "Task[\"" + desc + "\", " + priority + ", " + done + "]"; // NOI18N
        //return "Task[desc=\"" + desc + "\",prio=" + priority + ",done=" + done + ",temp=" + temporary + ",uid=" + uid + ",cat=" + category + ",created=" + created + ",edited=" + edited + "file=" + filename + ",line=" + linenumber + "] " + super.toString(); // NOI18N
    }
    */

    /** Setter for property silentUpdate.
     * When true, don't notify anybody of updates to this object - and don't
        modify the edited timestamp. Used by the restore code.
     * @param silentUpdate New value of property silentUpdate.
     @param fireChildren If true, fire children property changes
                            when updates are reenabled
     */
    // XXX make private again!
    public void setSilentUpdate(boolean silentUpdate,
                                boolean fireChildren) {
        this.silentUpdate = silentUpdate;
        if (!silentUpdate) {
            if (fireChildren) {
                fireStructureChanged();
            }
        }
    }

    /**
     * Counts all subtasks of this task recursively.
     *
     * @return number of subtasks
     */
    public int getSubtaskCountRecursively() {
        if(subtasks == null) return 0;

        int n = 0;
        Iterator it = subtasks.iterator();
        while(it.hasNext()) {
            Task t = (Task) it.next();
            n += t.getSubtaskCountRecursively() + 1;
        }
        return n;
    }

    /**
     * Create default nodes for this item.
     * Actual view may use them or use any replacement.
     */
    public Node[] createNode() {
        //if (hasSubtasks()) {
        if (subtasks != null) {  // Want to make root a non-leaf; empty list, not null
            return new Node[] {new TaskNode(this, new TaskChildren(this))};
        } else {
            return new Node[] {new TaskNode(this)};
        }
    }

    /** Create an identical copy of a task (a deep copy, e.g. the
        list of subtasks will be cloned as well */
    protected Object clone() {
        Task t = new Task();
        t.copyFrom(this);
        return t;
    }

    /**
     * Clones task's properies without its
     * membership relations (parent).
     */
    final Task cloneTask() {
        // Does not work well as we subclass a suggestion that is 1:1 to its agent 
        Task clone = (Task) clone();
        clone.parent = null;
        return clone;
    }

    /**
     * Returns a key shared by all task clones.
     */
    public final Object getKey() {
        return key;
    }

    /**
     * Get the provider. Not defined for tasks - will be subclassed
     * in SuggestionImpl but we don't want Task to be abstract...
     */
    public Object getSeed() {
         return null;
    }


    /** Copy all the fields in the given task into this object.
        Should only be called on an object of the EXACT same type.
        Thus, if you're implementing a subclass of Task, say
        UserTask, you can implement copy assuming that the passed
        in Task parameter is of type UserTask. When overriding,
        remember to call super.copy.
        <p>
        Make a deep copy - except when that doesn't make sense.
        For example, you can share the same icon reference.
        And in particular, the tasklist reference should be the same.
        But the list of subitems should be unique. You get the idea.
    */
    protected void copyFrom(Task from) {
        visitable = from.visitable;
        zombie = from.zombie;

        assert from.key != null;
        key = from.key;

        // Copy fields from the parent implementation
        super.setSummary(from.getSummary());
        super.setPriority(from.getPriority());
        super.setIcon(from.getIcon());
        super.setType(from.getType());
        super.setLine(from.getLine());
        super.setAction(from.getAction());
        super.setDetails(from.getDetails());

        // Copying the parent reference may seem odd, since for children
        // it should be changed - but this only affects the root node.
        // For children nodes, we override the parent reference after
        // cloning the child.
        parent = from.parent;

        // Copy the subtasks reference

        // XXX
	// Please note -- I'm NOT copying the universal id, these have to
	// be unique, even for copies
        if (from.subtasks != null) {
            ListIterator it = from.subtasks.listIterator();
            subtasks = new LinkedList();
            while (it.hasNext()) {
                Task task = (Task)it.next();
                Task mycopy = (Task)task.clone();
                mycopy.parent = this;
                subtasks.addLast(mycopy);
            }
        }
    }
}

