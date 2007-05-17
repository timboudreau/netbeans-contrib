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

package org.netbeans.modules.portalpack.servers.core;

import org.netbeans.modules.j2ee.deployment.plugins.spi.FindJSPServlet;
import org.netbeans.modules.portalpack.servers.core.api.PSDeploymentManager;
import org.netbeans.modules.portalpack.servers.core.api.PSStartServerInf;
import org.netbeans.modules.portalpack.servers.core.common.LogManager;
import org.netbeans.modules.portalpack.servers.core.util.PSConfigObject;
import org.netbeans.modules.portalpack.servers.core.util.NetbeanConstants;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.modules.j2ee.deployment.plugins.api.UISupport;
import org.openide.ErrorManager;
import org.openide.windows.OutputWriter;

/**
 *
 * @author Satyaranjan
 */
public class WS70StartServer extends PSStartServerInf{
    
    private static Logger logger = Logger.getLogger(NetbeanConstants.PORTAL_LOGGER);
    private PSConfigObject psconfig;
    private PSDeploymentManager dm;
    /** Creates a new instance of WS71StartServer */
    public WS70StartServer(PSDeploymentManager dm) {
        this.dm = dm;
        this.psconfig = dm.getPSConfig();
        
    }
    
    public void doStartServer() throws Exception {
        runProcess(makeProcessString("start"), true); //NO I18N  
        if(!dm.isRunningInstanceServer()) {
            runProcess(makeProcessStringForDomain("start"),true);
        }
    }
    
    public void doStopServer() throws Exception {
        
        runProcess(makeProcessString("stop"), true); //NO I18N
        if(dm.isRunningInstanceServer()) {
            runProcess(makeProcessStringForDomain("stop"),true);
        }
    }
     
    private int runProcess(String str, boolean wait) throws Exception {
        final Process child = Runtime.getRuntime().exec(str);
        LogManager manager = new LogManager(dm);
        manager.openServerLog(child,str + System.currentTimeMillis());
        if (wait)
            child.waitFor();
        
        return child.exitValue();
    }
    
    
    private String makeProcessString(String str) {
        if (org.openide.util.Utilities.isWindows()){
            return "net " + str + " " + "https-admserv70"; // NOI18N
        }else{
            String process = str+"serv";
            return ((PSDeploymentManager)dm).getServerLocation()+File.separator +
                    "admin-server" + File.separator+"bin" + File.separator + process; //NO I18N
        }
    }
    
    private String makeProcessStringForDomain(String str) {
        String domainDir = psconfig.getDomainDir();
        File fdomainDir = new File(domainDir);
        String domainName = fdomainDir.getName();
        if (org.openide.util.Utilities.isWindows()){
            return "net " + str + " " + domainName; // NOI18N
        }else{
            String process = str+"serv";
            return domainDir + File.separator + "bin" + File.separator + process;
        }
    }
    private void viewAdminLogs(){
        String uri = dm.getUri();
        String location = dm.getServerLocation();
        location = location+File.separator+"admin-server"+
                File.separator+"logs"+File.separator+"errors";
        
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

    public void doStartDebug() throws Exception {
    }

    public void doStopDebug() throws Exception {
    }

    public int getDebugPort() {
        return 0;
    }

    public FindJSPServlet getFindJSPServlet(PSDeploymentManager dm) {
        return null;
    }
    
    
    
    private  class MyLogWriterThread extends Thread {
        
        boolean isStop = false;
        private InputStream input;
        public MyLogWriterThread(InputStream input) {
            this.input = input;
        }
        public void run() {
            
            OutputWriter writer  = UISupport.getServerIO(dm.getUri()).getOut();
            
            String line;
            try {
                
                BufferedReader br = new BufferedReader(new InputStreamReader(input));
                while(br.ready())
                {
                    //logger.log(Level.INFO,br.read()+"");
                }
                while ((line = br.readLine()) != null) {
                    
                    writer.write(line);
                    writer.flush();
                }
                input.close();
            } catch (IOException ex) {
                logger.log(Level.SEVERE,"Error",ex);
            }
            
            logger.log(Level.FINEST,"MyLogWriterThread stopped ...");
        }
        
        public void setStop(boolean flag) {
            isStop = flag;
        }
    }
}
