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
import org.openide.util.actions.NodeAction;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.netbeans.modules.corba.wizard.nodes.utils.ValueBoxCreator;
/**
 * 
 * @author  tzezula
 * @version 
 */  
public class CreateValueBoxAction extends NodeAction implements org.netbeans.modules.corba.wizard.nodes.utils.Create {

    /** Creates new CreateValueBoxAction */
    public CreateValueBoxAction() {
    }

    
    public boolean enable (Node[] nodes) {
        if (nodes.length != 1)
            return false;
        return nodes[0].getCookie (ValueBoxCreator.class) != null;
    }
    
    public void performAction (Node[] nodes) {
        if ( this.enable (nodes)) {
            ValueBoxCreator creator = (ValueBoxCreator)nodes[0].getCookie (ValueBoxCreator.class);
            creator.createValueBox();
        }
    }
    
    public String getName () {
        return NbBundle.getBundle (CreateValueBoxAction.class).getString("TXT_CreateValueBox");
    }
    
    public HelpCtx getHelpCtx () {
        return HelpCtx.DEFAULT_HELP;
    }
    
    public String toString () {
        return NbBundle.getBundle (CreateValueBoxAction.class).getString ("TXT_ValueBox");
    }
    
    public boolean isEnabled(org.openide.nodes.Node[] nodes) {
        return this.enable(nodes);
    }
    
}
