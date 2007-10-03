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

package org.netbeans.modules.j2ee.ejbfreeform.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.JFileChooser;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.netbeans.api.project.Project;
import org.netbeans.api.queries.CollocationQuery;
import org.netbeans.modules.ant.freeform.spi.support.Util;
import org.netbeans.modules.j2ee.dd.api.ejb.DDProvider;
import org.netbeans.modules.j2ee.dd.api.ejb.EjbJar;
import org.netbeans.modules.j2ee.deployment.devmodules.api.Deployment;
import org.netbeans.modules.j2ee.deployment.devmodules.api.J2eeModule;
import org.netbeans.modules.j2ee.deployment.devmodules.api.J2eePlatform;
import org.netbeans.modules.j2ee.ejbfreeform.EJBProjectGenerator;
import org.netbeans.modules.j2ee.ejbfreeform.EJBProjectNature;
import org.netbeans.modules.j2ee.ejbfreeform.EjbFreeformProperties;
import org.openide.filesystems.FileUtil;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.netbeans.spi.project.AuxiliaryConfiguration;
import org.netbeans.spi.project.support.ant.AntProjectHelper;
import org.netbeans.spi.project.support.ant.PropertyUtils;
import org.netbeans.spi.project.support.ant.PropertyEvaluator;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;
import org.w3c.dom.Element;


/**
 *
 * @author  Radko Najman
 */
public class EJBLocationsPanel extends javax.swing.JPanel implements HelpCtx.Provider {
    
    /** Original project base folder */
    private File baseFolder;
    /** Freeform Project base folder */
    private File nbProjectFolder;
    
    private File srcPackagesLocation;
    
    private AntProjectHelper projectHelper;
    
    private java.util.List serverIDs;
    private ChangeListener listener;
    private DocumentListener documentListener;
    
    private BigDecimal ejbJarXmlVersion;
    
    private boolean isNew = false;
    private boolean isNS2 = false;
    private boolean is15 = false;
    
    private boolean updatingLevels = false;
    
    private static final String J2EE_SPEC_15_LABEL = NbBundle.getMessage(EJBLocationsPanel.class, "TXT_J2EESpecLevel_2"); //NOI18N
    private static final String J2EE_SPEC_14_LABEL = NbBundle.getMessage(EJBLocationsPanel.class, "TXT_J2EESpecLevel_0"); //NOI18N
    private static final String J2EE_SPEC_13_LABEL = NbBundle.getMessage(EJBLocationsPanel.class, "TXT_J2EESpecLevel_1"); //NOI18N
    
    /** Creates new form EJBLocations */
    public EJBLocationsPanel(EJBLocationsWizardPanel panel) {
        this.listener = panel;
        initComponents();
        
        isNew = true;
        initServerInstances();
        
        documentListener = new DocumentListener() {           
            public void insertUpdate(DocumentEvent e) {
                update(e);
            }
            public void removeUpdate(DocumentEvent e) {
                update(e);
            }
            public void changedUpdate(DocumentEvent e) {
                update(e);
            }
        };
        
        this.jTextFieldConfigFiles.getDocument().addDocumentListener(documentListener);
    }
    
