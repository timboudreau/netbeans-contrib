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


package org.netbeans.modules.helpbuilder.ui;

import org.netbeans.modules.helpbuilder.*;
import java.awt.Component;
import java.io.File;
import java.net.URL;
import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.swing.JFileChooser;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.tree.TreePath;
import org.netbeans.modules.helpbuilder.tree.HelpTree;
import org.netbeans.modules.helpbuilder.tree.HelpTreeNode;
import org.netbeans.modules.helpbuilder.tree.IndexTreeItem;
import org.netbeans.modules.helpbuilder.tree.IndexTreeNode;

import org.openide.WizardDescriptor;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

/** 
 *
 * @author  Richard Gregor
 */
public class IndexSetupPanel extends javax.swing.JPanel {
    private final IndexSetup descriptor;
    private HelpTree tree;
    private static IndexTreeNode node;
    
    /** Create the wizard panel and set up some basic properties. */
    public IndexSetupPanel(final IndexSetup descriptor) {
        this.descriptor = descriptor;
        initComponents ();                
        node = new IndexTreeNode(new IndexTreeItem("Root Node"));
        IndexTreeNode node1 = new IndexTreeNode(new IndexTreeItem("Index"));
        IndexTreeNode node2 = new IndexTreeNode(new IndexTreeItem("Sub Index"));
        node.add(node1);
        node1.add(node2);        
        tree = new HelpTree(node);
        tree.setRootVisible(false);
        TreePath newPath = new TreePath(node2.getPath());
        tree.makeVisible(newPath);        
        tree.scrollPathToVisible(newPath);
        jScrollPane1.setViewportView(tree);
        initActions();
        
        javax.swing.event.DocumentListener docList = new javax.swing.event.DocumentListener() {
                public void insertUpdate(javax.swing.event.DocumentEvent e) {                    
                    descriptor.fireChangeEvent();
                }
                public void removeUpdate(javax.swing.event.DocumentEvent e) {
                    descriptor.fireChangeEvent();                   
                }
                public void changedUpdate(javax.swing.event.DocumentEvent e) {                  
                    descriptor.fireChangeEvent();
                }
        };       
        setName(NbBundle.getMessage(ProjectSetupPanel.class, "TITLE_IndexPanel"));
        /*
        // Optional: provide a special description for this pane.
        // You must have turned on WizardDescriptor.WizardPanel_helpDisplayed
        // (see descriptor in standard iterator template for an example of this).
        try {
            putClientProperty ("WizardPanel_helpURL", // NOI18N
                new URL ("nbresloc:/wizard/Step1PanelHelp.html")); // NOI18N
        } catch (MalformedURLException mfue) {
            throw new IllegalStateException (mfue.toString ());
        }
        */
    }

    public static HelpTreeNode getNode(){
        return (HelpTreeNode) node;
    }
    
    private void initActions(){
        btnRemove.setAction(tree.getRemoveAction());
        btnRemove.setMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/helpbuilder/ui/Bundle").getString("ACS_btnRemove_mnc").charAt(0));
        btnAdd.setAction(tree.getAddIndexAction());
        btnAdd.setMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/helpbuilder/ui/Bundle").getString("ACS_btnAdd_mnc").charAt(0));
        btnRight.setAction(tree.getRightAction());
        btnRight.setMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/helpbuilder/ui/Bundle").getString("ACS_btnRight_mnc").charAt(0));
        btnUp.setAction(tree.getUpAction());
        btnUp.setMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/helpbuilder/ui/Bundle").getString("ACS_btnUp_mnc").charAt(0));
        btnDown.setAction(tree.getDownAction());
        btnDown.setMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/helpbuilder/ui/Bundle").getString("ACS_btnDown_mnc").charAt(0));
        btnLeft.setAction(tree.getLeftAction());
        btnLeft.setMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/helpbuilder/ui/Bundle").getString("ACS_btnLeft_mnc").charAt(0));
        btnEdit.setAction(tree.getEditIndexAction());
        btnEdit.setMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/helpbuilder/ui/Bundle").getString("ACS_btnEdit_mnc").charAt(0));
    }
        
    // --- VISUAL DESIGN OF PANEL ---

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        txtDesc = new javax.swing.JTextArea();
        contentPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        propertiesPanel = new javax.swing.JPanel();
        btnLeft = new javax.swing.JButton();
        btnRight = new javax.swing.JButton();
        btnDown = new javax.swing.JButton();
        btnUp = new javax.swing.JButton();
        btnRemove = new javax.swing.JButton();
        btnAdd = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        btnEdit = new javax.swing.JButton();
        topPanel = new javax.swing.JPanel();
        bottomPanel = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        rightPanel = new javax.swing.JPanel();

