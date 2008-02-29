package org.netbeans.installer.products.sample.panels;

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



import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JPanel;
import org.netbeans.installer.utils.helper.swing.NbiLabel;
import org.netbeans.installer.utils.ResourceUtils;
import org.netbeans.installer.utils.helper.swing.NbiCheckBox;
import org.netbeans.installer.wizard.components.panels.DestinationPanel;
import org.netbeans.installer.wizard.components.panels.DestinationPanel.DestinationPanelUi;
import org.netbeans.installer.wizard.ui.SwingUi;
import org.netbeans.installer.wizard.ui.WizardUi;
import org.netbeans.installer.wizard.containers.SwingContainer;

/**
 *
 * @author Kirill Sorokin
 */
public class SSBasePanel extends DestinationPanel {
    
    public SSBasePanel() {
        
        setProperty(TITLE_PROPERTY,
                DEFAULT_TITLE);
        setProperty(DESCRIPTION_PROPERTY,
                DEFAULT_DESCRIPTION);
        
        setProperty(DESTINATION_LABEL_TEXT_PROPERTY,
                DEFAULT_DESTINATION_LABEL_TEXT);
        setProperty(DESTINATION_BUTTON_TEXT_PROPERTY,
                DEFAULT_DESTINATION_BUTTON_TEXT);
        
        setProperty(BROWSE_BUTTON_TEXT_PROPERTY,
                DEFAULT_BROWSE_BUTTON_TEXT);        
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
    }
        
    /////////////////////////////////////////////////////////////////////////////////
    // Inner Classes
    public static class SSBaseDestinationPanelUi extends DestinationPanelUi {
        protected SSBasePanel panel;
        
        public SSBaseDestinationPanelUi(SSBasePanel panel) {
            super(panel);
            this.panel = panel;
        }
        
        public SwingUi getSwingUi(SwingContainer container) {
            if (swingUi == null) {
                swingUi = new NbBaseDestinationPanelSwingUi(panel, container);
            }            
            return super.getSwingUi(container);
        }
    }
    
    public static class NbBaseDestinationPanelSwingUi extends DestinationPanelSwingUi {
        protected SSBasePanel panel;
                
        private NbiCheckBox copySystemPrequesties;
        private NbiCheckBox createSymLinks;
        private NbiCheckBox installPatches;
        
        private NbiLabel copySystemPrequestiesLabel;
        private NbiLabel createSymLinksLabel;
        private NbiLabel installPatchesLabel;
                
        public NbBaseDestinationPanelSwingUi(
                final SSBasePanel panel,
                final SwingContainer container) {
            super(panel, container);            
            this.panel = panel;            
            initComponents();
        }
        
        // protected ////////////////////////////////////////////////////////////////
        @Override
        protected void initialize() {
            super.initialize();
        }
        
        @Override
        protected void saveInput() {
            super.saveInput();                        
        }
        
        @Override
        protected String validateInput() {
            String errorMessage = super.validateInput();
            return errorMessage;
        }
        
        // private //////////////////////////////////////////////////////////////////
        private void initComponents() {
            copySystemPrequesties = new NbiCheckBox();
            createSymLinks = new NbiCheckBox();
            installPatches = new NbiCheckBox();
            
            copySystemPrequestiesLabel = new NbiLabel();
            copySystemPrequestiesLabel.setText("Copy system prequesties");
            createSymLinksLabel = new NbiLabel();
            createSymLinksLabel.setText("Create symlinnks in /usr/bin");
            installPatchesLabel = new NbiLabel();
            installPatchesLabel.setText("Install product patches");
                                
            JPanel pane = new JPanel(new GridBagLayout());
            add(pane , new GridBagConstraints(
                    0, 3,                             // x, y
                    10, 10,                             // width, height
                    1.0, 0.0,                         // weight-x, weight-y
                    GridBagConstraints.LINE_START,    // anchor
                    GridBagConstraints.HORIZONTAL,    // fill
                    new Insets(11, 11, 0, 11),        // padding
                    0, 0));                           // padx, pady - ??? );
            
            pane.add(copySystemPrequesties, new GridBagConstraints(
                    0, 0,                             // x, y
                    1, 1,                             // width, height
                    0.0, 0.0,                         // weight-x, weight-y
                    GridBagConstraints.CENTER,    // anchor
                    GridBagConstraints.HORIZONTAL,    // fill
                    new Insets(0, 0, 0, 0),        // padding
                    0, 0));                           // padx, pady - ???
            pane.add(copySystemPrequestiesLabel, new GridBagConstraints(
                    1, 0,                             // x, y
                    1, 1,                             // width, height
                    10.0, 0.0,                         // weight-x, weight-y
                    GridBagConstraints.LINE_START,    // anchor
                    GridBagConstraints.HORIZONTAL,    // fill
                    new Insets(0, 0, 0, 0),        // padding
                    0, 0));                    
            pane.add(createSymLinks, new GridBagConstraints(
                    0, 3,                             // x, y
                    1, 1,                             // width, height
                    0.0, 0.0,                         // weight-x, weight-y
                    GridBagConstraints.LINE_START,    // anchor
                    GridBagConstraints.HORIZONTAL,    // fill
                    new Insets(0, 0, 0, 0),        // padding
                    0, 0));                           // padx, pady - ???
            pane.add(installPatches, new GridBagConstraints(
                    0, 4,                             // x, y
                    1, 1,                             // width, height
                    0.0, 0.0,                         // weight-x, weight-y
                    GridBagConstraints.LINE_START,    // anchor
                    GridBagConstraints.HORIZONTAL,    // fill
                    new Insets(0, 0, 0, 0),        // padding
                    0, 0));                           // padx, pady - ???
            pane.add(createSymLinksLabel, new GridBagConstraints(
                    1, 3,                             // x, y
                    2, 1,                             // width, height
                    10.0, 0.0,                         // weight-x, weight-y
                    GridBagConstraints.LINE_START,    // anchor
                    GridBagConstraints.HORIZONTAL,    // fill
                    new Insets(0, 0, 0, 0),        // padding
                    0, 0));                           // padx, pady - ???
            pane.add(installPatchesLabel, new GridBagConstraints(
                    1, 4,                             // x, y
                    2, 1,                             // width, height
                    10.0, 0.0,                         // weight-x, weight-y
                    GridBagConstraints.LINE_START,    // anchor
                    GridBagConstraints.HORIZONTAL,    // fill
                    new Insets(0, 0, 0, 0),        // padding
                    10, 0));                           // padx, pady - ???
            
        }
               
    }
    
    /////////////////////////////////////////////////////////////////////////////////
    // Constants
    public static final String BROWSE_BUTTON_TEXT_PROPERTY =
            "browse.button.text"; // NOI18N
    
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
    
    public static final String DEFAULT_BROWSE_BUTTON_TEXT =
            ResourceUtils.getString(SSBasePanel.class,
            "NBP.browse.button.text"); // NOI18N
    
}
