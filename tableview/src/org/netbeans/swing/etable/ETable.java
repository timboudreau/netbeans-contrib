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
import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.Vector;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.*;

/**
 * <UL>Extended JTable (ETable) adds these features to JTable:
 *     <LI> The notion of fully editable (non-editable) table. </LI>
 *     <LI> <strong>Sorting</strong> the rows of the table by clicking the header.
 *          Shift-Click allows to use more columns for the sort. The sort is
 *          based on the values implementing Comparable interface. </LI>
 *     <LI> Automatic <strong>column width</strong> after init or after
 *          the model is changed (or triggered by "Ctrl-+" shortcut). 
 *          Automatic resize the column after double-click
 *          in the header column divider araa. </LI>
 *     <LI> <strong>Persistence</strong> of the user customized settings via
 *          methods readSettings and writeSettings.
 *     <LI> <strong>Quick-Filter</strong> features allowing to show only
 *          certain rows from the model (see setQuickFilter()). </LI>
 *     <LI> 
 * </UL>
 * @author David Strupl
 */
public class ETable extends JTable {
    
    /** Possible value for editing property */
    private final static int FULLY_EDITABLE = 1;
    /** Possible value for editing property */
    private final static int FULLY_NONEDITABLE = 2;
    /** Possible value for editing property */
    private final static int DEFAULT = 3;

    /**
     * Property allowing to make the table FULLY_NONEDITABLE and
     * FULLY_EDITABLE.
     */
    private int editing = DEFAULT;
    
    /** 
     * Array with size exactly sama as the number of rows in the data model
     * or null. If it is not null the row originally at index i will be
     * displayed on index sortingPermutation[i].
     */
    transient int [] sortingPermutation;
    
    /**
     *
     */
    private transient int filteredRowCount;
    
    /**
     *
     */
    private Object quickFilterObject;
    
    /**
     *
     */
    private int quickFilterColumn = -1;
    
    /**
     * If the table data model is changed we reset (and then recompute)
     * the sorting permutation and the row count.
     */
    private TableModelListener tableModelListener = new TableModelListener() {
        public void tableChanged(TableModelEvent e) {
            sortingPermutation = null;
            filteredRowCount = -1;
        }
    };

    /**
     * Listener reacting to the user clicks on the header.
     */
    private MouseListener headerMouseListener = new HeaderMouseListener();

    /**
     * Constructs a default <code>JTable</code> that is initialized with a default
     * data model, a default column model, and a default selection
     * model.
     *
     * @see #createDefaultDataModel
     * @see #createDefaultColumnModel
     * @see #createDefaultSelectionModel
     */
    public ETable() {
        updateMouseListener();
    }
    
    /**
     * Constructs a <code>JTable</code> that is initialized with
     * <code>dm</code> as the data model, a default column model,
     * and a default selection model.
     *
     * @param dm        the data model for the table
     * @see #createDefaultColumnModel
     * @see #createDefaultSelectionModel
     */
    public ETable(TableModel dm) {
        super(dm);
        updateMouseListener();
    }
    
    /**
     * Constructs a <code>JTable</code> that is initialized with
     * <code>dm</code> as the data model, <code>cm</code>
     * as the column model, and a default selection model.
     *
     * @param dm        the data model for the table
     * @param cm        the column model for the table
     * @see #createDefaultSelectionModel
     */
    public ETable(TableModel dm, TableColumnModel cm) {
        super(dm, cm);
        updateMouseListener();
    }
    
    /**
     * Constructs a <code>JTable</code> that is initialized with
     * <code>dm</code> as the data model, <code>cm</code> as the
     * column model, and <code>sm</code> as the selection model.
     * If any of the parameters are <code>null</code> this method
     * will initialize the table with the corresponding default model.
     * The <code>autoCreateColumnsFromModel</code> flag is set to false
     * if <code>cm</code> is non-null, otherwise it is set to true
     * and the column model is populated with suitable
     * <code>TableColumns</code> for the columns in <code>dm</code>.
     *
     * @param dm        the data model for the table
     * @param cm        the column model for the table
     * @param sm        the row selection model for the table
     * @see #createDefaultDataModel
     * @see #createDefaultColumnModel
     * @see #createDefaultSelectionModel
     */
    public ETable(TableModel dm, TableColumnModel cm, ListSelectionModel sm) {
        super(dm, cm, sm);
        updateMouseListener();
    }
    
