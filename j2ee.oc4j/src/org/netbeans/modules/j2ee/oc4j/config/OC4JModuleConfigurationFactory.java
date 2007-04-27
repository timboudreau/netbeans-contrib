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

import org.netbeans.modules.j2ee.deployment.common.api.ConfigurationException;
import org.netbeans.modules.j2ee.deployment.devmodules.api.J2eeModule;
import org.netbeans.modules.j2ee.deployment.plugins.spi.config.ModuleConfiguration;
import org.netbeans.modules.j2ee.deployment.plugins.spi.config.ModuleConfigurationFactory;

/**
 * OC4J implementation of the ModuleConfigurationFactory.
 * 
 * @author Michal Mocnak
 */
public class OC4JModuleConfigurationFactory implements ModuleConfigurationFactory {
    
    public ModuleConfiguration create(J2eeModule j2eeModule) throws ConfigurationException {
        if (J2eeModule.WAR == j2eeModule.getModuleType()) {
            return new OC4JWarModuleConfiguration(j2eeModule);
        } else if (J2eeModule.EJB == j2eeModule.getModuleType()) {
            return new OC4JEjbModuleConfiguration(j2eeModule);
        } else if (J2eeModule.EAR == j2eeModule.getModuleType()) {
            return new OC4JEarModuleConfiguration(j2eeModule);
        }
        
        return new OC4JModuleConfiguration(j2eeModule);
    }
}