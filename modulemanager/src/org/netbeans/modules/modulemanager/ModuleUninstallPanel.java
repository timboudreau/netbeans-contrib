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

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.swing.*;

import org.openide.modules.ModuleInfo;
import org.openide.util.NbBundle;

/** XXX
 * @author Jirka Rechtacek (jrechtacek@netbeans.org)
 * @see "#20323"
 */
class ModuleUninstallPanel extends JPanel {
    Set<ModuleInfo> modules;
    String category;
    
    public ModuleUninstallPanel (Set<ModuleInfo> m, String category) {
        this.modules = m;
        this.category = category;
        initComponents ();
        postInitComponents ();
    }
    
    private void initComponents() {//GEN-BEGIN:initComponents
        jPanel1 = new javax.swing.JPanel();
        uninstallConfirmation = new javax.swing.JLabel();

        setLayout(new java.awt.BorderLayout(0, 2));

        add(jPanel1, java.awt.BorderLayout.CENTER);

        uninstallConfirmation.setText(org.openide.util.NbBundle.getMessage(ModuleUninstallPanel.class, "LBL_ModuleUninstallPanel_UninstallConfirmation", new Object[] {}));
        add(uninstallConfirmation, java.awt.BorderLayout.SOUTH);

    }//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel uninstallConfirmation;
    // End of variables declaration//GEN-END:variables
    
    private String getModuleNames () {
        if (category != null) {
            return category;
        }
        return modules.size () == 1 ? (modules.iterator ().next ()).getDisplayName () :
                    NbBundle.getMessage (ModuleUninstallPanel.class, "CTL_ModuleNodeActions_UninstallAction_many"); // NOI18N
    }
    
    private void postInitComponents () {
        
        Set<ModuleInfo> disableCandidates = new HashSet<ModuleInfo> ();
        ModuleManager manager = Hacks.getModuleManager ();
        for (ModuleInfo module : modules) {
            if (module.isEnabled () && ! Hacks.isAutoload (module) && ! Hacks.isEager (module)) {
                disableCandidates.add (module);
            }
        }
        
        boolean simple = true;
        
        if (! disableCandidates.isEmpty ()) {
            
            SortedSet<ModuleInfo> others = new TreeSet<ModuleInfo> (ModuleBean.AllModulesBean.getDefault ());
            for (ModuleInfo m : manager.simulateDisable (disableCandidates)) {
                if (! Hacks.isAutoload (m) && ! Hacks.isEager (m) && ! modules.contains (m)) {
                    others.add (m);
                }
            }
            
            if (! others.isEmpty ()) {
                Component c = new ModulesAndDescription ( 
				NbBundle.getMessage(ModuleUninstallPanel.class, "LBL_ModuleUninstallPanel_UninstallLabel_depend", // NOI18N
				new Object[] {getModuleNames ()}),
                                others.toArray (new ModuleInfo [others.size ()]));
                add (c, BorderLayout.CENTER);
                simple = false;
            }
        }
        
        if (simple) {
	    JLabel uninstallLabel = new JLabel ();
            if (category != null) {
                uninstallLabel.setText (NbBundle.getMessage(ModuleUninstallPanel.class, "LBL_ModuleUninstallPanel_UninstallLabel_noDepend", // NOI18N
                                            category,
                                            NbBundle.getMessage (ModuleUninstallPanel.class, "CTL_ModuleNodeActions_EnableDisableAction_category") // NOI18N
                                        ));
            } else {
                uninstallLabel.setText (NbBundle.getMessage(ModuleUninstallPanel.class, "LBL_ModuleUninstallPanel_UninstallLabel_noDepend", // NOI18N
                                            modules.size () == 1 ? (modules.iterator ().next ()).getDisplayName () : String.valueOf (modules.size ()),
                                            modules.size () == 1 ? NbBundle.getMessage (ModuleUninstallPanel.class, "CTL_ModuleNodeActions_EnableDisableAction_single") : // NOI18N
                                                                    NbBundle.getMessage (ModuleUninstallPanel.class, "CTL_ModuleNodeActions_EnableDisableAction_many") // NOI18N
                                        ));
            }
            uninstallConfirmation.setVisible (false);
            add (uninstallLabel, BorderLayout.CENTER);
        }
    }
}
