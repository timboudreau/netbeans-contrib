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

import java.awt.Dimension;
import java.awt.Point;
import java.awt.geom.Line2D;
import java.io.PrintWriter;

/**
 *
 * @author Jan Lahoda
 */
public class LineEdgeNode extends CurveEdgeNode {
    
    /** Creates a new instance of LineEdgeNode */
    public LineEdgeNode(StateNode source, StateNode target) {
        super(source, target);
        setName("");
    }
    
    private Line2D getLine() {
        Dimension grid = UIProperties.getGridSize();
        
        int xGrid = (int) grid.getWidth();
        int yGrid = (int) grid.getHeight();
        
        int dx = getTarget().getX() - getSource().getX();
        int dy = getTarget().getY() - getSource().getY();
        
        double sourceAngle = Math.atan2(-dy, dx);
        double targetAngle = Math.atan2(dy, -dx);
        
        Point source = getSource().getContourPoint(Math.toDegrees(sourceAngle));
        Point dest   = getTarget().getContourPoint(Math.toDegrees(targetAngle));
        
        return new Line2D.Double(source.getX(), source.getY(), dest.getX(), dest.getY());
    }
    
//    public void draw(Graphics g) {
//        Line2D line = getLine();
//        
//        g.drawLine((int) line.getX1(), (int) line.getY1(), (int) line.getX2(), (int) line.getY2());
//        g.drawOval((int) line.getX2() - 1, (int) line.getY2() - 1, 2, 2);
//    }
    
//    public Rectangle getOuterDimension() {
//        Rectangle first = getSource().getOuterDimension();
//        
//        return first.union(getTarget().getOuterDimension());
//    }

    public double distance(Point pos) {
        return getLine().ptLineDist(pos.getX(), pos.getY());
    }
    
    protected double getSourceAngle() {
        int dx = getTarget().getX() - getSource().getX();
        int dy = getTarget().getY() - getSource().getY();
        
        return Math.toDegrees(Math.atan2(-dy, dx));
    }
    
    protected double getSourceDistance() {
        return 0.0;
    }
    
    protected double getTargetAngle() {
        int dx = getTarget().getX() - getSource().getX();
        int dy = getTarget().getY() - getSource().getY();
        
        return Math.toDegrees(Math.atan2(dy, -dx));
    }
    
    protected double getTargetDistance() {
        return 0.0;
    }
    
    protected String getCommandBase() {
        return "\\Edge";
    }
    
    protected String getSpecialArgument() {
        return null;
    }
    
}
