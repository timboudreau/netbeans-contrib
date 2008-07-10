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
package org.netbeans.modules.portalpack.servers.liferay.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import org.netbeans.modules.j2ee.deployment.plugins.api.UISupport;
import org.netbeans.modules.portalpack.servers.core.api.PSDeploymentManager;
import org.netbeans.modules.portalpack.servers.core.common.DeploymentException;
import org.netbeans.modules.portalpack.servers.core.common.ExtendedClassLoader;
import org.netbeans.modules.portalpack.servers.core.common.FileLogViewerSupport;
import org.netbeans.modules.portalpack.servers.core.common.LogManager;
import org.netbeans.modules.portalpack.servers.core.common.ServerConstants;
import org.netbeans.modules.portalpack.servers.core.impl.DefaultPSTaskHandler;
import org.netbeans.modules.portalpack.servers.core.util.NetbeanConstants;
import org.netbeans.modules.portalpack.servers.core.util.PSConfigObject;
import org.netbeans.modules.portalpack.servers.liferay.ServerDeployHandler;
import org.netbeans.modules.portalpack.servers.liferay.ServerDeployerHandlerFactory;
import org.netbeans.modules.portalpack.servers.liferay.common.LiferayConstants;
//import org.netbeans.modules.portalpack.servers.liferay.webservices.client.PortletService;
//import org.netbeans.modules.portalpack.servers.liferay.webservices.client.PortletServiceService;
//import org.netbeans.modules.portalpack.servers.liferay.webservices.client.PortletServiceService;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.Repository;
//import org.openide.modules.InstalledFileLocator;
import org.openide.util.Exceptions;
import org.openide.windows.OutputWriter;

/**
 *
 * @author root
 */
public class LiferayTaskHandler extends DefaultPSTaskHandler {

