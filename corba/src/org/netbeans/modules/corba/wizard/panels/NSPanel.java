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

package org.netbeans.modules.corba.wizard.panels;

import java.beans.VetoableChangeListener;
import java.beans.PropertyChangeListener;
import java.awt.event.*;
import org.openide.explorer.*;
import org.openide.explorer.view.*;
import org.openide.*;
import org.openide.nodes.*;
import org.netbeans.modules.corba.wizard.*;
import org.openide.util.actions.*;
import org.netbeans.modules.corba.wizard.panels.util.CosNamingDetails;
/**
 *
 * @author  tzezula
 */
public class NSPanel extends BindingDetail implements PropertyChangeListener, VetoableChangeListener, javax.swing.event.DocumentListener {
    
    BeanTreeView bt;
    
    /** Creates new form NSPanel */
    public NSPanel() {
        initComponents();
        this.bt = new BeanTreeView();
        this.bt.setPopupAllowed (false);
        this.bt.setDefaultActionAllowed (false);
        this.explorer.add (bt);
        this.explorer.setBorder(new javax.swing.border.EtchedBorder());
        ExplorerManager manager = this.explorer.getExplorerManager();
        Node node = org.netbeans.modules.corba.browser.ns.ContextNode.getDefault();
        manager.setRootContext (node);
        manager.addPropertyChangeListener (this);
        manager.addVetoableChangeListener (this);
        this.contextName.setEditable (false);
        this.contextName.addFocusListener ( new java.awt.event.FocusListener () {
            public void focusGained (java.awt.event.FocusEvent event) {
                ((javax.swing.JTextField)event.getSource()).selectAll();
            }
            
            public void focusLost (java.awt.event.FocusEvent event) {
            }
        });
        this.newButton.setEnabled (false);
        this.bindButton.setEnabled (false);
        this.name.getDocument().addDocumentListener (this);
        this.jLabel1.setDisplayedMnemonic (this.bundle.getString("TXT_NameServiceBrowser_MNE").charAt(0));
        this.jLabel2.setDisplayedMnemonic (this.bundle.getString("TXT_NSName_MNE").charAt(0));
        this.jLabel3.setDisplayedMnemonic (this.bundle.getString("TXT_NamingContext_MNE").charAt(0));
        this.jLabel4.setDisplayedMnemonic (this.bundle.getString("TXT_NSKind_MNE").charAt(0));
        this.bindButton.setMnemonic (this.bundle.getString("TXT_BindContext_MNE").charAt(0));
        this.newButton.setMnemonic (this.bundle.getString("TXT_NewContext_MNE").charAt(0));
        this.refreshButton.setMnemonic (this.bundle.getString("TXT_IRRefresh_MNE").charAt(0));
        this.explorer.getAccessibleContext().setAccessibleDescription (this.bundle.getString("AD_NameServiceBrowser"));
        this.contextName.getAccessibleContext().setAccessibleDescription (this.bundle.getString("AD_NamingContext"));
        this.name.getAccessibleContext().setAccessibleDescription(this.bundle.getString("AD_NSName"));
        this.kind.getAccessibleContext().setAccessibleDescription(this.bundle.getString("AD_NSKind"));
        this.bindButton.getAccessibleContext().setAccessibleDescription(this.bundle.getString("AD_BindContext"));
        this.newButton.getAccessibleContext().setAccessibleDescription(this.bundle.getString("AD_NewContext"));
        this.refreshButton.getAccessibleContext().setAccessibleDescription(this.bundle.getString("AD_IRRefresh"));
        this.getAccessibleContext().setAccessibleDescription (this.bundle.getString ("AD_NSPanel"));
    }
    
    
    public void setData (Object data) {
        try {
            if (data instanceof CosNamingDetails) {
                CosNamingDetails nsd = (CosNamingDetails) data;
                if (nsd != null) {
                    if (nsd.node != null && (nsd.node instanceof org.netbeans.modules.corba.browser.ns.ContextNode ||
                    nsd.node instanceof org.netbeans.modules.corba.browser.ns.ObjectNode))
                        this.explorer.getExplorerManager().setSelectedNodes(new Node[]{nsd.node});
                        if (nsd.name != null)
                            this.name.setText (nsd.name);
                        if (nsd.kind != null)
                            this.kind.setText (nsd.kind);
                }
            }
        }
        catch (Exception e){
        }
    }
    
