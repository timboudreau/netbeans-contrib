/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
/*
 * TreeNodeMenu.java
 *
 * Created on May 21, 2004, 5:29 PM
 */

package org.netbeans.swing.menus.impl;

import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreePath;
import org.netbeans.swing.menus.spi.*;
import org.netbeans.swing.menus.impl.*;

/**
 * A menu rooted on one node in a MenuTreeModel.
 *
 * @author  Tim Boudreau
 */
public class TreeNodeMenu extends JMenu implements TreeModelListener {
    private Object node;
    private MenuTreeModel mdl;
    private boolean dirty = true;
    
    /** Creates a new instance of TreeNodeMenu */
    public TreeNodeMenu(MenuTreeModel mdl, Object node) {
        this.mdl = mdl;
        this.node = node;
        StateListener lis = new StateListener();
        addHierarchyListener (lis);
        getModel().addChangeListener(lis);
    }
    
    public boolean listeningToModel = false; //XXX public for early unit tests, make private
    private void setListeningToModel (boolean val) {
        if (val != listeningToModel) {
            if (val) {
                mdl.addTreeModelListener(this);
            } else {
                mdl.removeTreeModelListener(this);
            }
            listeningToModel = val;
        }
    }
    
    private boolean dirty() {
        boolean wasDirty = dirty;
        dirty = false;
        return wasDirty;
    }
    
    private void markDirty() {
        dirty = true;
        System.err.println("Marking " + getText() + " dirty.");
    }

    
    public void treeNodesChanged(TreeModelEvent e) {
        if (Util.isInteresting(e, Util.CHANGED, node)) {
            markDirty();
        }
    }
    
    public void treeNodesInserted(TreeModelEvent e) {
        if (Util.isInteresting(e, Util.INSERTED, node)) {
            markDirty();
        }
    }
    
    public void treeNodesRemoved(TreeModelEvent e) {
        if (Util.isInteresting(e, Util.REMOVED, node)) {
            markDirty();
        }
    }
    
    public void treeStructureChanged(TreeModelEvent e) {
        if (Util.isInteresting(e, Util.STRUCTURE, node)) {
            markDirty();
        }
    }
    
    private class StateListener implements ChangeListener, HierarchyListener {
        public void hierarchyChanged(HierarchyEvent e) {
            if (e.getChanged() == TreeNodeMenu.this) {
                if ((e.getChangeFlags() & HierarchyEvent.DISPLAYABILITY_CHANGED) != 0) {
                    setListeningToModel (isDisplayable());
                }
            }
            //XXX on show, update submenu child items
        }
        
        public void stateChanged(ChangeEvent e) {
            if (getModel().isSelected() && dirty()) {
                updateFromModel();
            }
        }
    }
    
    public Object itemForComponent (JComponent comp) { //XXX should not be public
        return componentsToItems.get(comp);
    }
    
    public JComponent componentForItem (Object item) { //XXX should not be public
        return (JComponent)itemsToComponents.get(item);
    }
    
    public void dump () {
        System.out.println("Items to components: ");
        for (Iterator i=itemsToComponents.keySet().iterator(); i.hasNext();) {
            Object key = i.next();
            Object val = itemsToComponents.get(key);
            System.out.println(" " + key + "@" + System.identityHashCode(key) +  "=" + val);
        }
        System.out.println("Components to items: ");
        for (Iterator i=componentsToItems.keySet().iterator(); i.hasNext();) {
            Object key = i.next();
            Object val = componentsToItems.get(key);
            System.out.println(" " + key + "=" + val);
        }
    }
    
    private HashMap itemsToComponents = new HashMap();
    private HashMap componentsToItems = new HashMap();
    private void updateFromModel() {
        MenuTreeModel.ComponentProvider provider = mdl.getComponentProvider();
        
        int max = mdl.getChildCount(node);
        ArrayList nodes = new ArrayList(itemsToComponents.keySet());
        for (int i=0; i < max; i++) {
            Object childNode = mdl.getChild(node, i);
            JComponent comp = (JComponent) itemsToComponents.get(childNode);
            nodes.remove(childNode);
            if (comp == null) {
                comp = provider.createItemFor(childNode);
                System.out.println("Installing for " + childNode + " " + comp);
                install (comp, childNode);
            } else {
                JComponent newComp = provider.syncStateOf(childNode, comp);
                if (newComp != comp) {
                    replace (comp, newComp, childNode);
                }
            }
        }
        if (!nodes.isEmpty()) {
            for (Iterator i = nodes.iterator(); i.hasNext();) {
                Object missing = i.next();
                JComponent jc = (JComponent) itemsToComponents.get(missing);
                uninstall (jc, missing);
            }
        }
        dirty = false;
    }
    
    private void install (JComponent comp, Object child) {
        itemsToComponents.put (child, comp);
        componentsToItems.put (comp, child);
        add(comp);
    }
    
    private void uninstall (JComponent comp, Object child) {
        itemsToComponents.remove (child);
        componentsToItems.remove(comp);
        remove (comp);
        mdl.getComponentProvider().dispose (comp, child);
    }
    
    private void replace (JComponent old, JComponent nue, Object child) {
        int idx = Arrays.asList(getComponents()).indexOf(old);
        remove (idx);
        componentsToItems.remove(old);
        mdl.getComponentProvider().dispose (old, child);
        add (nue, idx);
        itemsToComponents.put(child, nue);
        componentsToItems.put(nue, child);
    }
    
}
