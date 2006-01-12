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

package org.netbeans.modules.j2ee.sun.ws7.j2ee;

import org.netbeans.modules.j2ee.sun.ws7.dm.WS70SunDeploymentFactory;

import javax.enterprise.deploy.spi.factories.DeploymentFactory;
import javax.enterprise.deploy.spi.DeploymentManager;
import javax.enterprise.deploy.spi.Target;

import org.netbeans.modules.j2ee.deployment.plugins.api.RegistryNodeFactory;
import org.netbeans.modules.j2ee.sun.ws7.nodes.WS70ManagerNode;
import org.netbeans.modules.j2ee.sun.ws7.nodes.WS70TargetNode;

import org.openide.ErrorManager;
import org.openide.nodes.Node;
import org.openide.util.Lookup;

import org.openide.util.HelpCtx;


public class WS70NodeFactory implements RegistryNodeFactory {    

    /**
     * Return node representing the admin server.  
     * @param lookup will contain DeploymentFactory,
     * DeploymentManager, Management objects.
     * @return admin server node.
     */
    public Node getManagerNode(Lookup lookup) {                
        DeploymentManager depManager = (DeploymentManager)lookup.
            lookup(DeploymentManager.class);
        DeploymentFactory fac = (DeploymentFactory)lookup.lookup(DeploymentFactory.class);
        return new WS70ManagerNode(depManager);
    }


    /**
     * Provide node representing JSR88 Target object.  
     * @param lookup will contain DeploymentFactory,
     * DeploymentManager, Target, Management objects.
     * @return target server node
     */
    public Node getTargetNode(Lookup lookup){
        Target target = (Target)lookup.lookup(Target.class);
        DeploymentManager depManager = (DeploymentManager)lookup.
            lookup(DeploymentManager.class);
        DeploymentFactory fac = (DeploymentFactory)lookup.lookup(DeploymentFactory.class);        
        return new WS70TargetNode(lookup);
    }    
}
