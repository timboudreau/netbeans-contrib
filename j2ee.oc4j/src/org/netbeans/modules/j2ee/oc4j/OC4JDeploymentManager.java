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

package org.netbeans.modules.j2ee.oc4j;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;
import javax.enterprise.deploy.model.DeployableObject;
import javax.enterprise.deploy.shared.ActionType;
import javax.enterprise.deploy.shared.CommandType;
import javax.enterprise.deploy.shared.DConfigBeanVersionType;
import javax.enterprise.deploy.shared.ModuleType;
import javax.enterprise.deploy.shared.StateType;
import javax.enterprise.deploy.spi.DeploymentConfiguration;
import javax.enterprise.deploy.spi.DeploymentManager;
import javax.enterprise.deploy.spi.Target;
import javax.enterprise.deploy.spi.TargetModuleID;
import javax.enterprise.deploy.spi.exceptions.DConfigBeanVersionUnsupportedException;
import javax.enterprise.deploy.spi.exceptions.DeploymentManagerCreationException;
import javax.enterprise.deploy.spi.exceptions.InvalidModuleException;
import javax.enterprise.deploy.spi.exceptions.OperationUnsupportedException;
import javax.enterprise.deploy.spi.exceptions.TargetException;
import javax.enterprise.deploy.spi.factories.DeploymentFactory;
import javax.enterprise.deploy.spi.status.ClientConfiguration;
import javax.enterprise.deploy.spi.status.DeploymentStatus;
import javax.enterprise.deploy.spi.status.ProgressEvent;
import javax.enterprise.deploy.spi.status.ProgressListener;
import javax.enterprise.deploy.spi.status.ProgressObject;
import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import javax.swing.JOptionPane;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.modules.j2ee.dd.api.application.Application;
import org.netbeans.modules.j2ee.dd.api.application.DDProvider;
import org.netbeans.modules.j2ee.dd.api.application.Module;
import org.netbeans.modules.j2ee.deployment.plugins.api.InstanceProperties;
import org.netbeans.modules.j2ee.oc4j.config.EarDeploymentConfiguration;
import org.netbeans.modules.j2ee.oc4j.config.EjbDeploymentConfiguration;
import org.netbeans.modules.j2ee.oc4j.config.WarDeploymentConfiguration;
import org.netbeans.modules.j2ee.oc4j.config.gen.OrionWebApp;
import org.netbeans.modules.j2ee.oc4j.ide.OC4JDeploymentStatus;
import org.netbeans.modules.j2ee.oc4j.ide.OC4JErrorManager;
import org.netbeans.modules.j2ee.oc4j.ide.OC4JJ2eePlatformImpl;
import org.netbeans.modules.j2ee.oc4j.ide.OC4JLogger;
import org.netbeans.modules.j2ee.oc4j.util.OC4JPluginProperties;
import org.netbeans.modules.j2ee.oc4j.util.OC4JDebug;
import org.netbeans.modules.j2ee.oc4j.util.OC4JPluginUtils;
import org.netbeans.modules.web.api.webmodule.WebModule;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.JarFileSystem;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;

/**
 *
 * @author pblaha
 */
public class OC4JDeploymentManager implements DeploymentManager, ProgressObject, Runnable {
    static enum COMMAND { DEPLOY, START }
    static final String URI_PREFIX = "deployer:"; // NOI18N
    
    private String uri;
    private String uname;
    private String passwd;
    private OC4JPluginProperties ip;
    private DeploymentFactory dmF;
    private DeploymentManager dm;
    private Object oc4jDM;
    private OC4JClassLoader loader;
    private boolean isConnected;
    private Vector listeners = new Vector();
    private DeploymentStatus deploymentStatus;
    private OC4JTargetModuleID module_id;
    private File file;
    private TargetModuleID[] modules;
    private COMMAND command;
    private MBeanServerConnection jmxConnection;
    private InstanceProperties instanceProperties;
    private OC4JJ2eePlatformImpl oc4jPlatform;
    
    public OC4JDeploymentManager(String uri, String uname, String passwd) {
        this.uri = uri;
        this.uname = uname;
        this.passwd = passwd;
        ip = new OC4JPluginProperties(this);
    }
    
    public String getUri() {
        return uri;
    }
    
