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

package org.netbeans.modules.j2ee.hk2.ide;

import java.io.File;
import javax.enterprise.deploy.model.DeployableObject;
import javax.enterprise.deploy.shared.ActionType;
import javax.enterprise.deploy.shared.CommandType;
import javax.enterprise.deploy.shared.ModuleType;
import javax.enterprise.deploy.shared.StateType;
import javax.enterprise.deploy.spi.DeploymentConfiguration;
import javax.enterprise.deploy.spi.DeploymentManager;
import javax.enterprise.deploy.spi.Target;
import javax.enterprise.deploy.spi.TargetModuleID;
import javax.enterprise.deploy.spi.exceptions.OperationUnsupportedException;
import javax.enterprise.deploy.spi.status.ProgressObject;
import org.netbeans.modules.j2ee.deployment.devmodules.api.J2eeModule;
import org.netbeans.modules.j2ee.deployment.plugins.api.AppChangeDescriptor;
import org.netbeans.modules.j2ee.deployment.plugins.spi.IncrementalDeployment;
import org.netbeans.modules.j2ee.deployment.plugins.spi.config.ModuleConfiguration;
import org.netbeans.modules.j2ee.hk2.Hk2DeploymentManager;
import org.netbeans.modules.j2ee.hk2.progress.ProgressEventSupport;
import org.netbeans.modules.j2ee.hk2.progress.Status;
import org.openide.util.RequestProcessor;

/**
 *
 * @author ludo
 */
public class FastDeploy extends IncrementalDeployment {
    
    private DeploymentManager dm;
    
    /** Creates a new instance of FastDeploy 
     * @param dm 
     */
    public FastDeploy(DeploymentManager dm) {
//        System.out.println("fastdeploy");
        this.dm =dm;
    }
    
    /**
     * 
     * @param target 
     * @param app 
     * @param configuration 
     * @param file 
     * @return 
     */
    public ProgressObject initialDeploy(Target target, J2eeModule app, ModuleConfiguration configuration, File dir) {
//        System.out.println("initialDeploy ModuleConfiguration = " + configuration);
        
        Hk2ManagerImpl tmi = new Hk2ManagerImpl((Hk2DeploymentManager)dm);
        tmi.initialDeploy(target, dir);
        return tmi;
    }
    
    public ProgressObject initialDeploy(Target target,  File dir, String moduleName) {
//        System.out.println("initialDeploy  = " + dir);
        
        Hk2ManagerImpl tmi = new Hk2ManagerImpl((Hk2DeploymentManager)dm);
        tmi.initialDeploy(target, dir,moduleName);
        return tmi;
    }    /**
     * 
     * @param targetModuleID 
     * @param appChangeDescriptor 
     * @return 
     */
    public ProgressObject incrementalDeploy(TargetModuleID targetModuleID, AppChangeDescriptor appChangeDescriptor) {
//        System.out.println("incrementalDeploy");
        Hk2ManagerImpl tmi = new Hk2ManagerImpl((Hk2DeploymentManager)dm);
        tmi.reDeploy(targetModuleID);
        return tmi;    }
    
    /**
     * 
     * @param target 
     * @param deployable 
     * @return 
     */
    public boolean canFileDeploy(Target target, J2eeModule deployable) {
        if (null == target){
            return false;
        }
        if (null == deployable){
            return false;
        }
        
        if (deployable.getModuleType() == ModuleType.EAR ||
                deployable.getModuleType() == ModuleType.EJB){
            return false;
        }
        // return dm.isLocal();
//        System.out.println("canFileDeploy");
        return true;
        
    }
    /**
     * 
     * @param module 
     * @return 
     */
    public ProgressObject dummyProgressObject(final TargetModuleID module){
        final P p = new P(module);
        p.supp.fireHandleProgressEvent(module, new Status(ActionType.EXECUTE, CommandType.DISTRIBUTE, "", StateType.COMPLETED));
        RequestProcessor.getDefault().post(new Runnable() {
            public void run() {
                try {
                    p.supp.fireHandleProgressEvent(module, new Status(ActionType.EXECUTE, CommandType.DISTRIBUTE, "", StateType.COMPLETED));
                    
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        });
        return p;
    }
    
    /**
     * 
     * @param target 
     * @param app 
     * @param configuration
     * @return 
     */
    public File getDirectoryForNewApplication(Target target, J2eeModule app, ModuleConfiguration configuration) {
//        System.out.println("getDirectoryForNewApplication");
        return null;
    }
    
    /**
     * 
     * @param file 
     * @param string 
     * @param app 
     * @param configuration 
     * @return 
     */
    public File getDirectoryForNewModule(File file, String string, J2eeModule app, ModuleConfiguration configuration) {
        return null;
    }
    
    /**
     * 
     * @param targetModuleID 
     * @return 
     */
    public File getDirectoryForModule(TargetModuleID targetModuleID) {
        return null;
    }
    
    
    private static class P implements ProgressObject {
        
        ProgressEventSupport supp = new ProgressEventSupport(this);
        TargetModuleID tmid;
        
        P(TargetModuleID tmid) {
            this.tmid = tmid;
        }
        
        public void addProgressListener(javax.enterprise.deploy.spi.status.ProgressListener progressListener) {
            supp.addProgressListener(progressListener);
        }
        
        public void removeProgressListener(javax.enterprise.deploy.spi.status.ProgressListener progressListener) {
            supp.removeProgressListener(progressListener);
        }
        
        public javax.enterprise.deploy.spi.status.ClientConfiguration getClientConfiguration(javax.enterprise.deploy.spi.TargetModuleID targetModuleID) {
            return null;
        }
        
        public javax.enterprise.deploy.spi.status.DeploymentStatus getDeploymentStatus() {
            return supp.getDeploymentStatus();
        }
        
        public javax.enterprise.deploy.spi.TargetModuleID[] getResultTargetModuleIDs() {
            return new TargetModuleID [] {tmid};
        }
        
        public boolean isCancelSupported() {
            return false;
        }
        
        public boolean isStopSupported() {
            return false;
        }
        
        public void cancel() throws javax.enterprise.deploy.spi.exceptions.OperationUnsupportedException {
            throw new OperationUnsupportedException("");
        }
        
        public void stop() throws javax.enterprise.deploy.spi.exceptions.OperationUnsupportedException {
            throw new OperationUnsupportedException("");
        }
        
    }
}
