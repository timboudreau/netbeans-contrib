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

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.Set;
import javax.swing.*;

import org.netbeans.Module;
import org.openide.modules.ModuleInfo;
import org.openide.util.NbBundle;

/**
 * @author Jirka Rechtacek (jrechtacek@netbeans.org)
 * @see "#20323"
 */
class ModuleEnableDisablePanel extends JPanel {
    Set<Module> explicit;
    Set<Module> modules;
    private boolean toEnable;
    
    public ModuleEnableDisablePanel (boolean enable, Set<Module> explicit, Set<Module> implied) {
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
