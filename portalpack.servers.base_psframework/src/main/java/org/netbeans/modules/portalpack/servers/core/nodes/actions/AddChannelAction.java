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

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import org.netbeans.modules.portalpack.servers.core.api.PSTaskHandler;
import org.netbeans.modules.portalpack.servers.core.nodes.ChannelHolderNode;
import org.netbeans.modules.portalpack.servers.core.ui.CreateContainerChannelPanel;
import org.netbeans.modules.portalpack.servers.core.util.NetbeanConstants;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.actions.CookieAction;
import org.openide.windows.WindowManager;

/**
 * @author Satya
 */
public class AddChannelAction extends CookieAction {
    private static Logger logger = Logger.getLogger(NetbeanConstants.PORTAL_LOGGER);
    protected void performAction(final Node[] nodes) {
        
        SwingUtilities.invokeLater(new Runnable(){
            
        public void run(){
        if( (nodes == null) || (nodes.length < 1) )
            return;
        
        for(int i=0;i<nodes.length;i++) {
            ChannelHolderNode cookie = (ChannelHolderNode)nodes[i].getCookie(ChannelHolderNode.class);
                
            if(cookie == null)
                return;
            
            PSTaskHandler handler = cookie.getDeploymentManager().getTaskHandler();
            try {
                
                handler.addChannel(cookie.getDn());  
                ActionUtil.refresh(nodes[i]); 
            } catch (Exception ex) {
                logger.log(Level.SEVERE,"Error",ex);
                NotifyDescriptor nd =new  NotifyDescriptor.Message(org.openide.util.NbBundle.getMessage(AddChannelAction.class, "MSG_COULD_NOT_ADD_CHANNEL"),NotifyDescriptor.ERROR_MESSAGE);
                DialogDisplayer.getDefault().notify(nd);
            }            
        }
        }});
    }
    
    protected int mode() {
        return CookieAction.MODE_EXACTLY_ONE;
    }
    
    public String getName(){
        return org.openide.util.NbBundle.getMessage(AddChannelAction.class, "ACT_ADD_CHANNEL");
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
        
        if(nodes != null && nodes.length ==1)
            return true;
        else
            return false;
    }
    
    protected boolean asynchronous() {
        return true;
    }
    
}

