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


package org.netbeans.modules.jndi;

import org.openide.nodes.Node;
import org.openide.util.actions.NodeAction;
import org.openide.util.NbBundle;
import org.openide.util.HelpCtx;
import org.netbeans.modules.jndi.utils.DisconnectCtxCookie;

/**
 *
 * @author  root
 * @version 
 */
public class DisconnectAction extends NodeAction {

    /** Creates new DisconnectAction */
    public DisconnectAction() {
    }
    
    
    public boolean enable (Node[] nodes) {
        if (nodes == null || nodes.length != 1)
            return false;
        return nodes[0].getCookie (DisconnectCtxCookie.class) != null;
    }
    
    public void performAction (Node[] nodes) {
        DisconnectCtxCookie dc = (DisconnectCtxCookie) nodes[0].getCookie (DisconnectCtxCookie.class);
        dc.disconnect ();
    }
    
    public HelpCtx getHelpCtx () {
        return HelpCtx.DEFAULT_HELP;
    }
    
    public String getName () {
        return NbBundle.getBundle (DisconnectAction.class).getString ("TXT_DisconnectCtx");
    }

}
