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
 * The Original Software is the LaTeX module.
 * The Initial Developer of the Original Software is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002-2004.
 * All Rights Reserved.
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
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.bibtex.table;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;

/**
 *
 * @author Jan Lahoda
 */
public class SortingTableModel extends AbstractTableModel implements TableModelListener {
    
    private TableModel delegate;
    private int        sortingColumn;
    private boolean    ascendingSort;
    private int[]      rows;
    
    private MouseListener tableListener;
    
    /** Creates a new instance of SortingTableModel */
    public SortingTableModel(TableModel delegate) {
        this.delegate = delegate;
        delegate.addTableModelListener(this);
        sortingColumn = (-1);
        ascendingSort = true;
        rows = null;
    }
    
    public void addMouseListenerToTable(JTable table) {
        if (tableListener == null) {
            tableListener = new MouseAdapter() {
                public void mouseClicked(MouseEvent evt) {
                    System.err.println("evt=" + evt);
                    Component c = evt.getComponent();
                    if (c instanceof JTableHeader) {
                        JTableHeader h = (JTableHeader)c;
                        int index = h.columnAtPoint(evt.getPoint());
                        //issue 38442, column can be -1 if this is the
                        //upper right corner - there's no column there,
                        //so make sure it's an index >=0.
                        if (index >= 0) {
                            clickOnColumnAction( index );
                        }
                    }
                }
            };
        }
        
        table.getTableHeader().addMouseListener(tableListener);
    }
    
    /*package private*/int[] getRows() {
        return rows;
    }
    
    public void removeMouseListenerFromTable(JTable table) {
        if (tableListener != null)
            table.getTableHeader().removeMouseListener(tableListener);
    }
    
    private void clickOnColumnAction(int index) {
        if (sortingColumn != index) {
            setSorting(index, true);
        } else {
            if (ascendingSort) {
                setSorting(index, false);
            } else {
                setSorting(-1, true);
            }
        }
    }
    
    private void updateSorting() {
        if (sortingColumn == (-1)) {
            if (rows != null) {
                rows = null;
                fireTableDataChanged();
            }
            return ;
        }
        
        List columnData = new ArrayList();
        int  rowCount   = delegate.getRowCount();
        
        for (int row = 0; row < rowCount; row++) {
            columnData.add(new Item(row, delegate.getValueAt(row, sortingColumn)));
        }
        
        Collections.sort(columnData, new ItemComparator());
        
        System.err.println("columnData=" + columnData);
        
        if (rows == null || rows.length != columnData.size()) {
            rows = new int[columnData.size()];
        }
        
        for (int row = 0; row < rowCount; row++) {
            int index = ((Item) columnData.get(row)).index;
            
            rows[ascendingSort ? row : (rowCount - row - 1)] = index;
            System.err.println("rows[" + row + "]=" + rows[row]);
        }
        
        fireTableDataChanged();
    }
    
    private static class Item {
        private int index;
        private Object content;
        
        public Item(int index, Object content) {
            this.index   = index;
            this.content = content;
        }

        public String toString() {
            return "[Item " + index + ", " + content + "]";
        }
    }
    
    private static class ItemComparator implements Comparator {
        
        public int compare(Object o1, Object o2) {
            Item i1 = (Item) o1;
            Item i2 = (Item) o2;
            
            //the Collections.sort is stable, so no need to hasle with index:
            System.err.println(i1.content + "=" + i1.content.getClass());
            System.err.println(i2.content + "=" + i2.content.getClass());
            if (i1.content instanceof Comparable && i2.content instanceof Comparable) { //in order to assure that is correct compareTo (see javadoc)
                return ((Comparable) i1.content).compareTo(i2.content);
            }
            
            //Temporary:?
            String contentString = i1.content.toString();
            String oString = i2.content.toString();
            
            return contentString.compareTo(oString);
        }
    }
    
    public void setSorting(int sortingColumn, boolean ascendingSort) {
        this.sortingColumn = sortingColumn;
        this.ascendingSort = ascendingSort;
        
        updateSorting();
    }
    
    public int getSortingColumn() {
        return sortingColumn;
    }
    
    public boolean isAscendingSort() {
        return ascendingSort;
    }

    public int getColumnCount() {
        return delegate.getColumnCount();
    }

    public int getRowCount() {
        return delegate.getRowCount();
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        if (rows != null) {
            System.err.println("rows!=null");
            rowIndex = rows[rowIndex];
        } else {
            System.err.println("rows==");
        }
        return delegate.getValueAt(rowIndex, columnIndex);
    }
    
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        if (rows != null) {
            rowIndex = rows[rowIndex];
        }
        return delegate.isCellEditable(rowIndex, columnIndex);
    }
    
    public void setValueAt(Object o, int rowIndex, int columnIndex) {
        if (rows != null) {
            rowIndex = rows[rowIndex];
        }
        delegate.setValueAt(o, rowIndex, columnIndex);
    }
    
    public String getColumnName(int columnIndex) {
        return delegate.getColumnName(columnIndex);
    }
    
    public Class getColumnClass(int columnIndex) {
        return delegate.getColumnClass(columnIndex);
    }

    public void tableChanged(TableModelEvent e) {
        fireTableChanged(e);
    }
    
}
