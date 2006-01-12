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
 * EnableDisableModuleAction.java
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
public class EnableDisableModuleAction extends NodeAction{
    private boolean enabled;    

    protected void performAction(Node[] nodes){        
        Node parentNode  = nodes[0].getParentNode();
        WS70WebModuleNode node = (WS70WebModuleNode)nodes[0].getCookie(WS70WebModuleNode.class);
        WS70WebModule module = (WS70WebModule)nodes[0].getCookie(WS70WebModule.class);
        
        try{
            if(module.isModuleEnabled()) {
                module.setModuleEnabled(false);
                enabled = false;
            } else {
                module.setModuleEnabled(true);
                enabled = true;
            }
        }catch(Exception ex){
            Util.showError(ex.getMessage());
            return;
        } 
    }
    
    protected boolean enable(Node[] nodes){        
        if(nodes.length > 0) {
            Node node = nodes[0];
            
            WS70WebModule module = (WS70WebModule)nodes[0].getCookie(WS70WebModule.class);
            if(module!=null){
                if(module.isModuleEnabled()) {
                    enabled = true;
                } else {
                    enabled = false;
                }               
            }
        }        
  
        return nodes.length==1;
    }
    
    public String getName(){        
        if(!enabled){
            return NbBundle.getMessage(EnableDisableModuleAction.class, "LBL_EnableModuleAction");
        }else{
            return NbBundle.getMessage(EnableDisableModuleAction.class, "LBL_DisableModuleAction");
        }
    }
    
    public HelpCtx getHelpCtx(){
        return HelpCtx.DEFAULT_HELP;
    }    
}
