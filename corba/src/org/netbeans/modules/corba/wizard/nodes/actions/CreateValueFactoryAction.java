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
