/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

/*
 * WS70J2eePlatformFactory.java 
 */

package org.netbeans.modules.j2ee.sun.ws7.j2ee;

import javax.enterprise.deploy.spi.DeploymentManager;
import org.netbeans.modules.j2ee.deployment.plugins.api.J2eePlatformFactory;
import org.netbeans.modules.j2ee.deployment.plugins.api.J2eePlatformImpl;
import org.netbeans.modules.j2ee.sun.ws7.dm.WS70SunDeploymentManager;
import org.openide.util.NbBundle;
/**
 *
 * @author Administrator
 */
public class WS70J2eePlatformFactory extends J2eePlatformFactory{
    
    /**
     * Creates a new instance of WS70J2eePlatformFactory 
     */
    public WS70J2eePlatformFactory() {
    }
    
    public J2eePlatformImpl getJ2eePlatformImpl(DeploymentManager dm) {
        WS70SunDeploymentManager manager = (WS70SunDeploymentManager)dm;
        
        String location = manager.getServerLocation();
        return new WS70J2eePlatformImpl(location, 
                NbBundle.getMessage(WS70J2eePlatformFactory.class, "LBL_WS70J2eePlatformDisplayName"));
    }
}
