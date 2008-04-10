/*
 * GeDeploymentManager.java
 *
 */
package org.netbeans.modules.j2ee.geronimo2;

import java.io.File;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;
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
import org.netbeans.modules.j2ee.deployment.plugins.api.InstanceProperties;
import org.openide.util.NbBundle;

/**
 *
 * @author Max Sauer
 */
public class GeDeploymentManager implements DeploymentManager/*, ProgressObject, Runnable*/ {

    private static final Logger LOGGER = Logger.getLogger(GeDeploymentManager.class.getName());
    
    private String uri;
    private DeploymentManager geDm;
    private GePluginProperties ip;
    private InstanceProperties instanceProperties;
    private GeJ2eePlatformImpl GePlatform;
    private MBeanServerConnection jmxConnection;
    private List<ProgressListener> listeners = new CopyOnWriteArrayList<ProgressListener>();
    private DeploymentStatus deploymentStatus;
    private boolean connected = false;
    private String username;
    private String password;
    
    private final DeploymentFactory factory;
    
    
    //CONSTRUCTORS
    /** Create connected DM */
    public GeDeploymentManager(String uri, String username, String password) {
        this(uri, true);
        this.username = username;
        this.password = password;
    }

    /** Create disconnected DM */
    public GeDeploymentManager(String uri) {
        this(uri, false);
    }
    
    private GeDeploymentManager(String uri, boolean isConnected) {
        factory = GeDeploymentFactory.create();
        this.uri = uri;
        ip = new GePluginProperties(this);
        connected = isConnected;
    }

    /**
     * Distributes application into the server
     * 
     * Delegates the call to the server's deployment manager, checking whether
     * the server is connected, updating the manager if neccessary and throwing
     * the IllegalStateException is appropriate
     *
     * @param target server target
     * @param file distributed file
     * @param deploymentDescriptorFile deployment descriptor
     * @return ProgressObject
     * @throws java.lang.IllegalStateException
     */
    public ProgressObject distribute(Target[] target, File file, File deploymentDescriptorFile) throws IllegalStateException {
        
            //jsr88 version
            ClassLoader original = modifyLoader();
            try {
                return new DelegatingProgressObject(getSynchronisedDeploymentManager().distribute(target, file, null));
            } catch (DeploymentManagerCreationException ex) {
                return new FinishedProgressObject(ActionType.EXECUTE, CommandType.DISTRIBUTE,
                        NbBundle.getMessage(GeDeploymentManager.class, "MSG_DEPLOY_FAILED"), null, true);
            } finally {
                originalLoader(original);
            }
        
        //OLD VERSION BEGIN
        
        // release proprietary objects
//        releaseProprietaryObjects();
//
//        module_id = new GeTargetModuleID(target[0], file.getName());
//        this.file = file;
//        InstanceProperties properties = getProperties().getInstanceProperties();
//
//        try {
//            String server_url = "http://" + properties.getProperty(GePluginProperties.PROPERTY_HOST) + ":" +
//                    properties.getProperty(InstanceProperties.HTTP_PORT_NUMBER);
//            if (file.getName().endsWith(".war")) { // NOI18N
//                module_id.setContextURL(server_url + WebApp.createGraph(deploymentDescriptorFile).getContextRoot());
//            } else if (file.getName().endsWith(".ear")) { // NOI18N
//                JarFileSystem jfs = new JarFileSystem();
//                jfs.setJarFile(file);
//                //EE 5 aer doesn't have application.xml 
//                FileObject appXml = jfs.getRoot().getFileObject("META-INF/application.xml"); // NOI18N
//                if (appXml != null) {
//                    Application ear = DDProvider.getDefault().getDDRoot(appXml);
//                    Module mods[] = ear.getModule();
//                    for (int i = 0; i < mods.length; i++) {
//                        GeTargetModuleID mod_id = new GeTargetModuleID(target[0]);
//                        if (mods[i].getWeb() != null) {
//                            mod_id.setContextURL(server_url + mods[i].getWeb().getContextRoot());
//                        }
//                        module_id.addChild(mod_id);
//                    }
//                } else {
//                    Logger.getLogger("global").log(Level.INFO, "Cannot file META-INF/application.xml in " + file); // NOI18N
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            Logger.getLogger(GeDeploymentManager.class.getName()).log(Level.INFO, null, e);
//        }
//
//        command = COMMAND.DEPLOY;
//        fireHandleProgressEvent(null, new GeDeploymentStatus(ActionType.EXECUTE, CommandType.DISTRIBUTE, StateType.RUNNING, NbBundle.getMessage(GeDeploymentManager.class, "MSG_DEPLOYING", file.getAbsolutePath())));
//        RequestProcessor.getDefault().post(this, 0, Thread.NORM_PRIORITY);
//
//        return this;
    }
    
