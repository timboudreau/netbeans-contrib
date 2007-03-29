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

/*
 * WS70J2eePlatformFactory.java
 */

package org.netbeans.modules.j2ee.sun.ws7.j2ee;

import javax.enterprise.deploy.spi.DeploymentManager;
import org.netbeans.modules.j2ee.deployment.plugins.spi.J2eePlatformFactory;
import org.netbeans.modules.j2ee.deployment.plugins.spi.J2eePlatformImpl;
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
