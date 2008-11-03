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

package org.netbeans.modules.portalpack.servers.core.util;

import java.beans.PropertyChangeListener;
import javax.enterprise.deploy.spi.DeploymentManager;
import org.netbeans.modules.portalpack.servers.core.PSConfigCallBackHandler;
import java.io.File;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.WeakHashMap;
import java.util.Properties;
import org.netbeans.modules.j2ee.deployment.plugins.api.InstanceProperties;
import org.netbeans.modules.portalpack.servers.core.api.PSDeploymentManager;

/**
 *
 * @author Satya
 */
public class PSConfigObject {
    
   // public static final String SERVER_CONFIG_FILE = NetbeanConstants.CONFIG_DIR + File.separator + "ps71.xml";
    private static WeakHashMap map = new WeakHashMap();
    
    private String psHome = "";
    private String adminUser = "";
    private String adminPassWord = "";
    private String serverHome = "";
    private String serverType = "";
    private String portalId = "";
    private String filePath = "";
    
    private String instanceId = "";
    private String defaultDomain = "";
    private String host = "";
    private String port = "";
    private boolean isRemote  = false;
    private String domainDir = "";
    private String adminPort = "";
    private String classPath = "";
    private String uri = "";
    private String displayName = "";
    
    private String portalUri = "";
    private boolean directoryDeployment = false;
    
    private Properties props = null;
    
    private boolean newInstance = false;
   // private boolean libraryChanged = false;
    
    private static PSConfigObject instance = null;
    
    private PSConfigObject(String uri){
      //  File file = new File(SERVER_CONFIG_FILE);
        this.uri = uri;
       // load(file);
        load(uri);
    }
    
    public static PSConfigObject getPSConfigObject(String uri) {
        
        PSConfigObject object = (PSConfigObject)map.get(uri);
        
        if(object == null) {
            synchronized(PSConfigObject.class) {
                object = (PSConfigObject)map.get(uri);
                if(object == null) {
                    object = new PSConfigObject(uri);
                    map.put(uri,object);
                }
            }
        }
        
        return object;
    }
    
    public void destroy()
    {
        if(uri != null)
            map.remove(uri);
    }
    
    public void setPSHome(String pHome) {
        psHome = pHome;
    }
    
    public String getPSHome() {
        return psHome;
    }
    
    public String getAdminUser() {
        return adminUser;
    }
    
    public void setAdminUser(String adUser) {
        adminUser = adUser;
    }
    
    public String getAdminPassWord() {
        return adminPassWord;
    }
    public void setAdminPassWord(String password) {
        adminPassWord = password;
    }
    
    public String getPortalId() {
        return portalId;
    }
    
    public void setPortalId(String id) {
        portalId = id;
    }
    
    public String getServerHome() {
        return serverHome;
    }
    public void setServerHome(String home) {
        serverHome = home;
    }
    
    public void setServerType(String type) {
        serverType = type;
    }
    
    public String getServerType() {
        return serverType;
    }
    
    
    public void setDomainDir(String dir) {
        domainDir = dir;
    }
    
    public String getDomainDir() {
        return domainDir;
    }
    
    public String getAdminPort() {
        return adminPort;
    }
    
    public void setAdminPort(String port) {
        adminPort = port;
    }
    
    public String getDisplayName()
    {
        return displayName;
    }

    public boolean isDirectoryDeployment() {
        return directoryDeployment;
    }

    public void setDirectoryDeployment(boolean directoryDeployment) {
        this.directoryDeployment = directoryDeployment;
    }
    
 //comment start
    
