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

import java.beans.PropertyEditorSupport;

/**
 *
 * @author Jan Lahoda
 */
public class LoopEdgeNode extends CurveEdgeNode {

    public static final int NORTH = 0;
    public static final int EAST  = 1;
    public static final int SOUTH = 2;
    public static final int WEST  = 3;

    public static final int NORTHEAST = 4;
    public static final int SOUTHEAST = 5;
    public static final int SOUTHWEST = 6;
    public static final int NORTHWEST = 7;
    
    private int direction;
    private String[] directionChar = new String[] {"N", "E", "S", "W", "NE", "SE", "SW", "NW"};
    private double[] directionAngle = new double[] {90, 0, 270, 180, 45, 315, 225, 135};
    
    /** Creates a new instance of LoopEdgeNode */
    public LoopEdgeNode(StateNode state) {
        super(state, state);
        this.direction = NORTH;
    }
    
    public int getDirection() {
        return direction;
    }
    
    public void setDirection(int direction) {
        this.direction = direction;
    }
    
    protected double getSourceAngle() {
        return directionAngle[direction] + 30;
    }
    
    protected double getSourceDistance() {
        return 6.0;
    }
    
    protected double getTargetAngle() {
        return directionAngle[direction] - 30;
    }
    
    protected double getTargetDistance() {
        return 6.0;
    }
    
    public String getOrientationString() {
        return directionChar[direction];
    }
    
    protected String getCommandBase() {
        return "\\Loop";
    }
    
    protected String getSpecialArgument() {
        return null;
    }

    protected boolean isOnlySource() {
        return true;
    }

    public boolean equalsNode(Node node) {
        if (!super.equalsNode(node))
            return false;
        
        LoopEdgeNode ln = (LoopEdgeNode) node;
        
        return getOrientation() == ln.getOrientation();
    }

    public static class DirectionPropertyEditor extends PropertyEditorSupport {
        
        private static final String[] values = {"North", "East", "South", "West", "North-East", "South-East", "South-West", "North-West"};
        
        private Integer value;
        
        public String getAsText() {
            if (value.intValue() < values.length)
                return values[value.intValue()];
            else
                return "<unsupported>";
        }
        
        public String[] getTags() {
            return values;
        }
        
        public Object getValue() {
            return value;
        }
        
        public void setAsText(String text) throws java.lang.IllegalArgumentException {
            for (int cntr = 0; cntr < values.length; cntr++) {
                if (values[cntr].equals(text)) {
                    value = new Integer(cntr);
                    
                    return ;
                }
            }
            throw new IllegalArgumentException("Unknown value: " + text);
        }
        
        public void setValue(Object value) {
            if (!(value instanceof Integer))
                throw new IllegalArgumentException("Expected java.lang.Integer, got=" + value.getClass());
            
            Integer integerValue = (Integer) value;
            
            if (   integerValue.intValue() < 0
                && integerValue.intValue() >= values.length) {
                throw new IllegalArgumentException("Values should be either -1 or 1.");
            }
            
            this.value = integerValue;
        }
        
    }

}
