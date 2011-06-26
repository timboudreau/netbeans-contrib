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
package org.netbeans.modules.portalpack.servers.core.impl.j2eeservers.tomcat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.deploy.spi.Target;
import javax.enterprise.deploy.spi.TargetModuleID;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.tools.ant.module.api.support.ActionUtils;
import org.netbeans.modules.portalpack.servers.core.PSModuleID;
import org.netbeans.modules.portalpack.servers.core.api.PSDeploymentManager;
import org.netbeans.modules.portalpack.servers.core.impl.j2eeservers.api.DefaultServerDeployHandler;
import org.netbeans.modules.portalpack.servers.core.util.NetbeanConstants;
import org.netbeans.modules.portalpack.servers.core.util.PSConfigObject;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author root
 */
public class TomcatDeployHandler extends DefaultServerDeployHandler {

    private static Logger logger = Logger.getLogger(NetbeanConstants.PORTAL_LOGGER);
    private PSConfigObject psconfig;
    private PSDeploymentManager dm;
    private FileObject taskFile;
    private String DEPLOY_XML = ".liferaytask.xml";
    private static String TOMCAT_CONF_DIR = "conf" + File.separator + "Catalina" + File.separator + "localhost";

    /**
     * Creates a new instance of TomcatDeployHandler
     */
    public TomcatDeployHandler(PSDeploymentManager dm) {
        this.dm = dm;
        this.psconfig = dm.getPSConfig();
        this.taskFile = taskFile;
    //initBuildScript();
    }

    private void initBuildScript() {
        InputStream input = this.getClass().getClassLoader().getResourceAsStream("org/netbeans/modules/portalpack/" +
            "servers/liferay/antscripts/task.xml");
        OutputStream output = null;


        File tempFile = new File(System.getProperty("user.home"), ".liferaytask.xml");
        if (tempFile.exists()) {
            tempFile.delete();
        }
        File buildFile = new File(System.getProperty("user.home"), DEPLOY_XML);
        buildFile.deleteOnExit();

        try {
            output = new FileOutputStream(buildFile);
        } catch (FileNotFoundException ex) {
            logger.log(Level.SEVERE, "error", ex);
        }

        try {
            FileUtil.copy(input, output);
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "error", ex);
        }

        if (output != null) {
            try {
                output.flush();
                output.close();
            } catch (IOException ex) {
                logger.log(Level.SEVERE, "error", ex);
            }

        }
        if (input != null) {
            try {
                input.close();
            } catch (IOException ex) {
                logger.log(Level.SEVERE, "error", ex);
            }
        }

