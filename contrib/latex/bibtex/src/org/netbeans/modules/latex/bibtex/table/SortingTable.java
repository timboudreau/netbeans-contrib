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
 * The Original Software is the LaTeX module.
 * The Initial Developer of the Original Software is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002-2004.
 * All Rights Reserved.
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.bibtex.table;
import java.awt.Component;
import java.awt.Font;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

/**
 *
 * @author Jan Lahoda
 */
public class SortingTable extends JTable {

    private boolean initialized;

    /** Creates a new instance of SortingTable */
    public SortingTable() {
        super();
        getTableHeader().setDefaultRenderer(new SortingHeaderRenderer(getTableHeader().getDefaultRenderer()));
        initialized = true;
        setModel(getModel());
    }
    
    public SortingTable(TableModel model) {
        this();
        setModel(model);
    }
    
    public void setModel(TableModel model) {
        if (!initialized) {
            super.setModel(model);
            return ;
        }
        
        SortingTableModel stm = new SortingTableModel(model);
        if (super.getModel() instanceof SortingTableModel) {
            ((SortingTableModel) super.getModel()).removeMouseListenerFromTable(this);
        }
        super.setModel(stm);
        stm.addMouseListenerToTable(this);
    }
    
    public int[] getSelectedRows() {
        int[] result = super.getSelectedRows();
        int[] rows   = ((SortingTableModel) getModel()).getRows();
        
        if (rows != null) {
            for (int cntr = 0; cntr < result.length; cntr++) {
                result[cntr] = rows[result[cntr]];
            }
        }
        
        return result;
    }
    
    public int getSelectedRow() {
        int result = super.getSelectedRow();
        
        if (result == (-1))
            return result;
        
        int[] rows   = ((SortingTableModel) getModel()).getRows();
        
        if (rows != null)
            return rows[result];
        else
            return result;
    }
    
    public class SortingHeaderRenderer extends DefaultTableCellRenderer {
        
        private static final java.lang.String SORT_ASC_ICON = "org/openide/resources/columnsSortedAsc.gif";
        private static final java.lang.String SORT_DESC_ICON = "org/openide/resources/columnsSortedDesc.gif";
        private javax.swing.table.TableCellRenderer defaultRenderer;
        private SortingHeaderRenderer(TableCellRenderer defaultRenderer) {
            this.defaultRenderer = defaultRenderer;
        }
        /**
         * Overrides superclass method.
         */
        public Component getTableCellRendererComponent(JTable table, java.lang.Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            TableModel tm = table.getModel();
            
            if (!(tm instanceof SortingTableModel))
                return defaultRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            SortingTableModel stm = (SortingTableModel) tm;
            
            Component comp = defaultRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            if ( comp instanceof JLabel ) {
                if ( stm.getSortingColumn() == column ) {
                    ((JLabel)comp).setIcon( getProperIcon( stm.isAscendingSort() ) );
                    ((JLabel)comp).setHorizontalTextPosition( SwingConstants.LEFT );
                    comp.setFont( comp.getFont().deriveFont( Font.BOLD ) );
                }
                else
                    ((JLabel)comp).setIcon( null );
            }
            return comp;
        }
        private ImageIcon getProperIcon(boolean ascending) {
            
            if ( ascending )
                return new ImageIcon( org.openide.util.Utilities.loadImage( SORT_ASC_ICON ) );
            else
                return new ImageIcon( org.openide.util.Utilities.loadImage( SORT_DESC_ICON ) );
        }}
    
}

