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

package org.netbeans.modules.tasklist.timerwin;

import java.awt.Component;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import javax.swing.SwingUtilities;

/**
 * Dragging a window.
 *
 * @author tl
 */
public class MoveWindowMouseListener extends MouseAdapter implements 
        MouseMotionListener {
    private boolean mp;
    private Point p;
    private Component c;
    
    /** 
     * Creates a new instance of MoveWindowMouseListener.
     *
     * @param c window where this component resides will be dragged.
     */
    public MoveWindowMouseListener(Component c) {
        this.c = c;
        c.addMouseListener(this);
        c.addMouseMotionListener(this);
    }

    public void mouseMoved(java.awt.event.MouseEvent e) {
    }

    public void mouseDragged(java.awt.event.MouseEvent e) {
        if (mp) {
            // TAUtils.LOGGER.fine(e.getPoint() + " " + p);
            Window w = SwingUtilities.windowForComponent(c);
            w.setLocation(w.getX() + (e.getX() - p.x), 
                    w.getY() + (e.getY() - p.y));
        }
    }

    public void mousePressed(java.awt.event.MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            mp = true;
            p = e.getPoint();
            draggingStarted();
        }
    }

    public void mouseReleased(java.awt.event.MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            mp = false;
            draggingFinished();
        }
    }

    /**
     * Will be called when the dragging was finished.
     */
    private void draggingFinished() {
    }

    /**
     * Will be called when the dragging was started.
     */
    private void draggingStarted() {
    }
}
