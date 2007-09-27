/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is the LaTeX module.
 * The Initial Developer of the Original Software is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002,2003.
 * All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.gui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;

/**
 *
 * @author Jan Lahoda
 */
public abstract class EdgeNode extends NamedNode {

    public static final int LEFT  = -1;
    public static final int RIGHT =  1;
    public static final String PROP_LINE_STYLE = "lineStyle";
    public static final String PROP_LABEL_POSITION  = "labelPosition";
    public static final String PROP_ORIENTATION = "orientation";
    public static final String PROP_BORDER = "border";
    
    private StateNode source;
    
    private StateNode target;
    
    private double    labelPosition;
    
    private int       orientation;
    
    private boolean   border;
    
    private LineStyle lineStyle;
    
    /** Creates a new instance of EdgeNode */
    public EdgeNode(StateNode source, StateNode target) {
        this.source = source;
        this.target = target;
        labelPosition = 0.45;
        orientation = LEFT;
        lineStyle = new LineStyle(LineStyle.DEFAULT);
    }
    
    public abstract Point2D getPoint(double place);
    
    /** Getter for property source.
     * @return Value of property source.
     *
     */
    public StateNode getSource() {
        return source;
    }
    
    /** Getter for property target.
     * @return Value of property target.
     *
     */
    public StateNode getTarget() {
        return target;
    }
    
    public double distance(Point p) {
        return Double.POSITIVE_INFINITY;
    }
    
    /** Getter for property labelPosition.
     * @return Value of property labelPosition.
     *
     */
    public double getLabelPosition() {
        return labelPosition;
    }
    
    /** Setter for property labelPosition.
     * @param labelPosition New value of property labelPosition.
     *
     */
    public void setLabelPosition(double labelPosition) {
        this.labelPosition = labelPosition;
        firePropertyChange(PROP_LABEL_POSITION, null, null);
    }
    
    private boolean removed = false;
    public void remove() {
        synchronized (this) {
        if (removed)
            return ;
        
        removed = true;
        }
        
        getStorage().removeObject(this);
    }
    
    /** Getter for property orientation.
     * @return Value of property orientation.
     *
     */
    public int getOrientation() {
        return orientation;
    }
    
    /** Setter for property orientation.
     * @param orientation New value of property orientation.
     *
     */
    public void setOrientation(int orientation) {
        this.orientation = orientation;
        firePropertyChange(PROP_ORIENTATION, null, null);
    }
    
    private static final double arrowLength = 10; //TODO: temporar only
    private static final double arrowAngle  = 30; //TODO: temporar only.
    protected void drawArrow(Graphics2D g, Point2D pos, double angle) {
        double sX = pos.getX();
        double sY = pos.getY();
        double tX = sX + arrowLength * Math.cos(Math.toRadians(angle + arrowAngle));
        double tY = sY + -arrowLength * Math.sin(Math.toRadians(angle + arrowAngle));
        
//        System.err.println("sX = " + sX );
//        System.err.println("sY = " + sY );
//        System.err.println("tX = " + tX );
//        System.err.println("tY = " + tY );
        g.draw(new Line2D.Double(sX, sY, tX, tY));
        
        tX = sX + arrowLength * Math.cos(Math.toRadians(angle - arrowAngle));
        tY = sY + -arrowLength * Math.sin(Math.toRadians(angle - arrowAngle));

//        System.err.println("tX = " + tX );
//        System.err.println("tY = " + tY );
        g.draw(new Line2D.Double(sX, sY, tX, tY));
    }
    
    /**
     * Getter for property border.
     * @return Value of property border.
     */
    public boolean isBorder() {
        return border;
    }
    
    /**
     * Setter for property border.
     * @param border New value of property border.
     */
    public void setBorder(boolean border) {
        this.border = border;
        firePropertyChange(PROP_BORDER, null, null);
    }
    
    public LineStyle getLineStyle() {
        return lineStyle;
    }
    
    public void setLineStyle(LineStyle style) {
        this.lineStyle = style;
        firePropertyChange(PROP_LINE_STYLE, null, style);
    }
    
    public static class OrientationPropertyEditor extends PropertyEditorSupport {
        
        private static final String[] values = {"Left", "Right"};
        
        private Integer value;
        
        public String getAsText() {
            switch (value.intValue()) {
                case LEFT:
                    return values[0];
                case RIGHT:
                    return values[1];
                default:
                    return "<unsupported>";
            }
        }
        
        public String[] getTags() {
            return values;
        }
        
        public Object getValue() {
            return value;
        }
        
        public void setAsText(String text) throws java.lang.IllegalArgumentException {
            if (values[0].equals(text))
                value = new Integer(-1);
            else
                if (values[1].equals(text))
                    value = new Integer(1);
                else
                    throw new IllegalArgumentException("Unknown value: " + text);
        }
        
        public void setValue(Object value) {
            if (!(value instanceof Integer))
                throw new IllegalArgumentException("Expected java.lang.Integer, got=" + value.getClass());
            
            this.value = (Integer) value;
            
            if (   this.value.intValue() !=   1
                && this.value.intValue() != (-1)) {
                throw new IllegalArgumentException("Values should be either -1 or 1.");
            }
        }
        
    }

    public boolean equalsNode(Node node) {
        if (!super.equalsNode(node))
            return false;
        
        EdgeNode sn = (EdgeNode) node;
        
        return    getSource().equalsNode(sn.getSource())
               && getTarget().equalsNode(sn.getTarget())
               //TODO: the following is double. there should probably bee some epsilon:
               && getLabelPosition() == sn.getLabelPosition()
               && getOrientation() == sn.getOrientation()
               && isBorder() == sn.isBorder()
               && getLineStyle().equals(sn.getLineStyle());
    }
}
