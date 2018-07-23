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

package org.netbeans.modules.j2ee.oc4j.util;

import com.sun.org.apache.xml.internal.serialize.XMLSerializer;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.deploy.spi.TargetModuleID;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import org.netbeans.api.db.explorer.DatabaseException;
import org.netbeans.api.db.explorer.JDBCDriver;
import org.netbeans.api.db.explorer.JDBCDriverManager;
import org.netbeans.modules.j2ee.dd.api.web.DDProvider;
import org.netbeans.modules.j2ee.dd.api.web.Servlet;
import org.netbeans.modules.j2ee.dd.api.web.WebApp;
import org.netbeans.modules.j2ee.deployment.plugins.api.InstanceProperties;
import org.netbeans.modules.j2ee.oc4j.OC4JDeploymentManager;
import org.netbeans.modules.j2ee.oc4j.ide.OC4JErrorManager;
import org.netbeans.modules.web.api.webmodule.WebModule;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author pblaha
 */
public class OC4JPluginUtils {
    public static final String CONFIG_DIR = File.separator + "j2ee" + File.separator +
            "home" + File.separator + "config"; // NOI18N
    public static final String SERVER_XML = CONFIG_DIR + File.separator + "server.xml"; // NOI18N
    public static final String SYSTEM_JAZN_DATA_XML = CONFIG_DIR + File.separator + "system-jazn-data.xml"; // NOI18N
    
    //--------------- checking for possible server directory -------------
    private static Collection <String> fileRequired = new java.util.ArrayList<String>();
    
    static {
        fileRequired.add("bin/oc4j");        // NOI18N
        fileRequired.add("bin/oc4j.cmd");        // NOI18N
        fileRequired.add("j2ee/home/config");     // NOI18N
        fileRequired.add("j2ee/home/config/server.xml"); // NOI18N
        fileRequired.add("j2ee/home/config/default-web-site.xml"); // NOI18N
    }
    
    public static boolean isGoodOC4JHomeLocation(File candidate){
        OC4JDebug.log("org.netbeans.modules.j2ee.oc4j.util.OC4JPluginUtils", "Check location for: " + candidate);
        if (null == candidate ||
                !candidate.exists() ||
                !candidate.canRead() ||
                !candidate.isDirectory() ||
                !hasRequiredChildren(candidate, fileRequired))
            return false;
        OC4JDebug.log("org.netbeans.modules.j2ee.oc4j.util.OC4JPluginUtils", "Location is OK");
        return true;
    }
    
    public static boolean isLocalServer(InstanceProperties ip) {
        String host = ip.getProperty(OC4JPluginProperties.PROPERTY_HOST);
        
        if(host != null && host.equals("localhost"))
            return true;
        
        return false;
    }
    
    private static boolean hasRequiredChildren(File candidate, Collection <String> requiredChildren) {
        if (null == candidate)
            return false;
        String[] children = candidate.list();
        if (null == children)
            return false;
        if (null == requiredChildren)
            return true;
        Iterator iter = requiredChildren.iterator();
        while (iter.hasNext()){
            String next = (String)iter.next();
            File test = new File(candidate.getPath()+File.separator+next);
            if (!test.exists())
                return false;
        }
        return true;
    }
    
