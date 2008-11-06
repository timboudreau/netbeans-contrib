/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */
package org.netbeans.modules.ada.project.ui.wizards;

import org.netbeans.modules.ada.project.ui.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.StringTokenizer;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import org.netbeans.api.ada.platform.AdaPlatform;
import org.netbeans.api.ada.platform.AdaPlatformManager;
import org.openide.WizardDescriptor;
import org.openide.WizardValidationException;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.Repository;
import org.openide.loaders.DataObject;
import org.openide.loaders.InstanceDataObject;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.util.actions.CallableSystemAction;

/**
 * 
 * @author  Andrea Lucarelli
 */
public class PanelOptionsVisual extends SettingsPanel implements ActionListener, PropertyChangeListener {

    private static boolean lastMainClassCheck = true; // XXX Store somewhere    
    private PanelConfigureProject panel;
    private boolean valid;
    private String projectLocation;

    public PanelOptionsVisual(PanelConfigureProject panel, NewAdaProjectWizardIterator.WizardType type) {
        initComponents();
        this.panel = panel;
        this.platforms.setRenderer(Utils.createPlatformRenderer());
        this.platforms.setModel(Utils.createPlatformModel());

        switch (type) {
            case NEW:
                createMainCheckBox.addActionListener(this);
                createMainCheckBox.setSelected(lastMainClassCheck);
                mainFileTextField.setEnabled(lastMainClassCheck);
                break;

            case EXISTING:
                setAsMainCheckBox.setVisible(true);
                createMainCheckBox.setVisible(false);
                mainFileTextField.setVisible(false);
                break;
        }

        this.mainFileTextField.getDocument().addDocumentListener(new DocumentListener() {

            public void insertUpdate(DocumentEvent e) {
                mainFileChanged();
            }

            public void removeUpdate(DocumentEvent e) {
                mainFileChanged();
            }

            public void changedUpdate(DocumentEvent e) {
                mainFileChanged();
            }
        });

    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == createMainCheckBox) {
            lastMainClassCheck = createMainCheckBox.isSelected();
            mainFileTextField.setEnabled(lastMainClassCheck);
            this.panel.fireChangeEvent();
        }
    }

    public void propertyChange(PropertyChangeEvent event) {
        if (NewAdaProjectWizardIterator.PROP_PROJECT_NAME.equals(event.getPropertyName())) {
            String newProjectName = (String) event.getNewValue();
            this.mainFileTextField.setText(MessageFormat.format(
                    NbBundle.getMessage(PanelOptionsVisual.class, "TXT_MainFileName"), new Object[]{newProjectName}));
        }
        if (NewAdaProjectWizardIterator.PROP_PROJECT_LOCATION.equals(event.getPropertyName())) {
            projectLocation = (String) event.getNewValue();
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        createMainCheckBox = new javax.swing.JCheckBox();
        mainFileTextField = new javax.swing.JTextField();
        setAsMainCheckBox = new javax.swing.JCheckBox();
        jLabel1 = new javax.swing.JLabel();
        platforms = new javax.swing.JComboBox();
        manage = new javax.swing.JButton();

        createMainCheckBox.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(createMainCheckBox, org.openide.util.NbBundle.getBundle(PanelOptionsVisual.class).getString("LBL_createMainCheckBox")); // NOI18N
        createMainCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));

        mainFileTextField.setText("Main");

        setAsMainCheckBox.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(setAsMainCheckBox, org.openide.util.NbBundle.getBundle(PanelOptionsVisual.class).getString("LBL_setAsMainCheckBox")); // NOI18N
        setAsMainCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));

        jLabel1.setLabelFor(platforms);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(PanelOptionsVisual.class, "TXT_AdaPlatform")); // NOI18N

        platforms.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        org.openide.awt.Mnemonics.setLocalizedText(manage, org.openide.util.NbBundle.getMessage(PanelOptionsVisual.class, "TXT_ManagePlatfroms")); // NOI18N
        manage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                manageActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(setAsMainCheckBox)
                .addContainerGap(502, Short.MAX_VALUE))
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(createMainCheckBox)
                    .add(jLabel1))
                .add(18, 18, 18)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(platforms, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 233, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(manage)
                        .add(4, 4, 4))
                    .add(mainFileTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 498, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(setAsMainCheckBox)
                .add(5, 5, 5)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(createMainCheckBox)
                    .add(mainFileTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(jLabel1)
                        .add(platforms, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(manage))
                .addContainerGap(50, Short.MAX_VALUE))
        );

        createMainCheckBox.getAccessibleContext().setAccessibleName("null");
        createMainCheckBox.getAccessibleContext().setAccessibleDescription("null");
        mainFileTextField.getAccessibleContext().setAccessibleName("null");
        mainFileTextField.getAccessibleContext().setAccessibleDescription("null");
        setAsMainCheckBox.getAccessibleContext().setAccessibleName("null");
        setAsMainCheckBox.getAccessibleContext().setAccessibleDescription("null");

        getAccessibleContext().setAccessibleName("null");
        getAccessibleContext().setAccessibleDescription("null");
    }// </editor-fold>//GEN-END:initComponents

