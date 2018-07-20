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
package org.netbeans.modules.j2ee.jetty;

import javax.enterprise.deploy.spi.status.ProgressEvent;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MalformedObjectNameException;
import javax.management.ReflectionException;
import org.netbeans.modules.j2ee.jetty.ide.JetPluginProperties;
import org.netbeans.modules.j2ee.jetty.ide.JetJ2eePlatformImpl;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.enterprise.deploy.model.DeployableObject;
import javax.enterprise.deploy.shared.DConfigBeanVersionType;
import javax.enterprise.deploy.shared.ModuleType;
import javax.enterprise.deploy.spi.DeploymentConfiguration;
import javax.enterprise.deploy.spi.DeploymentManager;
import javax.enterprise.deploy.spi.Target;
import javax.enterprise.deploy.spi.TargetModuleID;
import javax.enterprise.deploy.spi.exceptions.DConfigBeanVersionUnsupportedException;
import javax.enterprise.deploy.spi.exceptions.InvalidModuleException;
import javax.enterprise.deploy.spi.exceptions.TargetException;
import javax.enterprise.deploy.spi.status.ProgressObject;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import org.netbeans.modules.j2ee.deployment.plugins.api.InstanceProperties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.deploy.shared.StateType;
import javax.enterprise.deploy.spi.status.DeploymentStatus;
import javax.enterprise.deploy.spi.status.ProgressListener;
import org.netbeans.modules.j2ee.jetty.ide.JetStartServer;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

/**
 * This class implements DeploymentManager interface from JSR 88 and is 
 * an access point to any deployment-related action with web module.
 * It allows:
 * <ul>
 *  <li> Configuring an application. </li>
 *  <li> Distributing an application. </li>
 *  <li> Starting the application. </li>
 *  <li> Stopping the application. </li>
 *  <li> Undeploying the application. </li>
 * </ul>  
 * @author novakm
 */
public class JetDeploymentManager implements DeploymentManager {

    private static enum COMMAND {

        DEPLOY, START
    }
    /** Enum value for get*Modules methods. */
    static final int ENUM_AVAILABLE = 0;
    static final int ENUM_RUNNING = 1;
    static final int ENUM_NONRUNNING = 2;
    private static final Logger LOGGER = Logger.getLogger(JetDeploymentManager.class.getName());
    private boolean isConnected;
    private String uri;
    private JetPluginProperties ip;
    private InstanceProperties instanceProperties;
    private JetJ2eePlatformImpl JetPlatform;
//    private MBeanServerConnection jmxConnection;
    private Process process;

    /**
     * Constructor for given uri that initializes
     * object's datastructures
     * @param uri - URI for given JetDeploymentManager
     */
    public JetDeploymentManager(String uri) {
        this.uri = uri;
        ip = new JetPluginProperties(this);
        isConnected = true;
    }

    /**
     * Method responsible for distributing web project to web server
     * @param target representing server we want to distribute to
     * @param archiveFile - war file
     * @param deplPlan - plan for deployning web applicatio (not used)
     * @return ProgressObject - representing state of deployment
     * @throws java.lang.IllegalStateException when called on disconnected DM
     */
    public ProgressObject distribute(Target[] target, File archiveFile, File deplPlan) throws IllegalStateException {
        LOGGER.log(Level.FINE, "Distributing to {0}", target);

        if (!isConnected()) {
            throw new IllegalStateException("JetDeploymentManager.distribute called on disconnected instance");   // NOI18N
        }
        String[] parts = uri.split(":");
        String warPath = null;
        if (Utilities.isWindows()) {
            warPath = parts[4] + ":" + parts[5] + File.separator + "webapps";
        } else {
            warPath = parts[4] + File.separator + "webapps";
        }
        JetStartServer jss = new JetStartServer(this);
        ProgressObject po;
        JetProgressUI pui = new JetProgressUI("", false, null);

        boolean restartNeeded = false;
        if (jss.isRunning(true)) {
            restartNeeded = true;
            po = jss.stopDeploymentManager();
            try {
                boolean completed = trackProgressObject(pui, po, JetStartServer.TIMEOUT);
            } catch (TimedOutException ex) {
                LOGGER.log(Level.WARNING, "Stopping Jetty server timeouted", ex);
            }
        }
        JetDeployer jd = new JetDeployer();
        jd.deploy(target[0], archiveFile, warPath);
        if (restartNeeded) {
            po = jss.startDeploymentManager();
            try {
                boolean completed = trackProgressObject(pui, po, JetStartServer.TIMEOUT);
            } catch (TimedOutException ex) {
                LOGGER.log(Level.WARNING, "Starting Jetty server timeouted", ex);
            }
        }
        return jd;
    }