    public Object getData () {
        CosNamingDetails nsDetails = new CosNamingDetails ();
        ExplorerManager manager = this.explorer.getExplorerManager();
        Node[] nodes = manager.getSelectedNodes();
        if (nodes != null && nodes.length == 1)
            if (nodes[0] instanceof org.netbeans.modules.corba.browser.ns.ContextNode)
                nsDetails.node = nodes[0];
            else if (nodes[0] instanceof org.netbeans.modules.corba.browser.ns.ObjectNode)
                nsDetails.node = nodes[0].getParentNode();
        nsDetails.name = this.name.getText();
        nsDetails.kind = this.kind.getText();
        return nsDetails;
    }
    
    public void setTitle (String title) {
    }
    
    public String getTitle () {
        return "";
    }
    
    public boolean isValid () {
        ExplorerManager manager = this.explorer.getExplorerManager();
        Node[] nodes = manager.getSelectedNodes();
        if (nodes == null)
            return false;
        if (nodes.length != 1)
            return false;
        if (this.name.getText().length() == 0)
            return false;
        return (nodes[0] instanceof org.netbeans.modules.corba.browser.ns.ContextNode && nodes[0] != org.netbeans.modules.corba.browser.ns.ContextNode.getDefault()) ||
        (nodes[0] instanceof org.netbeans.modules.corba.browser.ns.ObjectNode);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        explorer = new org.openide.explorer.ExplorerPanel();
        bindButton = new javax.swing.JButton();
        newButton = new javax.swing.JButton();
        refreshButton = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        contextName = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        name = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        kind = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();

        setLayout(new java.awt.GridBagLayout());

        setPreferredSize(new java.awt.Dimension(500, 340));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 0, 6);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(explorer, gridBagConstraints);

        bindButton.setText(bundle.getString("TXT_BindContext"));
        bindButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bindContext(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(12, 6, 6, 12);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.1;
        add(bindButton, gridBagConstraints);

        newButton.setText(bundle.getString("TXT_NewContext"));
        newButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newContext(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 12);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.1;
        add(newButton, gridBagConstraints);

        refreshButton.setText(bundle.getString("TXT_IRRefresh"));
        refreshButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 12);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.1;
        add(refreshButton, gridBagConstraints);

        jLabel3.setText(bundle.getString("TXT_NamingContext"));
        jLabel3.setLabelFor(contextName);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 0, 6);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(jLabel3, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(12, 6, 0, 12);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        add(contextName, gridBagConstraints);

        jLabel2.setText(bundle.getString("TXT_NSName"));
        jLabel2.setLabelFor(name);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 0, 6);
        add(jLabel2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(12, 6, 0, 12);
        add(name, gridBagConstraints);

        jLabel4.setText(bundle.getString("TXT_NSKind"));
        jLabel4.setLabelFor(kind);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 12, 6);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(jLabel4, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(12, 6, 12, 12);
        add(kind, gridBagConstraints);

        jLabel1.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/corba/wizard/panels/Bundle").getString("TXT_NameServiceBrowser"));
        jLabel1.setLabelFor(explorer);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 0, 12);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(jLabel1, gridBagConstraints);

    }//GEN-END:initComponents

    private void refreshButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshButtonActionPerformed
        Node[] nodes = this.explorer.getExplorerManager().getSelectedNodes();
        SystemAction action = SystemAction.get (org.netbeans.modules.corba.browser.ns.RefreshAction.class);
        ActionEvent event = new ActionEvent (nodes,0,"");
        action.actionPerformed (event);
    }//GEN-LAST:event_refreshButtonActionPerformed
    
