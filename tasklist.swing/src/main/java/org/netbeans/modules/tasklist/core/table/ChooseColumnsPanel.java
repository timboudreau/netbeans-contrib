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

package org.netbeans.modules.tasklist.core.table;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import org.netbeans.modules.tasklist.swing.checklist.CheckListModel;
import org.netbeans.modules.tasklist.swing.checklist.DefaultCheckListModel;

import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.NbBundle;

/**
 * Panel for choosing visible columns in a table.
 *
 * @author tl
 */
public class ChooseColumnsPanel extends javax.swing.JPanel {

    private static final long serialVersionUID = 1;

    /**
     * ActionListener for the "choose visible columns" dialog
     */
    private static class ChooseColumnsActionListener implements ActionListener {
        private JTable table;
        
        /**
         * Constructor
         *
         * @param t columns in this table will be configured
         */
        public ChooseColumnsActionListener(JTable t) {
            this.table = t;
        }
        
        public void actionPerformed(ActionEvent e) {
            TableModel tm = table.getModel();
            int n = tm.getColumnCount();
            String[] names = new String[n];
            for (int i = 0; i < n; i++) {
                names[i] = tm.getColumnName(i);
            }
            
            boolean[] checked = new boolean[n];
            TableColumnModel tcm = table.getColumnModel();
            for (int i = 0; i < tcm.getColumnCount(); i++) {
                checked[tcm.getColumn(i).getModelIndex()] = true;
            }
            
            // Show the dialog
            ChooseColumnsPanel ccp = new ChooseColumnsPanel(checked, names);
            DialogDescriptor dd = new DialogDescriptor(
                ccp, NbBundle.getMessage(ChooseColumnsPanel.class, 
                "ChangeVisibleColumns")); // NOI18N
            Object res = DialogDisplayer.getDefault().notify(dd);
            
            if (res == NotifyDescriptor.OK_OPTION) {
                ArrayList<TableColumn> old = new ArrayList<TableColumn>();
                for (int i = 0; i < tcm.getColumnCount(); i++) {
                    old.add(tcm.getColumn(i));
                }
                
                table.createDefaultColumnsFromModel();
                for (int i = 0; i < old.size(); i++) {
                    tcm.addColumn((TableColumn) old.get(i));
                    tcm.moveColumn(tcm.getColumnCount() - 1, i);
                }
                
                for (int i = 0; i < tcm.getColumnCount(); ) {
                    TableColumn tc = tcm.getColumn(i);
                    int index = tc.getModelIndex();
                    if (!checked[index]) {
                        tcm.removeColumn(tcm.getColumn(i));
                    } else {
                        checked[index] = false;
                        i++;
                    }
                }
            }
        }
    }
    
    /**
     * Finds a column 
     *
     * @param tcm a table columns model
     * @param index model's column index or -1
     */
    private static int findColumn(TableColumnModel tcm, int modelIndex) {
        for (int i = 0; i < tcm.getColumnCount(); i++) {
            if (tcm.getColumn(i).getModelIndex() == modelIndex)
                return i;
        }
        return -1;
    }
    
    /**
     * Installs a special button in the right upper corner of a scroll pane
     * that can be used to change visible columns in a table
     *
     * @param sp a scroll pane with a table
     */
    public static void installChooseColumnsButton(JScrollPane sp) {
        JTable table = (JTable) sp.getViewport().getView();
        JButton b = new JButton(
            new ImageIcon(ChooseColumnsPanel.class.getResource("columns.gif"))); // NOI18N
        b.addActionListener(new ChooseColumnsActionListener(table));
        sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        sp.setCorner(JScrollPane.UPPER_RIGHT_CORNER, b);
    }
    
    /** 
     * Creates new form ChooseColumnsPanel 
     *
     * @param visibility true = column visible
     * @param name column names
     */
    public ChooseColumnsPanel(boolean[] visibility, String[] names) {
        initComponents();
        columnsCheckList.setModel(new DefaultCheckListModel(visibility, names));
    }

    /**
     * Returns an array with visibility attributes
     *
     * @return true = visible
     */
    public boolean[] getChecked() {
        CheckListModel m = (CheckListModel) columnsCheckList.getModel();
        boolean[] b = new boolean[m.getSize()];
        for (int i = 0; i < m.getSize(); i++) {
            b[i] = m.isChecked(i);
        }
        return b;
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        columnsCheckList = new org.netbeans.modules.tasklist.swing.checklist.CheckList();

        setBorder(javax.swing.BorderFactory.createEmptyBorder(12, 12, 11, 11));
        setPreferredSize(new java.awt.Dimension(400, 300));
        setLayout(new java.awt.GridBagLayout());

        jLabel1.setLabelFor(columnsCheckList);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 11, 0);
        add(jLabel1, gridBagConstraints);

        jScrollPane1.setViewportView(columnsCheckList);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(jScrollPane1, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.netbeans.modules.tasklist.swing.checklist.CheckList columnsCheckList;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
    
}
