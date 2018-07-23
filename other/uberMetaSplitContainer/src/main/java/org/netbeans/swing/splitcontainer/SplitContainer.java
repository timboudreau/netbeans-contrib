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
* The Original Software is NetBeans. The Initial Developer of the Original
* Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
* Microsystems, Inc. All Rights Reserved.
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
*/
/*
 * SplitContainer.java
 *
 * Created on May 2, 2004, 4:48 PM
 */

package org.netbeans.swing.splitcontainer;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import javax.swing.JComponent;

/**
 * A multiple split - split container.
 *
 * @author  Tim Boudreau
 */
public class SplitContainer extends JComponent {
    private SplitLayoutModel layoutModel = null;
    private int gap = 8;

    /** Creates a new instance of SplitContainer */
    public SplitContainer() {
        updateUI();
    }
    
    public void updateUI() {
        SplitContainerUI ui = new SplitContainerUIImpl(this);
        setUI (ui);
    }
    
    void setLayoutModel (SplitLayoutModel mdl) {
        this.layoutModel = mdl;
    }
    
    public SplitLayoutModel getLayoutModel() {
        return layoutModel;
    }
    
    public boolean isValidateRoot() {
        return true;
    }
    
    /**
     * Spacing for splitters. <strong>This value must be an even number </strong>
     */
    public int getGap() {
        return gap;
    }
    
    protected void addImpl(Component comp, Object constraints, int idx) {
        if (!(constraints instanceof Constraint)) {
            throw new IllegalArgumentException ("Constraint must be an " +
                "instance of org.netbeans.swing.splitcontainer.Constraint");
        }
        if (layoutModel == null) {
            throw new NullPointerException ("Layout model is null");
        }
        Rectangle r = ((Constraint) constraints).getBounds(getSize());
        int gp = getGap() / 2;
        r.x += gp;
        r.y += gp;
        r.width -= getGap();
        r.height -= getGap();
        ((SplitLayoutModelImpl) layoutModel).putBounds (comp, r);
        super.addImpl (comp, constraints, idx);
    }
    
    public boolean isOptimizedDrawingEnabled() {
        return true;
    }
    
    public IntersticeFactory getIntersticeFactory() {
        
        AWTEvent eo = EventQueue.getCurrentEvent();
        if (eo instanceof MouseEvent) {
            MouseEvent me = (MouseEvent) eo;
            if (me.isAltDown()) {
                return new NearestNeighborIntersticeFactory();
            } else {
                return new LineOfSightIntersticeFactory();
            }
        }
         
        return new NearestNeighborIntersticeFactory(); //XXX
    }
    
    
}