    public OC4JPluginProperties getProperties() {
        return ip;
    }
    
    public InstanceProperties getInstanceProperties() {
        if (instanceProperties == null)
            instanceProperties = InstanceProperties.getInstanceProperties(getUri());
        
        return instanceProperties;
    }
    
    public ProgressObject distribute(Target[] target, File file, File file2) throws IllegalStateException {
        loadOC4JDeploymentManager();
        
        module_id = new OC4JTargetModuleID(target[0], file.getName());
        this.file = file;
        InstanceProperties ip = getProperties().getInstanceProperties();
        
        try{
            String server_url = "http://" + ip.getProperty(OC4JPluginProperties.PROPERTY_HOST) + ":" +
                    ip.getProperty(InstanceProperties.HTTP_PORT_NUMBER);
            if (file.getName().endsWith(".war")) { // NOI18N
                module_id.setContextURL(server_url + OrionWebApp.createGraph(file2).getContextRoot());
            } else if (file.getName().endsWith(".ear")) { // NOI18N
                JarFileSystem jfs = new JarFileSystem();
                jfs.setJarFile(file);
                FileObject appXml = jfs.getRoot().getFileObject("META-INF/application.xml"); // NOI18N
                if (appXml != null) {
                    Application ear = DDProvider.getDefault().getDDRoot(appXml);
                    Module modules [] = ear.getModule();
                    for (int i = 0; i < modules.length; i++) {
                        OC4JTargetModuleID mod_id = new OC4JTargetModuleID(target[0]);
                        if (modules[i].getWeb() != null) {
                            mod_id.setContextURL(server_url + modules[i].getWeb().getContextRoot());
                        }
                        module_id.addChild(mod_id);
                    }
                } else {
                    ErrorManager.getDefault().log("Cannot file META-INF/application.xml in " + file); // NOI18N
                }
            }
        } catch (Exception e){
            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
        }
        
        command = COMMAND.DEPLOY;
        fireHandleProgressEvent(null, new OC4JDeploymentStatus(ActionType.EXECUTE, CommandType.DISTRIBUTE, StateType.RUNNING, NbBundle.getMessage(OC4JDeploymentManager.class, "MSG_DEPLOYING", file.getAbsolutePath())));
        RequestProcessor.getDefault().post(this, 0, Thread.NORM_PRIORITY);
        
        return this;
    }
    
    public ProgressObject distribute(Target[] target, ModuleType type,
            InputStream inputStream, InputStream inputStream2) throws IllegalStateException {
        return distribute(target, inputStream, inputStream2);
    }
    
    public DeploymentConfiguration createConfiguration(DeployableObject deployableObject) throws InvalidModuleException {
        ModuleType type = deployableObject.getType();
        if (type == ModuleType.WAR) {
            return new WarDeploymentConfiguration(deployableObject);
        } else if (type == ModuleType.EAR) {
            return new EarDeploymentConfiguration(deployableObject);
        } else if (type == ModuleType.EJB) {
            return new EjbDeploymentConfiguration(deployableObject);
        } else {
            throw new InvalidModuleException("Unsupported module type: " + type.toString()); // NOI18N
        }
        
    }
    
    public ProgressObject redeploy(TargetModuleID[] targetModuleID, InputStream inputStream, InputStream inputStream2) throws UnsupportedOperationException, IllegalStateException {
        updateDeploymentManager();
        
        if (!isConnected) {
            throw new IllegalStateException(NbBundle.getMessage(
                    OC4JDeploymentManager.class, "MSG_ERROR_DISC_MANAGER"));   // NOI18N
        }
        
        loader.updateLoader();
        
        try {
            return dm.redeploy(targetModuleID, inputStream, inputStream2);
        } finally {
            loader.restoreLoader();
        }
    }
    
    public ProgressObject distribute(Target[] target, InputStream inputStream, InputStream inputStream2) throws IllegalStateException {
        updateDeploymentManager();
        
        if (!isConnected) {
            throw new IllegalStateException(NbBundle.getMessage(
                    OC4JDeploymentManager.class, "MSG_ERROR_DISC_MANAGER"));   // NOI18N
        }
        
        loader.updateLoader();
        
        try {
            return dm.distribute(target, inputStream, inputStream2);
        } finally {
            loader.restoreLoader();
        }
    }
    
