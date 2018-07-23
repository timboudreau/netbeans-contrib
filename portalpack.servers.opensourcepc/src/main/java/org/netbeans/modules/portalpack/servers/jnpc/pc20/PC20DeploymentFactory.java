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
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */
package org.netbeans.modules.portalpack.servers.jnpc.pc20;

import javax.enterprise.deploy.shared.factories.DeploymentFactoryManager;
import javax.enterprise.deploy.spi.factories.DeploymentFactory;
import org.netbeans.modules.portalpack.servers.jnpc.JNPCDeploymentFactory;
import org.netbeans.modules.portalpack.servers.jnpc.common.JNPCConstants;

/**
 *
 * @author Satyaranjan
 */
public class PC20DeploymentFactory extends JNPCDeploymentFactory {

    private static DeploymentFactory instance;  
     public static synchronized DeploymentFactory create() {
        if (instance == null) {
            instance = new PC20DeploymentFactory();
            DeploymentFactoryManager.getInstance().registerDeploymentFactory(instance);
        }
        return instance;
    }
    @Override
    public String getDisplayName() {
        return org.openide.util.NbBundle.getMessage(PC20DeploymentFactory.class, "Open_Portal_Portlet_Container_2_0");
    }
 
    @Override
    public String getURIPrefix() {
        return JNPCConstants.OP_PC_2_0_URI_PREFIX;
    }

    @Override
    public String getPSVersion() {
        return JNPCConstants.OP_PC_2_0;
    }

}
