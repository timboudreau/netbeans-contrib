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

package org.netbeans.modules.j2ee.oc4j.config.ds;

import org.netbeans.modules.j2ee.oc4j.config.ds.OC4JDatasource;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.deploy.spi.DeploymentManager;
import javax.management.MBeanServerConnection;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import org.netbeans.modules.j2ee.deployment.common.api.ConfigurationException;
import org.netbeans.modules.j2ee.deployment.common.api.Datasource;
import org.netbeans.modules.j2ee.deployment.common.api.DatasourceAlreadyExistsException;
import org.netbeans.modules.j2ee.deployment.plugins.spi.DatasourceManager;
import org.netbeans.modules.j2ee.oc4j.OC4JDeploymentManager;
import org.netbeans.modules.j2ee.oc4j.config.gen.ConnectionPool;
import org.netbeans.modules.j2ee.oc4j.config.gen.DataSources;
import org.netbeans.modules.j2ee.oc4j.config.gen.ManagedDataSource;
import org.netbeans.modules.j2ee.oc4j.config.gen.NativeDataSource;
import org.netbeans.modules.j2ee.oc4j.ide.OC4JErrorManager;
import org.netbeans.modules.j2ee.oc4j.util.OC4JPluginUtils;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbBundle;

/**
 *
 * @author Michal Mocnak
 */
public class OC4JDatasourceManager implements DatasourceManager {
    
    private static final String DATA_SOURCES_XML = "data-sources.xml"; // NOI18N
    private static final String CONFIG_DIR = File.separator + "j2ee" + File.separator +
            "home" + File.separator + "config"; // NOI18N
    
    // server's deployent manager
    private OC4JDeploymentManager dm;
    
    // server's deploy dir
    private FileObject serverDir;
    
    /** Creates a new instance of OC4JDatasourceManager */
    public OC4JDatasourceManager(DeploymentManager dm) {
        if (!(dm instanceof OC4JDeploymentManager)) {
            throw new IllegalArgumentException("Only OC4JDeplomentManager is supported"); //NOI18N
        }
        
        this.dm = (OC4JDeploymentManager) dm;
        
        serverDir = FileUtil.toFileObject(new File(this.dm.getProperties().getOC4JHomeLocation()));
    }
    
    public Set<Datasource> getDatasources() {
        if(OC4JPluginUtils.isLocalServer(dm.getInstanceProperties()))
            return getLocalDatasources();
        
        return getRemoteDatasources();
    }
        
    public void deployDatasources(Set<Datasource> datasources) throws ConfigurationException,
            DatasourceAlreadyExistsException {
        // Get deployed datasources
        Set<Datasource> deployedDS = getDatasources();
        
        for (Datasource d : datasources) {
            if(deployedDS.contains(d))
                continue;
            
            OC4JDatasource ds = (OC4JDatasource) d;
            
            try {
                String cpName = ds.getJndiName() + " Connection Pool";
                String cpClazz = ds.getDriverClassName();
                String cpUsername = ds.getUsername();
                String cpPassword = ds.getPassword();
                String cpUrl = ds.getUrl();
                
                // Connection pool deployment
                deployConnectionPool(cpName, cpClazz, cpUsername, cpPassword, cpUrl);
                
                String mdsJndiName = ds.getJndiName();
                Integer mdsIdleTimeout = new Integer(ds.getIdleTimeoutMinutes());
                
                // Managed data source deployment
                deployManagedDataSource(mdsJndiName, cpName, mdsIdleTimeout);
                
            } catch(Exception e) {
                OC4JErrorManager.getInstance(dm).error(dm.getUri(), e, OC4JErrorManager.GENERIC_FAILURE);
            }
        }
    }
    
    /**
     * Deploy a connection pool into the server
     *
     * @param name connetion pool name
     * @param clazz driver class name
     * @param username username to access the database
     * @param password password to access the database
     * @param url connection url
     */
    public void deployConnectionPool(String name, String clazz, String username, String password, String url) {
        if (null == name || null == clazz || null == username ||
                null == password || null == url)
            throw new NullPointerException();
        
        try {
            // Checks if the driver exists in the server
            if (OC4JPluginUtils.isLocalServer(dm.getInstanceProperties()) &&
                    !OC4JPluginUtils.checkClass(clazz, dm))
                return;
            
            // Creating environment
            MBeanServerConnection server = dm.getJMXConnector();
            
            // Checks if the connection pool exists in the server
            Iterator i = server.queryMBeans(new ObjectName("oc4j:j2eeType=JDBCResource,*"), null).iterator();
            
            while(i.hasNext()) {
                ObjectName elem = ((ObjectInstance) i.next()).getObjectName();
                String s = elem.getKeyProperty("name").substring(1, elem.getKeyProperty("name").length()-1);
                
                if (name.equals(s))
                    return;
            }
            
            Iterator j = server.queryMBeans(new ObjectName("oc4j:j2eeType=J2EEApplication,name=default,J2EEServer=standalone"), null).iterator();
            ObjectName elem = ((ObjectInstance) j.next()).getObjectName();
            
            // Connection pool deployment
            server.invoke(elem, "createJDBCConnectionPool", new Object[] {
                name, clazz, username, password, url}, new String[] {
                "java.lang.String",
                "java.lang.String",
                "java.lang.String",
                "java.lang.String",
                "java.lang.String"});
        } catch (Exception e) {
            OC4JErrorManager.getInstance(dm).error(dm.getUri(), e, OC4JErrorManager.GENERIC_FAILURE);
        }
    }
    
