/*
 * DisconnectAction.java
 *
 * Created on April 7, 2001, 10:39 AM
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
