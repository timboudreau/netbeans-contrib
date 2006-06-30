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

package org.netbeans.modules.vcscore.versioning;

import org.openide.util.actions.CookieAction;

/**
 * The action, that will refresh the revisions of a node of a versioning file system.
 * The node should implement RefreshRevisionsCookie.
 *
 * @author  Martin Entlicher
 */
public class RefreshRevisionsAction extends CookieAction {

    private static final long serialVersionUID = 4288687339093602713L;

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
