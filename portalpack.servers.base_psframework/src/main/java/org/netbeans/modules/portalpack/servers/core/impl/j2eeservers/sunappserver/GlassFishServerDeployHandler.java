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

package org.netbeans.modules.portalpack.servers.core.impl.j2eeservers.sunappserver;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.deploy.spi.Target;
import javax.enterprise.deploy.spi.TargetModuleID;
import org.netbeans.modules.j2ee.deployment.plugins.api.UISupport;
import org.netbeans.modules.portalpack.servers.core.JSR88DeploymentHandler;
import org.netbeans.modules.portalpack.servers.core.PSModuleID;
import org.netbeans.modules.portalpack.servers.core.api.PSDeploymentManager;
import org.netbeans.modules.portalpack.servers.core.common.DeploymentException;
import org.netbeans.modules.portalpack.servers.core.common.ExtendedClassLoader;
import org.netbeans.modules.portalpack.servers.core.common.ProcessLogManager;
import org.netbeans.modules.portalpack.servers.core.impl.j2eeservers.api.ServerDeployHandler;
import org.netbeans.modules.portalpack.servers.core.util.Command;
import org.netbeans.modules.portalpack.servers.core.util.NetbeanConstants;
import org.netbeans.modules.portalpack.servers.core.util.PSConfigObject;
import org.openide.util.NbBundle;

/**
 *
 * @author root
 */
public class GlassFishServerDeployHandler implements ServerDeployHandler{
    private static Logger logger = Logger.getLogger(NetbeanConstants.PORTAL_LOGGER);
    private PSConfigObject psconfig;
    private PSDeploymentManager dm;
    private String uri;
    private ExtendedClassLoader loader;
    private ExtendedClassLoader jmxLoader;
    private boolean isSecure;
    private boolean secureCheckDone;
    
    /**
     * Creates a new instance of SunAppServerDeployHandler
     */
    public GlassFishServerDeployHandler(PSDeploymentManager dm) {
        this.dm = dm;
        this.psconfig = dm.getPSConfig();
        this.uri = dm.getUri();
    }
    
     private void deployOnGlassFish(String warFile,String contextroot) throws Exception
    {
        File file = File.createTempFile("pcpwd",".tmp");
        file.deleteOnExit();
        
        FileOutputStream fout = null;
        try {
             fout = new FileOutputStream(file);
             fout.write(new String("AS_ADMIN_PASSWORD="+psconfig.getProperty(SunAppServerConstants.SERVER_PASSWORD)).getBytes());
        } catch (FileNotFoundException ex) {
            throw ex;
        } catch(IOException ex){
            throw ex;
        }finally{
            fout.flush();
            fout.close();
        }

        String gfVersion = getGlassFishVersion();

        Command cmd = new Command();
        String ext = "";
         if (org.openide.util.Utilities.isWindows()){
            ext = ".bat";
        }

        if(SunAppServerConstants.GLASSFISH_V2.equals(gfVersion)) {

            cmd.add(psconfig.getServerHome() + File.separator + "bin" + File.separator + "asadmin" + ext);
            cmd.add("deploy");
            cmd.add("--precompilejsp=false");
            cmd.add("--port");
            cmd.add(psconfig.getAdminPort());
            cmd.add("--user");
            cmd.add(psconfig.getProperty(SunAppServerConstants.SERVER_USER));
            cmd.add("--passwordfile");
            cmd.add(file.getAbsolutePath());
            if(contextroot != null) {
                cmd.add("--contextroot");
                cmd.add(contextroot);
                cmd.add("--name");
                cmd.add(contextroot);
            }

            cmd.add("--force=true");
            cmd.add(warFile);
        } else if(SunAppServerConstants.GLASSFISH_V3.equals(gfVersion)){
            cmd.add(psconfig.getServerHome() + File.separator + "bin" + File.separator + "asadmin" + ext);
            
            cmd.add("--port");
            cmd.add(psconfig.getAdminPort());
            cmd.add("--user");
            cmd.add(psconfig.getProperty(SunAppServerConstants.SERVER_USER));
            cmd.add("--passwordfile");
            cmd.add(file.getAbsolutePath());
            cmd.add("deploy");
            cmd.add("--precompilejsp=false");
            if(contextroot != null) {
                cmd.add("--contextroot");
                cmd.add(contextroot);
                cmd.add("--name");
                cmd.add(contextroot);
            }

            cmd.add("--force=true");

            cmd.add(warFile);
        }
                
        logger.info(cmd.toString());
        try {
            
            runProcess(cmd.getCmdArray(),true);
        } catch (Exception ex) {
            throw new Exception(NbBundle.getMessage(GlassFishServerDeployHandler.class,"MSG_DEPLOY_ON_GLASSFISH_FAILED"));
        }finally{
            file.delete();
        }
        logger.info("Password file: "+file.getAbsolutePath());
        
    }