    /**
     * Constructs a <code>JTable</code> with <code>numRows</code>
     * and <code>numColumns</code> of empty cells using
     * <code>DefaultTableModel</code>.  The columns will have
     * names of the form "A", "B", "C", etc.
     *
     * @param numRows           the number of rows the table holds
     * @param numColumns        the number of columns the table holds
     * @see javax.swing.table.DefaultTableModel
     */
    public ETable(int numRows, int numColumns) {
        super(numRows, numColumns);
        updateMouseListener();
    }
    
    /**
     * Constructs a <code>JTable</code> to display the values in the
     * <code>Vector</code> of <code>Vectors</code>, <code>rowData</code>,
     * with column names, <code>columnNames</code>.  The
     * <code>Vectors</code> contained in <code>rowData</code>
     * should contain the values for that row. In other words,
     * the value of the cell at row 1, column 5 can be obtained
     * with the following code:
     * <p>
     * <pre>((Vector)rowData.elementAt(1)).elementAt(5);</pre>
     * <p>
     * @param rowData           the data for the new table
     * @param columnNames       names of each column
     */
    public ETable(Vector rowData, Vector columnNames) {
        super(rowData, columnNames);
        updateMouseListener();
    }
    
    /**
     * Constructs a <code>JTable</code> to display the values in the two dimensional array,
     * <code>rowData</code>, with column names, <code>columnNames</code>.
     * <code>rowData</code> is an array of rows, so the value of the cell at row 1,
     * column 5 can be obtained with the following code:
     * <p>
     * <pre> rowData[1][5]; </pre>
     * <p>
     * All rows must be of the same length as <code>columnNames</code>.
     * <p>
     * @param rowData           the data for the new table
     * @param columnNames       names of each column
     */
    public ETable(final Object[][] rowData, final Object[] columnNames) {
        super(rowData, columnNames);
        updateMouseListener();
    }
    
    /**
     * Returns true if the cell at <code>row</code> and <code>column</code>
     * is editable.  Otherwise, invoking <code>setValueAt</code> on the cell
     * will have no effect.
     * <p>
     * Returns true always if the <code>ETable</code> is fully editable.
     * <p>
     * Returns false always if the <code>ETable</code> is fully non-editable.
     *
     * @param   row      the row whose value is to be queried
     * @param   column   the column whose value is to be queried
     * @return  true if the cell is editable
     * @see #setValueAt
     * @see #setFullyEditable
     * @see #setFullyNonEditable
     */
    public boolean isCellEditable(int row, int column) {
        if(editing == FULLY_EDITABLE) {
            return true;
        }
        if(editing == FULLY_NONEDITABLE) {
            return false;
        }
        int modelRow = convertRowIndexToModel(row);
        return super.isCellEditable(modelRow, column);
    }

    public TableCellRenderer getCellRenderer(int row, int column) {
        int modelRow = convertRowIndexToModel(row);
//        if (modelRow != row) {
//            System.err.println("getCellRenderer using converted value [" + row + "] --> " + modelRow);
//        }
        return super.getCellRenderer(modelRow, column);
    }
    
    public TableCellEditor getCellEditor(int row, int column) {
        int modelRow = convertRowIndexToModel(row);
//        if (modelRow != row) {
//            System.err.println("getCellEditor using converted value [" + row + "] --> " + modelRow);
//        }
        return super.getCellEditor(modelRow, column);
    }
    
    /**
     * Sets all the cells in the <code>ETable</code> to be editable if
     * <code>fullyEditable</code> is true.
     * if <code>fullyEditable</code> is false, sets the table cells into
     * their default state as in JTable.
     *
     * @param   fullyEditable   true if the table is meant to be fully editable.
     *                          false if the table is meant to take the defalut
     *                          state for editing.
     * @see #getFullyEditable
     */
    public void setFullyEditable(boolean fullyEditable) {
        if (fullyEditable) {
            editing = FULLY_EDITABLE;
            if(!getShowHorizontalLines()) {
                setShowHorizontalLines(true);
            }
            Border border = BorderFactory.createLineBorder
                    (UIManager.getColor("Table.borderAllEditable"));
            Border filler = BorderFactory.createLineBorder(getBackground());
            CompoundBorder compound = new CompoundBorder(border, filler);
            setBorder(new CompoundBorder(compound, border));
        } else {
            editing = DEFAULT;
            setBorder( null );
        }
        Color c = UIManager.getColor("Table.defaultGrid");
        if (c != null) {
            setGridColor(c);
        }
    }
    
