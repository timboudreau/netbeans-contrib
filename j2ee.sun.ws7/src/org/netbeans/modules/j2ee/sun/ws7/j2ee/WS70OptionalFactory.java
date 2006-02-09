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

package org.netbeans.modules.j2ee.sun.ws7.j2ee;

import javax.enterprise.deploy.spi.DeploymentManager;

import org.netbeans.modules.j2ee.deployment.plugins.api.FindJSPServlet;
import org.netbeans.modules.j2ee.deployment.plugins.api.IncrementalDeployment;
import org.netbeans.modules.j2ee.deployment.plugins.api.OptionalDeploymentManagerFactory;
import org.netbeans.modules.j2ee.deployment.plugins.api.StartServer;
import org.netbeans.modules.j2ee.deployment.plugins.api.TargetModuleIDResolver;

import org.openide.WizardDescriptor;
import org.netbeans.modules.j2ee.sun.ws7.ui.WS70ServerUIWizardIterator;
import org.netbeans.modules.j2ee.sun.ws7.j2ee.jsp.JSPServletFinder;
import org.netbeans.modules.j2ee.sun.ws7.dm.WS70SunDeploymentManager;


/** Factory for optional deployment functionality that a plugin can provide.
 * Plugins need to register an isntance of this class in module layer in folder
 * <code>J2EE/Jsr88Plugins/{plugin_name}</code>.
 *
 */
public class WS70OptionalFactory extends OptionalDeploymentManagerFactory {
    
    /** Create StartServer for given DeploymentManager.
     * The instance returned by this method till be cached by the j2eeserver.
     */ 
    public StartServer getStartServer (DeploymentManager dm) {
        return new WS70StartServer(dm);
    }
    
    /** Create IncrementalDeployment for given DeploymentManager.
     * The instance returned by this method till be cached by the j2eeserver.
     */
    public IncrementalDeployment getIncrementalDeployment (DeploymentManager dm) {
        return null; //TBD
    }
    
    
    /** Create FindJSPServlet for given DeploymentManager.
     * The instance returned by this method will be cached by the j2eeserver.
     */
    public FindJSPServlet getFindJSPServlet (DeploymentManager dm) {
        // can view Servlet for JSP if server is colocated.
        if(((WS70SunDeploymentManager)dm).isLocalServer()){
            return new JSPServletFinder(dm);
        }else{
            return null;
        }
        
    }
    
    
    /** Create AutoUndeploySupport for the given DeploymentManager.
     * The instance returned by this method will be cached by the j2eeserver.
     */
    public TargetModuleIDResolver getTargetModuleIDResolver(DeploymentManager dm) {
                                // XXX
        return null;
    }
    
    public WizardDescriptor.InstantiatingIterator getAddInstanceIterator() {
        return new WS70ServerUIWizardIterator();
    }
}
