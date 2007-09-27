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
 * InstructionsPanel.java
 *
 * Created on March 4, 2005, 8:59 PM
 */

package org.netbeans.modules.wizard;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import javax.accessibility.Accessible;
import javax.swing.JComponent;
import javax.swing.UIManager;
import org.netbeans.spi.wizard.Wizard;
import org.netbeans.spi.wizard.Wizard.WizardListener;

/**
 * A panel that displays a background image and optionally instructions
 * from a wizard, tracking the selected panel and showing that in bold.
 * <p>
 * <b><i><font color="red">This class is NOT AN API CLASS.  There is no
 * commitment that it will remain backward compatible or even exist in the
 * future.  The API of this library is in the packages <code>org.netbeans.api.wizard</code>
 * and <code>org.netbeans.spi.wizard</code></font></i></b>.
 *
 * @author Tim Boudreau
 */
public class InstructionsPanel extends JComponent implements WizardListener {
    private final BufferedImage img;
    private final Wizard wizard;

    public InstructionsPanel (Wizard wiz) {
        this (null, wiz);
        Font f = UIManager.getFont ("Tree.font");
        if (f != null) {
            setFont (f);
        }
    }
    
    public void addNotify() {
        super.addNotify();
        wizard.addWizardListener (this);
    }
    
    public void removeNotify() {
        wizard.removeWizardListener (this);
        super.removeNotify();
    }
    
    public InstructionsPanel(BufferedImage img, Wizard wizard) {
        this.img = img;
        this.wizard = wizard;
    }
    
    public boolean isOpaque() {
        return img != null;
    }
    
    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        Font f = getFont() != null ? getFont() : UIManager.getFont("controlFont");
        FontMetrics fm = g.getFontMetrics (f);
        Insets ins = getInsets();
        if (img != null) {
            int dx = ins.left;
            int dy = ins.top;
            int w = getWidth() - (ins. left + ins.right);
            int h = getHeight() - (ins.top + ins.bottom);
            
            g2d.drawImage(img, dx, dy, w, h, this);
        } else {
            g.setColor (Color.WHITE);
            g.fillRect (ins.left, ins.top, getWidth() - (ins.left + ins.right), getHeight() - (ins.top + ins.bottom));
        }
        String[] steps = wizard.getAllSteps();
        int y = fm.getMaxAscent() + ins.top;
        int x = ins.left;
        int h = fm.getMaxAscent() + fm.getMaxDescent() + 3;
        g.setColor (getForeground());
        
        for (int i=0; i < steps.length; i++) {
            String curr = i + ". " + (Wizard.UNDETERMINED_STEP.equals(steps[i]) ?
                "..." : wizard.getStepDescription(steps[i]));
            if (curr != null) {
                boolean selected = steps[i].equals (currentStep);
                if (selected) {
                    g.setFont (f.deriveFont (Font.BOLD));
                }
                
                int width = fm.stringWidth(curr);
                while (width > getWidth() - (ins.left + ins.right) && curr.length() > 5) {
                    curr = curr.substring(0, curr.length() - 5) + "...";
                }
                
                g.drawString (curr, x, y);
                if (selected) {
                    g.setFont (f);
                }
                y += h;
            }
        }
    }
    
    private String currentStep = "x";
    public void setCurrentStep (String s) {
        this.currentStep = s;
        repaint();
    }
    
    private int historicWidth = Integer.MIN_VALUE;
    public Dimension getPreferredSize() {
        Font f = getFont() != null ? getFont() : UIManager.getFont("controlFont");
        Graphics g = getGraphics();
        if (g == null) {
            g = new BufferedImage (1, 1, BufferedImage.TYPE_INT_ARGB).getGraphics();
        }
        f = f.deriveFont (Font.BOLD);
        FontMetrics fm = g.getFontMetrics(f);
        Insets ins = getInsets();
        int baseW = ins.left + ins.right;
        int h = fm.getHeight();
        
        String[] steps = wizard.getAllSteps();
        int w = Integer.MIN_VALUE;
        for (int i=0; i < steps.length; i++) {
            String desc = i + ". " + (Wizard.UNDETERMINED_STEP.equals(steps[i]) ?
                "..." : wizard.getStepDescription(steps[i]));
            if (desc != null) {
                w = Math.max (w, fm.stringWidth(desc));
            }
        }
        if (Integer.MIN_VALUE == w) {
            w = 250;
        }
        //Make sure we can grow but not shrink
        w = Math.max (w, historicWidth);
        historicWidth = w;
        return new Dimension (w,  ins.top + ins.bottom + ((h + 3) * steps.length));
    }
    
    public Dimension getMinimumSize() {
        return getPreferredSize();
    }
    
    public void stepsChanged(Wizard wizard) {
        repaint();
    }
    
    public void navigabilityChanged (Wizard wizard) {
        //do nothing
    }
}
