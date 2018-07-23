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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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
/*
 * CommonGeneralFinishPanel.java
 */

package org.netbeans.modules.j2ee.sun.ws7.serverresources.wizards;

import java.awt.Component;
import java.util.HashSet;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.Set;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.openide.loaders.TemplateWizard;
import org.openide.WizardDescriptor;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

import org.netbeans.modules.j2ee.sun.ws7.serverresources.beans.WS70ResourceUtils;

import org.netbeans.modules.j2ee.sun.sunresources.beans.FieldGroup;
import org.netbeans.modules.j2ee.sun.sunresources.beans.Wizard;
import org.netbeans.modules.j2ee.sun.ws7.serverresources.wizards.WS70WizardConstants;
import org.netbeans.modules.j2ee.sun.sunresources.beans.FieldGroupHelper;

/** A single panel descriptor for a wizard.
 * You probably want to make a wizard iterator to hold it.
 *
 * 
 * Code reused from Appserver common API module 
 */
public class CommonGeneralFinishPanel implements WizardDescriptor.FinishablePanel, WS70WizardConstants {
    protected ResourceBundle bundle = NbBundle.getBundle("org.netbeans.modules.j2ee.sun.ws7.serverresources.wizards.Bundle"); //NOI18N

    
    /** The visual component that displays this panel.
     * If you need to access the component from this class,
     * just use getComponent().
     */
    private CommonGeneralFinishVisualPanel component;
    private ResourceConfigHelper helper;    
    private Wizard wizardInfo;
    private String[] groupNames;
    
    /** Create the wizard panel descriptor. */
    public CommonGeneralFinishPanel(ResourceConfigHelper helper, Wizard wizardInfo, String[] groupNames) {
        this.helper = helper;
        this.wizardInfo = wizardInfo;
        this.groupNames = groupNames;
    }
    
    // Get the visual component for the panel. In this template, the component
    // is kept separate. This can be more efficient: if the wizard is created
    // but never displayed, or not all panels are displayed, it is better to
    // create only those which really need to be visible.
    public Component getComponent() {
        if (component == null) {
                FieldGroup[] groups = new FieldGroup[groupNames.length];
                for (int i = 0; i < this.groupNames.length; i++) {
                    groups[i] = FieldGroupHelper.getFieldGroup(wizardInfo, this.groupNames[i]);  //NOI18N
                }
                String panelType = null;
                if (wizardInfo.getName().equals(__MailResource)) {
                    panelType = CommonGeneralFinishVisualPanel.TYPE_MAIL_RESOURCE;
                }else if(wizardInfo.getName().equals(__CustomResource)) {
                    panelType = CommonGeneralFinishVisualPanel.TYPE_CUSTOM_RESOURCE;
                }else if(wizardInfo.getName().equals(__ExternalJndiResource)) {
                    panelType = CommonGeneralFinishVisualPanel.TYPE_EXTERNAL_RESOURCE;
                }else if(wizardInfo.getName().equals(WS70WizardConstants.__JdbcResource)) {
                    panelType = CommonGeneralFinishVisualPanel.TYPE_JDBC_RESOURCE;
                }                  
                component = new CommonGeneralFinishVisualPanel(this, groups, panelType);
        }
        return component;
    }
    
    public boolean createNew() {
        if (component == null)
            return false;
        else
            return component.createNew();
    }
    
    public String getResourceName() {
        return this.wizardInfo.getName();
    }
    
    public HelpCtx getHelp() {
        if (wizardInfo.getName().equals(__MailResource)) {
            return new HelpCtx("AS_Wiz_Mail_general"); //NOI18N
        }else{
            return HelpCtx.DEFAULT_HELP;
        }
        
    }
    
    public ResourceConfigHelper getHelper() {
        return helper;
    }
    
