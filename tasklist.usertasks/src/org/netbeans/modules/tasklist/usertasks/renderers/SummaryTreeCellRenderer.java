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
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import org.netbeans.modules.tasklist.usertasks.model.UserTask;
import org.netbeans.modules.tasklist.usertasks.UserTaskListTreeTableNode;
import org.netbeans.modules.tasklist.usertasks.UserTaskTreeTableNode;

import org.openide.util.NbBundle;
import org.openide.util.Utilities;

/**
 * Cell renderer for the summary attribute
 */
public class SummaryTreeCellRenderer extends DefaultTreeCellRenderer {

    private static final long serialVersionUID = 1;

    private ImageIcon icon = new ImageIcon();
    
    public SummaryTreeCellRenderer() {
        ImageIcon icon = new ImageIcon();
        
        // see TreeTable.TreeTableCellEditor.getTableCellEditorComponent
        setLeafIcon(icon);
        setOpenIcon(icon);
        setClosedIcon(icon);
    }
    
    public Component getTreeCellRendererComponent(JTree tree, Object value,
				   boolean selected, boolean expanded,
				   boolean leaf, int row, boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, selected, expanded,
            leaf, row, hasFocus);
        if (value instanceof UserTaskListTreeTableNode) {
            icon.setImage(UserTaskIconProvider.getUserTaskListImage());
            setText(NbBundle.getMessage(SummaryTreeCellRenderer.class, 
                "TaskList")); // NOI18N
        } else {
            UserTaskTreeTableNode utl = (UserTaskTreeTableNode) value;
            UserTask ut = utl.getUserTask();
            setText(ut.getSummary());
            icon.setImage(UserTaskIconProvider.getUserTaskImage(ut, utl.isUnmatched()));
        }
        setIcon(icon);
        return this;
    }
}
