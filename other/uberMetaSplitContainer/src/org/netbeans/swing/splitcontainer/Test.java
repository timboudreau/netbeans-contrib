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
 * Test.java
 *
 * Created on May 2, 2004, 4:01 PM
 */

package org.netbeans.swing.splitcontainer;

import java.awt.BorderLayout;
import java.awt.event.WindowListener;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.border.BevelBorder;

/**
 *
 * @author  Tim Boudreau
 */
public class Test {
    
    /** Creates a new instance of Test */
    public Test() {
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        JFrame jf = new JFrame();
        jf.getContentPane().setLayout (new BorderLayout());
        jf.addWindowListener (new WA());
        SplitContainer sc = new SplitContainer();
        sc.setBounds (0, 0, 380, 380);
        
        jf.getContentPane().add (sc, BorderLayout.CENTER);
        jf.setBounds (20, 20, 400, 400);

        for (int i=0; i < constraints.length; i++) {
            JTree jt = new JTree ();
            jt.setName (cnames[i]);
            jt.setBorder (BorderFactory.createBevelBorder(BevelBorder.LOWERED));
            sc.add (jt, constraints[i]);
        }
        
        jf.show();
    }
    
    private static Constraint[] constraints = new Constraint[] {
        Constraint.NORTHWEST, Constraint.NORTH, Constraint.NORTHEAST,
        Constraint.WEST, Constraint.CENTER, Constraint.EAST,
        Constraint.SOUTHWEST, Constraint.SOUTH, Constraint.SOUTHEAST
    };
    
    private static String[] cnames = new String[] {
        "northwest", "north", "northeast",
        "west", "center", "south",
        "southwest", "south", "southeast"
    };
    
    private static class WA implements WindowListener {
        
        public void windowActivated(java.awt.event.WindowEvent windowEvent) {
        }
        
        public void windowClosed(java.awt.event.WindowEvent windowEvent) {
        }
        
        public void windowClosing(java.awt.event.WindowEvent windowEvent) {
            System.exit(0);
        }
        
        public void windowDeactivated(java.awt.event.WindowEvent windowEvent) {
        }
        
        public void windowDeiconified(java.awt.event.WindowEvent windowEvent) {
        }
        
        public void windowIconified(java.awt.event.WindowEvent windowEvent) {
        }
        
        public void windowOpened(java.awt.event.WindowEvent windowEvent) {
        }
        
    }
    
}
