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


/**
 * Children object for the Task; used to track
 * TaskList modifications and update nodes appropriately.
 *
 * @author Tor Norbye
 * @author Petr Kuzel
 */
public class TaskChildren extends Children.Keys {
    
    /** Optional holder for the keys, to be used when changing them dynamically. */
    private List myKeys;
    private final Task parent;
    private Monitor monitor;

    public TaskChildren(Task parent) {
        this.parent = parent;
    }
    
    private void refreshKeys() {
        if (parent.hasSubtasks() == false) {
            setKeys(Collections.EMPTY_SET);
        } else {
            setKeys(parent.getSubtasks());
        }
    }

    /**
     * Called when the parent node is expanded; now we need
     * to create nodes for the children.
     */
    protected void addNotify() {
        super.addNotify();
        assert monitor == null : "Dangling addNotify()"; // NOI18N
        monitor = new Monitor();
        parent.getList().addListener(monitor);
        refreshKeys();
    }
    
    /** Called when the parent node is collapsed: cleanup */    
    protected void removeNotify() {
        myKeys = null;
        assert monitor != null : "Dangling removeNotify()"; // NOI18N
        parent.getList().removeListener(monitor);
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

        Task item = (Task)key;
        return item.createNode(); // XXX I do not like this model-view 1:1
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

        public void removedTask(Task t) {
            if (t.getParent() == parent) {
                refreshKeys();
            }
        }

        public void changedTask(Task t) {
            // it's node job
        }

        public void structureChanged(Task t) {
            if (t == parent) {
                refreshKeys();
            }
        }
    }
}
