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

package org.netbeans.modules.tasklist.usertasks.renderers;

import java.awt.Component;
import javax.swing.JTable;
import org.netbeans.modules.tasklist.usertasks.UserTaskTreeTableNode;
import org.netbeans.modules.tasklist.usertasks.model.UserTask;
import org.netbeans.modules.tasklist.usertasks.treetable.BooleanTableCellRenderer;
import org.netbeans.modules.tasklist.usertasks.treetable.TreeTable;

/**
 * Cell renderer for the "done" property.
 *
 * @author tl
 */
public class DoneTreeTableCellRenderer extends BooleanTableCellRenderer {
    /**
     * Creates a new instance of DoneTreeTableCellRenderer
     */
    public DoneTreeTableCellRenderer() {
    }

    @Override
    public java.awt.Component getTableCellRendererComponent(
            JTable table, 
            Object value, boolean isSelected, boolean hasFocus, 
            int row, int column) {
        Object node = ((TreeTable) table).getNodeForRow(row);
        Component cmp = super.getTableCellRendererComponent(
                table, value, isSelected, hasFocus, 
                row, column);
        if (value == null)
            return cmp;
        
        if (node instanceof UserTaskTreeTableNode) {
            UserTask ut = ((UserTaskTreeTableNode) node).getUserTask(); 
            setEnabled(!ut.isValuesComputed() && ut.areDependenciesDone());
        }
        return this;
    }
}
