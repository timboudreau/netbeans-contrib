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

package org.netbeans.modules.j2ee.sun.ws7.j2ee;

import javax.enterprise.deploy.spi.DeploymentManager;

import org.netbeans.modules.j2ee.deployment.plugins.spi.FindJSPServlet;
import org.netbeans.modules.j2ee.deployment.plugins.spi.IncrementalDeployment;
import org.netbeans.modules.j2ee.deployment.plugins.spi.OptionalDeploymentManagerFactory;
import org.netbeans.modules.j2ee.deployment.plugins.spi.StartServer;
import org.netbeans.modules.j2ee.deployment.plugins.spi.TargetModuleIDResolver;

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
