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

package org.netbeans.modules.j2ee.oc4j.config;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import org.netbeans.modules.j2ee.deployment.common.api.ConfigurationException;
import org.netbeans.modules.j2ee.deployment.devmodules.api.J2eeModule;
import org.netbeans.modules.j2ee.deployment.plugins.spi.config.DeploymentPlanConfiguration;
import org.netbeans.modules.j2ee.oc4j.config.gen.OrionApplication;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.Exceptions;

/**
 * EAR application deployment configuration handles orion-application.xml configuration
 * file creation.
 *
 * @author Michal Mocnak
 */
public class OC4JEarModuleConfiguration extends OC4JModuleConfiguration
        implements DeploymentPlanConfiguration, PropertyChangeListener {
    
    // deployment descriptor file
    private File orionApplicationFile;
    
    // orion-application.xml object
    private OrionApplication orionApplication;
    
    /**
     * Creates a new instance of EarDeploymentConfiguration
     */
    public OC4JEarModuleConfiguration(J2eeModule j2eeModule) {
        super(j2eeModule);
        
        orionApplicationFile = j2eeModule.getDeploymentConfigurationFile("orion-application.xml");
        
        // Initializar orion-ejb.xml
        getOC4JApplication();
        
        if (deploymentDescriptorDO == null) {
            try {
                deploymentDescriptorDO = deploymentDescriptorDO.find(FileUtil.toFileObject(orionApplicationFile));
                deploymentDescriptorDO.addPropertyChangeListener(this);
            } catch(DataObjectNotFoundException donfe) {
                Exceptions.printStackTrace(donfe);
            }
        }
    }
    
    // DeploymentPlanConfiguration Implementation
    
    public void save(OutputStream os) throws ConfigurationException {
        OrionApplication orionApplication = getOC4JApplication();
        if (orionApplication == null) {
            throw new ConfigurationException("Cannot read configuration, it is probably in an inconsistent state."); // NOI18N
        }
        try {
            orionApplication.write(os);
        } catch (IOException ioe) {
            throw new ConfigurationException(ioe.getLocalizedMessage());
        }
    }
    
    // PropertyChangeListener Implementation
    
    /**
     * Listen to orion-ejb.xml document changes.
     */
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName() == DataObject.PROP_MODIFIED &&
                evt.getNewValue() == Boolean.FALSE) {
            // dataobject has been modified, orionWebApp graph is out of sync
            synchronized (this) {
                orionApplication = null;
            }
        }
    }
    
    // Helper methods
    
    /**
     * Return oc4jApplication graph. If it was not created yet, load it from the file
     * and cache it. If the file does not exist, generate it.
     *
     * @return weblogicApplication graph or null if the weblogic-application.xml file is not parseable.
     */
    private synchronized OrionApplication getOC4JApplication() {
        if (orionApplication == null) {
            try {
                if (orionApplicationFile.exists()) {
                    // load configuration if already exists
                    try {
                        orionApplication = orionApplication.createGraph(orionApplicationFile);
                    } catch (IOException ioe) {
                        Exceptions.printStackTrace(ioe);
                    } catch (RuntimeException re) {
                        // orion-application.xml is not parseable, do nothing
                    }
                } else {
                    // create orion-application.xml if it does not exist yet
                    orionApplication = genereateOC4JApplication();
                    OC4JResourceConfigurationHelper.writefile(orionApplicationFile, orionApplication);
                }
            } catch (ConfigurationException ce) {
                Exceptions.printStackTrace(ce);
            }
        }
        return orionApplication;
    }
    
    /**
     * Genereate Context graph.
     */
    private OrionApplication genereateOC4JApplication() {
        return new OrionApplication();
    }
}