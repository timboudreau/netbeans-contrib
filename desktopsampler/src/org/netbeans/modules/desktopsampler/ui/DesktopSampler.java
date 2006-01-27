/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.desktopsampler.ui;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ResourceBundle;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JColorChooser;
import javax.swing.JOptionPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.JWindow;
import javax.swing.SwingConstants;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.util.Lookup;
import org.openide.util.datatransfer.ExClipboard;
import org.openide.windows.WindowManager;

/**
 * A combined desktop color sampler and magnifier.
 *
 * @author Sandip V. Chitale (Sandip.Chitale@Sun.Com)
 */
public class DesktopSampler
extends JToolBar
implements ChangeListener {
    static ImageIcon SHOW_MAGNIFIER_ICON = new ImageIcon(DesktopSampler.class.getResource("ShowMagnifier.gif")); // NOI18N
    static ImageIcon HIDE_MAGNIFIER_ICON = new ImageIcon(DesktopSampler.class.getResource("HideMagnifier.gif")); // NOI18N
    
    private ColorSampler  colorSampler;
    private Magnifier     magnifier;
    private JToggleButton toggleMagnifier;
    
    private JColorChooser colorChooser;
    
    private JWindow       magnifierWindow;
    
    private Clipboard clipboard;
    
    public DesktopSampler() {
        setFloatable(false);
        
        colorSampler = new ColorSampler();
        colorSampler.addChangeListener(this);
        add(colorSampler);
        
        colorSampler.getColorPreviewLabel().setToolTipText(ResourceBundle.getBundle("org/netbeans/modules/desktopsampler/ui/Bundle").getString("TOOLTIP_ColorLabel")); // NOI18N
        colorSampler.getColorPreviewLabel().addMouseListener(
        new MouseAdapter() {
            public void mouseClicked(MouseEvent me) {
                if (me.getClickCount() == 2) {
                    JColorChooser colorChooser = getColorChooser();
                    colorChooser.setColor(colorSampler.getSelectedColor());
                    switch (JOptionPane.showConfirmDialog(
                    WindowManager.getDefault().getMainWindow(),
                    colorChooser,
                    ResourceBundle.getBundle("org/netbeans/modules/desktopsampler/ui/Bundle").getString("Select_color"),
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE
                    )) {
                        case JOptionPane.OK_OPTION:
                            colorSampler.setSelectedColor(colorChooser.getColor());
                            break;
                    }
                }
            }
        }
        );
        
        toggleMagnifier = new JToggleButton(SHOW_MAGNIFIER_ICON);
        toggleMagnifier.setSelectedIcon(HIDE_MAGNIFIER_ICON);
        toggleMagnifier.setVerticalAlignment(SwingConstants.BOTTOM);
        toggleMagnifier.setHorizontalAlignment(SwingConstants.RIGHT);
        toggleMagnifier.setMargin(new Insets(1,1,1,1));
        toggleMagnifier.setBorderPainted(false);
        toggleMagnifier.setFocusPainted(false);
        toggleMagnifier.setRolloverEnabled(true);        
        toggleMagnifier.setToolTipText(ResourceBundle.getBundle("org/netbeans/modules/desktopsampler/ui/Bundle").getString("TOOLTIP_ShowHide_Magnifier")); // NOI18N
        
        add(toggleMagnifier);
        toggleMagnifier.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                toggleMagnifier();
            }
        });
        
        magnifierWindow = new JWindow(WindowManager.getDefault().getMainWindow());
        
        magnifier = new Magnifier();
        
        magnifier.setBorder(BorderFactory.createRaisedBevelBorder());
        
        magnifierWindow.setContentPane(magnifier);
        magnifierWindow.pack();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension magnifierWindowSize = magnifierWindow.getSize();
        magnifierWindow.setLocation(
        (screenSize.width - magnifierWindowSize.width)/2,
        (screenSize.height - magnifierWindowSize.height)/2);
        
        // set the border to deal with layout bug on certain look and feels e.g. Metal and Ocean.
        setBorder(BorderFactory.createEmptyBorder());
    }
      
    public void stateChanged(ChangeEvent ce) {
        StringSelection colorStringSelection = new StringSelection(colorSampler.format(colorSampler.getSelectedColor()));
        
        Clipboard clip = getExClipboard();
        if (clipboard != null) {
            clipboard.setContents(colorStringSelection, null);
        }
    }
    
    private void toggleMagnifier() {
        magnifierWindow.setVisible(!magnifierWindow.isVisible());
        toggleMagnifier.setSelected(magnifierWindow.isVisible());
        
        if (magnifierWindow.isVisible()) {
            Point p = getLocationOnScreen();
            Dimension s = getSize();
            magnifierWindow.setLocation(
            p.x,
            p.y + s.height + 2);
        }
    }
    
    private JColorChooser getColorChooser() {
        if (colorChooser == null) {
            colorChooser = new JColorChooser();
            ColorSamplerColorChooserPanel colorSamplerColorChooserPanel = new ColorSamplerColorChooserPanel();
            colorChooser.addChooserPanel(colorSamplerColorChooserPanel);
            AbstractColorChooserPanel[] colorChooserPanels = colorChooser.getChooserPanels();
            System.arraycopy(colorChooserPanels, 0, colorChooserPanels, 1, colorChooserPanels.length -1);
            colorChooserPanels[0] = colorSamplerColorChooserPanel;
            colorChooser.setChooserPanels(colorChooserPanels);
        }
        return colorChooser;
    }
    
    private Clipboard getExClipboard() {
        // Lookup and cache the Platfrom's clipboard
        if (clipboard == null) {
            clipboard = (ExClipboard) Lookup.getDefault().lookup(ExClipboard.class);
            if (clipboard == null) {
                clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            }
        }
        return clipboard;
    }
    
    
}
