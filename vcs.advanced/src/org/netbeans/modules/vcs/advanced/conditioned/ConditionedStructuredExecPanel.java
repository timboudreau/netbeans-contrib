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

import java.util.HashMap;
import java.util.Map;
import javax.swing.table.DefaultTableModel;

import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;

import org.netbeans.modules.vcs.advanced.commands.StructuredExecPanel;
import org.netbeans.modules.vcs.advanced.commands.ConditionedCommandsBuilder.ConditionedProperty;
import org.netbeans.modules.vcs.advanced.variables.Condition;

import org.netbeans.modules.vcscore.cmdline.exec.StructuredExec;
import org.netbeans.modules.vcscore.commands.VcsCommand;

/**
 *
 * @author  Martin Entlicher
 */
public class ConditionedStructuredExecPanel extends StructuredExecPanel {
    
    private ConditionedString cexecString;
    private ConditionedObject cexecStructured;
    private VcsCommand cmd;
    private Map cproperties;
    
    private javax.swing.JPanel jPanel1;
    private javax.swing.JButton addButton;
    private javax.swing.JComboBox conditionComboBox;
    private javax.swing.JLabel conditionLabel;
    private javax.swing.JButton editButton;
    private javax.swing.JButton removeButton;
    
    /** Creates a new instance of ConditionedStructuredExecPanel */
    public ConditionedStructuredExecPanel(VcsCommand cmd, Map cproperties) {
        super(cmd);
        this.cmd = cmd;
        this.cproperties = cproperties;
    }
    
