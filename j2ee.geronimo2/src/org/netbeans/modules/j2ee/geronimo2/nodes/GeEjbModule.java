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

import java.util.Comparator;
import javax.enterprise.deploy.spi.DeploymentManager;
import javax.enterprise.deploy.spi.TargetModuleID;
import org.netbeans.modules.j2ee.geronimo2.GeDeploymentManager;
import org.netbeans.modules.j2ee.geronimo2.nodes.actions.GeEjbModuleCookie;
import org.openide.nodes.Node;
import org.openide.util.RequestProcessor.Task;

/**
 *
 * @author maxa
 */
public class GeEjbModule implements GeEjbModuleCookie {
    private GeDeploymentManager manager;
    private volatile boolean isRunning;
    private TargetModuleID targetModule;
    private TargetModuleID[] target;
    private Node node;

    public GeEjbModule(DeploymentManager manager, TargetModuleID targetModule, boolean isRunning) {
        this.targetModule = targetModule;
        this.manager = (GeDeploymentManager) manager;
        this.isRunning = isRunning;
        target = new TargetModuleID[]{targetModule};
    }

    /** Simple comparator for sorting nodes by name. */
    public static final Comparator<GeEjbModule> GE_EJB_MODULE_COMPARATOR = new Comparator<GeEjbModule>() {

        public int compare(GeEjbModule wm1, GeEjbModule wm2) {
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
    
    //---------- implements
    public Task undeploy() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void start() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void stop() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isRunning() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void openLog() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean hasLogger() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
