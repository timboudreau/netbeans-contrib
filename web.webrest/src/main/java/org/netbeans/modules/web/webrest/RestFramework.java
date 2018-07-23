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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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
package org.netbeans.modules.web.webrest;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.modules.j2ee.deployment.devmodules.spi.J2eeModuleProvider;
import org.netbeans.modules.web.api.webmodule.WebModule;
import org.netbeans.modules.web.spi.webmodule.FrameworkConfigurationPanel;
import org.netbeans.modules.web.spi.webmodule.WebFrameworkProvider;
import org.netbeans.modules.websvc.rest.spi.RestSupport;
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
            try {
               rs.ensureRestDevelopmentReady();
            } catch(IOException ioe) {
               Logger.getLogger("global").log(Level.INFO, null, ioe);
            }
        }
        return Collections.EMPTY_SET;
    }
    
    public boolean isInWebModule(WebModule wm) {
        Project project = getProject(wm);
        RestSupport rs = project.getLookup().lookup(RestSupport.class);
        return rs.isRestSupportOn();
    }
    
    public File[] getConfigurationFiles(WebModule wm) {
        return new File[0];
    }
    
    public FrameworkConfigurationPanel getConfigurationPanel(WebModule wm) {
        return null;
    }
}
