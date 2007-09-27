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
