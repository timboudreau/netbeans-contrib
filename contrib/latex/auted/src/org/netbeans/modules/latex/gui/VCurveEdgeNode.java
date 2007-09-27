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
public class VCurveEdgeNode extends ControllableCurveEdgeNode {

    private double angleSource;
    private double angleTarget;
    private double curv;

    /** Creates a new instance of AngleEdgeNode */
    public VCurveEdgeNode(StateNode source, StateNode target) {
        super(source, target);
        angleSource = 0.0;
        angleTarget = 0.0;
        curv  = 1.0;
    }
    
    public double getSourceAngle() {
        return getAngleSource();
    }
    
    public double getSourceDistance() {
        return curv;
    }
    
    public double getTargetAngle() {
        return getAngleTarget();
    }
    
    public double getTargetDistance() {
        return curv;
    }
    
    /** Getter for property curv.
     * @return Value of property curv.
     *
     */
    public double getCurv() {
        return curv;
    }
    
    /** Setter for property curv.
     * @param curv New value of property curv.
     *
     */
    public void setCurv(double curv) {
        this.curv = curv;
    }
    
    /** Getter for property angleSource.
     * @return Value of property angleSource.
     *
     */
    public double getAngleSource() {
        return angleSource;
    }
    
    /** Setter for property angleSource.
     * @param angleSource New value of property angleSource.
     *
     */
    public void setAngleSource(double angleSource) {
        this.angleSource = angleSource;
    }
    
    /** Getter for property angleTarget.
     * @return Value of property angleTarget.
     *
     */
    public double getAngleTarget() {
        return angleTarget;
    }
    
    /** Setter for property angleTarget.
     * @param angleTarget New value of property angleTarget.
     *
     */
    public void setAngleTarget(double angleTarget) {
        this.angleTarget = angleTarget;
    }
        
    public void setSourceControlPoint(Point2D value) {
        double[] vals = getValuesForControlPoint(getSource(), value);
        
        setAngleSource(vals[0]);
        setCurv(vals[1]);
        firePropertyChange(PROP_SOURCE_CONTROL, null, null);
    }
    
    public void setTargetControlPoint(Point2D value) {
        double[] vals = getValuesForControlPoint(getTarget(), value);
        
        setAngleTarget(vals[0]);
        setCurv(vals[1]);
        firePropertyChange(PROP_TARGET_CONTROL, null, null);
    }
    
    protected String getCommandBase() {
        return "\\VCurve";
    }
    
    protected String getSpecialArgument() {
        return "angleA=" + printDouble(getSourceAngle()) + ",angleB=" + printDouble(getTargetAngle()) + ",ncurv=" + printDouble(getCurv());
    }
    
    public boolean equalsNode(Node node) {
        if (!super.equalsNode(node))
            return false;
        
        VCurveEdgeNode van = (VCurveEdgeNode) node;
        
        return    getAngleSource() == van.getAngleSource()
               && getAngleTarget() == van.getAngleTarget()
               && getCurv() == van.getCurv();
    }

}