    /**
     * Deploy a managed data source into the server
     *
     * @param jndiName jndi name of the resourse
     * @param connectionPoolName connection pool name on which it will be mapped
     * @param idleTimeout timeout value
     */
    public void deployManagedDataSource(String jndiName, String connectionPoolName, Integer idleTimeout) {
        if (null == jndiName || null == connectionPoolName || null == idleTimeout)
            throw new NullPointerException();
        
        try {
            // Creating environment
            MBeanServerConnection server = dm.getJMXConnector();
            
            // Checks if the Managed Data Source exists in the server
            Iterator i = server.queryMBeans(new ObjectName("oc4j:j2eeType=JDBCDataSource,*"), null).iterator();
            
            while(i.hasNext()) {
                ObjectName elem = ((ObjectInstance) i.next()).getObjectName();
                String name = elem.getKeyProperty("name").substring(1, elem.getKeyProperty("name").length()-1);
                String pool = elem.getKeyProperty("JDBCResource").substring(1, elem.getKeyProperty("JDBCResource").length()-1);
                
                if(pool.length() == 0)
                    continue;
                
                String s = (String) server.getAttribute(elem, "jndiName");
                
                if (jndiName.equals(s) || jndiName.equals(name))
                    return;
            }
            
            // Checks if the mapped connection pool exists in the server
            Iterator j = server.queryMBeans(new ObjectName("oc4j:j2eeType=JDBCResource,name=\"" + connectionPoolName + "\",*"), null).iterator();
            
            if (!j.hasNext())
                return;
            
            Iterator k = server.queryMBeans(new ObjectName("oc4j:j2eeType=J2EEApplication,name=default,J2EEServer=standalone"), null).iterator();
            ObjectName elem = ((ObjectInstance) k.next()).getObjectName();
            
            // Managed data source deployment
            server.invoke(elem, "createManagedDataSource", new Object[] {
                jndiName, "", "", jndiName, idleTimeout, connectionPoolName, "",
                ""}, new String[] {
                "java.lang.String",
                "java.lang.String",
                "java.lang.String",
                "java.lang.String",
                "java.lang.Integer",
                "java.lang.String",
                "java.lang.String",
                "java.lang.String",});
        } catch(Exception e) {
            OC4JErrorManager.getInstance(dm).error(dm.getUri(), e, OC4JErrorManager.GENERIC_FAILURE);
        }
    }
    
    /**
     * Undeploy a native data source from the server
     *
     * @param name native data source name
     */
    public void undeployNativeDataSource(String name) {
        if (null == name)
            throw new NullPointerException();
        
        try {
            // Creating environment
            MBeanServerConnection server = dm.getJMXConnector();
            
            Iterator i = server.queryMBeans(new ObjectName("oc4j:j2eeType=J2EEApplication,name=default,J2EEServer=standalone"), null).iterator();
            ObjectName elem = ((ObjectInstance) i.next()).getObjectName();
            
            // Native Data Source undeployemnt
            server.invoke(elem, "removeNativeDataSource", new Object[] {
                name,}, new String[] {"java.lang.String"});
        } catch (Exception e) {
            OC4JErrorManager.getInstance(dm).error(dm.getUri(), e, OC4JErrorManager.GENERIC_FAILURE);
        }
    }
    
    public void undeployManagedDataSource(String name) {
        if (null == name)
            throw new NullPointerException();
        
        try {
            // Creating environment
            MBeanServerConnection server = dm.getJMXConnector();
            
            Iterator i = server.queryMBeans(new ObjectName("oc4j:j2eeType=J2EEApplication,name=default,J2EEServer=standalone"), null).iterator();
            ObjectName elem = ((ObjectInstance) i.next()).getObjectName();
            
            // Managed Data Source undeployemnt
            server.invoke(elem, "removeManagedDataSource", new Object[] {
                name,}, new String[] {"java.lang.String"});
        } catch (Exception e) {
            OC4JErrorManager.getInstance(dm).error(dm.getUri(), e, OC4JErrorManager.GENERIC_FAILURE);
        }
    }
    
