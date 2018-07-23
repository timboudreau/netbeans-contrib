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

package org.netbeans.modules.languages.studio;

import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;

import java.awt.*;

public final class LanguagesManagerAction extends CallableSystemAction {
    
    public void performAction () {
        LanguagesManagerPanel panel = new LanguagesManagerPanel ();
        DialogDescriptor dialogDescriptor = new DialogDescriptor (
            panel,
            "Languages"
        );
        Dialog dialog = DialogDisplayer.getDefault ().
            createDialog (dialogDescriptor);
        dialog.setVisible (true);
        if (dialogDescriptor.getValue () != dialogDescriptor.OK_OPTION)
            return;
    }
    
    public String getName () {
        return NbBundle.getMessage (LanguagesManagerAction.class, "CTL_LanguagesManagerAction");
    }
    
    protected void initialize () {
        super.initialize ();
        // see org.openide.util.actions.SystemAction.iconResource() javadoc for more details
        putValue ("noIconInMenu", Boolean.TRUE);
    }
    
    public HelpCtx getHelpCtx () {
        return HelpCtx.DEFAULT_HELP;
    }
    
    protected boolean asynchronous () {
        return false;
    }
    
}
