/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2001 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.corba.browser.ir.actions;
import org.openide.util.actions.NodeAction;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.netbeans.modules.corba.browser.ir.nodes.IRContainerNode;
import org.netbeans.modules.corba.browser.ir.Util;

public class RefreshAction extends NodeAction {

    /** Creates new RefreshAction */
    public RefreshAction() {
    }


    protected boolean enable (Node[] nodes){
        if (nodes!= null && nodes.length == 1){
            return (nodes[0].getCookie (IRContainerNode.class) != null);
        }
        return false;
    }

    protected void performAction (Node[] nodes){
        if ( enable ( nodes)){
            ((IRContainerNode)nodes[0].getCookie (IRContainerNode.class)).refresh();
        }
    }

    public String getName() {
        return Util.getLocalizedString ("CTL_RefreshAction");
    }

    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP; // [PENDING]
    }

}
