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
