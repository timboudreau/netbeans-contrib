/*
 * GePluginUtils.java
 *
 * Created on February 12, 2007, 4:07 PM
 *
 */
package org.netbeans.modules.j2ee.geronimo2;


import org.netbeans.modules.j2ee.geronimo2.dialogs.GePasswordInputDialog;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import org.netbeans.modules.j2ee.deployment.plugins.api.InstanceProperties;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Class providing some helper methods
 * @author Max Sauer
 */
public class GePluginUtils {
    public static final String CONFIG_DIR = File.separator + "j2ee" + File.separator +
            "home" + File.separator + "config"; // NOI18N
    public static final String SERVER_XML = CONFIG_DIR + File.separator + "server.xml"; // NOI18N
    public static final String SYSTEM_JAZN_DATA_XML = CONFIG_DIR + File.separator + "system-jazn-data.xml"; // NOI18N
    
    //--------------- checking for possible server directory -------------
    private static Collection <String> fileRequired = new java.util.ArrayList<String>();

    
    
    
    /** Creates a new instance of GePluginUtils */
    public GePluginUtils() {
    }
    
    //--------------- checking for possible server directory -------------
    private static Collection<String> fileColl = new java.util.ArrayList<String>();
    
    //initialize necessary elements contained in a typicall Gronimo installation
    static {
        fileColl.add("bin");        // NOI18N
        fileColl.add("lib");     // NOI18N
        fileColl.add("repository");    // NOI18N
        //fileColl.add("lib/geronimo-common-1.1.1.jar"); // NOI18N
        fileColl.add("lib/geronimo-kernel-2.0.2.jar"); // NOI18N
    } 
    
    public static String getHttpPort(String instanceURL) {
        InstanceProperties ip = InstanceProperties.getInstanceProperties(instanceURL);
	if (ip != null)
	    return ip.getProperty(InstanceProperties.HTTP_PORT_NUMBER);
	else
	    return "8080";
    }
    
