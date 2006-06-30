/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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

