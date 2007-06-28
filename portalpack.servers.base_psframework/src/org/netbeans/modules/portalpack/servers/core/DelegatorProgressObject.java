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

import org.netbeans.modules.portalpack.servers.core.util.ProgressEventSupport;
import org.netbeans.modules.portalpack.servers.core.util.Status;
import org.netbeans.modules.portalpack.servers.core.util.NetbeanConstants;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.deploy.shared.ActionType;
import javax.enterprise.deploy.shared.CommandType;
import javax.enterprise.deploy.shared.StateType;
import javax.enterprise.deploy.spi.TargetModuleID;
import javax.enterprise.deploy.spi.exceptions.OperationUnsupportedException;
import javax.enterprise.deploy.spi.status.ClientConfiguration;
import javax.enterprise.deploy.spi.status.DeploymentStatus;
import javax.enterprise.deploy.spi.status.ProgressListener;
import javax.enterprise.deploy.spi.status.ProgressObject;

/**
 *
 * @author Satyaranjan
 */
public class DelegatorProgressObject implements ProgressObject{

    private static Logger logger = Logger.getLogger(NetbeanConstants.PORTAL_LOGGER);
    private ProgressEventSupport pes;
    /** Creates a new instance of DelegatorProgressObject */
    public DelegatorProgressObject() {

        pes = new ProgressEventSupport(this);
    }


     public ProgressObject startModule(TargetModuleID[] modules)
     {
          logger.log(Level.FINEST,"Inside StartModule......");
          pes.fireHandleProgressEvent(null,
                    new Status(ActionType.EXECUTE, CommandType.START,
                     org.openide.util.NbBundle.getMessage(DelegatorProgressObject.class, "MSG_START_MODULE_SERVER"),
                    StateType.RUNNING));

          Thread t2 = new Thread(){

              public void run(){
                pes.fireHandleProgressEvent(null,
                            new Status(ActionType.EXECUTE, CommandType.STOP,
                            org.openide.util.NbBundle.getMessage(DelegatorProgressObject.class, "MODULE_STOPPED"),
                            StateType.COMPLETED));
              }
          };
          t2.start();
           return this;
     }


     public ProgressObject stopModule(TargetModuleID[] modules)
     {
          pes.fireHandleProgressEvent(null,
                    new Status(ActionType.EXECUTE, CommandType.START,
                     org.openide.util.NbBundle.getMessage(DelegatorProgressObject.class, "MSG_START_MODULE_SERVER"),
                    StateType.RUNNING));

          Thread t2 = new Thread(){

              public void run(){
                pes.fireHandleProgressEvent(null,
                            new Status(ActionType.EXECUTE, CommandType.STOP,
                            org.openide.util.NbBundle.getMessage(DelegatorProgressObject.class, "MODULE_STOPPED"),
                            StateType.COMPLETED));
              }
          };
          t2.start();
           return this;

     }

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

}
