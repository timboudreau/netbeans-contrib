/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2009 Sun Microsystems, Inc. All rights reserved.
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
 * John Platts
 *
 * Portions Copyrighted 2009 Sun Microsystems, Inc.
 * Portions Copyrighted 2009 John Platts
 */
package org.netbeans.modules.portalpack.servers.core.impl.j2eeservers.jboss;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.deploy.spi.Target;
import javax.enterprise.deploy.spi.TargetModuleID;
import org.netbeans.modules.portalpack.servers.core.api.PSDeploymentManager;
import org.netbeans.modules.portalpack.servers.core.impl.j2eeservers.api.ServerDeployHandler;
import org.netbeans.modules.portalpack.servers.core.util.NetbeanConstants;
import org.netbeans.modules.portalpack.servers.core.util.PSConfigObject;
import org.openide.util.Exceptions;

/**
 * Deployment handler for JBoss Application Server
 * @author John Platts
 */
public class JBDeployHandler implements ServerDeployHandler {

	private PSConfigObject psconfig;
	private PSDeploymentManager dm;
	private static Logger logger = Logger.getLogger(NetbeanConstants.PORTAL_LOGGER);

	/**
	 * Creates a new instance of JBDeployHandler
	 */
	public JBDeployHandler(PSDeploymentManager dm) {
		this.dm = dm;
		this.psconfig = dm.getPSConfig();
	}

	public boolean deploy(String warFile) throws Exception {
		// Only called if deploying on Glassfish

		// Return false
		return false;
	}

	private boolean undeployFromJBoss(String appName) throws Exception {
		File warFile = null;
		if(!appName.endsWith(".war"))
			warFile = new File(psconfig.getProperty(JBConstant.SERVER_DIR) + File.separator + "deploy" + File.separator + appName + ".war");
		else
			warFile = new File(psconfig.getProperty(JBConstant.SERVER_DIR) + File.separator + "deploy" + File.separator + appName);

		if (warFile.exists()) {
			if (warFile.isDirectory()) {
				undeployDir(warFile);
			} else {
				warFile.delete();
			}
		}

		try {
			Thread.sleep(5000);
		} catch (InterruptedException ex) {
			Exceptions.printStackTrace(ex);
		}

		return true;
	}

	protected void undeployDir(File dir) {

		String files[] = dir.list();
		if (files == null) {
			files = new String[0];
		}
		for (int i = 0; i < files.length; i++) {
			File file = new File(dir, files[i]);
			if (file.isDirectory()) {
				undeployDir(file);
			} else {
				file.delete();
			}
			dir.delete();
		}
	}

	public boolean undeploy(String appName) throws Exception {
		try {
			//undeployOnTomcat(appName);
			return undeployFromJBoss(appName);
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Error", e);
			return false;
		}
	}

	public boolean install() throws Exception {
		return true;
	}

	public boolean deploy(String dir, String contextName) throws Exception {
		// Only called if deploying on Glassfish

		// Return false
		return false;
	}

	public void restart(String contextRoot) throws Exception {
		//throw new UnsupportedOperationException("Not supported yet.");
	}

	public boolean isDeployOnSaveSupported() {
		return false;
	//throw new UnsupportedOperationException("Not supported yet.");
	}

	public boolean isServerRunning() {
		return true;
	}

	public File getModuleDirectory(TargetModuleID module) {
		return null;
	}

	public TargetModuleID[] getAvailableModules(Target[] targets) {
		return new TargetModuleID[0];
	}
	
}
