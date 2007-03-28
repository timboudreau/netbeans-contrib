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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.j2ee.hk2.ide;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.enterprise.deploy.shared.ActionType;
import javax.enterprise.deploy.shared.CommandType;
import javax.enterprise.deploy.shared.StateType;
import org.netbeans.api.java.platform.JavaPlatform;
import org.netbeans.modules.j2ee.deployment.plugins.api.InstanceProperties;
import org.netbeans.modules.j2ee.hk2.Hk2DeploymentManager;
import org.netbeans.modules.j2ee.hk2.Hk2StartServer;


import org.openide.ErrorManager;
import org.openide.execution.NbProcessDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbBundle;

/**
 * @author ludo
 */
public class Hk2StartRunnable implements Runnable {
    
    
    private Hk2DeploymentManager dm;
    private String instanceName;
    private Hk2StartServer startServer;
    private InstanceProperties ip;
    
    /**
     * The amount of time in milliseconds during which the server should
     * start
     */
    private static final int TIMEOUT = 120000;
    
    /**
     * The amount of time in milliseconds that we should wait between checks
     */
    private static final int DELAY = 600;
    
    /**
     * 
     * @param dm 
     * @param startServer 
     */
    public Hk2StartRunnable(Hk2DeploymentManager dm, Hk2StartServer startServer) {
        this.dm = dm;
        this.ip = dm.getProperties().getInstanceProperties();
        this.instanceName = ip.getProperty(InstanceProperties.DISPLAY_NAME_ATTR);
        this.startServer = startServer;
    }
    
    /**
     * 
     */
    public void run() {
        // Save the current time so that we can deduct that the startup
        // Failed due to timeout
        long start = System.currentTimeMillis();
               
        Process serverProcess = createProcess();
        if (serverProcess == null) {
            return;
        }

        fireStartProgressEvent(StateType.RUNNING, createProgressMessage("MSG_START_SERVER_IN_PROGRESS"));
        
        // create a logger to the server's output stream so that a user
        // can observe the progress
        Hk2Logger.getInstance(dm.getUri()).readInputStreams(new InputStream[] {
            serverProcess.getInputStream(), serverProcess.getErrorStream()});
        
        // Waiting for server to start
        while (System.currentTimeMillis() - start < TIMEOUT) {
            // Send the 'completed' event and return when the server is running
            if (startServer.isRunning()) {
                fireStartProgressEvent(StateType.COMPLETED, createProgressMessage("MSG_SERVER_STARTED")); // NOI18N
                return;
            }
            
            // Sleep for a little so that we do not make our checks too
            // Often
            try {
                Thread.sleep(DELAY);
            } catch (InterruptedException e) {}
        }
        
        // If the server did not start in the designated time limits
        // We consider the startup as failed and warn the user
        fireStartProgressEvent(StateType.FAILED, createProgressMessage("MSG_START_SERVER_FAILED"));
    }
    
    private String[] createEnvironment() {
        StringBuilder javaOpts = new StringBuilder();
        List<String> envp = new ArrayList<String>(3);
        String rootDir = ip.getProperty(Hk2PluginProperties.PROPERTY_HK2_HOME);
        JavaPlatform platform = dm.getProperties().getJavaPlatform();
        FileObject fo = (FileObject) platform.getInstallFolders().iterator().next();
        String javaHome = FileUtil.toFile(fo).getAbsolutePath();
        
        if(startServer.getMode() == Hk2StartServer.MODE.DEBUG) {
            javaOpts.append(" -classic -Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,address="). // NOI18N
                    append(dm.getProperties().getDebugPort()).
                    append(",server=y,suspend=n"); // NOI18N
        }
        

        envp.add("JAVA_HOME=" + javaHome); // NOI18N
        
        return (String[]) envp.toArray(new String[envp.size()]);
    }
    
    private NbProcessDescriptor createProcessDescriptor() {

        String startScript = System.getProperty("java.home")+"/bin/java" ; 
        String jarlocation = ip.getProperty(Hk2PluginProperties.PROPERTY_HK2_HOME) +"/lib/glassfish.jar";
        if (!new File(jarlocation).exists()){
            fireStartProgressEvent(StateType.FAILED, createProgressMessage("MSG_START_SERVER_FAILED_FNF")); //NOI18N
            return null;
        }
        return new NbProcessDescriptor(startScript, " -jar "+jarlocation); //NOI18N
    }
    
    private Process createProcess() {
        NbProcessDescriptor pd = createProcessDescriptor();
        
        if (pd == null)
            return null;
        
        try {
            return pd.exec(null, createEnvironment(), true, new File(ip.getProperty(Hk2PluginProperties.PROPERTY_HK2_HOME)));
        } catch (java.io.IOException ioe) {
            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, ioe);
            fireStartProgressEvent(StateType.FAILED, createProgressMessage("MSG_START_SERVER_FAILED_PD"));
            return null;
        }
    }
    
    private String createProgressMessage(final String resName) {
        return createProgressMessage(resName, null);
    }
    
    private String createProgressMessage(final String resName, final String param) {
        return NbBundle.getMessage(Hk2StartRunnable.class, resName, instanceName, param);
    }
    
    private void fireStartProgressEvent(StateType stateType, String msg) {
        startServer.fireHandleProgressEvent(null, new Hk2DeploymentStatus(ActionType.EXECUTE, CommandType.START, stateType, msg));
    }
}