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
import java.util.Comparator;

/**
 * This "advanced" class provides filtering and sorting of nodes
 */
public abstract class AdvancedTreeTableNode extends AbstractTreeTableNode {
    protected TreeTableNode[] unfilteredChildren;
    
    /** 
     * Creates a new instance of AdvancedTreeTableNode 
     *
     * @param parent parent of this node or null if this node is a root
     */
    public AdvancedTreeTableNode(TreeTableNode parent) {
        super(parent);
    }

    /**
     * Gets a comparator
     *
     * @return comparator or null
     */
    public Comparator getComparator() {
        if (getParent() instanceof AdvancedTreeTableNode)
            return ((AdvancedTreeTableNode) getParent()).getComparator();
        else
            return null;
    }
    
    /**
     * Gets a filter
     *
     * @return filter or null
     */
    public FilterIntf getFilter() {
        if (getParent() instanceof AdvancedTreeTableNode)
            return ((AdvancedTreeTableNode) getParent()).getFilter();
        else
            return null;
    }

    /**
     * todo
     */
    protected abstract void loadUnfilteredChildren();

    /**
     * Should load the children in the field <code>children</code>
     */
    protected void loadChildren() {
        if (unfilteredChildren == null) {
            loadUnfilteredChildren();
        }
        
        // filtering
        FilterIntf filter = getFilter();
        if (filter != null) {
            ArrayList fc = new ArrayList();
            for (int j = 0; j < children.length; j++) {
                if (filter.accept(unfilteredChildren[j])) {
                    fc.add(children[j]);
                }
            }
            children = (TreeTableNode[]) fc.toArray(
                new TreeTableNode[fc.size()]);
        } else {
            children = unfilteredChildren;
        }
        
        // sorting
        if (getComparator() != null)
            Arrays.sort(children, getComparator());
    }

    public void refreshChildren() {
        this.children = null;
        this.unfilteredChildren = null;
    }
}
