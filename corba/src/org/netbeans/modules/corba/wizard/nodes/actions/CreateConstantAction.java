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
import org.openide.util.NbBundle;
import org.openide.util.actions.NodeAction;
import org.netbeans.modules.corba.wizard.nodes.utils.ConstantCreator;

/** Action sensitive to the node selection that does something useful.
 *
 * @author  root
 */
public class CreateConstantAction extends NodeAction implements org.netbeans.modules.corba.wizard.nodes.utils.Create {

    protected void performAction (Node[] nodes) {
        if (enable (nodes)) {
            ((ConstantCreator)nodes[0].getCookie(ConstantCreator.class)).createConstant();
        }
    }
  
    protected boolean enable (Node[] nodes) {
        return nodes.length == 1 && nodes[0].getCookie(ConstantCreator.class) != null;
    }

    public String getName () {
        return "Create Constant";
    }
    
    public String toString() {
        return java.util.ResourceBundle.getBundle("org/netbeans/modules/corba/wizard/nodes/actions/Bundle").getString("TXT_Constant");
    }

    protected String iconResource () {
        return java.util.ResourceBundle.getBundle("org/netbeans/modules/corba/wizard/nodes/actions/Bundle").getString("TXT_CreateConstant");
    }

    public HelpCtx getHelpCtx () {
        return HelpCtx.DEFAULT_HELP;
        // If you will provide context help then use:
        // return new HelpCtx (CreateConstantAction.class);
    }
    
    

    /** Perform extra initialization of this action's singleton.
     * PLEASE do not use constructors for this purpose!
     protected void initialize () {
     super.initialize ();
     putProperty ("someProp", value);
     }
    */
    
    public boolean isEnabled(org.openide.nodes.Node[] nodes) {
        return enable (nodes);
    }
  
}
