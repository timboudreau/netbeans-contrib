/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
 */


package org.netbeans.modules.tasklist.core;

import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;

import java.util.Collections;

/**
 * Node visualization of TaskList. It creates children
 * by taking default node from contained tasks.
 * 
 * @author Petr Kuzel
 */
public final class TaskListNode extends AbstractNode {

    /**
     * Creates plain tasklist node. Properties that cannot
     * be derrived from passed tasklist should be provided by
     * client. It covers displayName etc.
     *
     * @param tasklist to be visualized never <code>null</code>
     */
    public TaskListNode(ObservableList tasklist) {
        super(new TaskListChildren(tasklist));
//        TaskListChildren list = (TaskListChildren) getChildren();
//        list.setNodeFactory(nodeFactory);
    }

    /** Creates custom child nodes for TaskListNode */
    public static interface NodeFactory {

        /** Default task.createNode() */
        Node createNode(Object task);
    }

    static class TaskListChildren extends Children.Keys implements TaskListener {

        private ObservableList list;
        private NodeFactory nodeFactory;

        private TaskListChildren(ObservableList list) {
            assert list != null;
            this.list = list;
        }

        protected void addNotify() {
            super.addNotify();
            setKeys(list.getTasks());
            list.addTaskListener(this);
        }

        protected void removeNotify() {
            list.removeTaskListener(this);
            setKeys(Collections.EMPTY_SET);
            super.removeNotify();
        }

        protected Node[] createNodes(Object key) {
            Task task = (Task) key;
            Node[] nodes;
            if (nodeFactory == null) {
                nodes = task.createNode();
            } else {
                nodes = new Node[] {nodeFactory.createNode(task)};
            }
            return nodes;
        }

        public void setNodeFactory(NodeFactory nodeFactory) {
            this.nodeFactory = nodeFactory;
        }

        // TaskListener implementation ~~~~~~~~~~~~~~~

        public void selectedTask(Task t) {
        }

        public void warpedTask(Task t) {
        }

        public void addedTask(Task t) {
            setKeys(list.getTasks());
        }

        public void removedTask(Task pt, Task t) {
            setKeys(list.getTasks());
        }

        public void structureChanged(Task t) {
            setKeys(list.getTasks());
        }


    }
}
