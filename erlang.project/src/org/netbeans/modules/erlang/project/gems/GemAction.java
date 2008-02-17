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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.erlang.project.gems;

import java.awt.Dialog;
import org.netbeans.modules.erlang.platform.api.RubyInstallation;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;


public final class GemAction extends CallableSystemAction {
    public void performAction() {
        showGemManager(null);
    }
    
    public static void showGemManager(String availableFilter) {
        if (!RubyInstallation.getInstance().isValidRuby(true)) {
            return;
        }

        String gemProblem = GemManager.getGemProblem();

        if (gemProblem != null) {
            NotifyDescriptor nd =
                new NotifyDescriptor.Message(gemProblem, NotifyDescriptor.Message.ERROR_MESSAGE);
            DialogDisplayer.getDefault().notify(nd);

            return;
        }

        GemPanel customizer = new GemPanel(new GemManager(), availableFilter);
        javax.swing.JButton close =
            new javax.swing.JButton(NbBundle.getMessage(GemAction.class, "CTL_Close"));
        close.getAccessibleContext()
             .setAccessibleDescription(NbBundle.getMessage(GemAction.class, "AD_Close"));

        DialogDescriptor descriptor =
            new DialogDescriptor(customizer, NbBundle.getMessage(GemAction.class, "CTL_GemAction"),
                true, new Object[] { close }, close, DialogDescriptor.DEFAULT_ALIGN,
                new HelpCtx(GemAction.class), null); // NOI18N
        Dialog dlg = null;

        try {
            dlg = DialogDisplayer.getDefault().createDialog(descriptor);
            dlg.setVisible(true);
        } finally {
            if (dlg != null) {
                dlg.dispose();
            }
        }

        if (customizer.isModified()) {
            RubyInstallation.getInstance().recomputeRoots();
        }
    }
    
    public String getName() {
        return NbBundle.getMessage(GemAction.class, "CTL_GemAction");
    }

    protected void initialize() {
        super.initialize();
        // see org.openide.util.actions.SystemAction.iconResource() javadoc for more details
        putValue("noIconInMenu", Boolean.TRUE);
    }

    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    protected boolean asynchronous() {
        return false;
    }
}
