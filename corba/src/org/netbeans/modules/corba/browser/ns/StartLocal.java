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

package org.netbeans.modules.corba.browser.ns;

import org.openide.*;
import org.openide.nodes.*;
import org.openide.util.*;
import org.openide.util.actions.*;

public class StartLocal extends NodeAction {

    public boolean enable (final Node[] nodes) {
	if (nodes.length != 1)
	    return false;
	return nodes[0].getCookie (CosNamingCookie.class) != null;
    }
    
    public void performAction (final Node[] nodes) {
	if (enable (nodes)) {
	    CosNamingCookie cookie = (CosNamingCookie) nodes[0].getCookie(CosNamingCookie.class);
	    cookie.performInteractive();
	}
    }

    public String getName () {
	return NbBundle.getBundle (StartLocal.class).getString("CTL_StartLocal");
    }

    public HelpCtx getHelpCtx () {
	return HelpCtx.DEFAULT_HELP;
    }

}