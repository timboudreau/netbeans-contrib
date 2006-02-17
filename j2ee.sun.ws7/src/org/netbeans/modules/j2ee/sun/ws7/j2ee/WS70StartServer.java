/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

/*
 * WS70StartServer.java
 */

package org.netbeans.modules.j2ee.sun.ws7.j2ee;

import javax.enterprise.deploy.spi.DeploymentManager;
import javax.enterprise.deploy.spi.Target;
import javax.enterprise.deploy.spi.status.ProgressObject;

import org.netbeans.modules.j2ee.deployment.plugins.api.ServerDebugInfo;
import org.netbeans.modules.j2ee.deployment.plugins.api.StartServer;
import org.openide.ErrorManager;
import org.openide.util.NbBundle;
import org.netbeans.modules.j2ee.deployment.plugins.api.UISupport;
import javax.enterprise.deploy.spi.TargetModuleID;
import javax.enterprise.deploy.spi.exceptions.OperationUnsupportedException;
import javax.enterprise.deploy.spi.exceptions.TargetException;
import javax.enterprise.deploy.spi.status.ClientConfiguration;
import javax.enterprise.deploy.spi.status.DeploymentStatus;
import javax.enterprise.deploy.spi.status.ProgressListener;
import javax.enterprise.deploy.spi.status.ProgressObject;

import javax.enterprise.deploy.shared.ActionType;
import javax.enterprise.deploy.shared.CommandType;
import javax.enterprise.deploy.shared.StateType;

import org.netbeans.modules.j2ee.sun.ws7.dm.WS70SunDeploymentManager;
import org.netbeans.modules.j2ee.sun.ws7.util.ProgressEventSupport;
import org.netbeans.modules.j2ee.sun.ws7.util.Status;
import org.openide.util.RequestProcessor;

import java.io.File;
 /*
 * @author Mukesh Garg
 */
public class WS70StartServer extends StartServer implements ProgressObject, Runnable{
    
    private WS70SunDeploymentManager dm;    
    private ProgressEventSupport pes;
    private CommandType cmdType;

    
    public WS70StartServer(DeploymentManager dm) {
        this.dm = (WS70SunDeploymentManager)dm;
        pes = new ProgressEventSupport(this);
        
    }
    

    /**
     * Returns true if the admin server is also the given target server (share the same vm).
     * Start/stopping/debug apply to both servers.
     * @param the target server in question
     * @return true when admin is also target server
     */
    public boolean isAlsoTargetServer(Target target) {        
        return false;
    }
    
    
    /**
     * Returns true if the admin server can be started through this spi.
     */
    public boolean supportsStartDeploymentManager() {
        if(dm.isLocalServer()){
            return true;
        }
        return false;
    }
    
    
    /**
     * Starts the admin server. Note that this means that the DeploymentManager
     * was originally created disconnected. After calling this, the DeploymentManager
     * will be created connected, so the old DeploymentManager will be discarded.
     * This has the result that any unsaved changes in edited server configurations
     * need to be saved or discarded, requiring user prompting.  All diagnostic
     * should be communicated through ServerProgres with no exceptions thrown.
     *
     * @return ProgressObject object used to monitor start server progress
     */
    public ProgressObject startDeploymentManager() {        
        if(dm.isLocalServer()){
            cmdType = CommandType.START;
            pes.fireHandleProgressEvent(null,
                                    new Status(ActionType.EXECUTE, CommandType.START,
                                               NbBundle.getMessage(WS70StartServer.class, "MSG_STARTING_ADMIN_SERVER"),
                                               StateType.RUNNING));
            
            RequestProcessor.getDefault().post(this, 0, Thread.NORM_PRIORITY);
            return this;
        }else{
            return null;
        }                  
    }
    

    /**
     * Stops the admin server. The DeploymentManager object will be disconnected.
     * All diagnostic should be communicated through ServerProgres with no
     * exceptions thrown.
     * @return ServerProgress object used to monitor start server progress
     */
    public ProgressObject stopDeploymentManager() {
         if(dm.isLocalServer()){
            cmdType = CommandType.STOP;
            pes.fireHandleProgressEvent(null,
                                    new Status(ActionType.EXECUTE, CommandType.STOP,
                                               NbBundle.getMessage(WS70StartServer.class, "MSG_STOPPING_ADMIN_SERVER"),
                                               StateType.RUNNING));

            RequestProcessor.getDefault().post(this, 0, Thread.NORM_PRIORITY);
            return this;
        }else{
            return null;
        }        

    }
    
    
    /**
     * Returns true if the admin server should be started before server deployment configuration.
     */
    public boolean needsStartForConfigure() {
        return false;
    }
    
    /**
     * Returns true if the admin server should be started before asking for
     * target list.
     */
    public boolean needsStartForTargetList() {        
        return false;
    }
    
    
    /**
     * Returns true if the admin server should be started before admininistrative configuration.
     */
    public boolean needsStartForAdminConfig() {
        return false;
    }
    
    
    /**
     * Returns true if this admin server is running.
     */
    public boolean isRunning() {        
        return ((WS70SunDeploymentManager)dm).isRunning();
    }
    
    
    /**
     * The method <code>needsRestart</code> is used to signal that
     * there were some actions on the container which would need a
     * restart to take effect.
     * This method not abstract because not not all plugins might need
     * to implement it.
     * 
     * @return a <code>boolean</code> value
     */
    public boolean needsRestart() {
        return false;
    }
    