    public boolean isValid() {
        // If it is always OK to press Next or Finish, then:
        if (component != null && component.jLabels != null && component.jFields != null) {
            int i;
            for (i=0; i < component.jLabels.length; i++) {
                String jLabel = (String)component.jLabels[i].getText();
                if (jLabel.equals(bundle.getString("LBL_" + __JndiName))) { //NOI18N
                    String jndiName = (String)((JTextField)component.jFields[i]).getText();
                    if (jndiName == null || jndiName.trim().length() == 0) {
                        return false;
                    }else if(! WS70ResourceUtils.isLegalResourceName(jndiName)){
                        return false;
                    }    
                }
                if (wizardInfo.getName().equals(__MailResource)) {
                    if (jLabel.equals(bundle.getString("LBL_" + __Host))) { // NO18N
                        String host = (String)((JTextField)component.jFields[i]).getText();
                        if (host == null || host.trim().length() == 0) {
                            return false;
                        }
                    }
                    if (jLabel.equals(bundle.getString("LBL_" + __MailUser))) { // NO18N
                        String user = (String)((JTextField)component.jFields[i]).getText();
                        if (user == null || user.trim().length() == 0) {
                            return false;
                        }
                    }
                    if (jLabel.equals(bundle.getString("LBL_" + __From))) { //NOI18N
                        String from = (String)((JTextField)component.jFields[i]).getText();
                        if (from == null || from.trim().length() == 0) {
                            return false;
                        }
                    }
                }else if(wizardInfo.getName().equals(this.__CustomResource)){
                    if (jLabel.equals(bundle.getString("LBL_" + this.__ResType))) { // NO18N
                        String restype = (String)((JTextField)component.jFields[i]).getText();
                        if (restype == null || restype.trim().length() == 0) {
                            return false;
                        }
                    }
                    if (jLabel.equals(bundle.getString("LBL_" + this.__FactoryClass))) { // NO18N
                        String factclass = (String)((JTextField)component.jFields[i]).getText();
                        if (factclass == null || factclass.trim().length() == 0) {
                            return false;
                        }
                    }          
                    
                }else if(wizardInfo.getName().equals(this.__ExternalJndiResource)){
                    if (jLabel.equals(bundle.getString("LBL_" + this.__ResType))) { // NO18N
                        String restype = (String)((JTextField)component.jFields[i]).getText();
                        if (restype == null || restype.trim().length() == 0) {
                            return false;
                        }
                    }
                    if (jLabel.equals(bundle.getString("LBL_" + this.__FactoryClass))) { // NO18N
                        String factclass = (String)((JTextField)component.jFields[i]).getText();
                        if (factclass == null || factclass.trim().length() == 0) {
                            return false;
                        }
                    }                    
                    if (jLabel.equals(bundle.getString("LBL_" + this.__ExternalJndiName))) { // NO18N
                        String extjndiname = (String)((JTextField)component.jFields[i]).getText();
                        if (extjndiname == null || extjndiname.trim().length() == 0) {
                            return false;
                        }
                    }                    
                    
                }else if(wizardInfo.getName().equals(this.__JdbcResource)){
                    if (jLabel.equals(bundle.getString("LBL_" + this.__DatasourceClassname))) { // NO18N
                        String dsclassname = (String)((JTextField)component.jFields[i]).getText();
                        if (dsclassname == null || dsclassname.trim().length() == 0) {
                            return false;
                        }
                    }              
                }
            }//for
        }
        return true;
        // If it depends on some condition (form filled out...), then:
        // return someCondition ();
        // and when this condition changes (last form field filled in...) then:
        // fireChangeEvent ();
        // and uncomment the complicated stuff below.
    }
  
    public boolean isFinishPanel() {
        return isValid();
    }
     
    private final Set listeners = new HashSet (1); 
    public final void addChangeListener (ChangeListener l) {
        synchronized (listeners) {
            listeners.add (l);
        }
    }
    public final void removeChangeListener (ChangeListener l) {
        synchronized (listeners) {
            listeners.remove (l);
        }
    }
    protected final void fireChangeEvent () {
        Iterator it;
        synchronized (listeners) {
            it = new HashSet (listeners).iterator ();
        }
        ChangeEvent ev = new ChangeEvent (this);
        while (it.hasNext ()) {
            ((ChangeListener) it.next ()).stateChanged (ev);
        }
    }
     
    
    // You can use a settings object to keep track of state.155
    // Normally the settings object will be the WizardDescriptor,
    // so you can use WizardDescriptor.getProperty & putProperty
    // to store information entered by the user.
    public void readSettings(Object settings) {
        if (wizardInfo.getName().equals(__MailResource)) {
            TemplateWizard wizard = (TemplateWizard)settings;
            String targetName = wizard.getTargetName();
            targetName = WS70ResourceUtils.createUniqueFileName(targetName, WS70ResourceUtils.setUpExists(this.helper.getData().getTargetFileObject()), __MAILResource);
            this.helper.getData().setTargetFile(targetName);
            if(component == null)
                getComponent();
            component.setHelper(this.helper);
        }else if (wizardInfo.getName().equals(__CustomResource)) {
            TemplateWizard wizard = (TemplateWizard)settings;
            String targetName = wizard.getTargetName();
            targetName = WS70ResourceUtils.createUniqueFileName(targetName, WS70ResourceUtils.setUpExists(this.helper.getData().getTargetFileObject()), __CUSTOMResource);
            this.helper.getData().setTargetFile(targetName);
            if(component == null)
                getComponent();
            component.setHelper(this.helper);
        }else if (wizardInfo.getName().equals(__ExternalJndiResource)) {
            TemplateWizard wizard = (TemplateWizard)settings;
            String targetName = wizard.getTargetName();
            targetName = WS70ResourceUtils.createUniqueFileName(targetName, WS70ResourceUtils.setUpExists(this.helper.getData().getTargetFileObject()), __EXTERNALResource);
            this.helper.getData().setTargetFile(targetName);
            if(component == null)
                getComponent();
            component.setHelper(this.helper);
        }else if (wizardInfo.getName().equals(__JdbcResource)) {
            TemplateWizard wizard = (TemplateWizard)settings;
            String targetName = wizard.getTargetName();
            targetName = WS70ResourceUtils.createUniqueFileName(targetName, WS70ResourceUtils.setUpExists(this.helper.getData().getTargetFileObject()), __JDBCResource);
            this.helper.getData().setTargetFile(targetName);
            if(component == null)
                getComponent();
            component.setHelper(this.helper);
        }
    }
    public void storeSettings(Object settings) {
    }
    
    public void initData() {
        this.component.initData();
    }
}

