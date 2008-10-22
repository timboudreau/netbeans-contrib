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

package org.netbeans.installer.products.sunstudio.panels;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.io.File;
import org.netbeans.installer.product.components.Product;
import org.netbeans.installer.utils.LogManager;
import org.netbeans.installer.utils.ResourceUtils;
import org.netbeans.installer.utils.StringUtils;
import org.netbeans.installer.utils.SystemUtils;
import org.netbeans.installer.utils.env.ExistingSunStudioChecker;
import org.netbeans.installer.utils.helper.Platform;
import org.netbeans.installer.utils.helper.swing.NbiCheckBox;
import org.netbeans.installer.wizard.components.panels.DestinationPanel;
import org.netbeans.installer.wizard.components.panels.DestinationPanel.DestinationPanelUi;
import org.netbeans.installer.wizard.ui.SwingUi;
import org.netbeans.installer.wizard.ui.WizardUi;
import org.netbeans.installer.wizard.containers.SwingContainer;
import org.netbeans.installer.wizard.Utils;

public class SSBasePanel extends DestinationPanel {
    
    public SSBasePanel() {
        
        setProperty(TITLE_PROPERTY,
                DEFAULT_TITLE);
        setProperty(DESCRIPTION_PROPERTY,
                DEFAULT_DESCRIPTION);
        
        setProperty(DESTINATION_LABEL_TEXT_PROPERTY,
                DEFAULT_DESTINATION_LABEL_TEXT
                 // while packages are in SUNWspo / sunstudioceres
                + " (" + Utils.getMainDirectory() + " subdirectory will be created.)");
        setProperty(DESTINATION_BUTTON_TEXT_PROPERTY,
                DEFAULT_DESTINATION_BUTTON_TEXT);
          
    }
    
    @Override
    public WizardUi getWizardUi() {
        if (wizardUi == null) {
            wizardUi = new SSBaseDestinationPanelUi(this);
        }        
        return wizardUi;
    }


    @Override
    public void initialize() {
        super.initialize();
	if (!SystemUtils.getCurrentPlatform().isCompatibleWith(Platform.LINUX)) {
            Utils.getSSBase().setProperty(Utils.getSPROsslnkPropertyName(), "true");
	}
    }
        
    /////////////////////////////////////////////////////////////////////////////////
    // Inner Classes
    private static class SSBaseDestinationPanelUi extends DestinationPanelUi {
        protected SSBasePanel panel;
        
        public SSBaseDestinationPanelUi(SSBasePanel panel) {
            super(panel);
            this.panel = panel;
        }
        
        @Override
        public SwingUi getSwingUi(SwingContainer container) {
            if (swingUi == null) {
                swingUi = new SSBaseDestinationPanelSwingUi(panel, container);
            }            
            return super.getSwingUi(container);
        }
    }
    
    private static class SSBaseDestinationPanelSwingUi extends DestinationPanelSwingUi {
       // protected SSBasePanel panel;
        private NbiCheckBox createSymLinks = null;
        /*
        private NbiTextField alternateRoot;
        private NbiButton alternateRootButton;
        private NbiCheckBox copySystemPrequesties;        
        private NbiCheckBox installPatches;
        private NbiCheckBox currentZoneOnly;
        
        private NbiLabel alternateRootLabel;
          */      
        public SSBaseDestinationPanelSwingUi(
                final SSBasePanel panel,
                final SwingContainer container) {
            super(panel, container);            
            this.panel = panel;            
            initComponents();
        }
        
