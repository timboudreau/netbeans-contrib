/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.tasklist.usertasks.treetable;

import java.awt.Component;
import java.awt.Font;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

/**
 * Cell renderer for sorting column header.
 * Originally copied from org.openide.explorer.view.TreeTableView
 *
 * @author jrojcek
 * @author Tim Lebedkov
 */
public class SortingHeaderRenderer extends DefaultTableCellRenderer {

    private static final long serialVersionUID = 1;

    private static ImageIcon SORT_DESC_ICON =
        new ImageIcon(org.openide.util.Utilities.loadImage(
        "org/netbeans/modules/tasklist/compiler/treetable/columnsSortedDesc.gif")); // NOI18N
    private static ImageIcon SORT_ASC_ICON = 
        new ImageIcon(org.openide.util.Utilities.loadImage(
        "org/netbeans/modules/tasklist/compiler/treetable/columnsSortedAsc.gif")); // NOI18N
    
    /**
     * Constructor
     */
    public SortingHeaderRenderer() {
	setHorizontalAlignment(JLabel.CENTER);
        setHorizontalTextPosition(SwingConstants.LEFT);
    }
    
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        if (table != null) {
            JTableHeader header = table.getTableHeader();
            if (header != null) {
                setForeground(header.getForeground());
                setBackground(header.getBackground());
                setFont(header.getFont());
            }
            TableColumnModel tcm = header.getColumnModel();
            int modelIndex = tcm.getColumn(column).getModelIndex();
            TreeTableModel m = ((TreeTable) table).getTreeTableModel();
            if (m instanceof SortingModel) {
                SortingModel tableModel = (SortingModel) m;
                if (tableModel.getSortedColumn() == modelIndex) {
                    this.setIcon(
                        tableModel.isSortOrderDescending() ? 
                        SORT_DESC_ICON : SORT_ASC_ICON);
                    this.setFont(this.getFont().deriveFont(Font.BOLD));
                } else {
                    this.setIcon(null);
                }
            }
        }

        setText((value == null) ? "" : value.toString());
        setBorder(UIManager.getBorder("TableHeader.cellBorder"));
        
        return this;
    }
}


