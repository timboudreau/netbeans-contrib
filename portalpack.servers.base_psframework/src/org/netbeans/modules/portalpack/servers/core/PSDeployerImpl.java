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
import org.netbeans.modules.portalpack.servers.core.PSCommandType;
import org.netbeans.modules.portalpack.servers.core.PSModuleID;
import org.netbeans.modules.portalpack.servers.core.PSDeployer;
import org.netbeans.modules.portalpack.servers.core.util.ProgressEventSupport;
import org.netbeans.modules.portalpack.servers.core.util.Status;
import org.netbeans.modules.portalpack.servers.core.util.NetbeanConstants;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.deploy.shared.ActionType;
import javax.enterprise.deploy.shared.CommandType;
import javax.enterprise.deploy.shared.StateType;
import javax.enterprise.deploy.spi.Target;
import javax.enterprise.deploy.spi.TargetModuleID;
import javax.enterprise.deploy.spi.exceptions.OperationUnsupportedException;
import javax.enterprise.deploy.spi.status.ClientConfiguration;
import javax.enterprise.deploy.spi.status.DeploymentStatus;
import javax.enterprise.deploy.spi.status.ProgressListener;
import javax.enterprise.deploy.spi.status.ProgressObject;
import javax.management.MBeanException;
import org.netbeans.modules.j2ee.deployment.plugins.api.UISupport;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;

/**
 *
 * @author Satya
 */
public class PSDeployerImpl implements PSDeployer, Runnable{
    
    private static Logger logger = Logger.getLogger(NetbeanConstants.PORTAL_LOGGER);
    private PSCommandType cmdType;
    private ProgressEventSupport pes;
    private PSDeploymentManager dm;
    private PSModuleID module_id;
    private String host;
    private int port;
    private File deployFile;
    private File archiveFile;
    
    public PSDeployerImpl(PSDeploymentManager dm,String host,int port) {
       pes = new ProgressEventSupport(this);     
       this.dm = dm;
    }

