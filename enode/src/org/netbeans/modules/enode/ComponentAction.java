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
 * Software is Nokia. Portions Copyright 2003 Nokia.
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
