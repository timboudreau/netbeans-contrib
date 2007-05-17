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

package org.netbeans.modules.portalpack.servers.core.impl.j2eeservers.tomcat;

import java.io.File;
import org.netbeans.modules.j2ee.deployment.plugins.spi.FindJSPServlet;
import org.netbeans.modules.j2ee.deployment.plugins.api.UISupport;
import org.netbeans.modules.portalpack.servers.core.PSLogViewer;
import org.netbeans.modules.portalpack.servers.core.api.PSDeploymentManager;
import org.netbeans.modules.portalpack.servers.core.api.PSStartServerInf;
import org.netbeans.modules.portalpack.servers.core.common.LogManager;
import org.netbeans.modules.portalpack.servers.core.util.Command;
import org.netbeans.modules.portalpack.servers.core.util.PSConfigObject;
import org.openide.ErrorManager;

/**
 *  
 * @author satya
 */
public class TomcatStartServer extends PSStartServerInf implements TomcatConstant{
    
    private PSConfigObject psconfig;
    private PSDeploymentManager dm;
    /** Creates a new instance of TomcatStartServer */
    public TomcatStartServer(PSDeploymentManager dm) {
        this.dm = dm;
        this.psconfig = dm.getPSConfig();
    }

    public void doStartServer() throws Exception {
        runStartProcess(makeStartCommand(),true);
        //viewLogs();
    }

    public void doStopServer() throws Exception {
        runStopProcess(makeStopCommand(),true);
    }
    
    private String makeStartCommand()
    { 
        Command cmd = new Command();
        
        String script = "";
         if(org.openide.util.Utilities.isWindows()){
          script = "catalina.bat";
        }else
          script = "catalina.sh";
        
        cmd.add(psconfig.getProperty(CATALINA_HOME) + File.separator + "bin" + File.separator + script);
        cmd.add("run");

        System.out.println(cmd.toString());
        return cmd.toString();
    }
    
    private String makeStopCommand()
    {
        Command cmd = new Command();
        
        String script = "";
         if(org.openide.util.Utilities.isWindows()){
          script = "catalina.bat";
        }else
          script = "catalina.sh";
        cmd.add(psconfig.getProperty(CATALINA_HOME) + File.separator + "bin" + File.separator + script);
        
        cmd.add("stop");
        System.out.println(cmd.toString());
        return cmd.toString();
    }
    
    private int runStartProcess(String str, boolean wait) throws Exception{
        String[] env = setEnv();
        //env[2] = "JAVA_HOME="+System.getProperty("java.dir");
        //System.out.println(System.getProperty("java.home"));
        final Process child = Runtime.getRuntime().exec(str,env);
            
        LogManager manager = new LogManager(dm);
        manager.openServerLog(child,str + System.currentTimeMillis());
        if (wait)
        {
            try {
                //child.waitFor();
                Thread.currentThread().sleep(4000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
        while(!dm.isRunning()){
            try {
                Thread.currentThread().sleep(2000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
        return 1;
      //  return child.exitValue();
            
        
    }
    
    
    private int runStopProcess(String str, boolean wait) throws Exception {
        String[] env = setEnv();
        //env[2] = "JAVA_HOME="+System.getProperty("java.dir");
        //System.out.println(System.getProperty("java.home"));
        final Process child = Runtime.getRuntime().exec(str,env);
            
        LogManager manager = new LogManager(dm);
        manager.openServerLog(child,str + System.currentTimeMillis());
        if (wait)
            //child.waitFor();
            Thread.sleep(4000);
        while(dm.isRunning()){
            Thread.sleep(2000);
        }
        return child.exitValue();
            
        
    }

    private String[] setEnv() {
        
        String[] env = new String[5];
        env[0] = "CATALINA_HOME="+psconfig.getProperty(CATALINA_HOME);
        env[1] = "CATALINA_BASE="+psconfig.getProperty(CATALINA_BASE);
        env[2] = "JRE_HOME="+psconfig.getProperty(JAVA_HOME);
        env[3] = "JAVA_HOME="+psconfig.getProperty(JAVA_HOME);
        
        //needed for Windows OS
        String systemRoot = System.getenv("SystemRoot");
        if(systemRoot == null)
            systemRoot = "";
        env[4] = "SystemRoot="+ systemRoot;
        System.out.println(System.getenv("SystemRoot"));
              
        return env;
    }
    
     private void viewLogs(){
        String uri = dm.getUri();
        String location = psconfig.getProperty(CATALINA_BASE) + File.separator + "logs" + File.separator +"catalina.out";
        
        
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
    
}
