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
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.util.Comparator;
import java.util.Properties;
import javax.swing.Icon;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.UIResource;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

/**
 * Special type of TableColumn object used by ETable. 
 * @author David Strupl
 */
public class ETableColumn extends TableColumn implements Comparable {
    
    /** Used as a key or part of a key by the persistence mechanism. */
    private static final String PROP_PREFIX = "ETableColumn:";
    
    /** Used as a key or part of a key by the persistence mechanism. */
    private static final String PROP_WIDTH = "Width";
    
    /** Used as a key or part of a key by the persistence mechanism. */
    private static final String PROP_PREFERRED_WIDTH = "PreferredWidth";
    
    /** Used as a key or part of a key by the persistence mechanism. */
    private static final String PROP_SORT_RANK = "SortRank";
    
    /** Used as a key or part of a key by the persistence mechanism. */
    private static final String PROP_COMPARATOR = "Comparator";
    
    /** Used as a key or part of a key by the persistence mechanism. */
    private static final String PROP_HEADER_VALUE = "HeaderValue";
    
    /** Used as a key or part of a key by the persistence mechanism. */
    private static final String PROP_MODEL_INDEX = "ModelIndex";
    
    /** Used as a key or part of a key by the persistence mechanism. */
    private static final String PROP_ASCENDING = "Ascending";
    
    /** */
    private int sortRank = 0;
    /** */
    private Comparator comparator;
    /** */
    private boolean ascending = true;
    /** */
    private boolean headerRendererSetExternally = false;
    
    /** Header renderer created by createDefaultHeaderRenderer. */
    private TableCellRenderer headerRenderer;
    
    /** Creates a new instance of ETableColumn */
    public ETableColumn() {
        super();
    }
    
    /** Creates a new instance of ETableColumn */
    public ETableColumn(int modelIndex) {
        super(modelIndex);
    }
    
    /** Creates a new instance of ETableColumn */
    public ETableColumn(int modelIndex, int width) {
        super(modelIndex, width);
    }
    
    /** Creates a new instance of ETableColumn */
    public ETableColumn(int modelIndex, int width, TableCellRenderer cellRenderer, TableCellEditor cellEditor) {
        super(modelIndex, width, cellRenderer, cellEditor);
    }
    
    /**
     * This method marks this column as sorted. Value 0 of the parameter rank
     * means that this column is not sorted.
     * @param int rank value 1 means that this is the most important sorted
     *        column, number 2 means second etc.
     * @param comparator operates over ETable.RowMapping objects
     */
    void setSorted(int rank, Comparator comparator) {
        ascending = true;
        sortRank = rank;
        this.comparator = comparator;
    }
    
    /**
     * Returns true if the table is sorted using this column.
     */
    public boolean isSorted() {
        return comparator != null;
    }
    
    /**
     * Rank value 1 means that this is the most important column
     * (with respect to the table sort), value 2 means second etc.
     */
    void setSortRank(int newRank) {
        sortRank = newRank;
    }
    
    /**
     * Rank value 1 means that this is the most important column
     * (with respect to the table sort), value 2 means second etc.
     * To ask for the value of rank makes sense only when isSorted() returns
     * true. If isSorted() returns false this method should return 0.
     */
    public int getSortRank() {
        return sortRank;
    }
    
    /**
     * Returns the comparator used for sorting. The returned comparaotor
     * operates over ETable.RowMapping objects.
     */
    Comparator getComparator() {
        return comparator;
    }
    
    /**
     * Checks whether the sort order is ascending (true means ascending,
     * false means descending).
     */
    public boolean isAscending() {
        return ascending;
    }
    
    /**
     * Sets the sort order. Please note: the column has to be already
     * sorted when calling this method otherwise IllegalStateException
     * is thrown.
     */
    void setAscending(boolean ascending) {
        if (this.ascending == ascending) {
            return;
        }
        if (comparator == null) {
            throw new IllegalStateException("The column must be sorted when changing the sort order."); // NOI18N
        }
        this.ascending = ascending;
        if (comparator instanceof FlippingComparator){
            comparator = ((FlippingComparator)comparator).getOriginalComparator();
        } else {
            comparator = new FlippingComparator(comparator);
        }
    }
    
    /**
     * Allows to set the header renderer. If this method is not called
     * we use our special renderer created by method 
     * createDefaultHeaderRenderer().
     */
    public void setHeaderRenderer(TableCellRenderer tcr) {
        headerRendererSetExternally = true;
        super.setHeaderRenderer(tcr);
    }
    
