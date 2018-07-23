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

package org.netbeans.modules.j2ee.oc4j;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.deploy.shared.ModuleType;
import javax.enterprise.deploy.spi.TargetModuleID;
import org.netbeans.modules.j2ee.deployment.plugins.spi.FindJSPServlet;
import org.netbeans.modules.j2ee.oc4j.util.OC4JPluginProperties;

/**
 *
 * @author pblaha
 */
public class OC4JFindJSPServlet implements FindJSPServlet {
    
    private OC4JDeploymentManager dm;
    
    /** Creates a new instance of OC4JFindJSPServlet */
    public OC4JFindJSPServlet(OC4JDeploymentManager dm) {
        this.dm = dm;
    }
    
    public File getServletTempDirectory(String moduleContextPath) {
        String deploymentDir = dm.getInstanceProperties().getProperty(OC4JPluginProperties.PROPERTY_OC4J_HOME) +
                "j2ee" + File.separator + "home" + File.separator + "application-deployments"; // NOI18N
        String pagesDir = "persistence" + File.separator + "_pages"; // NOPI18N
        try {
            for(TargetModuleID application : dm.getAvailableModules(ModuleType.EAR, dm.getTargets())) {
                String parentModuleID = application.getModuleID();
                for(TargetModuleID webApps: application.getChildTargetModuleID()) {
                    if(webApps.getWebURL().equals(moduleContextPath)) {
                        return new File(deploymentDir + File.separator + parentModuleID +
                                File.separator + webApps.getModuleID() + pagesDir);
                    }
                }
            }
        } catch(Exception ex) {
            Logger.getLogger("global").log(Level.INFO, null, ex);
        }
        return null;
    }
    
    public String getServletResourcePath(String moduleContextPath,
            String jspResourcePath) {
        return "_" + jspResourcePath.substring(0, jspResourcePath.lastIndexOf(".")) + ".java";
    }
    
    public String getServletEncoding(String moduleContextPath,
            String jspResourcePath) {
        return "UTF8"; // NOI18N
    }
    
}
