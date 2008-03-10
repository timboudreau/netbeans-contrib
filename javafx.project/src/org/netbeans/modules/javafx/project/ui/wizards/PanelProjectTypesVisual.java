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

package org.netbeans.modules.javafx.project.ui.wizards;

import java.io.File;
import javax.swing.JFileChooser;
import org.netbeans.modules.javafx.project.ui.wizards.PanelSourceFolders;
import org.netbeans.modules.javafx.project.ui.wizards.SettingsPanel;
import org.netbeans.spi.project.ui.templates.support.Templates;
import org.openide.WizardDescriptor;
import org.openide.WizardValidationException;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbBundle;

/**
 *
 * @author  answer
 */
public class PanelProjectTypesVisual extends SettingsPanel {
    
    private WizardDescriptor wizardDescriptor;
    
    private PanelOptionsVisual optionsPanel;
    
    /** Creates new form PanelProjectTypesVisual */
    public PanelProjectTypesVisual(PanelOptionsVisual optionsPanel) {
        initComponents();
        
        // temporary disabled while this type of project not supported
        jRadioButton3.setVisible(false);
        jLabel1.setVisible(false);
        projectLocation.setVisible(false);
        browseButton.setVisible(false);
        
        this.optionsPanel = optionsPanel;
        ((FolderList)sourcePanel).setComponentsEnabled(false);
        projectLocation.setEnabled(false);
        browseButton.setEnabled(false);
    }
    
    boolean valid(WizardDescriptor wd) {
        return true; //TODO
    }
    
    void read (WizardDescriptor wd) {
        this.wizardDescriptor = wd;
        File projectLocation = (File) wd.getProperty ("projdir");         //NOI18N
        ((FolderList)this.sourcePanel).setProjectFolder(projectLocation);
        File[] srcRoot = (File[]) wd.getProperty ("sourceRoot");          //NOI18N
        assert srcRoot != null : "sourceRoot property must be initialized!" ;   //NOI18N
        ((FolderList)this.sourcePanel).setFiles(srcRoot);

        File currentDirectory = null;
        FileObject folder = Templates.getExistingSourcesFolder(wizardDescriptor);
        if (folder != null) {
            currentDirectory = FileUtil.toFile(folder);
        }        
        if (currentDirectory != null && currentDirectory.isDirectory()) {       
            ((FolderList)sourcePanel).setLastUsedDir(currentDirectory);
        }
        
        switch ((NewJavaFXProjectWizardIterator.WizardType)wd.getProperty("projectType")) {
        case APP:
            jRadioButton1.setSelected(true);
            break;
        case EXT:
            jRadioButton2.setSelected(true);
            break;
        }
    }
    
    void validate (WizardDescriptor wd) throws WizardValidationException {
        // nothing to validate
    }

