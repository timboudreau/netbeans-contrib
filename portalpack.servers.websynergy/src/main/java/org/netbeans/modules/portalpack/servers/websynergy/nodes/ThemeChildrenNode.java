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
 *
 * Portions Copyrighted 2009 Sun Microsystems, Inc.
 */
package org.netbeans.modules.portalpack.servers.websynergy.nodes;

import java.util.HashMap;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.modules.portalpack.servers.core.api.PSDeploymentManager;
import org.netbeans.modules.portalpack.servers.core.api.PSTaskHandler;
import org.netbeans.modules.portalpack.servers.core.util.NetbeanConstants;
import org.netbeans.modules.portalpack.servers.websynergy.impl.LiferayTaskHandler;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;

/**
 *
 * @author Santh Chetan Chadalavada
 */
class ThemeChildrenNode extends Children.Keys {

    private static Logger logger = Logger.getLogger(NetbeanConstants.PORTAL_LOGGER);
    private static final String WAIT_NODE = "wait_node"; //NOI18N
    private static String WAIT_ICON_BASE = "org/netbeans/modules/portalpack/servers/websynergy/resources/wait.gif";
    private String type;
    private String baseDN;
    private PSDeploymentManager dm;

    public ThemeChildrenNode(PSDeploymentManager dm, String type, String dn) {
        this.type = type;
        this.baseDN = dn;
        logger.log(Level.FINEST, "Setting base DN to ::: " + baseDN);
        this.dm = dm;
    }

    public void updateKeys() {
        TreeSet ts = new TreeSet();
        ts.add(WAIT_NODE);

        setKeys(ts);

        RequestProcessor.getDefault().post(new Runnable() {

            public void run() {

                if (type.equals(LiferayNodeConstants.THEME_NODE_TYPE)) {

                    PSTaskHandler handler = dm.getTaskHandler();
                    if (handler instanceof LiferayTaskHandler) {
                        String[] themes = ((LiferayTaskHandler) handler).getThemes();
                        TreeSet list = new TreeSet();
                        for (int i = 0; i < themes.length; i++) {
                            list.add(themes[i]);
                        }
                        setKeys(list);

                    }

                }

            }
        }, 0);

    }

    @Override
    protected void addNotify() {
        updateKeys();
    }

    @Override
    protected void removeNotify() {
        setKeys(java.util.Collections.EMPTY_SET);
    }

    @Override
    protected org.openide.nodes.Node[] createNodes(Object key) {
        if (key instanceof String) {

            if (key.equals(WAIT_NODE)) {
                return new Node[]{createWaitNode()};
            }


            ThemeNode node = new ThemeNode(dm, (String) key, baseDN);
            return new Node[]{node};
        }
        return null;
    }

    /* Creates and returns the instance of the node
     * representing the status 'WAIT' of the node.
     * It is used when it spent more time to create elements hierarchy.
     * @return the wait node.
     */
    private Node createWaitNode() {
        AbstractNode n = new AbstractNode(Children.LEAF);
        n.setName(NbBundle.getMessage(ThemeChildrenNode.class, "LBL_WaitNode_DisplayName")); //NOI18N
        n.setIconBaseWithExtension(WAIT_ICON_BASE); // NOI18N
        return n;
    }

    class RefreshThemeChildren implements Node.Cookie {

        ThemeChildrenNode children;

        RefreshThemeChildren(ThemeChildrenNode children) {
            this.children = children;
        }

        public void refresh() {
            children.updateKeys();
        }
    }
}
