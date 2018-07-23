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
 * AddAVKSupport.java
 *
 * Created on September 19, 2005, 4:21 PM
 */

package org.netbeans.modules.j2ee.sun.ide.avk;

import java.io.File;
import java.net.URL;
import java.awt.Dialog;
import java.util.ResourceBundle;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.modules.j2ee.sun.ide.avk.actions.StartInstrumentation;
import org.openide.nodes.Node;

import org.openide.util.HelpCtx;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.Lookup;
import org.openide.util.RequestProcessor;
import org.openide.filesystems.FileObject;
import org.openide.awt.HtmlBrowser.URLDisplayer;

import org.netbeans.modules.j2ee.dd.api.application.Web;
import org.netbeans.modules.j2ee.dd.api.application.Module;
import org.netbeans.modules.j2ee.dd.api.application.Application;
import org.netbeans.modules.j2ee.deployment.devmodules.api.J2eeModule;
import org.netbeans.modules.j2ee.deployment.devmodules.spi.J2eeAppProvider;
import org.netbeans.modules.j2ee.deployment.devmodules.spi.J2eeModuleProvider;

import javax.enterprise.deploy.spi.DeploymentManager;

import org.netbeans.modules.j2ee.sun.api.SunDeploymentManagerInterface;
import org.netbeans.modules.j2ee.sun.ide.j2ee.DeploymentManagerProperties;

/**
 *
 * @author  Nitya Doraisamy
 */
public class AddAVKSupport {
    static ResourceBundle bundle = ResourceBundle.getBundle("org.netbeans.modules.j2ee.sun.ide.avk.Bundle");// NOI18N
    boolean instrumentServer = false;
    boolean verify = false;
     
    private RequestProcessor processor = new RequestProcessor("instrument"); //NOI18N 
    
    public static final String APPSERVER_VERSION_UNKNOWN = "unknown"; // NOI18N
    public static final String APPSERVER_VERSION_9 = "9.0"; // NOI18N
    public static final String APPSERVER_VERSION_8_x = "8.x"; // NOI18N
    
    /** Creates new AddAVKSupport */
    public AddAVKSupport(){
    }        
    
    public boolean createAVKSupport(final DeploymentManager dm, final J2eeModuleProvider modProvider) {
        AddAVKFrame frame = new AddAVKFrame();
        DialogDescriptor dd = new DialogDescriptor(frame, bundle.getString("LBL_ChooseVerification"), true, null);
        Dialog dialog = DialogDisplayer.getDefault().createDialog(dd);
        dialog.setLocation(320, 325);
        dialog.pack();
        dialog.setVisible(true);
        if (dd.getValue() == DialogDescriptor.OK_OPTION) {
           instrumentServer = frame.instrumentServer();
           verify = frame.verify();
           if(instrumentServer) {
               instrument(dm, modProvider);
               return false;
           } else
               return verify;
        }else{
            instrumentServer = false;
            verify = false;
            return false;
        }    
    }   
        
    public boolean instrumentServer(){
        return instrumentServer;
    }

    public boolean verify(){
        return verify;
    }
    
    public boolean instrumentAndVerify(){
        return (verify && instrumentServer);
    }
    
    static class AddAVKFrame extends javax.swing.JPanel {
                        
            public AddAVKFrame(){
                initComponents();
                staticRadioButton.setSelected(true);
                HelpCtx.setHelpIDString(this, "AVKVerifyDialog");//NOI18N
            }
            /** This method is called from within the constructor to
             * initialize the form.
             * WARNING: Do NOT modify this code. The content of this method is
             * always regenerated by the Form Editor.
             */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jPanel1 = new javax.swing.JPanel();
        jTextPane1 = new javax.swing.JTextPane();
        jPanel2 = new javax.swing.JPanel();
        staticRadioButton = new javax.swing.JRadioButton();
        dynamicRadioButton = new javax.swing.JRadioButton();
        staticTextArea = new javax.swing.JTextArea();
        dynamicTextArea = new javax.swing.JTextArea();

        setLayout(new java.awt.GridBagLayout());

