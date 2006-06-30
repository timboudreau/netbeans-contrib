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

import java.awt.geom.Point2D;

/**
 *
 * @author Jan Lahoda
 */
public abstract class ControllableCurveEdgeNode extends CurveEdgeNode {

    public ControllableCurveEdgeNode(StateNode source, StateNode target) {
        super(source, target);
    }

    private static final double MAGIC = UIProperties.getCurveControllMagic();
    private Point2D   getControlPoint(StateNode node, double angle, double curv) {
        Point2D start = node.getContourPoint(angle);
        Point2D ret   = new Point2D.Double(start.getX() + curv * MAGIC * Math.cos(Math.toRadians(angle)),
        start.getY() + -curv * MAGIC * Math.sin(Math.toRadians(angle)));
        
        return ret;
    }
    
    public Point2D getSourceControlPoint() {
        return getControlPoint(getSource(), getSourceAngle(), getSourceDistance());
    }
    
    public Point2D getTargetControlPoint() {
        return getControlPoint(getTarget(), getTargetAngle(), getTargetDistance());
    }
    
    protected double[] getValuesForControlPoint(StateNode node, Point2D pos) {
        double centerX = node.getX() * UIProperties.getGridSize().getWidth();
        double centerY = node.getY() * UIProperties.getGridSize().getHeight();
        
//        System.err.println("center: " + centerX + "," + centerY);
        
        double angle = Math.toDegrees(Math.atan2((centerY - pos.getY()) , -(centerX - pos.getX())));
        
        Point2D start = node.getContourPoint(angle);
        
//        System.err.println("start = " + start);
        
        double[] result = new double[] {angle, start.distance(pos) / MAGIC};
        
//        System.err.println("result = [" + result[0] + "," + result[1] + "]" );
        return result;
    }
    
    public abstract void setSourceControlPoint(Point2D value);
    public abstract void setTargetControlPoint(Point2D value);
    
    protected abstract String getCommandBase();
    
    protected abstract String getSpecialArgument();
    
}
