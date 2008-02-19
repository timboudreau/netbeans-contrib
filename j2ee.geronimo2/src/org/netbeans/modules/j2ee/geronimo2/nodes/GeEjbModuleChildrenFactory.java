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
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package org.netbeans.modules.j2ee.geronimo2.nodes;

import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.deploy.shared.ModuleType;
import javax.enterprise.deploy.spi.DeploymentManager;
import javax.enterprise.deploy.spi.Target;
import javax.enterprise.deploy.spi.TargetModuleID;
import org.netbeans.modules.j2ee.geronimo2.GeDeploymentManager;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;
import org.openide.util.Lookup;

/**
 *
 * @author maxa
 */
public class GeEjbModuleChildrenFactory extends ChildFactory<GeEjbModule> {

    private final Lookup lookup;
    private static final GeEjbModule MODULE_WAITING_MARK = new GeEjbModule(null, null, false);
    
    public GeEjbModuleChildrenFactory(Lookup lookup) {
        this.lookup = lookup;
    }
    
    /**
     * Updates the keys and refreshes nodes.
     */
    public void updateKeys() {
        refresh(false);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected Node createNodeForKey(GeEjbModule key) {
        if (key == MODULE_WAITING_MARK) {
            return createWaitNode();
        }

        GeEjbModuleNode node = new GeEjbModuleNode(key);
        key.setRepresentedNode(node);
        return node;
    }
    
    @Override
    protected boolean createKeys(List<GeEjbModule> toPopulate) {
        DeploymentManager manager = lookup.lookup(DeploymentManager.class);
        Target target = lookup.lookup(Target.class);

        if (manager instanceof GeDeploymentManager && target != null) {
            GeDeploymentManager geDm = (GeDeploymentManager) manager;

            //disconnected deploymentManager
            if (!geDm.isConnected()) {
                return true;
            }
            //try to get running modules
            try {
                TargetModuleID[] modules = manager.getRunningModules(ModuleType.EJB, new Target[] {target});
                for (int i = 0; i < modules.length; i++) {
                    TargetModuleID targetModuleID = (TargetModuleID) modules[i];
                    toPopulate.add(new GeEjbModule(manager, targetModuleID, true));
                }

                modules = manager.getNonRunningModules(ModuleType.EJB, new Target[] {target});
                for (int i = 0; i < modules.length; i++) {
                    TargetModuleID targetModuleID = (TargetModuleID) modules[i];
                    toPopulate.add(new GeEjbModule(manager, targetModuleID, false));
                }

            } catch (Exception e) {
                e.printStackTrace();
                //TODO: Try to handle this correctly
                System.out.println("## Aquire of nodes to display failed");
                Logger.getLogger(GeWebModuleChildrenFactory.class.getName()).log(Level.INFO, null, e);
            }
        }
        
        //sort alphabeticallly
        Collections.sort(toPopulate, GeEjbModule.GE_EJB_MODULE_COMPARATOR);
        return true;
    }

}
