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
package org.netbeans.modules.tasklist.core;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import org.netbeans.api.tasklist.Suggestion;
import org.openide.TopManager;
import org.openide.nodes.Node;



// XXX todo: fire property change whenever anything changes in the node...

/** Class which represents a task in the
 * tasklist.
 * @author Tor Norbye */
public class Task extends Suggestion implements Cloneable {

    /** The subtask list has changed */
    public static final String PROP_CHILDREN_CHANGED = "children"; // NOI18N
    /** Some of this items attributes (such as its description - anything
     * except the subtask list) has changed */
    public static final String PROP_ATTRS_CHANGED = "attrs"; // NOI18N

    protected boolean temporary;
    protected TaskList list;

    /** When true, don't notify anybody of updates to this object - and don't
        modify the edited timestamp. Used by the restore code. */
    protected boolean silentUpdate = false;
    
    protected Task parent;
    
    /** If this item has subtasks, they are stored in this list */
    protected LinkedList subtasks = null;
    
    public Task() { // TODO consider using a factory instead
        super(null, null, null);
        parent = null;
	list = null;
    }
    
    public Task(String desc, Task parent) {
        super(null, desc, null);
        this.parent = parent;
	list = null;
    }
    
    /** Set the description/summary of the task.
     * @param ndesc The new description text */    
    public void setSummary(String ndesc) {
        super.setSummary(ndesc);
        if (!silentUpdate) {
            updatedValues();
        }
    }

    /** Set the "temporary" status of the task. Temporary tasks
	are not saved in the todo list, and they may be deleted
	automatically by the IDE when appropriate. For example,
	tasks added as source files are scanned will be deleted
	when you open another file.
    */
    public void setTemporary(boolean temp) {
	temporary = temp;
    }
    
    /** Get the "temporary" status of the task. Temporary tasks
	are not saved in the todo list, and they may be deleted
	automatically by the IDE when appropriate. For example,
	tasks added as source files are scanned will be deleted
	when you open another file.
    */
    public boolean isTemporary() {
	return temporary;
    }
    

    protected void updatedValues() {
        if (!silentUpdate) {
            supp.firePropertyChange(PROP_ATTRS_CHANGED, null, null);
	    if (!isTemporary() && (list != null)) {
		list.markChanged();
	    }
        }
    }

    protected void updatedStructure() {
        if (!silentUpdate) {
            supp.firePropertyChange(PROP_CHILDREN_CHANGED, null, null);
	    if (!isTemporary() && (list != null)) {
		list.markChanged();
	    }
        }
    }

    /** Return the display name of the task, which is identical
     * to the summary.
     * @return The description
     * @todo Decide if this method is necessary/used or not. */    
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

    protected final PropertyChangeSupport supp = 
        new PropertyChangeSupport(this);
    
    /** Listen to changes in bean properties.
     * @param l listener to be notified of changes */
    public void addPropertyChangeListener(PropertyChangeListener l) {
        supp.removePropertyChangeListener(l);
        supp.addPropertyChangeListener(l);
    }
    
    /** Stop listening to changes in bean properties.
     * @param l listener who will no longer be notified of changes */
    public void removePropertyChangeListener(PropertyChangeListener l) {
        supp.removePropertyChangeListener(l);
    }
    
    public List getSubtasks() {
        return subtasks;
    }

    /** Add subtask to this task. The task will be prepended
     * to the task list.
     * @param subtask task to be added as a subtask, to the front
     * of the list. */    
    public void addSubtask(Task subtask) {
        addSubtask(subtask, false);
    }
    
