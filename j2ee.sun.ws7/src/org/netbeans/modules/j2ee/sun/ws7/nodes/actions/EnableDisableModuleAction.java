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
            Object obj = nodes[0].getCookie(WS70WebModule.class);
            if(obj!=null && obj instanceof WS70WebModule){
                WS70WebModule module = (WS70WebModule)obj;
                if(module!=null){
                    if(module.isModuleEnabled()) {
                        enabled = true;
                    } else {
                        enabled = false;
                    }               
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
