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
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.geom.Arc2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;

/**
 *
 * @author Jan Lahoda
 */
public class StateNode extends PositionNode {
   
    public static final String PROP_FINAL_STATE = "finalState";
    public static final String PROP_INITIAL_STATE = "initialState";
    public static final String PROP_LINE_STYLE = "lineStyle";

    private int diameterIndex;
    
    private boolean finalState;
    private boolean initialState;
    
    private boolean isHidden;
    
    private LineStyle lineStyle;
    
    private static final int[] NODE_SIZES = new int[] {
        2,
        6,
        9,
        12
    };
    
    /** Creates a new instance of StateNode */
    public StateNode(int x, int y, String name) {
        super(x, y);
        this.diameterIndex = 2;
        setName(name);
        lineStyle = new LineStyle(LineStyle.DEFAULT);
    }
    
    public StateNode(int x, int y) {
        this(x, y, "");
    }
    
    public int getDiameter() {
        return NODE_SIZES[diameterIndex];
    }
    
    public Point getContourPoint(double angle) {
        Dimension grid = UIProperties.getGridSize();
        
        int xGrid = (int) grid.getWidth();
        int yGrid = (int) grid.getHeight();
        
        Point2D p2d = new Arc2D.Double(getX() * xGrid - getDiameter(), getY() * yGrid - getDiameter(), 2 * getDiameter(), 2 * getDiameter(), angle - 1, 1, Arc2D.OPEN).getEndPoint();//StartPoint();
        
        return new Point((int) p2d.getX(), (int) p2d.getY());
//        double cos = Math.sin(Math.toRadians(angle));
//        
//        int signX = angle >= 90 && angle <= 270 ? -1 : 1;
//        
//        double xR = Math.sqrt(((double) getDiameter() * getDiameter())/(1 + cos * cos));
//        double yR = xR * cos;
//        
//        return new Point((int) (xR * signX + getX() * UIProperties.getGridSize().getWidth()), (int) (yR + getY() * UIProperties.getGridSize().getHeight()));
    }
    
    
    //Copied from CurveEdgeNode. Refactor so it is on one place!
    private static final float[][] styles = new float[][] {
        new float[] {1},
        new float[] {1},
        new float[] {3, 3},
        new float[] {1, 3},
        new float[] {0.1f, 1},
    };
    
    private BasicStroke basic = new BasicStroke();
    private void setStroke(Graphics2D g) {
        LineStyle l = getLineStyle();
        Stroke newStroke = new BasicStroke(basic.getLineWidth(), basic.getEndCap(), basic.getLineJoin(), basic.getMiterLimit(), styles[l.getStyle()], 0);
        
        g.setStroke(newStroke);
    }

    public void draw(Graphics2D g) {
        Color old = g.getColor();
        Stroke oldStroke = g.getStroke();
        
        setStroke(g);
        
        Dimension grid = UIProperties.getGridSize();
        
        int xGrid = (int) grid.getWidth();
        int yGrid = (int) grid.getHeight();
        
        if (isIsHidden())
            g.setColor(Color.LIGHT_GRAY);
        
        if (getLineStyle().getStyle() != LineStyle.NONE)
            g.drawOval(getX() * xGrid - getDiameter(), getY() * yGrid - getDiameter(), 2 * getDiameter(), 2 * getDiameter());
        
        if (isFinalState()) {
            g.drawOval(getX() * xGrid - getDiameter() + 2, getY() * yGrid - getDiameter() + 2, 2 * getDiameter() - 4, 2 * getDiameter() - 4);
        }
        
        if (isInitialState()) {
            g.drawLine(getX() * xGrid - getDiameter(), getY() * yGrid, getX() * xGrid - getDiameter() - 20, getY() * yGrid);
        }
        
        Rectangle2D bounds = g.getFontMetrics().getStringBounds(getName(), g);
        
        Point center = getPosition();
        
        double posX = center.getX() - bounds.getWidth() / 2;
        double posY = center.getY() + bounds.getHeight() / 2;
        
        g.setStroke(oldStroke);
        
        g.drawString(getName(), (int) posX, (int) posY);
        
        g.setColor(old);
        g.setStroke(oldStroke);
//        for (int cntr = 0; cntr <= 360; cntr += 45) {
//            Point p = getContourPoint(cntr);
//            
//            g.fillOval((int) p.getX() - 1, (int) p.getY() - 1, 2, 2);
//        }
    }
    
    public Rectangle getOuterDimension() {
        Dimension grid = UIProperties.getGridSize();
        
        int xGrid = (int) grid.getWidth();
        int yGrid = (int) grid.getHeight();
        
        return new Rectangle(getX() * xGrid - getDiameter(), getY() * yGrid - getDiameter(), 2 * getDiameter(), 2 * getDiameter());
    }
    
    public double distance(Point p) {
        double distance = Point.distance(p.getX(), p.getY(), getX() * UIProperties.getGridSize().getWidth(), getY() * UIProperties.getGridSize().getHeight());
        
        if (distance < getDiameter())
            return 0.0;
        else
            return distance - getDiameter();
    }
    
    public void outputVaucansonSource(PrintWriter out) {
        if (isIsHidden())
            out.println("\\HideState");
        
        LineStyle l = getLineStyle();
        
        if (l.getStyle() != LineStyle.DEFAULT) {
            out.println("\\ChgStateLineStyle{" + LineStyle.values[l.getStyle()] + "}");
        }
        
        if (isFinalState()) {
            out.print("\\FinalState");
        } else {
            out.print("\\State");
        }
        
        if (getName().length() > 0) {
            out.print("[" + getName() + "]");
        }
        out.print("{(");
        out.print(getX());
        out.print(",");
        out.print(-getY());
        out.print(")}");
        out.print("{");
        out.print(getID());
        out.println("}");
        
        if (isInitialState()) {
            out.print("\\Initial{");
            out.print(getID());
            out.println("}");
        }
        
        if (isIsHidden())
            out.println("\\ShowState");

        if (l.getStyle() != LineStyle.DEFAULT) {
            out.println("\\RstStateLineStyle");
        }
    }
    
