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
