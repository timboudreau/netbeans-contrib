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
 * bundlizer.java
 *
 * Created on April 30, 2004, 1:42 PM
 */

package org.netbeans.modules.bundlizer;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;

/**
 *
 * @author  tim
 */
public class Bundlizer {
    
    /** Creates a new instance of bundlizer */
    private Bundlizer() {
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        JFrame jf = new JFrame();
        jf.addWindowListener (new WindowAdapter() {
            public void windowClosed(WindowEvent e) {
                System.exit(0);
            }
        });
        jf.setTitle ("Bundlizer!");
        jf.setBounds (20,20, 600, 600);
        jf.getContentPane().add (new BundlizerPanel());
        jf.setVisible(true);
    }
    
}
