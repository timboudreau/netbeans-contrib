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

package org.netbeans.bluej.welcome;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.LayoutManager;
import java.awt.Paint;
import java.awt.Point;
import java.awt.geom.Point2D;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import org.openide.util.Utilities;

/**
 * Wecome screen UI, mostly assembled at runtime. It has custom:
 * <ul>
 *   <li>paint() method to draw background
 *   <li>layout manager to precisely place components on background image
 * </ul>
 *
 * <p>The cod eit full of static constants to avoid expensive
 * runtime computations (during startup).
 *
 * @author  Petr Kuzel
 */
public class TitlePanel50 extends javax.swing.JPanel implements Runnable, java.awt.event.ActionListener, java.awt.event.MouseListener, LayoutManager {
    
    private final ImageIcon background;
    private int backgroundWidth = -1;
    private int backgroundHeight = -1;
    
    private static final Color TOP_COLOR = new Color(224,221,209);  // measured from gradient.png
    private static final Color BOT_COLOR = new Color(217,207,174);  // measured from gradient.png
    
    private static final int BUTTONS_X_OFFSET = 380; // taken from measuring background.png
    
    // it's tiled, values measured from gradient.png
    private static final int GRADIENT_WIDTH = 53;
    private static final int GRADIENT_HEIGHT = 89;
    
    // background has tranparent edge
    private static final int TRANSPARENT_EDGE_WIDTH = 15;
    
    public TitlePanel50() {
        background = new ImageIcon(Utilities.loadImage("org/netbeans/bluej/welcome/background.png"));  // NOI18N
        initComponents();
        
        ((OvalButton)jButton2).setScale(1.333f);
        setLayout(this);
        setOpaque(false);        
    }

    /** Paint custom background. */
    public void paint(Graphics g) {
        
//        long start = System.currentTimeMillis();
        
        Graphics2D g2d = (Graphics2D)g;
        int xx = getWidth();
        int yy = getHeight();

        int offset_x = (xx - backgroundWidth())/2;
        int offset_y = (yy - backgroundHeight())/2;
                
        // gradient background
        g2d.setColor(TOP_COLOR);
        g2d.fillRect(0, 0, xx, (yy-GRADIENT_HEIGHT)/2);
        Image image = Utilities.loadImage("org/netbeans/bluej/welcome/gradient.png");  // NOI18N
        for (int i = 0; i<xx; i+=GRADIENT_WIDTH) {
            if (i > (offset_x + TRANSPARENT_EDGE_WIDTH) && i< (offset_x + backgroundWidth() - GRADIENT_WIDTH - TRANSPARENT_EDGE_WIDTH)) {
                continue;
            }
            g2d.drawImage(image, i, (yy-GRADIENT_HEIGHT)/2, this);
        }        
        g2d.setColor(BOT_COLOR);
        g2d.fillRect(0, (yy+GRADIENT_HEIGHT)/2, xx, yy);        

        
        g.drawImage(background.getImage(), offset_x, offset_y, this);

        pc(jButton2, g);
        pc(jButton3, g);
        pc(jButton4, g);
        pc(jLabel3, g);        
        
//        long end = System.currentTimeMillis();
//        System.err.println("Paint:" + (end - start) + "ms");
    }
    
    private void pc (JComponent jc, Graphics g) {
        int x = jc.getX();
        int y = jc.getY();
        g.translate (x, y);
        jc.paint(g);
        g.translate(-x, -y);
    }    
    
    private int backgroundWidth() {
        if (backgroundWidth == -1) {
            backgroundWidth = background.getIconWidth();
        }
        return backgroundWidth;
    }

    private int backgroundHeight() {
        if (backgroundHeight == -1) {
            backgroundHeight = background.getIconHeight();
        }
        return backgroundHeight;
    }
    
    public void addNotify() {
        super.addNotify();
        //Get cursor lookup out of the way
        SwingUtilities.invokeLater(this);
    }
    
    public void run() {
        //XXX better to do this on a timer, but I can't override
        //actionPerformed() and I don't want to create another class
        Cursor cur = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
        jLabel3.setCursor(cur);
        jButton2.setCursor(cur);
        jButton3.setCursor(cur);
        jButton4.setCursor(cur);
    }    
    /**
     * Layout driven by background image.
     *
     * There is a horizontal line and two button groups: 
     * <ul>
     *  <li> two big buttons above the line
     *  <li> four small buttons under the line
     * </ul>
     * The line position is constant and buttons
     * should expand out it. There are following boxes:
     *
        <pre>     

                                      1

                                 222222
                333333           222222
            ---------------------------------
                                 444444
                                 444444
                                 444444
                55               444444



          1  is for Java logo:      44x26
          2s are for big buttons:  22x200
          3  is for product logo:  35x160
          4s are small buttons:    18x200
          5  is for Sun logo:       31x71
          --- stands for background golden "base" line
        </pre>
     */    
//    private class TitleLayout implements LayoutManager {
        public void addLayoutComponent(String name, Component comp) {
        }

