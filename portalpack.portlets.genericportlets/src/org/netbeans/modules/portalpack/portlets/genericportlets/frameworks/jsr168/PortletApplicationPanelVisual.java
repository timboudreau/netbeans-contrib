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

package org.netbeans.modules.portalpack.portlets.genericportlets.frameworks.jsr168;

import org.netbeans.modules.portalpack.portlets.genericportlets.core.codegen.CodeGenConstants;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.PortletContext;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.util.CoreUtil;
import org.netbeans.modules.web.api.webmodule.WebModule;
import org.netbeans.spi.project.ui.support.ProjectChooser;
import org.openide.WizardDescriptor;
import org.openide.WizardValidationException;
import org.openide.filesystems.FileObject;

/**
 * 
 * @author Satyaranjan
 */
public class PortletApplicationPanelVisual extends JPanel implements DocumentListener {
   
    public static final String PROP_PROJECT_NAME = "projectName";
    
    private PortletApplicationWizardPanel panel;
    private WebModule wm;
    private int type;
    
    /** Creates new form PanelProjectLocationVisual */
    public PortletApplicationPanelVisual(PortletApplicationWizardPanel panel,WebModule wm) {
        initComponents();
        this.panel = panel;
        this.type = type;
        this.wm = wm;
        initData();
        
        portletClassNameTf.getDocument().addDocumentListener(this);
        portletNameTf.getDocument().addDocumentListener(this);
        portletTitleTf.getDocument().addDocumentListener(this);
        portletDescTf.getDocument().addDocumentListener(this);
        portletDisplayNameTf.getDocument().addDocumentListener(this);
        portletShortTitleTf.getDocument().addDocumentListener(this);
        pkgTf.getDocument().addDocumentListener(this);
    }
     
    private void initData()
    {
        /*portletClassNameTf.setEnabled(false);
        portletNameTf.setEnabled(false);
        portletTitleTf.setEnabled(false);
        portletDescTf.setEnabled(false);
        portletDisplayNameTf.setEnabled(false);
        portletShortTitleTf.setEnabled(false);
        pkgTf.setEnabled(false);*/
        isCreatePortlet.setEnabled(true);
        isCreatePortlet.setSelected(false);
        enableCheckBoxes(false);
        //make editable false
        /*portletClassNameTf.setEditable(false);
        portletNameTf.setEditable(false);
        portletTitleTf.setEditable(false);
        portletDescTf.setEditable(false);
        portletDisplayNameTf.setEditable(false);
        portletShortTitleTf.setEditable(false);
        pkgTf.setEditable(false);*/
        enableTextComponents(false);
        
        if(wm != null)
        {         
            Project project = FileOwnerQuery.getOwner(wm.getDocumentBase());
            Sources sources = (Sources)project.getLookup().lookup(Sources.class);
            SourceGroup[] groups = sources.getSourceGroups(JavaProjectConstants.SOURCES_TYPE_JAVA);
             //FileObject[] sources = wm.getJavaSources();
            for(int i=0;i<groups.length;i++)
                 srcCombo.addItem(new CustomSourceGroup(groups[i]));
        }else{
             srcCombo.setEnabled(false);
             srcCombo.setEditable(false);
        }
      //  Project project = FileOwnerQuery.getOwner(wm.getDocumentBase());
      //  Sources sources = (Sources)project.getLookup().lookup(Sources.class);
     //   SourceGroup[] groups = sources.getSourceGroups(JavaProjectConstants.SOURCES_TYPE_JAVA);
            
    }
    
    class CustomSourceGroup
    {  
        private SourceGroup srcGroup;
        public CustomSourceGroup(SourceGroup srcGroup) {
            this.srcGroup = srcGroup;
        }
        
        public String toString()
        {
            return srcGroup.getDisplayName();
        }
        
        public FileObject getRootFolder()
        {
            return srcGroup.getRootFolder();
                 
        }
    }
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        srcRootLbl = new javax.swing.JLabel();
        srcCombo = new javax.swing.JComboBox();
        portletClassNameTf = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        portletDisplayNameTf = new javax.swing.JTextField();
        portletDescTf = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        portletTitleTf = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        portletShortTitleTf = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        pkgTf = new javax.swing.JTextField();
        portletNameTf = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        isCreatePortlet = new javax.swing.JCheckBox();
        jLabel8 = new javax.swing.JLabel();
        viewCheckbox = new javax.swing.JCheckBox();
        editCheckbox = new javax.swing.JCheckBox();
        helpCheckbox = new javax.swing.JCheckBox();
        isCreateJsps = new javax.swing.JCheckBox();

