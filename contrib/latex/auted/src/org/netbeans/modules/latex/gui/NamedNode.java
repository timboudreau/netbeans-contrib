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

import javax.swing.Icon;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.modules.latex.model.IconsStorage;

/**
 *
 * @author Jan Lahoda
 */
public abstract class NamedNode extends Node implements ChangeListener {
    
    public static final String PROP_NAME = "name";
    public static final String PROP_ICON = "icon";
    
    private String name;
    private IconsStorage.ChangeableIcon icon;
    
    /** Creates a new instance of NamedNode */
    public NamedNode() {
        this.name = "";
        this.icon = null;
    }
    
    /** Getter for property name.
     * @return Value of property name.
     *
     */
    public java.lang.String getName() {
        return name;
    }
    
    /** Setter for property name.
     * @param name New value of property name.
     *
     */
    public synchronized void setName(java.lang.String name) {
        if (icon != null)
            icon.removeChangeListener(this);
        
        this.name = name;
        this.icon = null;
        firePropertyChange(PROP_NAME, null, name);
    }
    
    protected synchronized Icon getIconForName() {
        if (icon == null) {
            icon = IconsStorage.getDefault().getIcon(getName());
            icon.addChangeListener(this);
        }
        
        return icon;
    }
    
    public boolean equalsNode(Node node) {
        if (!super.equalsNode(node))
            return false;
        
        NamedNode nn = (NamedNode) node;
        
        return getName() == nn.getName();
    }

    public void stateChanged(ChangeEvent e) {
        if (e.getSource() == icon)
            firePropertyChange(PROP_ICON, null, null);
    }

}
