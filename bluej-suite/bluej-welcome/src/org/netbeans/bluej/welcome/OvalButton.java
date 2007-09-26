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

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import org.openide.util.Utilities;


/**
 * Transparent button with bullet rollover effect.
 *
 * @author Petr Kuzel
 */
public  class OvalButton extends JButton implements MouseListener {

    private float scale = 1.0f;

    // conatants dictated by graphics
    private static final int MAX_FONT = 18;
    private static final int MIN_FONT = 13;

    private boolean mouseOver;

    public OvalButton() {
        super();
        addMouseListener(this);        
    }

    
    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D)g;
        int xx = getWidth();
        int yy = getHeight();
        
        Image bullet;
        if (getModel().isArmed() || mouseOver) {
            bullet = Utilities.loadImage("org/netbeans/bluej/welcome/button-over.png");  // NOI18N
        } else {
            bullet = Utilities.loadImage("org/netbeans/bluej/welcome/button-gray.png");  // NOI18N
        }
        ImageIcon icon = new ImageIcon(bullet);
        int icon_h = icon.getIconHeight();
        int icon_w = icon.getIconWidth();
        g.drawImage(bullet, 0, (yy-icon_w)/2, this);
                
        int text_x = icon_w + 6;
        Font f = getFont();
        FontMetrics fm = g.getFontMetrics(f);
        g.setFont(f);
        g.setColor(Color.WHITE);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.drawString(getText(),text_x, yy/2 + (fm.getAscent()+fm.getDescent())/2 - fm.getDescent());
    }
    
    public void setScale(float scale) {
        this.scale = scale;
    }
    
    public Font getFont() {
        Font oldFont = super.getFont();
        if (oldFont!=null) {
            int size = Math.max(oldFont.getSize(), MIN_FONT);
            size *= scale;
            size = Math.min(size, MAX_FONT);
            Font newFont = new Font("SansSerif", Font.BOLD, size);  // NOI18N
            return newFont;
        } else {
            return null;
        }
    }

    public boolean isOpaque() { 
        return false;
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
        mouseOver = true;
        repaint(0);
    }

    public void mouseExited(MouseEvent e) {
        mouseOver = false;
        repaint(0);        
    }
    
}
