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
