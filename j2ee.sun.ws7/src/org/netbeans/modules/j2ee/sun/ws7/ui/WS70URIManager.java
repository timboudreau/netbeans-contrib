/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

/*
 * WS70URIManager.java
 *
 */

package org.netbeans.modules.j2ee.sun.ws7.ui;
import java.io.File;
import org.netbeans.modules.j2ee.deployment.plugins.api.InstanceCreationException;
import org.netbeans.modules.j2ee.deployment.plugins.api.InstanceProperties;



/**
 *
 * @author Administrator
 */
public class WS70URIManager {
    
    public static final String WS70SERVERURI = "deployer:Sun:WebServer70::"; //NOI18N
    
    /**
     * Returns instance properties for the server instance.
     *
     * @param url the url connection string to get the instance deployment manager.
     * @return the InstanceProperties object, null if instance does not exists.
     */
    
    public static InstanceProperties getInstanceProperties(String serverLocation, String host, int port){
        InstanceProperties  instanceProperties =
                InstanceProperties.getInstanceProperties("["+serverLocation+"]"+WS70SERVERURI+host+":"+port);

        return instanceProperties;
    }
    /**
     * Create new instance and returns instance properties for the server instance.
     * 
     * @param url the url connection string to get the instance deployment manager.
     * @param username username which is used by the deployment manager.
     * @param password password which is used by the deployment manager.
     * @param displayName display name which is used by IDE to represent this 
     *        server instance.
     * @return the <code>InstanceProperties</code> object, <code>null</code> if 
     *         instance does not exists.
     * @exception InstanceCreationException when instance with same url already 
     *            registered.
     */
    public static InstanceProperties createInstanceProperties(String serverLocation, String host, String port, String user, String password, String displayName) throws InstanceCreationException {
        InstanceProperties  instanceProperties =  InstanceProperties.createInstanceProperties(
                "["+serverLocation+"]"+WS70SERVERURI+host+":"+port,
                user,
                password,displayName);
        
        return instanceProperties;
    }
    public static String getURIWithoutLocation(String uriwithlocation){
        int index = uriwithlocation.lastIndexOf("]");
        String retval = null;
        if(index!=-1){
            retval =  uriwithlocation.substring(index+1);
        }else{
            retval = uriwithlocation;
        }
        return retval;
    }
    public static String getLocation(String url){
        int index = url.lastIndexOf("]");
        String retval = null;
        if(index!=-1){
            if(url.startsWith("[")){
                retval =  url.substring(1, index);
            }else{
                retval =  url.substring(0, index);
            }
        }else{
            retval = "";
        }
        return retval;        
    }
    public static String getHostFromURI(String uri){
        int index = uri.lastIndexOf("::");
        String newUrl = null;
        if(index!=-1){
            newUrl = uri.substring(index+2);
        }else{
            newUrl = uri;
        }
        
        String[] vals = newUrl.split(":");        
        return vals[0];
        
    }
    public static String getPortFromURI(String uri){
       int index = uri.lastIndexOf("::");
        String newUrl = null;
        if(index!=-1){
            newUrl = uri.substring(index+2);
        }else{
            newUrl = uri;
        }
        
        String[] vals = newUrl.split(":");      
        return vals[1];                
    }
    
}
