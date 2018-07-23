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

import java.util.List;
import java.util.TreeSet;
import javax.enterprise.deploy.spi.DeploymentManager;
import javax.enterprise.deploy.shared.ModuleType;
import javax.enterprise.deploy.spi.Target;
import javax.enterprise.deploy.spi.TargetModuleID;
import org.netbeans.modules.j2ee.jetty.JetDeploymentManager;
import org.netbeans.modules.j2ee.jetty.JetModule;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;
import org.openide.util.Lookup;

/**
 * Factory class responsible for creating nodes representing
 * JetWebModules
 * @author novakm
 */
public class JetWebModuleChildrenFactory extends ChildFactory<JetWebModule> {

    private static final JetWebModule MODULE_WAITING_MARK = new JetWebModule(null, null);
    private final Lookup lookup;

    /**
     * Constructor 
     * @param lookup - lookup where the target and manager is available
     */
    public JetWebModuleChildrenFactory(Lookup lookup) {
        this.lookup = lookup;
    }

    /**
     * Updates the keys and refreshes nodes.
     */
    public void updateKeys() {
        refresh(false);
    }

    /**
     * Creates node for given key
     * @param key - JetWebModule
     * @return node representing the JetWebModule
     */
    @Override
    protected Node createNodeForKey(JetWebModule key) {
        if (key == MODULE_WAITING_MARK) {
            return createWaitNode();
        }

        JetWebModuleNode node = new JetWebModuleNode(key);
        key.setRepresentedNode(node);
        return node;
    }

    /**
     * Creates a list of keys which can be individually passed to
     * createNodes() to create child Nodes.
     * @param toPopulate A list to add key objects to
     * @return true if the list of keys has been completely populated,
     *         false if the list has only been partially populated and
     *         this method should be called again to batch more keys
     */
    @Override
    protected boolean createKeys(List<JetWebModule> toPopulate) {
        DeploymentManager manager = lookup.lookup(DeploymentManager.class);
        Target target = lookup.lookup(Target.class);

        TreeSet<JetWebModule> list = new TreeSet<JetWebModule>(
                JetWebModule.Jet_WEB_MODULE_COMPARATOR);

        if (manager instanceof JetDeploymentManager && target != null) {
            try {
                TargetModuleID[] modules = manager.getAvailableModules(ModuleType.WAR, new Target[]{target});
                for (int i = 0; i < modules.length; i++) {
                    list.add(new JetWebModule(manager, (JetModule) modules[i]));
                }

            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        toPopulate.addAll(list);
        return true;
    }
}
