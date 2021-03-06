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
 * The Original Software is the ETable module. The Initial Developer of the Original
 * Software is Nokia. Portions Copyright 2004 Nokia. All Rights Reserved.
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
package org.netbeans.swing.etable;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.table.TableColumnModel;

/**
 * Panel containing checkboxes for selecting visible columns
 * of a table.
 * @author David Strupl
 */
class ColumnSelectionPanel extends JPanel {

    /**
     * Map: ETableColumn --> JCheckBox
     */
    private Map checkBoxes = new HashMap();
    
    /**
     * Model allowing to show/hide columns.
     */
    private ETableColumnModel columnModel;
    
    /** Creates a new instance of ColumnSelectionPanel */
    public ColumnSelectionPanel(ETable table) {
        TableColumnModel columnModel = table.getColumnModel();
        setLayout(new GridBagLayout());
        if (! (columnModel instanceof ETableColumnModel)) {
            return;
        }
        ETableColumnModel etcm = (ETableColumnModel)columnModel;
        this.columnModel = etcm;
        List columns = Collections.list(etcm.getColumns());
        columns.addAll(etcm.hiddenColumns);
        Collections.sort(columns);
        int width = columns.size() / 10 + 1;
        layoutPanel(columns, width, table);
    }
    
    /**
     * Adds checkbox for each ETableColumn contained in the columns parameter.
     */
    private void layoutPanel(List columns, int width, ETable table) {
        Map displayNameToCheckBox = new HashMap();
        ArrayList displayNames = new ArrayList();
        for (Iterator it = columns.iterator(); it.hasNext(); ) {
            ETableColumn etc = (ETableColumn)it.next();
            JCheckBox checkBox = new JCheckBox();
            checkBoxes.put(etc, checkBox);
            String dName = table.getColumnDisplayName(etc.getHeaderValue().toString());
            checkBox.setText(dName);
            checkBox.setSelected(! columnModel.isColumnHidden(etc));
            checkBox.setEnabled(etc.isHidingAllowed());
            if (! displayNames.contains(dName)) {
                // the expected case
                displayNameToCheckBox.put(dName, checkBox);
            } else {
                // the same display name is used for more columns - fuj
                ArrayList al = null;
                Object theFirstOne = displayNameToCheckBox.get(dName);
                if (theFirstOne instanceof JCheckBox) {
                    JCheckBox firstCheckBox = (JCheckBox)theFirstOne;
                    al = new ArrayList();
                    al.add(firstCheckBox);
                } else {
                    // already a list there
                    if (theFirstOne instanceof ArrayList) {
                        al = (ArrayList)theFirstOne;
                    } else {
                        throw new IllegalStateException("Wrong object theFirstOne is " + theFirstOne);
                    }
                }
                al.add(checkBox);
                displayNameToCheckBox.put(dName, al);
            }
            displayNames.add(dName);
        }
        Collections.sort(displayNames, Collator.getInstance());
        int i = 0;
        int j = 0;
        int index = 0;
        int rows = columns.size() / width;
        for (Iterator it = displayNames.iterator(); it.hasNext(); i++) {
            if (i >= rows) {
                i = 0;
                j++;
            }
            String displayName = (String)it.next();
            Object obj = displayNameToCheckBox.get(displayName);
            JCheckBox checkBox = null;
            if (obj instanceof JCheckBox) {
                checkBox = (JCheckBox)obj;
            } else {
                // in case there are duplicate names we store ArrayLists
                // of JCheckBoxes
                if (obj instanceof ArrayList) {
                    ArrayList al = (ArrayList)obj;
                    if (index >= al.size()) {
                        index = 0;
                    }
                    checkBox = (JCheckBox)al.get(index++);
                } else {
                    throw new IllegalStateException("Wrong object obj is " + obj);
                }
            }
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = j;
            gridBagConstraints.gridy = i;
            gridBagConstraints.insets = new java.awt.Insets(5, 12, 0, 12);
            gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
            gridBagConstraints.weightx = 1;
            add(checkBox, gridBagConstraints);
        }
    }
    
    /**
     * After the user clicks Ok this method will hide/un-hide the
     * columns according to the selected checkboxes.
     */
    public void changeColumnVisibility() {
        if (columnModel == null) {
            return;
        }
        for (Iterator it = checkBoxes.keySet().iterator(); it.hasNext(); ) {
            ETableColumn etc = (ETableColumn) it.next();
            JCheckBox checkBox = (JCheckBox)checkBoxes.get(etc);
            columnModel.setColumnHidden(etc,! checkBox.isSelected());
        }
    }
    
