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

import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.ButtonGroup;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.*;
import java.beans.PropertyChangeListener;
import javax.swing.border.EtchedBorder;
import javax.swing.event.DocumentListener;
import org.openide.TopManager;
import org.openide.NotifyDescriptor;
import org.openide.Places;
import org.openide.nodes.Node;
import org.openide.nodes.Children;
import org.openide.util.actions.SystemAction;
import org.openide.explorer.view.BeanTreeView;
import org.openide.loaders.TemplateWizard;
import org.netbeans.modules.corba.wizard.IDLWizardData;
import org.netbeans.modules.corba.wizard.CorbaWizard;
import org.netbeans.modules.corba.wizard.utils.IdlFileFilter;
import org.netbeans.modules.corba.browser.ir.util.GenerateSupport;
import org.netbeans.modules.corba.browser.ir.util.Generatable;
import org.netbeans.modules.corba.browser.ir.nodes.IRContainerNode;

/** 
 *
 * @author  tzezula
 * @version 
 */
public class ImportIDLPanel extends AbstractIDLWizardPanel implements PropertyChangeListener {
  
    private BeanTreeView tv;
    private boolean fileValid = false;
    private IDLWizardData data;

    /** Creates new form IDLPanel */
    public ImportIDLPanel(IDLWizardData data) {
        this.data = data;
        initComponents ();
        this.tree.setLayout( new BorderLayout());
        this.tree.getExplorerManager().addPropertyChangeListener(this);
        this.tv = new BeanTreeView();
        this.tree.setBorder(new EtchedBorder());
        this.tree.add(this.tv, BorderLayout.CENTER);
        // Get the CORBA Ir Node
        this.tree.getExplorerManager().setRootContext(org.netbeans.modules.corba.browser.ir.IRRootNode.getDefault());
        this.tv.setPopupAllowed (false);
        this.tv.setDefaultActionAllowed (false);
        this.irId.setEditable(false);
        this.irId.addFocusListener ( new java.awt.event.FocusListener () {
            
            public void focusGained (java.awt.event.FocusEvent event) {
                ((javax.swing.JTextField)event.getSource()).selectAll();
            }
            
            public void focusLost (java.awt.event.FocusEvent event) {
            }
        });
        this.connect.setEnabled (false);
        this.refresh.setEnabled (false);
        this.setName(bundle.getString("TXT_CreateIDL"));
        putClientProperty(CorbaWizard.PROP_CONTENT_SELECTED_INDEX, new Integer(2));
        this.irLabel.setDisplayedMnemonic (this.bundle.getString("TXT_IrId_MNE").charAt(0));
        this.jLabel1.setDisplayedMnemonic (this.bundle.getString("TXT_InterfaceRepositoryBrowser_MNE").charAt(0));
        this.connect.setMnemonic (this.bundle.getString("TXT_IRConnect_MNE").charAt(0));
        this.refresh.setMnemonic (this.bundle.getString("TXT_IRRefresh_MNE").charAt(0));
        this.tree.getAccessibleContext().setAccessibleDescription(this.bundle.getString("AD_InterfaceRepositoryBrowser"));
        this.irId.getAccessibleContext().setAccessibleDescription(this.bundle.getString("AD_IrId"));
        this.connect.getAccessibleContext().setAccessibleDescription (this.bundle.getString("AD_IRConnect"));
        this.refresh.getAccessibleContext().setAccessibleDescription (this.bundle.getString("AD_IRRefresh"));
    }
  
    /** Sets the panel data from CorbaWizardDescriptor
     *  @param CorbaWizardDescriptor data
     */
    public void readIDLSettings (TemplateWizard data){
    }
  
    /** Stores panel data to CorbaWizardDescriptor
     *  @param CorbaWizardDescriptor data
     */
    public void storeIDLSettings (TemplateWizard data){
        String source="";
        Node[] nodes = this.tree.getExplorerManager().getSelectedNodes();
        try {
            java.io.StringWriter buffer = new java.io.StringWriter();
            java.io.PrintWriter out = new java.io.PrintWriter (buffer);
            for (int i=0; i< nodes.length; i++) {
                if (nodes[i] instanceof org.netbeans.modules.corba.browser.ir.util.Generatable)
                    ((org.netbeans.modules.corba.browser.ir.util.Generatable)nodes[i]).generateCode (out);
            }
            source = buffer.toString();
            this.data.setIdlSource (source);
        }catch (Exception ioe) {
            // Handle exception here
            TopManager.getDefault().getErrorManager().log (ioe.toString());
            this.data.setIdlSource (null);
            return;
        }
    }

