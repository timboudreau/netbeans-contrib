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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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

/*
 * WS70StartServer.java
 */

package org.netbeans.modules.j2ee.sun.ws7.j2ee;

import javax.enterprise.deploy.spi.DeploymentManager;
import javax.enterprise.deploy.spi.Target;
import javax.enterprise.deploy.spi.status.ProgressObject;

import org.netbeans.modules.j2ee.deployment.plugins.api.ServerDebugInfo;
import org.netbeans.modules.j2ee.deployment.plugins.spi.StartServer;
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
import org.netbeans.modules.j2ee.sun.ws7.Constants;
import org.openide.util.RequestProcessor;

import java.io.File;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
 /*
 * @author Mukesh Garg
 */
public class WS70StartServer extends StartServer implements ProgressObject, Runnable{
    
    private WS70SunDeploymentManager dm;    
    private ProgressEventSupport pes;    
    private WS70CommandType cmdType;
    private Target target;
    private static Map isDebugModeConfig = Collections.synchronizedMap((Map)new HashMap(2,1));
    
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
     * Starts the admin server. All diagnostic should be communicated
     * through ServerProgres with no exceptions thrown.
     *
     * @return ProgressObject object used to monitor start server progress
     */
    public ProgressObject startDeploymentManager() {         
        if(dm.isLocalServer()){
            cmdType = WS70CommandType.START;
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
     * Stops the admin server.
     * All diagnostic should be communicated through ServerProgres with no
     * exceptions thrown.
     * @return ServerProgress object used to monitor start server progress
     */
    public ProgressObject stopDeploymentManager() {
         if(dm.isLocalServer()){
            cmdType = WS70CommandType.STOP;
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
    public boolean isDebuggable(Target t) {        
        if(t==null){
            return false;
        }
        String config = null;
        try{
            config = this.getConfigNameFromTarget(t);
        }catch(Exception ex){            
            ex.printStackTrace();
            return false;
        }
        if(!isDebugModeConfig.containsKey(dm.getHost()+dm.getPort()+config)){
            return false;
        }
        
        if(!dm.isRunning(config)){
            isDebugModeConfig.remove(dm.getHost()+dm.getPort()+config);
            return false;
        }
        return true;
    }
    
    
    /**
     * Start or restart the target in debug mode.
     * 
     * All diagnostic should be communicated through ServerProgres with no exceptions thrown.
     * @param target the target server
     * @return ServerProgress object to monitor progress on start operation
     */
    public ProgressObject startDebugging(Target t) {        
         if(t!=null){
            this.target = t;
            cmdType = WS70CommandType.STARTTARGETDEBUG;
            pes.fireHandleProgressEvent(null,
                                    new Status(ActionType.EXECUTE, CommandType.START,
                                               NbBundle.getMessage(WS70StartServer.class, "MSG_STARTING_TARGET_SERVER_DEBUG"),
                                               StateType.RUNNING));
            
            RequestProcessor.getDefault().post(this, 0, Thread.NORM_PRIORITY);
            String config = null;
            try{
                config = this.getConfigNameFromTarget(t);
            }catch(Exception ex){            
                ex.printStackTrace();                
            }            
            isDebugModeConfig.put(dm.getHost()+dm.getPort()+config, new Object());
            return this;            
        }        
        return null;
    }
    

    /**
     * Returns the host/port necessary for connecting to the server's debug information.
     */    
    
    public ServerDebugInfo getDebugInfo(Target t) {        
        if(t==null){
            return null;
        }
        String uri = ((WS70SunDeploymentManager)dm).getUri();
        this.dm = org.netbeans.modules.j2ee.sun.ws7.dm.WS70SunDeploymentFactory.getConnectedCachedDeploymentManager(uri);        
        String debugOptions = this.dm.getDebugOptions();
        if(debugOptions==null){
            ErrorManager.getDefault().log(
                    ErrorManager.ERROR, NbBundle.getMessage(WS70StartServer.class, "ERR_DEBUG_OPTIONS_NULL"));
            return null;
        }        
        String nodeName = dm.getNodeNameForTarget(t);
        String address = debugOptions.substring(debugOptions.indexOf(Constants.DEBUG_OPTIONS_ADDRESS)+Constants.DEBUG_OPTIONS_ADDRESS.length(), debugOptions.length());
        int hasMore = address.indexOf("]"); //NOI18N
        if(hasMore != -1){ 
            address = address.substring(0, hasMore);
        }
        if(debugOptions.indexOf(Constants.ISDTSOCKET)!=-1){            
            int debugport = -1;
            try{
                debugport = Integer.parseInt(address.trim());
            }catch(NumberFormatException ex){
                ErrorManager.getDefault().log(
                    ErrorManager.ERROR, NbBundle.getMessage(WS70StartServer.class, "ERR_DTSOCKET_PORT_INVALID"));
                return null;
            }
            return new ServerDebugInfo(nodeName, debugport);
        }else if(debugOptions.indexOf(Constants.ISSHMEM)!=-1){            
            return new ServerDebugInfo(nodeName, address.trim());
        }else{
            ErrorManager.getDefault().log(
                    ErrorManager.ERROR, NbBundle.getMessage(WS70StartServer.class, "ERR_UNKNOW_DEBUG_OPTION"));
            return null;
        }
    }
    
    
    public boolean isRunning(Target t){        
        if(t==null){
            return true;
        }        
        String uri = ((WS70SunDeploymentManager)dm).getUri();
        this.dm = org.netbeans.modules.j2ee.sun.ws7.dm.WS70SunDeploymentFactory.getConnectedCachedDeploymentManager(uri);
        try{
            String configName = this.getConfigNameFromTarget(t);
            return ((WS70SunDeploymentManager)dm).isRunning(configName);        
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return true;
    }
    public ProgressObject startTarget(Target t){         
        if(t!=null){
            this.target = t;
            cmdType = WS70CommandType.STARTTARGET;
            pes.fireHandleProgressEvent(null,
                                    new Status(ActionType.EXECUTE, CommandType.START,
                                               NbBundle.getMessage(WS70StartServer.class, "MSG_STARTING_TARGET_SERVER"),
                                               StateType.RUNNING));
            
            RequestProcessor.getDefault().post(this, 0, Thread.NORM_PRIORITY);
            String config = null;
            try{
                config = this.getConfigNameFromTarget(t);
            }catch(Exception ex){            
                ex.printStackTrace();                
            }            
            isDebugModeConfig.remove(dm.getHost()+dm.getPort()+config);
            return this;            
        }        
        return null;
    }   
    public ProgressObject stoptTarget(Target t){            
        if(t!=null){
            this.target = t;
            cmdType = WS70CommandType.STOPTARGET;
            pes.fireHandleProgressEvent(null,
                                    new Status(ActionType.EXECUTE, CommandType.START,
                                               NbBundle.getMessage(WS70StartServer.class, "MSG_STOPPING_TARGET_SERVER"),
                                               StateType.RUNNING));
            
            RequestProcessor.getDefault().post(this, 0, Thread.NORM_PRIORITY);
            String config = null;
            try{
                config = this.getConfigNameFromTarget(t);
            }catch(Exception ex){            
                ex.printStackTrace();                
            }            
            isDebugModeConfig.remove(dm.getHost()+dm.getPort()+config);
            return this;            
        }        
        return null;    
    }   
    public boolean supportsStartTarget(Target t){        
        if(t==null){
            return false;
        }
        return true;
    }
    public boolean supportsStartDebugging(Target t){        
        if(t==null){
            return false;
        }
        return true;
    }
      
    // Ruunable run implementation
    public synchronized void run(){
        if (cmdType.equals(WS70CommandType.START)) {            
            try{
                runProcess(makeProcessString("start"), true); //NO I18N
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
        } else if(cmdType.equals(WS70CommandType.STOP)) {            
            try{
                runProcess(makeProcessString("stop"), true); //NO I18N
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
        }else if(cmdType.equals(WS70CommandType.STARTTARGET)) {                        
            try{                
                String configName = this.getConfigNameFromTarget(target); 
                dm.startServer(configName);
                pes.fireHandleProgressEvent(null,
                                new Status(ActionType.EXECUTE, cmdType,
                                           NbBundle.getMessage(WS70StartServer.class, "MSG_TARGET_SERVER_STARTED"),
                                           StateType.COMPLETED));
            }catch(Exception ex){
                pes.fireHandleProgressEvent(null,
                                 new Status(ActionType.EXECUTE, cmdType,
                                            ex.getLocalizedMessage(), 
                                            StateType.FAILED));
            }
            
        }else if(cmdType.equals(WS70CommandType.STOPTARGET)) {
            try{
                String configName = this.getConfigNameFromTarget(target);
                dm.stopServer(configName);
                pes.fireHandleProgressEvent(null,
                                new Status(ActionType.EXECUTE, cmdType,
                                           NbBundle.getMessage(WS70StartServer.class, "MSG_TARGET_SERVER_STOPPED"),
                                           StateType.COMPLETED));
            }catch(Exception ex){
                pes.fireHandleProgressEvent(null,
                               new Status(ActionType.EXECUTE, cmdType,
                                          ex.getLocalizedMessage(), 
                                          StateType.FAILED));
            }
            
        }else if(cmdType.equals(WS70CommandType.STARTTARGETDEBUG)) {
            try{                
                String configName = this.getConfigNameFromTarget(target);
                if(dm.isDebugModeEnabled()){
                    if(!dm.isRunning(configName)){ 
                        ErrorManager.getDefault().log(
                            ErrorManager.INFORMATIONAL, NbBundle.getMessage(WS70StartServer.class, "MSG_STARTING_TARGET_SERVER_DEBUG"));
                        //start server
                        dm.startServer(configName);
                        pes.fireHandleProgressEvent(null,
                                 new Status(ActionType.EXECUTE, cmdType,
                                            NbBundle.getMessage(WS70StartServer.class, "MSG_TARGET_SERVER_STARTED_DEBUG"),
                                            StateType.COMPLETED));
                    }else{
                        pes.fireHandleProgressEvent(null,
                                 new Status(ActionType.EXECUTE, cmdType,
                                            NbBundle.getMessage(WS70StartServer.class, "MSG_TARGET_SERVER_STARTED_DEBUG"),
                                            StateType.COMPLETED));                        
                    }
                }else{
                    if(dm.isRunning(configName)){  //stop target
                        ErrorManager.getDefault().log(
                            ErrorManager.INFORMATIONAL, NbBundle.getMessage(WS70StartServer.class, "MSG_STOPPING_TARGET_SERVER"));                        
                        pes.fireHandleProgressEvent(null,
                                 new Status(ActionType.EXECUTE, cmdType,
                                            NbBundle.getMessage(WS70StartServer.class, "MSG_RESTARTING_TARGET_SERVER_DEBUG"),
                                            StateType.RUNNING));                         
                        dm.stopServer(configName);                         
                        
                    }
                    // enable debug mode
                    ErrorManager.getDefault().log(
                        ErrorManager.INFORMATIONAL, NbBundle.getMessage(WS70StartServer.class, "MSG_TARGET_SERVER_SETTING_DEBUG_MODE"));                    
                    dm.changeDebugStatus(configName, true);
                    // start target
                    ErrorManager.getDefault().log(
                        ErrorManager.INFORMATIONAL, NbBundle.getMessage(WS70StartServer.class, "MSG_STARTING_TARGET_SERVER_DEBUG"));                    
                    dm.startServer(configName);
                    pes.fireHandleProgressEvent(null,
                             new Status(ActionType.EXECUTE, cmdType,
                                        NbBundle.getMessage(WS70StartServer.class, "MSG_TARGET_SERVER_STARTED_DEBUG"),
                                        StateType.COMPLETED));                      
                    
                }
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
    private String getConfigNameFromTarget(Target t) throws Exception{
        try{
            java.lang.reflect.Method getConfigName = t.getClass().getDeclaredMethod("getConfigName", new Class[]{});
            return (String)getConfigName.invoke(t, new Object[]{});
        }catch(Exception ex){
            throw ex;
        }        
    }    
}
