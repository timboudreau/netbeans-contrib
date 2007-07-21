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
 * Portions created by Jan Lahoda_ are Copyright (C) 2002-2007.
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
public class VArcEdgeNode extends ControllableCurveEdgeNode {

    private double angle;
    private double curv;

    /** Creates a new instance of AngleEdgeNode */
    public VArcEdgeNode(StateNode source, StateNode target) {
        super(source, target);
        angle = 30.0;
        curv  = 1.0;
    }
    
    private double computeAngle(StateNode s, StateNode t, int sign) {
        double dx = t.getPosition().getX() - s.getPosition().getX();
        double dy = t.getPosition().getY() - s.getPosition().getY();
        
        double sourceAngle = Math.toDegrees(Math.atan2((-1) * sign * dy, sign * dx));
//        System.err.println("sourceAngle = " + sourceAngle );
        double result = sourceAngle + sign * angle;

//        System.err.println("result = " + result );
        return result;
    }
    
    public double getSourceAngle() {
        return computeAngle(getSource(), getTarget(), 1);
    }
    
    public double getSourceDistance() {
        return curv;
    }
    
    public double getTargetAngle() {
        return computeAngle(getSource(), getTarget(), -1);
    }
    
    public double getTargetDistance() {
        return curv;
    }
    
    /** Getter for property curv.
     * @return Value of property curv.
     *
     */
    public double getCurv() {
        return curv;
    }
    
    /** Setter for property curv.
     * @param curv New value of property curv.
     *
     */
    public void setCurv(double curv) {
        this.curv = curv;
    }
    
    /** Getter for property angle.
     * @return Value of property angle.
     *
     */
    public double getAngle() {
        return angle;
    }
    
    /** Setter for property angle.
     * @param angle New value of property angle.
     *
     */
    public void setAngle(double angle) {
        this.angle = angle;
    }
    
    private void setAngleForControlPoint(double rough, int sign) {
        StateNode s = getSource();
        StateNode t = getTarget();
        double dx = t.getPosition().getX() - s.getPosition().getX();
        double dy = t.getPosition().getY() - s.getPosition().getY();
        
        double sourceAngle = Math.toDegrees(Math.atan2((-1) * sign * dy, sign * dx));
        //        System.err.println("sourceAngle = " + sourceAngle );
        double result = sourceAngle + sign * angle;
        double angle = (rough - sourceAngle) / (sign);
        
        System.err.println("settings angle=" + angle);
        setAngle(angle);
    }
    
    public void setSourceControlPoint(Point2D value) {
//        System.err.println("setSourceControlPoint(" + value + ")");
        setAngleForControlPoint(getValuesForControlPoint(getSource(), value)[0], 1);
        firePropertyChange(PROP_SOURCE_CONTROL, null, null);
    }
    
    public void setTargetControlPoint(Point2D value) {
//        System.err.println("setTargetControlPoint(" + value +")");
        setAngleForControlPoint(getValuesForControlPoint(getTarget(), value)[0], -1);
        firePropertyChange(PROP_TARGET_CONTROL, null, null);
    }
    
    protected String getCommandBase() {
        return "\\VArc";
    }
    
    protected String getSpecialArgument() {
        return "arcangle=" + printDouble(getAngle()) + ",ncurv=" + printDouble(getCurv());
    }
    
    public boolean equalsNode(Node node) {
        if (!super.equalsNode(node))
            return false;
        
        VArcEdgeNode van = (VArcEdgeNode) node;
        
        return    getAngle() == van.getAngle()
               && getCurv() == van.getCurv();
    }
}
