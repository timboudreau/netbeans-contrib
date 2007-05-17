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

package org.netbeans.modules.portalpack.servers.core;

import org.netbeans.modules.portalpack.servers.core.api.PSDeploymentManager;
import org.netbeans.modules.portalpack.servers.core.api.PSStartServerFactory;
import org.netbeans.modules.portalpack.servers.core.api.PSStartServerInf;
import org.netbeans.modules.portalpack.servers.core.util.ProgressEventSupport;
import org.netbeans.modules.portalpack.servers.core.util.Status;
import org.netbeans.modules.portalpack.servers.core.util.NetbeanConstants;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.deploy.shared.ActionType;
import javax.enterprise.deploy.shared.CommandType;
import javax.enterprise.deploy.shared.StateType;
import javax.enterprise.deploy.spi.DeploymentManager;
import javax.enterprise.deploy.spi.Target;
import javax.enterprise.deploy.spi.TargetModuleID;
import javax.enterprise.deploy.spi.exceptions.OperationUnsupportedException;
import javax.enterprise.deploy.spi.status.ClientConfiguration;
import javax.enterprise.deploy.spi.status.DeploymentStatus;
import javax.enterprise.deploy.spi.status.ProgressListener;
import javax.enterprise.deploy.spi.status.ProgressObject;
import org.netbeans.modules.j2ee.deployment.plugins.api.ServerDebugInfo;
import org.netbeans.modules.j2ee.deployment.plugins.spi.StartServer;
import org.netbeans.modules.j2ee.deployment.plugins.api.UISupport;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;

/**
 *
 * @author Satya
 */
public  class PSStartServer extends StartServer  implements ProgressObject, Runnable{
    private static Logger logger = Logger.getLogger(NetbeanConstants.PORTAL_LOGGER);
    private PSDeploymentManager dm;
    private ProgressEventSupport pes;
    private PSCommandType cmdType;
    private Target target;
    private PSStartServerInf startServerHandler;
    
    public PSStartServer(DeploymentManager dm) {
        this.dm = (PSDeploymentManager)dm;
        pes = new ProgressEventSupport(this);
        startServerHandler = ((PSDeploymentManager)dm).getStartServerHandler();
    }
    public ProgressObject startDebugging(Target target) {
        if(dm.isLocalServer()){
            cmdType = PSCommandType.STARTTARGETDEBUG;
            pes.fireHandleProgressEvent(null,
                    new Status(ActionType.EXECUTE, CommandType.START,
                    NbBundle.getMessage(PSStartServer.class, "MSG_STARTING_ADMIN_SERVER"),
                    StateType.RUNNING));
            
            logger.log(Level.FINEST,"Starting .....................deployment manager.........");
            RequestProcessor.getDefault().post(this, 0, Thread.NORM_PRIORITY);
            return this;
        }else{
            return null;
        }
    }
    
    public boolean isDebuggable(Target target) {
        if(dm.isRunning() && startServerHandler.getDebugPort() != 0) //to cross check if the debug implementation is done or not
          return true;
        else
          return false;
    }
    
    /**
     * Returns true if the admin server is also the given target server (share the same vm).
     * Start/stopping/debug apply to both servers.
     * @param the target server in question
     * @return true when admin is also target server
     */
    public boolean isAlsoTargetServer(Target target) {
        return true;
    }
    
    public ServerDebugInfo getDebugInfo(Target target) {
       int debugPort = startServerHandler.getDebugPort();
       if(debugPort == 0)
           return null;
       return new ServerDebugInfo(dm.getPSConfig().getHost(),debugPort);
       // return null;
    }
    /* return true if admin server can be started through this spi */
    public boolean supportsStartDeploymentManager() {
        if(dm.isLocalServer()){
            return true;
        }
        return false;
    }
    
    /**
     * Stops the admin server.
     * All diagnostic should be communicated through ServerProgres with no
     * exceptions thrown.
     * @return ServerProgress object used to monitor start server progress
     */
    
    public ProgressObject stopDeploymentManager() {
        
        if(dm.isLocalServer()){
            cmdType = PSCommandType.STOP;
            pes.fireHandleProgressEvent(null,
                    new Status(ActionType.EXECUTE, CommandType.STOP,
                    NbBundle.getMessage(PSStartServer.class, "MSG_STOPPING_ADMIN_SERVER"),
                    StateType.RUNNING));
            
            RequestProcessor.getDefault().post(this, 0, Thread.NORM_PRIORITY);
            return this;
        }else{
            return null;
        }
        
    }
    
    public ProgressObject startDeploymentManager() {
        if(dm.isLocalServer()){
            cmdType = PSCommandType.START;
            pes.fireHandleProgressEvent(null,
                    new Status(ActionType.EXECUTE, CommandType.START,
                    NbBundle.getMessage(PSStartServer.class, "MSG_STARTING_ADMIN_SERVER"),
                    StateType.RUNNING));
            
            logger.log(Level.FINEST,"Starting .....................deployment manager.........");
            RequestProcessor.getDefault().post(this, 0, Thread.NORM_PRIORITY);
            return this;
        }else{
            return null;
        }
    }
    
