/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcscore.actions;


import org.openide.nodes.*;
import javax.swing.Icon;

/**
 * ClusteringAction expects to have the popup menu to be defined
 * consisting of items implementing this interface. The menu is defined on the default filesystem
 * by the module's layer.
 *
 *
 * @author  Milos Kleint
 */
public interface ClusterItemVisualizer {

    /**
     * Display name of the item.
     */
    String getName();
    
    /**
     * Icon for the item. Is used only if the menu is constructed within the 
     * main menu structure. If it's defined as popup menu, icon is ignored.
     */
    Icon getIcon();
    
    /**
     * Indicates wheather the item enables a forced dialog display.
     * is valid only if the ClusteringAction returns true in isSwitchingEnabled().
     * In such case, items returning true here will be appended "3 dots" when 
     * user presses CTRL and is expected to display some kind of dialog when performed.
     */
    boolean isSwitchable();
    
    boolean isItemEnabled(ClusteringAction action);
    
    void performAction(Node[] nodes, ClusteringAction action);
    
    void setSwitched(boolean userHoldsCtrl);
}

