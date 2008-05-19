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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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

package org.netbeans.modules.portalpack.visualweb.ui;

// XXX org.netbeans.modules.visualweb.project.jsf is not accessible under NetBeans 6.0; needs friend-package
// Use local copy now
// import org.netbeans.modules.visualweb.project.jsf.api.JsfProjectUtils;
// import org.netbeans.modules.visualweb.project.jsf.api.JsfProjectConstants;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import org.netbeans.api.project.Project;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.PortletContext;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.codegen.WebDescriptorGenerator;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.util.CoreUtil;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.util.NetbeansUtil;
import org.openide.WizardDescriptor;
import org.openide.util.NbBundle;

/**
 *
 * @author  Po-Ting Wu
 */
public class PagebeanPackagePanelGUI extends javax.swing.JPanel implements DocumentListener {

    private Project project;
    private final List/*<ChangeListener>*/ listeners = new ArrayList();
    private List availablePortlets = null;
    private boolean isCreateNewPortlet = true;
    /** Creates new form SimpleTargetChooserGUI */
    public PagebeanPackagePanelGUI(Project project) {
        this.project = project;
        
        initComponents();
        initValues(project);
        pnameTf.getDocument().addDocumentListener(this);
        portletTitleTf.getDocument().addDocumentListener(this);
        portletShortTitleTf.getDocument().addDocumentListener(this);
        portletDisplayNameTf.getDocument().addDocumentListener(this);
        portletDescTf.getDocument().addDocumentListener(this);

        packageTextField.getDocument().addDocumentListener(this);
        availablePortlets = getAvailablePortlets();
    }

    public void initValues(Project project) {
        String packageName = JsfProjectUtils.getProjectProperty(project, JsfProjectConstants.PROP_JSF_PAGEBEAN_PACKAGE);
        if (packageName == null || packageName.length() == 0) {
            packageName = JsfProjectUtils.deriveSafeName(project.getProjectDirectory().getName());
        }
        packageTextField.setText(packageName);

        packageTextField.setEditable(!JsfProjectUtils.isJsfProject(project));
    }

    public String getPackageName() {
        String text = packageTextField.getText().trim();
        
        if (text.length() == 0) {
            return null;
        } else {
            return text;
        }
    }

    public synchronized void addChangeListener(ChangeListener l) {
        listeners.add(l);
    }
    
    public synchronized void removeChangeListener(ChangeListener l) {
        listeners.remove(l);
    }

    public void disableNewPortletCreateOption() {
        pnameTf.setEnabled(false);
        portletTitleTf.setEnabled(false);
        portletShortTitleTf.setEnabled(false);
        portletDisplayNameTf.setEnabled(false);
        portletDescTf.setEnabled(false);
        isCreateNewPortlet = false;
    }
    
    private void fireChange() {
        ChangeEvent e = new ChangeEvent(this);
        List templist;
        synchronized (this) {
            templist = new ArrayList (listeners);
        }
        Iterator it = templist.iterator();
        while (it.hasNext()) {
            ((ChangeListener)it.next()).stateChanged(e);
        }
    }
    
