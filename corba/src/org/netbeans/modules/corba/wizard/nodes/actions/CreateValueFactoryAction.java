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

import org.openide.util.actions.NodeAction;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;
import org.openide.util.HelpCtx;
import org.netbeans.modules.corba.wizard.nodes.utils.*;
/**
 *
 * @author  tzezula
 * @version
 */
public class CreateValueFactoryAction extends NodeAction implements Create {

    /** Creates new CreateValueFactoryAction */
    public CreateValueFactoryAction() {
    }

    public boolean enable (Node[] nodes) {
        if (nodes.length != 1)
            return false;
        ValueFactoryCreator vk = (ValueFactoryCreator) nodes[0].getCookie(ValueFactoryCreator.class);
        if ( vk == null)
            return false;
        return vk.canCreateFactory();
    }
    
    public void performAction (Node[] nodes) {
        if (enable (nodes)) {
            ((ValueFactoryCreator)nodes[0].getCookie(ValueFactoryCreator.class)).createFactory();
        }
    }
    
    public HelpCtx getHelpCtx () {
        return HelpCtx.DEFAULT_HELP;
    }
    
    public String getName() {
        return NbBundle.getBundle (CreateValueFactoryAction.class).getString ("TXT_CreateValueFactory");
    }
    
    public boolean isEnabled (Node[] nodes) {
        return this.enable (nodes);
    }
    
    public String toString () {
        return NbBundle.getBundle (CreateValueFactoryAction.class).getString ("TXT_ValueFactory");
    }

}
