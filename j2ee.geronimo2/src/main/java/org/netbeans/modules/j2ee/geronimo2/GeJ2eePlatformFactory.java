/*
 * GeJ2eePlatformFactory.java
 *
 */
package org.netbeans.modules.j2ee.geronimo2;


import javax.enterprise.deploy.spi.DeploymentManager;
import org.netbeans.modules.j2ee.deployment.plugins.spi.J2eePlatformFactory;
import org.netbeans.modules.j2ee.deployment.plugins.spi.J2eePlatformImpl;

/**
 *
 * @author Max Sauer
 */
public class GeJ2eePlatformFactory extends J2eePlatformFactory {
    public J2eePlatformImpl getJ2eePlatformImpl(DeploymentManager dm) {
        assert GeDeploymentManager.class.isAssignableFrom(dm.getClass()) :
            this + " cannot create platform for unknown deployment manager:" + dm;
        
        GeDeploymentManager manager  = (GeDeploymentManager) dm;
        return new GeJ2eePlatformImpl(manager);
    }
}