    public EJBLocationsPanel(EJBLocationsWizardPanel panel, Project project, AntProjectHelper projectHelper, PropertyEvaluator projectEvaluator, AuxiliaryConfiguration aux) {
        this(panel);
        this.projectHelper = projectHelper;
        setFolders(Util.getProjectLocation(projectHelper, projectEvaluator), FileUtil.toFile(projectHelper.getProjectDirectory()));
        
        Element ejbData = aux.getConfigurationFragment(EJBProjectNature.EL_EJB, EJBProjectNature.NS_EJB_2, true);
        if (ejbData != null) {
            isNS2 = true;
        }
                
        List ejbModules = EJBProjectGenerator.getEJBmodules(projectHelper, aux);
        if (ejbModules != null) {
            EJBProjectGenerator.EJBModule em = (EJBProjectGenerator.EJBModule) ejbModules.get(0);
            is15 = em.j2eeSpecLevel.equals("1.5") ? true : false;
        }

        isNew = false;
        updateJ2eeLevels();
        
        // remove Generic server for 1.5 projects from combo
        if (is15) {
            serverIDs.remove("GENERIC"); // NOI18N
            serverTypeComboBox.removeItem(Deployment.getDefault().getServerDisplayName("GENERIC")); // NOI18N
        }
        
        if (ejbModules != null) {
            EJBProjectGenerator.EJBModule wm = (EJBProjectGenerator.EJBModule)ejbModules.get(0);
            String configFiles = getLocationDisplayName(projectEvaluator, nbProjectFolder, wm.configFiles);
            String classpath = getLocationDisplayName(projectEvaluator, nbProjectFolder, wm.classpath);
            String resourceFiles = getLocationDisplayName(projectEvaluator, nbProjectFolder, projectEvaluator.getProperty(EjbFreeformProperties.RESOURCE_DIR));
            String serverID = projectEvaluator.getProperty(EjbFreeformProperties.J2EE_SERVER_TYPE);
            jTextFieldConfigFiles.setText(configFiles);
            resourcesTextField.setText(resourceFiles);
            
            setSrcPackages(classpath);
            if (wm.j2eeSpecLevel.equals("1.5")) {
                j2eeSpecComboBox.setSelectedItem(NbBundle.getMessage(EJBLocationsPanel.class, "TXT_J2EESpecLevel_2"));
            } else if (wm.j2eeSpecLevel.equals("1.4")) {
                j2eeSpecComboBox.setSelectedItem(NbBundle.getMessage(EJBLocationsPanel.class, "TXT_J2EESpecLevel_0"));
            } else {
                j2eeSpecComboBox.setSelectedItem(NbBundle.getMessage(EJBLocationsPanel.class, "TXT_J2EESpecLevel_1"));
            }
            
            if (serverID != null)
                selectServerID(serverID);
        }

    }
    
    private void updateJ2eeLevels() {
        String prevSelectedItem = (String)j2eeSpecComboBox.getSelectedItem();
        int selectedServerIndex = serverTypeComboBox.getSelectedIndex();
        if (selectedServerIndex == -1) {
            return;
        }
        String serverID = (String) serverIDs.get(selectedServerIndex);
        String servInsID = getFirstServerInstanceID(serverID);
        Set supportedVersions;
        if (servInsID != null) {
            J2eePlatform j2eePlatform = Deployment.getDefault().getJ2eePlatform(servInsID);
            supportedVersions = j2eePlatform.getSupportedSpecVersions();
        } else {
            supportedVersions = new HashSet();
            supportedVersions.add(J2eeModule.J2EE_13);
            supportedVersions.add(J2eeModule.J2EE_14);
        }
        updatingLevels = true;
        j2eeSpecComboBox.removeAllItems();
        
        if (supportedVersions.contains(J2eeModule.JAVA_EE_5) && (isNew || isNS2)) {
            j2eeSpecComboBox.addItem(J2EE_SPEC_15_LABEL);
        }
        if (supportedVersions.contains(J2eeModule.J2EE_14) && !is15) {
            j2eeSpecComboBox.addItem(J2EE_SPEC_14_LABEL);
        }
        if (supportedVersions.contains(J2eeModule.J2EE_13) && !is15) {
            j2eeSpecComboBox.addItem(J2EE_SPEC_13_LABEL);
        }
        if (prevSelectedItem != null) {
            j2eeSpecComboBox.setSelectedItem(prevSelectedItem);
        }
        updatingLevels = false;
        if (listener != null) {
            listener.stateChanged(null);
        }
    }
    
    private void update(DocumentEvent e) {
        setEjbJarXmlJ2eeVersion(findEbjJarXml(getAsFile(jTextFieldConfigFiles.getText())));
        if (listener != null) {
            listener.stateChanged(null);
        }
    }

