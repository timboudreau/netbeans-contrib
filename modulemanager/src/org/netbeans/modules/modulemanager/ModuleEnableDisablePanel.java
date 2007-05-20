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
import java.awt.Font;
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
class ModuleEnableDisablePanel extends JPanel {
    Set<ModuleInfo> explicit;
    Set<ModuleInfo> modules;
    private boolean toEnable;
    
    public ModuleEnableDisablePanel (boolean enable, Set<ModuleInfo> explicit, Set<ModuleInfo> implied) {
        this.explicit = explicit;
        this.modules = implied;
        this.toEnable = enable;
        initComponents ();
        postInitComponents ();
    }
    
    private void initComponents() {//GEN-BEGIN:initComponents
        jPanel1 = new javax.swing.JPanel();
        enableDisableConfirmation = new javax.swing.JLabel();

        setLayout(new java.awt.BorderLayout(0, 2));

        add(jPanel1, java.awt.BorderLayout.CENTER);

        enableDisableConfirmation.setText(org.openide.util.NbBundle.getMessage(ModuleEnableDisablePanel.class, "LBL_ModuleEnableDisablePanel_EnableConfirmation", new Object[] {}));
        add(enableDisableConfirmation, java.awt.BorderLayout.SOUTH);

    }//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel enableDisableConfirmation;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables
    
    private String getModuleNames () {
        return explicit.size () == 1 ? (explicit.iterator ().next ()).getDisplayName () :
                    NbBundle.getMessage (ModuleUninstallPanel.class, "CTL_ModuleNodeActions_UninstallAction_many"); // NOI18N
    }
    
    private void postInitComponents () {
        
	String enableDisableText;
        
        if (toEnable) {
            enableDisableText = NbBundle.getMessage(ModuleEnableDisablePanel.class, "LBL_ModuleEnableDisablePanel_EnableLabel", new Object[] {getModuleNames ()});
            enableDisableConfirmation.setText(org.openide.util.NbBundle.getMessage(ModuleEnableDisablePanel.class, "LBL_ModuleEnableDisablePanel_EnableConfirmation", new Object[] {}));
        } else {
            enableDisableText = NbBundle.getMessage(ModuleEnableDisablePanel.class, "LBL_ModuleEnableDisablePanel_DisableLabel", new Object[] {getModuleNames ()});
            enableDisableConfirmation.setText(org.openide.util.NbBundle.getMessage(ModuleEnableDisablePanel.class, "LBL_ModuleEnableDisablePanel_DisableConfirmation", new Object[] {}));
        }
        
        Component c = new ModulesAndDescription (enableDisableText, modules.toArray (new ModuleInfo [modules.size ()]));
        add (c, BorderLayout.CENTER);
    }
    
}
