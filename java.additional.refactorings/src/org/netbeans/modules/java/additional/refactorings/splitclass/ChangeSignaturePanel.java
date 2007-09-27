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
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */
package org.netbeans.modules.java.additional.refactorings.splitclass;

import java.awt.event.FocusEvent;
import org.netbeans.modules.java.additional.refactorings.visitors.ParameterRenamePolicy;
import org.netbeans.modules.java.additional.refactorings.splitclass.*;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.FocusListener;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.lang.model.element.ExecutableElement;
import javax.swing.AbstractButton;
import javax.swing.JRadioButton;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.netbeans.api.java.source.ElementHandle;
import org.netbeans.modules.refactoring.spi.ui.CustomRefactoringPanel;
import org.openide.awt.Mnemonics;
import org.openide.util.Mutex;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

/**
 *
 * @author  Tim Boudreau
 */
class ChangeSignaturePanel extends javax.swing.JPanel implements CustomRefactoringPanel, ListSelectionListener, DocumentListener, ParameterTableModel.UI, FocusListener {
    private final ChangeSignatureUI ui;
    private static final String PROP_POLICY = "renamePolicy";
    public ChangeSignaturePanel(ChangeSignatureUI ui) {
        this.ui = ui;
        initComponents();
        jTable1.setEnabled(false);
        jTable1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jTable1.getSelectionModel().addListSelectionListener(this);
        Component[] c = getComponents();
        for (int i = 0; i < c.length; i++) {
            if (c[i] instanceof AbstractButton) {
                Mnemonics.setLocalizedText((AbstractButton) c[i],
                        ((AbstractButton) c[i]).getText());
            }
            if (c[i] instanceof JTextField) {
                ((JTextField) c[i]).addFocusListener(this);
                ((JTextField) c[i]).getDocument().addDocumentListener(this);
            }
        }
        jdk6hackJtable(jTable1);
        jTable1.setSurrendersFocusOnKeystroke(true);
        Font f = jTable1.getFont();
        Font nue = new Font("Monospaced", f.getStyle(), f.getSize()); //NOI18N
        jTable1.setFont(nue);
        dontRenameButton.putClientProperty(PROP_POLICY, ParameterRenamePolicy.DO_NOT_RENAME);
        renameIfSameButton.putClientProperty(PROP_POLICY, ParameterRenamePolicy.RENAME_IF_SAME);
        alwaysRenameButton.putClientProperty(PROP_POLICY, ParameterRenamePolicy.RENAME_UNLESS_CONFLICT);
        problemLabel.setVisible(false);
        jTable1.putClientProperty("JTable.autoStartsEdit", Boolean.TRUE); //NOI18N
    }
    
    private static void jdk6hackJtable(JTable tbl) {
        //Calls a couple of methods new in JDK 6
        try {
            Method m = JTable.class.getDeclaredMethod("setAutoCreateRowSorter", Boolean.TYPE);
            m.invoke (tbl, Boolean.FALSE);
            m = JTable.class.getDeclaredMethod("setFillsViewportHeight", Boolean.TYPE);
            m.invoke (tbl, Boolean.TRUE);
        } catch (Exception e) {
            //do nothing
        }
    }
    
    ParameterRenamePolicy getRenamePolicy() {
        Component[] c = getComponents();
        for (int i = 0; i < c.length; i++) {
            if (c[i] instanceof JRadioButton && ((JRadioButton) c[i]).isSelected()) {
                return (ParameterRenamePolicy) ((JRadioButton) c[i]).getClientProperty(PROP_POLICY);
            }
        }
        throw new AssertionError("No button selected");
    }
    
    private boolean mayHaveOverrides = false;
    public void setMayHaveOverrides(boolean val) {
        this.mayHaveOverrides = val;
    }
    
    String origMethodName;
    void setMethodName(final String s) {
        origMethodName = s;
        Runnable r = new Runnable() {
            public void run() {
                methodNameField.setText(s);
            }
        };
        Mutex.EVENT.readAccess(r);
    }
    
    public JTable getTable() {
        return jTable1;
    }
    
    public List <Parameter> getOriginals() {
        return originals;
    }
    
