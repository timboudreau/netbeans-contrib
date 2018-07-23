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


package org.netbeans.modules.j2ee.hk2.nodes;

import java.awt.Component;
import org.netbeans.modules.j2ee.deployment.plugins.api.InstanceProperties;
import org.netbeans.modules.j2ee.hk2.Hk2DeploymentManager;
import org.netbeans.modules.j2ee.hk2.Hk2J2eePlatformFactory;
import org.netbeans.modules.j2ee.hk2.customizer.Customizer;
import org.netbeans.modules.j2ee.hk2.customizer.CustomizerDataSupport;
import org.netbeans.modules.j2ee.hk2.ide.Hk2PluginProperties;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author Ludo
 */
public class Hk2InstanceNode extends AbstractNode implements Node.Cookie {
    
    private static String ICON_BASE = "org/netbeans/modules/j2ee/hk2/resources/server.gif"; // NOI18N
    private Lookup lookup;    
    public Hk2InstanceNode(Lookup lookup) {
        super(new Children.Array());
        getCookieSet().add(this);
        this.lookup = lookup;
        setIconBaseWithExtension(ICON_BASE);
    }
       
    public String getDisplayName() {
        return NbBundle.getMessage(Hk2InstanceNode.class, "TXT_MyInstanceNode");
    }
    
    public String getShortDescription() {
        return getAdminURL();
    }
    
    public javax.swing.Action[] getActions(boolean context) {
        return new javax.swing.Action[]{};
    }
    
    public boolean hasCustomizer() {
        return true;
    }
    
    public Component getCustomizer() {
        CustomizerDataSupport dataSup = new CustomizerDataSupport(getDeploymentManager());
        return new Customizer(dataSup, new Hk2J2eePlatformFactory().getJ2eePlatformImpl(getDeploymentManager()));
    }
    
        public Hk2DeploymentManager getDeploymentManager() {
        return ((Hk2DeploymentManager) lookup.lookup(Hk2DeploymentManager.class));
    }
    public String  getAdminURL() {
        InstanceProperties ip = getDeploymentManager().getProperties().getInstanceProperties();
        String host = ip.getProperty(Hk2PluginProperties.PROPERTY_HOST);
        String httpPort = ip.getProperty(InstanceProperties.HTTP_PORT_NUMBER);
        return "http://" + host + ":" + httpPort ;
    }

}
