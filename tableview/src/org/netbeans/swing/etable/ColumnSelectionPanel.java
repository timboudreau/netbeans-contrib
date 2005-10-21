/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is the ETable module. The Initial Developer of the Original
 * Code is Nokia. Portions Copyright 2004 Nokia. All Rights Reserved.
 */
package org.netbeans.swing.etable;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;
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
    public ColumnSelectionPanel(TableColumnModel columnModel, ETable table) {
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
        int i = 0;
        int j = 0;
        int rows = columns.size() / width;
        for (Iterator it = columns.iterator(); it.hasNext(); i++) {
            if (i >= rows) {
                i = 0;
                j++;
            }
            ETableColumn etc = (ETableColumn)it.next();
            JCheckBox checkBox = new JCheckBox();
            checkBoxes.put(etc, checkBox);
            checkBox.setText(
                    table.getColumnDisplayName(etc.getHeaderValue().toString()));
            checkBox.setSelected(! columnModel.isColumnHidden(etc));
            checkBox.setEnabled(etc.isHidingAllowed());
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
}
