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

import java.awt.*;


/** Perform a refresh on the buglist
    @todo This should probably be made a CallableSystemAction - doesn't
    need to be a NodeAction (which causes more work to be done on activated
    node change when it's visible)
   @author Tor Norbye
*/
public class RefreshAction extends NodeAction {

    private static final long serialVersionUID = 1;

    protected boolean asynchronous() {
        return false;
    }

    protected boolean enable(Node[] node) {
        return true;
    }
    
    protected void performAction(Node[] node) {
        BugList list = (BugList) BugsView.getCurrent().getList();
        if (list != null) {
            list.refresh();
        } else {
            Toolkit.getDefaultToolkit().beep();
        }
    }
    
    public String getName() {
        return NbBundle.getMessage(RefreshAction.class, "Refresh"); // NOI18N
    }

    /*
    protected String iconResource() {
        return "org/netbeans/modules/tasklist/bugs/refresh.gif"; // NOI18N
    }
    */
    
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
        // If you will provide context help then use:
        // return new HelpCtx (NewTodoItemAction.class);
    }
    
}
