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
import org.netbeans.modules.corba.wizard.nodes.utils.ValueTypeCreator;
import org.netbeans.modules.corba.wizard.nodes.utils.Create;
/**
 *
 * @author  tzezula
 * @version 
 */
public class CreateValueTypeAction extends NodeAction implements Create {

    /** Creates new CreateValueTypeAction */
    public CreateValueTypeAction() {
    }
    
    public boolean enable (Node[] nodes) {
        if (nodes.length != 1)
            return false;
        return (nodes[0].getCookie (ValueTypeCreator.class) != null);
    }
    
    
    public void performAction (Node[] nodes) {
        if (enable (nodes)) {
            ((ValueTypeCreator)nodes[0].getCookie(ValueTypeCreator.class)).createValueType();
        }
    }
    
    public String getName () {
        return NbBundle.getBundle (CreateValueTypeAction.class).getString ("TXT_CreateValueType");
    }
    
    public HelpCtx getHelpCtx () {
        return HelpCtx.DEFAULT_HELP;
    }
    
    public String toString () {
        return NbBundle.getBundle (CreateValueTypeAction.class).getString ("TXT_ValueType");
    }
    
    public boolean isEnabled (Node[] nodes) {
        return this.enable (nodes);
    }

}
