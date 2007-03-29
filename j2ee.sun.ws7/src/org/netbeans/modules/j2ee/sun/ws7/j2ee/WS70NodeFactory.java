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

package org.netbeans.modules.j2ee.sun.ws7.j2ee;

import org.netbeans.modules.j2ee.sun.ws7.dm.WS70SunDeploymentFactory;

import javax.enterprise.deploy.spi.factories.DeploymentFactory;
import javax.enterprise.deploy.spi.DeploymentManager;
import javax.enterprise.deploy.spi.Target;

import org.netbeans.modules.j2ee.deployment.plugins.spi.RegistryNodeFactory;
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
