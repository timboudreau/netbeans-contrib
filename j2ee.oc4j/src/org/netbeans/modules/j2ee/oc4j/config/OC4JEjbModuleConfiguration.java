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
    
    public void bindEjbReference(String referenceName, String referencedEjbName) throws org.netbeans.modules.j2ee.deployment.common.api.ConfigurationException {
        // TO DO
    }
    
    public void bindEjbReferenceForEjb(String ejbName, String ejbType, String referenceName, String referencedEjbName) throws org.netbeans.modules.j2ee.deployment.common.api.ConfigurationException {
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
            orionEjbJar = null;
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