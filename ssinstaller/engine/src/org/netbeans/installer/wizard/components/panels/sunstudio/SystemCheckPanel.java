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

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JLabel;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import org.netbeans.installer.utils.ResourceUtils;
import org.netbeans.installer.utils.env.CheckStatus;
import org.netbeans.installer.utils.env.SystemCheckCategory;
import org.netbeans.installer.utils.helper.swing.NbiButton;
import org.netbeans.installer.utils.helper.swing.NbiLabel;
import org.netbeans.installer.utils.helper.swing.NbiPanel;
import org.netbeans.installer.wizard.components.panels.ErrorMessagePanel;
import org.netbeans.installer.wizard.components.panels.ErrorMessagePanel.ErrorMessagePanelSwingUi;
import org.netbeans.installer.wizard.containers.SwingContainer;
import org.netbeans.installer.wizard.ui.SwingUi;
import org.netbeans.installer.wizard.ui.WizardUi;


public class SystemCheckPanel extends ErrorMessagePanel {
    
    public static final String DEFAULT_TITLE = ResourceUtils.getString(SystemCheckPanel.class, "SCP.title"); // NOI18N
    public static final String DEFAULT_DESCRIPTION = ResourceUtils.getString(SystemCheckPanel.class, "SCP.description"); // NOI18N
    public static final String MORE_INFO_BUTTON_TEXT = ResourceUtils.getString(SystemCheckPanel.class, "SCP.more_info_button_text"); // NOI18N
    
    /////////////////////////////////////////////////////////////////////////////////
    // Instance
    public SystemCheckPanel() {}
    
    @Override
    public WizardUi getWizardUi() {
        if (wizardUi == null) {
            wizardUi = new SSSystemCheckPanelUi(this);
        }
        
        return wizardUi;
    }

    @Override
    public void initialize() {
        setProperty(TITLE_PROPERTY, DEFAULT_TITLE);
        setProperty(DESCRIPTION_PROPERTY, DEFAULT_DESCRIPTION);
    }

    /////////////////////////////////////////////////////////////////////////////////
    // Inner Classes
    public static class SSSystemCheckPanelUi extends ErrorMessagePanelUi {
        protected SystemCheckPanel component;
        
        public SSSystemCheckPanelUi(SystemCheckPanel component) {
            super(component);
            
            this.component = component;
        }
        
        @Override
        public SwingUi getSwingUi(SwingContainer container) {
            if (swingUi == null) {
                swingUi = new SSSystemCheckPanelSwingUi(component, container);
            }
            
            return super.getSwingUi(container);
        }
    }
    
    public static class SSSystemCheckPanelSwingUi extends ErrorMessagePanelSwingUi {
        protected SystemCheckPanel component;
        
       
        public SSSystemCheckPanelSwingUi(
                final SystemCheckPanel component,
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
            if (SystemCheckCategory.hasErrorCategories()) {
                container.getNextButton().setVisible(false);
                container.getBackButton().setVisible(false);
                container.getCancelButton().setText(component.getProperty(FINISH_BUTTON_TEXT_PROPERTY));
            }
        }
        
