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
    
    protected double getSourceAngle() {
        return computeAngle(getSource(), getTarget(), 1);
    }
    
    protected double getSourceDistance() {
        return curv;
    }
    
    protected double getTargetAngle() {
        return computeAngle(getSource(), getTarget(), -1);
    }
    
    protected double getTargetDistance() {
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
