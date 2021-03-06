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

package org.netbeans.modules.tasklist.docscan;

import org.netbeans.modules.tasklist.client.SuggestionPriority;
import org.netbeans.modules.tasklist.core.PriorityListCellRenderer;
import org.netbeans.modules.tasklist.core.editors.PriorityTableCellRenderer;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.Mnemonics;
import org.openide.explorer.propertysheet.editors.EnhancedCustomPropertyEditor;
import org.openide.util.NbBundle;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Customizer panel for the set of tags scanned from source.
 * <p>
 * Please read comment at the beginning of initA11y before editing
 * this file using the form builder.
 * <p>
 *
 * @author  Tor Norbye
 */
public final class TaskTagsPanel extends javax.swing.JPanel
        implements EnhancedCustomPropertyEditor, ActionListener {

    private static final long serialVersionUID = 1;

    private DefaultTableModel model = null;

    /** Creates new form TaskTagsPanel */
    public TaskTagsPanel(TaskTags tags) {
        initComponents();
        initA11y();
        setPreferredSize(new Dimension(400, 200));
        this.tags = tags;

        TaskTag[] tagy = tags.getTags();
        model = new DefaultTableModel(new Object[0][0], new String[] {Util.getString("pat-col"), Util.getString("pri-col")}) {
            Class[] types = new Class [] {
                String.class, SuggestionPriority.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return columnIndex == 1;
            }
        };

        for (int i = 0; i < tagy.length; i++) {
            model.addRow(new Object[]{
                tagy[i].getToken(),
                tagy[i].getPriority()
            });
        }
        patternsTable.setModel(model);

        patternsTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                updateSensitivity();
            }
        });

        // FIXME #41228 i18n
        TableColumn sportColumn = patternsTable.getColumnModel().getColumn(1);
        JComboBox combo = new JComboBox();
        combo.addItem(SuggestionPriority.HIGH);
        combo.addItem(SuggestionPriority.MEDIUM_HIGH);
        combo.addItem(SuggestionPriority.MEDIUM);
        combo.addItem(SuggestionPriority.MEDIUM_LOW);
        combo.addItem(SuggestionPriority.LOW);
        combo.setRenderer(new PriorityListCellRenderer());
        sportColumn.setCellEditor(new DefaultCellEditor(combo));
        sportColumn.setCellRenderer(new PriorityTableCellRenderer());

        addButton.addActionListener(this);
        changeButton.addActionListener(this);
        deleteButton.addActionListener(this);


