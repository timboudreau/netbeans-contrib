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
import org.openide.text.Line;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

import org.openide.util.actions.CookieAction;


/**
 * Go to the source code / associated file for a particular
 * task.
 *
 * @author Tor Norbye
 * @author tl
 */
public class GoToUserTaskAction extends CookieAction {

    private static final long serialVersionUID = 1;

    protected boolean asynchronous() {
        return false;
    }

    /** 
     * Do the actual jump to source
     * @param nodes Nodes, where the selected node should be a task
     * node. 
     */    
    protected void performAction(Node[] nodes) {
        SingleLineCookie c = 
            (SingleLineCookie) nodes[0].getCookie(SingleLineCookie.class);
        Line line = c.getLine();
        assert line != null;
        line.show(Line.SHOW_GOTO);
    }
    
    protected Class[] cookieClasses() {
        return new Class[] {SingleLineCookie.class};
    }
    
    protected int mode() {
        return CookieAction.MODE_EXACTLY_ONE;
    }    
    
    public String getName() {
        return NbBundle.getMessage(GoToUserTaskAction.class, "LBL_Goto"); // NOI18N
    }
    
    protected String iconResource() {
        return "org/netbeans/modules/tasklist/usertasks/actions/gotosource.png"; // NOI18N
    }
    
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
        // If you will provide context help then use:
        // return new HelpCtx (ShowTodoItemAction.class);
    }
}
