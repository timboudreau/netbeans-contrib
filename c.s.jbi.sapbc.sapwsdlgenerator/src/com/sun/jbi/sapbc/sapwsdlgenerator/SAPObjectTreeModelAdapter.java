package com.sun.jbi.sapbc.sapwsdlgenerator;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import org.openide.util.NbBundle;

/**
 * Swing TreeModel interface for BrowseTreeNode hierarchies.
 *
 * @author Noel Ang (noel.ang@sun.com)
 */
public class SAPObjectTreeModelAdapter implements TreeModel {

    /**
     * Create a SAPObjectTreeModelAdapter instance.
     *
     * @param nodes Iteration of BrowseTreeNode objects to process (as root
     *              node(s) of this TreeModel)
     *
     * @throws NullPointerException if nodes is <code>null</code>.
     */
    public SAPObjectTreeModelAdapter(Iterator<BrowseTreeNode> nodes) {
        if (nodes == null) {
            throw new NullPointerException("nodes");
        }
        mRoot = new TreeNodeImpl(null,
                NbBundle.getMessage(getClass(), "SAPObjectTreeModelAdapter.Root_node_name"),
                false,
                nodes);
    }

    public Object getRoot() {
        // Yes, this looks weird, but it's the contract.
        return (mRoot.getChildCount() != 0 ? mRoot : null);
    }

    public Object getChild(Object parent, int index) {
        TreeNode node = (TreeNode) parent;
        
        // IndexOutOfBoundsException not part of the contract.
        if (index < 0 || index >= node.getChildCount()) {
            return null;
        }
        
        return node.getChildAt(index);
    }

    public int getChildCount(Object parent) {
        TreeNode node = (TreeNode) parent;
        return node.getChildCount();
    }

    public boolean isLeaf(Object node) {
        TreeNode aNode = (TreeNode) node;
        return aNode.isLeaf();
    }

    public void valueForPathChanged(TreePath path, Object newValue) {
        if (path == null) {
            return;
        }
        
        TreeNode oldValue = walk(path);
        if (oldValue != null) {
            if (!oldValue.equals(newValue)) {
                replace(oldValue, (TreeNode) newValue);
            }
        }
    }

    public int getIndexOfChild(Object parent, Object child) {
        if (parent == null) {
            return -1;
        }
        if (child == null) {
            return -1;
        }
        TreeNode node = (TreeNode) parent;
        return node.getIndex((TreeNode) child);
    }

    public void addTreeModelListener(TreeModelListener l) {
        if (l != null) {
            synchronized (mListeners) {
                mListeners.add(l);
            }
        }
    }

    public void removeTreeModelListener(TreeModelListener l) {
        if (l != null) {
            synchronized (mListeners) {
                mListeners.remove(l);
            }
        }
    }

    /**
     * Traverses the given TreePath and returns the last part of the path,
     * <em>provided that the given TreePath exists in this TreeModel</em>.
     * For the specified TreePath to exist in this model, the first part of
     * the TreePath must equivalent to the root TreeNode of this model, and
     * all subsequent parts <code>p1 ... p<em>n</em></code>> in the TreePath
     * must exist in this model such that they are the child nodes of nodes
     * <code>root, p1, ... p(<em>n-1</em>)</code>, respectively.
     *
     * @param path The path to traverse
     *
     * @return If the specified TreePath is a real path in this TreeModel, then
     *         the last part of the path is returned, otherwise <code>null</code>
     *         is returned.
     */
    private TreeNode walk(TreePath path) {
        final int depth = path.getPathCount();
        TreeNode myNode = mRoot;
        TreeNode pathNode = (depth > 0 ? (TreeNode) path.getPathComponent(0) : null);
        if (!myNode.equals(pathNode)) {
            return null;
        }
        for (int i = 1; i < depth && pathNode != null; ++i) {
            pathNode = (TreeNode) path.getPathComponent(i);
            int myNextNode = myNode.getIndex(pathNode);
            if (myNextNode != -1) {
                myNode = myNode.getChildAt(myNextNode);
            } else {
                pathNode = null;
            }
        }
        return pathNode;
    }
    
    /**
     * Replaces <code>oldValue</code> with <code>newValue</code>.
     *
     * @param oldValue
     * @param newValue
     *
     * @throws IllegalArgumentException if replacement cannot be effected because
     *         a reference to <code>oldValue</code>'s parent is not available
     *         (TreeNode.getParent returns <code>null</code>).
     */
    private void replace(TreeNode oldValue, TreeNode newValue) {
        TreeNodeImpl parent = (TreeNodeImpl) oldValue.getParent();
        if (parent == null) {
            throw new IllegalArgumentException(
                    "oldValue has no parent; no anchor to effect replacement with newValue.");
        }
        int childIndex = parent.getIndex(oldValue);
        parent.setChildAt(childIndex, newValue);
    }
    
    private final TreeNode mRoot;
    private final Set<TreeModelListener> mListeners = new HashSet<TreeModelListener>();
}