    /**
     * Returns true if this target is in debug mode.
     */
    public boolean isDebuggable(Target target) {
        return false;
    }
    
    
    /**
     * Start or restart the target in debug mode.
     * If target is also domain admin, the amdin is restarted in debug mode.
     * All diagnostic should be communicated through ServerProgres with no exceptions thrown.
     * @param target the target server
     * @return ServerProgress object to monitor progress on start operation
     */
    public ProgressObject startDebugging(Target target) {
        return null;
    }
    

    /**
     * Returns the host/port necessary for connecting to the server's debug information.
     */
    // PENDING use JpdaDebugInfo from debuggercore
    
    public ServerDebugInfo getDebugInfo(Target target) {
        
        return new ServerDebugInfo("localhost", 4848);
    }
    
    
    public boolean isRunning(Target target){
        return true;
    }
    public ProgressObject startTarget(Target target){
        return null;
    }   
    public ProgressObject stoptTarget(Target target){
        return null;    
    }   
    public boolean supportsStartTarget(Target target){
        return false;
    }
    // Ruunable run implementation
    public synchronized void run(){
        if (cmdType.equals(CommandType.START)) {
            try{
                runProcess(makeProcessString("start"), true);
                this.viewAdminLogs();
                pes.fireHandleProgressEvent(null,
                                        new Status(ActionType.EXECUTE, cmdType,
                                                   NbBundle.getMessage(WS70StartServer.class, "MSG_ADMIN_SERVER_STARTED"),
                                                   StateType.COMPLETED));
            }catch(Exception ex){
                pes.fireHandleProgressEvent(null,
                                        new Status(ActionType.EXECUTE, cmdType,
                                                   ex.getLocalizedMessage(), 
                                                   StateType.FAILED));

            }
        } else if(cmdType.equals(CommandType.STOP)) {
            try{
                runProcess(makeProcessString("stop"), true);
                this.viewAdminLogs();
                pes.fireHandleProgressEvent(null,
                                        new Status(ActionType.EXECUTE, cmdType,
                                                   NbBundle.getMessage(WS70StartServer.class, "MSG_ADMIN_SERVER_STOPPED"),
                                                   StateType.COMPLETED));
            }catch(Exception ex){
                pes.fireHandleProgressEvent(null,
                                        new Status(ActionType.EXECUTE, cmdType,
                                                   ex.getLocalizedMessage(), 
                                                   StateType.FAILED));

            }
        } // end of else if (cmdType.equals(CommandType.START))        
    }

    /** JSR88 method. */
    public ClientConfiguration getClientConfiguration(TargetModuleID t) {
        return null;
    }

    /** JSR88 method. */
    public DeploymentStatus getDeploymentStatus() {
        return pes.getDeploymentStatus();        
    }

    /** JSR88 method. */
    public TargetModuleID[] getResultTargetModuleIDs() { 
        return new TargetModuleID [] {};
    }

    /** JSR88 method. */
    public boolean isCancelSupported() {
        return false;
    }

    /** JSR88 method. */
    public void cancel() throws OperationUnsupportedException {
        throw new OperationUnsupportedException("cancel not supported in WS deployment"); // NOI18N
    }

    /** JSR88 method. */
    public boolean isStopSupported() {
        return false;
    }

    /** JSR88 method. */
    public void stop() throws OperationUnsupportedException {
        throw new OperationUnsupportedException("stop not supported in WS deployment"); // NOI18N
    }

    /** JSR88 method. */
    public void addProgressListener(ProgressListener l) {
        pes.addProgressListener(l);
    }
    
    /** JSR88 method. */
    public void removeProgressListener(ProgressListener l) {
        pes.removeProgressListener(l);
    }        
  
    private int runProcess(String str, boolean wait) throws Exception {
        Process child = Runtime.getRuntime().exec(str);
        if (wait)
            child.waitFor();
        return child.exitValue();
    }
    private String makeProcessString(String str) {
        if (org.openide.util.Utilities.isWindows()){
            return "net " + str + " " + "https-admserv70"; // NOI18N
        }else{
            String process = str+"serv";
            return ((WS70SunDeploymentManager)dm).getServerLocation()+File.separator +
                    "admin-server" + File.separator+"bin" + File.separator + process; //NO I18N
        }
    }
    private void viewAdminLogs(){
        String uri = dm.getUri();
        String location = dm.getServerLocation();
        location = location+File.separator+"admin-server"+
                File.separator+"logs"+File.separator+"errors";

        WS70LogViewer logViewer = new WS70LogViewer(new File(location));
        
        try{
            logViewer.showLogViewer(UISupport.getServerIO(uri));
        }catch(Exception ex){
            ErrorManager.getDefault().notify(ErrorManager.WARNING, ex);
        }        
    }
}