    /**
     * Sets all the cells in the <code>ETable</code> to be non-editable if
     * <code>fullyNonEditable</code> is true.
     * If <code>fullyNonEditable</code> is false, sets the table cells into
     * their default state as in <code>JTable</code>.
     *
     * @param   fullyEditable   true if the table is meant to be fully non-editable.
     *                          false if the table is meant to take the defalut
     *                          state for editing.
     * @see #getFullyNonEditable
     */
    public void setFullyNonEditable(boolean fullyNonEditable) {
        if (fullyNonEditable) {
            editing = FULLY_NONEDITABLE;
            if(getShowHorizontalLines())
                setShowHorizontalLines(false);
            setBorder(BorderFactory.createLineBorder
                    (UIManager.getColor("Table.border")));
            Color c = UIManager.getColor("Table.noneditableGrid");
            if (c != null) {
                setGridColor(c);
            }
        } else {
            editing = DEFAULT;
            setBorder( null );
            if(!getShowHorizontalLines())
                setShowHorizontalLines(true);
            setGridColor(UIManager.getColor("Table.defaultGrid"));
        }
    }
    
    /**
     * Returns true if <code>ETable</code> is fully editable.
     *
     * @return  true if the the table is fully editable.
     * @see #setFullyEditable
     */
    public boolean getFullyEditable() {
        return editing == FULLY_EDITABLE;
    }
    
    /**
     * Returns true if <code>ETable</code> is fully non-editable.
     *
     * @return  true if the the table is fully non-editable.
     * @see #setFullyNonEditable
     */
    public boolean getFullyNonEditable() {
        return editing == FULLY_NONEDITABLE;
    }
    
    /**
     * Sets the table cell background colors accodring to NET UI guidelines.
     * <p>
     * This is needed in case where the user does not use the NET Look and Feel,
     * but still wants to paint the cell background colors accoring to NET L&F.
     * <p>
     * This needs to be called also in case where the user has custom table cell
     * renderer (that is not a <code>DefaultTableCellRenderer</code> or a
     * sub-class of it) for a cell even though NET L&F package is used, if the
     * cell background colors need to be consistent for the custom renderer.
     *
     * @param   renderer   the custom cell renderer to be painted
     * @param   isSelected true if the custom cell is selected
     * @param   row        the row, the custom cell corresponds to
     * @param   column     the column, the custom cell corresponds to
     */
    public void setNETCellBackground(Component renderer, boolean isSelected,
            int row, int column) {
        if (row%2 == 0) { //Background 2
            if(isSelected) {
                renderer.setBackground(UIManager.getColor("Table.selectionBackground2"));
            } else {
                renderer.setBackground(UIManager.getColor("Table.background2"));
            }
        } else { // Background 1
            if(isSelected) {
                renderer.setBackground(UIManager.getColor("Table.selectionBackground1"));
            } else {
                renderer.setBackground(UIManager.getColor("Table.background1"));
            }
        }
    }

    /**
     * Overriden to use ETableColumns instead of the original TableColumns.
     */
    public void createDefaultColumnsFromModel() {
        TableModel model = getModel();
        if (model != null) {
            int modelColumnCount = model.getColumnCount();
            TableColumn newColumns[] = new TableColumn[modelColumnCount];
            for (int i = 0; i < newColumns.length; i++) {
                newColumns[i] = createColumn(i);
            }
            TableColumnModel columnModel = getColumnModel();
            while (columnModel.getColumnCount() > 0) {
                columnModel.removeColumn(columnModel.getColumn(0));
            }
            for (int i = 0; i < newColumns.length; i++) {
                addColumn(newColumns[i]);
            }
        }
    }