    /**
     * Shows the popup allowing to show/hide columns.
     */
    static void showColumnSelectionPopup(Component c, final ETable table) {
        if (! table.isPopupUsedFromTheCorner()) {
            showColumnSelectionDialog(table);
            return;
        }
        JPopupMenu popup = new JPopupMenu();
        TableColumnModel columnModel = table.getColumnModel();
        if (! (columnModel instanceof ETableColumnModel)) {
            return;
        }
        final ETableColumnModel etcm = (ETableColumnModel)columnModel;
        List columns = Collections.list(etcm.getColumns());
        columns.addAll(etcm.hiddenColumns);
        Collections.sort(columns);
        Map displayNameToCheckBox = new HashMap();
        ArrayList displayNames = new ArrayList();
        for (Iterator it = columns.iterator(); it.hasNext(); ) {
            final ETableColumn etc = (ETableColumn)it.next();
            final JCheckBoxMenuItem checkBox = new JCheckBoxMenuItem();
            String dName = table.getColumnDisplayName(etc.getHeaderValue().toString());
            checkBox.setText(dName);
            checkBox.setSelected(! etcm.isColumnHidden(etc));
            checkBox.setEnabled(etc.isHidingAllowed());
            checkBox.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    etcm.setColumnHidden(etc,! checkBox.isSelected());
                    table.updateColumnSelectionMouseListener();
                }
            });
            if (! displayNames.contains(dName)) {
                // the expected case
                displayNameToCheckBox.put(dName, checkBox);
            } else {
                // the same display name is used for more columns - fuj
                ArrayList al = null;
                Object theFirstOne = displayNameToCheckBox.get(dName);
                if (theFirstOne instanceof JCheckBoxMenuItem) {
                    JCheckBoxMenuItem firstCheckBox = (JCheckBoxMenuItem)theFirstOne;
                    al = new ArrayList();
                    al.add(firstCheckBox);
                } else {
                    // already a list there
                    if (theFirstOne instanceof ArrayList) {
                        al = (ArrayList)theFirstOne;
                    } else {
                        throw new IllegalStateException("Wrong object theFirstOne is " + theFirstOne);
                    }
                }
                al.add(checkBox);
                displayNameToCheckBox.put(dName, al);
            }
            displayNames.add(dName);
        }
        Collections.sort(displayNames, Collator.getInstance());
        int index = 0;
        for (Iterator it = displayNames.iterator(); it.hasNext(); ) {
            String displayName = (String)it.next();
            Object obj = displayNameToCheckBox.get(displayName);
            JCheckBoxMenuItem checkBox = null;
            if (obj instanceof JCheckBoxMenuItem) {
                checkBox = (JCheckBoxMenuItem)obj;
            } else {
                // in case there are duplicate names we store ArrayLists
                // of JCheckBoxes
                if (obj instanceof ArrayList) {
                    ArrayList al = (ArrayList)obj;
                    if (index >= al.size()) {
                        index = 0;
                    }
                    checkBox = (JCheckBoxMenuItem)al.get(index++);
                } else {
                    throw new IllegalStateException("Wrong object obj is " + obj);
                }
            }
            popup.add(checkBox);
        }
        popup.show(c, 8, 8);
    }
    
    /**
     * Shows dialog allowing to show/hide columns.
     */
    static void showColumnSelectionDialog(ETable table) {
        TableColumnSelector tcs = table.getColumnSelector();
        if (tcs != null) {
            ETableColumnModel etcm = (ETableColumnModel)table.getColumnModel();
            TableColumnSelector.TreeNode root = etcm.getColumnHierarchyRoot();
            if (root != null) {
                String[] origVisible = getAvailableColumnNames(table, true);
                String[] visibleColumns = tcs.selectVisibleColumns(root, origVisible);
                makeVisibleColumns(table, visibleColumns);
            } else {
                String[] availableColumns = getAvailableColumnNames(table, false);
                String[] origVisible = getAvailableColumnNames(table, true);
                String[] visibleColumns = tcs.selectVisibleColumns(availableColumns, origVisible);
                makeVisibleColumns(table, visibleColumns);
            }
            return;
        }
        // The default behaviour:
        ColumnSelectionPanel panel = new ColumnSelectionPanel(table);
        int res = JOptionPane.showConfirmDialog(null, panel, table.selectVisibleColumnsLabel, JOptionPane.OK_CANCEL_OPTION);
        if (res == JOptionPane.OK_OPTION) {
            panel.changeColumnVisibility();
            table.updateColumnSelectionMouseListener();
        }
    }

    /**
     * This method is called after the user made a selection. Applies the
     * changes to the visible column for the given table.
     */
    private static void makeVisibleColumns(ETable table, String[] visibleColumns) {
        HashSet visible = new HashSet(Arrays.asList(visibleColumns));
        TableColumnModel columnModel = table.getColumnModel();
        if (! (columnModel instanceof ETableColumnModel)) {
            return;
        }
        final ETableColumnModel etcm = (ETableColumnModel)columnModel;
        List columns = Collections.list(etcm.getColumns());
        columns.addAll(etcm.hiddenColumns);
        Collections.sort(columns);
        for (Iterator it = columns.iterator(); it.hasNext(); ) {
            final ETableColumn etc = (ETableColumn)it.next();
            String dName = table.getColumnDisplayName(etc.getHeaderValue().toString());
            etcm.setColumnHidden(etc, !visible.contains(dName));
        }
    }

    /**
     * Computes the strings shown to the user in the selection dialog.
     */
    private static String[] getAvailableColumnNames(ETable table, boolean visibleOnly) {
        TableColumnModel columnModel = table.getColumnModel();
        if (! (columnModel instanceof ETableColumnModel)) {
            return new String[0];
        }
        final ETableColumnModel etcm = (ETableColumnModel)columnModel;
        List columns = Collections.list(etcm.getColumns());
        if (!visibleOnly) {
            columns.addAll(etcm.hiddenColumns);
        }
        Collections.sort(columns);
        ArrayList displayNames = new ArrayList();
        for (Iterator it = columns.iterator(); it.hasNext(); ) {
            final ETableColumn etc = (ETableColumn)it.next();
            String dName = table.getColumnDisplayName(etc.getHeaderValue().toString());
            displayNames.add(dName);
        }
        Collections.sort(displayNames, Collator.getInstance());
        return (String[])displayNames.toArray(new String[displayNames.size()]);
    }
}
