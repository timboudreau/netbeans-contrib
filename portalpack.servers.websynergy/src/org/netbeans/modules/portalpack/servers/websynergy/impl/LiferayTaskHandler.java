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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.netbeans.modules.j2ee.deployment.plugins.api.UISupport;
import org.netbeans.modules.portalpack.servers.core.api.PSDeploymentManager;
import org.netbeans.modules.portalpack.servers.core.common.DeploymentException;
import org.netbeans.modules.portalpack.servers.core.common.ExtendedClassLoader;
import org.netbeans.modules.portalpack.servers.core.common.FileLogViewerSupport;
import org.netbeans.modules.portalpack.servers.core.common.LogManager;
import org.netbeans.modules.portalpack.servers.core.common.ServerConstants;
import org.netbeans.modules.portalpack.servers.core.impl.DefaultPSTaskHandler;
import org.netbeans.modules.portalpack.servers.core.impl.j2eeservers.tomcat.TomcatConstant;
import org.netbeans.modules.portalpack.servers.core.util.NetbeanConstants;
import org.netbeans.modules.portalpack.servers.core.util.PSConfigObject;
import org.netbeans.modules.portalpack.servers.websynergy.ServerDeployHandler;
import org.netbeans.modules.portalpack.servers.websynergy.ServerDeployerHandlerFactory;
import org.netbeans.modules.portalpack.servers.websynergy.common.LiferayConstants;
//import org.netbeans.modules.portalpack.servers.liferay.webservices.client.PortletService;
import org.openide.util.Exceptions;
//import org.netbeans.modules.portalpack.servers.liferay.webservices.client.PortletServiceService;
//import org.netbeans.modules.portalpack.servers.liferay.webservices.client.PortletServiceService;

/**
 *
 * @author Satya
 */
public class LiferayTaskHandler extends DefaultPSTaskHandler {

    protected static Logger logger = Logger.getLogger(NetbeanConstants.PORTAL_LOGGER);
    protected ExtendedClassLoader loader;
    protected PSDeploymentManager dm;
    protected PSConfigObject psconfig;
    protected ServerDeployHandler deployerHandler;
    protected String uri;

    /** Creates a new instance of LifeRayTaskHandler */
    public LiferayTaskHandler(PSDeploymentManager dm) {
        this.dm = dm;
        this.psconfig = dm.getPSConfig();
        this.uri = dm.getUri();

        if (loader == null) {
            try {
                loader = initClassLoader();
            } catch (MalformedURLException ex) {
                logger.log(Level.SEVERE, "Error creating classLoader for portletcontainer", ex);
            }
        }
        deployerHandler = ServerDeployerHandlerFactory.getServerDeployerHandler(dm);
    }

    public String deploy(String warfile, String serveruri) throws Exception {

        /*_deployOnPC(warfile);
        
        File warFileObj = new File(warfile);
        String massagedWar = psconfig.getPSHome() + File.separator + "war" + File.separator + warFileObj.getName();
        
        try {
        deployerHandler.deploy(massagedWar);
        } catch (Exception e) {
        try {
        String fileName = warFileObj.getName();
        String appName = fileName.substring(0, fileName.indexOf("."));
        _undeployFromPC(appName, false);
        } catch (Exception ex) {
        // ex.printStackTrace();
        }
        //writeErrorToOutput(uri,e);
        throw e;
        }*/
        String deployDir = psconfig.getProperty(LiferayConstants.AUTO_DEPLOY_DIR);
        File deployDirFile = new File(deployDir);
        if (!deployDirFile.exists()) {
            deployDirFile.mkdirs();
        //copy(warfile, System.getProperty("user.home") + File.separator + "liferay" + File.separator + "deploy");
        }
        long baseTime = System.currentTimeMillis();
        copy(warfile, deployDir);
        showServerLog();
        return getDeploymentMessage(warfile, baseTime);
    }

