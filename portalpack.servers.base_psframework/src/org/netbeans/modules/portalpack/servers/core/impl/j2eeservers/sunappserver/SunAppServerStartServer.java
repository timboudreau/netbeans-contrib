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

package org.netbeans.modules.portalpack.servers.core.impl.j2eeservers.sunappserver;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.modules.j2ee.deployment.plugins.api.UISupport;
import org.netbeans.modules.portalpack.servers.core.PSLogViewer;
import org.netbeans.modules.portalpack.servers.core.api.PSDeploymentManager;
import org.netbeans.modules.portalpack.servers.core.api.PSStartServerInf;
import org.netbeans.modules.portalpack.servers.core.common.LogManager;
import org.netbeans.modules.portalpack.servers.core.util.NetbeanConstants;
import org.netbeans.modules.portalpack.servers.core.util.PSConfigObject;
import org.openide.ErrorManager;
import org.openide.windows.OutputWriter;

/**
 *
 * @author satya
 */
public class SunAppServerStartServer extends PSStartServerInf{
    private static Logger logger = Logger.getLogger(NetbeanConstants.PORTAL_LOGGER);
    private PSDeploymentManager dm;
    private PSConfigObject psconfig;
    /** Creates a new instance of SunAppServerStartServer */
    public SunAppServerStartServer(PSDeploymentManager dm) {
        this.dm = dm;
        this.psconfig = dm.getPSConfig();
    }

    public void doStartServer() throws Exception {       
        runProcess(makeStartCommand(), true); //NO I18N
        viewAdminLogs();      
    }
        
    public void doStopServer() throws Exception {       
        runProcess(makeStopCommand(), true); //NO I18N       
    }
     
    private int runProcess(String str, boolean wait) throws Exception {
        final Process child = Runtime.getRuntime().exec(str);
             
        LogManager manager = new LogManager(dm);
        manager.openServerLog(child,str + System.currentTimeMillis());
        if (wait)
            child.waitFor();        
        return child.exitValue(); 
        
    }
    
    
    private String makeStartCommand() {
        
        StringBuffer command = new StringBuffer();
        command.append(psconfig.getServerHome())
                .append(File.separator)
                .append("bin")
                .append(File.separator)
                .append("asadmin");
        if (org.openide.util.Utilities.isWindows()){
            command.append(".bat");
        }
        
        command.append(" ")
               .append("start-domain")
               .append(" ")
               .append(psconfig.getDefaultDomain());
        
        System.out.println("Return ::::"+command.toString());
        return command.toString();
    }
    
     private String makeStopCommand() {
        
        StringBuffer command = new StringBuffer();
        command.append(psconfig.getServerHome())
                .append(File.separator)
                .append("bin")
                .append(File.separator)
                .append("asadmin");
        if (org.openide.util.Utilities.isWindows()){
            command.append(".bat");
        }
        
        command.append(" ")
               .append("stop-domain")
               .append(" ")
               .append(psconfig.getDefaultDomain());
        
        System.out.println("Return ::::"+command.toString());
        return command.toString();
    }
    
    
    private void viewAdminLogs(){
        String uri = dm.getUri();
        String location = psconfig.getDomainDir() + File.separator + "logs" + File.separator +"server.log";
        
        
        PSLogViewer logViewer = new PSLogViewer(new File(location));
        
        
        try{
            logViewer.showLogViewer(UISupport.getServerIO(uri));
        }catch(Exception ex){
            ErrorManager.getDefault().notify(ErrorManager.WARNING, ex);
        }
    }
      
    private void viewInstanceLogs(){
        String uri = dm.getUri();
        String domainDir = psconfig.getDomainDir();
        File fdomainDir = new File(domainDir);
        String domainName = fdomainDir.getName();
        String location = domainDir +
                File.separator+"logs"+File.separator+"errors";
        
        PSLogViewer logViewer = new PSLogViewer(new File(location));
        
        try{
            logViewer.showLogViewer(UISupport.getServerIO(uri));
        }catch(Exception ex){
            ErrorManager.getDefault().notify(ErrorManager.WARNING, ex);
        }
    }
    
}