    private String getGlassFishVersion() {
        String gfVersion = psconfig.getProperty(SunAppServerConstants.GLASSFISH_VERSON);
        if (gfVersion == null || gfVersion.trim().length() == 0) {
            gfVersion = SunAppServerConstants.GLASSFISH_V2;
        }
        return gfVersion;
    }

    private Properties getJSR88Properties() {

        String gfVersion = getGlassFishVersion();
        Properties props = new Properties();
        if (isSecure) {
            props.put("jsr88.dm.id", "deployer:Sun:AppServer::" + psconfig.getHost() + ":" + psconfig.getAdminPort() + ":https");
        } else {
            props.put("jsr88.dm.id", "deployer:Sun:AppServer::" + psconfig.getHost() + ":" + psconfig.getAdminPort());
        }
        props.put("jsr88.dm.user", psconfig.getProperty(SunAppServerConstants.SERVER_USER));
        props.put("jsr88.dm.passwd", psconfig.getProperty(SunAppServerConstants.SERVER_PASSWORD));
        
        if (SunAppServerConstants.GLASSFISH_V2.equals(gfVersion)) {
            props.put("jsr88.df.classname", "com.sun.enterprise.deployapi.SunDeploymentFactory");
        } else {
            // for V3
            props.put("jsr88.df.classname", "org.glassfish.deployapi.SunDeploymentFactory");
        }
        return props;
    }

    private String getJMXHandlerClassName(String gfVersion) {

        if (SunAppServerConstants.GLASSFISH_V2.equals(gfVersion)) {
            return "org.netbeans.modules.portalpack.servers.core.impl.j2eeservers.sunappserver.GlassFishJMXHandler";
        } else if (SunAppServerConstants.GLASSFISH_V3.equals(gfVersion)) {
            return "org.netbeans.modules.portalpack.servers.core.impl.j2eeservers.sunappserver.GlassFishV3JMXHandler";
        } else
            return "org.netbeans.modules.portalpack.servers.core.impl.j2eeservers.sunappserver.GlassFishJMXHandler";

    }

