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

package org.netbeans.modules.corba.browser.ns;

import org.openide.nodes.*;
import org.openide.util.actions.*;
import org.openide.util.*;
import org.openide.*;


import org.netbeans.modules.corba.*;

/*
 * @author Karel Gardas
 */

public class UnbindObject extends NodeAction {

    public static final boolean DEBUG = false;

    static final long serialVersionUID =3721620137072712461L;
    public UnbindObject () {
        super ();
    }

    protected boolean enable (org.openide.nodes.Node[] nodes) {
        if (nodes == null || nodes.length != 1)
            return false;
        return (nodes[0].getCookie (ObjectNode.class) != null);
    }

    public String getName() {
        return NbBundle.getBundle (ContextNode.class).getString ("CTL_UnbindObject");
    }

    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP; // [PENDING]
    }

    protected void performAction (final Node[] activatedNodes) {
        if (DEBUG)
            System.out.println ("UnbindObject.java");
        if (enable (activatedNodes)) {
            try {
                ((ObjectNode) activatedNodes[0].getCookie(ObjectNode.class)).unbind ();
            } catch (Exception e) {
                if (DEBUG)
                    e.printStackTrace ();
                TopManager.getDefault ().notify (new NotifyDescriptor.Exception
                                                 ((java.lang.Throwable) e));
            }
        }
    }

}


/*
 * $Log
 * $
 */
