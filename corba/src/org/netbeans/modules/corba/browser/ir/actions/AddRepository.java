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
import org.openide.nodes.*;
import org.openide.util.actions.*;
import org.openide.util.*;
import org.openide.*;
import org.netbeans.modules.corba.browser.ir.IRRootNode;
import org.netbeans.modules.corba.browser.ir.Util;
import org.netbeans.modules.corba.browser.ir.gui.AddRepositoryPanel;


import org.netbeans.modules.corba.*;

/*
 * @author Karel Gardas
 */

public class AddRepository extends NodeAction {

    public static final boolean DEBUG = false;
    //public static final boolean DEBUG = true;

    public AddRepository () {
        super ();
    }

    protected boolean enable (org.openide.nodes.Node[] nodes) {                        
        if (nodes != null)
            for (int i = 0; i < nodes.length; i ++)
                if (nodes[i].getCookie (IRRootNode.class) == null)
                    return false;
        return true;
    }

    public String getName() {
        return Util.getLocalizedString ("CTL_AddRepository");
    }

    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP; // [PENDING]
    }

    protected void performAction (final Node[] activatedNodes) {
        if (DEBUG)
            System.out.println ("AddRepository.java");
        Vector names = new Vector ();
        Node tmp_node = activatedNodes[0];
        IRRootNode node = (IRRootNode)tmp_node.getCookie (IRRootNode.class);
        AddRepositoryPanel p = new AddRepositoryPanel ();
        DialogDescriptor dd = new DialogDescriptor
            (p, Util.getLocalizedString ("TITLE_CORBAPanel"), true, DialogDescriptor.OK_CANCEL_OPTION, DialogDescriptor.OK_OPTION,
             DialogDescriptor.BOTTOM_ALIGN, null, null);
        TopManager.getDefault ().createDialog (dd).show ();
        if (dd.getValue () == DialogDescriptor.OK_OPTION) {
            if (DEBUG) {
                System.out.println (":OK");
                System.out.println (p.getName ());
                //System.out.println (p.getKind ());
                System.out.println (p.getUrl ());
                System.out.println (p.getIOR ());
            }
            if (enable (activatedNodes)) {
                try {
                    ((IRRootNode) activatedNodes[0].getCookie(IRRootNode.class)).addRepository
                        (p.getName (), p.getUrl (), p.getIOR ());
                } catch (Exception e) {
                    if (DEBUG)
                        e.printStackTrace ();
                    TopManager.getDefault ().notify (new NotifyDescriptor.Message (e.toString(),NotifyDescriptor.Message.ERROR_MESSAGE));
                }
            }
        }

    }


}

/*
 * $Log
 * $
 */



