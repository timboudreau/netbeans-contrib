/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

/*
 * ViewTargetServerLogAction.java
 */

package org.netbeans.modules.j2ee.sun.ws7.nodes.actions;

import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.ErrorManager;
import org.openide.util.actions.NodeAction;
import org.netbeans.modules.j2ee.sun.ws7.nodes.WS70TargetNode;


/**
 *
 * @author Administrator
 */
public class ViewTargetServerLogAction extends NodeAction{
    
    /** Creates a new instance of ViewTargetServerLogAction */
    public ViewTargetServerLogAction() {
    }
    protected void performAction(Node[] nodes){
        WS70TargetNode target = (WS70TargetNode)nodes[0].getCookie(WS70TargetNode.class);
        if(target==null){
        ErrorManager.getDefault().log(
                ErrorManager.ERROR, NbBundle.getMessage(ViewTargetServerLogAction.class, "ERR_NULL_TARGET", this.getClass().getName()));
            return;
        }
        target.invokeLogViewer();
    }
    
    protected boolean enable(Node[] nodes){
        return nodes.length==1;
    }
    
    public String getName(){
        return NbBundle.getMessage(ViewTargetServerLogAction.class, "LBL_ViewTargetServerLogAction"); // NOI18N
    }
    
    public HelpCtx getHelpCtx(){
        return HelpCtx.DEFAULT_HELP;
    }           
}