     boolean valid(WizardDescriptor wizardDescriptor)
    {
        if(wizardDescriptor == null) 
            return true;
        
        if(!isCreateNewPortlet) return true;
        
        String packageName = getPackageName();
        if (!JsfProjectUtils.isValidJavaPackageName(packageName)) {
            wizardDescriptor.putProperty("WizardPanel_errorMessage", NbBundle.getMessage(PagebeanPackagePanelGUI.class, "MSG_InvalidPackageName", packageName)); // NOI18N
            return false;
        }
        String portletName = pnameTf.getText();
        if(!CoreUtil.validateString(portletName,false))
        {
            wizardDescriptor.putProperty("WizardPanel_errorMessage",
                    org.openide.util.NbBundle.getMessage(PagebeanPackagePanelGUI.class, "MSG_INVALID_PORTLET_NAME"));
            return false; 
        }else if(availablePortlets != null && availablePortlets.contains(portletName)){
             wizardDescriptor.putProperty("WizardPanel_errorMessage",
                    org.openide.util.NbBundle.getMessage(PagebeanPackagePanelGUI.class, "MSG_PORTLET_ALREADY_PRESENT"));
            return false;
        }else if(!CoreUtil.validateString(portletTitleTf.getText(),true)){
            wizardDescriptor.putProperty("WizardPanel_errorMessage",
                    org.openide.util.NbBundle.getMessage(PagebeanPackagePanelGUI.class, "MSG_INVALID_PORTLET_TITLE"));
            return false; 
        }else if(portletShortTitleTf.getText() != null &&
                    portletShortTitleTf.getText().trim().length() != 0 &&
                    !CoreUtil.validateString(portletShortTitleTf.getText(),true)){
            wizardDescriptor.putProperty("WizardPanel_errorMessage",
                    org.openide.util.NbBundle.getMessage(PagebeanPackagePanelGUI.class, "MSG_INVALID_PORTLET_SHORT_TITLE"));
            return false; 
        }else if(!CoreUtil.validateXmlString(portletDisplayNameTf.getText().trim()))
        {
            wizardDescriptor.putProperty("WizardPanel_errorMessage",
                    org.openide.util.NbBundle.getMessage(PagebeanPackagePanelGUI.class, "MSG_INVALID_PORTLET_DISPLAY_NAME"));
            return false; 
        }else if(!CoreUtil.validateXmlString(portletDescTf.getText().trim()))
        {
            wizardDescriptor.putProperty("WizardPanel_errorMessage",
                    org.openide.util.NbBundle.getMessage(PagebeanPackagePanelGUI.class, "MSG_INVALID_PORTLET_DESC"));
            return false; 
        }
        
        
        wizardDescriptor.putProperty("WizardPanel_errorMessage", "");
        
        return true;
    }
    public void store(WizardDescriptor d) {
        
        PortletContext context = (PortletContext)d.getProperty("context");
        
        if(context == null)
            context = new PortletContext();
        
        context.setPortletName(pnameTf.getText().trim());
        context.setPortletDescription(portletDescTf.getText().trim());
        context.setPortletDisplayName(portletDisplayNameTf.getText().trim());
        context.setPortletTitle(portletTitleTf.getText().trim());
        context.setPortletShortTitle(portletShortTitleTf.getText().trim());
        
        List modeList = new ArrayList();
            
        modeList.add("VIEW");
            
        context.setModes((String [])modeList.toArray(new String[0]));
            
        d.putProperty("context",context);
        d.putProperty("PACKAGE_NAME", getPackageName());
        
    }
    
    public List getAvailablePortlets()
    {
        String webInfDir = NetbeansUtil.getWebInfDir(project);
        File portletXml = new File(webInfDir+File.separator+"portlet.xml");
        if(!portletXml.exists()) {
           
        }else{
          
           return WebDescriptorGenerator.getPortlets(portletXml);
        }
        return Collections.EMPTY_LIST;
    }   
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        packageLabel = new javax.swing.JLabel();
        packageTextField = new javax.swing.JTextField();
        portletNameLabel = new javax.swing.JLabel();
        pnameTf = new javax.swing.JTextField();
        portletDisplayNameLabel = new javax.swing.JLabel();
        portletDisplayNameTf = new javax.swing.JTextField();
        portletDescLabel = new javax.swing.JLabel();
        portletDescTf = new javax.swing.JTextField();
        portletTitleLabel = new javax.swing.JLabel();
        portletTitleTf = new javax.swing.JTextField();
        portletShortTitleLabel = new javax.swing.JLabel();
        portletShortTitleTf = new javax.swing.JTextField();

        packageLabel.setDisplayedMnemonic(org.openide.util.NbBundle.getMessage(PagebeanPackagePanelGUI.class, "MNE_PagebeanPackage_Label").charAt(0));
        packageLabel.setLabelFor(packageTextField);
        packageLabel.setText(org.openide.util.NbBundle.getMessage(PagebeanPackagePanelGUI.class, "LBL_PagebeanPackage_Label")); // NOI18N

        portletNameLabel.setLabelFor(pnameTf);
        portletNameLabel.setText(org.openide.util.NbBundle.getMessage(PagebeanPackagePanelGUI.class, "LBL_PORTLET_NAME")); // NOI18N

        portletDisplayNameLabel.setLabelFor(portletDisplayNameTf);
        portletDisplayNameLabel.setText(org.openide.util.NbBundle.getMessage(PagebeanPackagePanelGUI.class, "LBL_PORTLET_DISPLAY_NAME")); // NOI18N

        portletDescLabel.setLabelFor(portletDescTf);
        portletDescLabel.setText(org.openide.util.NbBundle.getMessage(PagebeanPackagePanelGUI.class, "LBL_PORTLET_DESC")); // NOI18N

