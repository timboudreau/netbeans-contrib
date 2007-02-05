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
import org.netbeans.modules.portalpack.servers.core.nodes.ContainerNode;
import org.netbeans.modules.portalpack.servers.core.nodes.ContainersHolderNode;
import org.netbeans.modules.portalpack.servers.core.ui.CreateContainerChannelPanel;
import org.netbeans.modules.portalpack.servers.core.util.NetbeanConstants;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.actions.CookieAction;
import org.openide.windows.WindowManager;

/**
 * @author Satya
 */
public final class AddPortletAction extends CookieAction {
    private static Logger logger = Logger.getLogger(NetbeanConstants.PORTAL_LOGGER);
    protected void performAction(Node[] nodes) {
        
        if( (nodes == null) || (nodes.length < 1) )
            return;
        for(int i=0;i<nodes.length;i++) {
            String containerNamePrefix = "";
            
            ContainerNode cookie = null;//(ContainerNode)nodes[i].getCookie(ContainersHolderNode.class);
            if(cookie == null) {
                cookie = (ContainerNode)nodes[i].getCookie(ContainerNode.class);
                containerNamePrefix = cookie.getKey() + "/";
            }
            
            if(cookie == null)
                return;
            PSDeploymentManager manager = cookie.getDeploymentManager();
            
            if(manager == null) {
                logger.log(Level.SEVERE,"PSDeploymentManager is null...return back");
                return;
            }
            
            logger.log(Level.FINE,"Base Dn for Node: "+cookie.getDn()+"  coookie: "+cookie.getClass().getName());
            String[] portlets = null;
            
            PSTaskHandler handler = manager.getTaskHandler();
            try {
                portlets = handler.getPortlets(cookie.getDn());
            } catch (Exception ex) {
                ex.printStackTrace();
                
            }
            if(portlets == null)
                return;
            
            CreateContainerChannelPanel dialog = new CreateContainerChannelPanel(WindowManager.getDefault().getMainWindow(),org.openide.util.NbBundle.getMessage(AddPortletAction.class, "LBL_CHANNELS"),org.openide.util.NbBundle.getMessage(AddPortletAction.class, "LBL_PORTLETS"),org.openide.util.NbBundle.getMessage(AddPortletAction.class, "MSG_CREATE_A_PORTLET_CHANNEL"),portlets);
            dialog.setVisible(true);
            
            String channelName = dialog.getName();
            String portletName = dialog.getType();
            
            if(channelName == null || channelName.length() == 0)
                return;
            
            
            channelName = containerNamePrefix + channelName;
            
            logger.log(Level.FINE,"Channel name : "+channelName);
            logger.log(Level.FINE,"Portlet Name: " +portletName);
            try {
                
                handler.createPortletChannel(cookie.getDn(),channelName,portletName);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(WindowManager.getDefault().getMainWindow(),org.openide.util.NbBundle.getMessage(AddPortletAction.class, "MSG_Portlet_Could_not_be_added"));
            }
            
            
            //add channel to setSelectedList.
            ActionUtil.addChannelToSelectedList(cookie.getDn(),channelName,cookie.getKey(),handler);
            
        }
    }
    
    protected int mode() {
        return CookieAction.MODE_EXACTLY_ONE;
    }
    
    public String getName() {
        return org.openide.util.NbBundle.getMessage(AddPortletAction.class, "ACT_Add_Portlet_Channel");
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
        
        if (nodes == null || nodes.length < 1)
            return false;
        
        return true;
    }
    
    protected boolean asynchronous() {
        return true;
    }
}

