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

package org.netbeans.modules.tasklist.docscan;

import org.netbeans.modules.tasklist.core.ObservableList;
import org.netbeans.modules.tasklist.core.TaskListener;
import org.netbeans.modules.tasklist.core.Task;
import org.netbeans.modules.tasklist.core.TaskList;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.WeakHashMap;

/**
 * Delegate actions to original task list while
 * narrowing them to source tasks.
 *
 * @author Petr Kuzel
 */
final class FilteredTasksList implements ObservableList {

    /*
     * There is one realy strict implementation assumption
     * that source tasks never nest. It simplifies
     * implementation a lot because it degrades to list.
     *
     * It also assumes that visible view must have attached
     * TaskListener
     */

    private final TaskList peer;
    private List listeners = new ArrayList(1);
    private Task root;
    private EventHandler handler;
    private boolean silent = false;

    public FilteredTasksList(TaskList peer) {
        assert peer.getRoot() != null : "Unitialized list :" + peer;  // NOI18N
        this.peer = peer;
    }

    /**
     * Unlike real root this one is sampled. It
     * starts to monitor live peer once a listener
     * is attached.
     */
    public Task getRoot() {
        if (root == null) {
            root = new Task();  // see identity trick in fireStructureChanged
            refreshSnapshot();
        }
        return root;
    }

    public List getTasks() {
        return getRoot().getSubtasks();
    }

    public synchronized void addTaskListener(TaskListener l) {
        // we do not add directly to peer
        // because we filter fired events
        assert l != null;
        assert listeners.contains(l) == false; // missing removeListener ?
        ArrayList clisteners = new ArrayList(listeners);
        clisteners.add(l);
        if (clisteners.size() == 1) {
            handler = new EventHandler();
            peer.addTaskListener(handler);
        }
        listeners = clisteners;
    }

    public synchronized void removeTaskListener(TaskListener l) {
        ArrayList clisteners = new ArrayList(listeners);
        clisteners.remove(l);
        if (clisteners.size() == 0) {
            peer.removeTaskListener(handler);  // nobody is interested in changes
            handler = null;
        }
        listeners = clisteners;
    }

    /**
     * Notify that it's not needed anymore.
     */
    synchronized void byebye() {
        if (listeners.size() > 0) {
//            System.err.println("Leaked listeners: " + listeners);
        }
        listeners.clear();
        peer.removeTaskListener(handler);  // nobody is interested in changes
        handler = null;
    }

    private void fireStructureChanged(Task task) {
//        if (silent) return;  // the event comes from root.updatedStructure

        Task context = (peer.getRoot() == task) ? root : task;
        Iterator it = listeners.iterator();
        while (it.hasNext()) {
            TaskListener listener = (TaskListener) it.next();
            listener.structureChanged(context);
        }
    }

    private void refreshSnapshot() {
        root.clear();
        loadSourceTasks(peer.getRoot());
    }

    private void loadSourceTasks(Task parent) {
        if (parent.hasSubtasks() == false) return;
        Iterator it = parent.subtasksIterator();
        while (it.hasNext()) {
            Task task = (Task) it.next();
            if (task.getSeed() instanceof SourceTaskProvider) {
                // loosing identity here
                Task clone = task.cloneTask();
                root.addSubtask(clone, true);
            } else {
                // There are those nesting category tasks
                // if grouping treshold is matched.
                // Eliminate them to sustain list assumption.
                if (task.hasSubtasks()) {
                    loadSourceTasks(task);  // recursion
                }
            }
        }
    }

    /**
     * Forward filtered events
     */
    private class EventHandler implements TaskListener {
        public void selectedTask(Task t) {
            if (getTasks().contains(t)) {
                Iterator it = listeners.iterator();
                while (it.hasNext()) {
                    TaskListener listener = (TaskListener) it.next();
                    listener.selectedTask(t);
                }
            }
        }

        public void warpedTask(Task t) {
            assert false : "Not implemented";
        }

        public void addedTask(Task t) {
            if (t.getSeed() instanceof SourceTaskProvider) {
                try {
                    silent = true;
                    // loosing identity here
                    Task clone = t.cloneTask();
                    getRoot().addSubtask(clone, true);
                } finally {
                    silent = false;
                }
                Iterator it = listeners.iterator();
                while (it.hasNext()) {
                    TaskListener listener = (TaskListener) it.next();
                    listener.addedTask(t);
                }
            } else if (t.hasSubtasks()) {
                // category nodes
                Iterator it = t.subtasksIterator();
                while (it.hasNext()) {
                    Task task = (Task) it.next();
                    addedTask(task);  // recursion
                }
            }
        }

        public void removedTask(Task pt, Task t) {
            if (t.getSeed() instanceof SourceTaskProvider) {
                try {
                    // find pairing task by key identity
                    Object key = t.getKey();
                    Task remove = null;
                    Iterator it = getTasks().iterator();
                    while (it.hasNext()) {
                        Task task = (Task) it.next();
                        if (key == task.getKey()) {
                            remove = task;
                            break;
                        }
                    }

                    if (remove != null) {
                        silent = true;
                        getRoot().removeSubtask(t);
                    }
                } finally {
                    silent = false;
                }
                Iterator it = listeners.iterator();
                while (it.hasNext()) {
                    TaskListener listener = (TaskListener) it.next();
                    listener.removedTask(null, t); // TODO cannot find the parent of t
                }
            } else if (t.hasSubtasks()) {
                // category nodes
                Iterator it = t.subtasksIterator();
                while (it.hasNext()) {
                    Task task = (Task) it.next();
                    
                    // TODO cannot find the parent of task
                    removedTask(null, task);  // recursion
                }
            }
        }

        public void structureChanged(Task t) {
            // need to build it again
            try {
                silent = true;
                refreshSnapshot();
            } finally {
                silent = false;
            }
            fireStructureChanged(t);
        }
    }

}