        setLayout(new java.awt.BorderLayout());

        setMinimumSize(new java.awt.Dimension(700, 417));
        setPreferredSize(new java.awt.Dimension(700, 417));
        txtDesc.setBackground((java.awt.Color) javax.swing.UIManager.getDefaults().get("Button.background"));
        txtDesc.setEditable(false);
        txtDesc.setForeground((java.awt.Color) javax.swing.UIManager.getDefaults().get("RadioButtonMenuItem.acceleratorForeground"));
        txtDesc.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/helpbuilder/ui/Bundle").getString("TXT_IndextxtDesc"));
        add(txtDesc, java.awt.BorderLayout.NORTH);

        contentPanel.setLayout(new java.awt.BorderLayout());

        contentPanel.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        propertiesPanel.setLayout(new java.awt.GridBagLayout());

        btnLeft.setMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/helpbuilder/ui/Bundle").getString("ACS_btnLeft_mnc").charAt(0));
        btnLeft.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/helpbuilder/ui/Bundle").getString("btnLeft"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 12, 6);
        propertiesPanel.add(btnLeft, gridBagConstraints);

        btnRight.setMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/helpbuilder/ui/Bundle").getString("ACS_btnRight_mnc").charAt(0));
        btnRight.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/helpbuilder/ui/Bundle").getString("btnRight"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 6, 6);
        propertiesPanel.add(btnRight, gridBagConstraints);

        btnDown.setMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/helpbuilder/ui/Bundle").getString("ACS_btnDown_mnc").charAt(0));
        btnDown.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/helpbuilder/ui/Bundle").getString("btnDown"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 6, 6);
        propertiesPanel.add(btnDown, gridBagConstraints);

        btnUp.setMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/helpbuilder/ui/Bundle").getString("ACS_btnUp_mnc").charAt(0));
        btnUp.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/helpbuilder/ui/Bundle").getString("btnUp"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(24, 6, 6, 6);
        propertiesPanel.add(btnUp, gridBagConstraints);

        btnRemove.setMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/helpbuilder/ui/Bundle").getString("ACS_btnRemove_mnc").charAt(0));
        btnRemove.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/helpbuilder/ui/Bundle").getString("btnRemove"));
        btnRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 24, 6);
        propertiesPanel.add(btnRemove, gridBagConstraints);

        btnAdd.setMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/helpbuilder/ui/Bundle").getString("ACS_btnAdd_mnc").charAt(0));
        btnAdd.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/helpbuilder/ui/Bundle").getString("btnAdd"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 6, 6);
        propertiesPanel.add(btnAdd, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.weighty = 1.0;
        propertiesPanel.add(jPanel1, gridBagConstraints);

        btnEdit.setMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/helpbuilder/ui/Bundle").getString("ACS_btnEdit_mnc").charAt(0));
        btnEdit.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/helpbuilder/ui/Bundle").getString("btnEdit"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 0, 6);
        propertiesPanel.add(btnEdit, gridBagConstraints);

        contentPanel.add(propertiesPanel, java.awt.BorderLayout.EAST);

        contentPanel.add(topPanel, java.awt.BorderLayout.NORTH);

        bottomPanel.setLayout(new java.awt.GridBagLayout());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.weightx = 1.0;
        bottomPanel.add(jPanel2, gridBagConstraints);

        contentPanel.add(bottomPanel, java.awt.BorderLayout.SOUTH);

        contentPanel.add(rightPanel, java.awt.BorderLayout.WEST);

        add(contentPanel, java.awt.BorderLayout.CENTER);

    }//GEN-END:initComponents

    private void btnRemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveActionPerformed
        //tree.removeItem();
    }//GEN-LAST:event_btnRemoveActionPerformed

    public boolean isValid(){        
     /*   String txtHome = txtHomeID.getText();
        if((txtHome == null) || (txtHome.trim().length() < 1))
            return false;
        else*/
            return true;    
    }
        
        
        
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel topPanel;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnUp;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JButton btnDown;
    private javax.swing.JTextArea txtDesc;
    private javax.swing.JPanel propertiesPanel;
    private javax.swing.JButton btnLeft;
    private javax.swing.JButton btnEdit;
    private javax.swing.JButton btnRemove;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel rightPanel;
    private javax.swing.JButton btnRight;
    private javax.swing.JPanel contentPanel;
    private javax.swing.JPanel bottomPanel;
    // End of variables declaration//GEN-END:variables



}
