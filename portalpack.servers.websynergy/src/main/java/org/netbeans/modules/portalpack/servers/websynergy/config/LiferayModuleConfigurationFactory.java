/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.modules.portalpack.servers.websynergy.config;

import org.netbeans.modules.j2ee.deployment.common.api.ConfigurationException;
import org.netbeans.modules.j2ee.deployment.devmodules.api.J2eeModule;
import org.netbeans.modules.j2ee.deployment.plugins.spi.config.ModuleConfiguration;
import org.netbeans.modules.j2ee.deployment.plugins.spi.config.ModuleConfigurationFactory;

/**
 *
 * @author satyaranjan
 */
public class LiferayModuleConfigurationFactory implements ModuleConfigurationFactory {

    public ModuleConfiguration create(J2eeModule arg0) throws ConfigurationException {
        return new LiferayModuleConfiguration(arg0);
    }

}
