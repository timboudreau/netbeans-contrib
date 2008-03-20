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

import java.io.File;
import java.io.IOException;
import org.netbeans.modules.j2ee.deployment.plugins.api.UISupport;
import org.netbeans.modules.portalpack.servers.core.PSJ2eePlatformImpl;
import org.netbeans.modules.portalpack.servers.core.api.PSConfigPanelManager;
import org.netbeans.modules.portalpack.servers.core.api.PSDeploymentManager;
import org.netbeans.modules.portalpack.servers.core.api.PSNodeConfiguration;
import org.netbeans.modules.portalpack.servers.core.api.PSStartServerInf;
import org.netbeans.modules.portalpack.servers.core.api.PSTaskHandler;
import org.netbeans.modules.portalpack.servers.core.common.FileLogViewerSupport;
import org.netbeans.modules.portalpack.servers.core.common.LogManager;
import org.netbeans.modules.portalpack.servers.core.util.PSConfigObject;
import org.netbeans.modules.portalpack.servers.jnpc.common.JNPCConstants;
import org.netbeans.modules.portalpack.servers.jnpc.impl.JNPCTaskHandler;
import org.netbeans.modules.portalpack.servers.jnpc.pc20.PC20TaskHandler;
import org.openide.util.Exceptions;
import org.openide.windows.InputOutput;

/**
 *
 * @author root
 */
public class JNPCDeploymentManager extends PSDeploymentManager {
     
    private PSTaskHandler taskHandler;
    //private JNPCNodeConfiguration nodeConfigurator;
    
    public JNPCDeploymentManager(String uri,String psVersion)
    {
        super(uri,psVersion);
    }
    public PSTaskHandler getTaskHandler() {
        if(taskHandler == null){
            if(getPSVersion().equals(JNPCConstants.OS_PC_1_0))
                taskHandler = new JNPCTaskHandler(this);
            else if(getPSVersion().equals(JNPCConstants.OP_PC_2_0))
                taskHandler = new PC20TaskHandler(this);
            else
                taskHandler = new PC20TaskHandler(this);
        }
        return taskHandler;
    }

    public PSConfigPanelManager getPSConfigPanelManager() {
        return new JNPCConfigPanelManagerImpl();
    }
    
    public PSNodeConfiguration getPSNodeConfiguration() {
       // if(nodeConfigurator == null)
      //      nodeConfigurator = new JNPCNodeConfiguration(this);
      //  return nodeConfigurator;
        return JNPCNodeConfiguration.getInstance();
    }
    

    public PSStartServerInf getStartServerHandler() {
        
        return ContainerStartHandlerFactory.getStartServerHandler(this);
    }

    public PSJ2eePlatformImpl createPSJ2eePlatformImpl(PSConfigObject psconfig) {
         return new JNPCJ2eePlatformImpl(psconfig);
    }

    @Override
    public void showServerLog() {
        try {
            
            FileLogViewerSupport.removeLogViewerSupport(getUri());
            File f = new File(getPSConfig().getDomainDir() + File.separator + "/logs/server.log");
            FileLogViewerSupport p = FileLogViewerSupport.getLogViewerSupport(f, getUri(), 2000, true);
            p.showLogViewer(true);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    @Override
    public boolean isShowServerLogSupported() {
        return true;
    }
    
}
