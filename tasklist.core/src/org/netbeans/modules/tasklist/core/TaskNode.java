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


import java.awt.Image;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.StringWriter;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.modules.tasklist.core.filter.Filter;
import org.netbeans.modules.tasklist.core.filter.FilterAction;
import org.openide.ErrorManager;
import org.openide.actions.CopyAction;
import org.openide.actions.CutAction;
import org.openide.actions.DeleteAction;
import org.openide.actions.PasteAction;
import org.openide.actions.PropertiesAction;
import org.openide.loaders.InstanceSupport;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.nodes.NodeMemberEvent;
import org.openide.nodes.Sheet;

import org.openide.nodes.Children;
import org.openide.nodes.PropertySupport;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.util.WeakListeners;
import org.openide.util.actions.SystemAction;
import org.openide.util.datatransfer.ExTransferable;
import org.openide.util.datatransfer.PasteType;

public class TaskNode extends AbstractNode {
    private static final Logger LOGGER = TLUtils.getLogger(TaskNode.class);
    
    static {
        LOGGER.setLevel(Level.OFF);
    }

    protected final Task item;
    private Monitor monitor;

    /**
     * Leaf
     */
    public TaskNode(Task item) {
        super(Children.LEAF);
        this.item = item;
        assert item.getList() != null;
        init();
    }

    /**
     * On Task.class query returns associated Task.
     */
    public Node.Cookie getCookie(Class type) {
        if (type == Task.class) {
            return item;
        } else {
            return super.getCookie(type);
        }
    }

    /**
     * Non-leaf/parent
     */
    public TaskNode(Task item, Iterator subtasks) {
        super(new TaskChildren(item));
        this.item = item;
        assert item.getList() != null;
        init();
    }

    private void init() {
        setName(item.getSummary());
        monitor = new Monitor();
        item.getList().addTaskListener(monitor);
        item.addPropertyChangeListener(monitor);
        updateDisplayStuff();
        getCookieSet().add(new InstanceSupport.Instance(item));
        
        // Make reorderable:
        //TODO getCookieSet().add(new ReorderMe ());
    }

    public TaskChildren getTaskChildren() {
        return (TaskChildren) getChildren();
    }
     
    // Handle cloning specially (so as not to invoke the overhead of FilterNode):
    public Node cloneNode () {
        if (item.hasSubtasks()) {
            return new TaskNode(item, item.subtasksIterator());
        } else {
            return new TaskNode(item);
        }
    }

    protected final void updateDisplayStuff() {
        setDisplayName(item.getSummary());
        updateIcon();
    }

    protected void updateIcon() {
        // This lightbulb icon is really ugly, get something
        // better!
        setIconBase((item.getAction() != null) ?
		    "org/netbeans/modules/tasklist/core/lightbulb" : // NOI18N
                    "org/netbeans/modules/tasklist/core/task"); // NOI18N
    }
    
    public Image getIcon(int type) {
	if (item.getIcon() != null) {
	    return item.getIcon();
	} else {
	    return super.getIcon(type);
	}
    }

    public Image getOpenedIcon(int type) {
	if (item.getIcon() != null) {
	    return item.getIcon();
	} else {
	    return super.getOpenedIcon(type);
	}
    }

    public HelpCtx getHelpCtx() {
        return new HelpCtx(TaskNode.class);
    }
    
    protected SystemAction[] createActions() {
	// TODO Perform lookup here to compute an aggregate
	// menu from other modules as well. But how do we determine
	// order? I think NetBeans 4.0's actions re-work will have
	// some better support for integrating context menus so I won't
	// try to be too clever here...

        return new SystemAction[] {
            null,
            SystemAction.get(FilterAction.class),
            null,
            SystemAction.get(ExpandAllAction.class),
            null,
            SystemAction.get(CutAction.class),
            SystemAction.get(CopyAction.class),
            SystemAction.get(PasteAction.class),
            null,
            SystemAction.get(DeleteAction.class),
            null,
            SystemAction.get(PropertiesAction.class),
        };
    }

    public void destroy() throws IOException {
        item.removePropertyChangeListener(monitor);
        ObservableList tl = item.getList();
        tl.removeTaskListener(monitor);
        item.getParent().removeSubtask(item);
        super.destroy();
    }
    
    public boolean canDestroy() {
        return true;
    }
    
    /** Creates properties.
     */
    protected Sheet createSheet() {
        Sheet s = Sheet.createDefault();
        Sheet.Set ss = s.get(Sheet.PROPERTIES);
        
        try {
            Node.Property p;
            p = new PropertySupport.Reflection(item, String.class, "getSummary", "setSummary"); // NOI18N
            p.setName(TaskListView.PROP_TASK_SUMMARY);
            p.setDisplayName(NbBundle.getMessage(TaskNode.class, "Description")); // NOI18N
            p.setShortDescription(NbBundle.getMessage(TaskNode.class, "DescriptionHint")); // NOI18N
            ss.put(p);
        } catch (NoSuchMethodException nsme) {
            ErrorManager.getDefault().notify(nsme);
        }
        return s;
    }
    
    public boolean canRename() {
        return true;
    }

    public void setName(String nue) {
        super.setName(nue);
        if (!nue.equals(item.getSummary())) {
            item.setSummary(nue);
        }
    }
    
    protected void createPasteTypes(Transferable t, List s) {
    }
    
    // Handle copying and cutting specially:
    public boolean canCopy () {
        return true;
    }
    public boolean canCut () {
        return true;
    }

