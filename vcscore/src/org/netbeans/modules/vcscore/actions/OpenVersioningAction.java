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
        return "org/netbeans/modules/vcscore/versioning/impl/versioning.png";
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
