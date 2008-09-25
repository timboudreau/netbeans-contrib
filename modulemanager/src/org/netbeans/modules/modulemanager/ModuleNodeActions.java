/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2008 Sun Microsystems, Inc. All rights reserved.
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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2008 Sun
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

package org.netbeans.modules.modulemanager;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Set;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import org.netbeans.Module;
import org.openide.awt.Mnemonics;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.NodeAction;
import org.openide.util.actions.Presenter;

/**
 * @author Jirka Rechtacek (jrechtacek@netbeans.org), Jesse Glick
 */
public class ModuleNodeActions {
    
    public static class EnableDisableAction extends NodeAction {
        private String name;

        protected void performAction (final Node[] activatedNodes) {
            assert activatedNodes != null : "Cannot performAction when activatedNodes is null";
            final Boolean b = ModuleNodeUtils.isEnableCandidate (activatedNodes);
            assert b != null : "Cannot performAction if mixed statuses on activatedNodes " + Arrays.asList (activatedNodes);
            
            // fix of 62480: join all enable/disable to one group
            ModuleBean.AllModulesBean.getDefault ().pause ();
            Set<ModuleBean> beans = ModuleNodeUtils.getAllSelectedModuleBeans(activatedNodes);
            for (ModuleBean bean : beans) {
                bean.setEnabled (b.booleanValue ());
            }
            ModuleBean.AllModulesBean.getDefault ().resume ();
        }

        protected boolean enable (Node[] activatedNodes) {
            if (activatedNodes == null) {
                return false;
            }
            
            Set<ModuleNode.Item> items = ModuleNodeUtils.getAllSelectedModuleItems(activatedNodes);

            boolean allowed = ! items.isEmpty ();
            String moduleName = ""; // NOI18N
            for (ModuleNode.Item item : items) {
                Module m = item.getItem().getModule();
                moduleName = m.getDisplayName ();
                if (!ModuleNodeUtils.isEnableAllowed(m)) {
                    allowed = false;
                    break;
                }
            }
            
            // set action's name
            Boolean b = ModuleNodeUtils.isEnableCandidate (activatedNodes);

            // special handling of category
            if (activatedNodes.length == 1 && ! (activatedNodes [0] instanceof ModuleNode.Item)) {
                name = NbBundle.getMessage (ModuleNodeActions.class, "CTL_ModuleNodeActions_EnableDisableAction_format", // NOI18N
                            b == null || b.booleanValue () ? NbBundle.getMessage (ModuleNodeActions.class, "CTL_ModuleNodeActions_EnableDisableAction_enable") : // NOI18N
                                                             NbBundle.getMessage (ModuleNodeActions.class, "CTL_ModuleNodeActions_EnableDisableAction_disable"), // NOI18N
                            activatedNodes[0].getDisplayName ()); // NOI18N
            } else {
                name = NbBundle.getMessage (ModuleNodeActions.class, "CTL_ModuleNodeActions_EnableDisableAction_format", // NOI18N
                            b == null || b.booleanValue () ? NbBundle.getMessage (ModuleNodeActions.class, "CTL_ModuleNodeActions_EnableDisableAction_enable") : // NOI18N
                                                             NbBundle.getMessage (ModuleNodeActions.class, "CTL_ModuleNodeActions_EnableDisableAction_disable"), // NOI18N
                            items.size () > 1 ? NbBundle.getMessage (ModuleNodeActions.class, "CTL_ModuleNodeActions_EnableDisableAction_many") : moduleName); // NOI18N
            }
            
            return allowed && (ModuleNodeUtils.isEnableCandidate (activatedNodes) != null);
        }

        public String getName() {
            return name;
        }

        public HelpCtx getHelpCtx() {
            //return new HelpCtx (ModuleNodeActions.EnableAllAction.class);
            return null;
        }

        @Override
        protected boolean asynchronous() {
            return true;
        }
    }

    public static class EnableAllAction extends NodeAction {

        protected void performAction (Node[] activatedNodes) {
            assert activatedNodes != null : "Cannot performAction when activatedNodes is null";
            
            // fix of 62480: join all enable/disable to one group
            ModuleBean.AllModulesBean.getDefault ().pause ();
            for (ModuleBean bean : ModuleBean.AllModulesBean.getDefault().getModules()) {
                if (!bean.isEnabled() && ModuleNodeUtils.isEnableAllowed(bean.getModule())) {
                    bean.setEnabled(true);
                }
            }
            ModuleBean.AllModulesBean.getDefault ().resume ();
        }