    private String getJMXConnectorPort(String gfVersion) {

        if (SunAppServerConstants.GLASSFISH_V2.equals(gfVersion)) {
            return psconfig.getAdminPort();
        } else if (SunAppServerConstants.GLASSFISH_V3.equals(gfVersion)) {
            return psconfig.getProperty(SunAppServerConstants.JMX_CONNECTOR_PORT);
        } else
            return psconfig.getProperty(SunAppServerConstants.JMX_CONNECTOR_PORT);   
    }
     
     
     private void unDeployFromGlassFish(String appName) throws Exception
    {
        File file = File.createTempFile("pcpwd",".tmp");
        file.deleteOnExit();
        
        FileOutputStream fout = null;
        try {
             fout = new FileOutputStream(file);
             fout.write(new String("AS_ADMIN_PASSWORD="+psconfig.getProperty(SunAppServerConstants.SERVER_PASSWORD)).getBytes());
        } catch (FileNotFoundException ex) {
            throw ex;
        } catch(IOException ex){
            throw ex;
        }
             
        Command cmd = new Command();
        String ext = "";
         if (org.openide.util.Utilities.isWindows()){
            ext = ".bat";
        }
        cmd.add(psconfig.getServerHome() + File.separator + "bin" + File.separator + "asadmin" + ext);
        cmd.add("undeploy");
        cmd.add("--port");
        cmd.add(psconfig.getAdminPort());
        cmd.add("-u");
        cmd.add(psconfig.getProperty(SunAppServerConstants.SERVER_USER));
        cmd.add("--passwordfile");
        cmd.add(file.getAbsolutePath());
        cmd.add(appName);
                
        logger.info(cmd.toString());    
       
        try {
            
            runProcess(cmd.getCmdArray(),true);
        } catch (Exception ex) {
            throw new Exception(NbBundle.getMessage(GlassFishServerDeployHandler.class,"MSG_UNDEPLOY_ON_GLASSFISH_FAILED"));
        }
        logger.info("Password file: "+file.getAbsolutePath());
        file.delete();
        
    } 
      
    private int runProcess(String[] cmdArray, boolean wait) throws Exception {
        final Process child = Runtime.getRuntime().exec(cmdArray);
        
        SUNASProcessLogSupport logSupport = new SUNASProcessLogSupport();
        ProcessLogManager manager = new ProcessLogManager(dm);
        manager.openProcessLog(child,logSupport, "" + System.currentTimeMillis());
        if (wait)
            child.waitFor();
        while(!manager.isDone() && !logSupport.isErrorInOutput() && !logSupport.hasSuccess())
        {
            Thread.currentThread().sleep(10);
        }
        if(!logSupport.isErrorInOutput())
           return child.exitValue();
        else
           throw new Exception("Command failed");
    }

    public boolean deploy(String warFile) throws Exception {
        try{
            
           String gfVersion = psconfig.getProperty(SunAppServerConstants.GLASSFISH_VERSON);
           if(gfVersion == null || gfVersion.trim().length() == 0)
               gfVersion = SunAppServerConstants.GLASSFISH_V2;
           
           if(gfVersion.equals(SunAppServerConstants.GLASSFISH_V2)
                   || gfVersion.equals(SunAppServerConstants.GLASSFISH_V3)) {
               
               _deploy(warFile);
               
           }else {
               
               deployOnGlassFish(warFile,null);
           }
           
        }catch(Exception e){
            logger.log(Level.SEVERE, "Error",e);
            throw e;
        }
        return true;
    }

    public boolean undeploy(String appName) throws Exception {

        String gfVersion = psconfig.getProperty(SunAppServerConstants.GLASSFISH_VERSON);
        if(gfVersion == null || gfVersion.trim().length() == 0)
            gfVersion = SunAppServerConstants.GLASSFISH_V2;

        try{
           
            if(gfVersion.equals(SunAppServerConstants.GLASSFISH_V2)
                    || gfVersion.equals(SunAppServerConstants.GLASSFISH_V3)) {
                
                _undeploy(appName);

            } else {
                unDeployFromGlassFish(appName);
            }

        }catch(Exception e){
            logger.log(Level.SEVERE, "Error",e);
            throw e;
        }
        return true;
    }

    public boolean install() throws Exception {
        return true;
    }
    
     private void writeErrorToOutput(String uri,Exception e) {
        e.printStackTrace(UISupport.getServerIO(uri).getErr());
    }
   
    public void _deploy(String warFile) throws DeploymentException
    {
        _deploy(warFile,null);
    }

