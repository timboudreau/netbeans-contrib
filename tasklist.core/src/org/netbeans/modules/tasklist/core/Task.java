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
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.modules.tasklist.client.Suggestion;
import org.netbeans.modules.tasklist.client.SuggestionPriority;
import org.netbeans.modules.tasklist.providers.SuggestionProvider;
import org.openide.ErrorManager;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;

/**
 * Class which represents a task in the tasklist.
 *
 * <p>
 * Field "temporary":
 * Here's a little background on that field.  Earlier, there used to be a
 * single tasklist window which contained both your user entered tasks, as
 * well as "dynamic" items like TODOs scanned from your source code. You
 * obviously don't want these TODOs showing up in your user task list file -
 * or even cause a tasklist filesave when they're added or modified. Thus,
 * the temporary attribute.
 *
 * We don't need it now since we have separate views for user tasks
 * (persistent) and dynamic tasks (temporary). However, one of the feedback
 * datapoints from the UI review was that the distinction between user tasks
 * and suggestions is a bit murky (especially in the area of TODOs which the
 * user did enter - but in the source code). Afterall, VS.NET and Eclipse
 * have single tasklist windows, not multiple (although granted their user
 * task support is a bit more limited).
 *
 * So if we'll have a single view in the future the concept may become useful
 * again - but of course various other code changes will be necessary, so we
 * don't need to hang on to this attribute now.
 *
 * This attribute was used during saving, in firing change events and in
 * FormatTranslators.
 * </p>
 *
 * @author Tor Norbye
 * @author Tim Lebedkov
 */
public class Task extends Suggestion implements Cloneable {
    private static final Logger LOGGER = TLUtils.getLogger(Task.class);

    static {
        LOGGER.setLevel(Level.OFF);
    }

    /** Keys for the Bundle.properties */
    private static final String[] PRIORITIES_KEYS = {
        "PriorityHigh",  // NOI18N
        "PriorityMediumHigh", // NOI18N
        "PriorityMedium", // NOI18N
        "PriorityMediumLow", // NOI18N
        "PriorityLow" // NOI18N
    };

    /** Names for priorities */
    private static String[] PRIORITIES;

    static {
        PRIORITIES = new String[PRIORITIES_KEYS.length];
        ResourceBundle rb = NbBundle.getBundle(Task.class);
        for (int i = 0; i < PRIORITIES_KEYS.length; i++) {
            PRIORITIES[i] = rb.getString(PRIORITIES_KEYS[i]);
        }
    }

    /**
     * Returns names for priorities
     *
     * @return [0] - high, [1] - medium-high, ...
     */
    public static String[] getPriorityNames() {
        return PRIORITIES;
    }

    /**
     * Finds a priority.
     * @param n integer representation of a priority
     * @return priority
     */
    public static SuggestionPriority getPriority(int n) {
        switch (n) {
            case 1:
                return SuggestionPriority.HIGH;
            case 2:
                return SuggestionPriority.MEDIUM_HIGH;
            case 3:
                return SuggestionPriority.MEDIUM;
            case 4:
                return SuggestionPriority.MEDIUM_LOW;
            case 5:
                return SuggestionPriority.LOW;
            default:
                return SuggestionPriority.MEDIUM;
        }
    }

    /**
     * Some of this items attributes (such as its description - anything
     * except the subtask list) has changed
     */
    public static final String PROP_ATTRS_CHANGED = "attrs"; // NOI18N

    protected final PropertyChangeSupport supp = new PropertyChangeSupport(this);

    protected ObservableList list;
    protected boolean visitable;

    /**
     * When true, don't notify anybody of updates to this object - and don't
     * modify the edited timestamp. Used by the restore code.
     */
    protected boolean silentUpdate = false;

    protected Task parent;

    /** key shared by all clones */
    private Object key;

    /** If this item has subtasks, they are stored in this list */
    protected LinkedList subtasks = null;

    /** When true, this item has been removed from a list.
        The old list reference is still kept around so that
        we can use it to search for a reincarnation of the task. */
    protected boolean zombie = false;

    public Task() {
        super(null, null, null);
        parent = null;
	    list = null;
        visitable = true;
        key = new Object();
    }

    public Task(String desc, Task parent) {
        super(null, desc, null);
        this.parent = parent;
    	list = null;
        visitable = true;
        key = new Object();
    }

    /**
     * Removes all subtasks.
     */
    public void clear() {
        if (hasSubtasks()) {
            subtasks.clear();
            updatedStructure();
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
        if (!silentUpdate) {
            updatedValues();
        }
    }

    public void setDetails(String ndesc) {
        super.setDetails(ndesc);
        if (!silentUpdate) {
            updatedValues();
        }
    }

    public void setPriority(SuggestionPriority priority) {
        super.setPriority(priority);
        updatedValues();
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
            supp.firePropertyChange(propertyName, oldValue, newValue);
            if (getList() instanceof TaskListener) {
                ((TaskListener) getList()).changedTask(this);
            }
        }
    }

    protected void updatedValues() {
        if (!silentUpdate) {
            supp.firePropertyChange(PROP_ATTRS_CHANGED, null, null);
            if (getList() instanceof TaskListener) {
                ((TaskListener) getList()).changedTask(this);
            }
        }
    }

    protected void updatedStructure() {
        if (!silentUpdate) {
            if (getList() instanceof TaskListener) {
                ((TaskListener) getList()).structureChanged(this);
            }
        }
    }