    public Collection getStartingEdges() {
        Iterator iter = getStorage().getObjects().iterator();
        Collection result = new ArrayList();
        
        while (iter.hasNext()) {
            Object obj = iter.next();
            
            if (obj instanceof EdgeNode && ((EdgeNode) obj).getSource() == this) {
                result.add(obj);
            }
        }
        
        return result;
    }
    
    public Collection getEndingEdges() {
        Iterator iter = getStorage().getObjects().iterator();
        Collection result = new ArrayList();
        
        while (iter.hasNext()) {
            Object obj = iter.next();
            
            if (obj instanceof EdgeNode && ((EdgeNode) obj).getTarget() == this) {
                result.add(obj);
            }
        }
        
        return result;
    }
    
    /** Getter for property finalState.
     * @return Value of property finalState.
     *
     */
    public boolean isFinalState() {
        return finalState;
    }
    
    /** Setter for property finalState.
     * @param finalState New value of property finalState.
     *
     */
    public void setFinalState(boolean finalState) {
        this.finalState = finalState;
        firePropertyChange(PROP_FINAL_STATE, null, Boolean.valueOf(finalState));
    }
    
    /** Getter for property initialState.
     * @return Value of property initialState.
     *
     */
    public boolean isInitialState() {
        return initialState;
    }
    
    /** Setter for property initialState.
     * @param initialState New value of property initialState.
     *
     */
    public void setInitialState(boolean initialState) {
        this.initialState = initialState;
        firePropertyChange(PROP_INITIAL_STATE, null, Boolean.valueOf(initialState));
    }

    public LineStyle getLineStyle() {
        return lineStyle;
    }
    
    public void setLineStyle(LineStyle style) {
        this.lineStyle = style;
        firePropertyChange(PROP_LINE_STYLE, null, style);
    }

    protected Action[] createPopupMenu() {
        return new Action[] {
            new ToggleFinalAction(),
            new ToggleInitialAction(),
            null,
            new RemoveAction(),
            null,
            Editor.getSystemMenuFlag(),
            null,
            new PropertiesAction()
        };
    }
    
    private class ToggleFinalAction extends ToggleAction {
        
        public ToggleFinalAction() {
            super(new String[] {"Set final", "Set not final"}, StateNode.this.isFinalState() ? 1 : 0);
        }
        
        public void actionPerformed(ActionEvent e) {
            super.actionPerformed(e);
            StateNode.this.setFinalState(!StateNode.this.isFinalState());
            StateNode.this.redrawMe();
        }
        
    }
    
    private class ToggleInitialAction extends ToggleAction {
        
        public ToggleInitialAction() {
            super(new String[] {"Set initial", "Set not initial"}, StateNode.this.isInitialState() ? 1 : 0);
        }
        
        public void actionPerformed(ActionEvent e) {
            super.actionPerformed(e);
            StateNode.this.setInitialState(!StateNode.this.isInitialState());
            StateNode.this.redraw();
        }
        
    }
    
    private class PropertiesAction extends AbstractAction {
        
        public PropertiesAction() {
            super("Properties");
        }
        
        public void actionPerformed(ActionEvent e) {
            StateProperties properties = new StateProperties();
            
            properties.setLabel(getName());
            properties.setFinalState(isFinalState());
            properties.setInitialState(isInitialState());
        
            if (Utilities.showDialog("State Properties", properties) == JOptionPane.OK_OPTION) {
                setName(properties.getLabel());
                setFinalState(properties.getFinalState());
                setInitialState(properties.getInitialState());
                StateNode.this.redraw();
            }
        }
        
    }

    private boolean removed = false;
    
    private void removeEdges(Collection edges) {
        Iterator iter = edges.iterator();
        
        while (iter.hasNext()) {
            ((Node) iter.next()).remove();
        }
    }
    
    public void remove() {
        synchronized (this) {
            if (removed)
                return ;
            
            removed = true;
        }
        
        removeEdges(getStartingEdges());
        removeEdges(getEndingEdges());
        getStorage().removeObject(this);
    }
    
    /** Getter for property diameterIndex.
     * @return Value of property diameterIndex.
     *
     */
    public int getDiameterIndex() {
        return diameterIndex;
    }
    
    /** Setter for property diameterIndex.
     * @param diameterIndex New value of property diameterIndex.
     *
     */
    public void setDiameterIndex(int diameterIndex) {
        this.diameterIndex = diameterIndex;
    }
    
    /** Getter for property isHidden.
     * @return Value of property isHidden.
     *
     */
    public boolean isIsHidden() {
        return isHidden;
    }
    
    /** Setter for property isHidden.
     * @param isHidden New value of property isHidden.
     *
     */
    public void setIsHidden(boolean isHidden) {
        this.isHidden = isHidden;
    }
    
    public boolean equalsNode(Node node) {
        if (!super.equalsNode(node))
            return false;
        
        StateNode sn = (StateNode) node;
        
        return    getDiameterIndex() == sn.getDiameterIndex()
               && isFinalState() == sn.isFinalState()
               && isInitialState() == sn.isInitialState()
               && isIsHidden() == sn.isIsHidden()
               && getLineStyle().equals(sn.getLineStyle());
    }

}
