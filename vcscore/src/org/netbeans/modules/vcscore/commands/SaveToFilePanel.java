/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcscore.commands;

/**
 * Dialog that propmts the user for details when saving the output of the command to disk.
 *
 * @author  Milos Kleint
 */

import java.util.*;
import java.io.*;
import org.netbeans.modules.vcscore.util.*;
import javax.swing.*;

public class SaveToFilePanel extends javax.swing.JPanel {

    public static final int STDOUT_INDEX = 0;
    public static final int STDERR_INDEX = 1;
    public static final int DATOUT_INDEX = 2;
    public static final int DATERR_INDEX = 3;    
    /** Creates new form SaveToFilePanel */
    public SaveToFilePanel() {
        initComponents();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        lblFile = new javax.swing.JLabel();
        txFile = new javax.swing.JTextField();
        btnFile = new javax.swing.JButton();
        cbStdOut = new javax.swing.JCheckBox();
        cbStdErr = new javax.swing.JCheckBox();
        cbDataOut = new javax.swing.JCheckBox();
        cbDataErr = new javax.swing.JCheckBox();

        setLayout(new java.awt.GridBagLayout());
        java.awt.GridBagConstraints gridBagConstraints1;

        lblFile.setText(org.openide.util.NbBundle.getBundle(SaveToFilePanel.class).getString("SaveToFilePanel.lblFile.text"));
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.insets = new java.awt.Insets(12, 12, 0, 0);
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        add(lblFile, gridBagConstraints1);

        txFile.setPreferredSize(new java.awt.Dimension(200, 21));
        txFile.setMinimumSize(new java.awt.Dimension(100, 21));
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 1;
        gridBagConstraints1.gridy = 0;
        gridBagConstraints1.gridwidth = 6;
        gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints1.ipadx = 63;
        gridBagConstraints1.insets = new java.awt.Insets(12, 6, 0, 0);
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        add(txFile, gridBagConstraints1);

        btnFile.setText(org.openide.util.NbBundle.getBundle(SaveToFilePanel.class).getString("SaveToFilePanel.btnFile.text"));
        btnFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFileActionPerformed(evt);
            }
        });

        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 7;
        gridBagConstraints1.gridy = 0;
        gridBagConstraints1.insets = new java.awt.Insets(12, 6, 0, 12);
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        add(btnFile, gridBagConstraints1);

        cbStdOut.setText(org.openide.util.NbBundle.getBundle(SaveToFilePanel.class).getString("SaveToFilePanel.cbStdOut"));
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.gridy = 1;
        gridBagConstraints1.gridwidth = 4;
        gridBagConstraints1.insets = new java.awt.Insets(6, 12, 0, 0);
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        add(cbStdOut, gridBagConstraints1);

        cbStdErr.setText(org.openide.util.NbBundle.getBundle(SaveToFilePanel.class).getString("SaveToFilePanel.cbStdErr"));
        cbStdErr.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbStdErrActionPerformed(evt);
            }
        });

        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.gridy = 2;
        gridBagConstraints1.gridwidth = 4;
        gridBagConstraints1.insets = new java.awt.Insets(6, 12, 12, 0);
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        add(cbStdErr, gridBagConstraints1);

        cbDataOut.setText(org.openide.util.NbBundle.getBundle(SaveToFilePanel.class).getString("SaveToFilePanel.cbDataOut"));
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 6;
        gridBagConstraints1.gridy = 1;
        gridBagConstraints1.gridwidth = 2;
        gridBagConstraints1.insets = new java.awt.Insets(6, 6, 0, 0);
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        add(cbDataOut, gridBagConstraints1);

        cbDataErr.setText(org.openide.util.NbBundle.getBundle(SaveToFilePanel.class).getString("SaveToFilePanel.cbDataErr"));
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 6;
        gridBagConstraints1.gridy = 2;
        gridBagConstraints1.gridwidth = 2;
        gridBagConstraints1.insets = new java.awt.Insets(6, 6, 12, 0);
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        add(cbDataErr, gridBagConstraints1);

    }//GEN-END:initComponents

    private void btnFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFileActionPerformed
        // Add your handling code here:
        File initDir = new File(txFile.getText());
        if (initDir.exists()) {
            if (initDir.isFile()) {
                initDir = initDir.getParentFile();
            }
        } else {
            initDir = new File(System.getProperty("user.home")); //NOI18N
        }
        ChooseFileDialog chooseDir = new ChooseFileDialog(new JFrame(), initDir, false);
        VcsUtilities.centerWindow (chooseDir);
//        HelpCtx.setHelpIDString (chooseDir.getRootPane (), );
        chooseDir.show();
        String selected=chooseDir.getSelectedFile();
        if( selected==null ){
            //D.deb("no directory selected"); // NOI18N
            return ;
        }
        txFile.setText(selected);      
    }//GEN-LAST:event_btnFileActionPerformed

    private void cbStdErrActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbStdErrActionPerformed
        // Add your handling code here:
    }//GEN-LAST:event_cbStdErrActionPerformed

    /** Getter for property setCurrentPanel.
     * @return Value of property setCurrentPanel.
     */
    public int getCurrentPanel() {
        return this.currentPanel;
    }    

    /** Setter for property setCurrentPanel.
     * @param setCurrentPanel New value of property setCurrentPanel.
     */
    public void setCurrentPanel(int setCurrentPanel) {
        this.currentPanel = setCurrentPanel;
        disSelectAll();
        switch (currentPanel) {
            case STDOUT_INDEX : { 
                cbStdOut.setSelected(true);
                break;
            }
            case STDERR_INDEX : {
                cbStdErr.setSelected(true);
                break;
            }
            case DATERR_INDEX : {
                cbDataErr.setSelected(true);
                break;
            }
            case DATOUT_INDEX : {
                cbStdOut.setSelected(true);
                break;
            } 
        }
    }    
    
    private void disSelectAll() {
        cbDataErr.setSelected(false);
        cbDataOut.setSelected(false);
        cbStdOut.setSelected(false);
        cbStdErr.setSelected(false);
    }
    
    public boolean includeStdOut() {
        return cbStdOut.isSelected();
    }
    public boolean includeStdErr() {
        return cbStdErr.isSelected();
    }
    public boolean includeDatOut() {
        return cbDataOut.isSelected();
    }
    public boolean includeDatErr() {
        return cbDataErr.isSelected();
    }
    
    public String getFile() {
        return txFile.getText();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel lblFile;
    private javax.swing.JTextField txFile;
    private javax.swing.JButton btnFile;
    private javax.swing.JCheckBox cbStdOut;
    private javax.swing.JCheckBox cbStdErr;
    private javax.swing.JCheckBox cbDataOut;
    private javax.swing.JCheckBox cbDataErr;
    // End of variables declaration//GEN-END:variables

    /** Holds value of property setCurrentPanel. */
    private int currentPanel;    

    private static final long serialVersionUID = -1047633353725226509L;
    
}
