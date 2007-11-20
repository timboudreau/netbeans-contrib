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
import org.netbeans.modules.j2ee.dd.api.common.ComponentInterface;
import org.netbeans.modules.j2ee.deployment.common.api.ConfigurationException;
import org.netbeans.modules.j2ee.deployment.devmodules.api.J2eeModule;
import org.netbeans.modules.j2ee.deployment.plugins.spi.config.DeploymentPlanConfiguration;
import org.netbeans.modules.j2ee.deployment.plugins.spi.config.EjbResourceConfiguration;
import org.netbeans.modules.j2ee.oc4j.config.gen.OrionEjbJar;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.Exceptions;

/**
 * EJB module deployment configuration handles orion-ejb-jar.xml configuration file creation.
 *
 * @author Michal Mocnak
 */
public class OC4JEjbModuleConfiguration extends OC4JModuleConfiguration
        implements EjbResourceConfiguration, DeploymentPlanConfiguration, PropertyChangeListener {
    
    // deployment descriptor file
    private File orionEjbJarFile;
    
    // orion-ejb.xml object
    private OrionEjbJar orionEjbJar;
    
    /**
     * Creates a new instance of EjbDeploymentConfiguration
     */
    public OC4JEjbModuleConfiguration(J2eeModule j2eeModule) {
        super(j2eeModule);
        
        orionEjbJarFile = j2eeModule.getDeploymentConfigurationFile("orion-ejb-jar.xml");
        
        // Initializar orion-ejb.xml
        getOC4JEjbJar();
        
        if (deploymentDescriptorDO == null) {
            try {
                deploymentDescriptorDO = deploymentDescriptorDO.find(FileUtil.toFileObject(orionEjbJarFile));
                deploymentDescriptorDO.addPropertyChangeListener(this);
            } catch(DataObjectNotFoundException donfe) {
                Exceptions.printStackTrace(donfe);
            }
        }
    }
    
    // EjbResourceConfiguration Implementation
    
    public void ensureResourceDefined(ComponentInterface ejb, String jndiName) throws org.netbeans.modules.j2ee.deployment.common.api.ConfigurationException {
        // TO DO
    }
    
    public String findJndiNameForEjb(String ejbName) throws ConfigurationException {
        // TODO
        return null;
    }
    
    public void bindEjbReference(String referenceName, String jndiName) throws org.netbeans.modules.j2ee.deployment.common.api.ConfigurationException {
        // TO DO
    }
    
    public void bindEjbReferenceForEjb(String ejbName, String ejbType, String referenceName, String jndiName) throws org.netbeans.modules.j2ee.deployment.common.api.ConfigurationException {
        // TO DO
    }
    
    // DeploymentConfigurationPlan Implementation
    
    public void save(OutputStream os) throws ConfigurationException {
        OrionEjbJar orionEjbJar = getOC4JEjbJar();
        if (orionEjbJar == null) {
            throw new ConfigurationException("Cannot read configuration, it is probably in an inconsistent state."); // NOI18N
        }
        try {
            orionEjbJar.write(os);
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
                orionEjbJar = null;
            }
        }
    }
    
    // Helper Methods
    
    /**
     * Return OrionEjbJar graph. If it was not created yet, load it from the file
     * and cache it. If the file does not exist, generate it.
     *
     * @return OrionEjbJar graph or null if the orion-ejb-jar.xml file is not parseable.
     */
    public synchronized OrionEjbJar getOC4JEjbJar() {
        if (orionEjbJar == null) {
            try {
                if (orionEjbJarFile.exists()) {
                    // load configuration if already exists
                    try {
                        orionEjbJar = orionEjbJar.createGraph(orionEjbJarFile);
                    } catch (IOException ioe) {
                        Exceptions.printStackTrace(ioe);
                    } catch (RuntimeException re) {
                        // orion-ejb-jar.xml is not parseable, do nothing
                    }
                } else {
                    // create orion-ejb-jar.xml if it does not exist yet
                    orionEjbJar = genereateOC4JEjbJar();
                    OC4JResourceConfigurationHelper.writefile(orionEjbJarFile, orionEjbJar);
                }
            } catch (ConfigurationException ce) {
                Exceptions.printStackTrace(ce);
            }
        }
        return orionEjbJar;
    }
    
    /**
     * Genereate OrionEjbJar graph.
     */
    private OrionEjbJar genereateOC4JEjbJar() {
        return new OrionEjbJar();
    }
}