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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.web.webrest;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.libraries.Library;
import org.netbeans.modules.j2ee.deployment.devmodules.spi.J2eeModuleProvider;
import org.netbeans.modules.web.api.webmodule.WebModule;
import org.netbeans.modules.web.spi.webmodule.FrameworkConfigurationPanel;
import org.netbeans.modules.web.spi.webmodule.WebFrameworkProvider;
import org.netbeans.modules.websvc.rest.spi.RestSupport;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.util.NbBundle;

/**
 *
 * @author Nam Nguyen
 */
public class RestFramework extends WebFrameworkProvider {
    
    /** Creates a new instance of RestFramework */
    public RestFramework() {
        super(NbBundle.getMessage(RestFramework.class, "LBL_RestFramework"),
                NbBundle.getMessage(RestFramework.class, "LBL_RestFrameworkDesc"));
    }
    
    public static Project getProject(WebModule wm) {
        FileObject fo = wm.getDeploymentDescriptor();
        Project project = null;
        if (fo != null) {
            project = FileOwnerQuery.getOwner(fo);
        }
        if (project == null) {
            throw new IllegalArgumentException("Bad web module");
        }
        return project;
    }
    
    public String getTargeServerInstanceID(Project project) {
        return project.getLookup().lookup(J2eeModuleProvider.class).getServerInstanceID();
    }

    public Set extend(WebModule wm) {
        Project project = getProject(wm);
        RestSupport rs = project.getLookup().lookup(RestSupport.class);
        if (rs != null) {
            if (rs.needsSwdpLibrary(project)) {
                try {
                    rs.ensureRestDevelopmentReady();
                } catch(IOException ioe) {
                    ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, ioe);
                }
            }
        }
        return Collections.EMPTY_SET;
    }
    
    public boolean isInWebModule(WebModule wm) {
        Project project = getProject(wm);
        RestSupport rs = project.getLookup().lookup(RestSupport.class);
        return rs.isReady();
    }
    
    public File[] getConfigurationFiles(WebModule wm) {
        return new File[0];
    }
    
    public FrameworkConfigurationPanel getConfigurationPanel(WebModule wm) {
        return null;
    }
}
