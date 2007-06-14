
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
import com.sun.tthub.gdelib.fields.ComplexEntryFieldDisplayInfo;
import com.sun.tthub.gdelib.fields.DataTypeNature;
import com.sun.tthub.gdelib.fields.FieldDataEntryNature;
import com.sun.tthub.gdelib.fields.FieldDisplayInfo;
import com.sun.tthub.gdelib.fields.FieldInfo;
import com.sun.tthub.gdelib.fields.FieldMetaData;
import com.sun.tthub.gdelib.fields.SelectionFieldDisplayInfo;
import com.sun.tthub.gdelib.logic.TTValueDisplayInfoPersistenceDelegate;
import com.sun.tthub.gdelib.logic.TTValueFieldInfo;
import com.sun.tthub.gdelib.logic.TTValueDisplayInfo;

import com.sun.tthub.gde.logic.ExtTTValueInfoProcessor;
import com.sun.tthub.gde.logic.ExtTTValueInfoProcessorImpl;
import com.sun.tthub.gde.logic.GDEAppContext;
import com.sun.tthub.gde.logic.GDEPreferencesController;
import com.sun.tthub.gde.logic.PortletDeployParams;
import com.sun.tthub.gde.ui.GDEWizardModel;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;

public final class ConfirmationJPanel extends WizardContentJPanel {
    private WizardController controller;
    /**
     * Creates new form ConfirmationJPanel
     */
    public ConfirmationJPanel(WizardController controller) {
        this.controller = controller;
        initComponents();
    }
   
    
    private void fillFieldSummaryTree() {
        
        DefaultTreeModel model = (DefaultTreeModel) treeFieldsSummary.getModel();
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) model.getRoot();
        // Clean up the tree first.        
        node.removeAllChildren();        
        model.reload(node);        
        node.setUserObject("Extended TTValue Fields Info");
        TTValueDisplayInfo displayInfo =  
                            controller.getTTValueDisplayInfo();
        Map fieldInfoMap = displayInfo.getExtFieldInfoMap();
        Collection coll = fieldInfoMap.entrySet();
        for(Iterator it = coll.iterator(); it.hasNext(); ) {
            Map.Entry entry = (Map.Entry) it.next();
            String fieldName = (String) entry.getKey();
            FieldInfo fieldInfo = (FieldInfo) entry.getValue();
            FieldInfoJTreeUtil.paintFieldInfoNode(model, node, fieldInfo);
        }
        
