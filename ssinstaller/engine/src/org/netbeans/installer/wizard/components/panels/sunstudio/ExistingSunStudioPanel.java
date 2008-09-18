/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 * 
 * The contents of this file are subject to the terms of either the GNU General
 * Public License Version 2 only ("GPL") or the Common Development and Distribution
 * License("CDDL") (collectively, the "License"). You may not use this file except in
 * compliance with the License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html or nbbuild/licenses/CDDL-GPL-2-CP. See the
 * License for the specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header Notice in
 * each file and include the License file at nbbuild/licenses/CDDL-GPL-2-CP.  Sun
 * designates this particular file as subject to the "Classpath" exception as
 * provided by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the License Header,
 * with the fields enclosed by brackets [] replaced by your own identifying
 * information: "Portions Copyrighted [year] [name of copyright owner]"
 * 
 * Contributor(s):
 * 
 * The Original Software is NetBeans. The Initial Developer of the Original Software
 * is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun Microsystems, Inc. All
 * Rights Reserved.
 * 
 * If you wish your version of this file to be governed by only the CDDL or only the
 * GPL Version 2, indicate your decision by adding "[Contributor] elects to include
 * this software in this distribution under the [CDDL or GPL Version 2] license." If
 * you do not indicate a single choice of license, a recipient has the option to
 * distribute your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above. However, if
 * you add GPL Version 2 code and therefore, elected the GPL Version 2 license, then
 * the option applies only if the new code is made subject to such option by the
 * copyright holder.
 */

package org.netbeans.installer.wizard.components.panels.sunstudio;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.JSeparator;
import org.netbeans.installer.utils.ResourceUtils;
import org.netbeans.installer.utils.StringUtils;
import org.netbeans.installer.utils.env.ExistingSunStudioChecker;
import org.netbeans.installer.utils.helper.swing.NbiButton;
import org.netbeans.installer.utils.helper.swing.NbiDialog;
import org.netbeans.installer.utils.helper.swing.NbiFrame;
import org.netbeans.installer.utils.helper.swing.NbiLabel;
import org.netbeans.installer.utils.helper.swing.NbiPanel;
import org.netbeans.installer.utils.helper.swing.NbiTextPane;
import org.netbeans.installer.wizard.components.panels.ErrorMessagePanel;
import org.netbeans.installer.wizard.components.panels.ErrorMessagePanel.ErrorMessagePanelSwingUi;
import org.netbeans.installer.wizard.containers.SwingContainer;
import org.netbeans.installer.wizard.ui.SwingUi;
import org.netbeans.installer.wizard.ui.WizardUi;


public class ExistingSunStudioPanel extends ErrorMessagePanel {
    
    public static final String DEFAULT_TITLE = ResourceUtils.getString(ExistingSunStudioPanel.class, "ESSP.title"); // NOI18N
    public static final String DEFAULT_DESCRIPTION = ResourceUtils.getString(ExistingSunStudioPanel.class, "ESSP.description"); // NOI18N
    public static final String MORE_INFO_BUTTON_TEXT = ResourceUtils.getString(ExistingSunStudioPanel.class, "ESSP.more.info.button.text"); // NOI18N
    public static final String CLOSE_BUTTON_TEXT = ResourceUtils.getString(ExistingSunStudioPanel.class, "ESSP.close.button.text"); // NOI18N
    public static final String GET_LIST_BUTTON_TEXT = ResourceUtils.getString(ExistingSunStudioPanel.class, "ESSP.get.list.button.text"); // NOI18N
    public static final String ALREADY_INSTALLED_TEXT = ResourceUtils.getString(ExistingSunStudioPanel.class, "ESSP.already.installed.text"); // NOI18N
    public static final String COULD_NOT_BE_USED_TEXT = ResourceUtils.getString(ExistingSunStudioPanel.class, "ESSP.directories.not.used.installed.text"); // NOI18N
    public static final String NOT_POSSIBLE_TEXT = ResourceUtils.getString(ExistingSunStudioPanel.class, "ESSP.installation.not.possible.text"); // NOI18N
    public static final String ONLY_THIS_DIRECTORY_USED_TEXT = ResourceUtils.getString(ExistingSunStudioPanel.class, "ESSP.only.directory.used.installed.text"); // NOI18N
    public static final String LIST_INSTALLED_PACKAGES_TEXT = ResourceUtils.getString(ExistingSunStudioPanel.class, "ESSP.list.packages.text"); // NOI18N

