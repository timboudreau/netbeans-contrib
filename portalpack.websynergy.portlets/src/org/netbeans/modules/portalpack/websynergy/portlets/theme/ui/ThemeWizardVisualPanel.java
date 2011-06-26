/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2009 Sun Microsystems, Inc. All rights reserved.
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
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2009 Sun Microsystems, Inc.
 */

package org.netbeans.modules.portalpack.websynergy.portlets.theme.ui;

import java.io.File;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import org.netbeans.api.project.Project;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.actions.util.PortletProjectUtils;
import org.netbeans.modules.web.api.webmodule.WebModule;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbBundle;

public final class ThemeWizardVisualPanel extends JPanel implements DocumentListener {

    private ThemeWizardWizardPanel wizardPanel;
    private Project project;
    
    /** Creates new form ThemeWizardVisualPanel1 */
    public ThemeWizardVisualPanel(ThemeWizardWizardPanel wizardPanel,Project project) {
        initComponents();
        this.wizardPanel = wizardPanel;
        this.project = project;
        initData();
        themeIDTextField.getDocument().addDocumentListener(this);
        themeNameTextField.getDocument().addDocumentListener(this);
        folderTextField.getDocument().addDocumentListener(this);
        locationTextField.getDocument().addDocumentListener(this);
    }

    @Override
    public String getName() {
        return "Provide Theme Details";
    }

    public void readSettings(WizardDescriptor wizardDescriptor) {
    }

    public void writeSettings(WizardDescriptor wizardDescriptor) {
        wizardDescriptor.putProperty("themeId", themeIDTextField.getText());
        wizardDescriptor.putProperty("themeName", themeNameTextField.getText());
        wizardDescriptor.putProperty("themeFolder", folderTextField.getText());
        wizardDescriptor.putProperty("themeDir", locationTextField.getText());

    }

    public boolean isValid(WizardDescriptor wizard) {
        FileObject themeFolderFO = null;
        String themeId = themeIDTextField.getText();
        String themeName = themeNameTextField.getText();
        String themeFolder = folderTextField.getText();
        WebModule wm = PortletProjectUtils.getWebModule(project);
        FileObject docBase = wm.getDocumentBase();
        if (themeFolder != null && themeFolder.trim().length() > 0) {
            themeFolderFO = docBase.getFileObject(themeFolder, null);
        }
        if (themeId != null && themeId.trim().length() > 0
                && !validateString(themeId, Boolean.FALSE)) {
            wizard.putProperty("WizardPanel_errorMessage", NbBundle.getMessage(ThemeWizardVisualPanel.class, "INVALID_THEME_ID"));
            return false;
        } else if (themeName != null && themeName.trim().length() > 0
                && !validateString(themeName, Boolean.TRUE)) {
            wizard.putProperty("WizardPanel_errorMessage", NbBundle.getMessage(ThemeWizardVisualPanel.class, "INVALID_THEME_NAME"));
            return false;
        } else if (themeFolderFO != null) {
            wizard.putProperty("WizardPanel_errorMessage", NbBundle.getMessage(ThemeWizardVisualPanel.class, "THEME_FOLDER_ALREADY_EXISTS"));
            return false;
        } else if (themeFolder != null && themeFolder.trim().length() > 0
                && !validateString(themeFolder, Boolean.FALSE)) {
            wizard.putProperty("WizardPanel_errorMessage", NbBundle.getMessage(ThemeWizardVisualPanel.class, "INVALID_THEME_FOLDER_NAME"));
            return false;
        }

        wizard.putProperty("WizardPanel_errorMessage", "");
        return true;
    }

