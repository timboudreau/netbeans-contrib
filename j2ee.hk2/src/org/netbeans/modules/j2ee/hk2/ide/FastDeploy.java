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
import org.netbeans.modules.j2ee.deployment.plugins.api.AppChangeDescriptor;
import org.netbeans.modules.j2ee.deployment.plugins.api.IncrementalDeployment;
import org.netbeans.modules.j2ee.hk2.Hk2DeploymentManager;
import org.netbeans.modules.j2ee.hk2.progress.ProgressEventSupport;
import org.netbeans.modules.j2ee.hk2.progress.Status;
import org.openide.util.RequestProcessor;

/**
 *
 * @author ludo
 */
public class FastDeploy extends IncrementalDeployment{
    private DeploymentManager dm;
    
    /** Creates a new instance of FastDeploy 
     * @param dm 
     */
    public FastDeploy(DeploymentManager dm) {
        System.out.println("fastdeploy");
        this.dm =dm;
    }
    
    /**
     * 
     * @param target 
     * @param deployableObject 
     * @param deploymentConfiguration 
     * @param file 
     * @return 
     */
    public ProgressObject initialDeploy(Target target, DeployableObject deployableObject,
            DeploymentConfiguration deploymentConfiguration, File file) {
        System.out.println("initialDeploy deploymentConfiguration=" +deploymentConfiguration);
        
        Hk2ManagerImpl tmi = new Hk2ManagerImpl((Hk2DeploymentManager)dm);
        tmi.initialDeploy(target,  file);
        return tmi;
    }
    
    /**
     * 
     * @param targetModuleID 
     * @param appChangeDescriptor 
     * @return 
     */
    public ProgressObject incrementalDeploy(TargetModuleID targetModuleID, AppChangeDescriptor appChangeDescriptor) {
        System.out.println("incrementalDeploy");
        Hk2ManagerImpl tmi = new Hk2ManagerImpl((Hk2DeploymentManager)dm);
        tmi.reDeploy(targetModuleID);
        return tmi;    }
    
    /**
     * 
     * @param target 
     * @param deployableObject 
     * @return 
     */
    public boolean canFileDeploy(Target target, DeployableObject deployableObject) {
        if (null == target){
            return false;
        }
        if (null == deployableObject){
            return false;
        }
        
        if (deployableObject.getType() == ModuleType.EAR ||
                deployableObject.getType() == ModuleType.EJB){
            return false;
        }
        // return dm.isLocal();
        System.out.println("canFileDeploy");
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
     * @param deployableObject 
     * @param deploymentConfiguration 
     * @return 
     */
    public File getDirectoryForNewApplication(Target target, DeployableObject deployableObject, DeploymentConfiguration deploymentConfiguration) {
        System.out.println("getDirectoryForNewApplication");
        return null;
    }
    
    /**
     * 
     * @param file 
     * @param string 
     * @param deployableObject 
     * @param deploymentConfiguration 
     * @return 
     */
    public File getDirectoryForNewModule(File file, String string, DeployableObject deployableObject, DeploymentConfiguration deploymentConfiguration) {
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
