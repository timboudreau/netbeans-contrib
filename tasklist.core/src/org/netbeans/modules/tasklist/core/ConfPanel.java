/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.tasklist.core;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import org.openide.util.NbBundle;

import javax.swing.text.*;


/**
 * A confirmation panel for suggestion fixes etc.
 *
 * @author  Tor Norbye
 */
public class ConfPanel extends javax.swing.JPanel {
    
    /** Creates new form ConfPanel */
    public ConfPanel(String beforeDesc, String beforeContents,
                     String afterDesc, String afterContents,
                     String filename, int line, JPanel bottomPanel) {
        initComponents();
        
        mainLabel.setText(beforeDesc);
        beforeLabel.setText(beforeContents);
        if (afterDesc != null) {
            changedToLabel.setText(afterDesc);
        } else {
            changedToLabel.setVisible(false);
        }
        if (afterContents != null) {
            afterLabel.setText(afterContents);
        } else {
            afterLabel.setVisible(false);
        }
        fileLabel.setText(filename);
        if (line >= 0) {
            lineLabel.setText(Integer.toString(line));
        } else {
            lineLabel.setVisible(false);
            jLabel5.setVisible(false);
        }
        if (bottomPanel != null) {
            addPanel.setLayout(new BorderLayout());
            addPanel.add(bottomPanel, BorderLayout.CENTER);
        } else {
            addPanel.setVisible(false);
        }
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        mainLabel = new javax.swing.JLabel();
        beforeLabel = new javax.swing.JLabel();
        changedToLabel = new javax.swing.JLabel();
        afterLabel = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        fileLabel = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        lineLabel = new javax.swing.JLabel();
        addPanel = new javax.swing.JPanel();

        setLayout(new java.awt.GridBagLayout());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 0, 11);
        add(mainLabel, gridBagConstraints);

        beforeLabel.setBackground((java.awt.Color) javax.swing.UIManager.getDefaults().get("TextField.background"));
        beforeLabel.setOpaque(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 0, 11);
        add(beforeLabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 0, 11);
        add(changedToLabel, gridBagConstraints);

        afterLabel.setBackground((java.awt.Color) javax.swing.UIManager.getDefaults().get("TextField.background"));
        afterLabel.setOpaque(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 0, 11);
        add(afterLabel, gridBagConstraints);

        jLabel3.setText(NbBundle.getMessage(ConfPanel.class, "File")); // NOI18N();
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(18, 12, 0, 11);
        add(jLabel3, gridBagConstraints);

        fileLabel.setText("Test1");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(18, 0, 0, 11);
        add(fileLabel, gridBagConstraints);

        jLabel5.setText(NbBundle.getMessage(ConfPanel.class, "Line")); // NOI18N();
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 0, 11);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(jLabel5, gridBagConstraints);

        lineLabel.setText("Test2");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 11);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(lineLabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 11, 11);
        add(addPanel, gridBagConstraints);

    }//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel mainLabel;
    private javax.swing.JPanel addPanel;
    private javax.swing.JLabel fileLabel;
    private javax.swing.JLabel changedToLabel;
    private javax.swing.JLabel beforeLabel;
    private javax.swing.JLabel afterLabel;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel lineLabel;
    // End of variables declaration//GEN-END:variables
    
}
