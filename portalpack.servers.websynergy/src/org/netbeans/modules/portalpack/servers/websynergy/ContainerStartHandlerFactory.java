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

package org.netbeans.modules.portalpack.servers.websynergy;

import org.netbeans.modules.portalpack.servers.core.api.PSDeploymentManager;
import org.netbeans.modules.portalpack.servers.core.api.PSStartServerInf;
import org.netbeans.modules.portalpack.servers.core.common.ServerConstants;
import org.netbeans.modules.portalpack.servers.core.impl.DefaultStartServerImpl;
import org.netbeans.modules.portalpack.servers.core.impl.j2eeservers.sunappserver.SunAppServerStartServer;
import org.netbeans.modules.portalpack.servers.core.impl.j2eeservers.tomcat.TomcatStartServer;
import org.netbeans.modules.portalpack.servers.core.util.PSConfigObject;
import org.netbeans.modules.portalpack.servers.websynergy.impl.SunASStartStopListener;
import org.netbeans.modules.portalpack.servers.websynergy.impl.TomcatStartStopServerListener;

/**
 *
 * @author satya
 */
public class ContainerStartHandlerFactory implements ServerConstants{
    

    public static PSStartServerInf getStartServerHandler(PSDeploymentManager dm)
    {
        PSConfigObject psconfig = dm.getPSConfig();
        if(psconfig.getServerType() == null)
            return new DefaultStartServerImpl();
        
        if(psconfig.getServerType().equals(SUN_APP_SERVER_9))
        {
            PSStartServerInf startHandler = new SunAppServerStartServer(dm);
            startHandler.addListener(new SunASStartStopListener(dm));
            return startHandler;
        }
        else if(psconfig.getServerType().equals(TOMCAT_5_X))
        {
            PSStartServerInf startHandler = new TomcatStartServer(dm);
            startHandler.addListener(new TomcatStartStopServerListener(dm));
            return startHandler;
        }
        else
            return new DefaultStartServerImpl();
        
    }

    
}
