/*
*                 Sun Public License Notice
*
* The contents of this file are subject to the Sun Public License
* Version 1.0 (the "License"). You may not use this file except in
* compliance with the License. A copy of the License is available at
* http://www.sun.com/
*
* The Original Code is NetBeans. The Initial Developer of the Original
* Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
* Microsystems, Inc. All Rights Reserved.
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