        setEnabled(false);
        setFocusable(false);
        setMaximumSize(new java.awt.Dimension(500, 200));
        setMinimumSize(new java.awt.Dimension(500, 200));
        setPreferredSize(new java.awt.Dimension(500, 200));
        setRequestFocusEnabled(false);
        setVerifyInputWhenFocusTarget(false);
        getAccessibleContext().setAccessibleName(bundle.getString("LBL_ChooseVerification"));
        getAccessibleContext().setAccessibleDescription(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/ide/avk/Bundle").getString("LBL_ChooseVerification"));
        jPanel1.setLayout(new java.awt.GridBagLayout());

        jTextPane1.setEditable(false);
        jTextPane1.setText(bundle.getString("MSG_ChooseVerification"));
        jTextPane1.setFocusCycleRoot(false);
        jTextPane1.setFocusable(false);
        jTextPane1.setOpaque(false);
        jTextPane1.setRequestFocusEnabled(false);
        jTextPane1.setVerifyInputWhenFocusTarget(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 15;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel1.add(jTextPane1, gridBagConstraints);
        jTextPane1.getAccessibleContext().setAccessibleName(bundle.getString("LBL_ChooseVerification"));
        jTextPane1.getAccessibleContext().setAccessibleDescription(bundle.getString("MSG_ChooseVerification"));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 0, 12);
        add(jPanel1, gridBagConstraints);

        jPanel2.setLayout(new java.awt.GridBagLayout());

        staticRadioButton.setText(bundle.getString("LBL_StaticVerification"));
        staticRadioButton.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                staticRadioButtonItemStateChanged(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel2.add(staticRadioButton, gridBagConstraints);
        staticRadioButton.getAccessibleContext().setAccessibleName(bundle.getString("LBL_StaticVerification"));
        staticRadioButton.getAccessibleContext().setAccessibleDescription(bundle.getString("LBL_StaticVerification"));

        dynamicRadioButton.setText(bundle.getString("LBL_DynamicVerification"));
        dynamicRadioButton.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                dynamicRadioButtonItemStateChanged(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel2.add(dynamicRadioButton, gridBagConstraints);
        dynamicRadioButton.getAccessibleContext().setAccessibleName(bundle.getString("LBL_DynamicVerification"));
        dynamicRadioButton.getAccessibleContext().setAccessibleDescription(bundle.getString("LBL_DynamicVerification"));

        staticTextArea.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 11));
        staticTextArea.setLineWrap(true);
        staticTextArea.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/ide/avk/Bundle").getString("Msg_InfoVerifier"));
        staticTextArea.setWrapStyleWord(true);
        staticTextArea.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 19, 0, 0);
        jPanel2.add(staticTextArea, gridBagConstraints);
        staticTextArea.getAccessibleContext().setAccessibleName(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/ide/avk/Bundle").getString("LBL_StaticVerification"));
        staticTextArea.getAccessibleContext().setAccessibleDescription(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/ide/avk/Bundle").getString("Msg_InfoVerifier"));

        dynamicTextArea.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 11));
        dynamicTextArea.setLineWrap(true);
        dynamicTextArea.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/ide/avk/Bundle").getString("Msg_InfoAVK"));
        dynamicTextArea.setWrapStyleWord(true);
        dynamicTextArea.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 19, 0, 0);
        jPanel2.add(dynamicTextArea, gridBagConstraints);
        dynamicTextArea.getAccessibleContext().setAccessibleName(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/ide/avk/Bundle").getString("LBL_DynamicVerification"));
        dynamicTextArea.getAccessibleContext().setAccessibleDescription(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/ide/avk/Bundle").getString("Msg_InfoAVK"));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 12, 12);
        add(jPanel2, gridBagConstraints);

    }
    // </editor-fold>//GEN-END:initComponents

    private void dynamicRadioButtonItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_dynamicRadioButtonItemStateChanged
        if(dynamicRadioButton.isSelected()){
            staticRadioButton.setSelected(false);
        }
    }//GEN-LAST:event_dynamicRadioButtonItemStateChanged