    public ProgressObject undeploy(TargetModuleID[] targetModuleID) throws IllegalStateException {
        updateDeploymentManager();
        
        if (!isConnected) {
            throw new IllegalStateException(NbBundle.getMessage(
                    OC4JDeploymentManager.class, "MSG_ERROR_DISC_MANAGER"));   // NOI18N
        }
        
        loader.updateLoader();
        
        try {
            return dm.undeploy(targetModuleID);
        } finally {
            loader.restoreLoader();
        }
    }
    
    public ProgressObject redeploy(TargetModuleID[] targetModuleID, File file, File file2) throws UnsupportedOperationException, IllegalStateException {
        updateDeploymentManager();
        
        if (!isConnected) {
            throw new IllegalStateException(NbBundle.getMessage(
                    OC4JDeploymentManager.class, "MSG_ERROR_DISC_MANAGER"));   // NOI18N
        }
        
        loader.updateLoader();
        
        try {
            return dm.redeploy(targetModuleID, file, file2);
        } finally {
            loader.restoreLoader();
        }
    }
    
    public ProgressObject stop(TargetModuleID[] targetModuleID) throws IllegalStateException {
        updateDeploymentManager();
        
        if (!isConnected) {
            throw new IllegalStateException(NbBundle.getMessage(
                    OC4JDeploymentManager.class, "MSG_ERROR_DISC_MANAGER"));   // NOI18N
        }
        
        loader.updateLoader();
        
        try {
            return dm.stop(targetModuleID);
        } finally {
            loader.restoreLoader();
        }
    }
    
    public ProgressObject start(TargetModuleID[] targetModuleID) throws IllegalStateException {
        loadOC4JDeploymentManager();
        
        modules = targetModuleID;
        command = COMMAND.START;
        fireHandleProgressEvent(null, new OC4JDeploymentStatus(ActionType.EXECUTE, CommandType.START, StateType.RUNNING, NbBundle.getMessage(OC4JDeploymentManager.class, "MSG_STARTING_APP")));
        RequestProcessor.getDefault().post(this, 0, Thread.NORM_PRIORITY);
        
        return this;
    }
    
    public void setLocale(java.util.Locale locale) throws UnsupportedOperationException {
        updateDeploymentManager();
        
        dm.setLocale(locale);
    }
    
    public boolean isLocaleSupported(java.util.Locale locale) {
        updateDeploymentManager();
        
        return dm.isLocaleSupported(locale);
    }
    
    public TargetModuleID[] getAvailableModules(ModuleType moduleType, Target[] target) throws TargetException, IllegalStateException {
        updateDeploymentManager();
        
        return dm.getAvailableModules(moduleType, target);
    }
    
    public TargetModuleID[] getNonRunningModules(ModuleType moduleType, Target[] target) throws TargetException, IllegalStateException {
        updateDeploymentManager();
        
        return dm.getNonRunningModules(moduleType, target);
    }
    
    public TargetModuleID[] getRunningModules(ModuleType moduleType, Target[] target) throws TargetException, IllegalStateException {
        updateDeploymentManager();
        
        if (!isConnected) {
            throw new IllegalStateException(NbBundle.getMessage(
                    OC4JDeploymentManager.class, "MSG_ERROR_DISC_MANAGER"));   // NOI18N
        }
        
        loader.updateLoader();
        
        try {
            return dm.getRunningModules(moduleType, target);
        } finally {
            loader.restoreLoader();
        }
    }
    
    public void setDConfigBeanVersion(DConfigBeanVersionType dConfigBeanVersionType) throws DConfigBeanVersionUnsupportedException {
        updateDeploymentManager();
        
        dm.setDConfigBeanVersion(dConfigBeanVersionType);
    }
    
    public boolean isDConfigBeanVersionSupported(DConfigBeanVersionType dConfigBeanVersionType) {
        updateDeploymentManager();
        
        return dm.isDConfigBeanVersionSupported(dConfigBeanVersionType);
    }
    
    public void release() {
        if(dm != null) {
            dm.release();
        }
    }
    
    public boolean isRedeploySupported() {
        updateDeploymentManager();
        
        return dm.isRedeploySupported();
    }
    
