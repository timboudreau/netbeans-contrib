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
 */
package combobutton;

import combobutton.ComboButton.IconProvider;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

/**
 * Test app
 *
 * @author Tim Boudreau
 */
public class Main {
    
    /** Creates a new instance of Main */
    public Main() {
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
//        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        
        JFrame jf = new JFrame();
        jf.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        jf.getContentPane().setLayout (new FlowLayout());
        jf.add (new JLabel ("This is a ComboButton: "));
        FixedComboButton btn = new FixedComboButton();
        DefaultComboBoxModel mdl = new DefaultComboBoxModel();
        Action[] a = new Action[10];
        for (int i=0; i < a.length; i++) {
            a[i] = new A("Some sort of item " + i);
        }
        btn.setActions (a);
        btn.addActionListener(new AL());
//        btn.setModel(mdl);
//        btn.setTextVisible(true);
//        btn.setIconProvider(new IP(btn));
        jf.add (btn);
        jf.add (new JTextField ("Something else to give focus to"));
        jf.pack();
        jf.setVisible(true);
    }
    
    private static class AL implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            System.err.println("COMBO BOX FIRING ACTION " + e.getActionCommand());
        }
        
    }
    
    private static final class A extends AbstractAction {
        public A(String s) {
            putValue (Action.SMALL_ICON, new I());
            putValue (Action.NAME, s);
            putValue (Action.ACTION_COMMAND_KEY, s);
            putValue (Action.SHORT_DESCRIPTION, "Description of " + s);
        }
        
        public String toString() {
            return (String) getValue(Action.NAME);
        }

        public void actionPerformed(ActionEvent e) {
            System.err.println("ACTION PERFORMED: " + e.getActionCommand() + " on " + this);
        }
    }
    
    private static class IP implements IconProvider {
        private Map m = new HashMap();
        private ComboButton btn;
        IP (ComboButton btn) {
            this.btn = btn;
        }
        
        public Icon getIcon(Object objectInModel, int index) {
            index = index == -1 ? btn.getSelectedIndex() : index;
            Integer key = new Integer (index);
            Icon result = (Icon) m.get(key);
            if (result == null) {
                result = new I();
                m.put (key, result);
            }
            return result;
        }
    }
    
    private static Color genColor() {
        byte[] b = new byte[3];
        r.nextBytes(b);
        int[] ints = new int[b.length];
        for (int i = 0; i < b.length; i++) {
            ints[i] = Math.max (0, Math.min (255, b[i] < 0 ? 128 - b[i] : b[i]));
        }
        return new Color (ints[0], ints[1], ints[2]);
    }
    
    private static final Random r = new Random (System.currentTimeMillis());
    
    private static class I implements Icon, IconProvider {
        private Color color;
        public I() {
            color = genColor();
        }
        
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Polygon p = new Polygon (new int[] {
                x, x + 16, x,
            }, new int[] {
                y, y + 8, y + 16,
            }, 3);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor (color);
            g2d.fill (p);
            g2d.setColor (Color.BLACK);
            g2d.draw(p);
        }

        public int getIconWidth() {
            return 16;
        }

        public int getIconHeight() {
            return 16;
        }

        public Icon getIcon(Object objectInModel, int index) {
            return this;
        }        
    }
}
