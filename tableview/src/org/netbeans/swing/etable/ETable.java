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
import java.awt.Container;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Vector;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
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

    // icon of column button
    private static final String DEFAULT_COLUMNS_ICON = "columns.gif"; // NOI18N
    
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

    // Search text field related variables:
    /** */
    private String maxPrefix;
    /** */
    int SEARCH_FIELD_PREFERRED_SIZE = 160;
    /** */
    int SEARCH_FIELD_SPACE = 3;
    /** */
    final private JTextField searchTextField = new SearchTextField();
    /** */
    final private int heightOfTextField = searchTextField.getPreferredSize().height;
            
    
    
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
            Color colorBorderAllEditable = UIManager.getColor("Table.borderAllEditable");
            Border border = null;
            if (colorBorderAllEditable != null) {
                border = BorderFactory.createLineBorder(colorBorderAllEditable);
            } else {
                border = BorderFactory.createLineBorder(Color.GRAY);
            }
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
        if (isFullyNonEditable()) {
            setupSearch();
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
            Color lineBorderColor = UIManager.getColor("Table.border");
            if (lineBorderColor == null) {
                lineBorderColor = Color.GRAY;
            }
            setBorder(BorderFactory.createLineBorder(lineBorderColor));
            Color c = UIManager.getColor("Table.noneditableGrid");
            if (c != null) {
                setGridColor(c);
            }
        } else {
            editing = DEFAULT;
            setBorder( null );
            if(!getShowHorizontalLines())
                setShowHorizontalLines(true);
            Color defaultGridColor = UIManager.getColor("Table.defaultGrid");
            if (defaultGridColor != null) {
                setGridColor(defaultGridColor);
            }
        }
        if (isFullyNonEditable()) {
            setupSearch();
        }
    }
    
    /**
     * Returns true if <code>ETable</code> is fully editable.
     *
     * @return  true if the the table is fully editable.
     * @see #setFullyEditable
     */
    public boolean isFullyEditable() {
        return editing == FULLY_EDITABLE;
    }
    
    /**
     * Returns true if <code>ETable</code> is fully non-editable.
     *
     * @return  true if the the table is fully non-editable.
     * @see #setFullyNonEditable
     */
    public boolean isFullyNonEditable() {
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
        Color c = null;
        if (row%2 == 0) { //Background 2
            if(isSelected) {
                c = UIManager.getColor("Table.selectionBackground2");
            } else {
                c = UIManager.getColor("Table.background2");
            }
        } else { // Background 1
            if(isSelected) {
                c = UIManager.getColor("Table.selectionBackground1");
            } else {
                c = UIManager.getColor("Table.background1");
            }
        }
        if (c != null) {
            renderer.setBackground(c);
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
     * Overriden to install special button into the upper right hand corner.
     */
    protected void configureEnclosingScrollPane() {
        super.configureEnclosingScrollPane();
        
        if (isFullyNonEditable()) {
            setupSearch();
        }
        
        Container p = getParent();
        if (p instanceof JViewport) {
            Container gp = p.getParent();
            if (gp instanceof JScrollPane) {
                JScrollPane scrollPane = (JScrollPane)gp;
                // Make certain we are the viewPort's view and not, for
                // example, the rowHeaderView of the scrollPane -
                // an implementor of fixed columns might do this.
                JViewport viewport = scrollPane.getViewport();
                if (viewport == null || viewport.getView() != this) {
                    return;
                }
                Icon ii = UIManager.getIcon("Table.columnSelection");
                if (ii == null) {
                    ii = new ImageIcon(ETable.class.getResource(DEFAULT_COLUMNS_ICON));
                }
                final JButton b = new JButton(ii);
                b.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent evt) {
                        showColumnSelectionPopup(b);
                    }
                });
                b.addMouseListener(new MouseAdapter() {
                    public void mouseClicked(MouseEvent me) {
                        if (me.getButton() == MouseEvent.BUTTON3) {
                            showColumnSelectionDialog();
                        }
                    }
                });
                scrollPane.setCorner(JScrollPane.UPPER_RIGHT_CORNER, b);
            }
        }
    }
    
    /**
     * Shows the popup allowing to show/hide columns.
     */
    private void showColumnSelectionPopup(Component c) {
        JPopupMenu popup = new JPopupMenu();
        TableColumnModel columnModel = getColumnModel();
        if (! (columnModel instanceof ETableColumnModel)) {
            return;
        }
        final ETableColumnModel etcm = (ETableColumnModel)columnModel;
        List columns = Collections.list(etcm.getColumns());
        columns.addAll(etcm.hiddenColumns);
        Collections.sort(columns);
        for (Iterator it = columns.iterator(); it.hasNext(); ) {
            final ETableColumn etc = (ETableColumn)it.next();
            final JCheckBoxMenuItem checkBox = new JCheckBoxMenuItem();
            checkBox.setText(etc.getHeaderValue().toString());
            checkBox.setSelected(! etcm.isColumnHidden(etc));
            checkBox.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    etcm.setColumnHidden(etc,! checkBox.isSelected());
                }
            });

            popup.add(checkBox);
        }
        popup.show(c, 8, 8);
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
    
    /** searchTextField manages focus because it handles VK_TAB key */
    private class SearchTextField extends JTextField {
        public boolean isManagingFocus() {
            return true;
        }
        
        public void processKeyEvent(KeyEvent ke) {
            //override the default handling so that
            //the parent will never receive the escape key and
            //close a modal dialog
            if (ke.getKeyCode() == ke.VK_ESCAPE) {
                removeSearchField();
                // bugfix #32909, reqest focus when search field is removed
                SwingUtilities.invokeLater(new Runnable() {
                    //additional bugfix - do focus change later or removing
                    //the component while it's focused will cause focus to
                    //get transferred to the next component in the
                    //parent focusTraversalPolicy *after* our request
                    //focus completes, so focus goes into a black hole - Tim
                    public void run() {
                        ETable.this.requestFocus();
                    }
                });
            } else {
                super.processKeyEvent(ke);
            }
        }
    }
    
    private List doSearch(String prefix) {
        List results = new ArrayList();
        
        // do search forward the selected index
        int rows[] = getSelectedRows();
        int startIndex = (rows == null || rows.length == 0) ? 0 : rows[0];
        
        int size = getRowCount();
        if ( (size == 0) || (getColumnCount() == 0)) {
            // Empty table; cannot match anything.
            return results;
        }
        
        while (startIndex < size) {
            Object val = getValueAt(startIndex, 0);
            String s = null;
            if (val != null) {
                s = val.toString();    
            }   
            if ((s != null) && (s.toUpperCase().startsWith(prefix.toUpperCase()))) {
                results.add(new Integer(startIndex));
            }
            
            // initialize prefix
            if (maxPrefix == null) {
                maxPrefix = s;
            }

            maxPrefix = findMaxPrefix(maxPrefix, s);
            
            startIndex++;
        }
        
        return results;
    }
    
    private static String findMaxPrefix(String str1, String str2) {
        int i = 0;
        while (str1.regionMatches(true, 0, str2, 0, i)) {
            i++;
        }
        i--;
        if (i >= 0) {
            return str1.substring(0, i);    
        }
        return null;
    }
    
    private void setupSearch() {
        // Remove the default key listeners
        KeyListener keyListeners[] = (KeyListener[]) (getListeners(KeyListener.class));
        for (int i = 0; i < keyListeners.length; i++) {
            removeKeyListener(keyListeners[i]);
        }
        // Add new key listeners
        addKeyListener(new KeyAdapter() {
            private boolean armed = false;
            public void keyPressed(KeyEvent e) {
                int modifiers = e.getModifiers();
                int keyCode = e.getKeyCode();
                if ((modifiers > 0 && modifiers != KeyEvent.SHIFT_MASK) || e.isActionKey())
                    return ;
                char c = e.getKeyChar();
                if (!Character.isISOControl(c) && keyCode != KeyEvent.VK_SHIFT && keyCode != KeyEvent.VK_ESCAPE) {
                    armed = true;
                    e.consume();
                }
            }
            public void keyTyped(KeyEvent e) {
                if (armed) {
                    final KeyStroke stroke = KeyStroke.getKeyStrokeForEvent(e);
                    searchTextField.setText(String.valueOf(stroke.getKeyChar()));
                    
                    displaySearchField();
                    e.consume();
                    armed = false;
                }
            }
        });
        // Create a the "multi-event" listener for the text field. Instead of
        // adding separate instances of each needed listener, we're using a
        // class which implements them all. This approach is used in order
        // to avoid the creation of 4 instances which takes some time
        SearchFieldListener searchFieldListener = new SearchFieldListener();
        searchTextField.addKeyListener(searchFieldListener);
        searchTextField.addFocusListener(searchFieldListener);
        searchTextField.getDocument().addDocumentListener(searchFieldListener);
    }
    
    private class SearchFieldListener extends KeyAdapter
            implements DocumentListener, FocusListener {
        
        /** The last search results */
        private List results = new ArrayList();
        /** The last selected index from the search results. */
        private int currentSelectionIndex;
        
        /**
         * Default constructor.
         */
        SearchFieldListener() {
        }
        
        public void changedUpdate(DocumentEvent e) {
            searchForRow();
        }
        
        public void insertUpdate(DocumentEvent e) {
            searchForRow();
        }
        
        public void removeUpdate(DocumentEvent e) {
            searchForRow();
        }
        
        public void keyPressed(KeyEvent e) {
            int keyCode = e.getKeyCode();
            if (keyCode == KeyEvent.VK_ESCAPE) {
                removeSearchField();
                ETable.this.requestFocus();
            } else if (keyCode == KeyEvent.VK_UP) {
                currentSelectionIndex--;
                displaySearchResult();
                // Stop processing the event here. Otherwise it's dispatched
                // to the table too (which scrolls)
                e.consume();
            } else if (keyCode == KeyEvent.VK_DOWN) {
                currentSelectionIndex++;
                displaySearchResult();
                // Stop processing the event here. Otherwise it's dispatched
                // to the table too (which scrolls)
                e.consume();
            } else if (keyCode == KeyEvent.VK_TAB) {
                if (maxPrefix != null)
                    searchTextField.setText(maxPrefix);
                e.consume();
            } else if (keyCode == KeyEvent.VK_ENTER) {
                removeSearchField();
                
                // TODO: do something on hitting enter???
                e.consume();
                ETable.this.requestFocus();
            }
        }
        
        /** Searches for a row. */
        private void searchForRow() {
            currentSelectionIndex = 0;
            results.clear();
            maxPrefix = null;
            String text = searchTextField.getText().toUpperCase();
            if (text.length() > 0) {
                results = doSearch(text);
                displaySearchResult();
            }
        }
        
        private void displaySearchResult() {
            int sz = results.size();
            if (sz > 0) {
                currentSelectionIndex = ((Integer)results.get(0)).intValue();
                setRowSelectionInterval(currentSelectionIndex, currentSelectionIndex);
            } else {
                clearSelection();
            }
        }
        
        public void focusGained(FocusEvent e) {
            // Do nothing
        }
        
        public void focusLost(FocusEvent e) {
            removeSearchField();
        }
    }
    
    private void displaySearchField() {
        if (!searchTextField.isDisplayable()) {
            searchTextField.setFont(ETable.this.getFont());
            add(searchTextField);
            doLayout();
            searchTextField.repaint();
            // bugfix #28501, avoid the chars duplicated on jdk1.3
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    searchTextField.requestFocus();
                }
            });
        }
    }
    
    public void doLayout() {
        super.doLayout();
        Rectangle visibleRect = getVisibleRect();
        if (searchTextField.isDisplayable()) {
            int width = Math.min(
                    getPreferredSize().width - SEARCH_FIELD_SPACE * 2,
                    SEARCH_FIELD_PREFERRED_SIZE - SEARCH_FIELD_SPACE);
            
            searchTextField.setBounds(
                    Math.max(SEARCH_FIELD_SPACE,
                    visibleRect.x + visibleRect.width - width),
                    visibleRect.y + SEARCH_FIELD_SPACE,
                    Math.min(visibleRect.width, width) - SEARCH_FIELD_SPACE,
                    heightOfTextField);
        }
    }
    
    /**
     * Removes the search field from the table.
     */
    private void removeSearchField() {
        if (searchTextField.isDisplayable()) {
            remove(searchTextField);
            Rectangle r = searchTextField.getBounds();
            this.repaint(r);
        }
    }
    
    /**
     * Item to the collection when doing the sorting of table rows.
     */
    protected final static class RowMapping {
        // index (of the row) in the TableModel
        private int originalIndex;
        // table model of my table
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
     * Shows dialog allowing to show/hide columns.
     */
    private void showColumnSelectionDialog() {
        // right click will open the column visibility dialog
        ColumnSelectionPanel panel = new ColumnSelectionPanel(getColumnModel());
        int res = JOptionPane.showConfirmDialog(ETable.this, panel, "Select visible columns", JOptionPane.OK_CANCEL_OPTION);
        if (res == JOptionPane.OK_OPTION) {
            panel.changeColumnVisibility();
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
                showColumnSelectionDialog();
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