    String origMethodType;
    void setMethodType(final String s) {
        origMethodType = s;
        Runnable r = new Runnable() {
            public void run() {
                returnTypeField.setText(s);
            }
        };
        Mutex.EVENT.readAccess(r);
    }
    
    boolean anyChanges() {
        boolean result = false;
        if (jTable1.getModel() instanceof ParameterTableModel) {
            ParameterTableModel tm = (ParameterTableModel) jTable1.getModel();
            List <Parameter> params = tm.getParameters();
            result = originals.size() != params.size();
            if (!result) {
                int max = params.size();
                for (int i=0; i < max; i++) {
                    result |= originals.get(i) != params.get(i);
                    if (result) {
                        break;
                    }
                }
            }
            if (!result) {
                for (Parameter p : params) {
                    result |= p.isModified();
                    if (result) break;
                }
                if (!result && origMethodName != null) {
                    result |= !origMethodName.equals(
                            methodNameField.getText().trim());
                }
                if (!result && origMethodType != null) {
                    result |= !origMethodType.equals(
                            returnTypeField.getText().trim());
                }
            }
        }
        return result;
    }
    
    boolean anyRenamesOrNewParameters() {
        boolean result = false;
        if (jTable1.getModel() instanceof ParameterTableModel) {
            ParameterTableModel tm = (ParameterTableModel) jTable1.getModel();
            List <Parameter> params = tm.getParameters();
            for (Parameter p : params) {
                result |= p.isNameChanged() | p.isNew();
                if (result) {
                    break;
                }
            }
        }
        return result;
    }
    
    public void addNotify() {
        super.addNotify();
        ui.init();
    }
    
    public String getProblemText() {
        return problemLabel.getText().trim().length() == 0 ? null :
            problemLabel.getText();
    }
    
    void setProblemText(String s) {
        boolean hadText = getProblemText() != null;
        String txt = s == null ? "   " : s;
        problemLabel.setText(txt);
        boolean hasText = getProblemText() != null;
        if (hadText != hasText) {
            ui.change();
        }
    }
    
    void setProgress(final int val) {
        Runnable r = new Runnable() {
            public void run() {
                progress.setValue(val);
            }
        };
        Mutex.EVENT.readAccess(r);
    }
    
