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

package org.netbeans.modules.jackpot.ui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.text.MessageFormat;
import java.util.*;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import org.netbeans.modules.jackpot.*;
import org.openide.*;
import org.openide.filesystems.*;
import org.openide.loaders.*;
import org.openide.util.*;

/**
 * Inspection Manager dialog panel.
 */
public class RefactoringManagerPanel extends JPanel {
    InspectionsList inspections;
    
    private static final int DESCRIPTION_TAB_INDEX = 0;
    private static final int OPTIONS_TAB_INDEX = 1;
   
    /**
     * Creates new form RefactoringManagerPanel
     */
    public RefactoringManagerPanel() {
        inspections = InspectionsList.instance();
        initComponents();
        addInspectionListSelectionListener(new InspectionSelectionListener());
        enableQuerySetDelete();
        tabbedPane.setEnabledAt(OPTIONS_TAB_INDEX, false);
        inspections.addListDataListener((InspectionsModel)inspectionsTable.getModel());
    }
    
    QuerySet getSelectedQuerySet() {
        QuerySet[] qsets = QuerySetList.instance().getQuerySets();
        return qsets[getQuerySetIndex()];
    }
    
    int getQuerySetIndex() {
        return querySetComboBox.getSelectedIndex();
    }
    
    void selectQuerySet(int index) {
        querySetComboBox.setSelectedIndex(index);
    }
    
    int getQuerySelection() {
        return inspectionsTable.getSelectedRow();
    }
    
    void selectQuery(int index) {
        inspectionsTable.getSelectionModel().setSelectionInterval(index, index);
    }
    
    class InspectionSelectionListener implements ListSelectionListener {
        public void valueChanged(ListSelectionEvent e) {
            if (!e.getValueIsAdjusting()) {
                EventQueue.invokeLater(new Runnable() {
                    public void run() {
                        Inspection[] nodes = getSelection();
                        setDescriptionText(nodes);
                        setOptionsPane(nodes);
                        moveUpButton.setEnabled(isMoveUpEnabled(nodes));
                        moveDownButton.setEnabled(isMoveDownEnabled(nodes));
			deleteButton.setEnabled(isDeleteEnabled(nodes));
                        exportButton.setEnabled(isExportEnabled(nodes));
                    }
                });
            }
        }
    }
    
