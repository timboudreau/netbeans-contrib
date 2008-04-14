/*
 * GeStartServer.java
 *
 */
package org.netbeans.modules.j2ee.geronimo2;

import javax.enterprise.deploy.spi.DeploymentManager;
import javax.enterprise.deploy.spi.Target;
import javax.enterprise.deploy.spi.TargetModuleID;
import javax.enterprise.deploy.spi.exceptions.OperationUnsupportedException;
import javax.enterprise.deploy.spi.status.ClientConfiguration;
import javax.enterprise.deploy.spi.status.DeploymentStatus;
import javax.enterprise.deploy.spi.status.ProgressListener;
import javax.enterprise.deploy.spi.status.ProgressObject;
import org.netbeans.modules.j2ee.deployment.plugins.api.InstanceProperties;
import org.netbeans.modules.j2ee.deployment.plugins.api.ServerDebugInfo;
import org.netbeans.modules.j2ee.deployment.plugins.spi.StartServer;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.enterprise.deploy.shared.ActionType;
import javax.enterprise.deploy.shared.CommandType;
import javax.enterprise.deploy.shared.StateType;
import javax.enterprise.deploy.spi.status.ProgressEvent;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
/**
 *
 * @author Max Sauer
 */
public class GeStartServer extends StartServer implements ProgressObject {

    static enum MODE { RUN, DEBUG };
    
    private MODE mode;
    private volatile DeploymentStatus deploymentStatus;
    private GeDeploymentManager dm;
    private String serverName;
    private CopyOnWriteArrayList<ProgressListener> listeners = new CopyOnWriteArrayList<ProgressListener>();
    private InstanceProperties ip;
    private static Map isDebugModeUri = Collections.synchronizedMap((Map)new HashMap(2,1));
    private String url;


    public GeStartServer(DeploymentManager dm) {
	if (!(dm instanceof GeDeploymentManager)) {
	    throw new IllegalArgumentException("Only GeDeplomentManager is supported"); //NOI18N
	}
	this.dm = (GeDeploymentManager) dm;
	this.ip = ((GeDeploymentManager) dm).getProperties().getInstanceProperties();
	serverName = ip.getProperty(InstanceProperties.DISPLAY_NAME_ATTR);
	url = ip.getProperty(InstanceProperties.URL_ATTR);
    }

    public ProgressObject startDebugging(Target target) {
	return null;
    }

    public boolean isDebuggable(Target target) {
	return false;
    }

    public boolean isAlsoTargetServer(Target target) {
	return true;
    }

    public ServerDebugInfo getDebugInfo(Target target) {
	return null;
    }

    public boolean supportsStartDeploymentManager() {
	return true;
    }

    //stop server
    public ProgressObject stopDeploymentManager() {
        fireHandleProgressEvent(null, new GeDeploymentStatus(ActionType.EXECUTE, CommandType.STOP, StateType.RUNNING,
                NbBundle.getMessage(GeStartServer.class, "MSG_STOP_SERVER_IN_PROGRESS", serverName)));//NOI18N
        RequestProcessor.getDefault().post(new GeStopRunnable(dm, this), 0, Thread.NORM_PRIORITY);
        removeDebugModeUri();
        return this;
    }

    public ProgressObject startDeploymentManager() {
	mode = MODE.RUN;
        //String user = "system";
        String userName = ip.getProperty(InstanceProperties.USERNAME_ATTR);
        String password = ip.getProperty(InstanceProperties.PASSWORD_ATTR);
        String sName = ip.getProperty(InstanceProperties.DISPLAY_NAME_ATTR);
        fireHandleProgressEvent(null, new GeDeploymentStatus(ActionType.EXECUTE, CommandType.START, StateType.RUNNING,
                NbBundle.getMessage(GeStartServer.class, "MSG_START_SERVER_IN_PROGRESS", sName)));//NOI18N
        
            
        while(password == null || password.equals("")) {
            password = GePluginUtils.requestPassword(userName);
        }
        
        RequestProcessor.getDefault().post(new GeStartRunnable(dm, this), 0, Thread.NORM_PRIORITY);
        removeDebugModeUri();
        return this;
    }
    
    private void removeDebugModeUri() {
        isDebugModeUri.remove(url);
    }

    public void fireHandleProgressEvent(TargetModuleID targetModuleID, DeploymentStatus deploymentStatus) {
        ProgressEvent evt = new ProgressEvent(this, targetModuleID, deploymentStatus);
        this.deploymentStatus = deploymentStatus;

        for (ProgressListener listener : listeners) {
            listener.handleProgressEvent(evt);
        }
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
	return GePluginProperties.isRunning(ip.getProperty(GePluginProperties.PROPERTY_HOST),
                ip.getProperty(InstanceProperties.HTTP_PORT_NUMBER));
    }

    public DeploymentStatus getDeploymentStatus() {
	return deploymentStatus;
    }

    public TargetModuleID[] getResultTargetModuleIDs() {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    public ClientConfiguration getClientConfiguration(TargetModuleID arg0) {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isCancelSupported() {
	return false;
    }

    public void cancel() throws OperationUnsupportedException {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isStopSupported() {
	return false;
    }

    public void stop() throws OperationUnsupportedException {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    public void addProgressListener(ProgressListener l) {
        listeners.add(l);
    }

    public void removeProgressListener(ProgressListener l) {
        listeners.remove(l);
    }
    
    MODE getMode() {
        return mode;
    }
}