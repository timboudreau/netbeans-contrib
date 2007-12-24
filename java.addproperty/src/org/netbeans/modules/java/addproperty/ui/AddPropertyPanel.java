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
package org.netbeans.modules.java.addproperty.ui;

import java.awt.Rectangle;
import javax.lang.model.element.TypeElement;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.netbeans.api.java.source.ClasspathInfo;
import org.netbeans.api.java.source.ElementHandle;
import org.netbeans.api.java.source.ui.TypeElementFinder;
import org.netbeans.modules.java.addproperty.api.AddPropertyConfig;
import org.netbeans.modules.java.addproperty.api.AddPropertyGenerator;
import org.openide.filesystems.FileObject;

/**
 * A simple GUI for Add Property action.
 *
 * @author  Sandip V. Chitale (Sandip.Chitale@Sun.Com)
 */
public class AddPropertyPanel extends javax.swing.JPanel {

    private static AddPropertyPanel INSTANCE;

    private static boolean propNameModified = false;
    private DocumentListener propNameTextFieldDocumentListener;
    private FileObject fileObject;

    public static AddPropertyPanel getINSTANCE(FileObject fileObject) {
        if (INSTANCE == null) {
            INSTANCE = new AddPropertyPanel();
        }
        INSTANCE.setFileObject(fileObject);
        return INSTANCE;
    }

    private AddPropertyPanel() {
        initComponents();
        previewScrollPane.putClientProperty(
                "HighlightsLayerExcludes", // NOI18N
                "^org\\.netbeans\\.modules\\.editor\\.lib2\\.highlighting\\.CaretRowHighlighting$" // NOI18N
                );

        DocumentListener documentListener = new DocumentListener() {            
            public void insertUpdate(DocumentEvent e) {
                showPreview();
            }

            public void removeUpdate(DocumentEvent e) {
                showPreview();
            }

            public void changedUpdate(DocumentEvent e) {
            }
        };
        
        nameTextField.getDocument().addDocumentListener(documentListener);
        initializerTextField.getDocument().addDocumentListener(documentListener);

        propNameTextFieldDocumentListener = new DocumentListener() {

            public void insertUpdate(DocumentEvent e) {
                propNameModified = true;
                showPreview();
            }

            public void removeUpdate(DocumentEvent e) {
                propNameModified = true;
                showPreview();
            }

            public void changedUpdate(DocumentEvent e) {
            }
        };
        propNameTextField.getDocument().addDocumentListener(propNameTextFieldDocumentListener);
    }

    @Override
    public void addNotify() {
        super.addNotify();
        previewEditorPane.setText("");
        propNameModified = false;
        generatePropertyChangeSupportCheckBox.setSelected(false);
        generateVetoablePropertyChangeSupportCheckBox.setSelected(false);
        showPreview();
    }

    FileObject getFileObject() {
        return fileObject;
    }

    void setFileObject(FileObject fileObject) {
        this.fileObject = fileObject;
    }

