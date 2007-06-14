
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
 * Copyright 2007 Sun Microsystems, Inc. All Rights Reserved
 *
 */

package com.sun.tthub.gde.ui.panels;

import com.sun.tthub.gdelib.GDEException;
import com.sun.tthub.gde.logic.GDEAppContext;
import com.sun.tthub.gde.logic.GDEPreferences;
import com.sun.tthub.gde.logic.PortletDeployParams;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public final class DeployParamsJPanel extends WizardContentJPanel {

    private WizardController controller;
    /**
     * Creates new form DeployParamsJPanel
     */
    private PortletDeployParams validatedParams;
    
    public DeployParamsJPanel(WizardController controller) {
        this.controller = controller;
        initComponents();
    }
    
    /**
     * is called when the page is loaded
     */
    public void loadWizardContentPanel() {
        validatedParams = null;
        // fill the page with the existing portlet deployment parameters.
        PortletDeployParams params = controller.getPortletDeployParams();
        if(params == null) {
            resetControlState(false);
            chkDeployParams.setSelected(false);
        } else {
            txtPortalServHome.setText(params.getPortalServerHome());
            txtDirServUserDN.setText(params.getDirServerUserDN());
            txtDirServUserPwd.setText(params.getDirServerUserPwd());
            txtWebContainerPwd.setText(params.getWebContainerPwd());
            chkDeployParams.setSelected(true);
        }
    }

    private void resetControlState(boolean isEnabled) {
        txtPortalServHome.setEnabled(isEnabled);
        txtDirServUserDN.setEnabled(isEnabled);
        txtDirServUserPwd.setEnabled(isEnabled);
        txtWebContainerPwd.setEnabled(isEnabled);
        btnPortalServHome.setEnabled(isEnabled);
    }
    
    private void clearControls() {
        txtPortalServHome.setText("");
        txtDirServUserDN.setText("");
        txtDirServUserPwd.setText("");
        txtWebContainerPwd.setText("");
    }

    private void fillDefaultValues() throws GDEException {
        GDEPreferences pref = GDEAppContext.getInstance(
            ).getGdePrefsController().retrievePreferences();
        txtPortalServHome.setText(pref.getPortalServerHome());
        txtDirServUserDN.setText(pref.getDirServerUserDN());
    }
    
    private boolean isValidDirectory(String dirName) {
        File file = new File(dirName);
        return  (file.isDirectory() && file.canRead());
    }

    public void validateContents() throws GDEWizardPageValidationException {        
        
        Object[] selObjArr = chkDeployParams.getSelectedObjects();
        if(selObjArr == null || selObjArr[0] == null)
            return;
        
        String portalServHome = txtPortalServHome.getText();
        String dirServUserDN = txtDirServUserDN.getText();
        
        if(portalServHome == null || portalServHome.trim().equals("") ||
                    !isValidDirectory(portalServHome)) {
            throw new GDEWizardPageValidationException("The portal server home " +
                    " should be a valid readable directory.");
        }
        
        if(dirServUserDN == null || dirServUserDN.trim().equals("")) {
            throw new GDEWizardPageValidationException("The Distinguished name " +
                    "of the Dir server user should not be empty.");
        }
        try {
            char[] dirServUserPwd = txtDirServUserPwd.getPassword();
            char[] webContainerPwd = txtWebContainerPwd.getPassword();     
            if(dirServUserPwd.length == 0 || webContainerPwd.length == 0) {
                throw new GDEWizardPageValidationException("Both the Dir server" +
                        " pwd and the web container pwd should be provided.");
            }
            String dirUserPwd = new String(dirServUserPwd);
            String webUserPwd = new String(webContainerPwd);
            
            validatedParams = new PortletDeployParams(portalServHome.trim(), 
                    dirServUserDN.trim(), dirUserPwd.trim(), webUserPwd.trim());
            
        } catch (NullPointerException ex) {
            throw new GDEWizardPageValidationException("Both the Dir Server pwd" +
                    " and the Web container pwd should be provided");
        }
        
    }
    
    private String getFolderFromFileChooser() {
        JFileChooser fileChooser = new JFileChooser();
        // Disable all file filters.
        fileChooser.setAcceptAllFileFilterUsed(false);
        // Disable the multiselection feature
        fileChooser.setMultiSelectionEnabled(false); 
        //Set the filter so that the file chooser will display only the jsp files.
        fileChooser.setFileFilter(new CustomFileFilter("All Directories", true));
        
        int retVal = fileChooser.showOpenDialog(this); // Show the 'Open File' dialog.
        if(retVal == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            file.getPath();
        }                
        return null;
    }
    
    public boolean validationFailed(GDEWizardPageValidationException ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage(), 
                "Validation Failure", JOptionPane.ERROR_MESSAGE);
        return false;
    }
    
    /**
     * Method from the WizardContentProcessor. Does nothing in this panel.
     */
    public void preProcessWizardContents(int wizardAction) throws GDEException {}
    
    
    public void processWizardContents(int wizardAction) throws GDEException {
        // set the validated params to the wizard model.
        controller.getWizardModel().setPortletDeployParams(validatedParams);
    }
    
    public String getName() { return "DeployParamsJPanel"; }    
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        lblTitle = new javax.swing.JLabel();
        pnlMain = new javax.swing.JPanel();
        lblPortalServHome = new javax.swing.JLabel();
        txtPortalServHome = new javax.swing.JTextField();
        btnPortalServHome = new javax.swing.JButton();
        lblDirServUserDN = new javax.swing.JLabel();
        txtDirServUserDN = new javax.swing.JTextField();
        lblDirServUserPwd = new javax.swing.JLabel();
        txtDirServUserPwd = new javax.swing.JPasswordField();
        lblWebContainerPwd = new javax.swing.JLabel();
        txtWebContainerPwd = new javax.swing.JPasswordField();
        jPanel1 = new javax.swing.JPanel();
        chkDeployParams = new javax.swing.JCheckBox();

        lblTitle.setFont(new java.awt.Font("Dialog", 1, 14));
        lblTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTitle.setText("Portlet Deployment Parameters");

        pnlMain.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        lblPortalServHome.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblPortalServHome.setText("Portal Server Home:");

        txtPortalServHome.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        btnPortalServHome.setText("...");
        btnPortalServHome.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPortalServHomeActionPerformed(evt);
            }
        });

        lblDirServUserDN.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblDirServUserDN.setText("Dir Server User DN:");

        txtDirServUserDN.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        txtDirServUserDN.setRequestFocusEnabled(false);

        lblDirServUserPwd.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblDirServUserPwd.setText("Dir Server User Pwd:");

        txtDirServUserPwd.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        lblWebContainerPwd.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblWebContainerPwd.setText("Web Container Pwd:");

        txtWebContainerPwd.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        org.jdesktop.layout.GroupLayout pnlMainLayout = new org.jdesktop.layout.GroupLayout(pnlMain);
        pnlMain.setLayout(pnlMainLayout);
        pnlMainLayout.setHorizontalGroup(
            pnlMainLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.LEADING, pnlMainLayout.createSequentialGroup()
                .addContainerGap()
                .add(pnlMainLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, pnlMainLayout.createSequentialGroup()
                        .add(lblPortalServHome, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 118, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .add(org.jdesktop.layout.GroupLayout.LEADING, pnlMainLayout.createSequentialGroup()
                        .add(pnlMainLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, pnlMainLayout.createSequentialGroup()
                                .add(1, 1, 1)
                                .add(lblWebContainerPwd))
                            .add(lblDirServUserPwd)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, pnlMainLayout.createSequentialGroup()
                                .add(9, 9, 9)
                                .add(lblDirServUserDN)))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(pnlMainLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(txtPortalServHome, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 223, Short.MAX_VALUE)
                            .add(txtWebContainerPwd, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 223, Short.MAX_VALUE)
                            .add(txtDirServUserPwd, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 223, Short.MAX_VALUE)
                            .add(txtDirServUserDN, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 223, Short.MAX_VALUE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(btnPortalServHome, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 49, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(21, 21, 21))))
        );
        pnlMainLayout.setVerticalGroup(
            pnlMainLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.LEADING, pnlMainLayout.createSequentialGroup()
                .addContainerGap()
                .add(pnlMainLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, pnlMainLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(txtPortalServHome, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(btnPortalServHome))
                    .add(lblPortalServHome))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlMainLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblDirServUserDN)
                    .add(txtDirServUserDN, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlMainLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblDirServUserPwd)
                    .add(txtDirServUserPwd, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlMainLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblWebContainerPwd)
                    .add(txtWebContainerPwd, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlMainLayout.linkSize(new java.awt.Component[] {btnPortalServHome, lblDirServUserDN, lblDirServUserPwd, lblPortalServHome, lblWebContainerPwd, txtDirServUserDN, txtDirServUserPwd, txtPortalServHome, txtWebContainerPwd}, org.jdesktop.layout.GroupLayout.VERTICAL);

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        chkDeployParams.setText("Specify Deployment Parameters");
        chkDeployParams.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        chkDeployParams.setMargin(new java.awt.Insets(0, 0, 0, 0));
        chkDeployParams.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkDeployParamsActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(chkDeployParams, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(229, 229, 229))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(chkDeployParams)
                .addContainerGap())
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(lblTitle, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 445, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel1, 0, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(pnlMain)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                .addContainerGap()
                .add(lblTitle, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 26, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlMain, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(15, Short.MAX_VALUE))
        );
    }
    // </editor-fold>//GEN-END:initComponents

    private void btnPortalServHomeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPortalServHomeActionPerformed
        String folderName = getFolderFromFileChooser();
        if(folderName != null) {
            txtPortalServHome.setText(folderName);
        }        
    }//GEN-LAST:event_btnPortalServHomeActionPerformed

    private void chkDeployParamsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkDeployParamsActionPerformed
        Object[] objArr = chkDeployParams.getSelectedObjects();
        if(objArr[0] == null) {
            resetControlState(false);
            clearControls();
            controller.getWizardModel().setPortletDeployParams(null);
        } else {
            try {
                resetControlState(true);
                fillDefaultValues();
            } catch (GDEException ex) {/** Do nothing. set the fields blank */}
        }
    }//GEN-LAST:event_chkDeployParamsActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JButton btnPortalServHome;
    public javax.swing.JCheckBox chkDeployParams;
    public javax.swing.JPanel jPanel1;
    public javax.swing.JLabel lblDirServUserDN;
    public javax.swing.JLabel lblDirServUserPwd;
    public javax.swing.JLabel lblPortalServHome;
    public javax.swing.JLabel lblTitle;
    public javax.swing.JLabel lblWebContainerPwd;
    public javax.swing.JPanel pnlMain;
    public javax.swing.JTextField txtDirServUserDN;
    public javax.swing.JPasswordField txtDirServUserPwd;
    public javax.swing.JTextField txtPortalServHome;
    public javax.swing.JPasswordField txtWebContainerPwd;
    // End of variables declaration//GEN-END:variables

}

