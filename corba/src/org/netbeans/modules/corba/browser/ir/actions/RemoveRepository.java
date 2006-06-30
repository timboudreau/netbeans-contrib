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

    public static final boolean DEBUG = false;
    //public static final boolean DEBUG = true;

    public RemoveRepository () {
        super ();
    }

    protected boolean enable (org.openide.nodes.Node[] nodes) {
        if (nodes != null)
            for (int i = 0; i < nodes.length; i ++)
                if (nodes[i].getCookie (Removable.class) == null)
                    return false;
        return true;
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



