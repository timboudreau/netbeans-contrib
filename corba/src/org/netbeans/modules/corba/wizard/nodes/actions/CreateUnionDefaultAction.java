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
import org.netbeans.modules.corba.wizard.nodes.utils.UnionDefaultCreator;

/**
 *
 * @author  tzezula
 * @version 
 */
public class CreateUnionDefaultAction extends ExtNodeAction {

    /** Creates new CreateUnionDefaultAction */
    public CreateUnionDefaultAction() {
    }

    public boolean enable (Node[] nodes) {
        if (nodes.length != 1)
            return false;
        UnionDefaultCreator creator = (UnionDefaultCreator) nodes[0].getCookie (UnionDefaultCreator.class);
        if (creator == null)
            return false;
        return creator.canAdd ();
    }

    public void performAction (Node[] nodes) {
      if (enable (nodes)) {
        ((UnionDefaultCreator)nodes[0].getCookie (UnionDefaultCreator.class)).createUnionDefault ();
      }
    }

    public String getName () {
      return java.util.ResourceBundle.getBundle("org/netbeans/modules/corba/wizard/nodes/actions/Bundle").getString("TXT_CreateUnionDefault");
    }

    public HelpCtx getHelpCtx () {
      return HelpCtx.DEFAULT_HELP;
    }

}