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
 * Software is Nokia. Portions Copyright 2003-2004 Nokia.
 * All Rights Reserved.
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