private void manageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_manageActionPerformed
    // Workaround, Needs an API to display platform customizer
    final FileObject fo = Repository.getDefault().getDefaultFileSystem().findResource("Actions/Ada/org-netbeans-modules-ada-platform-AdaPlatformAction.instance");  //NOI18N
    if (fo != null) {
        try {
            InstanceDataObject ido = (InstanceDataObject) DataObject.find(fo);
            CallableSystemAction action = (CallableSystemAction) ido.instanceCreate();
            action.performAction();
            platforms.setModel(Utils.createPlatformModel()); //Currentl the AdaManager doesn't fire events, we need to replace model.
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        } catch (ClassNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        }
    }
}//GEN-LAST:event_manageActionPerformed

    boolean valid(WizardDescriptor settings) {
        if (platforms.getSelectedItem() == null) {
            settings.putProperty(WizardDescriptor.PROP_ERROR_MESSAGE,
                    NbBundle.getMessage(PanelOptionsVisual.class, "ERROR_IllegalPlatform"));
            return false;
        }
        if (mainFileTextField.isVisible() && mainFileTextField.isEnabled()) {
            if (!valid) {
                settings.putProperty(WizardDescriptor.PROP_ERROR_MESSAGE,
                        NbBundle.getMessage(PanelOptionsVisual.class, "ERROR_IllegalMainFileName")); //NOI18N
            }
            return this.valid;
        } else {
            return true;
        }
    }

    void read(WizardDescriptor d) {
        final AdaPlatformManager manager = AdaPlatformManager.getInstance();
        String pid = (String) d.getProperty(NewAdaProjectWizardIterator.PROP_PLATFORM_ID);
        if (pid == null) {
            pid = manager.getDefaultPlatform();
        }
        final AdaPlatform activePlatform = manager.getPlatform(pid);
        if (activePlatform != null) {
            platforms.setSelectedItem(activePlatform);
        }
    }

    void validate(WizardDescriptor d) throws WizardValidationException {
        // nothing to validate
    }

    void store(WizardDescriptor d) {
        d.putProperty(NewAdaProjectWizardIterator.SET_AS_MAIN, setAsMainCheckBox.isSelected() && setAsMainCheckBox.isVisible() ? Boolean.TRUE : Boolean.FALSE); // NOI18N
        d.putProperty(NewAdaProjectWizardIterator.MAIN_FILE, createMainCheckBox.isSelected() && createMainCheckBox.isVisible() ? mainFileTextField.getText() : null); // NOI18N
        d.putProperty(NewAdaProjectWizardIterator.PROP_PLATFORM_ID, ((AdaPlatform) platforms.getSelectedItem()).getName());
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox createMainCheckBox;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JTextField mainFileTextField;
    private javax.swing.JButton manage;
    private javax.swing.JComboBox platforms;
    private javax.swing.JCheckBox setAsMainCheckBox;
    // End of variables declaration//GEN-END:variables

    private void mainFileChanged() {
        String mainClassName = this.mainFileTextField.getText();
        StringTokenizer tk = new StringTokenizer(mainClassName, "."); //NOI18N
        boolean validity = true;
        while (tk.hasMoreTokens()) {
            String token = tk.nextToken();
            if (token.length() == 0 || !Utilities.isJavaIdentifier(token)) {
                validity = false;
                break;
            }
        }
        this.valid = validity;
        this.panel.fireChangeEvent();
    }

    private void librariesLocationChanged() {
        this.panel.fireChangeEvent();

    }
}

