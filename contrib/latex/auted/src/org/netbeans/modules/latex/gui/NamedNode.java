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
 * The Original Software is the LaTeX module.
 * The Initial Developer of the Original Software is Jan Lahoda.
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
