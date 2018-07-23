/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2009 Sun Microsystems, Inc. All rights reserved.
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
package org.netbeans.modules.fisheye;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import javax.swing.Icon;

/**
 *
 * @author Tim Boudreau
 */
final class Bubble {
    private String[] strings;
    private Rectangle bounds = new Rectangle();
    private FiMark mark;
    private int componentWidth;
    private int componentHeight;
    private int maxAscent = 0;
    
    public Bubble(FiMark mark, FontMetrics fm, int componentWidth, int componentHeight) {
        reset (mark, fm, componentWidth, componentHeight);
    }
    
    void reset (FiMark mark, FontMetrics fm, int componentWidth, int componentHeight) {
        this.mark = mark;
        this.componentWidth = componentWidth;
        this.componentHeight = componentHeight;
        this.maxAscent = fm.getMaxAscent();
    }

    private static final Stroke STROKE = new BasicStroke (1.5F);
    public Rectangle paint (Graphics2D g, Component comp, int y, float alpha) {
        Composite old = prepareGraphics(g, alpha);
        Dimension sz = paintText (g, 0, y, false, 1.0F);
        int x = componentWidth - sz.width;
        g.translate(comp.getWidth() - (sz.width + 50), 0);
        try {
            x = bounds.x = 0;
            int w = bounds.width = sz.width;
            int h = bounds.height = sz.height;
            
            g.translate (0, y);
            
            paintBubble (g, w + 65, h + 3);
            
            g.translate(20, 0);
            
            paintText (g, 15, 0, true, alpha);
            
            g.translate(-20, -y);
            Icon ic = mark.getIcon();
            if (ic != null) {
                ic.paintIcon(comp, g, x + 3, (y + (h / 2)) - ic.getIconHeight() / 2);
            }
            bounds.height += 6;
        } finally {
            g.translate (-1 * (comp.getWidth() - (sz.width + 50)), 0);
            if (old != null) {
                g.setComposite (old);
            }
        }
        return bounds;
    }
    
    private void paintBubble (Graphics2D g, int w, int h) {
        Color c = deriveColor (mark.getColor().brighter());
        GeneralPath bubble = createBubble(w, h);
        g.setPaint(c);
        g.fill (bubble);

        g.setPaint (Color.GRAY);
        g.setStroke(STROKE);
        g.draw (bubble);
    }
    
    private Color deriveColor(Color c) {
        float[] hsb = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
        if (hsb[2] > 0.7F) {
            hsb[2] = 0.7F;
            return new Color (Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]));
        } else {
            return c;
        }
    }

    private Dimension paintText (Graphics2D g, int x, int y, boolean paint, float alpha) {
        Composite oldComposite = g.getComposite();
        if (paint) {
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 
                    Math.min(1.0F, alpha + 0.2F)));
        }
        
        int max = (componentWidth / 3) * 2;
        bounds.width = max;
        g.setColor (Color.WHITE);
        String txt = mark.getDescription();
        FontMetrics fm = g.getFontMetrics();
        if (max < fm.getMaxAdvance()) {
            return new Dimension (0, 0);
        }
        y += maxAscent;
        String[] splits = txt.split ("\\s"); //NOI18N
        for (int i=0; i < splits.length; i++) {
            splits[i] += ' ';  //yuck
        }
        int pos = 0;
        int wid = -1;
        int ypos = y;
        int lines = 1;
        int maxWidth = 0;
        int currWidth = 0;
        for (int i = 0; i < splits.length; i++) {
            wid = fm.stringWidth(splits[i]);
            boolean cont;
            if (pos + wid < max) {
                if (paint) g.drawString(splits[i], pos, ypos);
                pos += wid;
                currWidth += wid;
                cont = true;
            } else if (wid >= max) {
                int pt = splits[i].length();
                while (pos + wid > bounds.width && pt > 1) {
                    pt--;
                    wid = fm.stringWidth(splits[i].substring(0, pt));
                    maxWidth = max;
                }
                if (paint) g.drawString(splits[i].substring(0, pt), pos, ypos);
                splits[i] = splits[i].substring(pt, splits[i].length());
                currWidth = Math.min (currWidth, max);
                i--;
                pos = 0;
                ypos += fm.getHeight();
                lines++;
                cont = true;
                //brutal wrap
            } else {
                ypos += fm.getHeight();
                lines++;
                pos = 0;
                if (paint) g.drawString(splits[i], pos, ypos);
                cont = true;
                currWidth = wid;
                pos = wid;
            }
            maxWidth = Math.max (maxWidth, currWidth);
            if (!cont) {
                currWidth = 0;
            }
        }
        Dimension result = new Dimension (maxWidth, lines * fm.getHeight());
        if (paint) g.setComposite(oldComposite);
        return result;
    }
    
    private Composite prepareGraphics (Graphics2D g, float alpha) {
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if (alpha >= 0F && alpha < 1.0F) {
            Composite old = g.getComposite();
            AlphaComposite comp = AlphaComposite.getInstance(
                    AlphaComposite.SRC_OVER, alpha);
            g.setComposite(comp);
            return old;
        } else {
            return null;
        }
    }
    
    private GeneralPath createBubble(int width, int height) {
        int right = width;
        int top = 30;
        int midy = top + (height / 2);
        int bottom = top + height;
        int rightin = width - 15;
        int rightout = width;        
        int left = 29;
        int leftin = left + 15;
        GeneralPath gp = new GeneralPath();
        gp.moveTo (left, midy);
        gp.quadTo (left, top, leftin, top);
        gp.lineTo (rightin, top);
        gp.quadTo (rightout, top, rightout, midy);
        gp.quadTo (rightout, bottom, rightin, bottom);
        gp.lineTo (leftin, bottom);
        gp.lineTo (leftin, bottom);
        gp.quadTo (left, bottom, left, midy);
        gp.closePath();
        Rectangle r = gp.getBounds();
        gp.transform(AffineTransform.getTranslateInstance(-r.x, -r.y));
        return gp;
    }    
}
