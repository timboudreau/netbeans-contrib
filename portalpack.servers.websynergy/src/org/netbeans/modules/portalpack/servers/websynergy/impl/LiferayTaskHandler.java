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
import java.lang.reflect.Method;
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
import org.netbeans.modules.j2ee.dd.api.web.DDProvider;
import org.netbeans.modules.j2ee.dd.api.web.WebApp;
import org.netbeans.modules.j2ee.deployment.plugins.api.UISupport;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.util.JarUtils;
import org.netbeans.modules.portalpack.servers.core.api.PSDeploymentManager;
import org.netbeans.modules.portalpack.servers.core.common.DeploymentException;
import org.netbeans.modules.portalpack.servers.core.common.ExtendedClassLoader;
import org.netbeans.modules.portalpack.servers.core.common.FileLogViewerSupport;
import org.netbeans.modules.portalpack.servers.core.common.LogManager;
import org.netbeans.modules.portalpack.servers.core.common.ServerConstants;
import org.netbeans.modules.portalpack.servers.core.impl.DefaultPSTaskHandler;
import org.netbeans.modules.portalpack.servers.core.impl.j2eeservers.api.ServerDeployHandler;
import org.netbeans.modules.portalpack.servers.core.impl.j2eeservers.api.ServerDeployerHandlerFactory;
import org.netbeans.modules.portalpack.servers.core.impl.j2eeservers.tomcat.TomcatConstant;
import org.netbeans.modules.portalpack.servers.core.util.NetbeanConstants;
import org.netbeans.modules.portalpack.servers.core.util.PSConfigObject;
import org.netbeans.modules.portalpack.servers.websynergy.common.LiferayConstants;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.RequestProcessor;

/**
 *
 * @author Satya
 */
public class LiferayTaskHandler extends DefaultPSTaskHandler {

    protected static Logger logger = Logger.getLogger(NetbeanConstants.PORTAL_LOGGER);
    protected PSDeploymentManager dm;
    protected PSConfigObject psconfig;
    protected ServerDeployHandler deployerHandler;
    protected String uri;
    private static String WS_PREFIX = "websynergy";

    /** Creates a new instance of LifeRayTaskHandler */
    public LiferayTaskHandler(PSDeploymentManager dm) {
        this.dm = dm;
        this.psconfig = dm.getPSConfig();
        this.uri = dm.getUri();

        deployerHandler = ServerDeployerHandlerFactory.getServerDeployerHandler(dm);
    }

