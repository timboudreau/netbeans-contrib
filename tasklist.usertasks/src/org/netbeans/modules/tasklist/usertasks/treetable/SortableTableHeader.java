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

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTable;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import org.netbeans.modules.tasklist.core.TLUtils;

/**
 * A table header that can work together with SortingModel
 */
public class SortableTableHeader extends JTableHeader {
    private static Logger LOGGER = TLUtils.getLogger(SortableTableHeader.class);
    
    static {
        LOGGER.setLevel(Level.OFF);
    }
    
    private static final long serialVersionUID = 1;

    /**
     * Constructs a <code>SortableTableHeader</code> with a default 
     * <code>TableColumnModel</code>.
     *
     * @see #createDefaultColumnModel
     */
    public SortableTableHeader() {
	this(null);
    }

    /**
     * Constructs a <code>SortableTableHeader</code> which is initialized with
     * <code>cm</code> as the column model.  If <code>cm</code> is
     * <code>null</code> this method will initialize the table header
     * with a default <code>TableColumnModel</code>.
     *
     * @param cm	the column model for the table
     * @see #createDefaultColumnModel
     */
    public SortableTableHeader(TableColumnModel cm) {
	super(cm);
        addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                mouseClick(e);
            }
        });
        setDefaultRenderer(new SortingHeaderRenderer());
    }
    
    /**
     * Mouse click handler
     *
     * @param e an event
     */
    private void mouseClick(MouseEvent e) {
        LOGGER.fine("clicked");
        int col = SortableTableHeader.this.columnAtPoint(e.getPoint());
        if (col == -1) 
            return;
        
        JTable t = SortableTableHeader.this.getTable();
        if (!(t instanceof TreeTable)) 
            return;
        
        LOGGER.fine("tt found");
        SortingModel sm = ((TreeTable) t).getSortingModel();
        if (sm == null)
            return;
        
        LOGGER.fine("model ok");
        int index = getColumnModel().getColumn(col).getModelIndex();
        if (sm.getColumnComparator(index) == null)
            return;
        LOGGER.fine("comparator ok");
        int cur = sm.getSortedColumn();
        if (index == cur) {
            if (sm.isSortOrderDescending())
                sm.setSortOrderDescending(false);
            else
                sm.setSortedColumn(-1);
        } else {
            sm.setSortOrderDescending(true);
            sm.setSortedColumn(index);
        }
    }
}
