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
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */
package org.netbeans.modules.portalpack.servers.jnpc.pc20;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.zip.ZipException;
import org.netbeans.modules.portalpack.servers.core.api.PSDeploymentManager;
import org.netbeans.modules.portalpack.servers.core.common.DeploymentException;
import org.netbeans.modules.portalpack.servers.jnpc.common.JNPCConstants;
import org.netbeans.modules.portalpack.servers.jnpc.impl.JNPCTaskHandler;
import org.openide.util.Exceptions;

/**
 *
 * @author Satyaranjan
 */
public class PC20TaskHandler extends JNPCTaskHandler {

    private static String PORTLET_ADMIN_INTERFACE = "com.sun.portal.portletcontainer.admin.mbeans.PortletAdmin";
    private static String PORTLET_REGISTRY_CONTEXT_FACTORY = "com.sun.portal.portletcontainer.context.registry.PortletRegistryContextFactory";
    private static String PORTLET_REGISTRY_CONTEXT_ABSTRACT_FACTORY = "com.sun.portal.portletcontainer.context.registry.PortletRegistryContextAbstractFactory";
    private static String PORTLET_REGISTRY_CONTEXT = "com.sun.portal.portletcontainer.context.registry.PortletRegistryContext";
    private static String PORTLET_REGISTRY_CACHE = "com.sun.portal.portletcontainer.admin.PortletRegistryCache";

    public PC20TaskHandler(PSDeploymentManager dm) {
        super(dm);
    }

    //Directory deployment can be done through url deployment
    @Override
    public String deploy(String deployedDir, String warfile, String serveruri) throws Exception {

        File warFileObj = new File(warfile);
        if(checkIfURLDeploymentSupported()) {

            doUrlDeployment(warFileObj.getName(), deployedDir);
        } else{
            super.deploy(deployedDir, warfile, serveruri);
            return org.openide.util.NbBundle.getMessage(JNPCTaskHandler.class, "Deployed_Successfully");
        }

        String appName = warFileObj.getName().substring(0,warFileObj.getName().indexOf("."));

        try{
            deployerHandler.deploy(deployedDir,appName);
        }catch(Exception e){
            try{
               // String fileName = warFileObj.getName();

                _undeployFromPC(appName,false);
            }catch(Exception ex){
               // ex.printStackTrace();
            }
            //writeErrorToOutput(uri,e);
            throw e;
        }
        return org.openide.util.NbBundle.getMessage(JNPCTaskHandler.class, "Deployed_Successfully");

    }