   /* public void load(File file) {
  
        if(!file.exists()) {
            if(!file.getParentFile().exists())
                file.getParentFile().mkdirs();
  
            doc = new Document();
  
            topElm = new Element("Servers");
  
            rootElm = new Element("Server");
            rootElm.setAttribute("name",uri);
            createXmlStructure();
            doc.setRootElement(topElm);
            newInstance = true;
  
        }else{
  
            if(doc == null)
                doc = XMLUtil.createDocFromFile(file.getAbsolutePath());
            if(topElm == null)
                topElm = doc.getRootElement();
            List list = topElm.getChildren("Server");
  
            for(int i=0;i<list.size();i++) {
                Element tempElm = (Element)list.get(i);
                if(tempElm.getAttributeValue("name").equalsIgnoreCase(uri)) {
                    rootElm = tempElm;
                    break;
                }
            }
  
            if(rootElm == null) {
                rootElm = new Element("Server");
                rootElm.setAttribute("name",uri);
                createXmlStructure();
                newInstance = true;
  
            }else {
  
                setPSHome(getElementValue("PS_HOME"));
                setAdminUser(getElementValue("ADMIN_USER"));
                setAdminPassWord(getElementValue("ADMIN_PASSWORD"));
                setPortalId(getElementValue("PORTAL_ID"));
  
                String remoteVal = getElementValue("IS_REMOTE");
                if(remoteVal == null)
                    setRemote(false);
                else if(remoteVal.trim().equalsIgnoreCase("true"))
                    setRemote(true);
                else
                    setRemote(false);
  
                
  
                setInstanceId(getElementValue("INSTANCE_ID"));
                setDefaultDomain(getElementValue("DEFAULT_DOMAIN"));
                setHost(getElementValue("HOST"));
                setPort(getElementValue("PORT"));
  
                setServerHome(getElementValue("SERVER_HOME"));
                setServerType(getElementValue("SERVER_TYPE"));
                setDomainDir(getElementValue("DOMAIN_DIR"));
                setAdminPort(getElementValue("ADMIN_PORT"));
                setClassPath(getElementValue("CLASSPATH"));
                setPortalUri(getElementValue("PORTAL_URI"));
            }
        }
  
        filePath = file.getAbsolutePath();
    }
    */
    //Not used
     public void load(String uri) {
         
        InstanceProperties ip = InstanceProperties.getInstanceProperties(uri);
        
        if(ip == null)
            return;
        
        displayName = ip.DISPLAY_NAME_ATTR;
        setPSHome(ip.getProperty("PS_HOME"));
       // System.out.println("PS HOME is : "+getPSHome());
        setAdminUser(ip.getProperty("ADMIN_USER"));
        setAdminPassWord(ip.getProperty("ADMIN_PASSWORD"));
        setPortalId(ip.getProperty("PORTAL_ID"));

        String remoteVal = ip.getProperty("IS_REMOTE");
        if(remoteVal == null)
            setRemote(false);
        else if(remoteVal.trim().equalsIgnoreCase("true"))
            setRemote(true);
        else
            setRemote(false);
        
        String directoryDeploymentVal = ip.getProperty("DIRECTORY_DEPL");
        if(directoryDeploymentVal == null)
            setDirectoryDeployment(false);
        else if(directoryDeploymentVal.trim().equalsIgnoreCase("true"))
            setDirectoryDeployment(true);
        else
            setDirectoryDeployment(false);

        //System.out.println("IsRemote is : "+isRemote());

        setInstanceId(ip.getProperty("INSTANCE_ID"));
        setDefaultDomain(ip.getProperty("DEFAULT_DOMAIN"));
        setHost(ip.getProperty("HOST"));
        //setHost(ip.getProperty(ip.HTTP_PORT_NUMBER));
        setPort(ip.getProperty("PORT"));
        
        //setPort(ip.getProperty(ip.));

        setServerHome(ip.getProperty("SERVER_HOME"));
        setServerType(ip.getProperty("SERVER_TYPE"));
        setDomainDir(ip.getProperty("DOMAIN_DIR"));
        setAdminPort(ip.getProperty("ADMIN_PORT"));
        setClassPath(ip.getProperty("CLASSPATH"));
        setPortalUri(ip.getProperty("PORTAL_URI"));
        
        //load specific properties
        Enumeration keys = ip.propertyNames();
        while(keys.hasMoreElements())
        {
            String key = (String)keys.nextElement();
            if(key.startsWith(PSConfigCallBackHandler.ADD_PROP_PREFIX));
            {
                if(props == null)
                    props = new Properties();
                String value = (String)ip.getProperty(key);
                if(value != null)
                    props.setProperty(key,ip.getProperty(key));
            }
            
        }
        
    }
  
    public synchronized void initData(PSConfigCallBackHandler handler) {
  
        handler.setPSHome(getPSHome());
        handler.setAdminUser(getAdminUser());
        handler.setAdminPassWord(getAdminPassWord());
        handler.setPortalId(getPortalId());
        handler.setServerHome(getServerHome());
        handler.setInstanceId(getIntanceId());
        handler.setDefaultDomain(getDefaultDomain());
  
        handler.setHost(getHost());
        handler.setPort(getPort());
        handler.setRemote(isRemote());
  
        handler.setServerType(getServerType());
        handler.setDomainDir(getDomainDir());
        handler.setAdminPort(getAdminPort());
        handler.setClassPath(getClassPath());
        handler.setPortalUri(getPortalUri());
        handler.setDirectoryDeployment(isDirectoryDeployment());
  
    }
  
  
 /* 
    private void save() {
        createXmlStructure();
        if(newInstance)
            topElm.addContent(rootElm);
        XMLUtil.writeXmlDocument(doc,filePath);
    }*/
 //comment end
    
    
    public void saveProperties() {
        InstanceProperties ip = InstanceProperties.getInstanceProperties(uri);
        ip.setProperty("PS_HOME",getPSHome());
        ip.setProperty("ADMIN_USER",getAdminUser());
        ip.setProperty("ADMIN_PASSWORD",getAdminPassWord());
        ip.setProperty("PORTAL_ID",getPortalId());
        ip.setProperty("SERVER_HOME",getServerHome());
        ip.setProperty("SERVER_TYPE",getServerType());
        
        ip.setProperty("IS_REMOTE",String.valueOf(isRemote()));
        ip.setProperty("DIRECTORY_DEPL", String.valueOf(isDirectoryDeployment()));
        
        ip.setProperty("INSTANCE_ID",getIntanceId());
        ip.setProperty("DEFAULT_DOMAIN",getDefaultDomain());
        ip.setProperty("HOST",getHost());
        ip.setProperty("PORT",getPort());
        ip.setProperty("DOMAIN_DIR",getDomainDir());
        ip.setProperty("ADMIN_PORT",getAdminPort());
        ip.setProperty("CLASSPATH",getClassPath());
        ip.setProperty("PORTAL_URI",getPortalUri());
        
        if(props != null)
        {
           Enumeration keys = props.propertyNames();
           while(keys.hasMoreElements())
           {
               String key = (String)keys.nextElement();
               if(key == null) continue;
                String value = props.getProperty(key);
                if(value != null)
                {
                    ip.setProperty(key,value);
                }
           }
         
        }
    }
    
    
    public String getIntanceId() {
        return instanceId;
    }
    
