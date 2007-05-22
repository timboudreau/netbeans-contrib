package com.sun.jbi.sapbc.sapwsdlgenerator;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Represents a category for separating BAPI/RFC objects.
 *
 * @author Noel Ang (noel.ang@sun.com)
 */
public class ApplicationNode implements BrowseTreeNode {
    
    public ApplicationNode(String name) {
        mName = name;
    }

    public String getName() {
        return mName;
    }
    
    public int childCount() {
        return mChildren.size();
    }

    public Iterator children() {
        return Collections.unmodifiableList(mChildren).listIterator();
    }

    public synchronized BrowseTreeNode parent() {
        return mParent;
    }
    
    public boolean isLeaf() {
        return false;
    }
    
    /**
     * Add a BrowseTreeNode object as a child of this node.
     *
     * @param node The node to designate as a child of this node.
     *
     * @returns true if the node is successfully added as a child.  If
     *          <code>node</code> is already a child, return false.
     *
     * @throws IllegalArgumentException if <code>node</code> is this node.
     */
    public boolean addChild(BrowseTreeNode node) {
        if (node == null) {
            return false;
        }
        if (mParent == node) {
            throw new IllegalArgumentException("cannot set parent as my child"); // TODO: localize
        }
        synchronized (mChildren) {
            if (!mChildren.contains(node)) {
                return mChildren.add(node);
            } else {
                return false;
            }
        }
    }
    
    /**
     * Remove one of this node's child nodes.
     *
     * @param node The child node to remove.
     *
     * @returns true if the specified node is successfully removed.
     */
    public boolean removeChild(BrowseTreeNode node) {
        synchronized (mChildren) {
            return mChildren.remove(node);
        }
    }
    
    /**
     * Makes the specified node the parent of this node.
     *
     * @param node The node to make a parent of this node.
     *
     * @throws IllegalArgumentException if the specified node is this node, or
     *         if the specified node is a child of this node.
     */
    public synchronized void setParent(BrowseTreeNode node) {
        if (this == node) {
            throw new IllegalArgumentException("cannot set myself as my parent"); // TODO: localize
        }
        synchronized (mChildren) {
            if (mChildren.contains(node)) {
                throw new IllegalArgumentException("cannot set a child as my parent"); // TODO: localize
            }
        }
        mParent = node;
    }

    private final String mName;
    private final List<BrowseTreeNode> mChildren = new LinkedList<BrowseTreeNode>();
    private BrowseTreeNode mParent;
}
