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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;

import org.openide.nodes.Node;
import org.openide.nodes.Children;
import org.openide.util.Mutex;
import org.openide.util.WeakListener;
import org.openide.util.WeakListeners;


/**
 * Children object for the Task; used to track
 * TaskList modifications and update nodes appropriately.
 *
 * @author Tor Norbye
 * @author Petr Kuzel
 */
public class TaskChildren extends Children.Keys {
    
    private java.util.Map keys2tasks;  // vs. Children.Map
    private final Task parent;
    private Monitor monitor;

    public TaskChildren(Task parent) {
        this.parent = parent;
    }
    
    private void refreshKeys() {
        Collection keys;
        if (parent.hasSubtasks() == false) {
            keys = Collections.EMPTY_SET;
        } else {

// It does not work and does not save any Node instance creation
//            // threat task clones as equal
//            // XXX we may need to refresh nodes that share key
//            // why do all pay here this extra overhead?
//
//            int size = parent.getSubtasks().size();
//            ArrayList list = new ArrayList(size);
//            keys2tasks = new WeakHashMap(size*2);
//            Iterator it = parent.getSubtasks().iterator();
//            while (it.hasNext()) {
//                Task task = (Task) it.next();
//                Object key = task.getKey();
//                list.add(key);
//                keys2tasks.put(key, task);
//            }
//            setKeys(list);
            keys = parent.getSubtasks();
        }

        // #37802 XXX workaround
        final Task[] tasks = (Task[])keys.toArray(new Task[keys.size()]);
        Mutex.EVENT.readAccess(new Runnable() {
            public void run() {
                setKeys(tasks);
            }
        });
    }

    /**
     * Called when the parent node is expanded; now we need
     * to create nodes for the children.
     */
    protected void addNotify() {
        super.addNotify();
        assert monitor == null : "Dangling addNotify()"; // NOI18N
        monitor = new Monitor();

        // weak listener must be used here because children
        // all listening on tasklist that has different
        // lifetime than parent node nor is driven by it.
        TaskListener l = (TaskListener) WeakListeners.create(TaskListener.class, monitor, parent);
        parent.addTaskListener(l);
        refreshKeys();
    }
    
    /** Called when the parent node is collapsed: cleanup */    
    protected void removeNotify() {
        keys2tasks = null;
        assert monitor != null : "Dangling removeNotify()"; // NOI18N
        // parent.getList().removeTaskListener(monitor);
        monitor = null;
        setKeys(Collections.EMPTY_SET);
        super.removeNotify();
    }
    
    /**
     * Create nodes for the specified key object (a task)
     * @param key The task used as a parent key
     * @return Node for the key task's children
     */
    protected Node[] createNodes(Object key) {
        // interpret your key here...usually one node generated, but could be zero or more
        //  return new Node[] { new TodoNode((MyParameter) key) };
        //return new Node[] { new TodoNode(key) };

//        Task item = (Task)keys2tasks.get(key);
//        assert item != null : "The key was held by Children.Keys!";
        return ((Task)key).createNode(); // XXX I do not like this model-view 1:1
    }

    // Monitor tasklist and react to changes ~~~~~~~~~~~~~~~~~~~~~

    private class Monitor implements TaskListener {
        public void selectedTask(Task t) {
            // it's node job
        }

        public void warpedTask(Task t) {
            // it's node job
        }

        public void addedTask(Task t) {
            if (t.getParent() == parent) {
                refreshKeys();
            }
        }

        public void removedTask(Task pt, Task t) {
            if (t.getParent() == parent) {
                refreshKeys();
            }
        }

        public void structureChanged(Task t) {
            if (t == null || t.isParentOf(parent)) {
                refreshKeys();
            }
        }

    }
}
