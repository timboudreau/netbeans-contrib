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

package org.netbeans.modules.portalpack.servers.jnpc.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.tools.ant.module.api.support.ActionUtils;
import org.netbeans.modules.portalpack.servers.core.api.PSDeploymentManager;
import org.netbeans.modules.portalpack.servers.core.common.ExtendedClassLoader;
import org.netbeans.modules.portalpack.servers.core.common.LogManager;
import org.netbeans.modules.portalpack.servers.core.impl.DefaultPSTaskHandler;
import org.netbeans.modules.portalpack.servers.core.impl.j2eeservers.sunappserver.SunAppServerConstants;
import org.netbeans.modules.portalpack.servers.core.impl.j2eeservers.tomcat.TomcatConstant;
import org.netbeans.modules.portalpack.servers.core.util.Command;
import org.netbeans.modules.portalpack.servers.core.util.NetbeanConstants;
import org.netbeans.modules.portalpack.servers.core.util.PSConfigObject;
import org.netbeans.modules.portalpack.servers.jnpc.ServerConstants;
import org.netbeans.modules.portalpack.servers.jnpc.ServerDeployHandler;
import org.netbeans.modules.portalpack.servers.jnpc.ServerDeployerHandlerFactory;
import org.netbeans.modules.portalpack.servers.jnpc.common.JNPCConstants;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 *
 * @author root
 */
public class JNPCTaskHandler extends DefaultPSTaskHandler{
    
    private static Logger logger = Logger.getLogger(NetbeanConstants.PORTAL_LOGGER);
    private static ExtendedClassLoader loader;
    private PSDeploymentManager dm;
    private PSConfigObject psconfig;
    //private FileObject taskFile;
    
    private ServerDeployHandler deployerHandler;
    
    
    /** Creates a new instance of JNPCTaskHandler */
    public JNPCTaskHandler(PSDeploymentManager dm) {
        this.dm = dm;
        this.psconfig = dm.getPSConfig();
        
        if(loader == null)
        {
            try {
                loader = initClassLoader();
            } catch (MalformedURLException ex) {
                logger.log(Level.SEVERE,"Error creating classLoader for portletcontainer",ex);
            }
        }
        deployerHandler = ServerDeployerHandlerFactory.getServerDeployerHandler(dm);
    }
    
           
    public String deploy(String warfile, String serveruri) throws Exception {
        
        try{
            Thread.currentThread().setContextClassLoader(loader);
            Class clazz = loader.loadClass("com.sun.portal.portletadmin.mbeans.PortletAdmin");
            Object ob = clazz.newInstance();
            
            System.setProperty("com.sun.portal.portletcontainer.dir",psconfig.getPSHome());
            Method method = clazz.getMethod("deploy",new Class[]{String.class,Properties.class,Properties.class});

            Boolean isDeployed = (Boolean)method.invoke(ob, new Object[]{warfile,new Properties(),new Properties()});
            if(isDeployed != null)
            {
                if(!isDeployed.booleanValue())
                    throw new Exception(org.openide.util.NbBundle.getMessage(JNPCTaskHandler.class, "Deployment_failed"));
                
            }else{
                    logger.log(Level.INFO,org.openide.util.NbBundle.getMessage(JNPCTaskHandler.class, "Problem_preparing_war"));
            }
            
        }catch(Exception e){
            throw e;
        }
                
        File warFileObj = new File(warfile);
        String massagedWar = psconfig.getPSHome() + File.separator + "war" + File.separator + warFileObj.getName();
        deployerHandler.deploy(massagedWar);
       /* if(psconfig.getServerType().equals(ServerConstants.SUN_APP_SERVER_9))
        {
            SunAppServerDeployerUtil handler = new SunAppServerDeployerUtil(dm);
            handler.deployOnGlassFish(psconfig.getPSHome() + File.separator + "war" + File.separator + warFileObj.getName());
        }
        else{
            TomcatDeployerUtil handler = new TomcatDeployerUtil(dm,taskFile);
            handler.deployOnTomcat(psconfig.getPSHome() + File.separator + "war" + File.separator + warFileObj.getName());
        }*/
        return org.openide.util.NbBundle.getMessage(JNPCTaskHandler.class, "Deployed_Successfully");
    }
    
     private int runProcess(String str, boolean wait) throws Exception {
        final Process child = Runtime.getRuntime().exec(str);
        
        
        LogManager manager = new LogManager(dm);
        manager.openServerLog(child,str + System.currentTimeMillis());
        if (wait)
            child.waitFor();
        
        return child.exitValue();
        
    }
      
