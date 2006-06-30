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