    @Override
    protected void _deployOnPC(final String warfile) throws Exception {

        if(checkIfURLDeploymentSupported()) {
            File warFileObj = new File(warfile);

            doUrlDeployment(warFileObj.getName(), warFileObj.getAbsolutePath());
            return;
        }

        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(loader);
            updateCache();
            Class clazz = loader.loadClass(PORTLET_ADMIN_INTERFACE);
            Object ob = clazz.newInstance();
            //System.setProperty("com.sun.portal.portletcontainer.dir",psconfig.getPSHome());
            Method method = null;
            Boolean isDeployed = Boolean.FALSE;
            try {
                method = clazz.getMethod("deploy", new Class[]{String.class, Properties.class, Properties.class, boolean.class});
                isDeployed = (Boolean) method.invoke(ob, new Object[]{warfile,new Properties(),new Properties(),Boolean.FALSE});
            } catch (NoSuchMethodException e) {
                logger.log(Level.SEVERE, "No deploy method is found : ", e);
            } catch(InvocationTargetException ite) {
                
                Exception de = new Exception(ite.getMessage()
                                            + "\n" 
                                            + "Please do a \"clean and build\" and try again !!!",ite);
                
                writeErrorToOutput(uri,ite);
                writeToOutput(uri, "Please do a \"clean and build\" and try again !!!");
                throw de;
                
            }  catch (Exception ex) {
                throw ex;
            }

            if (isDeployed != null) {
                if (!isDeployed.booleanValue()) {
                    throw new Exception(org.openide.util.NbBundle.getMessage(PC20TaskHandler.class, "Deployment_failed"));
                }
            } else {
                logger.log(Level.INFO, "Problem Preparing war file");
            }
        } catch (Exception e) {
            writeErrorToOutput(uri, e);
            throw e;
        } finally {

            Thread.currentThread().setContextClassLoader(contextClassLoader);
        }
    }

    @Override
    protected void _undeployFromPC(final String portletAppName, boolean logError) throws Exception {

        if(checkIfURLDeploymentSupported()) {
            doUrlUnDeployment(portletAppName);
            return;
        }

        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(loader);
            updateCache();
            Class clazz = loader.loadClass(PORTLET_ADMIN_INTERFACE);
            Object ob = clazz.newInstance();

            Method method = null;
            Boolean isUnDeployed = Boolean.FALSE;
            try {
                method = clazz.getMethod("undeploy", new Class[]{String.class, boolean.class});
                isUnDeployed = (Boolean) method.invoke(ob, new Object[]{portletAppName,Boolean.FALSE});
            } catch (NoSuchMethodException e) {
                logger.log(Level.SEVERE, "No undeploy method is found : ", e);
            } catch (Exception e) {
                throw e;
            }

            if (isUnDeployed != null) {
                if (!isUnDeployed.booleanValue()) {
                    throw new Exception(org.openide.util.NbBundle.getMessage(PC20TaskHandler.class, "UNDEPLOYMENT_FAILED"));
                }
            } else {
                logger.log(Level.INFO, "Problem unregistering application from the portlet container");
            }
        } catch (Exception e) {
            if (logError) {
                writeErrorToOutput(uri, e);
            }
            throw e;
        } finally {

            Thread.currentThread().setContextClassLoader(contextClassLoader);
        }
    }


    @Override
    public String[] getPortlets(String dn) {

         if(checkIfURLDeploymentSupported()) {
            String contextUri = getContextUri();
            if(contextUri == null)
                return new String[0];

            URL url = null;
            try {
                url = new URL("http://" + psconfig.getHost() + ":" + psconfig.getPort() + "/" + contextUri + "/list");
            } catch (MalformedURLException ex) {
                logger.log(Level.SEVERE, ex.getMessage(), ex);
            }
            String content = getContentFromHttpURL(url);
            if(content == null)
                return new String[0];

            String[] portlets = content.split(",");
            List portletList = new ArrayList();
            for(String portlet:portlets) {
                if(portlet != null && portlet.trim().length() != 0)
                    portletList.add(portlet);
            }

            return (String[]) portletList.toArray(new String[0]);
        }

        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(loader);
            Class clazz = null;
            Object factoryObj = null;
            Object portletRegistryContextObj = null;
            updateCache();

            try {
                Class absFactoryClazz = loader.loadClass(PORTLET_REGISTRY_CONTEXT_ABSTRACT_FACTORY);
                clazz = loader.loadClass(PORTLET_REGISTRY_CONTEXT_FACTORY);
                Object absFactoryObj = absFactoryClazz.newInstance();
                Method getPRCF = absFactoryClazz.getMethod("getPortletRegistryContextFactory", new Class[]{});
                factoryObj = getPRCF.invoke(absFactoryObj, new Object[]{});
            } catch (ClassNotFoundException ex) {
                logger.log(Level.SEVERE, "Class Not Found Exception: ", ex);
                return new String[0];
            }

            Method method = clazz.getMethod("getPortletRegistryContext", new Class[]{});
            portletRegistryContextObj = method.invoke(factoryObj, new Object[]{});
            Class registryContextClazz = null;
            try {
                registryContextClazz = loader.loadClass(PORTLET_REGISTRY_CONTEXT);
            } catch (ClassNotFoundException ex) {
                logger.log(Level.SEVERE, "REGISTRY CONTEXT Class Not Found : ", ex);
            }
            Method getPortletsMethod = registryContextClazz.getMethod("getAvailablePortlets", new Class[]{});

            List list = (List) getPortletsMethod.invoke(portletRegistryContextObj,new Object[]{});

            if (list == null) {
                return new String[]{};
            } else {
                return (String[]) list.toArray(new String[0]);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error getting portlet lists ", e);
            writeErrorToOutput(uri, e);
            return new String[]{};
        } finally {
            updateCache();
            Thread.currentThread().setContextClassLoader(contextClassLoader);
        }
    }

    @Override
    protected void updateCache() {
        try {
            Class cacheClazz = loader.loadClass(PORTLET_REGISTRY_CACHE);
            Method m = cacheClazz.getMethod("init", new Class[]{});
            m.invoke(null, null);
        } catch (Exception e) {
            logger.log(Level.INFO,"Error Updating Cache" + e.getMessage());
            //ignore exception incase of class not found.
        }
    }


    private boolean doUrlDeployment(String warFileName,String warPath) throws Exception {
        String  contextUri = getContextUri();

        if(contextUri == null)
            return false;

        warFileName = URLEncoder.encode(warFileName, "UTF-8");
        warPath = URLEncoder.encode(warPath, "UTF-8");
        
        URL url;
        HttpURLConnection urlConnection = null;
        try {
            url = new URL("http://" + psconfig.getHost() + ":" + psconfig.getPort() + "/" + contextUri + "/deploy?dt.war="+warFileName+"&dt.path="+warPath);
            int responseCode;
            urlConnection =  (HttpURLConnection) url.openConnection();
            responseCode = urlConnection.getResponseCode();

            if(responseCode == 500) {
                String errorMsg = urlConnection.getResponseMessage();
                throw new DeploymentException(errorMsg);
            }

        } catch (Exception ex) {
            writeErrorToOutput(uri,ex);
            writeToOutput(uri, ex.getMessage());
            throw ex;
        } finally {
            try{
                urlConnection.disconnect();
            }catch(Exception e){}
        }

        return true;

    }

    private boolean doUrlUnDeployment(String appName) throws Exception {
        String  contextUri = getContextUri();

        if(contextUri == null)
            return false;

        appName = URLEncoder.encode(appName, "UTF-8");
        
        URL url;
        HttpURLConnection urlConnection = null;
        try {
            url = new URL("http://" + psconfig.getHost() + ":" + psconfig.getPort() + "/" + contextUri + "/undeploy?dt.war="+appName);
            int responseCode;
            urlConnection =  (HttpURLConnection) url.openConnection();
            responseCode = urlConnection.getResponseCode();

            if(responseCode == 500) {
                String errorMsg = urlConnection.getResponseMessage();
                throw new DeploymentException(errorMsg);
            }
           
        } catch (Exception ex) {
            writeErrorToOutput(uri,ex);
            writeToOutput(uri, ex.getMessage());
            throw ex;
        } finally {
            try{
                urlConnection.disconnect();
            }catch(Exception e){}
        }

        return true;

    }


    private String getContextUri() {
        String  contextUri = psconfig.getPortalUri();

        if(contextUri == null)
            return null;

        String[] segments = contextUri.split("/");

        for(String segment:segments) {
            if(segment != null && segment.trim().length() != 0) {
                contextUri = segment;
                break;
            }
        }

        return contextUri;
    }
    /*
     * This will check if the pc version is 2.1.1 or later
     */
    private boolean checkIfURLDeploymentSupported() {

        String  contextUri = getContextUri();

        if(contextUri == null)
            return false;

        URL url;
        try {
            url = new URL("http://" + psconfig.getHost() + ":" + psconfig.getPort() + "/" + contextUri + "/deploy");
            int responseCode;
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            responseCode = urlConnection.getResponseCode();
            if(responseCode == 404 || responseCode == 503) //Not Found
            {
              logger.info("404 - Not Found Exception for deploy url .....");
              return false;
            }
            try{
                urlConnection.disconnect();
            }catch(Exception e) {}
        } catch (IOException ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
            return false;
        }

        return true;
     }

    private  String getContentFromHttpURL(URL url) {
        BufferedReader br = null;
        try {
            // TODO code application logic here
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setDoOutput(true);
            InputStream ins = con.getInputStream();

            br = new BufferedReader(new InputStreamReader(ins));
            String content = "";
            String line = br.readLine();
            while (line != null) {
                content += line;
                line = br.readLine();
            }

            return content;
        } catch (Exception e) {
            //logger.log(Level.SEVERE, "Error", e);
            logger.log(Level.SEVERE, e.getMessage(), e);
            return null;
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException ex) {
                }
            }
        }
    }

    private  String getErrorStream(HttpURLConnection urlCon) {
        BufferedReader br = null;
        try {

            InputStream ins = urlCon.getErrorStream();
            br = new BufferedReader(new InputStreamReader(ins));
            String content = "";
            String line = br.readLine();
            while (line != null) {
                content += line;
                line = br.readLine();
            }

            return content;
        } catch (Exception e) {
            //logger.log(Level.SEVERE, "Error", e);
            logger.log(Level.SEVERE, e.getMessage(), e);
            return null;
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException ex) {
                }
            }
        }

    }

}
