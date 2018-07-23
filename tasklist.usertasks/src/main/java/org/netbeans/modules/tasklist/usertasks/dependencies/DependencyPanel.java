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

package org.netbeans.modules.tasklist.usertasks.dependencies;

import java.awt.Image;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.ImageIcon;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;
import org.netbeans.modules.tasklist.usertasks.model.UserTask;
import org.openide.DialogDescriptor;

import org.openide.util.Utilities;
import org.netbeans.modules.tasklist.usertasks.model.Dependency;
import org.openide.util.NbBundle;

/**
 * Panel for editing a dependency
 */
public class DependencyPanel extends javax.swing.JPanel {
    /**
     * Tree cell renderer for user tasks
     */
    private static class UserTaskTreeCellRenderer extends DefaultTreeCellRenderer {
        private static final Image IMAGE =
            Utilities.loadImage("org/netbeans/modules/tasklist/core/task.gif"); // NOI18N
        private static final Image DONE =
            Utilities.loadImage("org/netbeans/modules/tasklist/core/doneItem.gif"); // NOI18N
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

                if (ut.isDone())
                    icon.setImage(DONE);
                else
                    icon.setImage(IMAGE);
            }
            
            return this;
        }        
    }
    
    private UserTask ut;
    private DialogDescriptor dd;
    private List curDeps;
    
    /**
     * Constructor
     *
     * @param ut user task that will have a new dependency on another task
     * @param curDeps <Dependency> current dependencies
     */
    public DependencyPanel(UserTask ut, List curDeps) {
        this.ut = ut;
        this.curDeps = curDeps;
        initComponents();
        jTree.setModel(new UserTaskTreeModel(ut.getList()));
        jTree.setCellRenderer(new UserTaskTreeCellRenderer());
        jTreeValueChanged(null);
    }
    
    /**
     * Sets a dialog descriptor
     *
     * @param dd a dialog descriptor
     */
    public void setDialogDescriptor(DialogDescriptor dd) {
        this.dd = dd;
        jTreeValueChanged(null);
    }
    
    /**
     * Selects another task in the tree.
     *
     * @param ut the task or null
     */
    public void setSelectedTask(UserTask ut) {
        if (ut != null)
            jTree.setSelectionPath(ut.getPathTo());
        else
            jTree.setSelectionPath(null);
    }

    /**
     * Returns selected task.
     *
     * @return selected task or null
     */
    public UserTask getSelectedTask() {
        TreePath tp = jTree.getSelectionPath();
        if (tp == null)
            return null;
        return (UserTask) tp.getLastPathComponent();
    }
    
    /**
     * Sets another dependency type
     *
     * @param type one of Dependency.BEGIN_BEGIN and Dependency.END_BEGIN
     */
    public void setDependencyType(int type) {
        if (type == Dependency.BEGIN_BEGIN)
            jRadioButtonBeginBegin.setSelected(true);
        else
            jRadioButtonEndBegin.setSelected(true);
    }

    /**
     * Returns selected dependency type
     *
     * @return one of Dependency.BEGIN_BEGIN and Dependency.END_BEGIN
     */
    public int getDependencyType() {
        if (jRadioButtonBeginBegin.isSelected())
            return Dependency.BEGIN_BEGIN;
        else
            return Dependency.END_BEGIN;
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        buttonGroup = new javax.swing.ButtonGroup();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTree = new javax.swing.JTree();
        jRadioButtonEndBegin = new javax.swing.JRadioButton();
        jRadioButtonBeginBegin = new javax.swing.JRadioButton();
        jLabelError = new javax.swing.JLabel();

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
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 11, 0);
        add(jScrollPane1, gridBagConstraints);

        buttonGroup.add(jRadioButtonEndBegin);
        jRadioButtonEndBegin.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(jRadioButtonEndBegin, org.openide.util.NbBundle.getBundle(DependencyPanel.class).getString("LBL_EndBegin"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 11, 12);
        add(jRadioButtonEndBegin, gridBagConstraints);

        buttonGroup.add(jRadioButtonBeginBegin);
        org.openide.awt.Mnemonics.setLocalizedText(jRadioButtonBeginBegin, org.openide.util.NbBundle.getBundle(DependencyPanel.class).getString("LBL_BeginBegin"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 11, 0);
        add(jRadioButtonBeginBegin, gridBagConstraints);

        jLabelError.setMinimumSize(new java.awt.Dimension(0, 15));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        add(jLabelError, gridBagConstraints);

    }//GEN-END:initComponents

    private void jTreeValueChanged(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_jTreeValueChanged
        String err = null;
        
        ResourceBundle rb = NbBundle.getBundle(DependencyPanel.class);
        
        TreePath tp = jTree.getSelectionPath();
        if (tp == null || !(tp.getLastPathComponent() instanceof UserTask)) {
            err =  org.openide.util.NbBundle.getBundle(DependencyPanel.class).
                    getString("NothingSelected"); // NOI18N
        } else {
            UserTask task = (UserTask) tp.getLastPathComponent();

            if (task == ut)
                err = rb.getString("TaskCannotDependOnItself."); // NOI18N
            else if (task == ut.getParent())
                err = rb.getString("TaskCannotDependOnParent"); // NOI18N
            else if (task.isAncestorOf(ut))
                err = rb.getString("TaskCannotDependOnAncestor"); // NOI18N
            else if (ut.isAncestorOf(task))
                err = rb.getString("TaskCannotDependOnChild"); // NOI18N
            
            for (int i = 0; i < curDeps.size(); i++) {
                Dependency d = (Dependency) curDeps.get(i);
                if (d.getDependsOn() == task)
                    err = rb.getString("DependencyAlreadyExists"); // NOI18N
                else if (task.isAncestorOf(d.getDependsOn()))
                    err = rb.getString("DependencyOnChildAlreadyExists"); // NOI18N
                else if (d.getDependsOn().isAncestorOf(task))
                    err = rb.getString("DependencyOnAncestorAlreadyExists"); // NOI18N
                
                if (err != null)
                    break;
            }
        }        
        
        jLabelError.setText(err == null ? "" : err); // NOI18N
        if (dd != null)
            dd.setValid(err == null);       
    }//GEN-LAST:event_jTreeValueChanged
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup;
    private javax.swing.JLabel jLabelError;
    private javax.swing.JRadioButton jRadioButtonBeginBegin;
    private javax.swing.JRadioButton jRadioButtonEndBegin;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTree jTree;
    // End of variables declaration//GEN-END:variables
    
}
