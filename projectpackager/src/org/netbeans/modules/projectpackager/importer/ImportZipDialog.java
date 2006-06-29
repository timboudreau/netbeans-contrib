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

package org.netbeans.modules.projectpackager.importer;

import java.util.Vector;

/**
 * Import zip dialog
 * @author Roman "Roumen" Strobl
 */
public class ImportZipDialog extends javax.swing.JFrame {
    private Vector listData;

    /** Creates new form ZipProjectDialog */
    public ImportZipDialog() {
        initComponents();
        setLocationRelativeTo(null);
        ImportZipUITools.setZipProjectDialog(this);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        projectZip = new javax.swing.JLabel();
        projectZipField = new javax.swing.JTextField();
        zipChooseButton = new javax.swing.JButton();
        unzipToDirectory = new javax.swing.JLabel();
        unzipToDirectoryField = new javax.swing.JTextField();
        dirChooseButton = new javax.swing.JButton();
        projectName = new javax.swing.JLabel();
        projectNameField = new javax.swing.JTextField();
        deleteCheckBox = new javax.swing.JCheckBox();
        cancelButton = new javax.swing.JButton();
        okButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Import Project from Zip");
        setResizable(false);
        projectZip.setText("Project Zip:");

        zipChooseButton.setText("...");
        zipChooseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                zipChooseButtonActionPerformed(evt);
            }
        });

        unzipToDirectory.setText("Unzip to Directory:");

        dirChooseButton.setText("...");
        dirChooseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dirChooseButtonActionPerformed(evt);
            }
        });

        projectName.setText("Project Folder Name:");

        deleteCheckBox.setText("Delete Zip After Import");
        deleteCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        deleteCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        okButton.setText("Ok");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                        .addContainerGap()
                        .add(projectZip))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                        .addContainerGap()
                        .add(unzipToDirectory))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                                .addContainerGap()
                                .add(unzipToDirectoryField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 225, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                                .add(24, 24, 24)
                                .add(projectZipField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 225, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 17, Short.MAX_VALUE)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(zipChooseButton)
                            .add(dirChooseButton)))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                        .addContainerGap()
                        .add(projectName))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                        .add(24, 24, 24)
                        .add(projectNameField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 174, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                        .addContainerGap()
                        .add(deleteCheckBox))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .addContainerGap()
                        .add(okButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 65, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(cancelButton)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                .addContainerGap()
                .add(projectZip)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(zipChooseButton)
                    .add(projectZipField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(unzipToDirectory)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(unzipToDirectoryField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(dirChooseButton))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(projectName)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(projectNameField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(deleteCheckBox)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 23, Short.MAX_VALUE)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(cancelButton)
                    .add(okButton))
                .addContainerGap())
        );
        pack();
    }
    // </editor-fold>//GEN-END:initComponents

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        ImportZipUITools.processOkButton();
    }//GEN-LAST:event_okButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
	ImportZipUITools.processCancelButton();
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void dirChooseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dirChooseButtonActionPerformed
        ImportZipUITools.showDirectoryChooser();
    }//GEN-LAST:event_dirChooseButtonActionPerformed

    private void zipChooseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_zipChooseButtonActionPerformed
        ImportZipUITools.showFileChooser();
    }//GEN-LAST:event_zipChooseButtonActionPerformed
                
    /**
     * Set zip file
     * @param zip zip file
     */
    public void setZip(String zip) {
        projectZipField.setText(zip);
    }
    
    /**
     * Set unzip directory
     * @param zipDirectory directory
     */
    public void setUnZipDir(String zipDirectory) {
        unzipToDirectoryField.setText(zipDirectory);
    }
    
    /**
     * Set project name
     * @param name project name
     */
    public void setProjectName(String name) {
        projectNameField.setText(name);
    }
    
    /**
     * Return zip file
     * @return zip file
     */
    public String getZipFile() {
        return projectZipField.getText();
    }
    
    /**
     * Return unzip directory
     * @return directory
     */
    public String getUnzipDir() {
        return unzipToDirectoryField.getText();
    }
    
    /**
     * Is delete selected?
     * @return true if selected
     */
    public boolean isDeleteSelected() {
        return deleteCheckBox.isSelected();
    }
    
    /**
     * Return project name
     * @return project name
     */
    public String getProjectName() {
        return projectNameField.getText();
    }
    
    /**
     * Set project name label (used if it's a source root)
     */
    public void setProjectNameLabel(String label) {
        projectName.setText(label);
    }
        
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JCheckBox deleteCheckBox;
    private javax.swing.JButton dirChooseButton;
    private javax.swing.JButton okButton;
    private javax.swing.JLabel projectName;
    private javax.swing.JTextField projectNameField;
    private javax.swing.JLabel projectZip;
    private javax.swing.JTextField projectZipField;
    private javax.swing.JLabel unzipToDirectory;
    private javax.swing.JTextField unzipToDirectoryField;
    private javax.swing.JButton zipChooseButton;
    // End of variables declaration//GEN-END:variables
    
}