    void store( WizardDescriptor wd) {
        File[] sourceRoots = ((FolderList)this.sourcePanel).getFiles();
        wd.putProperty ("sourceRoot",sourceRoots);    //NOI18N
        
        NewJavaFXProjectWizardIterator.WizardType projectType;
        if (jRadioButton1.isSelected()) projectType = NewJavaFXProjectWizardIterator.WizardType.APP;
            else projectType = NewJavaFXProjectWizardIterator.WizardType.EXT;
        wd.putProperty("projectType", projectType);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        buttonGroup1 = new javax.swing.ButtonGroup();
        jRadioButton1 = new javax.swing.JRadioButton();
        jRadioButton2 = new javax.swing.JRadioButton();
        jRadioButton3 = new javax.swing.JRadioButton();
        projectLocation = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        browseButton = new javax.swing.JButton();
        sourcePanel = new FolderList (NbBundle.getMessage(PanelProjectTypesVisual.class,"CTL_SourceRoots"), NbBundle.getMessage(PanelProjectTypesVisual.class,"MNE_SourceRoots").charAt(0),NbBundle.getMessage(PanelProjectTypesVisual.class,"AD_SourceRoots"), NbBundle.getMessage(PanelProjectTypesVisual.class,"CTL_AddSourceRoot"),
            NbBundle.getMessage(PanelProjectTypesVisual.class,"MNE_AddSourceFolder").charAt(0), NbBundle.getMessage(PanelProjectTypesVisual.class,"AD_AddSourceFolder"),NbBundle.getMessage(PanelProjectTypesVisual.class,"MNE_RemoveSourceFolder").charAt(0), NbBundle.getMessage(PanelProjectTypesVisual.class,"AD_RemoveSourceFolder"));

        setLayout(new java.awt.GridBagLayout());

        buttonGroup1.add(jRadioButton1);
        jRadioButton1.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(jRadioButton1, org.openide.util.NbBundle.getMessage(PanelProjectTypesVisual.class, "PanelProjectTypesVisual.jRadioButton1.text")); // NOI18N
        jRadioButton1.setActionCommand(org.openide.util.NbBundle.getMessage(PanelProjectTypesVisual.class, "PanelProjectTypesVisual.jRadioButton1.actionCommand")); // NOI18N
        jRadioButton1.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jRadioButton1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jRadioButton1.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jRadioButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                projectTypeChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.RELATIVE;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        add(jRadioButton1, gridBagConstraints);

        buttonGroup1.add(jRadioButton2);
        org.openide.awt.Mnemonics.setLocalizedText(jRadioButton2, org.openide.util.NbBundle.getMessage(PanelProjectTypesVisual.class, "PanelProjectTypesVisual.jRadioButton2.text")); // NOI18N
        jRadioButton2.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jRadioButton2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jRadioButton2.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jRadioButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                projectTypeChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.RELATIVE;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        add(jRadioButton2, gridBagConstraints);

        buttonGroup1.add(jRadioButton3);
        org.openide.awt.Mnemonics.setLocalizedText(jRadioButton3, org.openide.util.NbBundle.getMessage(PanelProjectTypesVisual.class, "PanelProjectTypesVisual.jRadioButton3.text")); // NOI18N
        jRadioButton3.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jRadioButton3.setEnabled(false);
        jRadioButton3.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jRadioButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                projectTypeChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.RELATIVE;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        add(jRadioButton3, gridBagConstraints);

        projectLocation.setText(org.openide.util.NbBundle.getMessage(PanelProjectTypesVisual.class, "PanelProjectTypesVisual.projectLocation.text")); // NOI18N
        projectLocation.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.RELATIVE;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        add(projectLocation, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(PanelProjectTypesVisual.class, "PanelProjectTypesVisual.jLabel1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 16, 0, 0);
        add(jLabel1, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(browseButton, org.openide.util.NbBundle.getMessage(PanelProjectTypesVisual.class, "PanelProjectTypesVisual.browseButton.text")); // NOI18N
        browseButton.setActionCommand(org.openide.util.NbBundle.getMessage(PanelProjectTypesVisual.class, "PanelProjectTypesVisual.browseButton.actionCommand")); // NOI18N
        browseButton.setEnabled(false);
        browseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseProjectLocation(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        add(browseButton, gridBagConstraints);
        browseButton.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(PanelProjectTypesVisual.class, "PanelProjectTypesVisual.browseButton.AccessibleContext.accessibleName")); // NOI18N

        sourcePanel.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 16, 0, 0);
        add(sourcePanel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

private void projectTypeChanged(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_projectTypeChanged
    ((FolderList)sourcePanel).setComponentsEnabled(jRadioButton2.isSelected());
    projectLocation.setEnabled(jRadioButton3.isSelected());
    browseButton.setEnabled(jRadioButton3.isSelected());
    optionsPanel.enableMainClass(jRadioButton1.isSelected());
}//GEN-LAST:event_projectTypeChanged

    private void browseProjectLocation(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseProjectLocation
        JFileChooser chooser = new JFileChooser();
        FileUtil.preventFileChooserSymlinkTraversal(chooser, null);
        chooser.setDialogTitle(NbBundle.getMessage(PanelSourceFolders.class,"LBL_NWP1_SelectProjectLocation"));
        chooser.setFileSelectionMode (JFileChooser.DIRECTORIES_ONLY);
        String path = this.projectLocation.getText();
        if (path.length() > 0) {
            File f = new File (path);
            if (f.exists()) {
                chooser.setSelectedFile (f);
            }
        }
        if (chooser.showOpenDialog(this)== JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            if (file != null) {
                this.projectLocation.setText (FileUtil.normalizeFile(file).getAbsolutePath());
            }
        }
}//GEN-LAST:event_browseProjectLocation
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton browseButton;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JRadioButton jRadioButton3;
    private javax.swing.JTextField projectLocation;
    private javax.swing.JPanel sourcePanel;
    // End of variables declaration//GEN-END:variables
    
}