    /**
     * Distributes application into the server.
     * 
     * Delegates the call to the server's deployment manager, checking whether
     * the server is connected, updating the manager if neccessary and throwing
     * the IllegalStateException is appropriate
     * 
     * @param target server target
     * @param inputStream distributed file
     * @param inputStream2 deployment descriptor
     * @return ProgressObject
     * @throws java.lang.IllegalStateException
     */
    public ProgressObject distribute(Target[] target, InputStream inputStream, InputStream inputStream2) throws IllegalStateException {
        ClassLoader original = modifyLoader();
        try {
            return new DelegatingProgressObject(getSynchronisedDeploymentManager().distribute(target, inputStream, inputStream2));
        } catch (DeploymentManagerCreationException ex) {
            return new FinishedProgressObject(ActionType.EXECUTE, CommandType.DISTRIBUTE,
                    NbBundle.getMessage(GeDeploymentManager.class, "MSG_DEPLOY_FAILED"), null, true);
        } finally {
            originalLoader(original);
        }
    }
    
    public ProgressObject distribute(Target[] target, ModuleType arg1, InputStream inputStream1, InputStream inputStream2) throws IllegalStateException {
        return distribute(target, inputStream1, inputStream2);
    }


    
    private static class DelegatingProgressObject implements ProgressObject, ProgressListener {

        private final ProgressObject original;

        private List<ProgressListener> listeners = new CopyOnWriteArrayList<ProgressListener>();

        public DelegatingProgressObject(ProgressObject original) {
            this.original = original;
            original.addProgressListener(this);
        }

        public DeploymentStatus getDeploymentStatus() {
            return original.getDeploymentStatus();
        }

        //TODO: Test wrapped ids
        public TargetModuleID[] getResultTargetModuleIDs() {
            TargetModuleID[] originalIds = original.getResultTargetModuleIDs();
            GeTargetModuleID[] wrappedIds = new GeTargetModuleID[originalIds.length];
            
            for (int i = 0; i < originalIds.length; i++) {
                wrappedIds[i] = new GeTargetModuleID(originalIds[i].getTarget());
                wrappedIds[i].setJARName(originalIds[i].getModuleID());
                
                //aquire contextURL
                String contextURL = originalIds[i].getModuleID().substring(7).replaceFirst("/\\d.*", "");
                
                //TODO: set proper host + port
                wrappedIds[i].setContextURL("http://localhost:8080"+ contextURL);
            }

            return wrappedIds;
//            return original.getResultTargetModuleIDs();
        }

        public ClientConfiguration getClientConfiguration(TargetModuleID targetModuleID) {
            return getClientConfiguration(targetModuleID);
        }

        public boolean isCancelSupported() {
            return original.isCancelSupported();
        }

        public void cancel() throws OperationUnsupportedException {
            original.cancel();
        }

        public boolean isStopSupported() {
            return original.isStopSupported();
        }

        public void stop() throws OperationUnsupportedException {
            original.stop();
        }

        public void addProgressListener(ProgressListener progressListener) {
            listeners.add(progressListener);
        }

        public void removeProgressListener(ProgressListener progressListener) {
            listeners.remove(progressListener);
        }

