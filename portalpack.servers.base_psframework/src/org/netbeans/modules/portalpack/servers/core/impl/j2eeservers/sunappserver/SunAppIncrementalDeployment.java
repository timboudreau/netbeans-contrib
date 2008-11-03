/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
 * 
 * Contributor(s):
 * 
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package org.netbeans.modules.portalpack.servers.core.impl.j2eeservers.sunappserver;

import java.io.File;
import java.io.IOException;
import javax.enterprise.deploy.spi.DeploymentManager;
import javax.enterprise.deploy.spi.Target;
import javax.enterprise.deploy.spi.TargetModuleID;
import javax.enterprise.deploy.spi.status.ProgressObject;
import org.netbeans.modules.j2ee.deployment.devmodules.api.J2eeModule;
import org.netbeans.modules.j2ee.deployment.plugins.api.AppChangeDescriptor;
import org.netbeans.modules.j2ee.deployment.plugins.spi.IncrementalDeployment;
import org.netbeans.modules.j2ee.deployment.plugins.spi.config.ModuleConfiguration;
import org.netbeans.modules.portalpack.servers.core.PSDeployer;
import org.netbeans.modules.portalpack.servers.core.PSDeployerImpl;
import org.netbeans.modules.portalpack.servers.core.api.PSDeploymentManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;

/**
 *
 * @author satyaranjan
 */
public class SunAppIncrementalDeployment extends IncrementalDeployment{

    private PSDeploymentManager dm;
    public SunAppIncrementalDeployment(PSDeploymentManager dm) {
        this.dm = dm;
    }
    
    @Override
    public ProgressObject initialDeploy(Target target, J2eeModule app, ModuleConfiguration configuration, File dir) {
        
        PSDeployer deployer = new PSDeployerImpl(dm, "",0);
        FileObject archive = null;
        File arFile = null;
        try {
            archive = app.getArchive();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        if(archive != null) {
            arFile = FileUtil.toFile(archive);
        }
        return deployer.deploy(target, dir, arFile);
    }

    @Override
    public ProgressObject incrementalDeploy(TargetModuleID module, AppChangeDescriptor changes) {
        PSDeployer deployer = new PSDeployerImpl((PSDeploymentManager)dm, "",0);
        return deployer;
        
    }

    @Override
    public boolean canFileDeploy(Target target, J2eeModule deployable) {
        return true;
       
    }

    @Override
    public File getDirectoryForNewApplication(Target target, J2eeModule app, ModuleConfiguration configuration) {
        return null;
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public File getDirectoryForNewModule(File appDir, String uri, J2eeModule module, ModuleConfiguration configuration) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public File getDirectoryForModule(TargetModuleID module) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isDeployOnSaveSupported() {
        return true;
    }
    
}