    /////////////////////////////////////////////////////////////////////////////////
    // Instance
    public ExistingSunStudioPanel() {}
    
    @Override
    public WizardUi getWizardUi() {
        if (wizardUi == null) {
            wizardUi = new ExistingSunStudioPanelUi(this);
        }
        
        return wizardUi;
    }

    @Override
    public void initialize() {
        setProperty(TITLE_PROPERTY, DEFAULT_TITLE);
        setProperty(DESCRIPTION_PROPERTY, DEFAULT_DESCRIPTION);
    }

    @Override
    public boolean canExecuteForward() {
        return true;
    }



    /////////////////////////////////////////////////////////////////////////////////
    // Inner Classes
    public static class ExistingSunStudioPanelUi extends ErrorMessagePanelUi {
        protected ExistingSunStudioPanel component;
        
        public ExistingSunStudioPanelUi(ExistingSunStudioPanel component) {
            super(component);
            
            this.component = component;
        }
        
        @Override
        public SwingUi getSwingUi(SwingContainer container) {
            if (swingUi == null) {
                swingUi = new ExistingSunStudioPanelSwingUi(component, container);
            }
            
            return super.getSwingUi(container);
        }
    }
    
    public static class ExistingSunStudioPanelSwingUi extends ErrorMessagePanelSwingUi {
        protected ExistingSunStudioPanel component;
        
        ExistingSunStudioChecker checker = ExistingSunStudioChecker.getInstance();
        
        public ExistingSunStudioPanelSwingUi(
                final ExistingSunStudioPanel component,
                final SwingContainer container) {
            super(component, container);
            
            this.component = component;
            initComponents();
        }
        
        // protected ////////////////////////////////////////////////////////////////
        @Override
        protected void initializeContainer() {
            super.initializeContainer();            
            container.getNextButton().setText(
                    panel.getProperty(NEXT_BUTTON_TEXT_PROPERTY));
            if (!checker.isInstallationPossible()) {
                container.getNextButton().setVisible(false);
                container.getBackButton().setVisible(false);
                container.getCancelButton().setText(component.getProperty(FINISH_BUTTON_TEXT_PROPERTY));
            }
        }
        
        // private //////////////////////////////////////////////////////////////////
        ConflictedPackagesDialog conflictedPackagesDialog;
        private void initComponents() {
            //List<SSInstallationInfo> infoList = new ArrayList<SSInstallationInfo>();
            this.setLayout(new GridBagLayout());
            GridBagConstraints constraints = new GridBagConstraints();
            constraints.anchor = GridBagConstraints.PAGE_START;
            constraints.fill = GridBagConstraints.HORIZONTAL;
            constraints.insets = new Insets(5, 5, 5, 5);
            constraints.gridy = 0;
            constraints.gridx = 0;
            constraints.gridheight = 1;
            constraints.gridheight = 1;
            constraints.weightx = 1.0;
       //     constraints.weighty = 0.5;
            
            for(String version : checker.getInstalledVersions()) {            
                SSInstallationInfo info = new SSInstallationInfo(version);
                //infoList.add(info);
                this.add(info, constraints);
                constraints.gridy ++;
            }
            constraints.weighty = 1.0;
            this.add(new JSeparator(), constraints);
            conflictedPackagesDialog = new ConflictedPackagesDialog();
        }
        
        @Override
        public void evaluateCancelButtonClick() {
            if (!checker.isInstallationPossible()) {
                component.getWizard().getFinishHandler().cancel();
            } else {
                super.evaluateCancelButtonClick();
            }
        }

        private class SSInstallationInfo extends NbiPanel {
            NbiLabel descriptionLabel;
            NbiLabel locationsLabel;
            NbiLabel resolutionLabel;
            NbiButton getListButton;            

     
            String version;
            
            public SSInstallationInfo(String version) {
                this.version = version;
                initComponents();
            }

