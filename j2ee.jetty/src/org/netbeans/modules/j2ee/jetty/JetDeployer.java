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

package org.netbeans.modules.j2ee.jetty;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
import org.netbeans.modules.j2ee.deployment.common.api.ConfigurationException;
import org.netbeans.modules.j2ee.jetty.config.gen.Configure;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.JarFileSystem;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 * This class implements deployment of web project to a web container and
 * implements ProgressObject which is neccessy for JSR-88.
 * @author novakm
 */
public class JetDeployer implements ProgressObject {

//	private DeploymentStatus status;
	/** TargetModuleID of module that is managed. */
	private JetModule tmId;
	/** Command type used for events. */
	private CommandType cmdType;
        private static final Logger LOGGER = Logger.getLogger(JetDeployer.class.getName());

	/**
	 * <p> This method deployes web module from NetBeans to server web container. </p>
	 * @param t - Target representing the server we are trying to deploy to
	 * @param warFile - project war file
	 * @param dest - String representing destination path
	 */
	public void deploy(Target t, File warFile, String dest) {
            String contextPath = null;
            cmdType = CommandType.DISTRIBUTE;
            String msg = NbBundle.getMessage(JetDeployer.class, "MSG_DeploymentInProgress");
            try {
                JarFileSystem jfs = new JarFileSystem();
                jfs.setJarFile(warFile);
                FileObject webXml = jfs.getRoot().getFileObject("WEB-INF/jetty-web.xml"); // NOI18N
                if (webXml != null) {
                    InputStream is = webXml.getInputStream();
                    try {
                        contextPath = Configure.createGraph(is).getContextRoot();
                    } catch (ConfigurationException ce) {
                        LOGGER.log(Level.WARNING, "No ContextPath was found in jetty-web.xml", ce);
                    } finally {
                        is.close();
                    }
                }
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "WAR doesn't exist!", e);
                status = (DeploymentStatus) new JetDeploymentStatus(ActionType.EXECUTE, cmdType, StateType.FAILED, msg);
                return;
            }
            String[] parts = contextPath.split("/");
            //create folder
            if (parts.length > 2) {
                String path = "";
                for (int i = 1; i < (parts.length - 1); i++) {
                    path += (File.separator + parts[i]);
                }
                File folder = new File(dest + path);
                folder.mkdir();
            }

            //create destination war file named according to .war file name
            File dstFile = new File(dest + File.separator + warFile.getName());

            //copy war from netbeans project to server
            InputStream in = null;
            try {
                in = new FileInputStream(warFile);
                OutputStream out = new FileOutputStream(dstFile);

                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                in.close();
                out.close();
                tmId = new JetModule(t, warFile.getName().replace(".war", ""), null, contextPath, dstFile.getAbsolutePath());
                status = (DeploymentStatus) new JetDeploymentStatus(ActionType.EXECUTE, cmdType, StateType.COMPLETED, msg);
            } catch (IOException e) {
            } finally {
                try {
                    in.close();
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
            try {
//                try to delete the deployment plan which is not used anyway
                JarFileSystem jfs = new JarFileSystem();
                jfs.setJarFile(dstFile);    
                jfs.getRoot().getFileObject("JetServer.dpf").delete();
            } catch (Exception e) {
                LOGGER.log(Level.FINE, "Unable to delete deployment plan file");
            }
	}
	
	private DeploymentStatus status;

	/**
	 * Method returning deployment status
	 * @return status
	 */
	public DeploymentStatus getDeploymentStatus() {
		return status;
//		throw new UnsupportedOperationException("Not supported yet. getDeploymentStatus");

	}

	/**
         * Retrievee the list of TargetModuleIDs successfully processed.
	 * @return tmID
	 */
	public TargetModuleID[] getResultTargetModuleIDs() {
		return new TargetModuleID[]{tmId};
	}

	/**
         * {@inheritDoc}
	 */
	public ClientConfiguration getClientConfiguration(TargetModuleID arg0) {
		throw new UnsupportedOperationException("Not supported yet. getClientConfiguration");
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isCancelSupported() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 * @throws javax.enterprise.deploy.spi.exceptions.OperationUnsupportedException
	 */
	public void cancel() throws OperationUnsupportedException {
		throw new UnsupportedOperationException("Not supported yet. isCancelSupported");
	}

	/**
	 * {@inheritDoc}
	 * @return false
	 */
	public boolean isStopSupported() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	public void stop() throws OperationUnsupportedException {
		throw new OperationUnsupportedException("stop not supported in Jetty deployment"); // NOI18N

	}

	/**
	 * {@inheritDoc}
	 */
	public void addProgressListener(ProgressListener pl) {
		return; //UnsupportedOperationException("Not supported yet. addProgressListener");

	}

	/**
	 * {@inheritDoc}
	 */
	public void removeProgressListener(ProgressListener pl) {
		return; //throw new UnsupportedOperationException("Not supported yet. removeProgressListener");

	}
        
        /**
         * Method just sets status of undeploy operation given parameter
         * @param jm - the JetModule being undeployed
         * @param st - what state should be set
         */
        protected void undeployModuleResult(JetModule jm, StateType st) {
                this.tmId = jm;
                cmdType = CommandType.START;
                String msg = NbBundle.getMessage(JetModule.class, "MSG_UNDEPLOY_IN_PROGRESS");
                status = (DeploymentStatus) new JetDeploymentStatus(ActionType.EXECUTE, CommandType.UNDEPLOY, st, msg);
        }
        
        /**
         * Method just sets status of start operation given parameter
         * @param jm - the JetModule being undeployed
         * @param st - what state should be set
         */
        protected void startModuleResult(JetModule jm, StateType st) {
                this.tmId = jm;
                cmdType = CommandType.START;
                String msg = NbBundle.getMessage(JetModule.class, "MSG_START_IN_PROGRESS");
                status = (DeploymentStatus) new JetDeploymentStatus(ActionType.EXECUTE, CommandType.START, st, msg);
        }
        
        /**
         * Method just sets status of start operation given parameter
         * @param jm - the JetModule being undeployed
         * @param st - what state should be set
         */
        protected void stopModuleResult(JetModule jm, StateType st) {
                this.tmId = jm;
                cmdType = CommandType.STOP;
                String msg = NbBundle.getMessage(JetModule.class, "MSG_STOP_IN_PROGRESS");
                status = (DeploymentStatus) new JetDeploymentStatus(ActionType.EXECUTE, CommandType.STOP, st, msg);
        }
}
