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

package org.netbeans.modules.rmi.util;

import java.util.ResourceBundle;

import org.openide.util.*;
import org.openide.nodes.*;
import org.openide.util.actions.CookieAction;

/** Action that perform refresh on given nodes.
*
* @author Martin Ryzl
*/
public class RefreshAction extends CookieAction {

    static final long serialVersionUID =-1115041514838335307L;
    
    protected void performAction(Node[] activatedNodes) {
        for (int i = 0; i < activatedNodes.length; i++) {
            RefreshCookie rc = (RefreshCookie) activatedNodes[i].getCookie(RefreshCookie.class);
            if (rc != null) rc.refresh();
        }
    }

    protected Class[] cookieClasses() {
        return new Class[] { RefreshCookie.class };
    }

    protected int mode() {
        return MODE_ALL;
    }

    public String getName() {
        return NbBundle.getBundle(RefreshAction.class).getString("LBL_RefreshActionName"); // NOI18B
    }

    public HelpCtx getHelpCtx() {
        return new HelpCtx(RefreshAction.class);
    }
}