/*    private void initServerComponents() {
        jLabel2 = new javax.swing.JLabel();
        name = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        kind = new javax.swing.JTextField();
 
        jLabel2.setText(bundle.getString("TXT_NSName"));
        java.awt.GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 0, 6);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(jLabel2, gridBagConstraints);
 
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(12, 6, 0, 12);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(name, gridBagConstraints);
 
        jLabel4.setText(bundle.getString("TXT_NSKind"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 0, 6);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(jLabel4, gridBagConstraints);
 
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(12, 6, 0, 12);
        add(kind, gridBagConstraints);
    }
 */
    private void bindContext(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bindContext
        // Add your handling code here:
        Node[] nodes = this.explorer.getExplorerManager().getSelectedNodes();
        if (nodes.length != 1 || ! (nodes[0] instanceof org.netbeans.modules.corba.browser.ns.ContextNode))
            return;
        SystemAction action = SystemAction.get (org.netbeans.modules.corba.browser.ns.BindNewContext.class);
        java.awt.event.ActionEvent event = new java.awt.event.ActionEvent (nodes,0,"");
        action.actionPerformed (event);
    }//GEN-LAST:event_bindContext
    
    private void newContext(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newContext
        // Add your handling code here:
        Node[] nodes = this.explorer.getExplorerManager().getSelectedNodes();
        if (nodes.length != 1)
            return;
        SystemAction action = SystemAction.get (org.netbeans.modules.corba.browser.ns.CreateNewContext.class);
        java.awt.event.ActionEvent event = new java.awt.event.ActionEvent (nodes,0,"");
        action.actionPerformed (event);
    }//GEN-LAST:event_newContext
    
    public void propertyChange(java.beans.PropertyChangeEvent event) {
        Object newValue = event.getNewValue();
        if (newValue == null || ! (newValue instanceof Node[]))
            return;
        Node[] node = (Node[])newValue;
        if (node.length != 1)
            return;
        if (node[0] instanceof org.netbeans.modules.corba.browser.ns.ContextNode) {
            this.contextName.setText(node[0].getName());
            if (node[0] != org.netbeans.modules.corba.browser.ns.ContextNode.getDefault())
                this.newButton.setEnabled (true);
            else
                this.newButton.setEnabled (false);
            this.bindButton.setEnabled (true);
            if (((org.netbeans.modules.corba.browser.ns.ContextNode)node[0]).isValid())
                this.refreshButton.setEnabled (true);
        }
        else if (node[0] instanceof org.netbeans.modules.corba.browser.ns.ObjectNode) {
            this.contextName.setText(node[0].getParentNode().getName());
            this.name.setText(node[0].getName());
            this.kind.setText(((org.netbeans.modules.corba.browser.ns.ObjectNode)node[0]).getKind());
            this.newButton.setEnabled (false);
            this.bindButton.setEnabled (false);
            this.refreshButton.setEnabled (false);
      }
        this.fireChange (this);
    }
    
    public void vetoableChange(java.beans.PropertyChangeEvent event) throws java.beans.PropertyVetoException {
        Object newValue = event.getNewValue();
        if (newValue == null || ! (newValue instanceof Node[]))
            return;
        Node[] nodes = (Node[]) newValue;
        if (nodes.length != 1)
            throw new java.beans.PropertyVetoException ("",event);
	if (!(nodes[0] instanceof org.netbeans.modules.corba.browser.ns.ContextNode || nodes[0] instanceof org.netbeans.modules.corba.browser.ns.ObjectNode))
	    throw new java.beans.PropertyVetoException ("",event);
    }
    
    public void changedUpdate(javax.swing.event.DocumentEvent event) {
        // No need to be called
    }
    
    public void removeUpdate(javax.swing.event.DocumentEvent event) {
        this.fireChange (this);
    }
    
    public void insertUpdate(javax.swing.event.DocumentEvent event) {
        this.fireChange (this);
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton newButton;
    private javax.swing.JButton bindButton;
    private javax.swing.JTextField contextName;
    private javax.swing.JButton refreshButton;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JTextField name;
    private javax.swing.JTextField kind;
    private org.openide.explorer.ExplorerPanel explorer;
    // End of variables declaration//GEN-END:variables
    
    private static final java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/netbeans/modules/corba/wizard/panels/Bundle");
}
