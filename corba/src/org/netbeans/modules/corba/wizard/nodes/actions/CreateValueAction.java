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
import org.openide.util.NbBundle;
import org.openide.util.HelpCtx;
import org.openide.util.actions.NodeAction;
import org.netbeans.modules.corba.wizard.nodes.utils.ValueCreator;
import org.netbeans.modules.corba.wizard.nodes.utils.Create;
/**
 *
 * @author  tzezula
 * @version 
 */
public class CreateValueAction extends NodeAction implements Create {

    /** Creates new CreateValueAction */
    public CreateValueAction() {
    }
    
    
    public boolean enable (Node[] nodes) {
        if (nodes.length != 1)
            return false;
        ValueCreator vk = (ValueCreator) nodes[0].getCookie (ValueCreator.class);
        if (vk == null)
            return false;
        return vk.canCreateValue();
    }
    
    public void performAction (Node[] nodes) {
        if (enable (nodes)) {
            ((ValueCreator)nodes[0].getCookie (ValueCreator.class)).createValue();
        }
    }
    
    public String getName () {
        return NbBundle.getBundle (CreateValueAction.class).getString ("TXT_CreateValue");
    }
    
    public HelpCtx getHelpCtx () {
        return HelpCtx.DEFAULT_HELP;
    }
    
    public String toString () {
        return NbBundle.getBundle(CreateValueAction.class).getString("TXT_Value");
    }

    public boolean isEnabled(org.openide.nodes.Node[] nodes) {
        return this.enable (nodes);
    }
    
}
