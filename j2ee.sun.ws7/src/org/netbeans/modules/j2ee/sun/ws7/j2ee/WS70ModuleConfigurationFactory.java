/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.j2ee.sun.ws7.j2ee;

import org.netbeans.modules.j2ee.deployment.common.api.ConfigurationException;
import org.netbeans.modules.j2ee.deployment.devmodules.api.J2eeModule;
import org.netbeans.modules.j2ee.deployment.plugins.spi.config.ModuleConfiguration;
import org.netbeans.modules.j2ee.deployment.plugins.spi.config.ModuleConfigurationFactory;
import org.openide.ErrorManager;


public class WS70ModuleConfigurationFactory implements ModuleConfigurationFactory {
    
    /** Creates a new instance of ModConFactory */
    public WS70ModuleConfigurationFactory() {
    }
    
    public ModuleConfiguration create(J2eeModule module) {
        ModuleConfiguration retVal = null;
        try {
            retVal = new WS70ModuleConfigurationImpl(module);
        } catch (ConfigurationException ce) {
            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, ce);
        }
        return retVal;
    }
    
}