        protected boolean enable (Node[] activatedNodes) {
            // XXX: don't enable if all modules are enabled
            return activatedNodes != null && activatedNodes.length > 0;
        }

        public String getName() {
            return NbBundle.getMessage (ModuleNodeActions.class, "CTL_ModuleNodeActions_EnableAllAction"); // NOI18N
        }

        public HelpCtx getHelpCtx() {
            //return new HelpCtx (ModuleNodeActions.EnableDisableAction.class);
            return null;
        }

        @Override
        protected boolean asynchronous() {
            return true;
        }
    }

    public static class UninstallAction extends NodeAction {
        private String name;
        
        protected void performAction (Node[] activatedNodes) {
            ModuleNodeUtils.doUninstall (activatedNodes);
        }

        protected boolean enable (Node[] activatedNodes) {
            name = ModuleNodeUtils.getUninstallActionName (activatedNodes, name);
            return ModuleNodeUtils.canUninstall (activatedNodes);
        }

        public String getName() {
            return name;
        }

        public HelpCtx getHelpCtx() {
            //return new HelpCtx (ModuleNodeActions.UninstallAction.class);
            return null;
        }

        @Override
        protected boolean asynchronous() {
            return true;
        }
    }

    public static class SortAction extends NodeAction implements Presenter.Popup {
        
        // private fields
        JMenu subMenu;
        JRadioButtonMenuItem sortByCategory;
        JRadioButtonMenuItem sortByName;
        JRadioButtonMenuItem sortByCluster;
                
        protected void performAction (Node[] activatedNodes) {
        }

        protected boolean enable (Node[] activatedNodes) {
            return true;
        }

        public String getName () {
            return NbBundle.getMessage (ModuleNodeActions.class, "CTL_ModuleNodeActions_SortAction"); // NOI18N
        }

        public HelpCtx getHelpCtx () {
            return null;
        }
        
        @Override
        public JMenuItem getPopupPresenter() {
            return getSubmenuPopupPresenter();
        }
        
        public JMenuItem getSubmenuPopupPresenter() {
            if (subMenu == null) {
                
                subMenu = new JMenu ();
                Mnemonics.setLocalizedText (subMenu, NbBundle.getMessage (ModuleNodeActions.class, "CTL_ModuleNodeActions_SortAction")); // NOI18N
                
                sortByCategory = new JRadioButtonMenuItem();
                Mnemonics.setLocalizedText (sortByCategory, NbBundle.getMessage (ModuleNodeActions.class, "CTL_ModuleNodeActions_SortByCategory")); // NOI18N
                sortByCategory.addActionListener (new MenuListener ());
                subMenu.add (sortByCategory);
                
                sortByName = new JRadioButtonMenuItem();
                Mnemonics.setLocalizedText (sortByName, NbBundle.getMessage (ModuleNodeActions.class, "CTL_ModuleNodeActions_SortByName")); // NOI18N
                sortByName.addActionListener (new MenuListener ());
                subMenu.add (sortByName);
                
                sortByCluster = new JRadioButtonMenuItem();
                Mnemonics.setLocalizedText(sortByCluster, NbBundle.getMessage(ModuleNodeActions.class, "CTL_ModuleNodeActions_SortByCluster"));
                sortByCluster.addActionListener(new MenuListener());
                subMenu.add(sortByCluster);
                
            }
            sortByCategory.setSelected(ModuleNode.getModuleSortMode() == ModuleNode.SortMode.BY_CATEGORY);
            sortByName.setSelected(ModuleNode.getModuleSortMode() == ModuleNode.SortMode.BY_DISPLAY_NAME);
            sortByCluster.setSelected(ModuleNode.getModuleSortMode() == ModuleNode.SortMode.BY_CLUSTER);
            return subMenu;
        }
        
        private class MenuListener implements ActionListener {

            public void actionPerformed (ActionEvent e) {
                JMenuItem source = (JMenuItem)e.getSource ();
                
                if (sortByCategory.equals (source)) {
                    ModuleNode.setModuleSortMode(ModuleNode.SortMode.BY_CATEGORY);
                } else if (sortByName.equals (source)) {
                    ModuleNode.setModuleSortMode(ModuleNode.SortMode.BY_DISPLAY_NAME);
                } else if (sortByCluster.equals(source)) {
                    ModuleNode.setModuleSortMode(ModuleNode.SortMode.BY_CLUSTER);
                } else {
                    assert false : "Invalid source " + source + " in actionPerformed()";
                }

            }

        }


    }
}
