/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

/*
 * WS70WebModulesChildren.java
 *
 */

package org.netbeans.modules.j2ee.sun.ws7.nodes;

import javax.enterprise.deploy.spi.DeploymentManager;
import javax.enterprise.deploy.spi.Target;
import javax.enterprise.deploy.spi.TargetModuleID;

import javax.enterprise.deploy.shared.ModuleType;

import org.openide.util.Lookup;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Node;
import org.openide.nodes.Children;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;

import java.util.TreeSet;


/**
 *
 * @author Administrator
 */
public class WS70WebModulesChildren extends Children.Keys{
    private static final String WAIT_NODE = "wait_node"; //NOI18N
    private Lookup lookup;
    /** Creates a new instance of WS70WebModulesChildren */
    public WS70WebModulesChildren(Lookup lookup) {
        this.lookup = lookup;
    }
    
    protected Node[] createNodes(Object key){
        if (key instanceof WS70WebModule){
            WS70WebModule module = (WS70WebModule)key;
            WS70WebModuleNode node = new WS70WebModuleNode(module);            
            return new Node[]{node};
        }
        if (key instanceof String && key.equals(WAIT_NODE)){
            return new Node[]{createWaitNode ()};
        }
        return null;
    }
    
    public void updateKeys(){        
        Target target = (Target)lookup.lookup(Target.class);
        DeploymentManager dm = (DeploymentManager)lookup.lookup(DeploymentManager.class);        
        TreeSet ts = new TreeSet();
        ts.add(WAIT_NODE);       
        setKeys(ts); 
        RequestProcessor.getDefault().post(new Runnable() {
            public void run () {
                DeploymentManager manager = (DeploymentManager)lookup.lookup(DeploymentManager.class);
                Target target = (Target)lookup.lookup(Target.class);                        
                TreeSet list = new TreeSet(new WS70WmComparator()); 
                if (target != null){

                    try{
                        TargetModuleID[] modules = manager.getAvailableModules(ModuleType.WAR, new Target[]{target}); 
                         for (int i = 0; i < modules.length; i ++){
                             list.add(new WS70WebModule(manager, modules[i]));
                         }
                       }
                    
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }       
               setKeys(list);
       
            }
        }, 0);
    }
    
    protected void addNotify() {
        updateKeys();
    }
   
    protected void removeNotify() {
        setKeys(java.util.Collections.EMPTY_SET);
    }    
    private Node createWaitNode () {
        AbstractNode n = new AbstractNode(Children.LEAF);
        n.setName(NbBundle.getMessage(WS70WebModulesChildren.class, "LBL_WaitNode_DisplayName")); //NOI18N
        n.setIconBaseWithExtension("org/openide/src/resources/wait.gif"); // NOI18N
        return n;
    } 
    public static class WS70WmComparator implements java.util.Comparator{
        
       public int compare(Object o1, Object o2) {
            WS70WebModule wm1 = (WS70WebModule) o1;
            WS70WebModule wm2 = (WS70WebModule) o2;            
            return wm1.getName().compareTo(wm2.getName());
        }            
        
    }    

}

