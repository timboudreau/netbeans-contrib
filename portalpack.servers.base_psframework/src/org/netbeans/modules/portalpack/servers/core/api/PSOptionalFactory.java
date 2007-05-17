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

package org.netbeans.modules.portalpack.servers.core.api;

import org.netbeans.modules.j2ee.deployment.plugins.spi.AntDeploymentProvider;
import org.netbeans.modules.portalpack.servers.core.*;
import org.netbeans.modules.portalpack.servers.core.common.NetbeansServerConstant;
import org.netbeans.modules.portalpack.servers.core.common.NetbeansServerType;
import org.netbeans.modules.portalpack.servers.core.impl.PSAntDeploymentProviderImpl;
import org.netbeans.modules.portalpack.servers.core.ui.PSInstantiatingIterator;
import javax.enterprise.deploy.spi.DeploymentManager;
import org.netbeans.modules.j2ee.deployment.plugins.spi.FindJSPServlet;
import org.netbeans.modules.j2ee.deployment.plugins.spi.IncrementalDeployment;
import org.netbeans.modules.j2ee.deployment.plugins.spi.OptionalDeploymentManagerFactory;
import org.netbeans.modules.j2ee.deployment.plugins.spi.StartServer;
import org.openide.WizardDescriptor.InstantiatingIterator;

/**
 *
 * @author Satya
 */
public abstract class PSOptionalFactory extends OptionalDeploymentManagerFactory {
    
    public StartServer getStartServer(DeploymentManager dm) {
        return new PSStartServer(dm);
    }
    
    public IncrementalDeployment getIncrementalDeployment(DeploymentManager dm) {
        return null;
    }
    
    public FindJSPServlet getFindJSPServlet(DeploymentManager dm) {
        return null;
    }
    
    public InstantiatingIterator getAddInstanceIterator() {
        return new PSInstantiatingIterator(getPSVersion(),getURIPrefix(),getPSConfigPanelManager());
    }
    
    public AntDeploymentProvider getAntDeploymentProvider(DeploymentManager dm) {
        return new PSAntDeploymentProviderImpl((PSDeploymentManager)dm);
    }
    public abstract PSConfigPanelManager getPSConfigPanelManager();
    public abstract String getPSVersion();
    public abstract String getURIPrefix();
}
