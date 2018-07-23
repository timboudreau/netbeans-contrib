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

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import org.netbeans.modules.j2ee.jetty.*;
import javax.enterprise.deploy.spi.DeploymentManager;
import javax.enterprise.deploy.spi.Target;
import javax.enterprise.deploy.spi.TargetModuleID;
import javax.enterprise.deploy.spi.exceptions.OperationUnsupportedException;
import javax.enterprise.deploy.spi.status.ClientConfiguration;
import javax.enterprise.deploy.spi.status.DeploymentStatus;
import javax.enterprise.deploy.spi.status.ProgressListener;
import javax.enterprise.deploy.spi.status.ProgressObject;
import org.netbeans.modules.j2ee.deployment.plugins.api.InstanceProperties;
import org.netbeans.modules.j2ee.deployment.plugins.api.ServerDebugInfo;
import org.netbeans.modules.j2ee.deployment.plugins.spi.StartServer;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.deploy.shared.ActionType;
import javax.enterprise.deploy.shared.CommandType;
import javax.enterprise.deploy.shared.StateType;
import javax.enterprise.deploy.spi.status.ProgressEvent;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;

/**
 * This class is an implementation of the StartServer interface.
 * Its purpose is to provide ability to start, stop, and determine 
 * the state of the server.
 * @author novakm
 */
public class JetStartServer extends StartServer implements ProgressObject {

    static enum MODE {
        RUN, DEBUG
    }
    protected static final String JETTY_STOP_KEY_OPT = "-DSTOP.KEY=";
    protected static String JETTY_STOP_KEY = "h4rd.T0_Gue3s";
    protected static final String JETTY_STOP_PORT_OPT = "-DSTOP.PORT=";
    protected static int JETTY_STOP_PORT = 8778;
    protected static final String START_JAR = "start.jar";
    protected static final String JVM_OPTS = "-Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.port=";
    protected static final String JETTY_XML = "/etc/jetty.xml";
    protected static final String JETTY_XML_JMX = "/etc/jetty-jmx.xml";
    protected static String JAVA_PARAMS = "";
    private MODE mode;
    private DeploymentStatus deploymentStatus;
    private JetDeploymentManager dm;
    private Vector<ProgressListener> listeners = new Vector<ProgressListener>();
    private InstanceProperties ip;
    @SuppressWarnings("unchecked")
    private static Map isDebugModeUri = Collections.synchronizedMap((Map) new HashMap(2, 1));
    private String url;
    private static final Logger LOGGER = Logger.getLogger(JetStartServer.class.getName());
    /**
     * The amount of time in milliseconds during which the server should
     * start
     */
    public static final int TIMEOUT = 20000;
    /**
     * The amount of time in milliseconds that we should wait between checks
     */
    public static final int DELAY = 1000;
//    private static final Logger LOGGER = Logger.getLogger(JetStartServer.class.getName());    

    /**
     * Constructs new object and initializes its variables for given DeploymentManager
     * @param dm - DeploymentManager
     */
    public JetStartServer(DeploymentManager dm) {
        if (!(dm instanceof JetDeploymentManager)) {
            throw new IllegalArgumentException("Only JetDeplomentManager is supported"); //NOI18N

        }
        this.dm = (JetDeploymentManager) dm;
        this.ip = ((JetDeploymentManager) dm).getProperties().getInstanceProperties();
        url = ip.getProperty(InstanceProperties.URL_ATTR);
    }

    /**
     * Debugging is not yet support.
     * @param target target server
     * @return null
     */
    public ProgressObject startDebugging(Target target) {
        return null;
    }
    /**
     * Debugging is not yet support.
     * @return false
     */

    public boolean isDebuggable(Target target) {
        return false;
    }

    public boolean isAlsoTargetServer(Target target) {
        return true;
    }
    
    /**
     * Debugging is not yet support.
     * @return null
     */
    public ServerDebugInfo getDebugInfo(Target target) {
        return null;
    }

    public boolean supportsStartDeploymentManager() {
        return true;
    }

    /**
     * Executes process that will stop running server in another thread
     * @return ProgressObject that can be queried for its status
     */
    public ProgressObject stopDeploymentManager() {
        mode = MODE.RUN;
        String srvrName = ip.getProperty(InstanceProperties.DISPLAY_NAME_ATTR);
        fireHandleProgressEvent(null, new JetDeploymentStatus(ActionType.EXECUTE, CommandType.STOP, StateType.RUNNING, NbBundle.getMessage(JetStartServer.class, "MSG_STOP_SERVER_IN_PROGRESS", srvrName))); //NOI18N
        // run the startup process in a separate thread

        RequestProcessor.getDefault().post(new JetStopRunnable(dm, this), 0, Thread.NORM_PRIORITY);
        removeDebugModeUri();
        return this;
    }
    
    /**
     * Executes process that will start the server in another thread
     * @return ProgressObject that can be queried for its status
     */
    public ProgressObject startDeploymentManager() {
        mode = MODE.RUN;
        String srvrName = ip.getProperty(InstanceProperties.DISPLAY_NAME_ATTR);
        fireHandleProgressEvent(null, new JetDeploymentStatus(ActionType.EXECUTE, CommandType.START, StateType.RUNNING, NbBundle.getMessage(JetStartServer.class, "MSG_START_SERVER_IN_PROGRESS", srvrName))); //NOI18N
        // run the startup process in a separate thread
        RequestProcessor.getDefault().post(new JetStartRunnable(dm, this), 0, Thread.NORM_PRIORITY);
        removeDebugModeUri();
        return this;
    }

