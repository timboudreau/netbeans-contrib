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

package org.netbeans.modules.portalpack.servers.core.api;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.netbeans.modules.j2ee.deployment.plugins.spi.J2eePlatformImpl;
import org.netbeans.modules.portalpack.servers.core.*;
import org.netbeans.modules.portalpack.servers.core.PSDeployer;
import org.netbeans.modules.portalpack.servers.core.common.LogManager;
import org.netbeans.modules.portalpack.servers.core.impl.DefaultPSNodeConfiguration;
import org.netbeans.modules.portalpack.servers.core.util.PSConfigObject;
import org.netbeans.modules.portalpack.servers.core.util.NetbeanConstants;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import org.netbeans.modules.j2ee.deployment.plugins.api.InstanceProperties;
//import org.netbeans.modules.j2ee.sun.api.SunURIManager;
import org.netbeans.modules.portalpack.servers.core.common.ServerConstants;
import org.openide.ErrorManager;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 * @author Satya
 */
public abstract class PSDeploymentManager implements DeploymentManager {
    
    private static Logger logger = Logger.getLogger(NetbeanConstants.PORTAL_LOGGER);
    private String uri;
    private String host;
    private int port;
    private PSConfigObject psconfig;
    private String psVersion;
    private LogManager logManager;
    private PSJ2eePlatformImpl psPlatformImpl;
    
