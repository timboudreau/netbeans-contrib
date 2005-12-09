/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.desktopsampler.ui;

import java.awt.Component;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;

/**
 * An action to host DesktopSampler.
 *
 * @author Sandip V. Chitale (Sandip.Chitale@Sun.Com)
 */
public class DesktopSamplerAction extends CallableSystemAction {
    
    private DesktopSampler desktopSampler;
    
    public DesktopSamplerAction() {
        desktopSampler = new DesktopSampler();
    }
    
    public void performAction() {
        // do nothing. We are using this action to install the Desktop Sampler toolbar.
    }
    
    public String getName() {
        return NbBundle.getMessage(DesktopSamplerAction.class, "LBL_Action"); // NOI18N
    }
    
    protected String iconResource() {
        return "org/netbeans/modules/desktopsampler/ui/Dropper.gif"; // NOI18N
    }
    
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
    
    protected boolean asynchronous(){
        // performAction() should run in event thread
        return false;
    }
    
    public Component getToolbarPresenter() {
        return desktopSampler;
    }
}
