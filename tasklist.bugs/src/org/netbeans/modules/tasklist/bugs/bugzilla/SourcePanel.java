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

package org.netbeans.modules.tasklist.bugs.bugzilla;

import org.netbeans.modules.tasklist.bugs.BugQuery;
import org.netbeans.modules.tasklist.bugs.QueryPanelIF;
import org.netbeans.modules.tasklist.bugs.BugEngine;
import org.netbeans.modules.tasklist.bugs.ProjectDesc;
import org.netbeans.modules.tasklist.bugs.javanet.ProjectList;
import org.netbeans.modules.tasklist.bugs.issuezilla.Issuezilla;
import org.openide.util.RequestProcessor;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.net.URL;
import java.net.MalformedURLException;

/**
 * Allows to customize IZ and java.net bug database connection.
 *
 * @author  Petr Kuzel
 */
public class SourcePanel extends javax.swing.JPanel implements QueryPanelIF {

    /**
     * Creates new form SourcePanel
     *
     * @param showServiceField XXX on true allows to enter IZ server on false access java.net
     */
    public SourcePanel(boolean showServiceField) {
        initComponents();

        serviceExampleLabel.setVisible(showServiceField);
        serviceLabel.setVisible(showServiceField);
        serviceTextField.setVisible(showServiceField);

        if (showServiceField) {
            serviceTextField.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    URL url = null;
                    try {
                        serviceExampleLabel.setText("probing...");
                        url = new URL(serviceTextField.getText());
                        String [] comps = Issuezilla.getComponents(url);
                        DefaultComboBoxModel model = new DefaultComboBoxModel(comps);
                        componentComboBox.setModel(model);
                        serviceExampleLabel.setText("Server OK");
                    } catch (MalformedURLException e1) {
                        serviceExampleLabel.setText("Invalid server URL!");
                    }
                }
            });
        } else {
            componentComboBox.setEnabled(false);
            RequestProcessor.getDefault().post(new Runnable() {
                public void run() {
                    ProjectDesc[] projects = ProjectList.listProjects();
                    DefaultComboBoxModel model = new DefaultComboBoxModel(projects);
                    componentComboBox.setModel(model);
                    componentComboBox.setEnabled(true);
                }
            });
        }
    }


    public BugQuery getQueryOptions(BugQuery inQuery) {
        if (serviceTextField.isVisible()) {
            inQuery.setBaseUrl(serviceTextField.getText());
        } else {
            inQuery.setBaseUrl("https://" + componentComboBox.getSelectedItem() + ".dev.java.net/issues/");
        }

        if (componentRadioButton.isSelected()) {
            inQuery.setQueryString("component=" + componentComboBox.getSelectedItem() + "&issue_status=NEW&issue_status=STARTED&issue_status=REOPENED");
        } else {
            inQuery.setQueryString(customTextField.getText());
        }
        return inQuery;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        buttonGroup1 = new javax.swing.ButtonGroup();
        serviceLabel = new javax.swing.JLabel();
        serviceTextField = new javax.swing.JTextField();
        serviceExampleLabel = new javax.swing.JLabel();
        componentRadioButton = new javax.swing.JRadioButton();
        componentLabel = new javax.swing.JLabel();
        componentComboBox = new javax.swing.JComboBox();
        customRadioButton = new javax.swing.JRadioButton();
        customLabel = new javax.swing.JLabel();
        customTextField = new javax.swing.JTextField();
        customExLabel = new javax.swing.JLabel();

        setLayout(new java.awt.GridBagLayout());

        serviceLabel.setText("Service URL");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(serviceLabel, gridBagConstraints);

        serviceTextField.setColumns(60);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 0, 0);
        gridBagConstraints.weightx = 1.0;
        add(serviceTextField, gridBagConstraints);

        serviceExampleLabel.setText("e.g. http://www.netbeans.org/issues/");
        serviceExampleLabel.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
        gridBagConstraints.weightx = 1.0;
        add(serviceExampleLabel, gridBagConstraints);

        componentRadioButton.setText("Show all opened issues");
        buttonGroup1.add(componentRadioButton);
        componentRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                componentRadioButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(componentRadioButton, gridBagConstraints);

        componentLabel.setText("for component");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(6, 24, 0, 0);
        add(componentLabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
        gridBagConstraints.weightx = 1.0;
        add(componentComboBox, gridBagConstraints);

        customRadioButton.setText("Custom query given by URL");
        buttonGroup1.add(customRadioButton);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(customRadioButton, gridBagConstraints);

        customLabel.setText("parameters");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(6, 24, 0, 0);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(customLabel, gridBagConstraints);

        customTextField.setColumns(60);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
        gridBagConstraints.weightx = 1.0;
        add(customTextField, gridBagConstraints);

        customExLabel.setText("e.g. component=www&state=NEW");
        customExLabel.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
        gridBagConstraints.weightx = 1.0;
        add(customExLabel, gridBagConstraints);

    }//GEN-END:initComponents

    private void componentRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_componentRadioButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_componentRadioButtonActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JComboBox componentComboBox;
    private javax.swing.JLabel componentLabel;
    private javax.swing.JRadioButton componentRadioButton;
    private javax.swing.JLabel customExLabel;
    private javax.swing.JLabel customLabel;
    private javax.swing.JRadioButton customRadioButton;
    private javax.swing.JTextField customTextField;
    private javax.swing.JLabel serviceExampleLabel;
    private javax.swing.JLabel serviceLabel;
    private javax.swing.JTextField serviceTextField;
    // End of variables declaration//GEN-END:variables
    
}