    protected void postInitComponents() {
        argTableModel = new DefaultTableModel(new Object[0][0], new Object[] {
            org.openide.util.NbBundle.getMessage(StructuredExecPanel.class, "StructuredExecPanel.Arguments"),
            org.openide.util.NbBundle.getMessage(StructuredExecPanel.class, "StructuredExecPanel.ArgLine"),
            org.openide.util.NbBundle.getMessage(ConditionedStructuredExecPanel.class, "ConditionedStructuredExecPanel.Conditions")
        }) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Boolean.class, IfUnlessCondition.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        };
        final javax.swing.JTable argTable = getArgTable();
        argTable.setModel(argTableModel);
        addConditionPanel();
        Object lineHeaderValue = argTable.getColumnModel().getColumn(1).getHeaderValue();
        javax.swing.table.TableCellRenderer tcr = argTable.getColumnModel().getColumn(1).getHeaderRenderer();
        if (tcr == null) {
            tcr = argTable.getTableHeader().getDefaultRenderer();
        }
        java.awt.Component lineHeaderComponent = tcr.getTableCellRendererComponent(argTable, lineHeaderValue, false, true, 0, 1);
        int width = lineHeaderComponent.getPreferredSize().width;
        argTable.getColumnModel().getColumn(1).setPreferredWidth(width + 24);
        argTable.getColumnModel().getColumn(1).setMaxWidth(width + 24);
        editButton.setVisible(false);
    }
    
    private void addConditionPanel() {
        java.awt.GridBagConstraints gridBagConstraints;
        
        jPanel1 = new javax.swing.JPanel();
        conditionLabel = new javax.swing.JLabel();
        conditionComboBox = new javax.swing.JComboBox();
        addButton = new javax.swing.JButton();
        editButton = new javax.swing.JButton();
        removeButton = new javax.swing.JButton();

        FormListener formListener = new FormListener();
        
        jPanel1.setLayout(new java.awt.GridBagLayout());

        conditionLabel.setText(org.openide.util.NbBundle.getMessage(ConditionedStringPanel.class, "ConditionedStringPanel.Condition"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 6);
        jPanel1.add(conditionLabel, gridBagConstraints);

        conditionComboBox.addActionListener(formListener);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 6);
        jPanel1.add(conditionComboBox, gridBagConstraints);

        addButton.setText(org.openide.util.NbBundle.getMessage(ConditionedStringPanel.class, "ConditionedStringPanel.addButton"));
        addButton.addActionListener(formListener);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 6);
        jPanel1.add(addButton, gridBagConstraints);

        editButton.setText(org.openide.util.NbBundle.getMessage(ConditionedStringPanel.class, "ConditionedStringPanel.editButton"));
        editButton.addActionListener(formListener);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 6);
        jPanel1.add(editButton, gridBagConstraints);

        removeButton.setText(org.openide.util.NbBundle.getMessage(ConditionedStringPanel.class, "ConditionedStringPanel.removeButton"));
        removeButton.addActionListener(formListener);

        jPanel1.add(removeButton, new java.awt.GridBagConstraints());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 10;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 11, 11);
        add(jPanel1, gridBagConstraints);
    }
    
    private class FormListener implements java.awt.event.ActionListener {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            if (evt.getSource() == conditionComboBox) {
                ConditionedStructuredExecPanel.this.conditionComboBoxActionPerformed(evt);
            }
            else if (evt.getSource() == addButton) {
                ConditionedStructuredExecPanel.this.addButtonActionPerformed(evt);
            }
            else if (evt.getSource() == editButton) {
                ConditionedStructuredExecPanel.this.editButtonActionPerformed(evt);
            }
            else if (evt.getSource() == removeButton) {
                ConditionedStructuredExecPanel.this.removeButtonActionPerformed(evt);
            }
        }
    }

    private void conditionComboBoxActionPerformed(java.awt.event.ActionEvent evt) {
        // Add your handling code here:
        setFieldsForCurrentCondition();
    }
    
    private void setFieldsForCurrentCondition() {
        IfUnlessCondition iuc = (IfUnlessCondition) conditionComboBox.getSelectedItem();
        if (cexecString != null) {
            String exec = cexecString.getValue(iuc);
            if (exec != null) {
                setExecString(exec);
            }
        }
        if (cexecStructured != null) {
            StructuredExec sexec = (StructuredExec) cexecStructured.getObjectValue(iuc);
            if (sexec != null) {
                setExecStructured(sexec);
            }
        }
    }

    private void removeButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // Add your handling code here:
        IfUnlessCondition iuc = (IfUnlessCondition) conditionComboBox.getSelectedItem();
        if (cexecString != null) {
            cexecString.removeValue(iuc);
        }
        if (cexecStructured != null) {
            cexecStructured.removeValue(iuc);
        }
        conditionComboBox.removeItem(iuc);
        removeButton.setEnabled(conditionComboBox.getItemCount() > 1);
    }

    private void editButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // Add your handling code here:
        IfUnlessCondition iuc = (IfUnlessCondition) conditionComboBox.getSelectedItem();
        IfUnlessConditionPanel panel = new IfUnlessConditionPanel(iuc, new String[0]);
        DialogDescriptor dd = new DialogDescriptor(panel, org.openide.util.NbBundle.getMessage(IfUnlessConditionPanel.class, "IfUnlessConditionPanel.title"));
        if (NotifyDescriptor.OK_OPTION.equals(DialogDisplayer.getDefault().notify(dd))) {
            iuc = panel.getCondition();
            //cs.setValue(iuc, cs.getValue((IfUnlessCondition) conditionComboBox.getSelectedItem()));  // Rather leave the last text
        }
    }

    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // Add your handling code here:
        IfUnlessCondition iuc = new IfUnlessCondition(null);
        IfUnlessConditionPanel panel = new IfUnlessConditionPanel(iuc, new String[0]);
        DialogDescriptor dd = new DialogDescriptor(panel, org.openide.util.NbBundle.getMessage(IfUnlessConditionPanel.class, "IfUnlessConditionPanel.title"));
        if (NotifyDescriptor.OK_OPTION.equals(DialogDisplayer.getDefault().notify(dd))) {
            iuc = panel.getCondition();
            conditionComboBox.addItem(iuc);
            //cs.setValue(iuc, cs.getValue((IfUnlessCondition) conditionComboBox.getSelectedItem()));  // Rather leave the last text
            conditionComboBox.setSelectedItem(iuc);
        }
        removeButton.setEnabled(conditionComboBox.getItemCount() > 1);
    }

    private void fillConditions() {
        conditionComboBox.removeAllItems();
        IfUnlessCondition[] iucs1 = null;
        if (cexecString != null) {
            iucs1 = cexecString.getIfUnlessConditions();
            for (int i = 0; i < iucs1.length; i++) {
                conditionComboBox.addItem(iucs1[i]);
            }
        }
        if (cexecStructured != null) {
            IfUnlessCondition[] iucs2 = cexecStructured.getIfUnlessConditions();
fori:       for (int i = 0; i < iucs2.length; i++) {
                if (iucs1 != null) {
                    //Condition c2 = iucs2[i].getCondition();
                    for (int j = 0; j < iucs1.length; j++) {
                        //Condition c1 = iucs1[j].getCondition();
                        //if (c1 == null && c2 == null || c1 != null && c1.equals(c2)) {
                        if (iucs2[i].equals(iucs1[j])) {
                            break fori;
                        }
                    }
                }
                conditionComboBox.addItem(iucs2[i]);
            }
        }
        removeButton.setEnabled(conditionComboBox.getItemCount() > 1);
    }
    
    protected void fieldsFocusLost() {
        IfUnlessCondition iuc = (IfUnlessCondition) conditionComboBox.getSelectedItem();
        if (isStringExecSelected()) {
            String exec = getExecString();
            if (cexecString == null) {
                cexecString = new ConditionedString(VcsCommand.PROPERTY_EXEC, new HashMap());
            }
            cexecString.setValue(iuc, exec);
        } else {
            StructuredExec sexec = getExecStructured();
            if (cexecStructured == null) {
                cexecStructured = new ConditionedObject(VcsCommand.PROPERTY_EXEC_STRUCTURED, new HashMap());
            }
            cexecStructured.setObjectValue(iuc, sexec);
        }
    }
    
    public ConditionedString getExecStringConditioned() {
        return cexecString;
    }
    
    public void setExecStringConditioned(ConditionedString cexecString) {
        this.cexecString = cexecString;
        fillConditions();
        setFieldsForCurrentCondition();
    }
    
    public void setExecStructuredConditioned(ConditionedObject cexecStructured) {
        this.cexecStructured = cexecStructured;
        fillConditions();
        setFieldsForCurrentCondition();
    }
    
    public ConditionedObject getExecStructuredConditioned() {
        return cexecStructured;
    }
    
    public Object getPropertyValue() throws IllegalStateException {
        ConditionedProperty cproperty = (ConditionedProperty) cproperties.get(VcsCommand.PROPERTY_EXEC);
        Map valuesByConditions = cexecString.getValuesByConditions();
        ConditionedProperty newCProperty;
        Object varValue = null;
        if (valuesByConditions.size() == 1 && valuesByConditions.keySet().iterator().next() == null) {
            newCProperty = null;
            varValue = valuesByConditions.get(null);
        } else {
            if (cproperty != null) {
                newCProperty = new ConditionedProperty(VcsCommand.PROPERTY_EXEC, cproperty.getCondition(), valuesByConditions);
            } else {
                newCProperty = new ConditionedProperty(VcsCommand.PROPERTY_EXEC, null, valuesByConditions);
            }
        }
        if (newCProperty != null) {
            cproperties.put(VcsCommand.PROPERTY_EXEC, newCProperty);
        } else {
            cproperties.remove(VcsCommand.PROPERTY_EXEC);
            cmd.setProperty(VcsCommand.PROPERTY_EXEC, varValue);
        }
        return cexecStructured;
    }
}
