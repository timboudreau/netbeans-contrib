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

import org.netbeans.modules.portalpack.servers.core.PSJ2eePlatformImpl;
import org.netbeans.modules.portalpack.servers.core.api.PSDeploymentManager;
import org.netbeans.modules.portalpack.servers.core.api.PSConfigPanelManager;
import org.netbeans.modules.portalpack.servers.core.api.PSNodeConfiguration;
import org.netbeans.modules.portalpack.servers.core.api.PSTaskHandler;
import org.netbeans.modules.portalpack.servers.core.util.PSConfigObject;
import org.netbeans.modules.portalpack.servers.sunps7.impl.PSCommandHandler;

/**
 *
 * @author Satya
 */
public class PS71DeploymentManager extends PSDeploymentManager{

    private PSTaskHandler taskHandler;
    public PS71DeploymentManager(String uri,String psVersion){
        super(uri,psVersion);
    }

    public PSTaskHandler getTaskHandler() {
        
        if(taskHandler == null)
        {
           synchronized(this)
           {
               if(taskHandler == null)
                    taskHandler = new PSCommandHandler(getUri());
           }
        }
        return taskHandler;
    }

    public PSConfigPanelManager getPSConfigPanelManager() {
        return new PS71ConfigPanelManagerImpl();
    }

    public PSNodeConfiguration getPSNodeConfiguration()
    {
       return PS71NodeConfiguration.getInstance();
    }

    public PSJ2eePlatformImpl createPSJ2eePlatformImpl(PSConfigObject psconfig) {
          return new PS71J2eePlatformImpl(psconfig);
    }

}
