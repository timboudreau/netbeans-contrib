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
package org.netbeans.modules.portalpack.servers.jnpc;

import javax.enterprise.deploy.spi.DeploymentManager;
import org.netbeans.modules.j2ee.deployment.plugins.spi.FindJSPServlet;
import org.netbeans.modules.portalpack.servers.core.api.PSConfigPanelManager;
import org.netbeans.modules.portalpack.servers.core.api.PSDeploymentManager;
import org.netbeans.modules.portalpack.servers.core.api.PSOptionalFactory;
import org.netbeans.modules.portalpack.servers.jnpc.common.JNPCConstants;

/**
 *
 * @author root
 */
public class JNPCOptionalFactory extends PSOptionalFactory {

    public PSConfigPanelManager getPSConfigPanelManager() {
        return new JNPCConfigPanelManagerImpl();
    }

    public String getURIPrefix() {
        return JNPCConstants.OS_PC_1_0_URI_PREFIX;
    }

    public String getPSVersion() {
        return JNPCConstants.OS_PC_1_0;
    }

    @Override
    public FindJSPServlet getFindJSPServlet(DeploymentManager dm) {
        return ((PSDeploymentManager) dm).getStartServerHandler().getFindJSPServlet((PSDeploymentManager) dm);
    }
}
