/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2001 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcs.advanced;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.openide.TopManager;
import org.openide.filesystems.*;
import java.awt.event.*;
import javax.swing.event.*;


import org.netbeans.modules.vcs.advanced.variables.VariableIO;
import org.netbeans.modules.vcs.advanced.variables.VariableIOCompat;

/**
 * The Save As dialog, that works on FileObjects. It is used to save configuration files.
 * @author  Martin Entlicher
 */
public class ConfigSaveAsDialog extends javax.swing.JDialog {

    private FileObject dir;
    private String selectedFile;
    private HashMap configLabels = new HashMap();

    static final long serialVersionUID =-4678964678643578543L;
    /** Creates new form ConfigSaveAsDialog */
    public ConfigSaveAsDialog(java.awt.Frame parent, boolean modal, FileObject dir) {
        super (parent, modal);
        this.dir = dir;
        setTitle(org.openide.util.NbBundle.getBundle(ConfigSaveAsDialog.class).getString("ConfigSaveAsDialog.title"));
        initComponents ();
        getRootPane().setDefaultButton(saveButton);
        fileNameLabel.setDisplayedMnemonic (org.openide.util.NbBundle.getBundle(ConfigSaveAsDialog.class).getString ("ConfigSaveAsDialog.fileNameLabel.mnemonic").charAt (0));
        configLabelLabel.setDisplayedMnemonic (org.openide.util.NbBundle.getBundle(ConfigSaveAsDialog.class).getString ("ConfigSaveAsDialog.configLabelLabel.mnemonic").charAt (0));
        saveButton.setMnemonic(org.openide.util.NbBundle.getBundle(ConfigSaveAsDialog.class).getString("ConfigSaveAsDialog.saveButton.text_Mnemonic").charAt(0));  // NOI18N
        cancelButton.setMnemonic(org.openide.util.NbBundle.getBundle(ConfigSaveAsDialog.class).getString("ConfigSaveAsDialog.cancelButton.text_Mnemonic").charAt(0));  // NOI18N
        fillFileList();
        this.setSize(340, 265);
        initAccessibility();
        ActionListener list = new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkToEnableSave();
            }
        };
        FocusAdapter focusList = new java.awt.event.FocusAdapter() {         
            public void focusLost(java.awt.event.FocusEvent evt) {
                checkToEnableSave();
            }
        };

        this.configLabelTextField.addActionListener(list);
        this.fileNameTextField.addActionListener(list);
        this.configLabelTextField.addFocusListener(focusList);
        this.fileNameTextField.addFocusListener(focusList);
        checkToEnableSave();
    }
    
    private void checkToEnableSave() {
        if (configLabelTextField.getText().length() == 0 || 
               fileNameTextField.getText().length() == 0) {
            saveButton.setEnabled(false);
        } else {
            saveButton.setEnabled(true);
        }
    }
    
    private void initAccessibility()
    {
        getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getBundle(ConfigSaveAsDialog.class).getString("ACS_ConfigSaveAsDialogA11yName"));  // NOI18N
        getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getBundle(ConfigSaveAsDialog.class).getString("ACS_ConfigSaveAsDialogA11yDesc"));  // NOI18N
        fileList.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getBundle(ConfigSaveAsDialog.class).getString("ACS_ConfigSaveAsDialog.fileListA11yName"));  // NOI18N
        fileList.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getBundle(ConfigSaveAsDialog.class).getString("ACS_ConfigSaveAsDialog.fileListA11yDesc")); // NOI18N
        fileNameLabel.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getBundle(ConfigSaveAsDialog.class).getString("ACS_ConfigSaveAsDialog.fileNameLabel.textA11yDesc"));  // NOI18N
        fileNameTextField.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getBundle(ConfigSaveAsDialog.class).getString("ACS_ConfigSaveAsDialog.fileNameTextField.textA11yName"));  // NOI18N
        configLabelLabel.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getBundle(ConfigSaveAsDialog.class).getString("ACS_ConfigSaveAsDialog.configLabelLabel.textA11yDesc"));  // NOI18N
        configLabelTextField.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getBundle(ConfigSaveAsDialog.class).getString("ACS_ConfigSaveAsDialog.configLabelTextField.textA11yName"));  // NOI18N
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the FormEditor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        listPanel = new javax.swing.JPanel();
        fileScrollPane = new javax.swing.JScrollPane();
        fileList = new javax.swing.JList();
        jPanel2 = new javax.swing.JPanel();
        fileNameLabel = new javax.swing.JLabel();
        fileNameTextField = new javax.swing.JTextField();
        configLabelLabel = new javax.swing.JLabel();
        configLabelTextField = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        saveButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();

        getContentPane().setLayout(new java.awt.GridBagLayout());

        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

        listPanel.setLayout(new java.awt.GridBagLayout());

        fileList.setToolTipText(org.openide.util.NbBundle.getBundle(ConfigSaveAsDialog.class).getString("ACS_ConfigSaveAsDialog.fileListA11yDesc"));
        fileList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fileListMouseClicked(evt);
            }
        });

        fileScrollPane.setViewportView(fileList);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        listPanel.add(fileScrollPane, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 0, 11);
        getContentPane().add(listPanel, gridBagConstraints);

        jPanel2.setLayout(new java.awt.GridBagLayout());

        fileNameLabel.setText(org.openide.util.NbBundle.getBundle(ConfigSaveAsDialog.class).getString("ConfigSaveAsDialog.fileNameLabel.text"));
        fileNameLabel.setLabelFor(fileNameTextField);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 12);
        jPanel2.add(fileNameLabel, gridBagConstraints);

        fileNameTextField.setColumns(10);
        fileNameTextField.setToolTipText(org.openide.util.NbBundle.getBundle(ConfigSaveAsDialog.class).getString("ACS_ConfigSaveAsDialog.fileNameTextField.textA11yDesc"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        jPanel2.add(fileNameTextField, gridBagConstraints);

        configLabelLabel.setText(org.openide.util.NbBundle.getBundle(ConfigSaveAsDialog.class).getString("ConfigSaveAsDialog.configLabelLabel.text"));
        configLabelLabel.setLabelFor(configLabelTextField);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 12);
        jPanel2.add(configLabelLabel, gridBagConstraints);

        configLabelTextField.setToolTipText(org.openide.util.NbBundle.getBundle(ConfigSaveAsDialog.class).getString("ACS_ConfigSaveAsDialog.configLabelTextField.textA11yDesc"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        jPanel2.add(configLabelTextField, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 17, 11);
        getContentPane().add(jPanel2, gridBagConstraints);

        jPanel1.setLayout(new java.awt.GridLayout(1, 0, 5, 0));

        saveButton.setText(org.openide.util.NbBundle.getBundle(ConfigSaveAsDialog.class).getString("ConfigSaveAsDialog.saveButton.text"));
        saveButton.setToolTipText(org.openide.util.NbBundle.getBundle(ConfigSaveAsDialog.class).getString("ACS_ConfigSaveAsDialog.saveButton.textA11yDesc"));
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });

        jPanel1.add(saveButton);

        cancelButton.setText(org.openide.util.NbBundle.getBundle(ConfigSaveAsDialog.class).getString("ConfigSaveAsDialog.cancelButton.text"));
        cancelButton.setToolTipText(org.openide.util.NbBundle.getBundle(ConfigSaveAsDialog.class).getString("ACS_ConfigSaveAsDialog.cancelButton.textA11yDesc"));
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        jPanel1.add(cancelButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 11, 11);
        getContentPane().add(jPanel1, gridBagConstraints);

    }//GEN-END:initComponents

    private void fileListMouseClicked (java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fileListMouseClicked
        // Add your handling code here:
        selectedFile = (String) fileList.getSelectedValue();
        fileNameTextField.setText(selectedFile);
        //fileNameTextField.repaint();
        String label = (String) configLabels.get(selectedFile);
        if (label != null) configLabelTextField.setText(label);
        checkToEnableSave();
    }//GEN-LAST:event_fileListMouseClicked

    private void saveButtonActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveButtonActionPerformed
        // Add your handling code here:
        selectedFile = fileNameTextField.getText();
        closeDialog(null);
    }//GEN-LAST:event_saveButtonActionPerformed

    private void cancelButtonActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        // Add your handling code here:
        selectedFile = null;
        closeDialog(null);
    }//GEN-LAST:event_cancelButtonActionPerformed

    /** Closes the dialog */
    private void closeDialog(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeDialog
        setVisible (false);
        dispose ();
    }//GEN-LAST:event_closeDialog

    private void fillFileList() {
        javax.swing.DefaultListModel model = new javax.swing.DefaultListModel();
        ArrayList configList = VariableIO.readConfigurations(dir);
        String[] configNames = (String[]) configList.toArray(new String[0]);
        Arrays.sort(configNames);
        for (int i = 0; i < configNames.length; i++) {
            String config = configNames[i];
            String configName;
            String configExt;
            int extIndex = config.lastIndexOf('.');
            if (extIndex < 0) {
                configName = config;
                configExt = "";
            } else {
                configName = config.substring(0, extIndex);
                configExt = config.substring(extIndex + 1);
            }
            model.addElement(configName);
            String label = null;
            if (configExt.equalsIgnoreCase(VariableIO.CONFIG_FILE_EXT)) {
                org.w3c.dom.Document doc = VariableIO.readPredefinedConfigurations(dir, config);
                if (doc != null) {
                    label = VariableIO.getConfigurationLabel(doc);
                } else {
                    label = org.openide.util.NbBundle.getBundle(ConfigSaveAsDialog.class).getString("CTL_No_label_configured");
                }
            } else if (configExt.equalsIgnoreCase(VariableIOCompat.CONFIG_FILE_EXT)) {
                label = VariableIOCompat.readPredefinedProperties(dir, config).
                         getProperty("label", org.openide.util.NbBundle.getBundle(ConfigSaveAsDialog.class).getString("CTL_No_label_configured"));
            }
            configLabels.put(configName, label);
        }
        fileList.setModel(model);
    }

    public String getSelectedFile() {
        return selectedFile;
    }
    
    public String getSelectedConfigLabel() {
        return configLabelTextField.getText();
    }

    /*
    * @param args the command line arguments

    public static void main (String args[]) {
      new ConfigSaveAsDialog (new javax.swing.JFrame (), true).show ();
}
    */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JList fileList;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane fileScrollPane;
    private javax.swing.JTextField fileNameTextField;
    private javax.swing.JTextField configLabelTextField;
    private javax.swing.JButton saveButton;
    private javax.swing.JLabel fileNameLabel;
    private javax.swing.JButton cancelButton;
    private javax.swing.JPanel listPanel;
    private javax.swing.JLabel configLabelLabel;
    // End of variables declaration//GEN-END:variables

}
