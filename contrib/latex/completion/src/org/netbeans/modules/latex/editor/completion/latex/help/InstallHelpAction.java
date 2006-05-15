/*
 *                          Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License Version
 * 1.0 (the "License"). You may not use this file except in compliance with
 * the License. A copy of the License is available at http://www.sun.com/
 *
 * The Original Code is the LaTeX module.
 * The Initial Developer of the Original Code is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002-2006.
 * All Rights Reserved.
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.editor.completion.latex.help;

import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;

/**
 *
 * @author Jan Lahoda
 */
public class InstallHelpAction extends CallableSystemAction {
    
    /** Creates a new instance of ViewFilesTabAction */
    public InstallHelpAction() {
    }
    
    public HelpCtx getHelpCtx() {
        return new HelpCtx(InstallHelpAction.class);
    }
    
    public String getName() {
        return NbBundle.getMessage(InstallHelpAction.class, "LBL_InstallHelpAction");
    }

    protected String iconResource() {
        return "org/netbeans/modules/latex/editor/completion/resources/InstallHelpActionIcon.gif";
    }

    public void performAction() {
        InstallHelp.installHelp();
    }
    
    protected boolean asynchronous() {
        return false;
    }

}
