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

package org.netbeans.modules.j2ee.oc4j.ide;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import javax.enterprise.deploy.shared.ActionType;
import javax.enterprise.deploy.shared.CommandType;
import javax.enterprise.deploy.shared.StateType;
import javax.enterprise.deploy.spi.DeploymentManager;
import javax.enterprise.deploy.spi.Target;
import javax.enterprise.deploy.spi.TargetModuleID;
import javax.enterprise.deploy.spi.exceptions.OperationUnsupportedException;
import javax.enterprise.deploy.spi.status.ClientConfiguration;
import javax.enterprise.deploy.spi.status.DeploymentStatus;
import javax.enterprise.deploy.spi.status.ProgressEvent;
import javax.enterprise.deploy.spi.status.ProgressListener;
import javax.enterprise.deploy.spi.status.ProgressObject;
import org.netbeans.modules.j2ee.deployment.plugins.api.InstanceProperties;
import org.netbeans.modules.j2ee.deployment.plugins.api.ServerDebugInfo;
import org.netbeans.modules.j2ee.deployment.plugins.spi.StartServer;
import org.netbeans.modules.j2ee.oc4j.OC4JDeploymentManager;
import org.netbeans.modules.j2ee.oc4j.util.OC4JPluginProperties;
import org.netbeans.modules.j2ee.oc4j.util.OC4JPluginUtils;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;

/**
 *
 * @author pblaha
 */
public class OC4JStartServer extends StartServer implements ProgressObject {
    
    static enum MODE { RUN, DEBUG };
    private MODE mode;
    private DeploymentStatus deploymentStatus;
    private OC4JDeploymentManager dm;
    private String serverName;
    private String serverHome;
    private Vector <ProgressListener> listeners = new Vector<ProgressListener>();
    private InstanceProperties ip;
    private static Map isDebugModeUri = Collections.synchronizedMap((Map)new HashMap(2,1));
    private String url;
    
    public OC4JStartServer(DeploymentManager dm) {
        if (!(dm instanceof OC4JDeploymentManager)) {
            throw new IllegalArgumentException("Only OC4JDeplomentManager is supported"); //NOI18N
        }
        this.dm = (OC4JDeploymentManager) dm;
        this.ip = ((OC4JDeploymentManager)dm).getProperties().getInstanceProperties();
        serverName = ip.getProperty(InstanceProperties.DISPLAY_NAME_ATTR);
        serverHome = ip.getProperty(OC4JPluginProperties.PROPERTY_OC4J_HOME);
        url = ip.getProperty(InstanceProperties.URL_ATTR);
    }
    
    public boolean supportsStartDebugging(Target target) {
        return OC4JPluginUtils.isLocalServer(ip);
    }
    
    public InstanceProperties getInstanceProperties() {
        return ip;
    }
    
    public ProgressObject startDebugging(Target target) {
        mode = MODE.DEBUG;
        String user = "oc4jadmin";
        String serverName = ip.getProperty(InstanceProperties.DISPLAY_NAME_ATTR);
        fireHandleProgressEvent(null, new OC4JDeploymentStatus(ActionType.EXECUTE, CommandType.START, StateType.RUNNING,
                NbBundle.getMessage(OC4JStartServer.class, "MSG_START_SERVER_IN_PROGRESS", serverName)));//NOI18N
        
        // Check if the server is initialized
        if(!OC4JPluginUtils.isUserActivated(serverHome, user)) {
            String password = null;
            
            while(password == null || password.equals("")) {
                password = OC4JPluginUtils.requestPassword(user);
            }
            
            if(!OC4JPluginUtils.activateUser(serverHome, user, password)) {
                fireHandleProgressEvent(null, new OC4JDeploymentStatus(ActionType.EXECUTE, CommandType.START, StateType.FAILED,
                        NbBundle.getMessage(OC4JStartServer.class, "MSG_START_SERVER_NOT_INITIALIZED", serverName))); //NOI18N
                
                return this;
            }
        }
        
        RequestProcessor.getDefault().post(new OC4JStartRunnable(dm, this), 0, Thread.NORM_PRIORITY);
        addDebugModeUri();
        return this;
    }
    
    private void addDebugModeUri() {
        isDebugModeUri.put(url, new Object());
    }
    
    private void removeDebugModeUri() {
        isDebugModeUri.remove(url);
    }
    
    private boolean existsDebugModeUri() {
        return isDebugModeUri.containsKey(url);
    }
    