        public void removeLayoutComponent(Component comp) {
        }

        public Dimension preferredLayoutSize(Container parent) {
            return new Dimension(backgroundWidth(), backgroundHeight());
        }

        public Dimension minimumLayoutSize(Container parent) {
            return preferredLayoutSize(parent);
        }

        public void layoutContainer(Container parent) {
            
            int offset_x = (parent.getSize().width - backgroundWidth())/2;
            int offset_y = (parent.getSize().height - backgroundHeight())/2;
            
            int base_x = offset_x + BUTTONS_X_OFFSET;  
            int base_y = offset_y + 287;  // taken from background.png            
            int SMALL_SPACE = 5;
            int NB_LOGO_ALIGN = 4;
            int BOT_GROUP_ALIGN = 16;  // 12 is real align with Sun logo by Leos said that 16 is optical align
            int TOP_BUTTON_HEIGHT = 22;
            int BOT_BUTTON_HEIGHT = 18;
            
            int y = base_y;
            int w = maxWidth();           
            
            y -= TOP_BUTTON_HEIGHT + SMALL_SPACE + NB_LOGO_ALIGN;
            jButton2.setLocation(base_x, y);
            resize(jButton2, w, TOP_BUTTON_HEIGHT);                        
            
//            y -= TOP_BUTTON_HEIGHT + SMALL_SPACE;
//            jButton1.setLocation(base_x, y);
//            resize(jButton1, w, TOP_BUTTON_HEIGHT);                        

            // horizontal line = base_y

            y = base_y + SMALL_SPACE + BOT_GROUP_ALIGN;
            jButton3.setLocation(base_x, y);
            resize(jButton3, w, BOT_BUTTON_HEIGHT);                        
            
            y += BOT_BUTTON_HEIGHT + SMALL_SPACE;
            jButton4.setLocation(base_x, y);
            resize(jButton4, w, BOT_BUTTON_HEIGHT);                        
                                    
            // NB logo
            jLabel3.setSize(160, 35); // measured image size
            jLabel3.setLocation(50 + offset_x, 246 + offset_y);            
            
        }

        private void resize(JComponent target, int width, int height) {
            target.setSize(width, height);
        }
        
        private int maxWidth() {
            return backgroundWidth() -  BUTTONS_X_OFFSET - TRANSPARENT_EDGE_WIDTH;  // 15pixels margin taken from measuring background.png
        }
        
//    }
    
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        jButton2 = new OvalButton();
        jButton3 = new OvalButton();
        jButton4 = new OvalButton();
        jLabel3 = new javax.swing.JLabel();

        org.openide.awt.Mnemonics.setLocalizedText(jButton2, org.openide.util.NbBundle.getMessage(TitlePanel50.class, "BK0002"));
        jButton2.setFocusable(false);
        jButton2.addActionListener(this);

        add(jButton2);

        org.openide.awt.Mnemonics.setLocalizedText(jButton3, org.openide.util.NbBundle.getMessage(TitlePanel50.class, "BK0003"));
        jButton3.setFocusable(false);
        jButton3.addActionListener(this);

        add(jButton3);

        org.openide.awt.Mnemonics.setLocalizedText(jButton4, org.openide.util.NbBundle.getMessage(TitlePanel50.class, "BK0004"));
        jButton4.setFocusable(false);
        jButton4.addActionListener(this);

        add(jButton4);

        jLabel3.addMouseListener(this);

        add(jLabel3);

    }

    // Code for dispatching events from components to event handlers.

    public void actionPerformed(java.awt.event.ActionEvent evt) {
        if (evt.getSource() == jButton2) {
            TitlePanel50.this.jButton2ActionPerformed(evt);
        }
        else if (evt.getSource() == jButton3) {
            TitlePanel50.this.jButton3ActionPerformed(evt);
        }
        else if (evt.getSource() == jButton4) {
            TitlePanel50.this.jButton4ActionPerformed(evt);
        }
    }

    public void mouseClicked(java.awt.event.MouseEvent evt) {
        if (evt.getSource() == jLabel3) {
            TitlePanel50.this.jLabel3MouseClicked(evt);
        }
    }

    public void mouseEntered(java.awt.event.MouseEvent evt) {
    }

    public void mouseExited(java.awt.event.MouseEvent evt) {
    }

    public void mousePressed(java.awt.event.MouseEvent evt) {
    }

    public void mouseReleased(java.awt.event.MouseEvent evt) {
    }// </editor-fold>//GEN-END:initComponents

    private void jLabel3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel3MouseClicked
        BusinessLogic.perform(103, this);
    }//GEN-LAST:event_jLabel3MouseClicked

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        BusinessLogic.perform(4, this);
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        BusinessLogic.perform(3, this);
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        BusinessLogic.perform(2, this);
    }//GEN-LAST:event_jButton2ActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JLabel jLabel3;
    // End of variables declaration//GEN-END:variables
    
}
