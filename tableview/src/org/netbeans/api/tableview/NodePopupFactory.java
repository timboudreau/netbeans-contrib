/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is the ETable module. The Initial Developer of the Original
 * Code is Nokia. Portions Copyright 2005 Nokia. All Rights Reserved.
 */
package org.netbeans.api.tableview;

import java.awt.Component;
import javax.swing.Action;
import javax.swing.JPopupMenu;
import org.netbeans.swing.etable.ETable;
import org.openide.nodes.Node;
import org.openide.nodes.NodeOp;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

/**
 * Allows customization of the popup menus of TableView and OutlineView.
 * Just create a subclass of this class and override method createPopupMenu
 * (and call setNodePopupFactory() on TableView or OutlineView).
 * @author David Strupl
 */
public class NodePopupFactory {
    
    private boolean showQuickFilter = true;
    
    /** Creates a new instance of NodePopupFactory */
    public NodePopupFactory() {
    }
    
    /**
     * Creates a popup menu with entries from the selected nodes
     * related to the given component (usually a ETable subclass). The popup
     * is created for the table element in given column and row (column
     *  and row are in the view's coordinates (not the model's)).
     */
    public JPopupMenu createPopupMenu(int row, int column, Node[] selectedNodes,
            Component component) {
        
        Action[] actions = NodeOp.findActions (selectedNodes);
        JPopupMenu res = Utilities.actionsToPopup(actions, component);
        if (showQuickFilter) {
            if (component instanceof ETable) {
                ETable et = (ETable)component;
                Object val = et.getValueAt(row, column);
                val = et.transformValue(val);
                String s = NbBundle.getBundle(NodePopupFactory.class).getString("LBL_QuickFilter");
                res.add(et.getQuickFilterPopup(column, val, s));
            }
        }
        return res;
    }

    /**
     * 
     */
    public void setShowQuickFilter(boolean show) {
        this.showQuickFilter = show;
    }
}
