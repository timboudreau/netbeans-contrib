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

/*
 * WS70ResourceChildren.java
 */

package org.netbeans.modules.j2ee.sun.ws7.nodes;
import javax.enterprise.deploy.spi.DeploymentManager;
import javax.enterprise.deploy.spi.Target;
import javax.enterprise.deploy.spi.TargetModuleID;

import javax.enterprise.deploy.shared.ModuleType;
import org.netbeans.modules.j2ee.sun.ws7.Constants;

import org.openide.util.Lookup;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Node;
import org.openide.nodes.Children;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;

import org.netbeans.modules.j2ee.sun.ws7.dm.WS70SunDeploymentManager;
import java.util.TreeSet;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.lang.reflect.Method;

import org.netbeans.modules.j2ee.sun.ws7.j2ee.ResourceType;
import org.netbeans.modules.j2ee.sun.ws7.ide.editors.TaggedValue;

/**
 *
 * @author Administrator
 */
public class WS70ResourceChildren extends Children.Keys implements Node.Cookie{
    public final String WAIT_NODE = "wait_node"; //NOI18N
    private Lookup lookup;
    private ResourceType resType;
    /** Creates a new instance of WS70ResourceChildren */
    public WS70ResourceChildren(Lookup lookup, ResourceType resType) {
        this.lookup = lookup;
        this.resType = resType;
    }
    
    protected Node[] createNodes(Object key){
        if (key instanceof WS70Resource){
            WS70Resource module = (WS70Resource)key;
            WS70ResourceNode node = new WS70ResourceNode(module);            
            return new Node[]{node};
        }
        if (key instanceof String && key.equals(WAIT_NODE)){
            return new Node[]{createWaitNode ()};
        }
        return null;
    }
    protected void addNotify() {
        updateKeys();
    }
   
    protected void removeNotify() {
        setKeys(java.util.Collections.EMPTY_SET);
    }    
    private Node createWaitNode () {
        AbstractNode n = new AbstractNode(Children.LEAF);
        n.setName(NbBundle.getMessage(WS70ResourceChildren.class, "LBL_WaitNode_DisplayName")); //NOI18N
        n.setIconBaseWithExtension("org/openide/src/resources/wait.gif"); // NOI18N
        return n;
    }

    public void updateKeys(){
        TreeSet ts = new TreeSet();
        ts.add(WAIT_NODE);       
        setKeys(ts); 
        RequestProcessor.getDefault().post(new Runnable() {
            public void run () {
                DeploymentManager manager = (DeploymentManager)lookup.lookup(DeploymentManager.class);
                Target target = (Target)lookup.lookup(Target.class); 
                String configName = null;
                try{
                    Method getConfigName = target.getClass().getDeclaredMethod("getConfigName", new Class[]{});
                    configName = (String)getConfigName.invoke(target, new Object[]{});            

                }catch(Exception ex){
                    ex.printStackTrace();
                    return;
                }                                        
                TreeSet list = new TreeSet(new WS70ResourceComparator()); 
                if (target != null){
                    List resources = null;
                    try{
                        resources = ((WS70SunDeploymentManager)manager).getResources(resType, configName);
                        Object[] res = resources.toArray();                       
                        for (int i = 0; i < res.length; i ++){
                            WS70Resource resource = null;                         
                            resource = new WS70Resource(manager, configName, (HashMap)res[i], resType);                            
                            HashMap properties = null;
                            // START- FIX issue# 89106. mail-resource now also has property element in it.
                            //if(!resType.eqauls(ResourceType.MAIL)){
                                properties = (HashMap) ((WS70SunDeploymentManager)manager).getUserResourceProps
                                                            (configName, resType.toString(), resource.getJndiName(), Constants.RES_PROPERTY);    
                            //}
                            // END-FIX issue# 89106.
                            
                            if(properties!=null){
                                resource.setProperties(Constants.RES_PROPERTY, properties);
                            }
                            if(resType.eqauls(ResourceType.JDBC)){
                                HashMap conn_lease_properties = (HashMap) ((WS70SunDeploymentManager)manager).getUserResourceProps
                                                                (configName, Constants.JDBC_RESOURCE, resource.getJndiName(), Constants.JDBC_RES_CONN_LEASE_PROPERTY);
                                if(conn_lease_properties!=null){
                                    resource.setProperties(Constants.JDBC_RES_CONN_LEASE_PROPERTY, conn_lease_properties);
                                }

                                HashMap conn_creation_properties = (HashMap) ((WS70SunDeploymentManager)manager).getUserResourceProps
                                                                (configName, Constants.JDBC_RESOURCE, resource.getJndiName(), Constants.JDBC_RES_CONN_CREATION_PROPERTY);
                                if(conn_creation_properties!=null){
                                    resource.setProperties(Constants.JDBC_RES_CONN_CREATION_PROPERTY, conn_creation_properties);
                                }
                            }
                            list.add(resource);
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

    public static class WS70ResourceComparator implements java.util.Comparator{
        
       public int compare(Object o1, Object o2) {
            WS70Resource res1 = (WS70Resource) o1;
            WS70Resource res2 = (WS70Resource) o2;            
            return res1.getJndiName().compareTo(res2.getJndiName());
        }            
        
    }    

}