    /**
     * Use a special renderer (result of calling createDefaultHeaderRenderer)
     * if it was not set by setHeaderRenderer.
     */
    public TableCellRenderer getHeaderRenderer() {
        if (headerRendererSetExternally) {
            return super.getHeaderRenderer();
        }
	return createDefaultHeaderRenderer();
    }

    /**
     * Computes preferred width of the column by checking all the
     * data in the given column. If the resize parameter is true
     * it also directly resizes the column to the computed size (besides
     * setting the preferred size).
     */
    void updatePreferredWidth(JTable table, boolean resize) {
        TableModel dataModel = table.getModel();
        int rows = dataModel.getRowCount();
        if (rows == 0) {
            return;
        }
        int sum = 0;
        int max = 15;
        for (int i = 0; i < rows; i++) {
            Object data = dataModel.getValueAt(i, modelIndex);
            int estimate = estimatedWidth(data, table);
            sum += estimate;
            if (estimate > max) {
                max = estimate;
            }
        }
        max += 5;
        setPreferredWidth(max);
        if (resize) {
            resize(max, table);
        }
    }

    /**
     * Forces the table to resize given column.
     */
    private void resize(int newWidth, JTable table) {
        int oldWidth = getWidth();
        JTableHeader header = table.getTableHeader();
        if (header == null) {
            return;
        }
        header.setResizingColumn(this);
        final int oldMin = getMinWidth();
        final int oldMax = getMaxWidth();
        setMinWidth(newWidth);
        setMaxWidth(newWidth);
        setWidth(newWidth);
        // The trick is to restore the original values
        // after the table has be layouted. During layout this column
        // has fixed width (by setting min==max==preffered)
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                setMinWidth(oldMin);
                setMaxWidth(oldMax);
            }
        });
        Container container;
        if ((header.getParent() == null) ||
                ((container = header.getParent().getParent()) == null) ||
                !(container instanceof JScrollPane)) {
            header.setResizingColumn(null);
            return;
        }
        
        if (!container.getComponentOrientation().isLeftToRight() &&
                ! header.getComponentOrientation().isLeftToRight()) {
            if (table != null) {
                JViewport viewport = ((JScrollPane)container).getViewport();
                int viewportWidth = viewport.getWidth();
                int diff = newWidth - oldWidth;
                int newHeaderWidth = table.getWidth() + diff;
                
                /* Resize a table */
                Dimension tableSize = table.getSize();
                tableSize.width += diff;
                table.setSize(tableSize);
                
                /* If this table is in AUTO_RESIZE_OFF mode and
                 * has a horizontal scrollbar, we need to update
                 * a view's position.
                 */
                if ((newHeaderWidth >= viewportWidth) &&
                        (table.getAutoResizeMode() == JTable.AUTO_RESIZE_OFF)) {
                    Point p = viewport.getViewPosition();
                    p.x = Math.max(0, Math.min(newHeaderWidth - viewportWidth, p.x + diff));
                    viewport.setViewPosition(p);
                }
            }
        }
        header.setResizingColumn(null);
    }
    
    /**
     * @returns width in pixels of the graphical representation of the data.
     */
    private int estimatedWidth(Object dataObject, JTable table) {
        TableCellRenderer cr = getCellRenderer();
        if (cr == null) {
            Class c = table.getModel().getColumnClass(modelIndex);
            cr = table.getDefaultRenderer(c);
        }
        Component c = cr.getTableCellRendererComponent(table, dataObject, false,
                false, 0, table.getColumnModel().getColumnIndex(getIdentifier()));
        return c.getPreferredSize().width;
    }
    
    /**
     * Method allowing to read stored values.
     * The stored values should be only those that the user has customized,
     * it does not make sense to store the values that were set using 
     * the initialization code because the initialization code can be run
     * in the same way after restart.
     */
    public void readSettings(Properties p, int index, String propertyPrefix) {
        String myPrefix = propertyPrefix + PROP_PREFIX + Integer.toString(index) + ":";
        String s0 = p.getProperty(myPrefix + PROP_MODEL_INDEX);
        if (s0 != null) {
            modelIndex = Integer.parseInt(s0);
        }
        String s1 = p.getProperty(myPrefix + PROP_WIDTH);
        if (s1 != null) {
            width = Integer.parseInt(s1);
        }
        String s2 = p.getProperty(myPrefix + PROP_PREFERRED_WIDTH);
        if (s2 != null) {
            setPreferredWidth(Integer.parseInt(s2));
        }
        String s3 = p.getProperty(myPrefix + PROP_SORT_RANK);
        if (s3 != null) {
            sortRank = Integer.parseInt(s3);
            if (sortRank > 0) {
                comparator = new ETable.RowComparator(modelIndex);
            }
        }
        headerValue = p.getProperty(myPrefix + PROP_HEADER_VALUE);
        ascending = true;
        String s4 = p.getProperty(myPrefix + PROP_ASCENDING);
        if ("false".equals(s4)) {
            setAscending(false);
        }
    }

    /**
     * Method allowing to store customization values.
     * The stored values should be only those that the user has customized,
     * it does not make sense to store the values that were set using 
     * the initialization code because the initialization code can be run
     * in the same way after restart.
     */
    public void writeSettings(Properties p, int index, String propertyPrefix) {
        String myPrefix = propertyPrefix + PROP_PREFIX + Integer.toString(index) + ":";
        p.setProperty(myPrefix + PROP_MODEL_INDEX, Integer.toString(modelIndex));
        p.setProperty(myPrefix + PROP_WIDTH, Integer.toString(width));
        p.setProperty(myPrefix + PROP_PREFERRED_WIDTH, Integer.toString(getPreferredWidth()));
        p.setProperty(myPrefix + PROP_SORT_RANK, Integer.toString(sortRank));
        p.setProperty(myPrefix + PROP_ASCENDING, ascending ? "true" : "false");
        if (headerValue != null) {
            p.setProperty(myPrefix + PROP_HEADER_VALUE, headerValue.toString());
        }
    }

    /*
     * Implementing interface Comparable.
     */
    public int compareTo(Object obj) {
        ETableColumn theOther = (ETableColumn)obj;
        if (modelIndex < theOther.modelIndex) {
            return -1;
        }
        if (modelIndex > theOther.modelIndex) {
            return 1;
        }
        return 0;
    }

    /**
     * Overriden to return our special header renderer.
     */
    protected TableCellRenderer createDefaultHeaderRenderer() {
        if (headerRenderer == null) {
            headerRenderer = new ETableCellRenderer();
        }
        return headerRenderer;
    }

    /**
     * An icon pointing up. It is used if the LAF does not supply
     * special icon.
     */
    private static class SortUpIcon implements Icon {
        
        public SortUpIcon() {
        }
        
        public int getIconWidth() {
            return 8;
        }
        
        public int getIconHeight() {
            return 8;
        }
        
        public void paintIcon(Component c, Graphics g, int x, int y) {
            g.setColor(Color.BLACK);
            g.drawLine(x    , y + 2, x + 8, y + 2);
            g.drawLine(x    , y + 2, x + 4, y + 6);
            g.drawLine(x + 8, y + 2, x + 4, y + 6);
        }
    }

    /**
     * An icon pointing down. It is used if the LAF does not supply
     * special icon.
     */
    private static class SortDownIcon implements Icon {
        
        public SortDownIcon() {
        }
        
        public int getIconWidth() {
            return 8;
        }
        
        public int getIconHeight() {
            return 8;
        }
        
        public void paintIcon(Component c, Graphics g, int x, int y) {
            g.setColor(Color.BLACK);
            g.drawLine(x    , y + 6, x + 8, y + 6);
            g.drawLine(x    , y + 6, x + 4, y + 2);
            g.drawLine(x + 8, y + 6, x + 4, y + 2);
        }
    }
    
    /**
     * Comparator reversing the order of the sorted objects (with
     * respect to the original comparator.
     */
    static class FlippingComparator implements Comparator {
        private Comparator origComparator;
        public FlippingComparator(Comparator orig) {
            origComparator = orig;
        }

        public int compare(Object o1, Object o2) {
            return -origComparator.compare(o1, o2);
        }
        
        public Comparator getOriginalComparator() {
            return origComparator;
        }
    }
    
    /**
     * Special renderer painting sorting icons and also special icon
     * for the QuickFilter columns.
     */
    private class ETableCellRenderer extends DefaultTableCellRenderer implements UIResource {
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            if (table != null) {
                JTableHeader header = table.getTableHeader();
                if (header != null) {
                    setForeground(header.getForeground());
                    setBackground(header.getBackground());
                    setFont(header.getFont());
                }
            }
            
            setBorder(UIManager.getBorder("TableHeader.cellBorder"));
            if (sortRank != 0) {
                setText((value == null) ? ""+sortRank : sortRank+" "+value.toString());
                if (ascending) {
                    Icon icon = UIManager.getIcon("ETableHeader.ascendingIcon");
                    if (icon == null) {
                        icon = new SortUpIcon();
                    }
                    setIcon(icon);
                } else {
                    Icon icon = UIManager.getIcon("ETableHeader.descendingIcon");
                    if (icon == null) {
                        icon = new SortDownIcon();
                    }
                    setIcon(icon);
                }
            } else { // sortRank == 0
                setText((value == null) ? "" : value.toString());
                setIcon(null);
            }
            return this;
        }
    }
}
