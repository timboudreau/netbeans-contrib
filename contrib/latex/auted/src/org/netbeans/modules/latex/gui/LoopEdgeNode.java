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
 * Portions created by Jan Lahoda_ are Copyright (C) 2002-2007.
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
    
    public double getSourceAngle() {
        return directionAngle[direction] + 30;
    }
    
    public double getSourceDistance() {
        return 6.0;
    }
    
    public double getTargetAngle() {
        return directionAngle[direction] - 30;
    }
    
    public double getTargetDistance() {
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
