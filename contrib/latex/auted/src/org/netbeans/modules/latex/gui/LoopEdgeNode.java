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

import java.io.PrintWriter;

/**
 *
 * @author Jan Lahoda
 */
public class LoopEdgeNode extends CurveEdgeNode {
    
    public static final int NORTH = 0;
    public static final int EAST= 1;
    public static final int SOUTH = 2;
    public static final int WEST = 3;
    
    private int orientation;
    private String[] orientationChar = new String[] {"N", "E", "S", "W"};
    private double[] orientationAngle = new double[] {90, 0, 270, 180};
    
    /** Creates a new instance of LoopEdgeNode */
    public LoopEdgeNode(StateNode state) {
        super(state, state);
        this.orientation = NORTH;
    }
    
    public int getOrientation() {
        return orientation;
    }
    
    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }
    
    protected double getSourceAngle() {
        return orientationAngle[orientation] - 30;
    }
    
    protected double getSourceDistance() {
        return 6.0;
    }
    
    protected double getTargetAngle() {
        return orientationAngle[orientation] + 30;
    }
    
    protected double getTargetDistance() {
        return 6.0;
    }
    
    public String getOrientationString() {
        return orientationChar[orientation];
    }
    
    protected String getCommandBase() {
        return "\\Loop";
    }
    
    protected String getSpecialArgument() {
        return null;
    }

    protected boolean isOnlySource() {
        return true;
    }

    public boolean equalsNode(Node node) {
        if (!super.equalsNode(node))
            return false;
        
        LoopEdgeNode ln = (LoopEdgeNode) node;
        
        return getOrientation() == ln.getOrientation();
    }

}
