/*
 * MyTreeTableView.java
 *
 * Created on 16. Dezember 2003, 12:08
 */

package org.netbeans.modules.tasklist.core;

import java.awt.Dimension;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import org.openide.explorer.view.TreeTableView;

    // Workaround - is this no longer necessary?
    // No, I can fish the JTable out of the TreeTableView - it's
    // a JScrollPane, so its getViewport().getView() will be the
// JTable!
// todo comments
public class MyTreeTableView extends TreeTableView implements TreeTableIntf {
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
        // XXX it's private: return tableModel;
        return (TableModel) treeTable.getModel();
    }
    
    public TableColumnModel getHeaderModel() {
        return treeTable.getTableHeader().getColumnModel();
    }
}

