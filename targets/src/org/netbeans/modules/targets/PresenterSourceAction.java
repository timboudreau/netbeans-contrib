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

import java.awt.Component;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;
import org.openide.windows.Mode;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
import org.openide.windows.Workspace;

/**
 * A standin system action which really just serves to provide a toolbar
 * presenter that targets can be dropped on
 *
 * @author  Tim Boudreau
 */
public class PresenterSourceAction extends CallableSystemAction {
    
    public PresenterSourceAction() {
        System.err.println("Creating presenter source action");
    }
    
    public void performAction() {        
        //Do nothing - should never be invoked
    }
    
    public String getName() {
        return NbBundle.getMessage(ShowTargets.class, "LBL_Action01");
    }
    
    protected String iconResource() {
        return "org/netbeans/modules/targets/resources/editorMode.gif";
    }
    
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
    
    protected boolean asynchronous() {
        return false;
    }
    
    public Component getToolbarPresenter() {
        System.err.println("Fetching tb presenter");
        return new TargetsToolbarPresenter();
    }
}
