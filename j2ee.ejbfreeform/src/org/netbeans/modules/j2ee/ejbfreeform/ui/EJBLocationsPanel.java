/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.j2ee.ejbfreeform.ui;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.netbeans.api.project.Project;
import org.netbeans.api.queries.CollocationQuery;
import org.netbeans.modules.ant.freeform.spi.ProjectPropertiesPanel;
import org.netbeans.modules.ant.freeform.spi.support.Util;
import org.netbeans.modules.j2ee.deployment.devmodules.api.Deployment;
import org.netbeans.modules.j2ee.deployment.devmodules.api.J2eeModule;
import org.netbeans.modules.j2ee.deployment.devmodules.api.J2eePlatform;
import org.netbeans.modules.j2ee.ejbfreeform.EJBProjectGenerator;
import org.netbeans.modules.j2ee.ejbjarproject.ui.customizer.EjbJarProjectProperties;
import org.openide.filesystems.FileUtil;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.netbeans.spi.project.AuxiliaryConfiguration;
import org.netbeans.spi.project.support.ant.AntProjectHelper;
import org.netbeans.spi.project.support.ant.PropertyUtils;
import org.netbeans.spi.project.support.ant.PropertyEvaluator;
import org.openide.WizardDescriptor;


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
    
    private static final String J2EE_SPEC_14_LABEL = NbBundle.getMessage(EJBLocationsPanel.class, "TXT_J2EESpecLevel_0"); //NOI18N
    private static final String J2EE_SPEC_13_LABEL = NbBundle.getMessage(EJBLocationsPanel.class, "TXT_J2EESpecLevel_1"); //NOI18N
    
    /** Creates new form EJBLocations */
    public EJBLocationsPanel(EJBLocationsWizardPanel panel) {
        this.listener = panel;
        initComponents();
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
        
        List l = EJBProjectGenerator.getEJBmodules(projectHelper, aux);
        if (l != null) {
            EJBProjectGenerator.EJBModule wm = (EJBProjectGenerator.EJBModule)l.get(0);
            String configFiles = getLocationDisplayName(projectEvaluator, baseFolder, wm.configFiles);
            String classpath = getLocationDisplayName(projectEvaluator, baseFolder, wm.classpath);
            String resourceFiles = getLocationDisplayName(projectEvaluator, baseFolder, projectEvaluator.getProperty(EjbJarProjectProperties.RESOURCE_DIR));
            String serverID = projectEvaluator.getProperty(EjbJarProjectProperties.J2EE_SERVER_TYPE);
            jTextFieldConfigFiles.setText(configFiles);
            resourcesTextField.setText(resourceFiles);
            
            setSrcPackages(classpath);
            
            if (wm.j2eeSpecLevel.equals("1.4"))
                j2eeSpecComboBox.setSelectedItem(NbBundle.getMessage(EJBLocationsPanel.class, "TXT_J2EESpecLevel_0"));
            else
                j2eeSpecComboBox.setSelectedItem(NbBundle.getMessage(EJBLocationsPanel.class, "TXT_J2EESpecLevel_1"));
            
            if (serverID != null)
                selectServerID(serverID);
        }

    }
    
    private void update(DocumentEvent e) {
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
        if (val == null)
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

        setLayout(new java.awt.GridBagLayout());

        setPreferredSize(new java.awt.Dimension(375, 135));
        jLabel1.setText(org.openide.util.NbBundle.getMessage(EJBLocationsPanel.class, "LBL_ConfigFilesPanel_Description"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 11, 0);
        add(jLabel1, gridBagConstraints);

        jLabel2.setLabelFor(jTextFieldConfigFiles);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(EJBLocationsPanel.class, "LBL_ConfigFilesPanel_ConfigFilesLocation_Label"));
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
        jTextFieldConfigFiles.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(EJBLocationsPanel.class, "ACS_LBL_ConfigFilesPanel_ConfigFilesLocation_A11YDesc"));

        org.openide.awt.Mnemonics.setLocalizedText(jButtonEJB, org.openide.util.NbBundle.getMessage(EJBLocationsPanel.class, "BTN_BasicProjectInfoPanel_browseAntScript"));
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
        jButtonEJB.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(EJBLocationsPanel.class, "ACS_LBL_ConfigFilesPanel_ConfigFilesLocationBrowse_A11YDesc"));

        jLabel5.setLabelFor(j2eeSpecComboBox);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel5, org.openide.util.NbBundle.getMessage(EJBLocationsPanel.class, "LBL_ConfigFilesPanel_J2EESpecLevel_Label"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 6, 11);
        add(jLabel5, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 11, 11);
        add(j2eeSpecComboBox, gridBagConstraints);
        j2eeSpecComboBox.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(EJBLocationsPanel.class, "ACS_LBL_ConfigFilesPanel_J2EESpecLevel_A11YDesc"));

        jLabel3.setLabelFor(serverTypeComboBox);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getMessage(EJBLocationsPanel.class, "LBL_ConfigFilesPanel_ServerType_Label"));
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
        serverTypeComboBox.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(EJBLocationsPanel.class, "ACS_LBL_ConfigFilesPanel_ServerType_A11YDesc"));

        jLabel6.setLabelFor(resourcesTextField);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel6, org.openide.util.NbBundle.getMessage(EJBLocationsPanel.class, "LBL_ConfigFilesPanel_Resources_label"));
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
        resourcesTextField.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(EJBLocationsPanel.class, "ACS_LBL_ConfigFilesPanel_ResourceFolder_A11YDesc"));

        org.openide.awt.Mnemonics.setLocalizedText(jButton1, org.openide.util.NbBundle.getMessage(EJBLocationsPanel.class, "BTN_ConfigFilesPanel_ResourcesBrowse"));
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
        jButton1.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(EJBLocationsPanel.class, "ACS_LBL_ConfigFilesPanel_ResourceFolderBrowse_A11YDesc"));

    }
    // </editor-fold>//GEN-END:initComponents

    private void serverTypeComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_serverTypeComboBoxItemStateChanged
        String prevSelectedItem = (String)j2eeSpecComboBox.getSelectedItem();
        String serverID = (String) serverIDs.get(serverTypeComboBox.getSelectedIndex());
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
        j2eeSpecComboBox.removeAllItems();
        if (supportedVersions.contains(J2eeModule.J2EE_14)) {
            j2eeSpecComboBox.addItem(J2EE_SPEC_14_LABEL);
        }
        if (supportedVersions.contains(J2eeModule.J2EE_13)) {
            j2eeSpecComboBox.addItem(J2EE_SPEC_13_LABEL);
        }
        if (prevSelectedItem != null) {
            j2eeSpecComboBox.setSelectedItem(prevSelectedItem);
        }
    }//GEN-LAST:event_serverTypeComboBoxItemStateChanged
    
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        JFileChooser chooser = createChooser(getResourcesLocation().getAbsolutePath());
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
    private javax.swing.JTextField jTextFieldConfigFiles;
    private javax.swing.JTextField resourcesTextField;
    private javax.swing.JComboBox serverTypeComboBox;
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
            if (j2eeLevel.equals(NbBundle.getMessage(EJBLocationsPanel.class, "TXT_J2EESpecLevel_0"))) {
                ejbModule.j2eeSpecLevel = "1.4";
            } else {
                ejbModule.j2eeSpecLevel = "1.3";
            }
        }
        ejbModule.classpath = getRelativeLocation(getSrcPackagesLocation());
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
        l.add(getRelativeLocation(resourceLoc));
        l.add(resourceLoc.getName());
        return l;
    }
    
    private File getAsFile(String filename) {
        final String s = filename.trim();
        final File f = new File(s);
        return f.isAbsolute() ? f : new File(baseFolder, s).getAbsoluteFile();
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
    
    protected void setConfigFiles(String path) {
        jTextFieldConfigFiles.setText(path);
    }
    
    protected void setSrcPackages(String path) {
        setSrcPackages(getAsFile(path));
    }
    
    private void setConfigFiles(final File file) {
        jTextFieldConfigFiles.setText(file.getAbsolutePath());
    }
    
    private void setResources(final File file) {
        resourcesTextField.setText(file.getAbsolutePath());
    }
    
    protected File getConfigFilesLocation() {
        return getAsFile(jTextFieldConfigFiles.getText()).getAbsoluteFile();
        
    }
    
    protected File getResourcesLocation() {
        return getAsFile(resourcesTextField.getText()).getAbsoluteFile();
    }
    
    private void setSrcPackages(final File file) {
        srcPackagesLocation = file;
    }
    
    protected File getSrcPackagesLocation() {
        return srcPackagesLocation;
    }
    
    private String relativizeFile(final File file) {
        String filePath = FileUtil.normalizeFile(file).getAbsolutePath();
        String parentPath = FileUtil.normalizeFile(baseFolder).getAbsolutePath() + File.pathSeparator;
        return PropertyUtils.relativizeFile(baseFolder, FileUtil.normalizeFile(file));
    }
    
    private String getRelativeLocation(final File location) {
        final File normalizedLocation = FileUtil.normalizeFile(location);
        return Util.relativizeLocation(baseFolder, nbProjectFolder, normalizedLocation);
    }
    
    public static class Panel implements ProjectPropertiesPanel {
        
        private Project project;
        private AntProjectHelper projectHelper;
        private PropertyEvaluator projectEvaluator;
        private AuxiliaryConfiguration aux;
        private EJBLocationsPanel panel;
        
        public Panel(Project project, AntProjectHelper projectHelper, PropertyEvaluator projectEvaluator, AuxiliaryConfiguration aux) {
            this.project = project;
            this.projectHelper = projectHelper;
            this.projectEvaluator = projectEvaluator;
            this.aux = aux;
        }
        
        public void storeValues() {
            if (panel == null) {
                return;
            }
            AuxiliaryConfiguration aux = Util.getAuxiliaryConfiguration(projectHelper);
            EJBProjectGenerator.putEJBModules(projectHelper, aux, panel.getEJBModules());
            EJBProjectGenerator.putServerID(projectHelper, panel.getSelectedServerID());
            EJBProjectGenerator.putResourceFolder(projectHelper, panel.getResourcesFolder());
            EJBProjectGenerator.putJ2EELevel(projectHelper, ((EJBProjectGenerator.EJBModule)panel.getEJBModules().get(0)).j2eeSpecLevel);
        }
        
        public String getDisplayName() {
            return NbBundle.getMessage(EJBLocationsPanel.class, "LBL_ProjectCustomizer_Category_EJB");
        }
        
        public JComponent getComponent() {
            if (panel == null) {
                panel = new EJBLocationsPanel(null, project, projectHelper, projectEvaluator, aux);
            }
            return panel;
        }
        
        public int getPreferredPosition() {
            return 40; // before Java sources panel
        }
    }
    
    private void initServerInstances() {
        String[] servInstIDs = Deployment.getDefault().getServerInstanceIDs();
        serverIDs = new ArrayList();
        serverIDs.add("GENERIC"); // NOI18N
        serverTypeComboBox.addItem(Deployment.getDefault().getServerDisplayName("GENERIC")); // NOI18N
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
        return item == null ? null: item.equals(J2EE_SPEC_14_LABEL) ? J2eeModule.J2EE_14 : J2eeModule.J2EE_13;
    }
    
    public boolean valid(WizardDescriptor wizardDescriptor) {
        File cfLoc = getConfigFilesLocation();
        if (!cfLoc.isDirectory()) {
            return false;
        }
        File[] dds = getConfigFilesLocation().listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.equals("ejb-jar.xml");
            }
        });
        if (dds.length == 0) {
            wizardDescriptor.putProperty("WizardPanel_errorMessage", NbBundle.getMessage(EJBLocationsPanel.class,"MSG_NoEjbJarXml")); //NOI18N
            return false;
        }

        wizardDescriptor.putProperty("WizardPanel_errorMessage", ""); //NOI18N
        return true;
    }
}
