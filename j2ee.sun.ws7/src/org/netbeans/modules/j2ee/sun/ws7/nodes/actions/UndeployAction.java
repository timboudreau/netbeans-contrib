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
