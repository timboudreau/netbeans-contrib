/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.targets;

import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;
import org.openide.windows.Mode;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
import org.openide.windows.Workspace;

/**
 * Action that can always be invoked and work procedurally.
 *
 * @author  Marek Slama
 */
public class ShowTargets extends CallableSystemAction {
    public String TEST_MODE_NAME = "targets";
    
    public void performAction() {        
        Workspace ws = WindowManager.getDefault().getCurrentWorkspace();               
        Mode mode = ws.findMode("targets");
        if (mode == null) {
            mode = ws.createMode
            (TEST_MODE_NAME, NbBundle.getMessage(ShowTargets.class, "LBL_TestMode"), null);
        }
        TopComponent tc = TargetsPanel.findDefault();
        TopComponent[] tcs = mode.getTopComponents();
        
        boolean foundInMode = false;
        for (int i = 0; i < tcs.length; i++) {
            if (tc == tcs[i]) {
                foundInMode = true;
                break;
            }
        }
        
        if (!foundInMode) {
            mode.dockInto(tc);
        }
        
        tc.open();
        tc.requestActive();
    }
    
    public String getName() {
        return NbBundle.getMessage(ShowTargets.class, "LBL_Action00");
    }
    
    protected String iconResource() {
        return "org/netbeans/modules/targets/resources/editorMode.gif";
    }
    
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
        // If you will provide context help then use:
        // return new HelpCtx (MyAction.class);
    }
    
    protected boolean asynchronous() {
        return false;
    }
    
}
