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
 * DefaultComponentProvider.java
 *
 * Created on May 21, 2004, 8:04 PM
 */

package org.netbeans.swing.menus.spi;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import org.netbeans.swing.menus.impl.TreeNodeMenu;

/**
 * Simple ComponentProvider implementation - provides JMenuItems for
 * nodes which are Action instances, and menus for everything else.
 * <p>
 * This class is final; implementations of MenuTreeModel which need additional
 * functionality should create an instance and proxy to it where appropriate.
 *
 * @author  Tim Boudreau
 */
public final class DefaultComponentProvider extends MenuTreeModel.ComponentProvider {
    public DefaultComponentProvider (MenuTreeModel mdl) {
        super (mdl);
    }

    /**
     * Create a component (such as a JMenuItem) for the passed object, which
     * represents one element from a MenuTreeModel.  This method should always
     * return a new component (or one known not to be in use).
     * <p>
     * This implementation returns JMenuItems if the passed node implements
     * Action, and JMenus for everything else.
     */
    public JComponent createItemFor(Object node) {
        if (node instanceof Action) {
            return new JMenuItem ((Action) node);
        } else {
            TreeNodeMenu result = new TreeNodeMenu (getModel(), node);
            result.setText (node.toString());
            return result;
        }
    }

    /**
     * Synchronize the state of a component representing a tree node with
     * the tree node it represents.  This method may return a replacement
     * component if necessary (for example, if a node which was formerly a
     * menu item now should be represented by a menu).
     * <p>
     * This method should update icons, mnemonics, display names, etc. as
     * appropriate so the component accurately reflects the state of the 
     * action it represents.
     */
    public JComponent syncStateOf(Object node, JComponent proxy) {
        if (node instanceof Action) {
            JMenuItem jm = (JMenuItem) proxy;
            Action act = (Action) node;
            jm.setText ((String) act.getValue(Action.NAME));
            jm.setIcon ((Icon) act.getValue(Action.SMALL_ICON));
        } else if (proxy instanceof TreeNodeMenu) {
            ((TreeNodeMenu) proxy).setText (node.toString());
        }
        return proxy;
    }

    /**
     * Dispose of a component, clearing any references or listeners it may
     * hold.
     */
    public void dispose(JComponent comp, Object node) {
        if (comp instanceof JMenuItem) {
            ((JMenuItem) comp).setAction(null);
        }
    }    
    
}
