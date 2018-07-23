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

