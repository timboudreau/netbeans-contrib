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
import org.netbeans.modules.portalpack.servers.core.api.PSTaskHandler;
import org.netbeans.modules.portalpack.servers.core.common.enterprise.NodeTypeConstants;
import org.netbeans.modules.portalpack.servers.core.util.NetbeanConstants;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;

/**
 *
 */
public class ChannelChildrenNode extends Children.Keys {
    
    private static Logger logger = Logger.getLogger(NetbeanConstants.PORTAL_LOGGER);
    private static final String WAIT_NODE = "wait_node"; //NOI18N
    private static String WAIT_ICON_BASE = "org/netbeans/modules/portalpack/servers/core/resources/wait.gif";
    
    private Lookup lookup;
    private String baseDN;
    private PSDeploymentManager manager;
   
    private String type;
    
    private String containerName;
    
    
    public static final String SELECTED_TYPE = "selected";
    public static final String EXISTING_TYPE = "existing";
    public static final String AVAILABLE_TYPE = "available";
    public static final String TOP_CHANNELS = "topchannels";
    
    private volatile String channelType = SELECTED_TYPE;
    
    ChannelChildrenNode(PSDeploymentManager manager,Lookup lkp,String type,String containerName, String baseDN) {
        lookup = lkp;
        this.baseDN = baseDN;
        this.manager = manager;
        this.type = type;
        this.containerName = containerName;
        
    }
    
    
   public void setChannelFilterType(String filter)
   {
       channelType = filter;
   }
   
   public String getChannelFilterType()
   {
        return channelType;
   }
    public synchronized void updateKeys(){
        TreeSet ts = new TreeSet();
        
        if(type != null && !type.equals(NodeTypeConstants.CONTAINER_NODE))
            ts.add(WAIT_NODE);
        
        setKeys(ts);
        
        RequestProcessor.getDefault().post(new Runnable() {
            public void run() {
                
                    PSDeploymentManager manager = (PSDeploymentManager)lookup.lookup(PSDeploymentManager.class);
                    PSTaskHandler handler = manager.getTaskHandler();
                    
                    logger.log(Level.FINEST,"Base DN for this child node is ::"+baseDN);
                    
                    if(containerName == null)
                    {
                        Set set = null;
                        channelType = TOP_CHANNELS;
                        try {
                            set = handler.getExistingChannels(baseDN,new Boolean(true));
                        } catch (Exception e) {
                            logger.log(Level.SEVERE,"Error",e);
                            return;
                        }
                        
                         if(set == null)
                        {
                            ArrayList customList = new ArrayList();
                            customList.add("top_channels_custom_node");
                            setKeys(customList);
                            return;
                        }
                        
                        List list = new ArrayList();
                        //remove all parent channels as adding those channel will cause deadlock
                        for(Iterator it=set.iterator();it.hasNext();)
                        {
                           String temp = (String)it.next();
                           list.add(temp);
                           
                        }
                    
                        list.add("top_channels_custom_node");
                        setKeys(list);
                        return;
                        
                    }
                    if(channelType.equals(EXISTING_TYPE))
                    {
                        Set set = null;
                        try{
                            set = handler.getAssignableChannels(baseDN,containerName);
                            
                        }catch(Exception e){
                            logger.log(Level.SEVERE,"Error",e);
                            return;
                        }
                        
                        if(set == null)
                        {
                            ArrayList customList = new ArrayList();
                            customList.add("custom_node");
                            setKeys(customList);
                            return;
                        }
                        
                        List list = new ArrayList();
                        //remove all parent channels as adding those channel will cause deadlock
                        for(Iterator it=set.iterator();it.hasNext();)
                        {
                           String temp = (String)it.next();
                           if(temp == null || containerName == null)
                               continue;
                           if(containerName.startsWith(temp))
                           {
                               //logger.log("Container: "+containerName+"           temp: "+temp);
                               continue;
                               
                           }
                           list.add(temp);
                           
                        }
                    
                        list.add("custom_node");
                        setKeys(list);
                        
                    } else if(channelType.equals(AVAILABLE_TYPE))
                    {
                                    
                        List availList = null;
                        try{
                            availList = handler.getAvailableChannels(baseDN,containerName);
                        }catch(Exception e){
                            logger.log(Level.SEVERE,"Error",e);
                            return;
                        }
                    
                        if(availList == null)
                        {
                            ArrayList customList = new ArrayList();
                            customList.add("custom_node");
                            setKeys(customList);
                            return;
                        }
                        availList.add("custom_node");
                        setKeys(availList);
                    
                    }
                    else //selected
                    {
                        List selList = null;
                        try{
                            selList = handler.getSelectedChannels(baseDN,containerName);
                        }catch(Exception e){
                            logger.log(Level.SEVERE,"Error",e);
                            return;
                        }
                    
                        if(selList == null)
                        {
                            ArrayList customList = new ArrayList();
                            customList.add("custom_node");
                            setKeys(customList);
                            return;
                        }
                        selList.add("custom_node");
                        setKeys(selList);
                    }
            }
        }, 0);
        
    }
    
    public String getContainerName()
    {
        return containerName;
    }
   
    public Lookup getLookup() {
        return lookup;
    }
    
    protected void addNotify() {
        updateKeys();
    }
    
    protected void removeNotify() {
        setKeys(java.util.Collections.EMPTY_SET);
    }
    
    protected org.openide.nodes.Node[] createNodes(Object key) {
     
       if(key.equals(WAIT_NODE)){
          return new Node[]{createWaitNode()};
       }
       
       if(key.equals("custom_node"))
       {
            Node[] nds = manager.getPSNodeConfiguration().getCustomChildrenForChannelNode(manager,baseDN,(String)key);
            if(nds != null && nds.length != 0)
            {
               return nds;
            }
            return new Node[0];
       }else if(key.equals("top_channels_custom_node")){
            Node[] nds = manager.getPSNodeConfiguration().getCustomChildrenForTopChannelsNode(manager,baseDN,(String)key);
            if(nds != null && nds.length != 0)
            {
               return nds;
            }
            return new Node[0];
       }
       
      ChannelNode node = new ChannelNode(this,manager,lookup,(String)key,getDisplayValue((String)key),baseDN);
      
      return new Node[]{node};
    }
    
    public String getChannelType()
    {
        return channelType;
    }
    
    protected String getDisplayValue(String key)
    {
        int index = key.indexOf("/");
        if(index == -1)
            return key;
        
        index = key.lastIndexOf("/");
        
        if(index < key.length())
        {
            key = key.substring(index + 1);
            return key;
        }
        
        return key;
        
    }
      
    /* Creates and returns the instance of the node
     * representing the status 'WAIT' of the node.
     * It is used when it spent more time to create elements hierarchy.
     * @return the wait node.
     */
    private Node createWaitNode() {
        AbstractNode n = new AbstractNode(Children.LEAF);
        n.setName(NbBundle.getMessage(DnChildrenNode.class, "LBL_WaitNode_DisplayName")); //NOI18N
        n.setIconBaseWithExtension(WAIT_ICON_BASE); // NOI18N
        return n;
    }    
}
