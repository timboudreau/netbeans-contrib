/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.modules.j2ee.geronimo2.config;

import java.io.File;
import org.netbeans.modules.j2ee.deployment.devmodules.api.J2eeModule;
import org.netbeans.modules.j2ee.deployment.plugins.spi.config.ModuleConfiguration;
import org.openide.loaders.DataObject;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;

/**
 * 
 * @author Max Sauer
 */
public class GeModuleConfiguration implements ModuleConfiguration {

    private J2eeModule j2eeModule;
    // the directory with resources - supplied by the configuration support in the construction time
    private File resourceDir;
    
    // cached data object for the server-specific configuration file (initialized by the subclasses)
    protected DataObject deploymentDescriptorDO;
    
    public GeModuleConfiguration(J2eeModule j2eeModule) {
	this.j2eeModule = j2eeModule;
	this.resourceDir = j2eeModule.getResourceDirectory();
    }

    public Lookup getLookup() {
	return Lookups.fixed(this);
    }

    public J2eeModule getJ2eeModule() {
	return j2eeModule;
    }

    public void dispose() {
    }

}