            void initComponents() {
                this.setBorder(javax.swing.BorderFactory.createEtchedBorder());
                descriptionLabel = new NbiLabel();
                locationsLabel = new NbiLabel();
                resolutionLabel = new NbiLabel();
                getListButton = new NbiButton();

                GridBagConstraints constraints = new GridBagConstraints();
                constraints.gridx = 0;
                constraints.gridy = 0;
                constraints.gridwidth = 1;
                constraints.fill = GridBagConstraints.HORIZONTAL;
                constraints.anchor = GridBagConstraints.NORTH;
                constraints.weightx = 1;
                constraints.weighty = 1;
                constraints.insets = new Insets(5, 5, 5, 5);
                
                descriptionLabel.setText(StringUtils.format(ALREADY_INSTALLED_TEXT, version)); 
                Font bf = descriptionLabel.getFont(); 
                descriptionLabel.setFont(bf.deriveFont(Font.BOLD, bf.getSize2D()));
                this.add(descriptionLabel, constraints);
                
                locationsLabel.setText(StringUtils.asString(checker.getBaseDirsForVersion(version), ", "));
                constraints.gridx = 1;
                this.add(locationsLabel, constraints);
                String text = COULD_NOT_BE_USED_TEXT;
                if (checker.getResolutionForVersion(version) == ExistingSunStudioChecker.INSTALLATION_BLOCKED) {
                    text = NOT_POSSIBLE_TEXT;
                }
                if (checker.getResolutionForVersion(version) == ExistingSunStudioChecker.ONLY_THIS_LOCATION_USED) {
                    text = ONLY_THIS_DIRECTORY_USED_TEXT;
                }
                resolutionLabel.setText(text);
                constraints.gridy = 1;
                constraints.gridx = 0;
                constraints.weightx = 2;
                constraints.gridwidth = GridBagConstraints.REMAINDER;
                bf = resolutionLabel.getFont(); 
                resolutionLabel.setFont(bf.deriveFont(Font.BOLD, bf.getSize2D()));
                this.add(resolutionLabel, constraints);
                
                getListButton.setText(GET_LIST_BUTTON_TEXT);
                constraints.gridy = 2;
                constraints.gridx = 0;
                constraints.anchor = GridBagConstraints.WEST;
                constraints.weightx = 1;
                constraints.gridwidth = 1;
                constraints.fill = GridBagConstraints.NONE;
                getListButton.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        conflictedPackagesDialog.show(version, checker.getPackagesForVersion(version));
                    }
                });

                this.add(getListButton, constraints);
            }

        }

    }
    
    public static class ConflictedPackagesDialog extends NbiDialog {        
        
        private NbiButton okButton;
        private NbiPanel buttonsPanel;
        private NbiPanel componentPanel;
        private NbiLabel header;
        private NbiTextPane descriptionPane;
        
        public ConflictedPackagesDialog() {
            super();
            initComponents();
            setModal(true);
        }
        
        private void initComponents() {
            descriptionPane = new NbiTextPane();
            okButton = new NbiButton();
            okButton.setText(CLOSE_BUTTON_TEXT);
            okButton.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    setVisible(false);
                }
            });
            componentPanel = new NbiPanel();
            header = new NbiLabel();            
            componentPanel.setLayout(new BorderLayout(6, 0));
            componentPanel.add(header, BorderLayout.NORTH);
            componentPanel.add(descriptionPane, BorderLayout.CENTER);
            buttonsPanel = new NbiPanel();
            buttonsPanel.add(okButton, new GridBagConstraints(
                    0, 0, // x, y
                    1, 1, // width, height
                    1.0, 0.0, // weight-x, weight-y
                    GridBagConstraints.CENTER, // anchor
                    GridBagConstraints.NONE, // fill
                    new Insets(0, 0, 0, 0), // padding
                    0, 0));                           // padx, pady - ???)
            getContentPane().add(componentPanel, new GridBagConstraints(
                    0, 0, // x, y
                    1, 1, // width, height
                    1.0, 1.0, // weight-x, weight-y
                    GridBagConstraints.PAGE_START, // anchor
                    GridBagConstraints.BOTH, // fill
                    new Insets(6, 11, 0, 11), // padding
                    0, 0));                           // padx, pady - ???
            getContentPane().add(buttonsPanel, new GridBagConstraints(
                    0, 1, // x, y
                    1, 1, // width, height
                    1.0, 0.0, // weight-x, weight-y
                    GridBagConstraints.PAGE_END, // anchor
                    GridBagConstraints.BOTH, // fill
                    new Insets(6, 11, 0, 11), // padding
                    0, 0));                           // padx, pady - ???
        }
                

        
        public void show(String version, List<String> names) {
            descriptionPane.setText(StringUtils.asString(names, "\n"));
            header.setText(StringUtils.format(LIST_INSTALLED_PACKAGES_TEXT, version));
            setVisible(true);
            requestFocus();
        }                
        
    }
    
    
}
