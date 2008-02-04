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

package org.netbeans.modules.portalpack.servers.sunps7;

import javax.enterprise.deploy.spi.DeploymentManager;
import org.netbeans.modules.portalpack.servers.core.api.PSDeploymentFactory;
import org.netbeans.modules.portalpack.servers.core.common.NetbeansServerConstant;
import javax.enterprise.deploy.shared.factories.DeploymentFactoryManager;
import javax.enterprise.deploy.spi.factories.DeploymentFactory;

/**
 *
 * @author Satya
 */
public class PS71DeploymentFactoryImpl extends PSDeploymentFactory{

   private static DeploymentFactory instance;

   public static synchronized DeploymentFactory create() {
        if (instance == null) {
            instance = new PS71DeploymentFactoryImpl();
            DeploymentFactoryManager.getInstance().registerDeploymentFactory(instance);
        }
        return instance;
    }

    public String getDisplayName() {
        return "Sun Java System Portal Server 7.0/7.1 (Updates)";
    }

    public String getURIPrefix() {
        return PS71ServerConstant.PS_71_URI_PREFIX;
    }

    public String getPSVersion()
    {
        return PS71ServerConstant.PS_71;
    }

    public DeploymentManager getPSDeploymentManager(String uri, String psVersion) {
        return new PS71DeploymentManager(uri,psVersion);
    }

}
