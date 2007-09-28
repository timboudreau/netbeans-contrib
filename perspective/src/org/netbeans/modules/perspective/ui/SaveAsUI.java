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
import javax.swing.JButton;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.netbeans.modules.perspective.utils.PerspectiveManagerImpl;
import org.netbeans.modules.perspective.persistence.PerspectivePreferences;
import org.netbeans.modules.perspective.utils.CurrentPerspectiveReader;
import org.netbeans.modules.perspective.views.Perspective;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.NbBundle;
import org.openide.windows.WindowManager;

/**
 *
 * @author  Anurdha
 */
public class SaveAsUI extends javax.swing.JDialog {

    private static final long serialVersionUID = 1l;
    private DefaultListModel defaultListModel = new DefaultListModel();
    private static String Here = NbBundle.getMessage(SaveAsUI.class,"HERE");
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
                    txtName.setText(perspective.getAlias());
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
        List<Perspective> perspectives = PerspectiveManagerImpl.getInstance().getPerspectives();
        for (Perspective perspective : perspectives) {
            defaultListModel.addElement(perspective);
        }
    }

    private void loadcmbPerspectives() {
        List<Perspective> perspectives = PerspectiveManagerImpl.getInstance().getPerspectives();
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
                cmbPosition.addItem(Here + " - " + perspective.getAlias());
            } else {
                cmbPosition.addItem(prev + Here + " - " + perspective.getAlias());
            }
            prev = perspective.getAlias() + " - ";
        }
        cmbPosition.addItem(prev + Here);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnCancel = new JButton(new CancelAction());
        btnOK = new JButton(new SaveAs());
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

        org.openide.awt.Mnemonics.setLocalizedText(btnCancel, org.openide.util.NbBundle.getMessage(SaveAsUI.class, "Cancel")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(btnOK, org.openide.util.NbBundle.getMessage(SaveAsUI.class, "OK")); // NOI18N

        modeList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane1.setViewportView(modeList);

        lblName.setText(NbBundle.getMessage(SaveAsUI.class,"Name")); // NOI18N

        txtName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtNameKeyReleased(evt);
            }
        });

        lblDescription.setText(NbBundle.getMessage(SaveAsUI.class,"Saveas_Header")); // NOI18N

        lblPosition.setText(NbBundle.getMessage(SaveAsUI.class,"SaveAsUI.lblPosition.text")); // NOI18N

        after.setText(org.openide.util.NbBundle.getMessage(SaveAsUI.class, "SaveAsUI.after.text")); // NOI18N
        after.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        after.setMargin(new java.awt.Insets(0, 0, 0, 0));

        before.setText(org.openide.util.NbBundle.getMessage(SaveAsUI.class, "SaveAsUI.before.text")); // NOI18N
        before.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        before.setMargin(new java.awt.Insets(0, 0, 0, 0));

        jLabel1.setText(org.openide.util.NbBundle.getMessage(SaveAsUI.class, "SaveAsUI.jLabel1.text")); // NOI18N

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 337, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(btnOK)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(btnCancel))
                    .add(lblDescription)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(lblPosition, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 98, Short.MAX_VALUE)
                            .add(layout.createSequentialGroup()
                                .add(lblName, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 94, Short.MAX_VALUE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)))
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(layout.createSequentialGroup()
                                .add(before)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(after))
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                                    .add(org.jdesktop.layout.GroupLayout.TRAILING, txtName)
                                    .add(org.jdesktop.layout.GroupLayout.TRAILING, cmbPosition, 0, 239, Short.MAX_VALUE)))))
                    .add(jLabel1))
                .addContainerGap())
        );

        layout.linkSize(new java.awt.Component[] {btnCancel, btnOK}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(lblDescription)
                .add(8, 8, 8)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblName)
                    .add(txtName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblPosition)
                    .add(cmbPosition, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(before)
                    .add(after))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(jLabel1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 163, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(btnCancel)
                    .add(btnOK))
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
            putValue(NAME, NbBundle.getMessage(SaveAsUI.class,"Cancel"));
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
        Perspective perspective = PerspectiveManagerImpl.getInstance().findPerspectiveByAlias(id);
        if (perspective != null) {
            modeList.setSelectedValue(perspective, true);
        } else {
            modeList.clearSelection();
            clear();
        }
    }

    private void saveAsMutilMode() {
        Perspective perspective = (Perspective) modeList.getSelectedValue();
        if (perspective != null) {
            NotifyDescriptor d = new NotifyDescriptor.Confirmation("'"+perspective.getAlias()+ NbBundle.getMessage(SaveAsUI.class,"OverWrite_Massage"), NbBundle.getMessage(SaveAsUI.class,"Overwrite_Perspective"),
                    NotifyDescriptor.YES_NO_OPTION);
            if (DialogDisplayer.getDefault().notify(d) != NotifyDescriptor.YES_OPTION) {
                return;
            }
            PerspectiveManagerImpl.getInstance().deregisterPerspective(perspective);
            perspective.clear();
        }else{
         
         perspective = new Perspective(PerspectivePreferences.getInstance().getCustomPerspectiveName(),
                 txtName.getText().trim());
         Perspective selectedPerspective=PerspectiveManagerImpl.getInstance().getSelected();
         perspective.setImagePath(selectedPerspective.getImagePath());
        }
        

        new CurrentPerspectiveReader(perspective);
        perspective.setBeforeSeparator(before.isSelected());
        perspective.setAfterSeparator(after.isSelected());
        PerspectiveManagerImpl.getInstance().registerPerspective(cmbPosition.getSelectedIndex(), perspective);
        PerspectiveManagerImpl.getInstance().setSelected(perspective);
        ToolbarStyleSwitchUI.getInstance().loadQuickPerspectives();
        dispose();
    }

    private class SaveAs extends AbstractAction {

        private static final long serialVersionUID = 1l;
        Perspective mode;

        public SaveAs() {
            putValue(NAME, NbBundle.getMessage(SaveAsUI.class,"OK"));
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