    /**
     * This method is defined only to satisfy JSR 88 implementation, but
     * should not be ever called
     */
    public DeploymentConfiguration createConfiguration(DeployableObject deployableObject) throws InvalidModuleException {
        throw new UnsupportedOperationException("This method should never be called!");
    }

    /**
     * This method is defined only to satisfy JSR 88 implementation, but
     * should not be ever called
     */
    public ProgressObject redeploy(TargetModuleID[] targetModuleID, InputStream inputStream, InputStream inputStream2) throws UnsupportedOperationException, IllegalStateException {
        throw new UnsupportedOperationException("This method should never be called!");
    }

    /**
     * This method is defined only to satisfy JSR 88 implementation, but
     * should not be ever called
     */
    public ProgressObject distribute(Target[] target, InputStream inputStream, InputStream inputStream2) throws IllegalStateException {
        throw new UnsupportedOperationException("This method should never be called!");
    }

    /**
     * This method is defined only to satisfy JSR 88 implementation, but
     * should not be ever called
     */
    public ProgressObject distribute(Target[] arg0, ModuleType arg1, InputStream arg2, InputStream arg3) throws IllegalStateException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * This method stops the web application, destroys its MBean and
     * then removes its web archive from the server.
     * @param targetModuleID representing JetModule to be undeployed
     * @return progressObject with status of the operation
     * @throws java.lang.IllegalStateException
     */
    public ProgressObject undeploy(TargetModuleID[] targetModuleID) throws IllegalStateException {
        JetDeployer jd = new JetDeployer();
        JetModule jt = (JetModule) targetModuleID[0];
        MBeanServerConnection jmxConnection = initJMX();
        if (jmxConnection == null) {
            DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(
                    NbBundle.getMessage(JetDeploymentManager.class, "MSG_CONNECTION_PROBLEM"),
                    NotifyDescriptor.ERROR_MESSAGE));            
            LOGGER.log(Level.WARNING, "Connection problem in undeploy(...)");
            jd.undeployModuleResult(jt, StateType.FAILED);
            return jd;           
        }
        try {
            ObjectName on;
            on = new ObjectName(jt.getObjectNameString());
            jmxConnection.invoke(on, "stop", null, null);
            jmxConnection.invoke(on, "destroy", null, null);
        } catch (InstanceNotFoundException ex) {
            LOGGER.log(Level.WARNING, "MBean " + jt.getObjectNameString() + " is not registered", ex);
            jd.undeployModuleResult(jt, StateType.FAILED);
            return jd;
        } catch (MBeanException ex) {
            LOGGER.log(Level.WARNING, "Method invoked on MBean threw an exception: ", ex);
            jd.undeployModuleResult(jt, StateType.FAILED);
            return jd;
        } catch (MalformedObjectNameException ex) {
            LOGGER.log(Level.WARNING, "Should not happen!", ex);
            jd.undeployModuleResult(jt, StateType.FAILED);
            return jd;
        } catch (ReflectionException ex) {
            LOGGER.log(Level.WARNING, "Exception while trying to invoke MBean's method was thrown: ", ex);
            jd.undeployModuleResult(jt, StateType.FAILED);
            return jd;
        } catch (IOException ex) {
            DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(
                    NbBundle.getMessage(JetDeploymentManager.class, "MSG_CONNECTION_PROBLEM"),
                    NotifyDescriptor.ERROR_MESSAGE));        
            LOGGER.log(Level.WARNING, "Connection problem ", ex);
            jd.undeployModuleResult(jt, StateType.FAILED);
            return jd;
        }
        String warPath = jt.getWarPath();
        FileObject fo = FileUtil.toFileObject(new File(warPath));
        if (fo != null) 
        {
            try {
                fo.delete();
            } catch (IOException ex) {
                LOGGER.log(Level.WARNING, "Unable to delete " + fo.getPath(), ex);
            }
        }
        jd.undeployModuleResult(jt, StateType.COMPLETED);
        return jd;
    }

    /**
     * This method stops the web application
     * @param targetModuleID representing JetModule to be stopped
     * @return progressObject with status of the operation
     * @throws java.lang.IllegalStateException
     */
    public ProgressObject stop(TargetModuleID[] targetModuleID) throws IllegalStateException {
        JetDeployer jd = new JetDeployer();
        JetModule jt = (JetModule) targetModuleID[0];
        MBeanServerConnection jmxConnection = initJMX();
        if (jmxConnection == null) {
            DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(
                    NbBundle.getMessage(JetDeploymentManager.class, "MSG_CONNECTION_PROBLEM"),
                    NotifyDescriptor.ERROR_MESSAGE));        
            LOGGER.log(Level.WARNING, "Connection problem in stop(...)");
            jd.stopModuleResult(jt, StateType.FAILED);
            return jd;            
        }
        try {
            ObjectName on = new ObjectName(jt.getObjectNameString());
            jmxConnection.invoke(on, "stop", null, null);
            jd.stopModuleResult((JetModule) targetModuleID[0], StateType.COMPLETED);
            return jd;
        } catch (InstanceNotFoundException ex) {
            LOGGER.log(Level.WARNING, "MBean " + jt.getObjectNameString() + " is not registered", ex);
            jd.stopModuleResult(jt, StateType.FAILED);
            return jd;
        } catch (MBeanException ex) {
            LOGGER.log(Level.WARNING, "Method invoked on MBean threw an exception: ", ex);
            jd.stopModuleResult(jt, StateType.FAILED);
            return jd;
        } catch (ReflectionException ex) {
            LOGGER.log(Level.WARNING, "Exception while trying to invoke MBean's method was thrown: ", ex);
            jd.undeployModuleResult(jt, StateType.FAILED);
            return jd;
        } catch (MalformedObjectNameException ex) {
            LOGGER.log(Level.WARNING, "Should not happen!", ex);
            jd.stopModuleResult(jt, StateType.FAILED);
            return jd;
        } catch (IOException ex) {
            DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(
                    NbBundle.getMessage(JetDeploymentManager.class, "MSG_CONNECTION_PROBLEM"),
                    NotifyDescriptor.ERROR_MESSAGE));        
            LOGGER.log(Level.WARNING, "Connection problem ", ex);
            jd.stopModuleResult(jt, StateType.FAILED);
            return jd;
        }
    }

    /**
     * This method starts the web application
     * @param targetModuleID representing JetModule to be started
     * @return progressObject with status of the operation
     * @throws java.lang.IllegalStateException
     */
    public ProgressObject start(TargetModuleID[] targetModuleID) throws IllegalStateException {
        JetModule jt = (JetModule) targetModuleID[0];
        JetDeployer jd = new JetDeployer();
        MBeanServerConnection jmxConnection = initJMX();
        if (jmxConnection == null) {
            DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(
                    NbBundle.getMessage(JetDeploymentManager.class, "MSG_CONNECTION_PROBLEM"),
                    NotifyDescriptor.ERROR_MESSAGE));        
            LOGGER.log(Level.WARNING, "Connection problem in start(...)");
            jd.startModuleResult(jt, StateType.FAILED);
            return jd;            
        }
        try {
            ObjectName on = null;
            String son = jt.getObjectNameString();
            if (son == null) {
                on = getJettyObjectName(jt.getName());
            } else {
                on = new ObjectName(jt.getObjectNameString());
            }
            jmxConnection.invoke(on, "start", null, null);
            jd.startModuleResult(jt, StateType.COMPLETED);
            return jd;
        } catch (InstanceNotFoundException ex) {
            LOGGER.log(Level.WARNING, "MBean " + jt.getObjectNameString() + " is not registered", ex);
            jd.startModuleResult(jt, StateType.FAILED);
            return jd;
        } catch (MBeanException ex) {
            LOGGER.log(Level.WARNING, "Method invoked on MBean threw an exception: ", ex);
            jd.startModuleResult(jt, StateType.FAILED);
            return jd;
        } catch (ReflectionException ex) {
            LOGGER.log(Level.WARNING, "Exception while trying to invoke MBean's method was thrown: ", ex);
            jd.startModuleResult(jt, StateType.FAILED);
            return jd;
        } catch (MalformedObjectNameException ex) {
            LOGGER.log(Level.WARNING, "Should not happen!", ex);
            jd.startModuleResult(jt, StateType.FAILED);
            return jd;
        } catch (IOException ex) {
            DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(
                    NbBundle.getMessage(JetDeploymentManager.class, "MSG_CONNECTION_PROBLEM"),
                    NotifyDescriptor.ERROR_MESSAGE));        
            LOGGER.log(Level.WARNING, "Connection problem ", ex);
            jd.startModuleResult(jt, StateType.FAILED);
            return jd;
        }
    }

    /**
     * This method is defined only to satisfy JSR 88 implementation, but
     * should not be ever called
     */
    public void setLocale(java.util.Locale locale) throws UnsupportedOperationException {
    }

    /**
     * This method is defined only to satisfy JSR 88 implementation, but
     * should not be ever called
     */
    public boolean isLocaleSupported(java.util.Locale locale) {
        return false;
    }

    /**
     * This method returns array of available (stopped or running)
     * web modules running on target server
     * @param moduleType - specifies modules of what to return (WAR)
     * @param target - server we want to look for modules
     * @return array of all web modules exposed via MBean on server
     * @throws javax.enterprise.deploy.spi.exceptions.TargetException
     * @throws java.lang.IllegalStateException
     */
    public TargetModuleID[] getAvailableModules(ModuleType moduleType, Target[] target) throws TargetException, IllegalStateException {
        return modules(ENUM_AVAILABLE, moduleType, target);
    }

    /**
     * This method returns array of stopped
     * web modules running on target server
     * @param moduleType - ENUM_NONRUNNING specifying what modules to return
     * @param target - server we want to look for modules
     * @return array of stopped web modules exposed via MBean on server
     * @throws javax.enterprise.deploy.spi.exceptions.TargetException
     * @throws java.lang.IllegalStateException
     */
    public TargetModuleID[] getNonRunningModules(ModuleType moduleType, Target[] target) throws TargetException, IllegalStateException {
        return modules(ENUM_NONRUNNING, moduleType, target);
    }

    /**
     * This method returns array of running
     * web modules running on target server
     * @param moduleType - ENUM_NONRUNNING specifying what modules to return
     * @param target - server we want to look for modules
     * @return array of running web modules exposed via MBean on server
     * @throws javax.enterprise.deploy.spi.exceptions.TargetException
     * @throws java.lang.IllegalStateException
     */
    public TargetModuleID[] getRunningModules(ModuleType moduleType, Target[] target) throws TargetException, IllegalStateException {
        return modules(ENUM_RUNNING, moduleType, target);
    }

    /**
     * This method is defined only to satisfy JSR 88 implementation, but
     * should not be ever called
     */
    public ProgressObject redeploy(TargetModuleID[] targetModuleID, File file, File file2) throws UnsupportedOperationException, IllegalStateException {
        return null;
    }

    /**
     * This method is defined only to satisfy JSR 88 implementation, but
     * should not be ever called
     */
    public void setDConfigBeanVersion(DConfigBeanVersionType dConfigBeanVersionType) throws DConfigBeanVersionUnsupportedException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * This method is defined only to satisfy JSR 88 implementation, but
     * should not be ever called
     */
    public boolean isDConfigBeanVersionSupported(DConfigBeanVersionType dConfigBeanVersionType) {
        return false;
    }

    /**
     * Method causing to release all resources and disconnecting the DM
     */
    public void release() {
    }

    /**
     * Redeploy is not supported
     */
    public boolean isRedeploySupported() {
        return false;
    }

    /**
     * This method is defined only to satisfy JSR 88 implementation, but
     * should not be ever called
     */
    public java.util.Locale getCurrentLocale() {
        return null;
    }

    /**
     * This method is defined only to satisfy JSR 88 implementation, but
     * should not be ever called
     */
    public DConfigBeanVersionType getDConfigBeanVersion() {
        return null;
    }

    /**
     * This method is defined only to satisfy JSR 88 implementation, but
     * should not be ever called
     */
    public java.util.Locale getDefaultLocale() {
        return null;
    }

    /**
     * This method is defined only to satisfy JSR 88 implementation, but
     * should not be ever called
     */
    public java.util.Locale[] getSupportedLocales() {
        return null;
    }

    /**
     * This methods returns target representing server we can work with
     * @return array of targets
     * @throws java.lang.IllegalStateException
     */
    public Target[] getTargets() throws IllegalStateException {
        if (!isConnected()) {
            throw new IllegalStateException("getTargets called on disconnected instance");   // NOI18N

        }
        return new JetTarget[]{
                    new JetTarget(uri, "Jetty at " + uri, uri)
                };
    }

    /**
     * This method initializase MBeanServerConnection to the server
     */
    public MBeanServerConnection initJMX() {
        MBeanServerConnection jmxConnection = null;
        try {
            int rmiPort = Integer.parseInt(getInstanceProperties().getProperty(JetPluginProperties.RMI_PORT_PROP));
            JMXServiceURL target = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://" + JetPluginProperties.PROPERTY_HOST + ":" + rmiPort + "/jmxrmi");
            JMXConnector connector = JMXConnectorFactory.connect(target);
            jmxConnection = connector.getMBeanServerConnection();
            return jmxConnection;
        } catch (Exception ex) {
            LOGGER.log(Level.FINE, "Exception in initJMX(): ", ex);
            return jmxConnection;
        }
    }

    @SuppressWarnings("unchecked")
    private TargetModuleID[] loadTargetModuleIDs() {
        Vector<TargetModuleID> result = new Vector<TargetModuleID>();
        initJMX();
        MBeanServerConnection jmxConnection = initJMX();
        if (jmxConnection != null) {
//            if the connection is OK, proceed
            try {
                Hashtable<String, String> ht = new Hashtable<String, String>();
                ObjectName on = new ObjectName("org.mortbay.jetty.webapp:*");
                Set<ObjectName> names = jmxConnection.queryNames(on, null);
                for (ObjectName objectName : names) {
                    String son = "" + objectName;
                    String[] parts = son.split(",");
                    String newID = parts[parts.length - 1].split("=")[1];
                    String name = parts[parts.length - 2].split("=")[1];
                    String oldID = ht.get(name);
                    if (oldID == null) {
                        ht.put(name, newID);
                    } else {
                        if (oldID.compareTo(newID) < 0) {
                            ht.put(name, newID);
                        }
                    }
                }
                Enumeration e = ht.keys();
                while (e.hasMoreElements()) {
                    String key = (String) e.nextElement();
                    String name = key;
                    String son = "org.mortbay.jetty.webapp:type=webappcontext,name=" + key + ",id=" + ht.get(key);
                    on = new ObjectName(son);
                    String war = (String) jmxConnection.getAttribute(on, "war");
                    war = war.replace("jar:", "");
                    war = war.replace("file:/", "");
                    if (!Utilities.isWindows()) {
                        war = File.separator+war;
                    }
                    war = war.replace("!", "");
                    String contextPath = (String) jmxConnection.getAttribute(on, "contextPath");
                    Target t = new JetTarget(uri, "Jetty at " + uri, uri);
                    result.add(new JetModule(t, name, son, contextPath, war));
                }
            } catch (Exception ex) {
                LOGGER.log(Level.WARNING, "Exception in loadTargetModuleIDs: ", ex);
            }
        } else {
//            otherwise do nothing, this way we will return new (though empty) array
        }
        return result.toArray(new TargetModuleID[result.size()]);
    }

    private boolean isConnected() {
        return isConnected;
    }

    private ObjectName getJettyObjectName(String name) {
        try {
            return new ObjectName("org.mortbay.jetty.webapp:type=webappcontext,name=" + name + ",id=0");
        } catch (Exception ex) {
            LOGGER.log(Level.FINE, "", ex);
            return null;
        }
    }

    /**
     * Returns URI of the deployment manager
     * @return uri
     */
    public String getUri() {
        return uri;
    }

    /**
     * Returns JetPluginProperties of the deployment manager
     *
     * @return JetPluginProperties
     */
    public JetPluginProperties getProperties() {
        return ip;
    }

    /**
     * Returns InstanceProperties of the deployment manager
     *
     * @return InstanceProperties
     */
    public InstanceProperties getInstanceProperties() {
        if (instanceProperties == null) {
            instanceProperties = InstanceProperties.getInstanceProperties(getUri());
        }
        return instanceProperties;
    }

    public JetJ2eePlatformImpl getJetPlatform() {
        if (JetPlatform == null) {
            JetPlatform = (JetJ2eePlatformImpl) new JetJ2eePlatformImpl(this);
        }
        return JetPlatform;
    }

    /**
     * Sets process of deployment manager
     * @param process
     */
    public synchronized void setServerProcess(Process process) {
        this.process = process;
    }

    /**
     * Returns process of deployment manager
     * @return process
     */
    public synchronized Process getServerProcess() {
        return process;
    }

    @SuppressWarnings("unchecked")
    private TargetModuleID[] modules(int state, ModuleType moduleType, Target[] target) throws TargetException {
        if (!isConnected) {
            throw new IllegalStateException("Called on disconnected instance");   // NOI18N

        }
        if (target.length != 1) {
            throw new TargetException("Supporting only one target");   // NOI18N

        }
        if (!ModuleType.WAR.equals(moduleType)) {
            return new TargetModuleID[0];
        }
        java.util.List modules = new java.util.ArrayList();
        TargetModuleID[] tmids = loadTargetModuleIDs();
        for (int i = 0; i < tmids.length; i++) {
            JetModule jm = (JetModule) tmids[i];
            switch (state) {
                case ENUM_AVAILABLE:
                    modules.add(jm);
                    break;
                case ENUM_RUNNING:
                    if (isJetModuleRunning(jm)) {
                        modules.add(jm);
                    }
                    break;
                case ENUM_NONRUNNING:
                    if (!isJetModuleRunning(jm)) {
                        modules.add(jm);
                    }
                    break;
            }
        }
        return (TargetModuleID[]) modules.toArray(new TargetModuleID[modules.size()]);
    }

    /**
     * Method that discovers whether is given JetModule running on the server
     * @param jm - JetModule
     * @return true if it is running, false otherwise
     */
    public boolean isJetModuleRunning(JetModule jm) {
        boolean runs=false;
        MBeanServerConnection jmxConnection = initJMX();
        if (jmxConnection == null) {
            return runs;
        } else {
            try {
                ObjectName on = new ObjectName(jm.getObjectNameString());
                runs = (Boolean) jmxConnection.getAttribute(on, "running");
            } catch (Exception ex) {
                LOGGER.log(Level.FINE, "Exception in isJetModuleRunning: ", ex);
            }
            return runs;
        }
    }

    /**
     * Waits till the progress object is in final state or till the timeout runs out.
     *
     * @param ui progress ui which will be notified about progress object changes .
     * @param po progress object which will be tracked.
     * @param timeout timeout in millis. Zero timeout means unlimited timeout.
     *
     * @return true if the progress object completed successfully, false otherwise.
     *         This is a workaround for issue 82428.
     * 
     * @throws TimedOutException when the task times out.
     */
    private boolean trackProgressObject(JetProgressUI ui, final ProgressObject po, long timeout) throws TimedOutException {
        assert po != null;
        assert ui != null;
        final AtomicBoolean completed = new AtomicBoolean();
        ui.setProgressObject(po);
        try {
            final CountDownLatch progressFinished = new CountDownLatch(1);
            ProgressListener listener = new ProgressListener() {

                public void handleProgressEvent(ProgressEvent progressEvent) {
                    DeploymentStatus status = progressEvent.getDeploymentStatus();
                    if (status.isCompleted()) {
                        completed.set(true);
                    }
                    if (status.isCompleted() || status.isFailed()) {
                        progressFinished.countDown();
                    }
                }
            };
            po.addProgressListener(listener);
            try {
                // the completion event might have arrived before the progress listener 
                // was registered, wait only if not yet finished
                DeploymentStatus status = po.getDeploymentStatus();
                if (!status.isCompleted() && !status.isFailed()) {
                    try {
                        if (timeout == 0) {
                            progressFinished.await();
                        } else {
                            progressFinished.await(timeout, TimeUnit.MILLISECONDS);
                            if (progressFinished.getCount() > 0) {
                                throw new TimedOutException();
                            }
                        }
                    } catch (InterruptedException e) {
                        LOGGER.log(Level.INFO, null, e);
                    }
                } else if (status.isCompleted()) {
                    completed.set(true);
                }
            } finally {
                po.removeProgressListener(listener);
            }
        } finally {
            ui.setProgressObject(null);
        }
        return completed.get();
    }

    private class TimedOutException extends Exception {
    }
}