        treeFieldsSummary.expandRow(0);
    }
    

    public void loadWizardContentPanel() {
        TTValueDisplayInfo displayInfo =  
                            controller.getTTValueDisplayInfo();
        // Fill the class and interface name labels.
        lblExtTTValueInterfaceVal.setText(displayInfo.getExtTTValueInterface());
        lblExtTTValueImplClassVal.setText(displayInfo.getExtTTValueImplClass());
        
        // Fill the Portlet deployment parameters.
        /* Not in use in Version 1
        PortletDeployParams params = controller.getPortletDeployParams();
        lblPortalServHomeVal.setText(params == null ? 
            "Deploy Params Not specified" : params.getPortalServerHome());
        lblDirServUserDNVal.setText(params ==  null ?
            "Deploy Params Not specified" : params.getDirServerUserDN());
        */
        // fill the Tree with the field summary.
        fillFieldSummaryTree();
    }
    

    public boolean validationFailed(GDEWizardPageValidationException ex) {
        return false;
    }    
    
    /**
     * Method from the WizardContents JPanel. Does nothing in this panel.
     */
    public void preProcessWizardContents(int wizardAction) throws GDEException {}
    
    public void processWizardContents(int wizardAction) throws GDEException {
        if(wizardAction == WizardActions.ACTION_FINAL) {
            ExtTTValueInfoProcessor processor = 
                    new ExtTTValueInfoProcessorImpl();
            
            processor.generateWarFile(controller.getTTValueDisplayInfo());
            
            GDEPreferencesController controller = 
                        GDEAppContext.getInstance().getGdePrefsController();
            String gdeFolder = controller.retrievePreferences().getGdeFolder();
            String warFileName = gdeFolder + "/build/dist/tthubportlets.war";
            JOptionPane.showMessageDialog(null, "Generated the war file. " +
                    "The war file '" +  warFileName + "'. Please Check", 
                    "GDE Processing Complete", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    
    public void validateContents() 
                    throws GDEWizardPageValidationException {
    }
    
    public String getName() { return "ConfirmationJPanel"; }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        lblTitle = new javax.swing.JLabel();
        lblInfo = new javax.swing.JLabel();
        pnlExtTTValueDet = new javax.swing.JPanel();
        lblExtTTValueInterfaceLbl = new javax.swing.JLabel();
        lblExtTTValueImplClassLbl = new javax.swing.JLabel();
        lblExtTTValueInterfaceVal = new javax.swing.JTextField();
        lblExtTTValueImplClassVal = new javax.swing.JTextField();
        pnlFieldsSummary = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        treeFieldsSummary = new javax.swing.JTree();

        lblTitle.setFont(new java.awt.Font("Dialog", 1, 14));
        lblTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTitle.setText("Extended TTValue Interface Display Info Summary");

        lblInfo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblInfo.setText("Press the 'Finish' button of the wizard to start generating the war file.....");

        pnlExtTTValueDet.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        lblExtTTValueInterfaceLbl.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblExtTTValueInterfaceLbl.setText("Ext. TTValue Interface:");

        lblExtTTValueImplClassLbl.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblExtTTValueImplClassLbl.setText("Extended TTValue Impl Class:");

        lblExtTTValueInterfaceVal.setOpaque(false);

        lblExtTTValueImplClassVal.setOpaque(false);

        org.jdesktop.layout.GroupLayout pnlExtTTValueDetLayout = new org.jdesktop.layout.GroupLayout(pnlExtTTValueDet);
        pnlExtTTValueDet.setLayout(pnlExtTTValueDetLayout);
        pnlExtTTValueDetLayout.setHorizontalGroup(
            pnlExtTTValueDetLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlExtTTValueDetLayout.createSequentialGroup()
                .addContainerGap()
                .add(pnlExtTTValueDetLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(lblExtTTValueImplClassLbl)
                    .add(lblExtTTValueInterfaceLbl, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 142, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlExtTTValueDetLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(lblExtTTValueInterfaceVal, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 330, Short.MAX_VALUE)
                    .add(lblExtTTValueImplClassVal, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 330, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnlExtTTValueDetLayout.setVerticalGroup(
            pnlExtTTValueDetLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlExtTTValueDetLayout.createSequentialGroup()
                .addContainerGap()
                .add(pnlExtTTValueDetLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblExtTTValueInterfaceLbl)
                    .add(lblExtTTValueInterfaceVal, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlExtTTValueDetLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblExtTTValueImplClassLbl)
                    .add(lblExtTTValueImplClassVal, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlFieldsSummary.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jScrollPane1.setViewportView(treeFieldsSummary);

        org.jdesktop.layout.GroupLayout pnlFieldsSummaryLayout = new org.jdesktop.layout.GroupLayout(pnlFieldsSummary);
        pnlFieldsSummary.setLayout(pnlFieldsSummaryLayout);
        pnlFieldsSummaryLayout.setHorizontalGroup(
            pnlFieldsSummaryLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlFieldsSummaryLayout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 476, Short.MAX_VALUE)
                .addContainerGap())
        );
        pnlFieldsSummaryLayout.setVerticalGroup(
            pnlFieldsSummaryLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlFieldsSummaryLayout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane1)
                .addContainerGap())
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(pnlFieldsSummary, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(pnlExtTTValueDet, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(lblTitle, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 500, Short.MAX_VALUE)
                    .add(lblInfo, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 500, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(lblTitle, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 32, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlExtTTValueDet, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlFieldsSummary, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(84, 84, 84)
                .add(lblInfo)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JScrollPane jScrollPane1;
    public javax.swing.JLabel lblExtTTValueImplClassLbl;
    public javax.swing.JTextField lblExtTTValueImplClassVal;
    public javax.swing.JLabel lblExtTTValueInterfaceLbl;
    public javax.swing.JTextField lblExtTTValueInterfaceVal;
    public javax.swing.JLabel lblInfo;
    public javax.swing.JLabel lblTitle;
    public javax.swing.JPanel pnlExtTTValueDet;
    public javax.swing.JPanel pnlFieldsSummary;
    public javax.swing.JTree treeFieldsSummary;
    // End of variables declaration//GEN-END:variables

}

