/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.modules.j2ee.geronimo2.config;

import org.netbeans.modules.j2ee.deployment.devmodules.api.J2eeModule;
import org.netbeans.modules.j2ee.deployment.plugins.spi.config.ModuleConfiguration;
import org.openide.util.Lookup;

/**
 * Server specific configuration data
 * TODO: add support fot DataSources
 * @author Max Sauer
 */
public class GeEarModuleConfiguration implements ModuleConfiguration {

    public GeEarModuleConfiguration(J2eeModule j2eeModule) {
    }

    public Lookup getLookup() {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    public J2eeModule getJ2eeModule() {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    public void dispose() {
	throw new UnsupportedOperationException("Not supported yet.");
    }

}
