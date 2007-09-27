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

package org.netbeans.modules.jackpot.ui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ItemEvent;
import java.util.ResourceBundle;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComponent;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.modules.jackpot.*;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.SharedClassObject;

/**
 * The "Inspect and Transform" initial Jackpot command dialog panel.
 */
public class QueryAndRefactorPanel extends javax.swing.JPanel {

    public boolean showDialog(String title) {
        assert EventQueue.isDispatchThread();
        validate();

        String optionInspect = getString("LBL_Inspect");
        Object[] options = new Object[] {
            optionInspect,
            NotifyDescriptor.CANCEL_OPTION
        };
        NotifyDescriptor descr = new DialogDescriptor(
            this, title, true, options, optionInspect, 
            DialogDescriptor.DEFAULT_ALIGN, null, null);
        descr.setMessageType(NotifyDescriptor.PLAIN_MESSAGE);
        Object answer = DialogDisplayer.getDefault().notify(descr);
	return answer == optionInspect;
    }
    
    /**
     * Creates new form QueryAndRefactorPanel
     */
    public QueryAndRefactorPanel() {
        initComponents();
        initialize();
        validate();
    }
    
    private void initialize() {
        configurationButton.doClick();
        setConfigurationsComboBoxList();
        setInspectionComboBoxList();
    }
    
    private void setConfigurationsComboBoxList() {
        QuerySet[] qsets = QuerySetList.instance().getQuerySets();
        String[] labels = new String[qsets.length];
        for (int i = 0; i < qsets.length; i++)
            labels[i] = qsets[i].getLocalizedName();
        configurationsComboBox.setModel(new DefaultComboBoxModel(labels));
    }
    
    private void setInspectionComboBoxList() {
        InspectionsList inspections = InspectionsList.instance();
        ComboBoxModel model = new DefaultComboBoxModel(inspections.getInspectorNames());
        inspectionComboBox.setModel(model);
        setInspectionOptions((String)inspectionComboBox.getItemAt(0));
    }
    
    private void setInspectionOptions(String inspector) {
        InspectionsList inspections = InspectionsList.instance();
        Inspection insp = inspections.getInspection(inspector);
        JComponent options = insp.getOptionsPanel();
        if (optionsPanel.getComponentCount() > 0)
            optionsPanel.removeAll();
        optionsPanel.add(options, BorderLayout.CENTER);
        validate();
        inspectionComboBox.setToolTipText(insp.getDescription());
    }
    
    public Inspection[] getSelectedInspections() {
        if (configurationButton.isSelected()) {
            QuerySet[] querySets = QuerySetList.instance().getQuerySets();
            QuerySet qset = querySets[configurationsComboBox.getSelectedIndex()];
            return qset.getInspections();
        } else {
            InspectionsList inspections = InspectionsList.instance();
            String inspector = (String)inspectionComboBox.getSelectedItem();
            return new Inspection[] { inspections.getInspection(inspector) };
        }
    }
    
    String getQuerySetName() {
        return configurationButton.isSelected() ? 
            (String)configurationsComboBox.getSelectedItem() : null;
    }
    
    private static String getProjectDisplayName(Project p) {
        return ProjectUtils.getInformation(p).getDisplayName();
    }
    