    private static String getString(String key) {
        return NbBundle.getBundle(RefactoringManagerPanel.class).getString(key);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        inspectionsLabel = new javax.swing.JLabel();
        tablePanel = new javax.swing.JPanel();
        inspectionListScroller = new javax.swing.JScrollPane();
        inspectionsTable = new javax.swing.JTable();
        importButton = new javax.swing.JButton();
        deleteButton = new javax.swing.JButton();
        moveUpButton = new javax.swing.JButton();
        moveDownButton = new javax.swing.JButton();
        querySetLabel = new javax.swing.JLabel();
        querySetComboBox = new javax.swing.JComboBox();
        deleteQuerySetButton = new javax.swing.JButton();
        duplicateButton = new javax.swing.JButton();
        exportButton = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();
        tabbedPane = new javax.swing.JTabbedPane();
        descriptionScroller = new javax.swing.JScrollPane();
        descriptionTextArea = new javax.swing.JTextArea();
        optionsPanel = new javax.swing.JPanel();
        jSeparator3 = new javax.swing.JSeparator();

        setPreferredSize(new java.awt.Dimension(640, 480));
        inspectionsLabel.setLabelFor(tablePanel);
        org.openide.awt.Mnemonics.setLocalizedText(inspectionsLabel, org.openide.util.NbBundle.getBundle(RefactoringManagerPanel.class).getString("LBL_RefactoringMgr_Queries"));

        tablePanel.setLayout(new java.awt.BorderLayout());

        tablePanel.setBackground(javax.swing.UIManager.getDefaults().getColor("Table.background"));
        inspectionListScroller.setBackground(javax.swing.UIManager.getDefaults().getColor("Table.background"));
        inspectionsTable.setModel(new InspectionsModel());
        inspectionsTable.setShowVerticalLines(false);
        setCheckboxColumnWidth();
        if (UIManager.getColor("control") != null) // NOI18N
        inspectionsTable.setGridColor(UIManager.getColor("control")); // NOI18N

        inspectionsTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                processInspectionsTableMouseClicked(evt);
            }
        });

        inspectionListScroller.setViewportView(inspectionsTable);

        tablePanel.add(inspectionListScroller, java.awt.BorderLayout.CENTER);

        org.openide.awt.Mnemonics.setLocalizedText(importButton, java.util.ResourceBundle.getBundle("org/netbeans/modules/jackpot/ui/Bundle").getString("BTN_RefactoringMgr_Import"));
        importButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                importButtonHandler(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(deleteButton, java.util.ResourceBundle.getBundle("org/netbeans/modules/jackpot/ui/Bundle").getString("BTN_RefactoringMgr_Delete"));
        deleteButton.setEnabled(false);
        deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteButtonHandler(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(moveUpButton, java.util.ResourceBundle.getBundle("org/netbeans/modules/jackpot/ui/Bundle").getString("BTN_RefactoringMgr_MoveUp"));
        moveUpButton.setEnabled(false);
        moveUpButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                moveUpButtonHandler(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(moveDownButton, java.util.ResourceBundle.getBundle("org/netbeans/modules/jackpot/ui/Bundle").getString("BTN_RefactoringMgr_MoveDown"));
        moveDownButton.setEnabled(false);
        moveDownButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                moveDownButtonHandler(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(querySetLabel, java.util.ResourceBundle.getBundle("org/netbeans/modules/jackpot/ui/Bundle").getString("LBL_QuerySets"));

        setQuerySetComboBoxList();
        querySetComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                querySetChanged(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(deleteQuerySetButton, java.util.ResourceBundle.getBundle("org/netbeans/modules/jackpot/ui/Bundle").getString("BTN_DeleteQuerySet"));
        deleteQuerySetButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteQuerySetAction(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(duplicateButton, java.util.ResourceBundle.getBundle("org/netbeans/modules/jackpot/ui/Bundle").getString("BTN_DuplicateQuerySet"));
        duplicateButton.setPreferredSize(new java.awt.Dimension(81, 25));
        duplicateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                duplicateButtonAction(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(exportButton, java.util.ResourceBundle.getBundle("org/netbeans/modules/jackpot/ui/Bundle").getString("BTN_RefactoringMgr_Export"));
        exportButton.setEnabled(false);
        exportButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportButtonHandler(evt);
            }
        });

        jSeparator1.setBackground(javax.swing.UIManager.getDefaults().getColor("Panel.background"));
        jSeparator1.setForeground(javax.swing.UIManager.getDefaults().getColor("Panel.background"));

        jSeparator2.setBackground(javax.swing.UIManager.getDefaults().getColor("Panel.background"));
        jSeparator2.setForeground(javax.swing.UIManager.getDefaults().getColor("Panel.background"));

        descriptionScroller.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        descriptionTextArea.setColumns(20);
        descriptionTextArea.setLineWrap(true);
        descriptionTextArea.setRows(5);
        descriptionTextArea.setWrapStyleWord(true);
        descriptionTextArea.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                descriptionFocusLostHandler(evt);
            }
        });

        descriptionScroller.setViewportView(descriptionTextArea);

        tabbedPane.addTab(java.util.ResourceBundle.getBundle("org/netbeans/modules/jackpot/ui/Bundle").getString("LBL_RefactoringMgr_Description"), descriptionScroller);

        org.jdesktop.layout.GroupLayout optionsPanelLayout = new org.jdesktop.layout.GroupLayout(optionsPanel);
        optionsPanel.setLayout(optionsPanelLayout);
        optionsPanelLayout.setHorizontalGroup(
            optionsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 611, Short.MAX_VALUE)
        );
        optionsPanelLayout.setVerticalGroup(
            optionsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 90, Short.MAX_VALUE)
        );
        tabbedPane.addTab(java.util.ResourceBundle.getBundle("org/netbeans/modules/jackpot/ui/Bundle").getString("LBL_RefactoringMgr_Options"), optionsPanel);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(tabbedPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 616, Short.MAX_VALUE)
                    .add(jSeparator3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 616, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(tablePanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 485, Short.MAX_VALUE)
                            .add(layout.createSequentialGroup()
                                .add(querySetLabel)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(querySetComboBox, 0, 279, Short.MAX_VALUE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(duplicateButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 125, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(inspectionsLabel))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(deleteQuerySetButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 122, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(importButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 125, Short.MAX_VALUE)
                            .add(exportButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 125, Short.MAX_VALUE)
                            .add(jSeparator1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 122, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(deleteButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 125, Short.MAX_VALUE)
                            .add(jSeparator2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 122, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(moveUpButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 125, Short.MAX_VALUE)
                            .add(moveDownButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 122, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );

        layout.linkSize(new java.awt.Component[] {deleteButton, deleteQuerySetButton, duplicateButton, exportButton, importButton, jSeparator1, jSeparator2, moveDownButton, moveUpButton}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(querySetLabel)
                    .add(duplicateButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(querySetComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(deleteQuerySetButton))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jSeparator3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(0, 0, 0)
                .add(inspectionsLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(tablePanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 271, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED))
                    .add(layout.createSequentialGroup()
                        .add(importButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(exportButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jSeparator1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(deleteButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jSeparator2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(moveUpButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(moveDownButton)))
                .add(tabbedPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 117, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void exportButtonHandler(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportButtonHandler
        doExport(getSelection());
    }//GEN-LAST:event_exportButtonHandler

    private void deleteQuerySetAction(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteQuerySetAction
        deleteQuerySet();
    }//GEN-LAST:event_deleteQuerySetAction

    private void duplicateButtonAction(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_duplicateButtonAction
        duplicateQuerySet();
    }//GEN-LAST:event_duplicateButtonAction

    private void processInspectionsTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_processInspectionsTableMouseClicked
        if (evt.getButton() == evt.BUTTON1) {
            java.awt.Point pt = evt.getPoint();
            int col = inspectionsTable.columnAtPoint(pt);
            int row = inspectionsTable.rowAtPoint(pt);
            if (evt.getClickCount() == 1) {
                if (col == 0)
                    toggleQuerySetMembership(row);
            }
            else if (evt.getClickCount() == 2) {
                editInspection(inspections.getInspection(row));
            }
        }
    }//GEN-LAST:event_processInspectionsTableMouseClicked

    private void querySetChanged(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_querySetChanged
        // redraw table with new checkbox values for this QuerySet
        inspectionsTable.repaint();
        enableQuerySetDelete();
    }//GEN-LAST:event_querySetChanged

    private void descriptionFocusLostHandler(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_descriptionFocusLostHandler
        setInspectionDescription();
    }//GEN-LAST:event_descriptionFocusLostHandler

    private void moveDownButtonHandler(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_moveDownButtonHandler
        moveNode(false);        
    }//GEN-LAST:event_moveDownButtonHandler

    private void moveUpButtonHandler(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_moveUpButtonHandler
        moveNode(true);
    }//GEN-LAST:event_moveUpButtonHandler

    private void moveNode(boolean moveUp) {
        Inspection[] nodes = getSelection();
        assert nodes.length == 1; // checked by isMoveUpEnabled
        if (moveUp)
            inspections.moveUp(nodes[0]);
        else
            inspections.moveDown(nodes[0]);
        int pos = inspections.indexOf(nodes[0]);
        selectQuery(pos);
    }
    
    private void deleteButtonHandler(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteButtonHandler
        Inspection[] nodes = getSelection();
        inspections.delete(nodes);
    }//GEN-LAST:event_deleteButtonHandler

    private void importButtonHandler(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_importButtonHandler
	doImport(getSelection());
    }//GEN-LAST:event_importButtonHandler
                    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton deleteButton;
    private javax.swing.JButton deleteQuerySetButton;
    private javax.swing.JScrollPane descriptionScroller;
    private javax.swing.JTextArea descriptionTextArea;
    private javax.swing.JButton duplicateButton;
    private javax.swing.JButton exportButton;
    private javax.swing.JButton importButton;
    private javax.swing.JScrollPane inspectionListScroller;
    private javax.swing.JLabel inspectionsLabel;
    private javax.swing.JTable inspectionsTable;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JButton moveDownButton;
    private javax.swing.JButton moveUpButton;
    private javax.swing.JPanel optionsPanel;
    private javax.swing.JComboBox querySetComboBox;
    private javax.swing.JLabel querySetLabel;
    private javax.swing.JTabbedPane tabbedPane;
    private javax.swing.JPanel tablePanel;
    // End of variables declaration//GEN-END:variables
    
    public Inspection[] getSelection() {
        Inspection[] inspt = inspections.getInspections();
        ArrayList<Inspection> list = new ArrayList<Inspection>();
        for (int i = 0; i < inspt.length; i++)
            if (inspectionsTable.isRowSelected(i))
                list.add(inspt[i]);
        return list.toArray(new Inspection[0]);
    }
    
    public void addInspectionListSelectionListener(ListSelectionListener l) {
        inspectionsTable.getSelectionModel().addListSelectionListener(l);
    }
    
    public void removeListSelectionListener(ListSelectionListener l) {
        inspectionsTable.getSelectionModel().removeListSelectionListener(l);
    }
    
    public void addQuerySetListSelectionListener(ActionListener l) {
        querySetComboBox.addActionListener(l);
    }
    
    public void removeQuerySetListSelectionListener(ActionListener l) {
        querySetComboBox.removeActionListener(l);
    }
    
    private void duplicateQuerySet() {
        try {
            final NewQuerySetPanel panel = new NewQuerySetPanel();
            ActionListener al = new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String newName = panel.getQuerySetName();
                    Inspection[] inspections = getSelectedQuerySet().getInspections();
                    QuerySet newQuerySet = new QuerySet(newName, newName, inspections);
                    QuerySetList.instance().add(newQuerySet);
                    querySetComboBox.addItem(newName);
                    querySetComboBox.setSelectedItem(newName);
                }
            };
            final QuerySetList querySetList = QuerySetList.instance();
            final DialogDescriptor dd = new DialogDescriptor(panel,
                    NbBundle.getMessage(RefactoringManagerAction.class, "LBL_NewQuerySet_Title"),  // NOI18N
                    true,
                    al);
            panel.addDocumentListener(new DocumentListener() {
                public void changedUpdate(DocumentEvent e) {
                    updateOKButton();
                }
                public void insertUpdate(DocumentEvent e) {
                    updateOKButton();
                }
                public void removeUpdate(DocumentEvent e) {
                    updateOKButton();
                }
                private void updateOKButton() {
                    String name = panel.getQuerySetName();
                    boolean okay = name.length() > 0 && querySetList.getQuerySet(name) == null;
                    dd.setValid(okay);
                }
            });
            java.awt.Dialog dialog = DialogDisplayer.getDefault().createDialog(dd);
            dialog.setVisible(true);
        } catch (Throwable t) {
            ErrorManager.getDefault().notify(t);
        }
    }
    
    private void enableQuerySetDelete() {
        QuerySet qs = getSelectedQuerySet();
        deleteQuerySetButton.setEnabled(qs.isDeletable());
    }
    
    private void deleteQuerySet() {
        QuerySet qs = getSelectedQuerySet();
        String name = qs.getName();
        try {
            qs.delete();
            querySetComboBox.removeItem(name);
            enableQuerySetDelete();
        } catch (IOException e) {
            ErrorManager.getDefault().notify(e);
        }
    }
    
    private void setDescriptionText(Inspection[] nodes) {
        String text = "";
        boolean editable = false;
        if (nodes.length == 1) {
            text = nodes[0].getDescription();
            editable = true;
        } 
        descriptionTextArea.setText(text);
        descriptionTextArea.setEditable(editable);
    }
    
    private void setOptionsPane(Inspection[] nodes) {
        JComponent options = (nodes.length == 1) ?
            nodes[0].getOptionsPanel() : new JPanel();
        tabbedPane.setComponentAt(OPTIONS_TAB_INDEX, options);
        tabbedPane.setEnabledAt(OPTIONS_TAB_INDEX, options.getComponentCount() > 0);
        validate();
    }
    
    private void setInspectionDescription() {
        Inspection[] nodes = getSelection();
        if (nodes.length == 1) {
            String description = descriptionTextArea.getText();
            nodes[0].setDescription(description);
        }
    }
    
    private void setQuerySetComboBoxList() {
        QuerySet[] qsets = QuerySetList.instance().getQuerySets();
        String[] labels = new String[qsets.length];
        for (int i = 0; i < qsets.length; i++)
            labels[i] = qsets[i].getLocalizedName();
        querySetComboBox.setModel(new DefaultComboBoxModel(labels));
    }
    
    private void editInspection(final Inspection insp) {
        try {
            final EditQueryPanel panel = new EditQueryPanel();
            panel.setQueryName(insp.getInspector());
            panel.setRefactoringName(insp.getTransformer());
            panel.setDescription(insp.getDescription());
            ActionListener al = new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    insp.setInspector(panel.getQueryName());
                    insp.setTransformer(panel.getRefactoringName());
                    insp.setDescription(panel.getDescription());
                    setDescriptionText(new Inspection[] { insp });
                }
            };
            final DialogDescriptor dd = new DialogDescriptor(panel,
                    NbBundle.getMessage(RefactoringManagerAction.class, "LBL_EditQuery_Title"),  // NOI18N
                    true,
                    al);
            panel.addDocumentListener(new DocumentListener() {
                public void changedUpdate(DocumentEvent e) {
                    updateOKButton();
                }
                public void insertUpdate(DocumentEvent e) {
                    updateOKButton();
                }
                public void removeUpdate(DocumentEvent e) {
                    updateOKButton();
                }
                private void updateOKButton() {
                    String name = panel.getQueryName();
                    boolean okay = name.length() > 0 && 
                                   name.equals(insp.getInspector()) || 
                                   inspections.getInspection(name) == null;
                    dd.setValid(okay);
                }
            });
            java.awt.Dialog dialog = DialogDisplayer.getDefault().createDialog(dd);
            dialog.setVisible(true);
        } catch (Throwable t) {
            ErrorManager.getDefault().notify(t);
        }
    }
    
    private boolean isMoveUpEnabled(Inspection[] nodes) {
        if (nodes.length != 1)
            return false;
        int pos = inspections.indexOf(nodes [0]);
        return pos != -1 && pos > 0;
    }
    
    private boolean isMoveDownEnabled(Inspection[] nodes) {
        if (nodes.length != 1)
            return false;
        int pos = inspections.indexOf(nodes [0]);
        return pos != -1 && pos < (inspections.size() - 1);
    }
    
    private boolean isDeleteEnabled(Inspection[] nodes) {
	return nodes.length > 0;
    }
    
    private boolean isExportEnabled(Inspection[] nodes) {
	return nodes.length == 1 && nodes[0].getCommand().endsWith(".rules");
    }
    
    void createCommandFromFile(File file) throws IOException {
        if (file == null) {
            throw new IllegalArgumentException("Argument file cannot be null!"); // NOI18N
        }
        FileObject sourceFO = FileUtil.toFileObject(file);
        assert sourceFO != null : "FileObject not found for file " + file;
        if (!JackpotModule.getInstance().isQueryScript(sourceFO)) {
            String msg = MessageFormat.format(getString("MSG_NotQueryFile"), file.getName());
            NotifyDescriptor d = new NotifyDescriptor.Message(msg, NotifyDescriptor.ERROR_MESSAGE);
            DialogDisplayer.getDefault().notify(d);
            return;
        }
        InspectionsList.instance().importScript(sourceFO);
    }
    
    private void doImport(Inspection[] nodes) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle(getString("LBL_Panel_JFileChooser_AddTitle")); // NOI18N
        chooser.setApproveButtonText(getString("BTN_Panel_JFileChooser_AddButtonName")); // NOI18N
        chooser.setFileHidingEnabled(false);
        chooser.setMultiSelectionEnabled(false);
        chooser.setFileFilter(ruleFileFilter);
        chooser.setAcceptAllFileFilterUsed(false);
        int result = chooser.showOpenDialog(null);
        if (JFileChooser.APPROVE_OPTION == result) {
            File f = chooser.getSelectedFile();
            lastDirectory = f.getParentFile();
            assert f != null;
            if (!f.exists()) {
                String msg = MessageFormat.format(getString("MSG_NoSuchFile"), f.getName());
                NotifyDescriptor d = new NotifyDescriptor.Message(msg, NotifyDescriptor.ERROR_MESSAGE);
                DialogDisplayer.getDefault().notify(d);
            }
            else 
                try {
                    createCommandFromFile(f);
                } catch (IOException e) {
                    ErrorManager.getDefault().notify(e);
                }
        }    
    }

    private void doExport(Inspection[] nodes) {
        assert nodes.length == 1;
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle(getString("LBL_Panel_JFileChooser_ExportTitle")); // NOI18N
        chooser.setFileFilter(ruleFileFilter);
        String defaultName = nodes[0].getCommand();
        defaultName = defaultName.substring(defaultName.lastIndexOf('/') + 1);
        File def = lastDirectory != null ? new File(lastDirectory, defaultName) : new File(defaultName);
        chooser.setSelectedFile(def);
        int result = chooser.showSaveDialog(null);
        if (JFileChooser.APPROVE_OPTION == result) {
            File f = chooser.getSelectedFile();
            lastDirectory = f.getParentFile();
            assert f != null;
            if (f.exists()) {
                String msg = MessageFormat.format(getString("MSG_OverwriteOK"), f.getName());
                NotifyDescriptor d = new NotifyDescriptor.Confirmation(msg);
                Object response = DialogDisplayer.getDefault().notify(d);
                if (response == NotifyDescriptor.CANCEL_OPTION)
                    return;
            }
            try {
                nodes[0].export(f);
            } catch (IOException e) {
                String msg = MessageFormat.format(getString("MSG_ErrorExporting"), f.getName(), e.getLocalizedMessage());
                NotifyDescriptor d = new NotifyDescriptor.Message(msg, NotifyDescriptor.ERROR_MESSAGE);
                DialogDisplayer.getDefault().notify(d);
            }
        }    
    }
    private File lastDirectory;
    
    private void setCheckboxColumnWidth() {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                java.awt.Dimension cbSize = new JCheckBox().getPreferredSize();
                javax.swing.table.TableColumn col = inspectionsTable.getColumnModel().getColumn(0);
                col.setMaxWidth(cbSize.width);   
                inspectionsTable.revalidate();
            }
        });
    }

    private void toggleQuerySetMembership(int row) {
        QuerySet qset = getSelectedQuerySet();
        boolean[] index = qset.getInspectionIndex();
        inspectionsTable.setValueAt(!index[row], row, 0);
    }

    void close() {
        inspections.removeListDataListener((InspectionsModel)inspectionsTable.getModel());
    }
    
    class InspectionsModel extends AbstractTableModel implements ListDataListener {
        public int getRowCount() {
            return inspections.size();
        }

        public int getColumnCount() {
            return 3;
        }

        public Object getValueAt(int rowIndex, int columnIndex) {
            Inspection insp = inspections.getInspection(rowIndex);
            switch (columnIndex) {
                case 0: {
                    QuerySet qset = getSelectedQuerySet();
                    return qset.indexOf(insp) >= 0 ? Boolean.TRUE : Boolean.FALSE;
                }
                case 1:
                    return insp.getInspector();
                case 2:
                    return insp.getTransformer();
                default:
                    throw new IndexOutOfBoundsException("invalid column: " + columnIndex);
            }
       }

        public String getColumnName(int column) {
            switch (column) {
                case 0: return "";  // no name for the checkbox column
                case 1: return getString("LBL_RefactoringMgr_SearchFor");
                case 2: return getString("LBL_RefactoringMgr_Refactoring");
                default:
                    throw new IndexOutOfBoundsException("invalid column: " + column);
            }
        }

        public Class<?> getColumnClass(int columnIndex) {
            return columnIndex == 0 ? Boolean.class : String.class;
        }

        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            if (columnIndex == 0) {
                QuerySet qset = getSelectedQuerySet();
                boolean[] index = qset.getInspectionIndex();
                index[rowIndex] = ((Boolean)aValue).booleanValue();
                qset.setInspectionsByIndex(index);
                fireTableRowsUpdated(rowIndex, rowIndex);
            }
        }

        public void intervalAdded(ListDataEvent e) {
            fireTableRowsInserted(e.getIndex0(), e.getIndex1());
        }

        public void intervalRemoved(ListDataEvent e) {
            fireTableRowsDeleted(e.getIndex0(), e.getIndex1());
        }

        public void contentsChanged(ListDataEvent e) {
            fireTableDataChanged();
        }
    }

    private static class RuleFileFilter extends javax.swing.filechooser.FileFilter {
        public boolean accept( File f ) {
            return f.isDirectory() || f.getName().toLowerCase().endsWith(".rules"); // NOI18N
        }

        public String getDescription() {
            return getString("CTL_RuleFileFilterDescription");
        }
    }
    private static final RuleFileFilter ruleFileFilter = new RuleFileFilter();
    
    private static class ClassFileFilter extends javax.swing.filechooser.FileFilter {
        public boolean accept( File f ) {
            return f.isDirectory() || f.getName().endsWith(".class"); // NOI18N
        }

        public String getDescription() {
            return getString("CTL_ClassFileFilterDescription");
        }
    }
    private static final ClassFileFilter classFileFilter = new ClassFileFilter();
}