    public java.util.Locale getCurrentLocale() {
        updateDeploymentManager();
        
        return dm.getCurrentLocale();
    }
    
    public DConfigBeanVersionType getDConfigBeanVersion() {
        updateDeploymentManager();
        
        return dm.getDConfigBeanVersion();
    }
    
    public java.util.Locale getDefaultLocale() {
        updateDeploymentManager();
        
        return dm.getDefaultLocale();
    }
    
    public java.util.Locale[] getSupportedLocales() {
        updateDeploymentManager();
        
        return dm.getSupportedLocales();
    }
    
    public Target[] getTargets() {
        updateDeploymentManager();
        
        OC4JDebug.log(getClass().getName(), "getTargets for Deployment Maneger");
        try{
            return dm.getTargets();
        } catch(Throwable e) {
            
        }
        return null;
    }
    
    private void loadDeploymentFactory() {
        if (OC4JDebug.isEnabled()) {
            System.out.println("loadDeploymentFactory");
        }
        String serverRoot = ip.getInstanceProperties().getProperty(OC4JPluginProperties.PROPERTY_OC4J_HOME);
        if (dmF == null && serverRoot != null) {
            loader = OC4JClassLoader.getInstance(serverRoot);
            if (OC4JDebug.isEnabled()) {
                System.out.println("loadDeplomentFactory: serverRoot=" + serverRoot);
            }
            loader.updateLoader();
            
            try {
                dmF = (DeploymentFactory) loader.loadClass(
                        "oracle.oc4j.admin.deploy.spi.factories.Oc4jDeploymentFactory"). // NOI18N
                        newInstance();
            } catch (ClassNotFoundException e) {
                ErrorManager.getDefault().notify(ErrorManager.ERROR, e);
            } catch (InstantiationException e) {
                ErrorManager.getDefault().notify(ErrorManager.ERROR, e);
            } catch (IllegalAccessException e) {
                ErrorManager.getDefault().notify(ErrorManager.ERROR, e);
            } finally {
                loader.restoreLoader();
            }
        }
    }
    
    private void updateDeploymentManager() {
        loadDeploymentFactory();
        
        if(loader != null) {
            loader.updateLoader();
            try {
                if (dm != null) {
                    dm.release();
                }
                if(!OC4JPluginProperties.isRunning(getInstanceProperties().getProperty(OC4JPluginProperties.PROPERTY_HOST),
                        getInstanceProperties().getProperty(InstanceProperties.HTTP_PORT_NUMBER)))
                    throw new DeploymentManagerCreationException("Server is OFF");
                dm = dmF.getDeploymentManager(URI_PREFIX + uri, uname, passwd);
                isConnected = true;
            } catch (Exception e) {
                OC4JErrorManager.getInstance(this).error(uri, e, OC4JErrorManager.GENERIC_FAILURE);
                
                isConnected = false;
                
                try {
                    dm = dmF.getDisconnectedDeploymentManager(uri);
                } catch (DeploymentManagerCreationException ex) {
                    ErrorManager.getDefault().notify(ErrorManager.ERROR, ex);
                }
            } finally {
                loader.restoreLoader();
            }
        }
    }
    
    /**
     *  Loads proprietary deployment manager
     */
    private void loadOC4JDeploymentManager() {
        String serverRoot = null;
        serverRoot = ip.getInstanceProperties().getProperty(OC4JPluginProperties.PROPERTY_OC4J_HOME);
        if (oc4jDM == null && serverRoot != null) {
            loader = OC4JClassLoader.getInstance(serverRoot);
            loader.updateLoader();
            try {
                Class cls = loader.loadClass("oracle.oc4j.admin.deploy.api.J2EEDeploymentManager");
                Class partypes[] = {String.class, String.class, String.class};
                Constructor ct = cls.getConstructor(partypes);
                Object arglist[] = {URI_PREFIX + uri, uname, passwd};
                oc4jDM = ct.newInstance(arglist);
            } catch (NoSuchMethodException e) {
                ErrorManager.getDefault().notify(ErrorManager.ERROR, e);
            } catch (ClassNotFoundException e) {
                ErrorManager.getDefault().notify(ErrorManager.ERROR, e);
            } catch (InstantiationException e) {
                ErrorManager.getDefault().notify(ErrorManager.ERROR, e);
            } catch (IllegalAccessException e) {
                ErrorManager.getDefault().notify(ErrorManager.ERROR, e);
            } catch (InvocationTargetException e) {
                ErrorManager.getDefault().notify(ErrorManager.ERROR, e);
            }catch (Exception e) {
                ErrorManager.getDefault().notify(ErrorManager.ERROR, e);
            } finally {
                loader.restoreLoader();
            }
        }
    }
    
