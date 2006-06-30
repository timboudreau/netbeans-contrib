/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcscore.actions;


import org.openide.nodes.*;
import javax.swing.Icon;

/**
 * TODO Is not it unused?
 * <p>
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