    public PSDeploymentManager(String uri,String psVersion)
    {
        this.psVersion = psVersion;
        psconfig = PSConfigObject.getPSConfigObject(uri);
        this.uri = uri;
        host = psconfig.getHost();
        try{
            port = Integer.parseInt(psconfig.getPort());
        }catch(Exception e){
            port = 80;
        }
        logManager = new LogManager(this);
      
        psconfig.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                String name = evt.getPropertyName();
                    if(name.equalsIgnoreCase("PORTAL_URI"))
                    {    // update Ant deployment properties file if it exists
                        try {
                            storeAntDeploymentProperties(getAntDeploymentPropertiesFile(), false);
                        } catch(IOException ioe) {
                            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, ioe);
                        }
                    }
                }
            }
        );
    }

    public PSJ2eePlatformImpl getPSJ2eePlatformImpl()
    {
        if(psPlatformImpl == null)
           psPlatformImpl = createPSJ2eePlatformImpl(psconfig);
        return psPlatformImpl;
    }
    public abstract PSJ2eePlatformImpl createPSJ2eePlatformImpl(PSConfigObject psconfig);
    
    
    public PSConfigObject getPSConfig()
    {
        return psconfig;
    }
    
    public LogManager getLogManager()
    {
        return logManager;
    }
    
    public void openLog(Process process,String displayName)
    {
        logManager.closeServerLog();
        logManager.openServerLog(process,displayName);
    }
    
    public String getPSVersion()
    {
        return psVersion;
    }
    public ProgressObject distribute(Target[] target, File file, File file2) throws IllegalStateException {
        logger.log(Level.FINEST,"Target:::::::::::::::::: "+target);
        logger.log(Level.FINEST,"Fike:::::::::::::::::::: "+file);
        logger.log(Level.FINEST,"FILE********************** " + file2);
           
        PSDeployer depl = new PSDeployerImpl(this,host,port);
        ProgressObject po = depl.deploy(target[0],file,file2);
       
       //if(file != null)
       //     logger.log(Level.INFO,"File "+file.getAbsolutePath()+" deployed successfully.");
        
        return po;
    }
    
    public ProgressObject distribute(Target[] target,ModuleType moduleType,InputStream in1, InputStream in2) throws IllegalStateException{
        return null;
    }

    public DeploymentConfiguration createConfiguration(DeployableObject deployableObject) throws InvalidModuleException {
        logger.log(Level.FINEST,"Inside createConfiguration..");
        return new PSConfiguration(deployableObject);
    }

    public ProgressObject redeploy(TargetModuleID[] targetModuleID, InputStream inputStream, InputStream inputStream2) throws UnsupportedOperationException, IllegalStateException {
        logger.log(Level.FINEST,"Inside redeploy ***");
        return null;
    }

    public ProgressObject distribute(Target[] target, InputStream inputStream, InputStream inputStream2) throws IllegalStateException {
        logger.log(Level.FINEST,"Inside distribute inputstream/outputstream **");
        return null;
    }

    public ProgressObject undeploy(TargetModuleID[] targetModuleID) throws IllegalStateException {
        logger.log(Level.FINEST,"Inside undeploy ***");
        return null;
    }

    public ProgressObject stop(TargetModuleID[] targetModuleID) throws IllegalStateException {
        logger.log(Level.FINEST,"Inside stop Module **");
        DelegatorProgressObject prog = new DelegatorProgressObject();
        return prog.stopModule(targetModuleID);
    }

    public ProgressObject start(TargetModuleID[] targetModuleID) throws IllegalStateException {
        logger.log(Level.FINEST,"Inside start Module **");
        DelegatorProgressObject prog = new DelegatorProgressObject();
        return prog.startModule(targetModuleID);
    }

    public void setLocale(java.util.Locale locale) throws UnsupportedOperationException {
    }

    public boolean isLocaleSupported(java.util.Locale locale) {
        return false;
    }

    public TargetModuleID[] getAvailableModules(ModuleType moduleType, Target[] target) throws TargetException, IllegalStateException {
        logger.log(Level.FINEST,"Inside getAvailableModules ***");
        return new TargetModuleID[]{};
    }

    public TargetModuleID[] getNonRunningModules(ModuleType moduleType, Target[] target) throws TargetException, IllegalStateException {
        logger.log(Level.FINEST,"Inside getNonRunningModules ***");
        return new TargetModuleID[]{};
    }

    public TargetModuleID[] getRunningModules(ModuleType moduleType, Target[] target) throws TargetException, IllegalStateException {
        logger.log(Level.FINEST,"Insidee getRunningModule ***");
        return new TargetModuleID[]{};
    }

    public ProgressObject redeploy(TargetModuleID[] targetModuleID, File file, File file2) throws UnsupportedOperationException, IllegalStateException {
        logger.log(Level.FINEST,"Inside redeploy file file *");
        return null;
    }

    public void setDConfigBeanVersion(DConfigBeanVersionType dConfigBeanVersionType) throws DConfigBeanVersionUnsupportedException {
        logger.log(Level.FINEST,"Inside DConfig deploy version **");
    }

    public boolean isDConfigBeanVersionSupported(DConfigBeanVersionType dConfigBeanVersionType) {
        return false;
    }

    public void release() {
    }

    public boolean isRedeploySupported() {
        return false;
    }

    public java.util.Locale getCurrentLocale() {
        return null;
    }

    public DConfigBeanVersionType getDConfigBeanVersion() {
        return null;
    }

    public java.util.Locale getDefaultLocale() {
        return null;
    }

    public java.util.Locale[] getSupportedLocales() {
        return null;
    }

    public Target[] getTargets() throws IllegalStateException {
        return new Target[]{new PSTarget(uri,"Portal Server at "+uri,uri)};
    }
    
    public boolean isLocalServer()
    {
        if(!psconfig.isRemote())
            return true;
        else
            return false;
    }
    
    public boolean isRunning(){        
       
        if(isRunningAdminServer() && isRunningInstanceServer())
            return true;
        else
            return false;
    }
    
    public boolean isRunningAdminServer()
    {
        int port = 0;
        try{
            port = Integer.parseInt(psconfig.getAdminPort());
        }catch(Exception e){
            logger.log(Level.SEVERE,"Invalid Admin Port : "+psconfig.getAdminPort(),e);
        }
        
        String host = psconfig.getHost();
           
       try {
             java.net.InetSocketAddress isa = new java.net.InetSocketAddress(java.net.InetAddress.getByName(host), port);
             java.net.Socket socket = new java.net.Socket();
             socket.connect(isa);
             socket.close();             
             return true;
        } catch (IOException e) {            
            return false;
        }
        
    }
    
    public String getServerLocation(){
       return psconfig.getServerHome();
    }
    
    public String getUri()
    {
        return uri;
    }

    public boolean isRunningInstanceServer() {
        int port = 0;
        try{
            port = Integer.parseInt(psconfig.getPort());
        }catch(Exception e){
            logger.log(Level.SEVERE,"Invalid Port: "+psconfig.getPort(),e);
        }
        
        String host = psconfig.getHost();
           
       try {
             java.net.InetSocketAddress isa = new java.net.InetSocketAddress(java.net.InetAddress.getByName(host), port);
             java.net.Socket socket = new java.net.Socket();
             socket.connect(isa);
             socket.close();             
             return true;
        } catch (IOException e) {            
            return false;
        }
    }

    public File getAntDeploymentPropertiesFile() {
        return new File(System.getProperty("netbeans.user"), getInstanceID() + ".properties"); // NOI18N
    }
    
    public void storeAntDeploymentProperties(File file, boolean create) throws IOException {
        if (!create && !file.exists()) {
            return;
        }
        Properties antProps = new Properties();
        antProps.setProperty("ps.client.url", getTaskHandler().getClientURL()); // NOI18N
        antProps.setProperty("client.url", getTaskHandler().getClientURL());
        file.createNewFile();
        FileObject fo = FileUtil.toFileObject(file);
        FileLock lock = null;
        try {
            lock = fo.lock();
            OutputStream os = fo.getOutputStream(lock);
            try {
                antProps.store(os,"");
            } finally {
                if (null != os) {
                    os.close();
                }
            }
        } finally {
            if (null != lock) {
                lock.releaseLock();
            }
        }
    }
    
    private String getInstanceID()
    {
        if(getPSConfig().getIntanceId() == null || getPSConfig().getIntanceId().length() == 0)
        {
            String host = getPSConfig().getHost().replace(".","_");
            String instanceID = getPSVersion()+"_"+host+"_"+getPSConfig().getPort();
            getPSConfig().setInstanceId(instanceID);
            getPSConfig().saveProperties();           
            return instanceID;
        }
        return getPSConfig().getIntanceId();
    }

    //custom methods 
    public ProgressObject undeploy(String portletAppName,String dn)
    {    
        PSDeployer depl = new PSDeployerImpl(this,host,port);
        ProgressObject po = depl.undeploy(portletAppName,dn);
        
        return po;
    }
    
   public PSStartServerInf getStartServerHandler()
   {
       return PSStartServerFactory.getPSStartServerHandler(this);
   }
   
  /* Uncomment this method for NetBeans 6.1
   public DeploymentManager getJ2EEContainerDeploymentManager()
   {
       if(psconfig.getServerType() != null && 
               psconfig.getServerType().equals(ServerConstants.SUN_APP_SERVER_9))
       {
           InstanceProperties props = SunURIManager.getInstanceProperties(new File(psconfig.getServerHome()), psconfig.getHost(), Integer.parseInt(psconfig.getPort()));
           if(props == null) return null;
           return props.getDeploymentManager();
       }
       return null;
   }*/
   
   public boolean isShowServerLogSupported()
   {
       return false;
   }
   
   public void showServerLog()
   {
       //do nothing
   }
  
   public abstract PSTaskHandler getTaskHandler();
   public abstract PSConfigPanelManager getPSConfigPanelManager();
   public PSNodeConfiguration getPSNodeConfiguration()
   {
       return DefaultPSNodeConfiguration.getInstance();
   }
       
}
