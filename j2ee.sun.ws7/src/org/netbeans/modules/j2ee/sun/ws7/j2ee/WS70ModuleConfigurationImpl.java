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

package org.netbeans.modules.j2ee.sun.ws7.j2ee;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;
import javax.enterprise.deploy.spi.DeploymentConfiguration;
import org.netbeans.modules.j2ee.deployment.common.api.ConfigurationException;
import org.netbeans.modules.j2ee.deployment.common.api.MessageDestination;
import org.netbeans.modules.j2ee.deployment.common.api.OriginalCMPMapping;
import org.netbeans.modules.j2ee.deployment.devmodules.api.J2eeModule;
import org.netbeans.modules.j2ee.deployment.plugins.spi.config.MessageDestinationConfiguration;
import org.netbeans.modules.j2ee.deployment.plugins.spi.config.ContextRootConfiguration;
import org.netbeans.modules.j2ee.deployment.plugins.spi.config.MappingConfiguration;
import org.netbeans.modules.j2ee.deployment.plugins.spi.config.ModuleConfiguration;
import org.netbeans.modules.j2ee.deployment.plugins.spi.config.DeploymentPlanConfiguration;
import org.netbeans.modules.j2ee.sun.share.configbean.EjbJarRoot;
import org.netbeans.modules.j2ee.sun.share.configbean.SunONEDeploymentConfiguration;
import org.openide.ErrorManager;
import org.openide.filesystems.FileUtil;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;

/** Implementation of ModuleConfiguration.
 *
 *  Primarily serves to delegate directly to the specified DeploymentConfiguration
 *  instance, as that is in shared code and has appropriate access and this instance
 *  is not.
 *
 */
public class WS70ModuleConfigurationImpl implements DeploymentPlanConfiguration,
        ContextRootConfiguration, MappingConfiguration, ModuleConfiguration, MessageDestinationConfiguration {
    
    private SunONEDeploymentConfiguration config;
    private J2eeModule module;
    private Lookup lookup;
    private File sunWebDescriptor;
    
    WS70ModuleConfigurationImpl(J2eeModule module) throws ConfigurationException {
        this.module = module;
        this.config = new SunONEDeploymentConfiguration(module);
        Object type = module.getModuleType();        
        
        if (module.WAR.equals(type)) {
            sunWebDescriptor = module.getDeploymentConfigurationFile("sun-web.xml");
        }
        
        try {
            File dds[] = new File[] { sunWebDescriptor };
            config.init(dds, module.getResourceDirectory(), true);
        } catch (javax.enterprise.deploy.spi.exceptions.ConfigurationException ex) {
            throw new ConfigurationException("", ex);
        }
    }
    
    public J2eeModule getJ2eeModule() {
        return module;
    }
    
    public synchronized Lookup getLookup() {
        if (lookup == null) {
            lookup = Lookups.fixed(this);
        }
        return lookup;
    }

        
    /** Called by j2eeserver to allow us to cleanup the deployment configuration object
     *  for this J2EE project.
     */
    public void dispose() {
        checkConfiguration(config);
        ((SunONEDeploymentConfiguration)config).dispose();
    }
    
    
    /** Conduit to pass the cmp mapping information directly to the configuration
     *  backend.
     */
    public void setMappingInfo(OriginalCMPMapping[] mapping){
        checkConfiguration(config);
        SunONEDeploymentConfiguration s1config = (SunONEDeploymentConfiguration) config;
        EjbJarRoot ejbJarRoot = s1config.getEjbJarRoot();
        if (ejbJarRoot != null) {
            ejbJarRoot.mapCmpBeans(mapping);
        }
    }


    /** Retrieves the context root field from sun-web.xml for this module, if the module is a
     *  web application.  Otherwise, returns null.
     */
    public String getContextRoot() {
        checkConfiguration(config);
        return ((SunONEDeploymentConfiguration)config).getContextRoot();
    }

    
    /** Sets the context root field in sun-web.xml for this module.
     */
    public void setContextRoot(String contextRoot) {
        checkConfiguration(config);
        ((SunONEDeploymentConfiguration)config).setContextRoot(contextRoot);
    }    
    
    
    /** Utility method to validate the configuration object being passed to the
     *  other methods in this class.
     */
    private void checkConfiguration(DeploymentConfiguration config) {
        if (config == null) {
            throw new IllegalArgumentException("DeploymentConfiguration is null");
        }
        if (!(config instanceof SunONEDeploymentConfiguration)) {
            throw new IllegalArgumentException("Wrong DeploymentConfiguration instance " + config.getClass().getName());
        }
    }
    
    /**
     * Write the deployment plan file to the specified output stream.
     * 
     * @param outputStream the deployment plan file should be written to.
     * @throws ConfigurationException if an error
     */
    public void save(OutputStream outputStream) throws ConfigurationException {
        FileInputStream is  = null;
        try {
            checkConfiguration(config);
            if (sunWebDescriptor != null) {
                is = new FileInputStream(sunWebDescriptor);
                FileUtil.copy(is, outputStream);
            }
        } catch (IOException ioe) {
            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, ioe);
            throw new ConfigurationException("Sun Deployment Descriptor Error", ioe); // NOI18N
        } finally {
            try {
                if (is != null)
                    is.close();
            } catch (IOException ignore) {}
        }
    }

    /****************************  MessageDestinationConfiguration ************************************/
    public Set<MessageDestination> getMessageDestinations() throws ConfigurationException {
        return getSunConfig().getMessageDestinations(); 
    }
    
    public boolean supportsCreateMessageDestination(){
        return true;
    }
    
    public MessageDestination createMessageDestination(String name, MessageDestination.Type type) throws UnsupportedOperationException, ConfigurationException {
        return getSunConfig().createMessageDestination(name, type);
    }
    
    public void bindMdbToMessageDestination(String mdbName, String name, MessageDestination.Type type) throws ConfigurationException {
        getSunConfig().bindMdbToMessageDestination(mdbName, name, type); 
    }
    
    public String findMessageDestinationName(String mdbName) throws ConfigurationException {
        return getSunConfig().findMessageDestinationName(mdbName); 
    }
    
    public void bindMessageDestinationReference(String referenceName, String connectionFactoryName, 
            String destName, MessageDestination.Type type) throws ConfigurationException {
        getSunConfig().bindMessageDestinationReference(referenceName, connectionFactoryName, 
            destName, type); 
    }
    
    public void bindMessageDestinationReferenceForEjb(String ejbName, String ejbType,
            String referenceName, String connectionFactoryName,
            String destName, MessageDestination.Type type) throws ConfigurationException {
        getSunConfig().bindMessageDestinationReferenceForEjb(ejbName, ejbType, referenceName, 
            connectionFactoryName, destName, type); 
    }
    
    private SunONEDeploymentConfiguration getSunConfig(){
        checkConfiguration(config);
        SunONEDeploymentConfiguration sunConfig = ((SunONEDeploymentConfiguration)config);
        return sunConfig;
    }
}   