    /**
     * returns Hashmap
     * key = server name
     * value = server folder full path
     */
    public static Hashtable getRegisteredDomains(String serverLocation){
        Hashtable result = new Hashtable();
        //  String domainListFile = File.separator+"common"+File.separator+"nodemanager"+File.separator+"nodemanager.domains";  // NOI18N

        if (isGoodGeServerLocation(new File(serverLocation)))
        {
//           File file = new File(serverLocation + File.separator + "server");  // NOI18N
//
//            String[] files = file.list(new FilenameFilter(){
//                public boolean accept(File dir, String name){
//                    if ((new File(dir.getAbsolutePath()+File.separator+name)).isDirectory()) return true;
//                    return false;
//                }
//            });
//
//            for(int i =0; i<files.length; i++){
//                String path = file.getAbsolutePath() + File.separator + files[i];
//
//                if (isGoodJBInstanceLocation4x(new File(path)) ||
//                    isGoodJBInstanceLocation5x(new File(path)))
//                {
//                    result.put(files[i], path);
//                }
//            }
	    
	    File file = new File(serverLocation);
	    result.put(file.getName(), file.getAbsolutePath());
        }
        return result;
    }

    
    public static int getHttpPort(String geHomeLocal, String webSite) {
        int httpPort = 8080;
        InputStream inputStream = null;
        Document document = null;
        String webSiteFilePath = "";
        
        for (Iterator<String> it = getPathForElement(geHomeLocal, "web-site").iterator(); it.hasNext();) { // NOI18N
            String webSitePath = it.next();
            String webSiteName =  webSitePath.substring( webSitePath.lastIndexOf(File.separatorChar) + 1,  webSitePath.indexOf("-web-site.xml")); // NOI18N
            if(webSiteName.equals(webSite))
                webSiteFilePath = webSitePath;
        }
        
        if(GeDebug.isEnabled()) {
            GeDebug.log("org.netbeans.modules.j2ee.geronimo2.GePluginUtils", webSite);
        }
        File webSiteFile = new File(webSiteFilePath);
        if(GeDebug.isEnabled()) {
            GeDebug.log("org.netbeans.modules.j2ee.geronimo2.GePluginUtils", webSiteFile.getAbsolutePath());
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
    
    
    /**
     * Verifies server instace location
     * @return true if the location is valid
     */ 
    public static boolean isGoodGeServerLocation(File candidate){
        if (null == candidate ||
                !candidate.exists() ||
                !candidate.canRead() ||
                !candidate.isDirectory()  ||
                !hasRequiredChildren(candidate, fileColl)) {
            return false;
        }
        return true;
    }
    
    /**
     * Verifies server instace location
     * @return true if the location is valid
     */ 
//    public static boolean isGoodJBInstanceLocation(File candidate){
//        if (null == candidate ||
//                !candidate.exists() ||
//                !candidate.canRead() ||
//                !candidate.isDirectory()  ||
//                !hasRequiredChildren(candidate, serverFileColl)) {
//            return false;
//        }
//        return true;
//    }
    
    private static boolean hasRequiredChildren(File candidate, Collection requiredChildren) {
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
    
    //TODO: think out some way to add users
    public static boolean isUserActivated(String geHomeLocal, String user) {
        File xmlFile = new File(geHomeLocal + File.separator + SYSTEM_JAZN_DATA_XML);
        
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
    
    public static boolean activateUser(String geHomeLocal, String user, String password) {
        File xmlFile = new File(geHomeLocal + File.separator + SYSTEM_JAZN_DATA_XML);
        
//        try {
//            XPathFactory factory = XPathFactory.newInstance();
//            XPath xpath = factory.newXPath();
//            
//            InputStream inputStream = new FileInputStream(xmlFile);
//            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputStream);
//            
//            XPathExpression exprActivate = xpath.compile("//user[name='"+user+"']");
//            XPathExpression exprPassword = xpath.compile("//user[name='"+user+"']/credentials/text()");
//            
//            Node nodeActivate = (Node) exprActivate.evaluate(document, XPathConstants.NODE);
//            Node nodePassword = (Node) exprPassword.evaluate(document, XPathConstants.NODE);
//            
//            if(nodeActivate != null) {
//                try {
//                    nodeActivate.getAttributes().removeNamedItem("deactivated");
//                } catch(DOMException e) {
//                    // Nothing to do
//                }
//            }
//            
//            if(nodePassword != null) {
//                try {
//                    nodePassword.setNodeValue("!"+password);
//                } catch(DOMException e) {
//                    return false;
//                }
//            }
//            
//            XMLSerializer serializer = new XMLSerializer();
//            serializer.setOutputCharStream(new FileWriter(xmlFile));
//            serializer.serialize(document);
//            
            return true;
//        } catch(Exception ex) {
//            Logger.getLogger("global").log(Level.INFO, null, ex);
//        }
//        
//        return false;
    }
    
    public static boolean isLocalServer(InstanceProperties ip) {
        String host = ip.getProperty(GePluginProperties.PROPERTY_HOST);
        
        if(host != null && host.equals("localhost"))
            return true;
        
        return false;
    }
    
    public static Collection<String> getUsers(String oc4jHomeLocal) {
        ArrayList<String> users = new ArrayList<String>();
	
	//XXX -- read users
	users.add("admin");
	
//        File xmlFile = new File(oc4jHomeLocal + File.separator + SYSTEM_JAZN_DATA_XML);
//        
//        try {
//            XPathFactory factory = XPathFactory.newInstance();
//            XPath xpath = factory.newXPath();
//            
//            InputStream inputStream = new FileInputStream(xmlFile);
//            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputStream);
//            
//            XPathExpression expr = xpath.compile("//role[name='oc4j-administrators']/members/member/name/text()");
//            
//            NodeList nodes = (NodeList) expr.evaluate(document, XPathConstants.NODESET);
//            
//            for(int i=0;i<nodes.getLength();i++)
//                users.add(nodes.item(i).getNodeValue());
//        } catch(Exception ex) {
//            Logger.getLogger("global").log(Level.INFO, null, ex);
//        }
        
        return users;
    }
    
    public static URL fileToUrl(File file) throws MalformedURLException {
        URL url = file.toURI().toURL();
        if (FileUtil.isArchiveFile(url)) {
            url = FileUtil.getArchiveRoot(url);
        }
        return url;
    }
    
    public static String requestPassword(String user) {
        GePasswordInputDialog d = new GePasswordInputDialog(user);
        
        if(DialogDisplayer.getDefault().notify(d) == NotifyDescriptor.OK_OPTION) {
            return d.getPassword();
        }
        
        return null;
    }
    
    public static Collection<String> getWebSites(String geHomeLocal) {
        ArrayList<String> webSiteNames = new ArrayList<String>();
	
	//XXX add webs
	webSiteNames.add("some web");
	
//        for(String webSite : getPathForElement(geHomeLocal, "web-site")) { //NOI18N
//            webSiteNames.add(webSite.substring(webSite.lastIndexOf(File.separatorChar) + 1, webSite.indexOf("-web-site.xml"))); //NOI18N
//        }
        return webSiteNames;
    }
    
    static Collection<String> getPathForElement(String geHomeLocal, String element) {
        InputStream inputStream = null;
        Document document = null;
        ArrayList<String> paths = new ArrayList<String>();
        File xmlFile = new File(geHomeLocal + File.separator + SERVER_XML);
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
                        paths.add(geHomeLocal + CONFIG_DIR + File.separator + path.substring(2));
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
    
    public static int getAdminPort(String geHomeLocal) {
        int adminPort = 1099;
//        InputStream inputStream = null;
//        Document document = null;
//        String rmiFilePath = getPathForElement(geHomeLocal, "rmi-config").iterator().next();
//        File rmiFile = new File(rmiFilePath);
//        try {
//            inputStream = new FileInputStream(rmiFile);
//            document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputStream);
//            Element rootElement = document.getDocumentElement();
//            adminPort = Integer.parseInt(rootElement.getAttributes().getNamedItem("port").getNodeValue()); // NOI18N
//        } catch(Exception ex) {
//            Logger.getLogger("global").log(Level.INFO, null, ex);
//        }
        return adminPort;
    }
    
}
