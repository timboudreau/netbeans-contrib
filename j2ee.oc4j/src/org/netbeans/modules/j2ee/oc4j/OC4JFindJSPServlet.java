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
