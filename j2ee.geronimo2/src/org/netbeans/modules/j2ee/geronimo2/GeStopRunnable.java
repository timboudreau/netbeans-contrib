/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 * 
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 * 
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 * 
 * Contributor(s):
 * 
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */

package org.netbeans.modules.j2ee.geronimo2;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.deploy.shared.ActionType;
import javax.enterprise.deploy.shared.CommandType;
import javax.enterprise.deploy.shared.StateType;
import org.netbeans.api.java.platform.JavaPlatform;
import org.netbeans.modules.j2ee.deployment.plugins.api.InstanceProperties;
import org.netbeans.modules.j2ee.geronimo2.util.GeLogger;
import org.openide.execution.NbProcessDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

/**
 * Class responsible for server stopping
 * @author maxa
 */
public class GeStopRunnable implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(GeStopRunnable.class.getName());
    
    private GeDeploymentManager dm;
    private GeStartServer startServer;
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
    
    GeStopRunnable(GeDeploymentManager dm, GeStartServer startServer) {
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
        GeLogger.getInstance(dm.getUri()).readInputStreams(new InputStream[] {
            serverProcess.getInputStream(), serverProcess.getErrorStream()});
        
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
        String rootDir = ip.getProperty(GePluginProperties.PROPERTY_GE_HOME);
        JavaPlatform platform = dm.getProperties().getJavaPlatform();
        FileObject fo = (FileObject) platform.getInstallFolders().iterator().next();
        String javaHome = FileUtil.toFile(fo).getAbsolutePath();
        envp.add("GE_HOME=" + rootDir); // NOI18N
        envp.add("JAVA_HOME=" + javaHome); // NOI18N
        envp.add("VERBOSE=on"); // NOI18N
        
        return (String[]) envp.toArray(new String[envp.size()]);
    }
    
    private NbProcessDescriptor createProcessDescriptor() {
        String serverLocation = ip.getProperty(GePluginProperties.PROPERTY_GE_HOME);
        String scriptPath = serverLocation + File.separator + "bin" + File.separator +
                (Utilities.isWindows() ? GeStartRunnable.SCRIPT_WIN : GeStartRunnable.SCRIPT_UNIX);
        String passwd = ip.getProperty(InstanceProperties.PASSWORD_ATTR);
        String adminPort = ip.getProperty(GePluginProperties.PROPERTY_ADMIN_PORT);
        String userName = ip.getProperty(InstanceProperties.USERNAME_ATTR);
        
        if(!ip.getProperty(InstanceProperties.USERNAME_ATTR).equals("system"))
            passwd = GePluginUtils.requestPassword("system");
        
        if (!new File(scriptPath).exists()){
            fireStartProgressEvent(StateType.FAILED, createProgressMessage("MSG_STOP_SERVER_FAILED_FNF", instanceName));//NOI18N
            return null;
        }
        
        if (LOGGER.isLoggable(Level.FINER)) {
            LOGGER.log(Level.FINER, "EXEC: " + scriptPath +
                " stop --port " + adminPort + " --user " + userName + " --password " + passwd);
        }
        
        return new NbProcessDescriptor(scriptPath, "stop --port " +
                adminPort + " --user " + userName +" --password " + passwd); // NOI18N
    }
    
    private Process createProcess() {
        NbProcessDescriptor pd = createProcessDescriptor();
        
        if (pd == null)
            return null;
        
        try {
            return pd.exec(null, createEnvironment(), true, new File(ip.getProperty(GePluginProperties.PROPERTY_GE_HOME)));
        } catch (java.io.IOException ioe) {
            LOGGER.log(Level.INFO, null, ioe);
            fireStartProgressEvent(StateType.FAILED, createProgressMessage("MSG_STOP_SERVER_FAILED_PD"));
            return null;
        }
    }
    
    private String createProgressMessage(final String resName) {
        return createProgressMessage(resName, null);
    }
    
    private String createProgressMessage(final String resName, final String param) {
        return NbBundle.getMessage(GeStartRunnable.class, resName, instanceName, param);
    }
    
    private void fireStartProgressEvent(StateType stateType, String msg) {
        startServer.fireHandleProgressEvent(null, new GeDeploymentStatus(ActionType.EXECUTE, CommandType.STOP, stateType, msg));
    }
    
}
