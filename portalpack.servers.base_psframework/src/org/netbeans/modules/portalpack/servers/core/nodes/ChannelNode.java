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

package org.netbeans.modules.portalpack.servers.core.nodes;

import org.netbeans.modules.portalpack.servers.core.api.PSDeploymentManager;
import org.netbeans.modules.portalpack.servers.core.common.enterprise.NodeTypeConstants;
import org.netbeans.modules.portalpack.servers.core.nodes.actions.AddChannelToSelected;
import org.netbeans.modules.portalpack.servers.core.nodes.actions.AddPortletAction;
import org.netbeans.modules.portalpack.servers.core.nodes.actions.CreateContainerAction;
import org.netbeans.modules.portalpack.servers.core.nodes.actions.DeleteChannelFromSelected;
import org.netbeans.modules.portalpack.servers.core.nodes.actions.DeleteContainerAction;
import org.netbeans.modules.portalpack.servers.core.nodes.actions.ShowAdminToolAction;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.actions.SystemAction;

/**
 *
 * @author Satya
 */
public class ChannelNode extends BaseNode implements Node.Cookie {
    
    private static String ICON_BASE = "org/netbeans/modules/portalpack/servers/core/resources/channel.gif"; // NOI18N
    private static String REF_ICON_BASE = "org/netbeans/modules/portalpack/servers/core/resources/channel-ref.gif"; // NOI18N
    private String key = "";
    private String displayValue = "";
    private PSDeploymentManager manager;
    private String baseDn = "";
    private String container = null;
    private ChannelChildrenNode ccNode;
    
    public ChannelNode()
    {
       super(new Children.Array());   
    }
    public ChannelNode(ChannelChildrenNode ccNode,PSDeploymentManager manager, Lookup lookup,String key,String displayValue,String baseDn) {
        super(Children.LEAF);
        this.key = key;
        this.displayValue = displayValue;
        this.manager = manager;
        this.baseDn = baseDn;
        this.container = container;
        this.ccNode = ccNode;
        
        getCookieSet().add(this);
        if(manager != null)
        {
            if(ccNode.getChannelFilterType().equals(ChannelChildrenNode.TOP_CHANNELS))
                setIconBaseWithExtension(ICON_BASE);
            else
                setIconBaseWithExtension(REF_ICON_BASE);
        }
        else
            setIconBaseWithExtension(ICON_BASE);
        
        setDisplayName(displayValue);
        setShortDescription(getShortDescription());
        getCookieSet().add(this);
    }
        
    public PSDeploymentManager getDeploymentManager()
    {
        return manager;
    }
    
    public String getShortDescription() {
        return key; // NOI18N
    }
    
    public String getDn()
    {
        return baseDn;
    }
    
    public String getParentKey()
    {
        if(key.length() <= displayValue.length())
            return null;
        else if(key.indexOf(displayValue) != -1)
        {
            int index = key.lastIndexOf(displayValue);
            
            if(index == 0)
                return key;
            
            String parent = key.substring(0,index-1);
            return parent;           
        }
        return null;
    }
    
    public String getValue(){
        return displayValue;
    }
    
    public javax.swing.Action[] getActions(boolean context) {
   
        if(manager != null)
        {
            if(ccNode.getChannelFilterType().equals(ChannelChildrenNode.TOP_CHANNELS))
                return manager.getPSNodeConfiguration().getTopChannelsActions();
            else
                return manager.getPSNodeConfiguration().getChannelActions(ccNode.getChannelFilterType());
        }
        javax.swing.Action[]  newActions = new javax.swing.Action[1] ;
        newActions[0]=(null);
        
        return newActions;
    }
    
    public boolean hasCustomizer() {
        return true;
    }

    public String getKey() {
        return key;
    }

    public String getType() {
        return NodeTypeConstants.CHANNEL;
    }
    
    public ChannelChildrenNode getParentChannelChildrenNode()
    {
        return ccNode;
    }
}
