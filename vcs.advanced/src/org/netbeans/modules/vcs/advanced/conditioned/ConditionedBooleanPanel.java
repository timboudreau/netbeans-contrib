/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcs.advanced.conditioned;

import java.beans.PropertyDescriptor;
import java.util.HashMap;
import java.util.Map;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import org.openide.explorer.propertysheet.DefaultPropertyModel;
import org.openide.explorer.propertysheet.PropertyPanel;
import org.openide.explorer.propertysheet.editors.EnhancedCustomPropertyEditor;

/**
 *
 * @author  Martin Entlicher
 */
public class ConditionedBooleanPanel extends javax.swing.JPanel implements EnhancedCustomPropertyEditor {
    
    private ConditionedBoolean cb;
    
    /** Creates new form ConditionObjectPanel */
    public ConditionedBooleanPanel(ConditionedBoolean cb) {
        this.cb = cb;
        initComponents();
        editButton.setVisible(false);
        jTable1.getSelectionModel().addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent lsev) {
                removeButton.setEnabled(jTable1.getSelectedRows().length > 0);
            }
        });
        fillConditions();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        infoLabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        addButton = new javax.swing.JButton();
        editButton = new javax.swing.JButton();
        removeButton = new javax.swing.JButton();

        FormListener formListener = new FormListener();

        setLayout(new java.awt.GridBagLayout());

        infoLabel.setText("Value");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 6, 11);
        add(infoLabel, gridBagConstraints);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "Title 1", "Title 2"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Boolean.class
            };
            boolean[] canEdit = new boolean [] {
                false, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTable1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 6, 11);
        add(jScrollPane1, gridBagConstraints);

        jPanel1.setLayout(new java.awt.GridBagLayout());

        addButton.setText(org.openide.util.NbBundle.getMessage(ConditionedBooleanPanel.class, "ConditionedStringPanel.addButton"));
        addButton.addActionListener(formListener);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 6, 0);
        jPanel1.add(addButton, gridBagConstraints);

        editButton.setText(org.openide.util.NbBundle.getMessage(ConditionedBooleanPanel.class, "ConditionedStringPanel.editButton"));
        editButton.addActionListener(formListener);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 6, 0);
        jPanel1.add(editButton, gridBagConstraints);

        removeButton.setText(org.openide.util.NbBundle.getMessage(ConditionedBooleanPanel.class, "ConditionedStringPanel.removeButton"));
        removeButton.addActionListener(formListener);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel1.add(removeButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 11, 11);
        add(jPanel1, gridBagConstraints);

    }

    // Code for dispatching events from components to event handlers.

    private class FormListener implements java.awt.event.ActionListener {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            if (evt.getSource() == addButton) {
                ConditionedBooleanPanel.this.addButtonActionPerformed(evt);
            }
            else if (evt.getSource() == editButton) {
                ConditionedBooleanPanel.this.editButtonActionPerformed(evt);
            }
            else if (evt.getSource() == removeButton) {
                ConditionedBooleanPanel.this.removeButtonActionPerformed(evt);
            }
        }
    }//GEN-END:initComponents

    private void removeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeButtonActionPerformed
        // Add your handling code here:
        int[] rows = jTable1.getSelectedRows();
        java.util.Arrays.sort(rows);
        for (int i = rows.length - 1; i >= 0; i--) {
            ((DefaultTableModel) jTable1.getModel()).removeRow(rows[i]);
        }
    }//GEN-LAST:event_removeButtonActionPerformed

    private void editButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editButtonActionPerformed
        // Add your handling code here:
    }//GEN-LAST:event_editButtonActionPerformed

    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
        // Add your handling code here:
        ((DefaultTableModel) jTable1.getModel()).addRow(new Object[] { new IfUnlessCondition(null), Boolean.FALSE });
    }//GEN-LAST:event_addButtonActionPerformed
    
    private void fillConditions() {
        infoLabel.setText(org.openide.util.NbBundle.getMessage(ConditionedIntegerPanel.class, "ConditionedStringPanel.title", cb.getName()));
        IfUnlessCondition[] iucs = cb.getIfUnlessConditions();
        jTable1.removeAll();
        Object[][] data = new Object[iucs.length][2];
        for (int i = 0; i < iucs.length; i++) {
            data[i][0] = iucs[i];
            data[i][1] = cb.getValue(iucs[i]);
        }
        DefaultTableModel model = new DefaultTableModel(data, new Object[] {
            org.openide.util.NbBundle.getMessage(ConditionedIntegerPanel.class, "ConditionedIntegerPanel.ConditionTitle"),
            org.openide.util.NbBundle.getMessage(ConditionedIntegerPanel.class, "ConditionedIntegerPanel.ValueTitle")
            }) {
                
            Class[] types = new Class [] {
                IfUnlessCondition.class, java.lang.Boolean.class
            };
            boolean[] canEdit = new boolean [] {
                true, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        };
        jTable1.setModel(model);
        javax.swing.table.TableCellEditor cellEditor = new IfUnlessPropertyEditor.IUTableCellEditor();
        javax.swing.table.TableCellRenderer cellRenderer = new IfUnlessPropertyEditor.IUTableCellRenderer();
        jTable1.getColumnModel().getColumn(0).setCellEditor(cellEditor);
        jTable1.getColumnModel().getColumn(0).setCellRenderer(cellRenderer);
        //removeButton.setEnabled(jTable1.getRowCount() > 1);
    }
    
    private ConditionedBoolean createConditionedBoolean() {
        Map valuesByConditions = new HashMap();
        TableModel model = jTable1.getModel();
        int nr = model.getRowCount();
        for (int i = 0; i < nr; i++) {
            IfUnlessCondition iuc = (IfUnlessCondition) model.getValueAt(i, 0);
            Object value = model.getValueAt(i, 1);
            valuesByConditions.put(iuc.getCondition(), value);
        }
        cb = new ConditionedBoolean(cb.getName(), valuesByConditions);
        return cb;
    }
    
    public Object getPropertyValue() throws IllegalStateException {
        return createConditionedBoolean();
    }    
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JButton editButton;
    private javax.swing.JLabel infoLabel;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JButton removeButton;
    // End of variables declaration//GEN-END:variables
    
}
