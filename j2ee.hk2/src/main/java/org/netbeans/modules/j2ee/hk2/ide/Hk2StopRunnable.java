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

import javax.enterprise.deploy.shared.ActionType;
import javax.enterprise.deploy.shared.CommandType;
import javax.enterprise.deploy.shared.StateType;
import org.netbeans.modules.j2ee.deployment.plugins.api.InstanceProperties;
import org.netbeans.modules.j2ee.hk2.Hk2DeploymentManager;
import org.netbeans.modules.j2ee.hk2.Hk2StartServer;



import org.openide.util.NbBundle;

/**
 * @author Ludo
 */
public class Hk2StopRunnable implements Runnable {
    
    private Hk2DeploymentManager dm;
    private Hk2StartServer startServer;
    private InstanceProperties ip;
    private String instanceName;
    
    /**
     * The amount of time in milliseconds during which the server should
     * stop
     */
    private static final int TIMEOUT = 120000;
    
    /**
     * The amount of time in milliseconds that we should wait between checks
     */
    private static final int DELAY = 600;
    
    /**
     * 
     * @param dm 
     * @param startServer 
     */
    public Hk2StopRunnable(Hk2DeploymentManager dm, Hk2StartServer startServer) {
        this.dm = dm;
        this.ip = dm.getProperties().getInstanceProperties();
        this.instanceName = ip.getProperty(InstanceProperties.DISPLAY_NAME_ATTR);
        this.startServer = startServer;
    }
    
    /**
     * 
     */
    public void run() {
        // save the current time so that we can deduct that the startup
        // failed due to timeout
        long start = System.currentTimeMillis();
        
        Hk2ManagerImpl h = new Hk2ManagerImpl (dm);
        h.stopServer(null);
        
        fireStartProgressEvent(StateType.RUNNING, createProgressMessage("MSG_STOP_SERVER_IN_PROGRESS", instanceName));
        
        // create a logger to the server's output stream so that a user
        // can observe the progress
////        Hk2Logger.getInstance(dm.getUri()).readInputStreams(new InputStream[] {
////            serverProcess.getInputStream(), serverProcess.getErrorStream()});
        
        // Waiting for server to start
        while (System.currentTimeMillis() - start < TIMEOUT) {
            // Send the 'completed' event and return when the server is stopped
            if (!startServer.isRunning()) {
                try {
                    Thread.sleep(1000);//flush the process
                } catch (InterruptedException e) {}
                fireStartProgressEvent(StateType.COMPLETED, createProgressMessage("MSG_SERVER_STOPPED", instanceName)); // NOI18N
                return;
            }
            
            // Sleep for a little so that we do not make our checks too
            // Often
            try {
                Thread.sleep(DELAY);
            } catch (InterruptedException e) {}
            
            fireStartProgressEvent(StateType.RUNNING,createProgressMessage("MSG_STOP_SERVER_IN_PROGRESS", instanceName));
        }
        
        fireStartProgressEvent(StateType.FAILED, createProgressMessage("MSG_STOP_SERVER_FAILED", instanceName));//NOI18N
        
    }
    


    

    
    private String createProgressMessage(final String resName) {
        return createProgressMessage(resName, null);
    }
    
    private String createProgressMessage(final String resName, final String param) {
        return NbBundle.getMessage(Hk2StartRunnable.class, resName, instanceName, param);
    }
    
    private void fireStartProgressEvent(StateType stateType, String msg) {
        startServer.fireHandleProgressEvent(null, new Hk2DeploymentStatus(ActionType.EXECUTE, CommandType.STOP, stateType, msg));
    }
}