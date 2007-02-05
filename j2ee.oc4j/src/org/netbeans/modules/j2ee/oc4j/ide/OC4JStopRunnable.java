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

package org.netbeans.modules.j2ee.oc4j.ide;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.enterprise.deploy.shared.ActionType;
import javax.enterprise.deploy.shared.CommandType;
import javax.enterprise.deploy.shared.StateType;
import org.netbeans.api.java.platform.JavaPlatform;
import org.netbeans.modules.j2ee.deployment.plugins.api.InstanceProperties;
import org.netbeans.modules.j2ee.oc4j.OC4JDeploymentManager;
import org.netbeans.modules.j2ee.oc4j.util.OC4JPluginProperties;
import org.netbeans.modules.j2ee.oc4j.util.OC4JDebug;
import org.netbeans.modules.j2ee.oc4j.util.OC4JPluginUtils;
import org.openide.ErrorManager;
import org.openide.execution.NbProcessDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

/**
 * @author Michal Mocnak
 */
class OC4JStopRunnable implements Runnable {
    
    private OC4JDeploymentManager dm;
    private OC4JStartServer startServer;
    private InstanceProperties ip;
    private String instanceName;
    
    /**
     * The amount of time in milliseconds during which the server should
     * stop
     */
    private static final int TIMEOUT = 120000;
    
    /**
     * The amount of time in milliseconds that we should wait between checks
     */
    private static final int DELAY = 5000;
    
    OC4JStopRunnable(OC4JDeploymentManager dm, OC4JStartServer startServer) {
        this.dm = dm;
        this.ip = dm.getProperties().getInstanceProperties();
        this.instanceName = ip.getProperty(InstanceProperties.DISPLAY_NAME_ATTR);
        this.startServer = startServer;
    }
    
    public void run() {
        // save the current time so that we can deduct that the startup
        // failed due to timeout
        long start = System.currentTimeMillis();
        
        Process serverProcess = createProcess();
        
        if (serverProcess == null) {
            return;
        }
        
        fireStartProgressEvent(StateType.RUNNING, createProgressMessage("MSG_STOP_SERVER_IN_PROGRESS", instanceName));
        
        // create a logger to the server's output stream so that a user
        // can observe the progress
        new OC4JLogger(new InputStream[] {serverProcess.getInputStream()
                , serverProcess.getErrorStream()}, dm.getUri());
        
        // Waiting for server to start
        while (System.currentTimeMillis() - start < TIMEOUT) {
            // Send the 'completed' event and return when the server is running
            if (!startServer.isRunning()) {
                fireStartProgressEvent(StateType.COMPLETED, createProgressMessage("MSG_SERVER_STOPPED", instanceName)); // NOI18N
                return;
            }
            
            // Sleep for a little so that we do not make our checks too
            // Often
            try {
                Thread.sleep(DELAY);
            } catch (InterruptedException e) {}
            
            fireStartProgressEvent(StateType.RUNNING,createProgressMessage("MSG_STOP_SERVER_IN_PROGRESS", instanceName));
        }
        
        fireStartProgressEvent(StateType.FAILED, createProgressMessage("MSG_STOP_SERVER_FAILED", instanceName));//NOI18N
        
        if(serverProcess != null)
            serverProcess.destroy();
    }
    
    private String[] createEnvironment() {
        List<String> envp = new ArrayList<String>(3);
        String rootDir = ip.getProperty(OC4JPluginProperties.PROPERTY_OC4J_HOME);
        JavaPlatform platform = dm.getProperties().getJavaPlatform();
        FileObject fo = (FileObject) platform.getInstallFolders().iterator().next();
        String javaHome = FileUtil.toFile(fo).getAbsolutePath();
        envp.add("ORACLE_HOME=" + rootDir); // NOI18N
        envp.add("JAVA_HOME=" + javaHome); // NOI18N
        envp.add("VERBOSE=on"); // NOI18N
        
        return (String[]) envp.toArray(new String[envp.size()]);
    }
    
    private NbProcessDescriptor createProcessDescriptor() {
        String serverLocation = ip.getProperty(OC4JPluginProperties.PROPERTY_OC4J_HOME);
        String scriptPath = serverLocation + File.separator + "bin" + File.separator +
                (Utilities.isWindows() ? OC4JStartRunnable.SCRIPT_WIN : OC4JStartRunnable.SCRIPT_UNIX);
        String passwd = ip.getProperty(InstanceProperties.PASSWORD_ATTR);
        String adminPort = ip.getProperty(OC4JPluginProperties.PROPERTY_ADMIN_PORT);
        
        if(!ip.getProperty(InstanceProperties.USERNAME_ATTR).equals("oc4jadmin"))
            passwd = OC4JPluginUtils.requestPassword("oc4jadmin");
        
        if (!new File(scriptPath).exists()){
            fireStartProgressEvent(StateType.FAILED, createProgressMessage("MSG_STOP_SERVER_FAILED_FNF", instanceName));//NOI18N
            return null;
        }
        
        OC4JDebug.log(getClass().getName(), "EXEC: " + scriptPath +
                " -shutdown -port " + adminPort + " -password " + passwd);
        
        return new NbProcessDescriptor(scriptPath, "-shutdown -port " +
                adminPort + " -password " + passwd); // NOI18N
    }
    
    private Process createProcess() {
        NbProcessDescriptor pd = createProcessDescriptor();
        
        if (pd == null)
            return null;
        
        try {
            return pd.exec(null, createEnvironment(), true, new File(ip.getProperty(OC4JPluginProperties.PROPERTY_OC4J_HOME)));
        } catch (java.io.IOException ioe) {
            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, ioe);
            fireStartProgressEvent(StateType.FAILED, createProgressMessage("MSG_STOP_SERVER_FAILED_PD"));
            return null;
        }
    }
    
    private String createProgressMessage(final String resName) {
        return createProgressMessage(resName, null);
    }
    
    private String createProgressMessage(final String resName, final String param) {
        return NbBundle.getMessage(OC4JStartRunnable.class, resName, instanceName, param);
    }
    
    private void fireStartProgressEvent(StateType stateType, String msg) {
        startServer.fireHandleProgressEvent(null, new OC4JDeploymentStatus(ActionType.EXECUTE, CommandType.STOP, stateType, msg));
    }
}