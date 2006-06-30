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

import org.openide.util.HelpCtx;
import org.openide.nodes.Node;
import org.openide.NotifyDescriptor;
import org.openide.TopManager;
import org.openide.util.actions.NodeAction;

/**
 *
 * @author  tzezula
 * @version
 */
public class RenameAction extends NodeAction {

    /** Creates new RenameAction */
    public RenameAction() {
    }

    public void performAction (Node[] nodes) {
       Node n = nodes[0];
       NotifyDescriptor.InputLine dlg = new NotifyDescriptor.InputLine(java.util.ResourceBundle.getBundle("org/netbeans/modules/corba/wizard/nodes/actions/Bundle").getString("TXT_RenameLabel"),
                                         java.util.ResourceBundle.getBundle("org/netbeans/modules/corba/wizard/nodes/actions/Bundle").getString("TXT_Rename"));
       dlg.setInputText(n.getName());
        if (NotifyDescriptor.OK_OPTION.equals(TopManager.getDefault().notify(dlg))) {
            try {
                String newname = dlg.getInputText();
                if (! newname.equals("")) n.setName(dlg.getInputText()); // NOI18N
            }
            catch (IllegalArgumentException e) {
                NotifyDescriptor.Message msg = new NotifyDescriptor.Message(
                                                   java.text.MessageFormat.format(
                                                       java.util.ResourceBundle.getBundle("org/netbeans/modules/corba/wizard/nodes/actions/Bundle").getString("MSG_BadFormat"),
                                                       new Object[] {n.getName()}),
                                                   NotifyDescriptor.ERROR_MESSAGE);
                TopManager.getDefault().notify(msg);
            }
        }
    }

    public boolean enable (Node[] nodes) {
        return (nodes.length == 1);
    }

    public String getName () {
        return java.util.ResourceBundle.getBundle("org/netbeans/modules/corba/wizard/nodes/actions/Bundle").getString("TXT_Rename");
    }

    public HelpCtx getHelpCtx () {
        return HelpCtx.DEFAULT_HELP;
    }

}