    Collection <ElementHandle<ExecutableElement>> overrides = Collections.<ElementHandle<ExecutableElement>>emptyList();
    void setOverrides(Collection <ElementHandle<ExecutableElement>> c) {
        this.overrides = overrides;
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        problemLabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        progress = new javax.swing.JProgressBar();
        moveUpButton = new javax.swing.JButton();
        moveDownButton = new javax.swing.JButton();
        addButton = new javax.swing.JButton();
        removeButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        methodNameLbl = new javax.swing.JLabel();
        methodNameField = new javax.swing.JTextField();
        returnTypeLbl = new javax.swing.JLabel();
        returnTypeField = new javax.swing.JTextField();
        dontRenameButton = new javax.swing.JRadioButton();
        renameIfSameButton = new javax.swing.JRadioButton();
        alwaysRenameButton = new javax.swing.JRadioButton();
        jLabel2 = new javax.swing.JLabel();
        defaultValueField = new javax.swing.JTextField();
        refactorFromBase = new javax.swing.JCheckBox();

        problemLabel.setForeground(javax.swing.UIManager.getDefaults().getColor("nb.errorForeground"));
        problemLabel.setText(org.openide.util.NbBundle.getMessage(ChangeSignaturePanel.class, "problemLabel.text")); // NOI18N

        jScrollPane1.setEnabled(false);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jTable1.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        jTable1.setNextFocusableComponent(addButton);
        jTable1.setSurrendersFocusOnKeystroke(true);
        jScrollPane1.setViewportView(jTable1);

        moveUpButton.setText(org.openide.util.NbBundle.getMessage(ChangeSignaturePanel.class, "jButton1.text")); // NOI18N
        moveUpButton.setEnabled(false);
        moveUpButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                moveUpButtonActionPerformed(evt);
            }
        });

        moveDownButton.setText(org.openide.util.NbBundle.getMessage(ChangeSignaturePanel.class, "jButton2.text")); // NOI18N
        moveDownButton.setEnabled(false);
        moveDownButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                moveDownButtonActionPerformed(evt);
            }
        });

        addButton.setText(org.openide.util.NbBundle.getMessage(ChangeSignaturePanel.class, "jButton3.text")); // NOI18N
        addButton.setEnabled(false);
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });

        removeButton.setText(org.openide.util.NbBundle.getMessage(ChangeSignaturePanel.class, "jButton4.text")); // NOI18N
        removeButton.setEnabled(false);
        removeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeButtonActionPerformed(evt);
            }
        });

        jLabel1.setFont(jLabel1.getFont().deriveFont(jLabel1.getFont().getStyle() | java.awt.Font.BOLD));
        jLabel1.setText(org.openide.util.NbBundle.getMessage(ChangeSignaturePanel.class, "jLabel1.text")); // NOI18N
        jLabel1.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, new java.awt.Color(0, 0, 0)));

        jScrollPane2.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jScrollPane2.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane2.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        jScrollPane2.setViewportBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));

        jTextArea1.setBackground(javax.swing.UIManager.getDefaults().getColor("control"));
        jTextArea1.setColumns(20);
        jTextArea1.setEditable(false);
        jTextArea1.setFont(problemLabel.getFont());
        jTextArea1.setRows(5);
        jTextArea1.setText(org.openide.util.NbBundle.getMessage(ChangeSignaturePanel.class, "jTextArea1.text")); // NOI18N
        jScrollPane2.setViewportView(jTextArea1);

        methodNameLbl.setLabelFor(methodNameField);
        methodNameLbl.setText(org.openide.util.NbBundle.getMessage(ChangeSignaturePanel.class, "jLabel2.text")); // NOI18N

        methodNameField.setText(org.openide.util.NbBundle.getMessage(ChangeSignaturePanel.class, "jTextField1.text")); // NOI18N

        returnTypeLbl.setLabelFor(returnTypeField);
        returnTypeLbl.setText(org.openide.util.NbBundle.getMessage(ChangeSignaturePanel.class, "jLabel3.text")); // NOI18N

        returnTypeField.setText(org.openide.util.NbBundle.getMessage(ChangeSignaturePanel.class, "jTextField2.text")); // NOI18N

        buttonGroup1.add(dontRenameButton);
        dontRenameButton.setSelected(true);
        dontRenameButton.setText(org.openide.util.NbBundle.getMessage(ChangeSignaturePanel.class, "jRadioButton1.text")); // NOI18N
        dontRenameButton.setEnabled(false);
        dontRenameButton.setMargin(new java.awt.Insets(0, 0, 0, 0));

        buttonGroup1.add(renameIfSameButton);
        renameIfSameButton.setText(org.openide.util.NbBundle.getMessage(ChangeSignaturePanel.class, "jRadioButton2.text")); // NOI18N
        renameIfSameButton.setEnabled(false);
        renameIfSameButton.setMargin(new java.awt.Insets(0, 0, 0, 0));

        buttonGroup1.add(alwaysRenameButton);
        alwaysRenameButton.setText(org.openide.util.NbBundle.getMessage(ChangeSignaturePanel.class, "jRadioButton3.text")); // NOI18N
        alwaysRenameButton.setEnabled(false);
        alwaysRenameButton.setMargin(new java.awt.Insets(0, 0, 0, 0));

        jLabel2.setText(org.openide.util.NbBundle.getMessage(ChangeSignaturePanel.class, "jLabel2.text_1")); // NOI18N
        jLabel2.setEnabled(false);

        defaultValueField.setText(org.openide.util.NbBundle.getMessage(ChangeSignaturePanel.class, "jTextField1.text_1")); // NOI18N
        defaultValueField.setEnabled(false);

        refactorFromBase.setText(org.openide.util.NbBundle.getMessage(ChangeSignaturePanel.class, "jCheckBox1.text")); // NOI18N
        refactorFromBase.setEnabled(false);
        refactorFromBase.setMargin(new java.awt.Insets(0, 0, 0, 0));

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, progress, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 524, Short.MAX_VALUE)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(returnTypeLbl)
                    .add(methodNameLbl))
                .add(16, 16, 16)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(methodNameField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 421, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .add(returnTypeField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 116, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jLabel2)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(defaultValueField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 156, Short.MAX_VALUE))))
            .add(jLabel1)
            .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 524, Short.MAX_VALUE)
            .add(dontRenameButton)
            .add(renameIfSameButton)
            .add(alwaysRenameButton)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                        .add(refactorFromBase)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 168, Short.MAX_VALUE)
                        .add(problemLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 50, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 405, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(removeButton)
                        .add(addButton))
                    .add(moveUpButton)
                    .add(moveDownButton)))
        );

        layout.linkSize(new java.awt.Component[] {addButton, moveDownButton, moveUpButton, removeButton}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .add(jLabel1)
                .add(17, 17, 17)
                .add(jScrollPane2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 38, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(methodNameLbl)
                    .add(methodNameField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(returnTypeLbl)
                    .add(returnTypeField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel2)
                    .add(defaultValueField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(12, 12, 12)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(addButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(removeButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(moveUpButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(moveDownButton))
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 199, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(refactorFromBase)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(dontRenameButton))
                    .add(problemLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 11, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(renameIfSameButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(alwaysRenameButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(progress, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(17, 17, 17))
        );
    }// </editor-fold>//GEN-END:initComponents
    
private void moveDownButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_moveDownButtonActionPerformed
    if (jTable1.getModel() instanceof ParameterTableModel) {
        ParameterTableModel tm = (ParameterTableModel) jTable1.getModel();
        int ix = Math.max(0, jTable1.getSelectedRow());
        tm.moveDown(ix);
    }
}//GEN-LAST:event_moveDownButtonActionPerformed

private void moveUpButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_moveUpButtonActionPerformed
    if (jTable1.getModel() instanceof ParameterTableModel) {
        ParameterTableModel tm = (ParameterTableModel) jTable1.getModel();
        int ix = Math.max(0, jTable1.getSelectedRow());
        tm.moveUp(ix);
    }
}//GEN-LAST:event_moveUpButtonActionPerformed

private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
    if (jTable1.getModel() instanceof ParameterTableModel) {
        ParameterTableModel tm = (ParameterTableModel) jTable1.getModel();
        int ix = Math.max(0, jTable1.getSelectedRow());
        Parameter nue = new Parameter("param", "");
        tm.add(nue, ix);
    }
}//GEN-LAST:event_addButtonActionPerformed

    private void removeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeButtonActionPerformed
        if (jTable1.getModel() instanceof ParameterTableModel) {
            ParameterTableModel tm = (ParameterTableModel) jTable1.getModel();
            int ix = Math.max(0, jTable1.getSelectedRow());
            tm.remove(ix);
        }
}//GEN-LAST:event_removeButtonActionPerformed
        // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JRadioButton alwaysRenameButton;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JTextField defaultValueField;
    private javax.swing.JRadioButton dontRenameButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextField methodNameField;
    private javax.swing.JLabel methodNameLbl;
    private javax.swing.JButton moveDownButton;
    private javax.swing.JButton moveUpButton;
    private javax.swing.JLabel problemLabel;
    private javax.swing.JProgressBar progress;
    private javax.swing.JCheckBox refactorFromBase;
    private javax.swing.JButton removeButton;
    private javax.swing.JRadioButton renameIfSameButton;
    private javax.swing.JTextField returnTypeField;
    private javax.swing.JLabel returnTypeLbl;
    // End of variables declaration//GEN-END:variables
    
    public void initialize() {
        System.err.println("Initialize");
        //never called. curious.
        //        ui.init();
    }
    
    public Component getComponent() {
        return this;
    }
    
    public List <Parameter> getNewParameters() {
        if (jTable1.getModel() instanceof ParameterTableModel) {
            ParameterTableModel tm = (ParameterTableModel) jTable1.getModel();
            return tm.getParameters();
        } else {
            return Collections.<Parameter>emptyList();
        }
    }
    
    public void change() {
        boolean problem = false;
        String mname = methodNameField.getText().trim();
        if (!Utilities.isJavaIdentifier(mname)) {
            setProblemText(NbBundle.getMessage(ChangeSignaturePanel.class,
                    "MSG_BAD_NAME", mname)); //NOI18N
            problem = true;
        }
        String type = returnTypeField.getText().trim();
        if (!problem && !Utilities.isJavaIdentifier(type) && !"void".equals(type) && !isPrimitiveTypeName(type)) {
            boolean qualName = !isQualifiedTypeName(type);
            if (!qualName || (qualName && !checkQualifiedTypeName(type))) {
                setProblemText(NbBundle.getMessage(ChangeSignaturePanel.class,
                        "MSG_BAD_TYPE", type)); //NOI18N
                problem = true;
            }
        }
        
        if (!problem && !(jTable1.getModel() instanceof ParameterTableModel)) {
            setProblemText(NbBundle.getMessage(ChangeSignaturePanel.class,
                    "MSG_INITIALIZING")); //NOI18N
        } else if (jTable1.getModel() instanceof ParameterTableModel) {
            ParameterTableModel tm = (ParameterTableModel) jTable1.getModel();
            Set <String> names = new HashSet <String>();
            List <Parameter> params = tm.getParameters();
            int ix = 0;
            for (Parameter p : params) {
                String nm = p.getName();
                if (names.contains(nm)) {
                    setProblemText(NbBundle.getMessage(ChangeSignaturePanel.class,
                            "MSG_DUPLICATE_NAMES", nm)); //NOI18N
                    problem = true;
                    break;
                }
                if (nm != null && !Utilities.isJavaIdentifier(nm)) {
                    setProblemText(NbBundle.getMessage(ChangeSignaturePanel.class,
                            "MSG_BAD_NAME", nm)); //NOI18N
                    problem = true;
                    break;
                }
                if (nm == null) {
                    setProblemText(NbBundle.getMessage(ChangeSignaturePanel.class,
                            "MSG_NO_NAME", ix)); //NOI18N
                    problem = true;
                    break;
                }
                if (p.isNew() && p.getDefaultValue() == null) {
                    setProblemText(NbBundle.getMessage(ChangeSignaturePanel.class,
                            "MSG_NO_DEFAULT_VALUE", nm)); //NOI18N
                    problem = true;
                    break;
                }
                String typeName = p.getTypeName();
                if (typeName == null && p.isNew()) {
                    setProblemText(NbBundle.getMessage(ChangeSignaturePanel.class,
                            "MSG_NO_TYPE", nm)); //NOI18N
                    problem = true;
                    break;
                }
                if (typeName != null && !Utilities.isJavaIdentifier(typeName) && !isPrimitiveTypeName(typeName) && !isQualifiedTypeName(typeName)) {
                    setProblemText(NbBundle.getMessage(ChangeSignaturePanel.class,
                            "MSG_BAD_TYPE", typeName)); //NOI18N
                    problem = true;
                    break;
                }
                String defValue = p.isNew() ? p.getDefaultValue() : null;
                if (defValue != null) {
                    if (!Utilities.isJavaIdentifier(defValue) && !"null".equals(defValue)) {
                        if (isPrimitiveTypeName(typeName) != isPrimitiveTypeEntry(typeName, defValue)) {
                            setProblemText(NbBundle.getMessage(ChangeSignaturePanel.class,
                                    "MSG_BAD_DEFAULT_VALUE", defValue, nm)); //NOI18N
                            problem = true;
                            break;
                        }
                    }
                    if ("null".equals(defValue) && p.isNew() && isPrimitiveTypeEntry(typeName, defValue)) {
                        tm.setValueAt("-1", ix, 2); //NOI18N
                        //avoid looping
                        return;
                    }
                }
                names.add(nm);
                ix ++;
            }
        }
        if (!problem && !anyChanges()) {
            setProblemText(NbBundle.getMessage(ChangeSignaturePanel.class,
                    "MSG_NOTHING_TO_DO")); //NOI18N
            problem = true;
        }
        if (!problem) {
            setProblemText(null);
        }
        if (mayHaveOverrides) {
            boolean activateRadioButtons = anyRenamesOrNewParameters();
            dontRenameButton.setEnabled(activateRadioButtons);
            renameIfSameButton.setEnabled(activateRadioButtons);
            alwaysRenameButton.setEnabled(activateRadioButtons);
        }
        if (!overrides.isEmpty()) {
            refactorFromBase.setEnabled(true);
        }
        String retType = getReturnType();
        if (retType != null && !"void".equals(retType) && !retType.equals(origMethodType)) {
            defaultValueField.setEnabled(true);
        } else {
            defaultValueField.setEnabled(false);
        }
        //        if (overrides.size() > 1) {
        //            problem = true;
        //            setProblemText(NbBundle.getMessage(ChangeSignaturePanel.class,
        //                                "MSG_MULTIPLE_INTERFACES"));
        //        }
        ui.change();
    }
    
    boolean isRefactorFromBase() {
        return refactorFromBase.isEnabled() && refactorFromBase.isSelected();
    }
    
    static boolean isPrimitiveTypeEntry(String typeName, String defValue) {
        if ("char".equals(typeName)) { //NOI18N
            return defValue.startsWith("'") && defValue.endsWith("'") &&
                    defValue.length() == 3;
        } else {
            String upc = defValue.toUpperCase();
            if (upc.startsWith("0x") || upc.endsWith("L") || upc.endsWith("F") || upc.endsWith("D")) { //NOI18N
                upc = upc.substring(0, upc.length());
            }
            try {
                Integer.parseInt(upc);
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return true;
    }
    
    static boolean isPrimitiveTypeName(String s) {
        String[] prims = new String[] {
            "int", "short", "byte", "char", "float", "double", "long", //NOI18N
        };
        return Arrays.asList(prims).indexOf(s) >= 0;
    }
    
    private boolean isQualifiedTypeName(String s) {
        return s.indexOf(".") >= 0;
    }
    
    List <Parameter> originals;
    void setParameters(final List <Parameter> descs) {
        Runnable r = new Runnable() {
            public void run() {
                originals = new ArrayList <Parameter> (descs);
                ParameterTableModel tm = new ParameterTableModel(descs, ChangeSignaturePanel.this);
                jTable1.setModel(tm);
                jTable1.setEnabled(true);
                boolean hasParams = descs.size() > 0;
                if (hasParams) {
                    jTable1.getSelectionModel().setAnchorSelectionIndex(0);
                    jTable1.getSelectionModel().setLeadSelectionIndex(0);
                }
                moveUpButton.setEnabled(hasParams);
                moveDownButton.setEnabled(hasParams);
                addButton.setEnabled(true);
                removeButton.setEnabled(hasParams);
                progress.setVisible(false);
            }
        };
        Mutex.EVENT.readAccess(r);
    }
    
    public String getMethodName() {
        String curr = methodNameField.getText().trim();
        return curr.length() == 0 ? null :
            curr.equals(origMethodName) ? null : curr;
    }
    
    public String getReturnType() {
        String curr = returnTypeField.getText().trim();
        return curr.length() == 0 ? null :
            curr.equals(origMethodType) ? null : curr;
    }
    
    public void valueChanged(ListSelectionEvent e) {
        int ix = jTable1.getSelectedRow();
        int max = jTable1.getRowCount();
        if (jTable1.getModel() instanceof ParameterTableModel) {
            moveUpButton.setEnabled(ix > 0);
            moveDownButton.setEnabled(ix >= 0 && ix < max - 1 && max > 1);
            removeButton.setEnabled(true);
            addButton.setEnabled(true);
        }
    }
    
    private boolean checkQualifiedTypeName(String nm) {
        String[] s = nm.split(".");
        boolean result = true;
        for (int i = 0; i < s.length; i++) {
            result &= Utilities.isJavaIdentifier(s[i]);
            if (!result) break;
        }
        return result;
    }
    
    public void insertUpdate(DocumentEvent e) {
        change();
    }
    
    public void removeUpdate(DocumentEvent e) {
        change();
    }
    
    public void changedUpdate(DocumentEvent e) {
        change();
    }
    
    public void focusGained(FocusEvent e) {
        JTextField jtf = (JTextField) e.getComponent();
        jtf.selectAll();
    }
    
    public void focusLost(FocusEvent e) {
        //do nothing
    }
}
