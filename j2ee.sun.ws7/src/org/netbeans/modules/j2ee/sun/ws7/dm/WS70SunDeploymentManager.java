/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

/*
 * WS70SunDeploymentManager.java
 */

package org.netbeans.modules.j2ee.sun.ws7.dm;

import java.util.List;
import java.util.Map;
import java.io.InputStream;
import java.io.File;
import java.util.Locale;
import java.lang.reflect.Method;

import javax.enterprise.deploy.model.DeployableObject;
import javax.enterprise.deploy.spi.Target;
import javax.enterprise.deploy.spi.TargetModuleID;
import javax.enterprise.deploy.spi.status.ProgressObject;
import javax.enterprise.deploy.spi.status.ProgressListener;
import javax.enterprise.deploy.spi.status.ProgressEvent;
import javax.enterprise.deploy.spi.status.DeploymentStatus;
import javax.enterprise.deploy.shared.DConfigBeanVersionType;
import javax.enterprise.deploy.spi.exceptions.DConfigBeanVersionUnsupportedException;
import javax.enterprise.deploy.spi.DeploymentManager;
import javax.enterprise.deploy.spi.DeploymentConfiguration;
import javax.enterprise.deploy.shared.ModuleType;
import javax.enterprise.deploy.spi.exceptions.InvalidModuleException;
import javax.enterprise.deploy.spi.exceptions.TargetException;
import javax.enterprise.deploy.spi.exceptions.DeploymentManagerCreationException;
import javax.enterprise.deploy.spi.factories.DeploymentFactory;
import org.netbeans.modules.j2ee.deployment.plugins.api.InstanceProperties;


import java.io.*;
import org.netbeans.modules.j2ee.sun.share.configbean.SunONEDeploymentConfiguration;
import org.netbeans.modules.j2ee.sun.dd.api.DDProvider;
import org.netbeans.modules.j2ee.sun.dd.api.web.SunWebApp;
import org.netbeans.modules.j2ee.sun.ws7.j2ee.ResourceType;
import org.netbeans.modules.j2ee.sun.ws7.ui.WS70URIManager;
import org.netbeans.modules.j2ee.sun.ws7.ui.WS70ConfigSelectDialog;

import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;

/**
 *
 * @author Administrator
 */
public class WS70SunDeploymentManager implements DeploymentManager{
    private DeploymentManager ws70DM;
    private DeploymentFactory ws70DF;
    private String uri;
    private String userName;
    private String password;
    private Class dmClass;
    private String serverLocation;
    private String host;
    private int port;

    
    /** Creates a new instance of WS70SunDeploymentManager */
    public WS70SunDeploymentManager(DeploymentFactory df, DeploymentManager dm, String uri, String username, String password) {
        this.ws70DF = df;
        this.ws70DM = dm;
        this.uri = uri;
        this.userName = username;
        this.password = password;
        dmClass = ws70DM.getClass();
        serverLocation = WS70URIManager.getLocation(uri);
        host = WS70URIManager.getHostFromURI(uri);
        String p = WS70URIManager.getPortFromURI(uri);
        try{
            port = Integer.parseInt(p);
        }catch(java.lang.NumberFormatException n){
            System.err.println("Error parsing adminport");
        }                
    }
    
    public String getUserName() {
        return userName;
    }
    
    public String getPassword() {
        return password;
    }
    public String getServerLocation(){
        return serverLocation;
    }
    public String getUri(){
        return uri;
    }
    public boolean isLocalServer(){
        //TBD Find a more accurate way
        //May be asking the user.
        
        if(host.equals("localhost")|| host.equals("127.0.0.1")){            
            return true;
        }
        return false;
    }
    
    public DeploymentConfiguration createConfiguration(DeployableObject deplObj)
        throws InvalidModuleException {
        if (!ModuleType.WAR.equals(deplObj.getType())) {
            throw new InvalidModuleException ("Only WAR modules are supported for SunWebDeploymentManager"); // NOI18N
        }

        return new SunONEDeploymentConfiguration(deplObj);
    }


    /**
     * Deploys web module using deploy command
     * @param targets Array containg one web module
     * @param is Web application stream
     * @param deplPlan Server specific data
     * @throws IllegalStateException when TomcatManager is disconnected
     * @return Object that reports about deployment progress
     */
    public ProgressObject distribute(Target[] targets, InputStream is, InputStream deplPlan)
                                        throws IllegalStateException {

        return ws70DM.distribute(targets, is, deplPlan);
    }


