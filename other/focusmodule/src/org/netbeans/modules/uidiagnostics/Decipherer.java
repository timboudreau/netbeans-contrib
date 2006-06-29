/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
/*
 * Decipherer.java
 *
 * Created on January 20, 2003, 6:11 PM
 */

package org.netbeans.modules.uidiagnostics;
import javax.swing.*;
import java.awt.event.*;
import java.util.*;
import java.awt.*;
import javax.swing.border.*;
/** A tool for diagnosing border issues by printing out the component
 *  tree for a given component.
 *
 * @author  Tim Boudreau
 */
class Decipherer extends JFrame implements AWTEventListener {

    /** Creates a new instance of Decipherer */
    public Decipherer() {
    }

    public void setComponent(Componentc) {
        if (component == c) return;
        component = c;
        update();
    }
    
    public void update () {
        Component c = component;
        if (c == null) return;
        while ((c != null) && !(c instanceof Frame)) {
            if (c instanceof JComponent) {
                outComp ((JComponent) c);
            } else {
                System.out.println(c);
            }
            if (c != null)
                c = c.getParent();
            else 
                c = null;
        }
    }
    
    Component component=null;
    int lastWidth=-1;
    int lastHeight=-1;
    private void outComp (JComponent c) {
        String cname = c.getClass().getName();
        Border b = c.getBorder();
        Insets i = null;
        if (b != null) 
            i = b.getBorderInsets(c);
        System.out.println("");
        int width = c.getWidth();
        int height = c.getHeight();
        if (height != lastHeight) {
            System.out.println("HEIGHT CHANGE: " + height);
        }
        if (width != lastWidth) {
            System.out.println("WIDTH CHANGE: " + width);
        }
        System.out.println(cname);
        System.out.println("Name: " + c.getName());
        System.out.println(i);
        System.out.println(b);
        if (c instanceof JTabbedPane) {
            JTabbedPane jt = (JTabbedPane) c;
            i = jt.getInsets();
            System.out.println("TabbedPane insets: " + i);
        }
        
        lastWidth = width;
        lastHeight = height;
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        final Decipherer d = new Decipherer();
        d.getContentPane().setLayout (new BorderLayout ());
        JButton jb = new JButton ("Go");
        d.getContentPane().add (jb);
        jb.addActionListener (new ActionListener () {
            public void actionPerformed (ActionEvent ae) {
                d.arm();
            }
        });
        d.setSize(300, 500);
        d.setLocation (20,20);
        d.show();
    }
    
    boolean armed=false;
    public void arm () {
        Toolkit.getDefaultToolkit().addAWTEventListener(this, AWTEvent.MOUSE_EVENT_MASK);
        armed = true;
    }
    
    public void disarm () {
        Toolkit.getDefaultToolkit().removeAWTEventListener(this);
        armed = false;
    }
    
    public void eventDispatched(AWTEvent event) {
        if (event instanceof MouseEvent) {
            if (((MouseEvent) event).getID() == MouseEvent.MOUSE_PRESSED) {
                Component c = ((MouseEvent) event).getComponent();
                disarm();
                setComponent (((MouseEvent) event).getComponent());
            }
        }
    }
    
}
