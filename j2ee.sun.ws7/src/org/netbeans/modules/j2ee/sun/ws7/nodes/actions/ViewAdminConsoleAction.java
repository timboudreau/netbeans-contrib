/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

/*
 * ViewAdminConsoleAction.java
 *
 */

package org.netbeans.modules.j2ee.sun.ws7.nodes.actions;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.NodeAction;

import org.netbeans.modules.j2ee.sun.ws7.nodes.WS70ManagerNode;

import java.net.URL;
import org.openide.awt.HtmlBrowser.URLDisplayer;

/**
 *
 * @author Administrator
 */
public class ViewAdminConsoleAction extends NodeAction{
    
    /** Creates a new instance of ViewAdminConsoleAction */
    public ViewAdminConsoleAction() {
    }
   protected void performAction(Node[] nodes){
        if ((nodes == null) || (nodes.length < 1)) {
            return;
        }
        WS70ManagerNode managerNode = (WS70ManagerNode)nodes[0].getCookie(WS70ManagerNode.class);        
        if (managerNode != null) {
            

            try {
                URLDisplayer.getDefault().showURL(new URL(managerNode.getAdminURL()));
            }
            catch (Exception e) {
                return;
            }
        }
    }
    
    protected boolean enable(Node[] nodes){
        return nodes.length==1;
    }
    
    public String getName(){
        return NbBundle.getMessage(ViewAdminConsoleAction.class, "LBL_AdminConsoleAction");
    }
    
    public HelpCtx getHelpCtx(){
        return HelpCtx.DEFAULT_HELP;
    }        
}