        // protected ////////////////////////////////////////////////////////////////        
        private void initComponents() {
            if (SystemUtils.getCurrentPlatform().isCompatibleWith(Platform.SOLARIS)) {
                createSymLinks = new NbiCheckBox();
                createSymLinks.setText(CREATE_SYMLINKS_CHECKBOX_TEXT);
                createSymLinks.setSelected(Boolean.parseBoolean(
                        component.getWizard().getProperty(CREATE_SYMLINKS_PROPERTY)));
                if (ExistingSunStudioChecker.getInstance().isOnlyLocalInstallationPossible()) {
                    createSymLinks.setSelected(false);
                    createSymLinks.setEnabled(false);
                }
                GridBagConstraints constraints = new GridBagConstraints();
                constraints.gridx = 0;
                constraints.gridy = GridBagConstraints.RELATIVE;
                constraints.anchor = GridBagConstraints.FIRST_LINE_START;
                constraints.insets = new Insets(11, 11, 11, 11);
                
                // for SSX only
                createSymLinks.setSelected(false);
                //add(createSymLinks, constraints);                
            }
        }
        
        @Override
        protected void saveInput() {
            super.saveInput();
            // configure NB prerequisite
            Product nbProduct = Utils.getNBExtra();
          //  nbProduct.setStatus(Status.NOT_INSTALLED);
            String nbLocation =  component.getWizard().getProperty(
                        Product.INSTALLATION_LOCATION_PROPERTY);
            nbLocation = nbLocation + File.separator + Utils.getNBDirectory();
            nbProduct.setInstallationLocation(new File(nbLocation));
            nbProduct.setParent(Utils.getSSBase());                        
            if (createSymLinks != null ) {
                 component.getWizard().setProperty(CREATE_SYMLINKS_PROPERTY,
                         Boolean.toString(createSymLinks.isSelected()));
            }
        }
        
        @Override
        protected String validateInput() {
            String errorMessage = super.validateInput();
            // This hack allows us to install in /opt while "SUNWspro" is a part of 
            // package path
            if (errorMessage != null 
                    && errorMessage.startsWith(component.
                    getProperty(DestinationPanel.ERROR_NOT_EMPTY_PROPERTY))) {
                errorMessage = null;
            }
            ExistingSunStudioChecker checker = ExistingSunStudioChecker.getInstance();
            final File file = new File(getDestinationField().getText().trim());
            if (checker.getAllowedDirectory() != null) {                 
                 if (!file.equals(new File(checker.getAllowedDirectory()))) {
                     errorMessage = StringUtils.format(ONLY_ONE_LOCATION_ALLOWED_TEXT,
                             checker.getAllowedDirectory());
                 }
            }
            for (String dirName : checker.getRestrictedDirectories()) {
                if (file.equals(new File (dirName))) {
                    errorMessage = ALREADY_INSTALLED_LOCATION_TEXT;
                }
            }
            return errorMessage;
        }
        
    }
    
    /////////////////////////////////////////////////////////////////////////////////
    // Constants
    
    public static final String DEFAULT_TITLE =
            ResourceUtils.getString(SSBasePanel.class,
            "NBP.title"); // NOI18N
    public static final String DEFAULT_DESCRIPTION =
            ResourceUtils.getString(SSBasePanel.class,
            "NBP.description"); // NOI18N
    
    public static final String DEFAULT_DESTINATION_LABEL_TEXT =
            ResourceUtils.getString(SSBasePanel.class,
            "NBP.destination.label.text"); // NOI18N
    public static final String DEFAULT_DESTINATION_BUTTON_TEXT =
            ResourceUtils.getString(SSBasePanel.class,
            "NBP.destination.button.text"); // NOI18N
    
    public static final String ONLY_ONE_LOCATION_ALLOWED_TEXT =
            ResourceUtils.getString(SSBasePanel.class,
            "NBP.destination.only.location.allowed");//NOI18N 
    
    public static final String ALREADY_INSTALLED_LOCATION_TEXT =
            ResourceUtils.getString(SSBasePanel.class,
            "NBP.destination.already.installed");//NOI18N 
    public static final String CREATE_SYMLINKS_CHECKBOX_TEXT =
            ResourceUtils.getString(SSBasePanel.class,
            "NBP.create.symlinks.text");//NOI18N 

    public static final String CREATE_SYMLINKS_PROPERTY =
            Utils.getSPROsslnkPropertyName();

}
