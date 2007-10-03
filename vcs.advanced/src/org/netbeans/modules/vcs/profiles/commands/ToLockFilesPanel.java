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


package org.netbeans.modules.vcs.profiles.commands;

/**
 *
 * @author  Milos Kleint
 */
import org.netbeans.modules.vcscore.ui.MyTableObject;
import org.netbeans.modules.vcscore.util.table.*;
import org.netbeans.modules.vcscore.annotation.AnnotationProvider;
import org.netbeans.modules.vcscore.annotation.AnnotationSupport;

import org.openide.filesystems.*;
import java.util.*;
import java.io.File;
import javax.swing.table.*;
import javax.swing.*;
import java.lang.reflect.*;
import org.openide.util.*;


public class ToLockFilesPanel extends javax.swing.JPanel {

    private TableInfoModel model;
    /** Creates new form ToAddFilesPanel */
    public ToLockFilesPanel(List fileObjectList) {
        initComponents();

        cbPerform.setMnemonic (org.openide.util.NbBundle.getBundle (ToLockFilesPanel.class).getString ("ToLockFilesPanel.cbPerform.mnemonic").charAt (0));
        rbAll.setMnemonic (org.openide.util.NbBundle.getBundle (ToLockFilesPanel.class).getString ("ToLockFilesPanel.rbAll.mnemonic").charAt (0));
        rbSelectedOnly.setMnemonic (org.openide.util.NbBundle.getBundle (ToLockFilesPanel.class).getString ("ToLockFilesPanel.rbSelectedOnly.mnemonic").charAt (0));
        jTextArea1.setFont (javax.swing.UIManager.getFont ("Label.font"));
        jTextArea1.setForeground (javax.swing.UIManager.getColor ("Label.foreground"));
        jTextArea1.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getBundle (ToLockFilesPanel.class).getString ("ACS_ToLockFilesPanel.textArea"));// NOI18N
        jTextArea1.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getBundle (ToLockFilesPanel.class).getString ("ACSD_ToLockFilesPanel.textArea"));// NOI18N

        getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getBundle (ToLockFilesPanel.class).getString ("ACS_ToLockFilesPanel"));// NOI18N
        getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getBundle (ToLockFilesPanel.class).getString ("ACSD_ToLockFilesPanel"));// NOI18N
        rbAll.setSelected(true);
        rbSelectedOnly.setSelected(false);
        rbAll.setEnabled(false);
        rbSelectedOnly.setEnabled(false);
        buttonGroup1.add(rbAll);
        buttonGroup1.add(rbSelectedOnly);        
        // setting the model....
        model = new TableInfoModel();
        Class classa = MyTableObject.class;
        FoStatusComparator comp = new FoStatusComparator();
        String  column1 = NbBundle.getBundle(ToLockFilesPanel.class).getString("ToLockFilesPanel.fileColumn"); // NOI18N
        String  column2 = NbBundle.getBundle(ToLockFilesPanel.class).getString("ToLockFilesPanel.pathColumn"); // NOI18N
        String  column3 = NbBundle.getBundle(ToLockFilesPanel.class).getString("ToLockFilesPanel.statusColumn"); // NOI18N
        try {
            Method method1 = classa.getMethod("getName", null);     //NOI18N
            Method method2 = classa.getMethod("getPackg", null);     //NOI18N
            Method method3 = classa.getMethod("getFilesystem", null); //NOI18N - can be whatever here.. will decide on the rowobject..
            model.setColumnDefinition(0, column1, method1, true, null);
            model.setColumnDefinition(1, column3, method3, true, comp);
            model.setColumnDefinition(2, column2, method2, true, null);
            
        } catch (NoSuchMethodException exc) {
            Thread.dumpStack();
        } catch (SecurityException exc2) {
            Thread.dumpStack();
        }
        Iterator it = fileObjectList.iterator();
        while (it.hasNext()) {
            FileObject fo = (FileObject)it.next();
            model.addElement(new MyTableObject(fo));
        }
        tblToBeAddedFiles.setModel(model);
        JTableHeader head = tblToBeAddedFiles.getTableHeader();
        head.setUpdateTableInRealTime(true);
        ColumnSortListener listen = new ColumnSortListener(tblToBeAddedFiles);
        head.addMouseListener(listen);        
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        buttonGroup1 = new javax.swing.ButtonGroup();
        cbPerform = new javax.swing.JCheckBox();
        rbAll = new javax.swing.JRadioButton();
        rbSelectedOnly = new javax.swing.JRadioButton();
        spToBeAddedFiles = new javax.swing.JScrollPane();
        tblToBeAddedFiles = new javax.swing.JTable();
        jTextArea1 = new javax.swing.JTextArea();

        FormListener formListener = new FormListener();

        setLayout(new java.awt.GridBagLayout());

        setPreferredSize(new java.awt.Dimension(250, 250));
        cbPerform.setText(org.openide.util.NbBundle.getBundle(ToLockFilesPanel.class).getString("ToLockFilesPanel.cbPerform.text"));
        cbPerform.addActionListener(formListener);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 12, 11);
        add(cbPerform, gridBagConstraints);

        rbAll.setText(org.openide.util.NbBundle.getBundle(ToLockFilesPanel.class).getString("ToLockFilesPanel.rbAll.text"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 0, 11);
        add(rbAll, gridBagConstraints);

        rbSelectedOnly.setText(org.openide.util.NbBundle.getBundle(ToLockFilesPanel.class).getString("ToLockFilesPanel.rbSelectedOnly.text"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 0, 11);
        add(rbSelectedOnly, gridBagConstraints);

        spToBeAddedFiles.setMinimumSize(new java.awt.Dimension(150, 120));
        spToBeAddedFiles.setViewportView(tblToBeAddedFiles);
        tblToBeAddedFiles.getAccessibleContext().setAccessibleName(NbBundle.getBundle("org/netbeans/modules/vcs/profiles/commands/Bundle").getString("ACS_ToLockFilesPanel.tbl"));
        tblToBeAddedFiles.getAccessibleContext().setAccessibleDescription(NbBundle.getBundle("org/netbeans/modules/vcs/profiles/commands/Bundle").getString("ACSD_ToLockFilesPanel.tbl"));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 30, 11, 11);
        add(spToBeAddedFiles, gridBagConstraints);

        jTextArea1.setEditable(false);
        jTextArea1.setLineWrap(true);
        jTextArea1.setText(org.openide.util.NbBundle.getBundle(ToLockFilesPanel.class).getString("ToLockFilesPanel.lblDescription.text"));
        jTextArea1.setWrapStyleWord(true);
        jTextArea1.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 0, 11);
        add(jTextArea1, gridBagConstraints);
        jTextArea1.getAccessibleContext().setAccessibleName(NbBundle.getBundle("org/netbeans/modules/vcs/profiles/commands/Bundle").getString("ACS_ToLockFilesPanel.textArea"));
        jTextArea1.getAccessibleContext().setAccessibleDescription(NbBundle.getBundle("org/netbeans/modules/vcs/profiles/commands/Bundle").getString("ACSD_ToLockFilesPanel.textArea"));

    }

    // Code for dispatching events from components to event handlers.

    private class FormListener implements java.awt.event.ActionListener {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            if (evt.getSource() == cbPerform) {
                ToLockFilesPanel.this.cbPerformActionPerformed(evt);
            }
        }
    }//GEN-END:initComponents

    private void cbPerformActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbPerformActionPerformed
        // Add your handling code here:
        rbAll.setEnabled(cbPerform.isSelected());
        rbSelectedOnly.setEnabled(cbPerform.isSelected());
    }//GEN-LAST:event_cbPerformActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JCheckBox cbPerform;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JRadioButton rbAll;
    private javax.swing.JRadioButton rbSelectedOnly;
    private javax.swing.JScrollPane spToBeAddedFiles;
    private javax.swing.JTable tblToBeAddedFiles;
    // End of variables declaration//GEN-END:variables

    private static final long serialVersionUID = -2911342615927356990L;    

    /** 
     * returns the selected fileobjects. if the action should not be performed, returns null
     * if the rbFsRoos is selected by the user, then it returns filesystems root fileobjects..
     */
    
    public java.util.List getFileObjects() {
        if (cbPerform.isSelected()) {
            java.util.List list = new LinkedList();
            if (rbAll.isSelected()) {
                Iterator it = model.getList().iterator();
                while (it.hasNext()) {
                    MyTableObject tb = (MyTableObject)it.next();
                    list.add(tb.getFileObject());
                }
            } 
            else  if (rbSelectedOnly.isSelected()) {
                int[] rows = tblToBeAddedFiles.getSelectedRows();
                for (int i = 0; i< rows.length; i++) {
                    MyTableObject tb = (MyTableObject)model.getElementAt(rows[i]);
                    list.add(tb.getFileObject());
                }
            }
            else {
                java.util.Set set = new HashSet();
                Iterator it = model.getList().iterator();
                while (it.hasNext()) {
                    MyTableObject tb = (MyTableObject)it.next();
                    FileObject fo = tb.getFileObject();
                    while (fo.getParent() != null) {
                        fo = fo.getParent();
                    }
                    set.add(fo);
                }
                list = new LinkedList(set);
            }
            return list;
        }
        return null;
    }
    

    
    private static class FoStatusComparator implements TableInfoComparator {

        public FoStatusComparator() {
        }
        
        
        public int compare(java.lang.Object obj, java.lang.Object obj1) {
            return 0;
        }
        
        public String getDisplayValue(Object obj, Object rowObject) {
            MyTableObject tb = (MyTableObject)rowObject;
            FileObject fo = tb.getFileObject();
            AnnotationProvider provider = (AnnotationProvider)fo.getAttribute(AnnotationProvider.ANN_PROVIDER_FO_ATTRIBUTE);
            if (provider != null) {
                return provider.getAttributeValue(fo.getPath(), AnnotationSupport.ANNOTATION_PATTERN_STATUS);
            }
                
            return "";
        }
        
    }
}
