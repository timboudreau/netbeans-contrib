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

package org.netbeans.modules.portalpack.servers.websynergy.nodes;

import org.netbeans.modules.portalpack.servers.core.api.PSDeploymentManager;
import org.netbeans.modules.portalpack.servers.core.api.PSTaskHandler;
import org.netbeans.modules.portalpack.servers.core.util.NetbeanConstants;
import java.util.HashMap;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.modules.portalpack.servers.websynergy.impl.LiferayTaskHandler;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;

/**
 * @author Satya
 */
public class HookChildrenNode extends Children.Keys {
    
    private static Logger logger = Logger.getLogger(NetbeanConstants.PORTAL_LOGGER);
    private static final String WAIT_NODE = "wait_node"; //NOI18N
    private static String WAIT_ICON_BASE = "org/netbeans/modules/portalpack/servers/websynergy/resources/wait.gif";
    private String type;
    private java.util.Map nodeDataMap;
    private String baseDN;
    private PSDeploymentManager dm;
    
    
    HookChildrenNode(PSDeploymentManager dm, String type, String dn) {
        this.type = type;
        this.nodeDataMap = new HashMap();
        this.baseDN = dn;
        logger.log(Level.FINEST,"Setting base DN to ::: "+baseDN);
        this.dm = dm;
    }
    
    public void updateKeys(){
       TreeSet ts = new TreeSet();
       ts.add(WAIT_NODE);
       
       setKeys(ts); 
       
        RequestProcessor.getDefault().post(new Runnable() {
            public void run () {
              
                if(type.equals(LiferayNodeConstants.HOOK_NODE_TYPE))
                {
                    
                    PSTaskHandler handler = dm.getTaskHandler();
                    if(handler instanceof LiferayTaskHandler) {
                        String[] hooks = ((LiferayTaskHandler)handler).getHooks();
                        TreeSet list = new TreeSet();
                        for(int i=0;i<hooks.length;i++)
                        {
                            list.add(hooks[i]);
                        }
                        setKeys(list);

                    }
                    
                }
       
            }
        }, 0);
       
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
            
                
            HookNode node = new HookNode(dm,(String) key,baseDN);
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
        n.setName(NbBundle.getMessage(HookChildrenNode.class, "LBL_WaitNode_DisplayName")); //NOI18N
        n.setIconBaseWithExtension(WAIT_ICON_BASE); // NOI18N
        return n;
    }
    
    
    class RefreshHookChildren implements Node.Cookie {
        HookChildrenNode children;
        RefreshHookChildren(HookChildrenNode children){
            this.children = children;
        }

        public void refresh() {
            children.updateKeys();
        }
    }

}