    private static String getString(String key) {
        ResourceBundle bundle = ResourceBundle.getBundle("org/netbeans/modules/jackpot/ui/Bundle");
        return bundle.getString(key);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        scopeGroup = new javax.swing.ButtonGroup();
        configurationsGroup = new javax.swing.ButtonGroup();
        inspectUsingLabel = new javax.swing.JLabel();
        configurationButton = new javax.swing.JRadioButton();
        configurationsComboBox = new javax.swing.JComboBox();
        manageButton = new javax.swing.JButton();
        inspectionComboBox = new javax.swing.JComboBox();
        inspectionButton = new javax.swing.JRadioButton();
        optionsScroller = new javax.swing.JScrollPane();
        optionsTitlePanel = new javax.swing.JPanel();
        optionsPanel = new javax.swing.JPanel();
        noOptionsLabel = new javax.swing.JLabel();

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/netbeans/modules/jackpot/ui/Bundle"); // NOI18N
        inspectUsingLabel.setText(bundle.getString("LBL_InspectUsing")); // NOI18N

        configurationsGroup.add(configurationButton);
        configurationButton.setText(bundle.getString("BTN_Configuration")); // NOI18N
        configurationButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        configurationButton.setMargin(new java.awt.Insets(0, 0, 0, 0));

        configurationsComboBox.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                configurationsComboFocusHandler(evt);
            }
        });

        manageButton.setText(bundle.getString("BTN_Manage")); // NOI18N
        manageButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                invokeRefactoringManager(evt);
            }
        });

        inspectionComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                inspectionSelectedHandler(evt);
            }
        });
        inspectionComboBox.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                inspectionComboFocusHandler(evt);
            }
        });

        configurationsGroup.add(inspectionButton);
        inspectionButton.setText(bundle.getString("BTN_SingleInspection")); // NOI18N
        inspectionButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        inspectionButton.setMargin(new java.awt.Insets(0, 0, 0, 0));

        optionsScroller.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(QueryAndRefactorPanel.class, "LBL_OptionsTitle"))); // NOI18N
        optionsScroller.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        optionsTitlePanel.setLayout(new java.awt.BorderLayout());

        optionsPanel.setLayout(new java.awt.BorderLayout());

        optionsPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 4, 4, 4));
        noOptionsLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        noOptionsLabel.setForeground(new java.awt.Color(128, 128, 128));
        noOptionsLabel.setText(org.openide.util.NbBundle.getMessage(QueryAndRefactorPanel.class, "LBL_None")); // NOI18N
        noOptionsLabel.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        noOptionsLabel.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        optionsPanel.add(noOptionsLabel, java.awt.BorderLayout.CENTER);

        optionsTitlePanel.add(optionsPanel, java.awt.BorderLayout.CENTER);

        optionsScroller.setViewportView(optionsTitlePanel);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(inspectUsingLabel)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, manageButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 91, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(layout.createSequentialGroup()
                        .add(12, 12, 12)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(optionsScroller, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 521, Short.MAX_VALUE)
                            .add(layout.createSequentialGroup()
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(configurationButton)
                                    .add(inspectionButton))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(inspectionComboBox, 0, 416, Short.MAX_VALUE)
                                    .add(configurationsComboBox, 0, 416, Short.MAX_VALUE))))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(inspectUsingLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(configurationButton)
                    .add(configurationsComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(inspectionButton)
                    .add(inspectionComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(manageButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(optionsScroller, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 141, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void configurationsComboFocusHandler(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_configurationsComboFocusHandler
        configurationButton.setSelected(true);
    }//GEN-LAST:event_configurationsComboFocusHandler

    private void inspectionComboFocusHandler(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_inspectionComboFocusHandler
        inspectionButton.setSelected(true);
    }//GEN-LAST:event_inspectionComboFocusHandler

    private void invokeRefactoringManager(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_invokeRefactoringManager
        RefactoringManagerPanel panel = new RefactoringManagerPanel();
        panel.selectQuerySet(configurationsComboBox.getSelectedIndex());
        panel.selectQuery(inspectionComboBox.getSelectedIndex());
        RefactoringManagerAction action = (RefactoringManagerAction)
            SharedClassObject.findObject(RefactoringManagerAction.class, true);
        action.performAction(panel);
        configurationsComboBox.setSelectedIndex(panel.getQuerySetIndex());
        inspectionComboBox.setSelectedIndex(panel.getQuerySelection());
    }//GEN-LAST:event_invokeRefactoringManager

    private void inspectionSelectedHandler(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_inspectionSelectedHandler
        if (evt.getStateChange() == ItemEvent.SELECTED)
            setInspectionOptions((String)evt.getItem());
    }//GEN-LAST:event_inspectionSelectedHandler
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JRadioButton configurationButton;
    private javax.swing.JComboBox configurationsComboBox;
    private javax.swing.ButtonGroup configurationsGroup;
    private javax.swing.JLabel inspectUsingLabel;
    private javax.swing.JRadioButton inspectionButton;
    private javax.swing.JComboBox inspectionComboBox;
    private javax.swing.JButton manageButton;
    private javax.swing.JLabel noOptionsLabel;
    private javax.swing.JPanel optionsPanel;
    private javax.swing.JScrollPane optionsScroller;
    private javax.swing.JPanel optionsTitlePanel;
    private javax.swing.ButtonGroup scopeGroup;
    // End of variables declaration//GEN-END:variables
    
}