    private void staticRadioButtonItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_staticRadioButtonItemStateChanged
        if(staticRadioButton.isSelected()){
            dynamicRadioButton.setSelected(false);
        }
    }//GEN-LAST:event_staticRadioButtonItemStateChanged
        
    public boolean instrumentServer() {
        return dynamicRadioButton.isSelected();
    }
    
    public boolean verify() {
        return staticRadioButton.isSelected();
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JRadioButton dynamicRadioButton;
    private javax.swing.JTextArea dynamicTextArea;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JTextPane jTextPane1;
    private javax.swing.JRadioButton staticRadioButton;
    private javax.swing.JTextArea staticTextArea;
    // End of variables declaration//GEN-END:variables
    }
    
    private void instrument(final DeploymentManager dm, final J2eeModuleProvider modProvider){
        processor.post(new Runnable() {
            public void run() {
                SunDeploymentManagerInterface sdm = (SunDeploymentManagerInterface)dm;
                String appServerVersion = getAppServerVersion(sdm.getPlatformRoot());
                if(APPSERVER_VERSION_9.equals(appServerVersion)) {
                   StartInstrumentation.getInstance().start(getProject(modProvider));
                } else {
                    
                    AVKSupport avkSupport = new AVKSupport((SunDeploymentManagerInterface)dm);
                    avkSupport.setAVK(true);
                    launchApp(dm, getContextRoot(modProvider));
                }
            }
        });
    }
    
    private String getContextRoot(J2eeModuleProvider modProvider){
        String url = null;
        if(modProvider != null){
            if(modProvider instanceof J2eeAppProvider){
                //Get contextRoot of first web module in application.xml
                Application appXml = (Application)modProvider.getJ2eeModule().getDeploymentDescriptor(J2eeModule.APP_XML);
                Module[] mods = appXml.getModule();
                for(int i=0; i<mods.length; i++){
                    Web webMod = mods[i].getWeb();
                    if(webMod != null){
                        url = webMod.getContextRoot();
                        break;
                    }
                }
            }else{
                url = modProvider.getConfigSupport().getWebContextRoot();
            }
        }
        return url;
    }
    
    private void launchApp(DeploymentManager dm, String contextRoot){
        try{
            if(contextRoot != null){
                SunDeploymentManagerInterface sdm = (SunDeploymentManagerInterface)dm;
                DeploymentManagerProperties dmProps = new DeploymentManagerProperties(dm);
                String start = "http://" + sdm.getHost() + ":" + dmProps.getHttpPortNumber(); //NOI18N
                if (contextRoot.startsWith("/")) //NOI18N
                    contextRoot = start + contextRoot;
                else
                    contextRoot = start + "/" + contextRoot; //NOI18N
                
                URLDisplayer.getDefault().showURL(new URL(contextRoot));
            }
        }catch(Exception ex){}
    }
    
    /** 
     * This code is copied from serverplugins/sun/appsrv81/src/org/netbeans/modules/j2ee/sun/ide/j2ee/PlatformImpl.java
     */
    public  String getAppServerVersion(File asInstallRoot) {
        String version = APPSERVER_VERSION_UNKNOWN;    // NOI18N

        if(asInstallRoot != null && asInstallRoot.exists()) {
            File sunDomain11Dtd = new File(asInstallRoot, "lib/dtds/sun-domain_1_1.dtd"); // NOI18N
            //now test for AS 9 (J2EE 5.0) which should work for this plugin
            File as9 = new File((asInstallRoot)+"/lib/dtds/sun-web-app_2_5-0.dtd");
            if(as9.exists()){
                version = APPSERVER_VERSION_9;

            } else    if(sunDomain11Dtd.exists()) {
                version = APPSERVER_VERSION_8_x;
            }
        }
        return version;
    }
    
    private Project getProject(J2eeModuleProvider modProvider) {
        Project project = null;
        try {
            FileObject archive = modProvider.getJ2eeModule().getArchive();
            project = FileOwnerQuery.getOwner(archive);
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        return project;
    }
    
}