        setAutoscrolls(true);

        org.openide.awt.Mnemonics.setLocalizedText(srcRootLbl, org.openide.util.NbBundle.getMessage(PortletApplicationPanelVisual.class, "LBL_SRC_ROOT")); // NOI18N

        srcCombo.setEnabled(false);

        portletClassNameTf.setText("HelloWorld");
        portletClassNameTf.setEnabled(false);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(PortletApplicationPanelVisual.class, "LBL_PORTLET_CLASS_NAME")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(PortletApplicationPanelVisual.class, "LBL_PORTLET_DISPLAY_NAME")); // NOI18N

        portletDisplayNameTf.setText("HelloWorldPortlet");
        portletDisplayNameTf.setEnabled(false);

        portletDescTf.setText("HelloWorldPortlet");
        portletDescTf.setEnabled(false);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getMessage(PortletApplicationPanelVisual.class, "LBL_PORTLET_DESCRIPTION")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel4, org.openide.util.NbBundle.getMessage(PortletApplicationPanelVisual.class, "LBL_PORTLET_TITLE")); // NOI18N

        portletTitleTf.setText("HelloWorldPortlet");
        portletTitleTf.setEnabled(false);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel5, org.openide.util.NbBundle.getMessage(PortletApplicationPanelVisual.class, "LBL_PORTLET_SHORT_TITLE")); // NOI18N

        portletShortTitleTf.setText("HelloWorld");
        portletShortTitleTf.setEnabled(false);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel6, org.openide.util.NbBundle.getMessage(PortletApplicationPanelVisual.class, "LBL_PACKAGE")); // NOI18N

        pkgTf.setText("com.test");
        pkgTf.setEnabled(false);

        portletNameTf.setText("HelloWorld");
        portletNameTf.setEnabled(false);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel7, org.openide.util.NbBundle.getMessage(PortletApplicationPanelVisual.class, "LBL_PORTLET_NAME")); // NOI18N

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/netbeans/modules/portalpack/portlets/genericportlets/apptype/jsr168/Bundle"); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(isCreatePortlet, bundle.getString("LBL_CREATE_PORTLET")); // NOI18N
        isCreatePortlet.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        isCreatePortlet.setMargin(new java.awt.Insets(0, 0, 0, 0));
        isCreatePortlet.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                isCreatePortletStateChanged(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jLabel8, org.openide.util.NbBundle.getMessage(PortletApplicationPanelVisual.class, "LBL_PORTLET_MODE")); // NOI18N

        viewCheckbox.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(viewCheckbox, org.openide.util.NbBundle.getMessage(PortletApplicationPanelVisual.class, "LBL_VIEW")); // NOI18N
        viewCheckbox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        viewCheckbox.setEnabled(false);
        viewCheckbox.setMargin(new java.awt.Insets(0, 0, 0, 0));

        editCheckbox.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(editCheckbox, org.openide.util.NbBundle.getMessage(PortletApplicationPanelVisual.class, "LBL_EDIT")); // NOI18N
        editCheckbox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        editCheckbox.setMargin(new java.awt.Insets(0, 0, 0, 0));

        helpCheckbox.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(helpCheckbox, org.openide.util.NbBundle.getMessage(PortletApplicationPanelVisual.class, "LBL_HELP")); // NOI18N
        helpCheckbox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        helpCheckbox.setMargin(new java.awt.Insets(0, 0, 0, 0));

        isCreateJsps.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(isCreateJsps, "Create Jsps");
        isCreateJsps.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        isCreateJsps.setMargin(new java.awt.Insets(0, 0, 0, 0));

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel8)
                    .add(jLabel5)
                    .add(jLabel4)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(layout.createSequentialGroup()
                                .add(jLabel3)
                                .add(15, 15, 15)
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(layout.createSequentialGroup()
                                        .add(viewCheckbox)
                                        .add(71, 71, 71)
                                        .add(editCheckbox)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 75, Short.MAX_VALUE)
                                        .add(helpCheckbox))
                                    .add(portletTitleTf, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 259, Short.MAX_VALUE)
                                    .add(portletDescTf, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 259, Short.MAX_VALUE)
                                    .add(portletShortTitleTf, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 259, Short.MAX_VALUE)))
                            .add(layout.createSequentialGroup()
                                .add(jLabel2)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(portletDisplayNameTf, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 259, Short.MAX_VALUE))
                            .add(layout.createSequentialGroup()
                                .add(jLabel7)
                                .add(41, 41, 41)
                                .add(portletNameTf, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 259, Short.MAX_VALUE))
                            .add(layout.createSequentialGroup()
                                .add(jLabel1)
                                .add(13, 13, 13)
                                .add(portletClassNameTf, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 259, Short.MAX_VALUE))
                            .add(layout.createSequentialGroup()
                                .add(jLabel6)
                                .add(63, 63, 63)
                                .add(pkgTf, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 259, Short.MAX_VALUE))
                            .add(layout.createSequentialGroup()
                                .add(srcRootLbl)
                                .add(44, 44, 44)
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(layout.createSequentialGroup()
                                        .add(isCreatePortlet, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 86, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                        .add(45, 45, 45)
                                        .add(isCreateJsps)
                                        .add(53, 53, 53))
                                    .add(srcCombo, 0, 259, Short.MAX_VALUE))))
                        .add(77, 77, 77)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(isCreatePortlet)
                    .add(isCreateJsps))
                .add(11, 11, 11)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(srcRootLbl)
                    .add(srcCombo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(14, 14, 14)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel6)
                    .add(pkgTf, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(14, 14, 14)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(portletClassNameTf, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 20, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(18, 18, 18)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jLabel7)
                    .add(portletNameTf, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 21, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(20, 20, 20)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel2)
                    .add(portletDisplayNameTf, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(20, 20, 20)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel3)
                    .add(portletDescTf, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(18, 18, 18)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel4)
                    .add(portletTitleTf, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(13, 13, 13)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jLabel5)
                    .add(portletShortTitleTf, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(17, 17, 17)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel8)
                    .add(viewCheckbox)
                    .add(helpCheckbox)
                    .add(editCheckbox)))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void isCreatePortletStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_isCreatePortletStateChanged