    public Transferable clipboardCopy() throws IOException {
        return new ExTransferable.Single(TaskTransfer.TODO_FLAVOR) {
            protected Object getData() {
                return item.clone();
            }
        };
    }
    
    public Transferable clipboardCut() throws IOException {
        destroy();
        return clipboardCopy();
    }

    /* This isn't ready yet; I need to change the dialog such that it 
       can work as a property sheet (no explicit "ok" action which copies
       GUI values into the todo item object.)
    // Permit user to customize whole node at once (instead of per-property):
    public boolean hasCustomizer () {
        return true;
    }
    public Component getCustomizer () {
        return new NewTodoItemPanel(this);
    }
    */

    /*    
    public Node.Handle getHandle() {
        return new TodoItemHandle(item);
    }
    */
    
    // Permit node to be reordered (you may also want to put
    // MoveUpAction and MoveDownAction on the subnodes, if you can,
    // but ReorderAction on the parent is enough):
            /*
    private class ReorderMe extends Index.Support {

        public Node[] getNodes () {
            return TaskNode.this.getChildren().getNodes();
        }

        public int getNodesCount () {
            return getNodes().length;
        }

        // This assumes that there is exactly one child node per key.
        // If you are using e.g. Children.Array, you can use shortcut implementations
        // of the Index cookie.
        public void reorder (int[] perm) {
            // Remember: {2, 0, 1} cycles three items forwards.
            List old = TaskNode.this.getTaskChildren().myKeys;
            if (list.size () != perm.length) {
                throw new IllegalArgumentException();
            }
            List nue = new ArrayList(perm.length);
            for (int i = 0; i < perm.length; i++)
                nue.set (i, old.get(perm[i]));
            TaskNode.this.getTaskChildren().setKeys(nue);



	    // Remember: {2, 0, 1} cycles three items forwards.
	    MyDataElement[] items = model.getChildElements();
	    if (items.length != perm.length) throw new IllegalArgumentException();
	    MyDataElement[] nue = new MyDataElement[perm.length];
	    for (int i = 0; i < perm.length; i++) {
		nue[i] = old[perm[i]];
            }
            // Should trigger an automatic child node update because the children
            // should be listening:
            model.setChildElements(nue);
        }
    }
            */

    /** Given a root node, locate the node below it which represents
     *  the given todoitem.
     */
    public static Node find(Node root, Task target) {
        Task item = getTask(root);
        if (item == target) {
            // Done - you called this method on the node which contains the item
            return root;
        }
        
        // First we've gotta locate the ancestry of the todo item,
        // such that we can descend the node hierarchy and know which
        // todoitem to look for (which ancestor) to pursue - that way
        // we don't have to look at the whole tree of nodes.
        // (Of course, I suspect that the tree will be really flat - most
        // todo item will be at the toplevel, at least the way -I- use
        // the todowindow - but of course other users may use more of
        // a hierarchical approach and then this will really help)

        // Find parent children objects
        Task p = target;
        LinkedList ancestry = new LinkedList();
        while ((p != null) && (p != item)) {
            ancestry.addFirst(p);
            p = p.getParent();
        }
        
        Node n = root;
        ListIterator it = ancestry.listIterator();
        while (it.hasNext()) {
            Task parent = (Task)it.next();
            // Locate this parent
            org.openide.nodes.Children c = n.getChildren();
            Node[] nc = c.getNodes();
            for (int i = 0; i < nc.length; i++) {
                n = nc[i];
                if (getTask(n) == parent) {
                    break;
                }
            }
        }
        if (getTask(n) == target) {
            return n;
        } else {
            return null;
        }
    }

    /** Find the Task corresponding to a given node, or null
        if this node does not represent a task */
    public static Task getTask(Node n) {
        if (n == null) {
            return null;
        }
        return (Task) n.getCookie(Task.class);
    }
    
    /** Find the TaskNode corresponding to a given node, or null
        if this node does not represent a TaskNode */
    public static TaskNode getTaskNode(Node n) {
        if (n == null) {
            return null;
        }
        if (n instanceof TaskNode) {
            return (TaskNode)n;
        } else if (n instanceof FilterTaskNode) {
            n = ((FilterTaskNode)n).getOriginal();
            if (n instanceof TaskNode) {
                return (TaskNode)n;
            }
        }
        return null;
    }

    // TaskListener implementation ~~~~~~~~~~~~~~~~~~~~~~

    private class Monitor implements TaskListener, PropertyChangeListener {
        public void selectedTask(Task t) {
            // it's view job
        }

        public void warpedTask(Task t) {
            // it's view job
        }

        public void addedTask(Task t) {
            if (t.getParent().getKey() == item.getKey()) {
                // Special case -- we've made a leaf into one containing children!
                Children c = getChildren();
                if (c == Children.LEAF) {
                    assert item.hasSubtasks();
                    // XXX This seems to get called more frequently than is necessary!
                    setChildren(new TaskChildren(item));
                }
            }
        }

        public void removedTask(Task pt, Task t) {
            // children's job
        }

        public void structureChanged(Task t) {
            if (t.getKey() == item.getKey()) {
                // Special case -- we've made a leaf into one containing children!
                Children c = getChildren();
                if ((c == Children.LEAF) && (item.hasSubtasks())) {
                    // XXX This seems to get called more frequently than is necessary!
                    setChildren(new TaskChildren(item));
                }
            }
        }

        public void propertyChange(PropertyChangeEvent evt) {
            // Some aspects of the module may have changed. Redisplay everything.
            updateDisplayStuff();
            firePropertyChange(evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
        }

    }
}

