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
    private static HashMap connectedManagers = new HashMap();
    private static HashMap disconnectedManagers = new HashMap();
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
        DeploymentManager manager = (DeploymentManager)connectedManagers.get(uri);
        if (manager != null)
            return manager;

        String location = WS70URIManager.getLocation(uri);        
        // If we can't find the location of the WS70 libraries
        // We can not really use the actual deployment manager
        // provided by jsr-88 impl of the WS70
        // This wont likely happene for the getDeploymentManager case
        // it happens in getDisconnectedDeploymentManager case if
        // Web project's target server was removed and the IDE restarts
        if (location==null) {
            return new WS70SunDeploymentManager(null, null, uri, uname, passwd);            
        }

        String ws70url = WS70URIManager.getURIWithoutLocation(uri);
        InstanceProperties ip =  InstanceProperties.getInstanceProperties(uri);
        String isSSL = ip.getProperty(WS70ServerUIWizardIterator.PROP_SSL_PORT);
        if (Boolean.parseBoolean(isSSL)) {
            ws70url=ws70url+HTTPS;
        }

        ClassLoader loader = getWs7ClassLoader(location);
        ClassLoader origClassLoader=Thread.currentThread().getContextClassLoader();        
        Thread.currentThread().setContextClassLoader(loader);            
        try {
            DeploymentFactory ws70DF = createInnerFactory(loader);
            manager = new WS70SunDeploymentManager(ws70DF, ws70DF.getDeploymentManager(ws70url, uname,  passwd), 
                                                                        uri, uname, passwd);            
            connectedManagers.put(uri, manager);
            return manager;        
        } catch(Exception e) {
            e.printStackTrace();
            throw new DeploymentManagerCreationException(e.getMessage());            
        } finally {
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
        DeploymentManager manager = (DeploymentManager)disconnectedManagers.get(uri);
        if (manager != null)
            return manager;
        
        String location = WS70URIManager.getLocation(uri);
        // If we can't find the location of the WS70 libraries
        // We can not really use the actual deployment manager
        // provided by jsr-88 impl of the WS70        // 
        // it happens in getDisconnectedDeploymentManager case if
        // Web project's target server was removed and the IDE restarts
        
        if (location==null) {
            return new WS70SunDeploymentManager(null, null, uri, null, null);            
        }
        String ws70url = WS70URIManager.getURIWithoutLocation(uri);
        InstanceProperties ip =  InstanceProperties.getInstanceProperties(uri);
        String isSSL = ip.getProperty(WS70ServerUIWizardIterator.PROP_SSL_PORT);
        if (Boolean.parseBoolean(isSSL)) {
            ws70url=ws70url+HTTPS;
        }

        ClassLoader loader = getWs7ClassLoader(location);
        ClassLoader origClassLoader=Thread.currentThread().getContextClassLoader();        
        Thread.currentThread().setContextClassLoader(loader);
        try {
            DeploymentFactory ws70DF = createInnerFactory(loader);
            manager = new WS70SunDeploymentManager(ws70DF, ws70DF.getDisconnectedDeploymentManager(ws70url),
                                                                        uri, null, null);
            disconnectedManagers.put(uri, manager);
            return manager;        
        } catch(Exception e) {
            e.printStackTrace();
            throw new DeploymentManagerCreationException(e.getMessage());            
        } finally {
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
        DeploymentFactory ws70DF;
        try {
            ClassLoader loader = getWs7ClassLoader(location);
            ws70DF = createInnerFactory(loader);
        } catch(Exception ex) {            
            return false;
        }                
        boolean ret = ws70DF.handlesURI(WS70URIManager.getURIWithoutLocation(uri)); 
        return ret;        
    }
    
    public String getDisplayName() {
        return NbBundle.getMessage(WS70SunDeploymentFactory.class, "LBL_WS70SunDeploymentFactory");
    }
    
    public String getProductVersion() {
        return NbBundle.getMessage(WS70SunDeploymentFactory.class, "LBL_WS70SunDeploymentFactoryVersion");
    }
    private static ClassLoader getWs7ClassLoader(String location) {        
        WS7LibsClassLoader ws7Loader = null;               
        try{
            String libsLocation = location+"/lib"; //NOI18N
            ClassLoader moduleClassLoader = new Dummy().getClass().getClassLoader();
            ErrorManager.getDefault().log(ErrorManager.USER, "WS70 moduleclassloader is "+moduleClassLoader.toString());
            ws7Loader = new WS7LibsClassLoader(moduleClassLoader);
            File f = null;
            f = new File(libsLocation+"/sun-ws-jsr88-dm.jar");//NO I118N
            ws7Loader.addURL(f);            
            f = new File(libsLocation+"/s1as-jsr160-client.jar");//NO I118N
            ws7Loader.addURL(f);          
            f = new File(libsLocation+"/jmxremote_optional.jar"); //NO I118N            
            ws7Loader.addURL(f);            
            f = new File(libsLocation+"/webserv-admin-shared.jar"); //NO I118N
            ws7Loader.addURL(f);            
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return ws7Loader;
    }
    private DeploymentFactory createInnerFactory(ClassLoader loader) throws DeploymentManagerCreationException{
        DeploymentFactory factory = null;
        try{
            factory = (DeploymentFactory)loader.loadClass("com.sun.web.admin.deployapi.SunDeploymentFactory").newInstance();
        } catch(Exception ex) {
            ex.printStackTrace();
            throw new DeploymentManagerCreationException(ex.getMessage());
        }
        return factory;
    }

    public static WS70SunDeploymentManager getConnectedCachedDeploymentManager(String uri){
        WS70SunDeploymentManager dm =  (WS70SunDeploymentManager)connectedManagers.get(uri);        
        return dm;
    }

    // Dummy class for WS70Deployment Factory to get classloader of this class
    public static class Dummy{
    }
}