    private void removeDebugModeUri() {
        isDebugModeUri.remove(url);
    }

    /**
     * Notifies all listeners about change of status
     * @param targetModuleID 
     * @param deploymentStatus - new deployment status
     */
    public void fireHandleProgressEvent(TargetModuleID targetModuleID, DeploymentStatus deploymentStatus) {
        ProgressEvent evt = new ProgressEvent(this, targetModuleID, deploymentStatus);
        this.deploymentStatus = deploymentStatus;
        java.util.Vector targets = null;
        synchronized (this) {
            if (listeners != null) {
                targets = (java.util.Vector) listeners.clone();
            }
        }
        if (targets != null) {
            for (int i = 0; i < targets.size(); i++) {
                ProgressListener target = (ProgressListener) targets.elementAt(i);
                target.handleProgressEvent(evt);
            }
        }
    }

    public boolean needsStartForTargetList() {
        return false;
    }

    public boolean needsStartForConfigure() {
        return false;
    }

    public boolean needsStartForAdminConfig() {
        return false;
    }

    /**
     * Method that is called by IDE to check whether the server is running.
     * By default it does only basic check, but is overloaded to provide
     * additional checks.
     */
    public boolean isRunning() {
        String host = JetPluginProperties.PROPERTY_HOST;
        int port = JetPluginProperties.HTTP_PORT;
        return isRunning(host, port, true);
    }

    /**
     * Overloaded method that checks whether the server is running
     * @param reallyRunning - boolean that represents whether we want
     * to perform additional tests
     * @return true if it runs, false otherwise
     */
    public boolean isRunning(boolean reallyRunning) {
        String host = JetPluginProperties.PROPERTY_HOST;
        int port = JetPluginProperties.HTTP_PORT;
        return isRunning(host, port, reallyRunning);
    }

    /**
     * The core function that checks whether the server is running
     * @param host - hostname on which the server should be running
     * @param port - port number on which the server should be running
     * @param reallyRunning - boolean representing whether we want to perform
     * additional checks like pinging web address and query MBean of the
     * server for running attribute
     * @return true if the server with location taken from its intstance properties
     * really runs, false otherwise
     */
    protected boolean isRunning(String host, int port, boolean reallyRunning) {
        if (null == host) {
            return false;
        }
        MBeanServerConnection jmxConnection = dm.initJMX();
        if (jmxConnection == null) {
            return false;
        }
        try {
            RuntimeMXBean remoteRuntime =
                    ManagementFactory.newPlatformMXBeanProxy(
                    jmxConnection,
                    ManagementFactory.RUNTIME_MXBEAN_NAME,
                    RuntimeMXBean.class);
            String jmxJettyHome = remoteRuntime.getSystemProperties().get("jetty.home");
            String ipJettyHome = ip.getProperty(JetPluginProperties.PROPERTY_JET_HOME);
            if (!jmxJettyHome.equals(ipJettyHome)) {
                return false;
            }
        } catch (Exception ex) {
            LOGGER.log(Level.FINE, "Connection problem in isRunning!", ex);
            return false;
        }
        boolean runs = false;
        try {
            ObjectName on = new ObjectName("org.mortbay.jetty:type=server,id=0");
            runs = jmxConnection.isRegistered(on);
            if (reallyRunning) {
                runs = canBePinged(host, port); 
            }
        } catch (Exception ex) {
            LOGGER.log(Level.FINE, "Exception in isRunning!", ex);
            return false;
        }
        return runs;
    }

    /**
     * This method "pings" the URL host:port to check whether the webserver
     * is running
     * @param host - hostname
     * @param port - port number
     * @return true if it can be pinged, false otherwise
     */
    private boolean canBePinged(String host, int port) {
        try {
            InetSocketAddress isa = new InetSocketAddress(host, port);
            Socket socket = new Socket();
            socket.connect(isa, 2);
            socket.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * @return deployment status of operation
     */
    public DeploymentStatus getDeploymentStatus() {
        return deploymentStatus;
    }

    /**
     * Dummy implementation to satisfy ProgressObject interface, is
     * not used anywhere
     */
    public TargetModuleID[] getResultTargetModuleIDs() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    /**
     * Dummy implementation to satisfy ProgressObject interface, is
     * not used anywhere
     */
    public ClientConfiguration getClientConfiguration(TargetModuleID arg0) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Dummy implementation to satisfy ProgressObject interface, is
     * not used anywhere
     */
    public boolean isCancelSupported() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Dummy implementation to satisfy ProgressObject interface, is
     * not used anywhere
     */
    public void cancel() throws OperationUnsupportedException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Dummy implementation to satisfy ProgressObject interface, is
     * not used anywhere
     */    
    public boolean isStopSupported() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Dummy implementation to satisfy ProgressObject interface, is
     * not used anywhere
     */    
    public void stop() throws OperationUnsupportedException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * adds progresslistener to those that want to be notified about 
     * status changes
     * @param pl - progressListener
     */
    public void addProgressListener(ProgressListener pl) {
        listeners.add(pl);
    }

    /**
     * removes progresslistener from those that want to be notified about 
     * status changes
     * @param pl - progressListener
     */
    public void removeProgressListener(ProgressListener pl) {
        listeners.remove(pl);
    }
    
    /**
     * @return mode of the server
     */
    protected MODE getMode() {
        return mode;
    }
}