    public String getDeploymentMessage(String warfile, long baseTime) throws Exception {
        File warFile = new File(warfile);
        String warName = warFile.getName();
        // if(!psconfig.getServerType().equals(ServerConstants.SUN_APP_SERVER_9))
        //     return org.openide.util.NbBundle.getMessage(LiferayTaskHandler.class, "Deployment_Done");

        String deployDir = psconfig.getProperty(LiferayConstants.AUTO_DEPLOY_DIR);

        File warInLRAutoDeployDir = new File(deployDir + File.separator + warName);
        int counter = 0;
        while (warInLRAutoDeployDir.exists()) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException ex) {
                logger.info(ex.getMessage());
            }
            counter++;
            if (counter >= 50) {
                return "Deployment done. Check server log for the status.";
            }
        }

        if (psconfig.getServerType().equals(ServerConstants.SUN_APP_SERVER_9)) {
            return _getGlassFishDeploymentMessage(warName, baseTime);
        }
        
        if(psconfig.getServerType().equals(ServerConstants.TOMCAT_5_X)) {
            return _getTomcatDeploymentMessage(warName, baseTime);
        }
        
        return org.openide.util.NbBundle.getMessage(LiferayTaskHandler.class, "Deployment_Done");
        
    }

    private String _getGlassFishDeploymentMessage(String warName,long baseTime)
        throws DeploymentException{
        
        File appServerAutoDeployDir = new File(psconfig.getDomainDir() + File.separator + "autodeploy");
        File warFileInAppServerAutoDeployDir = new File(appServerAutoDeployDir, warName + "_deployed");
        File deployFailFileInAppServerAutoDeployDir = new File(appServerAutoDeployDir, warName + "_deployFailed");

        int counter = 0;
        while (true) {
            if (warFileInAppServerAutoDeployDir.exists()) {
                if (warFileInAppServerAutoDeployDir.lastModified() >= baseTime) {
                    return warName + " deployed successfully. Check log for more message.";
                }
            }

            if (deployFailFileInAppServerAutoDeployDir.exists()) {
                if (deployFailFileInAppServerAutoDeployDir.lastModified() >= baseTime) {
                    throw new DeploymentException(warName + " deployment failed. For more into check log message. ");
                //return warName + " deployment failed. For more into check log message. ";
                }
            }
            try {

                Thread.sleep(300);
            } catch (InterruptedException ex) {
                logger.info(ex.getMessage());
            }
            counter++;
            if (counter >= 50) {
                return "Deployment done. Check server log for the status.";
            }
        }
    }
    
    private String _getTomcatDeploymentMessage(String warName,long baseTime)
        throws DeploymentException{
        
        String appDir = null;
        int index = warName.indexOf(".");
        if(index == -1)
        {
            appDir = warName;
        } else {
            appDir = warName.substring(0,index);
        }
         
        File tomcatDeployDir = new File(psconfig.getProperty(TomcatConstant.CATALINA_HOME)
                                            + File.separator + "webapps" + File.separator + appDir);

        int counter = 0;
        while (true) {
            if (tomcatDeployDir.exists()) {
                if (tomcatDeployDir.lastModified() >= baseTime) {
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException ex) {
                        //do nothing.
                    }
                    return warName + " deployed successfully. Check log for more message.";
                }
            }
            
           /* if (deployFailFileInAppServerAutoDeployDir.exists()) {
                if (deployFailFileInAppServerAutoDeployDir.lastModified() >= baseTime) {
                    throw new DeploymentException(warName + " deployment failed. For more into check log message. ");
                }
            }*/
            
            try {

                Thread.sleep(300);
            } catch (InterruptedException ex) {
                logger.info(ex.getMessage());
            }
            counter++;
            if (counter >= 50) {
                return "Deployment done. Check server log for the status.";
            }
        }
    }

    protected int runProcess(String str, boolean wait) throws Exception {
        final Process child = Runtime.getRuntime().exec(str);

        LogManager manager = new LogManager(dm);
        manager.openServerLog(child, str + System.currentTimeMillis());
        if (wait) {
            child.waitFor();
        }
        return child.exitValue();

    }

    protected int runProcess(String[] cmdArray, boolean wait) throws Exception {
        final Process child = Runtime.getRuntime().exec(cmdArray);


        LogManager manager = new LogManager(dm);
        manager.openServerLog(child, "" + System.currentTimeMillis());
        if (wait) {
            child.waitFor();
        }
        return child.exitValue();

    }

    public void undeploy(String portletAppName, String dn) throws Exception {

        deployerHandler.undeploy(portletAppName);
        showServerLog();
    /*if(psconfig.getServerType().equals(ServerConstants.SUN_APP_SERVER_9)){
    SunAppServerDeployerUtil handler = new SunAppServerDeployerUtil(dm);
    handler.unDeployFromGlassFish(portletAppName);
    }
    else{
    TomcatDeployerUtil handler = new TomcatDeployerUtil(dm,taskFile);
    handler.undeployOnTomcat(portletAppName);
    }*/

    }

    /*    protected void _undeployFromPC(final String portletAppName, boolean logError) throws Exception {
    
    ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
    try {
    
    Thread.currentThread().setContextClassLoader(loader);
    updateCache();
    Class clazz = loader.loadClass(PORTLET_ADMIN_INTERFACE);
    Object ob = clazz.newInstance();
    
    //System.setProperty("com.sun.portal.portletcontainer.dir",psconfig.getPSHome());
    
    Method method = null;
    Boolean isUnDeployed = Boolean.FALSE;
    try {
    method = clazz.getMethod("undeploy", new Class[]{String.class, boolean.class});
    isUnDeployed = (Boolean) method.invoke(ob, new Object[]{portletAppName, Boolean.FALSE});
    } catch (NoSuchMethodException e) {
    method = clazz.getMethod("undeploy", new Class[]{String.class});
    isUnDeployed = (Boolean) method.invoke(ob, new Object[]{portletAppName});
    } catch (Exception e) {
    throw e;
    }
    
    if (isUnDeployed != null) {
    if (!isUnDeployed.booleanValue()) {
    throw new Exception(org.openide.util.NbBundle.getMessage(LifeRayTaskHandler.class, "UNDEPLOYMENT_FAILED"));
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
     */
    public String[] getPortlets(String dn) {
        try {

            URL url = null;
            String urlStr = "http://" + psconfig.getHost() + ":" + psconfig.getPort() + "/c/" + psconfig.getPortalUri() + "/" + "json_service?serviceClassName=" + "com.liferay.portal.service.http.PortletServiceJSON" + "&serviceMethodName=getWARPortlets";
            try {
                url = new URL(urlStr);
            } catch (MalformedURLException e) {
                logger.warning("Failed to create URL for the websynergy json service ");
                logger.warning(e.getMessage());
                return new String[0];
            }

            String jsonString = getContentFromHttpURL(url);
            if (jsonString == null) {
                return new String[0];
            }
            JSONArray jsonArray = new JSONArray(jsonString);
            List<String> portlets = new ArrayList();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String portletName = jsonObject.getString("portlet_name");
                String appName = jsonObject.getString("servlet_context_name");
                portlets.add(appName + "." + portletName);
            }

            return (String[]) portlets.toArray(new String[0]);
        } catch (JSONException ex) {
            logger.log(Level.SEVERE, "Error", ex);
            return new String[0];
        }
    }

    protected ExtendedClassLoader initClassLoader() throws MalformedURLException {
        ExtendedClassLoader loader = new ExtendedClassLoader(getClass().getClassLoader());
        /*String jdkVersion = "1.5";
        try {
        loader.loadClass("java.awt.Desktop");
        jdkVersion = "1.6";
        } catch (ClassNotFoundException ex) {
        jdkVersion = "1.5";
        }
        
        File libDir = new File(psconfig.getServerHome() + File.separator + "lib");
        File[] files = null;
        
        if (jdkVersion.equals("1.5")) {
        files = new File[]{new File(libDir, "webservices-rt.jar"), new File(libDir, "activation.jar"),
        new File(libDir, "javaee.jar")
        };
        } else {
        files = new File[]{new File(libDir, "javaee.jar")};
        //}
        
        for (int i = 0; i < files.length; i++) {
        logger.info(files[i].getName());
        loader.addURL(files[i]);
        }*/
        return loader;
    }

    public String constructPortletViewURL(String dn, String portlet) {

        String contextUri = psconfig.getProperty(LiferayConstants.PORTLET_URI);
        if (contextUri.startsWith("/")) {
            if (contextUri.length() > 1) {
                contextUri = contextUri.substring(1);
            }
        }

        if (contextUri.endsWith("/")) {
            if (contextUri.length() > 1) {
                contextUri = contextUri.substring(0, contextUri.length() - 1);
            }
        }

        return "http://" + psconfig.getHost() + ":" + psconfig.getPort() + "/" + contextUri + "?pc.portletId=" + portlet;
    }

    public String constructAdminToolURL() {

        String contextUri = psconfig.getProperty(LiferayConstants.ADMIN_CONSOLE_URI);
        if (contextUri == null) {
            contextUri = "portal";
        }
        if (contextUri.startsWith("/")) {
            if (contextUri.length() > 1) {
                contextUri = contextUri.substring(1);
            }
        }
        return "http://" + psconfig.getHost() + ":" + psconfig.getPort() + "/" + contextUri;
    }

    public String getClientURL() {
        String contextUri = psconfig.getPortalUri();
        if (contextUri.startsWith("/")) {
            if (contextUri.length() > 1) {
                contextUri = contextUri.substring(1);
            }
        }
        return "http://" + psconfig.getHost() + ":" + psconfig.getPort() + "/" + contextUri;
    }

    public String getContentFromHttpURL(URL url) {
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
            logger.log(Level.SEVERE, "Error", e);
            return null;
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException ex) {
                    //Exceptions.printStackTrace(ex);
                }
            }
        }
    }

    protected void writeErrorToOutput(String uri, Exception e) {
        e.printStackTrace(UISupport.getServerIO(uri).getErr());
    }

    public static void copy(String fromFileName, String toFileName)
        throws IOException {
        File fromFile = new File(fromFileName);
        File toFile = new File(toFileName);

        if (!fromFile.exists()) {
            throw new IOException("FileCopy: " + "no such source file: " + fromFileName);
        }
        if (!fromFile.isFile()) {
            throw new IOException("FileCopy: " + "can't copy directory: " + fromFileName);
        }
        if (!fromFile.canRead()) {
            throw new IOException("FileCopy: " + "source file is unreadable: " + fromFileName);
        }
        if (toFile.isDirectory()) {
            toFile = new File(toFile, fromFile.getName());
        }
        FileInputStream from = null;
        FileOutputStream to = null;
        try {
            from = new FileInputStream(fromFile);
            to = new FileOutputStream(toFile);
            byte[] buffer = new byte[4096];
            int bytesRead;

            while ((bytesRead = from.read(buffer)) != -1) {
                to.write(buffer, 0, bytesRead); // write

            }
        } finally {
            if (from != null) {
                try {
                    from.close();
                } catch (IOException e) {
                }
            }
            if (to != null) {
                try {
                    to.close();
                } catch (IOException e) {
                    ;
                }
            }
        }
    }

    public void showServerLog() {
        try {
            File f = new File(psconfig.getDomainDir() + File.separator + "/logs/server.log");
            FileLogViewerSupport p = FileLogViewerSupport.getLogViewerSupport(f, dm.getUri(), 2000, true);
            p.showLogViewer(false);
        } catch (IOException ex) {
            //Exceptions.printStackTrace(ex);
        }
    }
}
