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

import java.awt.Point;

/**
 *
 * @author Jan Lahoda
 */
public abstract class PositionNode extends NamedNode {
    public static final String PROP_X = "x";
    public static final String PROP_Y = "y";

    private int x,y;

    /** Creates a new instance of PositionNode */
    public PositionNode(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /** Getter for property x.
     * @return Value of property x.
     *
     */
    public int getX() {
        return x;
    }
    
    /** Getter for property y.
     * @return Value of property y.
     *
     */
    public int getY() {
        return y;
    }
    
    /** Setter for property x.
     * @param x New value of property x.
     *
     */
    public void setX(int x) {
        this.x = x;
        firePropertyChange(PROP_X, null, null);
    }
    
    /** Setter for property y.
     * @param y New value of property y.
     *
     */
    public void setY(int y) {
        this.y = y;
        firePropertyChange(PROP_Y, null, null);
    }
    
    public Point getPosition() {
        return new Point((int) (getX() * UIProperties.getGridSize().getWidth()),
                         (int) (getY() * UIProperties.getGridSize().getHeight()));
    }
    
    public boolean equalsNode(Node node) {
        if (!super.equalsNode(node))
            return false;
        
        PositionNode pn = (PositionNode) node;
        
        return getX() == pn.getX() && getY() == pn.getY();
    }
}
