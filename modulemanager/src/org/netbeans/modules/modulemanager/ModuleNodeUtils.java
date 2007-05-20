/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.modulemanager;

import java.awt.Component;
import java.util.HashSet;
import java.util.Set;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.modules.ModuleInfo;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;

/**
 *
 * @author Jirka Rechtacek (jrechtacek@netbeans.org)
 */
class ModuleNodeUtils {

    private ModuleNodeUtils() {}

    static private Set<ModuleNode.Item> getCategoryModules (Node category) {
        assert ! category.isLeaf () : "Parent " + category + " cannot be leaf.";
        Set<ModuleNode.Item> modules = new HashSet<ModuleNode.Item>();
        for (Node child : category.getChildren().getNodes(true)) {
            if (child instanceof ModuleNode.Item) {
                modules.add((ModuleNode.Item) child);
            }
        }
        return modules;
    }
    
    static Set<ModuleNode.Item> getAllSelectedModuleItems(Node[] nodes) {
        Set<ModuleNode.Item> modules = new HashSet<ModuleNode.Item>();
        
        for (Node node : nodes) {
            if (node instanceof ModuleNode.Item) {
                modules.add((ModuleNode.Item) node);
            } else if (!node.isLeaf()) {
                modules.addAll(getCategoryModules(node));
            }
        }
        
        return modules;
    }    
    
    static Set<ModuleBean> getAllSelectedModuleBeans(Node[] nodes) {
        Set<ModuleBean> beans = new HashSet<ModuleBean>();
        
        for (Node node : nodes) {
            if (node instanceof ModuleNode.Item) {
                beans.add(((ModuleNode.Item) node).getItem());
            } else if (!node.isLeaf()) {
                beans.addAll(getAllSelectedModuleBeans(node.getChildren().getNodes(true)));
            }
        }
        
        return beans;
    }
    
    static Boolean isEnableCandidate (Node [] nodes) {
        Boolean res = null;
        for (ModuleBean b : getAllSelectedModuleBeans(nodes)) {
            if (res == null) {
                res = !b.isEnabled();
            } else {
                if (res == b.isEnabled()) {
                    // mixed value
                    return null;
                }
            }
        }
        return res;
    }
    
    static boolean isEnableAllowed (ModuleInfo m) {
        if (! Hacks.isValid (m)) return false;
        // XXX: now we can uninstall eager/autoload
        return ! ( ! (Hacks.getProblems (m).isEmpty ()) || Hacks.isAutoload (m) || Hacks.isEager (m) || Hacks.getJarFile (m) == null );
    }
    
    static String getUninstallActionName (Node [] activatedNodes, String oldName) {
        if (activatedNodes == null || activatedNodes.length == 0) {
            return oldName;
        }
        
        String name;
        // special handling of category
        if (activatedNodes.length == 1 && ! (activatedNodes [0] instanceof ModuleNode.Item)) {
            name = NbBundle.getMessage (ModuleNodeActions.class, "CTL_ModuleNodeActions_UninstallAction", // NOI18N
                        activatedNodes [0].getDisplayName ()); // NOI18N
        } else {
            Set<ModuleNode.Item> items = ModuleNodeUtils.getAllSelectedModuleItems (activatedNodes);
            name = NbBundle.getMessage (ModuleNodeActions.class, "CTL_ModuleNodeActions_UninstallAction", // NOI18N
                        items.size () > 1 ?
                            NbBundle.getMessage (ModuleNodeActions.class, "CTL_ModuleNodeActions_UninstallAction_many") : // NOI18N
                            items.iterator().next().getDisplayName());
        }
        
        return name;
    }
    
    static boolean isUninstallAllowed (ModuleInfo m) {
        // XXX: we can uninstall eager/autoload
        return ! (Hacks.isAutoload (m) || Hacks.isEager (m) || Hacks.isFixed (m));
    }
    
    static boolean canUninstall (Node[] activatedNodes) {
        if (activatedNodes == null || activatedNodes.length == 0) {
            return false;
        }

        Set<ModuleNode.Item> items = ModuleNodeUtils.getAllSelectedModuleItems(activatedNodes);
        if (items.isEmpty()) {
            return false;
        }

        for (ModuleNode.Item item : items) {
            if (!item.canDestroy()) {
                return false;
            }
        }

        return true;
    }

    static void doUninstall (final Node[] activatedNodes) {
        if (confirmUninstall (activatedNodes)) {
            ModuleSelectionPanel.getGUI (false).setWaitingState (true, true);
            RequestProcessor.getDefault ().post (new Runnable () {
                public void run () {
                    uninstallNodes (activatedNodes);
                }
            });
        }
    }
    
    static boolean confirmUninstall (Node[] activatedNodes) {
        assert activatedNodes != null : "Any ModuleNode must be selected";

        Set<ModuleNode.Item> items = ModuleNodeUtils.getAllSelectedModuleItems(activatedNodes);

        assert ! items.isEmpty () : "Any module must be selected";

        String category = null;
        if (activatedNodes.length == 1 && ! (activatedNodes [0] instanceof ModuleNode.Item)) {
            category = activatedNodes [0].getDisplayName ();
        }

        Set<ModuleInfo> modules = new HashSet<ModuleInfo>(items.size());
        for (ModuleNode.Item item : items) {
            modules.add(item.getItem().getModuleInfo());
        }

        Component c = new ModuleUninstallPanel (modules, category);
	c.getAccessibleContext ().setAccessibleDescription (
			NbBundle.getMessage (ModuleSelectionPanel.class, "ACD_ModuleUninstallPanel_form")); // NOI18N

        NotifyDescriptor nd = new NotifyDescriptor.Confirmation (c,
                        NbBundle.getMessage (ModuleSelectionPanel.class, "CTL_ModuleUninstallPanel_UninstallConfirmation"),
                        NotifyDescriptor.YES_NO_OPTION);

        return NotifyDescriptor.YES_OPTION.equals (DialogDisplayer.getDefault ().notify (nd));
    }

    static private void uninstallNodes (Node[] nodes) {
        assert nodes != null : "uninstallNodes cannot be called on null nodes.";
        for (Node node : nodes) {
            if (node.isLeaf()) {
                ((ModuleNode.Item) node).uninstall();
            } else {
                uninstallNodes(node.getChildren().getNodes(true));
            } 
        }
    }
    
}