    public void setInstanceId(String instance) {
        this.instanceId = instance;
    }
    
    
    public String getDefaultDomain() {
        return defaultDomain;
    }
    
    public void setDefaultDomain(String dd) {
        this.defaultDomain = dd;
    }
    
    public boolean isRemote(){
        return isRemote;
    }
    
    public void setRemote(boolean flag){
        isRemote = flag;
    }
    
    public String getHost() {
        return host;
    }
    
    public void setHost(String h){
        host = h;
    }
    
    public String getPort() {
        return port;
    }
    
    public void setPort(String po) {
        port = po;
    }
    
    public void setClassPath(String path) {
        classPath = path;
    }
    
    public String getClassPath() {
        return classPath;
    }
    
    public String getPortalUri(){
        return portalUri;
    }
    
    public String getUri()
    {
        return uri;
    }
    public void setPortalUri(String portalUri){
        this.portalUri = portalUri;
    }
    
    public void setAndSaveProperty(String key,String value)
    {
        if(props == null)
            props = new Properties();
        
        key = PSConfigCallBackHandler.ADD_PROP_PREFIX + key;
        props.setProperty(key,value);
        saveProperties();
    }
    
    public String getProperty(String key)
    {
        if(props == null)
            return "";
        
        key = PSConfigCallBackHandler.ADD_PROP_PREFIX + key;
        return props.getProperty(key);
    }
    
    public synchronized void populate(PSConfigCallBackHandler handler)
    {
        setPSHome(handler.getPSHome());
        setAdminUser(handler.getAdminUser());
        setAdminPassWord(handler.getAdminPassWord());
        setPortalId(handler.getPortalId());
        setServerHome(handler.getServerHome());
        setServerType(handler.getServerType());
        
        setInstanceId(handler.getIntanceId());
        setRemote(handler.isRemote());
        setDefaultDomain(handler.getDefaultDomain());
        setHost(handler.getHost());
        setPort(handler.getPort());
        setDomainDir(handler.getDomainDir());
        setAdminPort(handler.getAdminPort());
        setClassPath(handler.getClassPath());
        setPortalUri(handler.getPortalUri());
        
        setDirectoryDeployment(handler.isDirectoryDeployment());
        
        props = new Properties(handler.getProperties());
        
    }
    
    
    public synchronized void save(PSConfigCallBackHandler handler) {
        
        // start/*
   /*     File file = new File(filePath);
        if(!file.exists()) {
            if(!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
        }//end*/
        
        
        
        /*setPSHome(handler.getPSHome());
        setAdminUser(handler.getAdminUser());
        setAdminPassWord(handler.getAdminPassWord());
        setPortalId(handler.getPortalId());
        setServerHome(handler.getServerHome());
        setServerType(handler.getServerType());
        
        setInstanceId(handler.getIntanceId());
        setRemote(handler.isRemote());
        setDefaultDomain(handler.getDefaultDomain());
        setHost(handler.getHost());
        setPort(handler.getPort());
        setDomainDir(handler.getDomainDir());
        setAdminPort(handler.getAdminPort());
        setClassPath(handler.getClassPath());
        setPortalUri(handler.getPortalUri());*/
        populate(handler);
        saveProperties();
        try{
              InstanceProperties ip = InstanceProperties.getInstanceProperties(uri);
              DeploymentManager dm = ip.getDeploymentManager();
              if(dm instanceof PSDeploymentManager)
              {
                    ((PSDeploymentManager)dm).getPSJ2eePlatformImpl().notifyLibrariesChanged();
              }
         }catch(Exception e){
            e.printStackTrace();
         }
       // save();
        
    }

    public void addPropertyChangeListener(PropertyChangeListener propertyChangeListener)
    {
        InstanceProperties ip = InstanceProperties.getInstanceProperties(uri);
        ip.addPropertyChangeListener(propertyChangeListener);
    }
   
}