    /**
     * Allow to plug own TableColumn implementation.
     * This implementation returns ETableColumn.
     * Called from createDefaultColumnsFromModel().
     */
    protected TableColumn createColumn(int modelIndex) {
        return new ETableColumn(modelIndex);
    }
   
    /**
     * Overriden to use ETableColumnModel as TableColumnModel.
     */
    protected TableColumnModel createDefaultColumnModel() {
        return new ETableColumnModel();
    }

    public Object getValueAt(int row, int column) {
        int modelRow = convertRowIndexToModel(row);
//        if (modelRow != row) {
//            System.err.println("getValueAt using converted value [" + row + "] --> " + modelRow);
//        }
        return super.getValueAt(modelRow, column);
    }

    public void setValueAt(Object aValue, int row, int column) {
        int modelRow = convertRowIndexToModel(row);
        super.setValueAt(aValue, modelRow, column);
    }

    /**
     * If the quick-filter is applied the number of rows do not
     * match the number of rows in the model.
     */
    public int getRowCount() {
        if ((quickFilterColumn != -1) && (quickFilterObject != null)) {
            if (filteredRowCount == -1) {
                computeFilteredRowCount();
            }
            return filteredRowCount;
        }
        return super.getRowCount();
    }

    /**
     * Makes the table disply only the rows that match the given "quick-filter".
     * Filtering is done according to values from column with index column and
     * according to filterObject. There are 2 possibilities for the filterObject
     * paramterer
     * <OL> <LI> filterObject implements <strong>QuickFilter</strong> 
     *           interface: the method <code>accept(Object)</code> 
     *           of the QuickFilter is called to determine whether the 
     *           row will be shown</LI>
     *      <LI> if filterObject does not implement the interface the value
     *           is compared using method equals(Object) with the filterObject.
     *           If they are equal the row will be shown.
     * </OL>
     */
    public void setQuickFilter(int column, Object filterObject) {
        quickFilterColumn = column;
        quickFilterObject = filterObject;
        sortingPermutation = null;
        super.tableChanged(new TableModelEvent(getModel()));
    }
    
    /**
     * Makes the table show all the rows, resetting the filter state
     * (to no filter).
     */
    public void unsetQuickFilter() {
        quickFilterObject = null;
        quickFilterColumn = -1;
        filteredRowCount = -1;
        sortingPermutation = null;
        super.tableChanged(new TableModelEvent(getModel()));
    }
    
    /**
     * Overriden to update the header listeners and also to adjust the
     * preferred width of the collumns.
     */
    public void setModel(TableModel dataModel) {
        TableModel oldModel = getModel();
        if (oldModel != null) {
            oldModel.removeTableModelListener(tableModelListener);
        }
        
        super.setModel(dataModel);
        
        // force recomputation
        filteredRowCount = -1;
        sortingPermutation = null;
        quickFilterColumn = -1;
        quickFilterObject = null;
        
        dataModel.addTableModelListener(tableModelListener);
        
        updateMouseListener();
        if (defaultRenderersByColumnClass != null) {
            updatePreferredWidths();
        }
    }
    
    /**
     * Overriden to set the initial column widths.
     */
    protected void initializeLocalVars() {
        super.initializeLocalVars();
        updatePreferredWidths();
    }
    
    /**
     * Overriden to implement CTRL-+ for resizing of all columns.
     */
    protected boolean processKeyBinding(KeyStroke ks, KeyEvent e,
					int condition, boolean pressed) {
	boolean retValue = super.processKeyBinding(ks, e, condition, pressed);
        
        // This is here because the standard way using input map and action map
        // did not work since the event was "eaten" by the code in JTable that
        // forwards it to the CellEditor (the code resides in the
        // super.processKeyBinding method).
        if (pressed) {
            if (e.getKeyChar() == '+' && ( (e.getModifiers() & InputEvent.CTRL_MASK) == InputEvent.CTRL_MASK)) {
                updatePreferredWidths();
            }
        }
        return retValue;
    }

