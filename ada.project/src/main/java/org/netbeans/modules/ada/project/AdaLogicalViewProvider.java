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
package org.netbeans.modules.ada.project;

import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Action;
import org.netbeans.modules.ada.project.ui.TreeRootNode;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.spi.project.ActionProvider;
import org.netbeans.spi.project.ui.LogicalViewProvider;
import org.netbeans.spi.project.ui.support.CommonProjectActions;
import org.netbeans.spi.project.ui.support.DefaultProjectOperations;
import org.netbeans.spi.project.ui.support.NodeFactorySupport;
import org.netbeans.spi.project.ui.support.ProjectSensitiveActions;
import org.openide.actions.FindAction;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.util.actions.SystemAction;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Andrea Lucarelli
 */
class AdaLogicalViewProvider implements LogicalViewProvider {

    private AdaProject project;

    public AdaLogicalViewProvider(AdaProject project) {
        this.project = project;
    }

    public Node createLogicalView() {
        return new AdaProjectNode();
    }

    public Node findPath(Node root, Object target) {
        Project prj = root.getLookup().lookup(Project.class);
        if (prj == null) {
            return null;
        }
        if (target instanceof FileObject) {
            FileObject targetFO = (FileObject) target;
            Project owner = FileOwnerQuery.getOwner(targetFO);
            if (!prj.equals(owner)) {
                return null; // Don't waste time if project does not own the fo
            }

            Node[] rootChildren = root.getChildren().getNodes(true);
            for (int i = 0; i < rootChildren.length; i++) {
                TreeRootNode.PathFinder pf2 = rootChildren[i].getLookup().lookup(TreeRootNode.PathFinder.class);
                if (pf2 != null) {
                    Node n = pf2.findPath(rootChildren[i], target);
                    if (n != null) {
                        return n;
                    }
                }
                FileObject childFO = rootChildren[i].getLookup().lookup(DataObject.class).getPrimaryFile();
                if (targetFO.equals(childFO)) {
                    return rootChildren[i];
                }
            }
        }

        return null;
    }
    private static Image brokenProjectBadge = ImageUtilities.loadImage("org/netbeans/modules/ada/project/ui/resources/brokenProjectBadge.gif", true);

    private final class AdaProjectNode extends AbstractNode {

        private boolean broken; //for future use, marks the project as broken

        public AdaProjectNode() {
            super(NodeFactorySupport.createCompositeChildren(project, "Projects/org-netbeans-modules-ada-project/Nodes"),
                    Lookups.singleton(project));
            setIconBaseWithExtension("org/netbeans/modules/ada/project/ui/resources/ada-lovelace-16.png");
            super.setName(ProjectUtils.getInformation(project).getDisplayName());
        }

        public 
        @Override
        String getShortDescription() {
            //todo: Add Ada platform description
            String dirName = FileUtil.getFileDisplayName(project.getProjectDirectory());
            return NbBundle.getMessage(AdaLogicalViewProvider.class, "AdaLogicalView.ProjectTooltipDescription", dirName);
        }

        @Override
        public Image getIcon(int type) {
            Image original = super.getIcon(type);
            return broken ? ImageUtilities.mergeImages(original, brokenProjectBadge, 8, 0) : original;
        }

        @Override
        public Image getOpenedIcon(int type) {
            Image original = super.getOpenedIcon(type);
            return broken ? ImageUtilities.mergeImages(original, brokenProjectBadge, 8, 0) : original;
        }

        @Override
        public Action[] getActions(boolean context) {
            return getAdditionalActions();
        }

        @Override
        public boolean canRename() {
            return true;
        }

        @Override
        public void setName(String s) {
            DefaultProjectOperations.performDefaultRenameOperation(project, s);
        }

        @Override
        public HelpCtx getHelpCtx() {
            return new HelpCtx(AdaProjectNode.class);
        }

        private Action[] getAdditionalActions() {
            final List<Action> actions = new ArrayList<Action>();
            actions.add(CommonProjectActions.newFileAction());
            actions.add(null);
//            The action provider is not done yet
            actions.add(ProjectSensitiveActions.projectCommandAction(AdaActionProvider.COMMAND_BUILD, NbBundle.getMessage(AdaLogicalViewProvider.class, "LBL_BuildAction_Name"), null)); // NOI18N
            actions.add(ProjectSensitiveActions.projectCommandAction(AdaActionProvider.COMMAND_REBUILD, NbBundle.getMessage(AdaLogicalViewProvider.class, "LBL_RebuildAction_Name"), null)); // NOI18N
            actions.add(ProjectSensitiveActions.projectCommandAction(AdaActionProvider.COMMAND_CLEAN, NbBundle.getMessage(AdaLogicalViewProvider.class, "LBL_CleanAction_Name"), null)); // NOI18N
            actions.add(ProjectSensitiveActions.projectCommandAction(AdaActionProvider.COMMAND_ADADOC, NbBundle.getMessage(AdaLogicalViewProvider.class, "LBL_AdadocAction_Name"), null)); // NOI18N
            actions.add(null);
            actions.add(ProjectSensitiveActions.projectCommandAction(AdaActionProvider.COMMAND_RUN, NbBundle.getMessage(AdaLogicalViewProvider.class, "LBL_RunAction_Name"), null)); // NOI18N
            actions.add(ProjectSensitiveActions.projectCommandAction(AdaActionProvider.COMMAND_DEBUG, NbBundle.getMessage(AdaLogicalViewProvider.class, "LBL_DebugAction_Name"), null)); // NOI18N
            actions.add(ProjectSensitiveActions.projectCommandAction(AdaActionProvider.COMMAND_TEST, NbBundle.getMessage(AdaLogicalViewProvider.class, "LBL_TestAction_Name"), null)); // NOI18N
            actions.add(null);
            actions.add(CommonProjectActions.setAsMainProjectAction());
            actions.add(CommonProjectActions.openSubprojectsAction());
            actions.add(CommonProjectActions.closeProjectAction());
            actions.add(null);
            actions.add(CommonProjectActions.renameProjectAction());
            actions.add(CommonProjectActions.moveProjectAction());
            actions.add(CommonProjectActions.copyProjectAction());
            actions.add(CommonProjectActions.deleteProjectAction());
            actions.add(null);
            actions.add(SystemAction.get(FindAction.class));

            // honor 57874 contact
            actions.addAll(Utilities.actionsForPath("Projects/Actions")); //NOI18N

            actions.add(null);
            actions.add(CommonProjectActions.customizeProjectAction());
            return actions.toArray(new Action[actions.size()]);
        }
    }
}
