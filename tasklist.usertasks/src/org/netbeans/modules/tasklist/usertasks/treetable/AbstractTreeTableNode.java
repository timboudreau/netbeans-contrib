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

package org.netbeans.modules.tasklist.usertasks.treetable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import javax.swing.tree.TreeNode;

/**
 * Abstract node for a TreeTable
 */
public abstract class AbstractTreeTableNode implements TreeTableNode {
    /** could be used to show that this node has no children */
    protected static TreeTableNode[] EMPTY_CHILDREN = {};
    
    protected TreeTableNode parent;
    
    /** children of this node. null means "not yet loaded" */
    protected TreeTableNode children[];
    
    /** 
     * Creates a new instance of AbstractTreeTableNode 
     *
     * @param parent parent of this node or null if this node is a root
     */
    public AbstractTreeTableNode(TreeTableNode parent) {
        this.parent = parent;
    }
    
    /**
     * Should load the children in the field <code>children</code>
     */
    protected abstract void loadChildren();
    
    /**
     * Returns an array with children of this node
     *
     * @return array with children
     */
    public TreeTableNode[] getChildren() {
        if (children == null)
            loadChildren();
        return children;
    }

    public boolean isCellEditable(int column) {
        return false;
    }

    public TreeNode getChildAt(int childIndex) {
        return getChildren()[childIndex];
    }

    public int getChildCount() {
        return getChildren().length;
    }

    public TreeNode getParent() {
        return parent;
    }

    public int getIndex(TreeNode node) {
        TreeTableNode[] ch = getChildren();
        for (int i = 0; i < ch.length; i++) {
            if (ch[i] == node)
                return i;
        }
        return -1;
    }

    public boolean getAllowsChildren() {
        return true;
    }

    public boolean isLeaf() {
        return false;
    }

    public Enumeration children() {
        return Collections.enumeration(Arrays.asList(getChildren()));
    }

    /**
     * todo
     */
    public void refreshChildren() {
        this.children = null;
    }
    
    /**
     * Returns path from this node to the root 
     *
     * @return path to the root
     */
    public TreeTableNode[] getPathToRoot() {
        List path = new ArrayList();
        TreeTableNode n = this;
        while (n != null) {
            path.add(0, n);
            n = (TreeTableNode) n.getParent();
        }
        return (TreeTableNode[]) path.toArray(new TreeTableNode[path.size()]);
    }
    
    /**
     * Finds the next node that should be selected after this node was
     * deleted
     *
     * @return node to select or null
     */
    public TreeTableNode findNextNodeAfterDelete() {
        if (getParent() == null)
            return null;
        if (getParent().getChildCount() == 1)
            return (TreeTableNode) getParent();
        int index = getParent().getIndex(this);
        if (index == getParent().getChildCount() - 1)
            return (TreeTableNode) getParent().getChildAt(index - 1);
        else
            return (TreeTableNode) getParent().getChildAt(index + 1);
    }
}
