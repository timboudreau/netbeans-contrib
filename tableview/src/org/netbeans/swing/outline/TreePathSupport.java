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
/*
 * TreePathSupport.java
 *
 * Created on January 27, 2004, 7:06 PM
 */

package org.netbeans.swing.outline;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.AbstractLayoutCache;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreePath;

/** Manages expanded/collapsed paths for the Outline.  Provides services similar
 * to those JTree implements inside its own class body.  Propagates changes
 * in expanded state to the layout cache.
 *
 * @author  Tim Boudreau
 */
public final class TreePathSupport {
    private OutlineModel mdl;
    private Map expandedPaths = new HashMap();
    private List eListeners = new ArrayList();
    private List weListeners = new ArrayList();
    private AbstractLayoutCache layout;
    
    /** Creates a new instance of TreePathSupport */
    public TreePathSupport(OutlineModel mdl, AbstractLayoutCache layout) {
        this.mdl = mdl;
        this.layout = layout;
    }
    
    public void expandPath (TreePath path) {
        TreeExpansionEvent e = new TreeExpansionEvent (this, path);
        try {
            fireTreeWillExpand(e, true);
            expandedPaths.put(path, Boolean.TRUE);
            layout.setExpandedState(path, true);
            fireTreeExpansion(e, true);
        } catch (ExpandVetoException eve) {
            //do nothing
        }
    }
    
    public void collapsePath (TreePath path) {
        TreeExpansionEvent e = new TreeExpansionEvent (this, path);
        try {
            fireTreeWillExpand(e, false);
            expandedPaths.put(path, Boolean.FALSE);
            layout.setExpandedState(path, false);
            fireTreeExpansion(e, false);
        } catch (ExpandVetoException eve) {
            //do nothing
        }
    }
    
    private void fireTreeExpansion (TreeExpansionEvent e, boolean expanded) {
        int size = eListeners.size();
        
        TreeExpansionListener[] listeners = new TreeExpansionListener[size];
        synchronized (this) {
            listeners = (TreeExpansionListener[]) eListeners.toArray(listeners);
        }
        for (int i=0; i < listeners.length; i++) {
            if (expanded) {
                listeners[i].treeExpanded(e);
            } else {
                listeners[i].treeCollapsed(e);
            }
        }
    }
    
    private void fireTreeWillExpand (TreeExpansionEvent e, boolean expanded) throws ExpandVetoException {
        int size = eListeners.size();
        
        TreeWillExpandListener[] listeners = new TreeWillExpandListener[size];
        synchronized (this) {
            listeners = (TreeWillExpandListener[]) weListeners.toArray(listeners);
        }
        for (int i=0; i < listeners.length; i++) {
            if (expanded) {
                listeners[i].treeWillExpand(e);
            } else {
                listeners[i].treeWillCollapse(e);
            }
        }
    }
    
    public boolean hasBeenExpanded(TreePath path) {
	return (path != null && expandedPaths.get(path) != null);
    }

    /**
     * Returns true if the node identified by the path is currently expanded,
     * 
     * @param path  the <code>TreePath</code> specifying the node to check
     * @return false if any of the nodes in the node's path are collapsed, 
     *               true if all nodes in the path are expanded
     */
    public boolean isExpanded(TreePath path) {
	if(path == null)
	    return false;

	// Is this node expanded?
	Object          value = expandedPaths.get(path);

	if(value == null || !((Boolean)value).booleanValue())
	    return false;

	// It is, make sure its parent is also expanded.
	TreePath parentPath = path.getParentPath();

	if(parentPath != null)
	    return isExpanded(parentPath);
        return true;
    }
    
     protected void removeDescendantToggledPaths(Enumeration toRemove) {
	 if(toRemove != null) {
	     while(toRemove.hasMoreElements()) {
                 TreePath[] descendants = getDescendantToggledPaths(
                    (TreePath) toRemove.nextElement());
                 for (int i=0; i < descendants.length; i++) {
                     expandedPaths.remove(descendants[i]);
                 }
	     }
	 }
     }
     
    protected TreePath[] getDescendantToggledPaths(TreePath parent) {
	if(parent == null)
	    return null;

	ArrayList descendants = new ArrayList();
        Iterator nodes = expandedPaths.keySet().iterator();
        TreePath path;
        while (nodes.hasNext()) {
            path = (TreePath) nodes.next();
            if (parent.isDescendant(path)) {
                descendants.add(path);
            }
        }
        TreePath[] result = new TreePath[descendants.size()];
        return (TreePath[]) descendants.toArray(result);
    }
    
    public boolean isVisible(TreePath path) {
        if(path != null) {
	    TreePath parentPath = path.getParentPath();

	    if(parentPath != null) {
		return isExpanded(parentPath);
            }
	    // Root.
	    return true;
	}
        return false;
    }    
    
    public TreePath[] getExpandedDescendants(TreePath parent) {
        TreePath[] result = new TreePath[0];
	if(isExpanded(parent)) {
            TreePath path;
            Object value;
            List results = null;

            if (!expandedPaths.isEmpty()) {

                Iterator i = expandedPaths.keySet().iterator();

                while(i.hasNext()) {
                    path = (TreePath) i.next();
                    value = expandedPaths.get(path);

                    // Add the path if it is expanded, a descendant of parent,
                    // and it is visible (all parents expanded). This is rather
                    // expensive!
                    if(path != parent && value != null &&
                       ((Boolean)value).booleanValue() &&
                        parent.isDescendant(path) && isVisible(path)) {
                        if (results == null) {
                            results = new ArrayList();
                        }
                        results.add (path);
                    }
                }
                if (results != null) {
                    result = (TreePath[]) results.toArray(result);
                }
            }
        }
        return result;
    }    
    
    public synchronized void addTreeExpansionListener (TreeExpansionListener l) {
        eListeners.add(l);
    }
    
    public synchronized void removeTreeExpansionListener (TreeExpansionListener l) {
        eListeners.remove(l);
    }
    
    public synchronized void addTreeWillExpandListener (TreeExpansionListener l) {
        weListeners.add(l);
    }
    
    public synchronized void removeTreeWillExpandListener (TreeExpansionListener l) {
        weListeners.remove(l);
    }
}
