/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcscore.util.table;

import java.util.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.table.*;

/**
 *
 * @author  mkleint
 * @version
 */
public class ColumnSortListener extends MouseAdapter {
    protected JTable table;
    
    public ColumnSortListener(JTable usedTable) {
        table = usedTable;
    }
    
    
    public void mouseClicked(java.awt.event.MouseEvent mouseEvent) {
        TableColumnModel colTab = table.getColumnModel();
        TableInfoModel model = (TableInfoModel)table.getModel();
        Object oldSelection = model.getElementAt(table.getSelectedRow());
        int index = colTab.getColumnIndexAtX(mouseEvent.getX());
        if (!model.isColumnSortable(index)) {
            return;
        }
        int modIndex = colTab.getColumn(index).getModelIndex();
        if (modIndex < 0) return;
        for (int ind = 0; ind < model.getColumnCount(); ind++) {
            TableColumn col = colTab.getColumn(ind);
            String title = model.getColumnName(col.getModelIndex());
            if (col.getModelIndex() == modIndex) {
                if (model.getActiveColumn() == modIndex) {
                    model.setDirection(model.getDirection() == model.DESCENDING);
                } else {
                    model.setDirection(true);
                    // for new column always start with Ascending
                }
                if (model.getDirection() == model.ASCENDING) title = title + " (+)"; //NOI18N
                else title = title + " (-)"; //NOI18N
            }
            col.setHeaderValue(title);
        }
        table.getTableHeader().repaint();
        model.setActiveColumn(modIndex);
        Collections.sort(model.getList(), model);
        // find the previsously selected row.
        table.tableChanged(new javax.swing.event.TableModelEvent(model));
        table.repaint();
        for (int rowIndex = 0; rowIndex < model.getRowCount(); rowIndex++) {
            Object newObj = model.getElementAt(rowIndex);
            if (newObj == oldSelection) {
                table.changeSelection(rowIndex,0,false,false);
                break;
            }
        }
    }
    
}

