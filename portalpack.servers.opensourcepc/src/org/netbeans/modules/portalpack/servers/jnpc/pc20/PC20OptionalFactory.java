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

import javax.enterprise.deploy.spi.DeploymentManager;
import org.netbeans.modules.j2ee.deployment.plugins.spi.FindJSPServlet;
import org.netbeans.modules.portalpack.servers.core.api.PSConfigPanelManager;
import org.netbeans.modules.portalpack.servers.core.api.PSDeploymentManager;
import org.netbeans.modules.portalpack.servers.core.api.PSOptionalFactory;
import org.netbeans.modules.portalpack.servers.jnpc.JNPCConfigPanelManagerImpl;
import org.netbeans.modules.portalpack.servers.jnpc.common.JNPCConstants;

/**
 *
 * @author Satyaranjan
 */
public class PC20OptionalFactory extends PSOptionalFactory{

    public PC20OptionalFactory() {
    }

    public PSConfigPanelManager getPSConfigPanelManager() {
        return new JNPCConfigPanelManagerImpl();
    }

    public String getPSVersion() {
        return JNPCConstants.OP_PC_2_0;
    }

    public String getURIPrefix() {
        return JNPCConstants.OP_PC_2_0_URI_PREFIX;
    }
    
     public FindJSPServlet getFindJSPServlet(DeploymentManager dm) {
        return ((PSDeploymentManager)dm).getStartServerHandler().getFindJSPServlet((PSDeploymentManager)dm);
    }   

}
