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

package org.netbeans.modules.corba.wizard.nodes.actions;

import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.actions.NodeAction;
import org.openide.util.NbBundle;
import org.netbeans.modules.corba.wizard.nodes.utils.FwdDclCreator;
/**
 *
 * @author  tzezula
 * @version 
 */
public class CreateFwdDclAction extends NodeAction implements org.netbeans.modules.corba.wizard.nodes.utils.Create {

    /** Creates new CreateFwdDclAction */
    public CreateFwdDclAction() {
    }
    
    public void performAction (Node[] nodes) {
        FwdDclCreator creator = (FwdDclCreator) nodes[0].getCookie (FwdDclCreator.class);
        creator.createForwardDcl();
    }
    
    public boolean enable (Node[] nodes) {
        if (nodes.length != 1)
            return false;
        return (nodes[0].getCookie (FwdDclCreator.class) != null);
    }

    public boolean isEnabled(org.openide.nodes.Node[] nodes) {
        return enable (nodes);
    }
    
    public HelpCtx getHelpCtx () {
        return HelpCtx.DEFAULT_HELP;
    }
    
    public String getName () {
        return NbBundle.getBundle (CreateFwdDclAction.class).getString ("TXT_CreateFwdDcl");
    }
    
    public String toString () {
        return java.util.ResourceBundle.getBundle("org/netbeans/modules/corba/wizard/nodes/actions/Bundle").getString("TXT_FwdDcl");
    }
    
}
