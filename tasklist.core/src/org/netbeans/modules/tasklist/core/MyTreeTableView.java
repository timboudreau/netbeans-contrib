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

package org.netbeans.modules.tasklist.core;

import java.awt.Dimension;
import java.awt.event.KeyEvent;
import javax.swing.*;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import org.openide.explorer.view.TreeTableView;

    // Workaround - is this no longer necessary?
    // No, I can fish the JTable out of the TreeTableView - it's
    // a JScrollPane, so its getViewport().getView() will be the
// JTable!
// todo comments
final class MyTreeTableView extends TreeTableView implements TreeTableIntf {

    private static final long serialVersionUID = 1;

    public MyTreeTableView() {

        JTable table = treeTable;
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        
        //table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        // No, I can use TreeTableView.setTableAutoResizeMode(int) for this
        
        // No white clipping lines on selected table rows: reduce separator
        // to 0. That means text may touch but HIE prefers this.
        table.setIntercellSpacing(new Dimension(0, table.getRowMargin()));
        double height = new JLabel("Z").getPreferredSize().getHeight();
        int intheight = (int) height;
        table.setRowHeight(intheight);

        // #45006 "cancelEdit" bound to ESC eliminates our ESC handling
        table.getInputMap(WHEN_FOCUSED).remove(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0));

            /* Issue 23993 was fixed which probably makes this unnecessary:
// Grid color: HIE's asked for (230,230,230) but that seems troublesome
// since we'd have to make a GUI for customizing it. Instead, go
// with Metal's secondary2, since for alternative UIs this will continue
// to look good (and it's customizable by the user). And secondary2
// is close to the request valued - it's (204,204,204).
table.setGridColor((java.awt.Color)javax.swing.UIManager.getDefaults().get("Label.background")); // NOI18N
             */
    }
    
    public JTree getTree() {
        return tree;
    }
    
    public JTable getTable() {
        return treeTable;
    }
    
    public TableModel getModel() {
        return treeTable.getModel();
    }
    
    public TableColumnModel getHeaderModel() {
        return treeTable.getTableHeader().getColumnModel();
    }
}