        public void handleProgressEvent(ProgressEvent progressEvent) {
            for (ProgressListener target : listeners) {
                target.handleProgressEvent(progressEvent);
            }
        }

    }

    private static final class FinishedProgressObject implements ProgressObject {

        private final TargetModuleID[] moduleIds;

        private final DeploymentStatus status;

        public FinishedProgressObject(final ActionType action, final CommandType command,
                final String message, final TargetModuleID[] moduleIds, final boolean failed) {

            this.moduleIds = moduleIds == null ? new TargetModuleID[0] : moduleIds;
            status = new DeploymentStatus() {

                public ActionType getAction() {
                    return action;
                }

                public CommandType getCommand() {
                    return command;
                }

                public String getMessage() {
                    return message;
                }

                public StateType getState() {
                    return failed ? StateType.FAILED : StateType.COMPLETED;
                }

                public boolean isCompleted() {
                    return false;
                }

                public boolean isFailed() {
                    return true;
                }

                public boolean isRunning() {
                    return false;
                }

            };
        }

        public ClientConfiguration getClientConfiguration(TargetModuleID arg0) {
            throw new UnsupportedOperationException("Not supported."); // NOI18N
        }

        public DeploymentStatus getDeploymentStatus() {
            return status;
        }

        public TargetModuleID[] getResultTargetModuleIDs() {
            return moduleIds;
        }

        public boolean isCancelSupported() {
            return false;
        }

        public boolean isStopSupported() {
            return false;
        }

        public void cancel() throws OperationUnsupportedException {
            throw new UnsupportedOperationException("Not supported."); // NOI18N
        }

        public void stop() throws OperationUnsupportedException {
            throw new UnsupportedOperationException("Not supported."); // NOI18N
        }

        public void addProgressListener(ProgressListener arg0) {
            //nothing - object is in final state when constructed
        }

        public void removeProgressListener(ProgressListener arg0) {
            //nothing - object is in final state when constructed
        }

    }
    
    /**
     * This class is a wrapper to make deployment manager thread safe. Underlying
     * deployment manager is not thread safe (as this is not required by
     * specification). However it seems that J2EE Server sometimes invoke other
     * operation while previous is running (for example release() while running
     * getRunningModules()). This cause troubles like #85737.
     */
    private static final class SafeDeploymentManager implements DeploymentManager {

        private final DeploymentManager delegate;

        public SafeDeploymentManager(DeploymentManager delegate) {
            this.delegate = delegate;
        }

        public synchronized ProgressObject undeploy(TargetModuleID[] arg0) throws IllegalStateException {
            return delegate.undeploy(arg0);
        }

        public synchronized ProgressObject stop(TargetModuleID[] arg0) throws IllegalStateException {
            return delegate.stop(arg0);
        }

        public synchronized ProgressObject start(TargetModuleID[] arg0) throws IllegalStateException {
            return delegate.start(arg0);
        }

        public synchronized void setLocale(Locale arg0) throws UnsupportedOperationException {
            delegate.setLocale(arg0);
        }

        public synchronized void setDConfigBeanVersion(DConfigBeanVersionType arg0) throws DConfigBeanVersionUnsupportedException {
            delegate.setDConfigBeanVersion(arg0);
        }

        public synchronized void release() {
            delegate.release();
        }

        public synchronized ProgressObject redeploy(TargetModuleID[] arg0, InputStream arg1, InputStream arg2) throws UnsupportedOperationException, IllegalStateException {
            return delegate.redeploy(arg0, arg1, arg2);
        }

        public synchronized ProgressObject redeploy(TargetModuleID[] arg0, File arg1, File arg2) throws UnsupportedOperationException, IllegalStateException {
            return delegate.redeploy(arg0, arg1, arg2);
        }

        public synchronized boolean isRedeploySupported() {
            return delegate.isRedeploySupported();
        }

        public synchronized boolean isLocaleSupported(Locale arg0) {
            return delegate.isLocaleSupported(arg0);
        }