    private void doSecureCheck() {
         if (secureCheckDone == false) {
            try{
                isSecure = PortDetector.isSecurePort(psconfig.getHost(), Integer.parseInt(psconfig.getAdminPort()));
                secureCheckDone = true;

            } catch(Exception e){
            }
        }

    }
    //context should be passed as null for war deployment.
    public void _deploy(String warFile, String context) throws DeploymentException
    {
        doSecureCheck();
        Properties props = getJSR88Properties();
        
        ClassLoader ld = getServerClassLoader(new File(psconfig.getServerHome()));
         
        JSR88DeploymentHandler deploymentHandler = new JSR88DeploymentHandler(ld,props,UISupport.getServerIO(uri));
        
        //For war deployment
        if(context == null) {
            String warFileName = new File(warFile).getName();
            context = warFileName.substring(0,warFileName.length()-4);
        }
        
        deploymentHandler.runApp(warFile,context);
        deploymentHandler.releaseDeploymentManager();
    }
    
    public void _undeploy(String appName) throws DeploymentException
    {
        doSecureCheck();
        Properties props = getJSR88Properties();
        
        ClassLoader ld = getServerClassLoader(new File(psconfig.getServerHome()));
         
        JSR88DeploymentHandler deploymentHandler = new JSR88DeploymentHandler(ld,props,UISupport.getServerIO(uri));
        
        deploymentHandler.undeployApp(appName);
        deploymentHandler.releaseDeploymentManager();
    }

    public void restartJSR88(String appName) throws DeploymentException
    {
        doSecureCheck();
        Properties props = getJSR88Properties();

        ClassLoader ld = getServerClassLoader(new File(psconfig.getServerHome()));

        JSR88DeploymentHandler deploymentHandler = new JSR88DeploymentHandler(ld,props,UISupport.getServerIO(uri));

        deploymentHandler.restart(appName);
        deploymentHandler.releaseDeploymentManager();
    }

    public void restart(String appName) throws Exception{
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();

        String gfVersion = getGlassFishVersion();
        try{

            int adminPort = 4848;
            try {
                adminPort = Integer.parseInt(getJMXConnectorPort(gfVersion));
            } catch (Exception e) {
            }
            ClassLoader ld = getJMXClassLoader(new File(psconfig.getServerHome()));
            Thread.currentThread().setContextClassLoader(ld);
            
            Class clazz = ld.loadClass(getJMXHandlerClassName(gfVersion));
            Constructor con = clazz.getConstructor(String.class,String.class,String.class,int.class);
            Object instance = con.newInstance(psconfig.getHost(), psconfig.getAdminUser(), psconfig.getProperty(SunAppServerConstants.SERVER_PASSWORD), adminPort);
            //GlassFishJMXHandler jmxHandler = new GlassFishJMXHandler(psconfig.getHost(), psconfig.getAdminUser(), psconfig.getAdminPassWord(), adminPort);
            
            Method method = clazz.getMethod("reload", String.class);
            method.invoke(instance, appName);

        }catch(Throwable ex){
            ex.printStackTrace();
        }finally {
            Thread.currentThread().setContextClassLoader(contextClassLoader);
        }
    }

    public TargetModuleID[] getAvailableModules(Target[] target) {

        try{
            
            doSecureCheck();
            Properties props = getJSR88Properties();


            ClassLoader ld = getServerClassLoader(new File(psconfig.getServerHome()));

            JSR88DeploymentHandler deploymentHandler = new JSR88DeploymentHandler(ld,props,UISupport.getServerIO(uri));


            TargetModuleID[] tmids = deploymentHandler.getRunningModules();

            List<TargetModuleID> list = new ArrayList();
            for(TargetModuleID tmid:tmids) {
                //PSModuleID psMid = new PSModuleID(tmid.getTarget(), tmid.getModuleID(), "");
                PSModuleID psMid = new PSModuleID(target[0], tmid.getModuleID(), "");
                list.add(psMid);
            }
            return (TargetModuleID[]) list.toArray(new TargetModuleID[0]);

        }catch(Exception e) {
            logger.log(Level.SEVERE,"error",e);
            return new TargetModuleID[0];
        }
    }
    
