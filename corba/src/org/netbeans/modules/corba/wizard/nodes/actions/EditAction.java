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
