/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
/*
 * SaveAsUI.java
 *
 * Created on August 5, 2007, 5:22 PM
 */

package org.netbeans.modules.perspective.ui;

import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.DefaultListModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.netbeans.modules.perspective.PerspectiveManager;
import org.netbeans.modules.perspective.utils.CurrentPerspectiveReader;
import org.netbeans.modules.perspective.views.Perspective;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.windows.WindowManager;

/**
 *
 * @author  Anurdha
 */
public class SaveAsUI extends javax.swing.JDialog {

    private static final long serialVersionUID = 1l;
    private DefaultListModel defaultListModel = new DefaultListModel();
    private static String Here = "HERE";
    private Perspective selected;

    /** Creates new form SaveAsUI */
    private SaveAsUI() {
        super(WindowManager.getDefault().getMainWindow(), true);
        initComponents();
        modeList.setModel(defaultListModel);
        modeList.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent e) {

                Perspective perspective = (Perspective) modeList.getSelectedValue();
                if (perspective != null) {
                    txtName.setText(perspective.getName());
                    selected = perspective;

                    before.setSelected(perspective.isBeforeSeparator());
                    after.setSelected(perspective.isAfterSeparator());
                    loadcmbPerspectives();
                    cmbPosition.setSelectedIndex(perspective.getIndex());
                } else {
                    selected = null;
                    loadcmbPerspectives();
                }
                
                validateName();
            }
        });
        loadcmbPerspectives();
        loadPerspectives();
        validateName();
    }

    private void clear() {
        before.setSelected(false);
        after.setSelected(false);
    }

    private void loadPerspectives() {
        defaultListModel.clear();
        List<Perspective> perspectives = PerspectiveManager.getInstance().getPerspectives();
        for (Perspective perspective : perspectives) {
            defaultListModel.addElement(perspective);
        }
    }

    private void loadcmbPerspectives() {
        List<Perspective> perspectives = PerspectiveManager.getInstance().getPerspectives();
        cmbPosition.removeAllItems();
        if (perspectives.size() == 0) {
            cmbPosition.addItem(Here);
            return;
        }
        String prev = "";
        for (Perspective perspective : perspectives) {
            if (perspective.equals(selected)) {
                continue;
            }
            if (prev.isEmpty()) {
                cmbPosition.addItem(Here + " - " + perspective.getName());
            } else {
                cmbPosition.addItem(prev + Here + " - " + perspective.getName());
            }
            prev = perspective.getName() + " - ";
        }
        cmbPosition.addItem(prev + Here);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnCancel = new javax.swing.JButton();
        btnOK = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        modeList = new javax.swing.JList();
        lblName = new javax.swing.JLabel();
        txtName = new javax.swing.JTextField();
        lblDescription = new javax.swing.JLabel();
        lblPosition = new javax.swing.JLabel();
        cmbPosition = new javax.swing.JComboBox();
        after = new javax.swing.JCheckBox();
        before = new javax.swing.JCheckBox();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(org.openide.util.NbBundle.getMessage(SaveAsUI.class, "SaveAsUI.title")); // NOI18N
        setResizable(false);

        btnCancel.setAction(new CancelAction());
        btnCancel.setText(org.openide.util.NbBundle.getMessage(SaveAsUI.class, "SaveAsUI.btnCancel.text")); // NOI18N

        btnOK.setAction(new SaveAs());
        btnOK.setText(org.openide.util.NbBundle.getMessage(SaveAsUI.class, "SaveAsUI.btnOK.text")); // NOI18N

        modeList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane1.setViewportView(modeList);

        lblName.setText(org.openide.util.NbBundle.getMessage(SaveAsUI.class, "SaveAsUI.lblName.text")); // NOI18N

        txtName.setText(org.openide.util.NbBundle.getMessage(SaveAsUI.class, "SaveAsUI.txtName.text")); // NOI18N
        txtName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtNameKeyReleased(evt);
            }
        });

        lblDescription.setText(org.openide.util.NbBundle.getMessage(SaveAsUI.class, "SaveAsUI.lblDescription.text")); // NOI18N

        lblPosition.setText(org.openide.util.NbBundle.getMessage(SaveAsUI.class, "SaveAsUI.lblPosition.text_1")); // NOI18N

        after.setText(org.openide.util.NbBundle.getMessage(SaveAsUI.class, "SaveAsUI.after.text")); // NOI18N
        after.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        after.setMargin(new java.awt.Insets(0, 0, 0, 0));

        before.setText(org.openide.util.NbBundle.getMessage(SaveAsUI.class, "SaveAsUI.before.text")); // NOI18N
        before.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        before.setMargin(new java.awt.Insets(0, 0, 0, 0));

        jLabel1.setText(org.openide.util.NbBundle.getMessage(SaveAsUI.class, "SaveAsUI.jLabel1.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 272, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(btnOK)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnCancel))
                    .addComponent(lblDescription)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblName, javax.swing.GroupLayout.DEFAULT_SIZE, 65, Short.MAX_VALUE)
                            .addComponent(lblPosition, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 65, Short.MAX_VALUE))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cmbPosition, javax.swing.GroupLayout.Alignment.TRAILING, 0, 207, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(before)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(after))
                            .addComponent(txtName, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 207, Short.MAX_VALUE)))
                    .addComponent(jLabel1))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnCancel, btnOK});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblDescription)
                .addGap(8, 8, 8)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblName)
                    .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblPosition)
                    .addComponent(cmbPosition, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(before)
                    .addComponent(after))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 22, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCancel)
                    .addComponent(btnOK))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtNameKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNameKeyReleased
        validateName();
        validateExist();
    }//GEN-LAST:event_txtNameKeyReleased

    public static void createSaveAsUI() {
        SaveAsUI saveAsUI = new SaveAsUI();
        saveAsUI.pack();
        saveAsUI.setLocationRelativeTo(null);
        saveAsUI.setVisible(true);
    }

    private class CancelAction extends AbstractAction {

        private static final long serialVersionUID = 1l;
        Perspective mode;

        public CancelAction() {
            putValue(NAME, "Cancel");
        }

        public void actionPerformed(ActionEvent e) {
            dispose();
        }
    }

    private void validateName() {
        if (txtName.getText().isEmpty()) {
            btnOK.setEnabled(false);
        } else {
            btnOK.setEnabled(true);
        }
    }

    private void validateExist() {
        String id = txtName.getText();
        Perspective perspective = PerspectiveManager.getInstance().findPerspectiveByID(id);
        if (perspective != null) {
            modeList.setSelectedValue(perspective, true);
        } else {
            modeList.clearSelection();
            clear();
        }
    }

    private void saveAsMutilMode() {
        String id = txtName.getText();
        Perspective perspective = PerspectiveManager.getInstance().findPerspectiveByID(id);
        if (perspective != null) {
            NotifyDescriptor d = new NotifyDescriptor.Confirmation("'"+id+ "' already exists.Do you want\n overwrite? ", "Overwrite MultiMode View",
                    NotifyDescriptor.YES_NO_OPTION);
            if (DialogDisplayer.getDefault().notify(d) != NotifyDescriptor.YES_OPTION) {
                return;
            }
            PerspectiveManager.getInstance().deregisterPerspective(perspective);
        }
        perspective = new Perspective(txtName.getText().trim(), txtName.getText().trim());

        new CurrentPerspectiveReader(perspective);
        perspective.setBeforeSeparator(before.isSelected());
        perspective.setAfterSeparator(after.isSelected());
        PerspectiveManager.getInstance().registerPerspective(cmbPosition.getSelectedIndex(), perspective);
        PerspectiveManager.getInstance().setSelected(perspective);
        dispose();
    }

    private class SaveAs extends AbstractAction {

        private static final long serialVersionUID = 1l;
        Perspective mode;

        public SaveAs() {
            putValue(NAME, "OK");
        }

        public void actionPerformed(ActionEvent e) {
            saveAsMutilMode();
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox after;
    private javax.swing.JCheckBox before;
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnOK;
    private javax.swing.JComboBox cmbPosition;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblDescription;
    private javax.swing.JLabel lblName;
    private javax.swing.JLabel lblPosition;
    private javax.swing.JList modeList;
    private javax.swing.JTextField txtName;
    // End of variables declaration//GEN-END:variables
}
