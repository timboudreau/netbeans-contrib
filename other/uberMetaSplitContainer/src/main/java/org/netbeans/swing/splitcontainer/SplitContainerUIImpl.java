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
 * SplitContainerUIImpl.java
 *
 * Created on May 3, 2004, 6:02 PM
 */

package org.netbeans.swing.splitcontainer;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

/**
 *
 * @author  Tim Boudreau
 */
public class SplitContainerUIImpl extends SplitContainerUI {
    private boolean continuous = true;

    /** Creates a new instance of SplitContainerUIImpl */
    public SplitContainerUIImpl(SplitContainer sp) {
        super (sp);
    }
    
    public static ComponentUI createUI (JComponent jc) {
        return new SplitContainerUIImpl ((SplitContainer) jc);
    }
    
    public void install() {
        ContainerMouseListener cml = new ContainerMouseListener();
        container.addMouseListener (cml);
        container.addMouseMotionListener (cml);
        container.setFocusable (false);
        //XXX AWT EVENT LISTENER TO ABORT DRAGGING ON FOCUS CHANGES
    }
    
    protected SplitLayoutModel createLayoutModel() {
        return new SplitLayoutModelImpl(container);
    }   
    
    
    private class ContainerMouseListener implements MouseListener, MouseMotionListener {
        private Interstice dragged = null;
        private Point lastPoint = null;

        public void mouseClicked(MouseEvent mouseEvent) {
        }
        
        public void mouseEntered(MouseEvent mouseEvent) {
            
        }
        
        public void mouseExited(MouseEvent mouseEvent) {
        }
        
        public void mousePressed(MouseEvent mouseEvent) {
            lastPoint = mouseEvent.getPoint();
            dragged = layoutModel.intersticeAtPoint (mouseEvent.getPoint());
        }
        
        public void mouseReleased(MouseEvent mouseEvent) {
            if (dragged != null) {
                Point p = mouseEvent.getPoint();
                int deltaX = p.x - lastPoint.x;
                int deltaY = p.y - lastPoint.y;
                lastPoint = p;
                layoutModel.move (dragged, deltaX, deltaY, true);
            }
            dragged = null;
            lastPoint = null;
        }
        
        public void mouseDragged(MouseEvent mouseEvent) {
            if (dragged != null && continuous) {
                Point p = mouseEvent.getPoint();
                int deltaX = p.x - lastPoint.x;
                int deltaY = p.y - lastPoint.y;
                lastPoint = p;
                layoutModel.move (dragged, deltaX, deltaY, false);
            }
        }
        
        public void mouseMoved(java.awt.event.MouseEvent mouseEvent) {
        }
        
    }
    
    
    
    
}