    /**
     *  Invokes methods from proprietary deployment manager
     */
    private void invoke(String methodName, Class[] paramNames, Object[] args) throws Exception {
        loader.updateLoader();
        
        Method m = oc4jDM.getClass().getMethod(methodName, paramNames);
        m.invoke(oc4jDM, args);
        
        loader.restoreLoader();
    }
    
    public void run() {
        switch(command) {
        case DEPLOY:
            fireHandleProgressEvent(module_id, new OC4JDeploymentStatus(ActionType.EXECUTE, CommandType.DISTRIBUTE, StateType.RUNNING, NbBundle.getMessage(OC4JDeploymentManager.class, "MSG_DEPLOYING", file.getAbsolutePath())));
            String moduleID = module_id.getModuleID();
            
            // When JSF support included checks if there are libs on classpath
            WebModule w = WebModule.getWebModule(FileUtil.toFileObject(file));
            
            if (null != w && OC4JPluginUtils.isJSFInWebModule(w)) {
                ClassPath cp = ClassPath.getClassPath(w.getDocumentBase(), ClassPath.COMPILE);
                
                // Checks JSF and JSTL
                if (null == cp.findResource("javax/faces/FacesException.class")
                        || null == cp.findResource("javax/servlet/jsp/jstl/core/Config.class")) {
                    fireHandleProgressEvent(module_id, new OC4JDeploymentStatus(ActionType.EXECUTE, CommandType.DISTRIBUTE, StateType.FAILED, NbBundle.getMessage(OC4JDeploymentManager.class, "MSG_DEPLOY_FAILED")));
                    OC4JErrorManager.getInstance(this).reaction("com.evermind.server.http.deployment.WARAnnotationParser");
                    return;
                }
            }
            
            try{
                invoke("deploy", new Class[] {String.class, String.class, Map.class, boolean.class}, // NOI18N
                        new Object[] {file.getAbsolutePath(), moduleID, new HashMap(), true});
                // for web-apps we should bind
                if(moduleID.endsWith(".war") || moduleID.endsWith(".ear")) { // NOI18N
                    invoke("bindWebApp",  // NOI18N
                            new Class[] {String.class, String.class},
                            new Object[] {moduleID, ip.getInstanceProperties().getProperty(OC4JPluginProperties.PROPERTY_WEB_SITE) + "-web-site"});  // NOI18N
                }
            } catch (Exception e) {
                fireHandleProgressEvent(module_id, new OC4JDeploymentStatus(ActionType.EXECUTE, CommandType.DISTRIBUTE, StateType.FAILED, NbBundle.getMessage(OC4JDeploymentManager.class, "MSG_DEPLOY_FAILED")));
                OC4JErrorManager.getInstance(this).error(uri, e, OC4JErrorManager.GENERIC_FAILURE);
                return;
            }
            
            fireHandleProgressEvent(module_id, new OC4JDeploymentStatus(ActionType.EXECUTE, CommandType.DISTRIBUTE, StateType.COMPLETED, NbBundle.getMessage(OC4JDeploymentManager.class, "MSG_DEPLOYED")));
            break;
        case START:
            fireHandleProgressEvent(module_id, new OC4JDeploymentStatus(ActionType.EXECUTE, CommandType.START, StateType.RUNNING, NbBundle.getMessage(OC4JDeploymentManager.class, "MSG_STARTING_APP")));
            for(int i = 0; i < modules.length; i++) {
                TargetModuleID module = modules[i];
                try{
                    invoke("startApplication", new Class[] {String.class}, new Object[] {module.getModuleID()});
                } catch(Exception e) {
                    fireHandleProgressEvent(module_id, new OC4JDeploymentStatus(ActionType.EXECUTE, CommandType.DISTRIBUTE, StateType.FAILED, NbBundle.getMessage(OC4JDeploymentManager.class, "MSG_DEPLOY_FAILED")));
                    OC4JErrorManager.getInstance(this).error(uri, e, OC4JErrorManager.GENERIC_FAILURE);
                    return;
                }
            }
            
            fireHandleProgressEvent(module_id, new OC4JDeploymentStatus(ActionType.EXECUTE, CommandType.START, StateType.COMPLETED, NbBundle.getMessage(OC4JDeploymentManager.class, "MSG_STARTING_APP")));
            break;
        }
    }
    