    private void showPreview() {
        if (!propNameModified) {
            propNameTextField.getDocument().removeDocumentListener(propNameTextFieldDocumentListener);
            propNameTextField.setText("PROP_" + nameTextField.getText().toUpperCase());
            propNameTextField.getDocument().addDocumentListener(propNameTextFieldDocumentListener);
        }
        equalsLabel.setEnabled(initializerTextField.getText().trim().length() > 0);
        finalRequiresInitializerTipLabel.setEnabled(finalCheckBox.isSelected() && initializerTextField.getText().trim().length() == 0);
        boundTipLabel.setEnabled(boundCheckBox.isSelected() && (!generatePropertyChangeSupportCheckBox.isSelected()));
        vetoableTipLabel.setEnabled(boundCheckBox.isSelected() && vetoableCheckBox.isSelected() && (!generateVetoablePropertyChangeSupportCheckBox.isSelected()));
        propNameTipLabel.setEnabled(propNameModified);
        final String previewTemplate = AddPropertyGenerator.getDefault().generate(getAddPropertyConfig());
        previewEditorPane.setText(previewTemplate);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                previewEditorPane.setCaretPosition(0);
                previewEditorPane.scrollRectToVisible(new Rectangle(0, 0, 1, 1));
            }
        });
    }

    public AddPropertyConfig getAddPropertyConfig() {
        final String type = typeComboBox.getSelectedItem().toString().trim();
        final String name = nameTextField.getText().trim();
        final String initializer = initializerTextField.getText().trim();
        AddPropertyConfig.ACCESS access = AddPropertyConfig.ACCESS.PACKAGE;
        if (privateRadioButton.isSelected()) {
            access = AddPropertyConfig.ACCESS.PRIVATE;
        } else if (protectedRadioButton.isSelected()) {
            access = AddPropertyConfig.ACCESS.PROTECTED;
        } else if (publicRadioButton.isSelected()) {
            access = AddPropertyConfig.ACCESS.PUBLIC;
        }

        AddPropertyConfig.GENERATE generate = AddPropertyConfig.GENERATE.GETTER_AND_SETTER;
        if (generateGetterAndSetterRadioButton.isSelected()) {
            generate = AddPropertyConfig.GENERATE.GETTER_AND_SETTER;
        } else if (generateGetterRadioButton.isSelected()) {
            generate = AddPropertyConfig.GENERATE.GETTER;
        } else if (generateSetterRadioButton.isSelected()) {
            generate = AddPropertyConfig.GENERATE.SETTER;
        }

        AddPropertyConfig addPropertyConfig = new AddPropertyConfig(
                name, initializer, type, access, staticCheckBox.isSelected(), finalCheckBox.isSelected(), generate, generateJavadocCheckBox.isSelected(), boundCheckBox.isSelected(), propNameTextField.getText().trim(), vetoableCheckBox.isSelected(), indexedCheckBox.isSelected(), generatePropertyChangeSupportCheckBox.isSelected(), generateVetoablePropertyChangeSupportCheckBox.isSelected());
        return addPropertyConfig;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        accessGroup = new javax.swing.ButtonGroup();
        getterSetterGroup = new javax.swing.ButtonGroup();
        nameLabel = new javax.swing.JLabel();
        nameTextField = new javax.swing.JTextField();
        equalsLabel = new javax.swing.JLabel();
        initializerTextField = new javax.swing.JTextField();
        semicolonLabel = new javax.swing.JLabel();
        typeLabel = new javax.swing.JLabel();
        typeComboBox = new javax.swing.JComboBox();
        browseTypeButton = new javax.swing.JButton();
        privateRadioButton = new javax.swing.JRadioButton();
        packageRadioButton = new javax.swing.JRadioButton();
        protectedRadioButton = new javax.swing.JRadioButton();
        publicRadioButton = new javax.swing.JRadioButton();
        staticCheckBox = new javax.swing.JCheckBox();
        finalCheckBox = new javax.swing.JCheckBox();
        finalRequiresInitializerTipLabel = new javax.swing.JLabel();
        generateGetterAndSetterRadioButton = new javax.swing.JRadioButton();
        generateGetterRadioButton = new javax.swing.JRadioButton();
        generateSetterRadioButton = new javax.swing.JRadioButton();
        generateJavadocCheckBox = new javax.swing.JCheckBox();
        boundCheckBox = new javax.swing.JCheckBox();
        boundTipLabel = new javax.swing.JLabel();
        propNameTextField = new javax.swing.JTextField();
        propNameTipLabel = new javax.swing.JLabel();
        vetoableCheckBox = new javax.swing.JCheckBox();
        vetoableTipLabel = new javax.swing.JLabel();
        indexedCheckBox = new javax.swing.JCheckBox();
        generatePropertyChangeSupportCheckBox = new javax.swing.JCheckBox();
        generateVetoablePropertyChangeSupportCheckBox = new javax.swing.JCheckBox();
        previewLabel = new javax.swing.JLabel();
        previewScrollPane = new javax.swing.JScrollPane();
        previewEditorPane = new javax.swing.JEditorPane();

        nameLabel.setText(org.openide.util.NbBundle.getMessage(AddPropertyPanel.class, "AddPropertyPanel.nameLabel.text")); // NOI18N

        nameTextField.setText(org.openide.util.NbBundle.getMessage(AddPropertyPanel.class, "AddPropertyPanel.nameTextField.text")); // NOI18N

        equalsLabel.setText(org.openide.util.NbBundle.getMessage(AddPropertyPanel.class, "AddPropertyPanel.equalsLabel.text")); // NOI18N

        initializerTextField.setToolTipText(org.openide.util.NbBundle.getMessage(AddPropertyPanel.class, "AddPropertyPanel.initializerTextField.toolTipText")); // NOI18N

        semicolonLabel.setText(org.openide.util.NbBundle.getMessage(AddPropertyPanel.class, "AddPropertyPanel.semicolonLabel.text")); // NOI18N

        typeLabel.setText(org.openide.util.NbBundle.getMessage(AddPropertyPanel.class, "AddPropertyPanel.typeLabel.text")); // NOI18N

        typeComboBox.setEditable(true);
        typeComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "String", "int", "boolean", "long", "double", "long", "char", "short", "float" }));
        typeComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                typeComboBoxActionPerformed(evt);
            }
        });

        browseTypeButton.setText(org.openide.util.NbBundle.getMessage(AddPropertyPanel.class, "AddPropertyPanel.browseTypeButton.text")); // NOI18N
        browseTypeButton.setToolTipText(org.openide.util.NbBundle.getMessage(AddPropertyPanel.class, "AddPropertyPanel.browseTypeButton.toolTipText")); // NOI18N
        browseTypeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseTypeButtonActionPerformed(evt);
            }
        });

        accessGroup.add(privateRadioButton);
        privateRadioButton.setSelected(true);
        privateRadioButton.setText(org.openide.util.NbBundle.getMessage(AddPropertyPanel.class, "AddPropertyPanel.privateRadioButton.text")); // NOI18N
        privateRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                privateRadioButtonActionPerformed(evt);
            }
        });

        accessGroup.add(packageRadioButton);
        packageRadioButton.setText(org.openide.util.NbBundle.getMessage(AddPropertyPanel.class, "AddPropertyPanel.packageRadioButton.text")); // NOI18N
        packageRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                packageRadioButtonActionPerformed(evt);
            }
        });

        accessGroup.add(protectedRadioButton);
        protectedRadioButton.setText(org.openide.util.NbBundle.getMessage(AddPropertyPanel.class, "AddPropertyPanel.protectedRadioButton.text")); // NOI18N
        protectedRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                protectedRadioButtonActionPerformed(evt);
            }
        });

        accessGroup.add(publicRadioButton);
        publicRadioButton.setText(org.openide.util.NbBundle.getMessage(AddPropertyPanel.class, "AddPropertyPanel.publicRadioButton.text")); // NOI18N
        publicRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                publicRadioButtonActionPerformed(evt);
            }
        });

        staticCheckBox.setText(org.openide.util.NbBundle.getMessage(AddPropertyPanel.class, "AddPropertyPanel.staticCheckBox.text")); // NOI18N
        staticCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                staticCheckBoxActionPerformed(evt);
            }
        });

        finalCheckBox.setText(org.openide.util.NbBundle.getMessage(AddPropertyPanel.class, "AddPropertyPanel.finalCheckBox.text")); // NOI18N
        finalCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                finalCheckBoxActionPerformed(evt);
            }
        });

        finalRequiresInitializerTipLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/netbeans/modules/java/addproperty/ui/resources/tip.png"))); // NOI18N
        finalRequiresInitializerTipLabel.setToolTipText(org.openide.util.NbBundle.getMessage(AddPropertyPanel.class, "AddPropertyPanel.finalRequiresInitializerTipLabel.toolTipText")); // NOI18N
        finalRequiresInitializerTipLabel.setEnabled(false);

        getterSetterGroup.add(generateGetterAndSetterRadioButton);
        generateGetterAndSetterRadioButton.setSelected(true);
        generateGetterAndSetterRadioButton.setText(org.openide.util.NbBundle.getMessage(AddPropertyPanel.class, "AddPropertyPanel.generateGetterAndSetterRadioButton.text")); // NOI18N
        generateGetterAndSetterRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                generateGetterAndSetterRadioButtonActionPerformed(evt);
            }
        });

        getterSetterGroup.add(generateGetterRadioButton);
        generateGetterRadioButton.setText(org.openide.util.NbBundle.getMessage(AddPropertyPanel.class, "AddPropertyPanel.generateGetterRadioButton.text")); // NOI18N
        generateGetterRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                generateGetterRadioButtonActionPerformed(evt);
            }
        });

        getterSetterGroup.add(generateSetterRadioButton);
        generateSetterRadioButton.setText(org.openide.util.NbBundle.getMessage(AddPropertyPanel.class, "AddPropertyPanel.generateSetterRadioButton.text")); // NOI18N
        generateSetterRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                generateSetterRadioButtonActionPerformed(evt);
            }
        });

        generateJavadocCheckBox.setSelected(true);
        generateJavadocCheckBox.setText(org.openide.util.NbBundle.getMessage(AddPropertyPanel.class, "AddPropertyPanel.generateJavadocCheckBox.text")); // NOI18N
        generateJavadocCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                generateJavadocCheckBoxActionPerformed(evt);
            }
        });

        boundCheckBox.setText(org.openide.util.NbBundle.getMessage(AddPropertyPanel.class, "AddPropertyPanel.boundCheckBox.text")); // NOI18N
        boundCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                boundCheckBoxActionPerformed(evt);
            }
        });

        boundTipLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/netbeans/modules/java/addproperty/ui/resources/tip.png"))); // NOI18N
        boundTipLabel.setText(org.openide.util.NbBundle.getMessage(AddPropertyPanel.class, "AddPropertyPanel.boundTipLabel.text")); // NOI18N
        boundTipLabel.setToolTipText(org.openide.util.NbBundle.getMessage(AddPropertyPanel.class, "AddPropertyPanel.boundTipLabel.toolTipText")); // NOI18N
        boundTipLabel.setEnabled(false);

        propNameTextField.setText(org.openide.util.NbBundle.getMessage(AddPropertyPanel.class, "AddPropertyPanel.propNameTextField.text")); // NOI18N

        propNameTipLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/netbeans/modules/java/addproperty/ui/resources/tip.png"))); // NOI18N
        propNameTipLabel.setText(org.openide.util.NbBundle.getMessage(AddPropertyPanel.class, "AddPropertyPanel.propNameTipLabel.text")); // NOI18N
        propNameTipLabel.setToolTipText(org.openide.util.NbBundle.getMessage(AddPropertyPanel.class, "AddPropertyPanel.propNameTipLabel.toolTipText")); // NOI18N
        propNameTipLabel.setEnabled(false);

        vetoableCheckBox.setText(org.openide.util.NbBundle.getMessage(AddPropertyPanel.class, "AddPropertyPanel.vetoableCheckBox.text")); // NOI18N
        vetoableCheckBox.setEnabled(false);
        vetoableCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                vetoableCheckBoxActionPerformed(evt);
            }
        });

        vetoableTipLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/netbeans/modules/java/addproperty/ui/resources/tip.png"))); // NOI18N
        vetoableTipLabel.setText(org.openide.util.NbBundle.getMessage(AddPropertyPanel.class, "AddPropertyPanel.vetoableTipLabel.text")); // NOI18N
        vetoableTipLabel.setToolTipText(org.openide.util.NbBundle.getMessage(AddPropertyPanel.class, "AddPropertyPanel.vetoableTipLabel.toolTipText")); // NOI18N
        vetoableTipLabel.setEnabled(false);

        indexedCheckBox.setText(org.openide.util.NbBundle.getMessage(AddPropertyPanel.class, "AddPropertyPanel.indexedCheckBox.text")); // NOI18N
        indexedCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                indexedCheckBoxActionPerformed(evt);
            }
        });

        generatePropertyChangeSupportCheckBox.setText(org.openide.util.NbBundle.getMessage(AddPropertyPanel.class, "AddPropertyPanel.generatePropertyChangeSupportCheckBox.text")); // NOI18N
        generatePropertyChangeSupportCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                generatePropertyChangeSupportCheckBoxActionPerformed(evt);
            }
        });

        generateVetoablePropertyChangeSupportCheckBox.setText(org.openide.util.NbBundle.getMessage(AddPropertyPanel.class, "AddPropertyPanel.generateVetoablePropertyChangeSupportCheckBox.text")); // NOI18N
        generateVetoablePropertyChangeSupportCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                generateVetoablePropertyChangeSupportCheckBoxActionPerformed(evt);
            }
        });

        previewLabel.setText(org.openide.util.NbBundle.getMessage(AddPropertyPanel.class, "AddPropertyPanel.previewLabel.text")); // NOI18N

        previewEditorPane.setContentType(org.openide.util.NbBundle.getMessage(AddPropertyPanel.class, "AddPropertyPanel.previewEditorPane.contentType")); // NOI18N
        previewEditorPane.setEditable(false);
        previewEditorPane.setText(org.openide.util.NbBundle.getMessage(AddPropertyPanel.class, "AddPropertyPanel.previewEditorPane.text")); // NOI18N
        previewEditorPane.setToolTipText(org.openide.util.NbBundle.getMessage(AddPropertyPanel.class, "AddPropertyPanel.previewEditorPane.toolTipText")); // NOI18N
        previewScrollPane.setViewportView(previewEditorPane);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(nameLabel)
                            .add(typeLabel))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(layout.createSequentialGroup()
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(staticCheckBox)
                                    .add(privateRadioButton))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(layout.createSequentialGroup()
                                        .add(finalCheckBox)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(finalRequiresInitializerTipLabel))
                                    .add(layout.createSequentialGroup()
                                        .add(packageRadioButton)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(protectedRadioButton)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(publicRadioButton))))
                            .add(layout.createSequentialGroup()
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(layout.createSequentialGroup()
                                        .add(boundCheckBox)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(boundTipLabel)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(propNameTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 453, Short.MAX_VALUE)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(propNameTipLabel))
                                    .add(layout.createSequentialGroup()
                                        .add(21, 21, 21)
                                        .add(vetoableCheckBox)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(vetoableTipLabel))
                                    .add(layout.createSequentialGroup()
                                        .add(generateGetterAndSetterRadioButton)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(generateGetterRadioButton)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(generateSetterRadioButton))
                                    .add(generateJavadocCheckBox)
                                    .add(indexedCheckBox)
                                    .add(layout.createSequentialGroup()
                                        .add(generatePropertyChangeSupportCheckBox)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                        .add(generateVetoablePropertyChangeSupportCheckBox))))
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                                .add(typeComboBox, 0, 0, Short.MAX_VALUE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(browseTypeButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 86, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(layout.createSequentialGroup()
                                .add(nameTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 165, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(equalsLabel)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(initializerTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 366, Short.MAX_VALUE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(semicolonLabel))))
                    .add(layout.createSequentialGroup()
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(previewLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(previewScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 580, Short.MAX_VALUE)))
                .addContainerGap())
        );

        layout.linkSize(new java.awt.Component[] {nameLabel, previewLabel, typeLabel}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        layout.linkSize(new java.awt.Component[] {packageRadioButton, privateRadioButton, protectedRadioButton, publicRadioButton, staticCheckBox}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(nameLabel)
                    .add(nameTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(equalsLabel)
                    .add(semicolonLabel)
                    .add(initializerTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(typeLabel)
                    .add(browseTypeButton)
                    .add(typeComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(packageRadioButton)
                            .add(protectedRadioButton)
                            .add(publicRadioButton)
                            .add(privateRadioButton))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(staticCheckBox))
                    .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(finalCheckBox)
                        .add(finalRequiresInitializerTipLabel)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(generateGetterAndSetterRadioButton)
                    .add(generateSetterRadioButton)
                    .add(generateGetterRadioButton))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(generateJavadocCheckBox)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(boundCheckBox)
                    .add(boundTipLabel)
                    .add(propNameTipLabel)
                    .add(propNameTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 21, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(vetoableCheckBox)
                    .add(vetoableTipLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(indexedCheckBox)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(generatePropertyChangeSupportCheckBox)
                    .add(generateVetoablePropertyChangeSupportCheckBox))
                .add(8, 8, 8)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(previewLabel)
                    .add(previewScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 353, Short.MAX_VALUE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents
    private void finalCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_finalCheckBoxActionPerformed
        generateGetterAndSetterRadioButton.setEnabled(!finalCheckBox.isSelected());
        generateSetterRadioButton.setEnabled(!finalCheckBox.isSelected());
        if (finalCheckBox.isSelected()) {
            generateGetterRadioButton.setSelected(true);
        }
        showPreview();
    }//GEN-LAST:event_finalCheckBoxActionPerformed

    private void privateRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_privateRadioButtonActionPerformed
        showPreview();
    }//GEN-LAST:event_privateRadioButtonActionPerformed

    private void packageRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_packageRadioButtonActionPerformed
        showPreview();
}//GEN-LAST:event_packageRadioButtonActionPerformed

    private void protectedRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_protectedRadioButtonActionPerformed
        showPreview();
    }//GEN-LAST:event_protectedRadioButtonActionPerformed

    private void publicRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_publicRadioButtonActionPerformed
        showPreview();
}//GEN-LAST:event_publicRadioButtonActionPerformed

    private void staticCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_staticCheckBoxActionPerformed
        showPreview();
    }//GEN-LAST:event_staticCheckBoxActionPerformed

    private void indexedCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_indexedCheckBoxActionPerformed
        showPreview();
    }//GEN-LAST:event_indexedCheckBoxActionPerformed

    private void typeComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_typeComboBoxActionPerformed
        showPreview();
    }//GEN-LAST:event_typeComboBoxActionPerformed

    private void generateJavadocCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_generateJavadocCheckBoxActionPerformed
        showPreview();
    }//GEN-LAST:event_generateJavadocCheckBoxActionPerformed

    private void boundCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_boundCheckBoxActionPerformed
        vetoableCheckBox.setEnabled(boundCheckBox.isSelected());
        showPreview();
    }//GEN-LAST:event_boundCheckBoxActionPerformed

    private void vetoableCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_vetoableCheckBoxActionPerformed
        showPreview();
    }//GEN-LAST:event_vetoableCheckBoxActionPerformed

    private void generateGetterAndSetterRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_generateGetterAndSetterRadioButtonActionPerformed
        showPreview();
    }//GEN-LAST:event_generateGetterAndSetterRadioButtonActionPerformed

    private void generateSetterRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_generateSetterRadioButtonActionPerformed
        showPreview();
}//GEN-LAST:event_generateSetterRadioButtonActionPerformed

    private void generateGetterRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_generateGetterRadioButtonActionPerformed
        showPreview();
}//GEN-LAST:event_generateGetterRadioButtonActionPerformed

    private void generateVetoablePropertyChangeSupportCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_generateVetoablePropertyChangeSupportCheckBoxActionPerformed
        showPreview();
    }//GEN-LAST:event_generateVetoablePropertyChangeSupportCheckBoxActionPerformed

    private void generatePropertyChangeSupportCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_generatePropertyChangeSupportCheckBoxActionPerformed
        showPreview();
    }//GEN-LAST:event_generatePropertyChangeSupportCheckBoxActionPerformed

    private void browseTypeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseTypeButtonActionPerformed
        ElementHandle<TypeElement> type = TypeElementFinder.find(ClasspathInfo.create(fileObject), null);

        if (type != null) {
            String fqn = type.getQualifiedName().toString();

            typeComboBox.setSelectedItem(fqn);
        }
    }//GEN-LAST:event_browseTypeButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup accessGroup;
    private javax.swing.JCheckBox boundCheckBox;
    private javax.swing.JLabel boundTipLabel;
    private javax.swing.JButton browseTypeButton;
    private javax.swing.JLabel equalsLabel;
    private javax.swing.JCheckBox finalCheckBox;
    private javax.swing.JLabel finalRequiresInitializerTipLabel;
    private javax.swing.JRadioButton generateGetterAndSetterRadioButton;
    private javax.swing.JRadioButton generateGetterRadioButton;
    private javax.swing.JCheckBox generateJavadocCheckBox;
    private javax.swing.JCheckBox generatePropertyChangeSupportCheckBox;
    private javax.swing.JRadioButton generateSetterRadioButton;
    private javax.swing.JCheckBox generateVetoablePropertyChangeSupportCheckBox;
    private javax.swing.ButtonGroup getterSetterGroup;
    private javax.swing.JCheckBox indexedCheckBox;
    private javax.swing.JTextField initializerTextField;
    private javax.swing.JLabel nameLabel;
    private javax.swing.JTextField nameTextField;
    private javax.swing.JRadioButton packageRadioButton;
    private javax.swing.JEditorPane previewEditorPane;
    private javax.swing.JLabel previewLabel;
    private javax.swing.JScrollPane previewScrollPane;
    private javax.swing.JRadioButton privateRadioButton;
    private javax.swing.JTextField propNameTextField;
    private javax.swing.JLabel propNameTipLabel;
    private javax.swing.JRadioButton protectedRadioButton;
    private javax.swing.JRadioButton publicRadioButton;
    private javax.swing.JLabel semicolonLabel;
    private javax.swing.JCheckBox staticCheckBox;
    private javax.swing.JComboBox typeComboBox;
    private javax.swing.JLabel typeLabel;
    private javax.swing.JCheckBox vetoableCheckBox;
    private javax.swing.JLabel vetoableTipLabel;
    // End of variables declaration//GEN-END:variables
}
