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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.j2ee.oc4j.config;

import java.io.File;
import java.util.Set;
import org.netbeans.modules.j2ee.deployment.common.api.ConfigurationException;
import org.netbeans.modules.j2ee.deployment.common.api.Datasource;
import org.netbeans.modules.j2ee.deployment.common.api.DatasourceAlreadyExistsException;
import org.netbeans.modules.j2ee.deployment.devmodules.api.J2eeModule;
import org.netbeans.modules.j2ee.deployment.plugins.spi.config.DatasourceConfiguration;
import org.netbeans.modules.j2ee.deployment.plugins.spi.config.ModuleConfiguration;
import org.netbeans.modules.j2ee.oc4j.config.ds.OC4JDatasourceSupport;
import org.openide.loaders.DataObject;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;

/**
 * Server specific configuration data related to OC4J J2EE server
 *
 * @author Michal Mocnak
 */
public class OC4JModuleConfiguration implements ModuleConfiguration, DatasourceConfiguration {
    
    // j2ee module object
    private J2eeModule j2eeModule;
    
    // cached data object for the server-specific configuration file (initialized by the subclasses)
    protected DataObject deploymentDescriptorDO;
    
    // the directory with resources - supplied by the configuration support in the construction time
    private File resourceDir;
    
    //support for data sources
    private OC4JDatasourceSupport dsSupport;
    
    public OC4JModuleConfiguration(J2eeModule j2eeModule) {
        this.j2eeModule = j2eeModule;
        this.resourceDir = j2eeModule.getResourceDirectory();
    }
    
    // ModuleConfiguration Implementation
    
    public Lookup getLookup() {
        return Lookups.fixed(this);
    }
    
    public J2eeModule getJ2eeModule() {
        return j2eeModule;
    }
    
    public void dispose() {}
    
    // DatasourceConfiguration Implementation
    
    public Set<Datasource> getDatasources() throws ConfigurationException {
        return getDatasourceSupport().getDatasources();
    }
    
    public boolean supportsCreateDatasource() {
        return true;
    }
    
    public Datasource createDatasource(String jndiName, String url, String username, String password, String driver) throws UnsupportedOperationException, ConfigurationException, DatasourceAlreadyExistsException {
        return getDatasourceSupport().createDatasource(jndiName, url, username, password, driver);
    }
    
    public void bindDatasourceReference(String referenceName, String jndiName) throws ConfigurationException {
        // TO DO
    }
    
    public void bindDatasourceReferenceForEjb(String ejbName, String ejbType, String referenceName, String jndiName) throws ConfigurationException {
        // TO DO
    }
    
    public String findDatasourceJndiName(String referenceName) throws ConfigurationException {
        
        // TO DO
        
        return null;
    }
    
    public String findDatasourceJndiNameForEjb(String ejbName, String referenceName) throws ConfigurationException {
        
        // TO DO
        
        return null;
    }
    
    // Helper methods
    
    private OC4JDatasourceSupport getDatasourceSupport() {
        if (dsSupport == null) {
            dsSupport = new OC4JDatasourceSupport(resourceDir);
        }
        return dsSupport;
    }
}