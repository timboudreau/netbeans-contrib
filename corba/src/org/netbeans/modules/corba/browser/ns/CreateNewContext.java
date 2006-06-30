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

public class CreateNewContext extends NodeAction {

    public static final boolean DEBUG = false;
    //public static final boolean DEBUG = true;

    static final long serialVersionUID =-4377234682968736870L;
    public CreateNewContext () {
        super ();
    }

    protected boolean enable (org.openide.nodes.Node[] nodes) {
        if (nodes == null || nodes.length != 1)
            return false;
        ContextNode ctxNode = (ContextNode) nodes[0].getCookie(ContextNode.class);
        return (ctxNode != null && ctxNode.isValid());
    }

    public String getName() {
        return NbBundle.getBundle (ContextNode.class).getString ("CTL_CreateNewContext");
    }

    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP; // [PENDING]
    }

    protected void performAction (final Node[] activatedNodes) {
        if (DEBUG)
            System.out.println ("CreateNewContext.java");
        CreateNewContextPanel p = new CreateNewContextPanel ();
        DialogDescriptor dd = new DialogDescriptor
                              (p, NbBundle.getBundle(ContextNode.class).getString("CTL_CorbaTitle"), true, DialogDescriptor.OK_CANCEL_OPTION, DialogDescriptor.OK_OPTION,
                               DialogDescriptor.BOTTOM_ALIGN, null, null);
        TopManager.getDefault ().createDialog (dd).show ();
        if (dd.getValue () == DialogDescriptor.OK_OPTION) {
            if (DEBUG) {
                System.out.println (":OK");
                System.out.println (p.getName ());
                System.out.println (p.getKind ());
            }
            if (enable (activatedNodes)) {
                try {
                    ((ContextNode) activatedNodes[0].getCookie(ContextNode.class)).create_new_context
                    (p.getName (), p.getKind ());
                } catch (Exception e) {
                    if (DEBUG)
                        e.printStackTrace ();
                    TopManager.getDefault ().notify (new NotifyDescriptor.Message(e.toString(), NotifyDescriptor.Message.ERROR_MESSAGE));
                }

            }
        }
    }
}


/*
 * $Log
 * $
 */
