/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Nokia. Portions Copyright 2003 Nokia.
 * All Rights Reserved.
 */

package org.netbeans.modules.enode;

import javax.swing.*;

/**
 * Special action serving as a wrapper for JComponents added to the popup
 * menu.
 * @author David Strupl
 */
public class ComponentAction extends AbstractAction implements org.openide.util.actions.Presenter.Popup {
    
    private JComponent myComponent;
    
    /** Creates a new instance of ComponentAction */
    public ComponentAction(JComponent comp) {
        myComponent = comp;
    }
    
    /**
     * This action should not be performed as an action.
     */
    public void actionPerformed(java.awt.event.ActionEvent e) {
        throw new IllegalStateException("ComponentAction should not be performed as action."); // NOI18N
    }

    /**
     * The popup presenter returns either directly
     * the myComponent if it is a JMenuItem or
     * creates a new JMenuItem and adds myComponent to it.
     */
    public JMenuItem getPopupPresenter() {
        if (myComponent instanceof JMenuItem) {
            return (JMenuItem)myComponent;
        }
        JMenuItem newItem = new JMenuItem();
        newItem.add(myComponent);
        return newItem;
    }
    
}
