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
