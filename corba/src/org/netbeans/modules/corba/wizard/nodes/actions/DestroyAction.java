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

import java.text.MessageFormat;
import org.openide.util.actions.NodeAction;
import org.openide.util.HelpCtx;
import org.openide.TopManager;
import org.openide.NotifyDescriptor;
import org.openide.nodes.Node;

/**
 * Destroy Action called to destroy selected nodes
 * @author  tzezula
 * @version 1.0
 */
public class DestroyAction extends NodeAction {


    /** Performs the action on selected nodes
     *  @param Node[] nodes, array of nodes on which the action should be performed
     */
    public void performAction (Node[] nodes) {
        if (enable (nodes)) {
            TopManager tm = TopManager.getDefault ();
            NotifyDescriptor descriptor = new NotifyDescriptor.Confirmation (MessageFormat.format (java.util.ResourceBundle.getBundle("org/netbeans/modules/corba/wizard/nodes/actions/Bundle").getString("MSG_Destroy"),new Object[]{nodes[0].getName()}),NotifyDescriptor.OK_CANCEL_OPTION);
            tm.notify (descriptor);
            if (descriptor.getValue() == NotifyDescriptor.CANCEL_OPTION)
                return;
            if (nodes[0].canDestroy())
                try {
                    nodes[0].destroy();
                }catch (java.io.IOException ioe) {/*Never thrown*/}
        }
    }

    /** Is action enabled
     *  @param Node[] nodes
     *  @return boolean 
     */
    public boolean enable (Node[] nodes) {
        return nodes.length == 1;
    }

    /** Returns the name of action
     *  @return String
     */
    public String getName () {
        return java.util.ResourceBundle.getBundle("org/netbeans/modules/corba/wizard/nodes/actions/Bundle").getString("TXT_Destroy");
    }


    /** Returns the HelpContext
     *  @return HelpCtx
     */
    public HelpCtx getHelpCtx () {
        return HelpCtx.DEFAULT_HELP;
    }



}
