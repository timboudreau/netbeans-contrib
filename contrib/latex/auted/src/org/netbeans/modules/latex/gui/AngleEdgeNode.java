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

import java.awt.event.ActionEvent;
import java.awt.geom.Point2D;
import java.io.PrintWriter;
import javax.swing.Action;

/**
 *
 * @author Jan Lahoda
 */
public class AngleEdgeNode extends CurveEdgeNode {

    private int angle;

    /** Creates a new instance of AngleEdgeNode */
    public AngleEdgeNode(StateNode source, StateNode target) {
        super(source, target);
        angle = 1;
    }

    private double computeAngle(StateNode s, StateNode t, int sign) {
        double dx = t.getPosition().getX() - s.getPosition().getX();
        double dy = t.getPosition().getY() - s.getPosition().getY();
        
        double sourceAngle = Math.toDegrees(Math.atan2((-1) * sign * dy, sign * dx));
//        System.err.println("sourceAngle = " + sourceAngle );
        double result = sourceAngle - getOrientation() * sign * angle * 15.0;

//        System.err.println("result = " + result );
        return result;
    }
    
    public double getSourceAngle() {
        return computeAngle(getSource(), getTarget(), 1);
    }
    
    public double getSourceDistance() {
        return 0.8;
    }
    
    public double getTargetAngle() {
        return computeAngle(getSource(), getTarget(), -1);
    }
    
    public double getTargetDistance() {
        return 0.8;
    }
    
    public Action[] createPopupMenuAdder() {
        return new Action[] {
            new ToggleAngleAction(),
        };
    }
    
    /** Getter for property angle.
     * @return Value of property angle.
     *
     */
    public int getAngle() {
        return angle;
    }
    
    /** Setter for property angle.
     * @param angle New value of property angle.
     *
     */
    public void setAngle(int angle) {
        this.angle = angle;
    }
    
    protected String getCommandBase() {
        if (angle == 1)
            return "\\Arc";
        else
            return "\\LArc";
    }
    
    protected String getSpecialArgument() {
        return null;
    }
    
    private class ToggleAngleAction extends ToggleAction {
        public ToggleAngleAction() {
            super(new String[] {"Small angle", "Big angle"}, angle == 1 ? 1 : 0);
        }
        
        public void actionPerformed(ActionEvent e) {
            super.actionPerformed(e);
            angle = angle == 1 ? 2 : 1;
            AngleEdgeNode.this.redraw();
        }
    }

    public boolean equalsNode(Node node) {
        if (!super.equalsNode(node))
            return false;
        
        AngleEdgeNode an = (AngleEdgeNode) node;
        
        return    getAngle() == an.getAngle();
    }
}