        // private //////////////////////////////////////////////////////////////////
        private void initComponents() {
            NbiPanel mainPanel = new NbiPanel();
            mainPanel.setLayout(new GridBagLayout());
            GridBagConstraints mainConstraints = new GridBagConstraints();
            NbiLabel shortDescription = new NbiLabel();
            Font bf = shortDescription.getFont(); 
            if (bf.isBold()) shortDescription.setFont(bf.deriveFont(Font.ITALIC | Font.BOLD, bf.getSize2D()));
            else shortDescription.setFont(bf.deriveFont(Font.BOLD, bf.getSize2D()));
            JTextArea longDescription = new JTextArea();
            longDescription.setEditable(false);
            longDescription.setBackground((Color) UIManager.get("Label.background"));
            longDescription.setOpaque(false);
            longDescription.setForeground((Color) UIManager.get("Label.foreground"));
            longDescription.setBorder(null);
            longDescription.setLineWrap(true);
            longDescription.setWrapStyleWord(true);
            NbiPanel panel = new NbiPanel();
            panel.setLayout(new GridBagLayout());
            GridBagConstraints panelConstraints = new GridBagConstraints();
            int row = 0;
            for(SystemCheckCategory category: SystemCheckCategory.getProblemCategories()) {
                NbiLabel label = new NbiLabel();
                label.setText(category.getCaption() + ":");
                panelConstraints.gridy = row;
                panelConstraints.gridx = 0;
                panelConstraints.insets.right = 30;
                panelConstraints.fill = GridBagConstraints.HORIZONTAL;
                panel.add(label, panelConstraints);
                label = new NbiLabel();
                label.setText(category.getDisplayString());
                bf = label.getFont(); 
                if (bf.isBold()) label.setFont(bf.deriveFont(Font.ITALIC | Font.BOLD, bf.getSize2D()));
                else label.setFont(bf.deriveFont(Font.BOLD, bf.getSize2D()));;
                panelConstraints.gridx = 1;
                panelConstraints.weightx = 0.5;
                panel.add(label, panelConstraints);
                label = new NbiLabel();
                CheckStatus status = category.check();
                label.setText(status.getDisplayString());
                label.setForeground(status.getDisplayColor());
                panelConstraints.gridx = 2;
                panelConstraints.weightx = 0;
                panel.add(label, panelConstraints);
                NbiButton button = new NbiButton();
                button.setText(MORE_INFO_BUTTON_TEXT);
                button.addActionListener(new MoreInfoActionListener(shortDescription, longDescription, category));
                panelConstraints.gridx = 3;
                panelConstraints.fill = GridBagConstraints.NONE;
                panelConstraints.gridwidth = GridBagConstraints.REMAINDER;
                panelConstraints.insets.bottom = 3;
                panelConstraints.insets.top = 3;
                panelConstraints.insets.right = 0;
                panel.add(button, panelConstraints);
                row++;
            }
            mainConstraints.gridx = 0;
            mainConstraints.gridy = 0;
            mainConstraints.gridwidth = GridBagConstraints.REMAINDER;
            mainConstraints.fill = GridBagConstraints.HORIZONTAL;
            mainConstraints.anchor = GridBagConstraints.NORTH;
            mainConstraints.weightx = 1;
            mainConstraints.insets = new Insets(10, 10, 10, 10);
            mainPanel.add(panel, mainConstraints);
            mainConstraints.gridy = 1;
            mainConstraints.insets = new Insets(0, 0, 0, 0);
            mainPanel.add(new JSeparator(), mainConstraints);
            mainConstraints.gridy = 2;
            mainConstraints.insets = new Insets(10, 10, 0, 0);
            mainPanel.add(shortDescription, mainConstraints);        
            mainConstraints.gridy = 3;
            mainConstraints.insets = new Insets(10, 10, 0, 10);
            mainPanel.add(longDescription, mainConstraints);           
            mainPanel.revalidate();
            setLayout(new GridBagLayout());
            add(mainPanel, new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));            
        }
        
        @Override
        public void evaluateCancelButtonClick() {
            if (SystemCheckCategory.hasErrorCategories()) {
                component.getWizard().getFinishHandler().cancel();
            } else super.evaluateCancelButtonClick();
        }
        
    }
  
}

class MoreInfoActionListener implements ActionListener {
    
    private JLabel shortMessageLabel = null;
    private JTextArea longMessageLabel = null;
    private SystemCheckCategory category = null;

    public MoreInfoActionListener(JLabel shortMessageLabel, JTextArea longMessageLabel, SystemCheckCategory category) {
        this.shortMessageLabel = shortMessageLabel;
        this.longMessageLabel = longMessageLabel;
        this.category = category;
    }
    
    public void actionPerformed(ActionEvent arg0) {
        shortMessageLabel.setText(category.getShortErrorMessage());
        longMessageLabel.setText(category.getLongErrorMessage());
    }   
    
}
