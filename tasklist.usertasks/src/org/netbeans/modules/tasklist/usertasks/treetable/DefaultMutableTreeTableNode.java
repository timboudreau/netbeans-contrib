package org.netbeans.modules.tasklist.usertasks.treetable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

/**
 * Default mutable TT node
 * 
 * @author tl
 */
public class DefaultMutableTreeTableNode extends DefaultMutableTreeNode 
        implements TreeTableNode {
    private List<Object> values;
    
    /**
     * Creates a tree node that has no parent and no children, but which
     * allows children.
     */
    public DefaultMutableTreeTableNode() {
	super(null);
    }

    /**
     * Creates a tree node with no parent, no children, but which allows 
     * children, and initializes it with the specified user object.
     * 
     * @param userObject an Object provided by the user that constitutes
     *                   the node's data
     */
    public DefaultMutableTreeTableNode(Object userObject) {
	super(userObject, true);
    }

    /**
     * Creates a tree node with no parent, no children, initialized with
     * the specified user object, and that allows children only if
     * specified.
     * 
     * @param userObject an Object provided by the user that constitutes
     *        the node's data
     * @param allowsChildren if true, the node is allowed to have child
     *        nodes -- otherwise, it is always a leaf node
     */
    public DefaultMutableTreeTableNode(Object userObject, boolean allowsChildren) {
	super(userObject, allowsChildren);
    }
    
    public void setValueAt(Object aValue, int column) {
        if (values == null)
            values = new ArrayList<Object>();
        
        while (values.size() <= column) {
            values.add(null);
        }
        values.set(column, aValue);
    }        

    public Object getValueAt(int column) {
        if (values == null)
            return null;
        if (values.size() > column)
            return values.get(column);
        else
            return null;
    }    

    public boolean isCellEditable(int column) {
        return false;
    }
    
    /**
     * Sorts children nodes
     *
     * @param c a comparator for nodes comparing
     */
    @SuppressWarnings("unchecked") 
    public void sort(final Comparator<TreeNode> c) {
        if (children == null)
            return;
        
        // children is defined as Vector. This results in an "unchecked call"
        // warning
        Collections.sort(children, c);
        
        for (int i = 0; i < getChildCount(); i++) {
            TreeNode tn = getChildAt(i);
            ((DefaultMutableTreeTableNode) tn).sort(c);
        }
    }
}