    public ProgressObject deploy(Target target, File file1, File file2) {
    
         
        logger.log(Level.FINEST,"Inside Deploy of Deploy71.....");
         
         module_id = new PSModuleID(target, file1.getName() );
        
       
            String server_url = "http://" + host+":"+port;
            
            if (file1.getName().endsWith(".war")) {
               // module_id.setContextURL( server_url));
            }  

         this.deployFile = file1;
         this.archiveFile = file2;
         
         cmdType = PSCommandType.DISTRIBUTE;
         pes.fireHandleProgressEvent(null,
                                    new Status(ActionType.EXECUTE, CommandType.DISTRIBUTE,
                                               NbBundle.getMessage(PSDeployerImpl.class, "START_DEPLOY"),
                                               StateType.RUNNING));

         RequestProcessor.getDefault().post(this, 0, Thread.NORM_PRIORITY);
         return this;
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
        return new TargetModuleID[] {module_id};
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
  

    public void run() {
        
        if(cmdType.equals(PSCommandType.DISTRIBUTE))
        {
           // selectIOTab(dm.getUri());
            try {
                if(!deployFile.isDirectory())
                    dm.getTaskHandler().deploy(deployFile.getAbsolutePath(),dm.getUri());    
                else {
                    dm.getTaskHandler().deploy(deployFile.getAbsolutePath(), archiveFile.getAbsolutePath(), dm.getUri());
                }
                writeToOutput(dm.getUri(),org.openide.util.NbBundle.getMessage(PSDeployerImpl.class, "MSG_DEPLOYED"));
               
            } catch (Exception ex) {
                logger.log(Level.SEVERE,"Deployment Error",ex);
                writeErrorStackToOutput(dm.getUri(),ex);
                writeErrorToOutput(dm.getUri(),org.openide.util.NbBundle.getMessage(PSDeployerImpl.class,"MSG_DEPLOYMENT_FAILED")
                                         + " : " +ex.getMessage());
                pes.fireHandleProgressEvent(null,
                               new Status(ActionType.EXECUTE, cmdType,
                                           org.openide.util.NbBundle.getMessage(PSDeployerImpl.class, "MSG_DEPLOYMENT_FAILED"),
                                           StateType.FAILED));
                 return;
            }  
            
            pes.fireHandleProgressEvent(null,
                                new Status(ActionType.EXECUTE, cmdType,
                                           NbBundle.getMessage(PSDeployerImpl.class, "MSG_DEPLOYED"),
                                           StateType.COMPLETED));
        }
    }
    
    
    private void writeToOutput(String uri,String msg)
    {
        msg = org.openide.util.NbBundle.getMessage(PSDeployerImpl.class, "MSG_PORTALPACK")+msg;
        UISupport.getServerIO(uri).getOut().println(msg);
    }
    
    private void writeErrorToOutput(String uri,String msg)
    {
        msg = org.openide.util.NbBundle.getMessage(PSDeployerImpl.class, "MSG_PORTALPACK")+msg;
        UISupport.getServerIO(uri).getErr().println(msg);
    }
    
    private void writeErrorStackToOutput(String uri,Exception e) {
        e.printStackTrace(UISupport.getServerIO(uri).getErr());
    }
    
    private void selectIOTab(String uri)
    {
        UISupport.getServerIO(uri).select();
    }

    public ProgressObject startModule(TargetModuleID[] module) {
        logger.log(Level.FINEST,">>>>>>>>>>>>>>>> Inside startModule");
        return this;
    }

    public ProgressObject stopModule(TargetModuleID[] module) {
        return this;
    }
    
    

    public ProgressObject undeploy(final String portletAppName, final String dn) {
         cmdType = PSCommandType.UNDEPLOY;
         pes.fireHandleProgressEvent(null,
                                    new Status(ActionType.EXECUTE, CommandType.UNDEPLOY,
                                               NbBundle.getMessage(PSDeployerImpl.class, "START_UNDEPLOY"),
                                               StateType.RUNNING));

          RequestProcessor.getDefault().post(new Runnable() {
            public void run () {
                
              //  selectIOTab(dm.getUri());
                try {
                    dm.getTaskHandler().undeploy(portletAppName,dn);
                    writeToOutput(dm.getUri(),portletAppName +org.openide.util.NbBundle.getMessage(PSDeployerImpl.class, "MSG_UNDEPLOYED_SUCCESSFULLY"));
                } catch (Exception ex) {
                    writeErrorStackToOutput(dm.getUri(),ex);
                    logger.log(Level.SEVERE,"Deployment failed for application "+portletAppName,ex);
                    writeToOutput(dm.getUri(),portletAppName + org.openide.util.NbBundle.getMessage(PSDeployerImpl.class, "MSG_UNDEPLYOMENT_FAILED"));
                    pes.fireHandleProgressEvent(null,
                                new Status(ActionType.EXECUTE, cmdType,
                                           org.openide.util.NbBundle.getMessage(PSDeployerImpl.class, "MSG_UNDEPLYOMENT_FAILED"),
                                           StateType.FAILED));
                    return;
                }
                
                
                pes.fireHandleProgressEvent(null,
                                new Status(ActionType.EXECUTE, cmdType,
                                           NbBundle.getMessage(PSDeployerImpl.class, "MSG_UNDEPLOYED"),
                                           StateType.COMPLETED));
            }
        }, 0);
         return this;
    }

   public ProgressObject createChannel(String dn, String portletName, String channelName) {
        try {
            dm.getTaskHandler().createChannel(dn,portletName,channelName);
        } catch (MBeanException ex) {
            logger.log(Level.SEVERE,"Error",ex);
        } catch (Exception ex) {
            logger.log(Level.SEVERE,"Error",ex);
        }
        return this;
   }

    public ProgressObject createContainer(String containerName, String dn) {
        return this;
    }
    

    public ProgressObject createContainer(String dn, String container, String provider) {
        return this;
    }
       
}

