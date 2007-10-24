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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;
import org.netbeans.modules.j2ee.deployment.common.api.ConfigurationException;
import org.netbeans.modules.j2ee.deployment.common.api.Datasource;
import org.netbeans.modules.j2ee.deployment.common.api.DatasourceAlreadyExistsException;
import org.netbeans.modules.j2ee.oc4j.config.OC4JModuleConfiguration;
import org.netbeans.modules.j2ee.oc4j.config.OC4JResourceConfigurationHelper;
import org.netbeans.modules.j2ee.oc4j.config.gen.ConnectionFactory;
import org.netbeans.modules.j2ee.oc4j.config.gen.ConnectionPool;
import org.netbeans.modules.j2ee.oc4j.config.gen.DataSources;
import org.netbeans.modules.j2ee.oc4j.config.gen.ManagedDataSource;
import org.netbeans.modules.j2ee.oc4j.config.gen.NativeDataSource;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.cookies.EditorCookie;
import org.openide.cookies.SaveCookie;
import org.openide.filesystems.FileChangeAdapter;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 * Support class for Datasource management
 * 
 * @author Michal Mocnak
 */
public class OC4JDatasourceSupport {
    
    private static final String DS_RESOURCE_NAME = "data-sources.xml"; // NOI18N
    
    // the directory with resources - supplied by the configuration support in the construction time
    private File resourceDir;
    
    //model of the data source file
    private DataSources datasources;
    
    // data source file (placed in the resourceDir)
    private File datasourcesFile;
    
    //destination service file object
    private FileObject datasourcesFO;
    
    public OC4JDatasourceSupport(File resourceDir) {
        this.resourceDir = resourceDir;
        
        datasourcesFile = new File(resourceDir, DS_RESOURCE_NAME);
        
        if (datasourcesFile.exists()) {
            try {
                ensureDatasourcesFOExists();
            } catch (DataObjectNotFoundException donfe) {
                Exceptions.printStackTrace(donfe);
            }
        }
    }
    
    public Set<Datasource> getDatasources() throws ConfigurationException {
        HashSet<Datasource> projectDS = new HashSet<Datasource>();
        DataSources dss = getDatasourcesGraph();
        if (dss != null) {
            NativeDataSource nds[] = datasources.getNativeDataSource();
            for (NativeDataSource ds : nds) {
                if (ds.getJndiName().length() > 0) {
                    projectDS.add(new OC4JDatasource(
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
                            projectDS.add(new OC4JDatasource(
                                    ds.getJndiName(),
                                    cp.getConnectionFactory().getUrl(),
                                    cp.getConnectionFactory().getUser(),
                                    cp.getConnectionFactory().getPassword(),
                                    cp.getConnectionFactory().getFactoryClass()));
                        }
                    }
                }
            }
        }
        
        return projectDS;
    }
    
    public Datasource createDatasource(String jndiName, String url, String username, String password, String driver) throws UnsupportedOperationException, ConfigurationException, DatasourceAlreadyExistsException {
        OC4JDatasource ds = modifyDSResource(new DSResourceModifier(jndiName, url, username, password, driver) {
            OC4JDatasource modify(DataSources datasources) throws DatasourceAlreadyExistsException {
                
                ManagedDataSource mds[] = datasources.getManagedDataSource();
                for (ManagedDataSource ds : mds) {
                    String jndiName = ds.getJndiName();
                    if (this.jndiName.equals(jndiName)) {
                        //already exists
                        OC4JDatasource eDs = null;
                        String cpName = ds.getConnectionPoolName();
                        ConnectionPool[] cps = datasources.getConnectionPool();
                        for (ConnectionPool cp : cps) {
                            if(cpName.equals(cp.getName())) {
                                eDs = new OC4JDatasource(
                                        ds.getJndiName(),
                                        cp.getConnectionFactory().getUrl(),
                                        cp.getConnectionFactory().getUser(),
                                        cp.getConnectionFactory().getPassword(),
                                        cp.getConnectionFactory().getFactoryClass());
                            }
                        }
                        
                        throw new DatasourceAlreadyExistsException(eDs);
                    }
                }
                
                ManagedDataSource ds = new ManagedDataSource();
                ConnectionPool cp = new ConnectionPool();
                ConnectionFactory cf = new ConnectionFactory();
                String cpName = jndiName + " Connection Pool";
                
                // Connection Factory Init
                cf.setFactoryClass(driver);
                cf.setUser(username);
                cf.setPassword(password);
                cf.setUrl(url);
                
                // Connection Pool Init
                cp.setName(cpName);
                cp.setConnectionFactory(cf);
                
                // Managed Data Source Init
                ds.setName(jndiName);
                ds.setConnectionPoolName(cpName);
                ds.setJndiName(jndiName);
                
                datasources.addManagedDataSource(ds);
                datasources.addConnectionPool(cp);
                
                return new OC4JDatasource(jndiName, url, username, password, driver);
            }
        });
        
        return ds;
    }
    
    // Helper methods
    
