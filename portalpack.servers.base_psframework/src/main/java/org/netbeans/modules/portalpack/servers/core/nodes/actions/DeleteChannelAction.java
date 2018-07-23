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

package org.netbeans.modules.portalpack.servers.core.nodes.actions;

import org.netbeans.modules.portalpack.servers.core.api.PSDeploymentManager;
import org.netbeans.modules.portalpack.servers.core.api.PSTaskHandler;
import org.netbeans.modules.portalpack.servers.core.common.enterprise.NodeTypeConstants;
import org.netbeans.modules.portalpack.servers.core.nodes.BaseNode;
import org.netbeans.modules.portalpack.servers.core.nodes.ChannelChildrenNode;
import org.netbeans.modules.portalpack.servers.core.nodes.ChannelHolderNode;
import org.netbeans.modules.portalpack.servers.core.nodes.ChannelNode;
import org.netbeans.modules.portalpack.servers.core.nodes.DnNode;
import org.netbeans.modules.portalpack.servers.core.nodes.ContainerNode;
import org.netbeans.modules.portalpack.servers.core.nodes.ContainersHolderNode;
import org.netbeans.modules.portalpack.servers.core.ui.CreateContainerChannelPanel;
import org.netbeans.modules.portalpack.servers.core.ui.NodeDetailsTopComponent;
import org.netbeans.modules.portalpack.servers.core.util.NetbeanConstants;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Set;
import javax.swing.JOptionPane;
import org.openide.awt.HtmlBrowser;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.actions.CookieAction;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 * @author Satya
 */
public class DeleteChannelAction extends CookieAction {
    
    protected void performAction(Node[] nodes) {
        
        if( (nodes == null) || (nodes.length < 1) )
            return;
        
        for(int i=0;i<nodes.length;i++) {
            ChannelNode cookie = (ChannelNode)nodes[i].getCookie(ChannelNode.class);
                
            if(cookie == null)
                return;
          //  ChannelChildrenNode chChildrenNode = cookie.getParentChannelChildrenNode();
            
           // if(chChildrenNode == null)
             //   return;
            
            PSTaskHandler handler = cookie.getDeploymentManager().getTaskHandler();
            try{
                handler.deleteChannel(cookie.getDn(),cookie.getKey(), null);
                ActionUtil.refresh(nodes[i]); 
            }catch(Exception e){
               e.printStackTrace();
            }
        }
    }
    
    protected int mode() {
        return CookieAction.MODE_EXACTLY_ONE;
    }
    
    public String getName(){
        return org.openide.util.NbBundle.getMessage(DeleteChannelAction.class, "ACT_DELETE_CHANNEL");
    }
     
    
    protected Class[] cookieClasses() {
        return new Class[] {};
    }
    
    protected void initialize() {
        super.initialize();
        putValue("noIconInMenu", Boolean.TRUE);
    }
    
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
    
    protected boolean enable(Node[] nodes) {
        
        if(nodes != null && nodes.length == 1)
            return true;
        else
            return false;
    }
    
    protected boolean asynchronous() {
        return true;
    }
    
}

