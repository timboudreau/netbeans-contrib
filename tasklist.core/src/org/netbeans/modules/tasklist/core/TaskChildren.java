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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import org.openide.nodes.Node;
import org.openide.nodes.Children;


/** Children object for the Task list; used to track
 * TaskList modifications and update nodes appropriately.
 * @author Tor Norbye */
public class TaskChildren extends Children.Keys implements PropertyChangeListener {
    
    /** Optional holder for the keys, to be used when changing them dynamically. */
    protected List myKeys;
    private Task parent;
    
    public TaskChildren(Task parent) {
        myKeys = null;
        this.parent = parent;
    }
    
    private void refreshKeys() {
        List subtasks = parent.getSubtasks();
        if (subtasks == null) {
            setKeys(Collections.EMPTY_SET);
            return;
        }
        myKeys = new LinkedList();
        ListIterator it = subtasks.listIterator();
        while (it.hasNext()) {
            Task item = (Task)it.next();
            myKeys.add(item);
        }
        // XXX couldn't I just do
        //   setKeys(parent.getSubtasks()) ?? Check if this method
        // clones the list... if it doesn't I can do it
        setKeys(myKeys);
    }

    /** Called when the parent node is expanded; now we need
     * to create nodes for the children. */    
    protected void addNotify() {
        super.addNotify();
        parent.addPropertyChangeListener(this);
        refreshKeys();
    }
    
    /** Called when the parent node is collapsed: cleanup */    
    protected void removeNotify() {
        myKeys = null;
        parent.removePropertyChangeListener(this);
        setKeys(Collections.EMPTY_SET);
        super.removeNotify();
    }
    
    /** Create nodes for the specified key object (a task)
     * @param key The task used as a parent key
     * @return Node for the key task's children */    
    protected Node[] createNodes(Object key) {
        // interpret your key here...usually one node generated, but could be zero or more
        //  return new Node[] { new TodoNode((MyParameter) key) };
        //return new Node[] { new TodoNode(key) };
        Task item = (Task)key;
        return item.createNode();
    }
    
    /** Reacts to changes */
    public void propertyChange(PropertyChangeEvent ev) {
        if (Task.PROP_CHILDREN_CHANGED == ev.getPropertyName()) { // interned
            refreshKeys();
        }
    }
    
    /** Provide the node so we can expand it */
    Node findNode() {
        return getNode();
    }
}
