
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
 * Copyright 2007 Sun Microsystems, Inc. All Rights Reserved
 *
 */

package com.sun.tthub.gde.ui.framework;

import com.sun.tthub.gde.logic.GDEAppContext;
import com.sun.tthub.gde.logic.GDEClassesManager;
import com.sun.tthub.gde.logic.GDEController;
import com.sun.tthub.gde.logic.GDEInitParamKeys;
import com.sun.tthub.gde.ui.MainWizardUI;
import java.util.HashMap;
import javax.swing.JOptionPane;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;

public final class GDEWizardMenuAction extends CallableSystemAction {
    
    public void performAction() {
        GDEController controller = new GDEController();
        HashMap map = new HashMap();
        try {
            controller.initializeAppContext(map);
            // Create and initialize the GUI
            GDEWizardMainDlg dlg = new GDEWizardMainDlg();        
            MainWizardUI ui = new MainWizardUI(dlg);            
            GDEAppContext.getInstance().setWizardUI(ui);        

            dlg.setSize(709, 756);
            dlg.setLocationRelativeTo(null);                
            dlg.setVisible(true);
            dlg.toFront();            
        } catch(Exception ex) {
           ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Exception occured  " +
                    "while loading. Failed to initialize " +
                    "the context", "Context Initialization Error", 
                    JOptionPane.ERROR_MESSAGE);
        }        
    }
    
    public String getName() {
        return NbBundle.getMessage(GDEWizardMenuAction.class, "CTL_GDEWizardMenuAction");
    }
    
    protected String iconResource() {
        return "com/sun/tthub/gde/ui/wizard.gif";
    }
    
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
    
    protected boolean asynchronous() {
        return false;
    }
    
}