        public synchronized boolean isDConfigBeanVersionSupported(DConfigBeanVersionType arg0) {
            return delegate.isDConfigBeanVersionSupported(arg0);
        }

        public synchronized Target[] getTargets() throws IllegalStateException {
            return delegate.getTargets();
        }

        public synchronized Locale[] getSupportedLocales() {
            return delegate.getSupportedLocales();
        }

        public synchronized TargetModuleID[] getRunningModules(ModuleType arg0, Target[] arg1) throws TargetException, IllegalStateException {
            return delegate.getRunningModules(arg0, arg1);
        }

        public synchronized TargetModuleID[] getNonRunningModules(ModuleType arg0, Target[] arg1) throws TargetException, IllegalStateException {
            return delegate.getNonRunningModules(arg0, arg1);
        }

        public synchronized Locale getDefaultLocale() {
            return delegate.getDefaultLocale();
        }

        public synchronized DConfigBeanVersionType getDConfigBeanVersion() {
            return delegate.getDConfigBeanVersion();
        }

        public synchronized Locale getCurrentLocale() {
            return delegate.getCurrentLocale();
        }

        public synchronized TargetModuleID[] getAvailableModules(ModuleType arg0, Target[] arg1) throws TargetException, IllegalStateException {
            return delegate.getAvailableModules(arg0, arg1);
        }

        public synchronized ProgressObject distribute(Target[] arg0, ModuleType arg1, InputStream arg2, InputStream arg3) throws IllegalStateException {
            return delegate.distribute(arg0, arg1, arg2, arg3);
        }

        public synchronized ProgressObject distribute(Target[] arg0, InputStream arg1, InputStream arg2) throws IllegalStateException {
            return delegate.distribute(arg0, arg1, arg2);
        }

        public synchronized ProgressObject distribute(Target[] arg0, File arg1, File arg2) throws IllegalStateException {
            return delegate.distribute(arg0, arg1, arg2);
        }

        public synchronized DeploymentConfiguration createConfiguration(DeployableObject arg0) throws InvalidModuleException {
            return delegate.createConfiguration(arg0);
        }

    }

    
    private ClassLoader modifyLoader() {
        ClassLoader originalLoader = Thread.currentThread().getContextClassLoader();
        
        //TODO: XXX serverRoot should be passed a a parameter of GeDeploymentManger Constructor
        String serverRoot = getInstanceProperties().getProperty(GePluginProperties.PROPERTY_GE_HOME);
        // if serverRoot is null, then we are in a server instance registration process, thus this call
        // is made from InstanceProperties creation -> GePluginProperties singleton contains
        // install location of the instance being registered
        if (serverRoot == null)
            serverRoot = getProperties().getGeHomeLocation();
            
        Thread.currentThread().setContextClassLoader(GeClassLoader.getInstance(serverRoot));
        return originalLoader;
    }
    
    private void originalLoader(ClassLoader originalLoader) {
        Thread.currentThread().setContextClassLoader(originalLoader);
    }
    
    /**
     * Releases all proprietary objects to be refreshed
     */
    public void releaseProprietaryObjects() {
        if (null != geDm) {
            geDm.release();
            geDm = null;
        }

        //TODO: remove
        //if (null != GePropDm)
        //    GePropDm = null;

        if (null != jmxConnection)
            jmxConnection = null;
    }

    public DeploymentConfiguration createConfiguration(DeployableObject deployableObject) throws InvalidModuleException {
        return new GeConfiguration(deployableObject);
    }

    /**
     * 
     * @param targetModuleID target
     * @param inputStream mosule archive
     * @param inputStream2 deployment paln, could be null (probably)
     * @return PO
     * @throws java.lang.UnsupportedOperationException
     * @throws java.lang.IllegalStateException
     */
    public ProgressObject redeploy(TargetModuleID[] targetModuleID, InputStream inputStream, InputStream inputStream2) throws UnsupportedOperationException, IllegalStateException {
        ClassLoader original = modifyLoader();
        try {
            return new DelegatingProgressObject(getSynchronisedDeploymentManager().redeploy(targetModuleID, inputStream, inputStream2));
        } catch (DeploymentManagerCreationException ex) {
            return new FinishedProgressObject(ActionType.EXECUTE, CommandType.REDEPLOY,
                    NbBundle.getMessage(GeDeploymentManager.class, "MSG_Redeployment_Failed"), null, true);
        } finally {
            originalLoader(original);
        }
    }

