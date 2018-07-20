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

import java.util.HashMap;
import javax.enterprise.deploy.shared.factories.DeploymentFactoryManager;
import javax.enterprise.deploy.spi.DeploymentManager;
import javax.enterprise.deploy.spi.exceptions.DeploymentManagerCreationException;
import javax.enterprise.deploy.spi.factories.DeploymentFactory;
import org.openide.util.NbBundle;

/**
 * Factory class responsible for creating instance of JetDeploymentManager.
 * @author novakm
 */
public class JetDeploymentFactory implements DeploymentFactory {

    /**
     * Jet server root property
     */
    public static final String PROP_SERVER_ROOT = "jetty_server_root"; // NOI18N

    public static final String URI_PREFIX = "jetty:http"; // NOI18N

    private static DeploymentFactory instance;
    private HashMap<String, DeploymentManager> managers = new HashMap<String, DeploymentManager>();

    /**
     * Creates and registers instance of JetDeploymentFactory if it doesn't exist yet
     * @return mentioned instance
     */
    public static synchronized DeploymentFactory create() {
        if (instance == null) {
            instance = new JetDeploymentFactory();
            DeploymentFactoryManager.getInstance().registerDeploymentFactory(instance);
        }
        return instance;
    }

    /**
     * Checks whether uri is handled by this factory
     * @param uri - String representing URI
     * @return true if uri is is valid uri for jetty
     */
    public boolean handlesURI(String uri) {
        return uri != null && uri.startsWith(URI_PREFIX);
    }

    /**
     * Returns a JetDeploymentManager for given uri or creates and returns
     * one if it doesn't exist yet.
     * @param uri Uniform Resource Identifier for this factory
     * @param uname null
     * @param passwd null
     * @return reference to JetDeploymentManager
     * @throws javax.enterprise.deploy.spi.exceptions.DeploymentManagerCreationException
     */
    public DeploymentManager getDeploymentManager(String uri, String uname, String passwd) throws DeploymentManagerCreationException {
        if (!handlesURI(uri)) {
            throw new DeploymentManagerCreationException("Invalid URI:" + uri); // NOI18N
        }
        DeploymentManager manager = managers.get(uri);

        if (null == manager) {
            manager = new JetDeploymentManager(uri);
            managers.put(uri, manager);
        }

        return manager;
    }

    public DeploymentManager getDisconnectedDeploymentManager(String uri) throws DeploymentManagerCreationException {
        return getDeploymentManager(uri, null, null);

    }
    /**
     * Method returning product version
     * @return String representing product version
     */
    public String getProductVersion() {
        return "0.1"; // NOI18N

    }

    /**
     * Method returning display name of server.
     * @return name of server
     */
    public String getDisplayName() {
        return NbBundle.getMessage(JetDeploymentFactory.class, "TXT_DisplayName"); // NOI18N

    }
}
