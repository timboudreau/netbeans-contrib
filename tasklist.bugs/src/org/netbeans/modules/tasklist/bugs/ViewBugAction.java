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

package org.netbeans.modules.tasklist.bugs;

import org.netbeans.modules.tasklist.core.TaskNode;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.NodeAction;


/**
   @author Tor Norbye
*/
public class ViewBugAction extends NodeAction {

    private static final long serialVersionUID = 1;

    protected boolean asynchronous() {
        return false;
    }

    protected boolean enable(Node[] node) {
        return ((node != null) &&
		(node.length == 1) &&
                (TaskNode.getTaskNode(node[0]) != null) &&
                (TaskNode.getTaskNode(node[0]) instanceof BugNode));
    }
    
    protected void performAction(Node[] node) {
	Bug bug = (Bug)TaskNode.getTask(node[0]);
	bug.view();
    }
    
    public String getName() {
        return NbBundle.getMessage(ViewBugAction.class, "ViewBug"); // NOI18N
    }

    /*
    protected String iconResource() {
        return "org/netbeans/modules/tasklist/bugs/viewbug.gif"; // NOI18N
    }
    */
    
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
        // If you will provide context help then use:
        // return new HelpCtx (NewTodoItemAction.class);
    }
    
}
