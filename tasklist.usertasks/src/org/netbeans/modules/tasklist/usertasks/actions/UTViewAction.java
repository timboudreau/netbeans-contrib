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

import javax.swing.AbstractAction;
import javax.swing.event.ListSelectionListener;
import javax.swing.tree.TreePath;
import org.netbeans.modules.tasklist.usertasks.UserTaskTreeTableNode;
import org.netbeans.modules.tasklist.usertasks.UserTaskView;
import org.netbeans.modules.tasklist.usertasks.model.UserTask;

/**
 * An action that is aware of selected nodes in a UserTaskView.
 *
 * @author tl
 */
public abstract class UTViewAction extends AbstractAction implements
ListSelectionListener {
    /** view for this action */
    protected UserTaskView utv;
    
    /**
     * Constructor.
     * 
     * @param utv a user task view.
     * @param name Action.NAME
     */
    public UTViewAction(UserTaskView utv, String name) {
        super(name);
        this.utv = utv;
        utv.getTreeTable().getSelectionModel().addListSelectionListener(this);
        this.valueChanged(null);
    }
    
    /**
     * Returns the selected task. If more than one task is selected returns null.
     *
     * @return selected task or null
     */
    protected UserTask getSingleSelectedTask() {
        TreePath[] tp = utv.getTreeTable().getSelectedPaths();
        if (tp.length == 1) {
            Object last = tp[0].getLastPathComponent();
            if (last instanceof UserTaskTreeTableNode) {
                return ((UserTaskTreeTableNode) last).getUserTask();
            }
        }
        return null;
    }
}