    private  void updatePluginLoader(File platformLocation, ExtendedClassLoader loader) throws Exception{
        try {
            java.io.File f = platformLocation;
            if (null == f || !f.exists()){
                return;
            }

            String gfVersion = getGlassFishVersion();
            if(SunAppServerConstants.GLASSFISH_V3.equals(gfVersion)) {

                String installRoot = f.getAbsolutePath();
                f = new File(installRoot+"/modules/deployment-client.jar");//NOI18N
                loader.addURL(f);
                f = new File(installRoot+"/modules/javax.enterprise.deploy.jar");//NOI18N
                loader.addURL(f);
                f = new File(installRoot+"/modules/common-util.jar");//NOI18N
                loader.addURL(f);
                f = new File(installRoot+"/modules/deployment-common.jar");//NOI18N
                loader.addURL(f);
                f = new File(installRoot+"/modules/deployment-javaee-core.jar");//NOI18N
                loader.addURL(f);
                f = new File(installRoot+"/modules/admin-cli.jar");//NOI18N
                loader.addURL(f);
                f = new File(installRoot+"/modules/admin-core.jar");//NOI18N
                loader.addURL(f);
                f = new File(installRoot+"/modules/admin-util.jar");//NOI18N
                loader.addURL(f);

            } else { //for Glassfish V2

                String installRoot = f.getAbsolutePath();
                f = new File(installRoot+"/lib/appserv-admin.jar");//NOI18N
                loader.addURL(f);
                f = new File(installRoot+"/lib/appserv-ext.jar");//NOI18N
                loader.addURL(f);
                f = new File(installRoot+"/lib/appserv-rt.jar");//NOI18N
                loader.addURL(f);
                f = new File(installRoot+"/lib/appserv-cmp.jar");//NOI18N
                loader.addURL(f);
                f = new File(installRoot+"/lib/commons-logging.jar");//NOI18N
                loader.addURL(f);
                f = new File(installRoot+"/lib/admin-cli.jar");//NOI18N
                loader.addURL(f);
                f = new File(installRoot+"/lib/common-laucher.jar");//NOI18N
                loader.addURL(f);
                f = new File(installRoot+"/lib/j2ee.jar");//NOI18N
                loader.addURL(f);
                f = new File(installRoot+"/lib/install/applications/jmsra/imqjmsra.jar");//NOI18N
                loader.addURL(f);
            }
	    
	} catch (Exception ex2) {
	    throw new Exception(ex2.getLocalizedMessage());
	}
    }

    private void updateJMXPluginLoader(File platformLocation, ExtendedClassLoader loader) throws Exception{
        try {
            java.io.File f = platformLocation;
            if (null == f || !f.exists()){
                return;
            }
            
            String installRoot = f.getAbsolutePath();
            String gfVersion = getGlassFishVersion();

            if(SunAppServerConstants.GLASSFISH_V3.equals(gfVersion)) {
                
                f = new File(installRoot+"/modules/deployment-client.jar");//NOI18N
                loader.addURL(f);
                f = new File(installRoot+"/modules/jmxremote_optional-repackaged.jar");//NOI18N
                loader.addURL(f);

            } else { //GF V2

                f = new File(installRoot+"/lib/appserv-deployment-client.jar");//NOI18N
                loader.addURL(f);
                f = new File(installRoot+"/lib/jmxremote_optional.jar");//NOI18N
                loader.addURL(f);
                f = new File(installRoot+"/lib/appserv-ext.jar");//NOI18N
                loader.addURL(f);
            }

	} catch (Exception ex2) {
	    throw new Exception(ex2.getLocalizedMessage());
	}
    }
    
    synchronized ClassLoader getServerClassLoader(File platformLocation) {
	
	if(loader==null){        
	    try {
                loader = new ExtendedClassLoader(new Empty().getClass().getClassLoader());
                updatePluginLoader(platformLocation, loader);
	    } catch (Exception ex2) {
		org.openide.ErrorManager.getDefault().notify(ex2);
	    }
        }
	
	return loader;
    }

