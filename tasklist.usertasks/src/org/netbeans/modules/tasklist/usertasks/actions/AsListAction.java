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

import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

import org.openide.util.actions.BooleanStateAction;

/**
 * Action which brings up a dialog where you can create
 * a new subtask.
 */
public class AsListAction extends BooleanStateAction {

    private static final long serialVersionUID = 1;

    protected boolean enable(Node[] node) {
        return true;
    }
    
    protected void performAction(Node[] nodes) {
    }
    
    public String getName() {
        return NbBundle.getMessage(AsListAction.class, 
            "LBL_NewSubtask"); // NOI18N
    }
    
    protected String iconResource() {
        return "org/netbeans/modules/tasklist/usertasks/actions/asList.png"; // NOI18N
    }
    
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
        // If you will provide context help then use:
        // return new HelpCtx (NewTodoItemAction.class);
    }
    
    protected boolean asynchronous() {
        return false;
    }    
}
