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

/*
 * WS70SunDeploymentFactory.java
 */
package org.netbeans.modules.j2ee.sun.ws7.dm;

import javax.enterprise.deploy.shared.factories.DeploymentFactoryManager;
import javax.enterprise.deploy.spi.DeploymentManager;
import javax.enterprise.deploy.spi.factories.DeploymentFactory;
import javax.enterprise.deploy.spi.exceptions.DeploymentManagerCreationException;
import org.netbeans.modules.j2ee.deployment.plugins.api.InstanceProperties;

import org.openide.ErrorManager;
import org.openide.util.NbBundle;
import org.netbeans.modules.j2ee.sun.ws7.WS7LibsClassLoader;
import org.netbeans.modules.j2ee.sun.ws7.ui.WS70URIManager;
import org.netbeans.modules.j2ee.sun.ws7.ui.WS70ServerUIWizardIterator;
import java.io.File;
import java.util.HashMap;

public class WS70SunDeploymentFactory implements DeploymentFactory {    
    private DeploymentFactory ws70DF;
    private static HashMap ws7ClassLoaders;
    private static HashMap connectedDepManagers;
    private static final String HTTPS=":https";//NOI18N
    
    public static synchronized Object create(){
        try{            
            DeploymentFactory factory = new WS70SunDeploymentFactory();
            DeploymentFactoryManager.getInstance().registerDeploymentFactory (factory);            
            return factory;
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * Creates a new instance of WS70SunDeploymentFactory
     */
    public WS70SunDeploymentFactory() {
        ws7ClassLoaders = new HashMap();
        connectedDepManagers = new HashMap();
    }

    
    /**
     * Factory method to create DeploymentManager.
     * @param uri URL of configured manager application.
     * @param uname user with granted manager role
     * @param passwd user's password
     * @throws DeploymentManagerCreationException
     * @return {@link WS70SunDeploymentManager}
     */
    public DeploymentManager getDeploymentManager(String uri, String uname,
                                                  String passwd)
        throws DeploymentManagerCreationException {
        
        String location = WS70URIManager.getLocation(uri);
        
        // If we can't find the location of the WS70 libraries
        // We can not really use the actual deployment manager
        // provided by jsr-88 impl of the WS70
        // This wont likely happene for the getDeploymentManager case
        // it happens in getDisconnectedDeploymentManager case if
        // Web project's target server was removed and the IDE restarts
        
        if(location==null){
            return new WS70SunDeploymentManager(null, null, uri, uname, passwd);            
        }
        WS7LibsClassLoader ws7Libloader = (WS7LibsClassLoader)ws7ClassLoaders.get(location);
        if(ws7Libloader == null){            
            ws7Libloader = (WS7LibsClassLoader)getWs7ClassLoader(location);
            ws7ClassLoaders.put(location, ws7Libloader);
        }
        if(ws70DF==null){
            try{
                this.createInnerFactory(ws7Libloader);
            }catch(Exception ex){            
                throw new DeploymentManagerCreationException(ex.getMessage());
            }
        }
        
        ClassLoader origClassLoader=Thread.currentThread().getContextClassLoader();        
        try{
            String ws70url = WS70URIManager.getURIWithoutLocation(uri);
            InstanceProperties ip =  InstanceProperties.getInstanceProperties(uri);
            String isSSL = ip.getProperty(WS70ServerUIWizardIterator.PROP_SSL_PORT);
            if(Boolean.parseBoolean(isSSL)){
                ws70url=ws70url+HTTPS;
            }
            Thread.currentThread().setContextClassLoader(ws7Libloader);            
            DeploymentManager manager = new WS70SunDeploymentManager(ws70DF, ws70DF.getDeploymentManager(ws70url, uname,  passwd), 
                                                                        uri, uname, passwd);            
            if(connectedDepManagers!=null){
                connectedDepManagers.put(uri, manager);
            }
            return manager;        
        }catch(Exception e){
            e.printStackTrace();
            throw new DeploymentManagerCreationException(e.getMessage());            
        }finally{
            Thread.currentThread().setContextClassLoader(origClassLoader);
        }
    }
    
    /** This method returns a disconnected deployment manager.
     *
     * Should a disconnected deployment manager be able to become connected?
     *
     * @param uri
     * @throws DeploymentManagerCreationException
     * @return a deployment manager for doing configuration.
     */    
    public DeploymentManager getDisconnectedDeploymentManager(String uri)
        throws DeploymentManagerCreationException {    
        
        String location = WS70URIManager.getLocation(uri);
        // If we can't find the location of the WS70 libraries
        // We can not really use the actual deployment manager
        // provided by jsr-88 impl of the WS70        // 
        // it happens in getDisconnectedDeploymentManager case if
        // Web project's target server was removed and the IDE restarts
        
        if(location==null){
            return new WS70SunDeploymentManager(null, null, uri, null, null);            
        }
        WS7LibsClassLoader ws7Libloader = (WS7LibsClassLoader)ws7ClassLoaders.get(location);
        if(ws7Libloader == null){            
            ws7Libloader = (WS7LibsClassLoader)getWs7ClassLoader(location);
            ws7ClassLoaders.put(location, ws7Libloader);
        }
        if(ws70DF==null){
            try{
                this.createInnerFactory(ws7Libloader);
            }catch(Exception ex){            
                throw new DeploymentManagerCreationException(ex.getMessage());
            }
        }        
        
        ClassLoader origClassLoader=Thread.currentThread().getContextClassLoader();        
        try{
            String ws70url = WS70URIManager.getURIWithoutLocation(uri);
            InstanceProperties ip =  InstanceProperties.getInstanceProperties(uri);
            String isSSL = ip.getProperty(WS70ServerUIWizardIterator.PROP_SSL_PORT);
            if(Boolean.parseBoolean(isSSL)){
                ws70url=ws70url+HTTPS;
            }
            Thread.currentThread().setContextClassLoader(ws7Libloader);            
            DeploymentManager manager = new WS70SunDeploymentManager(ws70DF, ws70DF.getDisconnectedDeploymentManager(ws70url),
                                                                        uri, null, null);            
            return manager;        
        }catch(Exception e){
            e.printStackTrace();
            throw new DeploymentManagerCreationException(e.getMessage());            
        }finally{
            Thread.currentThread().setContextClassLoader(origClassLoader);
        }
    }
    
    /** Determines whether this URI is handled by the Deployment factory
     * Iniitally we need to test the prefix. If the factory will support 
     * multiple managers, we need to extend this test to catch those cases.
     *
     * The tests should also be extended to deteremine if the URI is
     * "complete" for this factory.  It has to have a machine name
     * (that can be resolved to an IP address) and a port. Whether the
     * server is "up" may be an open question.
     *
     * @param uri
     * @return boolean value
     */  
    public boolean handlesURI(String uri) {
        if (uri == null) {
            return false;
        }
        String location = WS70URIManager.getLocation(uri);
        if(location==null){
            return false;
        }
        WS7LibsClassLoader ws7Libloader = (WS7LibsClassLoader)ws7ClassLoaders.get(location);
        if(ws7Libloader == null){            
            ws7Libloader = (WS7LibsClassLoader)getWs7ClassLoader(location);
            ws7ClassLoaders.put(location, ws7Libloader);
        }
        if(ws70DF==null){
            try{
                this.createInnerFactory(ws7Libloader);
            }catch(Exception ex){            
                return false;
            }
        }                
        ClassLoader origClassLoader=Thread.currentThread().getContextClassLoader();        
        Thread.currentThread().setContextClassLoader(ws7Libloader);
        boolean ret = ws70DF.handlesURI(WS70URIManager.getURIWithoutLocation(uri)); 
        Thread.currentThread().setContextClassLoader(origClassLoader);
        return ret;        
    }
    
    public String getDisplayName() {
        return NbBundle.getMessage(WS70SunDeploymentFactory.class, "LBL_WS70SunDeploymentFactory");
    }
    
    public String getProductVersion() {
        return NbBundle.getMessage(WS70SunDeploymentFactory.class, "LBL_WS70SunDeploymentFactoryVersion");
    }
    private static ClassLoader getWs7ClassLoader(String location){
        
        WS7LibsClassLoader ws7Loader = null;               
        try{
            String libsLocation = location+"/lib"; //NOI18N            
            //ws7Loader = new WS7LibsClassLoader(Thread.currentThread().getContextClassLoader());
            ClassLoader moduleClassLoader = new Dummy().getClass().getClassLoader();
            ErrorManager.getDefault().log(ErrorManager.USER, "WS70 moduleclassloader is "+moduleClassLoader.toString());
            ws7Loader = new WS7LibsClassLoader(moduleClassLoader);
            File f = null;
            f = new File(libsLocation+"/s1as-jsr160-server.jar");//NO I118N
            ws7Loader.addURL(f);

            f = new File(libsLocation+"/sun-ws-jsr88-dm.jar");//NO I118N
            ws7Loader.addURL(f);            
            f = new File(libsLocation+"/s1as-jsr160-client.jar");//NO I118N
            ws7Loader.addURL(f);          

            f = new File(libsLocation+"/jmxremote_optional.jar"); //NO I118N            
            ws7Loader.addURL(f);
            
            f = new File(libsLocation+"/webserv-admin.jar"); //NO I118N
            ws7Loader.addURL(f);

            f = new File(libsLocation+"/webserv-admin-shared.jar"); //NO I118N
            ws7Loader.addURL(f);
            
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return ws7Loader;
    }
    private void createInnerFactory(WS7LibsClassLoader loader) throws Exception{
        try{
            Object factory = loader.loadClass("com.sun.web.admin.deployapi.SunDeploymentFactory").newInstance();
            ws70DF = (DeploymentFactory)factory;            
        }catch(Exception ex){
            ex.printStackTrace();
            throw ex;
        }        
    }
    public static WS70SunDeploymentManager getConnectedCachedDeploymentManager(String uri){
        WS70SunDeploymentManager dm =  (WS70SunDeploymentManager)connectedDepManagers.get(uri);        
        return dm;
    }
    public static ClassLoader getLibClassLoader(String location){
        return (ClassLoader)ws7ClassLoaders.get(location);
    }
    // Dummy class for WS70Deployment Factory to get classloader of this class
    public static class Dummy{
        
    }
                
}