    /**
     * Deploys web module using install command
     * @param targets Array containg one web module
     * @param moduleArchive directory with web module or WAR file
     * @param deplPlan Server specific data
     * @throws IllegalStateException when TomcatManager is disconnected
     * @return Object that reports about deployment progress
     */
    public ProgressObject distribute(Target[] targets, File moduleArchive,File deplPlan)
                                        throws IllegalStateException {
        SunWebApp swa = null;
        try {        
            
            InputStream inputStream = new BufferedInputStream(new FileInputStream(deplPlan),
                                                           4096);
            DDProvider provider = DDProvider.getDefault();
            swa = provider.getWebDDRoot(inputStream);            
        } catch (Exception e) {
            e.printStackTrace();
        } // end of try-catch            

        String ctxRoot = null;
        if (swa == null) {
            System.err.println("DeploymentManager: swa is null");
        }else{
            ctxRoot = swa.getContextRoot();
            System.err.println("Distribute, contextRoot is "+ctxRoot);
        }
        
        try{
            Method distribute = dmClass.getDeclaredMethod("distribute", new Class[]{Target[].class, File.class, String.class});
            ProgressObject po = (ProgressObject)distribute.invoke(this.ws70DM, new Object[]{targets, moduleArchive, ctxRoot});
            return po;
            
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return null;
    }

    public Locale getCurrentLocale() {                                
        return Locale.getDefault();
    }

    public Locale getDefaultLocale() {                                
        return Locale.getDefault();
    }
    
    public Locale[] getSupportedLocales() {                                
        return Locale.getAvailableLocales();
    }

    public boolean isLocaleSupported(Locale locale) {
        if (locale == null) {
            return false;
        }
        Locale [] supLocales = getSupportedLocales();
        for (int i =0; i<supLocales.length; i++) {
            if (locale.equals (supLocales[i])) {
                return true;
            }
        }
        return false;
    }

    public void setLocale(Locale locale)    
        throws UnsupportedOperationException {
         
    }
    
    public DConfigBeanVersionType getDConfigBeanVersion() {
        return DConfigBeanVersionType.V1_4;
    }

    public boolean isDConfigBeanVersionSupported(DConfigBeanVersionType v) {
        return DConfigBeanVersionType.V1_4.equals(v);
    }

    public void setDConfigBeanVersion(DConfigBeanVersionType version)
        throws DConfigBeanVersionUnsupportedException {
        if (!DConfigBeanVersionType.V1_4.equals(version)) {
            throw new DConfigBeanVersionUnsupportedException("unsupported version"); // NOI18N
        }
    }

    public TargetModuleID[] getAvailableModules(ModuleType moduleType,
                                                Target[] targetList)
                throws IllegalStateException, TargetException {
        return ws70DM.getAvailableModules(moduleType, targetList);
        
    }


    public TargetModuleID[] getNonRunningModules(ModuleType moduleType,
                                                 Target[] targetList)
        throws IllegalStateException, TargetException {
        return ws70DM.getNonRunningModules(moduleType, targetList);        
    }

    public TargetModuleID[] getRunningModules(ModuleType moduleType,
                                              Target[] targetList)
        throws IllegalStateException, TargetException {
        return ws70DM.getRunningModules(moduleType, targetList);        
    }

    public Target[] getTargets() throws IllegalStateException {
        Target[] targets = ws70DM.getTargets();
        if(targets.length==1){            
            return targets;
        }
        
        // If there are more than one configurations, ask user to select one 
        // and set it in the Instance properties.
        String[] configs = new String[targets.length];
        for(int i=0;i<targets.length;i++){
            try{
                configs[i] = this.getConfigNameFromTarget(targets[i]);
            }catch(Exception ex){
                configs[i]="";
                ex.printStackTrace();
            }
        }
        
        InstanceProperties ip =  InstanceProperties.getInstanceProperties(this.getUri());
        String config = ip.getProperty("configName");        
        if(config==null){ 
            //perheps this is the first time
            WS70ConfigSelectDialog d = new WS70ConfigSelectDialog(configs);        
            if (DialogDisplayer.getDefault().notify(d) ==NotifyDescriptor.OK_OPTION){                
                config = d.getSelectedConfig();
            }             
            if(config==null){
                //if some error set the first target as default
                try{
                    ip.setProperty("configName", this.getConfigNameFromTarget(targets[0]));
                }catch(Exception ex){
                    ex.printStackTrace();
                }
            }else{
                ip.setProperty("configName", config);
            }
            for(int i=0;i<targets.length;i++){
                String cname = null;
                try{
                    cname = this.getConfigNameFromTarget(targets[i]);
                }catch(Exception ex){
                    cname = "";
                    ex.printStackTrace();
                }                
                if(config.equals(cname)){                    
                    return new Target[]{targets[i]};                    
                }
            }            
        }else{
            // It was already selected by the user previously            
            for(int j=0;j<targets.length;j++){
                String cname = null;
                try{
                    cname = this.getConfigNameFromTarget(targets[j]);
                }catch(Exception ex){
                    cname = "";
                    ex.printStackTrace();
                }                                
                if(config.equals(cname)){                                        
                    return new Target[]{targets[j]};                    
                }
            }
        }

        System.err.println("ERROR in GETTARGETS returning "+targets[0].getName());
        return new Target[]{targets[0]};
    }
    
    public boolean isRedeploySupported() {
        return false;
    }

    public ProgressObject redeploy(TargetModuleID[] targetModuleID,
                                   InputStream inputStream,
                                   InputStream inputStream2)
        throws IllegalStateException, UnsupportedOperationException {
        throw new UnsupportedOperationException("SunWebDeploymentManager.redeploy not supported yet."); // NOI18N
    }

    public ProgressObject redeploy(TargetModuleID[] tmID, File file,
                                   File file2)
        throws IllegalStateException, UnsupportedOperationException {
        throw new UnsupportedOperationException("SunWebDeploymentManager.redeploy not supported yet."); // NOI18N
    }

    
    public void release() {
    }
    

    public ProgressObject start(TargetModuleID[] tmID)
        throws IllegalStateException {
        return ws70DM.start(tmID);
    }


    public ProgressObject stop(TargetModuleID[] tmID)
        throws IllegalStateException {
        return ws70DM.stop(tmID);

    }


    public ProgressObject undeploy(TargetModuleID[] tmID)
        throws IllegalStateException {
        return ws70DM.undeploy(tmID);
    }
 

    // Extended methods
   public boolean startServer(String configName, String nodeName){
       return true;
   }
   public boolean stopServer(String configName, String nodeName){
       return true;
   }
    public String getServerStatus(String configName, String nodeName){
        return "Server Status";
    }

    public List getJVMOptions(String configName, Boolean debugOptions, String profilerName){
        try{
            Method getJVMOptions = dmClass.getDeclaredMethod("getJVMOptions", new Class[]{String.class, Boolean.class, String.class});
            List options = (List)getJVMOptions.invoke(this.ws70DM, new Object[]{configName, debugOptions, profilerName});
            return options;
            
        }catch(Exception ex){
            ex.printStackTrace();
        }        
        return null;
    }
    public Map getJVMProps(String configName) throws IllegalStateException {
        try{
            Method getJvmProps = dmClass.getDeclaredMethod("getJVMProps", new Class[]{String.class});
            Map options = (Map)getJvmProps.invoke(this.ws70DM, new Object[]{configName});
            return options;
            
        }catch(Exception ex){
            ex.printStackTrace();
        }        
        return null;        
    }
    public String addJVMOption(String configName, String OptionValue ,Boolean debugOptions, Boolean profilerOptions){
        return "added jvm option";
    }   

    public String deleteJVMOption(String configName, String OptionValue ,Boolean debugOptions, Boolean profilerOptions){
        return "deleted jvm option";
    }
    public List getNodes(String configName){
        return null;
    }
    public boolean deployAndReconfig(String configName, String nodeName){
        return true;
    }
    public boolean changeDebugStatus(String configName, boolean enableDisable){
        return true;
    }
    public boolean changeAppProfilerStatus(String configName, boolean enableDisable){
        return true;
    }
    public List getResources(ResourceType resType, String configName) throws Exception{
        String methodName = null;
        if(resType.eqauls(ResourceType.JDBC)){
            methodName = "getJDBCResources";                
        }else if(resType.eqauls(ResourceType.JNDI)){
            methodName = "getJNDIResources";
        }else if(resType.eqauls(ResourceType.MAIL)){
            methodName = "getMailResources";
        }else if(resType.eqauls(ResourceType.CUSTOM)){
            methodName = "getCustomResources";
        }
        try{            
            Method getResources = dmClass.getDeclaredMethod(methodName, new Class[]{String.class});
            List resources = (List)getResources.invoke(this.ws70DM, new Object[]{configName});
            return resources;            
        }catch(Exception ex){
            ex.printStackTrace();
            throw ex;            
        }        
    }    
    public void addJDBCResource(String configName, String resName, Map resElements){
        
    }    
    //NB50:Methods to be used for Resource method invocations, remove all other resource related method
    public void setResource(ResourceType resType, String configName, String resName, Map resElements) throws Exception{
        try{
            String methodName = null;
            if(resType.eqauls(ResourceType.JDBC)){
                methodName = "setJDBCResource";                
            }else if(resType.eqauls(ResourceType.JNDI)){
                methodName = "setJNDIResource";
            }else if(resType.eqauls(ResourceType.MAIL)){
                methodName = "setMailResource";
            }else if(resType.eqauls(ResourceType.CUSTOM)){
                methodName = "setCustomResource";
            }
            Method setResource = dmClass.getDeclaredMethod(methodName, new Class[]{String.class, String.class, Map.class});
            setResource.invoke(this.ws70DM, new Object[]{configName, resName, resElements});
            
        }catch(Exception ex){
            ex.printStackTrace();
            throw ex;            
        }        
    }
    //REMOVE all del methods
    public void deleteResource(ResourceType resType, String configName, String resName)throws Exception{
        try{
            String methodName = null;
            if(resType.eqauls(ResourceType.JDBC)){
                methodName = "delJDBCResource";                
            }else if(resType.eqauls(ResourceType.JNDI)){
                methodName = "delJNDIResource";
            }else if(resType.eqauls(ResourceType.MAIL)){
                methodName = "delMailResource";
            }else if(resType.eqauls(ResourceType.CUSTOM)){
                methodName = "delCustomResource";
            }
            Method delResource = dmClass.getDeclaredMethod(methodName, new Class[]{String.class, String.class});
            delResource.invoke(this.ws70DM, new Object[]{configName, resName});
            
        }catch(Exception ex){
            ex.printStackTrace();
            throw ex;            
        }                
    }    
    public Map getUserResourceProps(String configName, String resourceType, String jndiName, String propType) throws IllegalStateException {
        try{
            Method getUserResourceProps = dmClass.getDeclaredMethod("getUserResourceProps", 
                    new Class[]{String.class, String.class, String.class, String.class});
            Map resources = (Map)getUserResourceProps.invoke(this.ws70DM, new Object[]{configName, resourceType, jndiName, propType});
            return resources;
            
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return null;                
    }
    public void addCustomResource(String configName, String resName, Map resElements){
        
    }

    public void addJNDIResource(String configName, String resName, Map resElements){
        
    }
    public String setJVMOptions(String configName, List jvmOptions, Boolean debugOptions, 
                                String profilerName) throws Exception {
          try{
            Method setJVMOptions = dmClass.getDeclaredMethod("setJVMOptions", 
                    new Class[]{String.class, List.class, Boolean.class, String.class});
            String message = (String)setJVMOptions.invoke(this.ws70DM, new Object[]{configName, jvmOptions, debugOptions, profilerName});
            return message;            
        }catch(Exception ex){
            throw ex;            
        }          
    }
    public String setJVMProps(String configName, Map jvmElements) throws Exception {
          try{
            Method setJVMProps = dmClass.getDeclaredMethod("setJVMProps", 
                    new Class[]{String.class, Map.class});
            String message = (String)setJVMProps.invoke(this.ws70DM, new Object[]{configName, jvmElements});
            return message;            
        }catch(Exception ex){
            throw ex;            
        }                  
    }
    public void addMailResource(String configName, String resName, Map resElements){
        
    }
    
    public boolean isRunning(){        
       try {
             java.net.InetSocketAddress isa = new java.net.InetSocketAddress(java.net.InetAddress.getByName("localhost"), port);
             java.net.Socket socket = new java.net.Socket();
             socket.connect(isa, 1);
             socket.close();             
             return true;
        } catch (IOException e) {            
            return false;
        }        

    }
    
    public boolean isRunning(Target target){
        if(target==null){
            return isRunning();
        }
        String configName = null;
        try{            
            configName = this.getConfigNameFromTarget(target);
            Method isRunning = dmClass.getDeclaredMethod("isServerRunning",  new Class[]{String.class});
            Boolean retVal= (Boolean)isRunning.invoke(this.ws70DM, new Object[]{configName});            
            return retVal.booleanValue();
            
        }catch(Exception ex){            
            ex.printStackTrace();
            return false;
        }                             
    }
    private String getConfigNameFromTarget(Target target) throws Exception{
        try{
            Method getConfigName = target.getClass().getDeclaredMethod("getConfigName", new Class[]{});
            String configName = (String)getConfigName.invoke(target, new Object[]{});
            return configName;
            
        }catch(Exception ex){
            throw ex;        
        }                             
    }

}
