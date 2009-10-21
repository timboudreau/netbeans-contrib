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

package org.netbeans.modules.php.fuse.ui.wizards;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.jdesktop.layout.GroupLayout;
import org.jdesktop.layout.LayoutStyle;
import org.netbeans.api.options.OptionsDisplayer;
import org.netbeans.modules.php.fuse.FuseFramework;
import org.netbeans.modules.php.fuse.utils.InitialFuseSetup;
import org.openide.awt.Mnemonics;
import org.openide.util.ChangeSupport;
import org.openide.util.NbBundle;

/**
 * @author Martin Fousek
 */
public class NewProjectConfigurationPanel extends JPanel {
    private static final long serialVersionUID = 1;

    private final ChangeSupport changeSupport = new ChangeSupport(this);

    public NewProjectConfigurationPanel() {
        initComponents();

        // disable second tab of the TabbedPane
        fuseProjectTabbedPane.setEnabledAt(1, false);

        // setup default values for forms
        dbHostnameTextField.setText("localhost");
        dbNameTextField.setText("");
        dbUsernameTextField.setText("");
        dbPasswordTextField.setText("");

        ItemListener defaultItemListener = new DefaultItemListener();
        setupDBCheckBox.addItemListener(defaultItemListener);

        DocumentListener defaultDocumentListener = new DefaultDocumentListener();
        dbHostnameTextField.getDocument().addDocumentListener(defaultDocumentListener);
        dbNameTextField.getDocument().addDocumentListener(defaultDocumentListener);
    }

    public void addChangeListener(ChangeListener listener) {
        changeSupport.addChangeListener(listener);
    }

    public String getWarningMessage() {
        String warnings = null;
        if (setupDBCheckBox.isSelected()) {
            warnings = validateParams(dbHostnameTextField.getText(), "database hostname");
            if (warnings != null) return warnings;
            warnings = validateParams(dbNameTextField.getText(), "database name");
            if (warnings != null) return warnings;
        }
        return null;
    }

    public void removeChangeListener(ChangeListener listener) {
        changeSupport.removeChangeListener(listener);
    }

    // saving parameters from wizard into help class
    public InitialFuseSetup getSettings() {
        InitialFuseSetup fuseSetup = new InitialFuseSetup();
        if (copyFuseCheckBox.isSelected()) {
            fuseSetup.setCopyFuseFrameworkIntoProject(true);
        }
        if (setupDBCheckBox.isSelected()) {
            fuseSetup.setupDatabase(true);
            fuseSetup.setDbHostname(dbHostnameTextField.getText());
            fuseSetup.setDbName(dbNameTextField.getText());
            fuseSetup.setDbUsername(dbUsernameTextField.getText());
            fuseSetup.setDbPassword(dbPasswordTextField.getText());
        }
        else {
            fuseSetup.setupDatabase(false);
        }
        return fuseSetup;
    }

    void fireChange() {
        changeSupport.fireChange();
    }

    void visibleApp(boolean visible, JLabel paramsLabel, JTextField paramsTextField, JTextField nameTextField) {
        paramsLabel.setVisible(visible);
        paramsTextField.setVisible(visible);
        if (nameTextField != null) {
            nameTextField.setVisible(visible);
        }
    }

