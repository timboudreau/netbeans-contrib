/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
/*
 * TranslucentStatusDisplay.java
 *
 * Created on September 25, 2000, 6:30 PM
 */

package org.netbeans.modules.statuspopup;

import java.awt.AWTEvent;
/**
 *
 * @author  Tim Boudreau
 * @version 0.1
 */

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.awt.geom.Rectangle2D;
import java.awt.Rectangle;
import java.awt.Graphics2D;
import javax.swing.JComponent;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.Event;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeListener;
import org.openide.awt.StatusDisplayer;
import org.openide.windows.WindowManager;

class NbTranslucentStatusDisplay extends DisappearingTranslucentLabel implements ChangeListener {
    private boolean attached = false;
    private boolean added = false;
    private int lastWidth = 0;
    private int lastHeight = 0;
    private JComponent inst;
    /** Creates new TranslucentStatusDisplay */
    public NbTranslucentStatusDisplay() {
        setRequestFocusEnabled (false);
//        long mask = (Event.MOUSE_DOWN | Event.MOUSE_UP | Event.MOUSE_ENTER | Event.KEY_ACTION | Event.MOUSE_MOVE | Event.MOUSE_DRAG | Event.MOUSE_ENTER | Event.MOUSE_EXIT | Event.GOT_FOCUS | Event.LOST_FOCUS | Event.LIST_SELECT | java.awt.AWTEvent.KEY_EVENT_MASK | java.awt.AWTEvent.INPUT_METHOD_EVENT_MASK);
//        disableEvents (mask);
//        setHighlighter (null);
//        setKeymap (null);
//        setEditable (false);
//        setLineWrap (true);
        setMaximumSize (new java.awt.Dimension(500,1280)); //XXX get screen size
        
        disableEvents(AWTEvent.MOUSE_EVENT_MASK | AWTEvent.FOCUS_EVENT_MASK | 
            AWTEvent.KEY_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK | 
            AWTEvent.MOUSE_WHEEL_EVENT_MASK);
        
        setVisible(false);
//        setWrapStyleWord (true);
    }
    
    public void attach() {
        if (!attached) {
            StatusDisplayer.getDefault().addChangeListener(this);
            attached = true;
        }
    }
    
    public void detach() {
        if (attached) {
            StatusDisplayer.getDefault().removeChangeListener(this);
            attached=false;
        }
    }
    
    public void addNotify () {
        super.addNotify();
        attach();
    }
    
    public void removeNotify () {
        super.removeNotify();
        detach();
    }
    
    public void setText (String value) {
        if (value.equals(EmptyString)) {
          super.setText(value);
          setVisible (false);
        } else {
          super.setText(value);
          synchronized (getTreeLock()) {
            syncPosition();
            setVisible (true);
          }
        }
        repaint();
    }
    
    public void syncPosition () {
        JComponent inst = (JComponent)getParent();
        if (inst == null) return;
        int instHeight = inst.getHeight();
        int instWidth = inst.getWidth();
        int workingHeight;
        int workingWidth;
        int preferredWidth = getPreferredSize().width;
        int preferredHeight = getPreferredSize().height;
        
        setBounds (50, 10, preferredWidth+10, preferredHeight+10);
        
        Object o = org.openide.util.Lookup.getDefault().lookup(org.openide.awt.StatusDisplayer.class);
        System.err.println("Default status displayer is " + o);
    }
    
    public void stateChanged(javax.swing.event.ChangeEvent e) {
        String s = StatusDisplayer.getDefault().getStatusText();
        setText(s);
    }
}
