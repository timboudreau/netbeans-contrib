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
     * 
     *  !PW FIXME This method is for ejb-jar's only.  Any reason why it even needs
     *  to be implemented in ws7 plugin?
     */
    public void setMappingInfo(OriginalCMPMapping[] mapping){
        throw new UnsupportedOperationException("Not supported.");
    }

    
    /** Called through j2eeserver when a new EJB resource may need to be added to the
     *  user's project.
     * 
     *  !PW FIXME This method is for ejb-jar's only.  Any reason why it even needs
     *  to be implemented in ws7 plugin?
     */
    public void setCMPResource(String ejbName, String jndiName) throws ConfigurationException {
        throw new UnsupportedOperationException("Not supported.");
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