    /**
     * Returns true if the admin server should be started before asking for
     * target list.
     */
    public boolean needsStartForTargetList() {
        return false;
    }
    
    /**
     * Returns true if the admin server should be started before server deployment configuration.
     */
    public boolean needsStartForConfigure() {
        return false;
    }
    /**
     * Returns true if the admin server should be started before admininistrative configuration.
     */
    
    public boolean needsStartForAdminConfig() {
        return false;
    }
    
    
    public boolean isRunning() {
        return ((PSDeploymentManager)dm).isRunning();
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
    
    public boolean isRunning(Target t){
        
        return isRunning();
        
    }
    public ProgressObject startTarget(Target t){
        logger.log(Level.FINEST,"Inside Start Target...");
        
        return null;
        
    }
    public ProgressObject stoptTarget(Target t){
        
        return null;
    }
    public boolean supportsStartTarget(Target t){
        
        logger.log(Level.FINEST,"Inside Support start target ***"+t);
        if(t==null){
            return false;
        }
        return false;
    }
    public boolean supportsStartDebugging(Target t){
      //  if(t==null){
        //    return false;
        //}
        if(startServerHandler.getDebugPort() != 0)
            return true;
        else
            return false;
    }
    
    // Ruunable run implementation
    public synchronized void run(){
        
        Object lock = new Object();
        if (cmdType.equals(PSCommandType.START)) {
            
            logger.log(Level.FINEST,"Just before starting server ****");
            try{
                
                
                //start admin server
                writeToOutput(org.openide.util.NbBundle.getMessage(PSStartServer.class, "MSG_STARTING_ADMIN_SERVER"));
                startServerHandler.startServer(); 
                pes.fireHandleProgressEvent(null,
                        new Status(ActionType.EXECUTE, cmdType,
                        NbBundle.getMessage(PSStartServer.class,"MSG_ADMIN_SERVER_STARTED"),
                        StateType.COMPLETED));
            }catch(Exception ex){
                pes.fireHandleProgressEvent(null,
                        new Status(ActionType.EXECUTE, cmdType,
                        ex.getLocalizedMessage(),
                        StateType.FAILED));
                
            }
        } else if(cmdType.equals(PSCommandType.STOP)) {
            try{
                            
                startServerHandler.stopServer();
                
                pes.fireHandleProgressEvent(null,
                        new Status(ActionType.EXECUTE, cmdType,
                        NbBundle.getMessage(PSStartServer.class,"MSG_ADMIN_SERVER_STOPPED"),
                        StateType.COMPLETED));
            }catch(Exception ex){
                pes.fireHandleProgressEvent(null,
                        new Status(ActionType.EXECUTE, cmdType,
                        ex.getLocalizedMessage(),
                        StateType.FAILED));
                
            }
        }else if(cmdType.equals(PSCommandType.STARTTARGET)) {
            
            //logger.log(Level.INFO,org.openide.util.NbBundle.getMessage(PSStartServer.class, "MSG_TARGET_STARTED"));
            
        }else if(cmdType.equals(PSCommandType.STOPTARGET)) {
           
            
        }else if(cmdType.equals(PSCommandType.STARTTARGETDEBUG)) {
            logger.log(Level.FINEST,"Just before starting debug server ****");
            try{
                 
                //start admin server
                writeToOutput(org.openide.util.NbBundle.getMessage(PSStartServer.class, "MSG_STARTING_ADMIN_SERVER"));
                startServerHandler.startDebug(); 
                pes.fireHandleProgressEvent(null,
                        new Status(ActionType.EXECUTE, cmdType,
                        NbBundle.getMessage(PSStartServer.class,"MSG_ADMIN_SERVER_STARTED"),
                        StateType.COMPLETED));
            }catch(Exception ex){
                pes.fireHandleProgressEvent(null,
                        new Status(ActionType.EXECUTE, cmdType,
                        ex.getLocalizedMessage(),
                        StateType.FAILED));
                
            }
        }
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
   
    private void writeToOutput(String msg) {
        String uri = dm.getUri();
        msg = org.openide.util.NbBundle.getMessage(PSStartServer.class, "MSG_PORTALPACK_MSG")+msg;
        UISupport.getServerIO(uri).getOut().println(msg);
    }
    private String getConfigNameFromTarget(Target t) throws Exception{
        try{
            java.lang.reflect.Method getConfigName = t.getClass().getDeclaredMethod("getConfigName", new Class[]{});
            return (String)getConfigName.invoke(t, new Object[]{});
        }catch(Exception ex){
            throw ex;
        }
    } 
}