    public ProgressObject undeploy(TargetModuleID[] targetModuleID) throws IllegalStateException {
        
        ClassLoader original = modifyLoader();
        try {
            return new DelegatingProgressObject(getSynchronisedDeploymentManager().undeploy(targetModuleID));
        } catch(DeploymentManagerCreationException ex) {
            return new FinishedProgressObject(ActionType.EXECUTE, CommandType.UNDEPLOY,
                    NbBundle.getMessage(GeDeploymentManager.class, "MSG_Undeployment_Failed"), null, true);
        } finally {
            originalLoader(original);
        }
    }

    public ProgressObject stop(TargetModuleID[] targetModuleID) throws IllegalStateException {
        ClassLoader original = modifyLoader();
        try {
            return new DelegatingProgressObject(getSynchronisedDeploymentManager().stop(targetModuleID));
        } catch (DeploymentManagerCreationException ex) {
            return new FinishedProgressObject(ActionType.EXECUTE, CommandType.STOP,
                    NbBundle.getMessage(GeDeploymentManager.class, "MSG_Application_Stop_Failed"), null, true);
        } finally {
            originalLoader(original);
        }
    }
    
    /**
     * Underlying deployment manager is not thread safe. J2EE Server
     * probably can sometimes invoke release while other operation is running.
     * So we wrap vendor deployment manager to be thread safe.
     */
    private DeploymentManager getSynchronisedDeploymentManager() throws DeploymentManagerCreationException {
        synchronized (factory) {
            if (geDm == null) {
                DeploymentFactory propFactory = ((GeDeploymentFactory) factory).getGeDeploymentFactory(uri); //laod factory from geronimo libs
                
                //if (isConnected()) {
                    geDm = new SafeDeploymentManager(
                            //factory.getDeploymentManager(uri, getUsername(), getPassword()));
                            propFactory.getDeploymentManager(uri, getUsername(), getPassword()));
                    setConnected(true);
//                } else {
//                    geDm = new SafeDeploymentManager(
//                            //factory.getDisconnectedDeploymentManager(uri));
//                            propFactory.getDisconnectedDeploymentManager(uri));
//                    setConnected(false);
//                }
            }
            return geDm;
        }
    }
    
    /**
     * Gets deployment manager
     * not thread safe, do not use for deploy-like operations
     * JSR-88
     */
    private synchronized DeploymentManager getGeDeploymentManager() {
        if (geDm == null) {  //TODO: add check (geDm == null)
            GeDeploymentFactory factory = (GeDeploymentFactory) GeDeploymentFactory.create();
            
            try {
                if(!GePluginProperties.isRunning(getInstanceProperties().getProperty(GePluginProperties.PROPERTY_HOST), getInstanceProperties().getProperty(InstanceProperties.HTTP_PORT_NUMBER)))
                    throw new DeploymentManagerCreationException(uri);
                
                DeploymentFactory propFactory = factory.getGeDeploymentFactory(uri); //laod factory from geronimo libs
                
                GeClassLoader.getInstance(getProperties().getGeHomeLocation()).updateLoader();
                
                geDm = propFactory.getDeploymentManager(uri, getUsername(), getPassword());
                
                setConnected(true);
            } catch (Exception e) {
                //TODO: implement error manager
                //GeErrorManager.getInstance(this).error(uri, e, GeErrorManager.GENERIC_FAILURE);
                e.printStackTrace();
                
                setConnected(false);
            } finally {
                GeClassLoader.getInstance(getProperties().getGeHomeLocation()).restoreLoader();
            }
        }
        
        return geDm;
    }
    
    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public boolean isConnected() {
        return connected;
    }
    
