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
