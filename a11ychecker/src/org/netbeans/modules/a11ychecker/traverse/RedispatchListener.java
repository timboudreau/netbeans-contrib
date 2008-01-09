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

package org.netbeans.modules.a11ychecker.traverse;
        
import java.awt.Component;
import java.awt.Container;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputAdapter;

/*
 * Listen for all mouse events and redispatch them. Used for making a component
 * active on an inactive glass pane.
 *
 * @author Michal Hapala, Pavel Stehlik
 */
class RedispatchListener extends MouseInputAdapter {
    Toolkit toolkit;
    Component liveButton;
    MyGlassPane glassPane;
    Container contentPane;
    
    public RedispatchListener(Component liveButton, MyGlassPane glassPane, Container contentPane) {
        toolkit = Toolkit.getDefaultToolkit();
        this.liveButton = liveButton;
        this.glassPane = glassPane;
        this.contentPane = contentPane;
    }
    
    public void mouseMoved(MouseEvent e) {
        redispatchMouseEvent(e, false);
    }
    
    public void mouseDragged(MouseEvent e) {
        redispatchMouseEvent(e, false);
    }
    
    public void mouseClicked(MouseEvent e) {
        redispatchMouseEvent(e, true);
    }
    
    public void mouseEntered(MouseEvent e) {
        redispatchMouseEvent(e, false);
    }
    
    public void mouseExited(MouseEvent e) {
        redispatchMouseEvent(e, false);
    }
    
    public void mousePressed(MouseEvent e) {
        redispatchMouseEvent(e, false);
    }
    
    public void mouseReleased(MouseEvent e) {
        redispatchMouseEvent(e, true);
    }
    
    //A basic implementation of redispatching events.
    private void redispatchMouseEvent(MouseEvent e,
            boolean repaint) {
        Point glassPanePoint = e.getPoint();
        Container container = contentPane;
        Point containerPoint = SwingUtilities.convertPoint(
                glassPane,
                glassPanePoint,
                contentPane);
        if (containerPoint.y < 0) { //we're not in the content pane
            
        } else {
            //The mouse event is probably over the content pane.
            //Find out exactly which component it's over.
            Component component =
                    SwingUtilities.getDeepestComponentAt(
                    container,
                    containerPoint.x,
                    containerPoint.y);
            
            if ((component != null)
            && (component.equals(liveButton))) {
                //Forward events over the check box.
                Point componentPoint = SwingUtilities.convertPoint(
                        glassPane,
                        glassPanePoint,
                        component);
                component.dispatchEvent(new MouseEvent(component,
                        e.getID(),
                        e.getWhen(),
                        e.getModifiers(),
                        componentPoint.x,
                        componentPoint.y,
                        e.getClickCount(),
                        e.isPopupTrigger()));
            }
        }
        
        //Update the glass pane if requested.
        if (repaint) {
            glassPane.repaint();
        }
    }
}
