/*
 * GeOptionalFactory.java
 *
 */
package org.netbeans.modules.j2ee.geronimo2;


import javax.enterprise.deploy.spi.DeploymentManager;
import org.netbeans.modules.j2ee.deployment.plugins.spi.FindJSPServlet;
import org.netbeans.modules.j2ee.deployment.plugins.spi.IncrementalDeployment;
import org.netbeans.modules.j2ee.deployment.plugins.spi.OptionalDeploymentManagerFactory;
import org.netbeans.modules.j2ee.deployment.plugins.spi.StartServer;
import org.openide.WizardDescriptor.InstantiatingIterator;

/**
 *
 * @author Max Sauer
 */
public class GeOptionalFactory extends OptionalDeploymentManagerFactory {
    
    public StartServer getStartServer(DeploymentManager dm) {
        return new GeStartServer(dm);
    }
    
    public IncrementalDeployment getIncrementalDeployment(DeploymentManager dm) {
        return null;
    }
    
    public FindJSPServlet getFindJSPServlet(DeploymentManager dm) {
        return null;
    }
    
    @Override
    public InstantiatingIterator getAddInstanceIterator() {
        return new GeInstantiatingIterator();
    }
    
}