    /**
     * Convert given string value (e.g. "${project.dir}/src" to a file
     * and try to relativize it.
     */
    // XXX: copied from java/freeform:SourceFoldersPanel.getLocationDisplayName
    public static String getLocationDisplayName(PropertyEvaluator evaluator, File base, String val) {
        if (val == null || val.trim().length() <= 0)
            return ""; // NOI18N
        File f = Util.resolveFile(evaluator, base, val);
        if (f == null) {
            return val;
        }
        String location = f.getAbsolutePath();
        if (CollocationQuery.areCollocated(base, f)) {
            location = PropertyUtils.relativizeFile(base, f).replace('/', File.separatorChar); // NOI18N
        }
        return location;
    }
    
    
    public HelpCtx getHelpCtx() {
        return new HelpCtx( EJBLocationsPanel.class );
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jTextFieldConfigFiles = new javax.swing.JTextField();
        jButtonEJB = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        j2eeSpecComboBox = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        serverTypeComboBox = new javax.swing.JComboBox();
        jLabel6 = new javax.swing.JLabel();
        resourcesTextField = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        warningLabel = new javax.swing.JLabel();

        setLayout(new java.awt.GridBagLayout());

        setPreferredSize(new java.awt.Dimension(375, 135));
        jLabel1.setText(org.openide.util.NbBundle.getMessage(EJBLocationsPanel.class, "LBL_ConfigFilesPanel_Description")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 11, 0);
        add(jLabel1, gridBagConstraints);

        jLabel2.setLabelFor(jTextFieldConfigFiles);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(EJBLocationsPanel.class, "LBL_ConfigFilesPanel_ConfigFilesLocation_Label")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 6, 11);
        add(jLabel2, gridBagConstraints);
        jLabel2.getAccessibleContext().setAccessibleName(null);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 6, 11);
        add(jTextFieldConfigFiles, gridBagConstraints);
        jTextFieldConfigFiles.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(EJBLocationsPanel.class, "ACS_LBL_ConfigFilesPanel_ConfigFilesLocation_A11YDesc")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jButtonEJB, org.openide.util.NbBundle.getMessage(EJBLocationsPanel.class, "BTN_BasicProjectInfoPanel_browseAntScript")); // NOI18N
        jButtonEJB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonEJBActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 6, 0);
        add(jButtonEJB, gridBagConstraints);
        jButtonEJB.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(EJBLocationsPanel.class, "ACS_LBL_ConfigFilesPanel_ConfigFilesLocationBrowse_A11YDesc")); // NOI18N

        jLabel5.setLabelFor(j2eeSpecComboBox);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel5, org.openide.util.NbBundle.getMessage(EJBLocationsPanel.class, "LBL_ConfigFilesPanel_J2EESpecLevel_Label")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 6, 11);
        add(jLabel5, gridBagConstraints);

        j2eeSpecComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                j2eeSpecComboBoxActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 11, 11);
        add(j2eeSpecComboBox, gridBagConstraints);
        j2eeSpecComboBox.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(EJBLocationsPanel.class, "ACS_LBL_ConfigFilesPanel_J2EESpecLevel_A11YDesc")); // NOI18N

        jLabel3.setLabelFor(serverTypeComboBox);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getMessage(EJBLocationsPanel.class, "LBL_ConfigFilesPanel_ServerType_Label")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 6, 11);
        add(jLabel3, gridBagConstraints);

        serverTypeComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                serverTypeComboBoxItemStateChanged(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 11, 11);
        add(serverTypeComboBox, gridBagConstraints);
        serverTypeComboBox.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(EJBLocationsPanel.class, "ACS_LBL_ConfigFilesPanel_ServerType_A11YDesc")); // NOI18N

        jLabel6.setLabelFor(resourcesTextField);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel6, org.openide.util.NbBundle.getMessage(EJBLocationsPanel.class, "LBL_ConfigFilesPanel_Resources_label")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 6, 11);
        add(jLabel6, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 6, 11);
        add(resourcesTextField, gridBagConstraints);
        resourcesTextField.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(EJBLocationsPanel.class, "ACS_LBL_ConfigFilesPanel_ResourceFolder_A11YDesc")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jButton1, org.openide.util.NbBundle.getMessage(EJBLocationsPanel.class, "BTN_ConfigFilesPanel_ResourcesBrowse")); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 6, 0);
        add(jButton1, gridBagConstraints);
        jButton1.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(EJBLocationsPanel.class, "ACS_LBL_ConfigFilesPanel_ResourceFolderBrowse_A11YDesc")); // NOI18N

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(jPanel1, gridBagConstraints);

        warningLabel.setForeground(new java.awt.Color(89, 71, 191));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(11, 0, 0, 0);
        add(warningLabel, gridBagConstraints);

    }// </editor-fold>//GEN-END:initComponents

    private void j2eeSpecComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_j2eeSpecComboBoxActionPerformed
        String selectedItem = (String)j2eeSpecComboBox.getSelectedItem();
        String warningMessage = null;
        
        if (!updatingLevels && listener != null) {
            listener.stateChanged(null);
        }
        
        if (J2EE_SPEC_14_LABEL.equals(selectedItem) && new BigDecimal(EjbJar.VERSION_2_0).equals(ejbJarXmlVersion)) {
            warningMessage = NbBundle.getMessage(EJBLocationsPanel.class, "MSG_EjbJarXmlUpdate");
        }
        
        warningLabel.setText(warningMessage);
    }//GEN-LAST:event_j2eeSpecComboBoxActionPerformed

    private void serverTypeComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_serverTypeComboBoxItemStateChanged
        updateJ2eeLevels();
    }//GEN-LAST:event_serverTypeComboBoxItemStateChanged
    
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        File resources = getResourcesLocation();
        JFileChooser chooser = createChooser(resources != null ? resources.getAbsolutePath() : baseFolder.getAbsolutePath()); // NOI18N
        if (JFileChooser.APPROVE_OPTION == chooser.showOpenDialog(this)) {
            setResources(chooser.getSelectedFile());
        }
    }//GEN-LAST:event_jButton1ActionPerformed
                
    private void jButtonEJBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonEJBActionPerformed
        JFileChooser chooser = createChooser(getConfigFilesLocation().getAbsolutePath());
        if (JFileChooser.APPROVE_OPTION == chooser.showOpenDialog(this)) {
            setConfigFiles(chooser.getSelectedFile());
        }
    }//GEN-LAST:event_jButtonEJBActionPerformed
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox j2eeSpecComboBox;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButtonEJB;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField jTextFieldConfigFiles;
    private javax.swing.JTextField resourcesTextField;
    private javax.swing.JComboBox serverTypeComboBox;
    private javax.swing.JLabel warningLabel;
    // End of variables declaration//GEN-END:variables
    
    private static JFileChooser createChooser(String path) {
        JFileChooser chooser = new JFileChooser();
        FileUtil.preventFileChooserSymlinkTraversal(chooser, new File(path));
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);
        
        return chooser;
    }
    
    protected List getEJBModules() {
        ArrayList l = new ArrayList();
        
        EJBProjectGenerator.EJBModule ejbModule = new EJBProjectGenerator.EJBModule();
        ejbModule.configFiles = getRelativeLocation(getConfigFilesLocation());
        
        String j2eeLevel = (String) j2eeSpecComboBox.getSelectedItem();
        if (j2eeLevel != null) {
            if (j2eeLevel.equals(NbBundle.getMessage(EJBLocationsPanel.class, "TXT_J2EESpecLevel_2"))) {
                ejbModule.j2eeSpecLevel = "1.5";
            } else if (j2eeLevel.equals(NbBundle.getMessage(EJBLocationsPanel.class, "TXT_J2EESpecLevel_0"))) {
                ejbModule.j2eeSpecLevel = "1.4";
            } else {
                ejbModule.j2eeSpecLevel = "1.3";
            }
        }
        // ejbModule.classpath = getRelativeLocation(getSrcPackagesLocation());
        // XXX since the contents of the classpath element is set to the (sometimes wrongly) 
        // detected Java source root, better to set it to null for now and let the 
        // Java nature provide the classpath (see EJBModules and issue 67651)
        ejbModule.classpath = ""; // NOI18N
        l.add(ejbModule);
        
        return l;
    }
    
    protected List getJavaSrcFolder() {
        ArrayList l = new ArrayList();
        File sourceLoc = getSrcPackagesLocation();
        l.add(getRelativeLocation(sourceLoc));
        l.add(sourceLoc.getName());
        return l;
    }
    
    /**
     * @return list of pairs [relative path, display name]
     */
    protected List getEJBSrcFolder() {
        ArrayList l = new ArrayList();
        final File webLocation = getConfigFilesLocation();
        l.add(getRelativeLocation(webLocation));
        l.add(webLocation.getName());
        return l;
    }
    
    protected List getResourcesFolder() {
        ArrayList l = new ArrayList();
        File resourceLoc = getResourcesLocation();
        l.add(resourceLoc != null ? getRelativeLocation(resourceLoc) : null);
        l.add(resourceLoc != null ? resourceLoc.getName() : null);
        return l;
    }
    
    private File getAsFile(String filename) {
        final File f = new File(filename.trim());
        return PropertyUtils.resolveFile(nbProjectFolder, filename);
    }
    
    /** Called from WizardDescriptor.Panel and ProjectCustomizer.Panel
     * to set base folder. Panel will use this for default position of JFileChooser.
     * @param baseFolder original project base folder
     * @param nbProjectFolder Freeform Project base folder
     */
    public void setFolders(File baseFolder, File nbProjectFolder) {
        this.baseFolder = baseFolder;
        this.nbProjectFolder = nbProjectFolder;
    }
    
    protected void setConfigFilesField(String path) {
        if (path != null && !path.equals("")) {
            setConfigFiles(new File(path));
        } else {
            setConfigFiles(path);
        }
    }
    
    protected void setConfigFiles(String path) {
        jTextFieldConfigFiles.setText(path);
    }
    
    protected void setSrcPackages(String path) {
        setSrcPackages(getAsFile(path));
    }
    
    private void setConfigFiles(final File file) {
        setConfigFiles(relativizeFile(file));
    }
    
    private void setResources(final File file) {
        resourcesTextField.setText(relativizeFile(file));
    }
    
    protected File getConfigFilesLocation() {
        return getAsFile(jTextFieldConfigFiles.getText()).getAbsoluteFile();
    }
    
    protected File getResourcesLocation() {
        String resources = resourcesTextField.getText().trim();
        return resources.length() <= 0 ? null : getAsFile(resourcesTextField.getText()).getAbsoluteFile();
    }
    
    private void setSrcPackages(final File file) {
        srcPackagesLocation = file;
    }
    
    protected File getSrcPackagesLocation() {
        return srcPackagesLocation;
    }
    
    private String relativizeFile(final File file) {
        File normalizedFile = FileUtil.normalizeFile(file);
        if (CollocationQuery.areCollocated(nbProjectFolder, file)) {
            return PropertyUtils.relativizeFile(nbProjectFolder, normalizedFile);
        } else {
            return normalizedFile.getAbsolutePath();
        }
    }
    
    private String getRelativeLocation(final File location) {
        final File normalizedLocation = FileUtil.normalizeFile(location);
        return Util.relativizeLocation(baseFolder, nbProjectFolder, normalizedLocation);
    }
    
    ActionListener getCustomizerOkListener() {
        return new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {

                AuxiliaryConfiguration aux = Util.getAuxiliaryConfiguration(projectHelper);
                EJBProjectGenerator.putEJBModules(projectHelper, aux, getEJBModules());
                EJBProjectGenerator.putServerID(projectHelper, getSelectedServerID());
                EJBProjectGenerator.putResourceFolder(projectHelper, getResourcesFolder());

                String j2eeLevel = ((EJBProjectGenerator.EJBModule)getEJBModules().get(0)).j2eeSpecLevel;
                EJBProjectGenerator.putJ2EELevel(projectHelper, j2eeLevel);
                
                // update the DD to 2.1 if it is 2.0 and the user switched to J2EE 1.4
                FileObject ejbJarXml = findEbjJarXml(getConfigFilesLocation());
                try {
                    if (j2eeLevel.equals("1.4") && !new BigDecimal(EjbJar.VERSION_2_1).equals(getEjbJarXmlVersion(ejbJarXml))) { // NOI18N
                        EjbJar root = DDProvider.getDefault().getDDRoot(ejbJarXml);
                        root.setVersion(new BigDecimal(EjbJar.VERSION_2_1));
                        root.write(ejbJarXml);
                    }
                } catch (IOException e) {
                    String message = NbBundle.getMessage(EJBLocationsPanel.class, "MSG_EjbJarXmlCorrupted");
                    Exceptions.printStackTrace(Exceptions.attachLocalizedMessage(e, message));
                }
            }
        };
    }
        
    
    private void initServerInstances() {
        String[] servInstIDs = Deployment.getDefault().getServerInstanceIDs();
        serverIDs = new ArrayList();
        for (int i = 0; i < servInstIDs.length; i++) {
            J2eePlatform j2eePlat = Deployment.getDefault().getJ2eePlatform(servInstIDs[i]);
            String serverID = Deployment.getDefault().getServerID(servInstIDs[i]);
            String servDisplayName = Deployment.getDefault().getServerDisplayName(serverID);
            if (servDisplayName != null && !serverIDs.contains(serverID)
                    && j2eePlat != null && j2eePlat.getSupportedModuleTypes().contains(J2eeModule.EJB)) {
                serverIDs.add(serverID);
                serverTypeComboBox.addItem(servDisplayName);
            }
        }
        serverIDs.add("GENERIC"); // NOI18N
        serverTypeComboBox.addItem(Deployment.getDefault().getServerDisplayName("GENERIC")); // NOI18N
        if (serverIDs.size() > 0) {
            serverTypeComboBox.setSelectedIndex(0);
        } else {
            serverTypeComboBox.setEnabled(false);
            j2eeSpecComboBox.setEnabled(false);
        }
    }

    /**
     * Returns ID of first found instance of server with given server ID,
     * @param serverID ID of server
     * @return ID of server instance 
     */
    private String getFirstServerInstanceID(String serverID) {
        if (serverID == null) {
            return null;
        }
        String[] servInstIDs = Deployment.getDefault().getServerInstanceIDs();
        for (int i = 0; i < servInstIDs.length; i++) {
            if (serverID == Deployment.getDefault().getServerID(servInstIDs[i])) {
                return (String) servInstIDs[i];
            }
        }
        return null;
    }

    public String getSelectedServerID() {
        int idx = serverTypeComboBox.getSelectedIndex();
        if (idx == -1) {
            return null;
        }
        String serverID = (String) serverIDs.get(idx);
        return serverID;
    }
    
    private void selectServerID(String serverID) {
        for (int i = 0; i < serverIDs.size(); i++) {
            if (serverID.equals(serverIDs.get(i))) {
                serverTypeComboBox.setSelectedIndex(i);
                break;
            }
        }
    }
    
    public String getSelectedJ2eeSpec() {
        Object item = j2eeSpecComboBox.getSelectedItem();
        if (item == null) {
            return null;
        } else {
            if (item.equals(J2EE_SPEC_15_LABEL)) {
                return J2eeModule.JAVA_EE_5;
            } else if (item.equals(J2EE_SPEC_14_LABEL)) {
                return J2eeModule.J2EE_14;
            } else {
                return J2eeModule.J2EE_13;
            }
        }
    }
    
    public boolean valid(WizardDescriptor wizardDescriptor) {
        File cfLoc = getConfigFilesLocation();
        if (jTextFieldConfigFiles.getText().equals("") || !cfLoc.isDirectory()) {
            wizardDescriptor.putProperty("WizardPanel_errorMessage", NbBundle.getMessage(EJBLocationsPanel.class,"MSG_NoConfFolder")); // NOI18N
            return false;
        }
        
        if (is15 && getSelectedServerID().equals("GENERIC")) {
            wizardDescriptor.putProperty("WizardPanel_errorMessage", NbBundle.getMessage(EJBLocationsPanel.class,"MSG_NoGENERICServer")); // NOI18N
            return false;
        }
        
        File[] dds = getConfigFilesLocation().listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.equals("ejb-jar.xml");
            }
        });
        
        if (dds.length == 0 && !getSelectedJ2eeSpec().equals(J2eeModule.JAVA_EE_5)) {
            wizardDescriptor.putProperty("WizardPanel_errorMessage", NbBundle.getMessage(EJBLocationsPanel.class,"MSG_NoEjbJarXml")); //NOI18N
            return false;
        }

        wizardDescriptor.putProperty("WizardPanel_errorMessage", ""); //NOI18N
        return true;
    }
    
    public static FileObject findEbjJarXml(File configFolder) {
        FileObject confFolderFo = FileUtil.toFileObject(configFolder);
        FileObject ejbJarXml = null;
        if (confFolderFo != null) {
             ejbJarXml = confFolderFo.getFileObject("ejb-jar.xml"); // NOI18N
        }
        return ejbJarXml;
    }
    
    public static BigDecimal getEjbJarXmlVersion(FileObject ejbJarXml) throws IOException {
        if (ejbJarXml != null) {
            return DDProvider.getDefault().getDDRoot(ejbJarXml).getVersion();
        } else {
            return null;
        }
    }
    
    private void setEjbJarXmlJ2eeVersion(FileObject ejbJarXml) {
        try {
            BigDecimal version = getEjbJarXmlVersion(ejbJarXml);
            ejbJarXmlVersion = version;
            if (version == null)
                return;
            
            if (new BigDecimal(EjbJar.VERSION_2_0).equals(version)) {
                j2eeSpecComboBox.setSelectedItem(J2EE_SPEC_13_LABEL);
            } else if (new BigDecimal(EjbJar.VERSION_2_1).equals(version)) {
                j2eeSpecComboBox.setSelectedItem(J2EE_SPEC_14_LABEL);
            } else if (new BigDecimal(EjbJar.VERSION_3_0).equals(version)) {
                j2eeSpecComboBox.setSelectedItem(J2EE_SPEC_15_LABEL);
            }
            
        } catch (IOException e) {
            String message = NbBundle.getMessage(EJBLocationsPanel.class, "MSG_EjbJarXmlCorrupted");
            Exceptions.printStackTrace(Exceptions.attachLocalizedMessage(e, message));
        }
    }
}
