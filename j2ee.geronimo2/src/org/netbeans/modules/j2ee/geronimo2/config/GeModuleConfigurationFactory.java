/*
 * 
 */

package org.netbeans.modules.j2ee.geronimo2.config;

import org.netbeans.modules.j2ee.deployment.common.api.ConfigurationException;
import org.netbeans.modules.j2ee.deployment.devmodules.api.J2eeModule;
import org.netbeans.modules.j2ee.deployment.plugins.spi.config.ModuleConfiguration;
import org.netbeans.modules.j2ee.deployment.plugins.spi.config.ModuleConfigurationFactory;

/**
 * Geronimo ModuleConfigurationFactory impl.
 * @author Max Sauer
 */
public class GeModuleConfigurationFactory implements ModuleConfigurationFactory {

    public ModuleConfiguration create(J2eeModule j2eeModule) throws ConfigurationException {
        if (J2eeModule.Type.WAR.equals(j2eeModule.getType())) {
            return new GeWarModuleConfiguration(j2eeModule);
        } else if (J2eeModule.Type.EJB.equals(j2eeModule.getType())) {
            return new GeEjbModuleConfiguration(j2eeModule);
        } else if (J2eeModule.Type.EAR.equals(j2eeModule.getType())) {
            return new GeEarModuleConfiguration(j2eeModule);
        }
        
        return new GeModuleConfiguration(j2eeModule);
    }

}
