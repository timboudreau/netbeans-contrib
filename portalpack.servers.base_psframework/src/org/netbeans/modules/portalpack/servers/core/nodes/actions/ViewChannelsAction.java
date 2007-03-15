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
import org.netbeans.modules.portalpack.servers.core.nodes.ContainerNode;
import org.netbeans.modules.portalpack.servers.core.util.NetbeanConstants;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.awt.HtmlBrowser;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.actions.CookieAction;

public final class ViewChannelsAction extends CookieAction {
    
    private static Logger logger = Logger.getLogger(NetbeanConstants.PORTAL_LOGGER);
    protected void performAction(Node[] nodes) {
        
        if( (nodes == null) || (nodes.length < 1) )
            return;
        
        for(int i=0;i<nodes.length;i++)
        {
            ContainerNode cookie = (ContainerNode)nodes[i].getCookie(ContainerNode.class);
            
            if (cookie == null)
                
                continue;
            String name = cookie.getName();
            PSDeploymentManager manager = cookie.getDeploymentManager();
            
            String  contextUri = manager.getPSConfig().getPortalUri().replaceAll("/","");
            
            String authlessUser = null;
            try{
                authlessUser = manager.getTaskHandler().getAuthlessUser(cookie.getDn());
            }catch(Exception e){
                logger.log(Level.SEVERE,"Error getting authless user for dn "+cookie.getDn(),e);
            }
            
            String portalUrl = null;
            if(authlessUser == null)
                portalUrl = "http://" + manager.getPSConfig().getHost() + ":" + manager.getPSConfig().getPort() +"/"+contextUri+"/dt?action=content&provider="+ cookie.getKey();
            else
                portalUrl = "http://" + manager.getPSConfig().getHost() + ":" + manager.getPSConfig().getPort() +"/"+contextUri+"/dt?desktop.suid="+authlessUser+"&action=content&provider="+ cookie.getKey();
            
            try {
                
                HtmlBrowser.URLDisplayer.getDefault().showURL(new URL(portalUrl));
                
            } catch (MalformedURLException ex) {
                
                logger.log(Level.SEVERE,"error",ex);
            }
        }
    }

    protected int mode() {    
        return CookieAction.MODE_EXACTLY_ONE;
    }
    public String getName() {        
        return org.openide.util.NbBundle.getMessage(ViewChannelsAction.class, "ACT_Lauch_Browser");        
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
        if (nodes == null || nodes.length < 1 || nodes.length > 1)
            return false;
        return true;
    }
    protected boolean asynchronous() {
        return true;
    }
}
