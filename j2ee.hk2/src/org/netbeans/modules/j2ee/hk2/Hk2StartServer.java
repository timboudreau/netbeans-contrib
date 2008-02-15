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


package org.netbeans.modules.j2ee.hk2;

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
import org.netbeans.modules.j2ee.hk2.ide.Hk2DeploymentStatus;
import org.netbeans.modules.j2ee.hk2.ide.Hk2ManagerImpl;
import org.netbeans.modules.j2ee.hk2.ide.Hk2PluginProperties;
import org.netbeans.modules.j2ee.hk2.ide.Hk2StartRunnable;
import org.netbeans.modules.j2ee.hk2.ide.Hk2StopRunnable;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;

/**
 *
 * @author ludo
 */
public class Hk2StartServer extends StartServer implements ProgressObject {
   
    public static enum MODE { RUN, DEBUG };
    private MODE mode;
    private DeploymentStatus deploymentStatus;
    private Hk2DeploymentManager dm;
    private String serverName;
    private String serverHome;
    private Vector <ProgressListener> listeners = new Vector<ProgressListener>();
    private InstanceProperties ip;
    private static Map isDebugModeUri = Collections.synchronizedMap((Map)new HashMap(2,1));
    private String url;
    
    public Hk2StartServer(DeploymentManager dm) {
        if (!(dm instanceof Hk2DeploymentManager)) {
            throw new IllegalArgumentException("Only GlassFish V3 is supported"); //NOI18N
        }
        this.dm = (Hk2DeploymentManager) dm;
        this.ip = ((Hk2DeploymentManager)dm).getProperties().getInstanceProperties();
        serverName = ip.getProperty(InstanceProperties.DISPLAY_NAME_ATTR);
        serverHome = ip.getProperty(Hk2PluginProperties.PROPERTY_HK2_HOME);
        url = ip.getProperty(InstanceProperties.URL_ATTR);
        deploymentStatus = new Hk2DeploymentStatus(ActionType.EXECUTE, CommandType.START, StateType.RUNNING, 
                NbBundle.getMessage(Hk2StartServer.class, "MSG_START_SERVER_IN_PROGRESS", serverName));
    }
    
    @Override
    public boolean supportsStartDebugging(Target target) {
        return true;
    }
    
    public InstanceProperties getInstanceProperties() {
        return ip;
    }
    
    public ProgressObject startDebugging(Target target) {
        mode = MODE.DEBUG;
        RequestProcessor.getDefault().post(new Hk2StartRunnable(dm, this), 0, Thread.NORM_PRIORITY);
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
        return new ServerDebugInfo(ip.getProperty(Hk2PluginProperties.PROPERTY_HOST), dm.getProperties().getDebugPort());
    }
    
    public boolean supportsStartDeploymentManager() {
        return true;
    }
    
    public ProgressObject stopDeploymentManager() {
        fireHandleProgressEvent(null, new Hk2DeploymentStatus(ActionType.EXECUTE, CommandType.START, StateType.RUNNING,
                NbBundle.getMessage(Hk2StartServer.class, "MSG_STOP_SERVER_IN_PROGRESS", serverName)));//NOI18N
        RequestProcessor.getDefault().post(new Hk2StopRunnable(dm, this), 0, Thread.NORM_PRIORITY);
        removeDebugModeUri();
        return this;
    }
    
    // start server
    public ProgressObject startDeploymentManager() {
        mode = MODE.RUN;
        fireHandleProgressEvent(null, new Hk2DeploymentStatus(ActionType.EXECUTE, CommandType.START, StateType.RUNNING,
                NbBundle.getMessage(Hk2StartServer.class, "MSG_START_SERVER_IN_PROGRESS", serverName)));//NOI18N
        RequestProcessor.getDefault().post(new Hk2StartRunnable(dm, this), 0, Thread.NORM_PRIORITY);
        removeDebugModeUri();
        return this;
    }
    
    public boolean needsStartForTargetList() {
        return false;
    }
    
    public boolean needsStartForConfigure() {
        return false;
    }
    
    public boolean needsStartForAdminConfig() {
        return false;
    }
    
    public boolean isRunning() {
        boolean result = Hk2PluginProperties.isRunning(
                ip.getProperty(Hk2PluginProperties.PROPERTY_HOST),
                ip.getProperty(InstanceProperties.HTTP_PORT_NUMBER));
        if(result) {
            Hk2ManagerImpl mgr = new Hk2ManagerImpl(dm);
            result = mgr.isV3Running();
        }
        return result;
    }
    
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
    }
    
    public boolean isStopSupported() {
        return true;
    }
    
    public void stop() throws OperationUnsupportedException {
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
    
    public MODE getMode() {
        return mode;
    }
}
