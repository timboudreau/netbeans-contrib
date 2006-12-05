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
import org.netbeans.modules.portalpack.servers.core.nodes.PortletNode;
import org.netbeans.modules.portalpack.servers.core.util.NetbeanConstants;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.deploy.shared.CommandType;
import javax.enterprise.deploy.shared.StateType;
import javax.enterprise.deploy.spi.status.DeploymentStatus;
import javax.enterprise.deploy.spi.status.ProgressEvent;
import javax.enterprise.deploy.spi.status.ProgressListener;
import javax.enterprise.deploy.spi.status.ProgressObject;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.StatusDisplayer;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.actions.CookieAction;

/**
 * @author Satya
 */
public final class UndeployAction extends CookieAction {
    private static Logger logger = Logger.getLogger(NetbeanConstants.PORTAL_LOGGER);
    protected void performAction(Node[] nodes) {
        logger.log(Level.FINE,"Length: "+nodes.length);
        if( (nodes == null) || (nodes.length < 1) )
             return;
        for(int i=0;i<nodes.length;i++)
        {
            PortletNode cookie = (PortletNode)nodes[i].getCookie(PortletNode.class);            
            if (cookie == null)
                 continue;
            
            String name = cookie.getName();
            int index = name.indexOf(".");
            if(index != -1)
            {
                name = name.substring(0,index);
            }
            logger.log(Level.FINE,"Portlet Name is ::::::::::::::::: "+name);
            PSDeploymentManager manager = cookie.getDeploymentManager();
            
            StatusDisplayer.getDefault().setStatusText(org.openide.util.NbBundle.getMessage(UndeployAction.class, "MSG_START_UNDEPLOYMENT")+name); 
            ProgressObject po = manager.undeploy(name,cookie.getDn());
            
            po.addProgressListener(new UnDeployManagerProgressListener(cookie,name));
                              
        }
    }
    
    protected int mode() {
        return CookieAction.MODE_EXACTLY_ONE;
    }
    
    public String getName() {
        return org.openide.util.NbBundle.getMessage(UndeployAction.class, "ACT_UNDEPLOY");
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

        if (nodes == null)
            return false;
        if(nodes.length == 1)
            return true;
        else
            return false;
        
    }
    
    protected boolean asynchronous() {
        return true;
    }
    
    class UnDeployManagerProgressListener implements ProgressListener {
        
        private ProgressHandle handle;
        private Node node;
        public UnDeployManagerProgressListener(Node node,String name)
        {
            this.node = node;
            handle = ProgressHandleFactory.createHandle(org.openide.util.NbBundle.getMessage(UndeployAction.class, "MSG_UNDEPLOY")+name);
            handle.start();
        }
        public void handleProgressEvent(ProgressEvent progressEvent) {
            DeploymentStatus deployStatus = progressEvent.getDeploymentStatus();
            
            logger.log(Level.FINEST,"Status type is :****" + deployStatus.getState()+"   command: "+deployStatus.getCommand());
            
            logger.log(Level.FINEST,"constant value :****" + StateType.COMPLETED +"   command: "+CommandType.UNDEPLOY);
            
            if (deployStatus.getState() == StateType.COMPLETED) {
                CommandType command = deployStatus.getCommand();           
                
                logger.log(Level.FINEST,"Status is completed............" + command);
                handle.finish();
               
                                
                if (command.getValue() == CommandType.START.getValue() || command.getValue() == CommandType.STOP.getValue()) {

                } else if (command.getValue() == CommandType.UNDEPLOY.getValue()) {
                         StatusDisplayer.getDefault().setStatusText(org.openide.util.NbBundle.getMessage(UndeployAction.class, "MSG_UNDEPLOYED_SUCCESSFULLY"));                
                
                }
            } else if (deployStatus.getState().equals(StateType.FAILED)) {
                handle.finish();
                NotifyDescriptor notDesc = new NotifyDescriptor.Message(
                        deployStatus.getMessage(), 
                        NotifyDescriptor.ERROR_MESSAGE);
                DialogDisplayer.getDefault().notify(notDesc);
                StatusDisplayer.getDefault().setStatusText(deployStatus.getMessage());                
            }
            
            if(node != null)
                ActionUtil.refresh(node);
        }
    }
    
}

