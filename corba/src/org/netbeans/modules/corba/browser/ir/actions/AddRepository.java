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
        if (nodes == null || nodes.length != 1)
            return false;
        return (nodes[0].getCookie (IRRootNode.class) != null);
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
            (p, "CORBA Panel", true, DialogDescriptor.OK_CANCEL_OPTION, DialogDescriptor.OK_OPTION,
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
                    TopManager.getDefault ().notify (new NotifyDescriptor.Exception
                        ((java.lang.Throwable) e));
                }
            }
        }

    }


}

/*
 * $Log
 * $
 */