// TODO add your handling code here:
        boolean selected = isCreatePortlet.isSelected();
        
        if(!selected){
        /*    portletClassNameTf.setEnabled(false);
            portletNameTf.setEnabled(false);
            portletDescTf.setEnabled(false);
            portletDisplayNameTf.setEnabled(false);
            portletTitleTf.setEnabled(false);
            portletShortTitleTf.setEnabled(false);
            pkgTf.setEnabled(false);*/
            enableTextComponents(selected);
            enableCheckBoxes(false);
            srcCombo.setEnabled(false);
        }else{
            /*portletClassNameTf.setEnabled(true);
            portletNameTf.setEnabled(true);
            portletDescTf.setEnabled(true);
            portletDisplayNameTf.setEnabled(true);
            portletTitleTf.setEnabled(true);
            portletShortTitleTf.setEnabled(true);
            pkgTf.setEnabled(true);*/
            enableTextComponents(selected);
            enableCheckBoxes(true);
            if(wm != null)
                srcCombo.setEnabled(true);
        }
        
        panel.fireChangeEvent();
    }//GEN-LAST:event_isCreatePortletStateChanged
        
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JCheckBox editCheckbox;
    private javax.swing.JCheckBox helpCheckbox;
    private javax.swing.JCheckBox isCreateJsps;
    private javax.swing.JCheckBox isCreatePortlet;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JTextField pkgTf;
    private javax.swing.JTextField portletClassNameTf;
    private javax.swing.JTextField portletDescTf;
    private javax.swing.JTextField portletDisplayNameTf;
    private javax.swing.JTextField portletNameTf;
    private javax.swing.JTextField portletShortTitleTf;
    private javax.swing.JTextField portletTitleTf;
    private javax.swing.JComboBox srcCombo;
    private javax.swing.JLabel srcRootLbl;
    private javax.swing.JCheckBox viewCheckbox;
    // End of variables declaration//GEN-END:variables
    
    public void addNotify() {
        super.addNotify();
        //same problem as in 31086, initial focus on Cancel button
        
    }
    
    boolean valid(WizardDescriptor wizardDescriptor) {
        
        boolean selected = isCreatePortlet.isSelected();
        if(!selected){
            wizardDescriptor.putProperty("WizardPanel_errorMessage", "");
            return true;
        }
        
        if(pkgTf.getText() == null || pkgTf.getText().trim().length() == 0)
        {
            
        } else{
            if(!CoreUtil.validatePackageName(pkgTf.getText()))
            {
                wizardDescriptor.putProperty("WizardPanel_errorMessage",
                    "Invalid package name..");
                 return false;
            }
        }
        if(portletNameTf.getText() == null || portletNameTf.getText().trim().length() == 0)
        {
              wizardDescriptor.putProperty("WizardPanel_errorMessage",
                    "Portlet Name cannot be empty...");
              return false;
        }
        if(portletClassNameTf.getText() == null || portletClassNameTf.getText().trim().length() == 0)
        {
             wizardDescriptor.putProperty("WizardPanel_errorMessage",
                    "Portlet class cannot be empty...");
              return false;
        }else if(!CoreUtil.validateJavaTypeName(portletClassNameTf.getText()))
        {
            wizardDescriptor.putProperty("WizardPanel_errorMessage",
                    "Invalid Class Name");
              return false;
        }else if(!CoreUtil.validateString(portletNameTf.getText(),false))
        {
            wizardDescriptor.putProperty("WizardPanel_errorMessage",
                    org.openide.util.NbBundle.getMessage(PortletApplicationPanelVisual.class, "MSG_INVALID_PORTLET_NAME"));
            return false;
        }else if(!CoreUtil.validateString(portletTitleTf.getText(),true))
        {
            wizardDescriptor.putProperty("WizardPanel_errorMessage",
                    org.openide.util.NbBundle.getMessage(PortletApplicationPanelVisual.class, "MSG_INVALID_PORTLET_TITLE"));
            return false;
        }else if(portletShortTitleTf.getText() != null &&
                    portletShortTitleTf.getText().trim().length() != 0 &&
                    !CoreUtil.validateString(portletShortTitleTf.getText(),true))
        {
            wizardDescriptor.putProperty("WizardPanel_errorMessage",
                    org.openide.util.NbBundle.getMessage(PortletApplicationPanelVisual.class, "MSG_INVALID_PORTLET_SHORT_TITLE"));
            return false;
        }else if(!CoreUtil.validateXmlString(portletDisplayNameTf.getText().trim()))
        {
            wizardDescriptor.putProperty("WizardPanel_errorMessage",
                    org.openide.util.NbBundle.getMessage(PortletApplicationPanelVisual.class, "MSG_INVALID_PORTLET_DISPLAY_NAME"));
            return false;
        }else if(!CoreUtil.validateXmlString(portletDescTf.getText().trim()))
        {
            wizardDescriptor.putProperty("WizardPanel_errorMessage",
                    org.openide.util.NbBundle.getMessage(PortletApplicationPanelVisual.class, "MSG_INVALID_PORTLET_DESC"));
            return false;
        }
        wizardDescriptor.putProperty("WizardPanel_errorMessage", "");
        return true;
    }
    
    void store(WizardDescriptor d) {
      
        d.putProperty("package",pkgTf.getText());
        if(isCreatePortlet.isSelected())
            d.putProperty("generate_portlet","true");
        else
            d.putProperty("generate_portlet","false");
        d.putProperty(CodeGenConstants.PORTLET_NAME,portletNameTf.getText().trim());
        d.putProperty(CodeGenConstants.PORTLET_CLASS,portletClassNameTf.getText().trim());
        d.putProperty(CodeGenConstants.PORTLET_DISPLAY_NAME,portletDisplayNameTf.getText().trim());
        d.putProperty(CodeGenConstants.PORTLET_DESCRIPTION,portletDescTf.getText().trim());
        d.putProperty(CodeGenConstants.PORTLET_TITLE,portletTitleTf.getText().trim());
        d.putProperty(CodeGenConstants.PORTLET_SHORT_TITLE,portletShortTitleTf.getText().trim());
    }
    
    /**
     * 
     * @return 
     */
    public Map getData()
    {
        Map d = new HashMap();
        d.put("package",pkgTf.getText());
        if(isCreatePortlet.isSelected())
            d.put("generate_portlet","true");
        else
            d.put("generate_portlet","false");
        
        CustomSourceGroup custSourceGroup = (CustomSourceGroup)srcCombo.getSelectedItem();
        if(custSourceGroup != null)
            d.put("src_folder",custSourceGroup.getRootFolder());
  
        PortletContext context = new PortletContext();
        List modeList = new ArrayList();
            
            if(viewCheckbox.isSelected())
                modeList.add("VIEW");
            if(editCheckbox.isSelected())
                modeList.add("EDIT");
            if(helpCheckbox.isSelected())
                modeList.add("HELP");
            
        context.setModes((String [])modeList.toArray(new String[0]));
        
        context.setPortletClass(portletClassNameTf.getText().trim());
        context.setPortletName(portletNameTf.getText().trim());
        context.setPortletDescription(portletDescTf.getText().trim());
        context.setPortletDisplayName(portletDisplayNameTf.getText().trim());
        context.setPortletTitle(portletTitleTf.getText().trim());
        context.setPortletShortTitle(portletShortTitleTf.getText().trim());
        context.setHasJsps(isCreateJsps.isSelected());
        d.put("context", context);
        return d;
        
       
    }
    
    void read(WizardDescriptor settings) {
        File projectLocation = (File) settings.getProperty("projdir");
        if (projectLocation == null || projectLocation.getParentFile() == null || !projectLocation.getParentFile().isDirectory()) {
            projectLocation = ProjectChooser.getProjectsFolder();
        } else {
            projectLocation = projectLocation.getParentFile();
        }
        
        String projectName = (String) settings.getProperty("name");
        if(projectName == null) {
            projectName = "";
        }
    }
    
    void validate(WizardDescriptor d) throws WizardValidationException {
        // nothing to validate
    }
    
    // Implementation of DocumentListener --------------------------------------
    
    public void changedUpdate(DocumentEvent e) {
        updateTexts(e);
    }
    
    public void insertUpdate(DocumentEvent e) {
        updateTexts(e);
    }
    
    public void removeUpdate(DocumentEvent e) {
        updateTexts(e);
        
    }
    
    /** Handles changes in the Project name and project directory, */
    private void updateTexts(DocumentEvent e) {
        
        Document doc = e.getDocument();
        
         if (doc == portletClassNameTf.getDocument()) {
            // Change in the project name
            
            String portletClassName = portletClassNameTf.getText();
            portletNameTf.setText(portletClassName);
            portletDescTf.setText(portletClassName);
            portletDisplayNameTf.setText(portletClassName);
            portletTitleTf.setText(portletClassName);
            portletShortTitleTf.setText(portletClassName);   
            
        }
        panel.fireChangeEvent(); // Notify that the panel changed
    }
    
    /**
     * 
     * @param enable 
     */
    public void enableComponents(boolean enable)
    {
        if(!enable)
        {
            enableTextComponents(false);
            enableCheckBoxes(false);
        }
        else if(enable && isCreatePortlet.isSelected())
        {
            enableTextComponents(true);
            enableCheckBoxes(true);
        }
        else if(enable && !isCreatePortlet.isSelected())
        {
            enableTextComponents(false);
            enableCheckBoxes(false);
        }
        
        isCreatePortlet.setEnabled(enable);
       /// isCreateJsps.setEnabled(enable);
        if(wm != null)
            srcCombo.setEnabled(enable);
      
    }
    
    private void enableCheckBoxes(boolean enable)
    {
        isCreateJsps.setEnabled(enable);
        editCheckbox.setEnabled(enable);
        helpCheckbox.setEnabled(enable);
        
    }
    private void enableTextComponents(boolean enable)
    {  
        portletClassNameTf.setEditable(enable);
        portletNameTf.setEditable(enable);
        portletTitleTf.setEditable(enable);
        portletDescTf.setEditable(enable);
        portletDisplayNameTf.setEditable(enable);
        portletShortTitleTf.setEditable(enable);
        pkgTf.setEditable(enable);
        
        portletClassNameTf.setEnabled(enable);
        portletNameTf.setEnabled(enable);
        portletTitleTf.setEnabled(enable);
        portletDescTf.setEnabled(enable);
        portletDisplayNameTf.setEnabled(enable);
        portletShortTitleTf.setEnabled(enable);
        pkgTf.setEnabled(enable);
      
    }
   
}
