/*
 *                          Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License Version
 * 1.0 (the "License"). You may not use this file except in compliance with
 * the License. A copy of the License is available at http://www.sun.com/
 *
 * The Original Code is the LaTeX module.
 * The Initial Developer of the Original Code is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002,2003.
 * All Rights Reserved.
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.gui;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;

public class ToggleAction extends AbstractAction {
    
    private String[] names;
    private int      currentName;
    
    public ToggleAction(String[] names, int currentName) {
        super(names[currentName]);
        this.names = names;
        this.currentName = currentName;
    }
    
    public void setName(String name) {
        putValue(Action.NAME, name);
        firePropertyChange(Action.NAME, null, name);
    }
    
    public void actionPerformed(ActionEvent e) {
        currentName = (currentName + 1) % 2;
        setName(names[currentName]);
    }
    
}

