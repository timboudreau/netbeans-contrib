/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcscore.actions;

import org.netbeans.modules.vcscore.versioning.impl.VersioningExplorer;
import org.netbeans.modules.vcscore.versioning.impl.VersioningExplorer.Panel;
import org.openide.util.actions.CallableSystemAction;
import org.openide.util.HelpCtx;

/**
 * This action opens the Versioning Explorer tab, but doesn't select node
 *
 * @author  Richard Gregor
 */
public class OpenVersioningAction extends CallableSystemAction {

    private static final long serialVersionUID = -4333229720968764504L;
    
    /** 
     * Creates new OpenVersioningAction
     */
    public OpenVersioningAction() {
    }

    public String getName() {
        return org.openide.util.NbBundle.getMessage(OpenVersioningAction.class, "LBL_OpenVersioning");
    }
    
    protected String iconResource () {
        return "org/netbeans/modules/vcscore/versioning/impl/versioningExplorer.gif";
    }

    public org.openide.util.HelpCtx getHelpCtx() {
        return null;
    }
    
    /**
     * @return false to run in AWT thread.
     */
    protected boolean asynchronous() {
        return false;
    }
    public void performAction() {
        Panel explorer = VersioningExplorer.getRevisionExplorer();
        explorer.open();       
        explorer.requestActive();
    }
 
}
