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
import java.lang.reflect.InvocationTargetException;

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
import org.netbeans.modules.j2ee.sun.ws7.ui.WS70ServerUIWizardIterator;

import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.ErrorManager;
import org.openide.util.NbBundle;

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
    // Target config selected by the user for deployment
    private Target defaultTarget;
    
    // debug-jvm-option value in the server.xml of the default config.
    private String debugOptions;
    private boolean isDebugModeEnabled;

    /** Creates a new instance of WS70SunDeploymentManager */
    public WS70SunDeploymentManager(DeploymentFactory df, DeploymentManager dm, String uri, String username, String password) {
        this.ws70DF = df;
        this.ws70DM = dm;
        this.uri = uri;
        this.userName = username;
        this.password = password;
        // ws70DM can be null in getDisconnectedDeploymentManager case if
        // Web project's target server was removed and the IDE restarts
        if(ws70DM!=null){
            dmClass = ws70DM.getClass();
            serverLocation = WS70URIManager.getLocation(uri);
            host = WS70URIManager.getHostFromURI(uri);
            String p = WS70URIManager.getPortFromURI(uri);
            try{
                port = Integer.parseInt(p);
            }catch(java.lang.NumberFormatException n){
                ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, n);
            }                
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

    public String getHost(){
        return host;        
    }

    public int getPort(){
        return port;
    }
    
    // Called from Manager Node Customizer only
    public void refreshInnerDM(String uname, String pword) {
        if(uname.equals(userName) && pword.equals(password)){            
            // Nothing is changed, take no action
            return;
        }
        ClassLoader origClassLoader=Thread.currentThread().getContextClassLoader();
        try{
            String ws70url = WS70URIManager.getURIWithoutLocation(uri);
            if(this.isAdminOnSSL()){
                ws70url=ws70url+":https";
            }

            ClassLoader loader = ws70DF.getClass().getClassLoader();
            Thread.currentThread().setContextClassLoader(loader);

            ws70DM = ws70DF.getDeploymentManager(ws70url, uname, pword);
            userName = uname;
            password = pword;
            InstanceProperties ip =  InstanceProperties.getInstanceProperties(this.getUri());
            ip.setProperty(InstanceProperties.USERNAME_ATTR, uname);
            ip.setProperty(InstanceProperties.PASSWORD_ATTR, pword);
        }catch(DeploymentManagerCreationException ex){
            ErrorManager.getDefault().log(ErrorManager.EXCEPTION, ex.getMessage());            
        }finally{
            Thread.currentThread().setContextClassLoader(origClassLoader);
        }        
    }

    public boolean isLocalServer(){
        InstanceProperties ip =  InstanceProperties.getInstanceProperties(uri);
        String isLocal = ip.getProperty(WS70ServerUIWizardIterator.PROP_LOCAL_SERVER);
        return Boolean.parseBoolean(isLocal);
    }

    public boolean isAdminOnSSL(){
        InstanceProperties ip =  InstanceProperties.getInstanceProperties(uri);
        String isSSL = ip.getProperty(WS70ServerUIWizardIterator.PROP_SSL_PORT);
        return Boolean.parseBoolean(isSSL);
    }    

    // returns the default config Target selected by the user.
    // used by JSPServletFinder
    public Target getDefaultTarget(){
        return defaultTarget;
    }

    public DeploymentConfiguration createConfiguration(DeployableObject deplObj)
        throws InvalidModuleException {
        if (!ModuleType.WAR.equals(deplObj.getType())) {
            throw new InvalidModuleException(
                      NbBundle.getMessage(WS70SunDeploymentManager.class, "Invalid_MODULE"));
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

        if (swa == null)
            throw new IllegalStateException(NbBundle.getMessage(WS70SunDeploymentManager.class, "ERR_NULL_SWA"));

        String ctxRoot = swa.getContextRoot();
        ErrorManager.getDefault().log(
                ErrorManager.USER, NbBundle.getMessage(WS70SunDeploymentManager.class, "MSG_CONTEXTROOT", ctxRoot));
        try {
            Method distribute = dmClass.getDeclaredMethod("distribute", new Class[]{Target[].class, File.class, String.class});            
            ProgressObject po = (ProgressObject)distribute.invoke(this.ws70DM, new Object[]{targets, moduleArchive, ctxRoot});
            return po;    
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    public ProgressObject distribute(Target[] target, ModuleType moduleType, InputStream inputStream, InputStream inputStream0) throws IllegalStateException {
        return distribute(target, inputStream, inputStream0);
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
            throw new DConfigBeanVersionUnsupportedException(
                    NbBundle.getMessage(WS70SunDeploymentManager.class, "Invalid_CONFIG_VERSION"));
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
        InstanceProperties ip =  InstanceProperties.getInstanceProperties(this.getUri());
        String config = ip.getProperty("configName");// NO I18N
        if(targets.length==1){       
            if(config==null){
                try{
                    String cname = this.getConfigNameFromTarget(targets[0]);
                    ip.setProperty("configName", cname);// NO I18N
                }catch(Exception ex){
                    ex.printStackTrace();
                }
            }
            defaultTarget = targets[0];
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
        

        if(config==null){ 
            //perheps this is the first time
            WS70ConfigSelectDialog d = new WS70ConfigSelectDialog(configs);        
            if (DialogDisplayer.getDefault().notify(d) ==NotifyDescriptor.OK_OPTION){                
                config = d.getSelectedConfig();
            }             
            if(config==null){
                //if some error set the first target as default
                try{
                    ip.setProperty("configName", this.getConfigNameFromTarget(targets[0]));// NO I18N
                }catch(Exception ex){
                    ex.printStackTrace();
                }
            }else{
                ip.setProperty("configName", config);// NO I18N
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
                    defaultTarget = targets[i];
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
                    defaultTarget = targets[j];
                    return new Target[]{targets[j]};                    
                }
            }
        }

        ErrorManager.getDefault().log(ErrorManager.WARNING, 
                NbBundle.getMessage(WS70SunDeploymentManager.class, 
                "ERR_GETTARGETS", targets[0].getName()));
        defaultTarget = targets[0];
        return new Target[]{targets[0]};
    }
    
    public boolean isRedeploySupported() {
        return false;
    }

    public ProgressObject redeploy(TargetModuleID[] targetModuleID,
                                   InputStream inputStream,
                                   InputStream inputStream2)
        throws IllegalStateException, UnsupportedOperationException {
        throw new UnsupportedOperationException(NbBundle.getMessage(
                WS70SunDeploymentManager.class, "UNSUPPORTED_REDPLOY"));
    }

    public ProgressObject redeploy(TargetModuleID[] tmID, File file,
                                   File file2)
        throws IllegalStateException, UnsupportedOperationException {
        throw new UnsupportedOperationException(NbBundle.getMessage(
                WS70SunDeploymentManager.class, "UNSUPPORTED_REDPLOY"));
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
   public void startServer(String configName) throws Exception{
        try{
            Method startServer = dmClass.getDeclaredMethod("startServer", new Class[]{String.class}); //NOI18N
            Boolean retVal = (Boolean)startServer.invoke(this.ws70DM, new Object[]{configName});            
        }catch(InvocationTargetException ite){
            ite.printStackTrace();
            throw (Exception)ite.getTargetException();
        }catch(Exception ex){
            ex.printStackTrace();
            throw ex;
        }                
   }
   public void stopServer(String configName) throws Exception {
        try{
            Method stopServer = dmClass.getDeclaredMethod("stopServer", new Class[]{String.class});//NOI18N
            Boolean retVal = (Boolean)stopServer.invoke(this.ws70DM, new Object[]{configName});            
            
        }catch(InvocationTargetException ite){
            ite.printStackTrace();
            throw (Exception)ite.getTargetException();            
        }catch(Exception ex){
            ex.printStackTrace();
            throw ex;
        }                
   }

    public List getJVMOptions(String configName, Boolean debugOptions, String profilerName){
        try{
            Method getJVMOptions = dmClass.getDeclaredMethod("getJVMOptions", new Class[]{String.class, Boolean.class, String.class});//NOI18N
            List options = (List)getJVMOptions.invoke(this.ws70DM, new Object[]{configName, debugOptions, profilerName});
            return options;
            
        }catch(Exception ex){
            ex.printStackTrace();
        }        
        return null;
    }
    public Map getJVMProps(String configName) throws IllegalStateException {
        try{
            Method getJvmProps = dmClass.getDeclaredMethod("getJVMProps", new Class[]{String.class});//NOI18N
            Map options = (Map)getJvmProps.invoke(this.ws70DM, new Object[]{configName});
            return options;
            
        }catch(Exception ex){
            ex.printStackTrace();
        }        
        return null;        
    }
    public void deployAndReconfig(String configName) throws Exception {
        try{                                                                  
            Method deployAndReconfig = dmClass.getDeclaredMethod("deployAndReconfig",  new Class[]{String.class});
            deployAndReconfig.invoke(this.ws70DM, new Object[]{configName});            
        }catch(InvocationTargetException ite){            
            throw (Exception)ite.getTargetException();            
        }catch(Exception ex){            
            throw ex;
        }                 
    }
    public void changeDebugStatus(String configName, 
                                  boolean enableDisable) throws Exception{
        try{
            Method changeDebugStatus = dmClass.getDeclaredMethod("changeDebugStatus", new Class[]{String.class, boolean.class});//NOI18N
            changeDebugStatus.invoke(this.ws70DM, new Object[]{configName, Boolean.valueOf(enableDisable)});
        }catch(InvocationTargetException ite){
            ite.printStackTrace();
            throw (Exception)ite.getTargetException();            
        }catch(Exception ex){
            ex.printStackTrace();
            throw ex;
        }
        deployAndReconfig(configName);
    }
   // debug-jvm-option string in the server.xml
    public String getDebugOptions(){                
        return this.debugOptions;
    }
    // debug-jvm-option string in the server.xml
    public void setDebugOptions(String debugString){                
        this.debugOptions = debugString;
    }
    public boolean isDebugModeEnabled(){
        return this.isDebugModeEnabled;
    }
    public void setDebugModeEnabled(boolean debugMode){
        isDebugModeEnabled  = debugMode;
    }    
    public String getNodeNameForTarget(Target target){        
        try{
            String configName = this.getConfigNameFromTarget(target);
            Method getNodeName = dmClass.getDeclaredMethod("getNodeName", new Class[]{String.class});
            return (String)getNodeName.invoke(this.ws70DM, new Object[]{configName});            
        }catch(Exception ex){
            ex.printStackTrace(); 
        }        
        return null;
    }
    public boolean changeAppProfilerStatus(String configName, boolean enableDisable){
        return true;
    }
    public List getResources(ResourceType resType, String configName) throws Exception{
        String methodName = null;
        if(resType.eqauls(ResourceType.JDBC)){
            methodName = "getJDBCResources";//NOI18N
        }else if(resType.eqauls(ResourceType.JNDI)){
            methodName = "getJNDIResources";//NOI18N
        }else if(resType.eqauls(ResourceType.MAIL)){
            methodName = "getMailResources";//NOI18N
        }else if(resType.eqauls(ResourceType.CUSTOM)){
            methodName = "getCustomResources";//NOI18N
        }
        try{            
            Method getResources = dmClass.getDeclaredMethod(methodName, new Class[]{String.class});
            List resources = (List)getResources.invoke(this.ws70DM, new Object[]{configName});
            return resources;            
        }catch(InvocationTargetException ite){
            ite.printStackTrace();
            throw (Exception)ite.getTargetException();            
        }catch(Exception ex){
            ex.printStackTrace();
            throw ex;
        }        
    }    

    
    public void setResource(ResourceType resType, String configName, 
            String resName, Map resElements, boolean reconfig) throws Exception{
        try{
            String methodName = null;
            if(resType.eqauls(ResourceType.JDBC)){
                methodName = "setJDBCResource";//NOI18N
            }else if(resType.eqauls(ResourceType.JNDI)){
                methodName = "setJNDIResource";//NOI18N
            }else if(resType.eqauls(ResourceType.MAIL)){
                methodName = "setMailResource";//NOI18N
            }else if(resType.eqauls(ResourceType.CUSTOM)){
                methodName = "setCustomResource";//NOI18N
            }
            Method setResource = dmClass.getDeclaredMethod(methodName, new Class[]{String.class, String.class, Map.class});
            setResource.invoke(this.ws70DM, new Object[]{configName, resName, resElements});
            if(reconfig){
                this.deployAndReconfig(configName);
            }
            
        }catch(InvocationTargetException ite){
            ite.printStackTrace();
            throw (Exception)ite.getTargetException();            
        }catch(Exception ex){
            ex.printStackTrace();
            throw ex;
        }        
    }
    public void setUserResourceProp(String configName, String resourceType, 
                        String jndiName, String propType, List userProps, boolean reconfig) throws Exception {
         try{

            Method setUserProps = dmClass.getDeclaredMethod("setUserResourceProp", 
                        new Class[]{String.class, String.class, String.class, String.class, List.class});
            setUserProps.invoke(this.ws70DM, new Object[]{configName, resourceType, 
                                        jndiName, propType, userProps});
            if(reconfig){
                this.deployAndReconfig(configName);
            }
            
        }catch(InvocationTargetException ite){
            ite.printStackTrace();
            throw (Exception)ite.getTargetException();            
        }catch(Exception ex){
            ex.printStackTrace();
            throw ex;
        }               
    }    
    
    public void deleteResource(ResourceType resType, String configName, String resName)throws Exception{
        try{
            String methodName = null;
            if(resType.eqauls(ResourceType.JDBC)){
                methodName = "delJDBCResource";//NOI18N
            }else if(resType.eqauls(ResourceType.JNDI)){
                methodName = "delJNDIResource";//NOI18N
            }else if(resType.eqauls(ResourceType.MAIL)){
                methodName = "delMailResource";//NOI18N
            }else if(resType.eqauls(ResourceType.CUSTOM)){
                methodName = "delCustomResource";//NOI18N
            }
            Method delResource = dmClass.getDeclaredMethod(methodName, new Class[]{String.class, String.class});
            delResource.invoke(this.ws70DM, new Object[]{configName, resName});
            this.deployAndReconfig(configName);
            
        }catch(InvocationTargetException ite){
            ite.printStackTrace();
            throw (Exception)ite.getTargetException();            
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
    public void addJdbcResource(String configName, String resName, 
                                Map resAttrs) throws Exception{
        try{
            Method addJdbcResource = dmClass.getDeclaredMethod("addJDBCResource", 
                    new Class[]{String.class, String.class, Map.class});
            addJdbcResource.invoke(this.ws70DM, new Object[]{configName, resName, resAttrs});            
        }catch(InvocationTargetException ite){            
            throw (Exception)ite.getTargetException();
        }catch(Exception ex){
            throw ex;
        }                   
        
    }        
    public void addCustomResource(String configName, String resName, 
                                Map resAttrs) throws Exception{
        try{
            Method addCustomResource = dmClass.getDeclaredMethod("addCustomResource", 
                    new Class[]{String.class, String.class, Map.class});
            addCustomResource.invoke(this.ws70DM, new Object[]{configName, resName, resAttrs});            
        }catch(InvocationTargetException ite){            
            throw (Exception)ite.getTargetException();
        }catch(Exception ex){
            throw ex;
        }                   
        
    }

    public void addJNDIResource(String configName, String resName, 
                                Map resAttrs) throws Exception{
        try{
            Method addJNDIResource = dmClass.getDeclaredMethod("addJNDIResource", 
                    new Class[]{String.class, String.class, Map.class});
            addJNDIResource.invoke(this.ws70DM, new Object[]{configName, resName, resAttrs});            
        }catch(InvocationTargetException ite){            
            throw (Exception)ite.getTargetException();
        }catch(Exception ex){
            throw ex;
        }                   
    }
    public void addMailResource(String configName, String resName, 
                                Map resAttrs) throws Exception{
        try{
            Method addMailResource = dmClass.getDeclaredMethod("addMailResource", 
                    new Class[]{String.class, String.class, Map.class});
            addMailResource.invoke(this.ws70DM, new Object[]{configName, resName, resAttrs});            
        }catch(InvocationTargetException ite){            
            throw (Exception)ite.getTargetException();
        }catch(Exception ex){
            throw ex;
        }                          
    }    
    public void setJVMOptions(String configName, List jvmOptions, Boolean debugOptions, 
                                String profilerName) throws Exception {
          try{
            Method setJVMOptions = dmClass.getDeclaredMethod("setJVMOptions", 
                    new Class[]{String.class, List.class, Boolean.class, String.class});
            setJVMOptions.invoke(this.ws70DM, new Object[]{configName, jvmOptions, debugOptions, profilerName});            
            this.deployAndReconfig(configName);            
        }catch(InvocationTargetException ite){            
            throw (Exception)ite.getTargetException();
        }catch(Exception ex){
            throw ex;
        }          
    }
    public void setJVMProps(String configName, Map jvmElements) throws Exception {
        try{
            Method setJVMProps = dmClass.getDeclaredMethod("setJVMProps", 
                    new Class[]{String.class, Map.class});
            setJVMProps.invoke(this.ws70DM, new Object[]{configName, jvmElements});            
            this.deployAndReconfig(configName);
        }catch(InvocationTargetException ite){            
            throw (Exception)ite.getTargetException();
        }catch(Exception ex){
            throw ex;
        }                  
    }

    
    public boolean isRunning(){        
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
    
    public boolean isRunning(String configName){
        if(configName==null){
            return isRunning();
        }        
        try{
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
            
        }catch(InvocationTargetException ite){            
            throw (Exception)ite.getTargetException();
        }catch(Exception ex){
            throw ex;
        }                             
    }
}