    /**
     * Return the display name of the task, which is identical
     * to the summary.
     *
     * @return The description
     * @deprecated use getSummary()
     * @todo Decide if this method is necessary/used or not.
     */
    public String getDisplayName() {
        return getSummary();
    }

    protected void recursivePropertyChange() {
        supp.firePropertyChange(PROP_ATTRS_CHANGED, null, null);
        if (subtasks != null) {
            ListIterator it = subtasks.listIterator();
            while (it.hasNext()) {
                Task item = (Task)it.next();
                item.recursivePropertyChange();
            }
        }
    }

    /**
     * Listen to changes in bean properties.
     * @param l listener to be notified of changes
     */
    public void addPropertyChangeListener(PropertyChangeListener l) {
        supp.removePropertyChangeListener(l);
        supp.addPropertyChangeListener(l);
    }

    /**
     * Stop listening to changes in bean properties.
     *
     * @param l listener who will no longer be notified of changes
     */
    public void removePropertyChangeListener(PropertyChangeListener l) {
        supp.removePropertyChangeListener(l);
    }

    /**
     * Returns subtasks of this task
     *
     * @return children
     */
    public List getSubtasks() {
        if (subtasks == null)
            return Collections.EMPTY_LIST;
        else
            return subtasks;
    }

    /**
     * This method is here only for the benefit of the XMLEncoder,
     * such that task trees can be persisted. Do not use it directly;
     * use addSubtask instead.
     */
    public void setSubtasks(LinkedList subtasks) {
        this.subtasks = subtasks;
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
    	subtask.list = list;
        if (!silentUpdate && !subtask.silentUpdate) {
            updatedStructure();
        }
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
            task.list = list;
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
        updatedStructure();
    }

   /**
    * Add a subtask to this task.
    * @param append When true, add to the end of the list of subtasks instead
    * of the beginning.
    */
    public void addSubtask(Task subtask, boolean append) {
    	subtask.list = list;
        subtask.parent = this;
        if (subtasks == null) {
            subtasks = new LinkedList();
        }

        assert !subtasks.contains(subtask);

        if (append) {
            subtasks.addLast(subtask);
        } else {
            subtasks.addFirst(subtask);
        }
        updatedStructure();
    }

    /**
     * Removes this task from the task list
     */
    public void remove() {
        assert parent != null : "parent == null";

        getParent().removeSubtask(this);
    }

    /** Remove a particular subtask
     * @param subtask The subtask to be removed */
    public void removeSubtask(Task subtask) {
	//subtask.list = null;
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
        if (!silentUpdate && !subtask.silentUpdate) {
            updatedStructure();
        }
    }

    /** For use with list iterators
	@param it Iterator, which should just have returned the next parameter
	@param item The item which was most recently next()'ed out of the iterator
     */
    public void removeSubtask(ListIterator it, Task item) { // Remove publicness
        if (it == null) {
            return;
        }
	item.list = null;
        it.remove();

        // XXX is the following allowed?
        if (subtasks.size() == 0) {
            subtasks = null;
        }
        if (!silentUpdate && !item.silentUpdate) {
            updatedStructure();
        }
    }

    /** Indicate whether or not this task has any subtasks
     * @return true iff the item has any subtasks */
    public boolean hasSubtasks() {
        return ((subtasks != null) && (subtasks.size() != 0));
    }

    public Task getParent() {
        return parent;
    }

    /** Determines whether given task lies in this context. */
    public final boolean isParentOf(Task task) {
        if (task.getKey() == getKey()) return true;
        Task nextLevel = task.getParent();
        if (nextLevel == null) return false;
        return isParentOf(nextLevel);  // recursion
    }

    public void setParent(Task parent) {
        this.parent = parent;
        // Should we broadcast this change??? Probably not, it's always
        // manipulated as part of add/deletion operations which are tracked
        // elsewhere (see addSubTask for example)
        // if (!silentUpdate) {
        //     supp.firePropertyChange(PROP_CHILDREN_CHANGED, null, null);
        // }
    }

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
     * @param fireAttrs If true, fire attribute property changes when
                       updates are reenabled
     * @param fireChildren If true, fire children property changes
                       when updates are reenabled
     */
    // XXX make private again!
    public void setSilentUpdate(boolean silentUpdate, boolean fireAttrs,
			 boolean fireChildren, boolean save) {
        this.silentUpdate = silentUpdate;
        if (!silentUpdate) {
            if (fireAttrs) {
                supp.firePropertyChange(PROP_ATTRS_CHANGED, null, null);
            }

            if (fireChildren) {
                getList().notifyStructureChanged(this);
            }
        }
    }

    /** Set the list this task is contained in. */
    public void setList(ObservableList list) { // XXX remove publicness
        this.list = list;
    }

    /** Get the list this task is contained in. */
    public ObservableList getList() {
        return list;
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

    /** Create a node for this item */
    public Node[] createNode() {
        //if (hasSubtasks()) {
        if (subtasks != null) {  // Want to make root a non-leaf; empty list, not null
            return new Node[] { new TaskNode(this, getSubtasks())};
        } else {
            return new Node[] { new TaskNode(this)};
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
     * membership relations (parent and list).
     */
    public Task cloneTask() {
        Task clone = (Task) clone();
        clone.list = null;
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
        list = from.list;
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

