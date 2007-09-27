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
 * NbLightweightPopup.java
 *
 * Created on December 22, 2003, 9:50 PM
 */

package org.netbeans.swing.dropshadow;

import java.awt.Component;
import java.awt.Container;
import java.awt.Point;
import java.awt.Window;
import java.util.*;
import javax.swing.JApplet;
import javax.swing.JInternalFrame;
import javax.swing.JLayeredPane;
import javax.swing.JRootPane;
import javax.swing.Popup;
import javax.swing.SwingUtilities;

/** Lightweight popup implementation which will use an NbPopupComponent with
 * drop-shadow as the popup container.
 *
 * @author  Tim Boudreau   */
class NbLightweightPopup extends Popup {
    private NbPopupComponent comp=null;
    private Point p;
    Component owner;
    Component child;
    
    private static List cache = null;
    
    public static Popup getInstance (Component owner, Component child, Point p) {
        if (cache == null) {
            cache = new LinkedList();
        }
        NbLightweightPopup result;
        synchronized (cache) {
            if (cache.size() > 0) {
                result = (NbLightweightPopup) cache.get(0);
                cache.remove(0);
            } else {
                result = new NbLightweightPopup(owner, child, p);
            }
        }
        result.owner = owner;
        result.child = child;
        result.p = p;
        return result;
    }
    
    private static void recycle(Popup popup) {
        synchronized(cache) {
            if (cache.size() < 5) {
                cache.add(popup);
            }
        }
    }
    
    private NbLightweightPopup(Component owner, Component child, Point p) {
        this.owner = owner;
        this.p = p;
        this.child = child;
    }
    
    public void hide() {
        super.hide();
        NbPopupComponent component = getComponent();
        component.setVisible(false);
        component.setChild(null);
        component.removeAll(); //just in case
        owner = null;
        child = null;
        p = null;
        recycle(this);
    }
    
    public void show() {
        Container parent = null;
        
        if (owner != null) {
            parent = (owner instanceof Container? (Container)owner : owner.getParent());
        }
        
        // Try to find a JLayeredPane and Window to add
        for (Container p = parent; p != null; p = p.getParent()) {
            if (p instanceof JRootPane) {
                if (p.getParent() instanceof JInternalFrame) {
                    continue;
                }
                parent = ((JRootPane)p).getLayeredPane();
                // Continue, so that if there is a higher JRootPane, we'll
                // pick it up.
            } else if(p instanceof Window) {
                if (parent == null) {
                    parent = p;
                }
                break;
            } else if (p instanceof JApplet) {
                // Painting code stops at Applets, we don't want
                // to add to a Component above an Applet otherwise
                // you'll never see it painted.
                break;
            }
        }
        
        Point pt = new Point(this.p);
        SwingUtilities.convertPointFromScreen(pt, parent);
        
        NbPopupComponent component = getComponent();
        component.setChild(child);
        
        component.setLocation(pt.x, pt.y);
        if (parent instanceof JLayeredPane) {
            ((JLayeredPane)parent).add(component,
            JLayeredPane.POPUP_LAYER, 0);
        } else {
            parent.add(component);
        }
        component.setVisible(true);
        
        component.setSize(component.getPreferredSize());
        component.validate();
    }
    
    private NbPopupComponent createComponent() {
        NbPopupComponent result = new NbPopupComponent();
        return result;
    }    
    
    private NbPopupComponent getComponent() {
        if (comp == null) {
            comp = createComponent();
        }
        return comp;
    }
}
