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

import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Point2D;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.util.ResourceBundle;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;

/**
 *
 * @author Jan Lahoda
 */
public abstract class CurveEdgeNode extends EdgeNode {
    
    public static final String PROP_SOURCE_CONTROL = "sourceControl";
    public static final String PROP_TARGET_CONTROL = "targetControl";
    
    /** Creates a new instance of CurveEdgeNode */
    public CurveEdgeNode(StateNode source, StateNode target) {
        super(source, target);
    }
    
    private CubicCurve2D getCurve() {
        Point sP = getSource().getContourPoint(getSourceAngle());
        Point tP = getTarget().getContourPoint(getTargetAngle());
        
        double sX = sP.getX();
        double sY = sP.getY();
        double tX = tP.getX();
        double tY = tP.getY();
        
        double distance = Point2D.distance(sX, sY, tX, tY);
        
        double sD = distance * getSourceDistance() / 2;
        double sA = Math.toRadians(-getSourceAngle());
        double tD = distance * getTargetDistance() / 2;
        double tA = Math.toRadians(-getTargetAngle());
        
        double csX = sX + sD * Math.cos(sA);
        double csY = sY + sD * Math.sin(sA);
        double ctX = tX + tD * Math.cos(tA);
        double ctY = tY + tD * Math.sin(tA);
        
//        System.err.println("sX = " + sX );
//        System.err.println("sY = " + sY );
//        System.err.println("csX = " + csX );
//        System.err.println("csY = " + csY );
//        System.err.println("ctX = " + ctX );
//        System.err.println("ctY = " + ctY );
//        System.err.println("tX = " + tX );
//        System.err.println("tY = " + tY );
        return new CubicCurve2D.Double(sX, sY, csX, csY, ctX, ctY, tX, tY);
    }
    
    private static final float[][] styles = new float[][] {
        new float[] {1},
        new float[] {1},
        new float[] {3, 3},
        new float[] {1, 3},
        new float[] {0, 1},
    };
    
    private BasicStroke basic = new BasicStroke();
    private void setStroke(Graphics2D g) {
        LineStyle l = getLineStyle();
        Stroke newStroke = new BasicStroke(basic.getLineWidth(), basic.getEndCap(), basic.getLineJoin(), basic.getMiterLimit(), styles[l.getStyle()], 0);

        g.setStroke(newStroke);
    }
    
    public void draw(Graphics2D g) {
        Stroke oldStroke = g.getStroke();
        
        setStroke(g);
        g.draw(getCurve());
        g.setStroke(oldStroke);
        
        Point2D point = getPoint(getLabelPosition());
        
        g.drawString(getName(), (int) point.getX(), (int) point.getY());
        
        drawArrow(g, getTarget().getContourPoint(getTargetAngle()), getTargetAngle());
    }
    
    public Rectangle getOuterDimension() {
        return getCurve().getBounds();
    }
    
    public void outputVaucansonSource(PrintWriter out) {
        if (isBorder())
            out.println("\\EdgeBorder");
        
        LineStyle l = getLineStyle();
        
        if (l.getStyle() != LineStyle.DEFAULT) {
            out.println("\\ChgEdgeLineStyle{" + LineStyle.values[l.getStyle()] + "}");
        }
//        out.println("\\VCurveL{angleA=" + (int) getSourceAngle() + ",angleB=" + (int) getTargetAngle() + ",ncurvA=" + getSourceDistance() + ",ncurv=" + getTargetDistance() + "}{" + getSource().getID() + "}{" + getTarget().getID() + "}{" + getName() + "}");
        
        out.print(getCommandBase());
        
        out.print(getOrientationString());
        
        out.print("[" + getLabelPosition() + "]");
        
        String special = getSpecialArgument();
        
        if (special != null)
            out.print("{" + special + "}");
            
        out.print("{" + getSource().getID() + "}");
        
        if (!isOnlySource())
            out.print("{" + getTarget().getID() + "}");
        
        out.println("{" + getName() + "}");
        
        if (l.getStyle() != LineStyle.DEFAULT) {
            out.println("\\RstEdgeLineStyle");
        }
        
        if (isBorder())
            out.println("\\EdgeBorderOff");
    }
    
    protected String getOrientationString() {
        if (getOrientation() == RIGHT)
            return "R";
        else
            return "L";
    }
    
    protected boolean isOnlySource() {
        return false;
    }
    