    /**
     * When the user clicks the header this method returns either
     * the column that should be resized or null.
     */
    private TableColumn getResizingColumn(Point p) {
        JTableHeader header = getTableHeader();
        if (header == null) {
            return null;
        }
        int column = header.columnAtPoint(p);
        if (column == -1) {
            return null;
        }
        Rectangle r = header.getHeaderRect(column);
        r.grow(-3, 0);
        if (r.contains(p)) {
            return null;
        }
        int midPoint = r.x + r.width/2;
        int columnIndex;
        if( header.getComponentOrientation().isLeftToRight() ) {
            columnIndex = (p.x < midPoint) ? column - 1 : column;
        } else {
            columnIndex = (p.x < midPoint) ? column : column - 1;
        }
        if (columnIndex == -1) {
            return null;
        }
        return header.getColumnModel().getColumn(columnIndex);
    }

    /**
     * Adds mouse listener to the header for sorting and auto-sizing
     * of the columns.
     */
    private void updateMouseListener() {
        JTableHeader jth = getTableHeader();
        if (jth != null) {
            jth.removeMouseListener(headerMouseListener); // not to add it twice
            jth.addMouseListener(headerMouseListener);
        }
    }
    
    /**
     *
     */
    private void computeFilteredRowCount() {
        if ((quickFilterColumn == -1) || (quickFilterObject == null) ) {
            filteredRowCount = -1;
            return;
        }
        if (sortingPermutation != null) {
            filteredRowCount = sortingPermutation.length;
            return;
        }
        sortAndFilter();
        if (sortingPermutation != null) {
            filteredRowCount = sortingPermutation.length;
        }
    }
    
    /**
     * Helper method converting the row index according to the active sorting
     * columns.
     */
    int convertRowIndexToModel(int row) {
        if (sortingPermutation == null) {
            sortAndFilter();
        }
        if ((sortingPermutation != null) && (row >= 0) && (row < sortingPermutation.length)){
            return sortingPermutation[row];
        }
        return row;
    }
    
    /**
     * Sorts the rows of the table.
     */
    private void sortAndFilter() {
        TableColumnModel tcm = getColumnModel();
        if (tcm instanceof ETableColumnModel) {
            ETableColumnModel etcm = (ETableColumnModel) tcm;
            Comparator c = etcm.getComparator();
            if (c != null) {
                TableModel model = getModel();
                int noRows = model.getRowCount();
                List rows = new ArrayList();
                for (int i = 0; i < noRows; i++) {
                    if (acceptByQuickFilter(model, i)) {
                        rows.add(new RowMapping(i, model));
                    }
                }
                Collections.sort(rows, c);
                int [] res = new int[rows.size()];
                for (int i = 0; i < res.length; i++) {
                    RowMapping rm = (RowMapping) rows.get(i);
                    res[i] = rm.getModelRowIndex();
                }
                sortingPermutation = res;
            }
        }
    }
    
    /**
     *
     */
    private boolean acceptByQuickFilter(TableModel model, int row) {
        if ((quickFilterColumn == -1) || (quickFilterObject == null) ) {
            return true;
        }
        Object value = model.getValueAt(row, quickFilterColumn);
        if (quickFilterObject instanceof QuickFilter) {
            QuickFilter filter = (QuickFilter) quickFilterObject;
            return filter.accept(value);
        }
        if (value == null) {
            return false;
        }
        // fallback test for equality with the filter object
        return value.equals(quickFilterObject);
    }
    
    /**
     * Compute the preferredVidths of all columns.
     */
    void updatePreferredWidths() {
        Enumeration en = getColumnModel().getColumns();
        while (en.hasMoreElements()) {
            Object obj = en.nextElement();
            if (obj instanceof ETableColumn) {
                ETableColumn etc = (ETableColumn) obj;
                etc.updatePreferredWidth(this, false);
            }
        }
    }
  
    /**
     * Method allowing to read stored values.
     * The stored values should be only those that the user has customized,
     * it does not make sense to store the values that were set using 
     * the initialization code because the initialization code can be run
     * in the same way after restart.
     */
    public void readSettings(Properties p, String propertyPrefix) {
        ETableColumnModel etcm = new ETableColumnModel();
        etcm.readSettings(p, propertyPrefix);
        setColumnModel(etcm);
    }

