/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2001 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcscore.versioning;

import org.openide.util.actions.CookieAction;

/**
 * The action, that will refresh the revisions of a node of a versioning file system.
 * The node should implement RefreshRevisionsCookie.
 *
 * @author  Martin Entlicher
 */
public class RefreshRevisionsAction extends CookieAction {

    public String getName() {
        return org.openide.util.NbBundle.getMessage(RefreshRevisionsAction.class, "RefreshRevisionsAction_Name");
    }
    
    public org.openide.util.HelpCtx getHelpCtx() {
        return null;
    }
    
    protected int mode() {
        return MODE_ANY;
    }
    
    protected Class[] cookieClasses() {
        return new Class[] { RefreshRevisionsCookie.class };
    }
    
    protected void performAction(final org.openide.nodes.Node[] activatedNodes) {
        for (int i = 0; i < activatedNodes.length; i++) {
            RefreshRevisionsCookie c =
                (RefreshRevisionsCookie) activatedNodes[i].getCookie(RefreshRevisionsCookie.class);
            if (c != null) {
                c.refreshRevisions();
            }
        }
    }
    
}
