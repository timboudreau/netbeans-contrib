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

package org.netbeans.bluej.export;

import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 *
 * @author  mkleint
 */
public class ExportPanel extends javax.swing.JPanel {
    
    /** Creates new form ExportPanel */
    public ExportPanel(FileObject dir, final ExportWizardPanel1 wizPanel) {
        initComponents();
        txtSource.setText(dir.getPath());
        setName("Convert Project");
        txtFolder.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                wizPanel.updateValue(txtFolder.getText());
            }
            public void insertUpdate(DocumentEvent e) {
                wizPanel.updateValue(txtFolder.getText());
            }
            public void removeUpdate(DocumentEvent e) {
                wizPanel.updateValue(txtFolder.getText());
            }
        });
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        lblWarning = new javax.swing.JLabel();
        lblFolder = new javax.swing.JLabel();
        txtFolder = new javax.swing.JTextField();
        btnFolder = new javax.swing.JButton();
        lblSource = new javax.swing.JLabel();
        txtSource = new javax.swing.JTextField();
        lblWarning2 = new javax.swing.JLabel();

        lblWarning.setText("Specify the BlueJ project to convert.");
        lblWarning.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        lblFolder.setLabelFor(lblFolder);
        lblFolder.setText("Destination Folder :");

        btnFolder.setText("Browse...");
        btnFolder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFolderActionPerformed(evt);
            }
        });

        lblSource.setLabelFor(txtSource);
        lblSource.setText("Project To Convert :");

        txtSource.setEditable(false);
        txtSource.setEnabled(false);

        lblWarning2.setText("Note: This will convert the specified BlueJ project to a NetBeans project with split classes and tests.");

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(lblWarning)
                    .add(layout.createSequentialGroup()
                        .add(lblSource)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(txtSource, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 517, Short.MAX_VALUE))
                    .add(layout.createSequentialGroup()
                        .add(lblFolder)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(txtFolder, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 413, Short.MAX_VALUE)
                        .add(12, 12, 12)
                        .add(btnFolder, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 92, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(lblWarning2))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(lblWarning)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblSource)
                    .add(txtSource, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblFolder)
                    .add(txtFolder, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(btnFolder))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lblWarning2)
                .addContainerGap(178, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    
    private void btnFolderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFolderActionPerformed
        JFileChooser chooser = new JFileChooser();
        FileUtil.preventFileChooserSymlinkTraversal(chooser, null);
        chooser.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
        chooser.setMultiSelectionEnabled( false );
        int option = chooser.showOpenDialog( SwingUtilities.getWindowAncestor( this ) ); // Sow the chooser
        if (txtFolder.getText().length() > 0) {
            chooser.setCurrentDirectory(new File(txtFolder.getText().trim()));
        }
        if ( option == JFileChooser.APPROVE_OPTION ) {
            
            File file = chooser.getSelectedFile();
            txtFolder.setText(FileUtil.normalizeFile(file).getAbsolutePath());
        }
        
    }//GEN-LAST:event_btnFolderActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnFolder;
    private javax.swing.JLabel lblFolder;
    private javax.swing.JLabel lblSource;
    private javax.swing.JLabel lblWarning;
    private javax.swing.JLabel lblWarning2;
    private javax.swing.JTextField txtFolder;
    private javax.swing.JTextField txtSource;
    // End of variables declaration//GEN-END:variables
    
}
