/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
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