/*
        ListCellRenderer priorityRenderer = new PriorityListCellRenderer();
        ComboBoxModel prioritiesModel =
        new DefaultComboBoxModel(Task.getPriorityNames());

        prioCombo.setModel(prioritiesModel);
        prioCombo.setRenderer(priorityRenderer);

        tokenList.setCellRenderer(new TaskTagRenderer());
        TaskTag[] t = tags.getTags();
        model = new DefaultListModel();
        for (int i = 0; i < t.length; i++) {
            model.addElement(t[i]);
        }
        tokenList.setModel(model);


        tokenList.addListSelectionListener(this);
        tokenList.setSelectionInterval(0, 0);

        updateSensitivity();
        nameField.getDocument().addDocumentListener(this);
        prioCombo.addActionListener(this);
*/
    }

    private TaskTags tags = null;

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        tagLabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        patternsTable = new javax.swing.JTable();
        addButton = new javax.swing.JButton();
        changeButton = new javax.swing.JButton();
        deleteButton = new javax.swing.JButton();

        setLayout(new java.awt.GridBagLayout());

        setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(12, 12, 11, 11)));
        /*
        tagLabel.setText("Tag List:");
        */
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(tagLabel, gridBagConstraints);

        jScrollPane1.setViewportView(patternsTable);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridheight = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        add(jScrollPane1, gridBagConstraints);

        /*
        addButton.setText("Add");
        */
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
        add(addButton, gridBagConstraints);

        /*
        changeButton.setText("Change");
        */
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
        add(changeButton, gridBagConstraints);

        /*
        deleteButton.setText("Delete");
        */
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
        add(deleteButton, gridBagConstraints);

    }//GEN-END:initComponents

    /** Initialize accessibility settings on the panel */
    private void initA11y() {
        /*
          I couldn't figure out how to use Mnemonics.setLocalizedText
          to set labels and checkboxes with a mnemonic using the
          form builder, so the closest I got was to use "/*" and "* /
          as code pre-init/post-init blocks, such that I don't actually
          execute the bundle lookup code - and then call it explicitly
          below. (I wanted to keep the text on the components so that
          I can see them when visually editing the GUI.
        */

        Mnemonics.setLocalizedText(addButton, NbBundle.getMessage(
                TaskTagsPanel.class, "AddTag")); // NOI18N
        addButton.setToolTipText (NbBundle.getMessage (
            TaskTagsPanel.class, 
            "HINT_AddTag"
        ));
        Mnemonics.setLocalizedText(changeButton, NbBundle.getMessage(
                TaskTagsPanel.class, "ChangeTag")); // NOI18N
        changeButton.setToolTipText (NbBundle.getMessage (
            TaskTagsPanel.class, 
            "HINT_ChangeTag"
        ));
        Mnemonics.setLocalizedText(deleteButton, NbBundle.getMessage(
                TaskTagsPanel.class, "DeleteTag")); // NOI18N
        deleteButton.setToolTipText (NbBundle.getMessage (
            TaskTagsPanel.class, 
            "HINT_DeleteTag"
        ));
        Mnemonics.setLocalizedText(tagLabel, NbBundle.getMessage(
                TaskTagsPanel.class, "TagList")); // NOI18N
        tagLabel.setToolTipText (NbBundle.getMessage (
            TaskTagsPanel.class, 
            "HINT_TagList"
        ));
        tagLabel.setLabelFor(patternsTable);

        patternsTable.setToolTipText (NbBundle.getMessage (
            TaskTagsPanel.class, 
            "HINT_TagList"
        ));

        this.getAccessibleContext().setAccessibleDescription(
                NbBundle.getMessage(TaskTagsPanel.class, "ACSD_Tags")); // NOI18N
        patternsTable.getAccessibleContext().setAccessibleDescription(
                NbBundle.getMessage(TaskTagsPanel.class, "ACSD_List")); // NOI18N
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JButton changeButton;
    private javax.swing.JButton deleteButton;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable patternsTable;
    private javax.swing.JLabel tagLabel;
    // End of variables declaration//GEN-END:variables


    // When used as a property customizer
    public Object getPropertyValue() throws IllegalStateException {
        return getEditedTags();
    }

    private TaskTags getEditedTags() {
        TaskTag[] ts = new TaskTag[model.getRowCount()];
        for (int i = 0; i < model.getRowCount(); i++) {
            String token = (String) model.getValueAt(i, 0);
            SuggestionPriority prio =  (SuggestionPriority) model.getValueAt(i,1);
            TaskTag tag = new TaskTag(token, prio);
            ts[i] = tag;
        }
        tags = new TaskTags();
        tags.setTags(ts);
        return tags;
    }

    private void updateSensitivity() {
        int[] selected = patternsTable.getSelectedRows();
        int count = (selected != null) ? selected.length : 0;
        deleteButton.setEnabled(count == 1);
        changeButton.setEnabled (count == 1);
        addButton.setEnabled(true);
    }

    public void actionPerformed(ActionEvent actionEvent) {
        Object source = actionEvent.getSource();
        if (source == addButton) {
            NotifyDescriptor.InputLine d = new NotifyDescriptor.InputLine(Util.getString("pat-col"), Util.getString("new-pat"));
            DialogDisplayer.getDefault().notify(d);
            if (d.getValue() == NotifyDescriptor.OK_OPTION) {
                String text = d.getInputText();
                if (text.length() > 0) {
                    model.addRow(new Object[] {text, SuggestionPriority.MEDIUM});
                }
            }
        } else if (source == changeButton) {
            int row = patternsTable.getSelectedRow();
            if (row == -1) {
                if (patternsTable.getRowCount() > 0) {
                    patternsTable.getSelectionModel().setSelectionInterval(0,0);
                    row = patternsTable.getSelectedRow();
                } else {
                    updateSensitivity();
                    return;
                }
            }
            String pattern = (String) model.getValueAt(row, 0);
            NotifyDescriptor.InputLine d = new NotifyDescriptor.InputLine(Util.getString("pat-col"), Util.getString("edit-pat"));
            d.setInputText(pattern);
            DialogDisplayer.getDefault().notify(d);
            if (d.getValue() == NotifyDescriptor.OK_OPTION) {
                String text = d.getInputText();
                if (text.length() > 0) {
                    model.setValueAt(text, row, 0);
                }
            }
        } else if (source == deleteButton) {
            int row = patternsTable.getSelectedRow();
            if (row == -1) {
                updateSensitivity();
            } else {
                model.removeRow(row);
            }
        }
        updateSensitivity();
    }

}
