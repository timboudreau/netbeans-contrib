package com.sun.jbi.sapbc.sapwsdlgenerator;

import java.util.Iterator;

/**
 * Defines the requirements for an object that can be used as a node in
 * the browse tree of a SAPObjectBrowser.
 *
 * @author Noel Ang (noel.ang@sun.com)
 */
public interface BrowseTreeNode {
    
    /**
     ** Returns the name of the node.
     */
    String getName();
    
    /**
     * Returns the number of child BrowseTreeNode contained by the node.
     */
    int childCount();
    
    /**
     * Returns an Iterator over the children of the node.
     */
    Iterator<BrowseTreeNode> children();
    
    /**
     * Returns the parent of the node.
     */
    BrowseTreeNode parent();
    
    /**
     * Indicates whether the node is a leaf node. A non-leaf node does not
     * necessarily mean that the node has chldren.
     */
    boolean isLeaf();
}
