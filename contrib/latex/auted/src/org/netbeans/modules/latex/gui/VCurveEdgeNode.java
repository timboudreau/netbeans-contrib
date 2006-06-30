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
public class VCurveEdgeNode extends ControllableCurveEdgeNode {

    private double angleSource;
    private double angleTarget;
    private double curv;

    /** Creates a new instance of AngleEdgeNode */
    public VCurveEdgeNode(StateNode source, StateNode target) {
        super(source, target);
        angleSource = 0.0;
        angleTarget = 0.0;
        curv  = 1.0;
    }
    
    protected double getSourceAngle() {
        return getAngleSource();
    }
    
    protected double getSourceDistance() {
        return curv;
    }
    
    protected double getTargetAngle() {
        return getAngleTarget();
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
        setCurv(vals[1]);
        firePropertyChange(PROP_SOURCE_CONTROL, null, null);
    }
    
    public void setTargetControlPoint(Point2D value) {
        double[] vals = getValuesForControlPoint(getTarget(), value);
        
        setAngleTarget(vals[0]);
        setCurv(vals[1]);
        firePropertyChange(PROP_TARGET_CONTROL, null, null);
    }
    
    protected String getCommandBase() {
        return "\\VCurve";
    }
    
    protected String getSpecialArgument() {
        return "angleA=" + printDouble(getSourceAngle()) + ",angleB=" + printDouble(getTargetAngle()) + ",ncurv=" + printDouble(getCurv());
    }
    
    public boolean equalsNode(Node node) {
        if (!super.equalsNode(node))
            return false;
        
        VCurveEdgeNode van = (VCurveEdgeNode) node;
        
        return    getAngleSource() == van.getAngleSource()
               && getAngleTarget() == van.getAngleTarget()
               && getCurv() == van.getCurv();
    }

}
