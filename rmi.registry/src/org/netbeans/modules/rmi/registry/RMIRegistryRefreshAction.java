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

package org.netbeans.modules.rmi.registry;

import java.util.ResourceBundle;

import org.openide.util.*;
import org.openide.nodes.*;
import org.openide.util.actions.CookieAction;

/** The node representation of RMIDataObject for Java sources.
*
* @author Martin Ryzl
*/
public class RMIRegistryRefreshAction extends CookieAction {

    /** Bundle. */
    private ResourceBundle bundle = NbBundle.getBundle(RMIRegistryRefreshAction.class);

    static final long serialVersionUID =-8495041514838335307L;
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
        return bundle.getString("PROP_RMIRegistryRefreshActionName"); // NOI18B
    }

    public HelpCtx getHelpCtx() {
        return new HelpCtx(RMIRegistryRefreshAction.class);
    }
}

/*
 * <<Log>>
 *  3    Gandalf   1.2         11/27/99 Patrik Knakal   
 *  2    Gandalf   1.1         10/23/99 Ian Formanek    NO SEMANTIC CHANGE - Sun
 *       Microsystems Copyright in File Comment
 *  1    Gandalf   1.0         8/27/99  Martin Ryzl     
 * $
 */