    public void _deployOnGF(String warfile, String serveruri) throws Exception {
        File warF = new File(warfile);
        
        File tempDir = new File(System.getProperty("java.io.tmpdir") + File.separator
                                    + "portalpack" + File.separator
                                    + System.currentTimeMillis());
        
        //long t1 = System.currentTimeMillis();
        JarUtils.unjar(new FileInputStream(warF), tempDir);
        
        writeToOutput(uri, "Extracted : "+warfile);
        //long t2 = System.currentTimeMillis();
        //System.out.println("Time taken to unjar::: "+(t2-t1));
        
        String context = warF.getName().substring(0,warF.getName().indexOf("."));
         
        File deployXmlFile = File.createTempFile("deploy",".xml");
        FileOutputStream fout = new FileOutputStream(deployXmlFile);
        String xml = "<Context docBase=\""+ tempDir + "\"></Context>"; 
        try{
            
            fout.write(xml.getBytes());
            fout.flush();
            
        }catch(Exception e) {
            e.printStackTrace();
        }finally {
            fout.close();
        }
        
        //Copy that file
        String autoDeployDir = psconfig.getProperty(LiferayConstants.AUTO_DEPLOY_DIR);
        File deployDirFile = new File(autoDeployDir);
        if (!deployDirFile.exists()) {
            deployDirFile.mkdirs();
        }
        
        //check if the conf directory exists before. Otherwise delete that.
        File confDir = new File(psconfig.getDomainDir() + File.separator + "conf");
        if(confDir.exists())
            confDir = null;
        
        //t1 = System.currentTimeMillis();
        
        String newDepXml = context + ".xml";
        copy(deployXmlFile.getAbsolutePath(), newDepXml, autoDeployDir);
        File webXml = new File(tempDir, "WEB-INF" + File.separator + "web.xml");
        long baseTime = webXml.lastModified();
        
        //wait
        File xmlInAutoDeployDir = new File(autoDeployDir + File.separator + newDepXml);
        int counter = 0;
        while (xmlInAutoDeployDir.exists()) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException ex) {
                logger.info(ex.getMessage());
            }
            counter++;
            if (counter >= 100) {
                break;
            }
        }
        
        long timestamp = 0;
        
        counter = 0;
        while(timestamp <= baseTime) {
            timestamp = webXml.lastModified();
         
          //  System.out.println("Counter value is::::::: " + counter);
            try {
                Thread.sleep(200);
            } catch (InterruptedException ex) {
                logger.info(ex.getMessage());
            }
            counter++;
            if (counter >= 100) {
                break;
            }
        }
        
        if(confDir != null) {
            deleteDir(confDir);
        }
        
        //t2 = System.currentTimeMillis();
        //System.out.println("Time taken to massaging :::: "+(t2-t1));
        
        //For backward compatiblility when server is Liferay.
        if(!isWebSynergy()) 
            verifyDisplayName(tempDir.getAbsolutePath(), context);
        
        File destWar = new File(tempDir.getParentFile(),warF.getName());
        if(destWar.exists())
            destWar.delete();
        
        //t1 = System.currentTimeMillis();
        JarUtils.jar(new FileOutputStream(destWar), tempDir.listFiles());
        
        writeToOutput(uri, warfile +" file updated successfully.");
        //t2 = System.currentTimeMillis();
        //System.out.println("Time taken to JAAAR::::: " + (t2-t1));
        
        //t1 = System.currentTimeMillis();
        deployerHandler.deploy(destWar.getAbsolutePath());
        
        //t2 = System.currentTimeMillis();
        //System.out.println("Time Taken to Deploy:::: "+(t2-t1));
        
        //cleanup all temporary files/directories.
        asynchDeleteDirectory(tempDir);
        destWar.delete();
        
        showServerLog();
        
    }
    
    private void verifyDisplayName(String deployDir, String displayName) {
        
        File webXmlFile = new File(deployDir + File.separator +
                                    "WEB-INF" + File.separator + "web.xml");
        if(!webXmlFile.exists())
            return;
        
        FileObject dd = FileUtil.toFileObject(FileUtil.normalizeFile(webXmlFile));
        try {
            WebApp ddRoot = DDProvider.getDefault().getDDRoot(dd);
            String disName = ddRoot.getDefaultDisplayName();
            
            if(disName == null || disName.trim().length() == 0)
                return;
            
            if(!displayName.equals(disName)) {
                ddRoot.setDisplayName(displayName);
                ddRoot.write(dd);
            }
                
        } catch (IOException ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }
    
    public String deploy(String warfile, String serveruri) throws Exception {

        if (psconfig.getServerType().equals(ServerConstants.SUN_APP_SERVER_9)
                && isDirectoryDeploymentSupported()) {
            
            _deployOnGF(warfile, serveruri);
            return "deployed";
        }
        
        String deployDir = psconfig.getProperty(LiferayConstants.AUTO_DEPLOY_DIR);
        File deployDirFile = new File(deployDir);
        if (!deployDirFile.exists()) {
            deployDirFile.mkdirs();
        }
        long baseTime = System.currentTimeMillis();
        copy(warfile, deployDir);
        showServerLog();
        return getDeploymentMessage(warfile, baseTime);
    }

    public String getDeploymentMessage(String warfile, long baseTime) throws Exception {
        File warFile = new File(warfile);
        String warName = warFile.getName();

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
    
    
    private boolean isWebSynergy() {
        if(uri.startsWith(WS_PREFIX))
            return true;
        else
            return false;
    }
    
    private boolean isDirectoryDeploymentSupported() {
     
        //For liferay directory deployment is always supported..
        if(!isWebSynergy())
            return true;

        //check if the directory deployment is supported...
        File portletContainerJar = new File(
                psconfig.getDomainDir() + File.separator + "lib" +
                File.separator + "portlet-container.jar");
        
        ExtendedClassLoader loader = null;
        try {
            loader = new ExtendedClassLoader();
            loader.addURL(portletContainerJar);
        } catch (MalformedURLException ex) {
            return false;
        } catch (RuntimeException ex) {
            return false;
        }
        
        
        Class _clazz = null;
        try {
            _clazz = loader.loadClass("com.sun.portal.portletcontainer.warupdater.PortletWarUpdater");
        } catch (ClassNotFoundException ex) {
            return false;
        }
        
        if(_clazz == null)
            return false;
        try {

            Method m = _clazz.getMethod("preparePortlet", new Class[]{String.class, File.class});
        } catch (NoSuchMethodException ex) {
            return false;
        } catch (SecurityException ex) {
            return false;
        }
        
        return true;
        
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
        /* 
        if(psconfig.getServerType().equals(ServerConstants.SUN_APP_SERVER_9)){
            String appPortletWar 
                    = psconfig.getDomainDir() + File.separator + "autodeploy" +
                    File.separator + portletAppName+".war";
            String appPortletWarUndeployed 
                    = appPortletWar+"_undeployed";
            File warF = new File(appPortletWar);
            File warFUndeployed =  new File(appPortletWarUndeployed);
            if (warF.exists()) {
                warF.delete();
                System.out.println("Deleted/Undeployed dude...");
            } 
            if (warFUndeployed.exists()) {
            warFUndeployed.delete();
            }
        } else {
            deployerHandler.undeploy(portletAppName);
        } */
        deployerHandler.undeploy(portletAppName);
        showServerLog();
    }

    @Override
    public String deploy(String deployedDir, String warfile, String serveruri) throws Exception {
        File warF = new File(warfile);
         String context = warF.getName().substring(0,warF.getName().indexOf("."));
         /*try{
            undeploy(context,"");
         }catch(Exception e){
             e.printStackTrace();
         }*/
        File deployXmlFile = File.createTempFile("deploy",".xml");
        FileOutputStream fout = new FileOutputStream(deployXmlFile);
        String xml = "<Context docBase=\""+ deployedDir + "\"></Context>"; 
        try{
            
            fout.write(xml.getBytes());
            fout.flush();
            
        }catch(Exception e) {
            e.printStackTrace();
        }finally {
            fout.close();
        }
        
        //Copy that file
        String autoDeployDir = psconfig.getProperty(LiferayConstants.AUTO_DEPLOY_DIR);
        File deployDirFile = new File(autoDeployDir);
        if (!deployDirFile.exists()) {
            deployDirFile.mkdirs();
        }
        
        File webXmlFile = new File(deployedDir + File.separator +
                                    "WEB-INF" + File.separator + "web.xml");
        long baseTime = webXmlFile.lastModified();
        
        //check if the conf directory exists before. Otherwise delete that.
        File confDir = new File(psconfig.getDomainDir() + File.separator + "conf");
        if(confDir.exists())
            confDir = null;
        
        String newDepXml = context + ".xml";
        copy(deployXmlFile.getAbsolutePath(), newDepXml, autoDeployDir);
        
        //wait
        File xmlInAutoDeployDir = new File(autoDeployDir + File.separator + newDepXml);
        int counter = 0;
        while (xmlInAutoDeployDir.exists()) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException ex) {
                logger.info(ex.getMessage());
            }
            counter++;
            if (counter >= 100) {
                break;
            }
        }
        
        long timestamp = 0;
        
        counter = 0;
        while(timestamp <= baseTime) {
            timestamp = webXmlFile.lastModified();
            
            try {
                Thread.sleep(200);
            } catch (InterruptedException ex) {
                logger.info(ex.getMessage());
            }
            counter++;
            if (counter >= 100) {
                break;
            }
        }
        
        if(confDir != null)
            deleteDir(confDir);
        
        if(!isWebSynergy())
            verifyDisplayName(deployedDir, context);
        
        showServerLog();
        
        writeToOutput(uri, deployedDir + " updated successfully.");
        //deploy(warfile, serveruri);
        
        if(psconfig.getServerType().equals(ServerConstants.SUN_APP_SERVER_9)) {
            
            deployerHandler.deploy(deployedDir,context);
            
        } else if(psconfig.getServerType().equals(ServerConstants.TOMCAT_5_X)
                    || psconfig.getServerType().equals(ServerConstants.TOMCAT_6_X)){
            
            //wait for 5 sec.. 
            try {
                Thread.sleep(5000);
            } catch (InterruptedException ex) {
                logger.info(ex.getMessage());
            }
        }
        
        if(deployXmlFile.exists())
            deployXmlFile.delete();
        
        return "Deployed...";
    }
    
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

     public static void copy(String fromFileName, String toFolder) throws IOException {
         copy(fromFileName, null, toFolder);
     }
    public static void copy(String fromFileName, String newFileName, String toFolder)
        throws IOException {
        File fromFile = new File(fromFileName);
        File toFile = new File(toFolder);
        
        if(newFileName == null)
            newFileName = fromFile.getName();

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
            toFile = new File(toFile, newFileName);
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
    
    protected void asynchDeleteDirectory(final File dir) {
        
        if(dir == null) return;
        RequestProcessor.getDefault().post(new Runnable() {

            public void run() {
                deleteDir(dir);
            }
            
        });
    }
    
    protected void deleteDir(File dir) {

        if(dir == null)
            return;
        
        String files[] = dir.list();
        if (files == null) {
            files = new String[0];
        }
        for (int i = 0; i < files.length; i++) {
            File file = new File(dir, files[i]);
            if (file.isDirectory()) {
                deleteDir(file);
            } else {
                file.delete();
            }
            dir.delete();
        }
    }

    public void showServerLog() {
        
        try {
            
            File f = new File(psconfig.getDomainDir() + File.separator + "logs"
                              + File.separator + "server.log");
            FileLogViewerSupport p = FileLogViewerSupport.getLogViewerSupport(f, dm.getUri(), 2000, true);
            p.showLogViewer(false);
            
        } catch (IOException ex) {
            //Exceptions.printStackTrace(ex);
        }
    }
    
    private void writeToOutput(String uri,String msg)
    {
        UISupport.getServerIO(uri).getOut().println(msg);
    }
}
