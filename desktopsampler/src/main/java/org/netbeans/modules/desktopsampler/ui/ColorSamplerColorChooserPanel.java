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

package org.netbeans.modules.desktopsampler.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import javax.swing.Icon;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * A simple ColorChooserPanel for JColorChooser based on ColorSampler.
 *
 * @author Sandip V. Chitale (Sandip.Chitale@Sun.Com)
 *
 * @see ColorSampler
 */
public class ColorSamplerColorChooserPanel
extends AbstractColorChooserPanel
implements ChangeListener {
    
    private boolean isAdjusting = false;
    
    private ColorSampler colorSampler;
    private Magnifier magnifier;
    
    public ColorSamplerColorChooserPanel() {
        colorSampler = new ColorSampler();
        colorSampler.addChangeListener(this);
        
        magnifier = new Magnifier();
    }
    
    public void stateChanged(ChangeEvent ce) {
        getColorSelectionModel().
        setSelectedColor(colorSampler.getSelectedColor());
    }
    
    // Implementation of AbstractColorChooserPanel
    /**
     * Return display name.
     *
     * @return a <code>String</code> value
     */
    public String getDisplayName() {
        return "Color Sampler";
    }
    
    /**
     * Update the chooser.
     *
     */
    public void updateChooser() {
        if (!isAdjusting) {
            isAdjusting = true;
            colorSampler.
            showColor(getColorSelectionModel().getSelectedColor(), false);
            isAdjusting = false;
        }
    }
    
    /**
     * Build the chooser panel.
     */
    public void buildChooser() {
        setLayout(new BorderLayout());
        add(colorSampler, BorderLayout.NORTH);
        
        add(magnifier, BorderLayout.CENTER);
    }
    
    /**
     * Return small icon.
     *
     * @return an <code>Icon</code> value
     */
    public Icon getSmallDisplayIcon() {
        return ColorSampler.COLOR_SAMPLER_ICON;
    }
    
    /**
     * Return large icon.
     *
     * @return an <code>Icon</code> value
     */
    public Icon getLargeDisplayIcon() {
        return null;
    }
    
    /**
     * Return font.
     *
     * @return a <code>Font</code> value
     */
    public Font getFont() {
        return null;
    }
    
    /**
     * <code>ColorSampler</code> test.
     *
     * @param args a <code>String[]</code> value
     */
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println(e);
        }
        JFrame frame = new JFrame("Color Sampler Color Chooser");
        
        JColorChooser colorChooser = new JColorChooser();
        colorChooser.addChooserPanel(new ColorSamplerColorChooserPanel());
        frame.setContentPane(colorChooser);
        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
