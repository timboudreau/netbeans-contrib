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

package org.netbeans.modules.corba.browser.ir.actions;

import java.util.Vector;
//import java.awt.datatransfer.StringSelection;

import org.openide.nodes.*;
import org.openide.util.actions.*;
import org.openide.util.*;
import org.openide.*;
import org.netbeans.modules.corba.browser.ir.Util;
import org.netbeans.modules.corba.browser.ir.IRRootNode;
import org.netbeans.modules.corba.browser.ir.nodes.*;
import org.netbeans.modules.corba.*;
import org.netbeans.modules.corba.browser.ir.util.Removable;

/*
 * @author Karel Gardas
 */

public class RemoveRepository extends NodeAction {

    //public static final boolean DEBUG = false;
    public static final boolean DEBUG = true;

    public RemoveRepository () {
        super ();
    }

    protected boolean enable (org.openide.nodes.Node[] nodes) {
        if (nodes == null || nodes.length != 1)
            return false;
        return (nodes[0].getCookie (Removable.class) != null);
    }

    public String getName() {
        return Util.getLocalizedString ("CTL_RemoveRepository");
    }

    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP; // [PENDING]
    }

    protected void performAction (final Node[] activatedNodes) {
        if (DEBUG)
            System.out.println ("RemoveRepository.java");

        if (enable (activatedNodes)) {
            Node rn = (Node)activatedNodes[0].getCookie (Removable.class);
            ((IRRootNode)rn.getParentNode ()).removeRepository (rn.getName ());
        }
    }

}

/*
 * $Log
 * $
 */



