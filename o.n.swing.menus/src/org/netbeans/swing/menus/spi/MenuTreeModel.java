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
 * MenuTreeModel.java
 *
 * Created on May 21, 2004, 5:12 PM
 */

package org.netbeans.swing.menus.spi;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.tree.TreeModel;
import org.netbeans.swing.menus.impl.TreeNodeMenu;

/**
 * A tree model representing the elements of a menu bar and its associated
 * submenus.  Nodes in the tree may be of any class; model instances are
 * expected to provide an implementation of ComponentProvider which will
 * create components to represent the tree nodes.  A default implementation is
 * provided which expects Action instances as tree nodes, and returns
 * JMenuItems.
 *
 * @author  Tim Boudreau
 */
public interface MenuTreeModel extends TreeModel {
    public ComponentProvider getComponentProvider();
    
    public static abstract class ComponentProvider {
        private final MenuTreeModel mdl;
        public ComponentProvider (MenuTreeModel mdl) {
            this.mdl = mdl;
        }
        
        protected final MenuTreeModel getModel() {
            return mdl;
        }
        
        /**
         * Create a component to represent this node.  
         */
        public abstract JComponent createItemFor (Object node);
        /**
         * Synchronize the state of a component when it is about to be
         * displayed, setting, for instance, enabled/disabled state,
         * mnemonics, etc.  This method may return a different component
         * than it is passed; if it does so, the GUI container should 
         * replace the old component with the returned one.
         */
        public abstract JComponent syncStateOf (Object node, JComponent proxy);
        /**
         * Called when a component has been removed from the component 
         * hierarchy, either because it has been replaced or destroyed.
         */
        public abstract void dispose (JComponent comp, Object node);
    }
}
