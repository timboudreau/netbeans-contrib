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
public class VVCurveEdgeNode extends ControllableCurveEdgeNode {
    
    private double angleSource;
    private double angleTarget;
    private double sourceCurv;
    private double targetCurv;
    
    /** Creates a new instance of AngleEdgeNode */
    public VVCurveEdgeNode(StateNode source, StateNode target) {
        super(source, target);
        angleSource = 0.0;
        angleTarget = 0.0;
        sourceCurv = 1.0;
        targetCurv = 1.0;
    }
    
    protected double getSourceAngle() {
        return getAngleSource();
    }
    
    protected double getSourceDistance() {
        return getSourceCurv();
    }
    
    protected double getTargetAngle() {
        return getAngleTarget();
    }
    
    protected double getTargetDistance() {
        return getTargetCurv();
    }
    
    /** Getter for property angleSource.
     * @return Value of property angleSource.
     *
     */
    public double getAngleSource() {
        return angleSource;
    }
    
    /** Setter for property angleSource.
     * @param angleSource New value of property angleSource.
     *
     */
    public void setAngleSource(double angleSource) {
        this.angleSource = angleSource;
    }
    
    /** Getter for property angleTarget.
     * @return Value of property angleTarget.
     *
     */
    public double getAngleTarget() {
        return angleTarget;
    }
    
    /** Setter for property angleTarget.
     * @param angleTarget New value of property angleTarget.
     *
     */
    public void setAngleTarget(double angleTarget) {
        this.angleTarget = angleTarget;
    }
    
    public void setSourceControlPoint(Point2D value) {
        double[] vals = getValuesForControlPoint(getSource(), value);
        
        setAngleSource(vals[0]);
        setSourceCurv(vals[1]);
        firePropertyChange(PROP_SOURCE_CONTROL, null, null);
    }
    
    public void setTargetControlPoint(Point2D value) {
        double[] vals = getValuesForControlPoint(getTarget(), value);
        
        setAngleTarget(vals[0]);
        setTargetCurv(vals[1]);
        firePropertyChange(PROP_TARGET_CONTROL, null, null);
    }
    
    /**
     * Getter for property sourceCurv.
     * @return Value of property sourceCurv.
     */
    public double getSourceCurv() {
        return sourceCurv;
    }
    
    /**
     * Setter for property sourceCurv.
     * @param sourceCurv New value of property sourceCurv.
     */
    public void setSourceCurv(double sourceCurv) {
        this.sourceCurv = sourceCurv;
    }
    
    /**
     * Getter for property targetCurv.
     * @return Value of property targetCurv.
     */
    public double getTargetCurv() {
        return targetCurv;
    }
    
    /**
     * Setter for property targetCurv.
     * @param targetCurv New value of property targetCurv.
     */
    public void setTargetCurv(double targetCurv) {
        this.targetCurv = targetCurv;
    }
    
    protected String getCommandBase() {
        return "\\VVCurve";
    }
    
    protected String getSpecialArgument() {
        return "angleA=" + printDouble(getSourceAngle()) + ",angleB=" + printDouble(getTargetAngle()) + ",ncurvA=" + printDouble(getSourceDistance()) + ",ncurvB=" + printDouble(getTargetDistance());
    }
    
    public boolean equalsNode(Node node) {
        if (!super.equalsNode(node))
            return false;
        
        VVCurveEdgeNode van = (VVCurveEdgeNode) node;
        
        return    getAngleSource() == van.getAngleSource()
               && getAngleTarget() == van.getAngleTarget()
               && getSourceCurv() == van.getSourceCurv()
               && getTargetCurv() == van.getTargetCurv();
    }
}
