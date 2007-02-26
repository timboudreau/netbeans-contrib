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

package org.netbeans.modules.j2ee.oc4j.nodes;

import java.util.Iterator;
import java.util.Vector;
import javax.management.MBeanServerConnection;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import org.netbeans.modules.j2ee.oc4j.OC4JDeploymentManager;
import org.netbeans.modules.j2ee.oc4j.nodes.actions.Refreshable;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;

/**
 * It describes children nodes of the JDBC Resources node
 *
 * @author Michal Mocnak
 */
public class OC4JManagedDataSourcesChildren extends Children.Keys implements Refreshable {
    
    private Lookup lookup;
    private final static Node WAIT_NODE = OC4JItemNode.createWaitNode();
    
    OC4JManagedDataSourcesChildren(Lookup lookup) {
        this.lookup = lookup;
    }
    
    public void updateKeys(){
        setKeys(new Object[] {WAIT_NODE});
        
        RequestProcessor.getDefault().post(new Runnable() {
            Vector keys = new Vector();
            OC4JDeploymentManager dm = lookup.lookup(OC4JDeploymentManager.class);
            
            public void run() {
                
                try {
                    MBeanServerConnection server = dm.getJMXConnector();
                    Iterator i = server.queryMBeans(new ObjectName("oc4j:j2eeType=JDBCDataSource,*"), null).iterator();
                    
                    while(i.hasNext()) {
                        ObjectName elem = ((ObjectInstance) i.next()).getObjectName();
                        String pool = elem.getKeyProperty("JDBCResource").substring(1, elem.getKeyProperty("JDBCResource").length()-1);
                        
                        if(pool.length() == 0)
                            continue;
                        
                        String name = (String) server.getAttribute(elem, "dataSourceName");
                        String jndiName = (String) server.getAttribute(elem, "jndiName");
                        Node node = new OC4JItemNode(lookup, Children.LEAF, name, OC4JItemNode.ItemType.JDBC_MANAGED_DATASOURCES);
                        node.setShortDescription(jndiName + " -> " + NbBundle.getMessage(OC4JManagedDataSourcesChildren.class, "TXT_ConnectionPool")+pool);
                        
                        keys.add(node);
                    }
                    
                } catch(Exception ex) {
                    // Nothing to do
                }
                
                setKeys(keys);
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
        if (key instanceof OC4JItemNode){
            return new Node[]{(OC4JItemNode)key};
        }
        
        if (key instanceof String && key.equals(WAIT_NODE)){
            return new Node[]{WAIT_NODE};
        }
        
        return null;
    }
}