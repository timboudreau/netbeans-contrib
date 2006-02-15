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

package org.netbeans.modules.tasklist.usertasks.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.tree.TreePath;
import org.netbeans.modules.tasklist.usertasks.UserTaskView;
import org.netbeans.modules.tasklist.usertasks.UserTaskViewRegistry;
import org.netbeans.modules.tasklist.usertasks.treetable.TreeTable;
import org.openide.util.NbBundle;

/**
 * Moves a task to the left.
 *
 * @author tl
 */
public class MoveLeftAction extends AbstractAction {
    private static final long serialVersionUID = 1;

    /**
     * Creates an instance.
     */
    public MoveLeftAction() {
        this.putValue(AbstractAction.NAME, 
                NbBundle.getMessage(MoveLeftAction.class,
                "MoveLeft")); // NOI18N
    }
    
    public void actionPerformed(ActionEvent event) {
        UserTaskView utv = UserTaskViewRegistry.getInstance().getCurrent();
        TreeTable tt = utv.getTreeTable();
        TreePath[] paths = tt.getSelectedPaths();
        
    }
}
