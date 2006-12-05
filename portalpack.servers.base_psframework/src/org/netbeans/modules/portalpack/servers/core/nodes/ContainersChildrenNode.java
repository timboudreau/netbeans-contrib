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
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
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
 * @author Satya
 */
public class ContainersChildrenNode extends Children.Keys {
    
    private static Logger logger = Logger.getLogger(NetbeanConstants.PORTAL_LOGGER);
    
    private static final String WAIT_NODE = "wait_node"; //NOI18N
     private static String WAIT_ICON_BASE = "org/netbeans/modules/portalpack/servers/core/resources/wait.gif";
    private Lookup lookup;
    private String baseDN;
    private PSDeploymentManager manager;
    
    private TreeMapObj nodeDataMap;
    private String type;
    
    ContainersChildrenNode(PSDeploymentManager manager,Lookup lkp,String type, String baseDN) {
        lookup = lkp;
        this.baseDN = baseDN;
        this.manager = manager;
        this.type = type;
        this.nodeDataMap = new TreeMapObj(baseDN);
        logger.log(Level.FINEST,"Setting base DN to ::: "+baseDN);
    }
    
    ContainersChildrenNode(Lookup lkp,String type, String baseDN) {
        lookup = lkp;
        this.baseDN = baseDN;
        this.nodeDataMap = new TreeMapObj(baseDN);
        this.type = type;
        manager = (PSDeploymentManager)lkp.lookup(PSDeploymentManager.class);
        logger.log(Level.FINEST,"Setting base DN to ::: "+baseDN);
    }
    
    public void updateKeys(){
        TreeSet ts = new TreeSet();
        
        if(type != null && !type.equals(NodeTypeConstants.CONTAINER_NODE)){        
          ts.add(WAIT_NODE);
        }
        
        setKeys(ts);
        
        RequestProcessor.getDefault().post(new Runnable() {
            public void run() {
                
                //dont refresh when node type is provider node.
                if(type != null && type.equals(NodeTypeConstants.CONTAINER_NODE))
                    return;
                
                    PSDeploymentManager manager = (PSDeploymentManager)lookup.lookup(PSDeploymentManager.class);
                    PSTaskHandler handler = manager.getTaskHandler();
                    
                    logger.log(Level.FINEST,"Base DN for this child node is :: "+baseDN);
                    
                  
                    String[] values = null;
                    try{
                        values = handler.getExistingContainers(baseDN,true);
                        //nodeDataMap = handler.getExistingProvider();
                    }catch(Exception e){
                        return;
                    }
                    
                    if(values == null)
                    {
                        setKeys(Collections.EMPTY_LIST);
                        return;
                    }
                    
                    
                    if(nodeDataMap != null)
                        nodeDataMap.clear();
                    
                    TreeSet list = new TreeSet();
                                     
                        for(int k=0;k<values.length;k++) {
                             createTreeMap(nodeDataMap,values[k],"");
                             
                        }
                    
                    if(nodeDataMap == null || nodeDataMap.size() == 0)
                        return;
                    
                    Set set = nodeDataMap.keySet();
                    
                    for(Iterator it=set.iterator();it.hasNext();)
                    {
                        list.add(it.next());
                    }
                    if(nodeDataMap != null)
                        logger.log(Level.FINEST,"Node Values:::: "+nodeDataMap.toString());
                    
                    list.add("custom_node");
                    setKeys(list);
                
            }
        }, 0);
        
    }
  
    
    
    private  void createTreeMap(HashMap parentMap,String value,String parentPrefix)
    {
            if(parentPrefix.length() != 0)
                parentPrefix += "/";
            
            if(value.indexOf("/") == -1)
            {

                if(parentMap.get(parentPrefix+value) != null)
                    return;
                else
                    parentMap.put(parentPrefix+value,new TreeMapObj(value));

                return;
            }
            else{
                 int index = value.indexOf("/");
                 String parent = value.substring(0,index);
                 
                 String child = value.substring(index + 1);

                 HashMap childMap = (HashMap)parentMap.get(parentPrefix + parent);

                 if(childMap == null)
                 {
                     childMap = new TreeMapObj(value);
                     parentMap.put(parentPrefix + parent,childMap);
                 }else{


                 }
                  createTreeMap(childMap,child,parentPrefix + parent);
                 return;
            }

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
        return createNodes(nodeDataMap,key);
    }
    
     protected org.openide.nodes.Node[] createNodes(TreeMapObj map,Object key) {
         
            if(key.equals(WAIT_NODE))
                return new Node[]{createWaitNode()};
            
            if(key.equals("custom_node"))
            {
                 Node[] nds = manager.getPSNodeConfiguration().getCustomChildrenForContainerNode(manager,baseDN,(String)key);
                 if(nds != null && nds.length != 0)
                 {
                    return nds;
                 }
                 return new Node[0];
            }
            
            TreeMapObj childmap = (TreeMapObj)map.get(key);
            if(childmap == null || childmap.size() ==0)
            {
                  ContainerNode node = new ContainerNode(manager,lookup,(String) key,(String)childmap.getValue(),baseDN);
                  
                  ChannelHolderNode channelHolderNode = new ChannelHolderNode(manager,lookup,(String)key,baseDN);
                  node.getChildren().add(new Node[]{channelHolderNode});
                 return new Node[]{node};
            }else{
                ContainerNode node = new ContainerNode(manager,lookup,(String) key,(String)childmap.getValue(),baseDN);
                
                Set keys = childmap.keySet();
                for(Iterator it=keys.iterator();it.hasNext();)
                {
                    node.getChildren().add(createNodes(childmap,it.next()));
                }
                
                ChannelHolderNode channelHolderNode = new ChannelHolderNode(manager,lookup,(String)key,baseDN);
                node.getChildren().add(new Node[]{channelHolderNode});
                return new Node[]{node};
            }

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
    
    class TreeMapObj extends HashMap
    {
        private String value;
        
        public TreeMapObj(String value)
        {
            this.value = value;
        }
        
        public String getValue()
        {
            return value;
        }
    }
    
}
