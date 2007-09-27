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
 * DropShadowBorder.java
 *
 * Created on December 22, 2003, 9:06 PM
 */

package org.netbeans.swing.dropshadow;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Composite;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Paint;
import javax.swing.UIManager;
import javax.swing.border.Border;

/** A translucent drop shadow border class with settable colors and size.
 * Generally it's a goood idea to use a transparent color as the second
 * color.  The default size is 5 pixels, and the default colors are 
 * <code>UIManager.getColor(&quot;controlDkShadow&quot;)</code> for the
 * darker portion of the shadow, fading towards transparent white.
 *
 * @author  Tim Boudreau   */
public final class DropShadowBorder implements Border {
    int offset = 5;
    /**
     * UIManager key for the Integer default size/offset if unspecified.  If not
     * present in UIManager, the default size/offset is 5 pixels.  The size
     * can be globally changed for all drop shadows that do not have a 
     * value set in their constructor by calling <code>UIManager.put(DropShadowBorder.UI_KEY_SHADOWSIZE,
     * new Integer (yourValueHere)</code>.
     */    
    public static final String UI_KEY_SHADOWSIZE="nb.dropShadowSize"; //NOI18N
    /** Scratch array for x coordinates to avoid extra allocations while painting */
    private static final int[] xpoints = new int[5];
    /** Scratch array for y coordinates to avoid extra allocations while painting */
    private static final int[] ypoints = new int[5];
    /** Default light shadow color if none is specified */
    private static final Color DEFAULT_SHADOWLIGHT=new Color(255, 255, 255, 0);
    /** Field for the light shadow color */
    private Color shadowLight = DEFAULT_SHADOWLIGHT;
    /** Field for the dark shadow color */
    private Color shadowDark = null;
    /** AlphaComposite doesn't cache non 1.0 alpha instances, so we will */
    private static final Composite transparency =
        AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f);
    
    /** Creates a new instance of DropShadowBorder with default settings */
    public DropShadowBorder() {
        Integer in = (Integer) UIManager.get(UI_KEY_SHADOWSIZE);
        if (in != null) {
            offset = in.intValue();
        }
    }
    
    /**
     * The size/offset for this drop shadow (the distance from the corners
     * and width/height of the shadow.
     * @param size The size
     */    
    public DropShadowBorder(int size) {
        offset = size;
    }
    
    /**
     * Create a drop shadow using the specified colors.  Note that it may
     * not look shadow-like if the value for <code>shadowDark</code> is
     * not transparent (the default), or nearly transparent.
     * @param shadowDark The color for darker regions of the shadow.
     * @param shadowLight The color the shadow should fade towards as it moves
     * away from the component.     */    
    public DropShadowBorder(Color shadowDark, Color shadowLight) {
        this();
        this.shadowDark = shadowDark;
        this.shadowLight = shadowLight;
    }
    
    /**
     * Specify a drop shadow with custom colors and size.
     * @param shadowDark The color of the dark portion of the shadow.
     * @param shadowLight The color the shadow should fade towards (transparent or
     * nearly transparent colors are usually best).
     * @param size The number of pixels from the edge of the component at
     * which the shadow should appear and extend.   */    
    public DropShadowBorder(Color shadowDark, Color shadowLight, int size) {
        this(size);
        this.shadowDark = shadowDark;
        this.shadowLight = shadowLight;
    }
    
    /** Get the offset in pixels of the shadow from the component, which
     * is also its thickness */
    public int getOffset() {
        return offset;
    }
    
    private Color getShadowLight() {
        return shadowLight;
    }
    
    private Color getShadowDark() {
        Color result = null;
        if (shadowDark == null) {
            result = UIManager.getColor("controlDkShadow"); //NOI18N
        } else {
            result = shadowDark;
        }
        return result;
    }
    
    public Insets getBorderInsets(Component c) {
        return new Insets(0,0,offset,10);
    }
    
    public boolean isBorderOpaque() {
        return false;
    }
    
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2d = (Graphics2D) g;
        Composite comp = g2d.getComposite();
        Paint paint = g2d.getPaint();
        try {
            g2d.setComposite(transparency);

            //draw the bottom portion with bottom edge wider and left and
            //right edges forming 90 degree angles (we'll paint in the
            //remaining bits later, so the gradient looks smooth at the
            //short edges of the shadow).  Use our scratch array to avoid
            //allocating any memory we don't need to.
            xpoints[0] = x + (offset * 2);
            xpoints[1] = x + offset;
            xpoints[2] = x + width;
            xpoints[3] = x + width - offset;
            
            ypoints[0] = y + height-offset;
            ypoints[1] = y + height;
            ypoints[2] = y + height;
            ypoints[3] = y + height-offset;

            g2d.setPaint(new GradientPaint(x+offset, y+height-offset, getShadowDark(), x+offset, y+height, getShadowLight()));
            g.fillPolygon(xpoints,ypoints, 4);
            
            xpoints[0] = x+width-offset;
            xpoints[1] = x + width;
            xpoints[2] = x + width;
            xpoints[3] = x + width - offset;
            
            ypoints[0] = y+(offset*2);
            ypoints[1] = y + offset;
            ypoints[2] = y + height;
            ypoints[3] = y + height-offset;
            
            g2d.setPaint(new GradientPaint(x+width-offset, 
                y+height-offset, getShadowDark(), x+width, y+height-offset, 
                getShadowLight()));

            g.fillPolygon(xpoints,ypoints, 4);


            xpoints[0] = x + (offset * 2);
            xpoints[1] = x + offset;
            xpoints[2] = x + offset;
            
            ypoints[0] = y + height-offset;
            ypoints[1] = y + height;
            ypoints[2] = y + height-offset;
            
            g2d.setPaint(new GradientPaint(x + (offset * 2), y+height-offset, 
                getShadowDark(), x + offset, y+height, getShadowLight()));
            
            g.fillPolygon(xpoints,ypoints, 3);
            
            xpoints[0] = x+width-offset;
            xpoints[1] = x + width;
            xpoints[2] = x + width - offset;
            
            ypoints[0] = y+(offset*2);
            ypoints[1] = y + offset;
            ypoints[2] = y + offset;
            
            g2d.setPaint(new GradientPaint(x+width-offset, y+(offset * 2), 
                getShadowDark(), x+width, y+offset, getShadowLight()));
            
            g.fillPolygon(xpoints,ypoints, 3);
            
        } finally {
            g2d.setComposite(comp);
            g2d.setPaint(paint);
        }
    }
}
