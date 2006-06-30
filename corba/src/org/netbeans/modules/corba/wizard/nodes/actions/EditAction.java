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

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import org.openide.TopManager;
import org.openide.nodes.*;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.NodeAction;
import org.netbeans.modules.corba.wizard.nodes.utils.EditCookie;
import org.netbeans.modules.corba.wizard.nodes.gui.ExDialogDescriptor;
import org.netbeans.modules.corba.wizard.nodes.gui.ExPanel;
/**
 *
 * @author  tzezula
 * @version
 */
public class EditAction extends NodeAction {
    
    private java.awt.Dialog dialog;

    /** Creates new EditAction */
    public EditAction() {
    }
    
    public boolean enable (Node[] nodes) {
        return (nodes.length == 1 && nodes[0].getCookie(EditCookie.class)!=null);
    }
    
    public void performAction (Node[] nodes) {
        if (enable (nodes)) {
            final EditCookie ec = (EditCookie) nodes[0].getCookie(EditCookie.class);
            final ExPanel p = ec.getEditPanel();
            ExDialogDescriptor descriptor = new ExDialogDescriptor (p, java.util.ResourceBundle.getBundle("org/netbeans/modules/corba/wizard/nodes/actions/Bundle").getString("TXT_Customize"), true, 
                                                                new ActionListener () {
                                                                        public void actionPerformed (ActionEvent event) {
                                                                            if (event.getActionCommand().equals(ExDialogDescriptor.OK)){
                                                                                ec.reInit (p);
                                                                            }
                                                                            dialog.setVisible (false);
                                                                            dialog.dispose();
                                                                        }
                                                                    });                                                      
            descriptor.disableOk();
            this.dialog = TopManager.getDefault().createDialog (descriptor);
            this.dialog.setVisible (true);
        }
    }
    
    public String getName () {
        return NbBundle.getBundle(EditAction.class).getString ("TXT_Edit");
    }
    
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

}
