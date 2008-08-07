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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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

package org.netbeans.modules.j2ee.jetty.ide;

import org.netbeans.modules.j2ee.jetty.*;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.deploy.shared.ActionType;
import javax.enterprise.deploy.shared.CommandType;
import org.netbeans.modules.j2ee.deployment.plugins.api.InstanceProperties;
import javax.enterprise.deploy.shared.StateType;
import org.netbeans.api.java.platform.JavaPlatform;
import org.openide.execution.NbProcessDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

/**
 * Class responsible for stopping new process that starts the server
 * and for notifying any listeners about its progress or state
 * @author novakm
 */
class JetStopRunnable implements Runnable  {

//    static String STOP_KEY = "h4rd.T0_Gue3s";
    static String JETTY_STOP_KEY = "baf";
    private JetDeploymentManager dm;
    private String instanceName;
    private JetStartServer startServer;
    private InstanceProperties ip;
    private static final Logger LOGGER = Logger.getLogger(JetStopRunnable.class.getName());

    public JetStopRunnable(JetDeploymentManager dm, JetStartServer startServer) {
        this.dm = dm;
        this.ip = dm.getProperties().getInstanceProperties();
        this.instanceName = ip.getProperty(InstanceProperties.DISPLAY_NAME_ATTR);
        this.startServer = startServer;
    }
        /**
         * Starts the stopping process in a new thread so it doesn't block IDE
         */
    public void run() {
        // Save the current time so that we can deduct that the startup
        // Failed due to timeout
        long start = System.currentTimeMillis();
        Process serverProcess = createProcess();
        if (serverProcess == null) {
            return;
        }
        JetLogger.getInstance(dm.getUri()).readInputStreams(new InputStream[]{serverProcess.getInputStream(), serverProcess.getErrorStream()});
        startServer.fireHandleProgressEvent(null, new JetDeploymentStatus(ActionType.EXECUTE, CommandType.STOP, StateType.RUNNING, createProgressMessage("MSG_STOP_SERVER_IN_PROGRESS")));
        // Waiting for server to stop
        while (System.currentTimeMillis() - start < JetStartServer.TIMEOUT) {
            // Send the 'completed' event and return when the server is running
            if (!startServer.isRunning(false)) {
                startServer.fireHandleProgressEvent(null, new JetDeploymentStatus(ActionType.EXECUTE, CommandType.STOP, StateType.COMPLETED, createProgressMessage("MSG_STOP_SERVER_FAILED")));
                return;
            }

            // Sleep for a little so that we do not make our checks too
            // Often
            try {
                Thread.sleep(JetStartServer.DELAY);
            } catch (InterruptedException e) {
            }
        }

        // If the server did not start in the designated time limits
        // We consider the startup as failed and warn the user
        LOGGER.log(Level.WARNING, "Server stop failed");
        startServer.fireHandleProgressEvent(null, new JetDeploymentStatus(ActionType.EXECUTE, CommandType.STOP, StateType.FAILED, createProgressMessage("MSG_STOP_SERVER_FAILED")));
    }

    private String[] createEnvironment() {
        StringBuilder javaOpts = new StringBuilder();
        List<String> envp = new ArrayList<String>(3);
        String rootDir = ip.getProperty(JetPluginProperties.PROPERTY_JET_HOME);
        JavaPlatform platform = dm.getProperties().getJavaPlatform();
        FileObject fo = (FileObject) platform.getInstallFolders().iterator().next();
        String javaHome = FileUtil.toFile(fo).getAbsolutePath();

        if (startServer.getMode() == JetStartServer.MODE.DEBUG) {
            javaOpts.append(" -classic -Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,address=").append(dm.getProperties().getDebugPort()).append(",server=y,suspend=n"); // NOI18N
        }

        envp.add("JET_HOME=" + rootDir); // NOI18N
        envp.add("JAVA_HOME=" + javaHome); // NOI18N
        envp.add("JET_JVM_ARGS=" + javaOpts); // NOI18N
        envp.add("VERBOSE=on"); // NOI18N
        return (String[]) envp.toArray(new String[envp.size()]);
    }

    private Process createProcess() {
        NbProcessDescriptor pd = createProcessDescriptor();

        if (pd == null) {
            return null;
        }
        try {
            return pd.exec(null, createEnvironment(), true, new File(ip.getProperty(JetPluginProperties.PROPERTY_JET_HOME)));
        } catch (java.io.IOException ioe) {
            startServer.fireHandleProgressEvent(null, new JetDeploymentStatus(ActionType.EXECUTE, CommandType.START, StateType.FAILED, createProgressMessage("MSG_STOP_SERVER_FAILED_PD")));
            return null;
        }
    }

    private String createProgressMessage(final String resName) {
        return createProgressMessage(resName, null);
    }

    private String createProgressMessage(final String resName, final String param) {
        return NbBundle.getMessage(JetStopRunnable.class, resName, instanceName, param);
    }

    private NbProcessDescriptor createProcessDescriptor() {
        final String serverLocation = ip.getProperty(JetPluginProperties.PROPERTY_JET_HOME);

        JavaPlatform platform = dm.getProperties().getJavaPlatform();
        FileObject fo = (FileObject) platform.getInstallFolders().iterator().next();
        String javaHome = FileUtil.toFile(fo).getAbsolutePath();
        final String startScript = javaHome + "/bin/java" + (Utilities.isWindows()?".exe":"");;
        final String jarLocation = serverLocation + File.separator + JetStartServer.START_JAR;
        final String params = " " + JetStartServer.JETTY_STOP_KEY_OPT + JetStartServer.JETTY_STOP_KEY + " " + JetStartServer.JETTY_STOP_PORT_OPT + JetStartServer.JETTY_STOP_PORT +
                " -jar " + serverLocation + File.separator + JetStartServer.START_JAR + " --stop";
        if (!new File(startScript).exists() || !new File(jarLocation).exists()) {
            LOGGER.log(Level.WARNING, "Startscript {0} doesn't exist", startScript);
            startServer.fireHandleProgressEvent(null, new JetDeploymentStatus(ActionType.EXECUTE, CommandType.STOP, StateType.FAILED, "MSG_STOP_SERVER_FAILED_FNF"));
            return null;
        }
        LOGGER.log(Level.INFO, "executing: " + startScript + " " + params);
        return new NbProcessDescriptor(startScript, params); //NOI18N
    }
}