    private String validateParams(String paramValue, String param) {
        if (paramValue.isEmpty())
            return NbBundle.getMessage(NewProjectConfigurationPanel.class, "MSG_EmptyParamUsed", param);
        else if (paramValue.matches("^.* .*")) {
            return NbBundle.getMessage(NewProjectConfigurationPanel.class, "MSG_SpacedParamUsed", param);
        }
        return null;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        fuseProjectTabbedPane = new JTabbedPane();
        generalTab = new JPanel();
        copyFuseCheckBox = new JCheckBox();
        setupDBCheckBox = new JCheckBox();
        databaseTab = new JPanel();
        dbHostnameLabel = new JLabel();
        dbUsernameLabel = new JLabel();
        dbPasswordLabel = new JLabel();
        dbNameLabel = new JLabel();
        dbUsernameTextField = new JTextField();
        dbHostnameTextField = new JTextField();
        dbPasswordTextField = new JTextField();
        dbNameTextField = new JTextField();

        fuseProjectTabbedPane.setDoubleBuffered(true);

        fuseProjectTabbedPane.addComponentListener(new ComponentAdapter() {
            public void componentShown(ComponentEvent evt) {
                fuseProjectTabbedPaneComponentShown(evt);
            }
        });
        Mnemonics.setLocalizedText(copyFuseCheckBox, NbBundle.getMessage(NewProjectConfigurationPanel.class, "NewProjectConfigurationPanel.copyFuseCheckBox.text"));
        Mnemonics.setLocalizedText(setupDBCheckBox, NbBundle.getMessage(NewProjectConfigurationPanel.class, "NewProjectConfigurationPanel.setupDBCheckBox.text"));
        setupDBCheckBox.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent evt) {
                setupDBCheckBoxStateChanged(evt);
            }
        });
        setupDBCheckBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                setupDBCheckBoxActionPerformed(evt);
            }
        });

        GroupLayout generalTabLayout = new GroupLayout(generalTab);
        generalTab.setLayout(generalTabLayout);









        generalTabLayout.setHorizontalGroup(
            generalTabLayout.createParallelGroup(GroupLayout.LEADING)
            .add(generalTabLayout.createSequentialGroup()
                .addContainerGap()
                .add(generalTabLayout.createParallelGroup(GroupLayout.LEADING)
                    .add(copyFuseCheckBox)
                    .add(setupDBCheckBox))
                .add(364, 364, 364))
        );
        generalTabLayout.setVerticalGroup(
            generalTabLayout.createParallelGroup(GroupLayout.LEADING)
            .add(generalTabLayout.createSequentialGroup()
                .addContainerGap()
                .add(copyFuseCheckBox)
                .addPreferredGap(LayoutStyle.RELATED)
                .add(setupDBCheckBox)
                .addContainerGap(109, Short.MAX_VALUE))
        );

        fuseProjectTabbedPane.addTab(NbBundle.getMessage(NewProjectConfigurationPanel.class, "NewProjectConfigurationPanel.generalTab.TabConstraints.tabTitle"), generalTab); // NOI18N
        Mnemonics.setLocalizedText(dbHostnameLabel, NbBundle.getMessage(NewProjectConfigurationPanel.class, "NewProjectConfigurationPanel.dbHostnameLabel.text"));
        Mnemonics.setLocalizedText(dbUsernameLabel, NbBundle.getMessage(NewProjectConfigurationPanel.class, "NewProjectConfigurationPanel.dbUsernameLabel.text"));
        Mnemonics.setLocalizedText(dbPasswordLabel, NbBundle.getMessage(NewProjectConfigurationPanel.class, "NewProjectConfigurationPanel.dbPasswordLabel.text"));
        Mnemonics.setLocalizedText(dbNameLabel, NbBundle.getMessage(NewProjectConfigurationPanel.class, "NewProjectConfigurationPanel.dbNameLabel.text"));
        dbUsernameTextField.setText(NbBundle.getMessage(NewProjectConfigurationPanel.class, "NewProjectConfigurationPanel.dbUsernameTextField.text")); // NOI18N
        dbHostnameTextField.setText(NbBundle.getMessage(NewProjectConfigurationPanel.class, "NewProjectConfigurationPanel.dbHostnameTextField.text")); // NOI18N
        dbPasswordTextField.setText(NbBundle.getMessage(NewProjectConfigurationPanel.class, "NewProjectConfigurationPanel.dbPasswordTextField.text")); // NOI18N
        dbNameTextField.setText(NbBundle.getMessage(NewProjectConfigurationPanel.class, "NewProjectConfigurationPanel.dbNameTextField.text")); // NOI18N
        GroupLayout databaseTabLayout = new GroupLayout(databaseTab);
        databaseTab.setLayout(databaseTabLayout);

        databaseTabLayout.setHorizontalGroup(
            databaseTabLayout.createParallelGroup(GroupLayout.LEADING)
            .add(databaseTabLayout.createSequentialGroup()
                .addContainerGap()
                .add(databaseTabLayout.createParallelGroup(GroupLayout.LEADING)
                    .add(dbHostnameLabel)
                    .add(dbNameLabel)
                    .add(dbUsernameLabel)
                    .add(dbPasswordLabel))
                .addPreferredGap(LayoutStyle.RELATED)
                .add(databaseTabLayout.createParallelGroup(GroupLayout.LEADING)
                    .add(GroupLayout.TRAILING, dbPasswordTextField, GroupLayout.DEFAULT_SIZE, 506, Short.MAX_VALUE)
                    .add(GroupLayout.TRAILING, dbUsernameTextField, GroupLayout.DEFAULT_SIZE, 506, Short.MAX_VALUE)
                    .add(dbHostnameTextField, GroupLayout.DEFAULT_SIZE, 506, Short.MAX_VALUE)
                    .add(GroupLayout.TRAILING, dbNameTextField, GroupLayout.DEFAULT_SIZE, 506, Short.MAX_VALUE))
                .addContainerGap())
        );
        databaseTabLayout.setVerticalGroup(
            databaseTabLayout.createParallelGroup(GroupLayout.LEADING)
            .add(databaseTabLayout.createSequentialGroup()
                .addContainerGap()
                .add(databaseTabLayout.createParallelGroup(GroupLayout.BASELINE)
                    .add(dbHostnameLabel)
                    .add(dbHostnameTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .add(7, 7, 7)
                .add(databaseTabLayout.createParallelGroup(GroupLayout.BASELINE)
                    .add(dbNameTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .add(dbNameLabel))
                .addPreferredGap(LayoutStyle.RELATED)
                .add(databaseTabLayout.createParallelGroup(GroupLayout.BASELINE)
                    .add(dbUsernameTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .add(dbUsernameLabel))
                .addPreferredGap(LayoutStyle.RELATED)
                .add(databaseTabLayout.createParallelGroup(GroupLayout.BASELINE)
                    .add(dbPasswordTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .add(dbPasswordLabel))
                .addContainerGap(32, Short.MAX_VALUE))
        );

        fuseProjectTabbedPane.addTab(NbBundle.getMessage(NewProjectConfigurationPanel.class, "NewProjectConfigurationPanel.databaseTab.TabConstraints.tabTitle"), databaseTab); // NOI18N
        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.LEADING)
            .add(fuseProjectTabbedPane, GroupLayout.DEFAULT_SIZE, 617, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.LEADING)
            .add(fuseProjectTabbedPane, GroupLayout.PREFERRED_SIZE, 202, GroupLayout.PREFERRED_SIZE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void setupDBCheckBoxActionPerformed(ActionEvent evt) {//GEN-FIRST:event_setupDBCheckBoxActionPerformed
        if (setupDBCheckBox.isSelected())
            fuseProjectTabbedPane.setEnabledAt(1, true);
        else
            fuseProjectTabbedPane.setEnabledAt(1, false);
    }//GEN-LAST:event_setupDBCheckBoxActionPerformed

    private void setupDBCheckBoxStateChanged(ChangeEvent evt) {//GEN-FIRST:event_setupDBCheckBoxStateChanged
        if (setupDBCheckBox.isSelected())
            fuseProjectTabbedPane.setEnabledAt(1, true);
        else
            fuseProjectTabbedPane.setEnabledAt(1, false);
    }//GEN-LAST:event_setupDBCheckBoxStateChanged

    private void fuseProjectTabbedPaneComponentShown(ComponentEvent evt) {//GEN-FIRST:event_fuseProjectTabbedPaneComponentShown
        changeSupport.fireChange();
    }//GEN-LAST:event_fuseProjectTabbedPaneComponentShown


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JCheckBox copyFuseCheckBox;
    private JPanel databaseTab;
    private JLabel dbHostnameLabel;
    private JTextField dbHostnameTextField;
    private JLabel dbNameLabel;
    private JTextField dbNameTextField;
    private JLabel dbPasswordLabel;
    private JTextField dbPasswordTextField;
    private JLabel dbUsernameLabel;
    private JTextField dbUsernameTextField;
    private JTabbedPane fuseProjectTabbedPane;
    private JPanel generalTab;
    private JCheckBox setupDBCheckBox;
    // End of variables declaration//GEN-END:variables

    private final class DefaultItemListener implements ItemListener {
        public void itemStateChanged(ItemEvent e) {
            fireChange();
        }
    }

    private final class DefaultDocumentListener implements DocumentListener {
        public void insertUpdate(DocumentEvent e) {
            processUpdate();
        }
        public void removeUpdate(DocumentEvent e) {
            processUpdate();
        }
        public void changedUpdate(DocumentEvent e) {
            processUpdate();
        }
        private void processUpdate() {
            fireChange();
        }
    }
}