    /**
     *
     * @return username from instanceProperties
     */
    public String getUsername() {
        return getInstanceProperties().getProperty(InstanceProperties.USERNAME_ATTR);
    }
    
    /**
     *
     * @return password from instanceproperties
     */
    public String getPassword() {
        return getInstanceProperties().getProperty(InstanceProperties.PASSWORD_ATTR);
    }

    private TargetModuleID[] getServerTargetModuleIds(TargetModuleID[] modules) {
        List<TargetModuleID> serverIds = new LinkedList<TargetModuleID>();
        for (TargetModuleID module : modules) {
//            if (module instanceof GeTargetModuleID) {
//                continue;
//            }
            serverIds.add(module);
        }
        return serverIds.toArray(new TargetModuleID[serverIds.size()]);
    }
    
    public ProgressObject start(TargetModuleID[] targetModuleID) throws IllegalStateException {
        
        TargetModuleID[] serverIds = getServerTargetModuleIds(targetModuleID);
        if (serverIds.length == 0) { //can't do anything with autodeployed apps
            return new FinishedProgressObject(ActionType.EXECUTE, CommandType.START,
                    NbBundle.getMessage(GeDeploymentManager.class, "MSG_Application_Started_Already"),
                    targetModuleID, false);
        }

        ClassLoader original = modifyLoader();
        try {
            return new DelegatingProgressObject(
                    getSynchronisedDeploymentManager().start(serverIds));
        } catch (DeploymentManagerCreationException ex) {
            return new FinishedProgressObject(ActionType.EXECUTE, CommandType.START,
                    NbBundle.getMessage(GeDeploymentManager.class, "MSG_DEPLOY_FAILED"), null, true);
        } finally {
            originalLoader(original);
        }
        //OLD IMPL
     // release proprietary objects
//        releaseProprietaryObjects();
//        
//        modules = targetModuleID;
//        command = COMMAND.START;
//        fireHandleProgressEvent(null, new GeDeploymentStatus(ActionType.EXECUTE, CommandType.START, StateType.RUNNING, NbBundle.getMessage(GeDeploymentManager.class, "MSG_STARTING_APP")));
//        RequestProcessor.getDefault().post(this, 0, Thread.NORM_PRIORITY);
//        
//        return this;   
    }

    public void setLocale(java.util.Locale locale) throws UnsupportedOperationException {
        getGeDeploymentManager().setLocale(locale);
    }

    public boolean isLocaleSupported(java.util.Locale locale) {
        return getGeDeploymentManager().isLocaleSupported(locale);
    }

    public synchronized TargetModuleID[] getAvailableModules(ModuleType moduleType, Target[] target) throws TargetException, IllegalStateException {
       ClassLoader original = modifyLoader();
       TargetModuleID t[];
        try { //TODO: remove hack preventing f.loop
            if(getSynchronisedDeploymentManager() != geDm) {
                t = getSynchronisedDeploymentManager().getAvailableModules(moduleType, target);
            } else
                t = geDm.getAvailableModules(moduleType, target);
            return t;
        } catch (DeploymentManagerCreationException ex) {
            throw new IllegalStateException(ex);
        } finally {
            originalLoader(original);
        }
    }

    public TargetModuleID[] getNonRunningModules(ModuleType moduleType, Target[] target) throws TargetException, IllegalStateException {
        ClassLoader original = modifyLoader();
        try {
            TargetModuleID t[] = getSynchronisedDeploymentManager().getNonRunningModules(moduleType, target);
            return t;
        } catch (DeploymentManagerCreationException ex) {
            throw new IllegalStateException(ex);
        } finally {
            originalLoader(original);
        }
    }

    public TargetModuleID[] getRunningModules(ModuleType moduleType, Target[] target) throws TargetException, IllegalStateException {
        ClassLoader original = modifyLoader();
        try {
            TargetModuleID t[] = getSynchronisedDeploymentManager().getRunningModules(moduleType, target);
            return t;
        } catch (DeploymentManagerCreationException ex) {
            throw new IllegalStateException(ex);
        } finally {
            originalLoader(original);
        }
    }