    public static int getHttpPort(String oc4jHomeLocal, String webSite) {
        int httpPort = 8888;
        InputStream inputStream = null;
        Document document = null;
        String webSiteFilePath = "";
        
        for (Iterator<String> it = getPathForElement(oc4jHomeLocal, "web-site").iterator(); it.hasNext();) { // NOI18N
            String webSitePath = it.next();
            String webSiteName =  webSitePath.substring( webSitePath.lastIndexOf(File.separatorChar) + 1,  webSitePath.indexOf("-web-site.xml")); // NOI18N
            if(webSiteName.equals(webSite))
                webSiteFilePath = webSitePath;
        }
        
        if(OC4JDebug.isEnabled()) {
            OC4JDebug.log("org.netbeans.modules.j2ee.oc4j.util.OC4JPluginUtils", webSite);
        }
        File webSiteFile = new File(webSiteFilePath);
        if(OC4JDebug.isEnabled()) {
            OC4JDebug.log("org.netbeans.modules.j2ee.oc4j.util.OC4JPluginUtils", webSiteFile.getAbsolutePath());
        }
        try {
            inputStream = new FileInputStream(webSiteFile);
            document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputStream);
            Element rootElement = document.getDocumentElement();
            httpPort = Integer.parseInt(rootElement.getAttributes().getNamedItem("port").getNodeValue()); // NOI18N
        } catch(Exception ex) {
            Logger.getLogger("global").log(Level.INFO, null, ex);
        }
        return httpPort;
    }
    
    public static String getHttpPort(String instanceURL) {
        InstanceProperties ip = InstanceProperties.getInstanceProperties(instanceURL);
        return ip.getProperty(InstanceProperties.HTTP_PORT_NUMBER);
    }
    
    public static int getAdminPort(String oc4jHomeLocal) {
        int adminPort = 23791;
        InputStream inputStream = null;
        Document document = null;
        String rmiFilePath = getPathForElement(oc4jHomeLocal, "rmi-config").iterator().next();
        File rmiFile = new File(rmiFilePath);
        try {
            inputStream = new FileInputStream(rmiFile);
            document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputStream);
            Element rootElement = document.getDocumentElement();
            adminPort = Integer.parseInt(rootElement.getAttributes().getNamedItem("port").getNodeValue()); // NOI18N
        } catch(Exception ex) {
            Logger.getLogger("global").log(Level.INFO, null, ex);
        }
        return adminPort;
    }
    
    public static Collection<String> getWebSites(String oc4jHomeLocal) {
        ArrayList<String> webSiteNames = new ArrayList<String>();
        for(String webSite : getPathForElement(oc4jHomeLocal, "web-site")) { //NOI18N
            webSiteNames.add(webSite.substring(webSite.lastIndexOf(File.separatorChar) + 1, webSite.indexOf("-web-site.xml"))); //NOI18N
        }
        return webSiteNames;
    }
    
    public static Collection<String> getUsers(String oc4jHomeLocal) {
        ArrayList<String> users = new ArrayList<String>();
        File xmlFile = new File(oc4jHomeLocal + File.separator + SYSTEM_JAZN_DATA_XML);
        
        try {
            XPathFactory factory = XPathFactory.newInstance();
            XPath xpath = factory.newXPath();
            
            InputStream inputStream = new FileInputStream(xmlFile);
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputStream);
            
            XPathExpression expr = xpath.compile("//role[name='oc4j-administrators']/members/member/name/text()");
            
            NodeList nodes = (NodeList) expr.evaluate(document, XPathConstants.NODESET);
            
            for(int i=0;i<nodes.getLength();i++)
                users.add(nodes.item(i).getNodeValue());
        } catch(Exception ex) {
            Logger.getLogger("global").log(Level.INFO, null, ex);
        }
        
        return users;
    }
    
    public static boolean isUserActivated(String oc4jHomeLocal, String user) {
        File xmlFile = new File(oc4jHomeLocal + File.separator + SYSTEM_JAZN_DATA_XML);
        
        try {
            XPathFactory factory = XPathFactory.newInstance();
            XPath xpath = factory.newXPath();
            
            InputStream inputStream = new FileInputStream(xmlFile);
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputStream);
            
            XPathExpression expr = xpath.compile("//user[name='"+user+"']");
            
            Node node = (Node) expr.evaluate(document, XPathConstants.NODE);
            
            if (node.getAttributes().getNamedItem("deactivated") != null)
                return false;
            
            return true;
        } catch(Exception ex) {
            Logger.getLogger("global").log(Level.INFO, null, ex);
        }
        
        return false;
    }
    
    public static boolean activateUser(String oc4jHomeLocal, String user, String password) {
        File xmlFile = new File(oc4jHomeLocal + File.separator + SYSTEM_JAZN_DATA_XML);
        
        try {
            XPathFactory factory = XPathFactory.newInstance();
            XPath xpath = factory.newXPath();
            
            InputStream inputStream = new FileInputStream(xmlFile);
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputStream);
            
            XPathExpression exprActivate = xpath.compile("//user[name='"+user+"']");
            XPathExpression exprPassword = xpath.compile("//user[name='"+user+"']/credentials/text()");
            
            Node nodeActivate = (Node) exprActivate.evaluate(document, XPathConstants.NODE);
            Node nodePassword = (Node) exprPassword.evaluate(document, XPathConstants.NODE);
            
            if(nodeActivate != null) {
                try {
                    nodeActivate.getAttributes().removeNamedItem("deactivated");
                } catch(DOMException e) {
                    // Nothing to do
                }
            }
            
            if(nodePassword != null) {
                try {
                    nodePassword.setNodeValue("!"+password);
                } catch(DOMException e) {
                    return false;
                }
            }
            
            XMLSerializer serializer = new XMLSerializer();
            serializer.setOutputCharStream(new FileWriter(xmlFile));
            serializer.serialize(document);
            
            return true;
        } catch(Exception ex) {
            Logger.getLogger("global").log(Level.INFO, null, ex);
        }
        
        return false;
    }
    
    public static String requestPassword(String user) {
        OC4JPasswordInputDialog d = new OC4JPasswordInputDialog(user);
        
        if(DialogDisplayer.getDefault().notify(d) == NotifyDescriptor.OK_OPTION) {
            return d.getPassword();
        }
        
        return null;
    }
    
    static Collection<String> getPathForElement(String oc4jHomeLocal, String element) {
        InputStream inputStream = null;
        Document document = null;
        ArrayList<String> paths = new ArrayList<String>();
        File xmlFile = new File(oc4jHomeLocal + File.separator + SERVER_XML);
        try{
            inputStream = new FileInputStream(xmlFile);
            document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputStream);
            NodeList elementsList = document.getElementsByTagName(element);
            if(elementsList.getLength() == 0) {
                return paths;
            } else {
                for(int i = 0; i < elementsList.getLength(); i++) {
                    Node pathAttr = elementsList.item(i).getAttributes().getNamedItem("path"); // NOI18N
                    String path = pathAttr.getNodeValue();
                    if(path.startsWith("./")) {
                        paths.add(oc4jHomeLocal + CONFIG_DIR + File.separator + path.substring(2));
                    } else {
                        paths.add(path);
                    }
                }
            }
        }catch(Exception e){
            Logger.getLogger("global").log(Level.INFO, null, e);
        }
        return paths;
    }
    
    public static String getName(TargetModuleID module) {
        String s = module.toString();
        s = s.substring(s.indexOf("name=")+5);
        
        return s.substring(0, (s.indexOf(".") == -1)?(s.indexOf(",")):(s.indexOf(".")));
    }
    
    public static String getServerRoot(String instanceURL) {
        InstanceProperties ip = InstanceProperties.getInstanceProperties(instanceURL);
        String serverRoot = ip.getProperty(OC4JPluginProperties.PROPERTY_OC4J_HOME);
        
        return serverRoot;
    }
    
    public static String getHostname(String instanceURL) {
        InstanceProperties ip = InstanceProperties.getInstanceProperties(instanceURL);
        return ip.getProperty(OC4JPluginProperties.PROPERTY_HOST);
    }
    
    public static void registerOracleJdbcDriver(String serverRoot) {
        if(serverRoot == null)
            return;
        
        List<URL> list = new ArrayList<URL>();
        File serverDir = new File(serverRoot);
        
        try{
            for(File file:new File(serverDir, "jdbc/lib").listFiles()) {
                if(FileUtil.isArchiveFile(file.toURI().toURL()))
                    list.add(fileToUrl(file));
            }
        } catch(MalformedURLException ex) {
            Logger.getLogger("global").log(Level.INFO, null, ex);
        }
        
        URL[] urls = list.toArray(new URL[list.size()]);
        String name = "Oracle";
        String clazz = "oracle.jdbc.driver.OracleDriver";
        
        JDBCDriver driver = JDBCDriver.create(name, name, clazz, urls);
        
        if(JDBCDriverManager.getDefault().getDrivers(clazz).length == 0) {
            try {
                JDBCDriverManager.getDefault().addDriver(driver);
            } catch(DatabaseException e) {
                // Nothing to do
            }
        }
    }
    
    public static URL fileToUrl(File file) throws MalformedURLException {
        URL url = file.toURI().toURL();
        if (FileUtil.isArchiveFile(url)) {
            url = FileUtil.getArchiveRoot(url);
        }
        return url;
    }
    
    public static boolean checkClass(String clazz, OC4JDeploymentManager dm) {
        // Creating a class loader to check if there is a driver on the server
        List<URL> l = dm.getProperties().getClasses();
        URL[] urls = l.toArray(new URL[] {});
        ClassLoader c = new URLClassLoader(urls);
        
        // Driver check
        try {
            Class.forName(clazz, true, c);
        } catch (ClassNotFoundException e) {
            OC4JErrorManager.getInstance(dm).error(clazz, e, OC4JErrorManager.GENERIC_FAILURE);
            return false;
        }
        
        return true;
    }
    
    public static boolean isJSFInWebModule(WebModule wm) {
        // The JavaEE 5 introduce web modules without deployment descriptor. In such wm can not be jsf used.
        FileObject dd = wm.getDeploymentDescriptor();
        return (dd != null && getActionServlet(dd) != null);
    }
    
    public static Servlet getActionServlet(FileObject dd) {
        if (dd == null) {
            return null;
        }
        try {
            WebApp webApp = DDProvider.getDefault().getDDRoot(dd);
            
            // Try to find according the servlet class name. The javax.faces.webapp.FacesServlet is final, so
            // it can not be extended.
            return (Servlet) webApp
                    .findBeanByName("Servlet", "ServletClass", "javax.faces.webapp.FacesServlet"); //NOI18N;
        } catch (java.io.IOException e) {
            return null;
        }
    }
}