    /**
     * Perform datasources graph changes defined by the jbossWeb modifier. Update editor
     * content and save changes, if appropriate.
     *
     * @param modifier
     */
    private OC4JDatasource modifyDSResource(DSResourceModifier modifier)
            throws ConfigurationException, DatasourceAlreadyExistsException {
        
        OC4JDatasource ds = null;
        
        try {
            ensureResourceDirExists();
            ensureDatasourcesFilesExists();
            ensureDatasourcesFOExists();
            
            DataObject datasourcesDO = DataObject.find(datasourcesFO);
            
            EditorCookie editor = (EditorCookie)datasourcesDO.getCookie(EditorCookie.class);
            StyledDocument doc = editor.getDocument();
            if (doc == null)
                doc = editor.openDocument();
            
            DataSources newDatasources = null;
            try {  // get the up-to-date model
                // try to create a graph from the editor content
                byte[] docString = doc.getText(0, doc.getLength()).getBytes();
                newDatasources = DataSources.createGraph(new ByteArrayInputStream(docString));
            } catch (RuntimeException e) {
                DataSources oldDatasources = getDatasourcesGraph();
                if (oldDatasources == null) {
                    // neither the old graph is parseable, there is not much we can do here
                    // TODO: should we notify the user?
                    throw new ConfigurationException(
                            NbBundle.getMessage(OC4JModuleConfiguration.class, "MSG_datasourcesXmlCannotParse", DS_RESOURCE_NAME)); // NOI18N
                }
                // current editor content is not parseable, ask whether to override or not
                NotifyDescriptor notDesc = new NotifyDescriptor.Confirmation(
                        NbBundle.getMessage(OC4JModuleConfiguration.class, "MSG_datasourcesXmlNotValid", DS_RESOURCE_NAME),
                        NotifyDescriptor.OK_CANCEL_OPTION);
                Object result = DialogDisplayer.getDefault().notify(notDesc);
                if (result == NotifyDescriptor.CANCEL_OPTION) {
                    // keep the old content
                    return null;
                }
                // use the old graph
                newDatasources = oldDatasources;
            }
            
            // perform changes
            ds = modifier.modify(newDatasources);
            
            // save, if appropriate
            boolean modified = datasourcesDO.isModified();
            
            OC4JResourceConfigurationHelper.replaceDocument(doc, newDatasources);
            
            if (!modified) {
                SaveCookie cookie = (SaveCookie)datasourcesDO.getCookie(SaveCookie.class);
                cookie.save();
            }
            
            datasources = newDatasources;
            
        } catch(DataObjectNotFoundException donfe) {
            Exceptions.printStackTrace(donfe);
        } catch (BadLocationException ble) {
            throw new ConfigurationException(ble.getMessage(), ble);
        } catch (IOException ioe) {
            throw new ConfigurationException(ioe.getMessage(), ioe);
        }
        
        return ds;
    }
    
    private void ensureResourceDirExists() {
        if (!resourceDir.exists())
            resourceDir.mkdir();
    }
    
    private void ensureDatasourcesFilesExists() {
        if (!datasourcesFile.exists())
            getDatasourcesGraph();
    }
    
    /**
     * Return Datasources graph. If it was not created yet, load it from the file
     * and cache it. If the file does not exist, generate it.
     *
     * @return Datasources graph or null if the jboss-ds.xml file is not parseable.
     */
    private synchronized DataSources getDatasourcesGraph() {
        
        try {
            if (datasourcesFile.exists()) {
                // load configuration if already exists
                try {
                    if (datasources == null)
                        datasources = DataSources.createGraph(datasourcesFile);
                } catch (IOException ioe) {
                    Exceptions.printStackTrace(ioe);
                } catch (RuntimeException re) {
                    // jboss-ds.xml is not parseable, do nothing
                }
            } else {
                // create jboss-ds.xml if it does not exist yet
                datasources = new DataSources();
                
                OC4JResourceConfigurationHelper.writefile(datasourcesFile, datasources);
            }
        } catch (ConfigurationException ce) {
            Exceptions.printStackTrace(ce);
        }
        
        return datasources;
    }
    
    private void ensureDatasourcesFOExists() throws DataObjectNotFoundException {
        if (!datasourcesFile.exists())
            return;
        
        if (datasourcesFO == null || !datasourcesFO.isValid()) {
            datasourcesFO = FileUtil.toFileObject(datasourcesFile);
            assert(datasourcesFO != null);
            datasourcesFO.addFileChangeListener(new DatasourceFileListener());
        }
    }
    
    // Helper classes
    
    /**
     * Listener of data-sources.xml document changes.
     */
    private class DatasourceFileListener extends FileChangeAdapter {
        
        public void fileChanged(FileEvent fe) {
            assert(fe.getSource() == datasourcesFO);
            datasources = null;
        }
        
        public void fileDeleted(FileEvent fe) {
            assert(fe.getSource() == datasourcesFO);
            datasources = null;
        }
    }
    
    private abstract class DSResourceModifier {
        String jndiName;
        String url;
        String username;
        String password;
        String driver;
        
        DSResourceModifier(String jndiName, String  url, String username, String password, String driver) {
            this.jndiName = jndiName;
            this.url = url;
            this.username = username;
            this.password = password;
            this.driver = driver;
        }
        
        abstract OC4JDatasource modify(DataSources datasources) throws DatasourceAlreadyExistsException;
    }   
}