    /**
     * Redeploys Module.
     * Delegates the call to the server's deployment manager, checking whether
     * the server is connected, updating the manager if neccessary and throwing
     * the IllegalStateException is appropriate
     * @param targetModuleID target
     * @param file module archive
     * @param file2 deployment plan
     */
    public ProgressObject redeploy(TargetModuleID[] targetModuleID, File file, File file2) throws UnsupportedOperationException, IllegalStateException {
        ClassLoader original = modifyLoader();
        try {
            return new DelegatingProgressObject(getSynchronisedDeploymentManager().redeploy(targetModuleID, file, file2));
        } catch (DeploymentManagerCreationException ex) {
            return new FinishedProgressObject(ActionType.EXECUTE, CommandType.REDEPLOY,
                    NbBundle.getMessage(GeDeploymentManager.class, "MSG_Redeployment_Failed"), null, true);
        } finally {
            originalLoader(original);
        }
    }

    public void setDConfigBeanVersion(DConfigBeanVersionType dConfigBeanVersionType) throws DConfigBeanVersionUnsupportedException {
        throw new UnsupportedOperationException("This method should never be called!"); // NOI18N
    }

    public boolean isDConfigBeanVersionSupported(DConfigBeanVersionType dConfigBeanVersionType) {
        return false;
    }

    /**
     * Releases geronimo vendor-specific Deployment manager.
     * Delegates the call to the server's deployment manager, checking whether
     * the server is connected, updating the manager if neccessary and throwing
     * the IllegalStateException is appropriate
     */
    public void release() {
        ClassLoader original = modifyLoader();
        try {
            synchronized (factory) {
                if (geDm != null) {
                    // delegate the call and clear the stored deployment manager
                    try {
                        geDm.release();
                    }
                    catch (Exception e) {
                        LOGGER.log(Level.INFO, null, e); // NOI18N
                    }
                    finally {
                        geDm = null;
                    }
                }
            }
        } finally {
            originalLoader(original);
        }
    }

    public boolean isRedeploySupported() {
        return false;
    }

    public java.util.Locale getCurrentLocale() {
        return getGeDeploymentManager().getCurrentLocale();
    }

    public DConfigBeanVersionType getDConfigBeanVersion() {
        return getGeDeploymentManager().getDConfigBeanVersion();
    }

    public java.util.Locale getDefaultLocale() {
        return getGeDeploymentManager().getDefaultLocale();
    }

    public java.util.Locale[] getSupportedLocales() {
        return getGeDeploymentManager().getSupportedLocales();
    }

    public Target[] getTargets() throws IllegalStateException {
        LOGGER.log(Level.FINER, "getTargets for GeDeploymentManager");
        
        ClassLoader original = modifyLoader();
        try {
            return getSynchronisedDeploymentManager().getTargets();
        } catch (DeploymentManagerCreationException ex) {
            throw new IllegalStateException(ex);
        } finally {
            originalLoader(original);
        }
        
        // release proprietary objects
//        releaseProprietaryObjects();
//        
//        try{
//            return getGeDeploymentManager().getTargets();
//        } catch(Exception e) {
//            //TODO: implement error manager
//            e.printStackTrace();
//            //GeErrorManager.getInstance(this).error(uri, e, GeErrorManager.GENERIC_FAILURE);
//        }
//        
//        return null;
    }

    
    /**
     * Return URI
     *
     * @return uri of the server
     */
    public String getUri() {
        return uri;
    }

    /**
     * Returns GePluginProperties
     *
     * @return GePluginProperties
     */
    public GePluginProperties getProperties() {
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

    public GeJ2eePlatformImpl getGePlatform() {
        if (GePlatform == null) {
            GePlatform = (GeJ2eePlatformImpl) new GeJ2eePlatformImpl(this);
        }
        return GePlatform;
    }
}
