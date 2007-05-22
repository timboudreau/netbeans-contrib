package com.sun.jbi.sapbc.sapwsdlgenerator;

import java.util.Collections;
import java.util.Iterator;

/**
 * Represents BAPI and RFC objects.  These are leaf (terminal) BrowseTreeNodes.
 *
 * @author Noel Ang (noel.ang@sun.com)
 */
public class BapiNode implements BrowseTreeNode {
    
    public BapiNode(String name) {
        mName = name;
    }
    
    public String getName() {
        return mName;
    }

    public int childCount() {
        return 0;
    }

    public Iterator<BrowseTreeNode> children() {
        // Leaf; no children ever.
        return (Iterator<BrowseTreeNode>) Collections.EMPTY_LIST.listIterator();
    }

    public synchronized BrowseTreeNode parent() {
        return mParent;
    }
    
    public boolean isLeaf() {
        return true;
    }
    
    /**
     * Makes the specified node the parent of this node.
     *
     * @param node The node to make a parent of this node.
     *
     * @throws IllegalArgumentException if the specified node is this node.
     */
    public synchronized void setParent(BrowseTreeNode node) {
        if (this == node) {
            throw new IllegalArgumentException("cannot set myself as my parent"); // TODO: localize
        }
        mParent = node;
    }

    private final String mName;
    private BrowseTreeNode mParent;
}