    /**
     * Method allowing to store customization values.
     * The stored values should be only those that the user has customized,
     * it does not make sense to store the values that were set using 
     * the initialization code because the initialization code can be run
     * in the same way after restart.
     */
    public void writeSettings(Properties p, String propertyPrefix) {
        TableColumnModel tcm = getColumnModel();
        if (tcm instanceof ETableColumnModel) {
            ETableColumnModel etcm = (ETableColumnModel) tcm;
            etcm.writeSettings(p, propertyPrefix);
        }
    }

    /**
     * Item to the collection when doing the sorting of table rows.
     */
    static class RowMapping {
        private int originalIndex;
        private TableModel model;
        public RowMapping(int index, TableModel model) {
            originalIndex = index;
            this.model = model;
        }
        public int getModelRowIndex() {
            return originalIndex;
        }
        public Object getModelObject(int column) {
            return model.getValueAt(originalIndex, column);
        }
    }
    
    /**
     * Comparator used for sorting the rows according to value in
     * a given column. Operates on the RowMapping objects.
     */
    static class RowComparator implements Comparator {
        private int column;
        public RowComparator(int column) {
            this.column = column;
        }
        public int compare(Object o1, Object o2) {
            RowMapping rm1 = (RowMapping)o1;
            RowMapping rm2 = (RowMapping)o2;
            Object obj1 = rm1.getModelObject(column);
            Object obj2 = rm2.getModelObject(column);
            if (obj1 == null && obj2 == null) {
                return 0;
            }
            if (obj1 == null) {
                return -1;
            }
            if (obj2 == null) {
                return 1;
            }
            if ((obj1 instanceof Comparable) && (obj2 instanceof Comparable)){
                Comparable c1 = (Comparable) obj1;
                Comparable c2 = (Comparable) obj2;
                return c1.compareTo(c2);
            }
            return obj1.toString().compareTo(obj2.toString());
        }
    }

    /** 
     * Comparator for RowMapping objects that sorts according
     * to the original indices of the rows in the model.
     */
    static class OriginalRowComparator implements Comparator {
        public OriginalRowComparator() {
        }
        public int compare(Object o1, Object o2) {
            RowMapping rm1 = (RowMapping)o1;
            RowMapping rm2 = (RowMapping)o2;
            int i1 = rm1.getModelRowIndex();
            int i2 = rm2.getModelRowIndex();
            return (i1 < i2 ? -1 : (i1 == i2 ? 0 : 1));
        }
    }
    
    /**
     * Mouse listener attached to the JTableHeader of this table. Single
     * click on the table header should trigger sorting on that column.
     * Double click on the column divider automatically resizes the column.
     */
    private class HeaderMouseListener extends MouseAdapter {
        public void mouseClicked(MouseEvent me) {
            if (me.getButton() == MouseEvent.BUTTON3) {
                // right click will open the column visibility dialog
                ColumnSelectionPanel panel = new ColumnSelectionPanel(getColumnModel());
                int res = JOptionPane.showConfirmDialog(ETable.this, panel, "Select visible columns", JOptionPane.OK_CANCEL_OPTION);
                if (res == JOptionPane.OK_OPTION) {
                    panel.changeColumnVisibility();
                }
                return;
            }
            TableColumn resColumn = getResizingColumn(me.getPoint());
            if ((resColumn == null) && (me.getClickCount() == 1)) {
                // ok, do the sorting
                int column = columnAtPoint(me.getPoint());
                TableColumnModel tcm = getColumnModel();
                if (tcm instanceof ETableColumnModel) {
                    ETableColumnModel etcm = (ETableColumnModel)tcm;
                    TableColumn tc = tcm.getColumn(column);
                    if (tc instanceof ETableColumn) {
                        ETableColumn etc = (ETableColumn)tc;
                        boolean clear = ((me.getModifiers() & InputEvent.SHIFT_MASK) != InputEvent.SHIFT_MASK);
                        etcm.toggleSortedColumn(etc, clear);
                        sortingPermutation = null;
                        ETable.super.tableChanged(new TableModelEvent(getModel(), 0, getModel().getRowCount()));
                    }
                }
            }
            if ((resColumn != null) && (me.getClickCount() == 2)) {
                // update the column width
                if (resColumn instanceof ETableColumn) {
                    ETableColumn etc = (ETableColumn)resColumn;
                    etc.updatePreferredWidth(ETable.this, true);
                }
            }
        }
    }
}
