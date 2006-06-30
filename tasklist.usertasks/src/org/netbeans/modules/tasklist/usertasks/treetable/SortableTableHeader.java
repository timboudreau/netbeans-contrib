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

package org.netbeans.modules.tasklist.usertasks.treetable;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTable;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import org.netbeans.modules.tasklist.core.table.SortingModel;

import org.netbeans.modules.tasklist.usertasks.util.UTUtils;

/**
 * A table header that can work together with SortingModel
 *
 * @author tl
 */
public class SortableTableHeader extends JTableHeader {
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
        UTUtils.LOGGER.fine("clicked"); // NOI18N
        int col = SortableTableHeader.this.columnAtPoint(e.getPoint());
        if (col == -1) 
            return;
        
        JTable t = SortableTableHeader.this.getTable();
        if (!(t instanceof TreeTable)) 
            return;
        
        UTUtils.LOGGER.fine("tt found"); // NOI18N
        SortingModel sm = ((TreeTable) t).getSortingModel();
        if (sm == null)
            return;
        
        UTUtils.LOGGER.fine("model ok"); // NOI18N
        int index = getColumnModel().getColumn(col).getModelIndex();
        if (sm.getColumnComparator(index) == null)
            return;
        UTUtils.LOGGER.fine("comparator ok"); // NOI18N
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
