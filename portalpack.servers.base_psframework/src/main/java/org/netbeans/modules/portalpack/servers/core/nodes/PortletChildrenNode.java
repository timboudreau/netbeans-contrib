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
import java.util.HashMap;
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
public class PortletChildrenNode extends Children.Keys {
    
    private static Logger logger = Logger.getLogger(NetbeanConstants.PORTAL_LOGGER);
    private static final String WAIT_NODE = "wait_node"; //NOI18N
    private static String WAIT_ICON_BASE = "org/netbeans/modules/portalpack/servers/core/resources/wait.gif";
    private Lookup lookup;
    private String type;
    private java.util.Map nodeDataMap;
    private String baseDN;
    private PSDeploymentManager dm;
    
    
    PortletChildrenNode(Lookup lkp,String type, String dn) {
        lookup = lkp;
        this.type = type;
        this.nodeDataMap = new HashMap();
        this.baseDN = dn;
        logger.log(Level.FINEST,"Setting base DN to ::: "+baseDN);
        dm = (PSDeploymentManager) lkp.lookup(PSDeploymentManager.class);       
    }
    
    public void updateKeys(){
       TreeSet ts = new TreeSet();
       ts.add(WAIT_NODE);
       
       setKeys(ts); 
       
        RequestProcessor.getDefault().post(new Runnable() {
            public void run () {
              
                if(type.equals(NodeTypeConstants.PORTLET))
                {
                    PSDeploymentManager manager = (PSDeploymentManager)lookup.lookup(PSDeploymentManager.class);
                    PSTaskHandler handler = manager.getTaskHandler();
                    String[] portlets = handler.getPortlets(baseDN);
                    TreeSet list = new TreeSet();
                    for(int i=0;i<portlets.length;i++)
                    {
                        list.add(portlets[i]);
                    }
                    
                    list.add("custom_node");
                    setKeys(list);
                    
                }
       
            }
        }, 0);
       
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
        if (key instanceof String){
            
            if (key.equals(WAIT_NODE))
                return new Node[]{createWaitNode ()};
            
            if(key.equals("custom_node"))
            {
                 Node[] nds = dm.getPSNodeConfiguration().getCustomChildrenForPortletNode(dm,baseDN,(String)key);
                 if(nds != null && nds.length != 0)
                 {
                    return nds;
                 }
                 return new Node[0];
            }
                
            PortletNode node = new PortletNode(dm,(String) key,baseDN);
            return new Node[]{node};
        }
        return null;
    }
    
    /* Creates and returns the instance of the node
    * representing the status 'WAIT' of the node.
    * It is used when it spent more time to create elements hierarchy.
    * @return the wait node.
    */
    private Node createWaitNode () {
        AbstractNode n = new AbstractNode(Children.LEAF);
        n.setName(NbBundle.getMessage(DnChildrenNode.class, "LBL_WaitNode_DisplayName")); //NOI18N
        n.setIconBaseWithExtension(WAIT_ICON_BASE); // NOI18N
        return n;
    }
    
    
    class RefreshPortletChildren implements Node.Cookie {
        PortletChildrenNode children;
        RefreshPortletChildren(PortletChildrenNode children){
            this.children = children;
        }

        public void refresh() {
            children.updateKeys();
        }
    }

}
