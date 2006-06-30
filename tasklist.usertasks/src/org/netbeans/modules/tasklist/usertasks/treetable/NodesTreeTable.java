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

import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyVetoException;
import java.util.logging.Level;

import javax.swing.Action;
import javax.swing.JPopupMenu;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.netbeans.modules.tasklist.usertasks.util.UTUtils;
import org.openide.awt.MouseUtils;
import org.openide.explorer.ExplorerManager;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Utilities;

/**
 * TreeTable with support for Nodes
 */
public abstract class NodesTreeTable extends TreeTable {
    /**
     * Root node for the selection
     */
    private static class RootNode extends AbstractNode {
        /**
         * Constructor
         */
        public RootNode() {
            super(new Children.Array());
        }
    }
    
    private ExplorerManager em;     
    
    /** 
     * This root node prevents double update of the properties view 
     * after calling setRootContext and setExploredContext
     */
    private RootNode rootNode;
    
    /** Creates a new instance of NodesTreeTable */
    public NodesTreeTable(ExplorerManager explorerManager, TreeTableModel ttm) {
        super(ttm);
        this.em = explorerManager;
        this.rootNode = new RootNode();
        this.em.setRootContext(rootNode);
        
        addMouseListener(new MouseUtils.PopupMouseAdapter() {
            public void showPopup(MouseEvent e) {
                int row = rowAtPoint(e.getPoint());
                int col = columnAtPoint(e.getPoint());
                Action[] actions;
                if (row < 0 || col < 0) {
                    actions = getFreeSpaceActions();
                } else {
                    if (!getSelectionModel().isSelectedIndex(row)) {
                        setRowSelectionInterval(row, row);
                    }
                    Node n = createNode(getNodeForRow(row));
                    if (n == null)
                        return;

                    actions = n.getActions(false);
                }
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
                int[] rows = getSelectedRows();
                Node[] nodes = new Node[rows.length];
                for (int i = 0; i < nodes.length; i++) {
                    nodes[i] = createNode(getNodeForRow(rows[i]));
                }
                
                Children.Array ch = (Children.Array) rootNode.getChildren();
                ch.remove(ch.getNodes());
                ch.add(nodes);
                try {
                    if (nodes.length > 0) {
                        em.setExploredContext(nodes[0], nodes);
                    } else {
                        em.setSelectedNodes(nodes);
                    }
                } catch (PropertyVetoException ex) {
                    UTUtils.LOGGER.log(Level.WARNING, "", e); // NOI18N
                }
            }
        }
        );
    }
    
    /**
     * Creates a node for the specified Object returned by the TreeTableModel
     *
     * @parem obj an object returned by the TreeTableModel
     * @return created Node if inappropriate
     */
    public abstract Node createNode(Object obj);
    
    /**
     * Returns actions for the popup requested on the free space
     *
     * @return actions
     */
    public Action[] getFreeSpaceActions() {
        return new Action[0];
    }
}
