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

package org.netbeans.modules.vcscore.actions;

import org.netbeans.modules.vcscore.commands.CommandOutputTopComponent;
import org.openide.util.HelpCtx;
import org.openide.util.actions.CallableSystemAction;

/**
 * The action, that will open VCS output.
 * @author  Richard Gregor
 */
public class VcsOutputAction extends CallableSystemAction {

    static final long serialVersionUID = 3367347903850705340L;

    /** Getter for help.
     */
    public HelpCtx getHelpCtx() {
        return null;
    }

    /** Getter for name
     */
    public String getName () {
        return org.openide.util.NbBundle.getMessage(VcsOutputAction.class,"CTL_VcsOutputActionName");
    }
    
    protected String iconResource() {     
        return "org/netbeans/modules/vcscore/commands/vcs_output.png";
    }
    
    /**
     * @return false to run in AWT thread.
     */
    protected boolean asynchronous() {
        return false;
    }
    
    /**
     * Open VCS global options
     */
    public void performAction() {        
        CommandOutputTopComponent top = CommandOutputTopComponent.getInstance();
        top.open();
        top.requestActive();   
    }

}
