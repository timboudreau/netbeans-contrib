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
 *
 *
 */

package org.netbeans.modules.helpbuilder;

import java.awt.Dialog;
import javax.swing.SwingUtilities;
import org.openide.DialogDisplayer;

import org.openide.ErrorManager;
import org.openide.WizardDescriptor;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;

 /**
 *
 * @author  Richard Gregor
 */
public class HelpBuilderWizardAction extends CallableSystemAction {
    public static ErrorManager err;

    static{
        Integer eger = new Integer(ErrorManager.UNKNOWN);
        System.setProperty("HelpBuilderWizardAction",eger.toString());
        err = ErrorManager.getDefault().getInstance("HelpBuilderWizardAction");
    }

    public void performAction () {
        
        Integer eger = new Integer(ErrorManager.WARNING);
        err.log(eger.toString());
    
        WizardDescriptor desc = new HelpBuilderWizardDescriptor();
        final Dialog dlg = DialogDisplayer.getDefault ().createDialog (desc);
        dlg.show ();
        
        if (desc.getValue () == WizardDescriptor.FINISH_OPTION) {
            //System.out.println ("User finished the wizard"); // NOI18N
        } else {
            //System.out.println ("User cancelled the wizard"); // NOI18N
        }
        
        
    }

    public String getName () {
        return NbBundle.getMessage(HelpBuilderWizardAction.class, "LBL_Action");
    }

    protected String iconResource () {
        return "org/netbeans/modules/helpbuilder/images/HelpBuilderWizardActionIcon.gif";
    }

    protected boolean asynchronous(){
        return false;
    }
    
    public HelpCtx getHelpCtx () {  
        return HelpCtx.DEFAULT_HELP;
        // If you will provide context help then use:
        // return new HelpCtx (HelpBuilderWizardAction.class);
    }

    /** Perform extra initialization of this action's singleton.
     * PLEASE do not use constructors for this purpose!
    protected void initialize () {
	super.initialize ();
     * putProperty (Action.SHORT_DESCRIPTION, NbBundle.getMessage (HelpBuilderWizardAction.class, "HINT_Action"));
    }
    */

}
