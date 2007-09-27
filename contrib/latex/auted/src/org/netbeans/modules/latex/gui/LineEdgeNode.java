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
    
    public double getSourceAngle() {
        int dx = getTarget().getX() - getSource().getX();
        int dy = getTarget().getY() - getSource().getY();
        
        return Math.toDegrees(Math.atan2(-dy, dx));
    }
    
    public double getSourceDistance() {
        return 0.0;
    }
    
    public double getTargetAngle() {
        int dx = getTarget().getX() - getSource().getX();
        int dy = getTarget().getY() - getSource().getY();
        
        return Math.toDegrees(Math.atan2(dy, -dx));
    }
    
    public double getTargetDistance() {
        return 0.0;
    }
    
    protected String getCommandBase() {
        return "\\Edge";
    }
    
    protected String getSpecialArgument() {
        return null;
    }
    
}
