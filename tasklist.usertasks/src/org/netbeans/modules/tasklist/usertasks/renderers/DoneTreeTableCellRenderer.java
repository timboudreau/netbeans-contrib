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

package org.netbeans.modules.tasklist.usertasks.renderers;

import java.awt.Component;
import org.netbeans.modules.tasklist.usertasks.UTUtils;
import org.netbeans.modules.tasklist.usertasks.UserTaskTreeTableNode;
import org.netbeans.modules.tasklist.usertasks.treetable.BooleanTableCellRenderer;
import org.netbeans.modules.tasklist.usertasks.treetable.TreeTableRenderer;

/**
 * Cell renderer for the "done" property.
 *
 * @author tl
 */
public class DoneTreeTableCellRenderer extends BooleanTableCellRenderer 
implements TreeTableRenderer {
    /** 
     * Creates a new instance of DoneTreeTableCellRenderer
     */
    public DoneTreeTableCellRenderer() {
    }

    public java.awt.Component getTreeTableCellRendererComponent(
            org.netbeans.modules.tasklist.usertasks.treetable.TreeTable table, 
            Object node, Object value, boolean isSelected, boolean hasFocus, 
            int row, int column) {
        Component cmp = super.getTableCellRendererComponent(
                table, value, isSelected, hasFocus, 
                row, column);
        if (value == null)
            return cmp;
        
        if (node instanceof UserTaskTreeTableNode) {
            boolean b = ((UserTaskTreeTableNode) node).getUserTask().
                    isProgressComputed();
            setEnabled(!b);
        }
        return this;
    }
}