    private synchronized ClassLoader getJMXClassLoader(File platformLocation) {

	if(jmxLoader==null){
	    try {
                jmxLoader = new ExtendedClassLoader(new Empty().getClass().getClassLoader());
                updateJMXPluginLoader(platformLocation, jmxLoader);
	    } catch (Exception ex2) {
		org.openide.ErrorManager.getDefault().notify(ex2);
	    }
        }

	return jmxLoader;
    }

    public boolean isDeployOnSaveSupported() {
        return true;
    }

    public boolean isServerRunning() {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        String gfVersion = getGlassFishVersion();

        try{

            int adminPort = 4848;
            try {
                adminPort = Integer.parseInt(getJMXConnectorPort(gfVersion));
            } catch (Exception e) {
            }
            ClassLoader ld = getJMXClassLoader(new File(psconfig.getServerHome()));
            Thread.currentThread().setContextClassLoader(ld);

            Class clazz = ld.loadClass(getJMXHandlerClassName(gfVersion));
            Constructor con = clazz.getConstructor(String.class,String.class,String.class,int.class);
            Object instance = con.newInstance(psconfig.getHost(), psconfig.getAdminUser(), psconfig.getProperty(SunAppServerConstants.SERVER_PASSWORD), adminPort);
            //GlassFishJMXHandler jmxHandler = new GlassFishJMXHandler(psconfig.getHost(), psconfig.getAdminUser(), psconfig.getAdminPassWord(), adminPort);

            Method method = clazz.getMethod("getConfigDirectory");
            Object object = method.invoke(instance);
            if(object == null)
                return true; //damage control..incase MBean doesn't exist

            if(!(object instanceof String))
                return true;

            String domainDirPath = new File((String)object).getParentFile().getCanonicalPath();
            //System.out.println("ConfigDir:::: " +domainDirPath);
            String domainPath = new File(psconfig.getDomainDir()).getCanonicalPath();
            if(domainDirPath.equals(domainPath))
                return true;
            else
                return false;

        }catch(Throwable ex){
            ex.printStackTrace();
            return true;
        }finally {
            Thread.currentThread().setContextClassLoader(contextClassLoader);
        }

    }

    public File getModuleDirectory(TargetModuleID module) {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        String gfVersion = getGlassFishVersion();
        try{

            int adminPort = 4848;
            try {
                adminPort = Integer.parseInt(getJMXConnectorPort(gfVersion));
            } catch (Exception e) {
            }
            ClassLoader ld = getJMXClassLoader(new File(psconfig.getServerHome()));
            Thread.currentThread().setContextClassLoader(ld);

            Class clazz = ld.loadClass(getJMXHandlerClassName(gfVersion));
            Constructor con = clazz.getConstructor(String.class,String.class,String.class,int.class);
            Object instance = con.newInstance(psconfig.getHost(), psconfig.getAdminUser(), psconfig.getProperty(SunAppServerConstants.SERVER_PASSWORD), adminPort);
            //GlassFishJMXHandler jmxHandler = new GlassFishJMXHandler(psconfig.getHost(), psconfig.getAdminUser(), psconfig.getAdminPassWord(), adminPort);

            Method method = clazz.getMethod("getModuleDirectory", TargetModuleID.class);
            return (File)method.invoke(instance, module);

        }catch(Throwable ex){
            //ex.printStackTrace();
        }finally {
            Thread.currentThread().setContextClassLoader(contextClassLoader);
        }

        return null;
    }


    
    static class Empty{
        
    }

    public boolean deploy(String dir, String contextName) throws Exception {
        try{
           //Directory deployment is not yet provided by deployment spi. So 
           //using asadmin to do the directory deployment.
           deployOnGlassFish(dir,contextName);
           //_deploy(dir,contextName);
        }catch(Exception e){
            logger.log(Level.SEVERE, "Error",e);
            throw e;
        }
        return true;
    }
}
