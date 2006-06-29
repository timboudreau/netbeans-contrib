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
