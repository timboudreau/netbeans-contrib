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
import java.awt.geom.Point2D;
import java.io.PrintWriter;
import javax.swing.Action;

/**
 *
 * @author Jan Lahoda
 */
public class AngleEdgeNode extends CurveEdgeNode {
    
    private int angle;
    
    /** Creates a new instance of AngleEdgeNode */
    public AngleEdgeNode(StateNode source, StateNode target) {
        super(source, target);
        angle = 1;
    }
    
    private double computeAngle(StateNode s, StateNode t, int sign) {
        double dx = t.getPosition().getX() - s.getPosition().getX();
        double dy = t.getPosition().getY() - s.getPosition().getY();
        
        double sourceAngle = Math.toDegrees(Math.atan2((-1) * sign * dy, sign * dx));
//        System.err.println("sourceAngle = " + sourceAngle );
        double result = sourceAngle - getOrientation() * sign * angle * 15.0;

//        System.err.println("result = " + result );
        return result;
    }
    
    protected double getSourceAngle() {
        return computeAngle(getSource(), getTarget(), 1);
    }
    
    protected double getSourceDistance() {
        return 0.8;
    }
    
    protected double getTargetAngle() {
        return computeAngle(getSource(), getTarget(), -1);
    }
    
    protected double getTargetDistance() {
        return 0.8;
    }
    
    public Action[] createPopupMenuAdder() {
        return new Action[] {
            new ToggleAngleAction(),
        };
    }
    
    /** Getter for property angle.
     * @return Value of property angle.
     *
     */
    public int getAngle() {
        return angle;
    }
    
    /** Setter for property angle.
     * @param angle New value of property angle.
     *
     */
    public void setAngle(int angle) {
        this.angle = angle;
    }
    
    protected String getCommandBase() {
        if (angle == 1)
            return "\\Arc";
        else
            return "\\LArc";
    }
    
    protected String getSpecialArgument() {
        return null;
    }
    
    private class ToggleAngleAction extends ToggleAction {
        public ToggleAngleAction() {
            super(new String[] {"Small angle", "Big angle"}, angle == 1 ? 1 : 0);
        }
        
        public void actionPerformed(ActionEvent e) {
            super.actionPerformed(e);
            angle = angle == 1 ? 2 : 1;
            AngleEdgeNode.this.redraw();
        }
    }

    public boolean equalsNode(Node node) {
        if (!super.equalsNode(node))
            return false;
        
        AngleEdgeNode an = (AngleEdgeNode) node;
        
        return    getAngle() == an.getAngle();
    }
}
