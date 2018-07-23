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

package org.netbeans.modules.portalpack.servers.core.impl;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import javax.swing.Action;
import org.netbeans.modules.portalpack.servers.core.api.*;
import org.netbeans.modules.portalpack.servers.core.nodes.ChannelChildrenNode;
import org.netbeans.modules.portalpack.servers.core.nodes.actions.AddChannelToSelected;
import org.netbeans.modules.portalpack.servers.core.nodes.actions.AddPortletAction;
import org.netbeans.modules.portalpack.servers.core.nodes.actions.CreateContainerAction;
import org.netbeans.modules.portalpack.servers.core.nodes.actions.DeleteChannelAction;
import org.netbeans.modules.portalpack.servers.core.nodes.actions.DeleteChannelFromSelected;
import org.netbeans.modules.portalpack.servers.core.nodes.actions.DeleteContainerAction;
import org.netbeans.modules.portalpack.servers.core.nodes.actions.RefreshPortletsAction;
import org.netbeans.modules.portalpack.servers.core.nodes.actions.*;
import org.netbeans.modules.portalpack.servers.core.nodes.actions.UndeployAction;
import org.netbeans.modules.portalpack.servers.core.nodes.actions.ViewChannelsAction;
import org.netbeans.modules.portalpack.servers.core.nodes.actions.ViewPortletAction;
import org.openide.nodes.Node;
import org.openide.util.actions.SystemAction;

/**
 *
 * @author root
 */
public class DefaultPSNodeConfiguration implements PSNodeConfiguration {
    
    private static DefaultPSNodeConfiguration instance;
    /**
     * Creates a new instance of DefaultPSNodeConfiguration
     */
    protected DefaultPSNodeConfiguration() {
    }
    
    public static PSNodeConfiguration getInstance()
    {
        if(instance == null)
        {
            synchronized(DefaultPSNodeConfiguration.class)
            {
                if(instance == null)
                    instance = new DefaultPSNodeConfiguration();
            }
        }
        
        return instance;
    }
    
    public boolean showDnNodes(){
        return true;
    }
    
    public boolean showPortletNodes(){
        return true;
    }
    
    public boolean showContainerNodes(){
        return true;
    }
    
    public boolean showChannelNodes(){
        return true;
    }
    
    public javax.swing.Action[] getDnActions() {
        javax.swing.Action[]  newActions = new javax.swing.Action[2] ;
        newActions[0]=(null);        
        newActions[1]= (SystemAction.get(CreateContainerAction.class));
        return newActions;
    }
    
    public javax.swing.Action[] getPortletActions() {
       javax.swing.Action[]  newActions = new javax.swing.Action[3] ;
       newActions[0]=(null);        
       newActions[1]= (SystemAction.get(UndeployAction.class));
       newActions[2] = (SystemAction.get(ViewPortletAction.class));
       return newActions;
    }
    
    public javax.swing.Action[] getContainerActions() {
        javax.swing.Action[]  newActions = new javax.swing.Action[5] ;
        newActions[0]=(null);        
        newActions[1]= (SystemAction.get(CreateContainerAction.class));
        newActions[2]= (SystemAction.get(DeleteContainerAction.class));
        newActions[3] = (SystemAction.get(AddPortletAction.class));
        newActions[4] = (SystemAction.get(ViewChannelsAction.class));
        return newActions;
    }
    
    /**
     *  type can be ChannelChildrenNode.EXISTING_TYPE/ChannelChildrenNode.AVAILABLE_TYPE/ChannelChildrenNode.SELECTED_TYPE
     **/
    public javax.swing.Action[] getChannelActions(String channelType) {
        javax.swing.Action[]  newActions = new javax.swing.Action[2] ;
        newActions[0]=(null);
        if(channelType.equals(ChannelChildrenNode.EXISTING_TYPE) || channelType.equals(ChannelChildrenNode.AVAILABLE_TYPE))
            newActions[1] = (SystemAction.get(AddChannelToSelected.class));
        else
            newActions[1] = (SystemAction.get(DeleteChannelFromSelected.class));
        return newActions;
    }
    
    public Action[] getTopChannelsActions() {
        javax.swing.Action[] newActions = new javax.swing.Action[2];
        newActions[0] = null;
      //  newActions[1] = SystemAction.get(DeleteChannelAction.class);
        return newActions;
    }

    public Node[] getCustomChildrenForDnNode(PSDeploymentManager dm, String baseDn, String key) {
        return null;
    }

    public Node[] getCustomChildrenForContainerNode(PSDeploymentManager dm, String baseDn, String key) {
        return null;
    }

    public Node[] getCustomChildrenForPortletNode(PSDeploymentManager dm, String baseDn, String key) {
        return null;
    }

    public Node[] getCustomChildrenForChannelNode(PSDeploymentManager dm, String baseDn, String key) {
        return null;
    }

    public Node[] getCustomChildrenForRootNode(PSDeploymentManager dm, String baseDn, String key) {
        return null;
    }

    public boolean showTopChannelsNode() {
        return true;
    }

    
    public Node[] getCustomChildrenForTopChannelsNode(PSDeploymentManager dm, String baseDn, String key) {
        return null;
    }

    public Action[] getChannelFolderActions() {
             return new SystemAction[] {
                SystemAction.get(RefreshPortletsAction.class),
                SystemAction.get(ShowExistingChannelAction.class),
                SystemAction.get(ShowAvailableChannelAction.class),
                SystemAction.get(ShowSelectedChannelAction.class)
            };
    }

    public Action[] getTopChannelFolderActions() {
        return new SystemAction[] {
           
                    SystemAction.get(RefreshPortletsAction.class),
                    SystemAction.get(AddChannelAction.class),
                 };
    }

    public boolean allowDragAndDrop() {
        return false;
    }
    
}
