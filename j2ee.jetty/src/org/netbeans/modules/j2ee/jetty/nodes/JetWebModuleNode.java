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

import java.awt.Image;
import java.util.LinkedList;
import javax.swing.Action;
import org.netbeans.modules.j2ee.jetty.nodes.action.StartAction;
import org.netbeans.modules.j2ee.jetty.nodes.action.StopAction;
import org.netbeans.modules.j2ee.jetty.nodes.action.UndeployAction;
import org.netbeans.modules.j2ee.deployment.plugins.api.UISupport;
import org.netbeans.modules.j2ee.deployment.plugins.api.UISupport.ServerIcon;
import org.netbeans.modules.j2ee.jetty.nodes.action.BrowseURLAction;
import org.netbeans.modules.j2ee.jetty.nodes.action.BrowseURLActionCookie;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.NbBundle;
import org.openide.util.actions.SystemAction;

/**
 * Class representing a web application (web module) on Jetty server
 * @author novakm
 */
public class JetWebModuleNode extends AbstractNode implements BrowseURLActionCookie {

    private JetWebModule module;

    /** Creates a new instance of JetWebModuleNode from given JetWebModule
     * @param module - JetWebModule
     */
    public JetWebModuleNode(JetWebModule module) {
        super(Children.LEAF);
        this.module = module;
        setDisplayName(constructName());
        setShortDescription(module.getJetModule().getWebURL());
        getCookieSet().add(module);
    }

    /**
     * Returns the list of actions that can be invoked from popup menu
     * on JetWebModuleNode
     * @param context -
     * @return Array of invokable actions
     */
    @Override
    @SuppressWarnings("unchecked")
    public Action[] getActions(boolean context) {
        java.util.List actions = new LinkedList();
        actions.add(SystemAction.get(StartAction.class));
        actions.add(SystemAction.get(StopAction.class));
        actions.add(SystemAction.get(UndeployAction.class));
        actions.add(SystemAction.get(BrowseURLAction.class));
        return (SystemAction[]) actions.toArray(new SystemAction[actions.size()]);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Image getIcon(int type) {
        return UISupport.getIcon(ServerIcon.WAR_ARCHIVE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Image getOpenedIcon(int type) {
        return getIcon(type);
    }

    private String constructName() {
        if (module.isRunning()) {
            return module.getJetModule().getContextPath();
        } else {
            return module.getJetModule().getContextPath() + " [" +
                    NbBundle.getMessage(JetWebModuleNode.class, "LBL_Stopped") // NOI18N
                    + "]";
        }
    }

    /**
     * Returns web URL on which is JetWebModule represented by this node running
     * @return web URL
     */
    public String getWebURL() {
        return module.getJetModule().getWebURL();
    }
}

