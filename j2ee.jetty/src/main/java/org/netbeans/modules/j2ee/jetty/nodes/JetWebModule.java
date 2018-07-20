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


package org.netbeans.modules.j2ee.jetty.nodes;

import org.netbeans.modules.j2ee.jetty.JetDeploymentManager;
import org.netbeans.modules.j2ee.jetty.JetModule;
import java.util.Comparator;
import javax.enterprise.deploy.spi.DeploymentManager;
import javax.enterprise.deploy.spi.TargetModuleID;
import org.netbeans.modules.j2ee.jetty.nodes.action.JetWebModuleCookie;
import org.openide.awt.StatusDisplayer;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.util.RequestProcessor.Task;

/**
 * Class representing web application in .war archive that can be
 * deployed/undeployed to/from Jetty server and can be started/stopped.
 * @author novakm
 */
public class JetWebModule implements JetWebModuleCookie {

	/** Simple comparator for sorting nodes by name. */
	public static final Comparator<JetWebModule> Jet_WEB_MODULE_COMPARATOR = new Comparator<JetWebModule>() {

		public int compare(JetWebModule m1, JetWebModule m2) {
			return m1.getJetModule().getModuleID().compareTo(m2.getJetModule().getModuleID());
		}
	};
	private final JetModule JetModule;
	private final JetDeploymentManager manager;
	private Node node;
	private final TargetModuleID[] target;

	/**
         * Creates new instance of JetWebModule and sets its target
         * and manager as appropriate
         * @param manager - JetDeploymentManager that will work with the module
         * @param JetModule - JetModule we want to create JetWebModule from
         */
	public JetWebModule(DeploymentManager manager, JetModule JetModule) {
		this.JetModule = JetModule;
		this.manager = (JetDeploymentManager) manager;
		target = new TargetModuleID[]{JetModule};
	}

       /**
        * {@inheritDoc }
        */
	public Task undeploy() {
		return RequestProcessor.getDefault().post(new Runnable() {

			public void run() {
				StatusDisplayer.getDefault().setStatusText(NbBundle.getMessage(JetWebModule.class, "MSG_START_UNDEPLOY", // NOI18N
					new Object[]{getJetModule().getContextPath()}));

				manager.undeploy(target);
			}
		}, 0);
	}

       /**
        * {@inheritDoc }
        */
        public void start() {
		RequestProcessor.getDefault().post(new Runnable() {

			public void run() {
				StatusDisplayer.getDefault().setStatusText(NbBundle.getMessage(JetWebModule.class, "MSG_START_STARTING", // NOI18N
					new Object[]{getJetModule().getContextPath()}));
				manager.start(target);
			}
		}, 0);
	}

       /**
        * {@inheritDoc }
        */
	public void stop() {
		RequestProcessor.getDefault().post(new Runnable() {

			public void run() {
				StatusDisplayer.getDefault().setStatusText(NbBundle.getMessage(JetWebModule.class, "MSG_START_STOPPING", // NOI18N
					new Object[]{getJetModule().getContextPath()}));
				manager.stop(target);
			}
		}, 0);
	}

       /**
        * {@inheritDoc }
        */
	public boolean isRunning() {
		return manager.isJetModuleRunning(JetModule);
	}

        /**
         * Returns JetModule we created this JetWebModule from
         * @return JetModule
         */
	public JetModule getJetModule() {
		return JetModule;
	}
        
        /**
         * Sets given node as a representation of JetWebModule
         * @param node
         */
	public void setRepresentedNode(Node node) {
		this.node = node;
	}
}