    protected static Logger logger = Logger.getLogger(NetbeanConstants.PORTAL_LOGGER);
    protected ExtendedClassLoader loader;
    protected PSDeploymentManager dm;
    protected PSConfigObject psconfig;
    //private FileObject taskFile;
    protected ServerDeployHandler deployerHandler;
    protected String uri;
    //protected static File liferayIntegrationClientFile;
    protected static String LR_INTEGRATION_CONTEXT = "PortalPackLiferayIntegration/IntegrationServlet";
    protected static String LR_INTEGRATION_APP_NAME = "PortalPackLiferayIntegration";
    protected static String LR_INTEGRATION_WAR = LR_INTEGRATION_APP_NAME + ".war";
    protected static String LR_INTEGRATION_WAR_VERSION = "2.0";

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
        if(!deployDirFile.exists())
            deployDirFile.mkdirs();
        //copy(warfile, System.getProperty("user.home") + File.separator + "liferay" + File.separator + "deploy");
        long baseTime = System.currentTimeMillis();
        copy(warfile,deployDir);
        showServerLog();
        return getDeploymentMessage(warfile,baseTime);
        //return org.openide.util.NbBundle.getMessage(LiferayTaskHandler.class, "Deployment_Done");
    }
    
    
    public String getDeploymentMessage(String warfile,long baseTime) throws Exception
    {
        File warFile = new File(warfile);
        String warName = warFile.getName();
        if(!psconfig.getServerType().equals(ServerConstants.SUN_APP_SERVER_9))
            org.openide.util.NbBundle.getMessage(LiferayTaskHandler.class, "Deployment_Done");
        
        String deployDir = psconfig.getProperty(LiferayConstants.AUTO_DEPLOY_DIR);
        
        File warInLRAutoDeployDir = new File(deployDir + File.separator +warName);
        int counter = 0;
        while(warInLRAutoDeployDir.exists())
        {
            try {
                Thread.sleep(200);
            } catch (InterruptedException ex) {
                logger.info(ex.getMessage());
            }
            counter ++;
            if(counter >= 50)
                return "Deployment done. Check server log for the status.";
        }
        
        File appServerAutoDeployDir = new File(psconfig.getDomainDir() + File.separator + "autodeploy");
        File warFileInAppServerAutoDeployDir = new File(appServerAutoDeployDir,warName + "_deployed");
        File deployFailFileInAppServerAutoDeployDir = new File(appServerAutoDeployDir,warName + "_deployFailed");
        
        counter = 0;
        while(true)
        {
            if(warFileInAppServerAutoDeployDir.exists())
            {
                if(warFileInAppServerAutoDeployDir.lastModified() >= baseTime)
                {
                    return warName + " deployed successfully. Check log for more message.";
                }
            }
            
            if(deployFailFileInAppServerAutoDeployDir.exists())
            {
                if(deployFailFileInAppServerAutoDeployDir.lastModified() >= baseTime)
                {
                    throw new DeploymentException(warName + " deployment failed. For more into check log message. ");
                    //return warName + " deployment failed. For more into check log message. ";
                }
            }
            try {

                Thread.sleep(300);
            } catch (InterruptedException ex) {
                logger.info(ex.getMessage());
            }
            counter ++;
            if(counter >= 50)
                return "Deployment done. Check server log for the status.";
        }
        
    }

    /*protected void _deployOnPC(final String warfile) throws Exception {
    
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
    isDeployed = (Boolean) method.invoke(ob, new Object[]{warfile, new Properties(), new Properties(), Boolean.FALSE});
    } catch (NoSuchMethodException e) {
    method = clazz.getMethod("deploy", new Class[]{String.class, Properties.class, Properties.class,});
    isDeployed = (Boolean) method.invoke(ob, new Object[]{warfile, new Properties(), new Properties()});
    } catch (Exception ex) {
    throw ex;
    }
    
    if (isDeployed != null) {
    if (!isDeployed.booleanValue()) {
    throw new Exception(org.openide.util.NbBundle.getMessage(LifeRayTaskHandler.class, "Deployment_failed"));
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
    }*/
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

        ///_undeployFromPC(portletAppName, true);

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
        logger.info("Inside getPortlet---------------1");
        URL url = null;
        String urlStr = "http://localhost:" + psconfig.getPort() + "/" + LR_INTEGRATION_CONTEXT;
        try {
            URL baseUrl;
            //  baseUrl = org.netbeans.modules.portalpack.servers.liferay.webservices.client.PortletServiceService.class.getResource(".");
            //url = new URL(/*baseUrl, */"http://localhost:" + psconfig.getPort() + "/liferayintegration/PortletServiceService?wsdl");
            url = new URL(urlStr);

        } catch (MalformedURLException e) {
            logger.warning("Failed to create URL for the lr integration servlet Location: 'http://host:port/PortalPackIntegration/IntegrationServlet'");
            logger.warning(e.getMessage());
        }
        logger.info("Inside getPortlets method....");
        int i = 0;
        while (!exists(url) && i < 3) {
            File liwar = getWarFile();
            try {
                deployerHandler.deploy(liwar.getAbsolutePath());
            } catch (Exception ex) {
                logger.info(ex.getMessage());
            //Exceptions.printStackTrace(ex);
            }
            i++;
        }
        try {
            url = new URL(urlStr + "?action=getVersion");
            String content = getContentFromHttpURL(url);
            if (content == null || !content.equals(LR_INTEGRATION_WAR_VERSION)) {
                File liwar = getWarFile();
                deployerHandler.deploy(liwar.getAbsolutePath());
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error", e);
            return new String[0];
        }

        try {
            url = new URL(urlStr + "?action=getPortlets");
            String content = getContentFromHttpURL(url);
            if (content == null) {
                return new String[0];
            }
            if (content.indexOf("^") == -1) {
                return new String[0];
            }
            List val = new ArrayList();
            StringTokenizer st = new StringTokenizer(content, "^");
            while (st.hasMoreTokens()) {
                String portlet = st.nextToken();
                if (portlet != null && portlet.length() != 0) {
                    val.add(portlet);
                }
            }

            return (String[]) val.toArray(new String[0]);

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error", e);
            return new String[0];
        }
    /* logger.info("Setting context classloader");
    ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
    try {
    
    Thread.currentThread().setContextClassLoader(loader);
    logger.info("After Setting context classloader");
    Class clazz = loader.loadClass("org.netbeans.modules.portalpack.servers.liferay.webservices.client.PortletServiceService");
    //.loadClass("com.sun.xml.ws.developer.WSBindingProvider");
    Class serviceClazz = loader.loadClass("org.netbeans.modules.portalpack.servers.liferay.webservices.client.PortletService");
    Class[] paramType = {URL.class, QName.class};
    Object[] paramValues = {url, new QName("http://webservices.liferay.servers.portalpack.modules.netbeans.org/", "PortletServiceService")};
    
    Constructor con = clazz.getConstructor(paramType);
    Object ob = con.newInstance(paramValues);
    
    Method getPortletServicePortMethod = clazz.getMethod("getPortletServicePort", null);
    Object returnObj = getPortletServicePortMethod.invoke(ob, null);
    
    //call getPortlet on PortletService
    //PortletService service = serviceFactory.getPortletServicePort();
    logger.info("Before calling getPortletss...");
    Method getPortletsMethod = serviceClazz.getMethod("getPortlets", null);
    Object listObj = getPortletsMethod.invoke(returnObj, null);
    logger.info("List object::" + listObj);
    if (listObj instanceof List) {
    List<String> list = (List<String>) listObj;
    if (list == null) {
    return new String[0];
    }
    return (String[]) list.toArray(new String[0]);
    }
    } catch (Exception e) {
    writeErrorToOutput(uri, e);
    return new String[0];
    } finally {
    
    Thread.currentThread().setContextClassLoader(contextClassLoader);
    }
     */

    //return new String[0];
    /*        PortletServiceService serviceFactory = new PortletServiceService(url, new QName("http://webservices.liferay.servers.portalpack.modules.netbeans.org/", "PortletServiceService"));
    PortletService service = serviceFactory.getPortletServicePort();
    List<String> list = service.getPortlets();
    if (list == null) {
    return new String[0];
    }
    return (String[]) list.toArray(new String[0]);*/


    }
    /*
    protected void updateCache() {
    try {
    Class cacheClazz = loader.loadClass(PORTLET_REGISTRY_CACHE);
    Method m = cacheClazz.getMethod("init", new Class[]{});
    m.invoke(null, null);
    } catch (Exception e) {
    e.printStackTrace();
    //ignore exception incase of class not found.
    }
    }*/

    protected ExtendedClassLoader initClassLoader() throws MalformedURLException {
        //System.setProperty("com.sun.portal.portletcontainer.dir",psconfig.getPSHome());
        ExtendedClassLoader loader = new ExtendedClassLoader(getClass().getClassLoader());

        //   ProtectionDomain prDomain = PortletServiceService.class.getProtectionDomain();
        //  CodeSource source = prDomain.getCodeSource();

        //loader.addURL(new File(source.getLocation().getFile()));
        //File configDir = new File(psconfig.getPSHome() + File.separator + "config");
        //logger.info(configDir.getAbsolutePath());
        //loader.addURL(configDir);

        String jdkVersion = "1.5";
        try {
            loader.loadClass("java.awt.Desktop");
            jdkVersion = "1.6";
        } catch (ClassNotFoundException ex) {
            jdkVersion = "1.5";
        }

        // File fFile = new File("c:\\liferayintegration-client.jar");
        // loader.addURL(fFile);


        File libDir = new File(psconfig.getServerHome() + File.separator + "lib");
        File[] files = null;

        if (jdkVersion.equals("1.5")) {
            files = new File[]{new File(libDir, "webservices-rt.jar"), new File(libDir, "activation.jar"),
                        new File(libDir, "javaee.jar")
                    };
        } else {
            files = new File[]{new File(libDir, "javaee.jar")};
        }
        /*libDir.listFiles(new FilenameFilter() {
        
        public boolean accept(File dir, String name) {
        if (name.endsWith(".jar")) {
        return true;
        }
        return false;
        }
        });*/

        for (int i = 0; i < files.length; i++) {
            logger.info(files[i].getName());
            loader.addURL(files[i]);
        }
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

    public File getWarFile() {
        FileObject fob = getWarFile(LR_INTEGRATION_WAR);
        if (fob != null) {
            File userHome = new File(System.getProperty("user.home") + File.separator + "portalpackliferaywar");
            if (!userHome.exists()) {
                userHome.mkdir();
            }

            File warFile = new File(userHome, LR_INTEGRATION_WAR);
            if (warFile.exists()) {
                warFile.delete();
            }
            FileObject tobj = FileUtil.toFileObject(userHome);
            try {

                FileUtil.copyFile(fob, tobj, LR_INTEGRATION_APP_NAME);
            } catch (Exception ex) {
                Exceptions.printStackTrace(ex);
            }
            return new File(userHome, LR_INTEGRATION_WAR);
        }
        return null;
    }

    public static FileObject getWarFile(String name) {
        FileObject fo = getLiferayWebSrvFolder() != null ? getLiferayWebSrvFolder().getFileObject(name) : null;
        return fo;
    }

    public static FileObject getLiferayWebSrvFolder() {

        FileObject folder = Repository.getDefault().getDefaultFileSystem().findResource("liferay/webservice");
        return folder;
    }

    private boolean exists(URL url) {

        try {
            int responseCode = ((HttpURLConnection) url.openConnection()).getResponseCode();
            final OutputWriter inOut = UISupport.getServerIO(dm.getUri()).getOut();
            //inOut.write(org.openide.util.NbBundle.getMessage(SunASStartStopListener.class, "CHECK_PORTLET_CONTAINER_INSTALLATION"));
            if (responseCode == 404 || responseCode == 503) //Not Found
            {
                logger.info("404 - Not Found Exception for pc.....");

                //inOut.write("PC Home not found ...........Assumes not installed.............");
                inOut.write(LR_INTEGRATION_WAR + " will be deployed automatically");

            } else {
                return true;
            }
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
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