    /**
     *  JSR-77 implementation
     */
    public MBeanServerConnection getJMXConnector() {
        loader.updateLoader();
        
        if(jmxConnection == null) {
            try {
                if(!OC4JPluginProperties.isRunning(getInstanceProperties().getProperty(OC4JPluginProperties.PROPERTY_HOST), getInstanceProperties().getProperty(InstanceProperties.HTTP_PORT_NUMBER)))
                    return null;
                
                Hashtable credentials= new Hashtable();
                credentials.put("login", ip.getInstanceProperties().getProperty(InstanceProperties.USERNAME_ATTR)); // NOI18N
                credentials.put("password", ip.getInstanceProperties().getProperty(InstanceProperties.PASSWORD_ATTR)); // NOI18N
                Hashtable env = new Hashtable();
                env.put(JMXConnectorFactory.PROTOCOL_PROVIDER_PACKAGES, "oracle.oc4j.admin.jmx.remote");
                env.put(JMXConnector.CREDENTIALS, credentials);
                
                JMXServiceURL serviceUrl= new JMXServiceURL( "rmi", // NOI18N
                        ip.getInstanceProperties().getProperty(OC4JPluginProperties.PROPERTY_HOST),
                        Integer.parseInt(ip.getInstanceProperties().getProperty(OC4JPluginProperties.PROPERTY_ADMIN_PORT)),
                        "/oc4j"); // NOI18N
                JMXConnector connection = JMXConnectorFactory.newJMXConnector(serviceUrl, env);
                connection.connect();
                jmxConnection = connection.getMBeanServerConnection();
            } catch(Exception e) {
                OC4JErrorManager.getInstance(this).error(uri, e, OC4JErrorManager.GENERIC_FAILURE);
            } finally {
                loader.restoreLoader();
            }
        }
        
        return jmxConnection;
    }
    
    public OC4JJ2eePlatformImpl getOC4JPlatform() {
        if (oc4jPlatform == null) {
            oc4jPlatform = (OC4JJ2eePlatformImpl) new OC4JJ2eePlatformImpl(this);
        }
        return oc4jPlatform;
    }
    
    public void addProgressListener(ProgressListener pl) {
        listeners.add(pl);
    }
    
    public void removeProgressListener(ProgressListener pl) {
        listeners.remove(pl);
    }
    
    public void stop() throws OperationUnsupportedException {
        throw new OperationUnsupportedException("");
    }
    
    public boolean isStopSupported() {
        return false;
    }
    
    public void cancel() throws OperationUnsupportedException {
        throw new OperationUnsupportedException("");
    }
    
    public boolean isCancelSupported() {
        return false;
    }
    
    public ClientConfiguration getClientConfiguration(TargetModuleID targetModuleID) {
        return null;
    }
    
    public TargetModuleID[] getResultTargetModuleIDs() {
        return new TargetModuleID[]{ module_id };
    }
    
    public DeploymentStatus getDeploymentStatus() {
        return deploymentStatus;
    }
    
    /** Report event to any registered listeners. */
    public void fireHandleProgressEvent(TargetModuleID targetModuleID, DeploymentStatus deploymentStatus) {
        this.deploymentStatus = deploymentStatus;
        Vector targets = null;
        ProgressEvent evt = new ProgressEvent(this, targetModuleID, deploymentStatus);
        
        synchronized (this) {
            if (listeners != null) {
                targets = (Vector) listeners.clone();
            }
        }
        
        if (targets != null) {
            for (int i = 0; i < targets.size(); i++) {
                ProgressListener target = (ProgressListener)targets.elementAt(i);
                target.handleProgressEvent(evt);
            }
        }
    }
}