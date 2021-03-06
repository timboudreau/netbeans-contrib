/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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

package org.netbeans.modules.autoproject.core;

import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;
import javax.swing.Action;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.modules.autoproject.spi.Cache;
import org.netbeans.modules.autoproject.spi.PathFinder;
import org.netbeans.spi.project.ActionProvider;
import org.netbeans.spi.project.ui.LogicalViewProvider;
import org.netbeans.spi.project.ui.support.CommonProjectActions;
import org.netbeans.spi.project.ui.support.NodeFactorySupport;
import org.netbeans.spi.project.ui.support.ProjectSensitiveActions;
import org.openide.actions.FindAction;
import org.openide.filesystems.FileUtil;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Node;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.util.actions.SystemAction;
import org.openide.util.lookup.Lookups;

class LogicalViewImpl implements LogicalViewProvider {

    private final AutomaticProject project;

    public LogicalViewImpl(AutomaticProject project) {
        this.project = project;
    }

    public Node createLogicalView() {
        return new RootNode(project);
    }

    public Node findPath(Node root, Object target) {
        for (PathFinder pf : Lookup.getDefault().lookupAll(PathFinder.class)) {
            for (Node child : root.getChildren().getNodes(true)) {
                Node r = pf.findNode(child, target);
                if (r != null) {
                    return r;
                }
            }
        }
        return null;
    }

    // Largely copied from ant.freeform:
    private static final class RootNode extends AbstractNode {

        private final AutomaticProject p;

        public RootNode(AutomaticProject p) {
            super(NodeFactorySupport.createCompositeChildren(p, "Projects/org-netbeans-modules-autoproject/Nodes"), Lookups.singleton(p));
            this.p = p;
        }

        @Override
        public String getName() {
            return ProjectUtils.getInformation(p).getName();
        }

        @Override
        public String getDisplayName() {
            return ProjectUtils.getInformation(p).getDisplayName();
        }

        @Override
        public Image getIcon(int type) {
            return ImageUtilities.icon2Image(ProjectUtils.getInformation(p).getIcon());
        }

        @Override
        public Image getOpenedIcon(int type) {
            return getIcon(type);
        }

        @Override
        public Action[] getActions(boolean context) {
            List<Action> actions = new ArrayList<Action>();
            actions.add(CommonProjectActions.newFileAction());
            ActionProvider ap = p.getLookup().lookup(ActionProvider.class);
            if (ap != null) {
                actions.add(null);
                for (String command : ap.getSupportedActions()) {
                    if (ap.isActionEnabled(command, Lookup.EMPTY)) {
                        String label;
                        try {
                            label = NbBundle.getMessage(LogicalViewImpl.class, "CMD_" + command);
                        } catch (MissingResourceException x) {
                            label = command;
                        }
                        actions.add(ProjectSensitiveActions.projectCommandAction(command, label, null));
                    }
                }
            }
            actions.addAll(Utilities.actionsForPath("Projects/Profiler_Actions_temporary")); //NOI18N
            actions.addAll(Utilities.actionsForPath("Projects/org-netbeans-modules-autoproject/Actions")); // NOI18N
            actions.add(null);
            actions.add(CommonProjectActions.openSubprojectsAction());
            actions.add(CommonProjectActions.closeProjectAction());
            actions.add(null);
            // XXX delete etc.: #157043
            actions.add(SystemAction.get(FindAction.class));
            actions.addAll(Utilities.actionsForPath("Projects/Actions")); // NOI18N
            actions.add(null);
            if ("true".equals(Cache.get(FileUtil.toFile(p.getProjectDirectory()) + Cache.PROJECT))) {
                actions.add(new DeregisterAction(p));
            }
            // XXX customize: #153233 etc.
            return actions.toArray(new Action[actions.size()]);
        }
    }

}
