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
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * This "advanced" class provides filtering and sorting of nodes
 */
public abstract class AdvancedTreeTableNode extends AbstractTreeTableNode {
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
     * Iterator over the objects of children nodes. The filter should not
     * be yet applied.
     *
     * @return the iterator
     */
    public abstract Iterator getChildrenObjectsIterator();
    
    /**
     * Creates a children node
     *
     * @param child child's object
     */
    public abstract AdvancedTreeTableNode createChildNode(Object child);
    
    /**
     * Filtering
     *
     * @param child a child object
     */
    public boolean accept(Object child) {
        if (getFilter() != null)
            return getFilter().accept(child);
        else
            return true;
    }
    
    protected void loadChildren() {
        List ch = new ArrayList();
        Iterator it = getChildrenObjectsIterator();
        while (it.hasNext()) {
            ch.add(it.next());
        }
        
        // filtering
        if (getFilter() != null) {
            it = ch.iterator();
            while (it.hasNext()) {
                Object obj = it.next();
                if (!accept(obj))
                    it.remove();
            }
        }
        
        children = new AdvancedTreeTableNode[ch.size()];
        for (int i = 0; i < ch.size(); i++) {
            children[i] = createChildNode(ch.get(i));
        }

        // sorting
        if (getComparator() != null)
            Arrays.sort(children, getComparator());
    }

    public void refreshChildren() {
        if (children != null) {
            for (int i = 0; i < children.length; i++) {
                ((AdvancedTreeTableNode) children[i]).destroy();
            }
            this.children = null;
        }
        model.fireTreeStructureChanged(model, this.getPathToRoot());
    }

    /**
     * Will be called after removing the node from the hierarchy
     */
    public void destroy() {
        this.parent = null;
        if (children != null) {
            for (int i = 0; i < children.length; i++) {
                ((AdvancedTreeTableNode) children[i]).destroy();
            }
        }
    }
}
