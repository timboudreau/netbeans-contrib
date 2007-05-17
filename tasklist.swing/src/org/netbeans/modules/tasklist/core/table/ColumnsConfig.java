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

package org.netbeans.modules.tasklist.core.table;

import java.io.Serializable;
import java.util.ArrayList;
import javax.swing.JTable;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

/**
 * Columns configuration.
 *
 * @author tl
 */
public class ColumnsConfig implements Serializable {
    public static final long serialVersionUID = 2L;

    /**
     * Returns the columns configuration that could be serialized.
     *
     * @param table a table
     * @return columns configuration (visible columns, sorting etc.)
     */
    public static ColumnsConfig getColumnsConfig(JTable table) {
        ColumnsConfig cc = new ColumnsConfig();
        
        TableColumnModel ctm = table.getColumnModel();
        assert ctm != null : "ctm == null"; // NOI18N
        
        cc.columns = new int[ctm.getColumnCount()];
        cc.columnWidths = new int[ctm.getColumnCount()];
        for (int i = 0; i < ctm.getColumnCount(); i++) {
            TableColumn c = ctm.getColumn(i);
            cc.columns[i] = c.getModelIndex();
            cc.columnWidths[i] = c.getWidth();
        }
        
        if (table instanceof SortableTable) {
            cc.sortedColumn = 
                    ((SortableTable) table).getSortingModel().getSortedColumn();
            cc.ascending = !((SortableTable) table).getSortingModel().
                    isSortOrderDescending();
        } 
        
        return cc;
    }
    
    /**
     * Sets columns configuration read from a stream.
     *
     * @param table a table
     * @param config columns configuration
     */
    public static void setColumnsConfig(JTable table, ColumnsConfig config) {
        //if (UTUtils.LOGGER.isLoggable(Level.FINE))
        //    Thread.dumpStack();
        
        assert config != null : "config == null"; // NOI18N
        
        table.createDefaultColumnsFromModel();

        ColumnsConfig cc = (ColumnsConfig) config;
        
        ArrayList<TableColumn> newc = new ArrayList<TableColumn>();
        TableColumnModel tcm = table.getColumnModel();
        assert tcm != null : "tcm == null"; // NOI18N

        for (int i = 0; i < cc.columns.length; i++) {
            for (int j = 0; j < tcm.getColumnCount(); j++) {
                TableColumn c = tcm.getColumn(j);
                if (cc.columns[i] == c.getModelIndex()) {
                    newc.add(c);
                    tcm.removeColumn(c);
                    c.setPreferredWidth(cc.columnWidths[i]);
                    c.setWidth(cc.columnWidths[i]);
                    break;
                }
            }
        }
        while (tcm.getColumnCount() > 0) {
            tcm.removeColumn(tcm.getColumn(0));
        }
        for (int i = 0; i < newc.size(); i ++) {
            tcm.addColumn(newc.get(i));
        }
    }

    /** 
     * Model indexes for visible columns
     */
    public int[] columns;

    /**
     * Widths of the columns in pixels
     */
    public int[] columnWidths;

    /**
     * Model index or -1
     */
    public int sortedColumn = -1;

    /**
     * Sorting order
     */
    public boolean ascending;

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("ColumnsConfig["); // NOI18N
        sb.append("sortedColumn=").append(sortedColumn); // NOI18N
        sb.append(", ascending=").append(sortedColumn); // NOI18N
        sb.append(", columns=["); // NOI18N
        for (int i = 0; i < columns.length; i++) {
            if (i != 0)
                sb.append(", "); // NOI18N
            sb.append(columns[i]);
            sb.append("->"); // NOI18N
            sb.append(columnWidths[i]);
        }
        sb.append("]"); // NOI18N
        sb.append("]"); // NOI18N
        return sb.toString();
    }
}

