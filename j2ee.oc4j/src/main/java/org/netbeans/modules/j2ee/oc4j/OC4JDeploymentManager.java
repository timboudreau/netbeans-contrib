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
import java.util.logging.Level;
import java.util.logging.Logger;
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
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.modules.j2ee.dd.api.application.Application;
import org.netbeans.modules.j2ee.dd.api.application.DDProvider;
import org.netbeans.modules.j2ee.dd.api.application.Module;
import org.netbeans.modules.j2ee.deployment.plugins.api.InstanceProperties;
import org.netbeans.modules.j2ee.oc4j.config.gen.OrionWebApp;
import org.netbeans.modules.j2ee.oc4j.ide.OC4JDeploymentStatus;
import org.netbeans.modules.j2ee.oc4j.ide.OC4JErrorManager;
import org.netbeans.modules.j2ee.oc4j.ide.OC4JJ2eePlatformImpl;
import org.netbeans.modules.j2ee.oc4j.util.OC4JPluginProperties;
import org.netbeans.modules.j2ee.oc4j.util.OC4JDebug;
import org.netbeans.modules.j2ee.oc4j.util.OC4JPluginUtils;
import org.netbeans.modules.web.api.webmodule.WebModule;
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
    
    private static enum COMMAND { DEPLOY, START }
    
    private String uri;
    
    private Object oc4jPropDm;
    private DeploymentManager oc4jDm;
    private OC4JPluginProperties ip;
    private InstanceProperties instanceProperties;
    private OC4JJ2eePlatformImpl oc4jPlatform;
    
    private OC4JTargetModuleID module_id;
    private MBeanServerConnection jmxConnection;
    private Vector listeners = new Vector();
    private TargetModuleID[] modules;
    private DeploymentStatus deploymentStatus;
    private File file;
    private COMMAND command;
    private boolean connected = false;
    
    /**
     * Creates an instance of OC4JDeploymentManager
     *
     * @param uri uri of the oc4j server
     */
    public OC4JDeploymentManager(String uri) {
        this.uri = uri;
        
        ip = new OC4JPluginProperties(this);
    }
    
    /**
     * Return URI
     *
     * @return uri of the oc4j server
     */
    public String getUri() {
        return uri;
    }
    
    /**
     *
     * @return
     */
    public String getUsername() {
        return getInstanceProperties().getProperty(InstanceProperties.USERNAME_ATTR);
    }
    
    /**
     *
     * @return
     */
    public String getPassword() {
        return getInstanceProperties().getProperty(InstanceProperties.PASSWORD_ATTR);
    }
    
    /**
     * Returns OC4JPluginProperties
     *
     * @return OC4JPluginProperties
     */
    public OC4JPluginProperties getProperties() {
        return ip;
    }
    
    /**
     * Returns InstanceProperties
     *
     * @return InstanceProperties
     */
    public InstanceProperties getInstanceProperties() {
        if (instanceProperties == null)
            instanceProperties = InstanceProperties.getInstanceProperties(getUri());
        
        return instanceProperties;
    }
    
    /**
     * Distributes application into the server
     *
     * @param target server target
     * @param file distributed file
     * @param file2 deployment descriptor
     * @return ProgressObject
     * @throws java.lang.IllegalStateException
     */
    public ProgressObject distribute(Target[] target, File file, File file2) throws IllegalStateException {
        // release proprietary objects
        releaseProprietaryObjects();
        
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
                    Logger.getLogger("global").log(Level.INFO, "Cannot file META-INF/application.xml in " + file); // NOI18N
                }
            }
        } catch (Exception e){
            Logger.getLogger("global").log(Level.INFO, null, e);
        }
        
        command = COMMAND.DEPLOY;
        fireHandleProgressEvent(null, new OC4JDeploymentStatus(ActionType.EXECUTE, CommandType.DISTRIBUTE, StateType.RUNNING, NbBundle.getMessage(OC4JDeploymentManager.class, "MSG_DEPLOYING", file.getAbsolutePath())));
        RequestProcessor.getDefault().post(this, 0, Thread.NORM_PRIORITY);
        
        return this;
    }
    
    /**
     * Distributes application into the server
     *
     * @param target server target
     * @param type type of the module
     * @param inputStream application file input stream
     * @param inputStream2 deployment descriptor input stream
     * @return ProgressObject
     * @throws java.lang.IllegalStateException
     */
    public ProgressObject distribute(Target[] target, ModuleType type,
            InputStream inputStream, InputStream inputStream2) throws IllegalStateException {
        return distribute(target, inputStream, inputStream2);
    }
    
    /**
     *
     * @param deployableObject
     * @return
     * @throws javax.enterprise.deploy.spi.exceptions.InvalidModuleException
     */
    public DeploymentConfiguration createConfiguration(DeployableObject deployableObject) throws InvalidModuleException {
        throw new RuntimeException("This should never be called"); // NOI18N
    }
    
    /**
     *
     * @param targetModuleID
     * @param inputStream
     * @param inputStream2
     * @return
     * @throws java.lang.UnsupportedOperationException
     * @throws java.lang.IllegalStateException
     */
    public ProgressObject redeploy(TargetModuleID[] targetModuleID, InputStream inputStream, InputStream inputStream2) throws UnsupportedOperationException, IllegalStateException {
        if (!isConnected()) {
            throw new IllegalStateException(NbBundle.getMessage(
                    OC4JDeploymentManager.class, "MSG_ERROR_DISC_MANAGER"));   // NOI18N
        }
        
        // release proprietary objects
        releaseProprietaryObjects();
        
        DeploymentManager manager = getOC4JDeploymentManager();
        
        OC4JClassLoader.getInstance(getProperties().getOC4JHomeLocation()).updateLoader();
        
        try {
            return manager.redeploy(targetModuleID, inputStream, inputStream2);
        } finally {
            OC4JClassLoader.getInstance(getProperties().getOC4JHomeLocation()).restoreLoader();
        }
    }
    
    /**
     *
     * @param target
     * @param inputStream
     * @param inputStream2
     * @return
     * @throws java.lang.IllegalStateException
     */
    public ProgressObject distribute(Target[] target, InputStream inputStream, InputStream inputStream2) throws IllegalStateException {
        if (!isConnected()) {
            throw new IllegalStateException(NbBundle.getMessage(
                    OC4JDeploymentManager.class, "MSG_ERROR_DISC_MANAGER"));   // NOI18N
        }
        
        // release proprietary objects
        releaseProprietaryObjects();
        
        DeploymentManager manager = getOC4JDeploymentManager();
        
        OC4JClassLoader.getInstance(getProperties().getOC4JHomeLocation()).updateLoader();
        
        try {
            return manager.distribute(target, inputStream, inputStream2);
        } finally {
            OC4JClassLoader.getInstance(getProperties().getOC4JHomeLocation()).restoreLoader();
        }
    }
    
    /**
     *
     * @param targetModuleID
     * @return
     * @throws java.lang.IllegalStateException
     */
    public ProgressObject undeploy(TargetModuleID[] targetModuleID) throws IllegalStateException {
        if (!isConnected()) {
            throw new IllegalStateException(NbBundle.getMessage(
                    OC4JDeploymentManager.class, "MSG_ERROR_DISC_MANAGER"));   // NOI18N
        }
        
        // release proprietary objects
        releaseProprietaryObjects();
        
        DeploymentManager manager = getOC4JDeploymentManager();
        
        OC4JClassLoader.getInstance(getProperties().getOC4JHomeLocation()).updateLoader();
        
        try {
            return manager.undeploy(targetModuleID);
        } finally {
            OC4JClassLoader.getInstance(getProperties().getOC4JHomeLocation()).restoreLoader();
        }
    }
    
    /**
     *
     * @param targetModuleID
     * @param file
     * @param file2
     * @return
     * @throws java.lang.UnsupportedOperationException
     * @throws java.lang.IllegalStateException
     */
    public ProgressObject redeploy(TargetModuleID[] targetModuleID, File file, File file2) throws UnsupportedOperationException, IllegalStateException {
        if (!isConnected()) {
            throw new IllegalStateException(NbBundle.getMessage(
                    OC4JDeploymentManager.class, "MSG_ERROR_DISC_MANAGER"));   // NOI18N
        }
        
        // release proprietary objects
        releaseProprietaryObjects();
        
        DeploymentManager manager = getOC4JDeploymentManager();
        
        OC4JClassLoader.getInstance(getProperties().getOC4JHomeLocation()).updateLoader();
        
        try {
            return manager.redeploy(targetModuleID, file, file2);
        } finally {
            OC4JClassLoader.getInstance(getProperties().getOC4JHomeLocation()).restoreLoader();
        }
    }
    
    /**
     *
     * @param targetModuleID
     * @return
     * @throws java.lang.IllegalStateException
     */
    public ProgressObject stop(TargetModuleID[] targetModuleID) throws IllegalStateException {
        if (!isConnected()) {
            throw new IllegalStateException(NbBundle.getMessage(
                    OC4JDeploymentManager.class, "MSG_ERROR_DISC_MANAGER"));   // NOI18N
        }
        
        // release proprietary objects
        releaseProprietaryObjects();
        
        DeploymentManager manager = getOC4JDeploymentManager();
        
        OC4JClassLoader.getInstance(getProperties().getOC4JHomeLocation()).updateLoader();
        
        try {
            return manager.stop(targetModuleID);
        } finally {
            OC4JClassLoader.getInstance(getProperties().getOC4JHomeLocation()).restoreLoader();
        }
    }
    
    /**
     *
     * @param targetModuleID
     * @return
     * @throws java.lang.IllegalStateException
     */
    public ProgressObject start(TargetModuleID[] targetModuleID) throws IllegalStateException {
        // release proprietary objects
        releaseProprietaryObjects();
        
        modules = targetModuleID;
        command = COMMAND.START;
        fireHandleProgressEvent(null, new OC4JDeploymentStatus(ActionType.EXECUTE, CommandType.START, StateType.RUNNING, NbBundle.getMessage(OC4JDeploymentManager.class, "MSG_STARTING_APP")));
        RequestProcessor.getDefault().post(this, 0, Thread.NORM_PRIORITY);
        
        return this;
    }
    
    /**
     *
     * @param locale
     * @throws java.lang.UnsupportedOperationException
     */
    public void setLocale(java.util.Locale locale) throws UnsupportedOperationException {
        getOC4JDeploymentManager().setLocale(locale);
    }
    
    /**
     *
     * @param locale
     * @return
     */
    public boolean isLocaleSupported(java.util.Locale locale) {
        return getOC4JDeploymentManager().isLocaleSupported(locale);
    }
    
    /**
     *
     * @param moduleType
     * @param target
     * @return
     * @throws javax.enterprise.deploy.spi.exceptions.TargetException
     * @throws java.lang.IllegalStateException
     */
    public TargetModuleID[] getAvailableModules(ModuleType moduleType, Target[] target) throws TargetException, IllegalStateException {
        if (!isConnected()) {
            throw new IllegalStateException(NbBundle.getMessage(
                    OC4JDeploymentManager.class, "MSG_ERROR_DISC_MANAGER"));   // NOI18N
        }
        
        // release proprietary objects
        releaseProprietaryObjects();
        
        DeploymentManager manager = getOC4JDeploymentManager();
        
        OC4JClassLoader.getInstance(getProperties().getOC4JHomeLocation()).updateLoader();
        
        try {
            return manager.getAvailableModules(moduleType, target);
        } finally {
            OC4JClassLoader.getInstance(getProperties().getOC4JHomeLocation()).restoreLoader();
        }
    }
    
    /**
     *
     * @param moduleType
     * @param target
     * @return
     * @throws javax.enterprise.deploy.spi.exceptions.TargetException
     * @throws java.lang.IllegalStateException
     */
    public TargetModuleID[] getNonRunningModules(ModuleType moduleType, Target[] target) throws TargetException, IllegalStateException {
        if (!isConnected()) {
            throw new IllegalStateException(NbBundle.getMessage(
                    OC4JDeploymentManager.class, "MSG_ERROR_DISC_MANAGER"));   // NOI18N
        }
        
        // release proprietary objects
        releaseProprietaryObjects();
        
        DeploymentManager manager = getOC4JDeploymentManager();
        
        OC4JClassLoader.getInstance(getProperties().getOC4JHomeLocation()).updateLoader();
        
        try {
            return manager.getNonRunningModules(moduleType, target);
        } finally {
            OC4JClassLoader.getInstance(getProperties().getOC4JHomeLocation()).restoreLoader();
        }
    }
    
    /**
     *
     * @param moduleType
     * @param target
     * @return
     * @throws javax.enterprise.deploy.spi.exceptions.TargetException
     * @throws java.lang.IllegalStateException
     */
    public TargetModuleID[] getRunningModules(ModuleType moduleType, Target[] target) throws TargetException, IllegalStateException {
        if (!isConnected()) {
            throw new IllegalStateException(NbBundle.getMessage(
                    OC4JDeploymentManager.class, "MSG_ERROR_DISC_MANAGER"));   // NOI18N
        }
        
        // release proprietary objects
        releaseProprietaryObjects();
        
        DeploymentManager manager = getOC4JDeploymentManager();
        
        OC4JClassLoader.getInstance(getProperties().getOC4JHomeLocation()).updateLoader();
        
        try {
            return manager.getRunningModules(moduleType, target);
        } finally {
            OC4JClassLoader.getInstance(getProperties().getOC4JHomeLocation()).restoreLoader();
        }
    }
    
    /**
     *
     * @param dConfigBeanVersionType
     * @throws javax.enterprise.deploy.spi.exceptions.DConfigBeanVersionUnsupportedException
     */
    public void setDConfigBeanVersion(DConfigBeanVersionType dConfigBeanVersionType) throws DConfigBeanVersionUnsupportedException {
        getOC4JDeploymentManager().setDConfigBeanVersion(dConfigBeanVersionType);
    }
    
    /**
     *
     * @param dConfigBeanVersionType
     * @return
     */
    public boolean isDConfigBeanVersionSupported(DConfigBeanVersionType dConfigBeanVersionType) {
        return getOC4JDeploymentManager().isDConfigBeanVersionSupported(dConfigBeanVersionType);
    }
    
    /**
     *
     * @param connected
     */
    public void setConnected(boolean connected) {
        this.connected = connected;
    }
    
    /**
     *
     * @return
     */
    public boolean isConnected() {
        return connected;
    }
    
    /**
     *
     */
    public void release() {
        releaseProprietaryObjects();
    }
    
    /**
     *
     * @return
     */
    public boolean isRedeploySupported() {
        return getOC4JDeploymentManager().isRedeploySupported();
    }
    
    /**
     *
     * @return
     */
    public java.util.Locale getCurrentLocale() {
        return getOC4JDeploymentManager().getCurrentLocale();
    }
    
    /**
     *
     * @return
     */
    public DConfigBeanVersionType getDConfigBeanVersion() {
        return getOC4JDeploymentManager().getDConfigBeanVersion();
    }
    
    /**
     *
     * @return
     */
    public java.util.Locale getDefaultLocale() {
        return getOC4JDeploymentManager().getDefaultLocale();
    }
    
    /**
     *
     * @return
     */
    public java.util.Locale[] getSupportedLocales() {
        return getOC4JDeploymentManager().getSupportedLocales();
    }
    
    /**
     *
     * @return
     */
    public Target[] getTargets() {
        OC4JDebug.log(getClass().getName(), "getTargets for Deployment Maneger");
        
        // release proprietary objects
        releaseProprietaryObjects();
        
        try{
            return getOC4JDeploymentManager().getTargets();
        } catch(Exception e) {
            OC4JErrorManager.getInstance(this).error(uri, e, OC4JErrorManager.GENERIC_FAILURE);
        }
        
        return null;
    }
    
    /**
     *  Gets deployment manager
     */
    private synchronized DeploymentManager getOC4JDeploymentManager() {
        if (null == oc4jDm) {
            OC4JDeploymentFactory factory = (OC4JDeploymentFactory) OC4JDeploymentFactory.getDefault();
            
            try {
                if(!OC4JPluginProperties.isRunning(getInstanceProperties().getProperty(OC4JPluginProperties.PROPERTY_HOST), getInstanceProperties().getProperty(InstanceProperties.HTTP_PORT_NUMBER)))
                    throw new DeploymentManagerCreationException(uri);
                
                DeploymentFactory propFactory = factory.getOC4JDeploymentFactory(uri);
                
                OC4JClassLoader.getInstance(getProperties().getOC4JHomeLocation()).updateLoader();
                
                oc4jDm = propFactory.getDeploymentManager(uri, getUsername(), getPassword());
                
                setConnected(true);
            } catch (Exception e) {
                OC4JErrorManager.getInstance(this).error(uri, e, OC4JErrorManager.GENERIC_FAILURE);
                
                setConnected(false);
            } finally {
                OC4JClassLoader.getInstance(getProperties().getOC4JHomeLocation()).restoreLoader();
            }
        }
        
        return oc4jDm;
    }
    
    /**
     *  Gets proprietary deployment manager
     */
    private synchronized Object getOC4JProprietaryDeploymentManager() {
        if (null == oc4jPropDm) {
            OC4JClassLoader.getInstance(getProperties().getOC4JHomeLocation()).updateLoader();
            
            try {
                Class cls =  OC4JClassLoader.getInstance(getProperties().getOC4JHomeLocation()).
                        loadClass("oracle.oc4j.admin.deploy.api.J2EEDeploymentManager");
                Class partypes[] = {String.class, String.class, String.class};
                Constructor ct = cls.getConstructor(partypes);
                Object arglist[] = {uri, getUsername(), getPassword()};
                oc4jPropDm = ct.newInstance(arglist);
            } catch (NoSuchMethodException e) {
                Logger.getLogger("global").log(Level.SEVERE, null, e);
            } catch (ClassNotFoundException e) {
                Logger.getLogger("global").log(Level.SEVERE, null, e);
            } catch (InstantiationException e) {
                Logger.getLogger("global").log(Level.SEVERE, null, e);
            } catch (IllegalAccessException e) {
                Logger.getLogger("global").log(Level.SEVERE, null, e);
            } catch (InvocationTargetException e) {
                Logger.getLogger("global").log(Level.SEVERE, null, e);
            } catch (Exception e) {
                Logger.getLogger("global").log(Level.SEVERE, null, e);
            } finally {
                OC4JClassLoader.getInstance(getProperties().getOC4JHomeLocation()).restoreLoader();
            }
        }
        
        return oc4jPropDm;
    }
    
    /**
     *  Invokes methods from proprietary deployment manager
     */
    private void invoke(String methodName, Class[] paramNames, Object[] args) throws Exception {
        Object manager = getOC4JProprietaryDeploymentManager();
        
        OC4JClassLoader.getInstance(getProperties().getOC4JHomeLocation()).updateLoader();
        
        Method m = manager.getClass().getMethod(methodName, paramNames);
        m.invoke(manager, args);
        
        OC4JClassLoader.getInstance(getProperties().getOC4JHomeLocation()).restoreLoader();
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
     *
     * @return
     */
    public MBeanServerConnection getJMXConnector() {
        OC4JClassLoader.getInstance(getProperties().getOC4JHomeLocation()).updateLoader();
        
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
                OC4JClassLoader.getInstance(getProperties().getOC4JHomeLocation()).restoreLoader();
            }
        }
        
        return jmxConnection;
    }
    
    /**
     * Releases all proprietary objects to be refreshed
     */
    public void releaseProprietaryObjects() {
        if (null != oc4jDm) {
            oc4jDm.release();
            oc4jDm = null;
        }
        
        if (null != oc4jPropDm)
            oc4jPropDm = null;
        
        if (null != jmxConnection)
            jmxConnection = null;
    }
    
    /**
     *
     * @return
     */
    public OC4JJ2eePlatformImpl getOC4JPlatform() {
        if (oc4jPlatform == null) {
            oc4jPlatform = (OC4JJ2eePlatformImpl) new OC4JJ2eePlatformImpl(this);
        }
        return oc4jPlatform;
    }
    
    /**
     *
     * @param pl
     */
    public void addProgressListener(ProgressListener pl) {
        listeners.add(pl);
    }
    
    /**
     *
     * @param pl
     */
    public void removeProgressListener(ProgressListener pl) {
        listeners.remove(pl);
    }
    
    /**
     *
     * @throws javax.enterprise.deploy.spi.exceptions.OperationUnsupportedException
     */
    public void stop() throws OperationUnsupportedException {
        throw new OperationUnsupportedException("");
    }
    
    /**
     *
     * @return
     */
    public boolean isStopSupported() {
        return false;
    }
    
    /**
     *
     * @throws javax.enterprise.deploy.spi.exceptions.OperationUnsupportedException
     */
    public void cancel() throws OperationUnsupportedException {
        throw new OperationUnsupportedException("");
    }
    
    /**
     *
     * @return
     */
    public boolean isCancelSupported() {
        return false;
    }
    
    /**
     *
     * @param targetModuleID
     * @return
     */
    public ClientConfiguration getClientConfiguration(TargetModuleID targetModuleID) {
        return null;
    }
    
    /**
     *
     * @return
     */
    public TargetModuleID[] getResultTargetModuleIDs() {
        return new TargetModuleID[]{ module_id };
    }
    
    /**
     *
     * @return
     */
    public DeploymentStatus getDeploymentStatus() {
        return deploymentStatus;
    }
    
    /** Report event to any registered listeners.
     *
     * @param targetModuleID
     * @param deploymentStatus
     */
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