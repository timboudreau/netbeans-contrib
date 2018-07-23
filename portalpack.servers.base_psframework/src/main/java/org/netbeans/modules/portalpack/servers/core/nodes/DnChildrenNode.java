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

import java.util.ArrayList;
import org.netbeans.modules.portalpack.servers.core.api.PSDeploymentManager;
import org.netbeans.modules.portalpack.servers.core.common.enterprise.NodeTypeConstants;
import org.netbeans.modules.portalpack.servers.core.util.NetbeanConstants;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.modules.portalpack.servers.core.api.PSTaskHandler;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;

/**
 * @author Satya
 */
public class DnChildrenNode extends Children.Keys {
    
    private static Logger logger = Logger.getLogger(NetbeanConstants.PORTAL_LOGGER);
    private static final String WAIT_NODE = "wait_node"; //NOI18N
    private static String WAIT_ICON_BASE = "org/netbeans/modules/portalpack/servers/core/resources/wait.gif";
    private Lookup lookup;
    private String type;
    private java.util.Map nodeDataMap;
    private String baseDN;
    private PSDeploymentManager manager;
    
    DnChildrenNode(PSDeploymentManager manager,Lookup lkp,String type, String baseDN) {
        lookup = lkp;
        this.type = type;
        this.nodeDataMap = new HashMap();
        this.baseDN = baseDN;
        this.manager = manager;
        logger.log(Level.FINEST,"Setting base DN to ::: "+baseDN);
    }
    
    DnChildrenNode(Lookup lkp,String type, String baseDN) {
        lookup = lkp;
        this.type = type;
        this.nodeDataMap = new HashMap();
        this.baseDN = baseDN;
        
        manager = (PSDeploymentManager)lkp.lookup(PSDeploymentManager.class);
        logger.log(Level.FINEST,"Setting base DN to ::: "+baseDN);
    }
    
    public void updateKeys(){
        
        if(manager != null && !manager.getPSNodeConfiguration().showDnNodes())
            return;
        
        TreeSet ts = new TreeSet();
        ts.add(WAIT_NODE);
        
        setKeys(ts);
        
        RequestProcessor.getDefault().post(new Runnable() {
            public void run() {
                
                if(type.equals(NodeTypeConstants.ORGANIZATION)) {
                    if(baseDN == null || !baseDN.equals(NodeTypeConstants.GLOBAL)) {
                        PSDeploymentManager manager = (PSDeploymentManager)lookup.lookup(PSDeploymentManager.class);
                        PSTaskHandler  handler = manager.getTaskHandler();
                        
                        logger.log(Level.FINEST,"Base DN for this child node is :::::::::::::::::::: "+baseDN);
                        
                        if(nodeDataMap != null)
                            nodeDataMap.clear();
                        try{
                            nodeDataMap = handler.getObjects(NodeTypeConstants.ORGANIZATION,"*",baseDN);
                        }catch(Exception e){
                            return;
                        }
                       
                        if(nodeDataMap != null)
                        {
                            if(nodeDataMap.remove(baseDN) == null)
                            {
                                if(baseDN != null)
                                {
                                    Object ob = nodeDataMap.remove(baseDN.toLowerCase());
                                }
                            }
                        }
                    }
                    
                    logger.log(Level.FINEST,"Map :::: "+nodeDataMap);
                    ArrayList list = new ArrayList();
                    
                    if(baseDN== null || !baseDN.equals(NodeTypeConstants.GLOBAL)) {
                        Set set = nodeDataMap.keySet();
                        
                        for(Iterator it = set.iterator();it.hasNext();) {
                            list.add(it.next());
                        }  
                    }
                    
                    if(baseDN != null){
                        list.add("portlet_list");
                        
                        //check if providers are required to be shown
                        if(manager.getPSNodeConfiguration().showContainerNodes())
                            list.add("containers");
                        
                        if(manager.getPSNodeConfiguration().showTopChannelsNode())
                            list.add("topchannels");
                        
                        list.add("custom_node");
                    }else{
                        list.add(0,NodeTypeConstants.GLOBAL);
                        list.add("custom_node");
                    }         
                    setKeys(list);
                }
                
            }
        }, 0);
        
    }
    
    public Lookup getLookup() {
        return lookup;
    }
    
    protected String getMapValue(Object key) {
        return (String)nodeDataMap.get(key);
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
        if(key.equals("portlet_list")) {
            PortletHolderNode holderNode = new PortletHolderNode(lookup,baseDN);
            return new Node[]{holderNode};
        } else if (key.equals(NodeTypeConstants.GLOBAL)) {
            DnNode node = new DnNode(manager,lookup,(String)key,"global");
            return new Node[]{node};
        }else if(key.equals("containers")) {
            ContainersHolderNode holderNode = new ContainersHolderNode(manager,lookup,baseDN);
            return new Node[]{holderNode};
        }else if(key.equals("topchannels")){
            ChannelHolderNode holderNode= new ChannelHolderNode(manager,lookup,null,baseDN);   
            return new Node[]{holderNode};
        }else if(key.equals("custom_node")) {
            if(baseDN != null) {
                //added for custom nodes
                Node[] nd = manager.getPSNodeConfiguration().getCustomChildrenForDnNode(manager,baseDN,key.toString());
                if(nd != null && nd.length != 0) {
                    return nd;
                }
                return new Node[0];
            }else {
                Node[] nd = manager.getPSNodeConfiguration().getCustomChildrenForRootNode(manager,baseDN,key.toString());
                if(nd != null && nd.length != 0) {
                    return nd;
                }
                return new Node[0];
                
            }
        }
        
        if (key instanceof String){
            String value = getMapValue(key);
            DnNode node = new DnNode(manager,lookup,(String) key,value);
            return new Node[]{node};
        }
        return null;
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
    
    
    class RefreshDnChildren implements Node.Cookie {
        DnChildrenNode children;
        
        RefreshDnChildren(DnChildrenNode children){
            this.children = children;
        }
        
        public void refresh() {
            children.updateKeys();
        }
    }
    
}
