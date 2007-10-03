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

package org.netbeans.modules.j2ee.oc4j.nodes;

import java.awt.Component;
import javax.swing.Action;
import org.netbeans.modules.j2ee.deployment.plugins.api.InstanceProperties;
import org.netbeans.modules.j2ee.oc4j.OC4JDeploymentManager;
import org.netbeans.modules.j2ee.oc4j.customizer.OC4JCustomizer;
import org.netbeans.modules.j2ee.oc4j.customizer.OC4JCustomizerDataSupport;
import org.netbeans.modules.j2ee.oc4j.ide.OC4JJ2eePlatformFactory;
import org.netbeans.modules.j2ee.oc4j.nodes.actions.ShowAdminToolAction;
import org.netbeans.modules.j2ee.oc4j.util.OC4JPluginProperties;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.actions.SystemAction;

/**
 *
 * @author pblaha
 */
public class OC4JInstanceNode extends AbstractNode implements Node.Cookie {
    
    private static String ICON_BASE = "org/netbeans/modules/j2ee/oc4j/resources/16x16.png"; // NOI18N
    private static final String ADMIN_URL = "/em/"; //NOI18N
    private static final String HTTP_HEADER = "http://";
    private Lookup lookup;
    
    public OC4JInstanceNode(Lookup lookup) {
        super(new Children.Array());
        this.lookup = lookup;
        getCookieSet().add(this);
        setIconBaseWithExtension(ICON_BASE);
    }
    
    public String  getAdminURL() {
        InstanceProperties ip = getDeploymentManager().getProperties().getInstanceProperties();
        String host = ip.getProperty(OC4JPluginProperties.PROPERTY_HOST);
        String httpPort = ip.getProperty(InstanceProperties.HTTP_PORT_NUMBER);
        return HTTP_HEADER + host + ":" + httpPort + ADMIN_URL;
    }
    
    public String getDisplayName() {
        return NbBundle.getMessage(OC4JInstanceNode.class, "TXT_MyInstanceNode");
    }
    
    public javax.swing.Action[] getActions(boolean context) {
        Action[] actions = new Action[2];
        actions[0] = null;
        actions[1] = (SystemAction.get(ShowAdminToolAction.class));
        return actions;
    }
    
    public boolean hasCustomizer() {
        return true;
    }
    
    public Component getCustomizer() {
        OC4JCustomizerDataSupport dataSup = new OC4JCustomizerDataSupport(getDeploymentManager());
        return new OC4JCustomizer(dataSup, new OC4JJ2eePlatformFactory().getJ2eePlatformImpl(getDeploymentManager()));
    }
    
    public OC4JDeploymentManager getDeploymentManager() {
        return ((OC4JDeploymentManager) lookup.lookup(OC4JDeploymentManager.class));
    }
}