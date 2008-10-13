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

import java.io.File;
import java.io.IOException;
import org.netbeans.modules.portalpack.servers.core.PSJ2eePlatformImpl;
import org.netbeans.modules.portalpack.servers.core.api.PSConfigPanelManager;
import org.netbeans.modules.portalpack.servers.core.api.PSDeploymentManager;
import org.netbeans.modules.portalpack.servers.core.api.PSNodeConfiguration;
import org.netbeans.modules.portalpack.servers.core.api.PSStartServerInf;
import org.netbeans.modules.portalpack.servers.core.api.PSTaskHandler;
import org.netbeans.modules.portalpack.servers.core.common.FileLogViewerSupport;
import org.netbeans.modules.portalpack.servers.core.common.ServerConstants;
import org.netbeans.modules.portalpack.servers.core.util.PSConfigObject;
import org.netbeans.modules.portalpack.servers.websynergy.common.LiferayConstants;
import org.netbeans.modules.portalpack.servers.websynergy.common.WSConstants;
import org.netbeans.modules.portalpack.servers.websynergy.impl.LiferayTaskHandler;
import org.openide.util.Exceptions;

/**
 *
 * @author root
 */
public class LiferayDeploymentManager extends PSDeploymentManager {

    private PSTaskHandler taskHandler;
    //private LifeRayNodeConfiguration nodeConfigurator;

    public LiferayDeploymentManager(String uri, String psVersion) {
        super(uri, psVersion);
    }

    public PSTaskHandler getTaskHandler() {
        if (taskHandler == null) {
            if (getPSVersion().equals(LiferayConstants.LR_1_0)
                    || getPSVersion().equals(WSConstants.WS_1_0)) {
                taskHandler = new LiferayTaskHandler(this);
            }
        }
        return taskHandler;
    }

    public PSConfigPanelManager getPSConfigPanelManager() {
        return new LiferayConfigPanelManagerImpl();
    }

    public PSNodeConfiguration getPSNodeConfiguration() {
        // if(nodeConfigurator == null)
        //      nodeConfigurator = new LifeRayNodeConfiguration(this);
        //  return nodeConfigurator;
        return LiferayNodeConfiguration.getInstance();
    }

    public PSStartServerInf getStartServerHandler() {

        return ContainerStartHandlerFactory.getStartServerHandler(this);
    }

    public PSJ2eePlatformImpl createPSJ2eePlatformImpl(PSConfigObject psconfig) {
        return new LiferayJ2eePlatformImpl(psconfig);
    }

    @Override
    public boolean isShowServerLogSupported() {
        return true;
    }

    @Override
    public void showServerLog() {

        if (getPSConfig().getServerType().equals(ServerConstants.SUN_APP_SERVER_9)) {
            
            try {

                FileLogViewerSupport.removeLogViewerSupport(getUri());
                File f = new File(getPSConfig().getDomainDir() + File.separator + "/logs/server.log");
                FileLogViewerSupport p = FileLogViewerSupport.getLogViewerSupport(f, getUri(), 2000, true);
                p.showLogViewer(true);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }
}
