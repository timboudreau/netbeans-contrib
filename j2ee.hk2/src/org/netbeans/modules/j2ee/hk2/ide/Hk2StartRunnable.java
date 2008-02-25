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
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
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
    
    private static final String GFV3_LIB_DIR_NAME = "lib"; // NOI18N
    private static final String GFV3_MODULES_DIR_NAME = "modules"; // NOI18N
    private static final String GFV3_SNAPSHOT_JAR_NAME = "glassfish-10.0-SNAPSHOT.jar"; // NOI18N"
    
    
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
        List<String> envp = new ArrayList<String>(3);
        JavaPlatform platform = dm.getProperties().getJavaPlatform();
        FileObject fo = platform.getInstallFolders().iterator().next();
        String javaHome = FileUtil.toFile(fo).getAbsolutePath();
        envp.add("JAVA_HOME=" + javaHome); // NOI18N
        return (String[]) envp.toArray(new String[envp.size()]);
    }
    
    private NbProcessDescriptor createProcessDescriptor() {
        String startScript = System.getProperty("java.home") + "/bin/java" ; 
        String hk2Home = ip.getProperty(Hk2PluginProperties.PROPERTY_HK2_HOME);
        
        String jarLocation = hk2Home + "/" + GFV3_MODULES_DIR_NAME + "/" + GFV3_SNAPSHOT_JAR_NAME;
        if (!new File(jarLocation).exists()){
            // !PW Older V3 installs (pre 12/01/07) put snapshot jar in lib folder.
            jarLocation = hk2Home + "/" + GFV3_LIB_DIR_NAME + "/" + GFV3_SNAPSHOT_JAR_NAME;
            if (!new File(jarLocation).exists()){
                fireStartProgressEvent(StateType.FAILED, createProgressMessage("MSG_START_SERVER_FAILED_FNF")); //NOI18N
                return null;
            }
        }
        
        StringBuilder javaOpts = new StringBuilder();
        if(startServer.getMode() == Hk2StartServer.MODE.DEBUG) {
            javaOpts.append(" -classic -Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,address="). // NOI18N
                    append(dm.getProperties().getDebugPort()).
                    append(",server=y,suspend=n"); // NOI18N
        }
        
        return new NbProcessDescriptor(startScript, javaOpts.toString() + " -jar \"" + jarLocation + "\""); //NOI18N
    }
    
    private Process createProcess() {
        NbProcessDescriptor pd = createProcessDescriptor();
        
        if (pd == null){
            return null;
        }
        
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