        portletTitleLabel.setLabelFor(portletTitleTf);
        portletTitleLabel.setText(org.openide.util.NbBundle.getMessage(PagebeanPackagePanelGUI.class, "LBL_PORTLET_TITLE")); // NOI18N

        portletShortTitleLabel.setLabelFor(portletShortTitleTf);
        portletShortTitleLabel.setText(org.openide.util.NbBundle.getMessage(PagebeanPackagePanelGUI.class, "LBL_PORTLET_SHORT_TITLE")); // NOI18N

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(packageLabel)
                    .add(portletDescLabel)
                    .add(portletTitleLabel)
                    .add(portletShortTitleLabel)
                    .add(portletNameLabel)
                    .add(portletDisplayNameLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(packageTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 286, Short.MAX_VALUE)
                    .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, portletShortTitleTf)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, portletTitleTf)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, portletDescTf)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, portletDisplayNameTf)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, pnameTf, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 202, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(packageLabel)
                    .add(packageTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(29, 29, 29)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(portletNameLabel)
                    .add(pnameTf, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(portletDisplayNameLabel)
                    .add(portletDisplayNameTf, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(portletDescLabel)
                    .add(portletDescTf, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(portletTitleLabel)
                    .add(portletTitleTf, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(portletShortTitleLabel)
                    .add(portletShortTitleTf, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(20, Short.MAX_VALUE))
        );

        packageTextField.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getBundle(PagebeanPackagePanelGUI.class).getString("AD_packageTextField")); // NOI18N
        portletNameLabel.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(PagebeanPackagePanelGUI.class, "ACSN_Portlet_Name")); // NOI18N
        portletNameLabel.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(PagebeanPackagePanelGUI.class, "ACSD_Portlet_Name")); // NOI18N
        portletDisplayNameLabel.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(PagebeanPackagePanelGUI.class, "ACSN_Portlet_Display_Name")); // NOI18N
        portletDisplayNameLabel.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(PagebeanPackagePanelGUI.class, "ACSD_Portlet_Display_Name")); // NOI18N
        portletDescLabel.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(PagebeanPackagePanelGUI.class, "ACSN_Portlet_Desc")); // NOI18N
        portletDescLabel.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(PagebeanPackagePanelGUI.class, "ACSD_Portlet_Desc")); // NOI18N
        portletTitleLabel.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(PagebeanPackagePanelGUI.class, "ACSN_Portlet_Title")); // NOI18N
        portletTitleLabel.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(PagebeanPackagePanelGUI.class, "ASCD_Portlet_Title")); // NOI18N
        portletShortTitleLabel.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(PagebeanPackagePanelGUI.class, "ACSN_Portlet_Short_Title")); // NOI18N
        portletShortTitleLabel.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(PagebeanPackagePanelGUI.class, "ASCD_Portlet_Short_Title")); // NOI18N

        getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getBundle(PagebeanPackagePanelGUI.class).getString("AD_PagebeanPackagePanelGUI")); // NOI18N
    }// </editor-fold>//GEN-END:initComponents
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel packageLabel;
    private javax.swing.JTextField packageTextField;
    private javax.swing.JTextField pnameTf;
    private javax.swing.JLabel portletDescLabel;
    private javax.swing.JTextField portletDescTf;
    private javax.swing.JLabel portletDisplayNameLabel;
    private javax.swing.JTextField portletDisplayNameTf;
    private javax.swing.JLabel portletNameLabel;
    private javax.swing.JLabel portletShortTitleLabel;
    private javax.swing.JTextField portletShortTitleTf;
    private javax.swing.JLabel portletTitleLabel;
    private javax.swing.JTextField portletTitleTf;
    // End of variables declaration//GEN-END:variables

    // DocumentListener implementation -----------------------------------------
    
    public void changedUpdate(javax.swing.event.DocumentEvent e) {
        updateText(e);
    }    
    
    public void insertUpdate(javax.swing.event.DocumentEvent e) {
        updateText(e);
    }
    
    public void removeUpdate(javax.swing.event.DocumentEvent e) {
        updateText(e);
    }
    
    private void updateText(javax.swing.event.DocumentEvent e) {
        Document doc = e.getDocument();
        
        if (doc == pnameTf.getDocument()) {
            // Change in the project name
            
            String portletName = pnameTf.getText();
            portletDescTf.setText(portletName);
            portletDisplayNameTf.setText(portletName);
            portletTitleTf.setText(portletName);
            portletShortTitleTf.setText(portletName);   
        }
        fireChange();
    }
}
