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

package org.netbeans.modules.vcs.advanced;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;

import org.openide.filesystems.*;
import java.awt.event.*;
import javax.swing.event.*;


import org.netbeans.modules.vcs.advanced.variables.VariableIO;
import org.netbeans.modules.vcs.advanced.variables.VariableIOCompat;
import org.netbeans.modules.vcscore.util.VcsUtilities;
import org.openide.util.UserCancelException;

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
        this.setSize(400, 300);
        initAccessibility();
        EnableSaveListener saveListener = new EnableSaveListener();
        this.configLabelTextField.addActionListener(saveListener);
        this.fileNameTextField.addActionListener(saveListener);
        this.configLabelTextField.addFocusListener(saveListener);
        this.fileNameTextField.addFocusListener(saveListener);
        this.configLabelTextField.addKeyListener(saveListener);
        this.fileNameTextField.addKeyListener(saveListener);
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
        profileLBL = new javax.swing.JLabel();

        FormListener formListener = new FormListener();

        getContentPane().setLayout(new java.awt.GridBagLayout());

        addWindowListener(formListener);

        listPanel.setLayout(new java.awt.GridBagLayout());

        fileList.setToolTipText(org.openide.util.NbBundle.getBundle(ConfigSaveAsDialog.class).getString("ACS_ConfigSaveAsDialog.fileListA11yDesc"));
        fileList.addMouseListener(formListener);

        fileScrollPane.setViewportView(fileList);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        listPanel.add(fileScrollPane, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(6, 12, 0, 11);
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
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
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(6, 12, 17, 11);
        gridBagConstraints.weightx = 1.0;
        getContentPane().add(jPanel2, gridBagConstraints);

        jPanel1.setLayout(new java.awt.GridLayout(1, 0, 5, 0));

        saveButton.setText(org.openide.util.NbBundle.getBundle(ConfigSaveAsDialog.class).getString("ConfigSaveAsDialog.saveButton.text"));
        saveButton.setToolTipText(org.openide.util.NbBundle.getBundle(ConfigSaveAsDialog.class).getString("ACS_ConfigSaveAsDialog.saveButton.textA11yDesc"));
        saveButton.addActionListener(formListener);

        jPanel1.add(saveButton);

        cancelButton.setText(org.openide.util.NbBundle.getBundle(ConfigSaveAsDialog.class).getString("ConfigSaveAsDialog.cancelButton.text"));
        cancelButton.setToolTipText(org.openide.util.NbBundle.getBundle(ConfigSaveAsDialog.class).getString("ACS_ConfigSaveAsDialog.cancelButton.textA11yDesc"));
        cancelButton.addActionListener(formListener);

        jPanel1.add(cancelButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 11, 11);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        getContentPane().add(jPanel1, gridBagConstraints);

        profileLBL.setDisplayedMnemonic(org.openide.util.NbBundle.getBundle("org/netbeans/modules/vcs/advanced/Bundle").getString("ACS_SaveAsConfigDialog.profileLBL_mnemonic").charAt(0));
        profileLBL.setLabelFor(fileList);
        profileLBL.setText(org.openide.util.NbBundle.getBundle("org/netbeans/modules/vcs/advanced/Bundle").getString("ConfigSaveAsDialog.profileLBL"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 0, 0);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        getContentPane().add(profileLBL, gridBagConstraints);
        profileLBL.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getBundle("org/netbeans/modules/vcs/advanced/Bundle").getString("ACSD_ConfigSaveAsDialog.profileLBL"));

    }

    // Code for dispatching events from components to event handlers.

    private class FormListener implements java.awt.event.ActionListener, java.awt.event.MouseListener, java.awt.event.WindowListener {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            if (evt.getSource() == saveButton) {
                ConfigSaveAsDialog.this.saveButtonActionPerformed(evt);
            }
            else if (evt.getSource() == cancelButton) {
                ConfigSaveAsDialog.this.cancelButtonActionPerformed(evt);
            }
        }

        public void mouseClicked(java.awt.event.MouseEvent evt) {
            if (evt.getSource() == fileList) {
                ConfigSaveAsDialog.this.fileListMouseClicked(evt);
            }
        }

        public void mouseEntered(java.awt.event.MouseEvent evt) {
        }

        public void mouseExited(java.awt.event.MouseEvent evt) {
        }

        public void mousePressed(java.awt.event.MouseEvent evt) {
        }

        public void mouseReleased(java.awt.event.MouseEvent evt) {
        }

        public void windowActivated(java.awt.event.WindowEvent evt) {
        }

        public void windowClosed(java.awt.event.WindowEvent evt) {
        }

        public void windowClosing(java.awt.event.WindowEvent evt) {
            if (evt.getSource() == ConfigSaveAsDialog.this) {
                ConfigSaveAsDialog.this.closeDialog(evt);
            }
        }

        public void windowDeactivated(java.awt.event.WindowEvent evt) {
        }

        public void windowDeiconified(java.awt.event.WindowEvent evt) {
        }

        public void windowIconified(java.awt.event.WindowEvent evt) {
        }

        public void windowOpened(java.awt.event.WindowEvent evt) {
        }
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
        try {
            if (selectedFile.indexOf('_') > 0 && willChangeNameWithUnderscore(selectedFile)) {
                return;
            }
        } catch (UserCancelException ucex) {
            selectedFile = null;
        }
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

    /**
     * Display a question that will ask whether the user is sure to use this name
     * even though it contains an underscore.
     */
    public static boolean willChangeNameWithUnderscore(String nameWithUnderscore) throws UserCancelException {
        NotifyDescriptor.Confirmation question =
                new NotifyDescriptor.Confirmation(org.openide.util.NbBundle.getMessage(ConfigSaveAsDialog.class, "CTL_NameUnderscore", nameWithUnderscore),
                                                  NotifyDescriptor.YES_NO_CANCEL_OPTION, NotifyDescriptor.WARNING_MESSAGE);
        Object ret = DialogDisplayer.getDefault().notify(question);
        if (ret == NotifyDescriptor.CANCEL_OPTION) {
            throw new UserCancelException();
        }
        return ret == NotifyDescriptor.YES_OPTION;
    }
    
    private void fillFileList() {
        javax.swing.DefaultListModel model = new javax.swing.DefaultListModel();
        String[] configNames = ProfilesFactory.getDefault().getProfilesNames();
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
            configLabels.put(configName, ProfilesFactory.getDefault().getProfileDisplayName(config));
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
    private javax.swing.JButton cancelButton;
    private javax.swing.JLabel configLabelLabel;
    private javax.swing.JTextField configLabelTextField;
    private javax.swing.JList fileList;
    private javax.swing.JLabel fileNameLabel;
    private javax.swing.JTextField fileNameTextField;
    private javax.swing.JScrollPane fileScrollPane;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel listPanel;
    private javax.swing.JLabel profileLBL;
    private javax.swing.JButton saveButton;
    // End of variables declaration//GEN-END:variables

    private class EnableSaveListener extends FocusAdapter implements ActionListener, KeyListener {
        
        public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
            checkToEnableSave();
        }
        
        public void focusLost(java.awt.event.FocusEvent evt) {
            checkToEnableSave();
        }
        
        public void keyPressed(java.awt.event.KeyEvent keyEvent) {
            checkToEnableSave();
        }
        
        public void keyReleased(java.awt.event.KeyEvent keyEvent) {
            checkToEnableSave();
        }
        
        public void keyTyped(java.awt.event.KeyEvent keyEvent) {
            checkToEnableSave();
        }
        
    }
}
