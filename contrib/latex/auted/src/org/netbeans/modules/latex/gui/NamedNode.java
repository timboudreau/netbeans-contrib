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

/**
 *
 * @author Jan Lahoda
 */
public abstract class NamedNode extends Node {
    
    public static final String PROP_NAME = "name";
    private String name;
    
    /** Creates a new instance of NamedNode */
    public NamedNode() {
        this.name = "";
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
    public void setName(java.lang.String name) {
        this.name = name;
        firePropertyChange(PROP_NAME, null, name);
    }
    
    public boolean equalsNode(Node node) {
        if (!super.equalsNode(node))
            return false;
        
        NamedNode nn = (NamedNode) node;
        
        return getName() == nn.getName();
    }

}
