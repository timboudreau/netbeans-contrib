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

import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Action;
import javax.swing.JPopupMenu;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.tree.TreePath;
import org.netbeans.modules.tasklist.core.ColumnProperty;
import org.netbeans.modules.tasklist.core.TLUtils;
import org.netbeans.modules.tasklist.core.TreeTableIntf;
import org.openide.ErrorManager;
import org.openide.awt.MouseUtils;
import org.openide.explorer.ExplorerManager;
import org.openide.nodes.Node;
import org.openide.util.Utilities;

/**
 * TreeTable with support for Nodes
 */
public abstract class NodesTreeTable extends TreeTable {
    private static final Logger LOGGER = TLUtils.getLogger(NodesTreeTable.class);
    
    static {
        LOGGER.setLevel(Level.OFF);
    }
    
    private ExplorerManager em;
    
    /** Creates a new instance of NodesTreeTable */
    public NodesTreeTable(ExplorerManager explorerManager, TreeTableModel ttm) {
        super(ttm);
        this.em = explorerManager;
        
        addMouseListener(new MouseUtils.PopupMouseAdapter() {
            public void showPopup(MouseEvent e) {
                int row = rowAtPoint(e.getPoint());
                int col = columnAtPoint(e.getPoint());
                Action[] actions;
                if (row < 0 || col < 0)
                    return;
                
                setRowSelectionInterval(row, row);
                Node n = createNode(getNodeForRow(row));
                if (n == null)
                    return;
                
                actions = n.getActions(false);
                JPopupMenu pm = Utilities.actionsToPopup(actions,
                    NodesTreeTable.this);
                if(pm != null)
                    pm.show(NodesTreeTable.this, e.getX(), e.getY());
            }
        });
        
        addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (!MouseUtils.isDoubleClick(e))
                    return;
                
                int row = rowAtPoint(e.getPoint());
                int col = columnAtPoint(e.getPoint());
                if (row < 0 || col < 0)
                    return;
                
                setRowSelectionInterval(row, row);
                Node n = createNode(getNodeForRow(row));
                if (n == null)
                    return;
                
                Action action = n.getPreferredAction();
                if (action != null) {
                    action.actionPerformed(
                        new ActionEvent(this, ActionEvent.ACTION_PERFORMED, 
                        null));
                }
            }
        });
        
        getSelectionModel().addListSelectionListener(
            new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                int row = getSelectedRow();
                Node[] nodes;
                if (row < 0) {
                    nodes = new Node[0];
                } else {
                    Node n = createNode(getNodeForRow(row));
                    if (n != null)
                        nodes = new Node [] {n};
                    else
                        nodes = new Node[0];
                }
                
                if (LOGGER.isLoggable(Level.FINE)) {
                    LOGGER.fine(em.getRootContext() + ""); // NOI18N
                    LOGGER.fine(nodes.length == 0 ?
                        "null" : nodes[0].toString()); // NOI18N
                }
                
                try {
                    if (nodes.length > 0)
                        em.setRootContext(nodes[0]);
                    em.setSelectedNodes(nodes);
                } catch (PropertyVetoException ex) {
                    ErrorManager.getDefault().notify(ex);
                }
            }
        }
        );
    }
    
    /**
     * Creates a node for the specified Object returned by the TreeTableModel
     *
     * @parem obj an object returned by the TreeTableModel
     * @return created Node or null if inappropriate
     */
    public abstract Node createNode(Object obj);
}