    public boolean isValid () {
        Node[] nodes = this.tree.getExplorerManager().getSelectedNodes();
        if (nodes == null || nodes.length == 0)
            return false;
        boolean result = true;
        for (int i=0; i< nodes.length; i++) {
            if (!(nodes[0] instanceof Generatable)){
                result = false;
                break;
            }
        }
        return result;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the FormEditor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        irLabel = new javax.swing.JLabel();
        irId = new javax.swing.JTextField();
        tree = new org.openide.explorer.ExplorerPanel();
        connect = new javax.swing.JButton();
        refresh = new javax.swing.JButton();

        setLayout(new java.awt.GridBagLayout());

        setToolTipText("");
        setPreferredSize(new java.awt.Dimension(500, 300));
        jPanel2.setLayout(new java.awt.GridBagLayout());

        jLabel1.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/corba/wizard/panels/Bundle").getString("TXT_InterfaceRepositoryBrowser"));
        jLabel1.setLabelFor(tree);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 0, 12);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel2.add(jLabel1, gridBagConstraints);

        jPanel3.setLayout(new java.awt.GridBagLayout());

        irLabel.setText(bundle.getString("TXT_IrId"));
        irLabel.setLabelFor(irId);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel3.add(irLabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 8, 0, 0);
        jPanel3.add(irId, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(6, 12, 12, 12);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        jPanel2.add(jPanel3, gridBagConstraints);

        tree.setPreferredSize(new java.awt.Dimension(400, 150));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 6, 12);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel2.add(tree, gridBagConstraints);

        connect.setText(bundle.getString("TXT_IRConnect"));
        connect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                connectIR(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(12, 0, 12, 12);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPanel2.add(connect, gridBagConstraints);

        refresh.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/corba/wizard/panels/Bundle").getString("TXT_IRRefresh"));
        refresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 12, 12);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPanel2.add(refresh, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(jPanel2, gridBagConstraints);

    }//GEN-END:initComponents

    private void refreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshActionPerformed
        // Add your handling code here:
        Node[] nodes = this.tree.getExplorerManager().getSelectedNodes();
        SystemAction action = SystemAction.get (org.netbeans.modules.corba.browser.ir.actions.RefreshAction.class);
        ActionEvent event = new ActionEvent (nodes,0,"");
        action.actionPerformed (event);
    }//GEN-LAST:event_refreshActionPerformed

    private void connectIR(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_connectIR
        // Add your handling code here:
        Node[] nodes = this.tree.getExplorerManager().getSelectedNodes();
        SystemAction action = SystemAction.get (org.netbeans.modules.corba.browser.ir.actions.AddRepository.class);
        ActionEvent event = new ActionEvent (nodes,0,"");
        action.actionPerformed (event);
    }//GEN-LAST:event_connectIR


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel irLabel;
    private org.openide.explorer.ExplorerPanel tree;
    private javax.swing.JButton connect;
    private javax.swing.JButton refresh;
    private javax.swing.JTextField irId;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel2;
    // End of variables declaration//GEN-END:variables

    private static final java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/netbeans/modules/corba/wizard/panels/Bundle");    

    public void propertyChange(final java.beans.PropertyChangeEvent event) {
        Object selection =  event.getNewValue();
        if ( selection != null && selection instanceof Node[]){
            Node[] nodes = (Node[]) selection;
            this.tree.setActivatedNodes (nodes);
            String irList = new String();
            for (int i=0; i < nodes.length; i++){
                if (nodes[i] instanceof Generatable){
                    GenerateSupport support = (GenerateSupport) nodes[i].getCookie (GenerateSupport.class);
                    if (support != null) {
                        if (i!=0)
                            irList = irList + ", ";
                        irList = irList + support.getRepositoryId();
                    }
                }
                else{
                    irList = java.util.ResourceBundle.getBundle("org/netbeans/modules/corba/wizard/panels/Bundle").getString("TXT_NoGenerateable");
                    break;
                }
            }
            this.irId.setText(irList);
            this.connect.setEnabled (nodes.length == 1 && nodes[0] == tree.getExplorerManager().getRootContext());
            this.refresh.setEnabled (nodes.length == 1 && nodes[0].getCookie (IRContainerNode.class) != null);
            this.fireChange (this);
        }
    }
}
