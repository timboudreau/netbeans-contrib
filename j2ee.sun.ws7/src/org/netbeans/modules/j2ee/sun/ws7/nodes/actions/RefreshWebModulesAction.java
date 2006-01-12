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
 * RefreshWebModulesAction.java
 */

package org.netbeans.modules.j2ee.sun.ws7.nodes.actions;

import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.NodeAction;
import org.netbeans.modules.j2ee.sun.ws7.nodes.WS70WebModulesRootNode;


/**
 *
 * @author Administrator
 */
public class RefreshWebModulesAction extends NodeAction{
    
    /** Creates a new instance of RefreshWebModulesAction */
    public RefreshWebModulesAction() {
    }
    
    protected void performAction(Node[] nodes){
        
        WS70WebModulesRootNode node = (WS70WebModulesRootNode)nodes[0].getCookie(WS70WebModulesRootNode.class);
        if(node!=null){
            node.refresh();
        }       
    }
    
    protected boolean enable(Node[] nodes){
        return nodes.length==1;
    }
    
    public String getName(){
        return NbBundle.getMessage(RefreshWebModulesAction.class, "LBL_RefreshAction");
    }
    
    public HelpCtx getHelpCtx(){
        return HelpCtx.DEFAULT_HELP;
    }
    
}
