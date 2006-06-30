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

package org.netbeans.modules.rmi.registry.util;

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
