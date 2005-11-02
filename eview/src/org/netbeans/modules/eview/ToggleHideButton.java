/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Nokia. Portions Copyright 2003-2004 Nokia.
 * All Rights Reserved.
 */
package org.netbeans.modules.eview;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.SwingConstants;


/**
 * Button capable of changing the visibility of a swing component. Whenever the
 * button is pressed it switches the visible state of the associated
 * component.
 *
 * @author Alex Zubiaga, David Strupl
 */
public class ToggleHideButton extends JButton {
    //~ Inner Classes ----------------------------------------------------------
    
    /**
     * Action triggered when button is pressed.
     *
     * @author zubiaga
     */
    private class MyActionListener implements ActionListener {
        //~ Methods ------------------------------------------------------------
        
        /* (non-Javadoc)
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e) {
            setPanelVisible(!isPanelVisible());
        }
    }
    
    //~ Static fields/initializers ---------------------------------------------
    
    final static public Color FOCUSED = (Color)javax.swing.UIManager.getDefaults().get("primary3");
    final static public Color UNFOCUSED = Color.white;
    static private ImageIcon myClosedIcon;
    static private ImageIcon myOpenIcon;
    
    //~ Instance fields --------------------------------------------------------
    
    private MyActionListener myActionListener;
    private JComponent myHideablePanel;
    
    //~ Constructors -----------------------------------------------------------
    
    /**
     *
     */
    public ToggleHideButton() {
        super();
        initComponents();
    }
    
    //~ Methods ----------------------------------------------------------------
    
    /**
     * Initializes icons and state of the button.
     */
    protected void initComponents() {
        if(myClosedIcon == null) {
            myClosedIcon =
                new ImageIcon(ToggleHideButton.class.getResource("arrowright.gif"));
        }
        
        if(myOpenIcon == null) {
            myOpenIcon = 
                new ImageIcon(ToggleHideButton.class.getResource("arrowbottom.gif"));
        }
        
        // set style
        setIcon(myClosedIcon);
        
        setHorizontalAlignment(SwingConstants.LEADING);
        
        setFocusable(true);
        
        myActionListener = new MyActionListener();
        addActionListener(myActionListener);
    }
    
    
    /**
     * Check the visibility of the controlled panel.
     *
     * @return true if the panel is visible
     */
    public boolean isPanelVisible() {
        return myHideablePanel == null ? false : myHideablePanel.isVisible();
    }
    
    
    /**
     * Sets visibility of the controlled panel.
     *
     * @param visible true if the panel should be visible
     */
    public void setPanelVisible(boolean visible) {
        if(myHideablePanel != null) {
            myHideablePanel.setVisible(visible);
            getParent().invalidate();
            getParent().validate();
            getParent().repaint();
        }
        
        setIcon(isPanelVisible() ? myOpenIcon : myClosedIcon);
    }
    
    
    /* (non-Javadoc)
     * @see java.awt.Component#setEnabled(boolean)
     */
    public void setEnabled(boolean enabled) {
        if(enabled != isEnabled()) {
            if(enabled) {
                addActionListener(myActionListener);
            }
            else {
                removeActionListener(myActionListener);
            }
        }
        
        super.setEnabled(enabled);
    }
    
    
    /**
     * Getter method to the panel controlled by this button.
     *
     * @return pannel controlled by the button
     */
    public JComponent getHideablePanel() {
        return myHideablePanel;
    }
    
    
    /**
     * Setter method to the panel controlled by this button.
     *
     * @param component to be hidden
     */
    public void setHideablePanel(JComponent component) {
        myHideablePanel = component;
        setPanelVisible(isPanelVisible());
    }
}

