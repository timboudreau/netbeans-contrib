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
 * 
 * Contributor(s):
 * 
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */

package org.netbeans.modules.j2ee.geronimo2.nodes;

import java.util.Comparator;
import javax.enterprise.deploy.shared.CommandType;
import javax.enterprise.deploy.shared.StateType;
import javax.enterprise.deploy.spi.DeploymentManager;
import javax.enterprise.deploy.spi.TargetModuleID;
import javax.enterprise.deploy.spi.status.DeploymentStatus;
import javax.enterprise.deploy.spi.status.ProgressEvent;
import javax.enterprise.deploy.spi.status.ProgressListener;
import javax.enterprise.deploy.spi.status.ProgressObject;
import org.netbeans.modules.j2ee.geronimo2.GeDeploymentManager;
import org.netbeans.modules.j2ee.geronimo2.nodes.actions.GeWebModuleCookie;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.StatusDisplayer;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;
import org.openide.util.Parameters;
import org.openide.util.RequestProcessor;
import org.openide.util.RequestProcessor.Task;

/**
 * Cookie holding tasks aviable over a web module node
 * @author Max Sauer
 */
public class GeWebModule implements GeWebModuleCookie {
    
    private final TargetModuleID targetModule;
    private volatile boolean isRunning;
    private final GeDeploymentManager manager;
    private final TargetModuleID[] target;
    private Node node;
    
    /** Creates a new instance of GeWebModule */
    public GeWebModule(DeploymentManager manager, TargetModuleID targetModule, boolean isRunning) {
        this.targetModule = targetModule;
        this.manager = (GeDeploymentManager) manager;
        this.isRunning = isRunning;
        target = new TargetModuleID[]{targetModule};
    }
    
    
    /** Simple comparator for sorting nodes by name. */
    public static final Comparator<GeWebModule> GE_WEB_MODULE_COMPARATOR = new Comparator<GeWebModule>() {

        public int compare(GeWebModule wm1, GeWebModule wm2) {
            return wm1.getGeModule ().getModuleID().compareTo(wm2.getGeModule ().getModuleID());
        }
    };

    public void setRepresentedNode(Node node) {
        this.node = node;
    }
    
    public Node getRepresentedNode() {
        return node;
    }

    public TargetModuleID getGeModule() {
        return targetModule;
    }

    //--- Node.Cookie implements
    //TODO: implement methods (tasks @see GeWebModuleCookie) over module nodes
    
    /**
     * Undeploys the web application described by this module.
     *
     * @return task processing undepl. 
     * When the task is finished it implicate that undeployment is finished
     * (failed or completed).
     */
    public Task undeploy() {
        return RequestProcessor.getDefault().post(new Runnable() {
            public void run () {
                StatusDisplayer.getDefault().setStatusText(NbBundle.getMessage(GeWebModule.class, "MSG_START_UNDEPLOY",  // NOI18N
                    new Object [] { getGeModule().getWebURL() })); //URL as module name?

                ProgressObject po = manager.undeploy(target);
                GeProgressListener listener = new GeProgressListener(po);
                po.addProgressListener(listener);
                listener.updateState();

                CompletionWait wait = new CompletionWait(po);
                wait.init();
                wait.waitFinished();
            }
        }, 0);
    }

    public void start() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void stop() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void openLog() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean hasLogger() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    //------- Helper classes
    
    private class GeProgressListener implements ProgressListener {

        private final ProgressObject progressObject;

        private boolean finished;

        public GeProgressListener(ProgressObject progressObject) {
            this.progressObject = progressObject;
        }

        public void handleProgressEvent(ProgressEvent progressEvent) {
            updateState();
        }

        public synchronized void updateState() {
            if (finished) {
                return;
            }

            DeploymentStatus deployStatus = progressObject.getDeploymentStatus();
            if (deployStatus == null) {
                return;
            }

            if (deployStatus.isCompleted() || deployStatus.isFailed()) {
                finished = true;
            }

            if (deployStatus.getState() == StateType.COMPLETED) {
                CommandType command = deployStatus.getCommand();

                if (command == CommandType.START || command == CommandType.STOP) {
                        StatusDisplayer.getDefault().setStatusText(deployStatus.getMessage());
                        if (command == CommandType.START) {
                            isRunning = true;
                        } else {
                            isRunning = false;
                        }
                        node.setDisplayName(constructDisplayName());
                } else if (command == CommandType.UNDEPLOY) {
                    StatusDisplayer.getDefault().setStatusText(deployStatus.getMessage());
                }
            } else if (deployStatus.getState() == StateType.FAILED) {
                NotifyDescriptor notDesc = new NotifyDescriptor.Message(
                        deployStatus.getMessage(),
                        NotifyDescriptor.ERROR_MESSAGE);
                DialogDisplayer.getDefault().notify(notDesc);
                StatusDisplayer.getDefault().setStatusText(deployStatus.getMessage());
            }
        }
        
        private String constructDisplayName() {
            if (isRunning())
                return getGeModule().getWebURL();
            else
                return getGeModule().getWebURL() + " [" + NbBundle.getMessage(GeWebModuleNode.class, "LBL_Stopped") // NOI18N
                        + "]";
        }
    }
    
    /**
     * Helper class for blocking wait until the deployment manager operation
     * gets finished.
     * <p>
     * The class is <i>thread safe</i>.
     *
     * @author Petr Hejl
     */
    private static class CompletionWait implements ProgressListener {

        private final ProgressObject progressObject;

        private boolean completed;

        /**
         * Constructs the CompletionWait object that will wait for
         * given ProgressObject.
         *
         * @param progressObject object that we want to wait for
         *             must not be <code>null</code>
         */
        public CompletionWait(ProgressObject progressObject) {
            Parameters.notNull("progressObject", progressObject);

            this.progressObject = progressObject;
        }

        /**
         * Initialize this object. Until calling this method any thread that
         * has called {@link #waitFinished()} will wait unconditionaly (does not
         * matter what is the state of the ProgressObject.
         */
        public void init() {
            synchronized (this) {
                progressObject.addProgressListener(this);
                // to be sure we didn't missed the state
                handleProgressEvent(null);
            }
        }

        /**
         * Handles the progress. May lead to notifying threads waiting in
         * {@link #waitFinished()}.
         *
         * @param evt event to handle
         */
        public void handleProgressEvent(ProgressEvent evt) {
            synchronized (this) {
                DeploymentStatus status = progressObject.getDeploymentStatus();
                if (status.isCompleted() || status.isFailed()) {
                    completed = true;
                    notifyAll();
                }
            }
        }

        /**
         * Block the calling thread until the progress object indicates the
         * competion or failure. If the task described by ProgressObject is
         * already finished returns immediately.
         */
        public void waitFinished() {
            synchronized (this) {
                if (completed) {
                    return;
                }

                while (!completed) {
                    try {
                        wait();
                    } catch (InterruptedException ex) {
                        // don't response to interrupt
                    }
                }
            }
        }
    }
}
