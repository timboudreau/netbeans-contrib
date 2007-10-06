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

package org.netbeans.modules.tasklist.timerwin;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;
import org.netbeans.modules.tasklist.usertasks.model.UserTask;
import org.netbeans.modules.tasklist.usertasks.model.UserTaskList;
import org.netbeans.modules.tasklist.usertasks.renderers.UserTaskIconProvider;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

/**
 * Chooser for a user task.
 *
 * @author tl
 */
public class UTChooserPanel extends javax.swing.JPanel {
    /**
     * Tree cell renderer for user tasks
     */
    private static class MyTreeCellRenderer extends DefaultTreeCellRenderer {
        private ImageIcon icon = new ImageIcon();
        
        public java.awt.Component getTreeCellRendererComponent(
            javax.swing.JTree tree, Object value, boolean sel, boolean expanded, 
            boolean leaf, int row, boolean hasFocus) {
            
            super.getTreeCellRendererComponent(
                tree, value, sel, expanded, leaf, row, hasFocus);
            if (value instanceof UserTask) {
                UserTask ut = (UserTask) value;
                this.setText(ut.getSummary());
                this.setIcon(icon);

                icon.setImage(UserTaskIconProvider.getUserTaskImage(ut, false));
            } else if (value instanceof UserTaskList) {
                UserTaskList utl = (UserTaskList) value;
                this.setText(FileUtil.getFileDisplayName(utl.getFile()));
                this.setIcon(icon);
                icon.setImage(UserTaskIconProvider.getUserTaskListImage());
            }
            
            return this;
        }        
    }
    
    /**
     * Shows a dialog for choosing a task.
     *
     * @return choosed task or null if cancelled
     */
    public static UserTask choose() {
        UTChooserPanel dp = new UTChooserPanel();
        dp.setBorder(new EmptyBorder(11, 11, 12, 12));
        DialogDescriptor dd = new DialogDescriptor(dp, 
            NbBundle.getMessage(
                UTChooserPanel.class, "ChooseTask")); // NOI18N
        dp.dd = dd;
        dd.setValid(false);
        Dialog d = DialogDisplayer.getDefault().createDialog(dd);
        d.setBounds(Utilities.findCenterBounds(new Dimension(400, 400)));
        d.show();
        if (dd.getValue() == DialogDescriptor.OK_OPTION) {
            return dp.getSelectedUserTask();
        } else {
            return null;
        }
    }
    
    private DialogDescriptor dd;
    
    /** 
     * Creates new form UTChooserPanel 
     */
    public UTChooserPanel() {
        initComponents();
        jTree.setCellRenderer(new MyTreeCellRenderer());
        jTree.setModel(new AllUserTasksTreeModel());
    }
    
    /**
     * Returns selected user task or null.
     *
     * @return selected user task or null
     */
    public UserTask getSelectedUserTask() {
        TreePath tp = jTree.getSelectionPath();
        if (tp == null)
            return null;
        Object obj = tp.getLastPathComponent();
        if (obj instanceof UserTask)
            return (UserTask) obj;
        else
            return null;
    }
    
    /** 
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jScrollPane1 = new javax.swing.JScrollPane();
        jTree = new javax.swing.JTree();
        jLabel1 = new javax.swing.JLabel();

        setLayout(new java.awt.GridBagLayout());

        jTree.setRootVisible(false);
        jTree.setShowsRootHandles(true);
        jTree.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                jTreeValueChanged(evt);
            }
        });

        jScrollPane1.setViewportView(jTree);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 11, 0);
        add(jScrollPane1, gridBagConstraints);

        jLabel1.setText(" ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(jLabel1, gridBagConstraints);

    }// </editor-fold>//GEN-END:initComponents

    private void jTreeValueChanged(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_jTreeValueChanged
        UserTask ut = getSelectedUserTask();
        dd.setValid(ut != null && ut.isStartable() && !ut.isStarted());
    }//GEN-LAST:event_jTreeValueChanged
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTree jTree;
    // End of variables declaration//GEN-END:variables
    
}
