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
 * DeleteResourceAction.java
 */

package org.netbeans.modules.j2ee.sun.ws7.nodes.actions;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.NodeAction;
import org.netbeans.modules.j2ee.sun.ws7.nodes.WS70ResourcesRootNode;
import org.netbeans.modules.j2ee.sun.ws7.nodes.WS70ResourceNode;
import org.netbeans.modules.j2ee.sun.ws7.nodes.WS70Resource;
import org.netbeans.modules.j2ee.sun.ws7.ui.Util;
/**
 *
 * @author Administrator
 */
public class DeleteResourceAction extends NodeAction{
    
    /**
     * Creates a new instance of DeleteResourceAction
     */
    public DeleteResourceAction() {
    }
    protected void performAction(Node[] nodes){
        Node parentNode  = nodes[0].getParentNode();
        WS70ResourceNode node = (WS70ResourceNode)nodes[0].getCookie(WS70ResourceNode.class);
        WS70Resource res = (WS70Resource)nodes[0].getCookie(WS70Resource.class);
        try{
            res.deleteResource();
        }catch(Exception ex){
            Util.showError(ex.getMessage());
            return;
        }
        WS70ResourcesRootNode resRootNode = (WS70ResourcesRootNode)parentNode.getCookie(WS70ResourcesRootNode.class);
        if(resRootNode!=null){           
            resRootNode.refresh();                    
        }
    }
    
    protected boolean enable(Node[] nodes){
        return nodes.length==1;
    }
    
    public String getName(){
        return NbBundle.getMessage(DeleteResourceAction.class, "LBL_DeleteResourceAction");
    }
    
    public HelpCtx getHelpCtx(){
        return HelpCtx.DEFAULT_HELP;
    }
    
}
