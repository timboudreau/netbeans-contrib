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
