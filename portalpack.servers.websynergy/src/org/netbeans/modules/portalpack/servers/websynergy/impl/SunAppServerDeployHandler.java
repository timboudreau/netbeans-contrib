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

package org.netbeans.modules.portalpack.servers.websynergy.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.modules.j2ee.deployment.plugins.api.UISupport;
import org.netbeans.modules.portalpack.servers.core.JSR88DeploymentHandler;
import org.netbeans.modules.portalpack.servers.core.api.PSDeploymentManager;
import org.netbeans.modules.portalpack.servers.core.common.DeploymentException;
import org.netbeans.modules.portalpack.servers.core.common.ExtendedClassLoader;
import org.netbeans.modules.portalpack.servers.core.common.LogManager;
import org.netbeans.modules.portalpack.servers.core.common.ProcessLogManager;
import org.netbeans.modules.portalpack.servers.core.impl.j2eeservers.sunappserver.SunAppServerConstants;
import org.netbeans.modules.portalpack.servers.core.util.Command;
import org.netbeans.modules.portalpack.servers.core.util.NetbeanConstants;
import org.netbeans.modules.portalpack.servers.core.util.PSConfigObject;
import org.netbeans.modules.portalpack.servers.websynergy.ServerDeployHandler;
import org.netbeans.modules.portalpack.servers.websynergy.common.SUNASProcessLogSupport;
import org.openide.filesystems.FileObject;
import org.openide.util.NbBundle;

/**
 *
 * @author root
 */
public class SunAppServerDeployHandler implements ServerDeployHandler{
    private static Logger logger = Logger.getLogger(NetbeanConstants.PORTAL_LOGGER);
    private PSConfigObject psconfig;
    private PSDeploymentManager dm;
    private String uri;
    private ExtendedClassLoader loader;
    /**
     * Creates a new instance of SunAppServerDeployHandler
     */
    public SunAppServerDeployHandler(PSDeploymentManager dm) {
        this.dm = dm;
        this.psconfig = dm.getPSConfig();
        this.uri = dm.getUri();
    }
    
     private void deployOnGlassFish(String warFile) throws Exception
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
        
        Command cmd = new Command();
        String ext = "";
         if (org.openide.util.Utilities.isWindows()){
            ext = ".bat";
        }
        cmd.add(psconfig.getServerHome() + File.separator + "bin" + File.separator + "asadmin" + ext);
        cmd.add("deploy");
        cmd.add("--precompilejsp=false");
        cmd.add("--port");
        cmd.add(psconfig.getAdminPort());
        cmd.add("-u");
        cmd.add(psconfig.getProperty(SunAppServerConstants.SERVER_USER));
        cmd.add("--passwordfile");
        cmd.add(file.getAbsolutePath());
        cmd.add(warFile);
                
        logger.info(cmd.toString());
        try {
            
            runProcess(cmd.getCmdArray(),true);
        } catch (Exception ex) {
            throw new Exception(NbBundle.getMessage(SunAppServerDeployHandler.class,"MSG_DEPLOY_ON_GLASSFISH_FAILED"));
        }finally{
            file.delete();
        }
        logger.info("Password file: "+file.getAbsolutePath());
        
    }
     
     
     private void unDeployFromGlassFish(String appName) throws Exception
    {
        String passwordFile = ".pcpwd.txt";
        File file = new File(passwordFile);
        if(file.exists())
            file.delete();
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
        cmd.add(passwordFile);
        cmd.add(appName);
                
        logger.info(cmd.toString());    
       
        try {
            
            runProcess(cmd.getCmdArray(),true);
        } catch (Exception ex) {
            throw new Exception(NbBundle.getMessage(SunAppServerDeployHandler.class,"MSG_UNDEPLOY_ON_GLASSFISH_FAILED"));
        }
        logger.info("Password file: "+passwordFile);
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
           deployOnGlassFish(warFile);
           //_deploy(warFile);
        }catch(Exception e){
            logger.log(Level.SEVERE, "Error",e);
            throw e;
        }
        return true;
    }

    public boolean undeploy(String appName) throws Exception {
        try{
           unDeployFromGlassFish(appName);
            //_undeploy(appName);
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
        Properties props = new Properties();
        props.put("jsr88.dm.id","deployer:Sun:AppServer::" + psconfig.getHost() + ":" + psconfig.getAdminPort());
        props.put("jsr88.dm.user",psconfig.getAdminUser());
        props.put("jsr88.dm.passwd",psconfig.getAdminPassWord());
        props.put("jsr88.df.classname","com.sun.enterprise.deployapi.SunDeploymentFactory");
        
        ClassLoader ld = getServerClassLoader(new File(psconfig.getServerHome()));
         
        JSR88DeploymentHandler deploymentHandler = new JSR88DeploymentHandler(ld,props,UISupport.getServerIO(uri));
        String warFileName = new File(warFile).getName();
        String contextName = warFileName.substring(0,warFileName.length()-4);
        deploymentHandler.runApp(warFile,contextName);
        deploymentHandler.releaseDeploymentManager();
    }
    
    public void _undeploy(String appName) throws DeploymentException
    {
        Properties props = new Properties();
        props.put("jsr88.dm.id","deployer:Sun:AppServer::" + psconfig.getHost() + ":" + psconfig.getAdminPort());
        props.put("jsr88.dm.user",psconfig.getAdminUser());
        props.put("jsr88.dm.passwd",psconfig.getAdminPassWord());
        props.put("jsr88.df.classname","com.sun.enterprise.deployapi.SunDeploymentFactory");
        
        ClassLoader ld = getServerClassLoader(new File(psconfig.getServerHome()));
         
        JSR88DeploymentHandler deploymentHandler = new JSR88DeploymentHandler(ld,props,UISupport.getServerIO(uri));
        
        deploymentHandler.undeployApp(appName);
        deploymentHandler.releaseDeploymentManager();
    }
    
    private static void updatePluginLoader(File platformLocation, ExtendedClassLoader loader) throws Exception{
        try {
            java.io.File f = platformLocation;
            if (null == f || !f.exists()){
                return;
            }
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
	    
	} catch (Exception ex2) {
	    throw new Exception(ex2.getLocalizedMessage());
	}
    }
    
    private synchronized ClassLoader getServerClassLoader(File platformLocation) {
	
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
    
    static class Empty{
        
    }
}
