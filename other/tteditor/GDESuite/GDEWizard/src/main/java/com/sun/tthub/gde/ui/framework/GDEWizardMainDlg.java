
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
 * Copyright 2007 Sun Microsystems, Inc. All Rights Reserved.
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
 * made subject to such option by the copyright holder. *
 */

package com.sun.tthub.gde.ui.framework;

import com.sun.tthub.gde.ui.controls.GDENavigationPanel;
import com.sun.tthub.gde.ui.panels.ExtendedTTInfoJPanel;
import com.sun.tthub.gde.ui.panels.LogsJPanel;
import com.sun.tthub.gde.ui.panels.PreferencesJPanel;
import com.sun.tthub.gde.ui.panels.WizardController;
import java.awt.Dimension;
import javax.swing.JPanel;

/**
 *
 * @author  Hareesh Ravindran
 */
public class GDEWizardMainDlg extends javax.swing.JDialog {
    
    private WizardController controller = new WizardController();
    
    /** Creates new form GDEWizardMainDlg */    
    public GDEWizardMainDlg(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }
    
    public GDEWizardMainDlg() { 
        super(); 
        initComponents();
        // fire the click event of the GDEPreferences button so that the
        // Preferences panel is loaded initially.
        this.navigationPanel.optGdePreferences.doClick();
    }
    
    /**
     * Replaces the contentpanel with the specified JPanel. This method will be
     * called based on the user events.
     */
    public void replaceContentPanel(JPanel contentPanel) {
        int dividerLocation = spltPaneGdeWizard.getDividerLocation();
        int contentPanelWidth = spltPaneGdeWizard.getWidth() - dividerLocation;
        this.contentPanel.setPreferredSize( new Dimension(
                contentPanelWidth, spltPaneGdeWizard.getHeight()));        
        spltPaneGdeWizard.setRightComponent(contentPanel);        
    }
    
    // End of the navigation selections
    
    public void processPreferencesSelection() {
        PreferencesJPanel panel = new PreferencesJPanel();        
        replaceContentPanel(panel);
    }
    
    public void processDisplayControlSelection() {
        controller.initialize();
        JPanel panel = controller.getWizardPanel();        
        replaceContentPanel(panel);
    }
    
    public void processLogsSelection() {
        LogsJPanel panel = new LogsJPanel();
        replaceContentPanel(panel);
    }
    
    //End of the navigation selections.
    
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        spltPaneGdeWizard = new javax.swing.JSplitPane();
        lblStatus = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setModal(true);
        spltPaneGdeWizard.setDividerLocation(150);
        // ---- Post initialization code for the split pane.

        // Initialize the navigation panel and the content panel.
        // The content panel is a dummy panel initialized at first.
        // later, this will be replaced by different other panels.
        // Set the navigation panel as the left component and the content
        // panel as the right component of the split pane.

        navigationPanel = new GDENavigationPanel(this);
        this.navigationPanel.setPreferredSize(new Dimension(
            150, this.spltPaneGdeWizard.getHeight()));

    contentPanel = new JPanel();
    int contentPanelWidth = spltPaneGdeWizard.getWidth() - 160;
    this.contentPanel.setPreferredSize( new Dimension(
        contentPanelWidth, spltPaneGdeWizard.getHeight()));

spltPaneGdeWizard.setLeftComponent(navigationPanel);
spltPaneGdeWizard.setRightComponent(contentPanel);

// ----- End of post initialization code for the split pane.

lblStatus.setText("Status Messages");
lblStatus.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
getContentPane().setLayout(layout);
layout.setHorizontalGroup(
    layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
        .addContainerGap()
        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
            .add(org.jdesktop.layout.GroupLayout.LEADING, spltPaneGdeWizard, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 576, Short.MAX_VALUE)
            .add(org.jdesktop.layout.GroupLayout.LEADING, lblStatus, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 576, Short.MAX_VALUE))
        .addContainerGap())
    );
    layout.setVerticalGroup(
        layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
            .addContainerGap()
            .add(spltPaneGdeWizard, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 350, Short.MAX_VALUE)
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(lblStatus)
            .addContainerGap())
    );
    pack();
    }
    // </editor-fold>//GEN-END:initComponents
    
   
    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JLabel lblStatus;
    public javax.swing.JSplitPane spltPaneGdeWizard;
    // End of variables declaration//GEN-END:variables
    
    public javax.swing.JPanel contentPanel;    
    public GDENavigationPanel navigationPanel;
    
}