    public double distance(Point pos) {
        System.err.println("pos=" + pos);
        double dist = Double.POSITIVE_INFINITY;
        
        for (double posit = 0.0; posit <= 1.0; posit += 0.05) {
            double actDist = getPoint(posit).distance(pos.getX(), pos.getY());
            
            if (actDist < dist)
                dist = actDist;
        }
        
        System.err.println("label=" + getName());
        System.err.println("dist=" + dist);
        return dist;
//        if (getCurve().intersects(pos.getX() - 4, pos.getY() - 4, 8, 8)) {
//            System.err.println("distance=4");
//            return 4;
//        } else
//            return Double.POSITIVE_INFINITY;
    }
    
    /**Get the source angle.
     *
     * @returns the source angle in degrees.
     */
    protected abstract double getSourceAngle();
    protected abstract double getTargetAngle();
    protected abstract double getSourceDistance();
    protected abstract double getTargetDistance();
    
    protected final void   outputCurveVaucansonSource(PrintWriter out) {}
    protected abstract String getCommandBase();
    protected abstract String getSpecialArgument();
    
    public Point2D getPoint(double place) {
        double oneMPlace  = 1 - place;
        double place2     = place * place;
        double place3     = place * place2;
        double oneMPlace2 = oneMPlace * oneMPlace;
        double oneMPlace3 = oneMPlace * oneMPlace2;
        CubicCurve2D cc2d = getCurve();
        
        return new Point2D.Double(  cc2d.getX1() * oneMPlace3
                                  + 3 * cc2d.getCtrlX1() * place * oneMPlace2
                                  + 3 * cc2d.getCtrlX2() * place2 * oneMPlace
                                  + cc2d.getX2() * place3,
                                    cc2d.getY1() * oneMPlace3
                                  + 3 * cc2d.getCtrlY1() * place * oneMPlace2
                                  + 3 * cc2d.getCtrlY2() * place2 * oneMPlace
                                  + cc2d.getY2() * place3);
    }
    
    public final /*temp*/Action[] createPopupMenu() {
        return new Action[] {
            new ToggleLeftRightAction(),
            null,
            new ConvertToAction(LineEdgeNode.class),
            new ConvertToAction(AngleEdgeNode.class),
            new ConvertToAction(VArcEdgeNode.class),
            new ConvertToAction(VCurveEdgeNode.class),
            new ConvertToAction(VVCurveEdgeNode.class),
            null,
            new RemoveAction(),
            null,
            Editor.getSystemMenuFlag(),
            null,
            new PropertiesAction(),
        };
    }
    
    protected Action createLeftRightAction() {
        return new ToggleLeftRightAction();
    }
    
    private class ToggleLeftRightAction extends ToggleAction {
        public ToggleLeftRightAction() {
            super(new String[] {"Left", "Right"}, getOrientation() == RIGHT ? 1 : 0);
        }
        
        public void actionPerformed(ActionEvent e) {
            super.actionPerformed(e);
            setOrientation(getOrientation() == RIGHT ? (-1) : 1);
//            Editor.this.repaint(new Rectangle(Editor.this.getSize()));
            CurveEdgeNode.this.redraw();
        }
    }
    
    private static String getNameForClass(Class clazz) {
        return ResourceBundle.getBundle("org/netbeans/modules/latex/gui/Bundle").getString("LBL_" + clazz.getName());
    }
    
    private class ConvertToAction extends AbstractAction {
        private Class clazz;
        
        public ConvertToAction(Class clazz) {
            super(getNameForClass(clazz));
            this.clazz = clazz;
        }
        
        public void actionPerformed(ActionEvent e) {
            try {
                Constructor c =clazz.getConstructor(new Class[] {StateNode.class, StateNode.class});
                EdgeNode node = (EdgeNode)c.newInstance(new Object[] {CurveEdgeNode.this.getSource(), CurveEdgeNode.this.getTarget()});
                node.setName(CurveEdgeNode.this.getName());
                CurveEdgeNode.this.getStorage().addObject(node);
                CurveEdgeNode.this.getStorage().removeObject(CurveEdgeNode.this);
                CurveEdgeNode.this.redraw();
//                Editor.this.clearSelection();
//                Editor.this.repaint(new Rectangle(Editor.this.getSize()));
            } catch (Exception ex) {
                ex.printStackTrace(System.err);
            }
        }
        
    }

    private class PropertiesAction extends AbstractAction {
        
        public PropertiesAction() {
            super("Properties");
        }
        
        public void actionPerformed(ActionEvent e) {
            CurveEdgeProperties properties = new CurveEdgeProperties();
            
            properties.setLabel(getName());
            
            if (Utilities.showDialog("Edge Properties", properties) == JOptionPane.OK_OPTION) {
                setName(properties.getLabel());
                CurveEdgeNode.this.redraw();
            }
        }
        
    }
}