    public boolean isDebuggable(Target target) {
        if (!existsDebugModeUri()) {
            return false;
        }
        if (!isRunning()) {
            return false;
        }
        return true;
    }
    
    public boolean isAlsoTargetServer(Target target) {
        return true;
    }
    
    public ServerDebugInfo getDebugInfo(Target target) {
        return new ServerDebugInfo(ip.getProperty(OC4JPluginProperties.PROPERTY_HOST), dm.getProperties().getDebugPort());
    }
    
    public boolean supportsStartDeploymentManager() {
        return OC4JPluginUtils.isLocalServer(ip);
    }
    
    public ProgressObject stopDeploymentManager() {
        fireHandleProgressEvent(null, new OC4JDeploymentStatus(ActionType.EXECUTE, CommandType.START, StateType.RUNNING,
                NbBundle.getMessage(OC4JStartServer.class, "MSG_STOP_SERVER_IN_PROGRESS", serverName)));//NOI18N
        RequestProcessor.getDefault().post(new OC4JStopRunnable(dm, this), 0, Thread.NORM_PRIORITY);
        removeDebugModeUri();
        return this;
    }
    
    // start server
    public ProgressObject startDeploymentManager() {
        mode = MODE.RUN;
        String user = "oc4jadmin";
        String serverName = ip.getProperty(InstanceProperties.DISPLAY_NAME_ATTR);
        fireHandleProgressEvent(null, new OC4JDeploymentStatus(ActionType.EXECUTE, CommandType.START, StateType.RUNNING,
                NbBundle.getMessage(OC4JStartServer.class, "MSG_START_SERVER_IN_PROGRESS", serverName)));//NOI18N
        
        // Check if the server is initialized
        if(!OC4JPluginUtils.isUserActivated(serverHome, user)) {
            String password = null;
            
            while(password == null || password.equals("")) {
                password = OC4JPluginUtils.requestPassword(user);
            }
            
            if(!OC4JPluginUtils.activateUser(serverHome, user, password)) {
                fireHandleProgressEvent(null, new OC4JDeploymentStatus(ActionType.EXECUTE, CommandType.START, StateType.FAILED,
                        NbBundle.getMessage(OC4JStartServer.class, "MSG_START_SERVER_NOT_INITIALIZED", serverName))); //NOI18N
                
                return this;
            }
        }
        
        RequestProcessor.getDefault().post(new OC4JStartRunnable(dm, this), 0, Thread.NORM_PRIORITY);
        removeDebugModeUri();
        return this;
    }
    
    public boolean needsStartForTargetList() {
        return true;
    }
    
    public boolean needsStartForConfigure() {
        return false;
    }
    
    public boolean needsStartForAdminConfig() {
        return true;
    }
    
    public boolean isRunning() {
        return OC4JPluginProperties.isRunning(ip.getProperty(OC4JPluginProperties.PROPERTY_HOST),
                ip.getProperty(InstanceProperties.HTTP_PORT_NUMBER));
    }
    
    // ProgressObject implementation
    
    public DeploymentStatus getDeploymentStatus() {
        return deploymentStatus;
    }
    
    public TargetModuleID[] getResultTargetModuleIDs() {
        return null;
    }
    
    public ClientConfiguration getClientConfiguration(TargetModuleID targetModuleID) {
        return null;
    }
    
    public boolean isCancelSupported() {
        return false;
    }
    
    public void cancel() throws OperationUnsupportedException {
        throw new OperationUnsupportedException("");
    }
    
    public boolean isStopSupported() {
        return false;
    }
    
    public void stop() throws OperationUnsupportedException {
        throw new OperationUnsupportedException("");
    }
    
    public void addProgressListener(ProgressListener progressListener) {
        listeners.add(progressListener);
    }
    
    public void removeProgressListener(ProgressListener progressListener) {
        listeners.remove(progressListener);
    }
    
    public void fireHandleProgressEvent(TargetModuleID targetModuleID, DeploymentStatus deploymentStatus) {
        ProgressEvent evt = new ProgressEvent(this, targetModuleID, deploymentStatus);
        this.deploymentStatus = deploymentStatus;
        
        java.util.Vector targets = null;
        synchronized (this) {
            if (listeners != null) {
                targets = (java.util.Vector) listeners.clone();
            }
        }
        
        if (targets != null) {
            for (int i = 0; i < targets.size(); i++) {
                ProgressListener target = (ProgressListener)targets.elementAt(i);
                target.handleProgressEvent(evt);
            }
        }
    }
    
    // Helper methods
    
    protected MODE getMode() {
        return mode;
    }
}