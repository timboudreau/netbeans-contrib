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
import java.io.FilenameFilter;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.modules.j2ee.deployment.plugins.api.UISupport;
import org.netbeans.modules.portalpack.servers.core.api.PSDeploymentManager;
import org.netbeans.modules.portalpack.servers.core.common.ExtendedClassLoader;
import org.netbeans.modules.portalpack.servers.core.common.LogManager;
import org.netbeans.modules.portalpack.servers.core.impl.DefaultPSTaskHandler;
import org.netbeans.modules.portalpack.servers.core.util.NetbeanConstants;
import org.netbeans.modules.portalpack.servers.core.util.PSConfigObject;
import org.netbeans.modules.portalpack.servers.jnpc.ServerDeployHandler;
import org.netbeans.modules.portalpack.servers.jnpc.ServerDeployerHandlerFactory;
import org.netbeans.modules.portalpack.servers.jnpc.common.JNPCConstants;

/**
 *
 * @author root
 */
public class JNPCTaskHandler extends DefaultPSTaskHandler{

    protected static Logger logger = Logger.getLogger(NetbeanConstants.PORTAL_LOGGER);
    protected ExtendedClassLoader loader;
    protected PSDeploymentManager dm;
    protected PSConfigObject psconfig;
    
    private static String PORTLET_ADMIN_INTERFACE = "com.sun.portal.portletadmin.mbeans.PortletAdmin";
    private static String PORTLET_REGISTRY_CONTEXT_FACTORY_OLD = "com.sun.portal.portletadmin.PortletRegistryContextFactory";
    private static String PORTLET_REGISTRY_CONTEXT_OLD = "com.sun.portal.portletadmin.PortletRegistryContext";
    private static String PORTLET_REGISTRY_CONTEXT_FACTORY_NEW = "com.sun.portal.portletcontainer.context.registry.PortletRegistryContextFactory";
    private static String PORTLET_REGISTRY_CONTEXT_NEW = "com.sun.portal.portletcontainer.context.registry.PortletRegistryContext";
    private static String PORTLET_REGISTRY_CACHE = "com.sun.portal.portletadmin.PortletRegistryCache";
    //private FileObject taskFile;

    protected ServerDeployHandler deployerHandler;
    protected String uri;


    /** Creates a new instance of JNPCTaskHandler */
    public JNPCTaskHandler(PSDeploymentManager dm) {
        this.dm = dm;
        this.psconfig = dm.getPSConfig();
        this.uri = dm.getUri();
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
        
        _deployOnPC(warfile);

        File warFileObj = new File(warfile);
        String massagedWar = psconfig.getPSHome() + File.separator + "war" + File.separator + warFileObj.getName();
        
        try{
            deployerHandler.deploy(massagedWar);
        }catch(Exception e){
            try{
                String fileName = warFileObj.getName();
                String appName = fileName.substring(0,fileName.indexOf("."));
                _undeployFromPC(appName,false);
            }catch(Exception ex){
               // ex.printStackTrace();
            }
            //writeErrorToOutput(uri,e);
            throw e;
        }
        return org.openide.util.NbBundle.getMessage(JNPCTaskHandler.class, "Deployed_Successfully");
    }

    protected void _deployOnPC(final String warfile) throws Exception {

        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        try{
            Thread.currentThread().setContextClassLoader(loader);
            updateCache();
            Class clazz = loader.loadClass(PORTLET_ADMIN_INTERFACE);
            Object ob = clazz.newInstance();
            //System.setProperty("com.sun.portal.portletcontainer.dir",psconfig.getPSHome());
            Method method = null;
            Boolean isDeployed = Boolean.FALSE;
            try{
                method = clazz.getMethod("deploy",new Class[]{String.class,Properties.class,Properties.class,boolean.class});
                isDeployed = (Boolean)method.invoke(ob, new Object[]{warfile,new Properties(),new Properties(),Boolean.FALSE});
            }catch(NoSuchMethodException e){
                method = clazz.getMethod("deploy",new Class[]{String.class,Properties.class,Properties.class,});
                isDeployed = (Boolean)method.invoke(ob, new Object[]{warfile,new Properties(),new Properties()});
            }catch(Exception ex){
                throw ex;
            }
            
            if(isDeployed != null)
            {
                if(!isDeployed.booleanValue())
                    throw new Exception(org.openide.util.NbBundle.getMessage(JNPCTaskHandler.class, "Deployment_failed"));

            }else{
                    logger.log(Level.INFO,"Problem Preparing war file");
            }

        }catch(Exception e){
            writeErrorToOutput(uri,e);
            throw e;
        }finally{
            
            Thread.currentThread().setContextClassLoader(contextClassLoader);
        }
    }