        taskFile = FileUtil.toFileObject(buildFile);

    }

    private void deployOnTomcat(String warFile) throws IOException {

        File file = new File(warFile);
        String fileName = file.getName();

        int index = fileName.lastIndexOf(".");
        if (index != -1) {
            fileName = fileName.substring(0, index);
        }

        String context = fileName;

        Properties props = new Properties();
        props.setProperty("war", warFile);
        props.setProperty("path", "/" + context);
        props.setProperty("host", psconfig.getHost());
        props.setProperty("port", psconfig.getPort());
        props.setProperty("username", psconfig.getProperty(TomcatConstant.MANAGER_USER));
        props.setProperty("password", psconfig.getProperty(TomcatConstant.MANAGER_PASSWORD));
        props.setProperty("taskjar", psconfig.getProperty(TomcatConstant.CATALINA_HOME) + File.separator + "server" + File.separator + "lib" + File.separator + "catalina-ant.jar");

        ActionUtils.runTarget(taskFile, new String[]{"deploy"}, props);

    }

    private void undeployOnTomcat(String appName) throws IOException {


        Properties props = new Properties();

        props.setProperty("path", "/" + appName);

        props.setProperty("username", psconfig.getProperty(TomcatConstant.MANAGER_USER));
        props.setProperty("password", psconfig.getProperty(TomcatConstant.MANAGER_PASSWORD));
        props.setProperty("host", psconfig.getHost());
        props.setProperty("port", psconfig.getPort());
        props.setProperty("taskjar", psconfig.getProperty(TomcatConstant.CATALINA_HOME) + File.separator + "server" + File.separator + "lib" + File.separator + "catalina-ant.jar");


        ActionUtils.runTarget(taskFile, new String[]{"undeploy"}, props);

    }

    public boolean deploy(String warFile) throws Exception {
        try {
            deployOnTomcat(warFile);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error", e);
            return false;
        }
        return true;
    }

    public boolean _undeployFromTomcat(String appName) {
        File appDepDir = new File(psconfig.getProperty(TomcatConstant.CATALINA_HOME) 
                            + File.separator + "webapps" + File.separator + appName);
        
        File warFile = new File(psconfig.getProperty(TomcatConstant.CATALINA_HOME) 
                            + File.separator + "webapps" + File.separator + appName
                            + ".war");
        if(warFile.exists())
            warFile.delete();
        
        if (appDepDir.exists()) {
            undeployDir(appDepDir);
            
        } 
        
        //Incase of exploded directory deployment
            
        File confDir = new File(psconfig.getProperty(TomcatConstant.CATALINA_HOME)
                                        + File.separator + TOMCAT_CONF_DIR);
        String contextXml = appName + ".xml";
            
        File contextXmlFile = new File(confDir,contextXml);
            
        if(contextXmlFile.exists())
            contextXmlFile.delete();
        
        try {

            Thread.sleep(5000);
        } catch (InterruptedException ex) {
            Exceptions.printStackTrace(ex);
        }
        return true;
    }

    protected void undeployDir(File dir) {

        String files[] = dir.list();
        if (files == null) {
            files = new String[0];
        }
        for (int i = 0; i < files.length; i++) {
            File file = new File(dir, files[i]);
            if (file.isDirectory()) {
                undeployDir(file);
            } else {
                file.delete();
            }
            dir.delete();
        }
    }

    public boolean undeploy(String appName) throws Exception {
        try {
            //undeployOnTomcat(appName);
            return _undeployFromTomcat(appName);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error", e);
            return false;
        }
    }

    public boolean install() throws Exception {
        return true;
    }

    public boolean deploy(String deployDir, String contextName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isDeployOnSaveSupported() {
        return true;
    }

    @Override
    public TargetModuleID[] getAvailableModules(Target[] target) {
        String[] portlets = dm.getTaskHandler().getPortlets(null);
        if(portlets == null || portlets.length ==0)
            return new TargetModuleID[0];
        if(target == null || target.length == 0)
            return new TargetModuleID[0];

        Map map = new HashMap();
        List<TargetModuleID> list = new ArrayList();
        for(String portlet:portlets) {

            String appName = portlet;
            int index = appName.lastIndexOf(".");
            if(index != -1) {
                appName = portlet.substring(0,index);
            }

            if(map.get(appName) == null) {
                map.put(appName, appName);
                list.add(new PSModuleID(target[0], appName, appName + ".war"));
            }
        }
        return  (TargetModuleID[]) list.toArray(new TargetModuleID[0]);
    }

    @Override
    public void restart(String contextRoot) throws Exception {
        File confDir = new File(psconfig.getProperty(TomcatConstant.CATALINA_HOME)
                                        + File.separator + TOMCAT_CONF_DIR);
        String contextXml = contextRoot + ".xml";

        File contextXmlFile = new File(confDir,contextXml);
        contextXmlFile.setLastModified(System.currentTimeMillis());
    }

    @Override
    public boolean isServerRunning() {
        /** Return true if a Tomcat server is running on the specifed port */
        int timeout = 2000;
        int port = Integer.parseInt(psconfig.getPort());
        // checking whether a socket can be created is not reliable enough, see #47048
        Socket socket = new Socket();
        try {
            try {
                socket.connect(new InetSocketAddress("localhost", port), timeout); // NOI18N
                socket.setSoTimeout(timeout);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                try {
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    try {
                        // request
                        out.println("HEAD /netbeans-tomcat-status-test HTTP/1.1\nHost: localhost:" + port + "\n"); // NOI18N

                        // response
                        String text = in.readLine();
                        if (text == null || !text.startsWith("HTTP/")) { // NOI18N
                            return false; // not an http response
                        }
                        Map headerFileds = new HashMap();
                        while ((text = in.readLine()) != null && text.length() > 0) {
                            int colon = text.indexOf(':');
                            if (colon <= 0) {
                                return false; // not an http header
                            }
                            String name = text.substring(0, colon).trim();
                            String value = text.substring(colon + 1).trim();
                            List list = (List) headerFileds.get(name);
                            if (list == null) {
                                list = new ArrayList();
                                headerFileds.put(name, list);
                            }
                            list.add(value);
                        }
                        List/*<String>*/ server = (List/*<String>*/) headerFileds.get("Server"); // NIO18N
                        if (server != null) {
                            if (server.contains("Apache-Coyote/1.1")) { // NOI18N
                                if (headerFileds.get("X-Powered-By") == null) { // NIO18N
                                    // if X-Powered-By header is set, it is probably jboss
                                    return true;
                                }
                            } else if (server.contains("Sun-Java-System/Web-Services-Pack-1.4")) {  // NOI18N
                                // it is probably Tomcat with JWSDP installed
                                return true;
                            }
                        }
                        return false;
                    } finally {
                        in.close();
                    }
                } finally {
                    out.close();
                }
            } finally {
                socket.close();
            }
        } catch (Exception ioe) {
            return false;
        }
    }

    @Override
    public File getModuleDirectory(TargetModuleID module) {
        try{
            String contextRoot = module.getModuleID();
            File confDir = new File(psconfig.getProperty(TomcatConstant.CATALINA_HOME) + File.separator + TOMCAT_CONF_DIR);
            String contextXml = contextRoot + ".xml";

            File contextXmlFile = new File(confDir, contextXml);
            if (!contextXmlFile.exists()) {
                return null;
            }

            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(contextXmlFile);
            Element elm = doc.getDocumentElement();
            String docBase = elm.getAttribute("docBase");
            File docBaseFile = new File(docBase);
            if(docBaseFile.exists())
                return docBaseFile;
            else
                return null;
        }catch(Throwable t) {
            return null;
        }

    }
    
}