    public void undeployConnectionPool(String name) {
        if (null == name)
            throw new NullPointerException();
        
        try {
            // Creating environment
            MBeanServerConnection server = dm.getJMXConnector();
            
            Iterator i = server.queryMBeans(new ObjectName("oc4j:j2eeType=J2EEApplication,name=default,J2EEServer=standalone"), null).iterator();
            ObjectName elem = ((ObjectInstance) i.next()).getObjectName();
            
            // Undeploying asociated managed data sources
            Iterator j = server.queryMBeans(new ObjectName("oc4j:j2eeType=JDBCResource,name=\"" + name + "\",*"), null).iterator();
            ObjectName pool = ((ObjectInstance) j.next()).getObjectName();
            String[] array = (String[]) server.getAttribute(pool, "jdbcDataSources");
            
            for (String s : array) {
                String dsName = new ObjectName(s).getKeyProperty("name");
                
                // remove quotes
                dsName = dsName.substring(1, dsName.length() - 1);
                
                // undeploy managed data source
                undeployManagedDataSource(dsName);
            }
            
            // Connection pool undeployemnt
            server.invoke(elem, "removeDataSourceConnectionPool", new Object[] {
                name,}, new String[] {"java.lang.String"});
        } catch (Exception e) {
            OC4JErrorManager.getInstance(dm).error(dm.getUri(), e, OC4JErrorManager.GENERIC_FAILURE);
        }
    }
    
    private Set<Datasource> getLocalDatasources() {
        
        Set<Datasource> globalDS = new HashSet<Datasource>();
        
        if (serverDir == null || !serverDir.isValid() || !serverDir.isFolder() || !serverDir.canRead()) {
            Logger.getLogger("global").log(Level.WARNING,  NbBundle.getMessage(OC4JDatasourceManager.class, "ERR_WRONG_DEPLOY_DIR"));
            return globalDS;
        }
        
        File dsFile = new File(FileUtil.toFile(serverDir), CONFIG_DIR + File.separator + DATA_SOURCES_XML);
        
        if(dsFile.exists()) {
            try {
                DataSources datasources = DataSources.createGraph(dsFile);
                
                NativeDataSource nds[] = datasources.getNativeDataSource();
                for (NativeDataSource ds : nds) {
                    if (ds.getJndiName().length() > 0) {
                        globalDS.add(new OC4JDatasource(
                                ds.getJndiName(),
                                ds.getUrl(),
                                ds.getUser(),
                                ds.getPassword(),
                                ds.getDataSourceClass()));
                    }
                }
                
                ManagedDataSource mds[] = datasources.getManagedDataSource();
                for (ManagedDataSource ds : mds) {
                    if (ds.getJndiName().length() > 0) {
                        String cpName = ds.getConnectionPoolName();
                        ConnectionPool[] cps = datasources.getConnectionPool();
                        for (ConnectionPool cp : cps) {
                            if(cpName.equals(cp.getName())) {
                                globalDS.add(new OC4JDatasource(
                                        ds.getJndiName(),
                                        cp.getConnectionFactory().getUrl(),
                                        cp.getConnectionFactory().getUser(),
                                        cp.getConnectionFactory().getPassword(),
                                        cp.getConnectionFactory().getFactoryClass()));
                            }
                        }
                    }
                }
            } catch(IOException ex) {
                Logger.getLogger("global").log(Level.INFO, ex.getMessage());
            }
        }
        
        return globalDS;
    }
    
    private Set<Datasource> getRemoteDatasources() {
        Set<Datasource> datasources = new HashSet<Datasource>();
        
        MBeanServerConnection serverConnection = dm.getJMXConnector();
        
        try {
            Iterator i = serverConnection.queryMBeans(new ObjectName("oc4j:j2eeType=JDBCDataSource,*"), null).iterator();
            while(i.hasNext()) {
                ObjectName dsElem = ((ObjectInstance) i.next()).getObjectName();
                
                String jndiName = (String) serverConnection.getAttribute(dsElem, "jndiName");
                String pool = dsElem.getKeyProperty("JDBCResource").substring(1, dsElem.getKeyProperty("JDBCResource").length()-1);
                String username = (String) serverConnection.getAttribute(dsElem, "user");
                String password = (String) serverConnection.getAttribute(dsElem, "password");
                
                Iterator j = serverConnection.queryMBeans(new ObjectName("oc4j:j2eeType=JDBCResource,name=\""+pool+"\",*"), null).iterator();
                
                if(!j.hasNext())
                    continue;
                
                ObjectName poolElem = ((ObjectInstance) j.next()).getObjectName();
                
                String url = (String) serverConnection.getAttribute(poolElem, "url");
                String factoryClassName = (String) serverConnection.getAttribute(poolElem, "factoryClass");
                
                Datasource ds = new OC4JDatasource(jndiName, url, username, password, factoryClassName);
                
                datasources.add(ds);
            }
        } catch(Exception ex) {
            // Nothing to do
        }
        
        return datasources;
    }
}