    private int runProcess(String[] cmdArray, boolean wait) throws Exception {
        final Process child = Runtime.getRuntime().exec(cmdArray);
        
        
        LogManager manager = new LogManager(dm);
        manager.openServerLog(child,"" + System.currentTimeMillis());
        if (wait)
            child.waitFor();
        
        return child.exitValue();
        
    }
       

    public void undeploy(String portletAppName, String dn) throws Exception {
        
        try{
            Thread.currentThread().setContextClassLoader(loader);
            Class clazz = loader.loadClass("com.sun.portal.portletadmin.mbeans.PortletAdmin");
            Object ob = clazz.newInstance();
            
            System.setProperty("com.sun.portal.portletcontainer.dir",psconfig.getPSHome());
            Method method = clazz.getMethod("undeploy",new Class[]{String.class});

            Boolean isUnDeployed = (Boolean)method.invoke(ob, new Object[]{portletAppName});
            if(isUnDeployed != null)
            {
                if(!isUnDeployed.booleanValue())
                {
                    throw new Exception(org.openide.util.NbBundle.getMessage(JNPCTaskHandler.class, "UNDEPLOYMENT_FAILED"));
                }
                    
            }
            else
            {
                logger.log(Level.INFO,org.openide.util.NbBundle.getMessage(JNPCTaskHandler.class, "PROBLEM_IN_UNREGISTER"));
            }
            
        }catch(Exception e){
            throw e;
        }
        
        deployerHandler.undeploy(portletAppName);
        /*if(psconfig.getServerType().equals(ServerConstants.SUN_APP_SERVER_9)){
            SunAppServerDeployerUtil handler = new SunAppServerDeployerUtil(dm);
            handler.unDeployFromGlassFish(portletAppName);
        }
        else{
            TomcatDeployerUtil handler = new TomcatDeployerUtil(dm,taskFile);
            handler.undeployOnTomcat(portletAppName);
        }*/
        
    }
    
    
    

    public String[] getPortlets(String dn)  {
        
        try{
            Thread.currentThread().setContextClassLoader(loader);
            Class clazz = loader.loadClass("com.sun.portal.portletadmin.PortletRegistryContextFactory");
            Method method = clazz.getMethod("getPortletRegistryContext",new Class[]{});
            Object portletRegistryContextObj = method.invoke(null,new Object[]{});
            
            Class registryContextClazz = loader.loadClass("com.sun.portal.portletadmin.PortletRegistryContext");
            Method getPortletsMethod = registryContextClazz.getMethod("getAvailablePortlets",new Class[]{});

            List list = (List) getPortletsMethod.invoke(portletRegistryContextObj,new Object[]{});

            if(list == null)
                return new String[]{};
            else
                return (String [])list.toArray(new String[0]);
        }catch(Exception e){
            logger.log(Level.SEVERE,"Error getting portlet lists ",e);
            return new String[]{};
        }
        
    }
    
   public ExtendedClassLoader initClassLoader() throws MalformedURLException
   {
       System.setProperty("com.sun.portal.portletcontainer.dir",psconfig.getPSHome());
        ExtendedClassLoader loader = new ExtendedClassLoader(this.getClass().getClassLoader());
        
        File libDir = new File(psconfig.getPSHome() + File.separator + "lib");
        File[] files = libDir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                if(name.endsWith(".jar"))
                {
                    return true;
                }
                return false;
            }
        });
        
        for(int i=0;i<files.length;i++)

        {
            logger.info(files[i].getName());
            loader.addURL(files[i]);
        }
        return loader;
   }
   

    public String constructPortletViewURL(String dn, String portlet) {
        
        String  contextUri = psconfig.getPortalUri();
        if(contextUri.startsWith("/"))
        {
           if(contextUri.length() > 1)
               contextUri = contextUri.substring(1);
        }
        
        if(contextUri.endsWith("/"))
        {
            if(contextUri.length() > 1)
               contextUri = contextUri.substring(0,contextUri.length() - 1);
        }
        
        return "http://"+psconfig.getHost()+":"+psconfig.getPort()+"/"+contextUri +"?pc.portletId=" + portlet;
    }
   
    public String constructAdminToolURL(){
        
        String  contextUri = psconfig.getProperty(JNPCConstants.ADMIN_CONSOLE_URI);
        if(contextUri.startsWith("/"))
        {
           if(contextUri.length() > 1)
               contextUri = contextUri.substring(1);
        }
        return "http://"+psconfig.getHost() + ":"+psconfig.getPort()+"/"+contextUri;
    }   

    
   
}