    public void undeploy(String portletAppName, String dn) throws Exception {
      
        _undeployFromPC(portletAppName,true);

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

    protected void _undeployFromPC(final String portletAppName,boolean logError) throws Exception {
        
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        try{
            Thread.currentThread().setContextClassLoader(loader);
            updateCache();
            Class clazz = loader.loadClass(PORTLET_ADMIN_INTERFACE);
            Object ob = clazz.newInstance();
           
            //System.setProperty("com.sun.portal.portletcontainer.dir",psconfig.getPSHome());
            
            Method method = null;
            Boolean isUnDeployed = Boolean.FALSE;
            try{
                method = clazz.getMethod("undeploy",new Class[]{String.class,boolean.class});
                isUnDeployed = (Boolean)method.invoke(ob, new Object[]{portletAppName,Boolean.FALSE});
            }catch(NoSuchMethodException e){
                method = clazz.getMethod("undeploy",new Class[]{String.class});
                isUnDeployed = (Boolean)method.invoke(ob, new Object[]{portletAppName});
            }catch(Exception e){
                throw e;
            }
            
            if(isUnDeployed != null)
            {
                if(!isUnDeployed.booleanValue())
                {
                    throw new Exception(org.openide.util.NbBundle.getMessage(JNPCTaskHandler.class, "UNDEPLOYMENT_FAILED"));
                }
            }
            else
            {
                logger.log(Level.INFO,"Problem unregistering application from the portlet container");
            }

        }catch(Exception e){
            if(logError)
                writeErrorToOutput(uri,e);
            throw e;
        }finally{
           
            Thread.currentThread().setContextClassLoader(contextClassLoader);
        }
    }


    public String[] getPortlets(String dn)  {

        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        try{
            Thread.currentThread().setContextClassLoader(loader);
            Class clazz = null;
            updateCache();
            try {
                clazz = loader.loadClass(PORTLET_REGISTRY_CONTEXT_FACTORY_NEW);
            } catch (ClassNotFoundException ex) {
                clazz = loader.loadClass(PORTLET_REGISTRY_CONTEXT_FACTORY_OLD);
            }
            Method method = clazz.getMethod("getPortletRegistryContext",new Class[]{});
            Object portletRegistryContextObj = method.invoke(null,new Object[]{});
            Class registryContextClazz = null;
            try {
                registryContextClazz = loader.loadClass(PORTLET_REGISTRY_CONTEXT_NEW);
            } catch (ClassNotFoundException ex) {
                registryContextClazz = loader.loadClass(PORTLET_REGISTRY_CONTEXT_OLD);
            }
            Method getPortletsMethod = registryContextClazz.getMethod("getAvailablePortlets",new Class[]{});

            List list = (List) getPortletsMethod.invoke(portletRegistryContextObj,new Object[]{});

            if(list == null)
                return new String[]{};
            else
                return (String [])list.toArray(new String[0]);
        }catch(Exception e){
            logger.log(Level.SEVERE,"Error getting portlet lists ",e);
            writeErrorToOutput(uri,e);
            return new String[]{};
        }finally{
            updateCache();
            Thread.currentThread().setContextClassLoader(contextClassLoader);
        }

    }
    
    
   protected void updateCache()
   {
       try{
             Class cacheClazz = loader.loadClass(PORTLET_REGISTRY_CACHE);
             Method m = cacheClazz.getMethod("init",new Class[]{});
             m.invoke(null,null);
        }catch(Exception e){
            logger.log(Level.WARNING,"Error Updating Cache.",e);
                //ignore exception incase of class not found.
        }
   }

   protected ExtendedClassLoader initClassLoader() throws MalformedURLException
   {
       //System.setProperty("com.sun.portal.portletcontainer.dir",psconfig.getPSHome());
        ExtendedClassLoader loader = new ExtendedClassLoader(getClass().getClassLoader());
        
        File configDir = new File(psconfig.getPSHome() + File.separator + "config");
        logger.info(configDir.getAbsolutePath());
        loader.addURL(configDir);
        
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

        String  contextUri = psconfig.getProperty(JNPCConstants.PORTLET_URI);
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

    public String getClientURL(){
        String  contextUri = psconfig.getPortalUri();
        if(contextUri.startsWith("/"))
        {
           if(contextUri.length() > 1)
               contextUri = contextUri.substring(1);
        }
        return "http://"+psconfig.getHost() + ":"+psconfig.getPort()+"/"+contextUri;
    }

    protected void writeErrorToOutput(String uri,Exception e) {
        e.printStackTrace(UISupport.getServerIO(uri).getErr());
    }
}
