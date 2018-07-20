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

package org.netbeans.modules.j2ee.oc4j;

import javax.enterprise.deploy.spi.DeploymentManager;
import javax.enterprise.deploy.spi.exceptions.DeploymentManagerCreationException;
import javax.enterprise.deploy.spi.factories.DeploymentFactory;
import org.openide.util.NbBundle;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.modules.j2ee.deployment.plugins.api.InstanceProperties;
import org.netbeans.modules.j2ee.oc4j.util.OC4JDebug;
import org.netbeans.modules.j2ee.oc4j.util.OC4JPluginProperties;
import org.openide.util.NbPreferences;

/**
 *
 * @author pblaha
 */
public class OC4JDeploymentFactory implements DeploymentFactory {
    
    /**
     *  oc4j URI prefix
     */
    public static final String URI_PREFIX = "deployer:oc4j"; // NOI18N
    
    /**
     * oc4j server root property
     */
    public static final String PROP_SERVER_ROOT = "oc4j_server_root"; // NOI18N
    
    private static OC4JDeploymentFactory instance;
    
    private HashMap<String, DeploymentFactory> factories = new HashMap<String, DeploymentFactory>();
    private HashMap<String, DeploymentManager> managers = new HashMap<String, DeploymentManager>();
    
    /**
     * Returns default instance of OC4JDeploymentFactory
     *
     * @return DeploymentFactory
     */
    public static synchronized DeploymentFactory getDefault() {
        if (instance == null)
            instance = new OC4JDeploymentFactory();
        
        return instance;
    }
    
    /**
     *
     * @param uri
     * @return
     */
    public boolean handlesURI(String uri) {
        return uri != null && uri.startsWith(URI_PREFIX);
    }
    
    /**
     *
     * @param uri
     * @param uname
     * @param passwd
     * @return
     * @throws javax.enterprise.deploy.spi.exceptions.DeploymentManagerCreationException
     */
    public DeploymentManager getDeploymentManager(String uri, String uname, String passwd) throws DeploymentManagerCreationException {
        if (!handlesURI(uri))
            throw new DeploymentManagerCreationException("Invalid URI:" + uri); // NOI18N
        
        DeploymentManager manager = managers.get(uri);
        
        if (null == manager) {
            manager = new OC4JDeploymentManager(uri);
            managers.put(uri, manager);
        }
        
        return manager;
    }
    
    /**
     *
     * @param uri
     * @return
     * @throws javax.enterprise.deploy.spi.exceptions.DeploymentManagerCreationException
     */
    public DeploymentManager getDisconnectedDeploymentManager(String uri) throws DeploymentManagerCreationException {
        return getDeploymentManager(uri, null, null);
    }
    
    public DeploymentFactory getOC4JDeploymentFactory(String uri) {
        if (OC4JDebug.isEnabled())
            System.out.println("loadDeploymentFactory");
        
        DeploymentFactory factory = factories.get(uri);
        
        if (null == factory) {
            InstanceProperties ip = InstanceProperties.getInstanceProperties(uri);
            
            String serverRoot = null;
            
            if (null != ip)
                serverRoot = ip.getProperty(OC4JPluginProperties.PROPERTY_OC4J_HOME);
            
            if (null == serverRoot)
                serverRoot = NbPreferences.forModule(OC4JDeploymentFactory.class).get(PROP_SERVER_ROOT, "");
            
            if (OC4JDebug.isEnabled())
                System.out.println("loadDeplomentFactory: serverRoot=" + serverRoot);

            OC4JClassLoader.getInstance(serverRoot).updateLoader();
            
            try {
                factory = (DeploymentFactory) OC4JClassLoader.getInstance(serverRoot).loadClass(
                        "oracle.oc4j.admin.deploy.spi.factories.Oc4jDeploymentFactory"). // NOI18N
                        newInstance();
            } catch (ClassNotFoundException e) {
                Logger.getLogger("global").log(Level.SEVERE, null, e);
            } catch (InstantiationException e) {
                Logger.getLogger("global").log(Level.SEVERE, null, e);
            } catch (IllegalAccessException e) {
                Logger.getLogger("global").log(Level.SEVERE, null, e);
            } finally {
                OC4JClassLoader.getInstance(serverRoot).restoreLoader();
            }
            
            factories.put(uri, factory);
        }
        
        return factory;
    }
    
    /**
     *
     * @return
     */
    public String getProductVersion() {
        return "0.2"; // NOI18N
    }
    
    /**
     *
     * @return
     */
    public String getDisplayName() {
        return NbBundle.getMessage(OC4JDeploymentFactory.class, "TXT_DisplayName"); // NOI18N
    }
}