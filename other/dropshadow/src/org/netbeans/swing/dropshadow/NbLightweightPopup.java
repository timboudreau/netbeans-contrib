/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
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
