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
    protected AdvancedTreeTableNode[] unfilteredChildren;
    protected FilterIntf filter;
    protected Comparator comparator;
    protected DefaultTreeTableModel model;
    protected Object object;
    
    /** 
     * Creates a new instance of AdvancedTreeTableNode 
     *
     * @param model tree table model this node belongs to
     * @param parent parent of this node or null if this node is a root
     * @param object object associated with this node
     */
    public AdvancedTreeTableNode(DefaultTreeTableModel model, 
        TreeTableNode parent, Object object) {
        super(parent);
        this.model = model;
        this.object = object;
    }

    /**
     * Returns object associated with this node
     *
     * @return object
     */
    public Object getObject() {
        return object;
    }
    
    /**
     * Finds a child with the specified user object
     *
     * @param obj user object
     * @return found node index or -1
     */
    public int getIndexOfObject(Object obj) {
        AdvancedTreeTableNode[] ch = (AdvancedTreeTableNode[]) getChildren();
        for (int i = 0; i < ch.length; i++) {
            if (ch[i].getObject() == obj)
                return i;
        }
        return -1;
    }

    /**
     * Sets new comparator or null
     *
     * @param comparator new comparator
     */
    public void setComparator(Comparator comparator) {
        if (this.comparator == comparator)
            return;
        
        this.comparator = comparator;
        
        refreshChildren();
    }
    
    /**
     * Gets a comparator
     *
     * @return comparator or null
     */
    public Comparator getComparator() {
        return comparator;
    }
    
    /**
     * Gets a filter
     *
     * @return filter or null
     */
    public FilterIntf getFilter() {
        return filter;
    }

    /**
     * Sets new filter
     *
     * @param filter new filter or null
     */
    public void setFilter(FilterIntf filter) {
        this.filter = filter;
        this.children = null;
    }
    
    /**
     * Loads unfiltered children of this node
     */
    protected abstract void loadUnfilteredChildren();

    protected void loadChildren() {
        if (unfilteredChildren == null) {
            loadUnfilteredChildren();
        }
        
        assert unfilteredChildren != null;
        
        // filtering
        FilterIntf filter = getFilter();
        if (filter != null) {
            ArrayList fc = new ArrayList();
            for (int j = 0; j < unfilteredChildren.length; j++) {
                if (filter.accept(((AdvancedTreeTableNode) 
                    unfilteredChildren[j]).getObject())) {
                    fc.add(unfilteredChildren[j]);
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
        if (unfilteredChildren != null) {
            for (int i = 0; i < unfilteredChildren.length; i++) {
                ((AdvancedTreeTableNode) unfilteredChildren[i]).destroy();
            }
        }
        this.children = null;
        this.unfilteredChildren = null;
        model.fireTreeStructureChanged(model, this.getPathToRoot());
    }

    /**
     * Will be called after removing the node from the hierarchy
     */
    public void destroy() {
        this.parent = null;
        if (unfilteredChildren != null) {
            for (int i = 0; i < unfilteredChildren.length; i++) {
                ((AdvancedTreeTableNode) unfilteredChildren[i]).destroy();
            }
        }
    }
}
