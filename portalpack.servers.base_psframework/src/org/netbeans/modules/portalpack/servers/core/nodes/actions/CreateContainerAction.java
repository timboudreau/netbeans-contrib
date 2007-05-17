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
import org.netbeans.modules.portalpack.servers.core.nodes.BaseNode;
import org.netbeans.modules.portalpack.servers.core.nodes.DnNode;
import org.netbeans.modules.portalpack.servers.core.nodes.ContainerNode;
import org.netbeans.modules.portalpack.servers.core.nodes.ContainersHolderNode;
import org.netbeans.modules.portalpack.servers.core.ui.CreateContainerChannelPanel;
import org.netbeans.modules.portalpack.servers.core.util.NetbeanConstants;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.actions.CookieAction;
import org.openide.windows.WindowManager;

/**
 * @author Satya
 */
public final class CreateContainerAction extends CookieAction {
    
    private static Logger logger = Logger.getLogger(NetbeanConstants.PORTAL_LOGGER);
    protected void performAction(final Node[] nodes) {
        try{
        SwingUtilities.invokeLater(new Runnable(){
            
        public void run(){
        if( (nodes == null) || (nodes.length < 1) )
            return;
        for(int i=0;i<nodes.length;i++) {
            
            String containerNamePrefix = "";
            BaseNode cookie = (BaseNode)nodes[i].getCookie(DnNode.class);
            if (cookie == null) {
                cookie = (BaseNode)nodes[i].getCookie(ContainersHolderNode.class);
                if(cookie == null) {
                    cookie = (BaseNode)nodes[i].getCookie(ContainerNode.class);
                    containerNamePrefix = cookie.getKey() + "/";
                }
                
                if(cookie == null)
                    return;
            }
            PSDeploymentManager manager = cookie.getDeploymentManager();
            
            if(manager == null) {
                logger.log(Level.SEVERE,"PSDeploymentManager is null...return back");
                return;
            }
            
            logger.log(Level.FINE,"Base Dn for Node: "+cookie.getDn()+"  coookie: "+cookie.getClass().getName());
            String[] providers = null;
            try {
                providers = manager.getTaskHandler().getExistingContainerProviders(cookie.getDn());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            if(providers == null)
                return;
            
            CreateContainerChannelPanel dialog = new CreateContainerChannelPanel(WindowManager.getDefault().getMainWindow(),org.openide.util.NbBundle.getMessage(CreateContainerAction.class, "LBL_Container"),org.openide.util.NbBundle.getMessage(CreateContainerAction.class, "LBL_Provider"),org.openide.util.NbBundle.getMessage(CreateContainerAction.class, "LBL_Create_a_container"),providers);
            
            dialog.setVisible(true);
            
            String containerName = dialog.getName();
            String providerName = dialog.getType();
            
            containerName = containerNamePrefix + containerName;
            
            logger.log(Level.FINE,containerName+"                      "+providerName);
            
            if(containerName == null || containerName.trim().length() == 0)
                return;
            if(providerName == null || providerName.trim().length() == 0)
                return;
            logger.log(Level.FINE,"Base Dn for Node: "+cookie.getDn()+"  coookie: "+cookie.getClass().getName());
            try {
                
                manager.getTaskHandler().createContainer(cookie.getDn(),containerName,providerName);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(WindowManager.getDefault().getMainWindow(),org.openide.util.NbBundle.getMessage(CreateContainerAction.class, "MSG_Could_not_create_container")+containerName);
            }
            
            
            PSTaskHandler handler = manager.getTaskHandler();
            //check if the container is in the selected list. If not then add it to available list and then selected list
            
            if(containerNamePrefix != null && containerNamePrefix.length() != 0) {
                //check if the channel is already there in selected list
                List selectedList = null;
                try {
                    
                    selectedList = handler.getSelectedChannels(cookie.getDn(),cookie.getKey());
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(WindowManager.getDefault().getMainWindow(),org.openide.util.NbBundle.getMessage(CreateContainerAction.class, "MSG_CONTAINER_COULDNOT_BE_ADDED_TO_SELECTED_LIST"));
                    return;
                }
                
                if(selectedList.contains(containerName)) {
                    logger.log(Level.WARNING,"Container "+containerName + " is already present in the selected list.");
                    return;
                }
                //Add to available list
                
                List availableList = null;
                try {
                    
                    availableList = handler.getAvailableChannels(cookie.getDn(),cookie.getKey());
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(WindowManager.getDefault().getMainWindow(),org.openide.util.NbBundle.getMessage(CreateContainerAction.class, "MSG_CONTAINER_COULD_NOT_BE_ADDED_TO_AVAILABLE_LIST"));
                    return;
                }
                
                logger.log(Level.FINE,"Available_list_:::_");
                if(availableList != null)
                    availableList.add(containerName);
                try {
                    
                    handler.setAvailableChannels(cookie.getDn(),availableList,cookie.getKey());
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(WindowManager.getDefault().getMainWindow(),org.openide.util.NbBundle.getMessage(CreateContainerAction.class, "MSG_CONTAINER_COULD_NOT_BE_ADDED_TO_AVAILABLE_LIST"));
                }
                
                //add to selected list
                
                logger.log(Level.FINE,"Selected list ::: "+selectedList);
                if(selectedList != null)
                    selectedList.add(containerName);
                try {
                    
                    handler.setSelectedChannels(cookie.getDn(),selectedList,cookie.getKey());
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(WindowManager.getDefault().getMainWindow(),org.openide.util.NbBundle.getMessage(CreateContainerAction.class, "MSG_CONTAINER_COULDNOT_BE_ADDED_TO_SELECTED_LIST"));
                }
                
            }
            
            ActionUtil.refresh(nodes[i]);            
        }
        }});
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    protected int mode() {
        return CookieAction.MODE_EXACTLY_ONE;
    }
    
    public String getName() {
        return org.openide.util.NbBundle.getMessage(CreateContainerAction.class, "ACT_Create_Container");
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

