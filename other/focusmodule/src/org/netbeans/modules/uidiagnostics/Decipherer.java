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