    /** Add subtask in a particular place in the parent's
     * subtask list
     * @param subtask The subtask to be added
     * @param after The task which will be immediately before
     * the new subtask after the addition (e.g. add
     * this subtask directly AFTER the specified
     * task) */    
    public void addSubtask(Task subtask, Task after) {
        if (subtasks == null) {
            // Internal error - shouldn't call this unless you already have a subtask "after")
            TopManager.getDefault().getErrorManager().log("addSubtask(subtask,after) called where subtasks==null"); // NOI18N
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
    * @param append When true, append to the list, otherwise prepend
    */
    public void addSubtasks(List tasks, boolean append) { // XXX remove publicness
	ListIterator it = tasks.listIterator();
	while (it.hasNext()) {
	    Task task = (Task)it.next();
	    task.list = list;
	}

	if (subtasks == null) {
	    // uh oh, we're adding tasks for the first time to a 
	    // "leaf" task; do some trickery to change the parent node

	    // yuck yuck yuck.... okay, duplicate the code here,
	    // then try to prune it when NetBeans 4.0 gets the feature
	    // (oh, and add the bug id here with a TODO!)
            subtasks = new LinkedList();
            if (parent == null) {
                // MUST be the root node....
		if (append) {
		    subtasks.addAll(tasks);
		} else {
		    subtasks.addAll(0, tasks);
		}
		if (!silentUpdate) {
		    supp.firePropertyChange(PROP_CHILDREN_CHANGED, null, null);
		}
                return;
            }
            // Parent is a leaf node (well, it might be a container
            // node which has had all its children removed, but that's
            // okay, the following will work)
            // Since we can't change the children object, we need
            // to recreate the node completely.
            List l = parent.getSubtasks();
            int pos = l.indexOf(this);
            // NEEDS14 assert pos != -1 : "parent " + parent +
	    //  " does not contain item " + item;
            parent.subtasks.remove(this);
            
            // XXX for some reason, this is necessary here
            // Is this necessary to get nodes etc. disassociated and
            // get rid of the old?
            if (!silentUpdate) {
                parent.supp.firePropertyChange(PROP_CHILDREN_CHANGED, null, null);
            }
            // append: subtasks.add(subtask);
	    if (append) {
		subtasks.addAll(tasks);
	    } else {
		subtasks.addAll(0, tasks);
	    }
            parent.subtasks.add(pos, this);
            if (!silentUpdate) {
                parent.supp.firePropertyChange(PROP_CHILDREN_CHANGED, null, null);
                supp.firePropertyChange(PROP_CHILDREN_CHANGED, null, null);
                updatedStructure();
            }
	} else {
	    if (append) {
		subtasks.addAll(tasks);
	    } else {
		subtasks.addAll(0, tasks);
	    }
            if (!silentUpdate) {
                supp.firePropertyChange(PROP_CHILDREN_CHANGED, null, null);
            }
	}
    }
    
   /** Convert a particular node from being a leaf to being
     * a container node.  The only way to do that now is to
     * delete the item, and recreate it */
    public void addSubtask(Task subtask, boolean append) { // XXX remove publicness
	subtask.list = list;
        if (subtasks != null) {
            // Parent already has subtasks - just do a normal add
            if (append) {
               subtasks.addLast(subtask);
            } else {
               subtasks.addFirst(subtask);
            }
            if (!silentUpdate) {
                supp.firePropertyChange(PROP_CHILDREN_CHANGED, null, null);
            }
        } else {
            subtasks = new LinkedList();
            if (parent == null) {
                // MUST be the root node....
                // append: subtasks.add(subtask);
                if (append) {
                   subtasks.addLast(subtask);
                } else {
                   subtasks.addFirst(subtask);
                }
                if (!silentUpdate) {
                    supp.firePropertyChange(PROP_CHILDREN_CHANGED, null, null);
                }
                return;
            }
            // Parent is a leaf node (well, it might be a container
            // node which has had all its children removed, but that's
            // okay, the following will work)
            // Since we can't change the children object, we need
            // to recreate the node completely.
            List l = parent.getSubtasks();
            int pos = l.indexOf(this);
            // NEEDS14 assert pos != -1 : "parent " + parent +
	    //  " does not contain item " + item;
            parent.subtasks.remove(this);
            
            // XXX for some reason, this is necessary here
            // Is this necessary to get nodes etc. disassociated and
            // get rid of the old?
            if (!silentUpdate) {
                parent.supp.firePropertyChange(PROP_CHILDREN_CHANGED, null, null);
            }
            // append: subtasks.add(subtask);
            if (append) {
                subtasks.addLast(subtask);
            } else {
                subtasks.addFirst(subtask);
            }
            parent.subtasks.add(pos, this);
            if (!silentUpdate && !subtask.silentUpdate) {
                parent.supp.firePropertyChange(PROP_CHILDREN_CHANGED, null, null);
                supp.firePropertyChange(PROP_CHILDREN_CHANGED, null, null);
                updatedStructure();
            }
        }
    }
    
    /** Remove a particular subtask
     * @param subtask The subtask to be removed */    
    public void removeSubtask(Task subtask) {
	subtask.list = null;
        if (subtasks == null) {
            return;
        }
        subtasks.remove(subtask);
        if (subtasks.size() == 0) {
            subtasks = null;
        }
        if (!silentUpdate && !subtask.silentUpdate) {
            supp.firePropertyChange(PROP_CHILDREN_CHANGED, null, null);
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
            supp.firePropertyChange(PROP_CHILDREN_CHANGED, null, null);
            updatedStructure();
        }
    }
    
    /** Indicate whether or not this task has any subtasks
     * @return true iff the item has any subtasks */    
    public boolean hasSubtasks() {
        return ((subtasks != null) && (subtasks.size() != 0));
    }
    
    public Task getParent() { // XXX make private again!
        return parent;
    }

    public void setParent(Task parent) { // XXX remove publicness
        this.parent = parent;
        // Should we broadcast this change??? Probably not, it's always
        // manipulated as part of add/deletion operations which are tracked
        // elsewhere (see addSupTask for example)
        // if (!silentUpdate) {
        //     supp.firePropertyChange(PROP_CHILDREN_CHANGED, null, null);
        // }
    }

    /** Write a TodoItem to a text stream. NOT DONE.
     * @param item The task to write out
     * @param w The writer to write the string to
     * @throws IOException Not thrown explicitly by this code, but perhaps
     * by the call it makes to w's write() method
     * @todo Finish the implementation here such that it
     * writes out all the fields, not just the
     * description. */
    public static void generate(Task item, Writer w) throws IOException {
	// XXX
	// For some odd reason, my TodoTransfer's convert method never seems
	// to get called, so I haven't been able to test this, that's why
	// I haven't expanded the code as much as it should be.
	w.write(item.getSummary());
    }

    /** Parse a task from a text stream.
     * @param r The reader to read the task from
     * @throws IOException Not thrown directly by this method, but
     * possibly by r's read() method which it calls
     * @return A new task object which represents the
     * data read from the reader
     * @todo Finish the implementation
     * @see generate */
    public static Task parse(Reader r) throws IOException {
        BufferedReader reader = new BufferedReader(r);
        List notes = new LinkedList(); // List<Note>
        String line;
        while ((line = reader.readLine()) != null) {
	    // XXX For some odd reason, my TodoTransfer's convert
	    // method never seems to get called, so I haven't been
	    // able to test this, that's why I haven't expanded the
	    // code as much as it should be.
	    Task item = new Task();
	    item.setSummary(line);
	    return item;
	    // XXX not done... see above
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
	    } else if (fireChildren) {
		supp.firePropertyChange(PROP_CHILDREN_CHANGED, null, null);
	    }
	    if (save && (list != null)) {
		list.markChanged();
	    }
	}
    }

    /** Set the list this task is contained in. */
    public void setList(TaskList list) { // XXX remove publicness
	this.list = list;
    }

    /** Get the list this task is contained in. */
    public TaskList getList() {
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
    protected Node[] createNode() {
        if (hasSubtasks()) {
            return new Node[] { new TaskNode(this, getSubtasks())};
        } else {
            return new Node[] { new TaskNode(this)};
        }
    }

    /** Create an identical copy of a task (a deep copy, e.g. the
        list of subtasks will be cloned as well */
    protected Object clone() throws CloneNotSupportedException {
        Task t = new Task();
        t.copyFrom(this);
        return t;
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
        temporary = from.temporary;
        list = from.list;

        // Copy fields from the parent implementation
        super.setSummary(from.getSummary());
        super.setPriority(from.getPriority());
        super.setIcon(from.getIcon());
        super.setType(from.getType());
        super.setLine(from.getLine());
        super.setAction(from.getAction());

        
        
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
                try {
                    Task mycopy = (Task)task.clone();
                    mycopy.parent = this;
                    subtasks.addLast(mycopy);
                } catch (java.lang.CloneNotSupportedException e) {
                    // Internal error - this should NOT happen since
                    // tasks implement Cloneable
                    e.printStackTrace();
                }
            }
        }
    }
}



