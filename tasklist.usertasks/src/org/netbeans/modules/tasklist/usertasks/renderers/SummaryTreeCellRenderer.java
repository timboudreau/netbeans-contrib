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
import org.netbeans.modules.tasklist.usertasks.UserTask;
import org.netbeans.modules.tasklist.usertasks.UserTaskListTreeTableNode;
import org.netbeans.modules.tasklist.usertasks.UserTaskTreeTableNode;

import org.netbeans.modules.tasklist.usertasks.treetable.DefaultMutableTreeTableNode;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

/**
 * Cell renderer for the summary attribute
 */
public class SummaryTreeCellRenderer extends DefaultTreeCellRenderer {

    private static final long serialVersionUID = 1;

    private static final Image IMAGE =
        Utilities.loadImage("org/netbeans/modules/tasklist/core/task.gif"); // NOI18N
    private static final Image DONE =
        Utilities.loadImage("org/netbeans/modules/tasklist/core/doneItem.gif"); // NOI18N
    private static final Image UNMATCHED =
        Utilities.loadImage("org/netbeans/modules/tasklist/core/unmatched.gif"); // NOI18N
    private ImageIcon icon = new ImageIcon();
    
    public Component getTreeCellRendererComponent(JTree tree, Object value,
				   boolean selected, boolean expanded,
				   boolean leaf, int row, boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, selected, expanded,
            leaf, row, hasFocus);
        if (value instanceof UserTaskListTreeTableNode) {
            icon.setImage(IMAGE);
            setText(NbBundle.getMessage(SummaryTreeCellRenderer.class, 
                "TaskList")); // NOI18N
        } else {
            UserTaskTreeTableNode utl = (UserTaskTreeTableNode) value;
            UserTask ut = utl.getUserTask();
            setText(ut.getSummary());
            if (utl.isUnmatched())
                icon.setImage(UNMATCHED);
            else if (ut.isDone())
                icon.setImage(DONE);
            else
                icon.setImage(IMAGE);
        }
        setIcon(icon);
        return this;
    }
}
