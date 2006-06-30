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
 * UndeployAction.java
 *
 */

package org.netbeans.modules.j2ee.sun.ws7.nodes.actions;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.NodeAction;
import org.netbeans.modules.j2ee.sun.ws7.nodes.WS70WebModule;
import org.netbeans.modules.j2ee.sun.ws7.nodes.WS70WebModuleNode;
import org.netbeans.modules.j2ee.sun.ws7.nodes.WS70WebModulesRootNode;
import org.netbeans.modules.j2ee.sun.ws7.ui.Util;

/**
 *
 * @author Administrator
 */
public class UndeployAction extends NodeAction{
    
    /** Creates a new instance of UndeployAction */
    public UndeployAction() {
    }
    protected void performAction(Node[] nodes){
        Node parentNode  = nodes[0].getParentNode();
        WS70WebModuleNode node = (WS70WebModuleNode)nodes[0].getCookie(WS70WebModuleNode.class);
        WS70WebModule module = (WS70WebModule)nodes[0].getCookie(WS70WebModule.class);
        try{            
            module.undeploy();
        }catch(Exception ex){
            Util.showError(ex.getMessage());
            return;
        }
        WS70WebModulesRootNode moduleRootNode = (WS70WebModulesRootNode)parentNode.getCookie(WS70WebModulesRootNode.class);
        if(moduleRootNode!=null){           
            moduleRootNode.refresh();                    
        }
    }
    
    protected boolean enable(Node[] nodes){
        return nodes.length==1;
    }
    
    public String getName(){
        return NbBundle.getMessage(UndeployAction.class, "LBL_UndeployModuleAction");
    }
    
    public HelpCtx getHelpCtx(){
        return HelpCtx.DEFAULT_HELP;
    }    
}