    /**
     *
     * @param name
     * @param allowSpaces
     * @return
     */
    public static boolean validateString(String name, boolean allowSpaces) {
        if(name == null || name.trim().length() == 0){
            return false;
        }
        String value = name.trim();
        for(int i=0; i<value.length(); i++) {
            char c = value.charAt(i);
            if(!Character.isLetterOrDigit(c) && !((c == '_') || (c == '-') || (allowSpaces && c == ' '))){
                return false;
            }
        }
        return true;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        themeIDTextField = new javax.swing.JTextField();
        themeNameTextField = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        folderTextField = new javax.swing.JTextField();
        locationTextField = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();

        setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(ThemeWizardVisualPanel.class, "ThemeWizardVisualPanel.border.title"))); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(ThemeWizardVisualPanel.class, "ThemeWizardVisualPanel.jLabel1.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(ThemeWizardVisualPanel.class, "ThemeWizardVisualPanel.jLabel2.text")); // NOI18N

        themeIDTextField.setText(org.openide.util.NbBundle.getMessage(ThemeWizardVisualPanel.class, "ThemeWizardVisualPanel.themeIDTextField.text")); // NOI18N
        themeIDTextField.setToolTipText(org.openide.util.NbBundle.getMessage(ThemeWizardVisualPanel.class, "ThemeWizardVisualPanel.themeIDTextField.toolTipText")); // NOI18N

        themeNameTextField.setText(org.openide.util.NbBundle.getMessage(ThemeWizardVisualPanel.class, "ThemeWizardVisualPanel.themeNameTextField.text")); // NOI18N
        themeNameTextField.setToolTipText(org.openide.util.NbBundle.getMessage(ThemeWizardVisualPanel.class, "ThemeWizardVisualPanel.themeNameTextField.toolTipText")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getMessage(ThemeWizardVisualPanel.class, "ThemeWizardVisualPanel.jLabel3.text")); // NOI18N

        folderTextField.setText(org.openide.util.NbBundle.getMessage(ThemeWizardVisualPanel.class, "ThemeWizardVisualPanel.folderTextField.text")); // NOI18N
        folderTextField.setToolTipText(org.openide.util.NbBundle.getMessage(ThemeWizardVisualPanel.class, "ThemeWizardVisualPanel.folderTextField.toolTipText")); // NOI18N

        locationTextField.setEditable(false);
        locationTextField.setText(org.openide.util.NbBundle.getMessage(ThemeWizardVisualPanel.class, "ThemeWizardVisualPanel.locationTextField.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel4, org.openide.util.NbBundle.getMessage(ThemeWizardVisualPanel.class, "ThemeWizardVisualPanel.jLabel4.text")); // NOI18N

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 66, Short.MAX_VALUE)
                            .add(jLabel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .add(405, 405, 405))
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(layout.createSequentialGroup()
                                .add(jLabel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 74, Short.MAX_VALUE)
                                .add(5, 5, 5))
                            .add(jLabel4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .add(10, 10, 10)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(locationTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 382, Short.MAX_VALUE)
                            .add(layout.createSequentialGroup()
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                                    .add(org.jdesktop.layout.GroupLayout.LEADING, folderTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)
                                    .add(org.jdesktop.layout.GroupLayout.LEADING, themeNameTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)
                                    .add(org.jdesktop.layout.GroupLayout.LEADING, themeIDTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 170, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                .add(212, 212, 212)))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(themeIDTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 17, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(themeNameTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(8, 8, 8)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel3)
                    .add(folderTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(8, 8, 8)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(locationTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel4))
                .addContainerGap(19, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField folderTextField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JTextField locationTextField;
    private javax.swing.JTextField themeIDTextField;
    private javax.swing.JTextField themeNameTextField;
    // End of variables declaration//GEN-END:variables

    private void initData() {
        WebModule wm = PortletProjectUtils.getWebModule(project);

        locationTextField.setText(FileUtil.toFile(wm.getDocumentBase()).getAbsolutePath() + File.separatorChar);
    }

    public void insertUpdate(DocumentEvent e) {
        updateTexts(e);
    }

    public void removeUpdate(DocumentEvent e) {
        updateTexts(e);
    }

    public void changedUpdate(DocumentEvent e) {
        updateTexts(e);
    }

   
    /** Handles changes in the Project name and project directory, */
    private void updateTexts(DocumentEvent e) {
        WebModule wm = PortletProjectUtils.getWebModule(project);
        Document doc = e.getDocument();
        if (doc == folderTextField.getDocument()) {
            String  folderName = folderTextField.getText();
            locationTextField.setText(wm.getDocumentBase().getPath()
                    + File.separatorChar + folderName);
        }
        fireChange();

    }

    private void fireChange() {
        wizardPanel.fireChangeEvent();
    }
}

