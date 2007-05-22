package com.sun.jbi.sapbc.sapwsdlgenerator;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import javax.swing.tree.TreeNode;

/**
 * TreeNode implementation used by SAPObjectTreeModelAdapter for representing
 * BrowseTreeNodes.
 *
 * @author Noel Ang (noel.ang@sun.com)
 */
class TreeNodeImpl implements TreeNode {
    
    /**
     * Creates a TreeNodeImpl instance.
     *
     * @param parent   Parent node for this node. Optional.
     * @param name     Name for this node. Optional.
     * @param isLeaf   Use <code>true</code> if this node is to be a terminal
     *                 node.
     * @param children Iteration of BrowseTreeNodes to make as children of this
     *                 node. Optional (a non-leaf node does not necessarily
     *                 have child nodes, and a leaf node cannot have child
     *                 nodes).
     */
    TreeNodeImpl(TreeNode parent,
                 String name,
                 boolean isLeaf,
                 Iterator<BrowseTreeNode> children) {
        
        mParent = parent;
        mName = (name != null ? name : "");
        mIsLeaf = isLeaf;

        if (!isLeaf && children != null) {
            adapt(children);
        }
    }

    public TreeNode getChildAt(int childIndex) {
        synchronized (mChildren) {
            // Not in the TreeNode contract to ever throw IndexOutOfBoundsException
            if (childIndex < 0 || childIndex >= mChildren.size()) {
                return null;
            }
            return mChildren.get(childIndex);
        }
    }

    public int getChildCount() {
        synchronized (mChildren) {
            return mChildren.size();
        }
    }

    public TreeNode getParent() {
        return mParent;
    }

    public int getIndex(TreeNode node) {
        synchronized (mChildren) {
            return mChildren.indexOf(node);
        }
    }

    public boolean getAllowsChildren() {
        return true;
    }

    public boolean isLeaf() {
        return mIsLeaf;
    }

    public Enumeration children() {
        synchronized (mChildren) {
            return Collections.enumeration(mChildren);
        }
    }

    public String toString() {
        return mName;
    }
    
    /**
     * Set the specified node as the <code>index</code>th child of this node.
     * If a child node already exists at the specified index, it is replaced.
     *
     * @param index Child node position for the specified node, counting from 0.
     * @param node  The node to make as child of this node.
     *
     * @throws IndexOutOfBoundsException if index is outside the range
     *         <code>(0 < index || index >= current child count)</code>.
     */
    void setChildAt(int index, TreeNode node) {
        synchronized (mChildren) {
            if (node != null) {
                mChildren.remove(index);
                mChildren.add(index, node);
            }
        }
    }

    /**
     * Populates this node's children hierarchy with TreeNodes corresponding to
     * the given BrowseTreeNode hierarchy, which is recursively traversed.
     */
    private final void adapt(Iterator<BrowseTreeNode> children) {
        synchronized (mChildren) {
            while (children.hasNext()) {
                BrowseTreeNode node = children.next();
                addChild(new TreeNodeImpl(this, node.getName(), node.isLeaf(), node.children()));
            }
        }
    }

    private void addChild(TreeNode node) {
        if (!mChildren.contains(node)) {
            mChildren.add(node);
        }
    }
    
    private final List<TreeNode> mChildren = new LinkedList<TreeNode>();
    private final TreeNode mParent;
    private final String mName;
    private final boolean mIsLeaf;
}
